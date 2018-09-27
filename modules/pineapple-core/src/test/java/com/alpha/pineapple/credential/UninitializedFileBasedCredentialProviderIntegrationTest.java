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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

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

import com.alpha.pineapple.CoreFactory;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Environment;
import com.alpha.pineapple.model.configuration.ObjectFactory;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.CoreTestConstants;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Integration test of the bean uninitializedFileBasedCredentialProvider defined
 * in the Spring context.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class UninitializedFileBasedCredentialProviderIntegrationTest {

	/**
	 * Object under test.
	 */
	@Resource
	CoreFactory coreFactory;

	/**
	 * /** Current test directory.
	 */
	File testDirectory;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Object factory for environment configuration model.
	 */
	ObjectFactory factory = new ObjectFactory();

	/**
	 * Random name.
	 */
	String randomName;

	@Before
	public void setUp() throws Exception {
		randomName = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

	}

	@After
	public void tearDown() throws Exception {
		testDirectory = null;
		envConfigMother = null;
		coreFactory = null;
	}

	/**
	 * Constructor test, i.e. that JAXB based credential provider instance can be
	 * created.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCanCreateInstance() throws Exception {
		// create environment configuration
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();

		// marshall configuration as credentials file.
		envConfigMother.marshallToCredentialsFile(configuration, testDirectory);

		// create file name
		File expectedFile = envConfigMother.createCredentialsFileName(testDirectory);

		// create provider
		CredentialProvider provider = coreFactory.createCredentialProvider(expectedFile);

		// test
		assertNotNull(provider);
	}

	/**
	 * Rejects undefined credentials file.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedCredentialsFile() throws Exception {
		// test
		CredentialProvider provider = coreFactory.createCredentialProvider((File) null);
	}

	/**
	 * Credential provider initialization fails if credentials file doesn't exist.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test(expected = CredentialsFileNotFoundException.class)
	public void testFailsIfCredentialsFileDoesntExist() throws Exception {
		// create file name
		File expectedFile = envConfigMother.createCredentialsFileName(testDirectory);

		// test
		CredentialProvider provider = coreFactory.createCredentialProvider((File) expectedFile);
	}

	/**
	 * Test that empty credentials file (with only the configuration elmment
	 * defined) can be processed by the provider.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanProcessEmptyCredentialsFile() throws Exception {
		// create environment configuration
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();

		// marshall configuration as credentials file.
		envConfigMother.marshallToCredentialsFile(configuration, testDirectory);

		// create file name
		File expectedFile = envConfigMother.createCredentialsFileName(testDirectory);

		// create provider
		CredentialProvider provider = coreFactory.createCredentialProvider(expectedFile);

		// test
		assertNotNull(provider);
	}

	/**
	 * Test that credentials file (with an empty environment defined) can be
	 * processed by the provider.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanProcessCredentialsFileWithEmptyEnvironment() throws Exception {
		// create environment configuration
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create environments
		configuration.setEnvironments(factory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();

		// add environment
		Environment environment = factory.createEnvironment();
		environment.setId(randomName);
		environments.add(environment);

		// marshall configuration as credentials file.
		envConfigMother.marshallToCredentialsFile(configuration, testDirectory);

		// create file name
		File expectedFile = envConfigMother.createCredentialsFileName(testDirectory);

		// create provider
		CredentialProvider provider = coreFactory.createCredentialProvider(expectedFile);

		// test
		assertNotNull(provider);
	}

	/**
	 * Test that credentials file (with an environment with an empty credentials
	 * element defined)
	 * 
	 * can be processed by the provider.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanProcessCredentialsFileWithNoCredentials() throws Exception {
		// create environment configuration
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create environments
		configuration.setEnvironments(factory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();

		// add environment
		Environment environment = factory.createEnvironment();
		environment.setId(randomName);
		environment.setCredentials(factory.createCredentials());
		environments.add(environment);

		// marshall configuration as credentials file.
		envConfigMother.marshallToCredentialsFile(configuration, testDirectory);

		// create file name
		File expectedFile = envConfigMother.createCredentialsFileName(testDirectory);

		// create provider
		CredentialProvider provider = coreFactory.createCredentialProvider(expectedFile);

		// test
		assertNotNull(provider);
	}

	/**
	 * Test that provider with one credential can be created.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCanCreateProviderContainingOneCredential() throws Exception {
		// create environment configuration
		Configuration configuration = envConfigMother.createEnvConfigWithSingleCredential();

		// marshall configuration as credentials file.
		envConfigMother.marshallToCredentialsFile(configuration, testDirectory);

		// create file name
		File expectedFile = envConfigMother.createCredentialsFileName(testDirectory);

		// create provider
		CredentialProvider provider = coreFactory.createCredentialProvider(expectedFile);

		// test
		assertNotNull(provider.get(CoreTestConstants.environmentIdOne, CoreTestConstants.credentialIdOne));
	}

	/**
	 * Test that provider with two credential can be created.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanCreateProviderContainingTwoCredentials() throws Exception {
		// create environment configuration
		Configuration configuration = envConfigMother.createEnvConfigWithTwoCredentials();

		// marshall configuration as credentials file.
		envConfigMother.marshallToCredentialsFile(configuration, testDirectory);

		// create file name
		File expectedFile = envConfigMother.createCredentialsFileName(testDirectory);

		// create provider
		CredentialProvider provider = coreFactory.createCredentialProvider(expectedFile);

		// test
		assertNotNull(provider.get(CoreTestConstants.environmentIdOne, CoreTestConstants.credentialIdOne));
		assertNotNull(provider.get(CoreTestConstants.environmentIdOne, CoreTestConstants.credentialIdTwo));
	}

	/**
	 * Test that get credential fails if credentials is undefined in provider.
	 */
	@Test(expected = CredentialNotFoundException.class)
	public void testGetFailsIfProviderDoesntContainRequestedCredential() throws Exception {
		// create environment configuration
		Configuration configuration = envConfigMother.createEnvConfigWithTwoCredentials();

		// marshall configuration as credentials file.
		envConfigMother.marshallToCredentialsFile(configuration, testDirectory);

		// create file name
		File expectedFile = envConfigMother.createCredentialsFileName(testDirectory);

		// create provider
		CredentialProvider provider = coreFactory.createCredentialProvider(expectedFile);

		// test
		Credential credential;
		credential = provider.get(CoreTestConstants.environmentIdOne, "some-identifier");

		fail("Test should never reach here..");
	}

	/**
	 * Test that get credential fails if environment is undefined in provider.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testGetFailsIfProviderDoesntContainRequestedEnvironment() throws Exception {
		// create environment configuration
		Configuration configuration = envConfigMother.createEnvConfigWithTwoCredentials();

		// marshall configuration as credentials file.
		envConfigMother.marshallToCredentialsFile(configuration, testDirectory);

		// create file name
		File expectedFile = envConfigMother.createCredentialsFileName(testDirectory);

		// create provider
		CredentialProvider provider = coreFactory.createCredentialProvider(expectedFile);

		// test
		Credential credential;
		credential = provider.get("some-environment", CoreTestConstants.credentialIdOne);

		fail("Test should never reach here..");
	}

	/**
	 * Test credential contains correct identifier.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testIdentifierIsDefinedInCredential() throws Exception {
		// create environment configuration
		Configuration configuration = envConfigMother.createEnvConfigWithTwoCredentials();

		// marshall configuration as credentials file.
		envConfigMother.marshallToCredentialsFile(configuration, testDirectory);

		// create file name
		File expectedFile = envConfigMother.createCredentialsFileName(testDirectory);

		// create provider
		CredentialProvider provider = coreFactory.createCredentialProvider(expectedFile);

		// expected
		String expected = CoreTestConstants.credentialIdOne;

		// test
		Credential credential;
		credential = provider.get(CoreTestConstants.environmentIdOne, CoreTestConstants.credentialIdOne);
		assertEquals(expected, credential.getId());
	}

	/**
	 * Test credential contains correct user.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testUserIsDefinedInCredential() throws Exception {
		// create environment configuration
		Configuration configuration = envConfigMother.createEnvConfigWithTwoCredentials();

		// marshall configuration as credentials file.
		envConfigMother.marshallToCredentialsFile(configuration, testDirectory);

		// create file name
		File expectedFile = envConfigMother.createCredentialsFileName(testDirectory);

		// create provider
		CredentialProvider provider = coreFactory.createCredentialProvider(expectedFile);

		// expected
		String expected = "some-user";

		// test
		Credential credential;
		credential = provider.get(CoreTestConstants.environmentIdOne, CoreTestConstants.credentialIdOne);
		assertEquals(expected, credential.getUser());
	}

	/**
	 * Test credential contains correct password.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testPasswordIsDefinedInCredential() throws Exception {
		// create environment configuration
		Configuration configuration = envConfigMother.createEnvConfigWithTwoCredentials();

		// marshall configuration as credentials file.
		envConfigMother.marshallToCredentialsFile(configuration, testDirectory);

		// create file name
		File expectedFile = envConfigMother.createCredentialsFileName(testDirectory);

		// create provider
		CredentialProvider provider = coreFactory.createCredentialProvider(expectedFile);

		// expected
		String expected = "some-password";

		// test
		Credential credential;
		credential = provider.get(CoreTestConstants.environmentIdOne, CoreTestConstants.credentialIdOne);
		assertEquals(expected, credential.getPassword());
	}

}
