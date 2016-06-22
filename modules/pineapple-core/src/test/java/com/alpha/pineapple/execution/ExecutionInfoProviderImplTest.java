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

package com.alpha.pineapple.execution;

import static org.junit.Assert.assertEquals;

import org.apache.commons.chain.Context;
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
 * Unit test of the class {@link ExecutionInfoProviderImpl}.
 */
public class ExecutionInfoProviderImplTest {

    /**
     * Object under test.
     */
    ExecutionInfoProviderImpl provider;

    /**
     * Mock execution info.
     */
    ExecutionInfo info;

    /**
     * Mock execution result.
     */
    ExecutionResult result;

    /**
     * Mock execution context.
     */
    Context context;

    /**
     * Mock message provider for I18N support.
     */
    MessageProvider messageProvider;

    /**
     * Execution context repository.
     */
    ExecutionContextRepository executionContextRepository;

    @Before
    public void setUp() throws Exception {
	provider = new ExecutionInfoProviderImpl();

	// create mock repository
	executionContextRepository = EasyMock.createMock(ExecutionContextRepository.class);

	// inject mocks
	ReflectionTestUtils.setField(provider, "executionContextRepository", executionContextRepository);

	// create mock provider
	messageProvider = EasyMock.createMock(MessageProvider.class);

	// inject message provider
	ReflectionTestUtils.setField(provider, "messageProvider", messageProvider);

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
	provider = null;
	info = null;
	result = null;
	executionContextRepository = null;
    }

    /**
     * Test that result can be retrieved.
     */
    @Test
    public void testGet() {

	// complete mock result initialization
	result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.replay(result);

	// complete mock info initialization
	info = EasyMock.createMock(ExecutionInfo.class);
	EasyMock.replay(info);

	// create context mock
	context = EasyMock.createMock(Context.class);
	EasyMock.expect(context.containsKey(CoreConstants.EXECUTION_INFO_KEY)).andReturn(true);
	EasyMock.expect(context.get(CoreConstants.EXECUTION_INFO_KEY)).andReturn(info);
	EasyMock.replay(context);

	// complete repository mock
	EasyMock.expect(executionContextRepository.get(result)).andReturn(context);
	EasyMock.replay(executionContextRepository);

	// test
	assertEquals(info, provider.get(result));

	// verify
	EasyMock.verify(context);
	EasyMock.verify(executionContextRepository);
	EasyMock.verify(info);
	EasyMock.verify(result);
    }

    /**
     * Test that undefined result is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRejectsUndefinedResult() throws Exception {

	// complete mock result initialization
	result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.replay(result);

	// complete mock info initialization
	info = EasyMock.createMock(ExecutionInfo.class);
	EasyMock.replay(info);

	// create context mock
	context = EasyMock.createMock(Context.class);
	EasyMock.replay(context);

	// complete repository mock
	EasyMock.replay(executionContextRepository);

	// invoke
	try {
	    provider.get(null);
	} catch (Exception e) {
	    throw e; // rethrow to verify mocks
	} finally {

	    // verify
	    EasyMock.verify(context);
	    EasyMock.verify(executionContextRepository);
	    EasyMock.verify(info);
	    EasyMock.verify(result);
	}
    }

    /**
     * Test that get info fails if context isn't defined in context repository.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test(expected = ExecutionInfoNotFoundException.class)
    public void testGetFailsIfContextIsntDefined() throws Exception {

	// complete mock result initialization
	result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.replay(result);

	// complete mock info initialization
	info = EasyMock.createMock(ExecutionInfo.class);
	EasyMock.replay(info);

	// create context mock
	context = EasyMock.createMock(Context.class);
	EasyMock.replay(context);

	// complete repository mock
	EasyMock.expect(executionContextRepository.get(result)).andReturn(null);
	EasyMock.replay(executionContextRepository);

	// invoke
	try {
	    provider.get(result);
	} catch (Exception e) {
	    throw e; // rethrow to verify mocks
	} finally {

	    // verify
	    EasyMock.verify(context);
	    EasyMock.verify(executionContextRepository);
	    EasyMock.verify(info);
	    EasyMock.verify(result);
	}
    }

    /**
     * Test that get info fails if context isn't defined in context repository.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test(expected = ExecutionInfoNotFoundException.class)
    public void testGetFailsIfExecutionInfoIsntDefinedInContext() throws Exception {

	// complete mock result initialization
	result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.replay(result);

	// complete mock info initialization
	info = EasyMock.createMock(ExecutionInfo.class);
	EasyMock.replay(info);

	// create context mock
	context = EasyMock.createMock(Context.class);
	EasyMock.expect(context.containsKey(CoreConstants.EXECUTION_INFO_KEY)).andReturn(false);
	EasyMock.replay(context);

	// complete repository mock
	EasyMock.expect(executionContextRepository.get(result)).andReturn(context);
	EasyMock.replay(executionContextRepository);

	// invoke
	try {
	    provider.get(result);
	} catch (Exception e) {
	    throw e; // rethrow to verify mocks
	} finally {

	    // verify
	    EasyMock.verify(context);
	    EasyMock.verify(executionContextRepository);
	    EasyMock.verify(info);
	    EasyMock.verify(result);
	}
    }

}
