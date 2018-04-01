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

package com.alpha.pineapple.plugin.activation;

import org.springframework.oxm.Unmarshaller;

import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.repository.PluginRuntimeRepository;
import com.alpha.pineapple.resource.ResourceRepository;
import com.alpha.pineapple.session.Session;

/**
 * Plugin activator which provides access to external resources through sessions
 * and operations.
 */
public interface PluginActivator {
	/**
	 * intialize plugin activator.
	 * 
	 * @param provider
	 *            Credential provider which provides access to resource credentials.
	 * @param resourceRepository
	 *            Resource repository which provides access to resources.
	 * @param pluginRepository
	 *            Plugin repository which provides access to registered plugins.
	 */
	public void initialize(CredentialProvider provider, ResourceRepository resourceRepository,
			PluginRuntimeRepository pluginRepository);

	/**
	 * Resolved operation.
	 * 
	 * @param environment
	 *            Environment where resource is defined.
	 * @param resource
	 *            Resource id.
	 * @param operation
	 *            Operation id.
	 * @return operation resolved from environment, resource id and operation id.
	 */
	public Operation getOperation(String environment, String resource, String operation);

	/**
	 * Resolve unmarshaller.
	 * 
	 * @param environment
	 *            Environment where resource is defined.
	 * @param resource
	 *            Resource id.
	 * 
	 * @return unmarshaller resolved from environment and resource id.
	 * 
	 * @throws randomPluginId
	 *             If unmarshaller resolutiob fails.
	 */
	public Unmarshaller getUnmarshaller(String environment, String resource) throws PluginExecutionFailedException;

	/**
	 * Resolve session.
	 * 
	 * @param environment
	 *            Environment where resource is defined.
	 * @param resource
	 *            Resource id.
	 * 
	 * @return session resolved from environment and resource id.
	 */
	public Session getSession(String environment, String resource);

	/**
	 * Returns true if input marshalling is enabled for resource.
	 * 
	 * @param environment
	 *            Environment where resource is defined.
	 * @param resource
	 *            Resource id.
	 * 
	 * @return true if input marshalling is enabled for resource.
	 */
	public boolean isInputMarshallingEnabled(String environment, String resource);

}
