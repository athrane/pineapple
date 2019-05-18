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
import static com.alpha.pineapple.plugin.git.GitConstants.GIT_REPO_SUFFIX;
import static com.alpha.pineapple.plugin.git.GitConstants.RESOURCE_PROPERTY_URI;

import java.io.File;
import java.util.Collection;

import javax.annotation.Resource;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.plugin.PluginSession;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;
import com.alpha.pineapple.session.SessionException;

/**
 * Implementation of the {@link GitSession} interface.
 */
@PluginSession
public class GitSessionImpl implements GitSession {

	/**
	 * No check out when cloning a repository.
	 */
	boolean NO_CHECK_OUT = true;

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "gitMessageProvider")
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
	 * Repository URI.
	 */
	String uri;

	/**
	 * Git instance.
	 */
	Git git;

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

			// get resource attributes
			var getter = new ResourcePropertyGetter(resource);
			uri = getter.getProperty(RESOURCE_PROPERTY_URI);

			// ls-remote to verify repository can be accessed, result isn't used
			lsRemote();

		} catch (Exception e) {
			var message = messageProvider.get("gs.connect_failure", resource.getId(), e);
			throw new SessionConnectException(message, e);
		}

	}

	@Override
	public void disconnect() throws SessionDisconnectException {
		if (git == null)
			return;

		// close repository
		var repository = git.getRepository();
		if (repository != null)
			repository.close();
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
	public String getProjectName() throws Exception {
		if (uri == null) {
			throw new SessionException(messageProvider.get("gs.get_reponame_uri_failure", resource.getId()));
		}
		if (uri.isBlank()) {
			throw new SessionException(messageProvider.get("gs.get_reponame_uri_blank_failure", resource.getId()));
		}
		if (!uri.endsWith(GIT_REPO_SUFFIX)) {
			throw new SessionException(messageProvider.get("gs.get_reponame_uri_postfix_failure", resource.getId()));
		}

		var lastIndex = uri.lastIndexOf("/");
		var repo = uri.substring(lastIndex);
		return repo.substring(0, repo.length() - GIT_REPO_SUFFIX.length());
	}

	@Override
	public void cloneRepository(String branch, File dest) throws Exception {

		// create command
		var command = Git.cloneRepository().setURI(this.uri);

		// add provider if auth is used
		var user = credential.getUser();
		var password = credential.getPassword();
		if (useAuthentification(user, password)) {
			var provider = new UsernamePasswordCredentialsProvider(user, password);
			command.setCredentialsProvider(provider);
		}

		// clone, returns Git object with reference to repository
		command.setBranch(branch);
		command.setDirectory(dest);
		git = command.call();
	}

	@Override
	public Collection<Ref> lsRemote() throws Exception {

		// create command
		var command = Git.lsRemoteRepository();

		// add provider if auth is used
		var user = credential.getUser();
		var password = credential.getPassword();
		if (useAuthentification(user, password)) {
			var provider = new UsernamePasswordCredentialsProvider(user, password);
			command.setCredentialsProvider(provider);
		}

		// execute command, returns Git object with reference to repository
		command.setRemote(this.uri);
		return command.call();
	}

	/**
	 * Returns true if authentication should be used.
	 * 
	 * @param user     user name.
	 * @param password password.
	 * 
	 * @return true if authentication should be used. If user abd password is
	 *         defined (i.e. not blank) then true is returned.
	 */
	boolean useAuthentification(String user, String password) {
		if (user.isBlank())
			return false;
		if (password.isBlank())
			return false;
		return true;
	}

}
