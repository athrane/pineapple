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

package com.alpha.pineapple.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hamcrest.core.IsAnything;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which copies example modules into the modules directory in a generated
 * default configuration.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>destination-dir</code> Defines the destination directory where the
 * example modules should be copied to. The directory must be not empty or null.
 * The type is <code>java.io.File</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in
 * the context:
 * 
 * <ul>
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the command succeeded. If the
 * command failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the command fails due to an exception then the exception isn't caught,
 * but passed on the the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * </ul>
 * </p>
 */
public class CopyExampleModulesCommand implements Command, ApplicationContextAware {
	/**
	 * Search string for locating the root directory in the
	 * pineapple-example-modules project which contains the example modules..
	 */
	static final String RESOURCES_SEARCH_STRING = "classpath:pineapple-example-modules/**/*";

	/**
	 * Name of the root directory in the pineapple-example-modules project which
	 * contains the example modules.
	 */
	static final String EXAMPLE_MODULES = "pineapple-example-modules";

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Key used to identify property in context: Defines the destination directory
	 * where example modules should be copied to.
	 * 
	 */
	public static final String DESTINATION_DIR_KEY = "destination-dir";

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Defines the destination directory where the example modules should be copied
	 * to.
	 */
	@Initialize(DESTINATION_DIR_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	File destinationDirectory;

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	/**
	 * Application context.
	 */
	ApplicationContext applicationContext;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public boolean execute(Context context) throws Exception {
		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// declare resources
		org.springframework.core.io.Resource[] resources = null;

		try {
			// get resources from example modules
			resources = applicationContext.getResources(RESOURCES_SEARCH_STRING);

			// iterator over the resources
			for (org.springframework.core.io.Resource resource : resources) {
				// copy resource if its a file
				if (isResourceFile(resource)) {
					copyResourceAsFile(resource);
				}
			}

		} catch (FileNotFoundException e) {
			executionResult.completeAsError(messageProvider, "cemc.locate_resources_error", e);
			return Command.CONTINUE_PROCESSING;

		} catch (IOException e) {
			executionResult.completeAsError(messageProvider, "cemc.copy_error", e);
			return Command.CONTINUE_PROCESSING;
		}

		// complete as success
		Object[] args = { destinationDirectory };
		executionResult.completeAsSuccessful(messageProvider, "cemc.success", args);

		// set successful result
		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Copy resource as a file.
	 * 
	 * @param resource
	 *            Spring resource.
	 * 
	 * @throws IOException
	 */
	void copyResourceAsFile(org.springframework.core.io.Resource resource) throws IOException {

		// get sub path
		String resourcePath = StringUtils.substringBetween(resource.getDescription(), EXAMPLE_MODULES,
				resource.getFilename());

		// create destination path for resource
		String destinationFileName = new StringBuilder().append(destinationDirectory).append(resourcePath)
				.append(File.separator).append(resource.getFilename()).toString();

		// create destination file
		File destinationFile = new File(destinationFileName);

		createDirectory(destinationFile);
		copyFile(resource, destinationFile);

		// add message
		Object[] args = { destinationFile };
		String message = messageProvider.getMessage("cemc.copied_resource_info", args);
		executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
	}

	/**
	 * Create directory if required.
	 * 
	 * @param file
	 *            Destination path for file.
	 * 
	 * @throws IOException
	 *             If creation fails.
	 */
	void createDirectory(File file) throws IOException {
		if (!file.getParentFile().exists()) {
			FileUtils.forceMkdir(file.getParentFile());
		}
	}

	/**
	 * Copy file.
	 * 
	 * @param file
	 *            Destination path for file.
	 * 
	 * @throws IOException
	 *             If creation fails.
	 */
	void copyFile(org.springframework.core.io.Resource resource, File file) throws IOException {
		InputStream inputStream = null;

		try {
			inputStream = resource.getInputStream();
			FileUtils.copyInputStreamToFile(inputStream, file);

		} finally {
			IOUtils.closeQuietly(inputStream);
			;
		}
	}

	/**
	 * Returns true if resource is a file.
	 * 
	 * @param resource
	 *            Spring resource.
	 * 
	 * @return true if resource is a file.
	 * 
	 * @throws IOException
	 *             if query fails.
	 */
	boolean isResourceFile(org.springframework.core.io.Resource resource) throws IOException {
		URI uri = resource.getURI();
		String uriAsString = uri.toString();
		return (!uriAsString.endsWith("/"));
	}
}
