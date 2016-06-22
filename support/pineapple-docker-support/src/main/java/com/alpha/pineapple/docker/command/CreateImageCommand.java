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

import static com.alpha.pineapple.docker.DockerConstants.CREATE_IMAGE_URI;
import static com.alpha.pineapple.docker.DockerConstants.CREATE_TAGGED_IMAGE_URI;
import static com.alpha.pineapple.docker.utils.ModelUtils.containsErrorMessage;
import static com.alpha.pineapple.docker.utils.ModelUtils.containsStatusUpdate;
import static com.alpha.pineapple.docker.utils.ModelUtils.isImageCreationSuccessful;
import static com.alpha.pineapple.docker.utils.ModelUtils.isTaggedImage;

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
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.rest.ImageCreation;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which creates a Docker image, either by pulling it from the registry or by
 * importing it.
 *
 * Command is idempotent. If target image already exists the command completes
 * with a successful result.
 *
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>image-info</code> contains Docker image info, which defines an
 * image through a Docker repository and image tag. Example of official
 * repository name is "ubuntu" which holds Ubuntu images. Example of user
 * repository (which has the name format user/repo) is thrane/pineapple. Example
 * of image tags are "latest" and"12.10". The type is
 * <code>com.alpha.pineapple.docker.ImageInfo</code>.</li>
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
 * <ul>
 * 
 * <li><code>image-creation-infos</code> contains array of image creation info's
 * from Docker. If image already exists then no info's are returned. The type is
 * <code>com.alpha.pineapple.docker.model.ImageCreation[]</code>.</li>
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
public class CreateImageCommand implements Command {

    /**
     * Null image creation info's.
     */
    static final ImageCreation[] NULL_INFOS = {};

    /**
     * Key used to identify property in context: Image info.
     */
    public static final String IMAGE_INFO_KEY = "image-info";

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
     * Key used to identify property in context: array of image creation info
     * objects.
     */
    public static final String IMAGE_CREATION_INFOS_KEY = "image-creation-infos";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Image info.
     */
    @Initialize(IMAGE_INFO_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    ImageInfo imageInfo;

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

    @SuppressWarnings("unchecked")
    public boolean execute(Context context) throws Exception {

	// initialize command
	CommandInitializer initializer = new CommandInitializerImpl();
	initializer.initialize(context, this);

	// exit if image exists in repository
	if (dockerClient.imageExists(session, imageInfo)) {

	    // store null creation info's
	    context.put(IMAGE_CREATION_INFOS_KEY, NULL_INFOS);

	    // set state
	    Object[] args = { imageInfo.getFullyQualifiedName() };
	    executionResult.completeAsSuccessful(messageProvider, "cic.image_already_exists_success", args);
	    String message = messageProvider.getMessage("cic.image_already_exists_note");
	    executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
	    return Command.CONTINUE_PROCESSING;
	}

	// set variables
	Map<String, String> uriVariables = new HashMap<String, String>(3);
	uriVariables.put("image", imageInfo.getRepository());

	// post to create image
	ImageCreation[] infos = null;
	if (isTaggedImage(imageInfo)) {
	    uriVariables.put("tag", imageInfo.getTag());
	    infos = session.httpPostForObjectWithMultipleRootElements(CREATE_TAGGED_IMAGE_URI, uriVariables,
		    ImageCreation[].class);
	} else {
	    infos = session.httpPostForObjectWithMultipleRootElements(CREATE_IMAGE_URI, uriVariables,
		    ImageCreation[].class);
	}

	// add messages
	ImageCreation failedInfo = null;
	Object[] args = { infos.length };
	String message = messageProvider.getMessage("cic.list_image_info", args);
	executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
	for (ImageCreation info : infos) {

	    // handle normal status update
	    if (containsStatusUpdate(info)) {
		Object[] args2 = { info.getStatus() };
		String message2 = messageProvider.getMessage("cic.list_single_info", args2);
		executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message2);
		continue;
	    }

	    // handle error and skip the loop
	    if (containsErrorMessage(info)) {
		failedInfo = info;
		break;
	    }
	}

	// store creation info's
	context.put(IMAGE_CREATION_INFOS_KEY, infos);

	// complete result
	if (isImageCreationSuccessful(infos)) {
	    Object[] args2 = { imageInfo.getFullyQualifiedName() };
	    executionResult.completeAsSuccessful(messageProvider, "cic.create_image_completed", args2);
	} else {
	    Object[] args2 = { failedInfo.getError() };
	    executionResult.completeAsFailure(messageProvider, "cic.create_image_failed", args2);
	}

	return Command.CONTINUE_PROCESSING;
    }

}
