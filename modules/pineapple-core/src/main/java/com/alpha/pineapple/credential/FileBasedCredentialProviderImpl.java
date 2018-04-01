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

package com.alpha.pineapple.credential;

import java.io.File;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.CoreException;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.resource.EnvironmentAlreadyExistsException;

/**
 * Implementation of the {@link CredentialProvider} interface which initializes
 * credentials from a XML file using JAXB to unmarshall data into objects from
 * the package <code>com.alpha.pineapple.model.configuration</code>.
 */
public class FileBasedCredentialProviderImpl implements CredentialProvider {
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
	@Resource
	MessageProvider messageProvider;

	/**
	 * Command runner
	 */
	@Resource
	CommandRunner commandRunner;

	/**
	 * Credential configuration marshaller.
	 */
	@Resource
	CredentialConfigurationMarshaller credentialConfigurationMarshaller;

	/**
	 * Credential info factory.
	 */
	@Resource
	CredentialInfoFactory credentialInfoFactory;

	/**
	 * Unmarshall JAXB objects command.
	 */
	@Resource
	Command unmarshallJAXBObjectsCommand;

	/**
	 * Configuration info.
	 */
	ConfigurationInfo configurationInfo;

	/**
	 * FileBasedCredentialProviderImpl no-arg constructor.
	 */
	public FileBasedCredentialProviderImpl() {
	}

	/**
	 * Internal factory method for creation of credential info.
	 * 
	 * @param id
	 *            credential ID.
	 * @param user
	 *            user name.
	 * @param password
	 *            password.
	 * @param environmentInfo
	 *            target environment info where credential is added to.
	 * 
	 * @return create credential info.
	 */
	CredentialInfo internalCreateCredentialInfo(String id, String user, String password,
			EnvironmentInfo environmentInfo) {
		// type cast
		EnvironmentInfoImpl typecastEnvInfo = (EnvironmentInfoImpl) environmentInfo;

		// create credential info
		CredentialInfo credentialInfo = credentialInfoFactory.createCredentialInfo(id, user, password);

		// add resource
		typecastEnvInfo.addCredential(credentialInfo);
		return credentialInfo;
	}

