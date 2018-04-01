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
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Unit test of the class {@link ResourcesRepositoryImpl }.
 */
public class ResourceRespositoryTest {
	/**
	 * Empty enviroments.
	 */
	final static EnvironmentInfo[] EMPTY_ENVIRONMENTS = new EnvironmentInfo[0];

	/**
	 * Null credential ID reference.
	 */
	final static String NULL_CREDENTIAL_ID_REF = null;

	/**
	 * Object under test.
	 */
	ResourceRepository repository;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * Mock resource configuration marshaller.
	 */
	ResourceConfigurationMarshaller resourceConfigurationMarshaller;

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
	 * Random plugin ID.
	 */
	String randomPluginId;

	@Before
	public void setUp() throws Exception {
		// initialize random fields
		randomEnvId = RandomStringUtils.randomAlphabetic(10);
		randomEnvId2 = RandomStringUtils.randomAlphabetic(10);
		randomDescription = RandomStringUtils.randomAlphabetic(10);
		randomResourceId = RandomStringUtils.randomAlphabetic(10);
		randomResourceId2 = RandomStringUtils.randomAlphabetic(10);
		randomPluginId = RandomStringUtils.randomAlphabetic(10);

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// create repository
		repository = new ResourceRepositoryImpl();

		// create marshaller and inject
		resourceConfigurationMarshaller = createMock(ResourceConfigurationMarshaller.class);
		ReflectionTestUtils.setField(repository, "resourceConfigurationMarshaller", resourceConfigurationMarshaller);

		// create mock provider an inject
		messageProvider = createMock(MessageProvider.class);
		ReflectionTestUtils.setField(repository, "messageProvider", messageProvider, MessageProvider.class);

		// complete mock source initialization
		IAnswer<String> answer = new MessageProviderAnswerImpl();
		expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
		expectLastCall().andAnswer(answer).anyTimes();
		expect(messageProvider.getMessage((String) EasyMock.isA(String.class),
				(Object[]) EasyMock.isA(Object[].class)));
		expectLastCall().andAnswer(answer).anyTimes();
		replay(messageProvider);
	}

	@After
	public void tearDown() throws Exception {
		envConfigMother = null;
		repository = null;
	}

	/**
	 * Constructor test, i.e. that repository can be created.
	 */
	@Test
	public void testCanCreateInstance() {
		// test
		assertNotNull(repository);
	}

	/**
	 * Initially repository doesn't contain named environment.
	 */
	@Test
	public void testInitiallyRepositoryDoesntContainNamedEnvironment() {
		// complete mock setup
		replay(resourceConfigurationMarshaller);

		assertFalse(repository.containsEnvironment(randomEnvId));

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Initially repository doesn't contain named resource.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testInitiallyRepositoryDoesntContainNamedResource() {
		// complete mock setup
		replay(resourceConfigurationMarshaller);

		repository.get(randomEnvId, randomResourceId);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Repository can be initialized with configuration with no environments.
	 */
	@Test
	public void testCanInitializeWithNoEnvironments() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		repository.initialize(envConfiguration);

		// verify mocks
		verify(resourceConfigurationMarshaller);
		verify(configurationInfo);
	}

