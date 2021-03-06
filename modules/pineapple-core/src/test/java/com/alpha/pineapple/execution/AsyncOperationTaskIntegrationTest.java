/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.execution;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test of the class <code>AsyncOperationTaskImpl</code>.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class AsyncOperationTaskIntegrationTest {

	/**
	 * Object under test.
	 */
	@Resource
	OperationTask asyncOperationTask;

	/**
	 * Object under test.
	 */
	@Resource
	ExecutionResultFactory executionResultFactory;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that task can be looked up from the context.
	 */
	@Test
	public void testCanGetTaskFromContext() {
		assertNotNull(asyncOperationTask);
	}

	/**
	 * Test that execution of operation fails with
	 * {@linkplain UnsupportedOperationException}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testMethodFailsWithUnsupportedOperationException() {
		String operation = RandomStringUtils.randomAlphabetic(10);
		String environment = RandomStringUtils.randomAlphabetic(10);
		String module = RandomStringUtils.randomAlphabetic(10);
		asyncOperationTask.execute(operation, environment, module);
	}

	/**
	 * Test that execution of operation fails with
	 * {@linkplain UnsupportedOperationException}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testMethodFailsWithUnsupportedOperationException2() {
		String operation = RandomStringUtils.randomAlphabetic(10);
		String environment = RandomStringUtils.randomAlphabetic(10);
		String module = RandomStringUtils.randomAlphabetic(10);
		String description = RandomStringUtils.randomAlphabetic(10);
		ExecutionResult result = executionResultFactory.startExecution(description);
		asyncOperationTask.executeComposite(operation, environment, module, description, result);
	}

}
