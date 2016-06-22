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

package com.alpha.pineapple.plugin.agent.command;

import static com.alpha.pineapple.plugin.agent.AgentConstants.SCHEDULE_OPERATION_URI;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.ExecutionResultMapperImpl;
import com.alpha.pineapple.model.execution.ObjectFactory;
import com.alpha.pineapple.plugin.agent.session.AgentSession;

/**
 * Unit test of the class {@linkplain ScheduleOperationCommand}.
 */
public class ScheduleOperationCommandTest {

    /**
     * Object under test.
     */
    Command command;

    /**
     * Context.
     */
    Context context;

    /**
     * Mock execution result.
     */
    ExecutionResult executionResult;

    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;

    /**
     * Object factory.
     */
    ObjectFactory resultModelObjectFactory;

    /**
     * Random value.
     */
    String randomValue;

    /**
     * Random value.
     */
    String randomValue2;

    /**
     * Random value.
     */
    String randomEnvironment;

    /**
     * Random value.
     */
    String randomOperation;

    /**
     * Random value.
     */
    String randomModule;

    /**
     * Random host.
     */
    String randomHost;

    /**
     * Mock session
     */
    AgentSession session;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	randomValue = RandomStringUtils.randomAlphabetic(10);
	randomValue2 = RandomStringUtils.randomAlphabetic(10);
	randomEnvironment = RandomStringUtils.randomAlphabetic(10);
	randomOperation = RandomStringUtils.randomAlphabetic(10);
	randomModule = RandomStringUtils.randomAlphabetic(10);
	randomHost = RandomStringUtils.randomAlphabetic(10);

	// create object factory
	resultModelObjectFactory = new ObjectFactory();

	// create command
	command = new ScheduleOperationCommand();

	// create context
	context = new ContextBase();

	// create execution result
	executionResult = EasyMock.createMock(ExecutionResult.class);

	// create mock session
	session = EasyMock.createMock(AgentSession.class);

	// create mock provider
	messageProvider = EasyMock.createMock(MessageProvider.class);

	// inject
	ReflectionTestUtils.setField(command, "messageProvider", messageProvider);

	// complete mock source initialization
	IAnswer<String> answer = new MessageProviderAnswerImpl();

	EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
	EasyMock.expectLastCall().andAnswer(answer).anyTimes();
	EasyMock.expect(
		messageProvider.getMessage((String) EasyMock.isA(String.class), (Object[]) EasyMock.anyObject()));
	EasyMock.expectLastCall().andAnswer(answer).anyTimes();
	EasyMock.replay(messageProvider);

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Create result map with root result.
     * 
     * @param result
     *            root result.
     * 
     * @return result map with root result.
     */
    Map<Integer, ExecutionResult> createResultMap(ExecutionResult result) {
	Map<Integer, ExecutionResult> resultsMap = new HashMap<Integer, ExecutionResult>();
	resultsMap.put(ExecutionResultMapperImpl.LOCAL_ROOT_INDEX_INTEGER, result);
	return resultsMap;
    }

