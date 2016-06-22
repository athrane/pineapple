/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
 * 
 * This file is part of Pineapple.
 * 
 * Pineapple is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * Pineapple is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public 
 * license for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/


package com.alpha.pineapple.jsr88.command;

import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.command.CommandException;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface which creates a connected JSR88
 * deployment manager. The deployment manager is created using the factory class contained by the key
 * <code>factory-implementation</code>. Furthermore the command uses the user/password contains in the keys
 * <code>user</code> and <code>password</code> to log on to a JEE domain defined in the URI contained by the key
 * <code>domain-uri</code>. After execution the deployment manager is stored in the context using the key
 * <code>deployment-manager</code>.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>factory-implementation</code> Contains Defines deployment factory 
 * implementation. Example for WebLogic 9.2: 
 * <code>weblogic.deploy.api.spi.factories.internal.DeploymentFactoryImpl<\code>.
 * The type is <code>java.lang.string</code>.</li>
 * 
 * <li><code>user</code> Defines user to log on to a JEE domain to create a 
 * connected deployment manager. The type is <code>java.lang.string</code>.</li>
 * 
 * <li><code>password</code> Defines password to log on to a JEE domain to create a 
 * connected deployment manager. The type is <code>java.lang.string</code>.</li>
 * 
 * <li><code>domain-uri</code> Defines deployment manager URI to log on to a JEE 
 * domain to create a connected deployment manager. Example for WebLogic: 
 * <code>deployer:WebLogic:t3://127.0.0.1:7001</code>. The type is 
 * <code>java.lang.string</code>.</li>   
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>deployment-manager</code> Contains the created deployment manager instance. The type is
 * <code>javax.enterprise.deploy.spi.DeploymentManager</code>.</li>
 * </ul>
 * </p>
 */
public class GetDeploymentManagerCommand implements Command
{

    /**
     * Key used to identify property in context: Defines application server user.
     */
    public static final String USER_KEY = "user";

    /**
     * Key used to identify property in context: Defines application server password. .
     */
    public static final String PASSWORD_KEY = "password";

    /**
     * Key used to identify property in context: Defines deployment manager URI. Example for WebLogic:
     * deployer:WebLogic:t3://127.0.0.1:7001
     */
    public static final String URI_KEY = "domain-uri";

    /**
     * Key used to identify property in context: Defines deployment factory implementation. Example for WebLogic:
     * weblogic.deploy.api.spi.factories.internal.DeploymentFactoryImpl
     */
    public static final String FACTORY_KEY = "factory";

    /**
     * Key used to identify property in context: Contains connected deployment manager after command execution.
     */
    public static final String MANAGER_KEY = "deployment-manager";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * JEE domain user.
     */
    @Initialize( USER_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )                    
    String user;

    /**
     * JEE domain password.
     */
    @Initialize( PASSWORD_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )                        
    String password;

    /**
     * Defines deployment manager URI.
     */
    @Initialize( URI_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )                        
    String uri;

    /**
     * Defines deployment factory implementation.
     */
    @Initialize( FACTORY_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )                        
    String factoryImpl;

    public boolean execute( Context context ) throws Exception
    {
        // initialize command
        CommandInitializer initializer =  new CommandInitializerImpl();
        initializer.initialize( context, this );
        
        // get factory manager
        DeploymentFactoryManager factory;
        factory = getFactory();

        // get deployment manager
        DeploymentManager manager = getDeploymentManager( factory );

        // store manager in context
        context.put( MANAGER_KEY, manager );

        return Command.CONTINUE_PROCESSING;
    }

    /**
     * Get deployment manager.
     * 
     * @param factory
     *            Deployment factory manager.
     * 
     * @return initialized deployment manager.
     * 
     * @throws CommandException
     *             If deployment manager initialization fails.
     */
    DeploymentManager getDeploymentManager( DeploymentFactoryManager factory ) throws CommandException
    {
        try
        {
            // log debug message
            if ( logger.isDebugEnabled() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "Will attempt to get deployment manager with URI <" );
                message.append( uri );
                message.append( "> , user <" );
                message.append( user );
                message.append( "> and password <" );
                message.append( password );
                message.append( ">." );

                logger.debug( message.toString() );
            }

            // get manager
            DeploymentManager manager;
            manager = factory.getDeploymentManager( uri, user, password );

            // log debug message
            if ( logger.isDebugEnabled() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "Got deployment manager instance <" );
                message.append( ToStringBuilder.reflectionToString( manager ) );
                message.append( ">." );

                logger.debug( message.toString() );
            }

            return manager;

        }
        catch ( DeploymentManagerCreationException e )
        {

            // log error message
            StringBuilder message = new StringBuilder();
            message.append( "Deployment manager initialization failed which exception <" );
            message.append( StackTraceHelper.getStrackTrace( e ) );
            message.append( ">. Command failed." );

            // throw exception
            throw new CommandException( message.toString(), e );
        }
    }

    /**
     * Get deployment factory manager.
     * 
     * @return deployment factory manager.
     * 
     * @throws CommandException
     *             If deployment factory manager initialization fails.
     */
    DeploymentFactoryManager getFactory() throws CommandException
    {
        try
        {
            // get factory manager
            DeploymentFactoryManager dfm;
            dfm = DeploymentFactoryManager.getInstance();

            // log debug message
            if ( logger.isDebugEnabled() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "Got deployment factory manager instance <" );
                message.append( ToStringBuilder.reflectionToString( dfm ) );
                message.append( ">." );

                logger.debug( message.toString() );
            }

            // get factory class
            Class dfClass = Class.forName( factoryImpl );

            // get factory instance
            DeploymentFactory dfInstance;
            dfInstance = (DeploymentFactory) dfClass.newInstance();

            // log debug message
            if ( logger.isDebugEnabled() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "Got deployment factory instance <" );
                message.append( ToStringBuilder.reflectionToString( dfInstance ) );
                message.append( ">." );

                logger.debug( message.toString() );
            }

            // register instance
            dfm.registerDeploymentFactory( dfInstance );

            return dfm;

        }
        catch ( Exception e )
        {

            // log error message
            StringBuilder message = new StringBuilder();
            message.append( "Deployment factory manager initialization failed which exception <" );
            message.append( StackTraceHelper.getStrackTrace( e ) );
            message.append( ">. Command failed." );

            // throw exception
            throw new CommandException( message.toString(), e );

        }
    }

}
