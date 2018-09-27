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

package com.alpha.pineapple.command.execution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;

/**
 * Unit test of the class {@link CommandRunnerImpl}
 */
public class CommandRunnerImplTest {

	/**
	 * Object under test.
	 */
	CommandRunner commandRunner;

	/**
	 * Mock execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Mock command
	 */
	Command command;

	/**
	 * Mock context
	 */
	Context context;

	/**
	 * Random description.
	 */
	String randomDescription;

	/**
	 * Random description.
	 */
	String randomExceptionCause;

	@Before
	public void setUp() throws Exception {
		randomDescription = RandomStringUtils.randomAlphabetic(10);
		randomExceptionCause = RandomStringUtils.randomAlphabetic(10);

		// create asserter
		commandRunner = new CommandRunnerImpl();

		// create mock result
		executionResult = EasyMock.createMock(ExecutionResult.class);

		// create mock command
		command = EasyMock.createMock(Command.class);

		// create mock context
		context = EasyMock.createMock(Context.class);
	}

	@After
	public void tearDown() throws Exception {
		commandRunner = null;
		executionResult = null;
	}

	/**
	 * Get execution result initially returns created result.
	 */
	@Test
	public void testGetExecutionResultInitiallyCreatesNewResult() {
		assertNotNull(commandRunner.getExecutionResult());
	}

	@Test
	public void testSetExecutionResult() {
		commandRunner.setExecutionResult(executionResult);
		assertEquals(executionResult, commandRunner.getExecutionResult());
	}

	/**
	 * Test that context can be created
	 */
	@Test
	public void testCreateContext() {
		assertNotNull(commandRunner.createContext());
	}

	/**
	 * Test that created context has correct type.
	 */
	@Test
	public void testCreatedContextHasCorrectType() {
		assertTrue(commandRunner.createContext() instanceof ContextBase);
	}

	/**
	 * Test that the method LastExecutionSucceeded() initially returns false.
	 */
	@Test
	public void testLastExecutionSucceededIntiallyReturnsFalse() {
		assertFalse(commandRunner.lastExecutionSucceeded());
	}

