/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.web.spring.rest;

import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_CREATE_CREDENTIAL_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_CREATE_ENVIRONMENT_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_CREATE_PROPERTY_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_CREATE_RESOURCE_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_DELETE_CREDENTIAL_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_DELETE_ENVIRONMENT_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_DELETE_PROPERTY_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_DELETE_RESOURCE_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_GET_CREDENTIALS_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_GET_CREDENTIAL_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_GET_RESOURCES_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_REFRESH_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_UPDATE_CREDENTIAL_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_UPDATE_ENVIRONMENT_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_UPDATE_RESOURCE_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_URI;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.alpha.pineapple.CoreException;
import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.admin.Administration;
import com.alpha.pineapple.credential.CredentialAlreadyExitsException;
import com.alpha.pineapple.credential.CredentialInfo;
import com.alpha.pineapple.credential.CredentialNotFoundException;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Credentials;
import com.alpha.pineapple.model.configuration.Environment;
import com.alpha.pineapple.model.configuration.Environments;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.model.configuration.Resources;
import com.alpha.pineapple.plugin.PluginInitializationFailedException;
import com.alpha.pineapple.plugin.repository.PluginRepository;
import com.alpha.pineapple.resource.EnvironmentInfo;
import com.alpha.pineapple.resource.EnvironmentNotFoundException;
import com.alpha.pineapple.resource.PropertyAlreadyExistsException;
import com.alpha.pineapple.resource.PropertyNotFoundException;
import com.alpha.pineapple.resource.ResourceAlreadyExistsException;
import com.alpha.pineapple.resource.ResourceInfo;
import com.alpha.pineapple.resource.ResourceNotFoundException;
import com.alpha.pineapple.resource.ResourceRepository;
import com.alpha.pineapple.web.WebAppCoreFactory;
import com.alpha.pineapple.web.model.RestResultMapper;

/**
 * Configuration REST web service controller.
 *
 */
@Controller
@RequestMapping(REST_CONFIGURATION_URI)
public class ConfigurationController {

    /**
     * Null credential ID reference.
     */
    private static final String NULL_CREDENTIAL_ID_REF = "";

    /**
     * First list index.
     */
    static final int FIRST_INDEX = 0;

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Pineapple core component.
     */
    @javax.annotation.Resource
    PineappleCore coreComponent;

    /**
     * Pineapple web application factory.
     */
    @javax.annotation.Resource
    WebAppCoreFactory webAppCoreFactory;

    /**
     * REST result mapper.
     */
    @javax.annotation.Resource
    RestResultMapper restResultMapper;

    /**
     * Message provider for I18N support.
     */
    @javax.annotation.Resource
    MessageProvider webMessageProvider;

    /**
     * Execution result factory.
     */
    @javax.annotation.Resource
    ExecutionResultFactory executionResultFactory;

