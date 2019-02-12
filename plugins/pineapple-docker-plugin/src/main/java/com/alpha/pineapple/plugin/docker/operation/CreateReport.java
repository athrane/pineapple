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

package com.alpha.pineapple.plugin.docker.operation;

import static com.alpha.javautils.ArgumentUtils.notNull;

import javax.annotation.Resource;

import com.alpha.javautils.OperationUtils;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.docker.DockerConstants;
import com.alpha.pineapple.plugin.docker.model.Mapper;
import com.alpha.pineapple.session.Session;

/**
 * Implements the create-report operation.
 * 
 * Creates a reports consisting of execution results which lists the set of
 * defined Docker images and containers.
 */
@PluginOperation(OperationNames.CREATE_REPORT)
public class CreateReport implements Operation {

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Operation utilities.
	 */
	@Resource
	OperationUtils operationUtils;

	/**
	 * Model mapper object.
	 */
	@Resource
	Mapper mapper;

	/**
	 * Docker client.
	 */
	@Resource
	DockerClient dockerClient;

	public void execute(Object content, Session session, ExecutionResult result) throws PluginExecutionFailedException {
		notNull(content, "content is undefined.");
		notNull(session, "session is undefined.");
		notNull(result, "result is undefined.");

		// validate parameters
		operationUtils.validateContentType(content, DockerConstants.LEGAL_CONTENT_TYPES);
		operationUtils.validateSessionType(session, DockerSession.class);

		try {
			// type cast session
			DockerSession dockerSession = (DockerSession) session;

			// ignore model and create report
			createReport(dockerSession, result);

			// compute execution state from children
			result.completeAsComputed(messageProvider, "cr.completed", null, "cr.failed", null);
		} catch (Exception e) {
			Object[] args = { e.toString() };
			String message = messageProvider.getMessage("cr.error", args);
			throw new PluginExecutionFailedException(message, e);
		}
	}

	/**
	 * Process model commands.
	 * 
	 * @param session
	 *            Docker session.
	 * @param result
	 *            execution result.
	 */
	void createReport(DockerSession session, ExecutionResult result) {
		reportOnImages(session, result);
		reportOnContainers(session, result);
	}

	/**
	 * Create report from Docker images.
	 * 
	 * @param session
	 *            Docker session.
	 * @param result
	 *            execution result
	 */
	void reportOnImages(DockerSession session, ExecutionResult result) {
		dockerClient.reportOnImages(session, result);
	}

	/**
	 * Create report from Docker containers.
	 * 
	 * @param session
	 *            Docker session.
	 * @param result
	 *            execution result
	 */
	void reportOnContainers(DockerSession session, ExecutionResult result) {
		dockerClient.reportOnContainers(session, result);
	}

}
