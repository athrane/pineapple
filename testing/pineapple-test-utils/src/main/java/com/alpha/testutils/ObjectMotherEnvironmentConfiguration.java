/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen.
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

package com.alpha.testutils;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Environment;
import com.alpha.pineapple.model.configuration.Environments;
import com.alpha.pineapple.model.configuration.ObjectFactory;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperation;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperations;
import com.alpha.pineapple.model.report.Report;
import com.alpha.pineapple.model.report.Reports;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for
 * unit testing classes which uses environment configuration, e.g. resources and
 * credentials.
 */
public class ObjectMotherEnvironmentConfiguration {
	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Object factory for environment configuration model.
	 */
	ObjectFactory envConfigurationfactory = new ObjectFactory();

	/**
	 * Object factory for scheduled operations model.
	 */
	com.alpha.pineapple.model.execution.scheduled.ObjectFactory scheduledOperationFactory = new com.alpha.pineapple.model.execution.scheduled.ObjectFactory();

	/**
	 * Object factory for reports.
	 */
	com.alpha.pineapple.model.report.ObjectFactory reportsFactory = new com.alpha.pineapple.model.report.ObjectFactory();

	/**
	 * Object mother for resources.
	 */
	ObjectMotherResource resourceMother;

	/**
	 * ObjectMotherEnvironmentConfiguration constructor.
	 */
	public ObjectMotherEnvironmentConfiguration() {
		resourceMother = new ObjectMotherResource();
	}

	/**
	 * Create credential.
	 *
	 * @param Id
	 *            credential ID.
	 * @param user
	 *            user.
	 * @param password
	 *            password.
	 */
	public Credential createCredential(String id, String user, String password) {
		Credential credential = new Credential();
		credential.setId(id);
		credential.setUser(user);
		credential.setPassword(password);
		return credential;
	}

	/**
	 * Create encrypted credential.
	 *
	 * @param Id
	 *            credential ID.
	 * @param user
	 *            user.
	 * @param password
	 *            password.
	 */
	public Credential createEncryptedCredential(String id, String user, String password) {
		Credential credential = new Credential();
		credential.setId(id);
		credential.setUser(user);
		credential.setPassword("encrypted:" + password);
		return credential;
	}

	/**
	 * Create file name for credentials configuration file.
	 * 
	 * @param testDir
	 *            The test case directory where the file should be placed.
	 * 
	 * @return File object containing absolute file name for for credentials
	 *         configuration file.
	 */
	public File createCredentialsFileName(File testDir) {
		return new File(testDir, TestUtilsTestConstants.credentialsFileName);
	}

	/**
	 * Create file name for resources configuration file.
	 * 
	 * @param testDir
	 *            The test case directory where the file should be placed.
	 * 
	 * @return File object containing absolute file name for for resources
	 *         configuration file.
	 */
	public File createResourcesFileName(File testDir) {
		return new File(testDir, TestUtilsTestConstants.resourcesFileName);
	}

	/**
	 * Marshall environment configuration object to credentials file named
	 * "credentials.xml" at provided test directory.
	 * 
	 * @param configuration
	 *            Environment configuration which should be marshalled.
	 * @param testDir
	 *            The test case directory where the file should be placed.
	 */
	public void marshallToCredentialsFile(Configuration configuration, File testDir) {

		// create file
		File file = createCredentialsFileName(testDir);

		// marshal
		jaxbMarshall(configuration, file);
	}

	/**
	 * Marshall environment configuration object to resources file named
	 * "resources.xml" at provided test directory.
	 * 
	 * @param configuration
	 *            Environment configuration which should be marshalled.
	 * @param testDir
	 *            The test case directory where the file should be placed.
	 */
	public void marshallToResourcesFile(Configuration configuration, File testDir) {

		// create file
		File file = createResourcesFileName(testDir);

		// marshal
		jaxbMarshall(configuration, file);
	}

