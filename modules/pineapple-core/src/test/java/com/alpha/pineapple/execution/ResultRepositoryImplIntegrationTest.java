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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleInfoImpl;

/**
 * Integration test of the class <code>ResultRepositoryImpl</code>.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ResultRepositoryImplIntegrationTest {

	/**
	 * Callable class which adds 10 child results and queries for the result
	 * sequence.
	 */
	public class Add10Results implements Callable<String> {
		ExecutionResult rootResult = null;
		ExecutionInfo info = null;

		public void setInfo(ExecutionInfo info) {
			this.info = info;
		}

		public void setResult(ExecutionResult result) {
			rootResult = result;
		}

		@Override
		public String call() throws Exception {
			rootResult.addChild(createRandomDescription());
			resultRepository.getResultSequence(info, 0, resultRepository.getCurrentResultIndex(info));
			rootResult.addChild(createRandomDescription());
			resultRepository.getResultSequence(info, 0, resultRepository.getCurrentResultIndex(info));
			rootResult.addChild(createRandomDescription());
			resultRepository.getResultSequence(info, 0, resultRepository.getCurrentResultIndex(info));
			rootResult.addChild(createRandomDescription());
			resultRepository.getResultSequence(info, 0, resultRepository.getCurrentResultIndex(info));
			rootResult.addChild(createRandomDescription());
			return Thread.currentThread().getName();
		}
	}

	/**
	 * Module id.
	 */
	static final String MODULE_ID = "module-id";

	/**
	 * Object under test.
	 */
	@Resource
	ResultRepository resultRepository;

	/**
	 * Module info.
	 */
	ModuleInfo moduleInfo;

	/**
	 * Random boolean
	 */
	boolean randomBoolean;

	/**
	 * Random description.
	 */
	String randomDescription;

	/**
	 * Random environment.
	 */
	String randomEnvironment;

	/**
	 * Random operation.
	 */
	String randomOperation;

	@Before
	public void setUp() throws Exception {
		randomBoolean = RandomUtils.nextBoolean();
		randomDescription = RandomStringUtils.randomAlphabetic(10) + "-desc";
		randomEnvironment = RandomStringUtils.randomAlphabetic(10) + "-env";
		randomOperation = RandomStringUtils.randomAlphabetic(10);

		moduleInfo = ModuleInfoImpl.getInstance(MODULE_ID, new String[] {}, randomBoolean, new File("some-directory"));
	}

	@After
	public void tearDown() throws Exception {
		moduleInfo = null;
	}

	/**
	 * Create random description.
	 * 
	 * @return random description.
	 */
	String createRandomDescription() {
		return RandomStringUtils.randomAlphabetic(10) + "-desc";
	}

	/**
	 * Test that repository can be looked up from the context.
	 */
	@Test
	public void testCanGetRepositoryFromContext() {
		assertNotNull(resultRepository);
	}

	/**
	 * Test that listeners array size is initially zero.
	 */
	@Test
	public void testListenersArraySizeIsIntiallyZero() {
		assertNotNull(resultRepository);
		assertEquals(0, resultRepository.getListeners().length);
	}

	/**
	 * Test new execution result returns defined result.
	 */
	@Test
	public void testNewExecutionReturnsDefinedResult() {
		ExecutionResult result = resultRepository.startExecution(randomDescription);
		assertNotNull(result);
	}

	/**
	 * Test new execution result contains correct description.
	 */
	@Test
	public void testExecutionResultContainsCorrectDescription() {
		ExecutionResult result = resultRepository.startExecution(randomDescription);
		assertEquals(randomDescription, result.getDescription());
	}

	/**
	 * Test new execution result is running.
	 */
	@Test
	public void testExecutionResultIsRunning() {
		ExecutionResult result = resultRepository.startExecution(randomDescription);
		assertEquals(ExecutionState.EXECUTING, result.getState());
	}

	/**
	 * Test new execution returns defined execution info.
	 */
	@Test
	public void testNewExecutionResturnsDefinedInfo() {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		assertNotNull(info);
	}

	/**
	 * Test new execution returns defined properties.
	 */
	@Test
	public void testExecutionInfoContainsCorrectProperties() {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		assertEquals(randomEnvironment, info.getEnvironment());
		assertEquals(moduleInfo, info.getModuleInfo());
		assertEquals(randomOperation, info.getOperation());
	}

	/**
	 * Test new execution returns defined execution result.
	 */
	@Test
	public void testExecutionInfoContainsDefinedExecutionResult() {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		assertNotNull(info.getResult());
		assertEquals(ExecutionState.EXECUTING, info.getResult().getState());
		assertTrue(info.getResult().getDescription().startsWith("Execute operation ["));
	}

	/**
	 * Test execution info is available for result sequence query
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testExecutionInfoIsDefinedForResultsSequenceIndexQueyAfterExecution() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);

		// test
		assertNotNull(resultRepository.getCurrentResultIndex(info));
	}

	/**
	 * Test result sequence index query returns expected index.
	 * 
	 * Execution creates a single result.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceIndexQueyReturnsExpectedIndex() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);

		// test
		assertEquals(1, resultRepository.getCurrentResultIndex(info));
	}

	/**
	 * Test result sequence index query returns expected index.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceIndexQueyReturnsExpectedIndex2() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);

		// add child result to increase result sequence size
		ExecutionResult rootResult = info.getResult();
		rootResult.addChild(randomDescription);

		// test
		assertEquals(2, resultRepository.getCurrentResultIndex(info));
	}

	/**
	 * Test result sequence index query returns expected index.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceIndexQueyReturnsExpectedIndex3() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);

		// add child result to increase result sequence size
		ExecutionResult rootResult = info.getResult();
		rootResult.addChild(randomDescription);
		rootResult.addChild(randomDescription);
		rootResult.addChild(randomDescription);
		rootResult.addChild(randomDescription);
		rootResult.addChild(randomDescription);

		// test
		assertEquals(6, resultRepository.getCurrentResultIndex(info));
	}

	/**
	 * Test execution info is available for result sequence query
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testExecutionInfoIsDefinedForResultsSequenceQueyAfterExecution() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);

		// test
		assertNotNull(resultRepository.getResultSequence(info, 0, 0));
	}

	/**
	 * Test empty result sequence can be queried from execution.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceQueyCanQueryEmptySequence() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionResultNotification[] sequence = resultRepository.getResultSequence(info, 0, 0);

		// test
		assertEquals(0, sequence.length);
	}

	/**
	 * Test result sequence of one can be queried from execution.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceQueyCanQuerySequenceWithOneResult() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionResultNotification[] sequence = resultRepository.getResultSequence(info, 0, 1);

		// test
		assertEquals(1, sequence.length);
	}

	/**
	 * Test result sequence of five can be queried from execution.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceQueyCanQuerySequenceWithFiveResults() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		// add child result to increase result sequence size
		ExecutionResult rootResult = info.getResult();
		rootResult.addChild(randomDescription);
		rootResult.addChild(randomDescription);
		rootResult.addChild(randomDescription);
		rootResult.addChild(randomDescription);

		ExecutionResultNotification[] sequence = resultRepository.getResultSequence(info, 0, 5);

		// test
		assertEquals(5, sequence.length);
	}

	/**
	 * Test result sequence of one for started execution contains the correct state:
	 * EXECUTING for the first notification.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceContainsCorrectExecutingState() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionResultNotification[] sequence = resultRepository.getResultSequence(info, 0, 1);

		// test
		assertEquals(ExecutionState.EXECUTING, sequence[0].getState());
		assertEquals(ExecutionState.EXECUTING, sequence[0].getResult().getState());
	}

	/**
	 * Test result sequence of two for successful execution contains the correct
	 * state: EXECUTING for the first notification. SUCCESS for the second
	 * notification.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceContainsCorrectSuccessfulState() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info.getResult().setState(ExecutionState.SUCCESS);
		ExecutionResultNotification[] sequence = resultRepository.getResultSequence(info, 0, 2);

		// test
		assertEquals(ExecutionState.EXECUTING, sequence[0].getState());
		assertEquals(ExecutionState.SUCCESS, sequence[1].getState());
		assertEquals(ExecutionState.SUCCESS, sequence[0].getResult().getState());
		assertEquals(ExecutionState.SUCCESS, sequence[1].getResult().getState());
	}

	/**
	 * Test result sequence of two for successful execution contains the correct
	 * state: EXECUTING for the first notification. SUCCESS for the second
	 * notification.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceContainsCorrectComputedState() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info.getResult().setState(ExecutionState.COMPUTED);
		ExecutionResultNotification[] sequence = resultRepository.getResultSequence(info, 0, 2);

		// test
		assertEquals(ExecutionState.EXECUTING, sequence[0].getState());
		assertEquals(ExecutionState.SUCCESS, sequence[1].getState());
		assertEquals(ExecutionState.SUCCESS, sequence[0].getResult().getState());
		assertEquals(ExecutionState.SUCCESS, sequence[1].getResult().getState());
	}

	/**
	 * Test result sequence of two for successful execution contains the correct
	 * state: EXECUTING for the first notification. FAILURE for the second
	 * notification.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceContainsCorrectFailureState() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info.getResult().setState(ExecutionState.FAILURE);
		ExecutionResultNotification[] sequence = resultRepository.getResultSequence(info, 0, 2);

		// test
		assertEquals(ExecutionState.EXECUTING, sequence[0].getState());
		assertEquals(ExecutionState.FAILURE, sequence[1].getState());
		assertEquals(ExecutionState.FAILURE, sequence[0].getResult().getState());
		assertEquals(ExecutionState.FAILURE, sequence[1].getResult().getState());
	}

	/**
	 * Test result sequence of two for successful execution contains the correct
	 * state: EXECUTING for the first notification. ERROR for the second
	 * notification.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceContainsCorrectErrorState() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info.getResult().setState(ExecutionState.ERROR);
		ExecutionResultNotification[] sequence = resultRepository.getResultSequence(info, 0, 2);

		// test
		assertEquals(ExecutionState.EXECUTING, sequence[0].getState());
		assertEquals(ExecutionState.ERROR, sequence[1].getState());
		assertEquals(ExecutionState.ERROR, sequence[0].getResult().getState());
		assertEquals(ExecutionState.ERROR, sequence[1].getResult().getState());
	}

	/**
	 * Test that setting the current state of a result in an execution, e.g. setting
	 * the state EXECUTING for an EXECUTING result, results in the addition of a
	 * notification.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testNotificationIsAddedWhenStateIsSetToCurrentState() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info.getResult().setState(ExecutionState.EXECUTING);
		int lastIndex = resultRepository.getCurrentResultIndex(info);

		// test
		assertEquals(2, lastIndex);
		ExecutionResultNotification[] sequence = resultRepository.getResultSequence(info, 0, 2);
		assertEquals(ExecutionState.EXECUTING, sequence[0].getState());
		assertEquals(ExecutionState.EXECUTING, sequence[1].getState());
		assertEquals(ExecutionState.EXECUTING, sequence[0].getResult().getState());
		assertEquals(ExecutionState.EXECUTING, sequence[1].getResult().getState());
	}

	/**
	 * Test that query of last index larger than current index throws exception
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsLastIndexLargerthanCurrentIndex() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		resultRepository.getResultSequence(info, 0, 5);
	}

	/**
	 * Test result sequence of one can be queried twice from execution.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceQueyCanQuerySequenceWithOneResultTwice() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionResultNotification[] sequence = resultRepository.getResultSequence(info, 0, 1);
		ExecutionResultNotification[] sequence2 = resultRepository.getResultSequence(info, 0, 1);

		// test
		assertEquals(1, sequence.length);
		assertEquals(1, sequence2.length);
	}

	/**
	 * Test result sequence of one can be queried twice from execution. It is
	 * asserted that both contains the same result.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testResultsSequenceQueyCanQuerySequenceWithOneResultTwice2() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionResultNotification[] sequence = resultRepository.getResultSequence(info, 0, 1);
		ExecutionResultNotification[] sequence2 = resultRepository.getResultSequence(info, 0, 1);

		// test
		assertEquals(sequence[0], sequence2[0]);
	}

	/**
	 * Test that the repository stores the last 5 running executions.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testRepositoryStoresTheLastFiveRunningExecutions() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info2 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info3 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info4 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info5 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info6 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);

		// test
		assertEquals(1, resultRepository.getResultSequence(info6, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info5, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info4, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info3, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info2, 0, 1).length);
	}

	/**
	 * Test that the repository stores the last 5 completed executions.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testRepositoryStoresTheLastFiveCompletedExecutions() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info.getResult().setState(ExecutionState.SUCCESS);
		ExecutionInfo info2 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info2.getResult().setState(ExecutionState.SUCCESS);
		ExecutionInfo info3 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info3.getResult().setState(ExecutionState.SUCCESS);
		ExecutionInfo info4 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info4.getResult().setState(ExecutionState.SUCCESS);
		ExecutionInfo info5 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info5.getResult().setState(ExecutionState.SUCCESS);
		ExecutionInfo info6 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info6.getResult().setState(ExecutionState.SUCCESS);

		// test
		assertEquals(1, resultRepository.getResultSequence(info6, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info5, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info4, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info3, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info2, 0, 1).length);

	}

	/**
	 * Test that the repository stores more than the the last 5 running executions.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testRepositoryStoresMoreThanTheLastFiveRunningExecutions() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info2 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info3 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info4 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info5 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info6 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info7 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info8 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		ExecutionInfo info9 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);

		// test
		assertEquals(1, resultRepository.getResultSequence(info9, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info8, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info7, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info6, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info5, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info4, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info3, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info2, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info, 0, 1).length);
	}

	/**
	 * Test that the repository only stores the last 5 completed executions.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = ExecutionInfoNotFoundException.class)
	public void testRepositoryOnlyStoresTheLastFiveCompletedExecutions() throws Exception {
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info.getResult().setState(ExecutionState.SUCCESS);
		ExecutionInfo info2 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info2.getResult().setState(ExecutionState.SUCCESS);
		ExecutionInfo info3 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info3.getResult().setState(ExecutionState.SUCCESS);
		ExecutionInfo info4 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info4.getResult().setState(ExecutionState.SUCCESS);
		ExecutionInfo info5 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info5.getResult().setState(ExecutionState.SUCCESS);
		ExecutionInfo info6 = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		info6.getResult().setState(ExecutionState.SUCCESS);

		// test
		assertEquals(1, resultRepository.getResultSequence(info6, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info5, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info4, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info3, 0, 1).length);
		assertEquals(1, resultRepository.getResultSequence(info2, 0, 1).length);

		// triggers execution
		assertEquals(1, resultRepository.getResultSequence(info, 0, 1).length);
	}

	/**
	 * Test result sequence can be concurrently accessed for insert and read. 100x
	 * inserts and reads.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testConcurrentResultsSequenceAccessX100Thread() throws Exception {
		int count = 100;
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		concurrentSequenceAcces(info, count);
	}

	/**
	 * Test result sequence can be concurrently accessed for insert and read. 50x
	 * inserts and reads.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testConcurrentResultsSequenceAccessX50Threads() throws Exception {
		int count = 50;
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		concurrentSequenceAcces(info, count);
	}

	/**
	 * Test result sequence can be concurrently accessed for insert and read. 1000x
	 * inserts and reads.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testConcurrentResultsSequenceAccessX1000Threads() throws Exception {
		int count = 1000;
		ExecutionInfo info = resultRepository.startExecution(moduleInfo, randomEnvironment, randomOperation);
		concurrentSequenceAcces(info, count);
	}

	/**
	 * Creates and concurrently executes tasks which a) Adds N x 10 child results.
	 * 
	 * @param info
	 *            execution info.
	 * @param threadCount
	 *            number of concurrent tasks each of which adds 10 children.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	void concurrentSequenceAcces(ExecutionInfo info, final int threadCount) throws Exception {
		ExecutionResult rootResult = info.getResult();

		// create task
		Add10Results task = new Add10Results();
		task.setResult(rootResult);
		task.setInfo(info);

		// clone and execute tasks
		List<Callable<String>> tasks = Collections.nCopies(threadCount, (Callable<String>) task);
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		List<Future<String>> futures = executorService.invokeAll(tasks);
		List<String> resultList = new ArrayList<String>(futures.size());

		// Check for exceptions
		for (Future<String> future : futures) {
			// Throws an exception if an exception was thrown by the task.
			resultList.add(future.get());
		}

		// test
		Assert.assertEquals(threadCount, futures.size());
	}

}
