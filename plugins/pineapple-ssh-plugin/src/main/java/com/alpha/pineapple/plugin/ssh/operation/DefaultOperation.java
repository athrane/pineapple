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

package com.alpha.pineapple.plugin.ssh.operation;

import static com.alpha.pineapple.plugin.ssh.SshConstants.EXECUTION_PAUSE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.ConcurrencyUtils;
import com.alpha.javautils.OperationUtils;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.ssh.SshConstants;
import com.alpha.pineapple.plugin.ssh.model.AssertContains;
import com.alpha.pineapple.plugin.ssh.model.AssertEquals;
import com.alpha.pineapple.plugin.ssh.model.Execute;
import com.alpha.pineapple.plugin.ssh.model.Mapper;
import com.alpha.pineapple.plugin.ssh.model.SecureCopy;
import com.alpha.pineapple.plugin.ssh.model.Ssh;
import com.alpha.pineapple.plugin.ssh.model.SshCommand;
import com.alpha.pineapple.plugin.ssh.session.SshSession;
import com.alpha.pineapple.plugin.ssh.utils.SshHelper;
import com.alpha.pineapple.session.Session;
import com.jcraft.jsch.ChannelExec;

@PluginOperation(OperationNames.WILDCARD_OPERATION)
public class DefaultOperation implements Operation {

	/**
	 * Command output buffer size.
	 */
	static final int BUFFER_SIZE = 1024;

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
	 * Secure copy-to command.
	 */
	@Resource
	Command secureCopyToCommand;

	/**
	 * Command runner
	 */
	@Resource
	CommandRunner commandRunner;

	/**
	 * Model mapper object.
	 */
	@Resource
	Mapper mapper;

	/**
	 * SSH helper.
	 */
	@Resource
	SshHelper sshHelper;

	/**
	 * Operation utilities.
	 */
	@Resource
	OperationUtils operationUtils;

	@SuppressWarnings("unchecked")
	public void execute(Object content, Session session, ExecutionResult executionResult)
			throws PluginExecutionFailedException {
		// validate parameters
		Validate.notNull(content, "content is undefined.");
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(executionResult, "executionResult is undefined.");

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { content.getClass().getName(), content };
			String message = messageProvider.getMessage("do.start", args);
			logger.debug(message);
		}

		// validate parameters
		operationUtils.validateContentType(content, SshConstants.LEGAL_CONTENT_TYPES);
		operationUtils.validateSessionType(session, SshSession.class);

