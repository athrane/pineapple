/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.credential;

import static com.alpha.pineapple.CoreConstants.CREDENTIALS_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Integration test of the class
 * {@linkplain CredentialConfigurationMarshallerImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })

public class CredentialConfigurationMarshallerImplIntegrationTest {

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Subject under test.
	 */
	@Resource(name = "defaultCredentialConfigurationMarshaller")
	CredentialConfigurationMarshaller marshaller;

	/**
	 * Runtime directory resolver.
	 */
	@Resource
	RuntimeDirectoryProvider runtimeDirectoryProvider;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Random environment id.
	 */
	String randomEnvId;

	/**
	 * Random environment id.
	 */
	String randomEnvId2;

	/**
	 * Random description.
	 */
	String randomDescription;

	/**
	 * Random resource id.
	 */
	String randomResourceId;

	@Before
	public void setUp() throws Exception {

		// initialize random fields
		randomEnvId = RandomStringUtils.randomAlphabetic(10);
		randomEnvId2 = RandomStringUtils.randomAlphabetic(10);
		randomDescription = RandomStringUtils.randomAlphabetic(10);
		randomResourceId = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// delete credential configuration
		File crdentialsFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), CREDENTIALS_FILE);
		if (crdentialsFile.exists())
			FileUtils.deleteQuietly(crdentialsFile);
	}

	@After
	public void tearDown() throws Exception {

		// delete credentials configuration
		File credentialsFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), CREDENTIALS_FILE);
		if (credentialsFile.exists())
			FileUtils.deleteQuietly(credentialsFile);

		// clear the pineapple.home.dir system property
		System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);

		// fail if the the pineapple.home.dir system property is set
		assertNull(System.getProperty(SystemUtils.PINEAPPLE_HOMEDIR));
	}

	/**
	 * Test that empty configuration can be saved.
	 */
	@Test
	public void testSaveEmptyConfiguration() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		marshaller.save(envConfiguration);

		// test
		File resourcesFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), CREDENTIALS_FILE);
		assertTrue(resourcesFile.exists());
		assertTrue(resourcesFile.isFile());
	}

	/**
	 * Test that configuration can be saved.
	 */
	@Test
	public void testSaveConfiguration() {
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		marshaller.save(envConfiguration);

		// test
		File credentialsFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), CREDENTIALS_FILE);
		assertTrue(credentialsFile.exists());
		assertTrue(credentialsFile.isFile());
	}

	/**
	 * Test that credentials configuration with no environments can be mapped.
	 */
	@Test
	public void testCanMapWithNoEnvironments() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// map
		ConfigurationInfo configInfo = marshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(0, configInfo.getEnvironments().length);
	}

	/**
	 * Test that resource configuration with single environments can be mapped.
	 */
	@Test
	public void testCanMapWithOneEnvironments() {
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
				randomDescription);

		// map
		ConfigurationInfo configInfo = marshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(1, configInfo.getEnvironments().length);
	}

	/**
	 * Test that resource configuration with multiple environments can be mapped.
	 */
	@Test
	public void testCanMapMultipleEnvironments() {
		// initialize repository
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
				randomDescription);
		envConfigMother.addEnvironment(randomEnvId, envConfiguration.getEnvironments().getEnvironment());
		envConfigMother.addEnvironment(randomEnvId2, envConfiguration.getEnvironments().getEnvironment());

		// map
		ConfigurationInfo configInfo = marshaller.map(envConfiguration);

		// test
		assertTrue(configInfo.containsEnvironment(randomEnvId));
		assertTrue(configInfo.containsEnvironment(randomEnvId2));
	}

	/**
	 * Test that credential configuration with no environments can be mapped, then
	 * credential configuration with no environments can be mapped again and the
	 * number of environments is zero.
	 */
	@Test
	public void testCanMapWithNoEnvironmentsTwice() {
		// map
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		ConfigurationInfo configInfo = marshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(0, configInfo.getEnvironments().length);

		// map
		envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		configInfo = marshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(0, configInfo.getEnvironments().length);

	}

	/**
	 * Test that credential configuration with no environments can be mapped, then a
	 * environment is added to the configuration info, then credential configuration
	 * with no environments can be mapped again and the number of environments is
	 * zero.
	 */
	@Test
	public void testCanMapWithNoEnvironmentsTwiceDespiteAddingEnvironment() {
		// map
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		ConfigurationInfo configInfo = marshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(0, configInfo.getEnvironments().length);

		// add environment
		TreeMap<String, CredentialInfo> resourceInfos = new TreeMap<String, CredentialInfo>();
		EnvironmentInfo environmentInfo = new EnvironmentInfoImpl(randomEnvId, randomDescription, resourceInfos);
		configInfo.addEnvironment(environmentInfo);

		// map
		envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		configInfo = marshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(0, configInfo.getEnvironments().length);
	}

	/**
	 * Test that configuration can be loaded.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testLoadConfiguration() throws Exception {
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		envConfigMother.marshallToCredentialsFile(envConfiguration, testDirectory);

		// load
		File file = envConfigMother.createCredentialsFileName(testDirectory);
		Configuration loadedConfiguration = marshaller.load(file);

		// test
		assertNotNull(loadedConfiguration);
	}

	/**
	 * Test that loaded configuration contains expected environment.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testLoadedConfigurationContainsExpectedEnvironment() throws Exception {
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		envConfigMother.marshallToCredentialsFile(envConfiguration, testDirectory);

		// load
		File file = envConfigMother.createCredentialsFileName(testDirectory);
		Configuration loadedConfiguration = marshaller.load(file);

		// test
		assertNotNull(loadedConfiguration.getEnvironments());
		assertNotNull(loadedConfiguration.getEnvironments().getEnvironment());
		assertEquals(1, loadedConfiguration.getEnvironments().getEnvironment().size());
		assertEquals(randomEnvId, loadedConfiguration.getEnvironments().getEnvironment().get(0).getId());
	}

	/**
	 * Test that loading configuration fails if file path is illegal.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = CredentialsFileNotFoundException.class)
	public void testLoadConfigurationFailsForIllegalFile() throws Exception {

		// load to trigger exception
		File file = new File(randomDescription);
		marshaller.load(file);
	}

	/**
	 * Test that loading configuration fails if file doesn't exist.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = CredentialsFileNotFoundException.class)
	public void testLoadConfigurationFailsForNonExistingFile() throws Exception {

		// load to trigger exception
		File file = new File(testDirectory, randomDescription);
		marshaller.load(file);
	}

}
