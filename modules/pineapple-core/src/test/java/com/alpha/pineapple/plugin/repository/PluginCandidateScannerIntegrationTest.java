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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.testutils.TestUtilsTestConstants;

/**
 * Integration test of the class {@link PluginCandidateScannerImpl }.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class PluginCandidateScannerIntegrationTest {

	/**
	 * Object under test.
	 */
	@Resource
	PluginCandidateScanner pluginCandidateScanner;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		pluginCandidateScanner = null;
	}

	/**
	 * Test that scanner can be lookup from context.
	 */
	@Test
	public void testCanCreateInstance() {
		// test
		assertNotNull(pluginCandidateScanner);
	}

	/**
	 * Test that scanner can scan with an empty list of plugin id's.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanScanWithEmptyPluginList() throws Exception {
		String[] pluginIds = {};
		ApplicationContext result = pluginCandidateScanner.scanForPlugins(pluginIds);

		// test
		assertNotNull(result);
	}

	/**
	 * Test that scanner rejects undefined list of plugin id's.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testScanRejectsUndefinedPluginList() throws Exception {
		pluginCandidateScanner.scanForPlugins(null);
	}

	/**
	 * Test that scanner can scan with defined plugin id.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanScanWithExistingPlugin() throws Exception {
		String[] pluginIds = { TestUtilsTestConstants.pluginIdHelloWorld };
		ApplicationContext result = pluginCandidateScanner.scanForPlugins(pluginIds);

		// test
		assertNotNull(result);
		assertTrue(result.containsBean("plugin:" + TestUtilsTestConstants.pluginIdHelloWorld));
	}

	/**
	 * Test that scanner can be scan with plugin id which doesn't exist on the class
	 * path.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanScanWithNonExistingPlugin() throws Exception {
		final String NONEXISTING_PLUGINID = "some.nonexisting.plugin";

		String[] pluginIds = { NONEXISTING_PLUGINID };
		ApplicationContext result = pluginCandidateScanner.scanForPlugins(pluginIds);

		// test
		assertNotNull(result);
		assertFalse(result.containsBean("plugin:" + TestUtilsTestConstants.pluginIdHelloWorld));
		assertFalse(result.containsBean("plugin:" + NONEXISTING_PLUGINID));
	}

}
