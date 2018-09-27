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

package com.alpha.pineapple.credential;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.resource.EnvironmentAlreadyExistsException;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Integration test of the class {@link FileBasedCredentialProviderImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class FileBasedCredentialProviderIntegrationTest {
	/**
	 * Null credential ID reference.
	 */
	final static String NULL_CREDENTIAL_ID_REF = null;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Object under test.
	 */
	@Resource
	CredentialProvider uninitializedFileBasedCredentialProvider;

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
	 * Random user.
	 */
	String randomUser;

	/**
	 * Random password.
	 */
	String randomPassword;

	/**
	 * Random ID.
	 */
	String randomCredentialId;

	@Before
	public void setUp() throws Exception {
		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// initialize random fields
		randomEnvId = RandomStringUtils.randomAlphabetic(10);
		randomEnvId2 = RandomStringUtils.randomAlphabetic(10);
		randomDescription = RandomStringUtils.randomAlphabetic(10);
		randomCredentialId = RandomStringUtils.randomAlphabetic(10);
		randomUser = RandomStringUtils.randomAlphabetic(10);
		randomPassword = RandomStringUtils.randomAlphabetic(10);

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());
	}

	@After
	public void tearDown() throws Exception {
		// clear the pineapple.home.dir system property
		System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);

		// fail if the the pineapple.home.dir system property is set
		assertNull(System.getProperty(SystemUtils.PINEAPPLE_HOMEDIR));

		envConfigMother = null;
		uninitializedFileBasedCredentialProvider = null;
	}

	/**
	 * Test that provider can be lookup from context.
	 */
	@Test
	public void testCanCreateInstance() {
		// test
		assertNotNull(uninitializedFileBasedCredentialProvider);
	}

	/**
	 * Provider can be initialized with configuration with no environments.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCanInitializeWithNoEnvironments() throws Exception {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		EnvironmentInfo[] environments = uninitializedFileBasedCredentialProvider.getEnvironments();
		assertNotNull(environments);
	}

	/**
	 * Provider can be initialized with configuration with no environments.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testInitializeWithNoEnvironmentsReturnsZeroEnvironments() throws Exception {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		EnvironmentInfo[] environments = uninitializedFileBasedCredentialProvider.getEnvironments();
		assertEquals(0, environments.length);
	}

	/**
	 * Initialization of provider with no environments, deletes previous defined
	 * environments.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testInitializeWithNoEnvironmentsDeletesPreviouslyDefinedEnvironments() throws Exception {
		// initialize provider with one environment
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
				randomDescription);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		EnvironmentInfo[] environments = uninitializedFileBasedCredentialProvider.getEnvironments();
		assertEquals(1, environments.length);

		// initialize provider with zero environments
		envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		providerImpl.initialize(envConfiguration);

		// test
		environments = uninitializedFileBasedCredentialProvider.getEnvironments();
		assertEquals(0, environments.length);
	}

	/**
	 * Initialization of provider with, deletes previous defined environments.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testInitializeDeletesPreviouslyDefinedEnvironments() throws Exception {
		// initialize provider with one environment
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
				randomDescription);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		EnvironmentInfo[] environments = uninitializedFileBasedCredentialProvider.getEnvironments();
		assertEquals(1, environments.length);
		assertTrue(uninitializedFileBasedCredentialProvider.containsEnvironment(randomEnvId));

		// initialize provider with one environments
		envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId2, randomDescription);
		providerImpl.initialize(envConfiguration);

		// test
		environments = uninitializedFileBasedCredentialProvider.getEnvironments();
		assertEquals(1, environments.length);
		assertTrue(uninitializedFileBasedCredentialProvider.containsEnvironment(randomEnvId2));
		assertFalse(uninitializedFileBasedCredentialProvider.containsEnvironment(randomEnvId));
	}

	/**
	 * Provider can be initialized with configuration with single environment.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testContainsEnvironment() throws Exception {
		// initialize provider
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
				randomDescription);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertTrue(uninitializedFileBasedCredentialProvider.containsEnvironment(randomEnvId));
	}

	/**
	 * Provider can be initialized with configuration with multiple environments.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testContainsMultipleEnvironments() throws Exception {
		// initialize provider
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
				randomDescription);
		envConfigMother.addEnvironment(randomEnvId, envConfiguration.getEnvironments().getEnvironment());
		envConfigMother.addEnvironment(randomEnvId2, envConfiguration.getEnvironments().getEnvironment());
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertTrue(uninitializedFileBasedCredentialProvider.containsEnvironment(randomEnvId));
		assertTrue(uninitializedFileBasedCredentialProvider.containsEnvironment(randomEnvId2));
	}

	/**
	 * Get credential method throws exception if environment is unknown. Environment
	 * isn't defined.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testGetThrowsExceptionIfEnvironmentIsUnknown() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// get credential
		uninitializedFileBasedCredentialProvider.get(randomEnvId, randomUser);
	}

	/**
	 * Get credential method throws exception if id is unknown in environment.
	 * Environment is defined, with no credential.
	 */
	@Test(expected = CredentialNotFoundException.class)
	public void testGetThrowsExceptionIfCredentialIsUnknownInEnvironment() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// get credential
		uninitializedFileBasedCredentialProvider.get(randomEnvId, randomUser);
	}

	/**
	 * Get credential method can look up credential in environment. Environment is
	 * defined, resource is defined.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetReturnsCredentialFromEnvironment() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvId,
				randomCredentialId, randomUser, randomPassword);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertTrue(uninitializedFileBasedCredentialProvider.containsEnvironment(randomEnvId));

		// get credential
		Credential credential = uninitializedFileBasedCredentialProvider.get(randomEnvId, randomCredentialId);

		// test
		assertEquals(randomCredentialId, credential.getId());
		assertEquals(randomUser, credential.getUser());
		assertEquals(randomPassword, credential.getPassword());
	}

	/**
	 * Contains credential method returns false if environment is unknown.
	 * Environment isn't defined.
	 */
	@Test
	public void testContainsReturnsFalseIfEnvironmentIsUnknown() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertFalse(uninitializedFileBasedCredentialProvider.contains(randomEnvId, randomCredentialId));
	}

	/**
	 * Contains credential method throws exception if id is unknown in environment.
	 * Environment is defined, with no resource.
	 */
	@Test
	public void testContainsReturnsFalseIfCredentialIsUnknownInEnvironment() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertFalse(uninitializedFileBasedCredentialProvider.contains(randomEnvId, randomCredentialId));
	}

	/**
	 * Contains credential method can look up credential in environment. Environment
	 * is defined, credential is defined.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testContainsReturnsResourceFromPrimaryEnvironment() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvId,
				randomCredentialId, randomUser, randomPassword);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertTrue(uninitializedFileBasedCredentialProvider.containsEnvironment(randomEnvId));
		assertTrue(uninitializedFileBasedCredentialProvider.contains(randomEnvId, randomCredentialId));
	}

	/**
	 * Contains resource method returns false if id is unknown in environment.
	 * Environment is defined, with no resource.
	 */
	@Test
	public void testContainsReturnFalseIfIdIsUnknownInEnvironment() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
				randomDescription);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertFalse(uninitializedFileBasedCredentialProvider.contains(randomEnvId, randomCredentialId));
	}

	/**
	 * Provider can create environment with null description.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCreateEnvironmentwithNullDescription() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertFalse(providerImpl.containsEnvironment(randomEnvId));

		// create environment
		providerImpl.createEnvironment(randomEnvId, null);

		// test
		assertTrue(providerImpl.containsEnvironment(randomEnvId));
	}

	/**
	 * Provider can create environment with empty description.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCreateEnvironmentwithEmptyDescription() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertFalse(providerImpl.containsEnvironment(randomEnvId));

		// create environment
		providerImpl.createEnvironment(randomEnvId, "");

		// test
		assertTrue(providerImpl.containsEnvironment(randomEnvId));
	}

	/**
	 * Provider can create environment.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCreateEnvironment() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertFalse(providerImpl.containsEnvironment(randomEnvId));

		// create environment
		providerImpl.createEnvironment(randomEnvId, randomDescription);

		// test
		assertTrue(providerImpl.containsEnvironment(randomEnvId));
	}

	/**
	 * Create environment return environment info object.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCreateEnvironmentReturnsEnvironmentInfo() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertFalse(providerImpl.containsEnvironment(randomEnvId));

		// create environment
		EnvironmentInfo environmentInfo = providerImpl.createEnvironment(randomEnvId, randomDescription);

		// test
		assertNotNull(environmentInfo);
		assertEquals(randomEnvId, environmentInfo.getId());
		assertEquals(randomDescription, environmentInfo.getDescription());
	}

	/**
	 * Create environment return environment info object, environment info doesn't
	 * contains any credentials.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCreateEnvironmentReturnsEnvironmentInfoWithNoCredentials() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertFalse(providerImpl.containsEnvironment(randomEnvId));

		// create environment
		EnvironmentInfo environmentInfo = providerImpl.createEnvironment(randomEnvId, randomDescription);

		// test
		assertNotNull(environmentInfo);
		assertNotNull(environmentInfo.getCredentials());
		assertEquals(0, environmentInfo.getCredentials().length);
	}

	/**
	 * Provider fails to create same environment twice.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = EnvironmentAlreadyExistsException.class)
	public void testFailsToCreateEnvironmentTwice() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// create environment
		providerImpl.createEnvironment(randomEnvId, randomDescription);
		providerImpl.createEnvironment(randomEnvId, randomDescription);
	}

	/**
	 * Provider can update environment ID.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testUpdateEnvironmentId() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		EnvironmentInfo environmentInfo = providerImpl.createEnvironment(randomEnvId, randomDescription);

		// update
		providerImpl.updateEnvironment(environmentInfo, randomEnvId2, randomDescription);

		// test
		assertFalse(providerImpl.containsEnvironment(randomEnvId));
		assertTrue(providerImpl.containsEnvironment(randomEnvId2));
		assertEquals(randomDescription, providerImpl.getEnvironment(randomEnvId2).getDescription());
	}

	/**
	 * Provider can update environment description.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testUpdateEnvironmentDescription() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		EnvironmentInfo environmentInfo = providerImpl.createEnvironment(randomEnvId, randomDescription);

		// update
		String randomDescription2 = RandomStringUtils.randomAlphanumeric(20);
		providerImpl.updateEnvironment(environmentInfo, randomEnvId, randomDescription2);

		// test
		assertTrue(providerImpl.containsEnvironment(randomEnvId));
		assertEquals(randomDescription2, providerImpl.getEnvironment(randomEnvId).getDescription());
	}

	/**
	 * Provider can update environment description to empty.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testUpdateEnvironmentDescriptionToEmpty() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		EnvironmentInfo environmentInfo = providerImpl.createEnvironment(randomEnvId, randomDescription);

		// update
		providerImpl.updateEnvironment(environmentInfo, randomEnvId, "");

		// test
		assertTrue(providerImpl.containsEnvironment(randomEnvId));
		assertEquals("", providerImpl.getEnvironment(randomEnvId).getDescription());
	}

	/**
	 * Provider can update with identical values.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testUpdateEnvironmentWithIdenticalValues() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		EnvironmentInfo environmentInfo = providerImpl.createEnvironment(randomEnvId, randomDescription);

		// update
		providerImpl.updateEnvironment(environmentInfo, randomEnvId, randomDescription);

		// test
		assertTrue(providerImpl.containsEnvironment(randomEnvId));
		assertEquals(randomDescription, providerImpl.getEnvironment(randomEnvId).getDescription());
	}

	/**
	 * Update fails if environment is unknown.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testUpdateEnvironmentFailsIfEnvironmentIsUknown() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		EnvironmentInfo environmentInfo = providerImpl.createEnvironment(randomEnvId, randomDescription);

		// delete environment to invalidate environment info
		providerImpl.deleteEnvironment(randomEnvId);

		// update
		providerImpl.updateEnvironment(environmentInfo, randomEnvId, randomDescription);
	}

	/**
	 * Provider can create credential.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCreateCredential() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// create credential
		providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		assertTrue(providerImpl.contains(randomEnvId, randomCredentialId));
	}

	/**
	 * Create credential returns info object.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCreateCredentialReturnCredentialInfo() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// create credential
		CredentialInfo info = providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		assertNotNull(info);
	}

	/**
	 * Returned credential info object contains expected properties.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCredentialInfoContainsExpectedProperties() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// create credential
		CredentialInfo info = providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		assertEquals(randomCredentialId, info.getId());
		assertEquals(randomUser, info.getUser());
	}

	/**
	 * Returned credential contains encrypted password..
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCredentialContainsEncryptedPassword() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// create credential
		CredentialInfo info = providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		assertTrue(info.getPassword().startsWith(CoreConstants.ENCRYPTED_PREFIX));
		assertEquals(randomPassword, providerImpl.get(randomEnvId, randomCredentialId).getPassword());
	}

	/**
	 * Credential provider returns credential.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetCredential() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// create credential
		providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		assertNotNull(providerImpl.get(randomEnvId, randomCredentialId));
		assertEquals(randomUser, providerImpl.get(randomEnvId, randomCredentialId).getUser());
		assertEquals(randomPassword, providerImpl.get(randomEnvId, randomCredentialId).getPassword());
	}

	/**
	 * Credential provider returns different credential instance on each invocation.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetCredentialReturnsDifferentCredentialInstances() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// create credential
		providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		int hash = providerImpl.get(randomEnvId, randomCredentialId).hashCode();
		int hash2 = providerImpl.get(randomEnvId, randomCredentialId).hashCode();

		assertFalse(hash == hash2);
	}

	/**
	 * Provider fails to create same credential twice.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = CredentialAlreadyExitsException.class)
	public void testFailsToCreateCredentialTwice() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// create credential
		providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);
		providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);
	}

	/**
	 * Delete environment.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteEnvironment() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// test
		assertTrue(providerImpl.containsEnvironment(randomEnvId));

		// delete
		providerImpl.deleteEnvironment(randomEnvId);

		// test
		assertFalse(providerImpl.containsEnvironment(randomEnvId));
	}

	/**
	 * Delete environment rejects null environment.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDeleteEnvironmentRejectsNullEnvironmentId() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// delete
		providerImpl.deleteEnvironment(null);
	}

	/**
	 * Delete environment rejects empty environment.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDeleteEnvironmentRejectsEmptyEnvironmentId() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// delete
		providerImpl.deleteEnvironment("");
	}

	/**
	 * Delete environment rejects unknown environment.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testDeleteEnvironmentRejectsUnknownEnvironmentId() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// delete
		providerImpl.deleteEnvironment(randomEnvId2);
	}

	/**
	 * Delete environment twice fails.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testDeleteEnvironmentTwiceFails() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// delete
		providerImpl.deleteEnvironment(randomEnvId);
		providerImpl.deleteEnvironment(randomEnvId);
	}

	/**
	 * Provider can delete credential.
	 * 
	 * @throws Exception
	 *             if test fails.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteCredential() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		providerImpl.createEnvironment(randomEnvId, null);
		providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// delete resource
		providerImpl.delete(randomEnvId, randomCredentialId);

		// test
		assertFalse(providerImpl.contains(randomEnvId, randomCredentialId));
	}

	/**
	 * Delete credential twice fails.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = CredentialNotFoundException.class)
	public void testDeleteCredentialTwiceFails() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		providerImpl.createEnvironment(randomEnvId, null);
		providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// delete
		providerImpl.delete(randomEnvId, randomCredentialId);
		providerImpl.delete(randomEnvId, randomCredentialId);
	}

	/**
	 * Delete credential method throws exception if environment is unknown.
	 * Environment isn't defined.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testDeleteCredentialThrowsExceptionIfEnvironmentIsUnknown() throws Exception {
		// initialize
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);

		// get credential
		providerImpl.delete(randomEnvId, randomCredentialId);
	}

	/**
	 * Provider can update credential. ID is updated.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testUpdateCredentialWithUpdatedId() throws Exception {
		String newId = RandomStringUtils.randomAlphabetic(10);

		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		CredentialInfo info = providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		assertTrue(providerImpl.contains(randomEnvId, randomCredentialId));

		// update
		CredentialInfo updatedInfo = providerImpl.update(info, randomEnvId, newId, randomUser, randomPassword);

		// test
		assertFalse(providerImpl.contains(randomEnvId, randomCredentialId));
		assertTrue(providerImpl.contains(randomEnvId, newId));
		assertEquals(newId, updatedInfo.getId());
		assertEquals(randomUser, updatedInfo.getUser());
		assertEquals(newId, providerImpl.get(randomEnvId, newId).getId());
		assertEquals(randomUser, providerImpl.get(randomEnvId, newId).getUser());
		assertEquals(randomPassword, providerImpl.get(randomEnvId, newId).getPassword());
	}

	/**
	 * Provider can update credential. Updated credential info has different hash
	 * code.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testUpdatedCredentialHasDifferentHashcode() throws Exception {
		String newId = RandomStringUtils.randomAlphabetic(10);

		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		CredentialInfo info = providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		assertTrue(providerImpl.contains(randomEnvId, randomCredentialId));

		// update
		CredentialInfo updatedInfo = providerImpl.update(info, randomEnvId, newId, randomUser, randomPassword);

		// test
		assertFalse(providerImpl.contains(randomEnvId, randomCredentialId));
		assertTrue(providerImpl.contains(randomEnvId, newId));
		assertFalse(info.hashCode() == updatedInfo.hashCode());
	}

	/**
	 * Provider can update credential. User is updated.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testUpdateCredentialIdWithUpdatedUser() throws Exception {
		String newUser = RandomStringUtils.randomAlphabetic(10);

		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		CredentialInfo info = providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		assertTrue(providerImpl.contains(randomEnvId, randomCredentialId));

		// update
		CredentialInfo updatedInfo = providerImpl.update(info, randomEnvId, randomCredentialId, newUser,
				randomPassword);

		assertTrue(providerImpl.contains(randomEnvId, randomCredentialId));
		assertEquals(randomCredentialId, updatedInfo.getId());
		assertEquals(newUser, updatedInfo.getUser());
		assertEquals(randomPassword, providerImpl.get(randomEnvId, randomCredentialId).getPassword());
	}

	/**
	 * Provider can update credential. Password is updated.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testUpdateCredentialIdWithUpdatedPassword() throws Exception {
		String newPassword = RandomStringUtils.randomAlphabetic(10);

		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		CredentialInfo info = providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		assertTrue(providerImpl.contains(randomEnvId, randomCredentialId));

		// update
		CredentialInfo updatedInfo = providerImpl.update(info, randomEnvId, randomCredentialId, randomUser,
				newPassword);

		assertTrue(providerImpl.contains(randomEnvId, randomCredentialId));
		assertEquals(randomCredentialId, updatedInfo.getId());
		assertEquals(randomUser, updatedInfo.getUser());
		assertEquals(newPassword, providerImpl.get(randomEnvId, randomCredentialId).getPassword());
	}

	/**
	 * Provider can update credential. ID, user and password is updated.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testUpdateCredentialWithAllAttributesUpdated() throws Exception {
		String newId = RandomStringUtils.randomAlphabetic(10);
		String newUser = RandomStringUtils.randomAlphabetic(10);
		String newPassword = RandomStringUtils.randomAlphabetic(10);

		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		CredentialInfo info = providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		assertTrue(providerImpl.contains(randomEnvId, randomCredentialId));

		// update
		CredentialInfo updatedInfo = providerImpl.update(info, randomEnvId, newId, newUser, newPassword);

		// test
		assertFalse(providerImpl.contains(randomEnvId, randomCredentialId));
		assertTrue(providerImpl.contains(randomEnvId, newId));
		assertEquals(newId, updatedInfo.getId());
		assertEquals(newUser, updatedInfo.getUser());
		assertEquals(newPassword, providerImpl.get(randomEnvId, newId).getPassword());
	}

	/**
	 * Update credential method throws exception if environment is unknown.
	 * Environment isn't defined.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testUpdateCredentialFailsIfEnvironmentIsUnknown() throws Exception {
		String newId = RandomStringUtils.randomAlphabetic(10);

		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		CredentialInfo info = providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		assertTrue(providerImpl.contains(randomEnvId, randomCredentialId));

		// delete environment - (also invalidates credential info reference)
		providerImpl.deleteEnvironment(randomEnvId);

		// update
		providerImpl.update(info, randomEnvId, newId, randomUser, randomPassword);
	}

	/**
	 * Update credential method throws exception if credential is unknown.
	 * Environment is defined but credential isn't
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = CredentialNotFoundException.class)
	public void testUpdateCredentialFailsIfCredentialIsUnknown() throws Exception {
		String newId = RandomStringUtils.randomAlphabetic(10);

		// initialize
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		FileBasedCredentialProviderImpl providerImpl = (FileBasedCredentialProviderImpl) uninitializedFileBasedCredentialProvider;
		providerImpl.initialize(envConfiguration);
		CredentialInfo info = providerImpl.create(randomEnvId, randomCredentialId, randomUser, randomPassword);

		// test
		assertTrue(providerImpl.contains(randomEnvId, randomCredentialId));

		// delete credential - (also invalidates credential info reference)
		providerImpl.delete(randomEnvId, randomCredentialId);

		// update
		providerImpl.update(info, randomEnvId, newId, randomUser, randomPassword);
	}

}
