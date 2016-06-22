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

import org.hamcrest.Matcher;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 *
 * Asserter which can assert Hamcrest {@link matcher} objects and store the result
 * in execution result object.
 */
public interface Asserter {

	   /**
     * Set the root execution result object for the asserter.
     *  
     * @param result The root execution result object. 
     */
    public void setExecutionResult(ExecutionResult result);

	/**
	 * Assert that an actual object matches a {@link org.hamcrest.Matcher} object. 
	 * 
	 * The result of the test is stored in the returned execution result object. 
	 * The result object is also added as a child result to the root execution 
	 * result object contained by the asserter object.
	 * 
	 * @param actual The actual object which is matched with the matcher object.
	 * @param matcher Matcher object which performs the assertion of some state.
	 * @param result Execution result object to whom the result of the test is added as a child object.
	 * @param description A description of the test. The description is added to the returned execution result object.        
	 */
	public ExecutionResult assertObject(Object actual, Matcher matcher, String description);

	/**
	 * Assert that an actual object matches a {@link org.hamcrest.Matcher} object. 
	 * 
	 * The result of the test is stored in the returned execution result object. 
	 * The result object isn't added as a child result to the root execution result object 
	 * contained by the asserter object.
	 * 
	 * @param actual The actual object which is matched with the matcher object.
	 * @param matcher Matcher object which performs the assertion of some state.
	 * @param result Execution result object to whom the result of the test is added as a child object.
	 * @param description A description of the test. The description is added to the returned execution result object.        
	 */
	public ExecutionResult assertWithoutCollectingExecutionResult(Object actual, Matcher matcher, String description);	
	
	/**
	 * Return true if the last assertion succeeded.
	 * 
	 * @return true if the last assertion succeeded. If no assertions
	 * have been executed then false is returned.
	 */
	public boolean lastAssertionSucceeded();

	/**
	 * Return the execution result of the last assertion. 
	 * 
	 * @return the execution result of the last assertion. If no assertions
	 * have been executed then null is returned.
	 */
	public ExecutionResult getLastAssertionResult();
	
	/**
	 * Set the state of the contained execution result as successful and add a message
	 * which is looked up from the message provider object. The looked up message is
	 * stored under the key 'Message'.   
	 * 
	 * @param messageProvider The message source used to look up the message.
	 * 
	 * @param key The key to lookup up in the message provider, such as 'calculator.noRateSet'. 
	 * Users of this class are encouraged to base message names on the relevant fully qualified class name, 
	 * thus avoiding conflict and ensuring maximum clarity.
	 * 
	 * @param args Array of arguments that will be filled in for params within the message 
	 * (params look like "{0}", "{1,date}", "{2,time}" within a message), or null if none. 
	 */
	public void completeTestAsSuccessful(MessageProvider messageProvider, String key, Object[] args);
	
}
