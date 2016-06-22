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

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

/**
 * Unit test of the class {@link PluginNameGeneratorImpl }.
 */
public class PluginNameGeneratorImplTest {

    /**
     * Object under test.
     */
    BeanNameGenerator pluginNameGenerator;

    /**
     * Mock bean definition.
     */
    BeanDefinition definition;

    /**
     * Mock bean definition registry.
     */
    BeanDefinitionRegistry registry;

    @Before
    public void setUp() throws Exception {

	// create generator
	pluginNameGenerator = new PluginNameGeneratorImpl();

	// create mock bean definition
	definition = EasyMock.createMock(BeanDefinition.class);

	// create mock bean definition
	registry = EasyMock.createMock(BeanDefinitionRegistry.class);
    }

    @After
    public void tearDown() throws Exception {
	pluginNameGenerator = null;
	definition = null;
	registry = null;
    }

    /**
     * Test that plugin generator generates excpected name.
     */
    @Test
    public void testGenerateBeanNameReturnsExpectedName() {

	String pluginId = "some.plugin.id";
	String pluginClass = pluginId + ".someclass";

	// complete mocks initialization
	EasyMock.expect(definition.getBeanClassName()).andReturn(pluginClass);
	EasyMock.replay(definition);
	EasyMock.replay(registry);

	// generate name
	String name = pluginNameGenerator.generateBeanName(definition, registry);

	// test
	assertEquals("plugin:" + pluginId, name);

	// verify mocks
	EasyMock.verify(definition);
	EasyMock.verify(registry);
    }

}
