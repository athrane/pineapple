/*******************************************************************************

 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2019 Allan Thrane Andersen.
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

package com.alpha.pineapple.io.file;

import static com.alpha.javautils.SystemUtils.JAVA_IO_TMPDIR;
import static com.alpha.javautils.SystemUtils.PINEAPPLE_CREDENTIALPROVIDER_PASSWORD_FILE;
import static com.alpha.javautils.SystemUtils.PINEAPPLE_HOMEDIR;
import static com.alpha.javautils.SystemUtils.USER_HOME;
import static com.alpha.javautils.SystemUtils.USER_NAME;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static com.alpha.javautils.FileUtils.USERS_DIR;


import java.io.File;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.javautils.FileUtils;
import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.module.ModuleInfo;

/**
 * Unit test of the class {@link DefaultRuntimeDirectoryProviderImpl}.
 */
public class DefaultRuntimeDirectoryProviderImplTest {

	/**
	 * Root path on Windows.
	 */
	static final String WINDOWS_ROOT_PATH = "C:\\";

	/**
	 * Windows C-drive.
	 */
	static final String C_DRIVE = WINDOWS_ROOT_PATH;

	/**
	 * Default name for pineapple directory.
	 */
	public static final String PINEAPPLE_DIR = ".pineapple";

	/**
	 * Default name for modules directory.
	 */
	public static final String MODULES_DIR = "modules";

	/**
	 * Default name for configuration directory.
	 */
	public static final String CONF_DIR = "conf";

	/**
	 * Default name for reports directory.
	 */
	public static final String REPORTS_DIR = "reports";

	/**
	 * Object under test.
	 */
	DefaultRuntimeDirectoryProviderImpl provider;

	/**
	 * Mock Java System properties.
	 */
	Properties systemProperties;

	/**
	 * Mock Java system utilities.
	 */
	SystemUtils systemUtils;

	/**
	 * Mock execution info provider.
	 */
	ExecutionInfoProvider coreExecutionInfoProvider;

	/**
	 * File utilities.
	 */
	FileUtils fileUtils;

	/**
	 * Random value.
	 */
	String randomUser;

	/**
	 * Random value.
	 */
	String randomValue;

	/**
	 * Random value.
	 */
	String randomValue2;

	/**
	 * Random value.
	 */
	String randomValue3;

	@Before
	public void setUp() throws Exception {

		randomUser = RandomStringUtils.randomAlphabetic(10);
		randomValue = RandomStringUtils.randomAlphabetic(10);
		randomValue2 = RandomStringUtils.randomAlphabetic(10);
		randomValue3 = RandomStringUtils.randomAlphabetic(10);

		// create provider
		provider = new DefaultRuntimeDirectoryProviderImpl();

		// create mocks
		fileUtils = createMock(FileUtils.class);
		systemProperties = createMock(Properties.class);
		systemUtils = createMock(SystemUtils.class);
		coreExecutionInfoProvider = createMock(ExecutionInfoProvider.class);

		// inject mocks
		ReflectionTestUtils.setField(provider, "systemProperties", systemProperties);
		ReflectionTestUtils.setField(provider, "systemUtils", systemUtils);
		ReflectionTestUtils.setField(provider, "fileUtils", fileUtils);
		ReflectionTestUtils.setField(provider, "coreExecutionInfoProvider", coreExecutionInfoProvider);
	}

	@After
	public void tearDown() throws Exception {
		provider = null;
	}

