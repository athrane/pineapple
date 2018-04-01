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

import static com.alpha.testutils.SshTestConstants.TEST_TIMEOUT;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.ssh.session.SshSession;
import com.alpha.pineapple.plugin.ssh.session.SshSessionImpl;
import com.alpha.pineapple.plugin.ssh.utils.JSchLog4JLogger;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.jcraft.jsch.JSch;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for
 * unit testing SSH sessions.
 * 
 * Please notice that this class mirrors the dependencies of the
 * {@linkplain SshSessionImpl} class since it inject all dependencies into the
 * session class.
 */
public class ObjectMotherSshSession {

	/**
	 * Spring configuration file for the SSH plugin.
	 */
	static final String SSH_SPRING_CONFIG = "/com.alpha.pineapple.plugin.ssh-config.xml";

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
	 * Resource property getter.
	 */
	@Resource
	ResourcePropertyGetter propertyGetter;

	/**
	 * JSCH log4j logger.
	 */
	@Resource
	JSchLog4JLogger jschLogger;

	/**
	 * SSH library.
	 */
	@Resource
	JSch jsch;

	/**
	 * Inject Spring dependencies in to session.
	 * 
	 * @param session
	 *            session to inject dependencies into.
	 */
	void injectSpringDependencies(SshSession session) {
		ReflectionTestUtils.setField(session, "messageProvider", messageProvider);
		ReflectionTestUtils.setField(session, "propertyGetter", propertyGetter);
		ReflectionTestUtils.setField(session, "jschLogger", jschLogger);
		ReflectionTestUtils.setField(session, "jsch", jsch);
	}

	/**
	 * Create connected SSH session using password authentication.
	 * 
	 * @param host
	 *            SSH host used to establish connection.
	 * @param port
	 *            TCP port where connection is established.
	 * @param user
	 *            user name.
	 * @param password
	 *            password.
	 * 
	 * @return connected SSH session.
	 */
	public SshSession createConnectedSshSessionWithPasswordAuthentication(String host, int port, String user,
			String password) {

		// declare session
		SshSessionImpl session;

		try {
			// create session
			session = new SshSessionImpl();
			injectSpringDependencies(session);
			session.setLogger();

			// connect
			session.connect(host, port, user, password, TEST_TIMEOUT);
			return session;
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
			return null;
		}
	}

	/**
	 * Create unconnected SSH session.
	 * 
	 * @return unconnected SSH session.
	 */
	public SshSession createUnconnectedSshSession() {

		// declare session
		SshSession session;

		try {
			// create session
			session = new SshSessionImpl();
			injectSpringDependencies(session);

			return session;
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
			return null;
		}
	}

}
