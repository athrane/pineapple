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

import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.plugin.activation.PluginActivator;
import com.alpha.pineapple.plugin.repository.PluginRuntimeRepository;
import com.alpha.pineapple.resource.ResourceRepository;
import com.alpha.testutils.ObjectMotherCredentialProvider;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Unit test of the class {@link InitializePluginActivatorCommand}.
 * 
 * Runs with spring context to get access to credential provider mother.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class InitializePluginActivatorCommandTest {
	/**
	 * Object under test.
	 */
	Command command;

	/**
	 * Object mother for credential provider
	 */
	@Resource
	ObjectMotherCredentialProvider providerMother;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * Mock execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Command context.
	 */
	Context context;

	/**
	 * Mock resource repository.
	 */
	ResourceRepository resourceRepository;

	/**
	 * Mock plugin repository.
	 */
	PluginRuntimeRepository pluginRepository;

	/**
	 * Mock plugin activator.
	 */
	PluginActivator pluginActivator;

	@Before
	public void setUp() throws Exception {
		context = new ContextBase();

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// create command
		command = new InitializePluginActivatorCommand();

		// create mock resource repository
		resourceRepository = EasyMock.createMock(ResourceRepository.class);

		// inject repository
		ReflectionTestUtils.setField(command, "resourceRepository", resourceRepository);

		// create mock plugin repository
		pluginRepository = EasyMock.createMock(PluginRuntimeRepository.class);

		// inject repository
		ReflectionTestUtils.setField(command, "pluginRepository", pluginRepository);

		// create mock plugin activator
		pluginActivator = EasyMock.createMock(PluginActivator.class);

		// inject repository
		ReflectionTestUtils.setField(command, "pluginActivator", pluginActivator);

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

		// create mock result
		executionResult = EasyMock.createMock(ExecutionResult.class);
	}

	@After
	public void tearDown() throws Exception {
		command = null;
		providerMother = null;
		context = null;
		resourceRepository = null;
		pluginRepository = null;
		pluginActivator = null;
	}

	/**
	 * Test that command fails if context is undefined.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedContext() throws Exception {
		// execute command
		command.execute(null);
	}

	/**
	 * Test that command fails if credential provider key is undefined.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testCommandFailsIfCredentialProviderKeyIsUndefinedInContext() throws Exception {
		// add environment configuration with resources to context
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();
		context.put(InitializePluginActivatorCommand.RESOURCES_KEY, configuration);

		// execute command
		command.execute(context);
	}

	/**
	 * Test that command fails if credential provider value is null.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testCommandFailsIfCredentialProviderValueIsUndefinedInContext() throws Exception {
		// add environment configuration with resources to context
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();
		context.put(InitializePluginActivatorCommand.RESOURCES_KEY, configuration);

		// add null credential provider
		context.put(InitializePluginActivatorCommand.CREDENTIAL_PROVIDER_KEY, null);

		// execute command
		command.execute(context);
	}

	/**
	 * Test that command fails if resources key is undefined (null).
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testCommandFailsIfResourcesKeyIsUndefinedInContext() throws Exception {
		// create credential provider
		CredentialProvider provider = providerMother.createProviderWithTestResourceCredential();
		context.put(InitializePluginActivatorCommand.CREDENTIAL_PROVIDER_KEY, provider);

		command.execute(context);
	}

	/**
	 * Test that command fails if resources value is undefined (null).
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testCommandFailsIfResourcesValueIsUndefinedInContext() throws Exception {
		// create credential provider
		CredentialProvider provider = providerMother.createProviderWithTestResourceCredential();
		context.put(InitializePluginActivatorCommand.CREDENTIAL_PROVIDER_KEY, provider);

		// add environment configuration with resources to context
		context.put(InitializePluginActivatorCommand.RESOURCES_KEY, null);

		command.execute(context);
	}

	/**
	 * Test that command fails if execution result key is undefined (null).
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testCommandFailsIfExecutionResultKeyIsUndefinedInContext() throws Exception {
		// create credential provider
		CredentialProvider provider = providerMother.createProviderWithTestResourceCredential();
		context.put(InitializePluginActivatorCommand.CREDENTIAL_PROVIDER_KEY, provider);

		// add environment configuration with resources to context
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();
		context.put(InitializePluginActivatorCommand.RESOURCES_KEY, configuration);

		command.execute(context);
	}

	/**
	 * Test that command fails if execution result value is undefined (null).
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testCommandFailsIfExecutionResultValueIsUndefinedInContext() throws Exception {
		// create credential provider
		CredentialProvider provider = providerMother.createProviderWithTestResourceCredential();
		context.put(InitializePluginActivatorCommand.CREDENTIAL_PROVIDER_KEY, provider);

		// add environment configuration with resources to context
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();
		context.put(InitializePluginActivatorCommand.RESOURCES_KEY, configuration);

		// add execution result
		context.put(InitializePluginActivatorCommand.EXECUTIONRESULT_KEY, null);

		command.execute(context);
	}

	/**
	 * Test that command can run and with null resources configuration, ie. a
	 * resource configuration with no defined environments and resources.
	 * 
	 * Success of the test is measured by the definition of the activator in the
	 * context after command execution.
	 */
	@Test
	public void testCanRunWithNullResourcesConfiguration() throws Exception {
		// create execution result mock
		executionResult.completeAsComputed(messageProvider, "ipac.succeed", null, "ipac.failed", null);
		EasyMock.replay(executionResult);

		// create plugin id array
		String[] pluginIds = {};

		// create environment configuration with resources to context
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create credential provider
		CredentialProvider provider = providerMother.createProviderWithTestResourceCredential();

		// complete initialization of mock resource repository
		resourceRepository.initialize(configuration);
		EasyMock.expect(resourceRepository.getPluginIds()).andReturn(pluginIds);
		EasyMock.replay(resourceRepository);

		// complete initialization of mock plugin repository
		pluginRepository.initialize(executionResult, pluginIds);
		EasyMock.replay(pluginRepository);

		// complete initialization of mock plugin activator
		pluginActivator.initialize(provider, resourceRepository, pluginRepository);
		EasyMock.replay(pluginActivator);

		// add command properties
		context.put(InitializePluginActivatorCommand.CREDENTIAL_PROVIDER_KEY, provider);
		context.put(InitializePluginActivatorCommand.RESOURCES_KEY, configuration);
		context.put(InitializePluginActivatorCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		Object expected = context.get(InitializePluginActivatorCommand.PLUGIN_ACTIVATOR_KEY);
		assertTrue(expected instanceof PluginActivator);

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(pluginActivator);
		EasyMock.verify(pluginRepository);
		EasyMock.verify(resourceRepository);
	}

	/**
	 * Test that command can run and with environment configuration, containing a
	 * single resource definition.
	 */
	@Test
	public void testCanRunWithResourcesConfigurationWithSingleResource() throws Exception {
		// create execution result mock
		executionResult.completeAsComputed(messageProvider, "ipac.succeed", null, "ipac.failed", null);
		EasyMock.replay(executionResult);

		// create plugin id array
		String[] pluginIds = {};

		// create environment configuration with resources to context
		Configuration configuration = envConfigMother.createEnvConfigWithSingleCredential();

		// create credential provider
		CredentialProvider provider = providerMother.createProviderWithTestResourceCredential();

		// complete initialization of mock resource repository
		resourceRepository.initialize(configuration);
		EasyMock.expect(resourceRepository.getPluginIds()).andReturn(pluginIds);
		EasyMock.replay(resourceRepository);

		// complete initialization of mock plugin repository
		pluginRepository.initialize(executionResult, pluginIds);
		EasyMock.replay(pluginRepository);

		// complete initialization of mock plugin activator
		pluginActivator.initialize(provider, resourceRepository, pluginRepository);
		EasyMock.replay(pluginActivator);

		// add command properties
		context.put(InitializePluginActivatorCommand.CREDENTIAL_PROVIDER_KEY, provider);
		context.put(InitializePluginActivatorCommand.RESOURCES_KEY, configuration);
		context.put(InitializePluginActivatorCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		Object expected = context.get(InitializePluginActivatorCommand.PLUGIN_ACTIVATOR_KEY);
		assertTrue(expected instanceof PluginActivator);

		// verify mocks
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that command can run and with environment configuration, containing a
	 * single resource definition.
	 * 
	 * The resource contains a single property.
	 */
	@Test
	public void testCanRunWithResourcesConfigurationWithSingleResource2() throws Exception {
		// create execution result mock
		executionResult.completeAsComputed(messageProvider, "ipac.succeed", null, "ipac.failed", null);
		EasyMock.replay(executionResult);

		// create plugin id array
		String[] pluginIds = {};

		// create environment configuration with resources to context
		Configuration configuration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();

		// create credential provider
		CredentialProvider provider = providerMother.createProviderWithTestResourceCredential();

		// complete initialization of mock resource repository
		resourceRepository.initialize(configuration);
		EasyMock.expect(resourceRepository.getPluginIds()).andReturn(pluginIds);
		EasyMock.replay(resourceRepository);

		// complete initialization of mock plugin repository
		pluginRepository.initialize(executionResult, pluginIds);
		EasyMock.replay(pluginRepository);

		// complete initialization of mock plugin activator
		pluginActivator.initialize(provider, resourceRepository, pluginRepository);
		EasyMock.replay(pluginActivator);

		// add command properties
		context.put(InitializePluginActivatorCommand.CREDENTIAL_PROVIDER_KEY, provider);
		context.put(InitializePluginActivatorCommand.RESOURCES_KEY, configuration);
		context.put(InitializePluginActivatorCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		Object expected = context.get(InitializePluginActivatorCommand.PLUGIN_ACTIVATOR_KEY);
		assertTrue(expected instanceof PluginActivator);

		// verify mocks
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that command can run and with environment configuration, containing a
	 * single resource definition.
	 * 
	 * The resource contains a multiple properties.
	 */
	@Test
	public void testCanRunWithResourcesConfigurationWithSingleResource3() throws Exception {
		// create execution result mock
		executionResult.completeAsComputed(messageProvider, "ipac.succeed", null, "ipac.failed", null);
		EasyMock.replay(executionResult);

		// create plugin id array
		String[] pluginIds = {};

		// create environment configuration with resources to context
		Configuration configuration = envConfigMother.createEnvConfigWithSingleTestResourceWithMultipleProperties();

		// create credential provider
		CredentialProvider provider = providerMother.createProviderWithTestResourceCredential();

		// complete initialization of mock resource repository
		resourceRepository.initialize(configuration);
		EasyMock.expect(resourceRepository.getPluginIds()).andReturn(pluginIds);
		EasyMock.replay(resourceRepository);

		// complete initialization of mock plugin repository
		pluginRepository.initialize(executionResult, pluginIds);
		EasyMock.replay(pluginRepository);

		// complete initialization of mock plugin activator
		pluginActivator.initialize(provider, resourceRepository, pluginRepository);
		EasyMock.replay(pluginActivator);

		// add command properties
		context.put(InitializePluginActivatorCommand.CREDENTIAL_PROVIDER_KEY, provider);
		context.put(InitializePluginActivatorCommand.RESOURCES_KEY, configuration);
		context.put(InitializePluginActivatorCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		Object expected = context.get(InitializePluginActivatorCommand.PLUGIN_ACTIVATOR_KEY);
		assertTrue(expected instanceof PluginActivator);

		// verify mocks
		EasyMock.verify(executionResult);
	}

}
