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

import com.alpha.javautils.ConcurrencyUtils;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.continuation.ContinuationPolicy;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Unit test of the {@link ExecutionResultImpl} class.
 */
public class ExecutionResultImplTest {

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
	 * Some milis used in tests.
	 */
	static final int SOME_MILIS = 123;

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
	 * Test that newly created instance reports itself as executing.
	 */
	@Test
	public void testInstanceReportsItsStateAsExecuting() {
		result = new ExecutionResultImpl(repository, randomDescription);

		// test
		assertTrue(result.isExecuting());
	}

	/**
	 * Test that newly created instance have the expected attributes.
	 */
	@Test
	public void testInstanceContainsExpectedAttributes() {
		result = new ExecutionResultImpl(repository, randomDescription);

		// test
		assertEquals(randomDescription, result.getDescription());
		assertNotNull(result.getChildren());
		assertEquals(0, result.getChildren().length);
		assertEquals(0, result.getMessages().size());
		assertEquals(null, result.getParent());
		assertEquals(ExecutionState.EXECUTING, result.getState());
		assertNotNull(result.getContinuationPolicy());
	}

	/**
	 * Test that continuation policy in newly created instance have the expected
	 * attributes.
	 */
	@Test
	public void testContinuationPolicyContainsExpectedAttributes() {
		result = new ExecutionResultImpl(repository, randomDescription);
		ContinuationPolicy policy = result.getContinuationPolicy();

		// test
		assertNotNull(policy);
		assertTrue(policy.continueExecution());
		assertTrue(policy.isContinueOnFailure());
		assertFalse(policy.isCancelled());
		assertNull(policy.getFailedResult());
	}

	/**
	 * Test that result accepts undefined repository.
	 */
	@Test
	public void testAccepctsUndefinedRepository() throws Exception {
		result = new ExecutionResultImpl(null, DESCRIPTION);

		// test
		assertNotNull(result);
	}

	/**
	 * Test that result rejects undefined description.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedDescription() throws Exception {
		result = new ExecutionResultImpl(repository, null);
	}

	/**
	 * Test that result accepts undefined parent.
	 */
	@Test
	public void testAcceptsUndefinedParent() throws Exception {
		result = new ExecutionResultImpl(repository, null, DESCRIPTION);
	}

	/**
	 * Number of children is initially zero.
	 */
	@Test
	public void testNumberOfChildrenIsInitiallyZero() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// get children
		ExecutionResult[] children = result.getChildren();

