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

package com.alpha.pineapple.plugin.filesystem.session;
import static com.alpha.javautils.ArgumentUtils.notNull;

import javax.annotation.Resource;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.plugin.PluginSession;
import com.alpha.pineapple.plugin.filesystem.model.Mapper;
import com.alpha.pineapple.plugin.filesystem.session.uri.VfsUriGenerator;
import com.alpha.pineapple.plugin.filesystem.session.uri.VfsUriGeneratorFactory;
import com.alpha.pineapple.resource.ResourceException;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;
import com.alpha.pineapple.session.SessionException;

/**
 * Implementation of the <code>FilesystemSession</code> interface.
 */
@PluginSession
public class FilesystemSessionImpl implements FileSystemSession {

	/**
	 * First array index.
	 */
	final int FIRST_INDEX = 0;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * File system resource.
	 */
	com.alpha.pineapple.model.configuration.Resource resource;

	/**
	 * Resource credential.
	 */
	Credential credential;

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

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Resource property getter.
	 */
	@Resource
	ResourcePropertyGetter resourcePropertyGetter;

	/**
	 * VFS URI generator factory.
	 */
	@Resource
	VfsUriGeneratorFactory vfsUriGeneratorFactory;

	/**
	 * VFS authenticator.
	 */
	StaticUserAuthenticator authenticator;

	/**
	 * VFS configuration builder.
	 */
	DefaultFileSystemConfigBuilder configbuilder;

	/**
	 * VFS manager.
	 */
	FileSystemManager vfsManager;

	/**
	 * VFS host.
	 */
	String host;

	/**
	 * VFS URI generator.
	 */
	VfsUriGenerator vfsUriGenerator;

	/**
	 * FilesystemSessionImpl no-arg constructor.
	 * 
	 * @throws Exception
	 *             If Session creation fails.
	 */
	public FilesystemSessionImpl() throws Exception {
		super();
	}

	public void connect(com.alpha.pineapple.model.configuration.Resource resource, Credential credential)
			throws SessionConnectException {
		notNull(resource, "resource is undefined.");
		notNull(credential, "credential is undefined.");

		try {

			// store in fields
			this.credential = credential;
			this.resource = resource;

			// initialize property getter
			resourcePropertyGetter.setResource(resource);

			// get resource attributes
			host = resourcePropertyGetter.getProperty("host");
			String protocol = resourcePropertyGetter.getProperty("protocol");
			String port = getPort();

			// create VFS manager
			vfsManager = VFS.getManager();

			// log debug message
			if (logger.isDebugEnabled()) {
				Object[] args = { ReflectionToStringBuilder.toString(vfsManager.getSchemes()) };
				logger.debug(messageProvider.getMessage("fssi.connect_init_vfs_info", args));
			}

			// create authenticator
			authenticator = new StaticUserAuthenticator(credential.getUser(), credential.getPassword(), null);

			// create file system configuration
			FileSystemOptions options = new FileSystemOptions();
			configbuilder = DefaultFileSystemConfigBuilder.getInstance();
			configbuilder.setUserAuthenticator(options, authenticator);

			// create VFS URI generator
			vfsUriGenerator = vfsUriGeneratorFactory.createGenerator(protocol, host, port, credential.getUser(),
					credential.getPassword());

			// log debug message
			if (logger.isDebugEnabled()) {
				Object[] args = { resource, host, protocol, credential.getId() };
				String message = messageProvider.getMessage("fssi.connect", args);
				logger.debug(message);
			}
		} catch (ResourceException e) {
			Object[] args = { e.toString() };
			String message = messageProvider.getMessage("fssi.connect_resource_properties_failed", args);
			throw new SessionConnectException(message, e);

		} catch (FileSystemException e) {
			Object[] args = { e.toString() };
			String message = messageProvider.getMessage("fssi.connect_vfs_init_failed", args);
			throw new SessionConnectException(message, e);
		}
	}

	/**
	 * 
	 * @return
	 */
	String getPort() {
		try {
			return resourcePropertyGetter.getProperty("port");
		} catch (ResourceException e) {
			return null;
		}
	}

	public void disconnect() throws SessionDisconnectException {
		// clear VFS objects
		authenticator = null;
		configbuilder = null;

		// clear the manager
		vfsManager.getFilesCache().close();
		vfsManager = null;

		// log debug message
		if (logger.isDebugEnabled()) {
			String message = messageProvider.getMessage("fssi.disconnect");
			logger.debug(message);
		}
	}

	public com.alpha.pineapple.model.configuration.Resource getResource() {
		return this.resource;
	}

	public Credential getCredential() {
		return this.credential;
	}

	public FileObject resolveFile(String path) throws SessionException {

		// declare uri
		String uri = null;

		try {

			// create URI
			uri = vfsUriGenerator.createUri(path);

			// log debug message
			if (logger.isDebugEnabled()) {
				Object[] args = { uri };
				String message = messageProvider.getMessage("fssi.resolvefile_start", args);
				logger.debug(message);
			}

			// resolve file
			FileObject vfsFile = vfsManager.resolveFile(uri);

			// refresh
			vfsFile.refresh();

			// log debug message
			if (logger.isDebugEnabled()) {
				Object[] args = { vfsFile };
				String message = messageProvider.getMessage("fssi.resolvefile_complete", args);
				logger.debug(message);
			}

			return vfsFile;

		} catch (FileSystemException e) {
			Object[] args = { uri, e.toString() };
			String message = messageProvider.getMessage("fssi.resolvefile_failed", args);
			throw new SessionException(message, e);
		}

	}

}
