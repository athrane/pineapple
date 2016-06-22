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


package com.alpha.pineapple.plugin.weblogic.deployment.session;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.enterprise.deploy.spi.DeploymentManager;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

import weblogic.Deployer;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.jsr88.command.GetAvailableModulesCommand;
import com.alpha.pineapple.jsr88.command.GetDeploymentManagerCommand;
import com.alpha.pineapple.jsr88.command.result.AvailableModulesResult;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.plugin.PluginSession;
import com.alpha.pineapple.resource.ResourceException;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.alpha.pineapple.session.SessionException;

/**
 * Implementation of the [{@link WeblogicDeploymentSession}.
 */
@PluginSession
public class WeblogicDeploymentSessionImplOLD implements WeblogicDeploymentSession
{

    /**
     * JSR88 WebLogic deployment factory.
     */
    static final String JSR88_FACTORY = "weblogic.deploy.api.spi.factories.internal.DeploymentFactoryImpl";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
    
    /**
     * Resource getter object.
     */
    @Resource    
    ResourcePropertyGetter propertyGetter;
    
    /**
     * Credential object.
     */
    Credential credential;

    /**
     * Resource object.
     */
    com.alpha.pineapple.model.configuration.Resource resource;
        
    /**
     * WebLogic deployer.
     */
    Deployer deployer;
       
    /**
     * WeblogicDeploymentSessionImpl constructor.
     */
    public WeblogicDeploymentSessionImplOLD()
    {
        super();
    }

