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

import javax.enterprise.deploy.spi.DeploymentManager;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;

/**
 * Implementation of the {@link Command} interface, which releases an 
 * JSR88 deployment manager.
 * <br/> The command releases a deployment manager contained by the key MANAGER_KEY. 
 */

/**
 * <p>Implementation of the <code>org.apache.commons.chain.Command</code> 
 * interface which which releases an JSR88 deployment manager.</p>
 * 
 * <p>Precondition for execution of the command is definition of these keys in 
 * the context:
 * 
 * <ul>
 * <li><code>deployment-manager</code> Contains the deployment manager 
 * instance which is to be released. The type is 
 * <code>javax.enterprise.deploy.spi.DeploymentManager</code>.</li> 
 * </ul>
 * </p> 
 * 
 * <p>Postcondition after execution of the command is definition of these keys 
 * in the context:
 * 
 * <ul>
 * <li>No changes to the context but the deployment manager is released.</li>             
 * </ul>
 * </p>      
 */
public class ReleaseDeploymentManagerCommand implements Command
{

    /**
     * Key used to identify property in context: Contains the deployment 
     * manager which should be released.
     */
    public static final String MANAGER_KEY = GetDeploymentManagerCommand.MANAGER_KEY;
    
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
        
        // release deployment manager
        manager.release();
        
        return Command.CONTINUE_PROCESSING;
    }

}
