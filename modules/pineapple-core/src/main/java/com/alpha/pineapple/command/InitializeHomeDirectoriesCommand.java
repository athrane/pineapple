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

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which create directories which are resolved from the runtime directory
 * provider.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
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
 * <li>After execution the these directories are created if they doesn't already
 * exist prior to execution of the command: the runtime directory, the
 * configuration directory, the modules directory and the reports directory.
 * </li>
 * 
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
public class InitializeHomeDirectoriesCommand implements Command {
    /**
     * Key used to identify property in context: Contains execution result
     * object,.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Runtime directory resolver.
     */
    @Resource
    RuntimeDirectoryProvider runtimeDirectoryProvider;

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Defines execution result object.
     */
    @Initialize(EXECUTIONRESULT_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    ExecutionResult executionResult;

    public boolean execute(Context context) throws Exception {
	// log debug message
	if (logger.isDebugEnabled()) {
	    logger.debug(messageProvider.getMessage("irdc.start"));
	}

	// initialize command
	CommandInitializer initializer = new CommandInitializerImpl();
	initializer.initialize(context, this);

	// do initialization
	doInitialization(context);

	// log debug message
	if (logger.isDebugEnabled()) {
	    logger.debug(messageProvider.getMessage("irdc.completed"));
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
    void doInitialization(Context context) throws CommandException {

	// get home directory
	File homeDir = runtimeDirectoryProvider.getHomeDirectory();

	// validate home is absolute directory
	if (!homeDir.isAbsolute()) {
	    // create error message and exit
	    Object[] args = { homeDir.getAbsolutePath() };
	    executionResult.completeAsFailure(messageProvider, "irdc.homedir_notabsolute_failure", args);
	    return;
	}

	// if it doesn't exist create home directory
	if (!homeDir.exists()) {

	    // create
	    try {
		FileUtils.forceMkdir(homeDir);
	    } catch (Exception e) {
		executionResult.completeAsError(messageProvider, "irdc.homedir_creation_error", e);
		return;
	    }

	    // add message
	    Object[] args = { homeDir.getAbsolutePath() };
	    String message = messageProvider.getMessage("irdc.homedir_created_info", args);
	    executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);

	} else {
	    // add message
	    Object[] args = { homeDir.getAbsolutePath() };
	    String message = messageProvider.getMessage("irdc.homedir_exists_info", args);
	    executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
	}

	// get conf directory
	File confDir = runtimeDirectoryProvider.getConfigurationDirectory();

	// if it doesn't exist create configuration directory
	if (!confDir.exists()) {

	    // create
	    try {
		FileUtils.forceMkdir(confDir);
	    } catch (Exception e) {
		executionResult.completeAsError(messageProvider, "irdc.confdir_creation_error", e);
		return;
	    }

	    // add message
	    Object[] args = { confDir.getAbsolutePath() };
	    String message = messageProvider.getMessage("irdc.confdir_created_info", args);
	    executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);

	} else {
	    // add message
	    Object[] args = { confDir.getAbsolutePath() };
	    String message = messageProvider.getMessage("irdc.confdir_exists_info", args);
	    executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
	}

	// get modules directory
	File modulesDir = runtimeDirectoryProvider.getModulesDirectory();

	// if it doesn't exist create modules directory
	if (!modulesDir.exists()) {

	    // create
	    try {
		FileUtils.forceMkdir(modulesDir);
	    } catch (Exception e) {
		executionResult.completeAsError(messageProvider, "irdc.modulesdir_creation_error", e);
		return;
	    }

	    // add message
	    Object[] args = { modulesDir.getAbsolutePath() };
	    String message = messageProvider.getMessage("irdc.modulesdir_created_info", args);
	    executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);

	} else {
	    // add message
	    Object[] args = { modulesDir.getAbsolutePath() };
	    String message = messageProvider.getMessage("irdc.modulesdir_exists_info", args);
	    executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
	}

	// get reports directory
	File reportsDir = runtimeDirectoryProvider.getReportsDirectory();

	// if it doesn't exist create reports directory
	if (!reportsDir.exists()) {

	    // create
	    try {
		FileUtils.forceMkdir(reportsDir);
	    } catch (Exception e) {
		executionResult.completeAsError(messageProvider, "irdc.reportsdir_creation_error", e);
		return;
	    }

	    // add message
	    Object[] args = { reportsDir.getAbsolutePath() };
	    String message = messageProvider.getMessage("irdc.reportsdir_created_info", args);
	    executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);

	} else {
	    // add message
	    Object[] args = { reportsDir.getAbsolutePath() };
	    String message = messageProvider.getMessage("irdc.reportsdir_exists_info", args);
	    executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
	}

	// complete operation as successful
	executionResult.completeAsSuccessful(messageProvider, "irdc.succeed");
    }

}
