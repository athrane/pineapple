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

package com.alpha.javautils;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.springutils.DirectoryTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirectoryTestExecutionListener.class })
public class FileUtilsTest {

	/**
	 * Name of test module of class path.
	 */
	static final String ARCHIVE_ON_CLASSPATH = "test-archive-on-plasspath.jar";

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Random file name
	 */
	String randomFileName;

	/**
	 * Random value.
	 */
	String randomValue;

	/**
	 * Random value.
	 */
	String randomValue2;

	/**
	 * Mock Java System properties.
	 */
	Properties systemProperties;

	/**
	 * Mock Java system utilities.
	 */
	SystemUtils systemUtils;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * File utilities.
	 */
	FileUtils fileUtils;

	/**
	 * Mock resource loader
	 */
	ResourceLoader resourceLoader;

	@Before
	public void setUp() throws Exception {

		randomFileName = RandomStringUtils.randomAlphabetic(10);
		randomValue = RandomStringUtils.randomAlphabetic(10);
		randomValue2 = RandomStringUtils.randomAlphabetic(10);

		// create file utils
		fileUtils = new FileUtils();

		// create mock properties
		systemProperties = createMock(Properties.class);

		// inject properties
		ReflectionTestUtils.setField(fileUtils, "systemProperties", systemProperties);

		// create mock system utils
		systemUtils = createMock(SystemUtils.class);

		// inject utils
		ReflectionTestUtils.setField(fileUtils, "systemUtils", systemUtils);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// inject message provider
		ReflectionTestUtils.setField(fileUtils, "messageProvider", messageProvider);

		// complete mock source initialization
		IAnswer<String> answer = new MessageProviderAnswerImpl();
		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class),
				(Object[]) EasyMock.isA(Object[].class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.replay(messageProvider);

	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that file object which refers to a file is returned unmodified.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testConvertToLongWindowsPathReturnUnmodifedFile() throws Exception {

		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true).anyTimes();
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// write file
		Collection<String> lines = new ArrayList<String>();
		lines.add("some line");
		File file = new File(testDirectory, randomFileName);
		org.apache.commons.io.FileUtils.writeLines(file, lines);

		// convert
		File actual = fileUtils.convertToLongWindowsPath(file);

		// test
		assertEquals(file, actual);

		// test
		verify(systemUtils);
		verify(systemProperties);
	}

	/**
	 * Test that file object which refers to a directory is returned unmodified if
	 * the directory doesn't contain a '~' character.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testConvertToLongWindowsPathReturnUnmodifedDirectory() throws Exception {

		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true).anyTimes();
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// create directory
		File dir1 = new File(testDirectory, randomFileName);
		File dirLongName = new File(dir1, "abcdefgh");
		dirLongName.mkdirs();

		// convert
		File dirShortName = new File(dir1, "ABC~1");
		File actual = fileUtils.convertToLongWindowsPath(dirShortName);

		// test
		assertEquals(dirLongName.getName(), actual.getName());

		// invoke again to return unmodified directory
		File actual2 = fileUtils.convertToLongWindowsPath(actual);

		// test
		assertEquals(actual, actual2);

		// test
		verify(systemUtils);
		verify(systemProperties);
	}

	/**
	 * Test that file object which refers to a directory is returned unmodified if
	 * the OS isn't Windows.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testConvertToLongWindowsPathReturnUnmodifedDirectoryForNonWindowsOS() throws Exception {

		String path = "/var/lib/pineapple/modules";

		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(false).anyTimes();
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		String actual = fileUtils.convertToLongWindowsPath(path).getPath();
		String expected = path.replace('/', File.separatorChar);

		// test
		assertEquals(expected, actual);

		// test
		verify(systemUtils);
		verify(systemProperties);
	}

	/**
	 * Test that file object which refers to a directory is converted if it contains
	 * a '~' character.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testConvertToLongWindowsPath() throws Exception {

		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true).anyTimes();
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// create directory
		File dir1 = new File(testDirectory, randomFileName);
		File dirLongName = new File(dir1, "abcdefgh");
		dirLongName.mkdirs();

		// convert
		File dirShortName = new File(dir1, "ABC~1");
		File actual = fileUtils.convertToLongWindowsPath(dirShortName);

		// test
		assertEquals(dirLongName.getName(), actual.getName());

		// test
		verify(systemUtils);
		verify(systemProperties);
	}

	/**
	 * Fails if system properties is undefined.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testIsPathInDocumentAndSettingsDirFailsIfsystemPropertiesIsUndefined() {

		// complete mock setup
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// invoke
		fileUtils.isPathInDocumentsAndSettingsDir(randomValue, null);

		// test
		verify(systemUtils);
		verify(systemProperties);
	}

	/**
	 * Test that method succeeds if path is in ""Documents and Settings" directory.
	 */
	@Test
	public void testIsPathInDocumentAndSettingsDirSucceeds() {

		// complete mock setup
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// define path
		String path = File.separatorChar + FileUtils.DOCUMENTS_AND_SETTINGS + File.separatorChar + randomValue;

		// test
		assertTrue(fileUtils.isPathInDocumentsAndSettingsDir(path, randomValue));

		// test
		verify(systemProperties);
	}

	/**
	 * Test that method fails if path isn't in ""Documents and Settings" directory,
	 * e.g. user name doesn't match.
	 */
	@Test
	public void testIsPathInDocumentAndSettingsDirFails() {

		// complete mock setup
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// define path
		String path = File.separatorChar + FileUtils.DOCUMENTS_AND_SETTINGS + File.separatorChar + randomValue2;

		// test
		assertFalse(fileUtils.isPathInDocumentsAndSettingsDir(path, randomValue));

		// test
		verify(systemUtils);
		verify(systemProperties);
	}

	/**
	 * Test that method fails if path isn't in ""Documents and Settings" directory,
	 * e.g. user name is missing.
	 */
	@Test
	public void testIsPathInDocumentAndSettingsDirFails2() {

		// complete mock setup
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// define path
		String path = FileUtils.DOCUMENTS_AND_SETTINGS + File.separatorChar;

		// test
		assertFalse(fileUtils.isPathInDocumentsAndSettingsDir(path, randomValue));

		// test
		verify(systemUtils);
		verify(systemProperties);
	}

	/**
	 * Test that method fails if path isn't in ""Documents and Settings" directory,
	 * e.g. leading separator is missing.
	 */
	@Test
	public void testIsPathInDocumentAndSettingsDirFails3() {

		// complete mock setup
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// define path
		String path = FileUtils.DOCUMENTS_AND_SETTINGS + File.separatorChar + randomValue;

		// test
		assertFalse(fileUtils.isPathInDocumentsAndSettingsDir(path, randomValue));

		// test
		verify(systemUtils);
		verify(systemProperties);
	}

}
