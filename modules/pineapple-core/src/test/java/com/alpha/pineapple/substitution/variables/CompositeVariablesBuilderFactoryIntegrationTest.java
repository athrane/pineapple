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

package com.alpha.pineapple.substitution.variables;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test of the Spring configured bean name
 * <code>compositeVariablesBuilderFactory</code> which is used t create
 * prototype instances of the class {@linkplain CompositeVariablesBuilderImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class CompositeVariablesBuilderFactoryIntegrationTest {

    /**
     * Object under test.
     */
    @Resource
    ObjectFactory<CompositeVariablesBuilder> compositeVariablesBuilderFactory;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test that factory can be looked up from the context.
     */
    @Test
    public void testCanGetFactoryFromContext() {
	assertNotNull(compositeVariablesBuilderFactory);
    }

    /**
     * Test builder can be created from factory.
     */
    @Test
    public void testCanCreateBuilder() {
	assertNotNull(compositeVariablesBuilderFactory.getObject());
    }

    /**
     * Test that each created builder is a Spring prototype.
     */
    @Test
    public void testCreatedBuilderIsSpringProtototype() {
	CompositeVariablesBuilder builder = compositeVariablesBuilderFactory.getObject();
	CompositeVariablesBuilder builder2 = compositeVariablesBuilderFactory.getObject();
	assertFalse(builder.hashCode() == builder2.hashCode());
    }

}