	/**
	 * Contains environment method rejects undefined environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testContainsEnvironmentRejectsUndefinedId() {
		// test
		repository.containsEnvironment(null);
	}

	/**
	 * Contains environment method rejects empty environment id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testContainsEnvironmentRejectsEmptyId() {
		// setup arguments
		String id = "";

		// add environment
		repository.containsEnvironment(id);
	}

	/**
	 * Get Resource method rejects undefined environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetRejectsUndefinedEnvironment() throws Exception {
		// get resource
		repository.get(null, randomEnvId);
	}

	/**
	 * Get Resource method rejects empty environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetRejectsEmptyEnvironment() throws Exception {
		// get resource
		repository.get("", randomResourceId);
	}

	/**
	 * Get Resource method rejects undefined id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetRejectsUndefinedId() throws Exception {
		// get resource
		repository.get(randomEnvId, null);

	}

	/**
	 * Get Resource method rejects empty id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetRejectsEmptyId() throws Exception {
		// get resource
		repository.get(randomEnvId, "");
	}

	/**
	 * Contain resource fails if environment is null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testContainResourceFailsWithUndefinedEnvironment() throws Exception {
		// create mock
		EnvironmentInfo environmentInfo = createMock(EnvironmentInfo.class);
		expect(environmentInfo.getId()).andReturn(randomEnvId);
		replay(environmentInfo);

		repository.contains(null, randomResourceId);
	}

	/**
	 * Contain resource fails if environment is empty.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testContainResourceFailsWithEmptyEnvironment() throws Exception {
		// create mock
		EnvironmentInfo environmentInfo = createMock(EnvironmentInfo.class);
		expect(environmentInfo.getId()).andReturn(randomEnvId);
		replay(environmentInfo);

		repository.contains("", randomResourceId);
	}

	/**
	 * Contain resource fails if environment is unknown.
	 */
	@Test
	public void testContainResourceFailsWithUnknownEnvironment() throws Exception {
		assertFalse(repository.contains(randomEnvId, randomResourceId));
	}

	/**
	 * Contain resource fails if resource is null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testContainResourceFailsWithUndefinedResource() throws Exception {
		repository.contains(randomEnvId, null);
	}

	/**
	 * Contain resource fails if resource is empty.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testContainResourceFailsWithEmptyResource() throws Exception {
		repository.contains(randomEnvId, "");
	}

	/**
	 * Contain resource fails if resource is unknown.
	 */
	@Test
	public void testContainResourceFailsWithUnknownResource() throws Exception {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock
		EnvironmentInfo environmentInfo = createMock(EnvironmentInfo.class);
		expect(environmentInfo.containsResource(randomResourceId)).andReturn(false);
		replay(environmentInfo);

		// create mock
		EnvironmentInfo environmentInfo2 = createMock(EnvironmentInfo.class);
		expect(environmentInfo2.containsResource(randomResourceId)).andReturn(false);
		replay(environmentInfo2);

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		expect(configurationInfo.containsEnvironment(randomEnvId)).andReturn(true);
		expect(configurationInfo.getEnvironment(randomEnvId)).andReturn(environmentInfo);
		expect(configurationInfo.containsEnvironment(WILDCARD_ENVIRONMENT_ID)).andReturn(true);
		expect(configurationInfo.getEnvironment(WILDCARD_ENVIRONMENT_ID)).andReturn(environmentInfo2);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		repository.initialize(envConfiguration);

		// test
		repository.contains(randomEnvId, randomResourceId);

		// verify mocks
		verify(resourceConfigurationMarshaller);
		verify(configurationInfo);
		verify(environmentInfo);
		verify(environmentInfo2);
	}

