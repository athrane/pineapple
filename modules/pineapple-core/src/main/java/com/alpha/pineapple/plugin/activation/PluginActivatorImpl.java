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

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.oxm.Unmarshaller;

import com.alpha.pineapple.credential.CredentialNotFoundException;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.repository.PluginInfo;
import com.alpha.pineapple.plugin.repository.PluginRuntimeRepository;
import com.alpha.pineapple.plugin.session.SessionHandlerFactory;
import com.alpha.pineapple.resource.ResourceInfo;
import com.alpha.pineapple.resource.ResourceRepository;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionException;

/**
 * Implementation of the {@link PluginActivator} interface.
 */
public class PluginActivatorImpl implements PluginActivator {
    /**
     * Null credential id.
     */
    Credential NULL_CREDENTIAL = null;

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Message provider for I18N support.
     */
    @javax.annotation.Resource
    MessageProvider messageProvider;

    /**
     * Session handler factory.
     */
    @javax.annotation.Resource
    SessionHandlerFactory retrySessionHandlerFactory;

    /**
     * Credential provider.
     */
    CredentialProvider provider;

    /**
     * resource repository.
     */
    ResourceRepository resourceRepository;

    /**
     * Plugin repository.
     */
    PluginRuntimeRepository pluginRepository;

    /**
     * PluginActivatorImpl no-arg constructor.
     */
    public PluginActivatorImpl() {
    }

    /**
     * Resolved resource from environment and id.
     * 
     * @param environment
     *            Environment where resource should be resolved from.
     * @param id
     *            Id of resource.
     * 
     * @return resource resolved from environment and id.
     * 
     * @throws SessionException
     *             If resolution of resource fails.
     */
    Resource getResource(String environment, String id) throws SessionException {
	// get resource info
	ResourceInfo info = resourceRepository.get(environment, id);

	// get resource
	Resource resourceObject = info.getResource();

	// log debug message
	if (logger.isDebugEnabled()) {
	    Object[] args = { environment, id, resourceObject };
	    String message = messageProvider.getMessage("pa.get_resource_info", args);
	    logger.debug(message);
	}

	return resourceObject;
    }

    /**
     * Resolve plugin id from environment and resource id.
     * 
     * @param environment
     *            Environment.
     * @param id
     *            Resource id.
     * 
     * @return Plugin id for resolved resource in environment.
     * 
     * @throws SessionException
     *             If resolution of plugin id fails.
     */
    String resolvePluginId(String environment, String id) throws SessionException {
	// log debug message
	if (logger.isDebugEnabled()) {
	    Object[] args = { id, environment };
	    String message = messageProvider.getMessage("pa.resolve_pluginid_info", args);
	    logger.debug(message);
	}

	// get resource info
	ResourceInfo resourceInfo = resourceRepository.get(environment, id);

	// get plugin id
	String pluginId = resourceInfo.getPluginId();

	if (logger.isDebugEnabled()) {
	    Object[] args = { pluginId };
	    String message = messageProvider.getMessage("pa.resolve_pluginid_completed", args);
	    logger.debug(message);
	}

	return pluginId;
    }

    /**
     * Get credential from credential provider.
     * 
     * @param environment
     *            The platform environment in which the resource is defined.
     * @param resource
     *            The resource for which the credential should be obtained.
     * 
     * @return Credential from credential provider for current resource. If the
     *         credential reference in the resource is null or empty then a null
     *         resource is returned.
     * 
     * @throws SessionException
     *             If retrieval of credential fails.
     * @throws IllegalArgumentException
     *             If credential identifier in resource is null.
     */
    Credential getCredential(String environment, Resource resource) throws SessionException {
	try {
	    // get credential id
	    String credentialId = resource.getCredentialIdRef();

	    // if credential id is null or empty then return null credential
	    if (credentialId == null)
		return NULL_CREDENTIAL;
	    if (credentialId.isEmpty())
		return NULL_CREDENTIAL;

	    // get credential
	    Credential credential = provider.get(environment, credentialId);

	    // log debug message
	    if (logger.isDebugEnabled()) {
		Object[] args = { credential, environment, resource };
		String message = messageProvider.getMessage("pa.get_credential_info", args);
		logger.debug(message);
	    }

	    return credential;

	} catch (CredentialNotFoundException e) {
	    // get credential id
	    String credentialId = resource.getCredentialIdRef();

	    // create error message
	    Object[] args = { credentialId, environment };
	    String message = messageProvider.getMessage("pa.get_credential_failed", args);
	    throw new SessionException(message, e);
	}
    }

