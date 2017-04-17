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

import static com.alpha.javautils.StringUtils.removeEnclosingBrackets;
import static com.alpha.pineapple.docker.DockerConstants.CUSTOM_JAXB_MAPS;
import static com.alpha.pineapple.docker.utils.ModelUtils.createTruncatedId;
import static com.alpha.pineapple.docker.utils.ModelUtils.isNoImageRepoTagsDefined;
import static com.alpha.pineapple.execution.report.JaxbReportUtils.reportOnObject;

import java.util.List;

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
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.model.rest.ImageInspect;
import com.alpha.pineapple.docker.model.rest.ListedImage;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which creates a report of all images in a Docker daemon.
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
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
 * 
 * <ul>
 * <li>The information about the processed images are added as a tree of
 * <code>ExecutionResult</code> object with properties.</li>
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
public class ReportOnImagesCommand implements Command {

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
	 * First repo tags list index.
	 */
	static final int FIRST_LIST_INDEX = 0;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

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
	 * Docker info object builder.
	 */
	@Resource
	InfoBuilder dockerInfoBuilder;

	/**
	 * Docker JAXB Getter method matcher.
	 */
	@Resource
	GetterMethodMatcher dockerJaxbGetterMethodMatcher;

	public boolean execute(Context context) throws Exception {
		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// list images
		ListedImage[] images = dockerClient.listImages(session, executionResult);

		// add messages
		Object[] args = { images.length };
		String message = messageProvider.getMessage("roic.list_image_info", args);
		executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);

		for (ListedImage image : images) {

			// skip image if it has no name
			if (isNoImageRepoTagsDefined(image))
				continue;

			String repoTagsAsString = removeEnclosingBrackets(image.getRepoTags().toString());
			Object[] args2 = { repoTagsAsString, createTruncatedId(image.getId()),
					createTruncatedId(image.getParentId()) };
			String message2 = messageProvider.getMessage("roic.list_single_image_header_info", args2);
			ExecutionResult childResult = executionResult.addChild(message2);
			inspectImage(image, childResult);

		}

		// complete result
		executionResult.completeAsSuccessful(messageProvider, "roic.list_images_completed");

		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Inspect image.
	 * 
	 * @param container
	 *            container meta data.
	 * @param result
	 *            result where container data should be added to.
	 */
	void inspectImage(ListedImage image, ExecutionResult result) {

		try {
			// report using reflection
			List<String> repoTags = image.getRepoTags();
			String firstRepoTag = repoTags.get(FIRST_LIST_INDEX);
			// reportOnObject(result, image, dockerJaxbGetterMethodMatcher,
			// CUSTOM_JAXB_MAPS);
			ImageInfo imageInfo = dockerInfoBuilder.buildImageInfoFromFQName(firstRepoTag);
			ImageInspect inspectedImage = dockerClient.inspectImage(session, imageInfo, result);
			reportOnObject(result, inspectedImage, dockerJaxbGetterMethodMatcher, CUSTOM_JAXB_MAPS);
			result.setState(ExecutionResult.ExecutionState.SUCCESS);

		} catch (Exception e) {
			result.completeAsError(messageProvider, "roic.inpect_image_failure", e);
		}
	}

}