	/**
	 * Test that home directory is resolved to
	 * <code>C&#058;&#092;Users&#092;&#036;&#123;user.name&#125;&#092;.pineapple</code>
	 * with these conditions meet: 1) The system property
	 * <code>pineapple.home.dir</code> isn't defined. 2) The <code>os.name</code>is
	 * Windows something-something 3) The <code>user.home</code> is located at
	 * <code>C&#058;&#092;Users&#092;&#036;&#123;user.name&#125;</code>
	 */
	@Test
	public void testHomeDirectoryForWindows() {

		final String path = "C:\\Users\\" + randomUser;
		String osIndependentPath = StringUtils.replaceChars(path, "\\", File.separator);

		// complete mock setup
		expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
		expect(systemUtils.getSystemProperty(USER_HOME, systemProperties)).andReturn(osIndependentPath);
		expect(systemUtils.getSystemProperty(USER_NAME, systemProperties)).andReturn(randomUser);
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		expect(fileUtils.isPathInUsersDir(osIndependentPath, randomUser)).andReturn(false);
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(C_DRIVE);
		expected.append(File.separatorChar);
		expected.append(USERS_DIR);
		expected.append(File.separatorChar);
		expected.append(randomUser);
		expected.append(File.separatorChar);
		expected.append(PINEAPPLE_DIR);

		// get directory
		File dir = provider.getHomeDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that home directory is resolved to
	 * <code>C&#058;&#092;Users&#092;&#036;&#123;user.name&#125;&#092;.pineapple</code>
	 * with these conditions meet: 1) The system property
	 * <code>pineapple.home.dir</code> isn't defined. 2) The <code>os.name</code>is
	 * Windows something-something 3) The <code>user.home</code> is located at
	 * <code>C&#058;&#092;Users&#092;&#036;&#123;user.name&#125;</code>
	 */
	@Test
	public void testHomeDirectoryForWindows2() {

		final String path = "C:\\XXXX\\" + randomUser;
		String osIndependentPath = StringUtils.replaceChars(path, "\\", File.separator);

		// complete mock setup
		expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
		expect(systemUtils.getSystemProperty(USER_HOME, systemProperties)).andReturn(osIndependentPath);
		expect(systemUtils.getSystemProperty(USER_NAME, systemProperties)).andReturn(randomUser);
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		expect(fileUtils.isPathInUsersDir(osIndependentPath, randomUser)).andReturn(false);
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(C_DRIVE);
		expected.append(File.separatorChar);
		expected.append(USERS_DIR);
		expected.append(File.separatorChar);
		expected.append(randomUser);
		expected.append(File.separatorChar);
		expected.append(PINEAPPLE_DIR);

		// get directory
		File dir = provider.getHomeDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that home directory is resolved to
	 * <code>C&#058;&#092;Users&#092;&#036;&#123;user.name&#125;&#092;.pineapple</code>
	 * 
	 * with these conditions meet: 1) The system property
	 * <code>pineapple.home.dir</code> isn't defined. 2) The <code>os.name</code>is
	 * Windows something-something 3) The <code>user.home</code> is located at the
	 * root of the C drive. e.g. c:
	 */
	@Test
	public void testHomeDirectoryForWindowsIfUserHomeIsLocatedAtRoot() {

		final String path = WINDOWS_ROOT_PATH;
		String osIndependentPath = StringUtils.replaceChars(path, "\\", File.separator);

		// complete mock setup
		expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
		expect(systemUtils.getSystemProperty(USER_HOME, systemProperties)).andReturn(osIndependentPath);
		expect(systemUtils.getSystemProperty(USER_NAME, systemProperties)).andReturn(randomUser);
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		expect(fileUtils.isPathInUsersDir(osIndependentPath, randomUser)).andReturn(false);
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(C_DRIVE);
		expected.append(File.separatorChar);
		expected.append(USERS_DIR);
		expected.append(File.separatorChar);
		expected.append(randomUser);
		expected.append(File.separatorChar);
		expected.append(PINEAPPLE_DIR);

		// get directory
		File dir = provider.getHomeDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that home directory is resolved to the directory defined by the system
	 * property "pineapple.home.dir".
	 */
	@Test
	public void testHomeDirectoryForWindowsWithPineappeHomeDefined() {

		final String path = "C:\\Programs Files\\Pineapple";
		String osIndependentPath = StringUtils.replaceChars(path, "\\", File.separator);

		// complete mock setup
		expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(true);
		expect(systemUtils.getSystemProperty(PINEAPPLE_HOMEDIR, systemProperties)).andReturn(osIndependentPath);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(C_DRIVE);
		expected.append(File.separatorChar);
		expected.append("Programs Files");
		expected.append(File.separatorChar);
		expected.append("Pineapple");

		// get directory
		File dir = provider.getHomeDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that modules directory is resolved to
	 * <code>C&#058;&#092;Users&#092;&#036;&#123;user.name&#125;&#092;.pineapple&#092;modules</code>
	 * 
	 * with these conditions meet: 1) The system property
	 * <code>pineapple.home.dir</code> isn't defined. 2) The <code>os.name</code>is
	 * Windows something-something 3) The <code>user.home</code> is located at the
	 * root of the C drive. e.g. c:
	 */
	@Test
	public void testModulesDirectoryForWindowsIfUserHomeIsLocatedAtRoot() {

		final String path = WINDOWS_ROOT_PATH;
		String osIndependentPath = StringUtils.replaceChars(path, "\\", File.separator);

		// complete mock setup
		expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
		expect(systemUtils.getSystemProperty(USER_HOME, systemProperties)).andReturn(osIndependentPath);
		expect(systemUtils.getSystemProperty(USER_NAME, systemProperties)).andReturn(randomUser);
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		expect(fileUtils.isPathInUsersDir(osIndependentPath, randomUser)).andReturn(false);
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(C_DRIVE);
		expected.append(File.separatorChar);
		expected.append(USERS_DIR);
		expected.append(File.separatorChar);
		expected.append(randomUser);
		expected.append(File.separatorChar);
		expected.append(PINEAPPLE_DIR);
		expected.append(File.separatorChar);
		expected.append(MODULES_DIR);

		// get directory
		File dir = provider.getModulesDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that configuration directory is resolved to
	 * <code>C&#058;&#092;Users&#092;&#036;&#123;user.name&#125;&#092;.pineapple&#092;conf</code>
	 * 
	 * with these conditions meet: 1) The system property
	 * <code>pineapple.home.dir</code> isn't defined. 2) The <code>os.name</code>is
	 * Windows something-something 3) The <code>user.home</code> is located at the
	 * root of the C drive. e.g. c:
	 */
	@Test
	public void testConfDirectoryForWindowsIfUserHomeIsLocatedAtRoot() {

		final String path = WINDOWS_ROOT_PATH;
		String osIndependentPath = StringUtils.replaceChars(path, "\\", File.separator);

		// complete mock setup
		expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
		expect(systemUtils.getSystemProperty(USER_HOME, systemProperties)).andReturn(osIndependentPath);
		expect(systemUtils.getSystemProperty(USER_NAME, systemProperties)).andReturn(randomUser);
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		expect(fileUtils.isPathInUsersDir(osIndependentPath, randomUser)).andReturn(false);
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(C_DRIVE);
		expected.append(File.separatorChar);
		expected.append(USERS_DIR);
		expected.append(File.separatorChar);
		expected.append(randomUser);
		expected.append(File.separatorChar);
		expected.append(PINEAPPLE_DIR);
		expected.append(File.separatorChar);
		expected.append(CONF_DIR);

		// get directory
		File dir = provider.getConfigurationDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that reports directory is resolved to
	 * <code>C&#058;&#092;Users&#092;&#036;&#123;user.name&#125;&#092;.pineapple&#092;reports</code>
	 * 
	 * with these conditions meet: 1) The system property
	 * <code>pineapple.home.dir</code> isn't defined. 2) The <code>os.name</code>is
	 * Windows something-something 3) The <code>user.home</code> is located at the
	 * root of the C drive. e.g. c:
	 */
	@Test
	public void testReportsDirectoryForWindowsIfUserHomeIsLocatedAtRoot() {

		final String path = WINDOWS_ROOT_PATH;
		String osIndependentPath = StringUtils.replaceChars(path, "\\", File.separator);

		// complete mock setup
		expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
		expect(systemUtils.getSystemProperty(USER_HOME, systemProperties)).andReturn(osIndependentPath);
		expect(systemUtils.getSystemProperty(USER_NAME, systemProperties)).andReturn(randomUser);
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		expect(fileUtils.isPathInUsersDir(osIndependentPath, randomUser)).andReturn(false);
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(C_DRIVE);
		expected.append(File.separatorChar);
		expected.append(USERS_DIR);
		expected.append(File.separatorChar);
		expected.append(randomUser);
		expected.append(File.separatorChar);
		expected.append(PINEAPPLE_DIR);
		expected.append(File.separatorChar);
		expected.append(REPORTS_DIR);

		// get directory
		File dir = provider.getReportsDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that home directory is resolved to
	 * <code>user.home&#092;.pineapple</code> if these conditions are meet:
	 * 
	 * 1) The system property <code>pineapple.home.dir</code> isn't defined. 2) The
	 * <code>os.name</code> is different from Windows something-something.
	 */
	@Test
	public void testHomeDirectoryIsResolvedForNonWindowsOS() {

		String osIndependentPath = randomValue + File.separator + randomValue2;

		// complete mock setup
		expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
		expect(systemUtils.getSystemProperty(USER_HOME, systemProperties)).andReturn(osIndependentPath);
		expect(systemUtils.getSystemProperty(USER_NAME, systemProperties)).andReturn(randomUser);
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(false);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(randomValue);
		expected.append(File.separatorChar);
		expected.append(randomValue2);
		expected.append(File.separatorChar);
		expected.append(PINEAPPLE_DIR);

		// get directory
		File dir = provider.getHomeDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that home directory is resolved to
	 * <code>user.home&#092;.pineapple</code> if these conditions are meet:
	 * 
	 * 1) The system property <code>pineapple.home.dir</code> isn't defined. 2) The
	 * <code>os.name</code> is different from Windows something-something.
	 */
	@Test
	public void testHomeDirectoryIsResolvedForNonWindowsOS2() {

		final String path = "/home/"+randomValue+"/";
		String osIndependentPath = StringUtils.replaceChars(path, "/", File.separator);

		// complete mock setup
		expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
		expect(systemUtils.getSystemProperty(USER_HOME, systemProperties)).andReturn(osIndependentPath);
		expect(systemUtils.getSystemProperty(USER_NAME, systemProperties)).andReturn(randomUser);
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(false);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(File.separatorChar);
		expected.append("home");
		expected.append(File.separatorChar);
		expected.append(randomValue);
		expected.append(File.separatorChar);
		expected.append(PINEAPPLE_DIR);

		// get directory
		File dir = provider.getHomeDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that home directory is resolved to
	 * <code>user.home&#092;.pineapple</code> if these conditions are meet:
	 * 
	 * 1) The system property <code>pineapple.home.dir</code> isn't defined. 2) The
	 * <code>os.name</code> is different from Windows something-something.
	 */
	@Test
	public void testHomeDirectoryIsResolvedForNonWindowsOS3() {

		final String path = "/var/lib/";
		String osIndependentPath = StringUtils.replaceChars(path, "/", File.separator);

		// complete mock setup
		expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
		expect(systemUtils.getSystemProperty(USER_HOME, systemProperties)).andReturn(osIndependentPath);
		expect(systemUtils.getSystemProperty(USER_NAME, systemProperties)).andReturn(randomUser);
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(false);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(File.separatorChar);
		expected.append("var");
		expected.append(File.separatorChar);
		expected.append("lib");
		expected.append(File.separatorChar);
		expected.append(PINEAPPLE_DIR);

		// get directory
		File dir = provider.getHomeDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that temporary directory is resolved to the value of the
	 * <code>java.io.tempdir</code> system property.
	 * 
	 * if these conditions are meet:
	 * 
	 * 1) The system property <code>pineapple.home.dir</code> isn't defined. 2) The
	 * <code>os.name</code> is Windows something-something.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetTempDirectoryForWindows() throws Exception {

		final String path = "C:\\Users\\" + randomUser + "\\Local Settings\\Temp";
		String osIndependentPath = StringUtils.replaceChars(path, "\\", File.separator);

		// complete mock setup
		expect(systemUtils.getSystemProperty(JAVA_IO_TMPDIR, systemProperties)).andReturn(osIndependentPath);
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		expect(fileUtils.convertToLongWindowsPath(osIndependentPath)).andReturn(new File(osIndependentPath));
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(C_DRIVE);
		expected.append(File.separatorChar);
		expected.append(USERS_DIR);
		expected.append(File.separatorChar);
		expected.append(randomUser);
		expected.append(File.separatorChar);
		expected.append("Local Settings");
		expected.append(File.separatorChar);
		expected.append("Temp");

		// get directory
		File dir = provider.getTempDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that temporary directory is resolved to the value of the
	 * <code>java.io.tempdir</code> system property.
	 * 
	 * if these conditions are meet:
	 * 
	 * 1) The system property <code>pineapple.home.dir</code> isn't defined. 2) The
	 * <code>os.name</code> is Windows something-something.
	 * 
	 * @throws Exception
	 *             if test fails.
	 * 
	 */
	@Test
	public void testGetTempDirectoryForWindowsWithShortNames() throws Exception {

		final String path = "C:\\Users\\" + randomUser + "\\Local Settings\\Temp";
		String osIndependentPath = StringUtils.replaceChars(path, "\\", File.separator);

		final String shortPath = "C:\\DOCUME~1\\" + randomUser + "\\LOCALS~1\\Temp";
		String shortOsIndependentPath = StringUtils.replaceChars(shortPath, "\\", File.separator);

		// complete mock setup
		expect(systemUtils.getSystemProperty(JAVA_IO_TMPDIR, systemProperties)).andReturn(shortOsIndependentPath);
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		expect(fileUtils.convertToLongWindowsPath(shortOsIndependentPath)).andReturn(new File(osIndependentPath));
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(C_DRIVE);
		expected.append(File.separatorChar);
		expected.append(USERS_DIR);
		expected.append(File.separatorChar);
		expected.append(randomUser);
		expected.append(File.separatorChar);
		expected.append("Local Settings");
		expected.append(File.separatorChar);
		expected.append("Temp");

		// get directory
		File dir = provider.getTempDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that temporary directory is resolved to the value of the
	 * <code>java.io.tempdir</code> system property.
	 * 
	 * if these conditions are meet:
	 * 
	 * 1) The system property <code>pineapple.home.dir</code> isn't defined. 2) The
	 * <code>os.name</code> isn't Windows something-something.
	 * 
	 */
	@Test
	public void testGetTempDirectoryForLinux() {

		final String path = "/tmp";
		String osIndependentPath = StringUtils.replaceChars(path, "/", File.separator);

		// complete mock setup
		expect(systemUtils.getSystemProperty(JAVA_IO_TMPDIR, systemProperties)).andReturn(osIndependentPath);
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(false);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(File.separatorChar);
		expected.append("tmp");

		// get directory
		File dir = provider.getTempDirectory();

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that model path is resolved correctly for Linux, e.g. the 'modulepath'
	 * identifier is resolved to the expected path.
	 */
	@Test
	public void testModelPathIsResolved() {

		final String path = "/var/lib/pineapple/modules/";
		String osIndependentPath = StringUtils.replaceChars(path, "/", File.separator);

		// complete mock setup
		replay(systemUtils, systemProperties, fileUtils);

		// model path
		String modelPath = DefaultRuntimeDirectoryProviderImpl.MODULEPATH + "bin";

		// create module info mock
		ModuleInfo info = createMock(ModuleInfo.class);
		expect(info.getDirectory()).andReturn(new File(osIndependentPath));
		replay(info);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(File.separatorChar);
		expected.append("var");
		expected.append(File.separatorChar);
		expected.append("lib");
		expected.append(File.separatorChar);
		expected.append("pineapple");
		expected.append(File.separatorChar);
		expected.append("modules");
		expected.append(File.separatorChar);
		expected.append("bin");

		// get directory
		File dir = provider.resolveModelPath(modelPath, info);

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(info);
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that model path is resolved correctly for Linux, e.g. the 'modulepath'
	 * identifier is resolved to the expected path.
	 */
	@Test
	public void testModelPathIsResolved2() {

		final String path = "\\var\\lib\\pineapple\\modules\\";
		String osIndependentPath = StringUtils.replaceChars(path, "\\", File.separator);

		// complete mock setup
		replay(systemUtils, systemProperties, fileUtils);

		// model path
		String modelPath = DefaultRuntimeDirectoryProviderImpl.MODULEPATH + "bin";

		// create module info mock
		ModuleInfo info = createMock(ModuleInfo.class);
		expect(info.getDirectory()).andReturn(new File(osIndependentPath));
		replay(info);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(File.separatorChar);
		expected.append("var");
		expected.append(File.separatorChar);
		expected.append("lib");
		expected.append(File.separatorChar);
		expected.append("pineapple");
		expected.append(File.separatorChar);
		expected.append("modules");
		expected.append(File.separatorChar);
		expected.append("bin");

		// get directory
		File dir = provider.resolveModelPath(modelPath, info);

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(info);
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that model path is resolved correctly for Linux, e.g. the 'modulepath'
	 * identifier is resolved to the expected path, where the modulepath is contains
	 * with a separator char, e.g modulepath:/bin
	 * 
	 * plea
	 */
	@Test
	public void testModelPathIsResolvedWihichContainsSeparatorChar() {

		final String path = "/var/lib/pineapple/modules/";
		String osIndependentPath = StringUtils.replaceChars(path, "/", File.separator);

		// complete mock setup
		replay(systemUtils, systemProperties, fileUtils);

		// model path
		String modelPath = DefaultRuntimeDirectoryProviderImpl.MODULEPATH + "/bin";

		// create module info mock
		ModuleInfo info = createMock(ModuleInfo.class);
		expect(info.getDirectory()).andReturn(new File(osIndependentPath));
		replay(info);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(File.separatorChar);
		expected.append("var");
		expected.append(File.separatorChar);
		expected.append("lib");
		expected.append(File.separatorChar);
		expected.append("pineapple");
		expected.append(File.separatorChar);
		expected.append("modules");
		expected.append(File.separatorChar);
		expected.append("bin");

		// get directory
		File dir = provider.resolveModelPath(modelPath, info);

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(info);
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that model path is resolved correctly for Linux, e.g. the 'modulepath'
	 * identifier is resolved to the expected path, where the modulepath is contains
	 * with a separator char, e.g modulepath:\\bin
	 * 
	 * plea
	 */
	@Test
	public void testModelPathIsResolvedWihichContainsSeparatorChar2() {

		final String path = "/var/lib/pineapple/modules/";
		String osIndependentPath = StringUtils.replaceChars(path, "/", File.separator);

		// complete mock setup
		replay(systemUtils, systemProperties, fileUtils);

		// model path
		String modelPath = DefaultRuntimeDirectoryProviderImpl.MODULEPATH + "\\bin";

		// create module info mock
		ModuleInfo info = createMock(ModuleInfo.class);
		expect(info.getDirectory()).andReturn(new File(osIndependentPath));
		replay(info);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(File.separatorChar);
		expected.append("var");
		expected.append(File.separatorChar);
		expected.append("lib");
		expected.append(File.separatorChar);
		expected.append("pineapple");
		expected.append(File.separatorChar);
		expected.append("modules");
		expected.append(File.separatorChar);
		expected.append("bin");

		// get directory
		File dir = provider.resolveModelPath(modelPath, info);

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(info);
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that model path is resolved correctly for Linux, e.g. the 'modulepath'
	 * identifier is resolved to the expected path, where the modulepath is contains
	 * with a separator char, e.g modulepath:\\bin
	 * 
	 * plea
	 */
	@Test
	public void testModelPathIsResolvedWihichContainsSeparatorChar3() {

		final String path = "/var/lib/pineapple/modules/";
		String osIndependentPath = StringUtils.replaceChars(path, "/", File.separator);

		// complete mock setup
		replay(systemUtils, systemProperties, fileUtils);

		// model path
		String modelPath = DefaultRuntimeDirectoryProviderImpl.MODULEPATH + File.separatorChar + "bin";

		// create module info mock
		ModuleInfo info = createMock(ModuleInfo.class);
		expect(info.getDirectory()).andReturn(new File(osIndependentPath));
		replay(info);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(File.separatorChar);
		expected.append("var");
		expected.append(File.separatorChar);
		expected.append("lib");
		expected.append(File.separatorChar);
		expected.append("pineapple");
		expected.append(File.separatorChar);
		expected.append("modules");
		expected.append(File.separatorChar);
		expected.append("bin");

		// get directory
		File dir = provider.resolveModelPath(modelPath, info);

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(info);
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that model path is resolved correctly when using randomized paths.
	 */
	@Test
	public void testModelPathIsResolvedWithRandomPath() {

		final String path = randomValue + File.separator + randomValue2;
		String osIndependentPath = StringUtils.replaceChars(path, "/", File.separator);

		// complete mock setup
		replay(systemUtils, systemProperties, fileUtils);

		// model path
		String modelPath = DefaultRuntimeDirectoryProviderImpl.MODULEPATH + randomValue3;

		// create module info mock
		ModuleInfo info = createMock(ModuleInfo.class);
		expect(info.getDirectory()).andReturn(new File(osIndependentPath));
		replay(info);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(randomValue);
		expected.append(File.separatorChar);
		expected.append(randomValue2);
		expected.append(File.separatorChar);
		expected.append(randomValue3);

		// get directory
		File dir = provider.resolveModelPath(modelPath, info);

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(info);
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that model path can be resolved from {@linkplain ExecutionResult}.
	 */
	@Test
	public void testModelPathIsResolvedfromExecutionResult() {

		final String path = randomValue + File.separator + randomValue2;
		String osIndependentPath = StringUtils.replaceChars(path, "/", File.separator);

		// create mock execution result
		ExecutionResult result = createMock(ExecutionResult.class);
		replay(result);

		// create module info mock
		ModuleInfo info = createMock(ModuleInfo.class);
		expect(info.getDirectory()).andReturn(new File(osIndependentPath));
		replay(info);

		// create execution info mock
		ExecutionInfo executionInfo = createMock(ExecutionInfo.class);
		expect(executionInfo.getModuleInfo()).andReturn(info);
		replay(executionInfo);

		// complete mock setup
		replay(systemUtils, systemProperties, fileUtils);

		// complete mock setup
		expect(coreExecutionInfoProvider.get(result)).andReturn(executionInfo);
		replay(coreExecutionInfoProvider);

		// model path
		String modelPath = DefaultRuntimeDirectoryProviderImpl.MODULEPATH + randomValue3;

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(randomValue);
		expected.append(File.separatorChar);
		expected.append(randomValue2);
		expected.append(File.separatorChar);
		expected.append(randomValue3);

		// get directory
		File dir = provider.resolveModelPath(modelPath, result);

		// test
		assertEquals(new File(expected.toString()), dir);

		// test
		verify(executionInfo);
		verify(info);
		verify(result);
		verify(coreExecutionInfoProvider);
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that model path can be resolved from {@linkplain ExecutionResult}. If
	 * path doesn't contain the variable name the original path is returned.
	 */
	@Test
	public void testModelPathIsResolvedfromExecutionResultReturnsOrgPathIfVariableIsntFound() {

		final String path = randomValue + File.separator + randomValue2;
		String osIndependentPath = StringUtils.replaceChars(path, "/", File.separator);

		// create mock execution result
		ExecutionResult result = createMock(ExecutionResult.class);
		replay(result);

		// complete mock setup
		replay(systemUtils, systemProperties, fileUtils);
		replay(coreExecutionInfoProvider);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(randomValue);
		expected.append(File.separatorChar);
		expected.append(randomValue2);
		expected.append(File.separatorChar);
		expected.append(randomValue3);

		// get directory
		File dir = provider.resolveModelPath(osIndependentPath, result);

		// test
		assertEquals(new File(osIndependentPath.toString()), dir);

		// test
		verify(result);
		verify(coreExecutionInfoProvider);
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that model path resolution fails if path is null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToResolveModelPathFromExecutionResultIfPathIsUndefined() {

		// create mock execution result
		ExecutionResult result = createMock(ExecutionResult.class);
		replay(result);

		// complete mock setup
		replay(systemUtils, systemProperties, fileUtils);
		replay(coreExecutionInfoProvider);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(randomValue);
		expected.append(File.separatorChar);
		expected.append(randomValue2);
		expected.append(File.separatorChar);
		expected.append(randomValue3);

		// get directory
		provider.resolveModelPath(null, result);
	}

	/**
	 * Test that model path resolution fails if path is empty.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToResolveModelPathFromExecutionResultIfPathIsEmpty() {

		// create mock execution result
		ExecutionResult result = createMock(ExecutionResult.class);
		replay(result);

		// complete mock setup
		replay(systemUtils, systemProperties, fileUtils);
		replay(coreExecutionInfoProvider);

		// get directory
		provider.resolveModelPath("", result);
	}

	/**
	 * Test that model path resolution fails if result is null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToResolveModelPathFromExecutionResultIfResultIsUndefined() {

		// create mock execution result
		ExecutionResult result = createMock(ExecutionResult.class);
		replay(result);

		// complete mock setup
		replay(systemUtils, systemProperties, fileUtils);
		replay(coreExecutionInfoProvider);

		// model path
		String modelPath = DefaultRuntimeDirectoryProviderImpl.MODULEPATH + randomValue3;

		// get directory
		provider.resolveModelPath(modelPath, (ExecutionResult) null);
	}

	/**
	 * Test that master password file is resolved to the location defined by the
	 * system property "pineapple.credentialprovider.password.file".
	 */
	@Test
	public void testPasswordFileForWindowsWithPineappePasswordfileDefined() {

		final String path = "C:\\Programs Files\\Pineapple";
		String osIndependentPath = StringUtils.replaceChars(path, "\\", File.separator);

		// complete mock setup
		expect(systemUtils.isPineappleCredentialProviderPasswordHomeDefined(systemProperties)).andReturn(true);
		expect(systemUtils.getSystemProperty(PINEAPPLE_CREDENTIALPROVIDER_PASSWORD_FILE, systemProperties))
				.andReturn(osIndependentPath);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(C_DRIVE);
		expected.append(File.separatorChar);
		expected.append("Programs Files");
		expected.append(File.separatorChar);
		expected.append("Pineapple");

		// get file
		File passwordFile = provider.getCredentialProviderMasterPasswordFile();

		// test
		assertEquals(new File(expected.toString()), passwordFile);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

	/**
	 * Test that master password file is resolved to the default location if the
	 * system property "pineapple.credentialprovider.password.file" is undefined.
	 */
	@Test
	public void testPasswordFileForWindowsWithPineappePasswordFileUndefined() {

		final String path = "C:\\Programs Files\\Pineapple";
		String osIndependentPath = StringUtils.replaceChars(path, "\\", File.separator);

		// complete mock setup
		expect(systemUtils.isPineappleCredentialProviderPasswordHomeDefined(systemProperties)).andReturn(false);
		expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(true);
		expect(systemUtils.getSystemProperty(PINEAPPLE_HOMEDIR, systemProperties)).andReturn(osIndependentPath);
		replay(systemUtils);

		// complete mock setup
		replay(systemProperties);

		// complete mock setup
		replay(fileUtils);

		// create expected value
		StringBuilder expected = new StringBuilder();
		expected.append(C_DRIVE);
		expected.append(File.separatorChar);
		expected.append("Programs Files");
		expected.append(File.separatorChar);
		expected.append("Pineapple");
		expected.append(File.separatorChar);
		expected.append("conf");
		expected.append(File.separatorChar);
		expected.append("credentialprovider.password");

		// get file
		File passwordFile = provider.getCredentialProviderMasterPasswordFile();

		// test
		assertEquals(new File(expected.toString()), passwordFile);

		// test
		verify(systemUtils, systemProperties, fileUtils);
	}

}
