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

import static com.alpha.pineapple.docker.DockerConstants.CREATE_CONTAINER_URI;
import static com.alpha.pineapple.docker.DockerConstants.RENAME_CONTAINER_URI;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ContainerInstanceInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.model.rest.ContainerConfiguration;
import com.alpha.pineapple.docker.model.rest.CreatedContainer;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which creates a Docker container from an base image.
 * 
 * After the initial creation the container is renamed to the container name
 * specified in the container info.
 * 
 * Command is idempotent. If a container with designated name already exists
 * then the command skips container creation with as a success. The existing
 * container isn't updated with container configuration in model.
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>container-info</code> contains Docker container info, which defines
 * an container to be created. The type is
 * <code>com.alpha.pineapple.docker.ContainerInfo</code>.</li>
 * 
 * <li><code>session</code> defines the plugin session used communicate with an
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
 * 
 * <ul>
 * <li><code>container-instance-info</code>contains a container instance info
 * which describes the created container instance. The type is
 * <code>com.alpha.pineapple.docker.ContainerInstanceInfo</code>.</li>
 * 
 * <li><code>container-id</code> contains the ID of the created container. The
 * type is <code>java.lang.String</code>.</li>
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
public class CreateContainerCommand implements Command {

    /**
     * Key used to identify property in context: the container info.
     */
    public static final String CONTAINER_INFO_KEY = "container-info";

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
     * Key used to identify property in context: Created container instance
     * info.
     */
    public static final String CONTAINER_INSTANCE_INFO_KEY = "container-instance-info";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Container info.
     */
    @Initialize(CONTAINER_INFO_KEY)
    @ValidateValue(ValidationPolicy.NOT_EMPTY)
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
     * Docker info objects builder.
     */
    @Resource
    InfoBuilder dockerInfoBuilder;

    /**
     * Docker client.
     */
    @Resource
    DockerClient dockerClient;

    @SuppressWarnings("unchecked")
    public boolean execute(Context context) throws Exception {
	// initialize command
	CommandInitializer initializer = new CommandInitializerImpl();
	initializer.initialize(context, this);

	// validate image info
	if (containerInfo.getImageInfo() == null) {
	    executionResult.completeAsFailure(messageProvider, "ccc.imageinfo_notdefined_failure");
	    return Command.CONTINUE_PROCESSING;
	}

	// exit if container with name already exist
	if (dockerClient.containerExists(session, containerInfo)) {
	    Object[] args = { containerInfo.getName() };
	    executionResult.completeAsSuccessful(messageProvider, "ccc.container_already_exists_success", args);
	    String message = messageProvider.getMessage("ccc.container_already_exists_note");
	    executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
	    return Command.CONTINUE_PROCESSING;
	}

	// configure container configuration object
	ContainerConfiguration containerConfig = containerInfo.getContainerConfiguration();
	containerConfig.setImage(containerInfo.getImageInfo().getFullyQualifiedName());

	// post to create container
	CreatedContainer createdContainer = session.httpPostForObject(CREATE_CONTAINER_URI, containerConfig,
		CreatedContainer.class);

	// add messages
	Object[] args = { createdContainer.getId(), containerInfo.getImageInfo().getFullyQualifiedName() };
	String message = messageProvider.getMessage("ccc.create_container_info", args);
	executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);

	// log warnings
	for (String warning : createdContainer.getWarnings()) {
	    executionResult.addMessage("Warnings", warning);
	}

	// rename container to container name
	Map<String, String> uriVariables = new HashMap<String, String>();
	uriVariables.put("id", createdContainer.getId());
	uriVariables.put("newname", containerInfo.getName());
	session.httpPost(RENAME_CONTAINER_URI, uriVariables);

	// add messages
	Object[] args2 = { containerInfo.getName() };
	message = messageProvider.getMessage("ccc.rename_container_info", args2);
	executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);

	// create container instance info and store it in the context
	String id = createdContainer.getId();
	ContainerInstanceInfo instanceInfo = dockerInfoBuilder.buildInstanceInfo(id, containerInfo);
	context.put(CONTAINER_INSTANCE_INFO_KEY, instanceInfo);

	// complete result
	Object[] args3 = { containerInfo.getName() };
	executionResult.completeAsSuccessful(messageProvider, "ccc.create_container_completed", args3);

	return Command.CONTINUE_PROCESSING;
    }

}