	/**
	 * Marshall object graph to file using JAXB.
	 * 
	 * @param rootObject
	 *            rootObject Root object of object graph which should be marshalled.
	 * @param file
	 *            File that the environment configuration should be saved to.
	 */
	public void jaxbMarshall(Object rootObject, File file) {
		// define stream for exception handling
		OutputStream os = null;

		try {
			// get package name
			String packageName = rootObject.getClass().getPackage().getName();

			// log debug message
			StringBuilder message = new StringBuilder();
			message.append("Will marshall environment configuration <");
			message.append(rootObject);
			message.append("> to file <");
			message.append(file.getAbsolutePath());
			message.append("> using package <");
			message.append(packageName);
			message.append(">.");
			logger.debug(message.toString());

			JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
			os = new FileOutputStream(file);
			marshaller.marshal(rootObject, os);
			os.close();
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} finally {

			// close OS
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					fail(StackTraceHelper.getStrackTrace(e));
				}
			}
		}

	}

	/**
	 * Marshall object graph to string using JAXB.
	 * 
	 * @param rootObject
	 *            rootObject Root object of object graph which should be marshalled.
	 * 
	 * @return string containing marshalled object graph.
	 */
	public String jaxbMarshallToString(Object rootObject) {
		try {
			// get package name
			String packageName = rootObject.getClass().getPackage().getName();

			// log debug message
			StringBuilder message = new StringBuilder();
			message.append("Will marshall environment configuration <");
			message.append(rootObject);
			message.append("> to string using package <");
			message.append(packageName);
			message.append(">.");
			logger.debug(message.toString());

			JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
			StringWriter stringWriter = new StringWriter();
			marshaller.marshal(rootObject, stringWriter);
			return stringWriter.toString();

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
			return null;
		}

	}

	/**
	 * Unmarshall file into object graph using JAXB.
	 * 
	 * @param file
	 *            File that the environment configuration should be loaded from.
	 * @param targetPackage
	 *            Defines the Java package which contains the JAXB generated classes
	 *            which should be use to unmarshall the XML file into.
	 * 
	 * @return contains the root object of the unmarshalled objects.
	 */
	public Object jaxbUnmarshall(File file, Package targetPackage) {

		try {
			// log debug message
			StringBuilder message = new StringBuilder();
			message.append("Will unmarshall file <");
			message.append(file.getAbsolutePath());
			message.append("> into environment configuration using package <");
			message.append(targetPackage);
			message.append(">.");
			logger.debug(message.toString());

			// create JAXB context
			JAXBContext jaxbContext;
			jaxbContext = JAXBContext.newInstance(targetPackage.getName());

			// create unmarshaller
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			// enable validation

			// unmarshall file
			return unmarshaller.unmarshal(file.toURL());

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
			return null;
		}
	}

	/**
	 * Create empty environment configuration object, i.e. with no environment or
	 * credentials.
	 * 
	 * @return Empty empty environment configuration object.
	 */
	public Configuration createEmptyEnvironmentConfiguration() {
		// create configuration
		Configuration configuration = envConfigurationfactory.createConfiguration();

		return configuration;
	}

	/**
	 * Create environment configuration with a single credential. The credentials
	 * are named "credential-1" and "credential-2" in environment "environment-1".
	 * 
	 * @return environment configuration with a single credential.
	 */
	public Configuration createEnvConfigWithTwoCredentials() {
		// create configuration
		Configuration configuration = createEmptyEnvironmentConfiguration();

		// create environments
		configuration.setEnvironments(envConfigurationfactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();

		Environment environment = addEnvironment(environments);

		// add credential "credential-1"
		Credential credential = envConfigurationfactory.createCredential();
		credential.setId(TestUtilsTestConstants.credentialIdOne);
		credential.setUser("some-user");
		credential.setPassword("some-password");
		environment.getCredentials().getCredential().add(credential);

		// add credential "credential-2"
		Credential credential2 = envConfigurationfactory.createCredential();
		credential2.setId(TestUtilsTestConstants.credentialIdTwo);
		credential2.setUser("some-user");
		credential2.setPassword("some-password");
		environment.getCredentials().getCredential().add(credential2);

		return configuration;
	}

	/**
	 * Create environment configuration with a single credential. The credential is
	 * name "credential-1" in environment "environment-1".
	 * 
	 * @return environment configuration with a single credential.
	 */
	public Configuration createEnvConfigWithSingleCredential() {
		// create configuration
		Configuration configuration = createEmptyEnvironmentConfiguration();

		// create environments
		configuration.setEnvironments(envConfigurationfactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();

		// add environment
		Environment environment = addEnvironment(environments);

		// add credential "credential-1"
		Credential credential = envConfigurationfactory.createCredential();
		credential.setId(TestUtilsTestConstants.credentialIdOne);
		credential.setUser("some-user");
		credential.setPassword("some-password");
		environment.getCredentials().getCredential().add(credential);

		return configuration;
	}

	/**
	 * Create environment configuration with a single credential in an environment.
	 *
	 * @param environmentId
	 *            environment ID.
	 * @param id
	 *            credential ID.
	 * @param user
	 *            user.
	 * @param password
	 *            password.
	 * 
	 * @return environment configuration with a single credential in an environment.
	 */
	public Configuration createEnvConfigWithSingleCredential(String environmentId, String id, String user,
			String password) {
		Configuration configuration = createEmptyEnvironmentConfiguration();
		configuration.setEnvironments(envConfigurationfactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();
		Environment environment = addEnvironment(environmentId, environments);
		addCredentialToEnvironment(environment, id, user, password);
		return configuration;
	}

	/**
	 * Create environment configuration with a single encrypted credential in an
	 * environment.
	 *
	 * @param environmentId
	 *            environment ID.
	 * @param id
	 *            credential ID.
	 * @param user
	 *            user.
	 * @param password
	 *            password.
	 * 
	 * @return environment configuration with a single encrypted credential in an
	 *         environment.
	 */
	public Configuration createEnvConfigWithSingleEncryptedCredential(String environmentId, String id, String user,
			String password) {
		Configuration configuration = createEmptyEnvironmentConfiguration();
		configuration.setEnvironments(envConfigurationfactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();
		Environment environment = addEnvironment(environmentId, environments);
		addEncryptedCredentialToEnvironment(environment, id, user, password);
		return configuration;
	}

	/**
	 * Add environment named "environment-1".
	 * 
	 * @param environments
	 *            environments holder.
	 * 
	 * @return Environment named "environment-1".
	 */
	public Environment addEnvironment(List<Environment> environments) {
		return addEnvironment(TestUtilsTestConstants.environmentIdOne, environments);
	}

	/**
	 * Add environment.
	 * 
	 * @param environments
	 *            environments container.
	 * 
	 * @return Environment which is added to the environment container.
	 */
	public Environment addEnvironment(String id, List<Environment> environments) {
		Environment environment = envConfigurationfactory.createEnvironment();
		environment.setId(id);
		environment.setDescription("Description for" + id);
		environment.setCredentials(envConfigurationfactory.createCredentials());
		environments.add(environment);
		return environment;
	}

	/**
	 * Create environment configuration with a single credential. The credential is
	 * name "test-resource-credentialidentifier" in environment "environment-1".
	 * 
	 * @return environment configuration with a single credential.
	 */
	public Configuration createEnvConfigWithTestResourceCredential() {
		// create configuration
		Configuration configuration = createEmptyEnvironmentConfiguration();

		// create environments
		configuration.setEnvironments(envConfigurationfactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();

		// add environment
		Environment environment = addEnvironment(environments);

		// add credential "test-resource-credentialidentifier"
		Credential credential = envConfigurationfactory.createCredential();
		credential.setId(TestUtilsTestConstants.credentialIdTestResource);
		credential.setUser(TestUtilsTestConstants.userTestResource);
		credential.setPassword(TestUtilsTestConstants.passwordTestResource);
		environment.getCredentials().getCredential().add(credential);

		return configuration;
	}

	/**
	 * Create environment configuration with a single resource. The resource is
	 * named "test-resource-resource-id" in environment "environment-1" and have a
	 * single property.
	 * 
	 * @return environment configuration with a single credential.
	 */
	public Configuration createEnvConfigWithSingleTestResourceWithSingleProperty() {
		// create configuration
		Configuration configuration = createEmptyEnvironmentConfiguration();

		// create environments
		configuration.setEnvironments(envConfigurationfactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();

		// add environment
		Environment environment = addEnvironment(environments);

		// create resource
		HashMap<String, String> properties;
		properties = new HashMap<String, String>();
		properties.put("key-1", "value-1");
		Resource resource = resourceMother.createTestResourceWithProperties(properties);

		// add resource
		environment.setResources(envConfigurationfactory.createResources());
		environment.getResources().getResource().add(resource);

		return configuration;
	}

	/**
	 * Create environment configuration with a single resource. The resource is
	 * named TestResource in environment "environment-1" and have multiple
	 * properties.
	 * 
	 * @return environment configuration with a single credential.
	 */
	public Configuration createEnvConfigWithSingleTestResourceWithMultipleProperties() {
		// create configuration
		Configuration configuration = createEmptyEnvironmentConfiguration();

		// create environments
		configuration.setEnvironments(envConfigurationfactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();

		// add environment
		Environment environment = addEnvironment(environments);

		// create resource
		HashMap<String, String> properties;
		properties = new HashMap<String, String>();
		properties.put("key-1", "value-1");
		properties.put("key-2", "value-2");
		Resource resource = resourceMother.createTestResourceWithProperties(properties);

		// add resource
		environment.setResources(envConfigurationfactory.createResources());
		environment.getResources().getResource().add(resource);

		return configuration;
	}

	/**
	 * Create environment configuration with single environment.
	 * 
	 * @param id
	 *            Environment id.
	 * 
	 * @return environment configuration with single environment.
	 */
	public Configuration createEnvConfigWithSingleEnvironment(String id) {
		// create document
		ObjectFactory factory = new ObjectFactory();
		Configuration configuration = factory.createConfiguration();
		Environments environments = factory.createEnvironments();
		configuration.setEnvironments(environments);

		// create environment
		Environment environment = factory.createEnvironment();
		environment.setId(id);

		// add environment to configuration
		environments.getEnvironment().add(environment);

		return configuration;
	}

	/**
	 * Create environment configuration with single environment.
	 * 
	 * @param id
	 *            Environment id.
	 * @param description
	 *            Environment description.
	 * 
	 * @return environment configuration with single environment.
	 */
	public Configuration createEnvConfigWithSingleEnvironment(String id, String description) {
		// create document
		ObjectFactory factory = new ObjectFactory();
		Configuration configuration = factory.createConfiguration();
		Environments environments = factory.createEnvironments();
		configuration.setEnvironments(environments);

		// create environment
		Environment environment = factory.createEnvironment();
		environment.setId(id);
		environment.setDescription(description);

		// add environment to configuration
		environments.getEnvironment().add(environment);

		return configuration;
	}

	/**
	 * Create environment configuration with a single resource in an environment.
	 *
	 * @param environmentId
	 *            environment ID.
	 * @param resourceId
	 *            resource ID.
	 * @param pluginId
	 *            plugin ID.
	 * @param credentialIdRef
	 *            credential ID reference.
	 * 
	 * @return environment configuration with a single resource in an environment.
	 */
	public Configuration createEnvConfigWithSingleResource(String environmentId, String resourceId, String pluginId,
			String credentialIdRef) {
		// create configuration
		Configuration configuration = createEmptyEnvironmentConfiguration();

		// create environments
		configuration.setEnvironments(envConfigurationfactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();

		// add environment
		Environment environment = addEnvironment(environmentId, environments);

		// create resource
		addResourceWithNoPropsToEnvironment(environment, resourceId, pluginId, credentialIdRef);

		return configuration;
	}

	/**
	 * Create environment configuration with a single resource in an environment.
	 *
	 * @param environmentId
	 *            environment ID.
	 * @param resourceId
	 *            resource ID.
	 * @param pluginId
	 *            plugin ID.
	 * 
	 * @return environment configuration with a single resource in an environment.
	 */
	public Configuration createEnvConfigWithSingleResource(String environmentId, String resourceId, String pluginId) {
		// create configuration
		Configuration configuration = createEmptyEnvironmentConfiguration();

		// create environments
		configuration.setEnvironments(envConfigurationfactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();

		// add environment
		Environment environment = addEnvironment(environmentId, environments);

		// create resource
		addResourceWithNoPropsNoCredentialToEnvironment(environment, resourceId, pluginId);

		return configuration;
	}

	/**
	 * Create environment configuration with a single resource in an environment.
	 * The plugin id of the added resource will be random geenrated.
	 *
	 * @param environmentId
	 *            environment ID.
	 * @param resourceId
	 *            resource ID.
	 * 
	 * @return environment configuration with a single resource in an environment.
	 */
	public Configuration createEnvConfigWithSingleResource(String environmentId, String resourceId) {
		String randomPluginId = RandomStringUtils.randomAlphanumeric(10);
		return createEnvConfigWithSingleResource(environmentId, resourceId, randomPluginId);
	}

	/**
	 * Add resource with no properties and no credential to environment.
	 *
	 * @param environment
	 *            Environment where resource is added.
	 * @param resourceId
	 *            resource ID.
	 * @param pluginId
	 *            plugin ID.
	 * 
	 * @return resource.
	 */
	public Resource addResourceWithNoPropsNoCredentialToEnvironment(Environment environment, String resourceId,
			String pluginId) {
		// create resource
		Resource resource = resourceMother.createResourceWithNoPropertiesNoCredential(resourceId, pluginId);

		// add resource
		environment.setResources(envConfigurationfactory.createResources());
		environment.getResources().getResource().add(resource);

		return resource;
	}

	/**
	 * Add resource with no properties and no credential to environment.
	 *
	 * @param environment
	 *            Environment where resource is added.
	 * @param resourceId
	 *            resource ID.
	 * @param pluginId
	 *            plugin ID.
	 * @param credentialIdRef
	 *            credential ID reference.
	 * 
	 * @return resource.
	 */
	public Resource addResourceWithNoPropsToEnvironment(Environment environment, String resourceId, String pluginId,
			String credentialIdRef) {
		// create resource
		Resource resource = resourceMother.createResourceWithNoPropertiesNoCredential(resourceId, pluginId,
				credentialIdRef);

		// add resource
		environment.setResources(envConfigurationfactory.createResources());
		environment.getResources().getResource().add(resource);

		return resource;
	}

	/**
	 * Add resource with no properties to environment.
	 *
	 * @param environment
	 *            Environment where resource is added.
	 * @param resourceId
	 *            resource ID.
	 * 
	 * @return resource.
	 */
	public Resource addResourceWithNoPropsToEnvironment(Environment environment, String resourceId) {
		// create resource
		Resource resource = resourceMother.createResourceWithNoProperties(resourceId);

		// add resource
		environment.setResources(envConfigurationfactory.createResources());
		environment.getResources().getResource().add(resource);

		return resource;
	}

	/**
	 * Add credential to environment.
	 *
	 * @param environment
	 *            Environment where credential is added.
	 * @param id
	 *            credentail Id.
	 * @param user
	 *            user.
	 * @param password
	 *            password.
	 * 
	 * @return credential.
	 */
	public Credential addCredentialToEnvironment(Environment environment, String id, String user, String password) {
		Credential credential = createCredential(id, user, password);

		// add credential
		environment.setCredentials(envConfigurationfactory.createCredentials());
		environment.getCredentials().getCredential().add(credential);
		return credential;
	}

	/**
	 * Add encrypted credential to environment.
	 *
	 * @param environment
	 *            Environment where credential is added.
	 * @param id
	 *            credentail Id.
	 * @param user
	 *            user.
	 * @param password
	 *            password.
	 * 
	 * @return credential.
	 */
	public Credential addEncryptedCredentialToEnvironment(Environment environment, String id, String user,
			String password) {
		Credential credential = createEncryptedCredential(id, user, password);

		// add credential
		environment.setCredentials(envConfigurationfactory.createCredentials());
		environment.getCredentials().getCredential().add(credential);
		return credential;
	}

	/**
	 * Create empty scheduled operation configuration object, i.e. with no
	 * operations.
	 * 
	 * @return Empty configuration object.
	 */
	public ScheduledOperations createEmptyScheduledOperationConfiguration() {
		return scheduledOperationFactory.createScheduledOperations();
	}

	/**
	 * Create scheduled operation configuration object, with a single operation.
	 * 
	 * @param value
	 *            value added to all fields in the operation.
	 * 
	 * @return configuration object.
	 */
	public ScheduledOperations createScheduledOperationConfigurationWithSingleOperation(String value) {
		ScheduledOperations operations = createEmptyScheduledOperationConfiguration();
		ScheduledOperation operation = scheduledOperationFactory.createScheduledOperation();
		operation.setName(value);
		operation.setCron(value);
		operation.setDescription(value);
		operation.setEnvironment(value);
		operation.setModule(value);
		operation.setOperation(value);
		operations.getScheduledOperation().add(operation);
		return operations;
	}

	/**
	 * Create empty reports object, i.e. with no reports.
	 * 
	 * @return Empty configuration object.
	 */
	public Reports createEmptyReports() {
		return reportsFactory.createReports();
	}

	/**
	 * Create reports object with one report with zero duration and defined to be
	 * scheduled.
	 * 
	 * @param value
	 *            value added to all fields in the report.
	 * 
	 * @return reports object with one report.
	 */
	public Reports createReportsWithOneReport(String value) {
		Reports reports = createEmptyReports();

		Report report = reportsFactory.createReport();
		report.setDirectory(value);
		report.setDuration(0L);
		report.setId(value);
		report.setIsScheduled(true);
		report.setModel(value);
		report.setModule(value);
		report.setOperation(value);
		report.setResult(value);
		report.setStart(value);

		reports.getReport().add(report);
		return reports;
	}

}
