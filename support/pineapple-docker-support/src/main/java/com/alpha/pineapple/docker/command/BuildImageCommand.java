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

import static com.alpha.pineapple.docker.DockerConstants.BUILD_IMAGE_URI;
import static com.alpha.pineapple.docker.DockerConstants.CONTENT_TYPE_KEY;
import static com.alpha.pineapple.docker.DockerConstants.CONTENT_TYPE_TAR;
import static com.alpha.pineapple.docker.utils.ModelUtils.containsStatusUpdate;
import static com.alpha.pineapple.docker.utils.ModelUtils.containsStreamUpdate;
import static com.alpha.pineapple.docker.utils.ModelUtils.remoteLfFromStreamUpdate;
import static com.alpha.pineapple.execution.ExecutionResult.MSG_MESSAGE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.rest.JsonMessage;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.docker.utils.RestResponseException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.session.SessionException;
import com.alpha.pineapple.test.matchers.PineappleMatchers;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which builds a Docker image from a TAR archive. The TAR archive is expected
 * to contain a Docker file at its root. The created image is tagged.
 *
 * Command is idempotent. If target image already exists, the command exits with
 * a successful result.
 *
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>tar-archive</code> contains path to TAR archive which contains
 * Docker file at the root and other files. The type is
 * <code>java.io.File</code>.</li>
 * 
 * <li><code>pull-image</code> defines whether image should be pulled from
 * registry even if local (older) copy exist. The type is
 * <code>java.lang.Boolean</code>.</li>
 * 
 * <li><code>target-image-info</code> contains target Docker image info, which
 * is used to tag the image created from the Docker file. The type is
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
 * from Docker. The type is
 * <code>com.alpha.pineapple.docker.model.rest.JsonMessage[]</code>.</li>
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
public class BuildImageCommand implements Command {

	/**
	 * Null image creation info's.
	 */
	static final JsonMessage[] NULL_INFOS = {};

	/**
	 * Single digit string representation for boolean true.
	 */
	static final String TRUE_AS_DIGIT = "1";

	/**
	 * Single digit string representation for boolean false.
	 */
	static final String FALSE_AS_DIGIT = "0";

	/**
	 * Key used to identify property in context: Image info.
	 */
	public static final String IMAGE_INFO_KEY = "image-info";

	/**
	 * Key used to identify property in context: Pull image behavior.
	 */
	public static final String PULL_IMAGE_KEY = "pull-image";

	/**
	 * Key used to identify property in context: Path to TAR archive.
	 */
	public static final String TAR_ARCHIVE_KEY = "tar-archive";

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
	 * TAR archive.
	 */
	@Initialize(TAR_ARCHIVE_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	File tarArchive;

	/**
	 * Pull image behavior.
	 */
	@Initialize(PULL_IMAGE_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	Boolean pullImage;

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
	 * Docker client.
	 */
	@Resource
	DockerClient dockerClient;

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "dockerMessageProvider")
	MessageProvider messageProvider;

	public boolean execute(Context context) throws Exception {

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		buildImage(context);

		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Build image.
	 * 
	 * @param context
	 *            command context.
	 * 
	 * @throws SessionException
	 *             if HTTP post fails.
	 */
	@SuppressWarnings("unchecked")
	void buildImage(Context context) throws SessionException {

		// exit if image exists in repository
		if (dockerClient.imageExists(session, imageInfo)) {

			// store null creation info's
			context.put(IMAGE_CREATION_INFOS_KEY, NULL_INFOS);

			Object[] args = { imageInfo.getFullyQualifiedName() };
			executionResult.completeAsSuccessful(messageProvider, "bic.image_already_exists_success", args);
			String message = messageProvider.getMessage("bic.image_already_exists_note");
			executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
			return;
		}

		// validate TAR archive exist
		Matcher<File> matcher = PineappleMatchers.doesFileExist();
		if (matcher.matches(tarArchive)) {
			Object[] args = { tarArchive };
			String message = messageProvider.getMessage("bic.validate_tar_exist_info", args);
			executionResult.addMessage(MSG_MESSAGE, message);
		} else {
			// complete with failure
			Object[] args = { tarArchive };
			executionResult.completeAsFailure(messageProvider, "bic.validate_tar_exist_failure", args);
			return;
		}

		// set URI variables
		Map<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("tag", imageInfo.getFullyQualifiedName());
		uriVariables.put("pull", getPullImageBehavior());

		// create HTTP request object
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(CONTENT_TYPE_KEY, CONTENT_TYPE_TAR);
		HttpEntity<byte[]> requestEntity;
		FileInputStream openInputStream = null;

		try {
			// read TAR archive into HTTP request
			openInputStream = FileUtils.openInputStream(tarArchive);
			byte[] byteArray = IOUtils.toByteArray(openInputStream);
			requestEntity = new HttpEntity<byte[]>(byteArray, requestHeaders);

			// close stream
			IOUtils.closeQuietly(openInputStream);

		} catch (IOException e) {

			// complete with error
			executionResult.completeAsError(messageProvider, "bic.read_tar_archive_error", e);

			// close stream
			IOUtils.closeQuietly(openInputStream);

			return;
		}

		// post to create image
		JsonMessage[] infos = null;

		try {

			infos = session.httpPostForObjectWithMultipleRootElements(BUILD_IMAGE_URI, uriVariables, requestEntity,
					JsonMessage[].class, CONTENT_TYPE_TAR);

		} catch (RestResponseException rre) {
			Object[] args2 = { rre.getStatusCode(), rre.getMessage() };
			executionResult.completeAsFailure(messageProvider, "bic.build_image_failed", args2);
			return;
		}

		// add messages
		Object[] args = { infos.length };
		String message = messageProvider.getMessage("bic.list_image_info", args);
		executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);

		for (JsonMessage info : infos) {

			// handle normal status update
			if (containsStatusUpdate(info)) {
				Object[] args2 = { info.getStatus() };
				String message2 = messageProvider.getMessage("bic.list_single_info", args2);
				executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message2);
				continue;
			}

			if (containsStreamUpdate(info)) {
				Object[] args2 = { remoteLfFromStreamUpdate(info.getStream()) };
				String message2 = messageProvider.getMessage("bic.list_single_stream_info", args2);
				executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message2);
				continue;
			}

		}

		// store creation info's
		context.put(IMAGE_CREATION_INFOS_KEY, infos);

		// complete successfully
		Object[] args2 = { imageInfo.getFullyQualifiedName() };
		executionResult.completeAsSuccessful(messageProvider, "bic.build_image_completed", args2);
	}

	/**
	 * Return the pull image behaviour as a string.
	 * 
	 * @return the pull image behaviour as a string.
	 */
	String getPullImageBehavior() {
		if (!pullImage.booleanValue())
			return FALSE_AS_DIGIT;
		return TRUE_AS_DIGIT;
	}

}
