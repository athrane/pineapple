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
import com.alpha.pineapple.plugin.weblogic.jmx.model.xmlbeans.XmlBeansModelAccessor;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedType;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface which 
 * tests the values of two resolved attributes contained in a traversal pair object. 
 * <p>
 * The primary participant is expected to contains an XMLBean enum, e.g. a type derived from 
 * <code>StringEnumAbstractBase</code>. The secondary participant is expected to contain an
 * <code>java.lang.String</code>.  
 * </p>
 *   
 * <p>
 * Precondition for execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>resolved-type</code> defines the resolved type to execute the test on. The type is
 * <code>com.alpha.pineapple.resolvedmodel.ResolvedType</code>.</li> 
 *  
 * <li><code>execution-result</code> contains execution result object which collects
 * information about the execution of the test. The type is 
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>    
 * </ul>  
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
public class TestEnumValueCommand implements Command
{	
    /**
     * Key used to identify property in context: Resolved type containing the values.
     */
    public static final String RESOLVED_TYPE = "resolved-type";

    /**
     * Key used to identify property in context: Contains execution result object,.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";
    
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
     * XMLBean model accessor.
     */
    @Resource
    XmlBeansModelAccessor xmlBeansModelAccessor;
    
    /**
     * Resolved type.
     */
    @Initialize( RESOLVED_TYPE )
    @ValidateValue( ValidationPolicy.NOT_NULL )        
    ResolvedType resolvedType;

    /**
     * Defines execution result object.
     */
    @Initialize( EXECUTIONRESULT_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )    
    ExecutionResult executionResult;

    	
    public boolean execute( Context context ) throws Exception
    {
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("tevc.start"));        	
        }
    	    	
        // initialize command
        CommandInitializer initializer =  new CommandInitializerImpl();
        initializer.initialize( context, this );
        
        // run test
        doTest( context );

        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("tevc.completed"));        	
        }
        
        return Command.CONTINUE_PROCESSING;
    }

    
    /**
     * Do test.
     * 
     * @param context
     *            Command context.
     * 
     * @throws Exception
     *             If test execution fails.
     */
    void doTest( Context context ) throws Exception
    {
        // validate whether primary participant was resolved successfully
        if (!resolvedType.getPrimaryParticipant().isResolutionSuccesful() ) {
        	
            // set failed result        	
    		Object[] args = { resolvedType.getPrimaryParticipant().getResolutionErrorAsSingleLineString() };        	
        	executionResult.completeAsFailure(messageProvider, "tevc.primary_resolution_failed", args);
            return;
        }

        // validate whether secondary participant was resolved successfully
        if (!resolvedType.getSecondaryParticiant().isResolutionSuccesful() ) {

            // set failed result        	
    		Object[] args = { resolvedType.getSecondaryParticiant().getResolutionErrorAsSingleLineString() };        	
        	executionResult.completeAsFailure(messageProvider, "tevc.secondary_resolution_failed", args);
            return;        	
        }
    	
        // get type of primary participant
        ResolvedParticipant primary = resolvedType.getPrimaryParticipant();
        
        // validate that the primary participant is XMLBeans enum type
        if (!(xmlBeansModelAccessor.isEnum(primary)))  {

            // set failed result        	
    		Object[] args = { resolvedType.getPrimaryParticipant().getType() };        	
        	executionResult.completeAsFailure(messageProvider, "tevc.primary_type_failed", args);
            return;        	        	
        }

        // get type of secondary participant
        Object secondaryType = resolvedType.getSecondaryParticiant().getType();
                                                        
        // validate whether secondary participant is string or int type
        if (!secondaryType.equals( String.class ) && (!secondaryType.equals( int.class )))  {
        	
            // set failed result        	
    		Object[] args = { resolvedType.getSecondaryParticiant().getType() };        	
        	executionResult.completeAsFailure(messageProvider, "tevc.secondary_type_failed", args);
            return;        	        	        	
        }
        
        // get value #1   
        Object value1 = resolvedType.getPrimaryParticipant().getValue();

        // get value #2 
        Object value2 = resolvedType.getSecondaryParticiant().getValue();
        
        // handle null case
        if ( value1 == null) {
            if (value2 == null) {
        
                // set successful result            	
            	executionResult.completeAsSuccessful(messageProvider, "tevc.null_succeed");
                return;
            }
        
            // primary is null and secondary isn't == not equal
            // set failed result        	
    		Object[] args = { resolvedType.getPrimaryParticipant().getValueAsSingleLineString(), 
    				resolvedType.getSecondaryParticiant().getValueAsSingleLineString() };        	
        	executionResult.completeAsFailure(messageProvider, "tevc.not_equal_failed", args);
            return;        	        	        	            
        }

        // handle case where primary is defined and secondary is null
        if (value2 == null) {

            // primary isn't null and secondary is null == not equal
            // set failed result        	
    		Object[] args = { resolvedType.getPrimaryParticipant().getValueAsSingleLineString(), 
    				resolvedType.getSecondaryParticiant().getValueAsSingleLineString() };        	
        	executionResult.completeAsFailure(messageProvider, "tevc.not_equal_failed", args);
            return;        	        	        	                    	
        }
    
        // test enum (as strings) value
        if ( value1.toString().equals( value2.toString() )) {
        	
            // set positive result
    		Object[] args = { resolvedType.getPrimaryParticipant().getValueAsSingleLineString(), 
    				resolvedType.getSecondaryParticiant().getValueAsSingleLineString() };        	
        	executionResult.completeAsSuccessful(messageProvider, "tevc.succeed", args);        	
        } else {
        	
            // set failed result
    		Object[] args = { resolvedType.getPrimaryParticipant().getValueAsSingleLineString(), 
    				resolvedType.getSecondaryParticiant().getValueAsSingleLineString() };        	
        	executionResult.completeAsFailure(messageProvider, "tevc.not_equal_failed", args);
            return;            
        }                
    }
           
}
