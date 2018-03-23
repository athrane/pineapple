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

package com.alpha.pineapple.credential;

import static com.alpha.pineapple.CoreConstants.CREDENTIALS_FILE;
import static com.alpha.pineapple.execution.ExecutionResult.MSG_ERROR_MESSAGE;
import static com.alpha.pineapple.execution.ExecutionResult.MSG_STACKTRACE;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alpha.pineapple.command.CommandFacade;
import com.alpha.pineapple.command.CommandFacadeException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Credentials;
import com.alpha.pineapple.model.configuration.Environment;
import com.alpha.pineapple.model.configuration.Environments;
import com.alpha.pineapple.model.configuration.ObjectFactory;

/**
 * Implementation of the {@linkplain CredentialConfigurationMarshaller}
 * interface.
 */
public class CredentialConfigurationMarshallerImpl implements CredentialConfigurationMarshaller {

    /**
     * JAXB object factory.
     */
    ObjectFactory factory = new ObjectFactory();

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Runtime directory resolver.
     */
    @Resource
    RuntimeDirectoryProvider runtimeDirectoryProvider;

    /**
     * Command facade.
     */
    @Resource
    CommandFacade commandFacade;

    @Override
    public void save(Configuration configuration) {

	// get conf directory and define credentials
	File confDirectory = runtimeDirectoryProvider.getConfigurationDirectory();
	File credentialsFile = new File(confDirectory, CREDENTIALS_FILE);

	try {
	    // load configuration
	    String message = messageProvider.getMessage("ccm.save_credentials_info");
	    ExecutionResult result = new ExecutionResultImpl(message);
	    commandFacade.saveJaxbObjects(credentialsFile, configuration, result);

	} catch (CommandFacadeException e) {
	    ExecutionResult failedResult = e.getResult();	    
	    
	    // get stack trace or error message 
	    String stackTraceMessage = failedResult.getMessages().get(MSG_STACKTRACE);
	    if (stackTraceMessage == null) stackTraceMessage = failedResult.getMessages().get(MSG_ERROR_MESSAGE);

	    // create and throw exception	    
	    Object[] args = { stackTraceMessage };
	    String errorMessage = messageProvider.getMessage("ccm.save_credentials_failed", args);
	    throw new SaveConfigurationFailedException(errorMessage);
	}
    }

    @Override
    public Configuration load(File credentialsFile) throws CredentialsFileNotFoundException {

	try {
	    // load configuration
	    String message = messageProvider.getMessage("ccm.load_credentials_info");
	    ExecutionResult result = new ExecutionResultImpl(message);
	    Object configuration = commandFacade.loadJaxbObjects(credentialsFile, Configuration.class, result);
	    return Configuration.class.cast(configuration);

	} catch (CommandFacadeException e) {
	    ExecutionResult failedResult = e.getResult();

	    // create and throw exception
	    String stackTraceMessage = failedResult.getMessages().get(MSG_STACKTRACE);
	    Object[] args = { stackTraceMessage };
	    String errorMessage = messageProvider.getMessage("ccm.load_credentials_failed", args);
	    throw new CredentialsFileNotFoundException(errorMessage);
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
	TreeMap<String, CredentialInfo> credentialInfos = new TreeMap<String, CredentialInfo>();

	// add description
	String description = environment.getDescription();
	if ((description == null) || (description.isEmpty()))
	    description = messageProvider.getMessage("ccm.null_environment_description");

	// create environment info
	EnvironmentInfoImpl environmentinfo = new EnvironmentInfoImpl(environment.getId(), description,
		credentialInfos);

	// get credential
	Credentials credentials = environment.getCredentials();
	if (credentials == null)
	    return environmentinfo;

	// credentials container
	List<Credential> credentialContainer = credentials.getCredential();
	if (credentialContainer == null)
	    return environmentinfo;

	// iterate over credentials and map
	for (Credential credential : credentialContainer) {
	    CredentialInfo credentialInfo = mapToCredentialInfo(credential);
	    credentialInfos.put(credentialInfo.getId(), credentialInfo);
	}

	// create environment info
	return environmentinfo;
    }

    /**
     * Map credential to credential info.
     * 
     * @param credential
     *            credential object which is mapped.
     * 
     * @return credential info.
     */
    CredentialInfo mapToCredentialInfo(Credential credential) {

	// create credential info
	CredentialInfoImpl credentialInfo = new CredentialInfoImpl(credential.getId(), credential.getUser(),
		credential.getPassword());

	return credentialInfo;
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
	environment.setCredentials(factory.createCredentials());
	List<Credential> credentials = environment.getCredentials().getCredential();

	// iterate over environments and map
	for (CredentialInfo credentialInfo : environmentInfo.getCredentials()) {
	    credentials.add(mapToCredential(credentialInfo));
	}

	return environment;
    }

    @Override
    public Credential mapToCredential(CredentialInfo credentialInfo) {
	Credential credential = factory.createCredential();
	credential.setId(credentialInfo.getId());
	credential.setUser(credentialInfo.getUser());
	credential.setPassword(credentialInfo.getPassword());
	return credential;
    }

}
