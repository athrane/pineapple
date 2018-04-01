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

package com.alpha.pineapple.credential;

import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.resource.EnvironmentAlreadyExistsException;

/**
 * Interface for credential provider, which provides security credentials to the
 * core component on request. The security credentials provides authentication
 * information used to access protected resources.
 */
public interface CredentialProvider {

	/**
	 * Returns true if credential is exists in designated environment.
	 * 
	 * @param environment
	 *            environment where credential is be defined.
	 * @param user
	 *            user..
	 * 
	 * @return true if credential exists in designated environment.
	 */
	boolean contains(String environment, String user);

	/**
	 * Get credential from selected environment.
	 * 
	 * @param environment
	 *            The environment where the credential should be defined.
	 * @param id
	 *            ID of requested credential.
	 * 
	 * @return requested credential.
	 * 
	 * @throws CredentialNotFoundException
	 *             if requested credential isn't defined in the credential provider.
	 */
	Credential get(String environment, String id) throws CredentialNotFoundException;

	/**
	 * Create credential.
	 * 
	 * @param environment
	 *            The environment where the credential should be defined.
	 * @param id
	 *            credential ID. Must be unique within environment.
	 * @param user
	 *            user name..
	 * @param password
	 *            password.
	 * 
	 * @returns credential info created credential.
	 * 
	 * @throws EnvironmentNotFoundException
	 *             if environment doesn't exists.
	 * @throws CredentialAlreadyExitsException
	 *             if credential with identical ID is already registered.
	 */
	CredentialInfo create(String environment, String id, String user, String password)
			throws EnvironmentNotFoundException, CredentialAlreadyExitsException;

	/**
	 * Update credential.
	 * 
	 * @param credentialInfo
	 *            credential info for credential to be updated.
	 * @param environment
	 *            environment where credential is defined.
	 * @param id
	 *            updated credential ID. Must be unique within environment.
	 * @param user
	 *            updated user name..
	 * @param password
	 *            updated password.
	 * 
	 * @return credential info for updated credential.
	 *
	 * @throws EnvironmentNotFoundException
	 *             if environment doesn't exists.
	 * @throws CredentialNotFoundException
	 *             if requested credential isn't defined in the credential provider.
	 */
	CredentialInfo update(CredentialInfo credentialInfo, String environment, String id, String user, String password)
			throws EnvironmentNotFoundException, CredentialNotFoundException;

	/**
	 * Delete credential.
	 * 
	 * @param environment
	 *            environment where resource is be defined.
	 * @param id
	 *            credential ID. Must be unique within environment.
	 * 
	 * @throws EnvironmentNotFoundException
	 *             if environment doesn't exists.
	 */
	void delete(String environment, String id) throws CredentialNotFoundException;

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
	 */
	EnvironmentInfo createEnvironment(String environment, String description) throws EnvironmentAlreadyExistsException;

	/**
	 * Returns true if provider contains environment.
	 * 
	 * @param environment
	 *            environment ID.
	 * 
	 * @return true if repository contains environment.
	 */
	boolean containsEnvironment(String environment);

	/**
	 * Get list of all environments.
	 * 
	 * @return list of all environments.
	 */
	EnvironmentInfo[] getEnvironments();

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
	 * Update environment.
	 * 
	 * @param environmentInfo
	 *            environment info which is updated.
	 * @param environment
	 *            updated ID.
	 * @param description
	 *            updated description.
	 * 
	 * @return environment info for updated environment.
	 */
	EnvironmentInfo updateEnvironment(EnvironmentInfo environmentInfo, String environment, String description);

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

}
