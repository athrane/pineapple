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

import static com.alpha.pineapple.docker.model.ContainerState.getContainerStateFromInspectedContainer;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.javautils.reflection.GetterMethodMatcher;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ContainerState;
import com.alpha.pineapple.docker.model.rest.ContainerJsonBase;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which tests a Docker container towards the expected configuration in the
 * container info.
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * 
 * <li><code>container-info</code>contains a container info which describes the
 * container to be inspected. The type is
 * <code>com.alpha.pineapple.docker.ContainerInfo</code>.</li>
 * 
 * <li><code>container-state</code>contains the expected container state. The
 * type is <code>com.alpha.pineapple.docker.model.ContainerState</code>.</li>
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
public class TestContainerCommand implements Command {

	/**
	 * Key used to identify property in context: plugin session object.
	 */
	public static final String SESSION_KEY = "session";

	/**
	 * Key used to identify property in context: Contains execution result
	 * object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Key used to identify property in context: Container info for the
	 * container to access.
	 */
	public static final String CONTAINER_INFO_KEY = "container-info";

	/**
	 * Key used to identify property in context: Expected container state.
	 */
	public static final String CONTAINER_STATE_KEY = "container-state";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Container info.
	 */
	@Initialize(CONTAINER_INFO_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ContainerInfo containerInfo;

	/**
	 * Expected container state.
	 */
	@Initialize(CONTAINER_STATE_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	ContainerState expectedContainerState;

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

	/**
	 * Docker JAXB Getter method matcher.
	 */
	@Resource
	GetterMethodMatcher dockerJaxbGetterMethodMatcher;

	public boolean execute(Context context) throws Exception {

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// fail if container doesn't exists in repository
		if (!dockerClient.containerExists(session, containerInfo)) {
			Object[] args = { containerInfo };
			executionResult.completeAsFailure(messageProvider, "tcc.test_container_notfound_failure", args);
			return Command.CONTINUE_PROCESSING;
		}

		// inspect container
		ContainerJsonBase inspectedContainer = dockerClient.inspectContainer(session, containerInfo, executionResult);

		// test container state
		ExecutionResult assertResult = assertContainerState(inspectedContainer);

		// handle failed result
		if (assertResult.isFailed()) {
			// complete result
			executionResult.completeAsFailure(messageProvider, "tcc.test_container_assertstate_failure");
			return Command.CONTINUE_PROCESSING;
		}

		// complete result
		Object[] args = { containerInfo.getName() };
		executionResult.completeAsSuccessful(messageProvider, "tcc.test_container_completed", args);
		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Assert container state.
	 * 
	 * @param inspectedContainer
	 *            inspected container information.
	 * 
	 * @return assertion result.
	 */
	ExecutionResult assertContainerState(ContainerJsonBase inspectedContainer) {
		ContainerState actualState = getContainerStateFromInspectedContainer(inspectedContainer);
		Object[] args = { expectedContainerState.toString().toUpperCase() };
		String message = messageProvider.getMessage("tcc.test_container_assert_state_info", args);
		ExecutionResult stateResult = executionResult.addChild(message);

		// handle successful case
		if (actualState.equals(expectedContainerState)) {
			Object[] args2 = { expectedContainerState.toString().toUpperCase() };
			stateResult.completeAsSuccessful(messageProvider, "tcc.test_container_assert_state_success", args2);
			return stateResult;
		}

		Object[] args3 = { expectedContainerState.toString().toUpperCase(), actualState.toString().toUpperCase() };
		stateResult.completeAsFailure(messageProvider, "tcc.test_container_assert_state_failure", args3);
		return stateResult;
	}

}
