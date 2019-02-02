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

package com.alpha.pineapple.docker.command;

import static com.alpha.pineapple.docker.DockerConstants.PAUSE_CONTAINER_URI;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.docker.utils.RestResponseException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which pauses a Docker container.
 * 
 * If container doesn't exist then the command is aborted with a failed
 * execution result. If container is stopped then the container is started and
 * paused.
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>container-info</code>contains a container info which describes the
 * container to be inspected. The type is
 * <code>com.alpha.pineapple.docker.ContainerInfo</code>.</li>
 * 
 * <li><code>session</code> defines the agent session used communicate with an
 * agent. The type is
 * <code>com.alpha.pineapple.docker.session.DockerSession</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is:
 * <ul>
 * 
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
public class PauseContainerCommand implements Command {

	/**
	 * Key used to identify property in context: plugin session object.
	 */
	public static final String SESSION_KEY = "session";

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Key used to identify property in context: Container info for the container to
	 * access.
	 */
	public static final String CONTAINER_INFO_KEY = "container-info";

	/**
	 * Container info.
	 */
	@Initialize(CONTAINER_INFO_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ContainerInfo containerInfo;

	/**
	 * Plugin session.
	 */
	@Initialize(SESSION_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	DockerSession session;

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "dockerMessageProvider")
	MessageProvider messageProvider;

	/**
	 * Docker client.
	 */
	@Resource
	DockerClient dockerClient;

	public boolean execute(Context context) throws Exception {

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// fail if container doesn't exists in repository
		if (!dockerClient.containerExists(session, containerInfo)) {
			Object[] args = { containerInfo };
			executionResult.completeAsFailure(messageProvider, "pcc.pause_container_notfound_failure", args);
			return Command.CONTINUE_PROCESSING;
		}

		// if container is paused then exit
		if (dockerClient.isContainerPaused(session, containerInfo)) {
			executionResult.completeAsSuccessful(messageProvider, "pcc.pause_container_already_paused");
			return Command.CONTINUE_PROCESSING;
		}

		// if container is stopped then start it
		if (!dockerClient.isContainerRunning(session, containerInfo)) {
			String message = messageProvider.getMessage("pcc.pause_container_start");
			executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
			dockerClient.startContainer(session, containerInfo, executionResult);
		}

		// get container name
		Map<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("id", containerInfo.getName());

		try {
			
			// post to start container
			session.httpPost(PAUSE_CONTAINER_URI, uriVariables);

		} catch (RestResponseException rre) {						
			Object[] args = { rre.getStatusCode(), rre.getMessage() };
			executionResult.completeAsFailure(messageProvider, "pcc.pause_container_failed", args);
			return Command.CONTINUE_PROCESSING;
		}
		
		// complete result
		Object[] args = { containerInfo.getName() };
		executionResult.completeAsSuccessful(messageProvider, "pcc.pause_container_completed", args);

		return Command.CONTINUE_PROCESSING;
	}

}