    public void connect( com.alpha.pineapple.model.configuration.Resource resource, Credential credential ) throws SessionException
    {
        // validate parameters
        Validate.notNull( resource, "resource is undefined." );
        Validate.notNull( credential, "credential is undefined." );

        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resource, credential.getId() };    	        	
        	String message = messageProvider.getMessage("wds.connect", args );
        	logger.debug( message );
        }
        
        // store in fields
        this.credential = credential;
        this.resource = resource;
        
        // initialize property getter
        this.propertyGetter.setResource(resource);
    }

    public void disconnect() throws SessionException
    {
        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	String message = messageProvider.getMessage("wds.disconnect" );
        	logger.debug( message );
        }
    }

	public com.alpha.pineapple.model.configuration.Resource getResource() {		
		return this.resource;
	}

	public Credential getCredential() {
		return this.credential;
	}
            
    public void invokeDeployer( List<String> deployerArguments ) throws SessionException
    {
        // validate parameters
        Validate.notNull( deployerArguments, "deployerArguments is undefined." );
        Validate.notEmpty( deployerArguments, "deployerArguments is empty." );

        // log debug message
        if ( logger.isDebugEnabled() )
        {
            // create array to print
            String[] stringArray;
            stringArray = (String[]) deployerArguments.toArray( new String[deployerArguments.size()] );
            
            // create debug message
            StringBuilder message = new StringBuilder();
            message.append( "Session invoked with deployer arguments <" );
            message.append( ReflectionToStringBuilder.toString( stringArray ) );
            message.append( ">." );

            // log debug message
            logger.debug( message.toString() );
        }

        // validate session is connected
        if ( !isconnected() )
        {
            // create error message
            StringBuilder message = new StringBuilder();
            message.append( "Deployment invocation failed. " );
            message.append( "Session must be connected before deployment operation is invoked. " );

            throw new IllegalStateException( message.toString() );
        }

        // create deployer argument list
        List<String> argumentsList = new ArrayList<String>();

        // add connection arguments
        addConnectionArguments( argumentsList );

        // add credential arguments
        addCredentialArguments( argumentsList );

        // add operation arguments
        argumentsList.addAll( deployerArguments );

        // commons arguments
        addCommonArguments( argumentsList );

        // convert to array
        String[] stringArray;
        stringArray = (String[]) argumentsList.toArray( new String[argumentsList.size()] );

        // log debug message
        if ( logger.isDebugEnabled() )
        {
            // create debug message
            StringBuilder message = new StringBuilder();
            message.append( "Will invoke weblogic.Deployer with arguments <" );
            message.append( ReflectionToStringBuilder.toString( stringArray ) );
            message.append( ">." );

            // log debug message
            logger.debug( message.toString() );
        }

        executeOperation( stringArray );
    }

    /**
     * Return true if session is connected.
     * 
     * @return true if session is connected.
     */
    boolean isconnected()
    {
        return (this.resource != null);
    }

    /**
     * Execution Deployer operation.
     * 
     * @param arguments
     *            deployer arguments
     * @throws SessionException
     *             If deployer invocation fails
     */
    void executeOperation( String[] arguments ) throws SessionException
    {
        // Deploy
        try
        {
            // create deployer if not created
            if ( this.deployer == null )
            {

                // log debug message
                if ( logger.isDebugEnabled() )
                {
                    // create debug message
                    StringBuilder message = new StringBuilder();
                    message.append( "weblogic.Deployer instance not found. " );
                    message.append( "Will create new instance." );

                    // log debug message
                    logger.debug( message.toString() );
                }

                // create instance
                this.deployer = new Deployer( new String[0] );
            }

            // invoke deployer
            deployer.run( arguments );
        }
        catch ( Exception e )
        {
            // create error message
            StringBuilder message = new StringBuilder();
            message.append( "Weblogic.Deployer invocation failed. " );

            throw new SessionException( message.toString(), e );
        }
    }

    /**
     * Add connection arguments to weblogic.deployer argument list. The connection arguments defines which server URL to
     * access.
     * 
     * @param parameterList
     *            Parameter list to which the arguments is added.
     * 
     * @throws SessionException
     *             If argument addition fails.
     */
    void addConnectionArguments( List<String> parameterList ) throws SessionException
    {
        // add administration URL
        parameterList.add( "-adminurl" );
        parameterList.add( createAdminURL() );
    }

    /**
     * Add credential arguments to weblogic.deployer argument list. The credential arguments defines user name and
     * password used to log on to the administration server..
     * 
     * @param parameterList
     *            Parameter list to which the arguments is added.
     * 
     */
    void addCredentialArguments( List<String> parameterList )
    {
        // add user name and password
        parameterList.add( "-username" );
        parameterList.add( credential.getUser() );
        parameterList.add( "-password" );
        parameterList.add( credential.getPassword() );
    }

    /**
     * Add common arguments to weblogic.deployer argument list.
     * 
     * @param parameterList
     *            Parameter list to which the arguments is added.
     */
    void addCommonArguments( List<String> parameterList )
    {
        // set debug mode
        parameterList.add( "-debug" );

        // set no-exit mode
        parameterList.add( "-noexit" );

        // set remote mode
        parameterList.add( "-remote" );

        // set verbose mode
        parameterList.add( "-verbose" );
    }

    /**
     * Create URL to WebLogic administration server.
     * 
     * @return URL to WebLogic administration server.
     * 
     * @throws SessionException
     *             If accessing session properties fails.
     */
    String createAdminURL() throws SessionException
    {
        try
        {
            StringBuilder url = new StringBuilder();
            url.append( propertyGetter.getProperty( PROTOCOL_PROPERTY ) );
            url.append( "://" );
            url.append( propertyGetter.getProperty( LISTENADDRESS_PROPERTY ) );
            url.append( ":" );
            url.append( propertyGetter.getProperty( LISTENPORT_PROPERTY ) );
            return url.toString();
        }
        catch ( ResourceException e )
        {
            // create error message
            StringBuilder message = new StringBuilder();
            message.append( "Administration URL creation failed with exception <" );
            message.append( StackTraceHelper.getStrackTrace( e ) );
            message.append( ">. " );

            throw new SessionException( message.toString(), e );
        }

    }

    /**
     * Create JSR88 URI to access WebLogic administration server.
     * 
     * @return JSR88 URI to access WebLogic administration server.
     * 
     * @throws SessionException If URI creation fails.
     */
    String createJsr88Uri() throws SessionException
    {
        StringBuilder jsr88URI = new StringBuilder();            
        jsr88URI.append( "deployer:WebLogic:" );
        jsr88URI.append( this.createAdminURL() );
        return jsr88URI.toString();
    }
    
    
    public boolean isTimeStampEnabled() throws SessionException
    {
        try
        {
            if(!propertyGetter.containsProperty( TIMESTAMP_PROPERTY )) return false;
            
            // get value
            String value = propertyGetter.getProperty( TIMESTAMP_PROPERTY );
            
            // parse to boolean 
            return Boolean.parseBoolean( value );
        }
        catch ( ResourceException e )
        {
            // create error message
            StringBuilder message = new StringBuilder();
            message.append( "Time stamp property resolution failed with exception <" );
            message.append( StackTraceHelper.getStrackTrace( e ) );
            message.append( ">. " );
            
            throw new SessionException( message.toString(), e );
        }        
    }

    public AvailableModulesResult getAvailableModules() throws SessionException
    {
        try
        {                                               
            // create context
            Context context;
            context = new ContextBase();
            
            // get manager
            DeploymentManager manager;
            manager = getDeploymentManager();

            // setup context for command
            context.put( GetAvailableModulesCommand.MANAGER_KEY, manager );

            // create get deployment manager command
            Command command = new GetAvailableModulesCommand();
            
            // execute command
            command.execute( context );       
            
            // get result
            AvailableModulesResult result;
            result = (AvailableModulesResult) context.get( GetAvailableModulesCommand.RESULT_KEY ); 
            
            // release manager
            manager.release();
            
            return result;                        
        }
        catch ( Exception e )
        {
            // create error message
            StringBuilder message = new StringBuilder();
            message.append( "Available J2EE modules retrieval failed with exception <" );
            message.append( StackTraceHelper.getStrackTrace( e ) );
            message.append( ">. " );

            throw new SessionException( message.toString(), e );
        }                     
    }

    public DeploymentManager getDeploymentManager() throws SessionException
    {
        try
        {                                   
            // create jsr88 URI
            String uri = createJsr88Uri();            
            
            // create context
            Context context;
            context = new ContextBase();
            context.put( GetDeploymentManagerCommand.USER_KEY, credential.getUser() );
            context.put( GetDeploymentManagerCommand.PASSWORD_KEY, credential.getPassword() );            
            context.put( GetDeploymentManagerCommand.URI_KEY, uri );                
            context.put( GetDeploymentManagerCommand.FACTORY_KEY, JSR88_FACTORY );
            
            // create get deployment manager command
            Command command = new GetDeploymentManagerCommand();
            
            // execute command            
            command.execute( context );
            
            // get manager
            DeploymentManager manager;
            manager = (DeploymentManager) context.get( GetDeploymentManagerCommand.MANAGER_KEY );
            
            return manager;                        
        }
        catch ( Exception e )
        {
            // create error message
            StringBuilder message = new StringBuilder();
            message.append( "Deployment manager retrieval failed with exception <" );
            message.append( StackTraceHelper.getStrackTrace( e ) );
            message.append( ">. " );

            throw new SessionException( message.toString(), e );
        }                     
    }

    
}
