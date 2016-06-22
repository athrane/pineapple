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

package com.alpha.pineapple.web.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.alpha.pineapple.credential.CredentialInfo;
import com.alpha.pineapple.execution.scheduled.ScheduledOperationInfo;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Environment;
import com.alpha.pineapple.model.configuration.ObjectFactory;
import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperation;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperations;
import com.alpha.pineapple.model.module.info.Model;
import com.alpha.pineapple.model.module.info.Module;
import com.alpha.pineapple.model.module.info.Modules;
import com.alpha.pineapple.model.report.Report;
import com.alpha.pineapple.model.report.Reports;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.resource.EnvironmentInfo;
import com.alpha.pineapple.resource.ResourceInfo;
import com.alpha.pineapple.resource.ResourcePropertyInfo;

/**
 * Implementation of the {@linkplain RestResultMapper} interface.
 */
public class RestResultMapperImpl implements RestResultMapper {

    /**
     * Message provider for I18N support.
     */
    @javax.annotation.Resource
    MessageProvider messageProvider;

    /**
     * Environment Configuration model object factory.
     */
    @javax.annotation.Resource
    ObjectFactory configurationModelObjectFactory;

    /**
     * Scheduled operation model object factory.
     */
    @javax.annotation.Resource
    com.alpha.pineapple.model.execution.scheduled.ObjectFactory scheduledOperationModelObjectFactory;

    /**
     * Scheduled operation model object factory.
     */
    @javax.annotation.Resource
    com.alpha.pineapple.model.report.ObjectFactory reportModelObjectFactory;

    /**
     * Module info model object factory.
     */
    @javax.annotation.Resource
    com.alpha.pineapple.model.module.info.ObjectFactory moduleModelObjectFactory;

    @Override
    public Configuration mapEnvironment(EnvironmentInfo environmentInfo) {

	// create configuration
	Configuration configuration = configurationModelObjectFactory.createConfiguration();

	// create environments
	configuration.setEnvironments(configurationModelObjectFactory.createEnvironments());
	List<Environment> environments = configuration.getEnvironments().getEnvironment();

	// create environment
	Environment modelEnvironment = configurationModelObjectFactory.createEnvironment();
	modelEnvironment.setId(environmentInfo.getId());
	modelEnvironment.setDescription(environmentInfo.getDescription());
	// ignore resources and credentials

	// add environment to to configuration
	environments.add(modelEnvironment);
	return configuration;
    }

    @Override
    public Configuration mapCredential(String environment, Credential credential) {

	// create configuration
	Configuration configuration = configurationModelObjectFactory.createConfiguration();

	// create environments
	configuration.setEnvironments(configurationModelObjectFactory.createEnvironments());
	List<Environment> environments = configuration.getEnvironments().getEnvironment();

	// create environment
	Environment modelEnvironment = configurationModelObjectFactory.createEnvironment();
	modelEnvironment.setId(environment);
	modelEnvironment.setCredentials(configurationModelObjectFactory.createCredentials());
	// ignore description and resources

	// add environment to to configuration
	environments.add(modelEnvironment);

	// add credential
	Credential modelCredential = internalMapToCredential(credential);
	modelEnvironment.getCredentials().getCredential().add(modelCredential);

	return configuration;
    }

    @Override
    public Configuration mapResource(String environment, ResourceInfo resourceInfo) {

	// create configuration
	Configuration configuration = configurationModelObjectFactory.createConfiguration();

	// create environments
	configuration.setEnvironments(configurationModelObjectFactory.createEnvironments());
	List<Environment> environments = configuration.getEnvironments().getEnvironment();

	// create environment
	Environment modelEnvironment = configurationModelObjectFactory.createEnvironment();
	modelEnvironment.setId(environment);
	modelEnvironment.setResources(configurationModelObjectFactory.createResources());
	// ignore description and credentials

	// add environment to to configuration
	environments.add(modelEnvironment);

	// add resource
	Resource modelResource = internalMapToResource(resourceInfo);
	modelEnvironment.getResources().getResource().add(modelResource);

	return configuration;
    }

    @Override
    public Configuration mapResourceConfiguration(EnvironmentInfo[] environmentInfos) {

	// create configuration
	Configuration configuration = configurationModelObjectFactory.createConfiguration();

	// create environments
	configuration.setEnvironments(configurationModelObjectFactory.createEnvironments());
	List<Environment> environments = configuration.getEnvironments().getEnvironment();

	// if container is null then exit
	if (environmentInfos.length == 0)
	    return configuration;

	// iterate over environments
	for (EnvironmentInfo environmentInfo : environmentInfos) {

	    // map environment
	    Environment environment = internalMapToEnvironmentForResource(environmentInfo);
	    environments.add(environment);
	}

	return configuration;
    }

    @Override
    public Configuration mapCredentialConfiguration(com.alpha.pineapple.credential.EnvironmentInfo[] environmentInfos) {

	// create configuration
	Configuration configuration = configurationModelObjectFactory.createConfiguration();

	// create environments
	configuration.setEnvironments(configurationModelObjectFactory.createEnvironments());
	List<Environment> environments = configuration.getEnvironments().getEnvironment();

	// if container is null then exit
	if (environmentInfos.length == 0)
	    return configuration;

	// iterate over environments
	for (com.alpha.pineapple.credential.EnvironmentInfo environmentInfo : environmentInfos) {

	    // map environment
	    Environment environment = internalMapToEnvironmentForCredential(environmentInfo);
	    environments.add(environment);
	}

	return configuration;
    }

