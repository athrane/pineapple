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

package com.alpha.pineapple.plugin.weblogic.installation.command;

import static com.alpha.pineapple.plugin.weblogic.installation.WeblogicInstallationConstants.SILENT_INSTALL_XML;

import java.io.File;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.weblogic.installation.response.ResponseFileBuilder;
import com.alpha.pineapple.test.Asserter;
import com.alpha.pineapple.test.matchers.PineappleMatchers;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which creates a silent XML file WebLogic installation.
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>target-directory</code> defines target directory where WebLogic is
 * installed. The type is <code>java.io.File</code>.</li>
 * 
 * <li><code>local-jvm</code> defines the location of the local JVM. The type is
 * <code>java.io.File</code>.</li>
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
 * <ul>
 * <li><code>silent-xml</code> contains a file object which represents the
 * created silent XML file. The type is <code>java.io.File</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is:
 * <ul>
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the command succeeded. If the
 * command failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the command fails due to an exception then the exception isn't caught,
 * but passed on to the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * </ul>
 * </p>
 */

public class CreateSilentXmlCommand implements Command {

	/**
	 * Key used to identify property in context: Target directory where WebLogic
	 * is installed.
	 */
	public static final String TARGET_DIRECTORY = "target-directory";

	/**
	 * Key used to identify property in context: Location of the local JVM.
	 */
	public static final String LOCAL_JVM = "local-jvm";

	/**
	 * Key used to identify property in context: Contains execution result
	 * object.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Key used to identify property in context: Contains returned silent XML
	 * file.
	 */
	public static final Object SILENTXML_FILE = "silent-xml";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Target directory where JRockit is installed.
	 */
	@Initialize(TARGET_DIRECTORY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	File targetDirectory;

	/**
	 * Defines the location of the local JVM.
	 */
	@Initialize(LOCAL_JVM)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	File localJvm;

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Runtime directory provider.
	 */
	@Resource
	RuntimeDirectoryProvider coreRuntimeDirectoryProvider;

	/**
	 * Hamcrest asserter.
	 */
	@Resource
	Asserter asserter;

	/**
	 * Pineapple matcher factory.
	 */
	@Resource
	PineappleMatchers pineappleMatchers;

	/**
	 * Response file builder for product.
	 * 
	 * Property isn't set by auto wiring using the resource annotation but
	 * instead, a setter method is used.
	 */
	ResponseFileBuilder responseFileBuilder;

	/**
	 * Setter used by Spring to set the builder.
	 * 
	 * @param responseFileBuilder
	 *            Product specific builder.
	 */
	public void setResponseFileBuilder(ResponseFileBuilder responseFileBuilder) {
		this.responseFileBuilder = responseFileBuilder;
	}

	public boolean execute(Context context) throws Exception {

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// register execution result with asserter
		asserter.setExecutionResult(executionResult);

		// create silent response file
		createResponseFile(context);

		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Create silent response file.
	 * 
	 * @param context
	 *            Command context.
	 * 
	 * @throws Exception
	 *             If execution fails.
	 */
	void createResponseFile(Context context) throws Exception {
		// get temporary directory
		File tempDirectory = coreRuntimeDirectoryProvider.getTempDirectory();

		// create file object
		File silentXmlFile = new File(tempDirectory, SILENT_INSTALL_XML);

		// delete file if it exists
		if (silentXmlFile.exists()) {
			boolean succeded = silentXmlFile.delete();

			// handle failure to delete file
			if (!succeded) {
				Object[] args = { silentXmlFile };
				executionResult.completeAsFailure(messageProvider, "csxc.delete_oldsilentxml_failed", args);
				return;
			}
		}

		// create silent XML file
		Collection<String> lines = responseFileBuilder.getResponseForInstallation(targetDirectory, localJvm);

		// write the file
		FileUtils.writeLines(silentXmlFile, lines);

		// create file exist matcher
		Matcher<File> fileMatcher = pineappleMatchers.doesFileExist();

		// assert whether file exists
		Object[] args = { silentXmlFile };
		String description = messageProvider.getMessage("csxc.assert_silentxml_exists", args);
		asserter.assertObject(silentXmlFile, fileMatcher, description);

		// handle positive case
		if (asserter.lastAssertionSucceeded()) {

			// add file to context
			context.put(SILENTXML_FILE, silentXmlFile);

			// complete as success
			Object[] args2 = { silentXmlFile };
			executionResult.completeAsSuccessful(messageProvider, "csxc.succeed", args);
			return;
		}

		// add null to context
		context.put(SILENTXML_FILE, null);

		// complete as failure
		Object[] args2 = { silentXmlFile };
		executionResult.completeAsFailure(messageProvider, "csxc.failed", args2);
	}

}
