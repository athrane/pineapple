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

package com.alpha.pineapple.plugin.ssh.command;

import static com.alpha.pineapple.plugin.ssh.SshConstants.DISABLE_CHMOD;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.ssh.session.SshSession;
import com.alpha.pineapple.plugin.ssh.utils.SshHelper;
import com.alpha.pineapple.substitution.VariableSubstitutionException;
import com.alpha.pineapple.substitution.VariableSubstitutionProvider;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which copies a local file to a SSH host.
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>local-file</code> defines the path name to the local file to copy.
 * The type is <code>java.lang.String</code>.</li>
 * 
 * <li><code>remote-file</code> defines the path name to the remote file. The
 * type is <code>java.lang.String</code>.</li>
 *
 * <li><code>file-permissions</code> defines the file permissions for the remote
 * file. The type is <code>java.lang.Integer</code>. The parameter is optional.
 * If the parameter is -1 then file permissions isn't set on the remote
 * file.</li>
 *
 * <li><code>user-ownership</code> defines the user ownership of the remote
 * file. The type is <code>java.lang.Integer</code>. The parameter is optional.
 * If the parameter is -1 then ownership isn't set on the remote file.</li>
 * 
 * <li><code>group-ownership</code> defines the group ownership of the remote
 * file. The type is <code>java.lang.Integer</code>. The parameter is optional.
 * If the parameter is -1 then ownership isn't set on the remote file.</li>
 * 
 * <li><code>substitute-variables</code> defines whether the content of the
 * source file will processed for variable substitution. The type is
 * <code>java.lang.Boolean</code>.</li>
 * 
 * <li><code>session</code> defines the agent session used communicate with an
 * agent. The type is
 * <code>com.alpha.pineapple.plugin.ssh.session.SshSession</code>.</li>
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
public class SecureCopyToCommand implements Command {

	/**
	 * Key used to identify property in context: Name of the remote file.
	 */
	public static final String REMOTE_FILE_KEY = "remote-file";

	/**
	 * Key used to identify property in context: Name of the local file.
	 */
	public static final String LOCAL_FILE_KEY = "local-file";

	/**
	 * Key used to identify property in context: File permission pattern.
	 */
	public static final String FILE_PERMISSIONS_KEY = "file-permissions";

	/**
	 * Key used to identify property in context: User ownership.
	 */
	public static final String USER_OWNERSHIP_KEY = "user-ownership";

	/**
	 * Key used to identify property in context: Gruop ownership.
	 */
	public static final String GROUP_OWNERSHIP_KEY = "group-ownership";

	/**
	 * Key used to identify property in context: Variable substitution.
	 */
	public static final String SUBSTITUTE_VARIABLES_KEY = "substitute-variables";

