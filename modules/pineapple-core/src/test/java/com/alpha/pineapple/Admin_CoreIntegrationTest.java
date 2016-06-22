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

package com.alpha.pineapple;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.admin.Administration;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Integration test of the class {@link CoreImpl} which focuses on the
 * administration API.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class Admin_CoreIntegrationTest {

    /**
     * Object under test.
     */
    @Resource
    CoreFactory coreFactory;

    /**
     * Current test directory.
     */
    File testDirectory;

    /**
     * Modules directory.
     */
    File modulesDir;

    /**
     * Conf directory.
     */
    File confDir;

    /**
     * Random file name.
     */
    String randomResourceXmlName;

    /**
     * Random directory name.
     */
    String randomDirName;

    /**
     * Random environment..
     */
    String randomEnvironment;

    /**
     * Random module.
     */
    String randomModuleName;

    /**
     * Random resource name.
     */
    String randomResourceName;

    @Before
    public void setUp() throws Exception {

	randomResourceXmlName = RandomStringUtils.randomAlphabetic(10) + ".xml";
	randomDirName = RandomStringUtils.randomAlphabetic(10);
	randomEnvironment = RandomStringUtils.randomAlphabetic(10);
	randomModuleName = RandomStringUtils.randomAlphabetic(10) + "-module";
	randomResourceName = RandomStringUtils.randomAlphabetic(10) + "-resource";

	// get the test directory
	testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

	// define directory names
	modulesDir = new File(testDirectory, "modules");
	confDir = new File(testDirectory, "conf");

	// clear the pineapple.home.dir system property
	System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);

	// fail if the the pineapple.home.dir system property is set
	assertNull(System.getProperty(SystemUtils.PINEAPPLE_HOMEDIR));
    }

    @After
    public void tearDown() throws Exception {

	// clear the pineapple.home.dir system setting
	System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);
    }

    /**
     * Get execution result from core which contains the state of the
     * initialization
     * 
     * @param core
     *            The core component.
     * 
     * @return execution result from core which contains the state of the
     *         initialization
     */
    ExecutionResult getInitializationResultFromCore(PineappleCore core) {
	CoreImpl coreImpl = (CoreImpl) core;
	ExecutionResult result = coreImpl.getInitializationInfo();
	return result;
    }

    /**
     * Test that initialization result isn't defined before initialization.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testGetAdministrationApiIsntDefinedBeforeInitialization() throws CoreException {

	// set the pineapple.home.dir system property
	System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

	// create core component
	PineappleCore core = new CoreImpl();

	// get administration API
	Administration admin = core.getAdministration();

	// test
	assertNull(admin);
    }

    /**
     * Test that initialization result is defined after initialization.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testGetAdministrationApiIsDefinedAfterInitialization() throws CoreException {

	// set the pineapple.home.dir system property
	System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

	// create core component
	PineappleCore core = coreFactory.createCore();

	// test
	ExecutionResult result = getInitializationResultFromCore(core);
	assertNotNull(result);
	assertFalse(result.isExecuting());
	assertTrue(result.isSuccess());
	assertTrue(result.isRoot());

	// get administration API
	Administration admin = core.getAdministration();

	// test
	assertNotNull(admin);
    }

    /**
     * Test that resource repository is defined in administration API.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testResourceRepositoryIsDefined() throws CoreException {

	// set the pineapple.home.dir system property
	System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

	// create core component
	PineappleCore core = coreFactory.createCore();

	// test
	ExecutionResult result = getInitializationResultFromCore(core);
	assertNotNull(result);
	assertFalse(result.isExecuting());
	assertTrue(result.isSuccess());
	assertTrue(result.isRoot());

	// get administration API
	Administration admin = core.getAdministration();

	// test
	assertNotNull(admin.getResourceRepository());
    }

    /**
     * Test that result repository is defined in administration API.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testResultRepositoryIsDefined() throws CoreException {

	// set the pineapple.home.dir system property
	System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

	// create core component
	PineappleCore core = coreFactory.createCore();

	// test
	ExecutionResult result = getInitializationResultFromCore(core);
	assertNotNull(result);
	assertFalse(result.isExecuting());
	assertTrue(result.isSuccess());
	assertTrue(result.isRoot());

	// get administration API
	Administration admin = core.getAdministration();

	// test
	assertNotNull(admin.getResultRepository());
    }

    /**
     * Test that credential provider is defined in administration API.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testCredentialProviderIsDefined() throws CoreException {

	// set the pineapple.home.dir system property
	System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

	// create core component
	PineappleCore core = coreFactory.createCore();

	// test
	ExecutionResult result = getInitializationResultFromCore(core);
	assertNotNull(result);
	assertFalse(result.isExecuting());
	assertTrue(result.isSuccess());
	assertTrue(result.isRoot());

	// get administration API
	Administration admin = core.getAdministration();

	// test
	assertNotNull(admin.getCredentialProvider());
    }

    /**
     * Test that scheduled operation repository is defined in administration API.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testScheduledOperationRepositoryIsDefined() throws CoreException {

	// set the pineapple.home.dir system property
	System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

	// create core component
	PineappleCore core = coreFactory.createCore();

	// test
	ExecutionResult result = getInitializationResultFromCore(core);
	assertNotNull(result);
	assertFalse(result.isExecuting());
	assertTrue(result.isSuccess());
	assertTrue(result.isRoot());

	// get administration API
	Administration admin = core.getAdministration();

	// test
	assertNotNull(admin.getModuleRepository());
    }

    
}