		// test
		assertEquals(0, children.length);
	}

	/**
	 * Can add single child.
	 */
	@Test
	public void testCanAddSingleChild() {
		result = new ExecutionResultImpl(repository, randomDescription);

		// add child
		ExecutionResult child1 = result.addChild(randomDescription2);

		// get children
		ExecutionResult[] children = result.getChildren();

		// test
		assertEquals(1, children.length);
		assertEquals(child1, children[0]);
	}

	/**
	 * Added child contains expected description.
	 */
	@Test
	public void testChildContainsExpectedDescription() {
		result = new ExecutionResultImpl(repository, randomDescription);

		// add child
		ExecutionResult child1 = result.addChild(randomDescription2);

		// get children
		ExecutionResult[] children = result.getChildren();

		// test
		assertEquals(1, children.length);
		assertEquals(child1, children[0]);
		assertEquals(randomDescription2, child1.getDescription());
	}

	/**
	 * Adding a child twice, results in two distinct children.
	 */
	@Test
	public void testAddingChildTwiceResultsInTwoDistinctChildren() {
		result = new ExecutionResultImpl(repository, randomDescription);

		// add child
		ExecutionResult child1 = result.addChild(randomDescription2);
		ExecutionResult child2 = result.addChild(randomDescription2);

		// get children
		ExecutionResult[] children = result.getChildren();

		// test
		assertEquals(2, children.length);
		assertEquals(child1, children[0]);
		assertEquals(child2, children[1]);
	}

	/**
	 * Can add multiple children
	 */
	@Test
	public void testCanAddMultipleChildren() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");
		ExecutionResult child2 = result.addChild("child#2");

		// get children
		ExecutionResult[] children = result.getChildren();

		// test
		assertEquals(2, children.length);
		assertEquals(child1, children[0]);
		assertEquals(child2, children[1]);
	}

	/**
	 * Test the result is root result object.
	 */
	@Test
	public void testIsRootResult() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// test
		assertTrue(result.isRoot());
	}

	/**
	 * Test the is success is false when executing.
	 */
	@Test
	public void testIsSuccessReturnsFalseWhenExecuting() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// test
		assertFalse(result.isSuccess());
	}

	/**
	 * Test the isInterrupted() is true when interrupted.
	 */
	@Test
	public void testIsInterruptedReturnsTrueWhenSuccessful() {

		// create result with no parent
		result = new ExecutionResultImpl(repository, DESCRIPTION);
		result.setState(ExecutionState.INTERRUPTED);

		// test
		assertTrue(result.isInterrupted());
	}

	/**
	 * Test the is isInterrupted() is false when executing.
	 */
	@Test
	public void testIsInterruptedReturnsFalseWhenExecuting() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// test
		assertFalse(result.isInterrupted());
	}

	/**
	 * Test the is success is true when successful.
	 */
	@Test
	public void testIsSuccessReturnsTrueWhenSuccessful() {

		// create result with no parent
		result = new ExecutionResultImpl(repository, DESCRIPTION);
		result.setState(ExecutionState.SUCCESS);

		// test
		assertTrue(result.isSuccess());
	}

	/**
	 * Test the is error is false when executing.
	 */
	@Test
	public void testIsErrorReturnsFalseWhenExecuting() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// test
		assertFalse(result.isError());
	}

	/**
	 * Test the is error is true when error.
	 */
	@Test
	public void testIsErrorReturnsTrueWhenError() {

		// create result with no parent
		result = new ExecutionResultImpl(repository, DESCRIPTION);
		result.setState(ExecutionState.ERROR);

		// test
		assertTrue(result.isError());
	}

	/**
	 * Test that added child isn't root result.
	 */
	@Test
	public void testChildIsntRoot() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		result.addChild("child#1");

		// get children
		ExecutionResult[] children = result.getChildren();

		// get first child
		ExecutionResult actualChild = children[0];

		// test
		assertFalse(actualChild.isRoot());
	}

	/**
	 * test that internal stop watch is stopped when the execution state is changed
	 * from executing to computed.
	 */
	@Test
	public void testTimeIsStoppedWhenStateChangesFromExecutingToComputed() throws Exception {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set state
		result.setState(ExecutionState.COMPUTED);

		// get time
		long time1 = result.getTime();
		ConcurrencyUtils.waitSomeMilliseconds(SOME_MILIS);
		long time2 = result.getTime();

		// test
		assertEquals(time1, time2);
	}

	/**
	 * test that internal stop watch is stopped when the execution state is changed
	 * from executing to successful.
	 */
	@Test
	public void testTimeIsStoppedWhenStateChangesFromExecutingToSuccessful() throws Exception {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set state
		result.setState(ExecutionState.SUCCESS);

		// get time
		long time1 = result.getTime();
		ConcurrencyUtils.waitSomeMilliseconds(SOME_MILIS);
		long time2 = result.getTime();

		// test
		assertEquals(time1, time2);
	}

	/**
	 * test that internal stop watch is stopped when the execution state is changed
	 * from executing to error.
	 */
	@Test
	public void testTimeIsStoppedWhenStateChangesFromExecutingToError() throws Exception {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set state
		result.setState(ExecutionState.ERROR);

		// get time
		long time1 = result.getTime();
		ConcurrencyUtils.waitSomeMilliseconds(SOME_MILIS);
		long time2 = result.getTime();

		// test
		assertEquals(time1, time2);
	}

	/**
	 * test that internal stop watch is stopped when the execution state is changed
	 * from executing to failure.
	 */
	@Test
	public void testTimeIsStoppedWhenStateChangesFromExecutingToFailure() throws Exception {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set state
		result.setState(ExecutionState.ERROR);

		// get time
		long time1 = result.getTime();
		ConcurrencyUtils.waitSomeMilliseconds(SOME_MILIS);
		long time2 = result.getTime();

		// test
		assertEquals(time1, time2);
	}

	/**
	 * test that internal stop watch isn't started if the execution state is changed
	 * to executing from another state.
	 */
	@Test
	public void testTimeIsntRestartedIfStateChangesToExecutingFromAnotherState() throws Exception {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set state
		result.setState(ExecutionState.ERROR);

		// get time
		long time1 = result.getTime();

		// wait
		ConcurrencyUtils.waitSomeMilliseconds(SOME_MILIS);

		// set state
		result.setState(ExecutionState.EXECUTING);

		long time2 = result.getTime();

		// test
		assertEquals(time1, time2);
	}

	/**
	 * test that internal stop watch is stopped when the execution state is changed
	 * from executing to interrupted.
	 */
	@Test
	public void testTimeIsStoppedWhenStateChangesFromExecutingToInterrupted() throws Exception {
		result = new ExecutionResultImpl(repository, randomDescription);

		// set state
		result.setState(ExecutionState.INTERRUPTED);

		// get time
		long time1 = result.getTime();
		ConcurrencyUtils.waitSomeMilliseconds(SOME_MILIS);
		long time2 = result.getTime();

		// test
		assertEquals(time1, time2);
	}

	/**
	 * Test that a failure in a child is propagated to a parent result when parent
	 * state is computed.
	 */
	@Test
	public void testChildFailureIsPropagatedToParentResultWithComputedState() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");
		child1.setState(ExecutionState.FAILURE);

		// set parent state
		result.setState(ExecutionState.COMPUTED);

		// test
		assertTrue(result.isFailed());
	}

	/**
	 * Test that a error in a child is propagated to a parent result when parent
	 * state is computed.
	 */
	@Test
	public void testChildErrorIsPropagatedToParentResultWithComputedState() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");
		child1.setState(ExecutionState.ERROR);

		// set parent state
		result.setState(ExecutionState.COMPUTED);

		// test
		assertEquals(ExecutionState.ERROR, result.getState());
	}

	/**
	 * Test that a success in a child is propagated to a parent result when parent
	 * state is computed.
	 */
	@Test
	public void testChildSuccessIsPropagatedToParentResultWithComputedState() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");
		child1.setState(ExecutionState.SUCCESS);

		// set parent state
		result.setState(ExecutionState.COMPUTED);

		// test
		assertEquals(ExecutionState.SUCCESS, result.getState());
	}

	/**
	 * Test that a computed child state is propagated to a parent result when parent
	 * state is computed.
	 */
	@Test
	public void testComputedChildStateIsPropagatedToParentResultWithComputedState() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");
		child1.setState(ExecutionState.COMPUTED);

		// set parent state
		result.setState(ExecutionState.COMPUTED);

		// test
		assertEquals(ExecutionState.SUCCESS, result.getState());
	}

	/**
	 * Test that a interruption in a child is propagated to a parent result when
	 * parent state is computed.
	 */
	@Test
	public void testChildInterruptionIsPropagatedToParentResultWithComputedState() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");
		child1.setState(ExecutionState.INTERRUPTED);

		// set parent state
		result.setState(ExecutionState.COMPUTED);

		// test
		assertEquals(ExecutionState.INTERRUPTED, result.getState());
	}

	/**
	 * Test that a failed child state is propagated to a an already completed
	 * (successfully) parent.
	 */
	@Test
	public void testChildFailureIsPropagatedToAlreadySuccessfullyCompletedParentResult() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");

		// completed set parent state
		result.setState(ExecutionState.SUCCESS);

		// completed set parent state
		child1.setState(ExecutionState.FAILURE);

		// test
		assertTrue(result.isFailed());
	}

	/**
	 * Test that a failed child state is propagated to a an already completed
	 * (successfully) parent.
	 */
	@Test
	public void testChildErrorIsPropagatedToAlreadySuccessfullyCompletedParentResult() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");

		// completed set parent state
		result.setState(ExecutionState.SUCCESS);

		// completed set parent state
		child1.setState(ExecutionState.ERROR);

		// test
		assertEquals(ExecutionState.ERROR, result.getState());
	}

	/**
	 * Test that a failed child state is propagated to a an already completed
	 * (successfully) parent.
	 */
	@Test
	public void testChildErrorIsPropagatedToAlreadyFailedCompletedParentResult() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");

		// completed set parent state
		result.setState(ExecutionState.FAILURE);

		// completed set parent state
		child1.setState(ExecutionState.ERROR);

		// test
		assertEquals(ExecutionState.ERROR, result.getState());
	}

	/**
	 * Test that a interrupted child state is propagated to a an already completed
	 * (successfully) parent.
	 */
	@Test
	public void testChildInterruptionIsPropagatedToAlreadyFailedCompletedParentResult() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");

		// completed set parent state
		result.setState(ExecutionState.FAILURE);

		// completed set parent state
		child1.setState(ExecutionState.INTERRUPTED);

		// test
		assertEquals(ExecutionState.INTERRUPTED, result.getState());
	}

	/**
	 * Test that a successful result can be recomputed.
	 */
	@Test
	public void testSuccessfulResultCanBeRecomputed() throws Exception {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set parent state to successful
		result.setState(ExecutionState.SUCCESS);

		// set parent state to computed
		result.setState(ExecutionState.COMPUTED);

		// test
		assertEquals(ExecutionState.SUCCESS, result.getState());
	}

	/**
	 * Test that a failed result can be recomputed.
	 */
	@Test
	public void testFailedResultCanBeRecomputed() throws Exception {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set parent state to successful
		result.setState(ExecutionState.FAILURE);

		// set parent state to computed
		result.setState(ExecutionState.COMPUTED);

		// test
		assertTrue(result.isFailed());
	}

	/**
	 * Test that a result with error can be recomputed.
	 */
	@Test
	public void testErrorResultCanBeRecomputed() throws Exception {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set parent state to successful
		result.setState(ExecutionState.ERROR);

		// set parent state to computed
		result.setState(ExecutionState.COMPUTED);

		// test
		assertEquals(ExecutionState.ERROR, result.getState());
	}

	/**
	 * Test that a result with error can be recomputed.
	 */
	@Test
	public void testComputedResultCanBeRecomputed() throws Exception {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set parent state to successful
		result.setState(ExecutionState.COMPUTED);

		// set parent state to computed
		result.setState(ExecutionState.COMPUTED);

		// test
		assertEquals(ExecutionState.SUCCESS, result.getState());
	}

	/**
	 * Test that a success in a child isn't propagated propagated to a failed parent
	 * result, when parent state is computed.
	 */
	@Test
	public void testChildSuccessDoesntOverrideParentFailureWithComputedState() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");
		child1.setState(ExecutionState.SUCCESS);

		// set parent state
		result.setState(ExecutionState.FAILURE);

		// test
		assertTrue(result.isFailed());
	}

	/**
	 * Test that the state of a running child running child updated to error, when
	 * parent state is computed. Which means that the error state propagated to the
	 * parent.
	 */
	@Test
	public void testStateOfRunningChildIsUpdatedToErrorWithComputedState() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add running child
		ExecutionResult child1 = result.addChild("child#1");

		// set parent state
		result.setState(ExecutionState.COMPUTED);

		// test
		assertEquals(ExecutionState.ERROR, child1.getState());
		assertEquals(ExecutionState.ERROR, result.getState());
	}

	/**
	 * Test that a success in a child isn't propagated to a parent result with an
	 * error.
	 */
	@Test
	public void testChildSuccessDoesntOverrideParentError() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");
		child1.setState(ExecutionState.SUCCESS);

		// set parent state
		result.setState(ExecutionState.ERROR);

		// test
		assertEquals(ExecutionState.ERROR, result.getState());
	}

	/**
	 * Test that a success in a child isn't propagated to a parent result with an
	 * failure.
	 */
	@Test
	public void testChildSuccessDoesntOverrideParentFailure() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");
		child1.setState(ExecutionState.SUCCESS);

		// set parent state
		result.setState(ExecutionState.FAILURE);

		// test
		assertTrue(result.isFailed());
	}

	/**
	 * Test that a failure in a child isn't propagated to a parent result with an
	 * success.
	 */
	@Test
	public void testChildFailureDoesntOverrideParentSuccess() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");
		child1.setState(ExecutionState.FAILURE);

		// set parent state
		result.setState(ExecutionState.SUCCESS);

		// test
		assertEquals(ExecutionState.SUCCESS, result.getState());
	}

	/**
	 * Test that an error in a child isn't propagated to a parent result with an
	 * success.
	 */
	@Test
	public void testChildErrorDoesntOverrideParentSuccess() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add child
		ExecutionResult child1 = result.addChild("child#1");
		child1.setState(ExecutionState.ERROR);

		// set parent state
		result.setState(ExecutionState.SUCCESS);

		// test
		assertEquals(ExecutionState.SUCCESS, result.getState());
	}

	/**
	 * Test that the repository is notified when the result is created.
	 */
	@Test
	public void testRepositoryIsNotifiedWhenResultIsCreated() throws Exception {

		// complete mock repository setup
		repository.notifyOfResultStateChange((ExecutionResult) EasyMock.isA(ExecutionResult.class));
		EasyMock.replay(repository);

		// create result
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// test

		// verify mock objects
		EasyMock.verify(repository);

	}

	/**
	 * Test that the repository is notified when the result changes state from
	 * executing to computed.
	 */
	@Test
	public void testRepositoryIsNotifiedWhenStateChangesFromExecutingToComputed() throws Exception {

		// complete mock repository setup
		repository.notifyOfResultStateChange((ExecutionResult) EasyMock.isA(ExecutionResult.class));
		repository.notifyOfResultStateChange((ExecutionResult) EasyMock.isA(ExecutionResult.class));
		EasyMock.replay(repository);

		// create result
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set state
		result.setState(ExecutionState.COMPUTED);

		// test

		// verify mock objects
		EasyMock.verify(repository);
	}

	/**
	 * Test that the repository is notified when the result changes state from
	 * executing to successful.
	 */
	@Test
	public void testRepositoryIsNotifiedWhenStateChangesFromExecutingToSuccessful() throws Exception {

		// complete mock repository setup
		repository.notifyOfResultStateChange((ExecutionResult) EasyMock.isA(ExecutionResult.class));
		repository.notifyOfResultStateChange((ExecutionResult) EasyMock.isA(ExecutionResult.class));
		EasyMock.replay(repository);

		// create result
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set state
		result.setState(ExecutionState.SUCCESS);

		// test

		// verify mock objects
		EasyMock.verify(repository);
	}

	/**
	 * Test that the repository is notified when the result changes state from
	 * executing to interrupted.
	 */
	@Test
	public void testRepositoryIsNotifiedWhenStateChangesFromExecutingToInterrupted() throws Exception {

		// complete mock repository setup
		repository.notifyOfResultStateChange((ExecutionResult) EasyMock.isA(ExecutionResult.class));
		repository.notifyOfResultStateChange((ExecutionResult) EasyMock.isA(ExecutionResult.class));
		EasyMock.replay(repository);

		// create result
		result = new ExecutionResultImpl(repository, randomDescription);

		// set state
		result.setState(ExecutionState.INTERRUPTED);

		// test

		// verify mock objects
		EasyMock.verify(repository);
	}

	/**
	 * Test that the repository is notified when the result changes state from
	 * executing to failure.
	 */
	@Test
	public void testRepositoryIsNotifiedWhenStateChangesFromExecutingToFailure() throws Exception {

		// complete mock repository setup
		repository.notifyOfResultStateChange((ExecutionResult) EasyMock.isA(ExecutionResult.class));
		repository.notifyOfResultStateChange((ExecutionResult) EasyMock.isA(ExecutionResult.class));
		EasyMock.replay(repository);

		// create result
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set state
		result.setState(ExecutionState.FAILURE);

		// test

		// verify mock objects
		EasyMock.verify(repository);
	}

	/**
	 * Test that the repository is notified when the result changes state from
	 * executing to error.
	 */
	@Test
	public void testRepositoryIsNotifiedWhenStateChangesFromExecutingToError() throws Exception {

		// complete mock repository setup
		repository.notifyOfResultStateChange((ExecutionResult) EasyMock.isA(ExecutionResult.class));
		repository.notifyOfResultStateChange((ExecutionResult) EasyMock.isA(ExecutionResult.class));
		EasyMock.replay(repository);

		// create result
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// set state
		result.setState(ExecutionState.ERROR);

		// test

		// verify mock objects
		EasyMock.verify(repository);
	}

	/**
	 * Test that result contains one message.
	 */
	@Test
	public void testInstanceContainsOneMessage() {

		// setup
		String description = "a description";
		String message = "message";

		// create result
		result = new ExecutionResultImpl(repository, description);
		result.addMessage("id", message);

		// test
		assertEquals(1, result.getMessages().size());
	}

	/**
	 * Test that result contains two message.
	 */
	@Test
	public void testInstanceContainsTwoMessages() {

		// setup
		String description = "a description";
		String message = "message";
		String message2 = "message2";

		// create result
		result = new ExecutionResultImpl(repository, description);
		result.addMessage("id", message);
		result.addMessage("id2", message2);

		// test
		assertEquals(2, result.getMessages().size());
	}

	/**
	 * Test successful result have the expected attributes.
	 */
	@Test
	public void testCompletedAsSuccessfulContainsExpectedAttributes() {

		// define arguments
		String key = "some key";
		Object[] args = null;

		// create result
		result = new ExecutionResultImpl(DESCRIPTION);

		// set state
		result.completeAsSuccessful(messageProvider, key, args);

		// test
		assertEquals(DESCRIPTION, result.getDescription());
		assertNotNull(result.getChildren());
		assertEquals(0, result.getChildren().length);
		assertEquals(1, result.getMessages().size());
		assertEquals(null, result.getParent());
		assertEquals(ExecutionState.SUCCESS, result.getState());
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_MESSAGE));
	}

	/**
	 * Test successful result have the expected attributes.
	 */
	@Test
	public void testCompletedAsSuccessfulWithNoArgsContainsExpectedAttributes() {

		// re-create provider with no args
		messageProvider = createMessageProviderWithNoArgs();

		// define arguments
		String key = "some key";

		// create result
		result = new ExecutionResultImpl(DESCRIPTION);

		// set state
		result.completeAsSuccessful(messageProvider, key);

		// test
		assertEquals(DESCRIPTION, result.getDescription());
		assertNotNull(result.getChildren());
		assertEquals(0, result.getChildren().length);
		assertEquals(1, result.getMessages().size());
		assertEquals(null, result.getParent());
		assertEquals(ExecutionState.SUCCESS, result.getState());
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_MESSAGE));
	}

	/**
	 * Test failed result have the expected attributes.
	 */
	@Test
	public void testCompletedAsFailureContainsExpectedAttributes() {

		// define arguments
		String key = "some key";
		Object[] args = null;

		// create result
		result = new ExecutionResultImpl(DESCRIPTION);

		// set state

		result.completeAsFailure(messageProvider, key, args);

		// test
		assertEquals(DESCRIPTION, result.getDescription());
		assertNotNull(result.getChildren());
		assertEquals(0, result.getChildren().length);
		assertEquals(1, result.getMessages().size());
		assertEquals(null, result.getParent());
		assertTrue(result.isFailed());
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_ERROR_MESSAGE));
		assertEquals(key, result.getMessages().get(ExecutionResult.MSG_ERROR_MESSAGE));
	}

	/**
	 * Test failed result have the expected attributes.
	 */
	@Test
	public void testCompletedAsFailureWithNoArgsContainsExpectedAttributes() {

		// re-create provider with no args
		messageProvider = createMessageProviderWithNoArgs();

		// define arguments
		String key = "some key";

		// create result
		result = new ExecutionResultImpl(DESCRIPTION);

		// set state

		result.completeAsFailure(messageProvider, key);

		// test
		assertEquals(DESCRIPTION, result.getDescription());
		assertNotNull(result.getChildren());
		assertEquals(0, result.getChildren().length);
		assertEquals(1, result.getMessages().size());
		assertEquals(null, result.getParent());
		assertTrue(result.isFailed());
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_ERROR_MESSAGE));
		assertEquals(key, result.getMessages().get(ExecutionResult.MSG_ERROR_MESSAGE));
	}

	/**
	 * Test error result have the expected attributes.
	 */
	@Test
	public void testCompletedAsErrorContainsExpectedAttributes() {

		// define arguments
		String key = "some key";
		Object[] args = null;
		Exception exception = new Exception(randomMessage);

		// create result
		result = new ExecutionResultImpl(DESCRIPTION);

		// set state
		result.completeAsError(messageProvider, key, args, exception);

		// test
		assertEquals(DESCRIPTION, result.getDescription());
		assertNotNull(result.getChildren());
		assertEquals(0, result.getChildren().length);
		assertEquals(2, result.getMessages().size());
		assertEquals(null, result.getParent());
		assertTrue(result.isError());
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_STACKTRACE));
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_ERROR_MESSAGE));
		assertEquals(key, result.getMessages().get(ExecutionResult.MSG_ERROR_MESSAGE));
	}

	/**
	 * Test error result have the expected attributes.
	 */
	@Test
	public void testCompletedAsErrorWithNoArgsContainsExpectedAttributes() {

		// re-create provider with no args
		messageProvider = createMessageProviderWithNoArgs();

		// define arguments
		String key = "some key";
		Exception exception = new Exception();

		// create result
		result = new ExecutionResultImpl(DESCRIPTION);

		// set state
		result.completeAsError(messageProvider, key, exception);

		// test
		assertEquals(DESCRIPTION, result.getDescription());
		assertNotNull(result.getChildren());
		assertEquals(0, result.getChildren().length);
		assertEquals(2, result.getMessages().size());
		assertEquals(null, result.getParent());
		assertEquals(ExecutionState.ERROR, result.getState());
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_ERROR_MESSAGE));
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_STACKTRACE));
		assertEquals(key, result.getMessages().get(ExecutionResult.MSG_ERROR_MESSAGE));
	}

	/**
	 * Test error result have the expected attributes.
	 */
	@Test
	public void testCompletedAsErrorWithExceptionContainsExpectedAttributes() {

		// re-create provider with no args
		messageProvider = createMessageProviderWithNoArgs();

		// define arguments
		String key = "some key";
		Exception exception = new Exception(randomMessage);

		// create result
		result = new ExecutionResultImpl(DESCRIPTION);

		// set state
		result.completeAsError(exception);

		// test
		assertEquals(DESCRIPTION, result.getDescription());
		assertNotNull(result.getChildren());
		assertEquals(0, result.getChildren().length);
		assertEquals(2, result.getMessages().size());
		assertEquals(null, result.getParent());
		assertEquals(ExecutionState.ERROR, result.getState());
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_ERROR_MESSAGE));
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_STACKTRACE));
		assertEquals(randomMessage, result.getMessages().get(ExecutionResult.MSG_ERROR_MESSAGE));
	}

	/**
	 * Test composite result have the expected attributes.
	 */
	@Test
	public void testCompletedAsCompositeContainsExpectedAttributes() {

		// define arguments
		String key = "some key";
		Object[] args = null;

		// create result
		result = new ExecutionResultImpl(DESCRIPTION);

		// set state
		result.completeAsComputed(messageProvider, key, args);

		// test
		assertEquals(DESCRIPTION, result.getDescription());
		assertNotNull(result.getChildren());
		assertEquals(0, result.getChildren().length);
		assertEquals(2, result.getMessages().size());
		assertEquals(null, result.getParent());
		assertEquals(ExecutionState.SUCCESS, result.getState());
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_MESSAGE));
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_COMPOSITE));
	}

	/**
	 * Test composite result have the expected attributes.
	 */
	@Test
	public void testCompletedAsCompositeWithNoArgsContainsExpectedAttributes() {

		// re-create provider with no args
		messageProvider = createMessageProviderWithNoArgs();

		// define arguments
		String key = "some key";

		// create result
		result = new ExecutionResultImpl(DESCRIPTION);

		// set state
		result.completeAsComputed(messageProvider, key);

		// test
		assertEquals(DESCRIPTION, result.getDescription());
		assertNotNull(result.getChildren());
		assertEquals(0, result.getChildren().length);
		assertEquals(2, result.getMessages().size());
		assertEquals(null, result.getParent());
		assertEquals(ExecutionState.SUCCESS, result.getState());
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_MESSAGE));
		assertTrue(result.getMessages().containsKey(ExecutionResult.MSG_COMPOSITE));
	}

	/**
	 * Test that added message is appended to existing message.
	 */
	@Test
	public void testAddedMessageIsAppended() {

		// create string
		String randomStr = RandomStringUtils.randomAlphabetic(10);
		String randomStr2 = RandomStringUtils.randomAlphabetic(10);

		// create result with no parent
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// add message
		result.addMessage(ExecutionResult.MSG_MESSAGE, randomStr);
		result.addMessage(ExecutionResult.MSG_MESSAGE, randomStr2);

		// get message
		String actual = result.getMessages().get(ExecutionResult.MSG_MESSAGE);

		// expected
		StringBuilder expected = new StringBuilder();
		expected.append(randomStr);
		expected.append(SystemUtils.LINE_SEPARATOR);
		expected.append(randomStr2);

		// test
		assertEquals(expected.toString(), actual);
	}

	/**
	 * Test that a re-computation replaces composite execution result text.
	 * 
	 * It is asserted that the second computation isn't appended to the message
	 * field but that it only contains a single message line despite the multiple
	 * computations.
	 */
	@Test
	public void testRecomputationOfResultReplacesCompositeExecutionResultMessage() throws Exception {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// compute state
		result.setState(ExecutionState.COMPUTED);

		// get text
		String compositeMsg = result.getMessages().get(ExecutionResult.MSG_COMPOSITE);

		// compute state again
		result.setState(ExecutionState.COMPUTED);

		// get text again
		String compositeMsg2 = result.getMessages().get(ExecutionResult.MSG_COMPOSITE);

		// test
		assertEquals(0, compositeMsg.compareToIgnoreCase(compositeMsg2));
		assertEquals(1, org.apache.commons.lang.StringUtils.countMatches(compositeMsg2, "Results: "));
	}

	/**
	 * Test that a re-computation replaces composite execution result text.
	 * 
	 * It is asserted that the hash code of the two messages is equal.
	 */
	@Test
	public void testRecomputationOfResultReplacesCompositeExecutionResultMessageWithIdentialHashcodes()
			throws Exception {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// compute state
		result.setState(ExecutionState.COMPUTED);

		// get text
		String compositeMsg = result.getMessages().get(ExecutionResult.MSG_COMPOSITE);

		// compute state again
		result.setState(ExecutionState.COMPUTED);

		// get text again
		String compositeMsg2 = result.getMessages().get(ExecutionResult.MSG_COMPOSITE);

		// test
		assertTrue(compositeMsg.hashCode() == compositeMsg2.hashCode());
	}

	/**
	 * Test that a re-computation replaces composite execution result text.
	 * 
	 * It is asserted that the two generated composite execution result texts are
	 * different (a child is added to the second).
	 * 
	 * It is asserted that the second computation replaces the first message despite
	 * the multiple computations.
	 */
	@Test
	public void testRecomputationOfResultReplacesCompositeExecutionResultMessage2() throws Exception {
		result = new ExecutionResultImpl(repository, DESCRIPTION);

		// compute state
		result.setState(ExecutionState.COMPUTED);

		// get text
		String compositeMsg = result.getMessages().get(ExecutionResult.MSG_COMPOSITE);
		assertEquals(1, org.apache.commons.lang.StringUtils.countMatches(compositeMsg,
				"Results: 0, successful: 0, failures: 0, errors: 0, interrupted: 0"));

		// add child
		ExecutionResult child1 = result.addChild("child#1");

		// set state , which is propagated to parent
		child1.setState(ExecutionState.SUCCESS);

		// get text again
		String compositeMsg2 = result.getMessages().get(ExecutionResult.MSG_COMPOSITE);

		// assert texts are identical
		assertEquals(-1, compositeMsg.compareToIgnoreCase(compositeMsg2));
		assertEquals(1, org.apache.commons.lang.StringUtils.countMatches(compositeMsg2,
				"Results: 1, successful: 1, failures: 0, errors: 0, interrupted: 0."));
	}

	/**
	 * Get root result return result itself if it is a root result.
	 */
	@Test
	public void testGetRootResultReturnsSelfForRoot() {

		// create result
		result = new ExecutionResultImpl(repository, randomDescription);

		// test
		assertEquals(result, result.getRootResult());
	}

	/**
	 * Get root result return root result.
	 */
	@Test
	public void testGetRootResult() {
		result = new ExecutionResultImpl(repository, randomDescription);

		// add child
		ExecutionResult child1 = result.addChild(randomDescription2);
		ExecutionResult child2 = child1.addChild(randomDescription2);

		// test
		assertEquals(result, child1.getRootResult());
		assertEquals(result, child2.getRootResult());
	}

	/**
	 * Test that get first child return null for root result.
	 */
	@Test
	public void testGetFirstChildReturnsNullForRoot() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);
		assertTrue(result.isRoot());
		assertNull(result.getFirstChild());
	}

	/**
	 * Test that get first child return child result.
	 */
	@Test
	public void testCanGetFirstChild() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);
		ExecutionResult child = result.addChild("child#1");
		assertNotNull(result.getFirstChild());
		assertEquals(child, result.getFirstChild());
	}

	/**
	 * Test that get first child return same child result event if multiple children
	 * are added.
	 */
	@Test
	public void testReturnsSameFirstChildWhenAddingMultipleChildren() {
		result = new ExecutionResultImpl(repository, DESCRIPTION);
		ExecutionResult child = result.addChild("child#1");
		result.addChild("child#2");
		result.addChild("child#3");
		assertNotNull(result.getFirstChild());
		assertEquals(child, result.getFirstChild());
	}

}
