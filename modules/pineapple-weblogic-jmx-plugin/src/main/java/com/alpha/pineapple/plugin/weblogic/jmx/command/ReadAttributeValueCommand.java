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


package com.alpha.pineapple.plugin.weblogic.jmx.command;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.resolvedmodel.ResolvedType;

/**
 * <p>
 * Implementation of the <code>Implementation of the <code>org.apache.commons.chain.Command</code> interface 
 * which reads the value of an attributes from the secondary participant contained in a resolved type. 
 * The contained attributes are expected to be primitives.
 * </p>
 *   
 * <p>
 * Precondition for execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>resolved-type</code> defines the resolved type from to execute the test on. The type is
 * <code>com.alpha.pineapple.plugin.weblogic.jmx.model.resolved.ResolvedType</code>.</li> 
 * 
 * <li><code>execution-result</code> contains execution result object which collects
 * information about the execution of the test. The type is 
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>  
 * </ul>
 * </p>
 * 
 * <p>Postcondition after execution of the command is: 
 * <ul> 
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the test succeeded. If the 
 * test failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the test fails due to an exception then the exception isn't caught, 
 * but passed on the the invoker whose responsibility it is to catch it and update 
 * the <code>ExecutionResult</code> with the state <code>ExecutionState.ERROR</code>.
 * </li>
 * </ul>  
 * </p>        
 */
public class ReadAttributeValueCommand implements Command
{
    /**
     * Key used to identify property in context: Contains execution result object,.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";

    /**
     * Key used to identify property in context: Resolved type containing the values.
     */
    public static final String RESOLVED_TYPE = "resolved-type";
    
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Resolved type.
     */
    @Initialize( RESOLVED_TYPE )
    @ValidateValue( ValidationPolicy.NOT_NULL )        
    ResolvedType resolvedType ;
        
    /**
     * Defines execution result object.
     */
    @Initialize( EXECUTIONRESULT_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )    
    ExecutionResult executionResult;

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
    
    public boolean execute( Context context ) throws Exception
    {
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("ravc.start"));        	
        }
        
        // initialize command
        CommandInitializer initializer = new CommandInitializerImpl();
        initializer.initialize( context, this );
        
        // read attribute
        readAttribute( context );

        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("ravc.completed"));        	
        }
        
        return Command.CONTINUE_PROCESSING;
    }

    /**
     * Read attribute
     * 
     * @param context
     *            Command context.
     * 
     * @throws Exception
     *             If the read operation  fails.
     */
    void readAttribute( Context context ) throws Exception
    {    	
        // validate whether secondary participant was resolved successfully
        if (!resolvedType.getSecondaryParticiant().isResolutionSuccesful() ) {

            // set failed result        	
    		Object[] args = { resolvedType.getSecondaryParticiant().getResolutionErrorAsSingleLineString() };    		
        	executionResult.completeAsFailure(messageProvider, "ravc.secondary_resolution_failed", args);
            return;
        }

        // set successful result
		Object[] args = { resolvedType.getSecondaryParticiant().getName(), 
				resolvedType.getSecondaryParticiant().getValueAsSingleLineString() };    		
    	executionResult.completeAsSuccessful(messageProvider, "ravc.succeed", args);        	
        return;                                
    }         
}
