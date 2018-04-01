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

package com.alpha.pineapple.resource;

import static com.alpha.pineapple.CoreConstants.RESOURCE_FILE;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.MarshallJAXBObjectsCommand;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Environment;
import com.alpha.pineapple.model.configuration.Environments;
import com.alpha.pineapple.model.configuration.ObjectFactory;
import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.model.configuration.Resources;

/**
 * Implementation of the {@linkplain ResourceConfigurationMarshaller} interface.
 */
public class ResourceConfigurationMarshallerImpl implements ResourceConfigurationMarshaller {

	/**
	 * JAXB object factory.
	 */
	ObjectFactory factory = new ObjectFactory();

	/**
	 * Null environment info's.
	 */
	static final Map<String, EnvironmentInfo> NULL_ENVIRONMENT_INFOS = new TreeMap<String, EnvironmentInfo>();

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
	 * Runtime directory resolver.
	 */
	@javax.annotation.Resource
	RuntimeDirectoryProvider runtimeDirectoryProvider;

	/**
	 * Command runner
	 */
	@javax.annotation.Resource
	CommandRunner commandRunner;

	/**
	 * Marshall JAXB objects command.
	 */
	@javax.annotation.Resource
	Command marshallJAXBObjectsCommand;

	@SuppressWarnings("unchecked")
	@Override
	public void save(ExecutionResult executionResult, Configuration configuration) {

		// get conf directory
		File confDirectory = runtimeDirectoryProvider.getConfigurationDirectory();

		// define resources
		File resourcesFile = new File(confDirectory, RESOURCE_FILE);

		// set execution result at command runner
		commandRunner.setExecutionResult(executionResult);

		// setup context
		Context context = commandRunner.createContext();
		context.put(MarshallJAXBObjectsCommand.FILE_KEY, resourcesFile);
		context.put(MarshallJAXBObjectsCommand.ROOT_KEY, configuration);

		// create description
		Object[] args2 = { resourcesFile.getAbsolutePath() };
		String message = messageProvider.getMessage("cmi.save_resource_info", args2);

		// run command
		commandRunner.run(marshallJAXBObjectsCommand, message, context);

		// exit as failure if creation failed
		if (!commandRunner.lastExecutionSucceeded()) {

			// complete as failure
			if (executionResult.isExecuting()) {
				Object[] args = { resourcesFile };
				executionResult.completeAsFailure(messageProvider, "cmi.save_resources_failed", args);
			}
			return;
		}
	}