	/**
	 * Repository fails to create resource with null environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateResourceWithUndefinedEnvironment() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// create environment
		repository.create(null, randomResourceId, randomPluginId, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Repository fails to create resource with empty environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateResourceWithEmptyEnvironment() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// create environment
		repository.create("", randomResourceId, randomPluginId, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Repository fails to create resource with unknown environment.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testFailsToCreateResourceWithUnknownEnvironment() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		expect(configurationInfo.containsEnvironment(randomEnvId)).andReturn(false);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// create environment
		repository.create(randomEnvId, randomResourceId, randomPluginId, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Repository fails to create resource with null resource id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateResourceWithUndefinedResource() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// create environment
		repository.create(randomEnvId, null, randomPluginId, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Repository fails to create resource with empty resource id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateResourceWithEmptyResource() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// create environment
		repository.create(randomEnvId, "", randomPluginId, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Repository fails to create resource with undefined plugin id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateResourceWithUndefinedPluginId() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// create environment
		repository.create(randomEnvId, randomResourceId, null, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Repository fails to create resource with empty plugin id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateResourceWithEmptyPluginId() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// create environment
		repository.create(randomEnvId, randomResourceId, "", NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Repository fails to update resource with null resource info.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToUpdateResourceWithUndefinedResourceInfo() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// update
		repository.update(null, randomEnvId, randomResourceId, randomPluginId, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Repository fails to update resource with null environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToUpdateResourceWithUndefinedEnvironment() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// create mock resource info
		ResourceInfo resourceInfo = createMock(ResourceInfo.class);
		replay(resourceInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// update
		repository.update(resourceInfo, null, randomResourceId, randomPluginId, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
		verify(resourceInfo);
	}

	/**
	 * Repository fails to update resource with empty environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToUpdateResourceWithEmptyEnvironment() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// create mock resource info
		ResourceInfo resourceInfo = createMock(ResourceInfo.class);
		replay(resourceInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// update
		repository.update(resourceInfo, "", randomResourceId, randomPluginId, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
		verify(resourceInfo);
	}

	/**
	 * Repository fails to update resource with unknown environment.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testFailsToUpdateResourceWithUnknownEnvironment() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		expect(configurationInfo.containsEnvironment(randomEnvId)).andReturn(false).times(2);
		expect(configurationInfo.containsEnvironment(WILDCARD_ENVIRONMENT_ID)).andReturn(false);
		replay(configurationInfo);

		// create mock resource info
		ResourceInfo resourceInfo = createMock(ResourceInfo.class);
		expect(resourceInfo.getId()).andReturn(randomResourceId); // no update
		// of the
		// resource id
		// in the this
		// test
		replay(resourceInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// update
		repository.update(resourceInfo, randomEnvId, randomResourceId, randomPluginId, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
		verify(resourceInfo);
	}

	/**
	 * Repository fails to update resource with unknown environment.
	 * 
	 * In this variation of the test, the resource ID and the ID defined in the
	 * resource info are different, e.g. the resource ID is updated.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testFailsToUpdateResourceWithUnknownEnvironment2() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		expect(configurationInfo.containsEnvironment(randomEnvId)).andReturn(false).times(2);
		expect(configurationInfo.containsEnvironment(WILDCARD_ENVIRONMENT_ID)).andReturn(false);
		replay(configurationInfo);

		// create mock resource info
		ResourceInfo resourceInfo = createMock(ResourceInfo.class);
		expect(resourceInfo.getId()).andReturn(randomResourceId2);
		replay(resourceInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// update
		repository.update(resourceInfo, randomEnvId, randomResourceId, randomPluginId, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
		verify(resourceInfo);
	}

	/**
	 * Repository fails to update resource with null resource id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToUpdateResourceWithUndefinedResource() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// create mock resource info
		ResourceInfo resourceInfo = createMock(ResourceInfo.class);
		replay(resourceInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// update
		repository.update(resourceInfo, randomEnvId, null, randomPluginId, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
		verify(resourceInfo);
	}

	/**
	 * Repository fails to update resource with empty resource id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToUpdateResourceWithEmptyResource() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// create mock resource info
		ResourceInfo resourceInfo = createMock(ResourceInfo.class);
		replay(resourceInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// update
		repository.update(resourceInfo, randomEnvId, "", randomPluginId, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
		verify(resourceInfo);
	}

	/**
	 * Repository fails to update resource with undefined plugin id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToUpdateResourceWithUndefinedPluginId() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// create mock resource info
		ResourceInfo resourceInfo = createMock(ResourceInfo.class);
		replay(resourceInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// update
		repository.update(resourceInfo, randomEnvId, randomResourceId, null, NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
		verify(resourceInfo);
	}

	/**
	 * Repository fails to update resource with empty plugin id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToUpdateResourceWithEmptyPluginId() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// create mock resource info
		ResourceInfo resourceInfo = createMock(ResourceInfo.class);
		replay(resourceInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// update
		repository.update(resourceInfo, randomEnvId, randomResourceId, "", NULL_CREDENTIAL_ID_REF);

		// verify mocks
		verify(resourceConfigurationMarshaller);
		verify(resourceInfo);
	}

	/**
	 * Repository fails to delete resource with null environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToDeleteResourceWithUndefinedEnvironment() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// delete environment
		repository.delete(null, randomResourceId);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Repository fails to delete resource with empty environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToDeleteResourceWithEmptyEnvironment() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// delete environment
		repository.delete("", randomResourceId);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Repository fails to delete resource with unknown environment.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testFailsToDeleteResourceWithUnknownEnvironment() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		expect(configurationInfo.containsEnvironment(randomEnvId)).andReturn(false);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// delete environment
		repository.delete(randomEnvId, randomResourceId);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Repository fails to delete resource with null resource id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToDeleteResourceWithUndefinedResource() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock
		EnvironmentInfo environmentInfo = createMock(EnvironmentInfo.class);
		expect(environmentInfo.containsResource(randomResourceId)).andReturn(false);
		replay(environmentInfo);

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		expect(configurationInfo.containsEnvironment(randomEnvId)).andReturn(true);
		expect(configurationInfo.getEnvironment(randomEnvId)).andReturn(environmentInfo);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// delete environment
		repository.delete(randomEnvId, null);

		// verify mocks
		verify(resourceConfigurationMarshaller);
		verify(environmentInfo);
	}

	/**
	 * Repository fails to delete resource with empty resource id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToDeleteResourceWithEmptyResource() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock
		EnvironmentInfo environmentInfo = createMock(EnvironmentInfo.class);
		expect(environmentInfo.containsResource(randomResourceId)).andReturn(false);
		replay(environmentInfo);

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		expect(configurationInfo.containsEnvironment(randomEnvId)).andReturn(true);
		expect(configurationInfo.getEnvironment(randomEnvId)).andReturn(environmentInfo);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// delete environment
		repository.delete(randomEnvId, "");

		// verify mocks
		verify(resourceConfigurationMarshaller);
		verify(environmentInfo);
	}

	/**
	 * Repository fails to create environment with null id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateEnvironmentWithNullId() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create mock configuration info
		ConfigurationInfo configurationInfo = createMock(ConfigurationInfo.class);
		expect(configurationInfo.getEnvironments()).andReturn(EMPTY_ENVIRONMENTS);
		replay(configurationInfo);

		// complete mock setup
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		expect(resourceConfigurationMarshaller.map(envConfiguration)).andReturn(configurationInfo);
		replay(resourceConfigurationMarshaller);

		// initialize repository
		repository.initialize(envConfiguration);

		// create environment
		repository.createEnvironment(null, randomDescription);

		// verify mocks
		verify(resourceConfigurationMarshaller);
	}

	/**
	 * Update environment rejects null environment info.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateEnvironmentRejectsUndefinedId() throws Exception {
		repository.updateEnvironment(null, randomEnvId, randomDescription);
	}

	/**
	 * Update environment rejects null environment ID.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateEnvironmentRejectsundefinedUpdatedId() throws Exception {
		// create mock
		EnvironmentInfo environmentInfo = createMock(EnvironmentInfo.class);
		replay(environmentInfo);

		repository.updateEnvironment(environmentInfo, null, randomDescription);
	}

	/**
	 * Update environment rejects empty environment ID.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateEnvironmentRejectsEmptyUpdatedId() throws Exception {
		// create mock
		EnvironmentInfo environmentInfo = createMock(EnvironmentInfo.class);
		replay(environmentInfo);

		repository.updateEnvironment(environmentInfo, "", randomDescription);
	}

	/**
	 * Update environment rejects undefined description.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateEnvironmentRejectsUndefinedDescription() throws Exception {
		// create mock
		EnvironmentInfo environmentInfo = createMock(EnvironmentInfo.class);
		replay(environmentInfo);

		repository.updateEnvironment(environmentInfo, randomEnvId, null);
	}

	/**
	 * Update environment fails if environment is unknown.
	 */
	@Test(expected = EnvironmentNotFoundException.class)
	public void testUpdateEnvironmentFailsWithUnknownEnvironment() throws Exception {
		// create mock
		EnvironmentInfo environmentInfo = createMock(EnvironmentInfo.class);
		expect(environmentInfo.getId()).andReturn(randomEnvId);
		replay(environmentInfo);

		repository.updateEnvironment(environmentInfo, randomEnvId, "");
	}

}
