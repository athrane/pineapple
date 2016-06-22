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

package com.alpha.pineapple.credential.encryption;

import static com.alpha.pineapple.CoreConstants.CRDENTIALPROVIDER_PASSWORD_FILE;
import static com.alpha.pineapple.CoreConstants.FILE_ENCODING_UTF8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.jasypt.encryption.pbe.config.PBEConfig;
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
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Integration test of the class {@linkplain FileBasedPasswordPBEConfigImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class FileBasedPasswordPBEConfigIntegrationTest {

    /**
     * Current test directory.
     */
    File testDirectory;

    /**
     * Subject under test.
     */
    @Resource
    PBEConfig fileBasedPasswordPBEConfigImpl;

    /**
     * Runtime directory provider.
     */
    @Resource
    RuntimeDirectoryProvider runtimeDirectoryProvider;

    @Before
    public void setUp() throws Exception {

	// get the test directory
	testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

	// set the pineapple.home.dir system property
	System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

    }

    @After
    public void tearDown() throws Exception {

	// clear the pineapple.home.dir system property
	System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);

	// fail if the the pineapple.home.dir system property is set
	assertNull(System.getProperty(SystemUtils.PINEAPPLE_HOMEDIR));
    }

    /**
     * Test instance can be looked up from context.
     */
    @Test
    public void testCanGetInstanceFromContext() {
	assertNotNull(fileBasedPasswordPBEConfigImpl);
    }

    /**
     * Test that returned password isn't null.
     */
    @Test
    public void testGetPasswordReturnsDefinedPassword() {
	String password = fileBasedPasswordPBEConfigImpl.getPassword();

	// test
	assertNotNull(password);
    }

    /**
     * Test that the same password is returned twice.
     */
    @Test
    public void testIdenticalPasswordIsReturned() {
	String password = fileBasedPasswordPBEConfigImpl.getPassword();
	String password2 = fileBasedPasswordPBEConfigImpl.getPassword();

	// test
	assertNotNull(password);
	assertNotNull(password2);
	assertEquals(password, password2);
    }

    /**
     * Test that if the password file doesn't exist then it is created.
     */
    @Test
    public void testPasswordFileIsCreated() {

	// delete password file
	File passwordFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(),
		CRDENTIALPROVIDER_PASSWORD_FILE);
	if (passwordFile.exists())
	    FileUtils.deleteQuietly(passwordFile);

	// test
	assertTrue(!passwordFile.exists());

	// get password to create new master password
	String password = fileBasedPasswordPBEConfigImpl.getPassword();

	// test new file is created
	assertNotNull(password);
	assertTrue(passwordFile.exists());
    }

    /**
     * Test that created password file isn't empty
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testCreatedPasswordFileIsntEmpty() throws Exception {

	// delete password file
	File passwordFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(),
		CRDENTIALPROVIDER_PASSWORD_FILE);
	if (passwordFile.exists())
	    FileUtils.deleteQuietly(passwordFile);

	// test
	assertTrue(!passwordFile.exists());

	// get password to create new master password
	fileBasedPasswordPBEConfigImpl.getPassword();
	String readPassword = FileUtils.readFileToString(passwordFile);

	// test
	assertNotNull(readPassword);
	assertFalse(readPassword.isEmpty());
    }

    /**
     * Test that created password in file is identical to returned password.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testCreatedPasswordInFileIsIdenticalToReturnedPAssword() throws Exception {

	// delete password file
	File passwordFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(),
		CRDENTIALPROVIDER_PASSWORD_FILE);
	if (passwordFile.exists())
	    FileUtils.deleteQuietly(passwordFile);

	// test
	assertTrue(!passwordFile.exists());

	// get password to create new master password
	String password = fileBasedPasswordPBEConfigImpl.getPassword();
	String readPassword = FileUtils.readFileToString(passwordFile);

	// test
	assertEquals(password, readPassword);
    }

    /**
     * Test that empty password in file is returned.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testEmptyPasswordInFileIsReturned() throws Exception {

	// delete password file and create empty file
	File passwordFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(),
		CRDENTIALPROVIDER_PASSWORD_FILE);
	if (passwordFile.exists())
	    FileUtils.deleteQuietly(passwordFile);
	FileUtils.writeStringToFile(passwordFile, "", FILE_ENCODING_UTF8);

	// get password to create new master password
	String password = fileBasedPasswordPBEConfigImpl.getPassword();

	// test
	assertTrue(password.isEmpty());
    }

}
