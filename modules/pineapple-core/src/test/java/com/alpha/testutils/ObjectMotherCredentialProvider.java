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

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alpha.pineapple.CoreFactory;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.model.configuration.Configuration;

/**
 * Implementation of the ObjectMother pattern, provides helper functions using
 * credentials providers in unit tests.
 * 
 * Please notice: this class is currently intended for usage for integration
 * tests only since it have the core factory injected as a dependency.
 */
public class ObjectMotherCredentialProvider {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother = new ObjectMotherEnvironmentConfiguration();

	/**
	 * Core factory.
	 */
	@Resource
	CoreFactory coreFactory;

	/**
	 * Create credential provider object for unit testing.
	 * 
	 * The credential provider is empty, i.e. it doesn't contain any credentials.
	 * 
	 * @return empty credential provider object for unit testing..
	 * 
	 * @throws Exception
	 *             if creation fails.
	 */
	public CredentialProvider createEmptyCredentialProvider() throws Exception {
		assertNotNull("The ObjectMotherCredentialProvider class is only intended for usage in integration tests",
				coreFactory);

		// create credentials configuration
		Configuration configuration;
		configuration = envConfigMother.createEmptyEnvironmentConfiguration();

		// create credential provider
		CredentialProvider provider = coreFactory.createCredentialProvider(configuration);

		return provider;
	}

	/**
	 * Create credential provider object for unit testing. The credential provider
	 * is configured with a single credential for access to the Test-Resource using
	 * the constants defined in the class CoreTestConstants.
	 * 
	 * @return credential provider object for unit testing, which contains
	 *         Test-Resource credential.
	 * @throws Exception
	 *             if creation fails.
	 */
	public CredentialProvider createProviderWithTestResourceCredential() throws Exception {
		assertNotNull("The ObjectMotherCredentialProvider class is only intended for usage in integration tests",
				coreFactory);

		// create environment configuration
		Configuration configuration;
		configuration = envConfigMother.createEnvConfigWithTestResourceCredential();

		// create credential provider
		CredentialProvider provider = coreFactory.createCredentialProvider(configuration);

		return provider;
	}

}
