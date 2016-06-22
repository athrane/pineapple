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

package com.alpha.pineapple.resource;

import static com.alpha.pineapple.CoreConstants.WILDCARD_ENVIRONMENT_ID;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.ObjectFactory;

/**
 * Implementation of the {@link ResourceRepository} interface.
 */
public class ResourceRepositoryImpl implements ResourceRepository {
    /**
     * Null environment info's.
     */
    static final EnvironmentInfo[] NULL_ENVIRONMENT_INFOS = new EnvironmentInfo[] {};

    /**
     * First array index.
     */
    static final int FIRST_INDEX = 0;

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
     * Environment configuration marshaller.
     */
    @javax.annotation.Resource
    ResourceConfigurationMarshaller resourceConfigurationMarshaller;

    /**
     * JAXB object factory.
     */
    ObjectFactory factory = new ObjectFactory();

    /**
     * Configuration info.
     */
    ConfigurationInfo configurationInfo;

    /**
     * ResourceRepositoryImpl constructor.
     */
    public ResourceRepositoryImpl() {
    }

    public void initialize(Configuration envConfiguration) {
	configurationInfo = resourceConfigurationMarshaller.map(envConfiguration);

	// log debug message
	if (logger.isDebugEnabled()) {
	    Object[] args = { configurationInfo.getEnvironments().length };
	    String message = messageProvider.getMessage("rr.initialize_success", args);
	    logger.debug(message);
	}
    }

    @Override
    public boolean contains(String environment, String resource) {
	// validate parameters
	Validate.notNull(environment, "environment is undefined.");
	Validate.notEmpty(environment, "environment is empty.");
	Validate.notNull(resource, "resource is undefined.");
	Validate.notEmpty(resource, "resource is empty.");

	// attempt to resolve environment
	if (containsEnvironment(environment)) {

	    EnvironmentInfo environmentInfo = configurationInfo.getEnvironment(environment);
	    if (environmentInfo.containsResource(resource))
		return true;
	}

	// attempt to resolve using wild card environment
	if (containsEnvironment(WILDCARD_ENVIRONMENT_ID)) {
	    EnvironmentInfo environmentInfo = configurationInfo.getEnvironment(WILDCARD_ENVIRONMENT_ID);
	    if (environmentInfo.containsResource(resource))
		return true;
	}

	return false;
    }

    public ResourceInfo get(String environment, String id) throws ResourceNotFoundException {
	// validate parameters
	Validate.notNull(environment, "environment is undefined.");
	Validate.notEmpty(environment, "environment is empty.");
	Validate.notNull(id, "id is undefined.");
	Validate.notEmpty(id, "id is empty.");

	// attempt to resolve environment
	if (containsEnvironment(environment)) {
	    EnvironmentInfo environmentInfo = configurationInfo.getEnvironment(environment);

	    // get resource if it exists exists
	    if (environmentInfo.containsResource(id))
		return environmentInfo.getResource(id);
	}

	// attempt to resolve using wild card environment
	if (containsEnvironment(WILDCARD_ENVIRONMENT_ID)) {
	    EnvironmentInfo environmentInfo = configurationInfo.getEnvironment(WILDCARD_ENVIRONMENT_ID);

	    // get resource if it exists exists
	    if (environmentInfo.containsResource(id))
		return environmentInfo.getResource(id);
	}

	// throw exception
	String message = null;
	if (!containsEnvironment(environment)) {
	    Object[] args = { environment };
	    message = messageProvider.getMessage("rr.get_resource_env_failed", args);
	    throw new EnvironmentNotFoundException(message);
	}

	Object[] args = { id, environment };
	message = messageProvider.getMessage("rr.get_resource_failed", args);
	throw new ResourceNotFoundException(message);
    }

    @Override
    public ResourceInfo create(String environment, String resource, String pluginId, String credentialIdRef)
	    throws ResourceAlreadyExistsException {

	// validate parameters
	Validate.notNull(environment, "environment is undefined.");
	Validate.notEmpty(environment, "environment is empty.");
	Validate.notNull(resource, "resource is undefined.");
	Validate.notEmpty(resource, "resource is empty.");
	Validate.notNull(pluginId, "pluginId is undefined.");
	Validate.notEmpty(pluginId, "pluginId is empty.");

	// create resource info
	Map<String, ResourcePropertyInfo> properties = new HashMap<String, ResourcePropertyInfo>();
	ResourceInfoImpl resourceInfo = new ResourceInfoImpl(resource, pluginId, credentialIdRef, properties,
		resourceConfigurationMarshaller);

	// get environment
	EnvironmentInfo environmentInfo = getEnvironment(environment);

	// type cast
	EnvironmentInfoImpl typecastEnvInfo = (EnvironmentInfoImpl) environmentInfo;

	// throw exception if resource already exists
	if (environmentInfo.containsResource(resource)) {
	    Object[] args = { resource, environment };
	    String message = messageProvider.getMessage("rr.resource_already_exists_failure", args);
	    throw new ResourceAlreadyExistsException(message);
	}

	// add resource
	typecastEnvInfo.addResource(resourceInfo);

	// save
	saveRepository();

	return resourceInfo;
    }

