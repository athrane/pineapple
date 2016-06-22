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

import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.TargetException;

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
import com.alpha.pineapple.jsr88.command.result.AvailableModulesResult;
import com.alpha.pineapple.jsr88.command.result.AvailableModulesResultImpl;

/**
 * <p>Implementation of the <code>org.apache.commons.chain.Command</code> 
 * interface which creates a list of JEE application modules running or not 
 * running on the on the platform. The result is stored as an instance of  
 * <code>AvailableModulesResult</code>.</p>
 * 
 * <p>Precondition for execution of the command is definition of these keys in 
 * the context:
 * 
 * <ul>
 * <li><code>deployment-manager</code> Contains the deployment manager 
 * instance which is to be used to access modules. The type is 
 * <code>javax.enterprise.deploy.spi.DeploymentManager</code>.</li> 
 * </ul>
 * </p> 
 * 
 * <p>Postcondition after execution of the command is definition of these keys 
 * in the context:
 * 
 * <ul>
 * <li><code>result</code> Contains the result object which contains the 
 * available modules. The type is <code>AvailableModulesResult</code>.</li>             
 * </ul>
 * </p>      
 */
public class GetAvailableModulesCommand implements Command
{

    /**
     * Key used to identify property in context: Contains the deployment 
     * manager which should be used to access modules.
     */
    public static final String MANAGER_KEY = GetDeploymentManagerCommand.MANAGER_KEY;

    /**
     * Key used to identify property in context: Contains the result object
     * which contains the available modules after command execution.
     */
    public static final String RESULT_KEY  = "result";
    
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * deployment manager.
     */
    @Initialize( MANAGER_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )                
    DeploymentManager manager;
    
    public boolean execute( Context context ) throws Exception
    {
        // initialize command
        CommandInitializer initializer =  new CommandInitializerImpl();
        initializer.initialize( context, this );
                
        // get targets
        Target[] targets = manager.getTargets();        
        
        // log debug message            
        if(logger.isDebugEnabled()) {
            StringBuilder message = new StringBuilder();
            message.append( "Got targets <" );
            message.append( ToStringBuilder.reflectionToString( targets ) );                
            message.append( ">." );                
         
            logger.debug( message.toString() );
        }

        // create result
        AvailableModulesResult result;
        result =  new AvailableModulesResultImpl();
                
        // get modules        
        getAvailableModules( ModuleType.CAR, targets, result );
        getAvailableModules( ModuleType.EAR, targets, result );
        getAvailableModules( ModuleType.EJB, targets, result );        
        getAvailableModules( ModuleType.RAR, targets, result );        
        getAvailableModules( ModuleType.WAR, targets, result );
        
        // store result in context
        context.put( RESULT_KEY, result );
                
        return Command.CONTINUE_PROCESSING;
    }

    /**
     * Get available modules of specific type and add them to result object.
     * 
     * @param type module type.
     * @param targets Available targets.
     * @param result Result object.
     * @throws CommandException If modules retrieval fails.
     * @throws TargetException 
     */
    void getAvailableModules( ModuleType type, Target[] targets, AvailableModulesResult result ) throws CommandException 
    {
        try
        {
            // get war modules 
            TargetModuleID[] modules;            
            modules = manager.getAvailableModules( type, targets );
            
            // log debug message            
            if(logger.isDebugEnabled()) {
                StringBuilder message = new StringBuilder();
                message.append( "Got <" );
                message.append( type.getModuleExtension() );
                message.append( "> type modules <" );                
                message.append( ToStringBuilder.reflectionToString( modules ) );                
                message.append( ">." );                
             
                logger.debug( message.toString() );
            }
            
            // if null then create null array
            if ( modules == null ) {
                modules = new TargetModuleID[0]; 
            }
            
            // add modules
            result.setModules( type, modules );
            
        }
        catch ( Exception e )
        {
            // log error message
            StringBuilder message = new StringBuilder();
            message.append( "Getting available modules of type <" );
            message.append( type.getModuleExtension() );
            message.append( "> failed which exception <" );            
            message.append( StackTraceHelper.getStrackTrace( e ) );
            message.append( ">. Command failed." );

            // throw exception
            throw new CommandException( message.toString(), e);
        }
    }

}
