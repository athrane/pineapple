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

package com.alpha.testutils;

import static com.alpha.testutils.SshTestConstants.SHARED_GUEST_LINUX_DIR;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.alpha.pineapple.plugin.ssh.model.Execute;
import com.alpha.pineapple.plugin.ssh.model.ObjectFactory;
import com.alpha.pineapple.plugin.ssh.model.SecureCopy;
import com.alpha.pineapple.plugin.ssh.model.Ssh;
import com.alpha.pineapple.plugin.ssh.model.SshCommand;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for
 * unit testing by creating content for operations.
 */
public class ObjectMotherContent {

	/**
	 * Object factory.
	 */
	ObjectFactory objectFactory;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * ObjectMotherContent constructor.
	 */
	public ObjectMotherContent() {
		super();

		// create test case factory
		objectFactory = new com.alpha.pineapple.plugin.ssh.model.ObjectFactory();
	}

	/**
	 * Resolve remote shared directory on Linux.
	 * 
	 * @param testDirectory
	 *            test directory.
	 * 
	 * @return remote shared directory on Windows.
	 */
	public String resolveRemoteSharedDirectory_OnLinux(File testDirectory) {
		String sharedRemoteTestDirectory = new StringBuilder().append(SHARED_GUEST_LINUX_DIR).append("/")
				.append(testDirectory.getName()).toString();
		return sharedRemoteTestDirectory;
	}

	/**
	 * Create empty SSH document.
	 * 
	 * @return empty SSH document.
	 */
	public Ssh createEmptySSH() {
		return objectFactory.createSsh();
	}

	/**
	 * Create SSH document with single file copy.
	 * 
	 * @param local
	 *            local file name.
	 * @param remote
	 *            remote file name.
	 * @param enableVarSubstitution
	 *            enable variable substitution.
	 * 
	 * @return SSH document with single file copy.
	 */
	public Ssh createModelWithSingleFileCopy(String local, String remote, boolean enableVarSubstitution) {
		Ssh ssh = createEmptySSH();
		List<SshCommand> commands = ssh.getCommands();

		SecureCopy command = objectFactory.createSecureCopy();
		command.setSource(local);
		command.setDestination(remote);
		command.setSubstituteVariables(new Boolean(enableVarSubstitution));
		commands.add(command);
		return ssh;
	}

	/**
	 * Create SSH document with two files to copy.
	 * 
	 * @param local
	 *            local file name.
	 * @param remote
	 *            remote file name.
	 * @param local
	 *            local file #2 name.
	 * @param remote
	 *            remote file #2 name.
	 * 
	 * @return SSH document with two files to copy.
	 */
	public Ssh createModelWithTwoFilesToCopy(String local, String remote, String local2, String remote2) {
		Ssh ssh = createEmptySSH();
		List<SshCommand> commands = ssh.getCommands();

		SecureCopy command = objectFactory.createSecureCopy();
		command.setSource(local);
		command.setDestination(remote);
		commands.add(command);

		SecureCopy command2 = objectFactory.createSecureCopy();
		command2.setSource(local2);
		command2.setDestination(remote2);
		commands.add(command2);

		return ssh;
	}

	/**
	 * Create SSH document with single remote command.
	 * 
	 * @param remoteCommand
	 *            remote command.
	 * 
	 * @return SSH document with single file copy.
	 */
	public Ssh createModelWithSingleExecuteCommand(String remoteCommand) {
		Ssh ssh = createEmptySSH();
		List<SshCommand> commands = ssh.getCommands();

		Execute command = objectFactory.createExecute();
		command.setCommand(remoteCommand);
		commands.add(command);

		return ssh;
	}

}