    public void initialize(CredentialProvider provider, ResourceRepository resourceRepository,
	    PluginRuntimeRepository pluginRepository) {

	// validate parameters
	Validate.notNull(provider, "provider is undefined.");
	Validate.notNull(resourceRepository, "resourceRepository is undefined.");
	Validate.notNull(pluginRepository, "pluginRepository is undefined.");

	// set parameters
	this.provider = provider;
	this.resourceRepository = resourceRepository;
	this.pluginRepository = pluginRepository;
    }

    public Operation getOperation(String environment, String resource, String operation) {
	try {
	    String pluginId = resolvePluginId(environment, resource);

	    // log debug message
	    if (logger.isDebugEnabled()) {
		Object[] args = { pluginId };
		String message = messageProvider.getMessage("pa.get_operation_info", args);
		logger.debug(message);
	    }

	    // get operation object
	    Operation operationObject = pluginRepository.getOperation(pluginId, operation);

	    // log debug message
	    if (logger.isDebugEnabled()) {
		Object[] args = { operationObject };
		String message = messageProvider.getMessage("pa.get_operation_completed", args);
		logger.debug(message);
	    }

	    // get plugin info
	    PluginInfo pluginInfo = pluginRepository.getPluginInfo(pluginId);

	    // if session handling isn't enabled then exit
	    if (!pluginInfo.isSessionHandlingEnabled())
		return operationObject;

	    // if session handling is enabled the set it up
	    // get resource
	    Resource resourceObject = getResource(environment, resource);

	    // get credential (can be null)
	    Credential credential = getCredential(environment, resourceObject);

	    // create session handler
	    Operation handler = retrySessionHandlerFactory.getInstance(resourceObject, credential, operationObject);

	    // log debug message
	    if (logger.isDebugEnabled()) {
		Object[] args = { handler };
		String message = messageProvider.getMessage("pa.get_operation_decorated", args);
		logger.debug(message);
	    }

	    return handler;

	} catch (SessionException e) {
	    throw new RuntimeException(e);
	}
    }

    public Unmarshaller getUnmarshaller(String environment, String resource) throws PluginExecutionFailedException {
	// validate parameters
	Validate.notNull(environment, "environment is undefined.");
	Validate.notNull(resource, "resource is undefined.");

	try {
	    String pluginId = resolvePluginId(environment, resource);

	    // log debug message
	    if (logger.isDebugEnabled()) {
		Object[] args = { pluginId };
		String message = messageProvider.getMessage("pa.get_unmarshaller_info", args);
		logger.debug(message);
	    }

	    return pluginRepository.getPluginUnmarshaller(pluginId);
	} catch (SessionException e) {
	    throw new RuntimeException(e);
	}
    }

    public Session getSession(String environment, String resource) {
	// validate parameters
	Validate.notNull(environment, "environment is undefined.");
	Validate.notNull(resource, "resource is undefined.");

	try {
	    // log debug message
	    if (logger.isDebugEnabled()) {
		Object[] args = { environment, resource };
		String message = messageProvider.getMessage("pa.get_session_info", args);
		logger.debug(message);
	    }

	    // get plugin id
	    String pluginId = resolvePluginId(environment, resource);

	    // get session
	    Session session = pluginRepository.getSession(pluginId);

	    // log debug message
	    if (logger.isDebugEnabled()) {
		Object[] args = { pluginId };
		String message = messageProvider.getMessage("pa.get_session_completed", args);
		logger.debug(message);
	    }

	    return session;
	} catch (SessionException e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public boolean isInputMarshallingEnabled(String environment, String resource) {
	// validate parameters
	Validate.notNull(environment, "environment is undefined.");
	Validate.notNull(resource, "resource is undefined.");

	try {
	    String pluginId = resolvePluginId(environment, resource);
	    PluginInfo info = pluginRepository.getPluginInfo(pluginId);
	    return info.isInputMarshallingEnabled();
	} catch (SessionException e) {
	    throw new RuntimeException(e);
	}
    }

}
