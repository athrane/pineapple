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

package com.alpha.pineapple.plugin.session.retry;

import static com.alpha.pineapple.CoreConstants.MSG_SESSION;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;

/**
 * Unit test of the class {@linkplain SessionRetryProxyFactoryImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class SessionRetryProxyFactoryIntegrationTest {

    /**
     * Session with no no-arg constructor or n-arg constructor.
     */
    class SessionWithNoConstructors implements Session {

	@Override
	public void connect(com.alpha.pineapple.model.configuration.Resource resource, Credential credential)
		throws SessionConnectException {
	    // NO-OP
	}

	@Override
	public void disconnect() throws SessionDisconnectException {
	    // NO-OP
	}

	@Override
	public com.alpha.pineapple.model.configuration.Resource getResource() {
	    // NO-OP
	    return null;
	}

	@Override
	public Credential getCredential() {
	    // NO-OP
	    return null;
	}

    }

    /**
     * Session with no no-arg constructor, e.g. class has public n-arg
     * constructor.
     */
    class SessionWithPublicOneArgConstructor implements Session {
	Credential value;

	public SessionWithPublicOneArgConstructor(Credential value) {
	    this.value = value;
	}

	@Override
	public void connect(com.alpha.pineapple.model.configuration.Resource resource, Credential credential)
		throws SessionConnectException {
	    // NO-OP
	}

	@Override
	public void disconnect() throws SessionDisconnectException {
	    // NO-OP
	}

	@Override
	public com.alpha.pineapple.model.configuration.Resource getResource() {
	    // NO-OP
	    return null;
	}

	@Override
	public Credential getCredential() {
	    return value;
	}

    }

    /**
     * Session with no no-arg constructor, e.g. class has private n-arg
     * constructor.
     */
    class SessionWithPrivateOneArgConstructor implements Session {
	Credential value;

	SessionWithPrivateOneArgConstructor(Credential value) {
	    this.value = value;
	}

	@Override
	public void connect(com.alpha.pineapple.model.configuration.Resource resource, Credential credential)
		throws SessionConnectException {
	    // NO-OP
	}

	@Override
	public void disconnect() throws SessionDisconnectException {
	    // NO-OP
	}

	@Override
	public com.alpha.pineapple.model.configuration.Resource getResource() {
	    // NO-OP
	    return null;
	}

	@Override
	public Credential getCredential() {
	    return value;
	}

    }

    /**
     * Test class with no-arg constructor, e.g. class has public no-arg
     * constructor.
     */
    class SessionWithPublicNoArgConstructor implements Session {
	Credential value;

	private SessionWithPublicNoArgConstructor() {
	}

	@Override
	public void connect(com.alpha.pineapple.model.configuration.Resource resource, Credential credential)
		throws SessionConnectException {
	    // NO-OP
	}

	@Override
	public void disconnect() throws SessionDisconnectException {
	    // NO-OP
	}

	@Override
	public com.alpha.pineapple.model.configuration.Resource getResource() {
	    // NO-OP
	    return null;
	}

	@Override
	public Credential getCredential() {
	    return value;
	}
    }

    /**
     * Session class with no-arg constructor, e.g. class has private no-arg
     * constructor.
     */
    class SessionWithPrivateNoArgConstructor implements Session {
	Credential value;

	private SessionWithPrivateNoArgConstructor() {
	}

	@Override
	public void connect(com.alpha.pineapple.model.configuration.Resource resource, Credential credential)
		throws SessionConnectException {
	    // NO-OP
	}

	@Override
	public void disconnect() throws SessionDisconnectException {
	    // NO-OP
	}

	@Override
	public com.alpha.pineapple.model.configuration.Resource getResource() {
	    // NO-OP
	    return null;
	}

	@Override
	public Credential getCredential() {
	    return value;
	}
    }

    /**
     * Session with no no-arg constructor or n-arg constructor. Throws exception
     */
    class ExceptingSessionWithNoConstructors implements Session {
	Credential value;

	/**
	 * Logger object
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	int numberExceptionsToThrow = 0;
	private int numberInvocations = 0;

	void configureNumberExceptionsToThrow(int numberExceptionsToThrow) {
	    this.numberExceptionsToThrow = numberExceptionsToThrow;
	    this.numberInvocations = 0;
	}

	int getNumberInvocations() {
	    return numberInvocations;
	}

	@Override
	public void connect(com.alpha.pineapple.model.configuration.Resource resource, Credential credential)
		throws SessionConnectException {
	    // NO-OP
	}

	@Override
	public void disconnect() throws SessionDisconnectException {
	    // NO-OP
	}

	@Override
	public com.alpha.pineapple.model.configuration.Resource getResource() {
	    // NO-OP
	    return null;
	}

	@Override
	public Credential getCredential() {
	    numberInvocations++;
	    // logger.debug("numberInvocations="+numberInvocations);
	    // logger.debug("numberExceptionsToThrow ="+numberExceptionsToThrow
	    // );
	    if (numberExceptionsToThrow == 0)
		return value;
	    numberExceptionsToThrow--;
	    throw new RuntimeException("Exception #" + numberInvocations);
	}

    }

    /**
     * First index.
     */
    static final int FIRST_INDEX = 0;

    /**
     * Session retry proxy factory factory.
     */
    @Resource
    ObjectFactory<SessionRetryProxyFactory> sessionRetryProxyFactoryFactory;

    /**
     * Execution result factory.
     */
    @Resource
    ExecutionResultFactory executionResultFactory;

    /**
     * Subject under test. Instance is created by
     * sessionRetryProxyFactoryFactory.
     */
    SessionRetryProxyFactory sessionRetryProxyFactory;

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Random key.
     */
    String randomKey;

    /**
     * Mock credential.
     */
    Credential mockCredential;

    /**
     * Random ID.
     */
    String randomId;

    /**
     * Random description.
     */
    String randomDescription;

    /**
     * Execution result.
     */
    ExecutionResult result;

    @Before
    public void setUp() throws Exception {
	randomKey = RandomStringUtils.randomAlphabetic(10);
	mockCredential = createMock(Credential.class);
	randomId = RandomStringUtils.randomAlphabetic(10);
	randomDescription = RandomStringUtils.randomAlphabetic(10);

	// create result
	result = executionResultFactory.startExecution(randomDescription);

	// create factory
	sessionRetryProxyFactory = sessionRetryProxyFactoryFactory.getObject();
	assertNotNull(sessionRetryProxyFactory);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test that factory factory can be gotten from context.
     */
    @Test
    public void testCanGetFactoryFactoryFromContext() {
	assertNotNull(sessionRetryProxyFactoryFactory);
    }

    /**
     * Test factory can decorate test class.
     */
    @Test
    public void testFactoryReturnsDefinedProxy() {
	SessionWithNoConstructors session = new SessionWithNoConstructors();

	// decorate
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	assertNotNull(proxiedObject);
    }

    /**
     * Test proxy creation fails if session is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testProxyCreationFailsIfSessionIsNull() {

	// decorate
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(null, result);

	// test
	assertNull(proxiedObject);
    }

    /**
     * Test proxy creation fails if result is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testProxyCreationFailsIfResultIsNull() {
	Session session = new SessionWithNoConstructors();

	// decorate
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, null);

	// test
	assertNull(proxiedObject);
    }

    /**
     * Test that session class with public n-arg constructor can be handled and
     * proxied.
     * 
     * Class has public constructor which requires arguments.
     */
    @Test
    public void testCreateProxyForSessionWithPublicOneArgConstructor() {
	Session session = new SessionWithPublicOneArgConstructor(mockCredential);
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	assertNotNull(proxiedObject);
	assertEquals(mockCredential, session.getCredential());
	assertEquals(mockCredential, proxiedObject.getCredential());

    }

    /**
     * Test that session class with n-arg constructor can be handled and
     * proxied.
     * 
     * Class has private constructor which requires arguments.
     */
    @Test
    public void testCreateProxyForSessionWithPrivateOneArgConstructor() {
	Session session = new SessionWithPrivateOneArgConstructor(mockCredential);
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	assertNotNull(proxiedObject);
	assertEquals(mockCredential, session.getCredential());
	assertEquals(mockCredential, proxiedObject.getCredential());
    }

    /**
     * Test that session class with public no-arg constructor can be handled and
     * proxied.
     * 
     * Class has public constructor which requires no arguments.
     */
    @Test
    public void testCreateProxyForSessionWithPublicNoArgConstructor() {
	Session session = new SessionWithPublicNoArgConstructor();
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	assertNotNull(proxiedObject);
	assertEquals(null, session.getCredential());
	assertEquals(null, proxiedObject.getCredential());

    }

    /**
     * Test that session class with private no-arg constructor can be handled
     * and proxied.
     * 
     * Class has private constructor which requires no arguments.
     */
    @Test
    public void testCreateProxyForSessionWithPrivateNoArgConstructor() {
	Session session = new SessionWithPrivateNoArgConstructor();
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	assertNotNull(proxiedObject);
	assertEquals(null, session.getCredential());
	assertEquals(null, proxiedObject.getCredential());
    }

    /**
     * Test that instanceof operator works with proxy.
     * 
     * Class has public constructor which requires no arguments.
     */
    @Test
    public void testInstanceOfOperatorWorksWithCreatedProxyForSessionWithPublicNoArgConstructor() {
	Session testObject = new SessionWithPublicNoArgConstructor();
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(testObject, result);

	// test
	assertNotNull(proxiedObject);
	assertTrue(proxiedObject instanceof Session);
    }

    /**
     * Test that exception triggers retry functionality.
     * 
     * Session has public constructor which requires no arguments.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void test1xExceptionsTriggers2xRetries() throws Exception {
	Session session = new ExceptingSessionWithNoConstructors();
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	assertNotNull(proxiedObject);
	((ExceptingSessionWithNoConstructors) session).configureNumberExceptionsToThrow(1);
	assertEquals(null, proxiedObject.getCredential()); // invoke to trigger
							   // exception
	assertEquals(2, ((ExceptingSessionWithNoConstructors) session).getNumberInvocations()); // number
												// invocations:
												// 1
												// (initial)
												// +
												// 1
												// (retry)
    }

    /**
     * Test that exception triggers retry functionality.
     * 
     * Session has public constructor which requires no arguments.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void test2xExceptionsTriggers3xRetries() throws Exception {
	Session session = new ExceptingSessionWithNoConstructors();
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	assertNotNull(proxiedObject);
	((ExceptingSessionWithNoConstructors) session).configureNumberExceptionsToThrow(2);
	assertEquals(null, proxiedObject.getCredential()); // invoke to trigger
							   // exception
	assertEquals(3, ((ExceptingSessionWithNoConstructors) session).getNumberInvocations()); // number
												// invocations:
												// 1
												// (initial)
												// +
												// 2
												// (retry)
    }

    /**
     * Test that exception triggers retry functionality.
     * 
     * Class has public constructor which requires no arguments.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void test3xExceptionsTriggers3xRetries() throws Exception {
	Session session = new ExceptingSessionWithNoConstructors();
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	assertNotNull(proxiedObject);
	((ExceptingSessionWithNoConstructors) session).configureNumberExceptionsToThrow(3);
	assertEquals(null, proxiedObject.getCredential()); // invoke to trigger
							   // exception
	assertEquals(4, ((ExceptingSessionWithNoConstructors) session).getNumberInvocations()); // number
												// invocations:
												// 1
												// (initial)
												// +
												// 3
												// (retry)
    }

    /**
     * Test that exception triggers retry functionality.
     * 
     * Class has public constructor which requires no arguments.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test(expected = Exception.class)
    public void test4xExceptionsTriggers3xRetriesAndFailure() throws Exception {
	Session session = new ExceptingSessionWithNoConstructors();
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	assertNotNull(proxiedObject);
	((ExceptingSessionWithNoConstructors) session).configureNumberExceptionsToThrow(4);
	assertEquals(null, proxiedObject.getCredential()); // invoke to trigger
							   // exception
    }

    /**
     * Test that two different classes can be handled in sequence.
     * 
     * Class has public constructor which requires no arguments.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testCanProxyTwoClassesInSequence() throws Exception {
	Session session = new ExceptingSessionWithNoConstructors();
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);
	Session session2 = new ExceptingSessionWithNoConstructors();
	Session proxiedObject2 = sessionRetryProxyFactory.decorateWithProxy(session2, result);

	// test
	assertNotNull(proxiedObject);
	assertNotNull(proxiedObject2);

	((ExceptingSessionWithNoConstructors) session).configureNumberExceptionsToThrow(2);
	((ExceptingSessionWithNoConstructors) session2).configureNumberExceptionsToThrow(2);

	assertEquals(null, proxiedObject.getCredential()); // invoke to trigger
							   // exception
	assertEquals(3, ((ExceptingSessionWithNoConstructors) session).getNumberInvocations()); // number
												// invocations:
												// 1
												// (initial)
												// +
												// 2
												// (retry)

	assertEquals(null, proxiedObject2.getCredential()); // invoke to trigger
							    // exception
	assertEquals(3, ((ExceptingSessionWithNoConstructors) session2).getNumberInvocations()); // number
												 // invocations:
												 // 1
												 // (initial)
												 // +
												 // 2
												 // (retry)
    }

    /**
     * Test that two different classes can be handled in sequence.
     * 
     * Class has public constructor which requires no arguments.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testCanProxyTwoClassesInSequence2() throws Exception {
	Session session = new ExceptingSessionWithNoConstructors();
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);
	Session session2 = new ExceptingSessionWithNoConstructors();
	Session proxiedObject2 = sessionRetryProxyFactory.decorateWithProxy(session2, result);

	// test
	assertNotNull(proxiedObject);
	assertNotNull(proxiedObject2);

	((ExceptingSessionWithNoConstructors) session).configureNumberExceptionsToThrow(2);
	assertEquals(null, proxiedObject.getCredential()); // invoke to trigger
							   // exception
	assertEquals(3, ((ExceptingSessionWithNoConstructors) session).getNumberInvocations()); // number
												// invocations:
												// 1
												// (initial)
												// +
												// 2
												// (retry)

	((ExceptingSessionWithNoConstructors) session2).configureNumberExceptionsToThrow(2);
	assertEquals(null, proxiedObject2.getCredential()); // invoke to trigger
							    // exception
	assertEquals(3, ((ExceptingSessionWithNoConstructors) session2).getNumberInvocations()); // number
												 // invocations:
												 // 1
												 // (initial)
												 // +
												 // 2
												 // (retry)

	((ExceptingSessionWithNoConstructors) session).configureNumberExceptionsToThrow(2);
	assertEquals(null, proxiedObject.getCredential()); // invoke to trigger
							   // exception
	assertEquals(3, ((ExceptingSessionWithNoConstructors) session).getNumberInvocations()); // number
												// invocations:
												// 1
												// (initial)
												// +
												// 2
												// (retry)
    }

    /**
     * Test that retry logic is reset across invocations.
     * 
     * Class has public constructor which requires no arguments.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testRetryLogicIsResetAcrossInvocations() throws Exception {
	Session session = new ExceptingSessionWithNoConstructors();
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	assertNotNull(proxiedObject);

	((ExceptingSessionWithNoConstructors) session).configureNumberExceptionsToThrow(2);
	assertEquals(null, proxiedObject.getCredential()); // invoke to trigger
							   // exception
	assertEquals(3, ((ExceptingSessionWithNoConstructors) session).getNumberInvocations()); // number
												// invocations:
												// 1
												// (initial)
												// +
												// 2
												// (retry)

	((ExceptingSessionWithNoConstructors) session).configureNumberExceptionsToThrow(2);
	assertEquals(null, proxiedObject.getCredential()); // invoke to trigger
							   // exception
	assertEquals(3, ((ExceptingSessionWithNoConstructors) session).getNumberInvocations()); // number
												// invocations:
												// 1
												// (initial)
												// +
												// 2
												// (retry)
    }

    /**
     * Test that no information about a retry is registered on a successful
     * connect.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testNoRetryInformationIsAddedToResultOnSuccessfulConnect() throws Exception {
	com.alpha.pineapple.model.configuration.Resource resource;
	resource = createMock(com.alpha.pineapple.model.configuration.Resource.class);
	Credential credential = createMock(Credential.class);
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	replay(session);

	// create proxy
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	assertNotNull(proxiedObject);

	// connect
	proxiedObject.connect(resource, credential);

	// test
	Map<String, String> messages = result.getMessages();
	assertFalse(messages.containsKey(MSG_SESSION));
	verify(session);
    }

    /**
     * Test that session throws expected exception after exhausting the retry
     * policy due to failed connects.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test(expected = SessionConnectException.class)
    public void testThrowsExpectedExceptionAfterMultipleFailedConnects() throws Exception {
	com.alpha.pineapple.model.configuration.Resource resource;
	resource = createMock(com.alpha.pineapple.model.configuration.Resource.class);
	Credential credential = createMock(Credential.class);
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	expectLastCall().andThrow(new SessionConnectException(randomDescription)).anyTimes();
	replay(session);

	// create proxy
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	assertNotNull(proxiedObject);

	// connect
	proxiedObject.connect(resource, credential);

	// test - never invoked due to exception
	verify(session);
    }

    /**
     * Test that information about a retry is registered on failed connect.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testRetryInformationIsAddedToResultOnFailedConnect() throws Exception {
	com.alpha.pineapple.model.configuration.Resource resource;
	resource = createMock(com.alpha.pineapple.model.configuration.Resource.class);
	Credential credential = createMock(Credential.class);
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	expectLastCall().andThrow(new SessionConnectException(randomDescription)).times(1);
	expectLastCall().times(1);
	replay(session);
	// create proxy
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	Map<String, String> messages = result.getMessages();
	assertFalse(messages.containsKey(MSG_SESSION));
	assertNotNull(proxiedObject);

	// connect
	proxiedObject.connect(resource, credential);

	// test
	messages = result.getMessages();
	assertTrue(messages.containsKey(MSG_SESSION));
	String message = messages.get(MSG_SESSION);

	// count occurrences of message srl.session_connect_error
	assertEquals(1, StringUtils.countMatches(message, "Failed to connect to session due to the error"));
	verify(session);
    }

    /**
     * Test that information about a retry is registered on multiple failed
     * connects.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testRetryInformationIsAddedToResultOnFailedConnects() throws Exception {
	com.alpha.pineapple.model.configuration.Resource resource;
	resource = createMock(com.alpha.pineapple.model.configuration.Resource.class);
	Credential credential = createMock(Credential.class);
	Session session = createMock(Session.class);
	session.connect(resource, credential);
	expectLastCall().andThrow(new SessionConnectException(randomDescription)).times(2);
	expectLastCall().times(1);
	replay(session);
	// create proxy
	Session proxiedObject = sessionRetryProxyFactory.decorateWithProxy(session, result);

	// test
	Map<String, String> messages = result.getMessages();
	assertFalse(messages.containsKey(MSG_SESSION));
	assertNotNull(proxiedObject);

	// connect
	proxiedObject.connect(resource, credential);

	// test
	messages = result.getMessages();
	assertTrue(messages.containsKey(MSG_SESSION));
	String message = messages.get(MSG_SESSION);

	// count occurrences of message srl.session_connect_error
	assertEquals(2, StringUtils.countMatches(message, "Failed to connect to session due to the error"));
	verify(session);
    }

}
