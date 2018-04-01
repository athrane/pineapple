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

import javax.annotation.Resource;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.test.matchers.PineappleMatchers;

/**
 * implementation of the {@linkplain AssertionHelper} interface.
 */
public class AssertionHelperImpl implements AssertionHelper {

	/**
	 * Hamcrest asserter.
	 */
	@Resource
	Asserter asserter;

	/**
	 * Execution result factory
	 */
	@Resource
	ExecutionResultFactory executionResultFactory;

	public ExecutionResult assertDirectoryExist(File directory, MessageProvider messageProvider, String key,
			ExecutionResult result) {

		// register execution result with asserter
		asserter.setExecutionResult(result);

		// validate directory exists
		Matcher<File> dirMatcher = PineappleMatchers.doesDirectoryExist();
		Object[] args = { directory };
		String description = messageProvider.getMessage(key, args);

		// assert and return result
		return asserter.assertObject(directory, dirMatcher, description);
	}

	public ExecutionResult assertDirectoryExist(File directory, MessageProvider messageProvider, String key) {

		Object[] args = { directory };
		String description = messageProvider.getMessage(key, args);

		// create isolated execution result
		ExecutionResult isolatedResult = executionResultFactory.startExecution(description);

		// assert
		return assertDirectoryExist(directory, messageProvider, key, isolatedResult);
	}

	public ExecutionResult assertFileExist(File file, MessageProvider messageProvider, String key,
			ExecutionResult result) {

		// register execution result with asserter
		asserter.setExecutionResult(result);

		// validate directory exists
		Matcher<File> fileMatcher = PineappleMatchers.doesFileExist();
		Object[] args = { file };
		String description = messageProvider.getMessage(key, args);

		// assert and return result
		return asserter.assertObject(file, fileMatcher, description);
	}

	public ExecutionResult assertFileDoesntExist(File file, MessageProvider messageProvider, String key,
			ExecutionResult result) {

		// register execution result with asserter
		asserter.setExecutionResult(result);

		// validate directory exists
		Matcher<File> fileMatcher = Matchers.not(PineappleMatchers.doesFileExist());
		Object[] args = { file };
		String description = messageProvider.getMessage(key, args);

		// assert and return result
		return asserter.assertObject(file, fileMatcher, description);
	}

	public ExecutionResult assertFileSizeIsSmaller(File file, long legalSize, MessageProvider messageProvider,
			String key, ExecutionResult result) {

		// register execution result with asserter
		asserter.setExecutionResult(result);

		// validate directory exists
		Matcher<File> fileMatcher = PineappleMatchers.isFileSizeSmaller(legalSize);
		Object[] args = { file };
		String description = messageProvider.getMessage(key, args);

		// assert and return result
		return asserter.assertObject(file, fileMatcher, description);
	}

}
