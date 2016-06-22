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

package com.alpha.pineapple.plugin.activation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.repository.PluginInfo;
import com.alpha.pineapple.plugin.repository.PluginRuntimeRepository;
import com.alpha.pineapple.plugin.session.SessionHandlerFactory;
import com.alpha.pineapple.resource.ResourceInfo;
import com.alpha.pineapple.resource.ResourceNotFoundException;
import com.alpha.pineapple.resource.ResourceRepository;
import com.alpha.pineapple.session.Session;
import com.alpha.testutils.CoreTestConstants;

/**
 * Unit test of the class {@link PluginActivatorImpl}.
 */
public class PluginActivatorImplTest {
    /**
     * Plugin id.
     */
    static final String PLUGIN_ID = "some-plugin-id";

    /**
     * Operation id.
     */
    static final String OPERATION_ID = "some-operation-id";

    /**
     * Null credential reference id.
     */
    static final String NULL_CRED_REF_ID = null;

    /**
     * Empty credential reference id.
     */
    static final String EMPTY_CRED_REF_ID = "";

    /**
     * Null credential id.
     */
    static final Credential NULL_CREDENTIAL = null;

    /**
     * Object under test.
     */
    PluginActivatorImpl pluginActivator;

    /**
     * Mock plugin repository.
     */
    PluginRuntimeRepository pluginRepository;

    /**
     * Mock resource repository.
     */
    ResourceRepository resourceRepository;

    /**
     * Credential provider
     */
    CredentialProvider credentialProvider;

    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;

    /**
     * Session handler factory.
     */
    SessionHandlerFactory retrySessionHandlerFactory;

