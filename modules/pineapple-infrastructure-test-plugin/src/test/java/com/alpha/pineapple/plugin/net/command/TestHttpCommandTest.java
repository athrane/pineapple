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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.net.http.HttpConfiguration;
import com.alpha.pineapple.plugin.net.http.HttpInvocationsSet;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet;

/**
 * Unit test of the class {@link TestHttpCommand}.
 */
public class TestHttpCommandTest {

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
	 * Response properties.
	 */
	ResponsePropertyInfoSet propertyInfoSet;

	/**
	 * Mock command runner.
	 */
	CommandRunner runner;

	/**
	 * Mock HTTP Get command .
	 */
	Command httpGetCommand;

	/**
	 * Mock test response properties Command.
	 */
	Command testResponsePropertiesCommand;

	/**
	 * Mock HTTP Configuration object.
	 */
	HttpConfiguration httpConfig;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	@Before
	public void setUp() throws Exception {
		// create command
		command = new TestHttpCommand();

		// create context
		context = new ContextBase();

		// create mock result
		executionResult = EasyMock.createMock(ExecutionResult.class);

		// create mock response properties
		propertyInfoSet = EasyMock.createMock(ResponsePropertyInfoSet.class);

		// create mock command runner
		runner = EasyMock.createMock(CommandRunner.class);

		// inject runner into command
		ReflectionTestUtils.setField(command, "runner", runner, CommandRunner.class);

		// create mock HTTP Get command
		httpGetCommand = EasyMock.createMock(Command.class);

		// inject command into command
		ReflectionTestUtils.setField(command, "invokeHttpGetMethodCommand", httpGetCommand, Command.class);

		// create mock test response properties command
		testResponsePropertiesCommand = EasyMock.createMock(Command.class);

		// inject command into command
		ReflectionTestUtils.setField(command, "testResponsePropertiesCommand", testResponsePropertiesCommand,
				Command.class);

		// create mock HTTP Configuration
		httpConfig = EasyMock.createMock(HttpConfiguration.class);

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
	}

	@After
	public void tearDown() throws Exception {
		command = null;
		context = null;
		executionResult = null;
		propertyInfoSet = null;
		runner = null;
		httpGetCommand = null;
		httpConfig = null;
		messageProvider = null;
	}

