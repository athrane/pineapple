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

/**
 * Interface to represent information about an environment configuration in the
 * file based credential provider.
 */
public interface ConfigurationInfo {

	/**
	 * Get environment info's. If no environments are registered then an empty array
	 * is returned.
	 * 
	 * @return array of environment info's.
	 */
	EnvironmentInfo[] getEnvironments();

	/**
	 * Returns true of configuration info contains environment,
	 * 
	 * @param id
	 *            environment ID.
	 * 
	 * @return true of configuration info contains environment,
	 */
	boolean containsEnvironment(String id);

	/**
	 * Get environment Info. Returns null if environment isn't defined.
	 * 
	 * @param id
	 *            environment ID.
	 * 
	 * @return environment info.
	 */
	EnvironmentInfo getEnvironment(String id);

	/**
	 * Add environment.
	 * 
	 * Please notice that addition of an environment through this method doesn't
	 * result in the environment configuration being persisted. In order to create
	 * an persisted environment create the environment through the credential
	 * provider.
	 * 
	 * @param environmentInfo
	 *            environment info.
	 */
	void addEnvironment(EnvironmentInfo environmentInfo);

	/**
	 * Delete environment.
	 *
	 * Please notice that deletion of an environment through this method doesn't
	 * result in the environment configuration being persisted. In order to persist
	 * the removal of an environment delete the environment through the credential
	 * provider.
	 * 
	 * @param environmentInfo
	 *            environment info.
	 */
	void deleteEnvironment(EnvironmentInfo environmentInfo);

}
