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

import java.io.File;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * interface for helper class which provides methods for asserting hamcrest
 * matchers and storing the results in execution results. 
 */
public interface AssertionHelper {

	/**
	 * Asserts whether the directory exists, e.g. the asserted file object represents a directory
	 * and that the directory exists.
	 * 
	 * The result of the assertion is added as a child execution result to
	 * the supplied execution result. 
	 * 
	 * The execution result is described by a lookup in the supplied message provider using
	 * the key parameter. The name of the directory is substituted into the message as argument #0.   
	 * 
	 * @param directory File object which contains directory to be validated. 
	 * @param messageProvider Message provider used to lookup matcher description.
	 * @param key Key used to lookup matcher description in message provider.
	 * @param result Execution result to which the assertion result is added as child.
	 * 
	 * @return the child execution result which contains the result of the assertion. 
	 */
	public ExecutionResult assertDirectoryExist(File directory, MessageProvider messageProvider, String key, ExecutionResult result );

	/**
	 * Asserts whether the directory exists, e.g. the asserted file object represents a directory
	 * and that the directory exists. The result of the assertion is returned as an execution result.
	 * 
	 * The execution result is described by a lookup in the supplied message provider using
	 * the key parameter. The name of the directory is substituted into the message as argument #0.   
	 * 
	 * @param directory File object which contains directory to be validated. 
	 * @param messageProvider Message provider used to lookup matcher description.
	 * @param key Key used to lookup matcher description in message provider.
	 * 
	 * @return execution result which contains the result of the assertion. 
	 */
	public ExecutionResult assertDirectoryExist(File directory, MessageProvider messageProvider, String key );
	
	/**
	 * Asserts whether the file exists, e.g. the asserted file object represents a file 
	 * and that the file exists.
	 * 
	 * The result of the assertion is added as a child execution result to
	 * the supplied execution result. 
	 * 
	 * The execution result is described by a lookup in the supplied message provider using
	 * the key parameter. The name of the file is substituted into the message as argument #0.   
	 * 
	 * @param directory File object which contains directory to be validated. 
	 * @param messageProvider Message provider used to lookup matcher description.
	 * @param key Key used to lookup matcher description in message provider.
	 * @param result Execution result to which the assertion result is added as child.
	 * 
	 * @return the child execution result which contains the result of the assertion.
	 */
	public ExecutionResult assertFileExist(File file, MessageProvider messageProvider, String key, ExecutionResult result );

	/**
	 * Asserts whether the file doesn't exists, e.g. the asserted file object 
	 * represents a file and that the file exists.
	 * 
	 * The result of the assertion is added as a child execution result to
	 * the supplied execution result. 
	 * 
	 * The execution result is described by a lookup in the supplied message provider using
	 * the key parameter. The name of the file is substituted into the message as argument #0.   
	 * 
	 * @param directory File object which contains directory to be validated. 
	 * @param messageProvider Message provider used to lookup matcher description.
	 * @param key Key used to lookup matcher description in message provider.
	 * @param result Execution result to which the assertion result is added as child.
	 * 
	 * @return the child execution result which contains the result of the assertion.
	 */
	public ExecutionResult assertFileDoesntExist(File file, MessageProvider messageProvider, String key, ExecutionResult result );

	/**
	 * Asserts whether the file size is smaller than maximum legal value (in bytes).
	 * 
	 * The result of the assertion is added as a child execution result to
	 * the supplied execution result. 
	 * 
	 * The execution result is described by a lookup in the supplied message provider using
	 * the key parameter. The name of the file is substituted into the message as argument #0.   
	 * 
	 * @param directory File object which contains file to be validated.
	 * @param legalSize maximum legal value in bytes.
	 * @param messageProvider Message provider used to lookup matcher description.
	 * @param key Key used to lookup matcher description in message provider.
	 * @param result Execution result to which the assertion result is added as child.
	 * 
	 * @return the child execution result which contains the result of the assertion.
	 */	
	public ExecutionResult assertFileSizeIsSmaller(File file, long legalSize, MessageProvider messageProvider, String key, ExecutionResult result );
	
}
