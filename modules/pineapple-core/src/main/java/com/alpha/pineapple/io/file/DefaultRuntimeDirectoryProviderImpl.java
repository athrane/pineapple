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

import static com.alpha.javautils.ArgumentUtils.notNull;
import static com.alpha.javautils.SystemUtils.JAVA_IO_TMPDIR;
import static com.alpha.javautils.SystemUtils.PINEAPPLE_CREDENTIALPROVIDER_PASSWORD_FILE;
import static com.alpha.javautils.SystemUtils.PINEAPPLE_HOMEDIR;
import static com.alpha.javautils.SystemUtils.USER_HOME;
import static com.alpha.javautils.SystemUtils.USER_NAME;
import static com.alpha.pineapple.CoreConstants.CONF_DIR;
import static com.alpha.pineapple.CoreConstants.CRDENTIALPROVIDER_PASSWORD_FILE;
import static com.alpha.pineapple.CoreConstants.MODULES_DIR;
import static com.alpha.pineapple.CoreConstants.PINEAPPLE_DIR;
import static com.alpha.pineapple.CoreConstants.REPORTS_DIR;
import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.File;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alpha.javautils.ConversionFailedException;
import com.alpha.javautils.FileUtils;
import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.module.ModuleInfo;

/**
 * Implementation of the <code>RuntimeDirectoryProvider</code> interface which
 * is used to resolve the default resolve runtime directories where Pineapple
 * will run from.
 * 
 * If the system property <code>pineapple.home.dir</code> is set then the home
 * directory is resolved to this directory.
 * 
 * Otherwise if the the OS is Windows then runtime directory is resolved to
 * <code>C&#058;&#092;Users&#092;&#036;&#123;user.name&#125;&#092;.pineapple</code>
 * 
 * Finally, otherwise the runtime root directory is resolved to value of the
 * system property <code>&#036;&#123;user.home&#125;&#092;.pineapple</code>
 */
public class DefaultRuntimeDirectoryProviderImpl implements RuntimeDirectoryProvider {

	/**
	 * Windows C drive.
	 */
	static final String C_DRIVE = "C:\\";

	/**
	 * Module path constant.
	 */
	static final String MODULEPATH = "modulepath:";

	/**
	 * Logger object
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Java System properties.
	 */
	@Resource
	Properties systemProperties;

	/**
	 * Java system utilities.
	 */
	@Resource
	SystemUtils systemUtils;

	/**
	 * File utilities.
	 */
	@Resource
	FileUtils fileUtils;

	/**
	 * Execution info provider.
	 */
	@Resource
	ExecutionInfoProvider coreExecutionInfoProvider;

	@Override
	public File getHomeDirectory() {

		// is home directory defined in system properties
		if (systemUtils.isPineappleHomeDefined(systemProperties)) {

			// get Pineapple home defined in system properties
			String pineappleHome = systemUtils.getSystemProperty(PINEAPPLE_HOMEDIR, systemProperties);
			File pineappleHomeDir = new File(pineappleHome);
			return pineappleHomeDir;
		}

		// get system properties
		String userHome = systemUtils.getSystemProperty(USER_HOME, systemProperties);
		String userName = systemUtils.getSystemProperty(USER_NAME, systemProperties);

		// handle windows OS
		if (systemUtils.isWindowsOperatingSystem(systemProperties)) {

			// validate if user.home is located in the "Users" directory
			if (!fileUtils.isPathInUsersDir(userHome, userName)) {

				// construct home directory in in the "Users" directory
				File rootDir = new File(C_DRIVE);
				File documentAndSettingsDir = new File(rootDir, FileUtils.USERS_DIR);
				File userHomeDir = new File(documentAndSettingsDir, userName);
				File pineappleHomeDir = new File(userHomeDir, PINEAPPLE_DIR);

				return pineappleHomeDir;
			}

			// use already set user home
			File pineappleHomeDir = new File(userHome, PINEAPPLE_DIR);
			return pineappleHomeDir;
		}

		// define default pineapple home directory
		File pineappleHomeDir = new File(userHome, PINEAPPLE_DIR);
		return pineappleHomeDir;
	}

	@Override
	public File getChildDirectory(String childDirectory) {
		return new File(getHomeDirectory(), childDirectory);
	}

	@Override
	public File getModulesDirectory() {
		return getChildDirectory(MODULES_DIR);
	}

	@Override
	public File getConfigurationDirectory() {
		return getChildDirectory(CONF_DIR);
	}

	@Override
	public File getReportsDirectory() {
		return getChildDirectory(REPORTS_DIR);
	}

	@Override
	public File getTempDirectory() {

		// get java.io.tmpdirfrom JVM
		final String ioTempDirectory = systemUtils.getSystemProperty(JAVA_IO_TMPDIR, systemProperties);

		// if OS isn't windows then exit
		if (!systemUtils.isWindowsOperatingSystem(systemProperties)) {
			return new File(ioTempDirectory);
		}

		// if OS is windows then convert to long file names
		try {
			// convert
			return fileUtils.convertToLongWindowsPath(ioTempDirectory);

		} catch (ConversionFailedException e) {

			// log error
			Object[] args = { e.toString() };
			String message = messageProvider.getMessage("drdp.win_tempdir_conversion_failed", args);
			logger.error(message);

			// return unconverted path
			return new File(ioTempDirectory);
		}
	}

	@Override
	public File resolveModelPath(String path, ModuleInfo info) {
		notNull(path, "path is undefined");
		notEmpty(path, "path is empty");
		notNull(info, "info is undefined");

		// resolve path if it starts with 'modulepath:'
		if (startsWithModulePathPrefix(path)) {
			String pathWithoutPrefix = path.substring(MODULEPATH.length());
			File resolvedFile = new File(info.getDirectory(), pathWithoutPrefix);
			return resolvedFile;

		} else {
			// resolve path as file object
			return new File(path);
		}
	}

	@Override
	public File resolveModelPath(String path, ExecutionResult result) {
		notNull(path, "path is undefined");
		notEmpty(path, "path is empty");
		notNull(result, "result is undefined");

		// exit if prefix isn't present
		if (!startsWithModulePathPrefix(path))
			return new File(path);

		// get module info
		ExecutionInfo executionInfo = coreExecutionInfoProvider.get(result);
		ModuleInfo moduleInfo = executionInfo.getModuleInfo();

		// resolve path
		return resolveModelPath(path, moduleInfo);
	}

	@Override
	public boolean startsWithModulePathPrefix(String path) {
		return path.startsWith(MODULEPATH);
	}

	@Override
	public File getCredentialProviderMasterPasswordFile() {

		// is password file defined in system properties
		if (systemUtils.isPineappleCredentialProviderPasswordHomeDefined(systemProperties)) {

			// get password file defined in system properties
			String passwordFile = systemUtils.getSystemProperty(PINEAPPLE_CREDENTIALPROVIDER_PASSWORD_FILE,
					systemProperties);
			return new File(passwordFile);
		}

		// return default file
		return new File(getConfigurationDirectory(), CRDENTIALPROVIDER_PASSWORD_FILE);
	}

}
