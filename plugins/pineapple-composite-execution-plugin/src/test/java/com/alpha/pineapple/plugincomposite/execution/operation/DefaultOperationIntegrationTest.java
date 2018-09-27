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

package com.alpha.pineapple.plugincomposite.execution.operation;

import static com.alpha.pineapple.execution.ExecutionResult.ExecutionState.ERROR;
import static com.alpha.pineapple.execution.ExecutionResult.ExecutionState.FAILURE;
import static com.alpha.pineapple.execution.ExecutionResult.ExecutionState.SUCCESS;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.admin.AdministrationProvider;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.OperationTask;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleRepository;
import com.alpha.pineapple.plugin.composite.execution.operation.DefaultOperation;
import com.alpha.pineapple.session.Session;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Integration test for the <code>DeployConfiguration</code> class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.plugin.composite.execution-config.xml" })
public class DefaultOperationIntegrationTest {

	/**
	 * EasyMock Answer which sets the state of the execution result in the captured
	 * execution info object.
	 */
	class SetChildResultStateAnswer implements IAnswer<ExecutionInfo> {

		/**
		 * Execution result state to set.
		 */
		ExecutionState state;

		/**
		 * SetChildResultStateAnswer constructor.
		 * 
		 * @param state
		 *            execution result state to set.
		 */
		public SetChildResultStateAnswer(ExecutionState state) {
			super();
			this.state = state;
		}

		@Override
		public ExecutionInfo answer() throws Throwable {
			ExecutionInfo executionInfo = (ExecutionInfo) getCurrentArguments()[0]; // first
			// argument
			ExecutionResult currentResult = executionInfo.getResult();
			currentResult.setState(state);
			return executionInfo;
		}
	}

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Object under test.
	 */
	@Resource
	DefaultOperation defaultOperation;

	/**
	 * Mock Execution info provider. From the "integration-test" profile.
	 */
	@Resource
	ExecutionInfoProvider coreExecutionInfoProvider;

	/**
	 * Mock Administration info provider. From the "integration-test" profile.
	 */
	@Resource
	AdministrationProvider coreAdministrationProvider;

	/**
	 * Object mother for the composite execution model.
	 */
	ObjectMotherContent contentMother;

	/**
	 * Mock module repository.
	 */
	ModuleRepository moduleRepository;

	/**
	 * Mock operation task.
	 */
	OperationTask operationTask;

	/**
	 * Plugin session.
	 */
	Session session;

	/**
	 * Execution result.
	 */
	ExecutionResult result;

	/**
	 * Random name.
	 */
	String randomName;

	/**
	 * Random name.
	 */
	String randomName2;

	/**
	 * Random operation.
	 */
	String randomOperation;

	/**
	 * Random environment.
	 */
	String randomEnvironment;

