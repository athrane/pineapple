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
 */

package com.alpha.pineapple.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.javautils.ConcurrencyUtils;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ExecutionResultNotificationImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.execution.Message;
import com.alpha.pineapple.model.execution.MessageValue;
import com.alpha.pineapple.model.execution.Messages;
import com.alpha.pineapple.model.execution.ObjectFactory;
import com.alpha.pineapple.model.execution.Result;
import com.alpha.pineapple.model.execution.ResultSequence;
import com.alpha.pineapple.model.execution.Results;

/**
 * Unit test for the {@linkplain ExecutionResultMapperImpl} class.
 */
public class ExecutionResultMapperTest {

	/**
	 * First array list index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * First array list index.
	 */
	static final int SECOND_INDEX = 1;

	/**
	 * Random value.
	 */
	String randomText;

	/**
	 * Random value.
	 */
	String randomText2;

	/**
	 * Random value.
	 */
	String randomText3;

	/**
	 * Random value.
	 */
	String randomText4;

	/**
	 * Random value.
	 */
	String randomText5;

	/**
	 * Random value.
	 */
	String randomText6;

	/**
	 * Random value.
	 */
	String randomText7;

	/**
	 * Random value.
	 */
	int randomCorrelationId;

	/**
	 * Object under test.
	 */
	ExecutionResultMapper mapper;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * Object factory.
	 */
	ObjectFactory resultModelObjectFactory;

