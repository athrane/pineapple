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

package com.alpha.pineapple.command;

import static com.alpha.pineapple.CoreConstants.TRIGGER_WILDCARD_OPERATION;
import static com.alpha.pineapple.CoreConstants.TRIGGER_WILDCARD_RESULT;
import static com.alpha.pineapple.execution.ExecutionResult.ExecutionState.ERROR;
import static com.alpha.pineapple.execution.ExecutionResult.ExecutionState.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoImpl;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.model.module.model.AggregatedModel;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleInfoImpl;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of the class {@link InvokeTriggersCommand}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class InvokeTriggersCommandIntegrationTest {

	/**
	 * No environments defined for module.
	 */
	static final String[] NO_ENVIRONENTS = {};

	/**
	 * Defines whether module descriptor is defined.
	 */
	static final boolean DESCRIPTOR_IS_DEFINED = true;

	/**
	 * Object under test.
	 */
	@Resource
	Command invokeTriggersCommand;

	/**
	 * Execution result for command.
	 */
	ExecutionResult executionResult;

	/**
	 * Execution result for model.
	 */
	ExecutionResultImpl modelResult;

	/**
	 * Chain context.
	 */
	Context context;

	/**
	 * Object mother for module.
	 */
	ObjectMotherModule moduleMother;

	/**
	 * Object mother for environment configuration..
	 */
	ObjectMotherEnvironmentConfiguration envConfigurationMother;

	/**
	 * Random environment string.
	 */
	String randomEnvironment;

	/**
	 * Random operation.
	 */
	String randomOperation;

	/**
	 * Random ID.
	 */
	String randomId;

	/**
	 * Random directory.
	 */
	File randomDirectory;

	/**
	 * Random target resource.
	 */
	String randomTargetResource;

	private ModuleInfo moduleInfo;

	private ExecutionInfo executionInfo;

	@Before
	public void setUp() throws Exception {
		randomId = RandomStringUtils.randomAlphabetic(10);
		randomTargetResource = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		randomOperation = RandomStringUtils.randomAlphabetic(10);
		randomDirectory = new File(RandomStringUtils.randomAlphabetic(10));

		// create context
		context = new ContextBase();

		// create execution result
		executionResult = new ExecutionResultImpl("command result");

		// create model result
		modelResult = new ExecutionResultImpl("model result");

		// create module info
		moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED, randomDirectory);

		// create execution info
		executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation, executionResult);

		// create module object mother
		moduleMother = new ObjectMotherModule();

		// create environment configuration mother
		envConfigurationMother = new ObjectMotherEnvironmentConfiguration();
	}

	@After
	public void tearDown() throws Exception {
		executionResult = null;
		context = null;
		moduleMother = null;
	}

	/**
	 * Execute command under test.
	 * 
	 * @param model
	 *            aggregated model containing the triggers.
	 * @param modelResult
	 *            model result.
	 * @param executionInfo
	 *            execution info.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@SuppressWarnings("unchecked")
	void executeCommand(AggregatedModel model, ExecutionResult modelResult, ExecutionInfo executionInfo)
			throws Exception {

		// set parameters
		context.put(InvokeTriggersCommand.AGGREGATED_MODEL_KEY, model);
		context.put(InvokeTriggersCommand.MODEL_RESULT_KEY, modelResult);
		context.put(InvokeTriggersCommand.EXECUTION_INFO_KEY, executionInfo);
		context.put(InvokeTriggersCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		invokeTriggersCommand.execute(context);
	}

	/**
	 * Test that command can be looked up from context.
	 */
	@Test
	public void testCanCreateInstance() {
		assertNotNull(invokeTriggersCommand);
	}

	/**
	 * Can successfully execute command with empty input, i.e. an aggregated model
	 * without any triggers.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testSuccessfullyExecuteModelWithNoTriggers() throws Exception {

		// create model
		Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isSuccess());
	}

	/**
	 * Execution of trigger with unknown module defined in trigger.
	 * 
	 * Execution fails due to the module not being found.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testExecutionOfTriggerWithUnknownModuleFails() throws Exception {

		// create model
		String triggerOnResult = TRIGGER_WILDCARD_RESULT;
		String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isError());
	}

	/**
	 * Child result is created for trigger.
	 * 
	 * Execution fails due to the module not being found.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testExecutionCreatesChildResult() throws Exception {

		// create model
		String triggerOnResult = TRIGGER_WILDCARD_RESULT;
		String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isError());
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult triggerResult = executionResult.getFirstChild();
		assertTrue(triggerResult.isError());

	}

	/**
	 * Can execute named trigger.
	 * 
	 * Trigger is resolved for execution due to the wild card value of the
	 * result-directive and wild card value for the operation directive
	 * 
	 * Trigger execution fails because the module in the trigger definition is
	 * unknown. The unknown module is used for convenience in the test.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanExecutedNamedTrigger() throws Exception {

		// create model
		String triggerOnResult = TRIGGER_WILDCARD_RESULT;
		String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isError());
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult triggerResult = executionResult.getFirstChild();
		assertTrue(triggerResult.isError());

	}

	/**
	 * Can execute unnamed trigger.
	 * 
	 * Trigger is resolved for execution due to the wild card value of the
	 * result-directive and wild card value for the operation directive
	 * 
	 * Trigger execution fails because the module in the trigger definition is
	 * unknown. The unknown module is used for convenience in the test.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanExecutedUnnamedTrigger() throws Exception {

		// create model
		String triggerOnResult = TRIGGER_WILDCARD_RESULT;
		String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// set name to null
		model.getModel().iterator().next().getTrigger().iterator().next().setName(null);

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isError());
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult triggerResult = executionResult.getFirstChild();
		assertTrue(triggerResult.isError());

	}

	/**
	 * Trigger is resolved for execution due to the wild card value of the
	 * result-directive and wild card value for the operation directive
	 * 
	 * Trigger is named.
	 * 
	 * Trigger execution fails because the module in the trigger definition is
	 * unknown. The unknown module is used for convenience in the test.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testIsExecutedWithWildcardResultDirectiveAndWildcardoperationDirective() throws Exception {

		// create model
		String triggerOnResult = TRIGGER_WILDCARD_RESULT;
		String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isError());
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult triggerResult = executionResult.getFirstChild();
		assertTrue(triggerResult.isError());

	}

	/**
	 * Trigger is resolved for execution due to matching of the result directive.
	 * The directive matches the successful outcome of the model execution with
	 * itself. The directive expects a success to trigger.
	 * 
	 * The value for the operation directive is the wild card which matches
	 * anything.
	 * 
	 * Trigger execution fails because the module in the trigger definition is
	 * unknown. The unknown module is used for convenience in the test.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testIsExecutedWithMatchingResultDirective() throws Exception {

		// set model result
		modelResult.setState(SUCCESS);

		// create model
		String triggerOnResult = modelResult.getState().toString();
		String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isError());
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult triggerResult = executionResult.getFirstChild();
		assertTrue(triggerResult.isError());

	}

	/**
	 * Trigger is resolved for execution due to matching of the result directive.
	 * The directive matches the successful outcome of the model execution with
	 * itself. The directive expects a success to trigger.
	 * 
	 * The result directive is specified using list notation.
	 * 
	 * The value for the operation directive is the wild card which matches
	 * anything.
	 * 
	 * Trigger execution fails because the module in the trigger definition is
	 * unknown. The unknown module is used for convenience in the test.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testIsExecutedWithMatchingResultDirective_UsingList() throws Exception {

		// set model result
		modelResult.setState(SUCCESS);

		// create model
		String triggerOnResult = "{" + modelResult.getState().toString() + "}";
		String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isError());
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult triggerResult = executionResult.getFirstChild();
		assertTrue(triggerResult.isError());
	}

	/**
	 * Trigger is resolved for execution due to matching of the result directive.
	 * The directive matches the successful outcome of the model execution with
	 * itself. The directive expects a success to trigger.
	 * 
	 * The result directive is specified using list notation with multiple values.
	 * 
	 * The value for the operation directive is the wild card which matches
	 * anything.
	 * 
	 * Trigger execution fails because the module in the trigger definition is
	 * unknown. The unknown module is used for convenience in the test.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testIsExecutedWithMatchingResultDirective_UsingListWithMultipeValues() throws Exception {

		// set model result
		modelResult.setState(SUCCESS);

		// create model
		String triggerOnResult = "{" + modelResult.getState().toString() + "," + ERROR.toString() + "}";
		String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isError());
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult triggerResult = executionResult.getFirstChild();
		assertTrue(triggerResult.isError());
	}

	/**
	 * Trigger isn't resolved for execution due to matching of the result directive.
	 * The directive matches the successful outcome of the model execution with
	 * itself. The directive expects a success to trigger.
	 * 
	 * The value for the operation directive is the wild card which matches
	 * anything.
	 * 
	 * Trigger execution fails because the module in the trigger definition is
	 * unknown. The unknown module is used for convenience in the test.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testIsntExecutedWithNonMatchingResultDirective() throws Exception {

		// set model result
		modelResult.setState(SUCCESS);

		// create model
		String triggerOnResult = ERROR.toString();
		String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isSuccess());
		assertEquals(0, executionResult.getChildren().length);
	}

	/**
	 * Trigger isn't resolved for execution due to matching of the result directive.
	 * The directive matches the successful outcome of the model execution with
	 * itself. The directive expects a success to trigger.
	 * 
	 * The result directive is specified using list notation with multiple values.
	 * None of the values in the list matches the actual outcome.
	 * 
	 * The value for the operation directive is the wild card which matches
	 * anything.
	 * 
	 * Trigger execution fails because the module in the trigger definition is
	 * unknown. The unknown module is used for convenience in the test.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testIsntExecutedWithNonMatchingResultDirective_UsingListWithMultipeValues() throws Exception {

		// set model result
		modelResult.setState(SUCCESS);

		// create model
		String triggerOnResult = "{" + FAILURE.toString() + "," + ERROR.toString() + "}";
		String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isSuccess());
		assertEquals(0, executionResult.getChildren().length);
	}

	/**
	 * Trigger is resolved for execution due to matching of the operation directive.
	 * The directive matches the invoked module operation with the with itself. The
	 * directive expects a success to trigger.
	 * 
	 * The value for the result directive is the wild card which matches anything.
	 * 
	 * Trigger execution fails because the module in the trigger definition is
	 * unknown. The unknown module is used for convenience in the test.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testIsExecutedWithMatchingOperationDirective() throws Exception {

		// create model
		String triggerOnResult = TRIGGER_WILDCARD_RESULT;
		String triggerOnOperation = randomOperation;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isError());
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult triggerResult = executionResult.getFirstChild();
		assertTrue(triggerResult.isError());
	}

	/**
	 * Trigger is resolved for execution due to matching of the operation directive.
	 * The directive matches the invoked module operation with the with itself. The
	 * directive expects a success to trigger.
	 * 
	 * The result directive is specified using list notation.
	 * 
	 * The value for the result directive is the wild card which matches anything.
	 * 
	 * Trigger execution fails because the module in the trigger definition is
	 * unknown. The unknown module is used for convenience in the test.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testIsExecutedWithMatchingOperationDirective_UsingList() throws Exception {

		// create model
		String triggerOnResult = TRIGGER_WILDCARD_RESULT;
		String triggerOnOperation = "{" + randomOperation + "}";
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isError());
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult triggerResult = executionResult.getFirstChild();
		assertTrue(triggerResult.isError());
	}

	/**
	 * Trigger is resolved for execution due to matching of the operation directive.
	 * The directive matches the invoked module operation with the with itself. The
	 * directive expects a success to trigger.
	 * 
	 * The result directive is specified using list notation with multiple values.
	 * 
	 * The value for the result directive is the wild card which matches anything.
	 * 
	 * Trigger execution fails because the module in the trigger definition is
	 * unknown. The unknown module is used for convenience in the test.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testIsExecutedWithMatchingOperationDirective_UsingListMultipleValues() throws Exception {

		// create model
		String triggerOnResult = TRIGGER_WILDCARD_RESULT;
		String triggerOnOperation = "{" + randomOperation + "," + RandomStringUtils.randomAlphabetic(10) + "}";
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isError());
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult triggerResult = executionResult.getFirstChild();
		assertTrue(triggerResult.isError());
	}

	/**
	 * Can execute multiple triggers.
	 * 
	 * Trigger is resolved for execution due to the wild card value of the
	 * result-directive and wild card value for the operation directive
	 * 
	 * Trigger execution fails because the module in the trigger definition is
	 * unknown. The unknown module is used for convenience in the test.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanExecutedMultipleTriggers() throws Exception {

		// create model
		String triggerOnResult = TRIGGER_WILDCARD_RESULT;
		String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();
		moduleMother.addTrigger(triggerOnResult, triggerOnOperation, aggregatedModel);

		// run command
		executeCommand(aggregatedModel, modelResult, executionInfo);

		// test
		assertTrue(executionResult.isError());
		assertEquals(2, executionResult.getChildren().length);
		ExecutionResult triggerResult = executionResult.getFirstChild();
		assertTrue(triggerResult.isError());
		ExecutionResult triggerResult2 = executionResult.getChildren()[1];
		assertTrue(triggerResult2.isError());
	}
}