    @Override
    public void delete(String environment, String id) throws EnvironmentNotFoundException, ResourceNotFoundException {

	// get environment and resource info
	EnvironmentInfo environmentInfo = getEnvironment(environment);

	// get resource info
	ResourceInfo resourceInfo = get(environment, id);
	if (resourceInfo == null) {
	    Object[] args = { id, environment };
	    String message = messageProvider.getMessage("rr.get_resource_failed", args);
	    throw new ResourceNotFoundException(message);
	}

	// type cast
	EnvironmentInfoImpl typecastEnvInfo = (EnvironmentInfoImpl) environmentInfo;

	// delete
	typecastEnvInfo.deleteResource(resourceInfo);

	// save
	saveRepository();
    }

    @Override
    public ResourceInfo update(ResourceInfo resourceInfo, String environment, String resource, String pluginId,
	    String credentialIdRef) {
	// validate parameters
	Validate.notNull(resourceInfo, "resourceInfo is undefined.");
	Validate.notNull(environment, "environment is undefined.");
	Validate.notEmpty(environment, "environment is empty.");
	Validate.notNull(resource, "resource is undefined.");
	Validate.notEmpty(resource, "resource is empty.");
	Validate.notNull(pluginId, "pluginId is undefined.");
	Validate.notEmpty(pluginId, "pluginId is empty.");

	// get resource info for internal data structure
	ResourceInfo validatedInfo = get(environment, resourceInfo.getId());

	// get environment info
	EnvironmentInfo environmentInfo = getEnvironment(environment);

	// type cast
	EnvironmentInfoImpl typecastEnvInfo = (EnvironmentInfoImpl) environmentInfo;

	// delete
	typecastEnvInfo.deleteResource(resourceInfo);

	// create updated info
	ResourceInfoImpl newInfo = (ResourceInfoImpl) create(environment, resource, pluginId, credentialIdRef);

	// add properties
	for (ResourcePropertyInfo propertyInfo : validatedInfo.getProperties()) {
	    newInfo.addProperty(propertyInfo);
	}

	// save
	saveRepository();

	return newInfo;
    }

    @Override
    public void createResourceProperty(String environment, String id, String key, String value) {
	// validate parameters
	Validate.notNull(environment, "environment is undefined.");
	Validate.notEmpty(environment, "environment is empty.");
	Validate.notNull(id, "id is undefined.");
	Validate.notEmpty(id, "id is empty.");
	Validate.notNull(key, "key is undefined.");
	Validate.notEmpty(key, "key is empty.");
	Validate.notNull(value, "value is undefined.");
	Validate.notEmpty(value, "value is empty.");

	// get resource info
	ResourceInfo resourceInfo = get(environment, id);

	// throw exception if property already exists
	if (resourceInfo.containsProperty(key)) {
	    Object[] args = { key, id, environment };
	    String message = messageProvider.getMessage("rr.property_already_exists_failure", args);
	    throw new PropertyAlreadyExistsException(message);
	}

	// type cast
	ResourceInfoImpl typecastResourceInfo = (ResourceInfoImpl) resourceInfo;

	// create property
	ResourcePropertyImpl propertyInfo = new ResourcePropertyImpl(key, value);

	// add property
	typecastResourceInfo.addProperty(propertyInfo);

	// save
	saveRepository();
    }

    @Override
    public void deleteResourceProperty(String environment, String id, String key) {
	// validate parameters
	Validate.notNull(environment, "environment is undefined.");
	Validate.notEmpty(environment, "environment is empty.");
	Validate.notNull(id, "id is undefined.");
	Validate.notEmpty(id, "id is empty.");
	Validate.notNull(key, "key is undefined.");
	Validate.notEmpty(key, "key is empty.");

	// get resource info
	ResourceInfo resourceInfo = get(environment, id);

	// throw exception if property doesn't exist
	if (!resourceInfo.containsProperty(key)) {
	    Object[] args = { key, environment, id };
	    String message = messageProvider.getMessage("rr.get_property_failed", args);
	    throw new PropertyNotFoundException(message);
	}

	// get property info
	ResourcePropertyInfo propertyInfo = resourceInfo.getProperty(key);

	// type cast
	ResourceInfoImpl typecastResourceInfo = (ResourceInfoImpl) resourceInfo;

	// delete
	typecastResourceInfo.deleteProperty(propertyInfo);

	// save
	saveRepository();
    }

    public String[] getPluginIds() {
	// create result set
	HashMap<String, String> pluginIds = new HashMap<String, String>();

	// get all environment names
	EnvironmentInfo[] environmentInfos = configurationInfo.getEnvironments();

	// iterate over the environments
	for (EnvironmentInfo environmentInfo : environmentInfos) {

	    // get all resource id
	    ResourceInfo[] resourceInfos = environmentInfo.getResources();

	    // iterate over the resources
	    for (ResourceInfo resourceInfo : resourceInfos) {

		// get plugin id
		String pluginId = resourceInfo.getPluginId();

		// add plugin id if it isn't already added
		if (!pluginIds.containsKey(pluginId)) {

		    pluginIds.put(pluginId, pluginId);
		}
	    }
	}

	// convert to string array
	String[] resultArray = pluginIds.keySet().toArray(new String[pluginIds.keySet().size()]);

	return resultArray;
    }

