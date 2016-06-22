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

package com.alpha.pineapple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.credential.FileBasedCredentialProviderImpl;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherCredentialProvider;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.TestUtilsTestConstants;

/**
 * Integration test of the creating a credential provider from the core factory.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
	DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class CredentialProviderIntegrationTest {

    /**
     * Object under test.
     */
    @Resource
    CoreFactory coreFactory;

    /**
     * Credential provider.
     */
    CredentialProvider provider;

    /**
     * Object mother for credential provider
     */
    @Resource
    ObjectMotherCredentialProvider providerMother;

    /**
     * Current test directory.
     */
    File testDirectory;

    /**
     * File based credential provider implementation.
     */
    @Resource
    FileBasedCredentialProviderImpl uninitializedFileBasedCredentialProvider;

    /**
     * Object mother for environment configuration.
     */
    ObjectMotherEnvironmentConfiguration envConfigMother;

    @Before
    public void setUp() throws Exception {

	// create environment configuration object mother
	envConfigMother = new ObjectMotherEnvironmentConfiguration();

	// create credential provider
	provider = providerMother.createEmptyCredentialProvider();

	// get the test directory
	testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

	// clear the pineapple.home.dir system property
	System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);

	// fail if the the pineapple.home.dir system property is set
	assertNull(System.getProperty(SystemUtils.PINEAPPLE_HOMEDIR));
    }

    @After
    public void tearDown() throws Exception {

	// clear the pineapple.home.dir system setting
	System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);
    }

    /**
     * Factory test, i.e. that credential provider can be created from
     * {@link CoreFactory}.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testCanCreateCredentialProviderFromFactory() throws CoreException {
	// create credentials file
	Configuration credentialConfig = envConfigMother.createEnvConfigWithSingleCredential();
	envConfigMother.marshallToCredentialsFile(credentialConfig, testDirectory);

	// create credential file
	File file = envConfigMother.createCredentialsFileName(testDirectory);

	// create core component
	CredentialProvider credentialProvider = coreFactory.createCredentialProvider(file);

	// test
	assertNotNull(credentialProvider);
    }

    /**
     * Factory test, i.e. that credential provider can be created from
     * {@link CoreFactory}.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testCanCreatedCredentialProviderContainsExpectedUser() throws Exception {
	// create credentials file
	Configuration credentialConfig = envConfigMother.createEnvConfigWithSingleCredential();
	envConfigMother.marshallToCredentialsFile(credentialConfig, testDirectory);

	// create credential file
	File file = envConfigMother.createCredentialsFileName(testDirectory);

	// create provider
	CredentialProvider credentialProvider = coreFactory.createCredentialProvider(file);

	// get credential
	Credential credential = credentialProvider.get(TestUtilsTestConstants.environmentIdOne,
		TestUtilsTestConstants.credentialIdOne);

	// test
	assertEquals(TestUtilsTestConstants.credentialIdOne, credential.getId());
	assertEquals("some-user", credential.getUser());
	assertEquals("some-password", credential.getPassword());
    }

    /**
     * Factory test.
     * 
     * If the factory is created with its dependencies injected by Spring, then
     * it will return the same provider instance when the factory method is
     * invoked as returned by the spring context.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testFactoryWithInjectedDependeciesReturnsSameProviderInstanceAsSpringContext() throws CoreException {
	// create credentials file
	Configuration credentialConfig = envConfigMother.createEnvConfigWithSingleCredential();
	envConfigMother.marshallToCredentialsFile(credentialConfig, testDirectory);

	// create credential file
	File file = envConfigMother.createCredentialsFileName(testDirectory);

	// create provider
	CredentialProvider credentialProvider = coreFactory.createCredentialProvider(file);

	// test
	assertEquals(uninitializedFileBasedCredentialProvider, credentialProvider);
    }

    /**
     * Factory test.
     * 
     * If the factory is created with its dependencies injected by Spring, then
     * it will return the same provider instance when invoked multiple times,
     * e.g. the method is idempotent.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testFactoryWithInjectedDependeciesIsIdempotentForProviderFactoryMethod() throws CoreException {
	// create credentials file
	Configuration credentialConfig = envConfigMother.createEnvConfigWithSingleCredential();
	envConfigMother.marshallToCredentialsFile(credentialConfig, testDirectory);

	// create credential file
	File file = envConfigMother.createCredentialsFileName(testDirectory);

	// create provider
	CredentialProvider credentialProvider = coreFactory.createCredentialProvider(file);

	// create provider
	CredentialProvider credentialProvider2 = coreFactory.createCredentialProvider(file);

	// test
	assertEquals(uninitializedFileBasedCredentialProvider, credentialProvider);
	assertEquals(uninitializedFileBasedCredentialProvider, credentialProvider2);
    }

}
