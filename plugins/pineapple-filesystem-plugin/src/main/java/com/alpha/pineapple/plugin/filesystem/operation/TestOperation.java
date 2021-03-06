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

package com.alpha.pineapple.plugin.filesystem.operation;
import static com.alpha.javautils.ArgumentUtils.notNull;
import static org.apache.commons.lang3.Validate.notEmpty;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.command.CommandException;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.filesystem.model.Filesystem;
import com.alpha.pineapple.plugin.filesystem.model.FilesystemRoot;
import com.alpha.pineapple.plugin.filesystem.model.Mapper;
import com.alpha.pineapple.plugin.filesystem.session.FileSystemSession;
import com.alpha.pineapple.session.Session;

@PluginOperation(OperationNames.TEST)
public class TestOperation implements Operation {

	/**
	 * First list index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * VFS file resolution test command.
	 */
	@Resource
	Command testVfsFilePropertiesCommand;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Command runner
	 */
	@Resource
	CommandRunner commandRunner;

	/**
	 * Model mapper object.
	 */
	@Resource
	Mapper mapper;

	public void execute(Object content, Session session, ExecutionResult executionResult)
			throws PluginExecutionFailedException {
		notNull(content, "content is undefined.");
		notNull(session, "session is undefined.");
		notNull(executionResult, "executionResult is undefined.");

		// throw exception if required type isn't available
		if (!(content instanceof Filesystem)) {
			Object[] args = { Filesystem.class };
			String message = messageProvider.getMessage("to.content_typecast_failed", args);
			throw new PluginExecutionFailedException(message);
		}

		// throw exception if required type isn't available
		if (!(session instanceof FileSystemSession)) {
			Object[] args = { FileSystemSession.class };
			String message = messageProvider.getMessage("to.session_typecast_failed", args);
			throw new PluginExecutionFailedException(message);
		}

		// configure command runner with execution result
		commandRunner.setExecutionResult(executionResult);

		try {
			// type cast to file system model object
			Filesystem model = (Filesystem) content;

			// type cast to file system session
			FileSystemSession fileSystemSession = (FileSystemSession) session;

			// do tests
			runFileObjectTests(model, fileSystemSession);

			// compute execution state from children
			executionResult.completeAsComputed(messageProvider, "to.completed", null, "to.failed", null);
		} catch (Exception e) {
			Object[] args = { e.toString() };
			String message = messageProvider.getMessage("to.error", args);
			throw new PluginExecutionFailedException(message, e);
		}
	}

	void runFileObjectTests(Filesystem model, FileSystemSession fileSystemSession) throws CommandException {
		// get root
		List<FilesystemRoot> roots = model.getRoot();

		for (FilesystemRoot root : roots) {
			// create description
			Object[] args = { root.getTargetPath() };
			String message = messageProvider.getMessage("to.resolve_file_info", args);

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapVfsFilePropertiesTest(root, fileSystemSession, context);

			// run test
			commandRunner.run(testVfsFilePropertiesCommand, message, context);
		}
	}

}
