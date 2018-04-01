/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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

import static com.alpha.pineapple.plugin.agent.AgentConstants.EXECUTE_MODULE_URI;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.alpha.javautils.ConcurrencyUtils;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.ExecutionResultMapper;
import com.alpha.pineapple.model.execution.Result;
import com.alpha.pineapple.model.execution.ResultSequence;
import com.alpha.pineapple.model.execution.Results;
import com.alpha.pineapple.plugin.agent.AgentConstants;
import com.alpha.pineapple.plugin.agent.session.AgentSession;
import com.alpha.pineapple.plugin.agent.utils.RestResponseException;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which executes a remote operation at a remote agent
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>module</code> defines name of the module used as input to
 * operation. The type is <code>java.lang.String</code>.</li>
 * 
 * <li><code>operation</code> defines the name of operation which should be
 * executed. The type is <code>java.lang.String</code>.</li>
 * 
 * <li><code>environment</code> defines the environment for which a model within
 * the module should be executed.. The type is <code>java.lang.String</code>.
 * </li>
 *
 * <li><code>session</code> defines the agent session used communicate with an
 * agent. The type is
 * <code>com.alpha.pineapple.plugin.agent.session.AgentSession</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is:
 * 
 * <ul>
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the test succeeded. If the test
 * failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the test fails due to an exception then the exception isn't caught,
 * but passed on the the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * </ul>
 * </p>
 */
public class ExecuteOperationCommand implements Command {

	/**
	 * URL environment variable.
	 */
	static final String URL_VAR_ENVIRONMENT = "environment";

	/**
	 * URL operation variable.
	 */
	static final String URL_VAR_OPERATION = "operation";

	/**
	 * URL module variable.
	 */
	static final String URL_VAR_MODULE = "module";

	/**
	 * First list index.
	 */
	static final int FIRST_LIST_INDEX = 0;

	/**
	 * Key used to identify property in context: Name of the module.
	 */
	public static final String MODULE_KEY = URL_VAR_MODULE;

	/**
	 * Key used to identify property in context: Operation to invoke.
	 */
	public static final String OPERATION_KEY = URL_VAR_OPERATION;

	/**
	 * Key used to identify property in context: Name of the environment.
	 */
	public static final String ENVIRONMENT_KEY = URL_VAR_ENVIRONMENT;

