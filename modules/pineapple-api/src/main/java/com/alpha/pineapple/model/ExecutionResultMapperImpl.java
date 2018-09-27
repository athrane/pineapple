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

package com.alpha.pineapple.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.execution.Message;
import com.alpha.pineapple.model.execution.MessageValue;
import com.alpha.pineapple.model.execution.Messages;
import com.alpha.pineapple.model.execution.ObjectFactory;
import com.alpha.pineapple.model.execution.Result;
import com.alpha.pineapple.model.execution.ResultSequence;
import com.alpha.pineapple.model.execution.Results;

/**
 * Implementation of the {@linkplain ExecutionResultMapper} interface.
 */
public class ExecutionResultMapperImpl implements ExecutionResultMapper {

	/**
	 * Index of the remote root result.
	 */
	static final int REMOTE_ROOT_INDEX = 0;

	/**
	 * Index of the local root result.
	 */
	static final int LOCAL_ROOT_INDEX = -1;

	/**
	 * Index for root result in execution.
	 */
	public static final Integer LOCAL_ROOT_INDEX_INTEGER = Integer.valueOf(LOCAL_ROOT_INDEX);

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Model object factory.
	 */
	@Resource
	ObjectFactory resultModelObjectFactory;

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "apiMessageProvider")
	MessageProvider messageProvider;

	@Override
	public Results mapNotificationsToModel(ExecutionResultNotification[] notifications) {
		Validate.notNull(notifications, "notifications is undefined.");

		// create model
		Results modelResults = resultModelObjectFactory.createResults();
		modelResults.setResultSequence(resultModelObjectFactory.createResultSequence());
		List<Result> modelUpdatedResults = modelResults.getResultSequence().getResult();

		// map results
		for (ExecutionResultNotification notification : notifications) {
			Result modelResult = internalMapSingleNotificationToModel(notification);
			modelUpdatedResults.add(modelResult);
		}

		return modelResults;
	}

	/**
	 * Map execution result notification to model result. Only the result itself is
	 * mapped, children aren't mapped.
	 * 
	 * Messages are sorted by key before they are added.
	 * 
	 * Correlation ID is mapped from the object hash code. The correlation ID of the
	 * parent result is also mapped from the parent result hash code.
	 * 
	 * @param notification
	 *            execution result notification which is mapped to model result.
	 * 
	 * @return mapped model execution result.
	 */
	Result internalMapSingleNotificationToModel(ExecutionResultNotification notification) {
		Validate.notNull(notification, "notification is undefined.");

		// create model result and set attributes
		ExecutionResult result = notification.getResult();
		Result modelResult = resultModelObjectFactory.createResult();
		modelResult.setDescription(result.getDescription());
		modelResult.setStartTime(result.getStartTime());
		modelResult.setState(notification.getState().name());
		modelResult.setTime(result.getTime());
		modelResult.setCorrelationId(result.hashCode());

		// map parent correlation ID
		if (!result.isRoot()) {
			ExecutionResult parent = result.getParent();
			modelResult.setParentCorrelationId(parent.hashCode());
		}

		// get result messages
		Messages modelMessages = resultModelObjectFactory.createMessages();
		modelResult.setMessages(modelMessages);

		// map result messages
		Map<String, String> messages = result.getMessages();
		TreeSet<String> sortedKeys = new TreeSet<String>(messages.keySet());
		for (String key : sortedKeys) {
			String value = messages.get(key);
			Message modelmessage = resultModelObjectFactory.createMessage();
			modelmessage.setName(key);

			// create and add value
			MessageValue modelMessageValue = resultModelObjectFactory.createMessageValue();
			modelMessageValue.setValue(value);
			modelmessage.getValue().add(modelMessageValue);

			// add message
			modelMessages.getMessage().add(modelmessage);
		}

		return modelResult;
	}

	@Override
	public void mapModelToResults(Results modelResults, Map<Integer, ExecutionResult> resultsMap) {
		Validate.notNull(modelResults, "modelResults is undefined.");
		Validate.notNull(resultsMap, "results is undefined.");

		// get model updates
		ResultSequence resultSequence = modelResults.getResultSequence();
		if (resultSequence == null)
			return;

		List<Result> modelUpdatesList = resultSequence.getResult();
		if (modelUpdatesList == null)
			return;

		// iterate over the received results
		for (Result modelResult : modelUpdatesList) {

			// handle new result
			if (isNewResult(modelResult, resultsMap)) {
				mapNewResult(modelResult, resultsMap);
				continue;
			}

			// update existing result
			updateResult(modelResult, resultsMap);
		}
	}

	@Override
	public Map<Integer, ExecutionResult> createExecutionResultMap(ExecutionResult rootResult) {
		Validate.notNull(rootResult, "rootResult is undefined.");

		Map<Integer, ExecutionResult> resultsMap = new HashMap<Integer, ExecutionResult>();
		resultsMap.put(LOCAL_ROOT_INDEX_INTEGER, rootResult);
		return resultsMap;
	}

	/**
	 * Returns true if model result is new. The result is interpreted as new if it
	 * is doesn't exist in the result map, e.g. is registered under its correlation
	 * ID.
	 * 
	 * @param modelResult
	 *            model result.
	 * @param results
	 *            map of known execution results.
	 * 
	 * @return true if model result is new.
	 */
	boolean isNewResult(Result modelResult, Map<Integer, ExecutionResult> resultsMap) {
		return (!resultsMap.containsKey(modelResult.getCorrelationId()));
	}

	/**
	 * Map new result.
	 * 
	 * @param modelResult
	 *            model result to be mapped.
	 * @param results
	 *            map of known execution results.
	 */
	void mapNewResult(Result modelResult, Map<Integer, ExecutionResult> results) {

		// get parent result
		int parentIndex = resolveParentCorrelationId(modelResult);
		ExecutionResult parentResult = results.get(parentIndex);

		// create new result
		ExecutionResult childResult = parentResult.addChild(modelResult.getDescription());

		// state isn't set explicit for a new result - since the only legal
		// value is executing
		// which is set implicit when the result is created

		// set messages
		Messages modelMessages = modelResult.getMessages();
		List<Message> modelMessagesContainer = modelMessages.getMessage();
		for (Message modelMessage : modelMessagesContainer) {
			List<MessageValue> modelMessageValue = modelMessage.getValue();
			MessageValue modelValue = modelMessageValue.get(0);
			childResult.addOrReplaceMessage(modelMessage.getName(), modelValue.getValue());
		}

		// set time
		// set start time

		// add result
		results.put(Integer.valueOf(modelResult.getCorrelationId()), childResult);
	}

	/**
	 * Update existing result.
	 *
	 * @param modelResult
	 *            model result to be mapped.
	 * @param results
	 *            map of known execution results.
	 */
	void updateResult(Result modelResult, Map<Integer, ExecutionResult> results) {

		// get target result
		ExecutionResult targetResult = results.get(modelResult.getCorrelationId());

		// set description
		targetResult.setDescription(modelResult.getDescription());

		// set state
		ExecutionState state = ExecutionResult.ExecutionState.valueOf(modelResult.getState());
		targetResult.setState(state);

		// set messages
		Messages modelMessages = modelResult.getMessages();
		List<Message> modelMessagesContainer = modelMessages.getMessage();
		for (Message modelMessage : modelMessagesContainer) {
			List<MessageValue> modelMessageValue = modelMessage.getValue();
			MessageValue modelValue = modelMessageValue.get(0);
			targetResult.addOrReplaceMessage(modelMessage.getName(), modelValue.getValue());
		}

		// set time
		// set start time
	}

	/**
	 * Resolve parent result index.
	 * 
	 * If the result doesn't have a parent correlation ID defined then the result is
	 * interpreted as the root result at the remote execution. The parent
	 * correlation ID for the root result is resolved to index of the local root
	 * result.
	 * 
	 * @param modelResult
	 *            model result for which the parent correlation ID is resolved.
	 * 
	 * @return parent correlation ID.
	 */
	int resolveParentCorrelationId(Result modelResult) {

		// handle root case
		// if parent correlation ID is null then result is handled as remote
		// root result
		if (modelResult.getParentCorrelationId() == null)
			return LOCAL_ROOT_INDEX;

		// get parent
		return modelResult.getParentCorrelationId();
	}

}
