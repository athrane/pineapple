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

package com.alpha.pineapple.plugin.net.command;

import static org.junit.Assert.fail;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.net.http.HttpInvocationSequenceImpl;
import com.alpha.pineapple.plugin.net.http.HttpInvocationsSet;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfo;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet;
import com.alpha.pineapple.test.Asserter;

/**
 * unit test of the <code>TestResponsePropertiesCommand</code> class.
 */
public class TestResponsePropertiesCommandTest {

	/**
	 * Command under test.
	 */
	Command command;

	/**
	 * Chain context.
	 */
	Context context;

	/**
	 * Mock execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Mock HTTP invocation result set.
	 */
	HttpInvocationsSet resultSet;

	/**
	 * Response properties.
	 */
	ResponsePropertyInfoSet propertyInfoSet;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * Mock asserter.
	 */
	Asserter asserter;

	@Before
	public void setUp() throws Exception {

		// create command
		command = new TestResponsePropertiesCommand();

		// create context
		context = new ContextBase();

		// create mock result
		executionResult = EasyMock.createMock(ExecutionResult.class);

		// create mock HTTP invocation result sequence
		resultSet = EasyMock.createMock(HttpInvocationsSet.class);

		// create mock response properties
		propertyInfoSet = EasyMock.createMock(ResponsePropertyInfoSet.class);

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// inject message provider
		ReflectionTestUtils.setField(command, "messageProvider", messageProvider, MessageProvider.class);

		// complete mock source initialization
		IAnswer<String> answer = new MessageProviderAnswerImpl();

		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class),
				(Object[]) EasyMock.isA(Object[].class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();

		EasyMock.replay(messageProvider);

		// create mock asserter
		asserter = EasyMock.createMock(Asserter.class);

		// inject asserter
		ReflectionTestUtils.setField(command, "asserter", asserter, Asserter.class);

	}

	@After
	public void tearDown() throws Exception {
		command = null;
		context = null;
		executionResult = null;
		resultSet = null;
		propertyInfoSet = null;
		messageProvider = null;
		asserter = null;
	}

	/**
	 * Test that undefined execution result is rejected.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedExecutionResult() throws Exception {

		// setup parameters
		context.put(TestResponsePropertiesCommand.EXECUTIONRESULT_KEY, null);
		context.put(TestResponsePropertiesCommand.INVOCATIONRESULTS_SET_KEY, resultSet);
		context.put(TestResponsePropertiesCommand.RESPONSEPROPERTIES_SET_KEY, propertyInfoSet);

		// execute command
		command.execute(context);

		// test
		fail("Test should never reach here.");
	}

	/**
	 * Test that undefined set is rejected.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedResultSet() throws Exception {

		// setup parameters
		context.put(TestResponsePropertiesCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(TestResponsePropertiesCommand.INVOCATIONRESULTS_SET_KEY, null);
		context.put(TestResponsePropertiesCommand.RESPONSEPROPERTIES_SET_KEY, propertyInfoSet);

		// execute command
		command.execute(context);

		// test
		fail("Test should never reach here.");
	}

	/**
	 * Test that undefined response properties is rejected.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedResponseProperties() throws Exception {

		// setup parameters
		context.put(TestResponsePropertiesCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(TestResponsePropertiesCommand.INVOCATIONRESULTS_SET_KEY, resultSet);
		context.put(TestResponsePropertiesCommand.RESPONSEPROPERTIES_SET_KEY, null);

		// execute command
		command.execute(context);

		// test
		fail("Test should never reach here.");
	}

	/**
	 * Test that command succeeds with an empty result set.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSucceedsWithEmptyResultSetAndEmptyPropertiesSet() throws Exception {

		// setup parameters
		context.put(TestResponsePropertiesCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(TestResponsePropertiesCommand.INVOCATIONRESULTS_SET_KEY, resultSet);
		context.put(TestResponsePropertiesCommand.RESPONSEPROPERTIES_SET_KEY, propertyInfoSet);

		// complete asserter initialization
		asserter.setExecutionResult(executionResult);
		EasyMock.expect(asserter.assertWithoutCollectingExecutionResult((HttpInvocationsSet) EasyMock.eq(resultSet),
				(Matcher) EasyMock.isA(Matcher.class), (String) EasyMock.isA(String.class))).andReturn(executionResult);
		EasyMock.expect(asserter.lastAssertionSucceeded()).andReturn(true);
		asserter.completeTestAsSuccessful(messageProvider, "trp.zero_size_sequence_succeed", null);
		EasyMock.replay(asserter);

		// complete execution result initialization
		EasyMock.replay(executionResult);

		// complete properties initialization
		EasyMock.replay(resultSet);

		// complete properties initialization
		EasyMock.replay(propertyInfoSet);

		// execute command
		command.execute(context);

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(resultSet);
		EasyMock.verify(propertyInfoSet);
	}

	/**
	 * Test that command succeeds with an single sequence and an empty properties
	 * set.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSucceedsWithSingleSequenceAndEmptyPropertiesSet() throws Exception {

		// setup parameters
		context.put(TestResponsePropertiesCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(TestResponsePropertiesCommand.INVOCATIONRESULTS_SET_KEY, resultSet);
		context.put(TestResponsePropertiesCommand.RESPONSEPROPERTIES_SET_KEY, propertyInfoSet);

		// complete asserter initialization
		asserter.setExecutionResult(executionResult);
		EasyMock.expect(asserter.assertWithoutCollectingExecutionResult((HttpInvocationsSet) EasyMock.eq(resultSet),
				(Matcher) EasyMock.isA(Matcher.class), (String) EasyMock.isA(String.class))).andReturn(executionResult);
		EasyMock.expect(asserter.lastAssertionSucceeded()).andReturn(false);
		EasyMock.replay(asserter);

		// crate child result
		// complete execution result initialization
		executionResult.setState(ExecutionState.COMPUTED);
		executionResult.addMessage((String) EasyMock.isA(String.class), (String) EasyMock.isA(String.class));
		EasyMock.expect(executionResult.getState()).andReturn(ExecutionState.SUCCESS);
		EasyMock.replay(executionResult);

		// complete properties initialization
		EasyMock.expect(resultSet.getSequences()).andReturn(new HttpInvocationSequenceImpl[1]).times(1);
		EasyMock.replay(resultSet);

		// complete properties initialization
		EasyMock.expect(propertyInfoSet.getProperties()).andReturn(new ResponsePropertyInfo[0]);
		EasyMock.replay(propertyInfoSet);

		// execute command
		command.execute(context);

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(resultSet);
		EasyMock.verify(propertyInfoSet);
	}

}
