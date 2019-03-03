/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2019 Allan Thrane Andersen.
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

package com.alpha.pineapple.plugin.git.session;

import static com.alpha.javautils.ArgumentUtils.notNull;

import java.io.File;

import javax.annotation.Resource;

import org.eclipse.jgit.api.Git;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.plugin.PluginSession;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;

/**
 * Implementation of the {@link GitSession} interface.
 */
@PluginSession
public class GitSessionImpl implements GitSession {

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Resource credential.
	 */
	Credential credential;

	/**
	 * Git resource.
	 */
	com.alpha.pineapple.model.configuration.Resource resource;

	/**
	 * GitPluginSessionImpl no-arg constructor.
	 * 
	 * @throws Exception If session creation fails.
	 */
	public GitSessionImpl() throws Exception {
		super();
	}

	/**
	 * GitPluginSessionImpl.
	 * 
	 * @param messageProvider I18N message provider.
	 * 
	 * @throws Exception If session creation fails.
	 */
	public GitSessionImpl(MessageProvider messageProvider) throws Exception {
		super();
		this.messageProvider = messageProvider;
	}

	@Override
	public void connect(com.alpha.pineapple.model.configuration.Resource resource, Credential credential)
			throws SessionConnectException {

		notNull(resource, "resource is undefined.");
		notNull(credential, "credential is undefined.");

		// store resource and credential
		this.credential = credential;
		this.resource = resource;

		try {

			// NO-OP at this point

		} catch (Exception e) {

			Object[] args = { resource.getId(), e };
			String message = messageProvider.getMessage("gs.connect_failure", args);
			throw new SessionConnectException(message, e);
		}

	}

	@Override
	public void disconnect() throws SessionDisconnectException {
		// NO-OP at this point
	}

	@Override
	public com.alpha.pineapple.model.configuration.Resource getResource() {
		return this.resource;
	}

	@Override
	public Credential getCredential() {
		return this.credential;
	}

	@Override
	public void cloneRepository(String uri, String branch, File dest) throws Exception {
		Git.cloneRepository().setURI(uri).setDirectory(dest).call();
	}

}
