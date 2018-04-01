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

package com.alpha.testutils;

import org.apache.log4j.Logger;

import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.resource.ResourceRepository;
import com.alpha.pineapple.resource.ResourceRepositoryImpl;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for
 * using resources repository in unit tests.
 */
public class ObjectMotherResourceRepository {

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Object mother for resources.
	 */
	ObjectMotherResource resourceMother;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * MotherResourceRepository constructor.
	 */
	public ObjectMotherResourceRepository() {
		resourceMother = new ObjectMotherResource();
		envConfigMother = new ObjectMotherEnvironmentConfiguration();
		;

	}

	/**
	 * Create empty initialized resource repository.
	 * 
	 * @return empty initialized resource repository.
	 */
	public ResourceRepository createEmptyResourceRepository() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		ResourceRepositoryImpl repository = new ResourceRepositoryImpl();
		repository.initialize(envConfiguration);
		return repository;
	}

	/**
	 * Create resource repository containing the environment "environment-1" with a
	 * single resource defined with the identifier "test-resource-resource-id" for
	 * the test plugin "com.alpha.pineapple.plugin.test".
	 * 
	 * @return cache with test-resource resource.
	 */
	public ResourceRepository createResourceRepositoryWithTestResource() {
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();
		ResourceRepositoryImpl repository = new ResourceRepositoryImpl();
		repository.initialize(envConfiguration);
		return repository;
	}

}
