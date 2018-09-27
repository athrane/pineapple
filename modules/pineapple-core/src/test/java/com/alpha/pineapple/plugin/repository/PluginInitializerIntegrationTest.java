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

package com.alpha.pineapple.plugin.repository;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.plugin.PluginInitializationFailedException;
import com.alpha.pineapple.plugin.test.inputmarshalling.InputMarshallingTestPluginImpl;

/**
 * Integration test of the class {@link PluginInitializerImpl }.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class PluginInitializerIntegrationTest {

	/**
	 * A non plugin class.
	 */
	class NonPluginClass {

	}

	/**
	 * Object under test.
	 */
	@Resource
	PluginInitializer pluginInitializer;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		pluginInitializer = null;
	}

	/**
	 * Test that initializer can be lookup from context.
	 */
	@Test
	public void testCanCreateInstance() {
		// test
		assertNotNull(pluginInitializer);
	}

	/**
	 * Test that initializer can initialize plugin class.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanInitializePlugin() throws Exception {
		Object pluginClass = new com.alpha.pineapple.plugin.helloworld.PluginImpl();
		String pluginId = pluginClass.getClass().getPackage().getName();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// test
		assertNotNull(info);
		assertEquals(pluginId, info.getPluginId());
	}

	/**
	 * Test that context is defined for initialized plugin.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testContextIsDefinedInInitializedPlugin() throws Exception {
		Object pluginClass = new com.alpha.pineapple.plugin.helloworld.PluginImpl();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// context
		ApplicationContext context = info.getContext();

		// test
		assertNotNull(context);
	}

	/**
	 * Test that context contains execution info provider.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testContextContainsExecutionInfoProvider() throws Exception {
		Object pluginClass = new com.alpha.pineapple.plugin.helloworld.PluginImpl();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// context
		ApplicationContext context = info.getContext();

		// get provider
		Object bean = context.getBean(PluginInitializerImpl.EXECUTION_INFO_PROVIDER_BEANID);

		// test
		assertNotNull(bean);
	}

	/**
	 * Test that context contains bean definition for execution info provider
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testContextContainsExecutionInfoProviderBeanDefinition() throws Exception {
		Object pluginClass = new com.alpha.pineapple.plugin.helloworld.PluginImpl();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// context
		ApplicationContext context = info.getContext();

		// get bean definition names
		String[] names = context.getBeanDefinitionNames();

		// test
		assertNotNull(names);
		assertTrue(ArrayUtils.contains(names, PluginInitializerImpl.EXECUTION_INFO_PROVIDER_BEANID));
	}

	/**
	 * Test that context contains runtime directory provider.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testContextContainsRuntimeDirectoryProvider() throws Exception {
		Object pluginClass = new com.alpha.pineapple.plugin.helloworld.PluginImpl();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// context
		ApplicationContext context = info.getContext();

		// get provider
		Object bean = context.getBean(PluginInitializerImpl.RUNTIME_DIRECTORY_PROVIDER_BEANID);

		// test
		assertNotNull(bean);
	}

	/**
	 * Test that context contains bean definition for runtime directory provider
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testContextContainsRuntimeDirectoryProviderBeanDefinition() throws Exception {
		Object pluginClass = new com.alpha.pineapple.plugin.helloworld.PluginImpl();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// context
		ApplicationContext context = info.getContext();

		// get bean definition names
		String[] names = context.getBeanDefinitionNames();

		// test
		assertNotNull(names);
		assertTrue(ArrayUtils.contains(names, PluginInitializerImpl.RUNTIME_DIRECTORY_PROVIDER_BEANID));
	}

	/**
	 * Test that context contains administration provider.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testContextContainsAdministrationProvider() throws Exception {
		Object pluginClass = new com.alpha.pineapple.plugin.helloworld.PluginImpl();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// context
		ApplicationContext context = info.getContext();

		// get provider
		Object bean = context.getBean(PluginInitializerImpl.ADMINISTRATION_PROVIDER_BEANID);

		// test
		assertNotNull(bean);
	}

	/**
	 * Test that context contains variable substitution provider.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testContextContainsVariableSubstitutionProvider() throws Exception {
		Object pluginClass = new com.alpha.pineapple.plugin.helloworld.PluginImpl();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// context
		ApplicationContext context = info.getContext();

		// get provider
		Object bean = context.getBean(PluginInitializerImpl.VARIABLE_SUBSTITUTION_PROVIDER_BEANID);

		// test
		assertNotNull(bean);
	}

	/**
	 * Test that context contains bean definition for administration provider
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testContextContainsAdministrationProviderBeanDefinition() throws Exception {
		Object pluginClass = new com.alpha.pineapple.plugin.helloworld.PluginImpl();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// context
		ApplicationContext context = info.getContext();

		// get bean definition names
		String[] names = context.getBeanDefinitionNames();

		// test
		assertNotNull(names);
		assertTrue(ArrayUtils.contains(names, PluginInitializerImpl.ADMINISTRATION_PROVIDER_BEANID));
	}

	/**
	 * Test that initializer fails to initialize a POJO class.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test(expected = PluginInitializationFailedException.class)
	public void testCanScanWithNonExistingPlugin() throws Exception {
		Object pluginClass = new NonPluginClass();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// test
		assertNotNull(info);
	}

	/**
	 * Test that initializer can initialize plugin class with configuration file,
	 * i.e.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanInitializePluginWithConfigurationFile() throws Exception {
		Object pluginClass = new InputMarshallingTestPluginImpl();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// test
		assertEquals(InputMarshallingTestPluginImpl.PLUGIN_ID, info.getPluginId());
		assertEquals(InputMarshallingTestPluginImpl.PLUGIN_ID + "-config.xml", info.getConfigFileName());
		assertFalse(info.isSessionHandlingEnabled());
	}

	/**
	 * Input marshalling is enabled if plugin configuration file contains
	 * unmarshaller element.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testInutMarshallingIsEnabledIfConfigurationFileContainsUnmarshallerElement() throws Exception {
		Object pluginClass = new InputMarshallingTestPluginImpl();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// test
		assertEquals(InputMarshallingTestPluginImpl.PLUGIN_ID, info.getPluginId());
		assertTrue(info.isInputMarshallingEnabled());
	}

	/**
	 * Input marshalling isn't enabled if plugin configuration file doesn't exist.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testInutMarshallingIsDisabledIfConfigurationFileDoesntExist() throws Exception {
		Object pluginClass = new com.alpha.pineapple.plugin.helloworld.PluginImpl();
		String pluginId = pluginClass.getClass().getPackage().getName();
		PluginInfo info = pluginInitializer.initializePlugin(pluginClass);

		// test
		assertEquals(pluginId, info.getPluginId());
		assertFalse(info.isInputMarshallingEnabled());
		assertEquals(pluginId + "-config.xml", info.getConfigFileName());

	}

}
