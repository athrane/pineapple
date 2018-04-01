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

import static com.alpha.pineapple.docker.DockerConstants.TAG_IMAGE_URI;

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
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which creates a tagged Docker image from an existing image.
 *
 * Command is idempotent. If target image already exists the command exits with
 * a successful result.
 *
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>source-image-info</code> contains source Docker image info, which
 * defines an image through a Docker repository and image tag. The type is
 * <code>com.alpha.pineapple.docker.ImageInfo</code>.</li>
 * 
 * <li><code>target-image-info</code> contains target Docker image info, which
 * defines an image through a Docker target repository and image tag. The type
 * is <code>com.alpha.pineapple.docker.ImageInfo</code>.</li>
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
 * <li>A Docker image is created in the repository named by the repository value
 * in the target image info object. The image is created with the "latest" tag.
 * The image is a tagged copy of the image defined in the source image object.
 * The two images shares the same image ID. The type is
 * <code>com.alpha.pineapple.docker.model.ImageCreationInfo</code>.</li>
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
public class CreateTaggedImageCommand implements Command {

	/**
	 * Key used to identify property in context: Source image info.
	 */
	public static final String SOURCE_IMAGE_INFO_KEY = "source-image-info";

	/**
	 * Key used to identify property in context: Target image info.
	 */
	public static final String TARGET_IMAGE_INFO_KEY = "target-image-info";

	/**
	 * Key used to identify property in context: plugin session object.
	 */
	public static final String SESSION_KEY = "session";

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Target image info.
	 */
	@Initialize(TARGET_IMAGE_INFO_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ImageInfo targetImageInfo;

	/**
	 * Source image info.
	 */
	@Initialize(SOURCE_IMAGE_INFO_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ImageInfo sourceImageInfo;

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

		// fail if source doesn't image exists in repository
		if (!dockerClient.imageExists(session, sourceImageInfo)) {
			Object[] args = { sourceImageInfo.getFullyQualifiedName() };
			executionResult.completeAsFailure(messageProvider, "ctic.tag_image_doesnt_exist_failure", args);
			return Command.CONTINUE_PROCESSING;
		}

		// exit if image exists in repository
		if (dockerClient.imageExists(session, targetImageInfo)) {
			Object[] args = { targetImageInfo.getFullyQualifiedName() };
			executionResult.completeAsSuccessful(messageProvider, "ctic.image_already_exists_success", args);
			String message = messageProvider.getMessage("ctic.image_already_exists_note");
			executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
			return Command.CONTINUE_PROCESSING;
		}

		// set variables
		Map<String, String> urlVariables = new HashMap<String, String>(3);
		urlVariables.put("image", sourceImageInfo.getFullyQualifiedName());
		urlVariables.put("repository", targetImageInfo.getRepository());
		urlVariables.put("tag", targetImageInfo.getTag());

		// post to tag image
		session.httpPost(TAG_IMAGE_URI, urlVariables);

		// complete result
		Object[] args = { targetImageInfo.getFullyQualifiedName(), sourceImageInfo.getFullyQualifiedName() };
		executionResult.completeAsSuccessful(messageProvider, "ctic.tag_image_completed", args);

		return Command.CONTINUE_PROCESSING;
	}

}
