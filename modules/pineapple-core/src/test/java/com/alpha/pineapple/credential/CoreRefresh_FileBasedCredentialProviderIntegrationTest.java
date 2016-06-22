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
import static org.junit.Assert.assertNull;

import java.io.File;

import javax.annotation.Resource;

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
import com.alpha.pineapple.CoreFactory;
import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.admin.Administration;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Integration test of the class {@link FileBasedCredentialProviderImpl} which
 * tests decryption of password across core re-initializations.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class CoreRefresh_FileBasedCredentialProviderIntegrationTest {
    /**
     * Current test directory.
     */
    File testDirectory;

    /**
     * Core component factory.
     */
    @Resource
    CoreFactory coreFactory;

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

    /**
     * Random environment.
     */
    String randomEnvironment;

    /***
     * Modules directory.
     */
    File modulesDir;

    /**
     * Conf directory.
     */
    File confDir;

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
	randomEnvironment = RandomStringUtils.randomAlphabetic(10);

	// get the test directory
	testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

	// create environment configuration object mother
	envConfigMother = new ObjectMotherEnvironmentConfiguration();

	// define directory names
	modulesDir = new File(testDirectory, "modules");
	confDir = new File(testDirectory, "conf");

	// set the pineapple.home.dir system property
	System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

	createEnvConfigurationAndModule();
    }

    @After
    public void tearDown() throws Exception {
	// clear the pineapple.home.dir system property
	System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);

	// fail if the the pineapple.home.dir system property is set
	assertNull(System.getProperty(SystemUtils.PINEAPPLE_HOMEDIR));

	envConfigMother = null;
    }

    /**
     * Create environment configuration and module.
     * 
     * The environment configuration contains a random environment with resource
     * for hello world plugin.
     * 
     * The module contains a model which uses the hello world plugin.
     * 
     * @throws Exception
     *             if test fails.
     */
    void createEnvConfigurationAndModule() throws Exception {
	// create pineapple runtime sub directories, e.g conf and modules
	confDir.mkdirs();
	modulesDir.mkdirs();

	// create environment configuration and save it
	Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();
	File resourcesFile = envConfigMother.createResourcesFileName(confDir);
	envConfigMother.jaxbMarshall(configuration, resourcesFile);

	// create credentials configuration and save it
	Configuration credentialsConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	File credentialsFile = envConfigMother.createCredentialsFileName(confDir);
	envConfigMother.jaxbMarshall(credentialsConfiguration, credentialsFile);
    }

    /**
     * Test that password decryption is stable across core component
     * re-initializations.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testPasswordDescryptionIStableAcrossCoreReinitializations() throws Exception {

	// create core component
	PineappleCore coreComponent = coreFactory.createCore();

	// create environment and credential
	Administration admin = coreComponent.getAdministration();
	CredentialProvider provider = admin.getCredentialProvider();
	provider.createEnvironment(randomEnvironment, randomDescription);
	provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);
	Credential credential = provider.get(randomEnvironment, randomCredentialId);

	// test
	assertEquals(randomPassword, credential.getPassword());

	// re-initialize core
	coreComponent = coreFactory.createCore();

	// get decrypted password
	admin = coreComponent.getAdministration();
	provider = admin.getCredentialProvider();
	Credential credential2 = provider.get(randomEnvironment, randomCredentialId);

	// test
	assertEquals(randomPassword, credential2.getPassword());

    }

}