	/**
	 * Test that command can be run.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanRunCommand() throws Exception {

		// complete command setup
		EasyMock.expect(command.execute(context)).andReturn(false);
		EasyMock.replay(command);

		// create child mock result
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		EasyMock.expect(childResult.getState()).andReturn(ExecutionState.SUCCESS);
		EasyMock.replay(childResult);

		// complete result setup
		EasyMock.expect(executionResult.addChild(randomDescription)).andReturn(childResult);
		EasyMock.replay(executionResult);

		// complete context setup
		EasyMock.expect(context.put(CommandRunnerImpl.EXECUTION_RESULT_CONTEXT_KEY, childResult)).andReturn(null);
		EasyMock.replay(context);

		// run
		commandRunner.setExecutionResult(executionResult);
		commandRunner.run(command, randomDescription, context);

		// test

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(command);
		EasyMock.verify(context);
		EasyMock.verify(childResult);
	}

	/**
	 * Test that command failing with an exception result in in execution state
	 * ERROR and the exception is added to the execution result.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testRunHandlesCommandWhichFailsWithException() throws Exception {
		Exception exception = new Exception(randomExceptionCause);

		// complete command setup
		EasyMock.expect(command.execute(context)).andThrow(exception);
		EasyMock.replay(command);

		// create child mock result
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		childResult.completeAsError(exception);
		EasyMock.expect(childResult.getState()).andReturn(ExecutionState.SUCCESS);
		EasyMock.replay(childResult);

		// complete result setup
		EasyMock.expect(executionResult.addChild(randomDescription)).andReturn(childResult);
		EasyMock.replay(executionResult);

		// complete context setup
		EasyMock.expect(context.put(CommandRunnerImpl.EXECUTION_RESULT_CONTEXT_KEY, childResult)).andReturn(null);
		EasyMock.replay(context);

		// run
		commandRunner.setExecutionResult(executionResult);
		commandRunner.run(command, randomDescription, context);

		// test

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(command);
		EasyMock.verify(context);
		EasyMock.verify(childResult);

	}

	/**
	 * Test that the method LastExecutionSucceeded() returns true of command
	 * succeeds.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testLastExecutionSucceededReturnsTrueIfSuccess() throws Exception {

		// complete command setup
		EasyMock.expect(command.execute(context)).andReturn(false);
		EasyMock.replay(command);

		// create child mock result
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		EasyMock.expect(childResult.getState()).andReturn(ExecutionState.SUCCESS);
		EasyMock.replay(childResult);

		// complete result setup
		EasyMock.expect(executionResult.addChild(randomDescription)).andReturn(childResult);
		EasyMock.replay(executionResult);

		// complete context setup
		EasyMock.expect(context.put(CommandRunnerImpl.EXECUTION_RESULT_CONTEXT_KEY, childResult)).andReturn(null);
		EasyMock.replay(context);

		// run
		commandRunner.setExecutionResult(executionResult);
		commandRunner.run(command, randomDescription, context);

		// test
		assertTrue(commandRunner.lastExecutionSucceeded());

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(command);
		EasyMock.verify(context);
		EasyMock.verify(childResult);
	}

	/**
	 * Test that runner rejects undefined command name.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRunMethodRejectsUndefinedCommand() throws Exception {
		// complete mock setup
		EasyMock.replay(command);
		EasyMock.replay(executionResult);
		EasyMock.replay(context);

		// run
		commandRunner.setExecutionResult(executionResult);
		commandRunner.run(null, randomDescription, context);

		// test
		assertTrue(commandRunner.lastExecutionSucceeded());

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(command);
		EasyMock.verify(context);
	}

	/**
	 * Test that runner rejects undefined description.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRunMethodRejectsUndefinedDescription() throws Exception {
		// complete mock setup
		EasyMock.replay(command);
		EasyMock.replay(executionResult);
		EasyMock.replay(context);

		// run
		commandRunner.setExecutionResult(executionResult);
		commandRunner.run(command, (String) null, context);

		// test
		assertTrue(commandRunner.lastExecutionSucceeded());

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(command);
		EasyMock.verify(context);
	}

	/**
	 * Test that runner rejects undefined context.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRunMethodRejectsUndefinedContext() throws Exception {
		// complete mock setup
		EasyMock.replay(command);
		EasyMock.replay(executionResult);
		EasyMock.replay(context);

		// run
		commandRunner.setExecutionResult(executionResult);
		commandRunner.run(command, randomDescription, null);

		// test
		assertTrue(commandRunner.lastExecutionSucceeded());

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(command);
		EasyMock.verify(context);
	}

	/**
	 * Test that runner rejects undefined command.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRunMethodRejectsUndefinedCommand2() throws Exception {
		// complete mock setup
		EasyMock.replay(command);
		EasyMock.replay(executionResult);
		EasyMock.replay(context);

		// run
		commandRunner.setExecutionResult(executionResult);
		commandRunner.run(null, executionResult, context);

		// test
		assertTrue(commandRunner.lastExecutionSucceeded());

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(command);
		EasyMock.verify(context);
	}

	/**
	 * Test that runner rejects undefined result.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRunMethodRejectsUndefinedResult() throws Exception {
		// complete mock setup
		EasyMock.replay(command);
		EasyMock.replay(executionResult);
		EasyMock.replay(context);

		// run
		commandRunner.setExecutionResult(executionResult);
		commandRunner.run(command, (ExecutionResult) null, context);

		// test
		assertTrue(commandRunner.lastExecutionSucceeded());

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(command);
		EasyMock.verify(context);
	}

	/**
	 * Test that runner rejects undefined context.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRunMethodRejectsUndefinedContext2() throws Exception {
		// complete mock setup
		EasyMock.replay(command);
		EasyMock.replay(executionResult);
		EasyMock.replay(context);

		// run
		commandRunner.setExecutionResult(executionResult);
		commandRunner.run(command, executionResult, null);

		// test
		assertTrue(commandRunner.lastExecutionSucceeded());

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(command);
		EasyMock.verify(context);
	}

}
