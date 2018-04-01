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

package com.alpha.pineapple.execution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.continuation.ContinuationPolicy;
import com.alpha.pineapple.execution.continuation.InterruptedExecutionException;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Unit test of the {@link ExecutionResultImpl} class which focusses on the
 * continuation support.
 */
public class Continuation_ExecutionResultImplTest {

	// can't include the MessageProviderAnswerImpl from pineapple-test-utils
	// project
	// due to circular Maven dependencies.
	public class MessageProviderAnswerImpl implements IAnswer<String> {

		public String answer() throws Throwable {

			// get current arguments
			Object[] args = EasyMock.getCurrentArguments();

			// return key as answer
			String key = (String) args[0];
			return key;
		}
	}

	final static String DESCRIPTION = "a description";

	/**
	 * Object under test.
	 */
	ExecutionResultImpl result;

	/**
	 * mock result repository.
	 */
	ResultRepository repository;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * Random value.
	 */
	String randomDescription;

	/**
	 * Random value.
	 */
	String randomDescription2;

	/**
	 * Random value.
	 */
	String randomMessage;

	@Before
	public void setUp() throws Exception {

		randomDescription = RandomStringUtils.randomAlphabetic(10);
		randomDescription2 = RandomStringUtils.randomAlphabetic(10);
		randomMessage = RandomStringUtils.randomAlphabetic(10);

		// create mock repository
		repository = EasyMock.createMock(ResultRepository.class);

		messageProvider = createMessageProviderWithArgs();
	}

	/**
	 * Create message provider which accept messages with arguments.
	 * 
	 * @return message provider which accept messages with arguments.
	 */
	MessageProvider createMessageProviderWithArgs() {

		// create mock provider
		MessageProvider messageProvider = EasyMock.createMock(MessageProvider.class);

		// complete mock source initialization
		IAnswer<String> answer = new MessageProviderAnswerImpl();

		EasyMock.expect(
				messageProvider.getMessage((String) EasyMock.isA(String.class), (Object[]) EasyMock.anyObject()));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();

		EasyMock.replay(messageProvider);

		return messageProvider;
	}

	/**
	 * Create message provider which accept messages with no arguments.
	 * 
	 * @return message provider which accept messages with no arguments.
	 */
	MessageProvider createMessageProviderWithNoArgs() {

		// create mock provider
		MessageProvider messageProvider = EasyMock.createMock(MessageProvider.class);

		// complete mock source initialization
		IAnswer<String> answer = new MessageProviderAnswerImpl();

		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();

		EasyMock.replay(messageProvider);

		return messageProvider;
	}

	@After
	public void tearDown() throws Exception {
		result = null;
		repository = null;
		messageProvider = null;
	}

	/**
	 * Test that the continuation policy is updated as expected when setCancelled()
	 * is invoked.
	 */
	@Test
	public void testCancelUpdatesTheContinuationPolicy() {
		result = new ExecutionResultImpl(repository, randomDescription);
		ContinuationPolicy policy = result.getContinuationPolicy();
		result.setCancelled();

		// test
		assertNotNull(policy);
		assertFalse(policy.continueExecution());
		assertTrue(policy.isContinueOnFailure());
		assertTrue(policy.isCancelled());
		assertNull(policy.getFailedResult());
	}

	/**
	 * Test that interrupted exception is thrown when attempting to create child
	 * after cancellation.
	 */
	@Test(expected = InterruptedExecutionException.class)
	public void testCreatingChildAfterCancellationThrowsException() {
		result = new ExecutionResultImpl(repository, randomDescription);
		result.setCancelled();

		// add child to trigger exception
		result.addChild(randomDescription2);
	}

	/**
	 * Test that interrupted state is set when attempting to create child after
	 * cancellation.
	 */
	@Test()
	public void testCreatingChildAfterCancellationSetsInterruptedState() {
		result = new ExecutionResultImpl(repository, randomDescription);
		result.setCancelled();

		try {
			// add child to trigger exception
			result.addChild(randomDescription2);

		} catch (InterruptedExecutionException e) {

			// test
			assertEquals(ExecutionState.INTERRUPTED, result.getState());
		}
	}

	/**
	 * Test that interrupted exception is thrown when attempting to create child
	 * after a failure.
	 */
	@Test(expected = InterruptedExecutionException.class)
	public void testCreatingChildAfterAbortOnFailureThrowsException() {
		result = new ExecutionResultImpl(repository, randomDescription);
		result.getContinuationPolicy().disableContinueOnFailure();

		// add child and set failure
		ExecutionResult child = result.addChild(randomDescription2);
		child.setState(ExecutionState.FAILURE);

		// add child to trigger exception
		result.addChild(randomDescription2);
	}

	/**
	 * Test that interrupted state is set when attempting to create child after
	 * failure.
	 */
	@Test()
	public void testCreatingChildAfterAbortSetsInterruptedState() {
		result = new ExecutionResultImpl(repository, randomDescription);
		result.getContinuationPolicy().disableContinueOnFailure();

		// declare
		ExecutionResult child = null;
		ExecutionResult child2 = null;

		try {
			// add child and set failure
			child = result.addChild(randomDescription2);
			child.setState(ExecutionState.FAILURE);

			// add child to trigger exception
			result.addChild(randomDescription2);

		} catch (InterruptedExecutionException e) {

			// test
			assertEquals(ExecutionState.INTERRUPTED, result.getState()); // root
			// result
			assertEquals(ExecutionState.FAILURE, child.getState()); // child #1
			assertNull(child2); // child #2 was never created
		}
	}

}
