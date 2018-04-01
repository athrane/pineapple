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

import static org.junit.Assert.assertNotNull;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Integration test of the class {@link DirectoryBasedModuleRepositoryImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class DirectoryBasedModuleRepositoryImplIntegrationTest {
	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Object under test.
	 */
	@Resource(name = "moduleRepository")
	ModuleRepository moduleRepository;

	/**
	 * Random value.
	 */
	String randomName;

	@Before
	public void setUp() throws Exception {
		randomName = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that module repository can be looked up from the context.
	 */
	@Test
	public void testCanGetRepositoryFromContext() {
		assertNotNull(moduleRepository);
	}

}