	@Before
	public void setUp() throws Exception {
		randomName = RandomStringUtils.randomAlphanumeric(10);
		randomName2 = RandomStringUtils.randomAlphanumeric(10);
		randomOperation = RandomStringUtils.randomAlphanumeric(10);
		randomEnvironment = RandomStringUtils.randomAlphanumeric(10);

		// create mocks
		session = createMock(Session.class);
		moduleRepository = createMock(ModuleRepository.class);
		operationTask = createMock(OperationTask.class);

		// create execution result
		result = new ExecutionResultImpl("Root result");

		// create content mother
		contentMother = new ObjectMotherContent();

		// reset plugin provider
		reset(coreAdministrationProvider);
		reset(coreExecutionInfoProvider);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * initialize {@linkplain ExecutionInfo} mock.
	 * 
	 * @return {@linkplain ExecutionInfo} mock
	 */
	ExecutionInfo initializeExecutionInfoMock() {
		ExecutionInfo executionInfo = createMock(ExecutionInfo.class);
		expect(executionInfo.getOperation()).andReturn(randomOperation).anyTimes();
		expect(executionInfo.getEnvironment()).andReturn(randomEnvironment).anyTimes();
		replay(executionInfo);
		return executionInfo;
	}

	/**
	 * initialize {@linkplain ModuleInfo} mock.
	 * 
	 * @return {@linkplain ModuleInfo} mock
	 */
	ModuleInfo initializeModuleInfoMock() {
		return initializeModuleInfoMock(randomName);
	}

	/**
	 * initialize {@linkplain ModuleInfo} mock.
	 * 
	 * @param name
	 *            module name.
	 * 
	 * @return {@linkplain ModuleInfo} mock
	 */
	ModuleInfo initializeModuleInfoMock(String name) {
		ModuleInfo moduleInfo = createMock(ModuleInfo.class);
		expect(moduleInfo.getId()).andReturn(name).anyTimes();
		replay(moduleInfo);
		return moduleInfo;
	}

	/**
	 * Test that TestOperation can be looked up from the context.
	 */
	@Test
	public void testCanGetInstanceFromContext() {
		assertNotNull(defaultOperation);
	}

	/**
	 * Test that the operation can execute with a minimal model.
	 */
	@Test
	public void testCanExecuteWithMinimalModel() throws Exception {
		Object content = contentMother.createEmptyCompositeExecutionModel();

		// setup mocks
		replay(session);
		replay(coreAdministrationProvider);
		replay(coreExecutionInfoProvider);
		replay(moduleRepository);
		replay(operationTask);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(coreAdministrationProvider);
		verify(coreExecutionInfoProvider);
		verify(session);
		verify(moduleRepository);
		verify(operationTask);
	}

	/**
	 * Test that the operation can execute with a single composite. Composite
	 * completes with success which is propagated to the root result.
	 */
	@Test
	public void testCanExecuteWithSingleSuccessfulComposite() throws Exception {
		Object content = contentMother.createCompositeExecutionWithSingleComposite(randomName);

		ExecutionInfo executionInfo = initializeExecutionInfoMock();
		ModuleInfo moduleInfo = initializeModuleInfoMock();

		replay(session);
		expect(coreAdministrationProvider.getModuleRepository()).andReturn(moduleRepository);
		expect(coreAdministrationProvider.getOperationTask()).andReturn(operationTask);
		replay(coreAdministrationProvider);
		expect(coreExecutionInfoProvider.get(result)).andReturn(executionInfo).times(2);
		replay(coreExecutionInfoProvider);
		expect(moduleRepository.resolveModule(randomName, randomEnvironment)).andReturn(moduleInfo);
		replay(moduleRepository);
		operationTask.execute(isA(ExecutionInfo.class));
		expectLastCall().andAnswer(new SetChildResultStateAnswer(SUCCESS));
		replay(operationTask);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		// test
		assertTrue(result.isSuccess());
		assertEquals(1, result.getChildren().length);
		ExecutionResult compositeResult = result.getChildren()[0]; // first
		// child
		assertNotNull(compositeResult);
		assertTrue(compositeResult.isSuccess());

		verify(coreAdministrationProvider);
		verify(coreExecutionInfoProvider);
		verify(session);
		verify(moduleRepository);
		verify(executionInfo);
		verify(moduleInfo);
		verify(operationTask);
	}

	/**
	 * Test that the operation can execute with a single composite. Composite
	 * completes with error which is propagated to the root result.
	 */
	@Test
	public void testCanExecuteWithSingleErroneousComposite() throws Exception {
		Object content = contentMother.createCompositeExecutionWithSingleComposite(randomName);

		ExecutionInfo executionInfo = initializeExecutionInfoMock();
		ModuleInfo moduleInfo = initializeModuleInfoMock();

		replay(session);
		expect(coreAdministrationProvider.getModuleRepository()).andReturn(moduleRepository);
		expect(coreAdministrationProvider.getOperationTask()).andReturn(operationTask);
		replay(coreAdministrationProvider);
		expect(coreExecutionInfoProvider.get(result)).andReturn(executionInfo).times(2);
		replay(coreExecutionInfoProvider);
		expect(moduleRepository.resolveModule(randomName, randomEnvironment)).andReturn(moduleInfo);
		replay(moduleRepository);
		operationTask.execute(isA(ExecutionInfo.class));
		expectLastCall().andAnswer(new SetChildResultStateAnswer(ERROR));
		replay(operationTask);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertTrue(result.isError());
		assertEquals(1, result.getChildren().length);
		ExecutionResult compositeResult = result.getChildren()[0]; // first
		// child
		assertNotNull(compositeResult);
		assertTrue(compositeResult.isError());

		verify(coreAdministrationProvider);
		verify(coreExecutionInfoProvider);
		verify(session);
		verify(moduleRepository);
		verify(executionInfo);
		verify(moduleInfo);
		verify(operationTask);
	}

	/**
	 * Test that the operation can execute with a single composite. Composite
	 * completes with failure which is propagated to the root result.
	 */
	@Test
	public void testCanExecuteWithSingleFailedComposite() throws Exception {
		Object content = contentMother.createCompositeExecutionWithSingleComposite(randomName);

		ExecutionInfo executionInfo = initializeExecutionInfoMock();
		ModuleInfo moduleInfo = initializeModuleInfoMock();

		replay(session);
		expect(coreAdministrationProvider.getModuleRepository()).andReturn(moduleRepository);
		expect(coreAdministrationProvider.getOperationTask()).andReturn(operationTask);
		replay(coreAdministrationProvider);
		expect(coreExecutionInfoProvider.get(result)).andReturn(executionInfo).times(2);
		replay(coreExecutionInfoProvider);
		expect(moduleRepository.resolveModule(randomName, randomEnvironment)).andReturn(moduleInfo);
		replay(moduleRepository);
		operationTask.execute(isA(ExecutionInfo.class));
		expectLastCall().andAnswer(new SetChildResultStateAnswer(FAILURE));
		replay(operationTask);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertTrue(result.getState() == FAILURE);
		assertEquals(1, result.getChildren().length);
		ExecutionResult compositeResult = result.getChildren()[0]; // first
		// child
		assertNotNull(compositeResult);
		assertTrue(compositeResult.getState() == FAILURE);

		verify(coreAdministrationProvider);
		verify(coreExecutionInfoProvider);
		verify(session);
		verify(moduleRepository);
		verify(executionInfo);
		verify(moduleInfo);
		verify(operationTask);
	}

	/**
	 * Test that the operation fails if composite result is still executing when
	 * operation task returns.
	 */
	@Test
	public void testFailsIfCompositeResultIsRunningWhenExecutionIsComplete() throws Exception {
		Object content = contentMother.createCompositeExecutionWithSingleComposite(randomName);

		ExecutionInfo executionInfo = initializeExecutionInfoMock();
		ModuleInfo moduleInfo = initializeModuleInfoMock();

		replay(session);
		expect(coreAdministrationProvider.getModuleRepository()).andReturn(moduleRepository);
		expect(coreAdministrationProvider.getOperationTask()).andReturn(operationTask);
		replay(coreAdministrationProvider);
		expect(coreExecutionInfoProvider.get(result)).andReturn(executionInfo).times(2);
		replay(coreExecutionInfoProvider);
		expect(moduleRepository.resolveModule(randomName, randomEnvironment)).andReturn(moduleInfo);
		replay(moduleRepository);
		operationTask.execute(isA(ExecutionInfo.class));
		// expectLastCall().andAnswer(new
		// SetChildResultStateAnswer(SUCCESS));
		replay(operationTask);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertTrue(result.isError());

		verify(coreAdministrationProvider);
		verify(coreExecutionInfoProvider);
		verify(session);
		verify(moduleRepository);
		verify(executionInfo);
		verify(moduleInfo);
		verify(operationTask);
	}

	/**
	 * Test that the operation can execute with a single composite with null name.
	 */
	@Test
	public void testCanExecuteWithSingleCompositeWithNullName() throws Exception {
		Object content = contentMother.createCompositeExecutionWithSingleComposite(null);

		ExecutionInfo executionInfo = initializeExecutionInfoMock();
		ModuleInfo moduleInfo = createMock(ModuleInfo.class);
		replay(moduleInfo);

		replay(session);
		replay(coreAdministrationProvider);
		expect(coreExecutionInfoProvider.get(result)).andReturn(executionInfo).times(2);
		replay(coreExecutionInfoProvider);
		replay(moduleRepository);
		replay(operationTask);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertTrue(result.isError());
		assertEquals(1, result.getChildren().length);
		ExecutionResult compositeResult = result.getChildren()[0]; // first
		// child
		assertNotNull(compositeResult);
		assertTrue(compositeResult.isError());

		verify(coreAdministrationProvider);
		verify(coreExecutionInfoProvider);
		verify(session);
		verify(moduleRepository);
		verify(executionInfo);
		verify(moduleInfo);
		verify(operationTask);
	}

	/**
	 * Test that the operation fails to execute with a single composite with an
	 * empty name. Composite completes with failure which is propagated to the root
	 * result.
	 */
	@Test
	public void testFailsToExecuteWithSingleCompositeWithEmptyName() throws Exception {
		Object content = contentMother.createCompositeExecutionWithSingleComposite("");

		ExecutionInfo executionInfo = initializeExecutionInfoMock();
		ModuleInfo moduleInfo = createMock(ModuleInfo.class);
		replay(moduleInfo);

		replay(session);
		replay(coreAdministrationProvider);
		expect(coreExecutionInfoProvider.get(result)).andReturn(executionInfo).times(2);
		replay(coreExecutionInfoProvider);
		replay(moduleRepository);
		replay(operationTask);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertTrue(result.isError());
		assertEquals(1, result.getChildren().length);
		ExecutionResult compositeResult = result.getChildren()[0]; // first
		// child
		assertNotNull(compositeResult);
		assertTrue(compositeResult.isError());

		verify(coreAdministrationProvider);
		verify(coreExecutionInfoProvider);
		verify(session);
		verify(moduleRepository);
		verify(executionInfo);
		verify(moduleInfo);
		verify(operationTask);
	}

	/**
	 * Test that the operation can execute with a two composites. Composites
	 * completes with success which is propagated to the root result.
	 */
	@Test
	public void testCanExecuteWithTwoSuccessfulComposites() throws Exception {
		Object content = contentMother.createCompositeExecutionWithTwoComposites(randomName, randomName2);

		ExecutionInfo executionInfo = initializeExecutionInfoMock();
		ModuleInfo moduleInfo = initializeModuleInfoMock();
		ModuleInfo moduleInfo2 = initializeModuleInfoMock(randomName2);

		replay(session);
		expect(coreAdministrationProvider.getModuleRepository()).andReturn(moduleRepository).times(2);
		expect(coreAdministrationProvider.getOperationTask()).andReturn(operationTask).times(2);
		replay(coreAdministrationProvider);
		expect(coreExecutionInfoProvider.get(result)).andReturn(executionInfo).anyTimes();
		replay(coreExecutionInfoProvider);
		expect(moduleRepository.resolveModule(randomName, randomEnvironment)).andReturn(moduleInfo);
		expect(moduleRepository.resolveModule(randomName2, randomEnvironment)).andReturn(moduleInfo2);
		replay(moduleRepository);
		operationTask.execute(isA(ExecutionInfo.class));
		expectLastCall().andAnswer(new SetChildResultStateAnswer(SUCCESS));
		operationTask.execute(isA(ExecutionInfo.class));
		expectLastCall().andAnswer(new SetChildResultStateAnswer(SUCCESS));
		replay(operationTask);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		assertEquals(2, result.getChildren().length);
		ExecutionResult compositeResult = result.getChildren()[0]; // first
		// child
		assertNotNull(compositeResult);
		assertTrue(compositeResult.isSuccess());
		ExecutionResult compositeResult2 = result.getChildren()[1]; // second
		// child
		assertNotNull(compositeResult2);
		assertTrue(compositeResult2.isSuccess());

		verify(coreAdministrationProvider);
		verify(coreExecutionInfoProvider);
		verify(session);
		verify(moduleRepository);
		verify(executionInfo);
		verify(moduleInfo);
		verify(moduleInfo2);
		verify(operationTask);
	}

	/**
	 * Test that the operation can execute with a two composites. Composite one
	 * completes with error which is propagated to the root result.
	 */
	@Test
	public void testCanExecuteWithTwoErroneousComposites() throws Exception {
		Object content = contentMother.createCompositeExecutionWithTwoComposites(randomName, randomName2);

		ExecutionInfo executionInfo = initializeExecutionInfoMock();
		ModuleInfo moduleInfo = initializeModuleInfoMock();
		ModuleInfo moduleInfo2 = initializeModuleInfoMock(randomName2);

		replay(session);
		expect(coreAdministrationProvider.getModuleRepository()).andReturn(moduleRepository).times(2);
		expect(coreAdministrationProvider.getOperationTask()).andReturn(operationTask).times(2);
		replay(coreAdministrationProvider);
		expect(coreExecutionInfoProvider.get(result)).andReturn(executionInfo).anyTimes();
		replay(coreExecutionInfoProvider);
		expect(moduleRepository.resolveModule(randomName, randomEnvironment)).andReturn(moduleInfo);
		expect(moduleRepository.resolveModule(randomName2, randomEnvironment)).andReturn(moduleInfo2);
		replay(moduleRepository);
		operationTask.execute(isA(ExecutionInfo.class));
		expectLastCall().andAnswer(new SetChildResultStateAnswer(ERROR));
		operationTask.execute(isA(ExecutionInfo.class));
		expectLastCall().andAnswer(new SetChildResultStateAnswer(SUCCESS));
		replay(operationTask);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertTrue(result.isError());
		assertEquals(2, result.getChildren().length);
		ExecutionResult compositeResult = result.getChildren()[0]; // first
		// child
		assertNotNull(compositeResult);
		assertTrue(compositeResult.isError());
		ExecutionResult compositeResult2 = result.getChildren()[1]; // second
		// child
		assertNotNull(compositeResult2);
		assertTrue(compositeResult2.isSuccess());

		verify(coreAdministrationProvider);
		verify(coreExecutionInfoProvider);
		verify(session);
		verify(moduleRepository);
		verify(executionInfo);
		verify(moduleInfo);
		verify(moduleInfo2);
		verify(operationTask);
	}

	/**
	 * Test that the operation can execute with a two composites. Composite one
	 * completes with error which is propagated to the root result.
	 */
	@Test
	public void testCanExecuteWithTwoErroneousCompositesWithContinuationDisabled() throws Exception {
		Object content = contentMother.createCompositeExecutionWithTwoComposites(randomName, randomName2);

		ExecutionInfo executionInfo = initializeExecutionInfoMock();
		ModuleInfo moduleInfo = initializeModuleInfoMock();
		ModuleInfo moduleInfo2 = initializeModuleInfoMock(randomName2);

		replay(session);
		expect(coreAdministrationProvider.getModuleRepository()).andReturn(moduleRepository);
		expect(coreAdministrationProvider.getOperationTask()).andReturn(operationTask);
		replay(coreAdministrationProvider);
		expect(coreExecutionInfoProvider.get(result)).andReturn(executionInfo).anyTimes();
		replay(coreExecutionInfoProvider);
		expect(moduleRepository.resolveModule(randomName, randomEnvironment)).andReturn(moduleInfo);
		replay(moduleRepository);
		operationTask.execute(isA(ExecutionInfo.class));
		expectLastCall().andAnswer(new SetChildResultStateAnswer(ERROR));
		replay(operationTask);

		// disable continuation
		result.getContinuationPolicy().disableContinueOnFailure();

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertTrue(result.isError());
		assertEquals(1, result.getChildren().length);
		ExecutionResult compositeResult = result.getChildren()[0]; // first
		// child
		assertNotNull(compositeResult);
		assertTrue(compositeResult.isError());

		verify(coreAdministrationProvider);
		verify(coreExecutionInfoProvider);
		verify(session);
		verify(moduleRepository);
		verify(executionInfo);
		verify(moduleInfo);
		verify(moduleInfo2);
		verify(operationTask);
	}

}
