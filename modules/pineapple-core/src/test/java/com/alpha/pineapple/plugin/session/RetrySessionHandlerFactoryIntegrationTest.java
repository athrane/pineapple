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

package com.alpha.pineapple.plugin.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.plugin.Operation;

/**
 * Integration test of the class {@link RetrySessionHandlerFactoryImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class RetrySessionHandlerFactoryIntegrationTest {

    /**
     * Object under test.
     */
    @javax.annotation.Resource
    SessionHandlerFactory retrySessionHandlerFactory;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test that factory can be looked up from context.
     */
    @Test
    public void testCanCreateInstance() {
	// test
	assertNotNull(retrySessionHandlerFactory);
    }

    /**
     * Test that session handler can be created.
     */
    @Test
    public void testCreateSessionHandler() {

	// complete initialization of mock resource
	Resource resource = org.easymock.classextension.EasyMock.createMock(Resource.class);
	org.easymock.classextension.EasyMock.replay(resource);

	// complete initialization of mock credential
	Credential credential = org.easymock.classextension.EasyMock.createMock(Credential.class);
	org.easymock.classextension.EasyMock.replay(credential);

	// complete initialization of mock operation
	Operation operation = EasyMock.createMock(Operation.class);
	EasyMock.replay(operation);

	// create
	Operation sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);

	// test
	assertNotNull(sessionHandler);
	assertTrue(sessionHandler instanceof RetrySessionHandlerImpl);

	// type cast
	RetrySessionHandlerImpl sessionHandlerImpl = (RetrySessionHandlerImpl) sessionHandler;
	assertEquals(resource, sessionHandlerImpl.resource);
	assertEquals(credential, sessionHandlerImpl.credential);
	assertEquals(operation, sessionHandlerImpl.operation);

	org.easymock.classextension.EasyMock.verify(resource);
	org.easymock.classextension.EasyMock.verify(credential);
	org.easymock.classextension.EasyMock.verify(operation);
    }

    /**
     * Test that session handler can be created with undefined credential.
     */
    @Test
    public void testCreationSucceedsWithNullCredential() {

	// complete initialization of mock resource
	Resource resource = org.easymock.classextension.EasyMock.createMock(Resource.class);
	org.easymock.classextension.EasyMock.replay(resource);

	// complete initialization of mock operation
	Operation operation = EasyMock.createMock(Operation.class);
	EasyMock.replay(operation);

	// create
	Operation sessionHandler = retrySessionHandlerFactory.getInstance(resource, null, operation);

	// test
	assertNotNull(sessionHandler);
	assertTrue(sessionHandler instanceof RetrySessionHandlerImpl);

	// type cast
	RetrySessionHandlerImpl sessionHandlerImpl = (RetrySessionHandlerImpl) sessionHandler;
	assertEquals(resource, sessionHandlerImpl.resource);
	assertNotNull(sessionHandlerImpl.credential);
	assertEquals("", sessionHandlerImpl.credential.getId());
	assertEquals("", sessionHandlerImpl.credential.getPassword());
	assertEquals("", sessionHandlerImpl.credential.getUser());
	assertEquals(operation, sessionHandlerImpl.operation);

	org.easymock.classextension.EasyMock.verify(resource);
	org.easymock.classextension.EasyMock.verify(operation);
    }

    /**
     * Test that creation fails if resource is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreationFailsWithNullResource() {

	// complete initialization of mock credential
	Credential credential = org.easymock.classextension.EasyMock.createMock(Credential.class);
	org.easymock.classextension.EasyMock.replay(credential);

	// complete initialization of mock operation
	Operation operation = EasyMock.createMock(Operation.class);
	EasyMock.replay(operation);

	// create
	retrySessionHandlerFactory.getInstance(null, credential, operation);
    }

    /**
     * Test that creation fails if operation is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreationFailsWithNullOperation() {

	// complete initialization of mock resource
	Resource resource = org.easymock.classextension.EasyMock.createMock(Resource.class);
	org.easymock.classextension.EasyMock.replay(resource);

	// complete initialization of mock credential
	Credential credential = org.easymock.classextension.EasyMock.createMock(Credential.class);
	org.easymock.classextension.EasyMock.replay(credential);

	// create
	retrySessionHandlerFactory.getInstance(resource, credential, null);
    }

    /**
     * Test that each created session handler builder is a Spring prototype.
     */
    @Test
    public void testCreatedSessionHandlerIsSpringProtototype() {

	// complete initialization of mock resource
	Resource resource = org.easymock.classextension.EasyMock.createMock(Resource.class);
	org.easymock.classextension.EasyMock.replay(resource);

	// complete initialization of mock credential
	Credential credential = org.easymock.classextension.EasyMock.createMock(Credential.class);
	org.easymock.classextension.EasyMock.replay(credential);

	// complete initialization of mock operation
	Operation operation = EasyMock.createMock(Operation.class);
	EasyMock.replay(operation);

	// create
	Operation sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);
	Operation sessionHandler2 = retrySessionHandlerFactory.getInstance(resource, credential, operation);

	// test
	assertFalse(sessionHandler.equals(sessionHandler2));
	assertFalse(sessionHandler.hashCode() == sessionHandler2.hashCode());

	org.easymock.classextension.EasyMock.verify(resource);
	org.easymock.classextension.EasyMock.verify(credential);
	org.easymock.classextension.EasyMock.verify(operation);
    }

}