	/**
	 * Key used to identify property in context: plugin session object.
	 */
	public static final String SESSION_KEY = "session";

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Module name.
	 */
	@Initialize(MODULE_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	String module;

	/**
	 * Environment name.
	 */
	@Initialize(ENVIRONMENT_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	String environment;

	/**
	 * Operation name.
	 */
	@Initialize(OPERATION_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	String operation;

	/**
	 * Plugin session.
	 */
	@Initialize(SESSION_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	AgentSession session;

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Execution result model mapper.
	 */
	@Resource
	ExecutionResultMapper resultMapper;

	public boolean execute(Context context) throws Exception {
		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// execute
		doExecute();

		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Execute operation.
	 * 
	 * @throws Exception
	 *             If distribution fails.
	 */
	void doExecute() {
		try {

			// create URI
			UriComponents uriComponents = UriComponentsBuilder.fromUriString(EXECUTE_MODULE_URI).build()
					.expand(module, operation, environment).encode();
			String serviceUrl = session.createServiceUrl(uriComponents.toUriString());
			session.addServiceUrlMessage(serviceUrl, executionResult);

			// post to start execution
			URI location = session.httpPostForLocation(serviceUrl, AgentConstants.CONTENT_TYPE_TEXT_HTML);
			appendLocationMessage(location);

			// create execution index map and register root result
			Map<Integer, ExecutionResult> executionResultMap = resultMapper.createExecutionResultMap(executionResult);

			// get initial results
			Results modelResults = getRemoteExecutionResult(location);

			// map results
			resultMapper.mapModelToResults(modelResults, executionResultMap);

			// get mapped model root result
			ExecutionResult mappedRootResult = getMappedRootExecutionResult(executionResultMap, modelResults, location);

			while (isOperationExecuting(mappedRootResult)) {
				ConcurrencyUtils.waitSomeMilliseconds(AgentConstants.OPERATION_STATUS_POLLING_DELAY);
				modelResults = getRemoteExecutionResult(location);

				// map model
				resultMapper.mapModelToResults(modelResults, executionResultMap);
			}

			// delete operation status
			session.httpDelete(location.toASCIIString());

			// complete result
			executionResult.completeAsComputed(messageProvider, "eoc.completed", null, "eoc.failed", null);

		} catch (RestResponseException e) {
			executionResult.addMessage("HTTP Headers", e.getHeaders().toString());
			executionResult.addMessage("HTTP Status Code", e.getStatusCode().toString());
			executionResult.addMessage("HTTP Body", e.getBody());
			executionResult.completeAsError(messageProvider, "eoc.error", e);

		} catch (Exception e) {
			executionResult.completeAsError(messageProvider, "eoc.error", e);
		}

	}

	/**
	 * Get remote execution result from server.
	 * 
	 * @param location
	 *            URI to query for status update.
	 * 
	 * @return execution result model from server.
	 */
	Results getRemoteExecutionResult(URI location) {

		// get status
		Results modelResults = session.httpGetForObject(location.toASCIIString(), Results.class);

		// TODO: retry three times and throw exception
		// if( modelResults == null)

		return modelResults;
	}

	/**
	 * Get first mapped execution result.
	 *
	 * @param resultMap
	 *            execution result map.
	 * @param model
	 *            from first service invocation.
	 * @param location
	 *            URI to query for status update.
	 * 
	 * @return mapped root execution result from server.
	 * 
	 * @throws Exception
	 *             if retrieving result fails.
	 */
	ExecutionResult getMappedRootExecutionResult(Map<Integer, ExecutionResult> resultMap, Results results, URI location)
			throws Exception {
		ResultSequence sequence = results.getResultSequence();

		// validate sequence is defined
		if (sequence == null) {
			Object[] args = { location.toASCIIString() };
			String message = messageProvider.getMessage("eoc.undefined_sequence_failure", args);
			throw new UnexpectedModelResponseException(message);
		}

		// validate result list is defined
		List<Result> resultList = sequence.getResult();
		if (resultList == null) {
			Object[] args = { location.toASCIIString() };
			String message = messageProvider.getMessage("eoc.undefined_resultlist_failure", args);
			throw new UnexpectedModelResponseException(message);
		}

		// validate result list isn't empty
		if (resultList.isEmpty()) {
			Object[] args = { location.toASCIIString() };
			String message = messageProvider.getMessage("eoc.undefined_resultlist_failure", args);
			throw new UnexpectedModelResponseException(message);
		}

		// get first result
		Result rootResult = resultList.get(FIRST_LIST_INDEX);

		// validate correlation ID is defined
		Integer rootCorrelationId = rootResult.getCorrelationId();
		if (rootCorrelationId == null) {
			Object[] args = { location.toASCIIString() };
			String message = messageProvider.getMessage("eoc.undefined_root_correlationid_failure", args);
			throw new UnexpectedModelResponseException(message);
		}

		// validate result is mapped with correlation ID
		ExecutionResult mappedRootResult = resultMap.get(rootCorrelationId);
		if (mappedRootResult == null) {
			Object[] args = { location.toASCIIString() };
			String message = messageProvider.getMessage("eoc.undefined_mappedresult_failure", args);
			throw new UnexpectedModelResponseException(message);
		}

		return mappedRootResult;
	}

	/**
	 * Returns true if operation is still executing.
	 * 
	 * @param rootResult
	 *            mapped model root result.
	 * 
	 * @return true if operation is still executing.
	 */
	boolean isOperationExecuting(ExecutionResult rootResult) {
		return rootResult.isExecuting();
	}

	/**
	 * Append location message to execution result.
	 * 
	 * @param location
	 *            location URL.
	 */
	void appendLocationMessage(URI location) {
		Object[] args = { session.createServiceUrl(location.toASCIIString()) };
		String key = messageProvider.getMessage("eoc.agent_communication_info_key");
		String message = messageProvider.getMessage("eoc.location_info", args);
		executionResult.addMessage(key, message);
		return;
	}

}