    /**
     * Test that command fails if context is undefined.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRejectsUndefinedContext() throws Exception {
	// create context
	context = null;

	// complete mock execution result setup
	EasyMock.replay(executionResult);

	// execute command
	command.execute(context);

	// Verify mocks
	EasyMock.verify(executionResult);
    }

    /**
     * Test that command fails if module property is undefined.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = CommandInitializationFailedException.class)
    public void testCommandFailsIfModuleKeyIsUndefinedInContext() throws Exception {
	// complete mock execution result setup
	EasyMock.replay(executionResult);

	// setup context
	context.put(ScheduleOperationCommand.DESCRIPTION_KEY, randomValue);
	context.put(ScheduleOperationCommand.ENVIRONMENT_KEY, randomEnvironment);
	context.put(ScheduleOperationCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(ScheduleOperationCommand.NAME_KEY, randomValue);
	context.put(ScheduleOperationCommand.OPERATION_KEY, randomOperation);
	context.put(ScheduleOperationCommand.SCHEDULING_EXPRESSION_KEY, randomValue);
	context.put(ScheduleOperationCommand.SESSION_KEY, session);

	// execute command
	command.execute(context);

	// Verify mocks
	EasyMock.verify(executionResult);
    }

    /**
     * Test that command fails if environment property is undefined.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = CommandInitializationFailedException.class)
    public void testCommandFailsIfEnvironmentKeyIsUndefinedInContext() throws Exception {
	// complete mock execution result setup
	EasyMock.replay(executionResult);

	// setup context
	context.put(ScheduleOperationCommand.DESCRIPTION_KEY, randomValue);
	context.put(ScheduleOperationCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(ScheduleOperationCommand.MODULE_KEY, randomModule);
	context.put(ScheduleOperationCommand.NAME_KEY, randomValue);
	context.put(ScheduleOperationCommand.OPERATION_KEY, randomOperation);
	context.put(ScheduleOperationCommand.SCHEDULING_EXPRESSION_KEY, randomValue);
	context.put(ScheduleOperationCommand.SESSION_KEY, session);

	// execute command
	command.execute(context);

	// Verify mocks
	EasyMock.verify(executionResult);
    }

    /**
     * Test that command fails if operation property is undefined.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = CommandInitializationFailedException.class)
    public void testCommandFailsIfOperationKeyIsUndefinedInContext() throws Exception {
	// complete mock execution result setup
	EasyMock.replay(executionResult);

	// setup context
	context.put(ScheduleOperationCommand.DESCRIPTION_KEY, randomValue);
	context.put(ScheduleOperationCommand.ENVIRONMENT_KEY, randomEnvironment);
	context.put(ScheduleOperationCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(ScheduleOperationCommand.MODULE_KEY, randomModule);
	context.put(ScheduleOperationCommand.NAME_KEY, randomValue);
	context.put(ScheduleOperationCommand.SCHEDULING_EXPRESSION_KEY, randomValue);
	context.put(ScheduleOperationCommand.SESSION_KEY, session);

	// execute command
	command.execute(context);

	// Verify mocks
	EasyMock.verify(executionResult);
    }

    /**
     * Test that command fails if session property is undefined.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = CommandInitializationFailedException.class)
    public void testCommandFailsIfSessionKeyIsUndefinedInContext() throws Exception {
	// complete mock execution result setup
	EasyMock.replay(executionResult);

	// setup context
	context.put(ScheduleOperationCommand.DESCRIPTION_KEY, randomValue);
	context.put(ScheduleOperationCommand.ENVIRONMENT_KEY, randomEnvironment);
	context.put(ScheduleOperationCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(ScheduleOperationCommand.MODULE_KEY, randomModule);
	context.put(ScheduleOperationCommand.NAME_KEY, randomValue);
	context.put(ScheduleOperationCommand.OPERATION_KEY, randomOperation);
	context.put(ScheduleOperationCommand.SCHEDULING_EXPRESSION_KEY, randomValue);

	// execute command
	command.execute(context);

	// Verify mocks
	EasyMock.verify(executionResult);
    }

    /**
     * Test that command can execute operation which returns single successful
     * root result.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCommandCanExecuteOperaton() throws Exception {

	// create URI
	String uriString = UriComponentsBuilder.fromUriString(SCHEDULE_OPERATION_URI).build()
		.expand(randomValue, randomModule, randomEnvironment, randomOperation, randomValue, randomValue).encode().toUriString();

	// create service URL
	String serviceUrl = new StringBuilder().append(randomHost).append("/").append(uriString).toString();

	// complete mock execution result setup
	executionResult.completeAsSuccessful(messageProvider, "soc.schedule_operation_completed");
	EasyMock.replay(executionResult);

	EasyMock.expect(session.createServiceUrl(uriString)).andReturn(serviceUrl);
	session.addServiceUrlMessage(serviceUrl, executionResult);
	MultiValueMap<String, Object> urlVariables = new LinkedMultiValueMap<String, Object>();
	session.httpPost(serviceUrl, urlVariables);
	EasyMock.replay(session);

	// setup context
	context.put(ScheduleOperationCommand.DESCRIPTION_KEY, randomValue);
	context.put(ScheduleOperationCommand.ENVIRONMENT_KEY, randomEnvironment);
	context.put(ScheduleOperationCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(ScheduleOperationCommand.MODULE_KEY, randomModule);
	context.put(ScheduleOperationCommand.NAME_KEY, randomValue);
	context.put(ScheduleOperationCommand.OPERATION_KEY, randomOperation);
	context.put(ScheduleOperationCommand.SCHEDULING_EXPRESSION_KEY, randomValue);
	context.put(ScheduleOperationCommand.SESSION_KEY, session);

	// execute command
	command.execute(context);

	// Verify mocks
	EasyMock.verify(executionResult);
	EasyMock.verify(session);
    }

}
