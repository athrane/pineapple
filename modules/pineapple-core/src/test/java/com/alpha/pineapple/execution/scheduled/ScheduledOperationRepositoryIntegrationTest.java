/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.execution.scheduled;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.execution.OperationTask;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperation;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Integration test of the {@linkplain ScheduledOperationRepositoryImpl} class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class ScheduledOperationRepositoryIntegrationTest {

	/**
	 * First list index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * CRON value scheduled for daily execution.
	 */
	static final String CRON_DAILY = "0 0 * * * *";

	/**
	 * CRON value scheduled for execution every minute.
	 */
	static final String CRON_MIN = "* * * * * *";

	/**
	 * Scheduled operation repository.
	 */
	@Resource
	ScheduledOperationRespository scheduledOperationRepository;

	/**
	 * Execution result factory.
	 */
	@Resource
	ExecutionResultFactory executionResultFactory;

	/**
	 * Random value.
	 */
	String randomName;

	/**
	 * Random value.
	 */
	String randomModule;

	/**
	 * Random value.
	 */
	String randomOperation;

	/**
	 * Random value.
	 */
	String randomEnvironment;

	/**
	 * Random value.
	 */
	String randomDescription;

	/**
	 * Execution result.
	 */
	ExecutionResult result;

	@Before
	public void setUp() throws Exception {
		randomName = RandomStringUtils.randomAlphabetic(10);
		randomModule = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		randomOperation = RandomStringUtils.randomAlphabetic(10);
		randomDescription = RandomStringUtils.randomAlphabetic(10);

		result = executionResultFactory.startExecution(randomDescription);
	}

	@After
	public void tearDown() throws Exception {

		// delete all operations
		scheduledOperationRepository.deleteAll();
	}

	/**
	 * Test that instance can be retrieved from application context.
	 */
	@Test
	public void testGetInstanceFromContext() {
		assertNotNull(scheduledOperationRepository);
	}

	/**
	 * Test that repository can be initialized.
	 */
	@Test
	public void testInitialize() {
		scheduledOperationRepository.initialize(result);
	}

	/**
	 * Test that initialized repository returns expected execution result.
	 */
	@Test
	public void testInitializeContainsExpectedResult() {
		scheduledOperationRepository.initialize(result);
		assertTrue(result.isExecuting());
		assertEquals(1, result.getChildren().length);
		assertTrue(result.getFirstChild().isSuccess());
	}

	/**
	 * Test that no operations are defined after first initialization.
	 */
	@Test
	public void testNoOperationsAreDefinedAfterFirstInitialization() {
		scheduledOperationRepository.initialize(result);
		assertNotNull(scheduledOperationRepository.getOperations());
		assertEquals(0, scheduledOperationRepository.getOperations().count());
	}

	/**
	 * Test that operations are retained after initialization.
	 */
	@Test
	public void testOperationsAreRetainedAfterInitialization() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		assertEquals(1, scheduledOperationRepository.getOperations().count());
		scheduledOperationRepository.initialize(result);
		assertNotNull(scheduledOperationRepository.getOperations());
		assertEquals(1, scheduledOperationRepository.getOperations().count());
	}

	/**
	 * Test that empty repository can be initialized multiple times
	 */
	@Test
	public void testEmptyRepositoryCanBeInitializedMultipleTimes() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.deleteAll();
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.initialize(result);
		assertNotNull(scheduledOperationRepository.getOperations());
		assertEquals(0, scheduledOperationRepository.getOperations().count());
	}

	/**
	 * Test that repository with one operation can be initialized multiple times
	 */
	@Test
	public void testRepositoryCanBeInitializedMultipleTimes() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.initialize(result);
		assertNotNull(scheduledOperationRepository.getOperations());
		assertEquals(1, scheduledOperationRepository.getOperations().count());
	}

	/**
	 * Test that operation can be added to repository.
	 */
	@Test
	public void testCreateOperation() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		assertEquals(1, scheduledOperationRepository.getOperations().count());
		List<ScheduledOperationInfo> operationList = scheduledOperationRepository.getOperations()
				.collect(Collectors.toList());
		assertNotNull(operationList);
		ScheduledOperationInfo info = operationList.get(FIRST_INDEX);
		ScheduledOperation operation = info.getOperation();
		assertNotNull(operation);

		assertEquals(randomName, operation.getName());
		assertEquals(randomModule, operation.getModule());
		assertEquals(randomOperation, operation.getOperation());
		assertEquals(randomEnvironment, operation.getEnvironment());
		assertEquals(randomDescription, operation.getDescription());
		assertEquals(CRON_DAILY, operation.getCron());
	}

	/**
	 * Test that multiple operations can be added to repository.
	 */
	@Test
	public void testCreateMultipleOperations() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		scheduledOperationRepository.create(randomName + "2", randomModule + "2", randomOperation + "2",
				randomEnvironment + "2", randomDescription + "2", CRON_DAILY);
		assertEquals(2, scheduledOperationRepository.getOperations().count());
		List<ScheduledOperationInfo> operationList = scheduledOperationRepository.getOperations()
				.collect(Collectors.toList());
		assertNotNull(operationList);
		ScheduledOperationInfo info = operationList.get(FIRST_INDEX);
		ScheduledOperation operation = info.getOperation();
		assertNotNull(operation);

		assertEquals(randomName, operation.getName());
		assertEquals(randomModule, operation.getModule());
		assertEquals(randomOperation, operation.getOperation());
		assertEquals(randomEnvironment, operation.getEnvironment());
		assertEquals(randomDescription, operation.getDescription());
		assertEquals(CRON_DAILY, operation.getCron());

		ScheduledOperationInfo info2 = operationList.get(1);
		ScheduledOperation operation2 = info2.getOperation();
		assertNotNull(operation2);

		assertEquals(randomName + "2", operation2.getName());
		assertEquals(randomModule + "2", operation2.getModule());
		assertEquals(randomOperation + "2", operation2.getOperation());
		assertEquals(randomEnvironment + "2", operation2.getEnvironment());
		assertEquals(randomDescription + "2", operation2.getDescription());
		assertEquals(CRON_DAILY, operation2.getCron());
	}

	/**
	 * Test that addition of operation with undefined name fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateOperationWithUndefinedName() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(null, randomModule, randomOperation, randomEnvironment, randomDescription,
				CRON_DAILY);
	}

	/**
	 * Test that addition of operation with empty name fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateOperationWithEmptyName() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create("", randomModule, randomOperation, randomEnvironment, randomDescription,
				CRON_DAILY);
	}

	/**
	 * Test that addition of operation with undefined module fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateOperationWithUndefinedModule() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, null, randomOperation, randomEnvironment, randomDescription,
				CRON_DAILY);
	}

	/**
	 * Test that addition of operation with empty module fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateOperationWithEmptyModule() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, "", randomOperation, randomEnvironment, randomDescription,
				CRON_DAILY);
	}

	/**
	 * Test that addition of operation with undefined operation fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateOperationWithUndefinedOperation() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, null, randomEnvironment, randomDescription,
				CRON_DAILY);
	}

	/**
	 * Test that addition of operation with empty operation fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateOperationWithEmptyOperation() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, "", randomEnvironment, randomDescription,
				CRON_DAILY);
	}

	/**
	 * Test that addition of operation with undefined environment fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateOperationWithUndefinedEnvironment() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, null, randomDescription,
				CRON_DAILY);
	}

	/**
	 * Test that addition of operation with empty environment fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateOperationWithEmptyEnvironment() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, "", randomDescription,
				CRON_DAILY);
	}

	/**
	 * Test that addition of operation with undefined description fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateOperationWithUndefinedDescription() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment, null,
				CRON_DAILY);
	}

	/**
	 * Test that operation with empty description can be created.
	 */
	@Test
	public void testCanCreateOperationWithEmptyDescription() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment, "",
				CRON_DAILY);
		assertEquals(1, scheduledOperationRepository.getOperations().count());
	}

	/**
	 * Test that addition of operation with undefined scheduling expression fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateOperationWithUndefinedCron() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, null);
	}

	/**
	 * Test that addition of operation with empty scheduling expression fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateOperationWithEmptyCron() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, "");
	}

	/**
	 * Test that addition of operation fails if name isn't unique.
	 */
	@Test(expected = ScheduledOperationAlreadyExistsException.class)
	public void testFailsToCreateOperationIfNameIsntUnique() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
	}

	@Test
	public void testFailsToCreateOperationIfModuleDoesntExist() {
		fail("Not yet implemented");
	}

	@Test
	public void testFailsToCreateOperationIfModuleModelDoesntExist() {
		fail("Not yet implemented");
	}

	/**
	 * Test that scheduled operation can be removed using the name.
	 */
	@Test
	public void testDeleteByName() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		assertEquals(1, scheduledOperationRepository.getOperations().count());
		scheduledOperationRepository.delete(randomName);
		assertEquals(0, scheduledOperationRepository.getOperations().count());
	}

	/**
	 * Test that removal of operation by name fails with undefined name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToDeleteByNameWithUndefinedName() {
		scheduledOperationRepository.initialize(result);
		String nullName = null;
		scheduledOperationRepository.delete(nullName);
	}

	/**
	 * Test that removal of operation by name fails with empty name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToDeleteByNameWithEmptyName() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.delete("");
	}

	/**
	 * Test that removal of unknown operation fails.
	 */
	@Test(expected = ScheduledOperationNotFoundException.class)
	public void testFailsToDeleteUnknownOperationByName() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.delete(randomName);
		assertEquals(0, scheduledOperationRepository.getOperations().count());
	}

	/**
	 * Can get empty set of operations.
	 */
	@Test
	public void testGetEmptySetOfOperations() {
		scheduledOperationRepository.initialize(result);
		assertEquals(0, scheduledOperationRepository.getOperations().count());
	}

	/**
	 * Can get set with one operation.
	 */
	@Test
	public void testGetSetWithOneOperation() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		assertEquals(1, scheduledOperationRepository.getOperations().count());
		List<ScheduledOperationInfo> operationList = scheduledOperationRepository.getOperations()
				.collect(Collectors.toList());
		assertNotNull(operationList);
		ScheduledOperationInfo info = operationList.get(FIRST_INDEX);
		ScheduledOperation operation = info.getOperation();
		assertNotNull(operation);
		assertEquals(randomName, operation.getName());
	}

	/**
	 * Can get set with two operations.
	 */
	@Test
	public void testGetSetWithTwoOperations() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		scheduledOperationRepository.create(randomName + "2", randomModule + "2", randomOperation + "2",
				randomEnvironment + "2", randomDescription + "2", CRON_DAILY);
		assertEquals(2, scheduledOperationRepository.getOperations().count());
		List<ScheduledOperationInfo> operationList = scheduledOperationRepository.getOperations()
				.collect(Collectors.toList());
		assertNotNull(operationList);
		ScheduledOperationInfo info = operationList.get(FIRST_INDEX);
		ScheduledOperation operation = info.getOperation();
		assertNotNull(operation);

		assertEquals(randomName, operation.getName());
		ScheduledOperationInfo info2 = operationList.get(1);
		ScheduledOperation operation2 = info2.getOperation();
		assertNotNull(operation);

		assertEquals(randomName + "2", operation2.getName());
	}

	/**
	 * Can delete all on set on empty set of operations.
	 */
	@Test
	public void testDeleteAllOnEmptySetOfOperations() {
		scheduledOperationRepository.initialize(result);
		assertEquals(0, scheduledOperationRepository.getOperations().count());
		scheduledOperationRepository.deleteAll();
		assertEquals(0, scheduledOperationRepository.getOperations().count());
	}

	/**
	 * Can delete all on a set with one operation.
	 */
	@Test
	public void testDeleteAllOnSetWithOneOperation() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		assertEquals(1, scheduledOperationRepository.getOperations().count());
		scheduledOperationRepository.deleteAll();
		assertEquals(0, scheduledOperationRepository.getOperations().count());
	}

	/**
	 * Can remove all on a set with multiple operations.
	 */
	@Test
	public void testDeleteAllOnSetWithMultipleOperations() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		scheduledOperationRepository.create(randomName + "2", randomModule + "2", randomOperation + "2",
				randomEnvironment + "2", randomDescription + "2", CRON_DAILY);
		assertEquals(2, scheduledOperationRepository.getOperations().count());
		scheduledOperationRepository.deleteAll();
		assertEquals(0, scheduledOperationRepository.getOperations().count());
	}

	/**
	 * Can delete all on a set with multiple operations.
	 */
	@Test
	public void testDeleteAllOnSetWithMultipleOperations2() {
		scheduledOperationRepository.initialize(result);
		scheduledOperationRepository.create(randomName, randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		scheduledOperationRepository.create(randomName + "1", randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		scheduledOperationRepository.create(randomName + "2", randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		scheduledOperationRepository.create(randomName + "3", randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		scheduledOperationRepository.create(randomName + "4", randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		scheduledOperationRepository.create(randomName + "5", randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		scheduledOperationRepository.create(randomName + "6", randomModule, randomOperation, randomEnvironment,
				randomDescription, CRON_DAILY);
		scheduledOperationRepository.deleteAll();
		assertEquals(0, scheduledOperationRepository.getOperations().count());
	}

	/**
	 * Test that added operation is scheduled.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCreateedOperationIsScheduled() throws Exception {

		// create and inject mock core
		ExecutionInfo mockExecutionInfo = createMock(ExecutionInfo.class);
		OperationTask mockOperationTask = createMock(OperationTask.class);
		ReflectionTestUtils.setField(scheduledOperationRepository, "operationTask", mockOperationTask);
		expect(mockOperationTask.execute(randomOperation, randomEnvironment, randomModule))
				.andReturn(mockExecutionInfo);
		replay(mockOperationTask);

		// initialize and schedule
		scheduledOperationRepository.initialize(result);
		ScheduledOperationInfo info = scheduledOperationRepository.create(randomName, randomModule, randomOperation,
				randomEnvironment, randomDescription, CRON_MIN);

		// initial test
		ScheduledFuture<?> future = info.getFuture();
		assertFalse(future.isCancelled());
		assertFalse(future.isDone());

		future.get(); // invoke to wait

		// test
		verify(mockOperationTask);
	}

}
