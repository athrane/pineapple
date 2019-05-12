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

import static com.alpha.pineapple.test.matchers.PineappleMatchers.doesFileExist;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Environment;
import com.alpha.pineapple.model.configuration.ObjectFactory;
import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.test.Asserter;
import com.alpha.pineapple.test.AssertionHelper;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which can create a default environment configuration for the core component
 * if none exists.
 * </p>
 * <p>
 * The command validates whether the resources configuration file and
 * credentials file exist. If they don't exist then two default configuration
 * files are created.
 * 
 * If a resources configuration exists then the creation of the default
 * configuration is skipped. If a credentials configuration exists then the
 * creation of the default configuration is skipped.
 * </p>
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in
 * the context:
 * 
 * <ul>
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the command succeeded. If the
 * command failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the command fails due to an exception then the exception isn't caught,
 * but passed on the the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * 
 * <li>If none of the configurations files existed then both of the
 * configuration files are created with some example configurations. Furthermore
 * a set of example modules are created.</li>
 * </ul>
 * </p>
 */
public class CreateDefaultEnvironmentConfigurationCommand implements Command {
	/**
	 * Index to second child result.
	 */
	static final int SECOND_CHILD_RESULT = 1;

	/**
	 * Index to third child result.
	 */
	static final int THIRD_CHILD_RESULT = 2;

	/**
	 * Environment description.
	 */
	static final String LOCAL_DESC = "Environment to support execution of modules on a local host.";

	/**
	 * Environment description.
	 */
	static final String WILDCARD_DESC = "Wildcard environment for definition of resources available in ALL evironments.";

	/**
	 * Environment description.
	 */
	static final String PINEAPPLE_CI_LINUX_DESC = "Environment to for the Pineapple continous integration setup.";

	/**
	 * Environment description.
	 */
	static final String VAGRANT_LINUX_DESC = "Environment to support execution of modules in a Vagrant multi-machine Linux environment.";

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * JAXB object factory.
	 */
	ObjectFactory factory = new ObjectFactory();

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Runtime directory resolver.
	 */
	@Resource
	RuntimeDirectoryProvider runtimeDirectoryProvider;

	/**
	 * Command facade.
	 */
	@Resource
	CommandFacade commandFacade;

	/**
	 * Hamcrest asserter.
	 */
	@Resource
	Asserter asserter;

	/**
	 * Assertion helper.
	 */
	@Resource
	AssertionHelper assertionHelper;

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	public boolean execute(Context context) throws Exception {
		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// defines directories
		File confDirectory = runtimeDirectoryProvider.getConfigurationDirectory();
		File resourcesFile = new File(confDirectory, CoreConstants.RESOURCE_FILE);
		File credentialsFile = new File(confDirectory, CoreConstants.CREDENTIALS_FILE);

		// exit if configuration exist
		if (validateConfigurationExist(resourcesFile, credentialsFile)) {
			return Command.CONTINUE_PROCESSING;
		}

		// create default resources configuration
		Configuration resourcesConfiguration = createDefaultResourceConfiguration();

		try {

			// save resource configuration
			commandFacade.saveJaxbObjects(resourcesFile, resourcesConfiguration, executionResult);

		} catch (CommandFacadeException e) {
			Object[] args = { resourcesFile };
			executionResult.completeAsFailure(messageProvider, "cdecc.create_defaultresources_failed", args);
			return Command.CONTINUE_PROCESSING;
		}

		// create default credentials configuration
		Configuration credentialsConfiguration = createDefaultCredentialsConfiguration();

		try {
			// save credentials configuration
			commandFacade.saveJaxbObjects(credentialsFile, credentialsConfiguration, executionResult);

		} catch (CommandFacadeException e) {
			Object[] args = { resourcesFile };
			executionResult.completeAsFailure(messageProvider, "cdecc.create_defaultcredentials_failed", args);
			return Command.CONTINUE_PROCESSING;
		}

		try {
			// copy modules
			File modulesDirectory = runtimeDirectoryProvider.getModulesDirectory();
			commandFacade.copyExampleModules(modulesDirectory, executionResult);

		} catch (CommandFacadeException e) {
			Object[] args = { credentialsFile };
			executionResult.completeAsFailure(messageProvider, "cdecc.create_examplemodules_failed", args);
			return Command.CONTINUE_PROCESSING;
		}

		// compute state
		executionResult.completeAsComputed(messageProvider, "cdecc.succeed", null, "cdecc.failed", null);
		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Validate of configuration exist. Configuration is deemed to exist if either
	 * the resource file or the credentials file exist.
	 * 
	 * @param resourcesFile   resource file.
	 * @param credentialsFile credentials file.
	 * 
	 * @return true if resource file or credentials file exist.
	 */
	boolean validateConfigurationExist(File resourcesFile, File credentialsFile) {

		// add path info to execution result
		String messageHeader = messageProvider.getMessage("cdecc.resources_config_info");
		executionResult.addMessage(messageHeader, resourcesFile.getAbsolutePath());
		// add path info to execution result
		messageHeader = messageProvider.getMessage("cdecc.credentials_config_info");
		executionResult.addMessage(messageHeader, credentialsFile.getAbsolutePath());

		// assert resources configuration doesn't exist
		if (doesFileExist().matches(resourcesFile)) {
			// complete as success
			Object[] args = { resourcesFile };
			executionResult.completeAsSuccessful(messageProvider, "cdecc.assert_resources_config_exist_success", args);
			return true;

		}

		// add message that resource doesn't exist
		String message = messageProvider.getMessage("cdecc.assert_resources_config_exist_failure");
		executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);

		// assert credentials configuration doesn't exists
		if (doesFileExist().matches(credentialsFile)) {
			// complete as success
			Object[] args = { credentialsFile };
			executionResult.completeAsSuccessful(messageProvider, "cdecc.assert_credentials_config_exist_success",
					args);
			return true;
		}

		// add message that resource doesn't exist
		message = messageProvider.getMessage("cdecc.assert_resources_config_exist_failure");
		executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);

