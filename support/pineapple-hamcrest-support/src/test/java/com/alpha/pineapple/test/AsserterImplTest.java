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

package com.alpha.pineapple.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsAnything;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.test.matchers.IsMapContainingKeys;
import com.alpha.pineapple.test.matchers.PineappleMatchers;

/**
 * Unit test of the {@link AsserterImpl} class.
 */
public class AsserterImplTest {

	/**
	 * Object under test.
	 */
	Asserter asserter;

	/**
	 * Mock execution result.
	 */
	ExecutionResult executionResult;

	@Before
	public void setUp() throws Exception {

		// create asserter
		asserter = new AsserterImpl();

		// create mock result
		executionResult = EasyMock.createMock(ExecutionResult.class);

		// set root execution object
		asserter.setExecutionResult(executionResult);
	}

	@After
	public void tearDown() throws Exception {
		asserter = null;
		executionResult = null;
	}

	/**
	 * Can assert with the {@link IsAnything} matcher class.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanAssertWithAnyhtingMatcher() {

		// create object which is matched
		String matchedObject = "matched-object";

		// create matcher
		Matcher matcher = CoreMatchers.anything();

		// create child mock result
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		childResult.setState(ExecutionState.SUCCESS);
		childResult.addMessage((String) EasyMock.isA(String.class), (String) EasyMock.isA(String.class));
		EasyMock.replay(childResult);

		// complete execution result mock initialization
		executionResult.addChild((String) EasyMock.isA(String.class));
		EasyMock.expectLastCall().andReturn(childResult);
		EasyMock.replay(executionResult);

		// assert object
		ExecutionResult result = asserter.assertObject(matchedObject, matcher, "some description");

		// test
		assertNotNull(result);

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(childResult);
	}

	/**
	 * Test that assertion with the {@link IsAnything} matcher class is successful
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSucceedsWithAnyhtingMatcher() {

		// create object which matched
		String matchedObject = "matched-object";

		// create matcher
		Matcher matcher = CoreMatchers.anything();

		// create child mock result
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		childResult.setState(ExecutionState.SUCCESS);
		childResult.addMessage((String) EasyMock.isA(String.class), (String) EasyMock.isA(String.class));
		EasyMock.replay(childResult);

		// complete execution result mock initialization
		executionResult.addChild((String) EasyMock.isA(String.class));
		EasyMock.expectLastCall().andReturn(childResult);
		EasyMock.replay(executionResult);

		// assert object
		ExecutionResult result = asserter.assertObject(matchedObject, matcher, "some description");

		// test
		assertEquals(childResult, result);

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(childResult);
	}

	/**
	 * Test that last assertion is stored correctly.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testLastAssertionReturnTrueIfAssertionSuccceded() {

		// create object which matched
		String matchedObject = "matched-object";

		// create matcher
		Matcher matcher = CoreMatchers.anything();

		// create child mock result
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		childResult.setState(ExecutionState.SUCCESS);
		childResult.addMessage((String) EasyMock.isA(String.class), (String) EasyMock.isA(String.class));
		EasyMock.expect(childResult.getState()).andReturn(ExecutionState.SUCCESS);
		EasyMock.replay(childResult);

		// complete execution result mock initialization
		executionResult.addChild((String) EasyMock.isA(String.class));
		EasyMock.expectLastCall().andReturn(childResult);
		EasyMock.replay(executionResult);

		// assert object
		asserter.assertObject(matchedObject, matcher, "some description");

		// test
		assertTrue(asserter.lastAssertionSucceeded());

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(childResult);
	}

	/**
	 * Test that last assertion result is stored correctly.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testLastAssertionResultReturnsCorrectResult() {

		// create object which matched
		String matchedObject = "matched-object";

		// create matcher
		Matcher matcher = CoreMatchers.anything();

		// create child mock result
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		childResult.setState(ExecutionState.SUCCESS);
		childResult.addMessage((String) EasyMock.isA(String.class), (String) EasyMock.isA(String.class));
		EasyMock.replay(childResult);

		// complete execution result mock initialization
		executionResult.addChild((String) EasyMock.isA(String.class));
		EasyMock.expectLastCall().andReturn(childResult);
		EasyMock.replay(executionResult);

		// assert object
		asserter.assertObject(matchedObject, matcher, "some description");

		// test
		assertEquals(childResult, asserter.getLastAssertionResult());

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(childResult);
	}

	/**
	 * Test that assertion with the not'ed {@link IsAnything} matcher class fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFailsWithNegatedAnyhtingMatcher() {

		// create object which matched
		String matchedObject = "matched-object";

		// create matcher
		Matcher matcher = CoreMatchers.not(CoreMatchers.anything());

		// create child mock result
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		childResult.setState(ExecutionState.FAILURE);
		childResult.addMessage((String) EasyMock.isA(String.class), (String) EasyMock.isA(String.class));
		EasyMock.replay(childResult);

		// complete execution result mock initialization
		executionResult.addChild((String) EasyMock.isA(String.class));
		EasyMock.expectLastCall().andReturn(childResult);
		EasyMock.replay(executionResult);

		// assert object
		ExecutionResult result = asserter.assertObject(matchedObject, matcher, "some description");

		// test
		assertEquals(childResult, result);

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(childResult);
	}

	/**
	 * Test that last assertion is stored correctly.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testLastAssertionReturnFalseIfAssertionFailed() {

		// create object which matched
		String matchedObject = "matched-object";

		// create matcher
		Matcher matcher = CoreMatchers.not(CoreMatchers.anything());

		// create child mock result
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		childResult.setState(ExecutionState.FAILURE);
		childResult.addMessage((String) EasyMock.isA(String.class), (String) EasyMock.isA(String.class));
		EasyMock.expect(childResult.getState()).andReturn(ExecutionState.FAILURE);
		EasyMock.replay(childResult);

		// complete execution result mock initialization
		executionResult.addChild((String) EasyMock.isA(String.class));
		EasyMock.expectLastCall().andReturn(childResult);
		EasyMock.replay(executionResult);

		// assert object
		asserter.assertObject(matchedObject, matcher, "some description");

		// test
		assertFalse(asserter.lastAssertionSucceeded());

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(childResult);
	}

	/**
	 * Test that last assertion result is stored correctly.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testLastAssertionResultsResturnsCorrectResult2() {

		// create object which matched
		String matchedObject = "matched-object";

		// create matcher
		Matcher matcher = CoreMatchers.not(CoreMatchers.anything());

		// create child mock result
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		childResult.setState(ExecutionState.FAILURE);
		childResult.addMessage((String) EasyMock.isA(String.class), (String) EasyMock.isA(String.class));
		EasyMock.replay(childResult);

		// complete execution result mock initialization
		executionResult.addChild((String) EasyMock.isA(String.class));
		EasyMock.expectLastCall().andReturn(childResult);
		EasyMock.replay(executionResult);

		// assert object
		asserter.assertObject(matchedObject, matcher, "some description");

		// test
		assertEquals(childResult, asserter.getLastAssertionResult());

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(childResult);
	}

	/**
	 * Test that last assertion initially returns false.
	 */
	@Test
	public void testLastAssertionIntiallyReturnsFalse() {

		// test
		assertFalse(asserter.lastAssertionSucceeded());
	}

