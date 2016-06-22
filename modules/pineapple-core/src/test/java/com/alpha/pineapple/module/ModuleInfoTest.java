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

package com.alpha.pineapple.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Unit test of the class <code>DirectoryBasedModuleRepositoryImpl</code>.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(DirectoryTestExecutionListener.class)
public class ModuleInfoTest {

    /**
     * Current test directory.
     */
    File testDirectory;

    /**
     * Object under test.
     */
    ModuleInfo info;

    @Before
    public void setUp() throws Exception {

	// get the test directory
	testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();
    }

    @After
    public void tearDown() throws Exception {
	testDirectory = null;
	info = null;
    }

    /**
     * Test instance can be created.
     */
    @Test
    public void testGetInstance() {
	String id = "id";
	String[] environments = new String[] {};
	boolean isDescriptorDefined = RandomUtils.nextBoolean();
	File directory = testDirectory;
	info = ModuleInfoImpl.getInstance(id, environments, isDescriptorDefined, directory);

	// test
	assertNotNull(info);
	assertEquals(id, info.getId());
	assertEquals(directory, info.getDirectory());
	assertNotNull(info.getModelEnvironments());
	assertEquals(0, info.getModelEnvironments().length);
    }

    /**
     * Test info contains correct models.
     */
    @Test
    public void testContainsModel() {
	String id = "id";
	String[] environments = new String[] { "env1", "env2" };
	boolean isDescriptorDefined = RandomUtils.nextBoolean();
	File directory = testDirectory;
	info = ModuleInfoImpl.getInstance(id, environments, isDescriptorDefined, directory);

	// test
	assertNotNull(info);
	assertFalse(info.containsModel("env0"));
	assertTrue(info.containsModel("env1"));
	assertTrue(info.containsModel("env2"));
	assertFalse(info.containsModel("env3"));
    }

    /**
     * Test models are returned sorted
     */
    @Test
    public void testGetModelEnvironments() {
	String id = "id";
	String[] environments = new String[] { "BBB", "AAA", "CCC" };
	boolean isDescriptorDefined = RandomUtils.nextBoolean();
	File directory = testDirectory;
	info = ModuleInfoImpl.getInstance(id, environments, isDescriptorDefined, directory);

	// get environments
	String[] modelsEnvs = info.getModelEnvironments();

	// test
	assertNotNull(info);
	assertEquals(3, modelsEnvs.length);
	assertEquals("AAA", modelsEnvs[0]);
	assertEquals("BBB", modelsEnvs[1]);
	assertEquals("CCC", modelsEnvs[2]);
    }

    /**
     * Test is descriptor state is returned.
     */
    @Test
    public void testIsDescriptorDefined() {
	String id = "id";
	String[] environments = new String[] { "BBB", "AAA", "CCC" };
	boolean isDescriptorDefined = RandomUtils.nextBoolean();
	File directory = testDirectory;
	info = ModuleInfoImpl.getInstance(id, environments, isDescriptorDefined, directory);

	// test
	assertEquals(isDescriptorDefined, info.isDescriptorDefined());
    }

    /**
     * Test null instance can be created.
     */
    @Test
    public void testGetNullInstance() {
	info = ModuleInfoImpl.getNullInstance();

	// test
	assertNotNull(info);
	assertEquals("null", info.getId());
	assertEquals(null, info.getDirectory());
	assertEquals(false, info.isDescriptorDefined());
	assertNotNull(info.getModelEnvironments());
	assertEquals(0, info.getModelEnvironments().length);
    }

}