    /**
     * Refresh core component configuration.
     * 
     * Will re-initialize the core component by creating a core component
     * instance from web application factory.
     * 
     * @throws CoreException
     *             if refresh fails.
     */
    @RequestMapping(value = REST_CONFIGURATION_REFRESH_PATH, method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void refresh() throws CoreException {
	// invoke factory to trigger call to core initialization method
	webAppCoreFactory.createCore();
    }

    /**
     * Create environment for resources and credentials.
     * 
     * @param environment
     *            environment ID.
     * @param description
     *            environment description.
     */
    @RequestMapping(value = REST_CONFIGURATION_CREATE_ENVIRONMENT_PATH, method = RequestMethod.POST, produces = {
	    MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void createEnvironment(@PathVariable String environment, @PathVariable String description) {
	// get resource repository
	Administration admin = coreComponent.getAdministration();
	ResourceRepository resourceRepository = admin.getResourceRepository();

	// create environment for resources
	if (!resourceRepository.containsEnvironment(environment)) {
	    resourceRepository.createEnvironment(environment, description);
	}

	// get credential provider
	CredentialProvider provider = admin.getCredentialProvider();

	// create environment for credentials
	if (!provider.containsEnvironment(environment)) {
	    provider.createEnvironment(environment, description);
	}

    }

    /**
     * Update environment for resources and credentials.
     * 
     * @param environment
     *            environment ID.
     * @param configuration
     *            model containing updated environment.
     * 
     * @return configuration which contains the updated environment.
     */
    @RequestMapping(value = REST_CONFIGURATION_UPDATE_ENVIRONMENT_PATH, method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void updateEnvironment(@PathVariable String environment, @RequestBody Configuration configuration) {
	// declare
	EnvironmentInfo environmentInfo = null;

	// get environments
	Environments environments = configuration.getEnvironments();
	if (environments == null) {
	    String message = webMessageProvider
		    .getMessage("cc.update_environment_invalid_model_environments_not_found_failure");
	    throw new EnvironmentNotFoundException(message);
	}

	// get environments
	List<Environment> environmentContainer = environments.getEnvironment();
	if (environmentContainer == null) {
	    String message = webMessageProvider
		    .getMessage("cc.update_environment_invalid_model_environments_not_found_failure");
	    throw new EnvironmentNotFoundException(message);
	}

	// exit if no environment is defined
	if (environmentContainer.isEmpty()) {
	    String message = webMessageProvider
		    .getMessage("cc.update_environment_invalid_model_environment_not_found_failure");
	    throw new EnvironmentNotFoundException(message);
	}

	// process first model
	Environment modelEnvironment = environmentContainer.get(FIRST_INDEX);

	// get resource repository
	Administration admin = coreComponent.getAdministration();
	ResourceRepository resourceRepository = admin.getResourceRepository();

	// exit if environment doesn't exist
	if (!resourceRepository.containsEnvironment(environment)) {
	    Object[] args = { modelEnvironment.getId() };
	    String message = webMessageProvider.getMessage("cc.update_environment_environment_not_found_failure", args);
	    throw new EnvironmentNotFoundException(message);
	}

	// get credential provider
	CredentialProvider provider = admin.getCredentialProvider();

	// exit if environment doesn't exist
	if (!provider.containsEnvironment(environment)) {
	    Object[] args = { modelEnvironment.getId() };
	    String message = webMessageProvider.getMessage("cc.update_environment_environment_not_found_failure", args);
	    throw new EnvironmentNotFoundException(message);
	}

	// update in resource repository
	environmentInfo = resourceRepository.getEnvironment(environment);
	environmentInfo = resourceRepository.updateEnvironment(environmentInfo, modelEnvironment.getId(),
		modelEnvironment.getDescription());

	// update in resource repository
	com.alpha.pineapple.credential.EnvironmentInfo environmentInfo2 = provider.getEnvironment(environment);
	environmentInfo2 = provider.updateEnvironment(environmentInfo2, modelEnvironment.getId(),
		modelEnvironment.getDescription());
    }

    /**
     * Delete environment for resources and credentials.
     * 
     * @param environment
     *            environment ID.
     */
    @RequestMapping(value = REST_CONFIGURATION_DELETE_ENVIRONMENT_PATH, method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteEnvironment(@PathVariable String environment) {
	// get resource repository
	Administration admin = coreComponent.getAdministration();
	ResourceRepository resourceRepository = admin.getResourceRepository();

	// delete environment for resources
	if (resourceRepository.containsEnvironment(environment)) {
	    resourceRepository.deleteEnvironment(environment);
	}

	// get credential provider
	CredentialProvider provider = admin.getCredentialProvider();

	// create environment for credentials
	if (provider.containsEnvironment(environment)) {
	    provider.deleteEnvironment(environment);
	}
    }

    /**
     * Create resource.
     * 
     * @param environment
     *            environment to create resource in.
     * @param resource
     *            resource ID.
     * @param pluginid
     *            plugin ID.
     * @param credentialidref
     *            credential ID reference
     * 
     * @return configuration which contains the created resource.
     * 
     * @throws PluginInitializationFailedException
     *             if plugin initialization fails.
     * @throws EnvironmentNotFoundException
     *             if creation fails. The exception is handled by the spring
     *             exception handler.
     */
    @RequestMapping(value = REST_CONFIGURATION_CREATE_RESOURCE_PATH, method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void createResource(@PathVariable String environment, @PathVariable String resource,
	    @PathVariable String pluginid, @PathVariable String credentialidref)
		    throws PluginInitializationFailedException, EnvironmentNotFoundException {
	// get resource repository
	Administration admin = coreComponent.getAdministration();
	ResourceRepository resourceRepository = admin.getResourceRepository();

	// handle null credential ID reference
	if (credentialidref == null)
	    credentialidref = NULL_CREDENTIAL_ID_REF;

	// create resource
	resourceRepository.create(environment, resource, pluginid, credentialidref);

	// initialize plugin
	ExecutionResult pluginResult = executionResultFactory.startExecution("Initialize plugin");
	PluginRepository pluginRepository = admin.getPluginRepository();
	pluginRepository.initializePlugin(pluginResult, pluginid);
    }

    /**
     * Update resource.
     * 
     * @param environment
     *            environment where resource is defined in.
     * @param resource
     *            resource which should be updated.
     * @param configuration
     *            model containing updated environment.
     */
    @RequestMapping(value = REST_CONFIGURATION_UPDATE_RESOURCE_PATH, method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void updateResource(@PathVariable String environment, @PathVariable String resource,
	    @RequestBody Configuration configuration) {

	// get environments
	Environments environments = configuration.getEnvironments();
	if (environments == null) {
	    String message = webMessageProvider
		    .getMessage("cc.update_resource_invalid_model_environments_not_found_failure");
	    throw new EnvironmentNotFoundException(message);
	}

	// get environments
	List<Environment> environmentContainer = environments.getEnvironment();
	if (environmentContainer == null) {
	    String message = webMessageProvider
		    .getMessage("cc.update_resource_invalid_model_environments_not_found_failure");
	    throw new EnvironmentNotFoundException(message);
	}

	// exit if no environment is defined
	if (environmentContainer.isEmpty()) {
	    String message = webMessageProvider
		    .getMessage("cc.update_resource_invalid_model_environment_not_found_failure");
	    throw new EnvironmentNotFoundException(message);
	}

	// process first model environment
	Environment modelEnvironment = environmentContainer.get(FIRST_INDEX);

	// get resource repository
	Administration admin = coreComponent.getAdministration();
	ResourceRepository resourceRepository = admin.getResourceRepository();

	// exit if environment doesn't exist
	if (!resourceRepository.containsEnvironment(environment)) {
	    Object[] args = { modelEnvironment.getId() };
	    String message = webMessageProvider.getMessage("cc.update_resource_environment_not_found_failure", args);
	    throw new EnvironmentNotFoundException(message);
	}

	// get resources
	Resources resources = modelEnvironment.getResources();
	if (resources == null) {
	    String message = webMessageProvider
		    .getMessage("cc.update_resource_invalid_model_resources_not_found_failure");
	    throw new ResourceNotFoundException(message);
	}

	// get resources
	List<Resource> resourceContainer = resources.getResource();
	if (resourceContainer == null) {
	    String message = webMessageProvider
		    .getMessage("cc.update_resource_invalid_model_resources_not_found_failure");
	    throw new ResourceNotFoundException(message);
	}

	// process first model resource
	Resource modelResource = resourceContainer.get(FIRST_INDEX);

	// exit if resource doesn't exist
	if (!resourceRepository.contains(environment, resource)) {
	    Object[] args = { resource, environment };
	    String message = webMessageProvider.getMessage("cc.update_resource_resource_not_found_failure", args);
	    throw new ResourceNotFoundException(message);
	}

	// get resource info
	ResourceInfo resourceInfo = resourceRepository.get(environment, resource);

	// update
	resourceInfo = resourceRepository.update(resourceInfo, environment, modelResource.getId(),
		modelResource.getPluginId(), modelResource.getCredentialIdRef());
    }

    /**
     * Delete resource.
     * 
     * @param environment
     *            environment ID.
     * @param resource
     *            resource ID.
     * 
     * @throws ResourceNotFoundException
     *             if creation fails. The exception is handled by the spring
     *             exception handler.
     * @throws EnvironmentNotFoundException
     *             if creation fails. The exception is handled by the spring
     *             exception handler.
     */
    @RequestMapping(value = REST_CONFIGURATION_DELETE_RESOURCE_PATH, method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteResource(@PathVariable String environment, @PathVariable String resource) {
	// get resource repository
	Administration admin = coreComponent.getAdministration();
	ResourceRepository resourceRepository = admin.getResourceRepository();

	// delete resource
	resourceRepository.delete(environment, resource);
    }

    /**
     * Create resource property.
     * 
     * @param environment
     *            target environment.
     * @param resource
     *            target resource.
     * @param key
     *            property key.
     * @param value
     *            property value.
     * 
     * @throws EnvironmentNotFoundException
     *             if creation fails. The exception is handled by the spring
     *             exception handler.
     * @throws ResourceNotFoundException
     *             if creation fails. The exception is handled by the spring
     *             exception handler.
     * @throws ResourceAlreadyExistsException
     *             if creation fails. The exception is handled by the spring
     *             exception handler.
     */
    @RequestMapping(value = REST_CONFIGURATION_CREATE_PROPERTY_PATH, method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void createProperty(@PathVariable String environment, @PathVariable String resource,
	    @PathVariable String key, @PathVariable String value) {
	// get resource repository
	Administration admin = coreComponent.getAdministration();
	ResourceRepository resourceRepository = admin.getResourceRepository();

	// create property
	resourceRepository.createResourceProperty(environment, resource, key, value);
    }

    /**
     * Delete resource property.
     * 
     * @param environment
     *            target environment.
     * @param resource
     *            target resource.
     * @param key
     *            property key.
     * 
     * @throws EnvironmentNotFoundException
     *             if creation fails. The exception is handled by the spring
     *             exception handler.
     * @throws ResourceNotFoundException
     *             if creation fails. The exception is handled by the spring
     *             exception handler.
     * @throws PropertyNotFoundException
     *             if creation fails. The exception is handled by the spring
     *             exception handler.
     */
    @RequestMapping(value = REST_CONFIGURATION_DELETE_PROPERTY_PATH, method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteProperty(@PathVariable String environment, @PathVariable String resource,
	    @PathVariable String key) {
	// get resource repository
	Administration admin = coreComponent.getAdministration();
	ResourceRepository resourceRepository = admin.getResourceRepository();

	// create property
	resourceRepository.deleteResourceProperty(environment, resource, key);
    }

    /**
     * Get credential in target environment.
     * 
     * @param environment
     *            environment where credential is defined.
     * @param credential
     *            credential ID.
     * 
     * @return credential from target environment.
     * 
     * @throws Exception
     *             if getting credential fails.
     */
    @RequestMapping(value = REST_CONFIGURATION_GET_CREDENTIAL_PATH, method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Configuration getCredential(@PathVariable String environment, @PathVariable String credential)
	    throws Exception {
	// get credential provider and then credential
	Administration admin = coreComponent.getAdministration();
	CredentialProvider provider = admin.getCredentialProvider();
	Credential modeCredential = provider.get(environment, credential);

	// map to model
	return restResultMapper.mapCredential(environment, modeCredential);
    }

    /**
     * Create credential in target environment.
     * 
     * @param environment
     *            environment to create credential in.
     * @param credential
     *            credential ID.
     * @param user
     *            user name.
     * @param password
     *            password.
     * 
     * @throws CredentialAlreadyExitsException
     *             if creation fails. The exception is handled by the spring
     *             exception handler.
     * @throws com.alpha.pineapple.credential.EnvironmentNotFoundException
     *             if creation fails. The exception is handled by the spring
     *             exception handler.
     */
    @RequestMapping(value = REST_CONFIGURATION_CREATE_CREDENTIAL_PATH, method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void createCredential(@PathVariable String environment, @PathVariable String credential,
	    @PathVariable String user, @PathVariable String password)
		    throws com.alpha.pineapple.credential.EnvironmentNotFoundException,
		    CredentialAlreadyExitsException {
	// get credential provider and create credential
	Administration admin = coreComponent.getAdministration();
	CredentialProvider provider = admin.getCredentialProvider();
	provider.create(environment, credential, user, password);
    }

    /**
     * Update credential.
     * 
     * @param environment
     *            target environment where credential is defined in.
     * @param credential
     *            ID of credential which is updated.
     * @param configuration
     *            model containing updated credential.
     * 
     * @throws CredentialNotFoundException
     *             if credential isn't found in target environment.
     */
    @RequestMapping(value = REST_CONFIGURATION_UPDATE_CREDENTIAL_PATH, method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void updateCredential(@PathVariable String environment, @PathVariable String credential,
	    @RequestBody Configuration configuration) throws CredentialNotFoundException {

	// get environments
	Environments environments = configuration.getEnvironments();
	if (environments == null) {
	    String message = webMessageProvider
		    .getMessage("cc.update_credential_invalid_model_environments_not_found_failure");
	    throw new EnvironmentNotFoundException(message);
	}

	// get environments
	List<Environment> environmentContainer = environments.getEnvironment();
	if (environmentContainer == null) {
	    String message = webMessageProvider
		    .getMessage("cc.update_credential_invalid_model_environments_not_found_failure");
	    throw new EnvironmentNotFoundException(message);
	}

	// exit if no environment is defined
	if (environmentContainer.isEmpty()) {
	    String message = webMessageProvider
		    .getMessage("cc.update_credential_invalid_model_environment_not_found_failure");
	    throw new EnvironmentNotFoundException(message);
	}

	// process first model environment
	Environment modelEnvironment = environmentContainer.get(FIRST_INDEX);

	// get credential provider
	Administration admin = coreComponent.getAdministration();
	CredentialProvider provider = admin.getCredentialProvider();

	// exit if environment doesn't exist
	if (!provider.containsEnvironment(environment)) {
	    Object[] args = { modelEnvironment.getId() };
	    String message = webMessageProvider.getMessage("cc.update_credential_environment_not_found_failure", args);
	    throw new EnvironmentNotFoundException(message);
	}

	// get credentials
	Credentials credentials = modelEnvironment.getCredentials();
	if (credentials == null) {
	    String message = webMessageProvider
		    .getMessage("cc.update_credential_invalid_model_credentials_not_found_failure");
	    throw new CredentialNotFoundException(message);
	}

	// get credentials
	List<Credential> credentialContainer = credentials.getCredential();
	if (credentialContainer == null) {
	    String message = webMessageProvider
		    .getMessage("cc.update_credential_invalid_model_credentials_not_found_failure");
	    throw new CredentialNotFoundException(message);
	}

	// process first model credential
	Credential modelCredential = credentialContainer.get(FIRST_INDEX);

	// exit if credential doesn't exist
	if (!provider.contains(environment, credential)) {
	    Object[] args = { credential, environment };
	    String message = webMessageProvider.getMessage("cc.update_credential_credential_not_found_failure", args);
	    throw new CredentialNotFoundException(message);
	}

	// get credential info
	com.alpha.pineapple.credential.EnvironmentInfo environmentInfo = provider.getEnvironment(environment);
	CredentialInfo credentialInfo = environmentInfo.getCredential(credential);

	// update
	credentialInfo = provider.update(credentialInfo, environment, modelCredential.getId(),
		modelCredential.getUser(), modelCredential.getPassword());
    }

    /**
     * Delete credential.
     * 
     * @param environment
     *            environment ID.
     * @param credential
     *            credential ID.
     * 
     * @throws CredentialNotFoundException
     *             if deletion fails. The exception is handled by the spring
     *             exception handler.
     */
    @RequestMapping(value = REST_CONFIGURATION_DELETE_CREDENTIAL_PATH, method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteCredential(@PathVariable String environment, @PathVariable String credential)
	    throws CredentialNotFoundException {
	// get credential provider
	Administration admin = coreComponent.getAdministration();
	CredentialProvider provider = admin.getCredentialProvider();

	// delete credential
	provider.delete(environment, credential);
    }

    /**
     * Get credential configuration.
     * 
     * @return credential configuration.
     */
    @RequestMapping(value = REST_CONFIGURATION_GET_CREDENTIALS_PATH, method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Configuration getCredentialConfiguration() {

	// get credential provider and create credential
	Administration admin = coreComponent.getAdministration();
	CredentialProvider provider = admin.getCredentialProvider();

	// get credential configuration
	com.alpha.pineapple.credential.EnvironmentInfo[] environmentInfos = provider.getEnvironments();

	// map to model
	return restResultMapper.mapCredentialConfiguration(environmentInfos);
    }

    /**
     * Get resource configuration.
     * 
     * @return resource configuration.
     */
    @RequestMapping(value = REST_CONFIGURATION_GET_RESOURCES_PATH, method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Configuration getResourceConfiguration() {

	// get resource repository
	Administration admin = coreComponent.getAdministration();
	ResourceRepository resourceRepository = admin.getResourceRepository();

	// get resources configuration
	EnvironmentInfo[] environmentInfos = resourceRepository.getEnvironments();

	// map to model
	return restResultMapper.mapResourceConfiguration(environmentInfos);
    }

    /**
     * Exception handler for handling core component refresh failure.
     * 
     * @param e
     *            core exception.
     * @param response
     *            HTTP response.
     * 
     * @return HTTP status code and error message.
     */
    @ExceptionHandler(CoreException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(CoreException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling credential creation failure.
     * 
     * @param e
     *            environment not found exception.
     * @param response
     *            HTTP response.
     * 
     * @return HTTP status code and error message.
     */
    @ExceptionHandler(com.alpha.pineapple.credential.EnvironmentNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(com.alpha.pineapple.credential.EnvironmentNotFoundException e,
	    HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling credential creation failure.
     * 
     * @param e
     *            credential already exists exception.
     * @param response
     *            HTTP response.
     * 
     * @return HTTP status code and error message.
     */
    @ExceptionHandler(CredentialAlreadyExitsException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(CredentialAlreadyExitsException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling credential deletion failure.
     * 
     * @param e
     *            credential not found exception.
     * @param response
     *            HTTP response.
     * 
     * @return HTTP status code and error message.
     */
    @ExceptionHandler(CredentialNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(CredentialNotFoundException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling resource creation failure.
     * 
     * @param e
     *            environment not found exception.
     * @param response
     *            HTTP response.
     * 
     * @return HTTP status code and error message.
     */
    @ExceptionHandler(EnvironmentNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(EnvironmentNotFoundException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling resource creation failure.
     * 
     * @param e
     *            resource already exists exception.
     * @param response
     *            HTTP response.
     * 
     * @return HTTP status code and error message.
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(ResourceAlreadyExistsException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling resource deletion failure.
     * 
     * @param e
     *            resource not found exception.
     * @param response
     *            HTTP response.
     * 
     * @return HTTP status code and error message.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(ResourceNotFoundException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling resource property creation failure.
     * 
     * @param e
     *            property already exists exception.
     * @param response
     *            HTTP response.
     * 
     * @return HTTP status code and error message.
     */
    @ExceptionHandler(PropertyAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(PropertyAlreadyExistsException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling resource property deletion failure.
     * 
     * @param e
     *            property not found exception.
     * @param response
     *            HTTP response.
     * 
     * @return HTTP status code and error message.
     */
    @ExceptionHandler(PropertyNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(PropertyNotFoundException e, HttpServletResponse response) {
	return e.getMessage();
    }

}
