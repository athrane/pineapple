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

package com.alpha.pineapple.plugin.ssh.session;

import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionException;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;

/**
 * Session which provides access to a SSH session.
 */
public interface SshSession extends Session {

	/**
	 * Connect to host using SSH and password authentication.
	 * 
	 * @param host
	 *            host to connect to.
	 * @param port
	 *            TCP port to connect to.
	 * @param user
	 *            user name.
	 * @param password
	 *            password.
	 * @param timeOut
	 *            connection time out in ms.
	 * 
	 * @throws SessionConnectException
	 *             if connecting fails.
	 */
	void connect(String host, int port, String user, String password, int timeOut) throws SessionConnectException;

	/**
	 * Connect to host using SSH and public key authentication.
	 * 
	 * @param host
	 *            host to connect to.
	 * @param port
	 *            TCP port to connect to.
	 * @param user
	 *            user name.
	 * @param passPhrase
	 *            private key pass phrase. Can be null.
	 * @param privateKeyFile
	 *            private key file. Absolute path to private key file.
	 * @param timeOut
	 *            connection time out in ms.
	 * 
	 * @throws SessionConnectException
	 *             if connecting fails.
	 */
	void connect(String host, int port, String user, String passPhrase, String privateKeyFile, int timeOut)
			throws SessionConnectException;

	/**
	 * Returns true session is connected to an host using SSH.
	 * 
	 * @return true session is connected to an host using SSH.
	 */
	public boolean isConnected();

	/**
	 * Get SFTP channel.
	 * 
	 * @return SFTP channel.
	 * 
	 * @throws SessionException
	 *             if SFTP channel creation fails.
	 */
	ChannelSftp getSftpChannel() throws SessionException;

	/**
	 * Get EXEC channel.
	 * 
	 * @return EXEC channel.
	 * 
	 * @throws SessionException
	 *             if EXEC channel creation fails.
	 */
	ChannelExec getExecuteChannel() throws SessionException;

}