    /**
     * Map environment info to environment.
     * 
     * @param environmentInfo
     *            environment info which is mapped.
     * 
     * @return environment.
     */
    Environment internalMapToEnvironmentForResource(EnvironmentInfo environmentInfo) {

	Environment environment = configurationModelObjectFactory.createEnvironment();
	environment.setId(environmentInfo.getId());
	environment.setDescription(environmentInfo.getDescription());

	// get properties
	environment.setResources(configurationModelObjectFactory.createResources());
	List<Resource> resources = environment.getResources().getResource();

	// iterate over environments and map
	for (ResourceInfo resourceInfo : environmentInfo.getResources()) {
	    resources.add(internalMapToResource(resourceInfo));
	}

	return environment;
    }

    /**
     * Map environment info to environment.
     * 
     * @param environmentInfo
     *            environment info which is mapped.
     * 
     * @return environment.
     */
    Environment internalMapToEnvironmentForCredential(com.alpha.pineapple.credential.EnvironmentInfo environmentInfo) {

	Environment environment = configurationModelObjectFactory.createEnvironment();
	environment.setId(environmentInfo.getId());
	environment.setDescription(environmentInfo.getDescription());

	// get properties
	environment.setCredentials(configurationModelObjectFactory.createCredentials());
	List<Credential> credentials = environment.getCredentials().getCredential();

	// iterate over environments and map
	for (CredentialInfo credentialInfo : environmentInfo.getCredentials()) {
	    credentials.add(internalMapToCredential(credentialInfo));
	}

	return environment;
    }

    /**
     * Map model credential to model credential.
     * 
     * @param model
     *            credential which is mapped.
     * 
     * @return model credential.
     */
    Credential internalMapToCredential(Credential credential) {
	Credential newCredential = configurationModelObjectFactory.createCredential();
	newCredential.setId(credential.getId());
	newCredential.setUser(credential.getUser());
	newCredential.setPassword(credential.getPassword());
	return newCredential;
    }

    /**
     * Map model credential info to model credential.
     * 
     * @param credential
     *            info which is mapped.
     * 
     * @return model credential.
     */
    Credential internalMapToCredential(CredentialInfo credentialInfo) {
	Credential newCredential = configurationModelObjectFactory.createCredential();
	newCredential.setId(credentialInfo.getId());
	newCredential.setUser(credentialInfo.getUser());
	newCredential.setPassword(credentialInfo.getPassword());
	return newCredential;
    }

    /**
     * Map resource info to resource .
     * 
     * @param resourceInfo
     *            resource info which is mapped.
     * 
     * @return resource.
     */
    Resource internalMapToResource(ResourceInfo resourceInfo) {
	Resource resource = configurationModelObjectFactory.createResource();
	resource.setId(resourceInfo.getId());
	resource.setPluginId(resourceInfo.getPluginId());
	resource.setCredentialIdRef(resourceInfo.getCredentialIdRef());

	// get properties
	List<Property> resourceProps = resource.getProperty();

	// iterate over properties and map
	for (ResourcePropertyInfo propertyInfo : resourceInfo.getProperties()) {
	    resourceProps.add(internalMapToProperty(propertyInfo));
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
    Property internalMapToProperty(ResourcePropertyInfo propertyInfo) {
	Property resourceProp = new Property();
	resourceProp.setKey(propertyInfo.getKey());
	resourceProp.setValue(propertyInfo.getValue());
	return resourceProp;
    }

    @Override
    public ScheduledOperations mapScheduledOperations(Stream<ScheduledOperationInfo> operations) {
	ScheduledOperations modelOperations = scheduledOperationModelObjectFactory.createScheduledOperations();
	List<ScheduledOperation> modelOperationsList = modelOperations.getScheduledOperation();
	operations.map(info -> info.getOperation()).forEach(modelOperationsList::add);
	return modelOperations;
    }

    @Override
    public Reports mapReports(Stream<Report> reports) {
	Reports modelReports = reportModelObjectFactory.createReports();
	List<Report> reportList = modelReports.getReport();
	reports.forEach(reportList::add);
	return modelReports;
    }

    @Override
    public Modules mapModules(ModuleInfo[] infos) {
	Modules modelModules = moduleModelObjectFactory.createModules();
	List<Module> modulesList = modelModules.getModule();
	Stream<ModuleInfo> stream = Arrays.stream(infos);
	stream.map(info -> internalMapToModule(info)).forEach(modulesList::add);
	return modelModules;
    }

    /**
     * Map module info to model module.
     * 
     * @param info
     *            module info.
     * @return model module.
     */
    Module internalMapToModule(ModuleInfo info) {
	Module module = moduleModelObjectFactory.createModule();
	module.setDescription("n/a");
	module.setId(info.getId());
	module.setDirectory(info.getDirectory().getAbsolutePath());
	module.setIsDescriptorDefined(info.isDescriptorDefined());
	List<Model> modelList = module.getModel();
	Stream<String> stream = Arrays.stream(info.getModelEnvironments());
	stream.map(env -> internalMapToModel(env)).forEach(modelList::add);
	return module;
    }

    /**
     * Map environment to model model.
     * 
     * @param environment
     *            environment.
     * @return model model
     */
    Model internalMapToModel(String environment) {
	Model model = moduleModelObjectFactory.createModel();
	model.setId(environment);
	return model;
    }

}
