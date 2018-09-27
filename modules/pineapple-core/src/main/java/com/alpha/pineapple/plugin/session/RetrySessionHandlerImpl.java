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

package com.alpha.pineapple.plugin.session;

import static com.alpha.pineapple.CoreConstants.MSG_SESSION;

import javax.annotation.Resource;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.session.retry.SessionRetryProxyFactory;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;

/**
 * Implementation of the {@linkplain Operation} interface which decorates an
 * operation with a session.
 * 
 * This implementation add support for retry logic through instrumentation of
 * the plugin session class.
 */
public class RetrySessionHandlerImpl implements Operation {
	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Pineapple operation
	 */
	Operation operation;

	/**
	 * Resource object.
	 */
	com.alpha.pineapple.model.configuration.Resource resource;

	/**
	 * Credential object.
	 */
	Credential credential;

	/**
	 * Session retry proxy factory.
	 */
	@Resource
	SessionRetryProxyFactory sessionRetryProxyFactory;

	/**
	 * Initialize session handler.
	 * 
	 * @param resource
	 *            Resource object.
	 * @param credential
	 *            Credential object.
	 * @param operation
	 *            Operation object.
	 */
	void initialize(com.alpha.pineapple.model.configuration.Resource resource, Credential credential,
			Operation operation) {
		this.resource = resource;
		this.credential = credential;
		this.operation = operation;
	}

	public void execute(Object content, Session session, ExecutionResult result) {

		try {
			// validate arguments
			Validate.notNull(result, "result is undefined");

			// if session is null then skip session handling
			// invoke operation with null session
			if (session == null) {
				addExecutionResultNullSessionMessage(result);
				operation.execute(content, null, result);
				return;
			}

			// encapsulate session with retry proxy
			Session retrySession = sessionRetryProxyFactory.decorateWithProxy(session, result);

			// connect with retry session
			addExecutionResultSessionConnectMessage(result);
			retrySession.connect(resource, credential);
			addExecutionResultSessionConnectedMessage(result);

			// execute operation
			operation.execute(content, session, result);
			disconnectSession(result, session);

		} catch (SessionConnectException e) {
			// throw runtime exception to channel the exception to invoking part
			// of the core
			throw new RuntimeException(e);
		} catch (SessionDisconnectException e) {
			addExecutionResultSessionDisconnectFailureMessage(result, e);

			// throw runtime exception to channel the exception to invoking part
			// of the core
			throw new RuntimeException(e);
		} catch (PluginExecutionFailedException e) {
			disconnectSessionAndHandleException(session, result);

			// throw runtime exception to channel the exception to invoking part
			// of the core
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			disconnectSessionAndHandleException(session, result);

			// throw runtime exception to channel the exception to invoking part
			// of the core
			throw new RuntimeException(e);
		}
	}

	/**
	 * Disconnect session and handle any exception.
	 * 
	 * @param session
	 *            session to disconnect.
	 * @param result
	 *            execution result.
	 */
	void disconnectSessionAndHandleException(Session session, ExecutionResult result) {
		try {
			// disconnect session
			disconnectSession(result, session);

		} catch (Exception e) {
			addExecutionResultSessionDisconnectFailureMessage(result, e);
		}
	}

	/**
	 * Disconnect session and add session disconnection info.
	 * 
	 * @param result
	 *            execution result.
	 * @param session
	 *            session to disconnect.
	 * @throws SessionDisconnectException
	 */
	void disconnectSession(ExecutionResult result, Session session) throws SessionDisconnectException {
		addExecutionResultSessionDisconnectMessage(result);
		session.disconnect();
		addExecutionResultSessionDisconnectedMessage(result);
	}

	/**
	 * Add null session handling information to execution result.
	 * 
	 * @param result
	 *            execution result.
	 */
	void addExecutionResultNullSessionMessage(ExecutionResult result) {
		String message = messageProvider.getMessage("sh.execute_nullsession_info");
		result.addMessage(MSG_SESSION, message);
	}

	/**
	 * Add session connecting information to execution result.
	 * 
	 * @param result
	 *            execution result.
	 */
	void addExecutionResultSessionConnectMessage(ExecutionResult result) {
		Object[] args = { resource.getId() };
		String message = messageProvider.getMessage("sh.execute_session_connect_info", args);
		result.addMessage(MSG_SESSION, message);
	}

	/**
	 * Add session connected information to execution result.
	 * 
	 * @param result
	 *            execution result.
	 */
	void addExecutionResultSessionConnectedMessage(ExecutionResult result) {
		String message = messageProvider.getMessage("sh.execute_session_connected_info");
		result.addMessage(MSG_SESSION, message);
	}

	/**
	 * Add session disconnecting information to execution result.
	 * 
	 * @param result
	 *            execution result.
	 */
	void addExecutionResultSessionDisconnectMessage(ExecutionResult result) {
		Object[] args = { resource.getId() };
		String message = messageProvider.getMessage("sh.execute_session_disconnect_info", args);
		result.addMessage(MSG_SESSION, message);
	}

	/**
	 * Add session disconnected information to execution result.
	 * 
	 * @param result
	 *            execution result.
	 */
	void addExecutionResultSessionDisconnectedMessage(ExecutionResult result) {
		String message = messageProvider.getMessage("sh.execute_session_disconnected_info");
		result.addMessage(MSG_SESSION, message);
	}

	/**
	 * Add session disconnect failure information to execution result.
	 * 
	 * @param result
	 *            execution result.
	 * @param sde
	 *            exception thrown during session disconnect.
	 */
	void addExecutionResultSessionDisconnectFailureMessage(ExecutionResult result, Exception sde) {
		Object[] args = { StackTraceHelper.getStrackTrace(sde) };
		String message = messageProvider.getMessage("sh.execute_session_disconnect_error", args);
		result.addMessage(MSG_SESSION, message);
	}

}
