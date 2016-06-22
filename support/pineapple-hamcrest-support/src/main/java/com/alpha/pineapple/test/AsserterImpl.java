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


package com.alpha.pineapple.test;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Default implementation of the {@link Asserter} interface.
 */
public class AsserterImpl implements Asserter {

	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
        	
    /**
     * Message provider for I18N support.
     */
    @Resource(name="hamcrestMessageProvider")
    MessageProvider messageProvider;
    
    /**
     * Root execution result. 
     */
    ExecutionResult rootResult;

    /**
     * Result of last assertion.
     */
	ExecutionResult lastAssertionResult;

	public void setExecutionResult(ExecutionResult result) {
		rootResult = result;		
	}
	
	@SuppressWarnings("unchecked")
	public ExecutionResult assertObject(Object actual, Matcher matcher, String description)	
    {
        // log debug message
        if ( logger.isDebugEnabled() )        	
        {
        	StringBuilder message = new StringBuilder();
        	message .append("Starting to assert <");
        	message .append( description);
        	message .append("> with matcher <");
        	message .append( matcher );
        	message .append(">.");        	        	
        	logger.debug( message.toString() );        	
        }
        
		// create execution result object
		ExecutionResult childResult = rootResult.addChild( description );		
		
        try
        {        	        	
            if(matcher.matches(actual))
            {            	
            	// create description of successful test outcome
                Description message = new StringDescription();                
                message.appendText("Asserted that ");
                matcher.describeTo(message);
                message.appendText(".");            	
                
            	// set execution state
            	childResult.setState(ExecutionState.SUCCESS);                
            	childResult.addMessage("Message", message.toString() );
            	
	        	// register last assertion result
	        	lastAssertionResult = childResult;

                // log debug message
                if ( logger.isDebugEnabled() )        	
                {
                	StringBuilder debugMessage = new StringBuilder();
                	debugMessage.append("Successfully completed assertion with positive result <");
                	debugMessage.append( message.toString() );
                	debugMessage.append(">.");        	        	
                	logger.debug( debugMessage.toString() );        	
                }
            	
                return childResult;
             } 
                       
        	// create description of failed test outcome 
            Description message = new StringDescription();                
            message.appendText("Failed to assert ");
            matcher.describeTo( message );
            message.appendText(". Assertion failed because  ");      		
            matcher.describeMismatch( actual, message );            
            message.appendText(".");
            
        	// set execution state
        	childResult.setState(ExecutionState.FAILURE);                
        	childResult.addMessage("Message", message.toString() );        	        	
        	
        	// register last assertion result
        	lastAssertionResult = childResult;

            // log debug message
            if ( logger.isDebugEnabled() )        	
            {
            	StringBuilder debugMessage = new StringBuilder();
            	debugMessage.append("Successfully completed assertion with negative result <");
            	debugMessage.append( message.toString() );
            	debugMessage.append(">.");        	        	
            	logger.debug( debugMessage.toString() );        	
            }
        	
            return childResult;        	
        }        
        catch ( Exception e )
        {
        	
        	// create description of outcome with error
            Description message = new StringDescription();
            message.appendText("Assertion of [");            
            matcher.describeTo(message);
            message.appendText("] terminated due to exception. ");

        	// set execution state
            childResult.setState(ExecutionState.ERROR);                        
        	childResult.addMessage("Message", message.toString() );        	
        	childResult.addMessage("StackTrace", StackTraceHelper.getStrackTrace( e ));

        	// register last assertion result
        	lastAssertionResult = childResult;
        	        	
            return childResult;
        }        
    }


	

	public ExecutionResult assertWithoutCollectingExecutionResult(Object actual, Matcher matcher, String description) {

		// log debug message
        if ( logger.isDebugEnabled() )        	
        {
        	StringBuilder message = new StringBuilder();
        	message .append("Starting to execute isolated assert <");
        	message .append( description);
        	message .append("> with matcher <");
        	message .append( matcher.getClass() );
        	message .append(">.");        	        	
        	logger.debug( message.toString() );        	
        }

		// create execution result object
		ExecutionResult childResult = new ExecutionResultImpl(null, description);		
		
	       try
	        {        	        	
	            if(matcher.matches(actual))
	            {            	
	            	// create description of successful test outcome
	                Description message = new StringDescription();                
	                message.appendText("Asserted that ");
	                matcher.describeTo(message);
	                message.appendText(".");            	
	                
	            	// set execution state
	            	childResult.setState(ExecutionState.SUCCESS);                
	            	childResult.addMessage("Message", message.toString() );
	            	
		        	// register last assertion result
		        	lastAssertionResult = childResult;

	                // log debug message
	                if ( logger.isDebugEnabled() )        	
	                {
	                	StringBuilder debugMessage = new StringBuilder();
	                	debugMessage.append("Successfully completed assertion with positive result <");
	                	debugMessage.append( message.toString() );
	                	debugMessage.append(">.");        	        	
	                	logger.debug( debugMessage.toString() );        	
	                }
	            	
	                return childResult;
	             } 
	                       
	        	// create description of failed test outcome 
	            Description message = new StringDescription();                
	            message.appendText("Failed to assert that ");
	            matcher.describeTo( message );            
	            message.appendText(". The reason for the failure is that ");      		
	            matcher.describeMismatch( actual, message );            
	            message.appendText(".");
	            
	        	// set execution state
	        	childResult.setState(ExecutionState.FAILURE);                
	        	childResult.addMessage("Message", message.toString() );        	        	
	        	
	        	// register last assertion result
	        	lastAssertionResult = childResult;

	            // log debug message
	            if ( logger.isDebugEnabled() )        	
	            {
	            	StringBuilder debugMessage = new StringBuilder();
	            	debugMessage.append("Successfully completed assertion with negative result <");
	            	debugMessage.append( message.toString() );
	            	debugMessage.append(">.");        	        	
	            	logger.debug( debugMessage.toString() );        	
	            }
	        	
	            return childResult;        	
	        }        
	        catch ( Exception e )
	        {
	        	
	        	// create description of outcome with error
	            Description message = new StringDescription();
	            message.appendText("Assertion of [");            
	            matcher.describeTo(message);
	            message.appendText("] terminated due to exception. ");

	        	// set execution state
	            childResult.setState(ExecutionState.ERROR);                        
	        	childResult.addMessage("Message", message.toString() );        	
	        	childResult.addMessage("StackTrace", StackTraceHelper.getStrackTrace( e ));

	        	// register last assertion result
	        	lastAssertionResult = childResult;
	        	
	            return childResult;
	        }   		
	}

	public boolean lastAssertionSucceeded() {
		if (lastAssertionResult == null) return false; 
		return (lastAssertionResult.getState() == ExecutionState.SUCCESS);
	}
	
	public ExecutionResult getLastAssertionResult() {
		return lastAssertionResult;
	}

	public void completeTestAsSuccessful(MessageProvider messageProvider, String key, Object[] args) {
		
		// set test as successful 
		rootResult.completeAsSuccessful(messageProvider, key, args);		
	}	

}
