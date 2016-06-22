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

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.alpha.pineapple.model.configuration.Resource;

/**
 * Implementation of the {@link ResourceInfo} interface.
 */
public class ResourceInfoImpl implements ResourceInfo {

    /**
     * Resource configuration marshaller.
     */
    @javax.annotation.Resource
    @Deprecated
    ResourceConfigurationMarshaller resourceConfigurationMarshaller;

    /**
     * Resource ID.
     */
    String id;

    /**
     * Plugin ID.
     */
    String pluginId;

    /**
     * CredentialIdRef
     */
    String credentialIdRef;

    /**
     * Resource properties.
     */
    Map<String, ResourcePropertyInfo> properties;

    /**
     * ResourceInfoImpl constructor.
     * 
     * @param id
     *            resource ID.
     * @param pluginId
     *            plugin ID.
     * @param credentialRefId
     *            credential Reference ID.
     * @param properties
     *            resource properties.
     * @param resourceConfigurationMarshaller
     *            resource configuration marshaller.
     * 
     * @throws IllegalArgumentException
     *             if the resource parameter is undefined.
     */
    public ResourceInfoImpl(String id, String pluginId, String credentialRefId,
	    Map<String, ResourcePropertyInfo> properties,
	    ResourceConfigurationMarshaller resourceConfigurationMarshaller) {
	// validate parameters
	Validate.notNull(id, "id is undefined.");
	Validate.notNull(pluginId, "pluginId is undefined.");
	Validate.notNull(properties, "properties is undefined.");
	this.id = id;
	this.pluginId = pluginId;
	this.credentialIdRef = credentialRefId;
	this.properties = properties;
	this.resourceConfigurationMarshaller = resourceConfigurationMarshaller;
    }

    public Resource getResource() {
	return resourceConfigurationMarshaller.mapToResource(this);
    }

    public String getPluginId() {
	return pluginId;
    }

    @Override
    public String getId() {
	return id;
    }

    @Override
    public String getCredentialIdRef() {
	return credentialIdRef;
    }

    @Override
    public boolean containsProperty(String key) {
	return properties.containsKey(key);
    }

    @Override
    public ResourcePropertyInfo getProperty(String key) throws PropertyNotFoundException {
	if (!this.containsProperty(key)) {
	    StringBuilder message = new StringBuilder().append("Failed to get property: ").append(key);
	    throw new PropertyNotFoundException(message.toString());
	}
	return properties.get(key);
    }

    @Override
    public String getPropertyValue(String key, String defaultValue) {
	if (!this.containsProperty(key))
	    return defaultValue;
	return properties.get(key).getValue();
    }

    @Override
    public ResourcePropertyInfo[] getProperties() {
	Collection<ResourcePropertyInfo> values = properties.values();
	return (ResourcePropertyInfo[]) values.toArray(new ResourcePropertyInfo[values.size()]);
    }

    /**
     * Add property.
     * 
     * @param propertyInfo
     *            property info object.
     */
    public void addProperty(ResourcePropertyInfo propertyInfo) {
	properties.put(propertyInfo.getKey(), propertyInfo);
    }

    /**
     * Delete property.
     * 
     * @param resourceInfo
     */
    public void deleteProperty(ResourcePropertyInfo propertyInfo) {
	if (!containsProperty(propertyInfo.getKey()))
	    return;
	properties.remove(propertyInfo.getKey());
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }

}
