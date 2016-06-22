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

import static com.alpha.pineapple.CoreConstants.MSG_ENVIRONMENT;
import static com.alpha.pineapple.CoreConstants.MSG_MODULE;
import static com.alpha.pineapple.CoreConstants.MSG_MODULE_FILE;
import static com.alpha.pineapple.CoreConstants.MSG_OPERATION;
import static com.alpha.pineapple.CoreConstants.TRIGGER_WILDCARD_OPERATION;
import static com.alpha.pineapple.CoreConstants.TRIGGER_WILDCARD_RESULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoImpl;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.continuation.ContinuationPolicy;
import com.alpha.pineapple.execution.continuation.DefaultContinuationPolicyImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.module.Module;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleInfoImpl;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.activation.PluginActivator;
import com.alpha.pineapple.resource.EnvironmentNotFoundException;
import com.alpha.pineapple.resource.ResourceNotFoundException;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;
import com.alpha.testutils.testplugins.exception.channeled.PluginImpl;
import com.alpha.testutils.testplugins.exception.channeled.ThrowsNullPointerExceptionOperationImpl;
import com.alpha.testutils.testplugins.exception.channeled.ThrowsPluginExecutionFailedExceptionOperationImpl;
import com.alpha.testutils.testplugins.exception.channeled.ThrowsSessionConnectExceptionOperationImpl;
import com.alpha.testutils.testplugins.exception.channeled.ThrowsSessionDisconnectExceptionOperationImpl;
import com.alpha.testutils.testplugins.nooperations.NoOperationsPluginImpl;
import com.alpha.testutils.testplugins.wildcard.operation.WildcardOperationTestPluginImpl;