	/**
	 * Test that command fails if execution result is undefined in context.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedExecutionResult() throws Exception {
		// initialize parameters
		String[] urls = new String[] { "http://127.0.0.1/sniffer" };

		// setup parameters
		context.put(TestHttpCommand.URLS_KEY, urls);
		context.put(TestHttpCommand.REQUESTS_KEY, 4);
		context.put(TestHttpCommand.RESET_KEY, true);
		context.put(TestHttpCommand.HTTPCONFIGURATION_KEY, httpConfig);
		context.put(TestHttpCommand.EXECUTIONRESULT_KEY, null);
		context.put(TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, propertyInfoSet);

		// execute command to trigger exception
		command.execute(context);

		fail("Test should never reach here.");
	}

	/**
	 * Test that undefined response properties is rejected.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedResponseProperties() throws Exception {
		// initialize parameters
		String[] urls = new String[] { "http://127.0.0.1/sniffer" };

		// setup parameters
		context.put(TestHttpCommand.URLS_KEY, urls);
		context.put(TestHttpCommand.REQUESTS_KEY, 4);
		context.put(TestHttpCommand.RESET_KEY, true);
		context.put(TestHttpCommand.HTTPCONFIGURATION_KEY, httpConfig);
		context.put(TestHttpCommand.EXECUTIONRESULT_KEY, null);
		context.put(TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, null);

		// execute command to trigger exception
		command.execute(context);

		fail("Test should never reach here.");
	}

	/**
	 * Test that command fails if URL is undefined in context.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedUrls() throws Exception {

		// setup parameters
		context.put(TestHttpCommand.URLS_KEY, null);
		context.put(TestHttpCommand.REQUESTS_KEY, 4);
		context.put(TestHttpCommand.RESET_KEY, true);
		context.put(TestHttpCommand.HTTPCONFIGURATION_KEY, httpConfig);
		context.put(TestHttpCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command to trigger exception
		command.execute(context);

		fail("Test should never reach here.");
	}

	/**
	 * Test that command fails if URL is undefined in context.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsEmptyUrlsArray() throws Exception {
		// initialize parameters
		String[] urls = new String[] {};

		// setup parameters
		context.put(TestHttpCommand.URLS_KEY, urls);
		context.put(TestHttpCommand.REQUESTS_KEY, 4);
		context.put(TestHttpCommand.RESET_KEY, true);
		context.put(TestHttpCommand.HTTPCONFIGURATION_KEY, httpConfig);
		context.put(TestHttpCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command to trigger exception
		command.execute(context);

		fail("Test should never reach here.");
	}

	/**
	 * Test that command fails if number of requests is undefined in context.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedRequests() throws Exception {
		// initialize parameters
		String[] urls = new String[] { "http://wintermute" };

		// setup parameters
		context.put(TestHttpCommand.URLS_KEY, urls);
		context.put(TestHttpCommand.RESET_KEY, true);
		context.put(TestHttpCommand.HTTPCONFIGURATION_KEY, httpConfig);
		context.put(TestHttpCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, propertyInfoSet);

		// execute command to trigger exception
		command.execute(context);

		fail("Test should never reach here.");
	}

	/**
	 * Test that command fails if HTTP configuration is undefined in context.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedHttpConfiguration() throws Exception {
		// initialize parameters
		String[] urls = new String[] { "http://wintermute" };

		// setup parameters
		context.put(TestHttpCommand.URLS_KEY, urls);
		context.put(TestHttpCommand.REQUESTS_KEY, 4);
		context.put(TestHttpCommand.RESET_KEY, true);
		context.put(TestHttpCommand.HTTPCONFIGURATION_KEY, null);
		context.put(TestHttpCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, propertyInfoSet);

		// execute command to trigger exception
		command.execute(context);

		fail("Test should never reach here.");
	}

	/**
	 * Test that result keys is defined in context after execution.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testResultKeysIsDefinedInContextAfterExecution() {
		final Object PUT_RETURN_VALUE = null;

		try {
			// create parameters
			String[] urls = new String[] { "http://wintermute" };
			int requests = 4;

			// crate mock child execution result for InvokeHttpGetMethodCommand
			ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
			EasyMock.replay(childResult);

			// create capture to snap response properties when InvokeHttpGetMethodCommand is
			// invoked
			Capture<ResponsePropertyInfoSet> responsePropsCapture = new Capture<ResponsePropertyInfoSet>();

			// create mock HTTP invocation set
			HttpInvocationsSet httpInvocationsSet = EasyMock.createMock(HttpInvocationsSet.class);
			EasyMock.replay(httpInvocationsSet);

			// crate mock context for InvokeHttpGetMethodCommand
			Context childContext = EasyMock.createMock(Context.class);
			EasyMock.expect(childContext.put(InvokeHttpGetMethodCommand.URLS_KEY, urls)).andReturn(PUT_RETURN_VALUE);
			EasyMock.expect(childContext.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, requests))
					.andReturn(PUT_RETURN_VALUE);
			EasyMock.expect(childContext.put(InvokeHttpGetMethodCommand.RESET_KEY, true)).andReturn(PUT_RETURN_VALUE);
			EasyMock.expect(childContext.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig))
					.andReturn(PUT_RETURN_VALUE);
			EasyMock.expect(
					childContext.put((String) EasyMock.eq(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY),
							EasyMock.capture(responsePropsCapture)))
					.andReturn(PUT_RETURN_VALUE);
			EasyMock.expect(childContext.get(InvokeHttpGetMethodCommand.RESULTS_KEY)).andReturn(httpInvocationsSet);
			EasyMock.replay(childContext);

			// create capture to catch response properties when
			// TestResponsePropertiesCommand is invoked
			Capture<ResponsePropertyInfoSet> responsePropsCapture2 = new Capture<ResponsePropertyInfoSet>();

			// crate mock context for TestResponsePropertiesCommand
			Context childContext2 = EasyMock.createMock(Context.class);
			EasyMock.expect(
					childContext2.put((String) EasyMock.eq(TestResponsePropertiesCommand.RESPONSEPROPERTIES_SET_KEY),
							EasyMock.capture(responsePropsCapture2)))
					.andReturn(PUT_RETURN_VALUE);
			EasyMock.expect(
					childContext2.put((String) EasyMock.eq(TestResponsePropertiesCommand.INVOCATIONRESULTS_SET_KEY),
							(HttpInvocationsSet) EasyMock.eq(httpInvocationsSet)))
					.andReturn(PUT_RETURN_VALUE);
			EasyMock.replay(childContext2);

			// crate mock child execution result for TestResponsePropertiesCommand
			ExecutionResult childResult2 = EasyMock.createMock(ExecutionResult.class);
			EasyMock.replay(childResult2);

			// complete runner initialization
			// return the child context for the InvokeHttpGetMethodCommand
			EasyMock.expect(runner.createContext()).andReturn(childContext);
			runner.setExecutionResult(executionResult);
			// execute InvokeHttpGetMethodCommand
			EasyMock.expect(runner.run((Command) EasyMock.eq(httpGetCommand), (String) EasyMock.isA(String.class),
					(Context) EasyMock.eq(childContext)));
			EasyMock.expectLastCall().andReturn(childResult);
			// return the child context for the TestResponsePropertiesCommand
			EasyMock.expect(runner.createContext()).andReturn(childContext2);
			// execute TestResponsePropertiesCommand
			EasyMock.expect(runner.run((Command) EasyMock.eq(testResponsePropertiesCommand),
					(String) EasyMock.isA(String.class), (Context) EasyMock.eq(childContext2)));
			EasyMock.expectLastCall().andReturn(childResult2);
			EasyMock.replay(runner);

			// complete execution result initialization
			executionResult.completeAsComputed(messageProvider, "thc.succeed", null, "thc.failed", null);
			EasyMock.replay(executionResult);

			// setup parameters
			context.put(TestHttpCommand.URLS_KEY, urls);
			context.put(TestHttpCommand.REQUESTS_KEY, requests);
			context.put(TestHttpCommand.RESET_KEY, true);
			context.put(TestHttpCommand.HTTPCONFIGURATION_KEY, httpConfig);
			context.put(TestHttpCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, propertyInfoSet);

			// execute command
			command.execute(context);

			// verify mocks
			EasyMock.verify(childContext);
			EasyMock.verify(childContext2);
			EasyMock.verify(childResult);
			EasyMock.verify(childResult2);
			EasyMock.verify(runner);
			EasyMock.verify(executionResult);

			// get captured response properties object
			ResponsePropertyInfoSet responseProperties = responsePropsCapture.getValue();
			assertNotNull(responseProperties);

			ResponsePropertyInfoSet responseProperties2 = responsePropsCapture2.getValue();
			assertNotNull(responseProperties2);

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

}
