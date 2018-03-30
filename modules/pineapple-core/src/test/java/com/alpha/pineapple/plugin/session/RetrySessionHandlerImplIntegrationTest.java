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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;

/**
 * Integration test of the class {@link SessionHandlerImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class RetrySessionHandlerImplIntegrationTest {

    /**
     * Object under test.
     */
    Operation sessionHandler;

    /**
     * Session handler factory.
     */
    @javax.annotation.Resource
    SessionHandlerFactory retrySessionHandlerFactory;

    /**
     * Execution result factory.
     */
    @javax.annotation.Resource
    ExecutionResultFactory executionResultFactory;

    /**
     * Execution result.
     */
    ExecutionResult result;

    /**
     * Random description.
     */
    String randomDescription;

    /**
     * Random ID.
     */
    String randomId;

    /**
     * Mock resource.
     */
    Resource resource;

    /**
     * Mock credential.
     */
    Credential credential;

    /**
     * Mock operation.
     */
    Operation operation;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
	randomDescription = RandomStringUtils.randomAlphabetic(10);
	randomId = RandomStringUtils.randomAlphabetic(10);
	result = executionResultFactory.startExecution(randomDescription);

	// complete initialization of mock credential
	credential = createMock(Credential.class);
	replay(credential);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test that operation can be executed.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testCanExecuteOperation() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	expect(resource.getId()).andReturn(randomId).times(2);
	replay(resource);

	// complete initialization of mock content
	Object content = createMock(Object.class);
	replay(content);

	// complete initialization of mock session
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	session.disconnect();
	replay(session);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	operation.execute(content, session, result);
	;
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);
	sessionHandler.execute(content, session, result);

	// test
	verify(resource);
	verify(credential);
	verify(operation);
	verify(session);
	verify(content);
    }

    /**
     * Test that session handler add session information to execution result.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testSessionHandlerAddSessionInfoToExecutionResult() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	expect(resource.getId()).andReturn(randomId).times(2);
	replay(resource);

	// complete initialization of mock content
	Object content = createMock(Object.class);
	replay(content);

	// complete initialization of mock session
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	session.disconnect();
	replay(session);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	operation.execute(content, session, result);
	;
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);
	sessionHandler.execute(content, session, result);

	// test
	assertNotNull(result.getMessages());
	assertNotNull(result.getMessages().containsKey(CoreConstants.MSG_SESSION));
	assertNotNull(result.getMessages().get(CoreConstants.MSG_SESSION));
	assertNotNull(result.getMessages().get(CoreConstants.MSG_SESSION));
	verify(resource);
	verify(credential);
	verify(operation);
	verify(session);
	verify(content);
    }

    /**
     * Test that operation can be executed with null session.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testCanExecuteWithNullSession() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	replay(resource);

	// complete initialization of mock content
	Object content = createMock(Object.class);
	replay(content);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	operation.execute(content, null, result);
	;
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);
	sessionHandler.execute(content, null, result);

	// test
	verify(resource);
	verify(credential);
	verify(operation);
	verify(content);
    }

    /**
     * Test that operation can be executed with null content.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testCanExecuteOperationWithNullContent() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	expect(resource.getId()).andReturn(randomId).times(2);
	replay(resource);

	// complete initialization of mock session
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	session.disconnect();
	replay(session);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	operation.execute(null, session, result);
	;
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);
	sessionHandler.execute(null, session, result);

	// test
	verify(resource);
	verify(credential);
	verify(operation);
	verify(session);
    }

    /**
     * Test that operation fails if handler is invoked with null result.
     * 
     * Session handler throws thrown a {@linkplain IllegalArgumentException}
     * which is tunneled in a {@linkplain RuntimeException},
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test(expected = RuntimeException.class)
    public void testExecutionFailsIfInvokedWithNullResult() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	expect(resource.getId()).andReturn(randomId).times(2);
	replay(resource);

	// complete initialization of mock content
	Object content = createMock(Object.class);
	replay(content);

	// complete initialization of mock session
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	session.disconnect();
	replay(session);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	operation.execute(content, session, null);
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);
	sessionHandler.execute(content, session, null);

	// test
	verify(resource);
	verify(credential);
	verify(operation);
	verify(session);
	verify(content);
    }

    /**
     * Test that thrown {@link PluginExecutionFailedException} is channeled in a
     * runtime exception.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testThrownPluginExceptionIsChanneled() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	expect(resource.getId()).andReturn(randomId).times(2);
	replay(resource);

	// complete initialization of mock content
	Object content = createMock(Object.class);
	replay(content);

	// complete initialization of mock session
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	session.disconnect();
	replay(session);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	operation.execute(content, session, result);
	PluginExecutionFailedException exception = new PluginExecutionFailedException(randomDescription);
	expectLastCall().andThrow(exception);
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);

	try {
	    sessionHandler.execute(content, session, result);
	    fail("No exception was thrown as expected");
	} catch (RuntimeException e) {
	    assertEquals(exception, e.getCause());
	    assertEquals(randomDescription, e.getCause().getMessage());
	    assertTrue(e.getCause() instanceof PluginExecutionFailedException);
	}

	// test
	verify(resource);
	verify(credential);
	verify(operation);
	verify(session);
	verify(content);
    }

    /**
     * Test that thrown {@linkplain SessionConnectException} is channeled in a
     * runtime exception.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testThrownSessionConnectExceptionDuringConnectIsChanneled() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	expect(resource.getId()).andReturn(randomId);
	replay(resource);

	// complete initialization of mock content
	Object content = createMock(Object.class);
	replay(content);

	// complete initialization of mock session
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	SessionConnectException exception = new SessionConnectException(randomDescription);
	expectLastCall().andThrow(exception).times(4); // retry logic
								// will catch
								// the exception
								// the first 3
								// times
	replay(session);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);

	try {
	    sessionHandler.execute(content, session, result);
	    fail("No exception was thrown as expected");
	} catch (RuntimeException e) {
	    assertEquals(exception, e.getCause());
	    assertEquals(randomDescription, e.getCause().getMessage());
	    assertTrue(e.getCause() instanceof SessionConnectException);
	}

	// test
	verify(resource);
	verify(credential);
	verify(operation);
	verify(session);
	verify(content);
    }

    /**
     * Test that thrown {@linkplain SessiondisconnectException} is channeled in
     * a runtime exception.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testThrownSessionDisconnectExceptionDuringDisconnectIsChanneled() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	expect(resource.getId()).andReturn(randomId).times(2);
	replay(resource);

	// complete initialization of mock content
	Object content = createMock(Object.class);
	replay(content);

	// complete initialization of mock session
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	session.disconnect();
	SessionDisconnectException exception = new SessionDisconnectException(randomDescription);
	expectLastCall().andThrow(exception);
	replay(session);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	operation.execute(content, session, result);
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);

	try {
	    sessionHandler.execute(content, session, result);
	    fail("No exception was thrown as expected");
	} catch (RuntimeException e) {
	    assertEquals(exception, e.getCause());
	    assertEquals(randomDescription, e.getCause().getMessage());
	    assertTrue(e.getCause() instanceof SessionDisconnectException);
	}

	// test
	verify(resource);
	verify(credential);
	verify(operation);
	verify(session);
	verify(content);
    }

    /**
     * Test that thrown {@link IllegalArgumentException} is channeled in a
     * runtime exception.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testThrownIllegalArgumentExceptionIsChanneled() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	expect(resource.getId()).andReturn(randomId).times(2);
	replay(resource);

	// complete initialization of mock content
	Object content = createMock(Object.class);
	replay(content);

	// complete initialization of mock session
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	session.disconnect();
	replay(session);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	operation.execute(content, session, result);
	IllegalArgumentException exception = new IllegalArgumentException(randomDescription);
	expectLastCall().andThrow(exception);
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);

	try {
	    sessionHandler.execute(content, session, result);
	    fail("No exception was thrown as expected");
	} catch (RuntimeException e) {
	    assertEquals(exception, e.getCause());
	    assertEquals(randomDescription, e.getCause().getMessage());
	    assertTrue(e.getCause() instanceof IllegalArgumentException);
	}

	// test
	verify(resource);
	verify(credential);
	verify(operation);
	verify(session);
	verify(content);
    }

    /**
     * Test that session disconnect is invoked if
     * {@link PluginExecutionFailedException} is thrown.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testDisconnectIsInvokedIfPluginExecutionExceptionIsThrown() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	expect(resource.getId()).andReturn(randomId).times(2);
	replay(resource);

	// complete initialization of mock content
	Object content = createMock(Object.class);
	replay(content);

	// complete initialization of mock session
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	session.disconnect();
	replay(session);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	operation.execute(content, session, result);
	PluginExecutionFailedException exception = new PluginExecutionFailedException(randomDescription);
	expectLastCall().andThrow(exception);
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);
	try {
	    sessionHandler.execute(content, session, result);
	    fail("No exception was thrown as expected");

	} catch (Exception e) {

	    // test channeled exception
	    assertEquals(exception, e.getCause());
	    assertEquals(randomDescription, e.getCause().getMessage());
	    assertTrue(e.getCause() instanceof PluginExecutionFailedException);
	}

	// test
	verify(resource);
	verify(credential);
	verify(operation);
	verify(session);
	verify(content);
    }

    /**
     * Test that session disconnect is invoked if
     * {@link IllegalArgumentException} is thrown.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testDisconnectIsInvokedIfIllegalArgumentExceptionIsThrown() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	expect(resource.getId()).andReturn(randomId).times(2);
	replay(resource);

	// complete initialization of mock content
	Object content = createMock(Object.class);
	replay(content);

	// complete initialization of mock session
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	session.disconnect();
	replay(session);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	operation.execute(content, session, result);
	IllegalArgumentException exception = new IllegalArgumentException(randomDescription);
	expectLastCall().andThrow(exception);
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);
	try {
	    sessionHandler.execute(content, session, result);
	    fail("No exception was thrown as expected");

	} catch (Exception e) {

	    // test channeled exception
	    assertEquals(exception, e.getCause());
	    assertEquals(randomDescription, e.getCause().getMessage());
	    assertTrue(e.getCause() instanceof IllegalArgumentException);
	}

	// test
	verify(resource);
	verify(credential);
	verify(operation);
	verify(session);
	verify(content);
    }

    /**
     * Test that session disconnect is NOT invoked if
     * {@link SessionConnectException} is thrown 4x times so that session
     * establishment is aborted.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testDisconnectIsNotInvokedIfSessionConnectExceptionIsThrown() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	expect(resource.getId()).andReturn(randomId);
	replay(resource);

	// complete initialization of mock content
	Object content = createMock(Object.class);
	replay(content);

	// complete initialization of mock session
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	SessionConnectException exception = new SessionConnectException(randomDescription);
	expectLastCall().andThrow(exception).times(4);
	replay(session);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);
	try {
	    sessionHandler.execute(content, session, result);
	    fail("No exception was thrown as expected");

	} catch (Exception e) {

	    // test channeled exception
	    assertEquals(exception, e.getCause());
	    assertEquals(randomDescription, e.getCause().getMessage());
	    assertTrue(e.getCause() instanceof SessionConnectException);
	}

	// test
	verify(resource);
	verify(credential);
	verify(operation);
	verify(session);
	verify(content);
    }

    /**
     * Test that (original) PluginExecutionFailedException is channeled even if
     * session disconnect fails due to an exception.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testPluginExecutionExceptionIsChanneledIfSessionDisconnectFails() throws Exception {

	// complete initialization of mock resource
	resource = createMock(Resource.class);
	expect(resource.getId()).andReturn(randomId).times(2);
	replay(resource);

	// complete initialization of mock content
	Object content = createMock(Object.class);
	replay(content);

	// complete initialization of mock session
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	session.disconnect();
	SessionDisconnectException disConnectException = new SessionDisconnectException(randomDescription);
	expectLastCall().andThrow(disConnectException);
	replay(session);

	// complete initialization of mock operation
	operation = createMock(Operation.class);
	operation.execute(content, session, result);
	PluginExecutionFailedException exception = new PluginExecutionFailedException(randomDescription);
	expectLastCall().andThrow(exception);
	replay(operation);

	// create and execute
	sessionHandler = retrySessionHandlerFactory.getInstance(resource, credential, operation);
	try {
	    sessionHandler.execute(content, session, result);
	    fail("No exception was thrown as expected");

	} catch (Exception e) {

	    // test channeled exception
	    assertEquals(exception, e.getCause());
	    assertEquals(randomDescription, e.getCause().getMessage());
	    assertTrue(e.getCause() instanceof PluginExecutionFailedException);
	}

	// test
	verify(resource);
	verify(credential);
	verify(operation);
	verify(session);
	verify(content);
    }

}
