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
import static com.alpha.pineapple.CoreConstants.CONF_DIR;
import static com.alpha.pineapple.CoreConstants.CRDENTIALPROVIDER_PASSWORD_FILE;
import static com.alpha.pineapple.CoreConstants.MODULEPATH;
import static com.alpha.pineapple.CoreConstants.MODULEROOT;
import static com.alpha.pineapple.CoreConstants.MODULES_DIR;
import static com.alpha.pineapple.CoreConstants.PINEAPPLE_DIR;
import static com.alpha.pineapple.CoreConstants.REPORTS_DIR;
import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.File;
import java.util.Properties;

import javax.annotation.Resource;

import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.module.ModuleInfo;

/**
 * Implementation of the <code>RuntimeDirectoryProvider</code> interface which
 * is used to resolve the default resolve runtime directories where Pineapple
 * will run from.
 * 
 * If the system property <code>pineapple.home.dir</code> is set then the home
 * directory is resolved to this directory.
 * 
 * Otherwise the runtime root directory is resolved to value of the system
 * property <code>&#036;&#123;user.home&#125;&#092;.pineapple</code>
 */
public class DefaultRuntimeDirectoryProviderImpl implements RuntimeDirectoryProvider {

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
		final String ioTempDirectory = systemUtils.getSystemProperty(JAVA_IO_TMPDIR, systemProperties);
		return new File(ioTempDirectory);
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
		}

		// resolve path if it starts with 'moduleroot:'
		if (startsWithModuleRootPrefix(path)) {
			String pathWithoutPrefix = path.substring(MODULEROOT.length());
			File resolvedFile = new File(getModulesDirectory(), pathWithoutPrefix);
			return resolvedFile;
		}

		// resolve path as file object
		return new File(path);
	}

	@Override
	public File resolveModelPath(String path, ExecutionResult result) {
		notNull(path, "path is undefined");
		notEmpty(path, "path is empty");
		notNull(result, "result is undefined");

		// get module info
		ExecutionInfo executionInfo = coreExecutionInfoProvider.get(result);
		ModuleInfo moduleInfo = executionInfo.getModuleInfo();

		// resolve path
		return resolveModelPath(path, moduleInfo);
	}

	@Override
	public boolean startsWithModulePathPrefix(String path) {
		notNull(path, "path is undefined");
		notEmpty(path, "path is empty");
		return path.startsWith(MODULEPATH);
	}

	@Override
	public boolean startsWithModuleRootPrefix(String path) {
		notNull(path, "path is undefined");
		notEmpty(path, "path is empty");
		return path.startsWith(MODULEROOT);
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