	@Before
	public void setUp() throws Exception {

		randomText = RandomStringUtils.randomAlphabetic(10);
		randomText2 = RandomStringUtils.randomAlphabetic(10);
		randomText3 = RandomStringUtils.randomAlphabetic(10);
		randomText4 = RandomStringUtils.randomAlphabetic(10);
		randomText5 = RandomStringUtils.randomAlphabetic(10);
		randomText6 = RandomStringUtils.randomAlphabetic(10);
		randomText7 = RandomStringUtils.randomAlphabetic(10);
		randomCorrelationId = RandomUtils.nextInt();

		// create mapper
		mapper = new ExecutionResultMapperImpl();

		resultModelObjectFactory = new ObjectFactory();
		ReflectionTestUtils.setField(mapper, "resultModelObjectFactory", resultModelObjectFactory);

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// inject message provider
		ReflectionTestUtils.setField(mapper, "messageProvider", messageProvider);

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
	 * Create model with single result with no messages.
	 * 
	 * @param description
	 *            description.
	 * @param state
	 *            execution state.
	 * 
	 * @return model result with no messages.
	 */
	Results createModelResultWithNoMessages(String description, ExecutionState state) {
		Results model = resultModelObjectFactory.createResults();
		model.setResultSequence(resultModelObjectFactory.createResultSequence());
		Result modelResult = resultModelObjectFactory.createResult();
		modelResult.setDescription(description);
		modelResult.setState(state.name());
		modelResult.setCorrelationId(randomCorrelationId);
		Messages modelMessages = resultModelObjectFactory.createMessages();
		modelResult.setMessages(modelMessages);
		model.getResultSequence().getResult().add(modelResult);
		return model;
	}

	/**
	 * Create model with single result with one message.
	 * 
	 * @param description
	 *            description.
	 * @param state
	 *            execution state.
	 * @param msgKey
	 *            message key.
	 * @param msgValue
	 *            message value.
	 * 
	 * @return model result with single messages.
	 */
	Results createModelResultWithOneMessage(String description, ExecutionState state, String msgKey, String msgValue) {
		Results model = resultModelObjectFactory.createResults();
		model.setResultSequence(resultModelObjectFactory.createResultSequence());
		Result modelResult = resultModelObjectFactory.createResult();
		modelResult.setDescription(description);
		modelResult.setState(state.name());
		modelResult.setCorrelationId(randomCorrelationId);
		Messages modelMessages = resultModelObjectFactory.createMessages();
		modelResult.setMessages(modelMessages);
		model.getResultSequence().getResult().add(modelResult);
		addMessageToModelResult(modelResult, msgKey, msgValue);
		return model;
	}

	/**
	 * Add message to model result.
	 * 
	 * @param modelResult
	 *            model result in model to which the message is added.
	 * @param msgKey
	 *            message key
	 * @param msgValue
	 *            message value
	 */
	void addMessageToModelResult(Result modelResult, String msgKey, String msgValue) {
		Messages modelMessages = modelResult.getMessages();
		Message message = resultModelObjectFactory.createMessage();
		message.setName(msgKey);
		MessageValue value = resultModelObjectFactory.createMessageValue();
		value.setValue(msgValue);
		message.getValue().add(value);
		modelMessages.getMessage().add(message);
	}

	/**
	 * Create model result with no upgraded results..
	 * 
	 * @param description
	 *            description.
	 * @param state
	 *            execution state.
	 * 
	 * @return model result with no upgraded results.
	 */
	Results createModelResultWithNoUpgradedResults(String description, ExecutionState state) {
		Results model = createModelResultWithNoMessages(description, state);
		model.setResultSequence(resultModelObjectFactory.createResultSequence());
		return model;
	}

	/**
	 * Get first model result in result sequence.
	 * 
	 * @param modelResults
	 *            model.
	 * 
	 * @return first model result in result sequence.
	 */
	Result getFirstModelResult(Results modelResults) {
		ResultSequence resultSequence = modelResults.getResultSequence();
		List<Result> resultList = resultSequence.getResult();
		Result modelResult = resultList.get(FIRST_INDEX);
		assertNotNull(modelResult);
		return modelResult;
	}

	/**
	 * Get second model result in result sequence.
	 * 
	 * @param modelResults
	 *            model.
	 * 
	 * @return second model result in result sequence.
	 */
	Result getSecondModelResult(Results modelResults) {
		ResultSequence resultSequence = modelResults.getResultSequence();
		List<Result> resultList = resultSequence.getResult();
		Result modelResult = resultList.get(SECOND_INDEX);
		assertNotNull(modelResult);
		return modelResult;
	}

	/**
	 * Map execution result fails if execution results is undefined.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMapResultsToModel_ExecutionResultFailsIfResultsIsUndefined() {

		// invoke
		mapper.mapNotificationsToModel(null);
	}

	/**
	 * Map execution result returns defined model.
	 */
	@Test
	public void testMapResultsToModel_ReturnsDefinedModel() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.SUCCESS);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		assertNotNull(modelResults);
		ResultSequence resultSequence = modelResults.getResultSequence();
		assertNotNull(resultSequence);
	}

	/**
	 * Map execution result returns empty model if no results is defined in input.
	 */
	@Test
	public void testMapResultsToModel_CanMapEmptyModel() {
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] {};

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		assertNotNull(modelResults);
		ResultSequence resultSequence = modelResults.getResultSequence();
		assertNotNull(resultSequence);
		List<Result> resultList = resultSequence.getResult();
		assertNotNull(resultList);
	}

	/**
	 * Map execution result to model containing model result.
	 */
	@Test
	public void testMapResultsToModel_ReturnedModelContainsResult() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.SUCCESS);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		ResultSequence resultSequence = modelResults.getResultSequence();
		assertNotNull(resultSequence);
		List<Result> resultList = resultSequence.getResult();
		assertNotNull(resultList);
		assertEquals(1, resultList.size());
	}

	/**
	 * Map execution result to model containing model result with expected
	 * description.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedDescription() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.SUCCESS);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertEquals(randomText, modelResult.getDescription());
	}

	/**
	 * Map execution result to model containing model result with expected state.
	 * 
	 * The state is mapped from the notification and not the result.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedState_Execution() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		result.setState(ExecutionState.ERROR);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertEquals(ExecutionState.EXECUTING.name(), modelResult.getState());
	}

	/**
	 * Map execution result to model containing model result with expected state.
	 * 
	 * The state is mapped from the notification and not the result.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedState_Computed() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		result.setState(ExecutionState.ERROR);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.COMPUTED);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertEquals(ExecutionState.COMPUTED.name(), modelResult.getState());
	}

	/**
	 * Map execution result to model containing model result with expected state.
	 * 
	 * The state is mapped from the notification and not the result.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedState_Success() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		result.setState(ExecutionState.ERROR);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.SUCCESS);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertEquals(ExecutionState.SUCCESS.name(), modelResult.getState());
	}

	/**
	 * Map execution result to model containing model result with expected state.
	 * 
	 * The state is mapped from the notification and not the result.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedState_Failure() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		result.setState(ExecutionState.ERROR);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.FAILURE);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertEquals(ExecutionState.FAILURE.name(), modelResult.getState());
	}

	/**
	 * Map execution result to model containing model result with expected state.
	 * 
	 * The state is mapped from the notification and not the result.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedState_Error() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		result.setState(ExecutionState.EXECUTING);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.ERROR);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertEquals(ExecutionState.ERROR.name(), modelResult.getState());
	}

	/**
	 * Map execution result to model containing model result with expected start
	 * time.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedStartTime() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };
		long startTime = result.getStartTime();

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertNotNull(modelResult.getStartTime());
		assertEquals(startTime, modelResult.getStartTime().longValue());
	}

	/**
	 * Map execution result to model containing model result with expected execution
	 * time.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedTime() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		result.setState(ExecutionState.SUCCESS);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.SUCCESS);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };
		long time = result.getTime();

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertNotNull(modelResult.getTime());
		assertEquals(time, modelResult.getTime().longValue());
	}

	/**
	 * Map execution result to model containing model result with expected execution
	 * time.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedTime2() throws Exception {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		ConcurrencyUtils.waitOneSec(); // wait one sec.

		// get time after mapping
		long time = result.getTime();

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertEquals(ExecutionState.EXECUTING, result.getState());
		assertNotNull(modelResult);
		assertNotNull(modelResult.getTime());
		assertTrue(time > modelResult.getTime().longValue());
	}

	/**
	 * Map execution result to model containing model result with no message.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsNoMessage() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertNotNull(modelResult);
		Messages modelMessages = modelResult.getMessages();
		assertNotNull(modelMessages);
		List<Message> modelMessagesContainer = modelMessages.getMessage();
		assertNotNull(modelMessagesContainer);
		assertEquals(0, modelMessagesContainer.size());
	}

	/**
	 * Map execution result to model containing model result with expected message.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedMessage() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };
		result.addMessage(randomText2, randomText3);

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertNotNull(modelResult);
		Messages modelMessages = modelResult.getMessages();
		assertNotNull(modelMessages);
		List<Message> modelMessagesContainer = modelMessages.getMessage();
		assertNotNull(modelMessagesContainer);
		assertEquals(1, modelMessagesContainer.size());
		Message modelMessage = modelMessagesContainer.get(0);
		assertNotNull(modelMessage);
		assertEquals(randomText2, modelMessage.getName());
		List<MessageValue> modelMessageValue = modelMessage.getValue();
		assertNotNull(modelMessageValue);
		assertEquals(1, modelMessageValue.size());
		MessageValue modelValue = modelMessageValue.get(0);
		assertNotNull(modelValue);
		assertEquals(randomText3, modelValue.getValue());
	}

	/**
	 * Map execution result to model containing model result with expected message.
	 * 
	 * Result contains two messages
	 * 
	 * Messages are added in sorted order.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedTwoMessages() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };
		result.addMessage("aa" + randomText2, randomText3);
		result.addMessage("bb" + randomText4, randomText5);

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertNotNull(modelResult);
		Messages modelMessages = modelResult.getMessages();
		assertNotNull(modelMessages);
		List<Message> modelMessagesContainer = modelMessages.getMessage();
		assertNotNull(modelMessagesContainer);
		assertEquals(2, modelMessagesContainer.size());

		// test message #1
		Message modelMessage = modelMessagesContainer.get(0);
		assertNotNull(modelMessage);
		assertEquals("aa" + randomText2, modelMessage.getName());
		List<MessageValue> modelMessageValue = modelMessage.getValue();
		assertNotNull(modelMessageValue);
		assertEquals(1, modelMessageValue.size());
		MessageValue modelValue = modelMessageValue.get(0);
		assertNotNull(modelValue);
		assertEquals(randomText3, modelValue.getValue());

		// test message #2
		modelMessage = modelMessagesContainer.get(1);
		assertNotNull(modelMessage);
		assertEquals("bb" + randomText4, modelMessage.getName());
		modelMessageValue = modelMessage.getValue();
		assertNotNull(modelMessageValue);
		assertEquals(1, modelMessageValue.size());
		modelValue = modelMessageValue.get(0);
		assertNotNull(modelValue);
		assertEquals(randomText5, modelValue.getValue());
	}

	/**
	 * Map execution result to model. Execution result contains one child result
	 * which is also provided in the sequence. Mapped model contains the two
	 * results.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedOneChildResult() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResult childResult = result.addChild(randomText2);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification notification2 = ExecutionResultNotificationImpl.getInstance(childResult,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification, notification2 };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		ResultSequence resultSequence = modelResults.getResultSequence();
		List<Result> resultList = resultSequence.getResult();
		assertEquals(2, resultList.size());
	}

	/**
	 * Map execution result to model. Execution result contains one child result
	 * which is also provided in the sequence. Mapped model contains the two
	 * results. The description of the mapped model child result is asserted.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedOneChildResultWithDescription() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResult childResult = result.addChild(randomText2);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification notification2 = ExecutionResultNotificationImpl.getInstance(childResult,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification, notification2 };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		ResultSequence resultSequence = modelResults.getResultSequence();
		List<Result> resultList = resultSequence.getResult();
		assertEquals(2, resultList.size());

		// test child #1
		Result modelChild = resultList.get(SECOND_INDEX);
		assertEquals(randomText2, modelChild.getDescription());
	}

	/**
	 * Map execution result to model. Execution result contains one child result
	 * which is also provided in the sequence. Mapped model contains the two
	 * results.
	 * 
	 * The state of the mapped model child result is asserted. The state is mapped
	 * from the notification and not the result.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedOneChildResultWithState_Success() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResult childResult = result.addChild(randomText2);
		// childResult.setState(ExecutionState.SUCCESS);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification notification2 = ExecutionResultNotificationImpl.getInstance(childResult,
				ExecutionState.SUCCESS);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification, notification2 };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		ResultSequence resultSequence = modelResults.getResultSequence();
		List<Result> resultList = resultSequence.getResult();
		assertEquals(2, resultList.size());

		// test result is executing
		Result rootModelResult = resultList.get(FIRST_INDEX);
		assertEquals(ExecutionState.EXECUTING.name(), rootModelResult.getState());

		// test child #1
		Result modelChild = resultList.get(SECOND_INDEX);
		assertEquals(ExecutionState.SUCCESS.name(), modelChild.getState());
	}

	/**
	 * Map execution result to model. Execution result contains two child results
	 * which is also provided in the sequence. Mapped model contains the three
	 * results.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedTwoChildResults() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResult childResult = result.addChild(randomText2);
		ExecutionResult childResult2 = result.addChild(randomText3);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification notification2 = ExecutionResultNotificationImpl.getInstance(childResult,
				ExecutionState.EXECUTING);
		ExecutionResultNotification notification3 = ExecutionResultNotificationImpl.getInstance(childResult2,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification, notification2,
				notification3 };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		ResultSequence resultSequence = modelResults.getResultSequence();
		List<Result> resultList = resultSequence.getResult();
		assertEquals(3, resultList.size());
	}

	/**
	 * Map execution result to model containing model result with expected
	 * correlation ID. Expected correlation ID is the hash code of the source
	 * execution results.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedCorrelationID() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		result.addMessage(randomText2, randomText3);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		assertNotNull(modelResult);
		assertEquals(result.hashCode(), modelResult.getCorrelationId().intValue());
	}

	/**
	 * Map execution result to model containing model result with expected parent
	 * correlation ID. Expected parent correlation ID is the hash code of the source
	 * root execution result.
	 */
	@Test
	public void testMapResultsToModel_MappedExecutionResultContainsExpectedParentCorrelationID() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResult childResult = result.addChild(randomText2);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification notification2 = ExecutionResultNotificationImpl.getInstance(childResult,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification, notification2 };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getSecondModelResult(modelResults);
		assertNotNull(modelResult);
		assertEquals(result.hashCode(), modelResult.getParentCorrelationId().intValue());
	}

	/**
	 * Map execution result to model. Execution result contains the execution result
	 * twice with the same state. Mapped model contains the two results.
	 */
	@Test
	public void testMapResultsToModel_SequenceCanContainExecutionResultTwice() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification notification2 = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification, notification2 };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		ResultSequence resultSequence = modelResults.getResultSequence();
		List<Result> resultList = resultSequence.getResult();
		assertEquals(2, resultList.size());
	}

	/**
	 * Map execution result to model. Execution result contains the execution result
	 * notification twice. Mapped model contains the two results.
	 */
	@Test
	public void testMapResultsToModel_SequenceCanContainNotificationTwice() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification, notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		ResultSequence resultSequence = modelResults.getResultSequence();
		List<Result> resultList = resultSequence.getResult();
		assertEquals(2, resultList.size());
	}

	/**
	 * Map execution result to model. Execution result contains the execution result
	 * twice with the same state. Mapped model contains the two results. The mapping
	 * of the result properties are asserted.
	 */
	@Test
	public void testMapResultsToModel_SequenceCanContainExecutionResultTwice2() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification notification2 = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification, notification2 };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		Result modelResult2 = getSecondModelResult(modelResults);
		assertEquals(modelResult.getCorrelationId(), modelResult2.getCorrelationId());
		assertEquals(modelResult.getDescription(), modelResult2.getDescription());
		assertEquals(modelResult.getParentCorrelationId(), modelResult2.getParentCorrelationId());
		assertEquals(modelResult.getStartTime(), modelResult2.getStartTime());
		assertEquals(modelResult.getState(), modelResult2.getState());
	}

	/**
	 * Map execution result to model. Execution result contains the execution result
	 * notification twice with the same state. Mapped model contains the two
	 * results. The mapping of the result properties are asserted.
	 */
	@Test
	public void testMapResultsToModel_SequenceCanContainExecutionResultNotificationTwice2() {
		ExecutionResult result = new ExecutionResultImpl(randomText);
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result,
				ExecutionState.EXECUTING);
		ExecutionResultNotification[] notifications = new ExecutionResultNotification[] { notification, notification };

		// map
		Results modelResults = mapper.mapNotificationsToModel(notifications);

		// test
		Result modelResult = getFirstModelResult(modelResults);
		Result modelResult2 = getSecondModelResult(modelResults);
		assertEquals(modelResult.getCorrelationId(), modelResult2.getCorrelationId());
		assertEquals(modelResult.getDescription(), modelResult2.getDescription());
		assertEquals(modelResult.getParentCorrelationId(), modelResult2.getParentCorrelationId());
		assertEquals(modelResult.getStartTime(), modelResult2.getStartTime());
		assertEquals(modelResult.getState(), modelResult2.getState());
	}

	/**
	 * Fails registering fails if root result is undefined.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateExecutionResultMap_FailsIfRootResultIsUndefined() {
		mapper.createExecutionResultMap(null);
	}

	/**
	 * Root result is registered at expected index.
	 */
	@Test
	public void testCreateExecutionResultMap_RootResultIsRegistered() {
		ExecutionResult result = new ExecutionResultImpl(randomText);

		// create map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(result);

		// test
		assertEquals(1, resultsMap.size());
		assertEquals(result, resultsMap.get(ExecutionResultMapperImpl.LOCAL_ROOT_INDEX_INTEGER));
	}

	/**
	 * Fails map fails if model is undefined.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMapModelToResults_FailsIfModelIsUndefined() {
		Map<Integer, ExecutionResult> resultsMap = new HashMap<Integer, ExecutionResult>();

		// map
		mapper.mapModelToResults(null, resultsMap);
	}

	/**
	 * Fails map fails if map is undefined.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMapModelToResults_FailsIfMapIsUndefined() {
		Results model = resultModelObjectFactory.createResults();

		// map
		mapper.mapModelToResults(model, null);
	}

	/**
	 * Map an empty model (without any model result) then no children is added to
	 * the root execution result.
	 */
	@Test
	public void testMapModelToResults_WithEmptyModelNoModelResultsIsAddedToResult() {
		Results model = resultModelObjectFactory.createResults();
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, resultsMap.size());
		assertEquals(0, executionResult.getChildren().length);
	}

	/**
	 * Map an empty model (without any model result) then no children is added to
	 * the root execution result.
	 */
	@Test
	public void testMapModelToResults_WithEmptyModelNoModelResultsIsAddedToResult2() {
		ExecutionResult rootResult = new ExecutionResultImpl(randomText);
		Results model = resultModelObjectFactory.createResults();
		model.setResultSequence(resultModelObjectFactory.createResultSequence());

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(rootResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, resultsMap.size());
		assertEquals(0, rootResult.getChildren().length);
	}

	/**
	 * Model with single result is added to execution result as child result.
	 */
	@Test
	public void testMapModelToResults_IsMappedAsChildResult() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
	}

	/**
	 * Model with single result is added to execution result as child result. Child
	 * result contains expected description.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedDescription() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
		assertEquals(randomText2, childResult.getDescription());
	}

	/**
	 * Model with single result is added to execution result as child result. Child
	 * result contains expected state.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedState_Executing() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
		assertEquals(ExecutionState.EXECUTING, childResult.getState());
	}

	/**
	 * Model with single result is added to execution result as child result. Child
	 * result contains expected state.
	 * 
	 * A new result with state "computed" is mapped to "executing" state as part of
	 * mapping process - the "computed" state is basically ignored.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedState_Computed() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.COMPUTED);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
		assertEquals(ExecutionState.EXECUTING, childResult.getState());
	}

	/**
	 * Model with single result is added to execution result as child result. Child
	 * result contains expected state.
	 * 
	 * A new result with state "success" is mapped to "executing" state as part of
	 * mapping process - the "success" state is basically ignored.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedState_Success() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.COMPUTED);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
		assertEquals(ExecutionState.EXECUTING, childResult.getState());
	}

	/**
	 * Model with single result is added to execution result as child result. Child
	 * result contains expected state.
	 * 
	 * A new result with state "failure" is mapped to "executing" state as part of
	 * mapping process - the "failure" state is basically ignored.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedState_Failure() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.FAILURE);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
		assertEquals(ExecutionState.EXECUTING, childResult.getState());
	}

	/**
	 * Model with single result is added to execution result as child result. Child
	 * result contains expected state.
	 * 
	 * A new result with state "error" is mapped to "executing" state as part of
	 * mapping process - the "error" state is basically ignored.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedState_Error() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.ERROR);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
		assertEquals(ExecutionState.EXECUTING, childResult.getState());
	}

	/**
	 * Map model result returns execution result containing model result with no
	 * message.
	 */
	@Test
	public void testMapModelToResults_ContainsNoMessage() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
		Map<String, String> messages = childResult.getMessages();
		assertNotNull(messages);
		assertEquals(0, messages.size());
	}

	/**
	 * Map model result returns execution result containing model result with
	 * expected message.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedMessage() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithOneMessage(randomText2, ExecutionState.EXECUTING, randomText3,
				randomText4);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
		Map<String, String> messages = childResult.getMessages();
		assertNotNull(messages);
		assertEquals(1, messages.size());
		assertTrue(messages.containsKey(randomText3));
		assertEquals(randomText4, childResult.getMessages().get(randomText3));
	}

	/**
	 * Map model result returns execution result containing model result with
	 * expected two messages.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedTwoMessages() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithOneMessage(randomText2, ExecutionState.EXECUTING, randomText3,
				randomText4);
		Result modelResult = getFirstModelResult(model);
		addMessageToModelResult(modelResult, randomText5, randomText6);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
		Map<String, String> messages = childResult.getMessages();
		assertNotNull(messages);
		assertEquals(2, messages.size());
		assertTrue(messages.containsKey(randomText3));
		assertEquals(randomText4, childResult.getMessages().get(randomText3));
		assertTrue(messages.containsKey(randomText5));
		assertEquals(randomText6, childResult.getMessages().get(randomText5));
	}

	/**
	 * Map model result returns execution result mapped model results is stored in
	 * result map under expected correlation ID, e.g. the correlation ID from the
	 * models result.
	 */
	@Test
	public void testMapModelToResults_ResultIsMappedWithExpectedCorrelationId() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		assertEquals(2, resultsMap.size()); // two entries, the root result and
		// the mapped result.
		assertTrue(resultsMap.containsKey(getFirstModelResult(model).getCorrelationId().intValue()));
	}

	/**
	 * Map model result returns execution result not containing start time from
	 * model result but from time when model result is mapped to result.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedStartTime() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// get time prior to mapping
		StopWatch watch = new StopWatch();
		watch.start();
		long timePriorToMapping = watch.getTime();

		// map
		mapper.mapModelToResults(model, resultsMap);

		// get time after mapping
		long timeAfterMapping = watch.getTime();

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
		assertTrue(timePriorToMapping < childResult.getStartTime());
		assertTrue(timeAfterMapping < childResult.getStartTime());
	}

	/**
	 * Map model result returns execution result not containing execution time from
	 * model result but from time when model result is mapped to result where state
	 * changes from executing..
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedTime() throws Exception {
		final int MS_TO_WAIT = 100;

		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// wait
		ConcurrencyUtils.waitSomeMilliseconds(MS_TO_WAIT);

		// map
		model = createModelResultWithNoMessages(randomText2, ExecutionState.SUCCESS);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
		assertTrue(MS_TO_WAIT <= childResult.getTime());
	}

	/**
	 * Model can be mapped twice with model content single root result is added to
	 * execution result as child result.
	 * 
	 * Correlation ID ensures that results aren't create twice.
	 */
	@Test
	public void testMapModelToResults_IsMappedAsChildResult_MappedTwice() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map twice
		mapper.mapModelToResults(model, resultsMap);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
	}

	/**
	 * Model can be mapped twice with model content single root result is added to
	 * execution result as child result.
	 * 
	 * Correlation Id ensures that results isn't create twice.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedDescription_MappedTwice() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map twice
		mapper.mapModelToResults(model, resultsMap);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);

		// description
		assertEquals(randomText2, childResult.getDescription());
	}

	/**
	 * Model can be mapped twice with model content single root result is added to
	 * execution result as child result.
	 * 
	 * Correlation Id ensures that results isn't create twice.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedNoMessages_MappedTwice() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map twice
		mapper.mapModelToResults(model, resultsMap);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);

		// no messages
		Map<String, String> messages = childResult.getMessages();
		assertNotNull(messages);
		assertEquals(0, messages.size());
	}

	/**
	 * Model can be mapped twice with model content single root result is added to
	 * execution result as child result.
	 * 
	 * Correlation Id ensures that results isn't create twice.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedState_Execution_MappedTwice() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map twice
		mapper.mapModelToResults(model, resultsMap);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);

		// state
		assertEquals(ExecutionState.EXECUTING, childResult.getState());
	}

	/**
	 * Model can be mapped twice with model content single root result is added to
	 * execution result as child result.
	 * 
	 * Correlation Id ensures that results isn't create twice.
	 */
	@Test
	public void testMapModelToResults_ContainsExpectedState_Computed_MappedTwice() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.COMPUTED);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map twice
		mapper.mapModelToResults(model, resultsMap);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);

		// state
		assertEquals(ExecutionState.SUCCESS, childResult.getState());
	}

	/**
	 * Model can be mapped twice with model content single root result is added to
	 * execution result as child result.
	 * 
	 * Correlation ID ensures that results isn't create twice.
	 * 
	 * Model result is first mapped to execution state and secondly mapped to
	 * computed state (which is resolved to success).
	 * 
	 * Both model results are created with the same correlation ID, an hence mapped
	 * to the execution result.
	 */
	@Test
	public void testMapModelToResults_MappedTwiceContainsUpdatedState_Computed() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// Step 1: map model with executing state
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);

		// state
		assertEquals(ExecutionState.EXECUTING, childResult.getState());

		// Step 2: map model to computed state
		model = createModelResultWithNoMessages(randomText2, ExecutionState.COMPUTED);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		children = executionResult.getChildren();
		childResult = children[0];
		assertNotNull(childResult);

		// state
		assertEquals(ExecutionState.SUCCESS, childResult.getState());
	}

	/**
	 * Model can be mapped twice with model content single root result is added to
	 * execution result as child result.
	 * 
	 * Correlation ID ensures that results isn't create twice.
	 * 
	 * Model result is first mapped to execution state and secondly mapped to
	 * failure state.
	 * 
	 * Both model results are created with the same correlation ID, an hence mapped
	 * to the execution result.
	 */
	@Test
	public void testMapModelToResults_MappedTwiceContainsUpdatedState_Failure() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// Step 1: map model with executing state
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);

		// state
		assertEquals(ExecutionState.EXECUTING, childResult.getState());

		// Step 2: map model to computed state
		model = createModelResultWithNoMessages(randomText2, ExecutionState.FAILURE);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		children = executionResult.getChildren();
		childResult = children[0];
		assertNotNull(childResult);

		// state
		assertEquals(ExecutionState.FAILURE, childResult.getState());
	}

	/**
	 * Model can be mapped twice with model content single root result is added to
	 * execution result as child result.
	 * 
	 * Model description is updated at second update.
	 */
	@Test
	public void testMapModelToResults_MappedTwiceContainsUpdatedDescription() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// Step 1: map model
		Results model = createModelResultWithNoMessages(randomText2, ExecutionState.EXECUTING);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);

		// test description
		assertEquals(randomText2, childResult.getDescription());

		// Step 2: map model with new description
		model = createModelResultWithNoMessages(randomText3, ExecutionState.COMPUTED);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		children = executionResult.getChildren();
		childResult = children[0];
		assertNotNull(childResult);

		// test description
		assertEquals(randomText3, childResult.getDescription());
	}

	/**
	 * Model can be mapped twice with model content single root result is added to
	 * execution result as child result.
	 * 
	 * Model message is updated at second update.
	 */
	@Test
	public void testMapModelToResults_MappedTwiceContainsUpdatedMessage() {
		ExecutionResult executionResult = new ExecutionResultImpl(randomText);
		Results model = createModelResultWithOneMessage(randomText2, ExecutionState.EXECUTING, randomText3,
				randomText4);

		// create and register root execution result in map
		Map<Integer, ExecutionResult> resultsMap = mapper.createExecutionResultMap(executionResult);

		// map
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		ExecutionResult[] children = executionResult.getChildren();
		ExecutionResult childResult = children[0];
		assertNotNull(childResult);
		Map<String, String> messages = childResult.getMessages();
		assertNotNull(messages);
		assertEquals(1, messages.size());
		assertTrue(messages.containsKey(randomText3));
		assertEquals(randomText4, childResult.getMessages().get(randomText3));

		// Step 2: map model with new description
		model = createModelResultWithOneMessage(randomText5, ExecutionState.EXECUTING, randomText3, randomText5);
		mapper.mapModelToResults(model, resultsMap);

		// test
		assertEquals(1, executionResult.getChildren().length);
		children = executionResult.getChildren();
		childResult = children[0];
		assertNotNull(childResult);
		messages = childResult.getMessages();
		assertNotNull(messages);
		assertEquals(1, messages.size());
		assertTrue(messages.containsKey(randomText3));
		assertEquals(randomText5, childResult.getMessages().get(randomText3));
	}

}