	/**
	 * Test that get last assertion result initially returns false.
	 */
	@Test
	public void testLastAssertionResultIntiallyReturnsNull() {

		// test
		assertNull(asserter.getLastAssertionResult());
	}

	/**
	 * Test that correct execution state is set when the
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCompletedTestAssuccessfulSetsCorrectState() {

		// complete execution result initialization
		executionResult.completeAsSuccessful((MessageProvider) EasyMock.isA(MessageProvider.class),
				(String) EasyMock.isA(String.class), (Object[]) EasyMock.isA(Object[].class));
		EasyMock.replay(executionResult);

		// create mock message provider
		MessageProvider provider = EasyMock.createMock(MessageProvider.class);
		EasyMock.expect(provider.getMessage((String) EasyMock.isA(String.class), (Object[]) EasyMock.isNull()))
				.andReturn("some-message");
		EasyMock.replay(provider);

		// set execution result as successful
		Object[] args = { "arg1" };
		asserter.completeTestAsSuccessful(provider, "some-key", args);

		// test
		// verify mocks
		EasyMock.verify(executionResult);

	}

	/**
	 * Can assert with the {@link IsMapContainingKeys} matcher class.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanAssertWithIsMapContainingKeysMatcherAndEmptyMap() {

		// create mock set
		Set<Object> expectedKeys = EasyMock.createMock(Set.class);
		EasyMock.replay(expectedKeys);

		// create mock set
		Map<Object, Object> map = EasyMock.createMock(Map.class);
		Set<Object> keys = EasyMock.createMock(Set.class);
		EasyMock.expect(keys.containsAll(expectedKeys)).andReturn(true);
		EasyMock.replay(keys);
		EasyMock.expect(map.keySet()).andReturn(keys);
		EasyMock.replay(map);

		// create matcher
		Matcher matcher = PineappleMatchers.mapContainsKekys(expectedKeys);

		// create child mock result
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		childResult.setState(ExecutionState.SUCCESS);
		childResult.addMessage((String) EasyMock.isA(String.class), (String) EasyMock.isA(String.class));
		EasyMock.replay(childResult);

		// complete execution result mock initialization
		executionResult.addChild((String) EasyMock.isA(String.class));
		EasyMock.expectLastCall().andReturn(childResult);
		EasyMock.replay(executionResult);

		// assert object
		ExecutionResult result = asserter.assertObject(map, matcher, "some description");

		// test
		assertNotNull(result);

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(childResult);
	}

	/**
	 * Can assert with the {@link IsSetEmpty} matcher class.
	 */
	/*
	 * @SuppressWarnings("unchecked")
	 * 
	 * @Test public void testCanAssertWithIsSetEmptyMatcherAndNonEmptySet() {
	 * 
	 * // initialize sequences HttpInvocationSequence[] sequences = new
	 * HttpInvocationSequence[2] ;
	 * 
	 * // create mock set HttpInvocationsSet set = EasyMock.createMock(
	 * HttpInvocationsSet.class); EasyMock.expect( set.getSequences() ).andReturn(
	 * sequences ); EasyMock.expect( set.getSequences() ).andReturn( sequences );
	 * EasyMock.replay( set );
	 * 
	 * // create matcher Matcher matcher = PineappleMatchers.isSetEmpty();
	 * 
	 * // create child mock result ExecutionResult childResult =
	 * EasyMock.createMock( ExecutionResult.class );
	 * childResult.setState(ExecutionState.FAILURE); childResult.addMessage(
	 * (String) EasyMock.isA( String.class ), (String) EasyMock.isA( String.class
	 * )); EasyMock.expect(
	 * childResult.getState()).andReturn(ExecutionState.FAILURE); EasyMock.replay(
	 * childResult);
	 * 
	 * // complete execution result mock initialization
	 * executionResult.addChild((String) EasyMock.isA( String.class ) );
	 * EasyMock.expectLastCall().andReturn(childResult); EasyMock.replay(
	 * executionResult );
	 * 
	 * // assert object ExecutionResult result = asserter.assertObject(set ,
	 * matcher, "some description");
	 * 
	 * // test assertNotNull(result);
	 * 
	 * // verify mocks EasyMock.verify(executionResult);
	 * EasyMock.verify(childResult); }
	 */

}