	@Override
	public Configuration map(ConfigurationInfo info) {

		// create configuration
		Configuration configuration = factory.createConfiguration();

		// create environments
		configuration.setEnvironments(factory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();

		// get environments from configuration
		EnvironmentInfo[] environmentInfos = info.getEnvironments();

		// if container is null then exit
		if (environmentInfos.length == 0)
			return configuration;

		// iterate over environments
		for (EnvironmentInfo environmentInfo : environmentInfos) {

			// map environment
			Environment environment = mapToEnvironment(environmentInfo);
			environments.add(environment);
		}

		return configuration;
	}

	@Override
	public ConfigurationInfo map(Configuration configuration) {

		// get environments from configuration
		Environments environmentContainer = configuration.getEnvironments();

		// if container is null then exit
		if (environmentContainer == null) {
			Map<String, EnvironmentInfo> environmentInfos = new TreeMap<String, EnvironmentInfo>();
			return new ConfigurationInfoImpl(environmentInfos);
		}

		// get environments
		List<Environment> environments = environmentContainer.getEnvironment();

		// if environments is null then exit
		if (environments == null) {
			Map<String, EnvironmentInfo> environmentInfos = new TreeMap<String, EnvironmentInfo>();
			return new ConfigurationInfoImpl(environmentInfos);
		}

		// iterate over environments
		Map<String, EnvironmentInfo> environmentInfos = new TreeMap<String, EnvironmentInfo>();
		for (Environment environment : environments) {

			// skip if environment exists
			if (environmentInfos.containsKey(environment.getId()))
				continue;

			// map environment
			EnvironmentInfo environmentInfo = mapToEnvironmentInfo(environment);
			environmentInfos.put(environmentInfo.getId(), environmentInfo);
		}

		// create configuration info
		return new ConfigurationInfoImpl(environmentInfos);
	}

	/**
	 * Map environment to environment info.
	 * 
	 * @param environment
	 *            environment object which is mapped.
	 * 
	 * @return environment info.
	 */
	EnvironmentInfo mapToEnvironmentInfo(Environment environment) {

		TreeMap<String, ResourceInfo> resourceInfos = new TreeMap<String, ResourceInfo>();

		// add description
		String description = environment.getDescription();
		if ((description == null) || (description.isEmpty()))
			description = messageProvider.getMessage("cmi.null_environment_description");

		// create environment info
		EnvironmentInfoImpl environmentinfo = new EnvironmentInfoImpl(environment.getId(), description, resourceInfos);

		// get resource
		Resources resources = environment.getResources();
		if (resources == null)
			return environmentinfo;

		// resources container
		List<Resource> resourceContainer = resources.getResource();
		if (resourceContainer == null)
			return environmentinfo;

		// iterate over resources and map
		for (Resource resource : resourceContainer) {
			ResourceInfo resourceInfo = mapToResourceInfo(resource);
			resourceInfos.put(resourceInfo.getId(), resourceInfo);
		}

		// create environment info
		return environmentinfo;
	}

	/**
	 * Map resource to resource info.
	 * 
	 * @param resource
	 *            resource object which is mapped.
	 * 
	 * @return resource info.
	 */
	ResourceInfo mapToResourceInfo(Resource resource) {

		Map<String, ResourcePropertyInfo> propertyInfos = new TreeMap<String, ResourcePropertyInfo>();

		// create resource info
		ResourceInfoImpl resourceInfo = new ResourceInfoImpl(resource.getId(), resource.getPluginId(),
				resource.getCredentialIdRef(), propertyInfos, this);

		// get properties
		List<Property> properties = resource.getProperty();
		if (properties == null)
			return resourceInfo;

		// iterate over properties and map
		for (Property property : properties) {
			ResourcePropertyInfo resourcePropertyInfo = mapToResourcePropertyInfo(property);
			propertyInfos.put(resourcePropertyInfo.getKey(), resourcePropertyInfo);
		}

		return resourceInfo;
	}

	/**
	 * Map resource property to resource property info.
	 * 
	 * @param resource
	 *            property object which is mapped.
	 * 
	 * @return resource property info.
	 */
	ResourcePropertyInfo mapToResourcePropertyInfo(Property property) {
		return new ResourcePropertyImpl(property.getKey(), property.getValue());
	}

	/**
	 * Map environment info to environment.
	 * 
	 * @param environmentInfo
	 *            environment info which is mapped.
	 * 
	 * @return environment.
	 */
	Environment mapToEnvironment(EnvironmentInfo environmentInfo) {

		Environment environment = factory.createEnvironment();
		environment.setId(environmentInfo.getId());
		environment.setDescription(environmentInfo.getDescription());

		// get properties
		environment.setResources(factory.createResources());
		List<Resource> resources = environment.getResources().getResource();

		// iterate over environments and map
		for (ResourceInfo resourceInfo : environmentInfo.getResources()) {
			resources.add(mapToResource(resourceInfo));
		}

		return environment;
	}

	@Override
	public Resource mapToResource(ResourceInfo resourceInfo) {
		Resource resource = factory.createResource();
		resource.setId(resourceInfo.getId());
		resource.setPluginId(resourceInfo.getPluginId());
		resource.setCredentialIdRef(resourceInfo.getCredentialIdRef());

		// get properties
		List<Property> resourceProps = resource.getProperty();

		// iterate over properties and map
		for (ResourcePropertyInfo propertyInfo : resourceInfo.getProperties()) {
			resourceProps.add(mapToProperty(propertyInfo));
		}

		return resource;
	}

	/**
	 * Map resource property info to property.
	 * 
	 * @param propertyInfo
	 *            resource property.
	 * 
	 * @return property.
	 */
	Property mapToProperty(ResourcePropertyInfo propertyInfo) {
		Property resourceProp = new Property();
		resourceProp.setKey(propertyInfo.getKey());
		resourceProp.setValue(propertyInfo.getValue());
		return resourceProp;
	}

}
