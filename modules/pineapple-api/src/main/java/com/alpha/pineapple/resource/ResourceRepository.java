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

package com.alpha.pineapple.resource;

import com.alpha.pineapple.model.configuration.Configuration;

/**
 * Resource repository which holds information about all resources defined in
 * the environment configuration. The resources are grouped by environment.
 */
public interface ResourceRepository {

    /**
     * Initialize repository to populate itself with resources.
     * 
     * @param envConfiguration
     *            Environment configuration which contains resources.
     */
    void initialize(Configuration envConfiguration);

    /**
     * Get resource info with id and environment. If the no module exists with
     * the id and a module model for the requested environment then a
     * <code>ResourceNotFoundException</code> is thrown.
     * 
     * @param environment
     *            Environment identifier.
     * @param id
     *            Resource ID.
     * 
     * @return cached resource defined in specified environment with queried
     *         identifier.
     * 
     * @throws EnvironmentNotFoundException
     *             If environment isn't defined.
     * @throws ResourceNotFoundException
     *             If resource isn't defined in target environment.
     */
    ResourceInfo get(String environment, String id) throws ResourceNotFoundException;

    /**
     * Create resource with no properties.
     * 
     * @param environment
     *            environment where resource is created.
     * @param id
     *            resource ID. Must be unique within environment.
     * @param pluginId
     *            plugin ID for resource. Must exist on class path.
     * @param credentialIdRef
     *            credential ID reference. Can be null or empty if no reference
     *            is defined.
     * 
     * @throws EnvironmentNotFoundException
     *             if environment doesn't exists.
     * @throws ResourceAlreadyExistsException
     *             if resource with identical ID is already registered.
     * @throws PluginIdNotFoundException
     *             if plugin ID did't correspond to valid plugin.
     */
    ResourceInfo create(String environment, String id, String pluginid, String credentialidref)
	    throws EnvironmentNotFoundException, PluginIdNotFoundException, ResourceAlreadyExistsException;

    /**
     * Returns true if resource is exists in designated environment.
     * 
     * @param environment
     *            environment where resource is be defined.
     * @param id
     *            resource ID.
     * 
     * @return true if resource is exists in designated environment.
     */
    boolean contains(String environment, String id);

    /**
     * Update resource.
     * 
     * @param resourceInfo
     *            resource info for resource to be updated.
     * @param environment
     *            environment where resource is defined.
     * @param id
     *            updated resource ID. Must be unique within environment.
     * @param pluginId
     *            updated plugin ID for resource. Must exist on class path.
     * @param credentialIdRef
     *            updated credential ID reference. Can be null or empty if no
     *            reference is defined.
     * 
     * @return resource info for updated resource.
     */
    ResourceInfo update(ResourceInfo resourceInfo, String environment, String id, String pluginId,
	    String credentialIdRef);

    /**
     * Delete resource.
     * 
     * @param environment
     *            environment where resource is be defined.
     * @param id
     *            resource ID.
     * 
     * @throws EnvironmentNotFoundException
     *             If environment isn't defined.
     * @throws ResourceNotFoundException
     *             If resource isn't defined in target environment.
     */
    void delete(String environment, String id) throws EnvironmentNotFoundException, ResourceNotFoundException;

    /**
     * Get plugin id's for all registered resources in all environments. A
     * plugin id is only returned once despite being used by several resources.
     * 
     * @return plugin id's for all registered resources in all environments.
     */
    String[] getPluginIds();

    /**
     * Get resources registered in environment.
     * 
     * @param environment
     *            Environment to get resources for.
     * 
     * @return resources registered in environment.
     */
    ResourceInfo[] getResources(String environment);

    /**
     * Create environment.
     * 
     * @param environment
     *            environment ID. Must be unique within environment.
     * @param description
     *            description of the environment.
     * 
     * @throws EnvironmentAlreadyExistsException
     *             if environment already exists.
     * @throws SaveConfigurationFailedException
     *             if saving the configuration fails.
     */
    EnvironmentInfo createEnvironment(String environment, String description) throws EnvironmentAlreadyExistsException;

    /**
     * Get environment.
     * 
     * @param environment
     *            environment ID.
     * 
     * @return requested environment.
     * 
     * @throws EnvironmentNotFoundException
     *             if environment doesn't exists.
     */
    EnvironmentInfo getEnvironment(String environment) throws EnvironmentNotFoundException;

    /**
     * Get list of all environments.
     * 
     * @return list of all environments.
     */
    EnvironmentInfo[] getEnvironments();

    /**
     * Returns true if repository contains environment.
     * 
     * @param environment
     *            environment ID.
     * 
     * @return true if repository contains environment.
     */
    boolean containsEnvironment(String environment);

    /**
     * Delete environment.
     * 
     * @param environment
     *            environment ID.
     * 
     * @throws EnvironmentNotFoundException
     *             if environment doesn't exists.
     */
    void deleteEnvironment(String environment) throws EnvironmentNotFoundException;

    /**
     * Update environment.
     * 
     * @param environmentInfo
     *            environment info which is updated.
     * @param id
     *            updated environment ID.
     * @param description
     *            updated description.
     * 
     * @return environment info for updated environment.
     */
    EnvironmentInfo updateEnvironment(EnvironmentInfo environmentInfo, String id, String description);

    /**
     * Create resource property
     * 
     * @param environment
     * @param resource
     * @param key
     * @param value
     * 
     * @param environment
     *            environment where resource is created.
     * @param id
     *            resource where property is created.
     * @param key
     *            property key.
     * @param value
     *            property value.
     * 
     * @throws EnvironmentNotFoundException
     *             if environment doesn't exists.
     * @throws ResourceNotFoundException
     *             If resource isn't defined in target environment.
     * @throws PropertyAlreadyExistsException
     *             if property with identical ID is already registered.
     */
    void createResourceProperty(String environment, String id, String key, String value)
	    throws EnvironmentNotFoundException, ResourceNotFoundException, PropertyAlreadyExistsException;

    /**
     * Delete resource property
     * 
     * @param environment
     *            environment where resource is created.
     * @param id
     *            resource where property is created.
     * @param key
     *            property key.
     * 
     * @throws EnvironmentNotFoundException
     *             if environment doesn't exists.
     * @throws ResourceNotFoundException
     *             If resource isn't defined in target environment.
     * @throws PropertyNotFoundException
     *             if property isn't defined on target resource.
     */
    void deleteResourceProperty(String environment, String id, String key)
	    throws EnvironmentNotFoundException, ResourceNotFoundException, PropertyNotFoundException;

}
