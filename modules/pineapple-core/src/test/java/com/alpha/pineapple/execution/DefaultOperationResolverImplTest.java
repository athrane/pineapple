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

package com.alpha.pineapple.execution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Unit test of the class {@linkplain DefaultOperationResolverImpl}.
 */
public class DefaultOperationResolverImplTest {

	/**
	 * SUT.
	 */
	OperationResolver resolver;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * Mock result.
	 */
	ExecutionResult result;

	/**
	 * Random operation..
	 */
	String randomOperation;

	/**
	 * Random operation.
	 */
	String randomOperation2;

	@Before
	public void setUp() throws Exception {
		randomOperation = RandomStringUtils.randomAlphabetic(16);
		randomOperation2 = RandomStringUtils.randomAlphabetic(16);

		// create resolver
		resolver = new DefaultOperationResolverImpl();

		result = new ExecutionResultImpl("root");

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// inject message provider
		ReflectionTestUtils.setField(resolver, "messageProvider", messageProvider);

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
	}

	/**
	 * Can resolve null target operation.
	 */
	@Test
	public void testResolveNullTargetOperation() {
		assertFalse(resolver.restrictOperationFromExecution(randomOperation, null, result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve empty target operation.
	 */
	@Test
	public void testResolveEmptyTargetOperation() {
		assertFalse(resolver.restrictOperationFromExecution(randomOperation, "", result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve equal target operation.
	 */
	@Test
	public void testResolveEqualTargetOperation() {
		assertFalse(resolver.restrictOperationFromExecution(randomOperation, randomOperation, result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve equal target operation.
	 */
	@Test
	public void testResolveEqualTargetOperation2() {
		assertFalse(resolver.restrictOperationFromExecution(randomOperation, "    " + randomOperation, result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve equal target operation.
	 */
	@Test
	public void testResolveEqualTargetOperation3() {
		assertFalse(resolver.restrictOperationFromExecution(randomOperation, randomOperation + "    ", result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve wildcard target operation.
	 */
	@Test
	public void testResolveWildcardTargetOperation() {
		assertFalse(resolver.restrictOperationFromExecution(randomOperation, CoreConstants.WILDCARD_OPERATION, result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve unmatched target operation.
	 */
	@Test
	public void testResolveUnmatchedTargetOperation() {
		assertTrue(resolver.restrictOperationFromExecution(randomOperation, randomOperation2, result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve empty list target operation.
	 */
	@Test
	public void testResolveEmptyListTargetOperation() {
		assertTrue(resolver.restrictOperationFromExecution(randomOperation, "{}", result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve empty list target operation.
	 */
	@Test
	public void testResolveEmptyListTargetOperation2() {
		assertTrue(resolver.restrictOperationFromExecution(randomOperation, "{   }", result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve matched list target operation.
	 */
	@Test
	public void testResolveMatchedListTargetOperation() {
		assertFalse(resolver.restrictOperationFromExecution(randomOperation, "{" + randomOperation + "}", result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve matched list target operation.
	 */
	@Test
	public void testResolveMatchedListTargetOperation2() {
		assertFalse(resolver.restrictOperationFromExecution(randomOperation,
				"{" + randomOperation + "," + randomOperation + "}", result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve matched list target operation.
	 */
	@Test
	public void testResolveMatchedListTargetOperation3() {
		assertFalse(resolver.restrictOperationFromExecution(randomOperation,
				"{" + randomOperation + "," + randomOperation2 + "}", result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve wildcard matched list target operation.
	 */
	@Test
	public void testResolveMatchedWildcardListTargetOperation() {
		assertFalse(resolver.restrictOperationFromExecution(randomOperation, "{*}", result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve wildcard matched list target operation.
	 */
	@Test
	public void testResolveMatchedWildcardListTargetOperation2() {
		assertFalse(resolver.restrictOperationFromExecution(randomOperation, "{   *   }  ", result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve wildcard matched list target operation.
	 */
	@Test
	public void testResolveMatchedWildcardListTargetOperation3() {
		assertFalse(resolver.restrictOperationFromExecution(randomOperation, "{*," + randomOperation2 + "}", result));
		assertEquals(1, result.getMessages().keySet().size());
	}

	/**
	 * Can resolve unmatched list target operation.
	 */
	@Test
	public void testResolveUnMatchedListTargetOperation3() {
		assertTrue(resolver.restrictOperationFromExecution(randomOperation, "{" + randomOperation2 + "}", result));
		assertEquals(1, result.getMessages().keySet().size());
	}

}
