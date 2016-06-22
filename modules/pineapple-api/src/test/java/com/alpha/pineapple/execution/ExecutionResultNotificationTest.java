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

package com.alpha.pineapple.execution;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;

/**
 * Unit test of the class {@linkplain ExecutionResultNotificationImpl}
 */
public class ExecutionResultNotificationTest {

    /**
     * Object under test.
     */
    ExecutionResultNotification notification;

    /**
     * Mock execution result.
     */
    ExecutionResult result;

    @Before
    public void setUp() throws Exception {

	// create mock result
	result = EasyMock.createMock(ExecutionResult.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test that notification can be created.
     */
    @Test
    public void testCreateNotification() {
	EasyMock.replay(result);
	notification = ExecutionResultNotificationImpl.getInstance(result, ExecutionState.EXECUTING);

	// test
	assertNotNull(notification);
	EasyMock.verify(result);
    }

    /**
     * Test that created notification contains expected properties.
     */
    @Test
    public void testContainsExpectedProperties() {
	EasyMock.replay(result);
	ExecutionState state = ExecutionState.EXECUTING;
	notification = ExecutionResultNotificationImpl.getInstance(result, state);

	// test
	assertEquals(result, notification.getResult());
	assertEquals(state, notification.getState());
	EasyMock.verify(result);
    }

    /**
     * Test that creation fails with undefined result.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreationFailsWithUndefinedResult() {
	ExecutionState state = ExecutionState.EXECUTING;
	notification = ExecutionResultNotificationImpl.getInstance(null, state);
    }

    /**
     * Test that creation fails with undefined state.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreationFailsWithUndefinedState() {
	EasyMock.replay(result);
	notification = ExecutionResultNotificationImpl.getInstance(result, null);
    }

}