    @Override
    public ResourceInfo[] getResources(String environment) {
	Validate.notNull(environment, "environment is undefined.");
	Validate.notEmpty(environment, "environment is empty.");

	if (configurationInfo.containsEnvironment(environment)) {
	    EnvironmentInfo environmentInfo = configurationInfo.getEnvironment(environment);
	    return environmentInfo.getResources();
	}

	// throw exception
	Object[] args = { environment };
	String message = messageProvider.getMessage("rr.get_resources_environment_notfound_failed", args);
	throw new EnvironmentNotFoundException(message);
    }

    @Override
    public EnvironmentInfo[] getEnvironments() {
	return configurationInfo.getEnvironments();
    }

    public boolean containsEnvironment(String environment) {
	// validate parameters
	Validate.notNull(environment, "environment is undefined.");
	Validate.notEmpty(environment, "environment is empty.");

	// exit if repository isn't initialized yet
	if (configurationInfo == null)
	    return false;

	return configurationInfo.containsEnvironment(environment);
    }

    @Override
    public EnvironmentInfo getEnvironment(String environment) throws EnvironmentNotFoundException {
	// validate parameters
	Validate.notNull(environment, "environment is undefined.");
	Validate.notEmpty(environment, "environment is empty.");

	if (containsEnvironment(environment)) {
	    return configurationInfo.getEnvironment(environment);
	}

	// throw exception
	Object[] args = { environment };
	String message = messageProvider.getMessage("rr.get_environment_notfound_failed", args);
	throw new EnvironmentNotFoundException(message);
    }

    @Override
    public EnvironmentInfo createEnvironment(String environment, String description)
	    throws EnvironmentAlreadyExistsException {
	// validate parameters
	Validate.notNull(environment, "environment is undefined.");
	Validate.notEmpty(environment, "environment is empty.");

	if (configurationInfo.containsEnvironment(environment)) {
	    Object[] args = { environment };
	    String message = messageProvider.getMessage("rr.environment_already_exists_failure", args);
	    throw new EnvironmentAlreadyExistsException(message);
	}

	// create environment
	TreeMap<String, ResourceInfo> resourceInfos = new TreeMap<String, ResourceInfo>();
	EnvironmentInfo environmentInfo = new EnvironmentInfoImpl(environment, description, resourceInfos);
	configurationInfo.addEnvironment(environmentInfo);

	// save
	saveRepository();

	return environmentInfo;
    }

    @Override
    public EnvironmentInfo updateEnvironment(EnvironmentInfo environmentInfo, String id, String description) {
	// validate parameters
	Validate.notNull(environmentInfo, "environmentInfo is undefined.");
	Validate.notNull(id, "id is undefined.");
	Validate.notEmpty(id, "id is empty.");
	Validate.notNull(description, "description is undefined.");

	// get environment info for internal data structure
	EnvironmentInfo validatedInfo = getEnvironment(environmentInfo.getId());

	// delete info
	configurationInfo.deleteEnvironment(validatedInfo);

	// create updated info
	EnvironmentInfoImpl newInfo = (EnvironmentInfoImpl) createEnvironment(id, description);

	// add resource infos
	for (ResourceInfo resourceInfo : validatedInfo.getResources()) {
	    newInfo.addResource(resourceInfo);
	}

	return newInfo;
    }

    @Override
    public void deleteEnvironment(String environment) throws EnvironmentNotFoundException {
	// get info or provoke exceptions
	EnvironmentInfo environmentInfo = getEnvironment(environment);
	configurationInfo.deleteEnvironment(environmentInfo);

	// save
	saveRepository();
    }

    /**
     * Save repository.
     * 
     * @return true if save succeeded.
     * 
     * @throws SaveConfigurationFailedException
     *             if save fails.
     */
    void saveRepository() throws SaveConfigurationFailedException {
	Configuration configuration = resourceConfigurationMarshaller.map(configurationInfo);
	ExecutionResult result = new ExecutionResultImpl("Save resource configuration");
	resourceConfigurationMarshaller.save(result, configuration);

	// get save result
	ExecutionResult[] resultChildren = result.getChildren();
	ExecutionResult saveResult = resultChildren[FIRST_INDEX];

	// handle successful save
	// log debug message
	if (saveResult.isSuccess()) {
	    if (logger.isDebugEnabled()) {
		logger.debug(messageProvider.getMessage("rr.save_configuration_info"));
	    }
	    return;
	}

	// handle unsuccessful save
	StringBuilder message = new StringBuilder().append(messageProvider.getMessage("rr.save_configuration_failed"))
		.append(result.getMessages().get(ExecutionResult.MSG_STACKTRACE));
	throw new SaveConfigurationFailedException(message.toString());
    }

}
