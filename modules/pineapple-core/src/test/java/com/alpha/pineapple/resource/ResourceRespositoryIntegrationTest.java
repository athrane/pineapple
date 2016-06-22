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

package com.alpha.pineapple.resource;

import static com.alpha.pineapple.CoreConstants.WILDCARD_ENVIRONMENT_ID;
import static com.alpha.testutils.testplugins.createresource.CreateResourceTestPluginImpl.PLUGIN_ID;

import com.alpha.testutils.testplugins.pluginprovider.administration.AdministrationProviderTestPluginImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Environment;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.TestUtilsTestConstants;

/**
 * Integration test of the class {@link ResourcesRepositoryImpl }.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class ResourceRespositoryIntegrationTest {
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
    ResourceRepository resourceRepository;

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

    /**
     * Random resource id.
     */
    String randomResourceId2;

    /**
     * Random plugin id.
     */
    String randomPluginId;

    /**
     * Random credential ID reference.
     */
    String randomCredentialIdRef;

    /**
     * Random key.
     */
    String randomKey;

    /**
     * Random value.
     */
    String randomValue;

    @Before
    public void setUp() throws Exception {
	// get the test directory
	testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

	// initialize random fields
	randomEnvId = RandomStringUtils.randomAlphabetic(10);
	randomEnvId2 = RandomStringUtils.randomAlphabetic(10);
	randomDescription = RandomStringUtils.randomAlphabetic(10);
	randomResourceId = RandomStringUtils.randomAlphabetic(10);
	randomResourceId2 = RandomStringUtils.randomAlphabetic(10);
	randomPluginId = RandomStringUtils.randomAlphabetic(10);
	randomKey = RandomStringUtils.randomAlphabetic(10);
	randomValue = RandomStringUtils.randomAlphabetic(10);

	// create environment configuration object mother
	envConfigMother = new ObjectMotherEnvironmentConfiguration();

	// initialize repository with empty configuration
	// Configuration envConfiguration =
	// envConfigMother.createEmptyEnvironmentConfiguration();
	// resourceRepository.initialize(envConfiguration);

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
	resourceRepository = null;
    }

    /**
     * Test that repository can be lookup from context.
     */
    @Test
    public void testCanCreateInstance() {
	// test
	assertNotNull(resourceRepository);
    }

    /**
     * Repository can be initialized with configuration with no environments.
     */
    @Test
    public void testCanInitializeWithNoEnvironments() {
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	EnvironmentInfo[] environments = resourceRepository.getEnvironments();
	assertNotNull(environments);
    }

    /**
     * Repository can be initialized with configuration with no environments.
     */
    @Test
    public void testInitializeWithNoEnvironmentsReturnsZeroEnvironments() {
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	EnvironmentInfo[] environments = resourceRepository.getEnvironments();
	assertEquals(0, environments.length);
    }

    /**
     * Initialization of repository with no environments, deletes previous
     * defined environments.
     */
    @Test
    public void testInitializeWithNoEnvironmentsDeletesPreviouslyDefinedEnvironments() {
	// initialize repository with one environment
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
		randomDescription);
	resourceRepository.initialize(envConfiguration);

	// test
	EnvironmentInfo[] environments = resourceRepository.getEnvironments();
	assertEquals(1, environments.length);

	// initialize repository with zero environments
	envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	environments = resourceRepository.getEnvironments();
	assertEquals(0, environments.length);
    }

    /**
     * Initialization of repository with, deletes previous defined environments.
     */
    @Test
    public void testInitializeDeletesPreviouslyDefinedEnvironments() {
	// initialize repository with one environment
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
		randomDescription);
	resourceRepository.initialize(envConfiguration);

	// test
	EnvironmentInfo[] environments = resourceRepository.getEnvironments();
	assertEquals(1, environments.length);
	assertTrue(resourceRepository.containsEnvironment(randomEnvId));

	// initialize repository with one environments
	envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId2, randomDescription);
	resourceRepository.initialize(envConfiguration);

	// test
	environments = resourceRepository.getEnvironments();
	assertEquals(1, environments.length);
	assertTrue(resourceRepository.containsEnvironment(randomEnvId2));
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));
    }

    /**
     * Repository can be initialized with configuration with single environment.
     */
    @Test
    public void testContainsEnvironment() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
		randomDescription);
	resourceRepository.initialize(envConfiguration);

	// test
	assertTrue(resourceRepository.containsEnvironment(randomEnvId));
    }

    /**
     * Repository can be initialized with configuration with multiple
     * environments.
     */
    @Test
    public void testContainsMultipleEnvironments() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
		randomDescription);
	envConfigMother.addEnvironment(randomEnvId, envConfiguration.getEnvironments().getEnvironment());
	envConfigMother.addEnvironment(randomEnvId2, envConfiguration.getEnvironments().getEnvironment());

	resourceRepository.initialize(envConfiguration);

	// test
	assertTrue(resourceRepository.containsEnvironment(randomEnvId));
	assertTrue(resourceRepository.containsEnvironment(randomEnvId2));
    }

    /**
     * Get resource method throws exception if primary environment is unknown.
     * Primary environment isn't defined. Wild card environment isn't defined.
     */
    @Test(expected = EnvironmentNotFoundException.class)
    public void testGetThrowsExceptionIfPrimaryEnvironmentIsUnknown() throws Exception {
	// initialize
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// get resource
	resourceRepository.get(randomEnvId, randomResourceId);
    }

    /**
     * Get resource method throws exception if id is unknown in primary
     * environment. Primary environment is defined, with no resource. Wild card
     * environment isn't defined.
     */
    @Test(expected = ResourceNotFoundException.class)
    public void testGetThrowsExceptionIfResourceIsUnknownInPrimaryEnvironment() throws Exception {
	// initialize
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();
	resourceRepository.initialize(envConfiguration);

	// get resource
	resourceRepository.get(TestUtilsTestConstants.environmentIdOne, randomResourceId);
    }

    /**
     * Get resource method can look up resource directly in wild card
     * environment. Primary environment is defined, with no resource. Wild card
     * environment is defined, resource is defined.
     */
    @Test
    public void testGetReturnsResourceDirectlyFromQueryIntoTheWildcardEnvironment() throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleResource(WILDCARD_ENVIRONMENT_ID,
		randomResourceId);
	resourceRepository.initialize(envConfiguration);

	// get resource
	ResourceInfo resourceInfo = resourceRepository.get(WILDCARD_ENVIRONMENT_ID, randomResourceId);

	// test
	assertNotNull(resourceInfo);
	assertEquals(randomResourceId, resourceInfo.getId());
	assertNotNull(resourceInfo.getResource());
	assertEquals(randomResourceId, resourceInfo.getResource().getId());

    }

    /**
     * Get resource method can look up resource in primary environment. Primary
     * environment is defined, resource is defined. Wild card environment isn't
     * defined.
     */
    @Test
    public void testGetReturnsResourceFromPrimaryEnvironment() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleResource(randomEnvId,
		randomResourceId);
	resourceRepository.initialize(envConfiguration);

	// test
	assertTrue(resourceRepository.containsEnvironment(randomEnvId));

	// get resource
	ResourceInfo resource = resourceRepository.get(randomEnvId, randomResourceId);

	// test
	assertEquals(randomResourceId, resource.getResource().getId());
    }

    /**
     * Get resource method throws exception if id is unknown in wild card
     * environment. Primary environment isn't defined. Wild card environment is
     * defined, with no resource.
     */
    @Test(expected = EnvironmentNotFoundException.class)
    public void testGetThrowsExceptionIfIdIsUnknownInWildcardEnvironment() throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(WILDCARD_ENVIRONMENT_ID);
	resourceRepository.initialize(envConfiguration);

	// get resource to trigger exception
	resourceRepository.get(randomEnvId, randomResourceId);
    }

    /**
     * Get resource method throws exception if id is unknown in primary and wild
     * card environment. Primary environment is defined, with no resource. Wild
     * card environment is defined, with no resource.
     */
    @Test(expected = ResourceNotFoundException.class)
    public void testGetThrowsExceptionIfIdIsUnknownInPrimaryAndWildcardEnvironment() throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	envConfigMother.addEnvironment(WILDCARD_ENVIRONMENT_ID, envConfiguration.getEnvironments().getEnvironment());
	resourceRepository.initialize(envConfiguration);

	// get resource to trigger exception
	resourceRepository.get(randomEnvId, randomResourceId);
    }

    /**
     * Get resource method can look up resource in primary environment. Primary
     * environment is defined, resource is defined. Wild card environment is
     * defined, with no resource.
     */
    @Test
    public void testGetReturnsResourceFromPrimaryEnvironmentWithEmptyWildCardEnvironment() throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleResource(randomEnvId,
		randomResourceId);
	envConfigMother.addEnvironment(WILDCARD_ENVIRONMENT_ID, envConfiguration.getEnvironments().getEnvironment());
	resourceRepository.initialize(envConfiguration);

	// get resource
	ResourceInfo resource = resourceRepository.get(randomEnvId, randomResourceId);

	// test
	assertEquals(randomResourceId, resource.getResource().getId());
    }

    /**
     * Get resource method can look up resource in wild card environment if the
     * primary environment isn't defined.
     * 
     * Primary environment isn't defined. Wild card environment is defined,
     * resource is defined.
     */
    @Test
    public void testGetReturnsResourceFromWildcardEnvironmentWithUnknownPrimaryEnvironment() throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleResource(WILDCARD_ENVIRONMENT_ID,
		randomResourceId);
	resourceRepository.initialize(envConfiguration);

	// get resource
	ResourceInfo resource = resourceRepository.get(randomEnvId, randomResourceId);

	// test
	assertEquals(randomResourceId, resource.getResource().getId());
    }

    /**
     * Get resource method can look up resource in wild card environment if the
     * resource isn't defined in the primary environment.
     * 
     * Primary environment is defined, with no resource. Wild card environment
     * is defined, resource is defined.
     */
    @Test
    public void testGetReturnsResourceFromWildcardEnvironmentWithEmptyPrimaryEnvironment() throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleResource(WILDCARD_ENVIRONMENT_ID,
		randomResourceId);
	envConfigMother.addEnvironment(randomEnvId, envConfiguration.getEnvironments().getEnvironment());
	resourceRepository.initialize(envConfiguration);

	// get resource
	ResourceInfo resource = resourceRepository.get(randomEnvId, randomResourceId);

	// test
	assertEquals(randomResourceId, resource.getResource().getId());
    }

    /**
     * Get resource method can look up resource in wild card environment if the
     * resource isn't defined in the primary environment.
     * 
     * Primary environment is defined, with no resource. Wild card environment
     * is defined, resource is defined.
     */
    @Test
    public void testGetReturnsResourceFromPrimaryEnvironmentIfResourceIsDefinedInBothPrimaryAndWildcardEnvironment()
	    throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleResource(WILDCARD_ENVIRONMENT_ID,
		randomResourceId);
	Environment primaryEnv = envConfigMother.addEnvironment(randomEnvId,
		envConfiguration.getEnvironments().getEnvironment());
	envConfigMother.addResourceWithNoPropsToEnvironment(primaryEnv, randomResourceId);
	resourceRepository.initialize(envConfiguration);

	// get resource
	ResourceInfo resource = resourceRepository.get(randomEnvId, randomResourceId);

	// test
	assertEquals(randomResourceId, resource.getResource().getId());
    }

    /**
     * Contains resource method returns false if primary environment is unknown.
     * Primary environment isn't defined. Wild card environment isn't defined.
     */
    @Test
    public void testContainsReturnsFalseIfPrimaryEnvironmentIsUnknown() throws Exception {
	// initialize
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Contains resource method throws exception if id is unknown in primary
     * environment. Primary environment is defined, with no resource. Wild card
     * environment isn't defined.
     */
    @Test
    public void testContainsReturnsFalseIfResourceIsUnknownInPrimaryEnvironment() throws Exception {
	// initialize
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Contains resource method can look up resource directly in wild card
     * environment. Primary environment is defined, with no resource. Wild card
     * environment is defined, resource is defined.
     */
    @Test
    public void testContainsReturnsTrueForResourceDirectlyFromQueryIntoTheWildcardEnvironment() throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleResource(WILDCARD_ENVIRONMENT_ID,
		randomResourceId);
	resourceRepository.initialize(envConfiguration);

	// test
	assertTrue(resourceRepository.contains(WILDCARD_ENVIRONMENT_ID, randomResourceId));
    }

    /**
     * Contains resource method can look up resource in primary environment.
     * Primary environment is defined, resource is defined. Wild card
     * environment isn't defined.
     */
    @Test
    public void testContainsReturnsResourceFromPrimaryEnvironment() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleResource(randomEnvId,
		randomResourceId);
	resourceRepository.initialize(envConfiguration);

	// test
	assertTrue(resourceRepository.containsEnvironment(randomEnvId));
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Contains resource method returns false if id is unknown in wild card
     * environment. Primary environment isn't defined. Wild card environment is
     * defined, with no resource.
     */
    @Test
    public void testContainsReturnFalseIfIdIsUnknownInWildcardEnvironment() throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(WILDCARD_ENVIRONMENT_ID);
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Contains resource method returns false if id is unknown in primary and
     * wild card environment. Primary environment is defined, with no resource.
     * Wild card environment is defined, with no resource.
     */
    @Test
    public void testContainsReturnFalseIfIdIsUnknownInPrimaryAndWildcardEnvironment() throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	envConfigMother.addEnvironment(WILDCARD_ENVIRONMENT_ID, envConfiguration.getEnvironments().getEnvironment());
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Contains resource method can look up resource in primary environment.
     * Primary environment is defined, resource is defined. Wild card
     * environment is defined, with no resource.
     */
    @Test
    public void testContainsReturnsTrueWithResourceFromPrimaryEnvironmentWithEmptyWildCardEnvironment()
	    throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleResource(randomEnvId,
		randomResourceId);
	envConfigMother.addEnvironment(WILDCARD_ENVIRONMENT_ID, envConfiguration.getEnvironments().getEnvironment());
	resourceRepository.initialize(envConfiguration);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Contains resource method can look up resource in wild card environment if
     * the primary environment isn't defined.
     * 
     * Primary environment isn't defined. Wild card environment is defined,
     * resource is defined.
     */
    @Test
    public void testContainsReturnsTrueWithResourceFromWildcardEnvironmentWithUnknownPrimaryEnvironment()
	    throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleResource(WILDCARD_ENVIRONMENT_ID,
		randomResourceId);
	resourceRepository.initialize(envConfiguration);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Contains resource method can look up resource in wild card environment if
     * the resource isn't defined in the primary environment.
     * 
     * Primary environment is defined, with no resource. Wild card environment
     * is defined, resource is defined.
     */
    @Test
    public void testContainsReturnsTrueWithResourceFromWildcardEnvironmentWithEmptyPrimaryEnvironment()
	    throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleResource(WILDCARD_ENVIRONMENT_ID,
		randomResourceId);
	envConfigMother.addEnvironment(randomEnvId, envConfiguration.getEnvironments().getEnvironment());
	resourceRepository.initialize(envConfiguration);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Contains resource method can look up resource in wild card environment if
     * the resource isn't defined in the primary environment.
     * 
     * Primary environment is defined, with no resource. Wild card environment
     * is defined, resource is defined.
     */
    @Test
    public void testContainsReturnsTrueWithResourceFromPrimaryEnvironmentIfResourceIsDefinedInBothPrimaryAndWildcardEnvironment()
	    throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleResource(WILDCARD_ENVIRONMENT_ID,
		randomResourceId);
	Environment primaryEnv = envConfigMother.addEnvironment(randomEnvId,
		envConfiguration.getEnvironments().getEnvironment());
	envConfigMother.addResourceWithNoPropsToEnvironment(primaryEnv, randomResourceId);
	resourceRepository.initialize(envConfiguration);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Repository can create resource with: plugin id references existing plugin
     * on class path, no properties, null credential ID reference.
     */
    @Test
    public void testCreateResourceWithNoPropertiesAndNullCredentialIdRef() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);

	// create resource
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Repository can create resource with: plugin id references existing plugin
     * on class path, no properties, empty credential ID reference.
     */
    @Test
    public void testCreateResourceWithNoPropertiesAndEmptyCredentialIdRef() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);

	// create resource
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Repository can create resource with: plugin id references existing plugin
     * on class path, no properties, empty credential ID reference.
     */
    @Ignore
    @Test(expected = PluginIdNotFoundException.class)
    public void test_FIX_ME_FailsToCreateResourceWithFictionalPluginId() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);

	// create resource
	resourceRepository.create(randomEnvId, randomResourceId, randomPluginId, NULL_CREDENTIAL_ID_REF);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Fails to create resource if environment doesn't exists.
     */
    @Test(expected = EnvironmentNotFoundException.class)
    public void testCreateResourceFailsIfEnvironmentDoesntExist() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// create resource
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
    }

    /**
     * Fails to create resource twice.
     */
    @Test(expected = ResourceAlreadyExistsException.class)
    public void testFailsToCreateResourceTwice() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);

	// create resource
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
    }

    /**
     * Update resource
     */
    @Test
    public void testUpdateResourceContainsUpdatedValues() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);
	resourceRepository.createEnvironment(randomEnvId, randomDescription);
	ResourceInfo resourceInfo = resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID,
		NULL_CREDENTIAL_ID_REF);

	// update
	ResourceInfo updatedInfo = resourceRepository.update(resourceInfo, randomEnvId, randomResourceId2,
		AdministrationProviderTestPluginImpl.PLUGIN_ID, randomCredentialIdRef);

	// test
	assertEquals(randomResourceId2, updatedInfo.getId());
	assertEquals(AdministrationProviderTestPluginImpl.PLUGIN_ID, updatedInfo.getPluginId());
	assertEquals(randomCredentialIdRef, updatedInfo.getCredentialIdRef());
    }

    /**
     * Update resource
     */
    @Test
    public void testUpdateResourceCanUpdateTwice() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);
	resourceRepository.createEnvironment(randomEnvId, randomDescription);
	ResourceInfo resourceInfo = resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID,
		NULL_CREDENTIAL_ID_REF);

	// update
	ResourceInfo updatedInfo = resourceRepository.update(resourceInfo, randomEnvId, randomResourceId2,
		AdministrationProviderTestPluginImpl.PLUGIN_ID, randomCredentialIdRef);

	updatedInfo = resourceRepository.update(updatedInfo, randomEnvId, randomResourceId2,
		AdministrationProviderTestPluginImpl.PLUGIN_ID, randomCredentialIdRef);

	// test
	assertEquals(randomResourceId2, updatedInfo.getId());
	assertEquals(AdministrationProviderTestPluginImpl.PLUGIN_ID, updatedInfo.getPluginId());
	assertEquals(randomCredentialIdRef, updatedInfo.getCredentialIdRef());
    }

    /**
     * Repository can delete environment.
     */
    @Test
    public void testDeleteResource() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);
	resourceRepository.createEnvironment(randomEnvId, null);
	resourceRepository.create(randomEnvId, randomResourceId, randomPluginId, "");

	// delete resource
	resourceRepository.delete(randomEnvId, randomResourceId);

	// test
	assertFalse(resourceRepository.contains(randomEnvId, randomResourceId));
    }

    /**
     * Repository can delete environment in wild card environment.
     */
    @Test
    public void testDeleteResourceInWildcardEnvironment() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);
	resourceRepository.createEnvironment(WILDCARD_ENVIRONMENT_ID, null);
	resourceRepository.create(WILDCARD_ENVIRONMENT_ID, randomResourceId, randomPluginId, "");

	// delete resource
	resourceRepository.delete(WILDCARD_ENVIRONMENT_ID, randomResourceId);

	// test
	assertFalse(resourceRepository.contains(WILDCARD_ENVIRONMENT_ID, randomResourceId));
    }

    /**
     * Delete resource method throws exception if environment is unknown.
     * Environment isn't defined.
     */
    @Test(expected = EnvironmentNotFoundException.class)
    public void testDeleteResourceThrowsExceptionIfEnvironmentIsUnknown() throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// delete resource
	resourceRepository.delete(randomEnvId, randomResourceId);
    }

    /**
     * Delete resource method throws exception if resource is unknown.
     * Environment is defined, but resource isn't.
     */
    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteResourceThrowsExceptionIfResourceIsUnknown() throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// delete resource
	resourceRepository.delete(randomEnvId, randomResourceId);
    }

    /**
     * Delete credential twice fails.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteResourceTwiceFails() throws Exception {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);
	resourceRepository.createEnvironment(randomEnvId, null);
	resourceRepository.create(randomEnvId, randomResourceId, randomPluginId, "");

	// delete resource
	resourceRepository.delete(randomEnvId, randomResourceId);
	resourceRepository.delete(randomEnvId, randomResourceId);
    }

    /**
     * Repository can create environment with null description.
     */
    @Test
    public void testCreateEnvironmentwithNullDescription() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));

	// create environment
	resourceRepository.createEnvironment(randomEnvId, null);

	// test
	assertTrue(resourceRepository.containsEnvironment(randomEnvId));
    }

    /**
     * Repository can create environment with empty description.
     */
    @Test
    public void testCreateEnvironmentwithEmptyDescription() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));

	// create environment
	resourceRepository.createEnvironment(randomEnvId, "");

	// test
	assertTrue(resourceRepository.containsEnvironment(randomEnvId));
    }

    /**
     * Repository can create environment.
     */
    @Test
    public void testCreateEnvironment() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// test
	assertTrue(resourceRepository.containsEnvironment(randomEnvId));
    }

    /**
     * Create environment return environment info object.
     */
    @Test
    public void testCreateEnvironmentReturnsEnvironmentInfo() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));

	// create environment
	EnvironmentInfo environmentInfo = resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// test
	assertNotNull(environmentInfo);
	assertEquals(randomEnvId, environmentInfo.getId());
	assertEquals(randomDescription, environmentInfo.getDescription());
    }

    /**
     * Create environment return environment info object, environment info
     * doesn't contains any resources.
     */
    @Test
    public void testCreateEnvironmentReturnsEnvironmentInfoWithNoResources() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));

	// create environment
	EnvironmentInfo environmentInfo = resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// test
	assertNotNull(environmentInfo);
	assertNotNull(environmentInfo.getResources());
	assertEquals(0, environmentInfo.getResources().length);
    }

    /**
     * Repository fails to create same environment twice.
     */
    @Test(expected = EnvironmentAlreadyExistsException.class)
    public void testFailsToCreateIdenticalEnvironmentTwice() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);
	resourceRepository.createEnvironment(randomEnvId, randomDescription);
    }

    /**
     * Delete environment.
     */
    @Test
    public void testDeleteEnvironment() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// test
	assertTrue(resourceRepository.containsEnvironment(randomEnvId));

	// delete
	resourceRepository.deleteEnvironment(randomEnvId);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));
    }

    /**
     * Delete environment rejects null environment.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteEnvironmentRejectsNullEnvironmentId() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// delete
	resourceRepository.deleteEnvironment(null);
    }

    /**
     * Delete environment rejects empty environment.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteEnvironmentRejectsEmptyEnvironmentId() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// delete
	resourceRepository.deleteEnvironment("");
    }

    /**
     * Delete environment rejects unknown environment.
     */
    @Test(expected = EnvironmentNotFoundException.class)
    public void testDeleteEnvironmentRejectsUnknownEnvironmentId() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// delete
	resourceRepository.deleteEnvironment(randomEnvId2);
    }

    /**
     * Delete environment twice fails.
     */
    @Test(expected = EnvironmentNotFoundException.class)
    public void testDeleteTwiceFails() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// delete
	resourceRepository.deleteEnvironment(randomEnvId);
	resourceRepository.deleteEnvironment(randomEnvId);
    }

    /**
     * Repository can create update environment ID.
     */
    @Test
    public void testUpdateEnvironmentId() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	EnvironmentInfo environmentInfo = resourceRepository.getEnvironment(randomEnvId);
	resourceRepository.updateEnvironment(environmentInfo, randomEnvId2, randomDescription);

	// test
	assertTrue(resourceRepository.containsEnvironment(randomEnvId2));
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));
    }

    /**
     * Repository can create update description to empty.
     */
    @Test
    public void testUpdateEnvironmentToEmptyDescription() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	EnvironmentInfo environmentInfo = resourceRepository.getEnvironment(randomEnvId);
	resourceRepository.updateEnvironment(environmentInfo, randomEnvId, "");

	// test
	assertTrue(resourceRepository.containsEnvironment(randomEnvId));
    }

    /**
     * Repository can create update description.
     */
    @Test
    public void testUpdateEnvironmentDescription() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	EnvironmentInfo environmentInfo = resourceRepository.getEnvironment(randomEnvId);
	resourceRepository.updateEnvironment(environmentInfo, randomEnvId, randomEnvId2);

	// test
	assertTrue(resourceRepository.containsEnvironment(randomEnvId));
	assertEquals(randomEnvId2, resourceRepository.getEnvironment(randomEnvId).getDescription());

    }

    /**
     * Get environment.
     */
    @Test
    public void testGetEnvironment() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// get
	EnvironmentInfo environmentInfo = resourceRepository.getEnvironment(randomEnvId);

	// test
	assertNotNull(environmentInfo);
	assertEquals(environmentInfo.getId(), randomEnvId);
    }

    /**
     * Get environment rejects null environment.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetEnvironmentRejectsNullEnvironmentId() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// get
	resourceRepository.getEnvironment(null);
    }

    /**
     * Get environment rejects empty environment.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetEnvironmentRejectsEmptyEnvironmentId() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// get
	resourceRepository.getEnvironment("");
    }

    /**
     * Get environment rejects unknown environment.
     */
    @Test(expected = EnvironmentNotFoundException.class)
    public void testGetEnvironmentRejectsUnknownEnvironmentId() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);

	// test
	assertFalse(resourceRepository.containsEnvironment(randomEnvId));

	// create environment
	resourceRepository.createEnvironment(randomEnvId, randomDescription);

	// get
	resourceRepository.getEnvironment(randomEnvId2);
    }

    /**
     * Get resources method throws exception if environment is null. Primary
     * environment is defined, with no resource. Wild card environment isn't
     * defined.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesThrowsExceptionIfEnvironmentIsUndefined() throws Exception {
	// initialize
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();
	resourceRepository.initialize(envConfiguration);

	// get resource
	resourceRepository.getResources(null);
    }

    /**
     * Get resources method throws exception if environment is unknown. Primary
     * environment is defined, with no resource. Wild card environment isn't
     * defined.
     */
    @Test(expected = EnvironmentNotFoundException.class)
    public void testGetResourcesThrowsExceptionIfEnvironmentIsUnknown() throws Exception {
	// initialize
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();
	resourceRepository.initialize(envConfiguration);

	// get resource
	ResourceInfo[] resources = resourceRepository.getResources(randomEnvId);

	// test
	assertNotNull(resources);

    }

    /**
     * Repository can create resource property.
     */
    @Test
    public void testCreateResourceProperty() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, randomKey, randomValue);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
	assertTrue(resourceRepository.get(randomEnvId, randomResourceId).containsProperty(randomKey));
	assertEquals(randomValue,
		resourceRepository.get(randomEnvId, randomResourceId).getPropertyValue(randomKey, null));
	assertEquals(randomValue,
		resourceRepository.get(randomEnvId, randomResourceId).getProperty(randomKey).getValue());
    }

    /**
     * Repository can create resource property on resource defined in wild card
     * environment. Property is created through the primary environment.
     */
    @Test
    public void testCreateResourcePropertyOnResourceInWildcardEnvironment() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);
	resourceRepository.createEnvironment(WILDCARD_ENVIRONMENT_ID, null);
	resourceRepository.create(WILDCARD_ENVIRONMENT_ID, randomResourceId, randomPluginId, "");
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, randomKey, randomValue);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
	assertTrue(resourceRepository.get(randomEnvId, randomResourceId).containsProperty(randomKey));
	assertEquals(randomValue,
		resourceRepository.get(randomEnvId, randomResourceId).getPropertyValue(randomKey, null));
	assertEquals(randomValue,
		resourceRepository.get(randomEnvId, randomResourceId).getProperty(randomKey).getValue());

	assertTrue(resourceRepository.contains(WILDCARD_ENVIRONMENT_ID, randomResourceId));
	assertTrue(resourceRepository.get(WILDCARD_ENVIRONMENT_ID, randomResourceId).containsProperty(randomKey));
	assertEquals(randomValue,
		resourceRepository.get(WILDCARD_ENVIRONMENT_ID, randomResourceId).getPropertyValue(randomKey, null));
	assertEquals(randomValue,
		resourceRepository.get(WILDCARD_ENVIRONMENT_ID, randomResourceId).getProperty(randomKey).getValue());
    }

    /**
     * Repository can create resource property on resource defined in wild card
     * environment. Property is created through the wild card environment.
     */
    @Test
    public void testCreateResourcePropertyOnResourceInWildcardEnvironment2() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);
	resourceRepository.createEnvironment(WILDCARD_ENVIRONMENT_ID, null);
	resourceRepository.create(WILDCARD_ENVIRONMENT_ID, randomResourceId, randomPluginId, "");
	resourceRepository.createResourceProperty(WILDCARD_ENVIRONMENT_ID, randomResourceId, randomKey, randomValue);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
	assertTrue(resourceRepository.get(randomEnvId, randomResourceId).containsProperty(randomKey));
	assertEquals(randomValue,
		resourceRepository.get(randomEnvId, randomResourceId).getPropertyValue(randomKey, null));
	assertEquals(randomValue,
		resourceRepository.get(randomEnvId, randomResourceId).getProperty(randomKey).getValue());

	assertTrue(resourceRepository.contains(WILDCARD_ENVIRONMENT_ID, randomResourceId));
	assertTrue(resourceRepository.get(WILDCARD_ENVIRONMENT_ID, randomResourceId).containsProperty(randomKey));
	assertEquals(randomValue,
		resourceRepository.get(WILDCARD_ENVIRONMENT_ID, randomResourceId).getPropertyValue(randomKey, null));
	assertEquals(randomValue,
		resourceRepository.get(WILDCARD_ENVIRONMENT_ID, randomResourceId).getProperty(randomKey).getValue());
    }

    /**
     * Fails to create resource property twice.
     */
    @Test(expected = PropertyAlreadyExistsException.class)
    public void testFailsToCreateResourcePropertyTwice() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, randomKey, randomValue);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, randomKey, randomValue);
    }

    /**
     * Fails to create resource property with undefined resource.
     */
    @Test(expected = ResourceNotFoundException.class)
    public void testFailsToCreateResourcePropertyWithUndefinedResource() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, randomKey, randomValue);
    }

    /**
     * Fails to create resource property with undefined environment.
     */
    @Test(expected = EnvironmentNotFoundException.class)
    public void testFailsToCreateResourcePropertyWithUndefinedEnvironment() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, randomKey, randomValue);
    }

    /**
     * Fails to create resource property with empty key.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFailsToCreateResourcePropertyWithEmptyKey() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, "", randomValue);
    }

    /**
     * Fails to create resource property with null key.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFailsToCreateResourcePropertyWithUndefinedKey() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, null, randomValue);
    }

    /**
     * Fails to create resource property with empty value.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFailsToCreateResourcePropertyWithEmptyValue() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, randomKey, "");
    }

    /**
     * Fails to create resource property with null value.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFailsToCreateResourcePropertyWithUndefinedValue() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, randomKey, null);
    }

    /**
     * Repository can delete resource property.
     */
    @Test
    public void testDeleteResourceProperty() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, randomKey, randomValue);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
	assertTrue(resourceRepository.get(randomEnvId, randomResourceId).containsProperty(randomKey));

	// delete
	resourceRepository.deleteResourceProperty(randomEnvId, randomResourceId, randomKey);

	// test
	assertFalse(resourceRepository.get(randomEnvId, randomResourceId).containsProperty(randomKey));
    }

    /**
     * Repository can delete resource property in wild card environment.
     * Property is deleted through the wild card environment.
     */
    @Test
    public void testDeleteResourcePropertyInWildcardEnvironment() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);
	resourceRepository.createEnvironment(WILDCARD_ENVIRONMENT_ID, null);
	resourceRepository.create(WILDCARD_ENVIRONMENT_ID, randomResourceId, randomPluginId, "");
	resourceRepository.createResourceProperty(WILDCARD_ENVIRONMENT_ID, randomResourceId, randomKey, randomValue);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
	assertTrue(resourceRepository.get(randomEnvId, randomResourceId).containsProperty(randomKey));
	assertTrue(resourceRepository.contains(WILDCARD_ENVIRONMENT_ID, randomResourceId));
	assertTrue(resourceRepository.get(WILDCARD_ENVIRONMENT_ID, randomResourceId).containsProperty(randomKey));

	// delete
	resourceRepository.deleteResourceProperty(WILDCARD_ENVIRONMENT_ID, randomResourceId, randomKey);

	// test
	assertFalse(resourceRepository.get(randomEnvId, randomResourceId).containsProperty(randomKey));
    }

    /**
     * Repository can delete resource property in wild card environment.
     * Property is deleted through the primary environment.
     */
    @Test
    public void testDeleteResourcePropertyInWildcardEnvironment2() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);
	resourceRepository.createEnvironment(WILDCARD_ENVIRONMENT_ID, null);
	resourceRepository.create(WILDCARD_ENVIRONMENT_ID, randomResourceId, randomPluginId, "");
	resourceRepository.createResourceProperty(WILDCARD_ENVIRONMENT_ID, randomResourceId, randomKey, randomValue);

	// test
	assertTrue(resourceRepository.contains(randomEnvId, randomResourceId));
	assertTrue(resourceRepository.get(randomEnvId, randomResourceId).containsProperty(randomKey));
	assertTrue(resourceRepository.contains(WILDCARD_ENVIRONMENT_ID, randomResourceId));
	assertTrue(resourceRepository.get(WILDCARD_ENVIRONMENT_ID, randomResourceId).containsProperty(randomKey));

	// delete
	resourceRepository.deleteResourceProperty(randomEnvId, randomResourceId, randomKey);

	// test
	assertFalse(resourceRepository.get(randomEnvId, randomResourceId).containsProperty(randomKey));
    }

    /**
     * Fails to delete resource property twice.
     */
    @Test(expected = PropertyNotFoundException.class)
    public void testFailsToDeleteResourcePropertyTwice() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, randomKey, randomValue);
	resourceRepository.deleteResourceProperty(randomEnvId, randomResourceId, randomKey);
	resourceRepository.deleteResourceProperty(randomEnvId, randomResourceId, randomKey);
    }

    /**
     * Fails to delete resource property with undefined resource.
     */
    @Test(expected = ResourceNotFoundException.class)
    public void testFailsToDeleteResourcePropertyWithUndefinedResource() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);
	resourceRepository.deleteResourceProperty(randomEnvId, randomResourceId, randomKey);
    }

    /**
     * Fails to delete resource property with undefined environment.
     */
    @Test(expected = EnvironmentNotFoundException.class)
    public void testFailsToDeleteResourcePropertyWithUndefinedEnvironment() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	resourceRepository.initialize(envConfiguration);
	resourceRepository.deleteResourceProperty(randomEnvId, randomResourceId, randomKey);
    }

    /**
     * Fails to delete resource property with empty key.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFailsToDeleteResourcePropertyWithEmptyKey() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, randomKey, randomValue);
	resourceRepository.deleteResourceProperty(randomEnvId, randomResourceId, "");
    }

    /**
     * Fails to delete resource property with null key.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFailsToDeleteResourcePropertyWithUndefinedKey() {
	// initialize repository
	Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
	resourceRepository.initialize(envConfiguration);
	resourceRepository.create(randomEnvId, randomResourceId, PLUGIN_ID, NULL_CREDENTIAL_ID_REF);
	resourceRepository.createResourceProperty(randomEnvId, randomResourceId, randomKey, randomValue);
	resourceRepository.deleteResourceProperty(randomEnvId, randomResourceId, null);
    }

}