		try {
			// type cast model
			Ssh pluginModel = (Ssh) content;

			// type cast session
			SshSession sshSession = (SshSession) session;

			// process model
			processModel(pluginModel, sshSession, executionResult);

			// compute execution state from children
			executionResult.completeAsComputed(messageProvider, "do.completed", null, "do.failed", null);
		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("do.error", args);
			throw new PluginExecutionFailedException(message, e);
		}
	}

	/**
	 * Process model commands.
	 * 
	 * @param sshModel
	 *            SSH model.
	 * @param session
	 *            SSH session.
	 * @param result
	 *            execution result.
	 */
	void processModel(Ssh sshModel, SshSession session, ExecutionResult result) {

		List<SshCommand> commands = sshModel.getCommands();
		for (SshCommand command : commands) {

			// execute command
			if (command instanceof SecureCopy) {
				executeSecureCopy(session, (SecureCopy) command, result);
				continue;
			}

			if (command instanceof Execute) {
				executeExecute(session, (Execute) command, result);
				continue;
			}

			if (command instanceof AssertEquals) {
				executeAssertEquals(session, (AssertEquals) command, result);
				continue;
			}

			if (command instanceof AssertContains) {
				executeAssertContains(session, (AssertContains) command, result);
				continue;
			}
		}
	}

	/**
	 * Execute secure copy.
	 * 
	 * @param session
	 *            SSH session.
	 * @param command
	 *            secure copy command.
	 * @param result
	 *            operation result.
	 */
	void executeSecureCopy(SshSession session, SecureCopy command, ExecutionResult result) {

		// create description
		Object[] args = { command.getSource(), command.getDestination() };
		String message = messageProvider.getMessage("do.securecopy_info", args);

		// map model content to context
		Context context = commandRunner.createContext();
		mapper.mapSecureCopy(command, context, session);

		// run command
		commandRunner.setExecutionResult(result);
		commandRunner.run(secureCopyToCommand, message, context);
	}

	/**
	 * Execute remote command.
	 * 
	 * @param session
	 *            SSH session.
	 * @param command
	 *            remote execute command.
	 * @param result
	 *            operation result.
	 */
	void executeExecute(SshSession session, Execute command, ExecutionResult result) {
		ExecutionResult commandResult = null;
		ChannelExec execChannel = null;
		int status = -1;

		try {
			// get data
			String cmdAsString = command.getCommand();

			// create result
			Object[] args = { cmdAsString };
			String scpDescription = messageProvider.getMessage("do.execute_info", args);
			commandResult = result.addChild(scpDescription);

			// get EXEC channel and set command
			execChannel = session.getExecuteChannel();
			execChannel.setCommand(cmdAsString);

			// Allocate stream buffers
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ByteArrayOutputStream byteArrayErrorStream = new ByteArrayOutputStream();

			// set streams
			execChannel.setInputStream(null);
			execChannel.setOutputStream(byteArrayOutputStream);
			execChannel.setExtOutputStream(byteArrayOutputStream);
			execChannel.setErrStream(byteArrayErrorStream);

			// connect to invoke
			execChannel.connect();

			// collect output
			while (true) {

				// handle channel closure
				if (execChannel.isClosed()) {
					status = execChannel.getExitStatus();
					addStatusCodeToResult(commandResult, status);
					addCommandOutputToResult(commandResult, "Standard Out",
							new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
					addCommandOutputToResult(commandResult, "Error Out",
							new ByteArrayInputStream(byteArrayErrorStream.toByteArray()));
					break;
				}

				ConcurrencyUtils.waitSomeMilliseconds(EXECUTION_PAUSE);
			}

			// handle exit code signals error
			if (status != 0) {
				// complete result
				commandResult.completeAsFailure(messageProvider, "do.execute_statuscode_failed");
				return;
			}

			// handle exit code signals execution OK
			// complete result
			commandResult.completeAsSuccessful(messageProvider, "do.execute_completed");

		} catch (Exception e) {
			commandResult.completeAsError(messageProvider, "do.execute_error", e);

		} finally {
			if (execChannel != null) {
				if (execChannel.isConnected())
					execChannel.disconnect();
			}
		}
	}

	/**
	 * Execute assert equals command.
	 * 
	 * @param session
	 *            SSH session.
	 * @param command
	 *            assert equals command.
	 * @param result
	 *            operation result.
	 */
	void executeAssertEquals(SshSession session, AssertEquals command, ExecutionResult result) {
		ExecutionResult commandResult = null;
		ChannelExec execChannel = null;
		int status = -1;
		String errorOutMessage = null;
		String stdOutMessage = null;

		try {
			// get data
			String cmdAsString = command.getCommand();
			String cmdExpectedValue = command.getExpectedValue();

			// create result
			Object[] args = { cmdExpectedValue };
			String scpDescription = messageProvider.getMessage("do.execute_assert_equals_info", args);
			commandResult = result.addChild(scpDescription);

			// get EXEC channel and set command
			execChannel = session.getExecuteChannel();
			execChannel.setCommand(cmdAsString);

			// set streams
			execChannel.setInputStream(null);
			execChannel.setOutputStream(System.out);
			execChannel.setExtOutputStream(System.out);
			execChannel.setErrStream(System.err);
			InputStream stdout = execChannel.getInputStream();
			InputStream stderr = execChannel.getErrStream();

			// connect to invoke
			execChannel.connect();

			// collect output
			while (true) {

				// handle channel closure
				if (execChannel.isClosed()) {
					status = execChannel.getExitStatus();
					addStatusCodeToResult(commandResult, status);
					stdOutMessage = addCommandOutputToResult(commandResult, "Standard Out", stdout);
					errorOutMessage = addCommandOutputToResult(commandResult, "Error Out", stderr);
					break;
				}

				ConcurrencyUtils.waitSomeMilliseconds(EXECUTION_PAUSE);
			}

			// handle exit code signals error
			if (status != 0) {
				commandResult.completeAsFailure(messageProvider, "do.execute_statuscode_failed");
				return;
			}

			// assert
			boolean assertionResult = assertExpectedValue(commandResult, cmdExpectedValue, stdOutMessage);
			if (!assertionResult) {
				Object[] args2 = { cmdExpectedValue, stdOutMessage };
				commandResult.completeAsFailure(messageProvider, "do.execute_assert_equals_assert_failed", args2);
				return;
			}

			// handle exit code signals execution OK
			commandResult.completeAsSuccessful(messageProvider, "do.execute_completed");

		} catch (Exception e) {
			commandResult.completeAsError(messageProvider, "do.execute_assert_equals_error", e);

		} finally {
			if (execChannel != null) {
				if (execChannel.isConnected())
					execChannel.disconnect();
			}
		}
	}

	/**
	 * Execute assert contains command.
	 * 
	 * @param session
	 *            SSH session.
	 * @param command
	 *            assert equals command.
	 * @param result
	 *            operation result.
	 */
	void executeAssertContains(SshSession session, AssertContains command, ExecutionResult result) {
		ExecutionResult commandResult = null;
		ChannelExec execChannel = null;
		int status = -1;
		String errorOutMessage = null;
		String stdOutMessage = null;

		try {
			// get data
			String cmdAsString = command.getCommand();
			String cmdExpectedValue = command.getExpectedValue();

			// create result
			Object[] args = { cmdExpectedValue };
			String scpDescription = messageProvider.getMessage("do.execute_assert_contains_info", args);
			commandResult = result.addChild(scpDescription);

			// get EXEC channel and set command
			execChannel = session.getExecuteChannel();
			execChannel.setCommand(cmdAsString);

			// set streams
			execChannel.setInputStream(null);
			execChannel.setOutputStream(System.out);
			execChannel.setExtOutputStream(System.out);
			execChannel.setErrStream(System.err);
			InputStream stdout = execChannel.getInputStream();
			InputStream stderr = execChannel.getErrStream();

			// connect to invoke
			execChannel.connect();

			// collect output
			while (true) {

				// handle channel closure
				if (execChannel.isClosed()) {
					status = execChannel.getExitStatus();
					addStatusCodeToResult(commandResult, status);
					stdOutMessage = addCommandOutputToResult(commandResult, "Standard Out", stdout);
					errorOutMessage = addCommandOutputToResult(commandResult, "Error Out", stderr);
					break;
				}

				ConcurrencyUtils.waitSomeMilliseconds(EXECUTION_PAUSE);
			}

			// handle exit code signals error
			if (status != 0) {
				commandResult.completeAsFailure(messageProvider, "do.execute_statuscode_failed");
				return;
			}

			// assert
			boolean assertionResult = assertContainsValue(commandResult, cmdExpectedValue, stdOutMessage);
			if (!assertionResult) {
				Object[] args2 = { cmdExpectedValue, stdOutMessage };
				commandResult.completeAsFailure(messageProvider, "do.execute_assert_contains_assert_failed", args2);
				return;
			}

			// handle exit code signals execution OK
			commandResult.completeAsSuccessful(messageProvider, "do.execute_completed");

		} catch (Exception e) {
			commandResult.completeAsError(messageProvider, "do.execute_assert_contains_error", e);

		} finally {
			if (execChannel != null) {
				if (execChannel.isConnected())
					execChannel.disconnect();
			}
		}
	}

	/**
	 * Assert remote command output versus expected value.
	 */
	boolean assertExpectedValue(ExecutionResult result, String cmdExpectedValue, String stdoutMessage)
			throws Exception {
		String message = stdoutMessage.trim();

		// create message
		String assertionHeader = messageProvider.getMessage("do.execute_assertion_header");
		Object[] args = { message, cmdExpectedValue };
		String assertionDescription = messageProvider.getMessage("do.execute_assert_equals_assertion", args);
		result.addMessage(assertionHeader, assertionDescription);

		// assert
		return (message.equals(cmdExpectedValue));
	}

	/**
	 * Assert remote command output versus expected value.
	 */
	boolean assertContainsValue(ExecutionResult result, String cmdExpectedValue, String stdoutMessage)
			throws Exception {
		String message = stdoutMessage.trim();

		// create message
		String assertionHeader = messageProvider.getMessage("do.execute_assertion_header");
		Object[] args = { message, cmdExpectedValue };
		String assertionDescription = messageProvider.getMessage("do.execute_assert_contains_assertion", args);
		result.addMessage(assertionHeader, assertionDescription);

		// assert
		return (message.contains(cmdExpectedValue));
	}

	/**
	 * Add remote command output to execution result.
	 * 
	 * @param result
	 *            execution result.
	 * @param in
	 *            input stream where is read from.
	 * 
	 * @return command output
	 * 
	 * @throws Exception
	 *             If addition fails.
	 */
	String addCommandOutputToResult(ExecutionResult result, String messageKey, InputStream in) throws Exception {
		String message = IOUtils.toString(in);
		result.addMessage(messageKey, message);
		return message;
	}

	/**
	 * Store status code from remote command execution.
	 * 
	 * @param result
	 *            result where status code is added.
	 * @param status
	 *            the status code.
	 */
	void addStatusCodeToResult(ExecutionResult result, int status) {
		String statusAsString = Integer.toString(status);
		String statusHeader = messageProvider.getMessage("do.execute_status_header");
		result.addMessage(statusHeader, statusAsString);
	}

}
