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

import static com.alpha.pineapple.CoreConstants.MODULE_FILENAME;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which initializes runtime directories. The directories are initialized from
 * the directories read from the runtime directory provider in the Spring
 * context.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
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
 * <li><code>module-file</code> Defines absolute path to the module file for the
 * initialized module. The type is <code>java.io.File</code>.</li>
 * 
 * <li><code>module-model-file</code> Defines absolute path to the module model
 * file for the initialized module. The type is <code>java.io.File</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is:
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
public class InitializeOperationCommand implements Command {
	/**
	 * Key used to identify property in context: Defines absolute path to the module
	 * file for the initialized module.
	 */
	public static final String MODULE_FILE_KEY = "module-file";

	/**
	 * Key used to identify property in context: Defines absolute path to the module
	 * model file for the initialized module.
	 */
	public static final String MODULE_MODEL_FILE_KEY = "module-model-file";

	/**
	 * Key used to identify property in context: The execution info which contains
	 * information about the operation which should be executed.
	 */
	public static final String EXECUTION_INFO_KEY = CoreConstants.EXECUTION_INFO_KEY;

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Execution info.
	 */
	@Initialize(EXECUTION_INFO_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionInfo executionInfo;

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	public boolean execute(Context context) throws Exception {
		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("ioc.start"));
		}

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// do initialization
		doInitialization(context);

		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("ioc.completed"));
		}

		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Initialize the operation.
	 * 
	 * @param context
	 *            Command context.
	 * 
	 */
	@SuppressWarnings("unchecked")
	void doInitialization(Context context) throws CommandException {

		// create module directory object
		File moduleDirectory = executionInfo.getModuleInfo().getDirectory();

		// create module file object
		File moduleFile = createModuleFile(moduleDirectory);

		// add module file to context
		context.put(MODULE_FILE_KEY, moduleFile);

		// create directory name for model files
		File modelsDirectory = createModelDirectory(moduleDirectory);

		// create model file object
		File modelFile = createModelFile(modelsDirectory);

		// validate file exists, else fail command
		if (!modelFile.exists()) {
			// create error message and exit
			Object[] args = { modelFile.getAbsolutePath() };
			executionResult.completeAsFailure(messageProvider, "ioc.model_file_notfound", args);
			return;
		}

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { moduleFile.getAbsolutePath() };
			String message = messageProvider.getMessage("ioc.model_file_found", args);
			logger.debug(message);
		}

		// complete operation as successful
		executionResult.completeAsSuccessful(messageProvider, "ioc.succeed");

		// add file names to context
		context.put(MODULE_MODEL_FILE_KEY, modelFile);
	}

	/**
	 * Create fully qualified path to module file.
	 *
	 * @param moduleDirectory
	 *            The module directory.
	 * 
	 * @return fully qualified path to module file.
	 */
	File createModuleFile(File moduleDirectory) {
		return new File(moduleDirectory, MODULE_FILENAME);
	}

	/**
	 * Create fully qualified path to model file for target environment.
	 * 
	 * @param moduleDirectory
	 *            The module directory.
	 * 
	 * @return fully qualified path to model file for target environment.
	 */
	File createModelFile(File moduleDirectory) {
		// create file name
		StringBuilder fileName = new StringBuilder();
		fileName.append(executionInfo.getEnvironment());
		fileName.append(".xml");

		// add path to file name
		return new File(moduleDirectory, fileName.toString());
	}

	/**
	 * Create directory where model files in module are located.
	 * 
	 * @param moduleDirectory
	 *            The module directory.
	 * 
	 * @return directory where model files in module are located.
	 */
	File createModelDirectory(File moduleDirectory) {
		// add path to file name
		return new File(moduleDirectory, "models");
	}

}