	/**
	 * Initialize provider.
	 * 
	 * @param envConfiguration
	 *            Environment configuration containing the credentials.
	 * 
	 * @throws Exception
	 *             If initialization fails.
	 */
	public void initialize(Configuration envConfiguration) throws CoreException {
		Validate.notNull(envConfiguration, "envConfiguration is undefined.");
		configurationInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// save to write back encrypted passwords
		saveProvider();

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { configurationInfo.getEnvironments().length };
			String message = messageProvider.getMessage("fbcp.initialize_success", args);
			logger.debug(message);
		}
	}

	/**
	 * Initialize provider.
	 * 
	 * @param file
	 *            File object which defines location of credentials file.
	 * 
	 * @throws Exception
	 *             If initialization fails.
	 */
	public void initialize(File file) throws CoreException {
		Validate.notNull(file, "file is undefined.");
		Configuration envConfiguration = credentialConfigurationMarshaller.load(file);
		initialize(envConfiguration);
	}

	@Override
	public boolean contains(String environment, String id) {
		// validate parameters
		Validate.notNull(environment, "environment is undefined.");
		Validate.notEmpty(environment, "environment is empty.");
		Validate.notNull(id, "id is undefined.");
		Validate.notEmpty(id, "id is empty.");

		// attempt to resolve environment
		if (containsEnvironment(environment)) {

			EnvironmentInfo environmentInfo = configurationInfo.getEnvironment(environment);
			if (environmentInfo.containsCredential(id))
				return true;
		}

		return false;
	}

	@Override
	public Credential get(String environment, String id) throws CredentialNotFoundException {
		// validate parameters
		Validate.notNull(environment, "environment is undefined.");
		Validate.notEmpty(environment, "environment is empty.");
		Validate.notNull(id, "id is undefined.");
		Validate.notEmpty(id, "id is empty.");

		// attempt to resolve environment
		if (containsEnvironment(environment)) {
			EnvironmentInfo environmentInfo = configurationInfo.getEnvironment(environment);

			// get credential if it exists exists
			if (environmentInfo.containsCredential(id)) {
				CredentialInfo info = environmentInfo.getCredential(id);
				return this.credentialConfigurationMarshaller.mapToCredential(info);
			}
		}

		// throw exception
		String message = null;
		if (!containsEnvironment(environment)) {
			Object[] args = { environment };
			message = messageProvider.getMessage("fbcp.get_credential_env_failed", args);
			throw new EnvironmentNotFoundException(message);
		}

		Object[] args = { id, environment };
		message = messageProvider.getMessage("fbcp.get_credential_failed", args);
		throw new CredentialNotFoundException(message);
	}

	@Override
	public CredentialInfo create(String environment, String id, String user, String password)
			throws EnvironmentNotFoundException, CredentialAlreadyExitsException {

		// validate parameters
		Validate.notNull(environment, "environment is undefined.");
		Validate.notEmpty(environment, "environment is empty.");
		Validate.notNull(id, "id is undefined.");
		Validate.notEmpty(id, "id is empty.");
		Validate.notNull(user, "user is undefined.");
		Validate.notEmpty(user, "user is empty.");
		Validate.notNull(password, "password is undefined.");
		Validate.notEmpty(password, "password is empty.");

		// get environment info - throws exception if environment doesn't exist
		EnvironmentInfo environmentInfo = getEnvironment(environment);

		// throw exception if credential already exists
		if (environmentInfo.containsCredential(id)) {
			Object[] args = { id, environment };
			String message = messageProvider.getMessage("fbcp.credential_already_exists_failure", args);
			throw new CredentialAlreadyExitsException(message);
		}

		// create info - bypassing integrity controls
		CredentialInfo credentialInfo = internalCreateCredentialInfo(id, user, password, environmentInfo);

		// save
		this.saveProvider();

		return credentialInfo;
	}

	@Override
	public CredentialInfo update(CredentialInfo credentialInfo, String environment, String id, String user,
			String password) throws EnvironmentNotFoundException, CredentialNotFoundException {

		// validate parameters
		Validate.notNull(credentialInfo, "credentialInfo is undefined.");
		Validate.notNull(environment, "environment is undefined.");
		Validate.notEmpty(environment, "environment is empty.");
		Validate.notNull(id, "id is undefined.");
		Validate.notEmpty(id, "id is empty.");
		Validate.notNull(user, "user is undefined.");
		Validate.notEmpty(user, "user is empty.");
		Validate.notNull(password, "password is undefined.");
		Validate.notEmpty(password, "password is empty.");

		// get environment info - throws exception if environment doesn't exist
		EnvironmentInfo environmentInfo = getEnvironment(environment);

		// get credential info - throws exception if credential doesn't exist
		CredentialInfo validatedInfo = environmentInfo.getCredential(credentialInfo.getId());
		if (validatedInfo == null) {
			Object[] args = { credentialInfo.getId(), environment };
			String message = messageProvider.getMessage("fbcp.update_credential_failed", args);
			throw new CredentialNotFoundException(message);
		}

		// type cast
		EnvironmentInfoImpl typecastEnvInfo = (EnvironmentInfoImpl) environmentInfo;

		// delete
		typecastEnvInfo.deleteCredential(credentialInfo);

		// create info - bypassing integrity controls
		CredentialInfo newInfo = internalCreateCredentialInfo(id, user, password, environmentInfo);

		// save
		saveProvider();

		return newInfo;
	}

	@Override
	public void delete(String environment, String id) throws CredentialNotFoundException {

		// get environment info
		EnvironmentInfo environmentInfo = getEnvironment(environment);

		// get credential info
		CredentialInfo credentialInfo = environmentInfo.getCredential(id);
		if (credentialInfo == null) {
			Object[] args = { id, environment };
			String message = messageProvider.getMessage("fbcp.get_credential_failed", args);
			throw new CredentialNotFoundException(message);
		}

		// type cast
		EnvironmentInfoImpl typecastEnvInfo = (EnvironmentInfoImpl) environmentInfo;

		// delete
		typecastEnvInfo.deleteCredential(credentialInfo);

		// save
		saveProvider();

	}

	@Override
	public boolean containsEnvironment(String environment) {
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
		String message = messageProvider.getMessage("fbcp.get_environment_notfound_failed", args);
		throw new EnvironmentNotFoundException(message);
	}

	@Override
	public EnvironmentInfo[] getEnvironments() {
		return configurationInfo.getEnvironments();
	}

	@Override
	public EnvironmentInfo createEnvironment(String environment, String description)
			throws EnvironmentAlreadyExistsException {
		// validate parameters
		Validate.notNull(environment, "environment is undefined.");
		Validate.notEmpty(environment, "environment is empty.");

		if (configurationInfo.containsEnvironment(environment)) {
			Object[] args = { environment };
			String message = messageProvider.getMessage("fbcp.environment_already_exists_failure", args);
			throw new EnvironmentAlreadyExistsException(message);
		}

		// create environment
		TreeMap<String, CredentialInfo> credentialInfos = new TreeMap<String, CredentialInfo>();
		EnvironmentInfo environmentInfo = new EnvironmentInfoImpl(environment, description, credentialInfos);
		configurationInfo.addEnvironment(environmentInfo);

		// save
		saveProvider();

		return environmentInfo;
	}

	@Override
	public void deleteEnvironment(String environment) throws EnvironmentNotFoundException {
		// get info or provoke exceptions
		EnvironmentInfo environmentInfo = getEnvironment(environment);
		configurationInfo.deleteEnvironment(environmentInfo);

		// save
		saveProvider();
	}

	@Override
	public EnvironmentInfo updateEnvironment(EnvironmentInfo environmentInfo, String environment, String description) {

		// validate parameters
		Validate.notNull(environmentInfo, "environmentInfo is undefined.");
		Validate.notNull(environment, "environment is undefined.");
		Validate.notEmpty(environment, "environment is empty.");
		Validate.notNull(description, "resource is description.");

		// get info or provoke exceptions
		EnvironmentInfo validatedInfo = getEnvironment(environmentInfo.getId());

		// delete info
		configurationInfo.deleteEnvironment(validatedInfo);

		// create environment
		TreeMap<String, CredentialInfo> credentialInfos = new TreeMap<String, CredentialInfo>();
		EnvironmentInfo newInfo = new EnvironmentInfoImpl(environment, description, credentialInfos);
		configurationInfo.addEnvironment(newInfo);

		// save
		saveProvider();

		return newInfo;
	}

	/**
	 * Save provider.
	 * 
	 * @return true if save succeeded.
	 * 
	 * @throws SaveConfigurationFailedException
	 *             if save fails.
	 */
	void saveProvider() throws SaveConfigurationFailedException {
		Configuration configuration = credentialConfigurationMarshaller.map(configurationInfo);
		credentialConfigurationMarshaller.save(configuration);
	}

}
