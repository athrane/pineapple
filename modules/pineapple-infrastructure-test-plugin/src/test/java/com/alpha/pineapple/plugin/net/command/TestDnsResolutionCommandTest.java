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
import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.net.http.HttpInvocationsSet;
import com.alpha.pineapple.test.Asserter;

/**
 * Unit test of the class {@link TestDnsResolutionCommand}.
 */
public class TestDnsResolutionCommandTest {

	/**
	 * Host name for local host
	 */
	static final String LOCALHOST_IP = "127.0.0.1";

	/**
	 * IP address for local host.
	 */
	static final String LOCALHOST_HOST = "localhost";

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
		command = new TestDnsResolutionCommand();

		// create context
		context = new ContextBase();

		// create mock result
		executionResult = EasyMock.createMock(ExecutionResult.class);

		// create mock asserter
		asserter = EasyMock.createMock(Asserter.class);

		// inject asserter
		ReflectionTestUtils.setField(command, "asserter", asserter, Asserter.class);

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// inject message source
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
		messageProvider = null;
		asserter = null;
	}

	/**
	 * Test that command can resolve localhost to 127.0.0.1.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testResolveToLocalhost() {

		try {
			// complete asserter initialization
			asserter.setExecutionResult(executionResult);
			EasyMock.expect(asserter.assertObject(EasyMock.anyObject(), (Matcher) EasyMock.isA(Matcher.class),
					(String) EasyMock.isA(String.class))).andReturn(executionResult).times(2);
			EasyMock.replay(asserter);

			// complete execution result initialization
			executionResult.completeAsComputed((MessageProvider) EasyMock.isA(MessageProvider.class),
					(String) EasyMock.isA(String.class), (Object[]) EasyMock.isNull(),
					(String) EasyMock.isA(String.class), (Object[]) EasyMock.isNull());
			EasyMock.replay(executionResult);

			// setup parameters
			context.put(TestDnsResolutionCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(TestDnsResolutionCommand.HOSTNAME_KEY, LOCALHOST_HOST);
			context.put(TestDnsResolutionCommand.IP_KEY, LOCALHOST_IP);

			// execute command
			command.execute(context);

			// verify mocks
			EasyMock.verify(executionResult);

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that undefined execution result is rejected.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedExecutionResult() throws Exception {

		// setup parameters
		context.put(TestDnsResolutionCommand.EXECUTIONRESULT_KEY, null);
		context.put(TestDnsResolutionCommand.HOSTNAME_KEY, LOCALHOST_HOST);
		context.put(TestDnsResolutionCommand.IP_KEY, LOCALHOST_IP);

		// execute command
		command.execute(context);

		// test
		fail("Test should never reach here.");
	}

	/**
	 * Test that undefined host is rejected.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedHost() throws Exception {

		// setup parameters
		context.put(TestDnsResolutionCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(TestDnsResolutionCommand.HOSTNAME_KEY, null);
		context.put(TestDnsResolutionCommand.IP_KEY, LOCALHOST_IP);

		// execute command
		command.execute(context);

		// test
		fail("Test should never reach here.");
	}

	/**
	 * Test that undefined ip address is rejected.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedIpAddress() throws Exception {

		// setup parameters
		context.put(TestDnsResolutionCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(TestDnsResolutionCommand.HOSTNAME_KEY, LOCALHOST_HOST);
		context.put(TestDnsResolutionCommand.IP_KEY, null);

		// execute command
		command.execute(context);

		// test
		fail("Test should never reach here.");
	}

	/**
	 * Test that command fails if the name can't resolve to expected IP address.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testTestCaseFailsIfNameCanBeResolvedToIP() {
		try {
			// complete asserter initialization
			asserter.setExecutionResult(executionResult);
			EasyMock.expect(asserter.assertObject(EasyMock.anyObject(), (Matcher) EasyMock.isA(Matcher.class),
					(String) EasyMock.isA(String.class))).andReturn(executionResult).times(2);
			EasyMock.replay(asserter);

			// complete execution result initialization
			executionResult.completeAsComputed((MessageProvider) EasyMock.isA(MessageProvider.class),
					(String) EasyMock.isA(String.class), (Object[]) EasyMock.isNull(),
					(String) EasyMock.isA(String.class), (Object[]) EasyMock.isNull());
			EasyMock.replay(executionResult);

			// setup parameters
			context.put(TestDnsResolutionCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(TestDnsResolutionCommand.HOSTNAME_KEY, LOCALHOST_HOST);
			context.put(TestDnsResolutionCommand.IP_KEY, "1.1.1.1");

			// execute command
			command.execute(context);

			// verify mocks
			EasyMock.verify(executionResult);

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that command fails with an error if the name can't resolve to expected
	 * IP address.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testTestCaseFailsWithErrorIfNameIsntAnHostName() {
		try {
			// complete asserter initialization
			asserter.setExecutionResult(executionResult);
			EasyMock.expect(asserter.assertObject(EasyMock.anyObject(), (Matcher) EasyMock.isA(Matcher.class),
					(String) EasyMock.isA(String.class))).andReturn(executionResult).times(2);
			EasyMock.replay(asserter);

			// complete execution result initialization
			executionResult.completeAsComputed((MessageProvider) EasyMock.isA(MessageProvider.class),
					(String) EasyMock.isA(String.class), (Object[]) EasyMock.isNull(),
					(String) EasyMock.isA(String.class), (Object[]) EasyMock.isNull());
			EasyMock.replay(executionResult);

			// setup parameters
			context.put(TestDnsResolutionCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(TestDnsResolutionCommand.HOSTNAME_KEY, "not-an-host");
			context.put(TestDnsResolutionCommand.IP_KEY, LOCALHOST_IP);

			// execute command
			command.execute(context);

			// verify mocks
			EasyMock.verify(executionResult);

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

}
