/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
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

package com.alpha.javautils;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.session.Session;

/**
 * Unit test of the class {@linkplain OperationUtils }.
 */
public class OperationUtilsTest {

	/**
	 * Test interface.
	 */
	interface SessionSubType extends Session {

	}

	/**
	 * Object under test.
	 */
	OperationUtils operationUtils;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	@Before
	public void setUp() throws Exception {

		// create operation utility
		operationUtils = new OperationUtilsImpl();

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// inject message source
		ReflectionTestUtils.setField(operationUtils, "messageProvider", messageProvider, MessageProvider.class);

		// complete mock source initialization
		IAnswer<String> answer = new MessageProviderAnswerImpl();

		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.expect(
				messageProvider.getMessage((String) EasyMock.isA(String.class), (Object[]) EasyMock.anyObject()));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.replay(messageProvider);

	}

	@After
	public void tearDown() throws Exception {
		operationUtils = null;
	}

	/**
	 * Test string type can be validated.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testValidateContentTypeSucceedsWithString() throws Exception {

		Object content = "a string";
		Class<?>[] legalTypes = { String.class };
		// execute
		operationUtils.validateContentType(content, legalTypes);

		// if we got here then validation succeeded
	}

	/**
	 * Test string type can be validated among multiple values in the set.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testValidateContentTypeSucceedsWithMultipleValues() throws Exception {

		Object content = "a string";
		Class<?>[] legalTypes = { Integer.class, String.class };
		// execute
		operationUtils.validateContentType(content, legalTypes);

		// if we got here then validation succeeded
	}

	/**
	 * Test empty set results in validation failure.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = PluginExecutionFailedException.class)
	public void testValidateContentTypeFailsWithEmptySet() throws Exception {

		Object content = "a string";
		Class<?>[] legalTypes = {};

		// execute trigger exception
		operationUtils.validateContentType(content, legalTypes);
	}

	/**
	 * Test validation fails if object can't be cast to type
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = PluginExecutionFailedException.class)
	public void testValidateContentTypeFailsIfObjectCantBeCastToType() throws Exception {

		Object content = "a string";
		Class<?>[] legalTypes = { Integer.class };

		// execute trigger exception
		operationUtils.validateContentType(content, legalTypes);
	}

	/**
	 * Test session type can be validated.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testValidateSessionTypeSucceedsWithSession() throws Exception {

		// create session mock
		Session session = EasyMock.createMock(Session.class);

		// execute
		operationUtils.validateSessionType(session, session.getClass());

		// if we got here then validation succeeded
	}

	/**
	 * Test session sub type can be validated.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testValidateSessionTypeSucceedsWithSessionSubType() throws Exception {

		// create session mock
		Session session = EasyMock.createMock(SessionSubType.class);

		// execute
		operationUtils.validateSessionType(session, SessionSubType.class);

		// if we got here then validation succeeded
	}

	/**
	 * Test validation fails if session type isn't session
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = PluginExecutionFailedException.class)
	public void testValidateSessionTypeFailsIfTypeinstSession() throws Exception {

		// create session mock
		Session session = EasyMock.createMock(SessionSubType.class);

		// execute
		operationUtils.validateSessionType(session, String.class);
	}

}