/**
 * Integration test of the class {@link InvokePluginsCommand}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class InvokePluginsCommandIntegrationTest {

    static final String[] NO_ENVIRONENTS = {};

    /**
     * Defines whether module descriptor is defined.
     */
    static final boolean DESCRIPTOR_IS_DEFINED = true;

    /**
     * Object under test.
     */
    @Resource
    Command invokePluginsCommand;

    /**
     * Initialize plugin activator command.
     */
    @Resource
    Command initializePluginActivatorCommand;

    /**
     * Plugin activator.
     */
    PluginActivator pluginActivator;

    /**
     * Credential provider.
     */
    @Resource
    CredentialProvider credentialProvider;

    /**
     * Execution result.
     */
    ExecutionResult executionResult;

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

    @Before
    public void setUp() throws Exception {
	randomTargetResource = RandomStringUtils.randomAlphabetic(10);
	randomEnvironment = RandomStringUtils.randomAlphabetic(10);
	randomOperation = RandomStringUtils.randomAlphabetic(10);
	randomDirectory = new File(RandomStringUtils.randomAlphabetic(10));

	// create context
	context = new ContextBase();

	// create execution result
	executionResult = new ExecutionResultImpl("Root result");

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
     * Initialize plugin activator.
     * 
     * @param resourcesConfiguration
     *            resources configuration.
     * 
     * @throws Exception
     *             if test fails.
     */
    @SuppressWarnings("unchecked")
    void initializePluginActivator(Configuration resourcesConfiguration) throws Exception {
	context.put(InitializePluginActivatorCommand.CREDENTIAL_PROVIDER_KEY, credentialProvider);
	context.put(InitializePluginActivatorCommand.RESOURCES_KEY, resourcesConfiguration);
	context.put(InitializePluginActivatorCommand.EXECUTIONRESULT_KEY,
		new ExecutionResultImpl("Initialize plugin activator"));
	initializePluginActivatorCommand.execute(context);
	pluginActivator = (PluginActivator) context.get(InitializePluginActivatorCommand.PLUGIN_ACTIVATOR_KEY);
    }

    /**
     * Execute command under test.
     * 
     * @param model
     *            module model.
     * @param module
     *            module module.
     * @param executionInfo
     *            execution info.
     * 
     * @throws Exception
     *             if test fails
     */
    @SuppressWarnings("unchecked")
    void executeCommand(Models model, Module module, ExecutionInfo executionInfo) throws Exception {

	// set parameters
	context.put(InvokePluginsCommand.EXECUTION_INFO_KEY, executionInfo);
	context.put(InvokePluginsCommand.MODULE_KEY, module);
	context.put(InvokePluginsCommand.MODULE_MODEL_KEY, model);

	// execute command
	invokePluginsCommand.execute(context);
    }

    /**
     * Test that command can be looked up from context.
     */
    @Test
    public void testCanCreateInstance() {
	assertNotNull(invokePluginsCommand);
    }

    /**
     * Test that command can successfully execute with empty input.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testExecuteWithEmptyInput() throws Exception {

	// create arguments
	Models model = moduleMother.createModelObject();
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(executionResult.isSuccess());
    }

    /**
     * Test that command can successfully execute with empty input and that
     * expected messages are added to the result.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testExecutionContainsExpectedResultMessages() throws Exception {

	// create arguments
	Models model = moduleMother.createModelObject();
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(executionResult.getMessages().containsKey(MSG_OPERATION));
	assertTrue(executionResult.getMessages().containsKey(MSG_ENVIRONMENT));
	assertTrue(executionResult.getMessages().containsKey(MSG_MODULE));
	assertTrue(executionResult.getMessages().containsKey(MSG_MODULE_FILE));
    }

    /**
     * Test that command can successfully execute with single model with an
     * empty target resource value (i.e. empty string).
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testExecuteWithSingleModelWithEmptyTargetResource() throws Exception {

	// create arguments
	boolean isDescriptorDefined = true;
	Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute("");
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, isDescriptorDefined,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(executionResult.isSuccess());
    }

    /**
     * Test that command fails to execute with single model with an unknown
     * environment.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test(expected = EnvironmentNotFoundException.class)
    public void testFailsSingleModelWithUnknownEnvironment() throws Exception {
	Configuration resourcesConfiguration = envConfigurationMother.createEmptyEnvironmentConfiguration();
	initializePluginActivator(resourcesConfiguration);

	// create arguments
	Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);
    }

    /**
     * Test that command fails to execute with single model with an unknown
     * target resource value.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test(expected = ResourceNotFoundException.class)
    public void testFailsSingleModelWithUnknownTargetResource() throws Exception {
	Configuration resourcesConfiguration = envConfigurationMother
		.createEnvConfigWithSingleEnvironment(randomEnvironment);
	initializePluginActivator(resourcesConfiguration);

	// create arguments
	boolean isDescriptorDefined = true;
	Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, isDescriptorDefined,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);
    }

    /**
     * Test that command fails to execute with single model with an target
     * resource value on a plugin with no operations defined.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testFailsSingleModelOnPluginWithNoOperations() throws Exception {
	Configuration resourcesConfiguration = envConfigurationMother.createEnvConfigWithSingleResource(
		randomEnvironment, randomTargetResource, NoOperationsPluginImpl.PLUGIN_ID);
	initializePluginActivator(resourcesConfiguration);

	// create arguments
	Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(executionResult.isError());
    }

    /**
     * Test that command successfully can execute with single model with known
     * target resource value.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testExecuteSingleModelWithKnownTargetResource() throws Exception {
	Configuration resourcesConfiguration = envConfigurationMother.createEnvConfigWithSingleResource(
		randomEnvironment, randomTargetResource, WildcardOperationTestPluginImpl.PLUGIN_ID);
	initializePluginActivator(resourcesConfiguration);

	// create arguments
	Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(executionResult.isSuccess());
    }

    /**
     * Test that the continue-on-failure policy is activated by default if it
     * isn't defined in the model.
     * 
     * This test case uses a mock policy object.
     * 
     * Model: The initial policy state is set to true (the default). Policy: The
     * initial policy state is set to true (the default).
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testSetContinueOnFailurePolicyIsActivatedByDefault() throws Exception {

	// mock policy is used to record continuation policy changes
	ContinuationPolicy policy = EasyMock.createMock(ContinuationPolicy.class);
	policy.enableContinueOnFailure();
	EasyMock.expect(policy.isContinueOnFailure()).andReturn(true);
	EasyMock.replay(policy);

	// mock result is used to record continuation policy
	executionResult = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(executionResult.getContinuationPolicy()).andReturn(policy).anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), EasyMock.isA(String.class));
	EasyMock.expectLastCall().anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), (String) EasyMock.isNull());
	executionResult.completeAsComputed(EasyMock.isA(MessageProvider.class), EasyMock.isA(String.class),
		(Object[]) EasyMock.isNull(), EasyMock.isA(String.class), (Object[]) EasyMock.isNull());
	EasyMock.replay(executionResult);

	// create arguments
	Models model = moduleMother.createModelObject();
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	EasyMock.verify(executionResult);
	EasyMock.verify(policy);
    }

    /**
     * Test that the continue-on-failure policy is activated by default if it
     * isn't defined in the model.
     * 
     * This test case uses a real policy object.
     * 
     * Model: The initial policy state is set to true (the default). Policy: The
     * initial policy state is set to true (the default).
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testSetContinueOnFailurePolicyIsActivatedByDefault2() throws Exception {

	// continuation policy
	ContinuationPolicy policy = DefaultContinuationPolicyImpl.getInstance();

	// mock result is used to record continuation policy
	executionResult = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(executionResult.getContinuationPolicy()).andReturn(policy).anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), EasyMock.isA(String.class));
	EasyMock.expectLastCall().anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), (String) EasyMock.isNull());
	executionResult.completeAsComputed(EasyMock.isA(MessageProvider.class), EasyMock.isA(String.class),
		(Object[]) EasyMock.isNull(), EasyMock.isA(String.class), (Object[]) EasyMock.isNull());
	EasyMock.replay(executionResult);

	// create arguments
	Models model = moduleMother.createModelObject();
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertTrue(policy.continueExecution());

	// test
	EasyMock.verify(executionResult);
    }

    /**
     * Test that the continue-on-failure policy is activated if it is activated
     * in the model.
     * 
     * This test case uses a mock policy object.
     * 
     * Model: The initial policy state is set to true (the default).
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testSetContinueOnFailurePolicyIsActivated() throws Exception {

	// mock policy is used to record continuation policy changes
	ContinuationPolicy policy = EasyMock.createMock(ContinuationPolicy.class);
	policy.enableContinueOnFailure();
	EasyMock.expect(policy.isContinueOnFailure()).andReturn(true);
	EasyMock.replay(policy);

	// mock result is used to record continuation policy
	executionResult = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(executionResult.getContinuationPolicy()).andReturn(policy).anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), EasyMock.isA(String.class));
	EasyMock.expectLastCall().anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), (String) EasyMock.isNull());
	executionResult.completeAsComputed(EasyMock.isA(MessageProvider.class), EasyMock.isA(String.class),
		(Object[]) EasyMock.isNull(), EasyMock.isA(String.class), (Object[]) EasyMock.isNull());
	EasyMock.replay(executionResult);

	// create arguments
	Models model = moduleMother.createModelObject();
	model.setContinue(true);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	EasyMock.verify(executionResult);
	EasyMock.verify(policy);
    }

    /**
     * Test that the continue-on-failure policy is activated if it is activated
     * in the model.
     * 
     * This test case uses a real policy object.
     * 
     * Model: The initial policy state is set to true (the default).
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testSetContinueOnFailurePolicyIsActivated2() throws Exception {

	// continuation policy
	ContinuationPolicy policy = DefaultContinuationPolicyImpl.getInstance();

	// mock result is used to record continuation policy
	executionResult = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(executionResult.getContinuationPolicy()).andReturn(policy).anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), EasyMock.isA(String.class));
	EasyMock.expectLastCall().anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), (String) EasyMock.isNull());
	executionResult.completeAsComputed(EasyMock.isA(MessageProvider.class), EasyMock.isA(String.class),
		(Object[]) EasyMock.isNull(), EasyMock.isA(String.class), (Object[]) EasyMock.isNull());
	EasyMock.replay(executionResult);

	// create arguments
	Models model = moduleMother.createModelObject();
	model.setContinue(true);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertTrue(policy.continueExecution());

	// test
	EasyMock.verify(executionResult);
    }

    /**
     * Test that the continue-on-failure policy is deactivated if it is
     * deactivated in the model.
     * 
     * Model: The initial policy state is set to false (not default).
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testSetContinueOnFailurePolicyIsDeactivated() throws Exception {

	// mock policy is used to record continuation policy changes
	ContinuationPolicy policy = EasyMock.createMock(ContinuationPolicy.class);
	policy.disableContinueOnFailure();
	EasyMock.expect(policy.isContinueOnFailure()).andReturn(false);
	EasyMock.replay(policy);

	// mock result is used to record continuation policy
	executionResult = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(executionResult.getContinuationPolicy()).andReturn(policy).anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), EasyMock.isA(String.class));
	EasyMock.expectLastCall().anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), (String) EasyMock.isNull());
	executionResult.completeAsComputed(EasyMock.isA(MessageProvider.class), EasyMock.isA(String.class),
		(Object[]) EasyMock.isNull(), EasyMock.isA(String.class), (Object[]) EasyMock.isNull());
	EasyMock.replay(executionResult);

	// create arguments
	Models model = moduleMother.createModelObject();
	model.setContinue(false);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	EasyMock.verify(executionResult);
	EasyMock.verify(policy);
    }

    /**
     * Test that the continue-on-failure policy is enforced if it is deactivated
     * in the model and an abort signal has been given to the policy.
     * 
     * Model: The initial policy state is set to false (not default).
     * 
     * The result of setting it true is done by measuring the lack of added
     * messages to the execution result regarding resolution of target resources
     * and operation.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testContinueOnFailurePolicyIsEnforced() throws Exception {
	Configuration resourcesConfiguration = envConfigurationMother.createEnvConfigWithSingleResource(
		randomEnvironment, randomTargetResource, WildcardOperationTestPluginImpl.PLUGIN_ID);
	initializePluginActivator(resourcesConfiguration);

	// mock policy is used to record continuation policy changes
	ContinuationPolicy policy = EasyMock.createMock(ContinuationPolicy.class);
	policy.disableContinueOnFailure();
	EasyMock.expect(policy.isContinueOnFailure()).andReturn(false);
	EasyMock.expect(policy.continueExecution()).andReturn(false); // signal
								      // dis-continue
	EasyMock.replay(policy);

	// mock result is used to record continuation policy
	executionResult = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(executionResult.getContinuationPolicy()).andReturn(policy).anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), EasyMock.isA(String.class));
	EasyMock.expectLastCall().anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), (String) EasyMock.isNull());
	executionResult.completeAsComputed(EasyMock.isA(MessageProvider.class), EasyMock.isA(String.class),
		(Object[]) EasyMock.isNull(), EasyMock.isA(String.class), (Object[]) EasyMock.isNull());
	EasyMock.replay(executionResult);

	// create arguments
	Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);
	model.setContinue(false);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	EasyMock.verify(executionResult);
	EasyMock.verify(policy);
    }

    /**
     * Test that the continue-on-failure policy is set to default in model. and
     * then the command is invoked again, e.g. through composite then the policy
     * continue-on-failure can't be updated by the composite model to be disable
     * the policy.
     * 
     * See PINEAPPLE-812: Verify a default policy can't be overwritten by a
     * composite
     * 
     * Model: The initial policy state is set to true (default). Model2
     * (simulates composite): The initial policy state is set to false (not
     * default).
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testCompositeCantDisableDefaultPolicy() throws Exception {
	Configuration resourcesConfiguration = envConfigurationMother.createEnvConfigWithSingleResource(
		randomEnvironment, randomTargetResource, WildcardOperationTestPluginImpl.PLUGIN_ID);
	initializePluginActivator(resourcesConfiguration);

	// continuation policy
	ContinuationPolicy policy = DefaultContinuationPolicyImpl.getInstance();

	// child mock result
	ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
	childResult.setState(ExecutionResult.ExecutionState.SUCCESS);
	EasyMock.expect(childResult.isExecuting()).andReturn(false);
	childResult.addMessage(EasyMock.isA(String.class), EasyMock.isA(String.class));	
	EasyMock.replay(childResult);

	// child mock result
	ExecutionResult childResult2 = EasyMock.createMock(ExecutionResult.class);
	childResult2.setState(ExecutionResult.ExecutionState.SUCCESS);
	EasyMock.expect(childResult2.isExecuting()).andReturn(false);
	childResult2.addMessage(EasyMock.isA(String.class), EasyMock.isA(String.class));		
	EasyMock.replay(childResult2);

	// mock result is used to record continuation policy
	executionResult = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(executionResult.getContinuationPolicy()).andReturn(policy).anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), EasyMock.isA(String.class));
	EasyMock.expectLastCall().anyTimes();
	executionResult.addMessage(EasyMock.isA(String.class), (String) EasyMock.isNull());
	EasyMock.expectLastCall().anyTimes();
	EasyMock.expect(executionResult.addChild(EasyMock.isA(String.class))).andReturn(childResult);
	executionResult.completeAsComputed(EasyMock.isA(MessageProvider.class), EasyMock.isA(String.class),
		(Object[]) EasyMock.isNull(), EasyMock.isA(String.class), (Object[]) EasyMock.isNull());
	EasyMock.replay(executionResult);

	// mock result is used to record continuation policy
	ExecutionResult executionResult2 = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(executionResult2.getContinuationPolicy()).andReturn(policy).anyTimes();
	executionResult2.addMessage(EasyMock.isA(String.class), EasyMock.isA(String.class));
	EasyMock.expectLastCall().anyTimes();
	executionResult2.addMessage(EasyMock.isA(String.class), (String) EasyMock.isNull());
	EasyMock.expectLastCall().anyTimes();
	EasyMock.expect(executionResult2.addChild(EasyMock.isA(String.class))).andReturn(childResult2);
	executionResult2.completeAsComputed(EasyMock.isA(MessageProvider.class), EasyMock.isA(String.class),
		(Object[]) EasyMock.isNull(), EasyMock.isA(String.class), (Object[]) EasyMock.isNull());
	EasyMock.replay(executionResult2);

	// create arguments
	Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);
	model.setContinue(true);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// create arguments
	Models model2 = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);
	model.setContinue(false);
	Module module2 = moduleMother.createModuleObject();
	ModuleInfo moduleInfo2 = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo2 = new ExecutionInfoImpl(moduleInfo2, randomEnvironment, randomOperation,
		executionResult2);

	executeCommand(model2, module2, executionInfo2);

	// test
	assertTrue(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertTrue(policy.continueExecution());

	// test
	EasyMock.verify(executionResult);
	EasyMock.verify(executionResult2);
	EasyMock.verify(childResult);
	EasyMock.verify(childResult2);
    }

    /**
     * Test that execution of a plugin which throws channeled
     * {@linkplain NullPointerException} is handled by termination of child
     * result execution the model with an error and propagating the error to the
     * root result.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testHandlesPluginsThrowsNullPointerException() throws Exception {
	String operation = ThrowsNullPointerExceptionOperationImpl.OPERATION_ID;

	Configuration resourcesConfiguration = envConfigurationMother
		.createEnvConfigWithSingleResource(randomEnvironment, randomTargetResource, PluginImpl.PLUGIN_ID);
	initializePluginActivator(resourcesConfiguration);

	// create result with default continuation policy
	executionResult = new ExecutionResultImpl("root");

	// create arguments
	Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);
	model.setContinue(false);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, operation, executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(executionResult.isError());
	assertEquals(1, executionResult.getChildren().length);
	ExecutionResult childResult = executionResult.getFirstChild();
	assertTrue(childResult.isError());
	String stackTrace = childResult.getMessages().get(ExecutionResult.MSG_STACKTRACE);
	assertTrue(stackTrace.contains(NullPointerException.class.getName()));
	assertTrue(childResult.getMessages().get(ExecutionResult.MSG_ERROR_MESSAGE)
		.contains("Execution failed with the error:")); // ipc.execute_model_failed
    }

    /**
     * Test that execution of a plugin which throws channeled
     * {@linkplain PluginExecutionFailedException} is handled by termination of
     * child result execution the model with an error and propagating the error
     * to the root result.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testHandlesPluginsThrowsPluginExecutionFailedException() throws Exception {
	String operation = ThrowsPluginExecutionFailedExceptionOperationImpl.OPERATION_ID;

	Configuration resourcesConfiguration = envConfigurationMother
		.createEnvConfigWithSingleResource(randomEnvironment, randomTargetResource, PluginImpl.PLUGIN_ID);
	initializePluginActivator(resourcesConfiguration);

	// create result with default continuation policy
	executionResult = new ExecutionResultImpl("root");

	// create arguments
	Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);
	model.setContinue(false);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, operation, executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(executionResult.isError());
	assertEquals(1, executionResult.getChildren().length);
	ExecutionResult childResult = executionResult.getChildren()[0]; // get
									// first
									// child
	assertTrue(childResult.isError());
	String stackTrace = childResult.getMessages().get(ExecutionResult.MSG_STACKTRACE);
	assertTrue(stackTrace.contains(PluginExecutionFailedException.class.getName()));
	assertTrue(childResult.getMessages().get(ExecutionResult.MSG_ERROR_MESSAGE)
		.contains("Plugin execution failed with the error:")); // ipc.execute_plugin_failed

    }

    /**
     * Test that execution of a plugin which throws channeled
     * {@linkplain SessionConnectException} is handled by termination of child
     * result execution the model with an error and propagating the error to the
     * root result.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testHandlesPluginsThrowsSessionConnectExecution() throws Exception {
	String operation = ThrowsSessionConnectExceptionOperationImpl.OPERATION_ID;

	Configuration resourcesConfiguration = envConfigurationMother
		.createEnvConfigWithSingleResource(randomEnvironment, randomTargetResource, PluginImpl.PLUGIN_ID);
	initializePluginActivator(resourcesConfiguration);

	// create result with default continuation policy
	executionResult = new ExecutionResultImpl("root");

	// create arguments
	Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);
	model.setContinue(false);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, operation, executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(executionResult.isError());
	assertEquals(1, executionResult.getChildren().length);
	ExecutionResult childResult = executionResult.getChildren()[0]; // get
									// first
									// child
	assertTrue(childResult.isError());
	String stackTrace = childResult.getMessages().get(ExecutionResult.MSG_STACKTRACE);
	assertTrue(stackTrace.contains(SessionConnectException.class.getName()));
	assertTrue(childResult.getMessages().get(ExecutionResult.MSG_ERROR_MESSAGE)
		.contains("Establishing session to resource failed with the error:")); // ipc.session_connect_failed
    }

    /**
     * Test that execution of a plugin which throws channeled
     * {@linkplain SessionDisconnectException} is handled by termination of
     * child result execution the model with an error and propagating the error
     * to the root result.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testHandlesPluginsThrowsSessionDisconnectExecution() throws Exception {
	String operation = ThrowsSessionDisconnectExceptionOperationImpl.OPERATION_ID;

	Configuration resourcesConfiguration = envConfigurationMother
		.createEnvConfigWithSingleResource(randomEnvironment, randomTargetResource, PluginImpl.PLUGIN_ID);
	initializePluginActivator(resourcesConfiguration);

	// create result with default continuation policy
	executionResult = new ExecutionResultImpl("root");

	// create arguments
	Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);
	model.setContinue(false);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, operation, executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(executionResult.isError());
	assertEquals(1, executionResult.getChildren().length);
	ExecutionResult childResult = executionResult.getChildren()[0]; // get
									// first
									// child
	assertTrue(childResult.isError());
	String stackTrace = childResult.getMessages().get(ExecutionResult.MSG_STACKTRACE);
	assertTrue(stackTrace.contains(SessionDisconnectException.class.getName()));
	assertTrue(childResult.getMessages().get(ExecutionResult.MSG_ERROR_MESSAGE)
		.contains("Disconnecting session from resource failed with the error:")); // ipc.session_disconnect_failed
    }

    /**
     * Test that a model with no triggers can be executed successfully.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testExecuteModelWithNoTriggers() throws Exception {
	Configuration resourcesConfiguration = envConfigurationMother.createEnvConfigWithSingleResource(
		randomEnvironment, randomTargetResource, WildcardOperationTestPluginImpl.PLUGIN_ID);
	initializePluginActivator(resourcesConfiguration);

	// create arguments
	Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(executionResult.isSuccess());
	assertEquals(1+0, executionResult.getChildren().length);	
    }

    /**
     * Trigger execution fails because module is unknown. The failed trigger
     * execution makes the entire module execution fail. An unknown trigger is
     * used for convenience in the test.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testFailsIfTriggerModuleIsUnknown() throws Exception {
	Configuration resourcesConfiguration = envConfigurationMother.createEnvConfigWithSingleResource(
		randomEnvironment, randomTargetResource, WildcardOperationTestPluginImpl.PLUGIN_ID);
	initializePluginActivator(resourcesConfiguration);

	// create arguments
	String triggerOnResult = TRIGGER_WILDCARD_RESULT;
	String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
	Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
		triggerOnOperation);
	Module module = moduleMother.createModuleObject();
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
		randomDirectory);
	ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
		executionResult);

	executeCommand(model, module, executionInfo);

	// test
	assertTrue(executionResult.isError());
	assertEquals(1+1, executionResult.getChildren().length);	
    }

}