    @Before
    public void setUp() throws Exception {
	// create plugin activator
	pluginActivator = new PluginActivatorImpl();

	// create mock credential provider
	credentialProvider = EasyMock.createMock(CredentialProvider.class);

	// create mock plugin repository
	pluginRepository = EasyMock.createMock(PluginRuntimeRepository.class);

	// inject repository
	ReflectionTestUtils.setField(pluginActivator, "pluginRepository", pluginRepository);

	// create mock resource repository
	resourceRepository = EasyMock.createMock(ResourceRepository.class);

	// inject repository
	ReflectionTestUtils.setField(pluginActivator, "resourceRepository", resourceRepository);

	// create mock provider
	messageProvider = EasyMock.createMock(MessageProvider.class);

	// inject message provider
	ReflectionTestUtils.setField(pluginActivator, "messageProvider", messageProvider);

	// create mock factory
	retrySessionHandlerFactory = org.easymock.classextension.EasyMock.createMock(SessionHandlerFactory.class);

	// inject message provider
	ReflectionTestUtils.setField(pluginActivator, "retrySessionHandlerFactory", retrySessionHandlerFactory);

	// complete mock source initialization
	IAnswer<String> answer = new MessageProviderAnswerImpl();
	EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
	EasyMock.expectLastCall().andAnswer(answer).anyTimes();
	EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class),
		(Object[]) EasyMock.isA(Object[].class)));
	EasyMock.expectLastCall().andAnswer(answer).anyTimes();
	EasyMock.replay(messageProvider);

    }

    @After
    public void tearDown() throws Exception {
	// delete objects
	pluginActivator = null;
	pluginRepository = null;
	resourceRepository = null;
	credentialProvider = null;
	retrySessionHandlerFactory = null;
    }

    /**
     * Constructor test, i.e. that plugin activator instance can be created.
     */
    @Test
    public void testCanCreateInstance() {
	assertNotNull(pluginActivator);
    }

    /**
     * Activator rejects undefined credential provider.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitializeRejectsUndefinedProvider() {
	// invoke to provoke exception
	pluginActivator.initialize(null, resourceRepository, pluginRepository);
    }

    /**
     * Activator rejects undefined resource repository.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitializeRejectsUndefinedResourceRepository() {
	// invoke to provoke exception
	pluginActivator.initialize(credentialProvider, null, pluginRepository);
    }

    /**
     * Activator rejects undefined plugin repository.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitializeRejectsUndefinedPluginRepository() {
	// invoke to provoke exception
	pluginActivator.initialize(credentialProvider, resourceRepository, null);
    }

    /**
     * Test that activator can be initialized.
     */
    @Test
    public void testInitialize() {
	// complete initialization of mock provider
	EasyMock.replay(credentialProvider);

	// complete initialization of mock plugin repository
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.replay(pluginRepository);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// verify mocks
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
    }

    /**
     * Can get session.
     */
    @Test
    public void testGetSession() {
	String environment = CoreTestConstants.environmentIdOne;
	String id = CoreTestConstants.resourceIdentifierTestResource;

	// create mock resource info
	ResourceInfo resourceInfo = EasyMock.createMock(ResourceInfo.class);
	EasyMock.expect(resourceInfo.getPluginId()).andReturn(PLUGIN_ID);
	EasyMock.replay(resourceInfo);

	// complete initialization of mock session
	Session session = EasyMock.createMock(Session.class);
	EasyMock.replay(session);

	// complete initialization of mock provider
	EasyMock.replay(credentialProvider);

	// complete initialization of mock plugin repository
	EasyMock.expect(resourceRepository.get(environment, id)).andReturn(resourceInfo);
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.expect(pluginRepository.getSession(PLUGIN_ID)).andReturn(session);
	EasyMock.replay(pluginRepository);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get session
	Session pluginSession = pluginActivator.getSession(environment, id);

	// test
	assertNotNull(pluginSession);
	assertEquals(session, pluginSession);

	// verify mocks
	EasyMock.verify(resourceInfo);
	EasyMock.verify(session);
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
    }

    /**
     * Get session is rejected if environment is undefined.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetSessionRejectsUndefinedEnvironment() throws Exception {
	String environment = CoreTestConstants.environmentIdOne;
	String id = CoreTestConstants.resourceIdentifierTestResource;

	// complete initialization of mock provider
	EasyMock.replay(credentialProvider);

	// complete initialization of mock plugin repository
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.replay(pluginRepository);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get session
	Session pluginSession = pluginActivator.getSession(null, id);

	// test
	assertNotNull(pluginSession);

	// verify mocks
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
    }

    /**
     * Get session is rejected if resource id is undefined.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetSessionRejectsUndefinedResourceId() throws Exception {
	String environment = CoreTestConstants.environmentIdOne;
	String id = CoreTestConstants.resourceIdentifierTestResource;

	// complete initialization of mock provider
	EasyMock.replay(credentialProvider);

	// complete initialization of mock plugin repository
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.replay(pluginRepository);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get session
	Session pluginSession = pluginActivator.getSession(environment, null);

	// test
	assertNotNull(pluginSession);

	// verify mocks
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
    }

    /**
     * Get session is rejected if environment is unknown.
     */
    @Test(expected = ResourceNotFoundException.class)
    public void testGetSessionRejectsUnknownEnvironment() {
	String environment = "unknown-environment";
	String id = CoreTestConstants.resourceIdentifierTestResource;

	// create mock resource info
	ResourceInfo resourceInfo = EasyMock.createMock(ResourceInfo.class);
	EasyMock.expect(resourceInfo.getPluginId()).andReturn(PLUGIN_ID);
	EasyMock.replay(resourceInfo);

	// complete initialization of mock session
	Session session = EasyMock.createMock(Session.class);
	EasyMock.replay(session);

	// complete initialization of mock provider
	EasyMock.replay(credentialProvider);

	// complete initialization of mock plugin repository
	Throwable exception = new ResourceNotFoundException("message");
	EasyMock.expect(resourceRepository.get(environment, id)).andThrow(exception);
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.expect(pluginRepository.getSession(PLUGIN_ID)).andReturn(session);
	EasyMock.replay(pluginRepository);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get session
	Session pluginSession = pluginActivator.getSession(environment, id);

	// test
	assertNotNull(pluginSession);
	assertEquals(session, pluginSession);

	// verify mocks
	EasyMock.verify(resourceInfo);
	EasyMock.verify(session);
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
    }

    /**
     * Get session is rejected if resource id is unknown.
     */
    @Test(expected = ResourceNotFoundException.class)
    public void testGetSessionRejectsUnknownResource() {
	String environment = CoreTestConstants.environmentIdOne;
	String id = "unknown-id";

	// create mock resource info
	ResourceInfo resourceInfo = EasyMock.createMock(ResourceInfo.class);
	EasyMock.expect(resourceInfo.getPluginId()).andReturn(PLUGIN_ID);
	EasyMock.replay(resourceInfo);

	// complete initialization of mock session
	Session session = EasyMock.createMock(Session.class);
	EasyMock.replay(session);

	// complete initialization of mock provider
	EasyMock.replay(credentialProvider);

	// complete initialization of mock plugin repository
	Throwable exception = new ResourceNotFoundException("message");
	EasyMock.expect(resourceRepository.get(environment, id)).andThrow(exception);
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.expect(pluginRepository.getSession(PLUGIN_ID)).andReturn(session);
	EasyMock.replay(pluginRepository);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get session
	Session pluginSession = pluginActivator.getSession(environment, id);

	// test
	assertNotNull(pluginSession);
	assertEquals(session, pluginSession);

	// verify mocks
	EasyMock.verify(resourceInfo);
	EasyMock.verify(session);
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
    }

    /**
     * Get unmarshaller is rejected if environment is undefined.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetUnmarshallerRejectsUndefinedEnvironment() throws Exception {
	String environment = CoreTestConstants.environmentIdOne;
	String id = CoreTestConstants.resourceIdentifierTestResource;

	// complete initialization of mock provider
	EasyMock.replay(credentialProvider);

	// complete initialization of mock plugin repository
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.replay(pluginRepository);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get session
	Unmarshaller unmarshaller = pluginActivator.getUnmarshaller(null, id);

	// test
	assertNotNull(unmarshaller);

	// verify mocks
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
    }

    /**
     * Get unmarshaller fails if plugin id isn't defined in id is undefined.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetUnmarshallerRejectsUndefinedResourceId() throws Exception {
	String environment = CoreTestConstants.environmentIdOne;
	String id = CoreTestConstants.resourceIdentifierTestResource;

	// complete initialization of mock provider
	EasyMock.replay(credentialProvider);

	// complete initialization of mock plugin repository
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.replay(pluginRepository);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get session
	Unmarshaller unmarshaller = pluginActivator.getUnmarshaller(environment, null);

	// test
	assertNotNull(unmarshaller);

	// verify mocks
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
    }

    /**
     * Can get operation without session handling.
     */
    @Test
    public void testGetOperationWithoutSessionHandling() {
	String environment = CoreTestConstants.environmentIdOne;
	String resourceId = CoreTestConstants.resourceIdentifierTestResource;

	// create mock resource info
	ResourceInfo resourceInfo = EasyMock.createMock(ResourceInfo.class);
	EasyMock.expect(resourceInfo.getPluginId()).andReturn(PLUGIN_ID);
	EasyMock.replay(resourceInfo);

	// complete initialization of mock operation
	Operation operation = EasyMock.createMock(Operation.class);
	EasyMock.replay(operation);

	// complete initialization of mock plugin info
	PluginInfo pluginInfo = EasyMock.createMock(PluginInfo.class);
	EasyMock.expect(pluginInfo.isSessionHandlingEnabled()).andReturn(false);
	EasyMock.replay(pluginInfo);

	// complete initialization of mock provider
	EasyMock.replay(credentialProvider);

	// complete initialization of mock plugin repository
	EasyMock.expect(resourceRepository.get(environment, resourceId)).andReturn(resourceInfo);
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.expect(pluginRepository.getPluginInfo(PLUGIN_ID)).andReturn(pluginInfo);
	EasyMock.expect(pluginRepository.getOperation(PLUGIN_ID, OPERATION_ID)).andReturn(operation);
	EasyMock.replay(pluginRepository);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get operation
	Operation actualOperation = pluginActivator.getOperation(environment, resourceId, OPERATION_ID);

	// test
	assertNotNull(actualOperation);
	assertEquals(actualOperation, operation);

	// verify mocks
	EasyMock.verify(resourceInfo);
	EasyMock.verify(operation);
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
	EasyMock.verify(pluginInfo);
    }

    /**
     * Can get operation with session handling.
     */
    @Test
    public void testGetOperationWithSessionHandling() throws Exception {
	String environment = CoreTestConstants.environmentIdOne;
	String resourceId = CoreTestConstants.resourceIdentifierTestResource;
	String credentialId = "some-credential-id";

	// complete initialization of mock resource
	Resource resource = org.easymock.classextension.EasyMock.createMock(Resource.class);
	org.easymock.classextension.EasyMock.expect(resource.getCredentialIdRef()).andReturn(credentialId);
	org.easymock.classextension.EasyMock.replay(resource);

	// complete initialization of mock credential
	Credential credential = org.easymock.classextension.EasyMock.createMock(Credential.class);
	org.easymock.classextension.EasyMock.replay(credential);

	// create mock resource info
	ResourceInfo resourceInfo = EasyMock.createMock(ResourceInfo.class);
	EasyMock.expect(resourceInfo.getPluginId()).andReturn(PLUGIN_ID);
	EasyMock.expect(resourceInfo.getResource()).andReturn(resource);
	EasyMock.replay(resourceInfo);

	// complete initialization of mock operation
	Operation operation = EasyMock.createMock(Operation.class);
	EasyMock.replay(operation);

	// complete initialization of mock plugin info
	PluginInfo pluginInfo = EasyMock.createMock(PluginInfo.class);
	EasyMock.expect(pluginInfo.isSessionHandlingEnabled()).andReturn(true);
	EasyMock.replay(pluginInfo);

	// complete initialization of mock provider
	EasyMock.expect(credentialProvider.get(environment, credentialId)).andReturn(credential);
	EasyMock.replay(credentialProvider);

	// complete initialization of mock resource repository
	EasyMock.expect(resourceRepository.get(environment, resourceId)).andReturn(resourceInfo).times(2);
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.expect(pluginRepository.getPluginInfo(PLUGIN_ID)).andReturn(pluginInfo);
	EasyMock.expect(pluginRepository.getOperation(PLUGIN_ID, OPERATION_ID)).andReturn(operation);
	EasyMock.replay(pluginRepository);

	// complete initialization of mock session handler
	Operation sessionHandler = EasyMock.createMock(Operation.class);
	EasyMock.replay(sessionHandler);

	// complete initialization of mock factory
	org.easymock.classextension.EasyMock
		.expect(retrySessionHandlerFactory.getInstance(resource, credential, operation))
		.andReturn(sessionHandler);
	org.easymock.classextension.EasyMock.replay(retrySessionHandlerFactory);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get operation
	Operation actualOperation = pluginActivator.getOperation(environment, resourceId, OPERATION_ID);

	// test
	assertNotNull(actualOperation);
	assertEquals(sessionHandler, actualOperation);

	// verify mocks
	EasyMock.verify(resourceInfo);
	EasyMock.verify(operation);
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
	EasyMock.verify(pluginInfo);
	org.easymock.classextension.EasyMock.verify(resource);
	org.easymock.classextension.EasyMock.verify(credential);
	org.easymock.classextension.EasyMock.verify(retrySessionHandlerFactory);
	EasyMock.verify(sessionHandler);
    }

    /**
     * Can get operation with session handling where the credential reference is
     * null.
     */
    @Test
    public void testGetOperationWithSessionHandlingWithNullCredentialRefInResource() throws Exception {
	String environment = CoreTestConstants.environmentIdOne;
	String resourceId = CoreTestConstants.resourceIdentifierTestResource;

	// complete initialization of mock resource
	Resource resource = org.easymock.classextension.EasyMock.createMock(Resource.class);
	org.easymock.classextension.EasyMock.expect(resource.getCredentialIdRef()).andReturn(NULL_CRED_REF_ID);
	org.easymock.classextension.EasyMock.replay(resource);

	// complete initialization of mock credential
	Credential credential = org.easymock.classextension.EasyMock.createMock(Credential.class);
	org.easymock.classextension.EasyMock.replay(credential);

	// create mock resource info
	ResourceInfo resourceInfo = EasyMock.createMock(ResourceInfo.class);
	EasyMock.expect(resourceInfo.getPluginId()).andReturn(PLUGIN_ID);
	EasyMock.expect(resourceInfo.getResource()).andReturn(resource);
	EasyMock.replay(resourceInfo);

	// complete initialization of mock operation
	Operation operation = EasyMock.createMock(Operation.class);
	EasyMock.replay(operation);

	// complete initialization of mock plugin info
	PluginInfo pluginInfo = EasyMock.createMock(PluginInfo.class);
	EasyMock.expect(pluginInfo.isSessionHandlingEnabled()).andReturn(true);
	EasyMock.replay(pluginInfo);

	// complete initialization of mock provider
	EasyMock.replay(credentialProvider);

	// complete initialization of mock resource repository
	EasyMock.expect(resourceRepository.get(environment, resourceId)).andReturn(resourceInfo).times(2);
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.expect(pluginRepository.getPluginInfo(PLUGIN_ID)).andReturn(pluginInfo);
	EasyMock.expect(pluginRepository.getOperation(PLUGIN_ID, OPERATION_ID)).andReturn(operation);
	EasyMock.replay(pluginRepository);

	// complete initialization of mock session handler
	Operation sessionHandler = EasyMock.createMock(Operation.class);
	EasyMock.replay(sessionHandler);

	// complete initialization of mock factory
	org.easymock.classextension.EasyMock
		.expect(retrySessionHandlerFactory.getInstance(resource, NULL_CREDENTIAL, operation))
		.andReturn(sessionHandler);
	org.easymock.classextension.EasyMock.replay(retrySessionHandlerFactory);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get operation
	Operation actualOperation = pluginActivator.getOperation(environment, resourceId, OPERATION_ID);

	// test
	assertNotNull(actualOperation);
	assertEquals(sessionHandler, actualOperation);

	// verify mocks
	EasyMock.verify(resourceInfo);
	EasyMock.verify(operation);
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
	EasyMock.verify(pluginInfo);
	org.easymock.classextension.EasyMock.verify(resource);
	org.easymock.classextension.EasyMock.verify(credential);
	org.easymock.classextension.EasyMock.verify(retrySessionHandlerFactory);
	EasyMock.verify(sessionHandler);
    }

    /**
     * Get credential method with defined credential reference returns
     * credential.
     */
    @Test
    public void testGetCredentialWithDefinedCredentialRefReturnsCredential() throws Exception {
	String environment = CoreTestConstants.environmentIdOne;
	String resourceId = CoreTestConstants.resourceIdentifierTestResource;
	String credentialId = "some-credential-id";

	// complete initialization of mock resource
	Resource resource = org.easymock.classextension.EasyMock.createMock(Resource.class);
	org.easymock.classextension.EasyMock.expect(resource.getCredentialIdRef()).andReturn(credentialId);
	org.easymock.classextension.EasyMock.replay(resource);

	// complete initialization of mock credential
	Credential credential = org.easymock.classextension.EasyMock.createMock(Credential.class);
	org.easymock.classextension.EasyMock.replay(credential);

	// complete initialization of mock operation
	Operation operation = EasyMock.createMock(Operation.class);
	EasyMock.replay(operation);

	// complete initialization of mock provider
	EasyMock.expect(credentialProvider.get(environment, credentialId)).andReturn(credential);
	EasyMock.replay(credentialProvider);

	// complete initialization of mock plugin repository
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.replay(pluginRepository);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get credential
	Credential actualCredential = pluginActivator.getCredential(environment, resource);

	// test
	assertNotNull(actualCredential);
	assertTrue(actualCredential instanceof Credential);
	assertEquals(credential, actualCredential);

	// verify mocks
	EasyMock.verify(operation);
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
	org.easymock.classextension.EasyMock.verify(resource);
	org.easymock.classextension.EasyMock.verify(credential);
    }

    /**
     * Get credential method with undefined credential reference returns null
     * credential.
     */
    @Test
    public void testGetCredentialWithUndefinedCredentialRefReturnsNullCredential() throws Exception {
	String environment = CoreTestConstants.environmentIdOne;
	String resourceId = CoreTestConstants.resourceIdentifierTestResource;

	// complete initialization of mock resource
	Resource resource = org.easymock.classextension.EasyMock.createMock(Resource.class);
	org.easymock.classextension.EasyMock.expect(resource.getCredentialIdRef()).andReturn(NULL_CRED_REF_ID);
	org.easymock.classextension.EasyMock.replay(resource);

	// complete initialization of mock credential
	Credential credential = org.easymock.classextension.EasyMock.createMock(Credential.class);
	org.easymock.classextension.EasyMock.replay(credential);

	// complete initialization of mock operation
	Operation operation = EasyMock.createMock(Operation.class);
	EasyMock.replay(operation);

	// complete initialization of mock provider
	EasyMock.replay(credentialProvider);

	// complete initialization of mock resource repository
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.replay(pluginRepository);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get credential
	Credential actualCredential = pluginActivator.getCredential(environment, resource);

	// test
	assertNull(actualCredential);

	// verify mocks
	EasyMock.verify(operation);
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
	org.easymock.classextension.EasyMock.verify(resource);
	org.easymock.classextension.EasyMock.verify(credential);
    }

    /**
     * Get credential method with undefined credential reference returns empty
     * credential.
     */
    @Test
    public void testGetCredentialWithEmptyCredentialRefReturnsNullCredential() throws Exception {
	String environment = CoreTestConstants.environmentIdOne;
	String resourceId = CoreTestConstants.resourceIdentifierTestResource;

	// complete initialization of mock resource
	Resource resource = org.easymock.classextension.EasyMock.createMock(Resource.class);
	org.easymock.classextension.EasyMock.expect(resource.getCredentialIdRef()).andReturn(EMPTY_CRED_REF_ID);
	org.easymock.classextension.EasyMock.replay(resource);

	// complete initialization of mock credential
	Credential credential = org.easymock.classextension.EasyMock.createMock(Credential.class);
	org.easymock.classextension.EasyMock.replay(credential);

	// complete initialization of mock operation
	Operation operation = EasyMock.createMock(Operation.class);
	EasyMock.replay(operation);

	// complete initialization of mock provider
	EasyMock.replay(credentialProvider);

	// complete initialization of mock resource repository
	EasyMock.replay(resourceRepository);

	// complete initialization of mock plugin repository
	EasyMock.replay(pluginRepository);

	// initialize
	pluginActivator.initialize(credentialProvider, resourceRepository, pluginRepository);

	// get credential
	Credential actualCredential = pluginActivator.getCredential(environment, resource);

	// test
	assertNull(actualCredential);

	// verify mocks
	EasyMock.verify(operation);
	EasyMock.verify(credentialProvider);
	EasyMock.verify(resourceRepository);
	EasyMock.verify(pluginRepository);
	org.easymock.classextension.EasyMock.verify(resource);
	org.easymock.classextension.EasyMock.verify(credential);
    }

}
