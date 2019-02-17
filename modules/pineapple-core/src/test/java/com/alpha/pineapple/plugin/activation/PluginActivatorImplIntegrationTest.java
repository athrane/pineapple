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

package com.alpha.pineapple.plugin.activation;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.plugin.repository.PluginRuntimeRepository;
import com.alpha.pineapple.resource.ResourceRepository;
import com.alpha.spring.config.IntegrationTestSpringConfig;
import com.alpha.testutils.ObjectMotherCredentialProvider;
import com.alpha.testutils.ObjectMotherResourceRepository;

/**
 * Integration test of the class {@link PluginActivatorImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@ContextHierarchy({ @ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" }),
		@ContextConfiguration(classes = IntegrationTestSpringConfig.class) })
public class PluginActivatorImplIntegrationTest {
	/**
	 * Object under test.
	 */
	@Resource
	PluginActivator pluginActivator;

	/**
	 * Plugin repository.
	 */
	@Resource
	PluginRuntimeRepository pluginRepository;

	/**
	 * Resource repository.
	 */
	@Resource
	ResourceRepository resourceRepository;

	/**
	 * Object mother for credential provider
	 */
	@Resource
	ObjectMotherCredentialProvider providerMother;

	/**
	 * Credential provider
	 */
	CredentialProvider provider;

	/**
	 * Object mother for resources cache.
	 */
	ObjectMotherResourceRepository resourceCacheMother;

	@Before
	public void setUp() throws Exception {
		// create resources cache object mother
		resourceCacheMother = new ObjectMotherResourceRepository();

		// create credential provider
		provider = providerMother.createEmptyCredentialProvider();
	}

	@After
	public void tearDown() throws Exception {
		// delete objects
		pluginActivator = null;
		pluginRepository = null;
		resourceRepository = null;
		provider = null;
		resourceCacheMother = null;
		providerMother = null;
	}

	/**
	 * Test that plugin activator can be looked up from the context.
	 */
	@Test
	public void testCanGetActivatorFromContext() {
		assertNotNull(pluginActivator);
	}

	/**
	 * Test that activator can be initialized.
	 */
	@Test
	public void testInitialize() {
		// invoke to provoke exception
		pluginActivator.initialize(provider, resourceRepository, pluginRepository);
	}

}