		return false;
	}

	/**
	 * Create default resource configuration.
	 * 
	 * @return created default resource configuration.
	 */
	Configuration createDefaultResourceConfiguration() {

		// create configuration
		Configuration configuration = factory.createConfiguration();

		// create environments
		configuration.setEnvironments(factory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();

		// create local environment
		Environment environment = createEnvironment(environments, CoreConstants.WILDCARD_ENVIRONMENT_ID, WILDCARD_DESC);
		addInfrastructureTestResource(environment);
		addCompositeExecutionResource(environment);
		addGitResource(environment, "git-pineapple-example-repo",
				"https://github.com/athrane/pineapple-git-plugin-test-repository.git");

		// create local environment
		environment = createEnvironment(environments, "local", LOCAL_DESC);

		// create linux-vagrant environment
		environment = createEnvironment(environments, "linux-vagrant", VAGRANT_LINUX_DESC);
		addSshResource(environment, "ssh-node1", "192.168.34.10", "22");
		addSshResource(environment, "ssh-node2", "192.168.34.11", "22");
		addSshResource(environment, "ssh-node3", "192.168.34.12", "22");
		addDockerResource(environment, "docker-node", "192.168.34.10", "8082");

		// create linux-pineapple-ci environment
		environment = createEnvironment(environments, "linux-pineapple-ci", PINEAPPLE_CI_LINUX_DESC);
		addSshResource(environment, "ssh-ci-node1", "192.168.99.10", "22");
		addSshResource(environment, "ssh-ci-node2", "192.168.99.11", "22");
		addDockerResource(environment, "docker-ci-node1", "192.168.99.10", "8082");
		addDockerResource(environment, "docker-ci-node2", "192.168.99.11", "8082");
		return configuration;
	}

	/**
	 * Create environment
	 * 
	 * @param environments Environments container.
	 * @param id           Environment id.
	 * @param desc         Environment description.
	 * @return Created environment.
	 */
	Environment createEnvironment(List<Environment> environments, String id, String desc) {
		Environment environment = factory.createEnvironment();
		environment.setId(id);
		environment.setDescription(desc);
		environment.setResources(factory.createResources());
		environments.add(environment);
		return environment;
	}

	/**
	 * Create resource for composite execution plugin.
	 * 
	 * @param environment Environment where resource is added to.
	 */
	void addCompositeExecutionResource(Environment environment) {
		com.alpha.pineapple.model.configuration.Resource resource = factory.createResource();
		resource.setId("composite-execution");
		resource.setPluginId("com.alpha.pineapple.plugin.composite.execution");
		environment.getResources().getResource().add(resource);
	}

	/**
	 * Create resource for infrastructure test plugin.
	 * 
	 * @param environment Environment where resource is added to.
	 */
	void addInfrastructureTestResource(Environment environment) {
		com.alpha.pineapple.model.configuration.Resource resource = factory.createResource();
		resource.setId("infrastructure-test");
		resource.setPluginId("com.alpha.pineapple.plugin.net");
		environment.getResources().getResource().add(resource);
	}

	/**
	 * Create resource for SSH plugin.
	 * 
	 * @param environment Environment where resource is added to.
	 * @param id          Resource ID.
	 * @param host        SSH host.
	 * @param port        SSH port.
	 */
	void addSshResource(Environment environment, String id, String host, String port) {
		com.alpha.pineapple.model.configuration.Resource resource = factory.createResource();
		resource.setId(id);
		resource.setPluginId("com.alpha.pineapple.plugin.ssh");
		resource.setCredentialIdRef(id);
		environment.getResources().getResource().add(resource);
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("host", host);
		properties.put("port", port);
		properties.put("timeout", "1000");
		addProperties(properties, resource);
	}

	/**
	 * Create resource for Agent plugin.
	 * 
	 * @param environment Environment where resource is added to.
	 * @param id          Resource ID.
	 * @param host        agent host.
	 * @param port        agent port.
	 */
	void addAgentResource(Environment environment, String id, String host, String port) {
		com.alpha.pineapple.model.configuration.Resource resource = factory.createResource();
		resource.setId(id);
		resource.setPluginId("com.alpha.pineapple.plugin.agent");
		environment.getResources().getResource().add(resource);
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("host", host);
		properties.put("port", port);
		properties.put("timeout", "30000");
		addProperties(properties, resource);
	}

	/**
	 * Create resource for Docker plugin.
	 * 
	 * @param environment Environment where resource is added to.
	 * @param id          Resource ID.
	 * @param host        Docker daemon host.
	 * @param port        Docker daemon port.
	 */
	void addDockerResource(Environment environment, String id, String host, String port) {
		com.alpha.pineapple.model.configuration.Resource resource = factory.createResource();
		resource.setId(id);
		resource.setPluginId("com.alpha.pineapple.plugin.docker");
		environment.getResources().getResource().add(resource);
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("host", host);
		properties.put("port", port);
		properties.put("timeout", "5000");
		addProperties(properties, resource);
	}

	/**
	 * Create resource for Git plugin.
	 * 
	 * @param environment Environment where resource is added to.
	 * @param id          Resource ID.
	 * @param uri         Git repository URI.
	 */
	void addGitResource(Environment environment, String id, String uri) {
		com.alpha.pineapple.model.configuration.Resource resource = factory.createResource();
		resource.setId(id);
		resource.setPluginId("com.alpha.pineapple.plugin.git");
		environment.getResources().getResource().add(resource);
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("uri", uri);
		addProperties(properties, resource);
	}

	/**
	 * Create default credentials configuration.
	 * 
	 * @return created default credentials configuration.
	 */
	Configuration createDefaultCredentialsConfiguration() {

		// create configuration
		Configuration configuration = factory.createConfiguration();

		// create environments
		configuration.setEnvironments(factory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();

		// create local environment
		Environment environment = createCredentialEnvironment(environments, "local", LOCAL_DESC);

		// create linux-vagrant environment
		environment = createCredentialEnvironment(environments, "linux-vagrant", VAGRANT_LINUX_DESC);
		addSshCredential(environment, "ssh-node1");
		addSshCredential(environment, "ssh-node2");
		addSshCredential(environment, "ssh-node3");

		// create linux-pineapple-ci environment
		environment = createCredentialEnvironment(environments, "linux-pineapple-ci", PINEAPPLE_CI_LINUX_DESC);
		addSshCredential(environment, "ssh-ci-node1");
		addSshCredential(environment, "ssh-ci-node2");
		return configuration;
	}

	/**
	 * Create environment for credentials.
	 * 
	 * @param environments Environments container.
	 * @param id           Environment id.
	 * @param desc         Environment description.
	 * @return Created environment.
	 */
	Environment createCredentialEnvironment(List<Environment> environments, String id, String desc) {
		Environment environment = factory.createEnvironment();
		environment.setId(id);
		environment.setDescription(desc);
		environment.setCredentials(factory.createCredentials());
		environments.add(environment);
		return environment;
	}

	/**
	 * Add credential for SSH plugin.
	 * 
	 * @param environment Environment where credential is added to.
	 * @param id          Resource ID.
	 */
	void addSshCredential(Environment environment, String id) {
		Credential credential = factory.createCredential();
		credential.setId(id);
		credential.setUser("vagrant");
		credential.setPassword("vagrant");
		environment.getCredentials().getCredential().add(credential);
	}

	/**
	 * Add properties to resource.
	 * 
	 * @param properties Hash map containing properties which is added to resource .
	 * @param resource   Target resource.
	 */
	void addProperties(HashMap<String, String> properties, com.alpha.pineapple.model.configuration.Resource resource) {
		// get properties
		List<Property> resourceProps = resource.getProperty();

		// map properties to resource properties
		Set<String> keys = properties.keySet();
		for (String key : keys) {
			String value = properties.get(key);

			// create new property
			Property resourceProp = new Property();
			resourceProp.setKey(key);
			resourceProp.setValue(value);

			// store property
			resourceProps.add(resourceProp);
		}
	}

}