	/**
	 * Key used to identify property in context: plugin session object.
	 */
	public static final String SESSION_KEY = "session";

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Remote file name.
	 */
	@Initialize(REMOTE_FILE_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	String remoteFile;

	/**
	 * Local file.
	 */
	@Initialize(LOCAL_FILE_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	String localFile;

	/**
	 * File permissions.
	 */
	@Initialize(FILE_PERMISSIONS_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	Integer filePermissions;

	/**
	 * User ownership.
	 */
	@Initialize(USER_OWNERSHIP_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	Integer userOwnership;

	/**
	 * Group ownership.
	 */
	@Initialize(GROUP_OWNERSHIP_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	Integer groupOwnership;

	/**
	 * Variable substitution.
	 */
	@Initialize(SUBSTITUTE_VARIABLES_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	Boolean substituteVariables;

	/**
	 * Plugin session.
	 */
	@Initialize(SESSION_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	SshSession session;

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
	 * SSH helper.
	 */
	@Resource
	SshHelper sshHelper;

	/**
	 * Variable substitution provider.
	 */
	@Resource
	VariableSubstitutionProvider coreVariableSubstitutionProvider;

	public boolean execute(Context context) throws Exception {
		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		try {
			// get SFTP channel
			ChannelSftp sftpChannel = session.getSftpChannel();

			// resolve module path
			localFile = sshHelper.resolveModulePath(executionResult, localFile);
			File localFileObject = new File(localFile);

			// assert local file is valid
			if (!sshHelper.isLocalFileValid(localFileObject, executionResult)) {
				executionResult.completeAsFailure(messageProvider, "scct.securecopy_localfile_exists_failure");
				return Command.CONTINUE_PROCESSING;
			}

			// do value substitution
			File processedFile = doVariableSubstitution(localFileObject);

			// upload file
			sftpChannel.put(processedFile.getAbsolutePath(), remoteFile, ChannelSftp.OVERWRITE);

			// set permissions
			setFilePermissions(sftpChannel);
			setUserOwnership(sftpChannel);
			setGroupOwnership(sftpChannel);

			// disconnect
			sftpChannel.disconnect();

			// complete result
			executionResult.completeAsSuccessful(messageProvider, "scct.securecopy_completed");

		} catch (Exception e) {
			executionResult.completeAsError(messageProvider, "scct.securecopy_error", e);
		}

		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Set file permissions.
	 * 
	 * If a null file permissions value is supplied to the command then no
	 * permissions is set.
	 * 
	 * @param sftpChannel
	 *            FTP channel.
	 * 
	 * @throws SftpException
	 *             if operation fails.
	 */
	void setFilePermissions(ChannelSftp sftpChannel) throws SftpException {
		if (skipSettingFilePermissions())
			return;

		int octal = Integer.parseInt(filePermissions.toString(), 8);
		sftpChannel.chmod(octal, remoteFile);

		Object[] args = { filePermissions.toString() };
		String message = messageProvider.getMessage("scct.securecopy_chmod_info", args);
		executionResult.addMessage("File Permissions (CHMOD)", message);
	}

	/**
	 * Set user ownership.
	 * 
	 * If a null user ownership value is supplied to the command then no permissions
	 * is set.
	 * 
	 * @param sftpChannel
	 *            FTP channel.
	 * 
	 * @throws SftpException
	 *             if operation fails.
	 */
	void setUserOwnership(ChannelSftp sftpChannel) throws SftpException {
		if (skipSettingUserOwnership())
			return;

		sftpChannel.chown(userOwnership.intValue(), remoteFile);

		Object[] args = { userOwnership.toString() };
		String message = messageProvider.getMessage("scct.securecopy_chown_info", args);
		executionResult.addMessage("User ownership(CHOWN)", message);
	}

	/**
	 * Set group ownership.
	 * 
	 * If a null user ownership value is supplied to the command then no permissions
	 * is set.
	 * 
	 * @param sftpChannel
	 *            FTP channel.
	 * 
	 * @throws SftpException
	 *             if operation fails.
	 */
	void setGroupOwnership(ChannelSftp sftpChannel) throws SftpException {
		if (skipSettingGroupOwnership())
			return;

		sftpChannel.chown(groupOwnership.intValue(), remoteFile);

		Object[] args = { groupOwnership.toString() };
		String message = messageProvider.getMessage("scct.securecopy_chgrp_info", args);
		executionResult.addMessage("Group ownership (CHGRP)", message);
	}

	/**
	 * Return true is file permissions shouldn't be set.
	 * 
	 * @return true is file permissions shouldn't be set.
	 */
	boolean skipSettingFilePermissions() {
		return (filePermissions.intValue() == DISABLE_CHMOD);
	}

	/**
	 * Return true is user ownership shouldn't be set.
	 * 
	 * @return true is is user ownership shouldn't be set.
	 */
	boolean skipSettingUserOwnership() {
		return (userOwnership.intValue() == DISABLE_CHMOD);
	}

	/**
	 * Return true is group ownership shouldn't be set.
	 * 
	 * @return true is is group ownership shouldn't be set.
	 */
	boolean skipSettingGroupOwnership() {
		return (groupOwnership.intValue() == DISABLE_CHMOD);
	}

	/**
	 * Do variable substitution if enabled.
	 * 
	 * @param source
	 *            source file whose content should be processed.
	 * 
	 * @return if variable substitution is enabled then the path to the processed
	 *         file is returned. Otherwise the path to the original source file is
	 *         returned.
	 * 
	 * @throws VariableSubstitutionException
	 *             if processing fails.
	 */
	File doVariableSubstitution(File source) throws VariableSubstitutionException {
		// return source;
		if (!this.substituteVariables)
			return source;
		return coreVariableSubstitutionProvider.createSubstitutedFile(source, session, executionResult);
	}

}
