/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2013 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 *   
 *    ExecutionResultNotificationTest.java created by AllanThrane on 31/05/2014 
 *    File revision  $Revision: 0.0 $
 */

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

package com.alpha.pineapple.execution.continuation;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.execution.ExecutionResult;

/**
 * Unit test of the class {@linkplain DefaultContinuationPolicyImpl}
 */
public class DefaultContinuationPolicyTest {

    /**
     * Object under test.
     */
    ContinuationPolicy policy;

    /**
     * Mock execution result.
     */
    ExecutionResult result;

    /**
     * Mock execution result.
     */
    ExecutionResult result2;

    @Before
    public void setUp() throws Exception {

	// create mock result
	result = EasyMock.createMock(ExecutionResult.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test that policy can be created.
     */
    @Test
    public void testCreatePolicy() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();

	// test
	assertNotNull(policy);
	EasyMock.verify(result);
    }

    /**
     * Test that created policy contains expected properties.
     */
    @Test
    public void testContainsExpectedInitialProperties() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();

	// test
	assertTrue(policy.continueExecution());
	assertTrue(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertNull(policy.getFailedResult());
	EasyMock.verify(result);
    }

    /**
     * Test that continue-on-failure directive can be enabled.
     * 
     * And that it doesn't influences the continuation state, since no failure
     * is registered.
     */
    @Test
    public void testCanEnableContinueOnFailure() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();
	policy.enableContinueOnFailure();

	// test
	assertTrue(policy.continueExecution());
	assertTrue(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertNull(policy.getFailedResult());
	EasyMock.verify(result);
    }

    /**
     * Test that continue-on-failure directive can be enabled multiple times.
     * 
     * And that it doesn't influences the continuation state but only once.
     */
    @Test
    public void testCanEnableContinueOnFailureMultipleTimes() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();
	policy.enableContinueOnFailure();

	// test
	assertTrue(policy.continueExecution());
	assertTrue(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertNull(policy.getFailedResult());

	policy.enableContinueOnFailure();

	// test
	assertTrue(policy.continueExecution());
	assertTrue(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertNull(policy.getFailedResult());

	EasyMock.verify(result);
    }

    /**
     * Test that continue-on-failure directive can be disabled.
     * 
     * And that it doesn't influences the continuation state, since no failure
     * is registered.
     */
    @Test
    public void testCanDisableContinueOnFailure() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();
	policy.disableContinueOnFailure();

	// test
	assertTrue(policy.continueExecution());
	assertFalse(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertNull(policy.getFailedResult());
	EasyMock.verify(result);
    }

    /**
     * Test that continue-on-failure directive can be disabled multiple times.
     * 
     * And that it doesn't influences the continuation state but only once.
     */
    @Test
    public void testCanDisableContinueOnFailureMultipleTimes() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();
	policy.disableContinueOnFailure();

	// test
	assertTrue(policy.continueExecution());
	assertFalse(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertNull(policy.getFailedResult());

	policy.disableContinueOnFailure();

	// test
	assertTrue(policy.continueExecution());
	assertFalse(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertNull(policy.getFailedResult());

	EasyMock.verify(result);
    }

    /**
     * Test that continue-on-failure directive can't be changed once it is
     * enabled.
     */
    @Test
    public void testCantChangeContinueOnFailureOnceEnabled() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();
	policy.enableContinueOnFailure();

	// test
	assertTrue(policy.continueExecution());
	assertTrue(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertNull(policy.getFailedResult());

	policy.disableContinueOnFailure();

	// test
	assertTrue(policy.continueExecution());
	assertTrue(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertNull(policy.getFailedResult());

	EasyMock.verify(result);
    }

    /**
     * Test that continue-on-failure directive can't be changed once it is
     * disabled.
     */
    @Test
    public void testCantChangeContinueOnFailureOnceDisabled() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();
	policy.disableContinueOnFailure();

	// test
	assertTrue(policy.continueExecution());
	assertFalse(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertNull(policy.getFailedResult());

	policy.enableContinueOnFailure();

	// test
	assertTrue(policy.continueExecution());
	assertFalse(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertNull(policy.getFailedResult());

	EasyMock.verify(result);
    }

    /**
     * Test that continue-on-failure state affects the continuation state when a
     * failure is registered.
     */
    @Test
    public void testRegisterFailedResult() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();
	policy.disableContinueOnFailure();
	policy.setFailed(result);

	// test
	assertFalse(policy.continueExecution());
	assertFalse(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertEquals(result, policy.getFailedResult());
	EasyMock.verify(result);
    }

    /**
     * Test that registering a failed result without setting the
     * continue-on-failure state is ignored.
     */
    @Test
    public void testRegisteringFailedResultWithoutDisablingContinueOnFailureIsIgnored() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();
	policy.setFailed(result);

	// test
	assertTrue(policy.continueExecution());
	assertTrue(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertEquals(result, policy.getFailedResult());
	EasyMock.verify(result);
    }

    /**
     * Test that registering a failed result only stores the first registered
     * result.
     * 
     * Results attempted to be stored afterwards are ignored.
     */
    @Test
    public void testRegisteringFailedResultOnlyStoresFirstResult() {
	result2 = EasyMock.createMock(ExecutionResult.class);
	EasyMock.replay(result);
	EasyMock.replay(result2);
	policy = DefaultContinuationPolicyImpl.getInstance();
	policy.setFailed(result);
	policy.setFailed(result2);

	// test
	assertTrue(policy.continueExecution());
	assertTrue(policy.isContinueOnFailure());
	assertFalse(policy.isCancelled());
	assertEquals(result, policy.getFailedResult());
	EasyMock.verify(result);
	EasyMock.verify(result2);
    }

    /**
     * Test that cancellation directive can be set.
     * 
     * And that it influences the continuation state.
     */
    @Test
    public void testCanCancelExecution() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();
	policy.setCancelled();

	// test
	assertFalse(policy.continueExecution());
	assertTrue(policy.isContinueOnFailure());
	assertTrue(policy.isCancelled());
	assertNull(policy.getFailedResult());
	EasyMock.verify(result);
    }

    /**
     * Test that cancellation directive can be set twice.
     */
    @Test
    public void testCanCancelExecutionTwice() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();
	policy.setCancelled();

	// test
	assertFalse(policy.continueExecution());
	assertTrue(policy.isContinueOnFailure());
	assertTrue(policy.isCancelled());
	assertNull(policy.getFailedResult());

	policy.setCancelled();

	// test
	assertFalse(policy.continueExecution());
	assertTrue(policy.isContinueOnFailure());
	assertTrue(policy.isCancelled());
	assertNull(policy.getFailedResult());

	EasyMock.verify(result);
    }

    /**
     * Test that setting failed null result fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreationFailsWithUndefinedState() {
	EasyMock.replay(result);
	policy = DefaultContinuationPolicyImpl.getInstance();
	policy.setFailed(null);
    }

}
