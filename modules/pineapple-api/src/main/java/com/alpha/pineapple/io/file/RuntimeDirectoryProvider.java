/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
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

import java.io.File;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.module.ModuleInfo;

/**
 * Runtime directory provider which resolves the runtime directories where
 * Pineapple reads and writes its files to.
 */
public interface RuntimeDirectoryProvider {

	/**
	 * Get the absolute home directory where Pineapple is running from.
	 * 
	 * The default value for this directory is
	 * <code>&#036;&#123;user.home&#125;&#047;.pineapple<code> where
	 * <code>&#036;&#123;user.home&#125;<code> is the value of the system property.
	 * 
	 * @return the absolute home directory where Pineapple is running from.
	 */
	File getHomeDirectory();

	/**
	 * Get a runtime child directory local to the home directory where Pineapple is
	 * running from.
	 * 
	 * The default value for this directory is
	 * <code>&#036;&#123;user.home&#125;&#047;.pineapple&#047;&#036;&#123;child.
	 * dir&#125;<code> where <code>&#036;&#123;user.home&#125;<code> is the value of
	 * the system property and <code>&#036;&#123;child.dir&#125; <code> is the value
	 * of the <code>childDirectory<code> child argument.
	 * 
	 * @return a runtime child directory local to the runtime root directory where
	 *         Pineapple is running from.
	 */
	File getChildDirectory(String childDirectory);

	/**
	 * Get the modules directory where Pineapple should look for modules.
	 * 
	 * The default value for this directory is
	 * <code>&#036;&#123;user.home&#125;&#047;.pineapple&#047;modules&#125; <code>
	 * where <code>&#036;&#123;user.home&#125;<code> is the value of the system
	 * property
	 * 
	 * @return the modules directory where Pineapple should look for modules.
	 */
	File getModulesDirectory();

	/**
	 * Get the configuration directory where Pineapple should look for its
	 * configuration.
	 * 
	 * The default value for this directory is
	 * <code>&#036;&#123;user.home&#125;&#047;.pineapple&#047;conf&#125; <code>
	 * where <code>&#036;&#123;user.home&#125;<code> is the value of the system
	 * property
	 * 
	 * @return the configuration directory where Pineapple should look for its
	 *         configuration.
	 */
	File getConfigurationDirectory();

	/**
	 * Get the configuration directory where Pineapple should store reports.
	 * 
	 * The default value for this directory is
	 * <code>&#036;&#123;user.home&#125;&#047;.pineapple&#047;reports&#125; <code>
	 * where <code>&#036;&#123;user.home&#125;<code> is the value of the system
	 * property
	 * 
	 * @return the configuration directory where Pineapple should store reports.
	 */
	File getReportsDirectory();

	/**
	 * Get the absolute runtime temporary directory where Pineapple is running from.
	 * 
	 * The default value for this directory is
	 * <code>&#036;&#123;java.io.tmpdir&#125;<code> where
	 * <code>&#036;&#123;java.io.tmpdir&#125;<code> is the value of the system
	 * property
	 * 
	 * @return the configuration directory where Pineapple should store reports.
	 */
	File getTempDirectory();

	/**
	 * Resolve model path to the physical path where the module is located on disk,
	 * e.g. the module directory. A model element path is a path definition which
	 * occurs in a module model which starts with the prefix 'modulepath:'.
	 * 
	 * If the model path dosn't start with the 'modulepath:' prefix then the path is
	 * resolved from a string into a File object.
	 * 
	 * @param path
	 *            Model element path.
	 * @param info
	 *            Module info object used to resolve the path.
	 * 
	 * @return resolved model element path.
	 */
	File resolveModelPath(String path, ModuleInfo info);

	/**
	 * Resolve model path to the physical path where the module is located on disk,
	 * e.g. the module directory. A model element path is a path definition which
	 * occurs in a module model which starts with the prefix 'modulepath:'.
	 * 
	 * @param path
	 *            Model element path.
	 * @param result
	 *            Execution result used to look up execution info.
	 * 
	 * @return Resolved local file path where 'modulepath' prefix is resolved to
	 *         physical module directory.
	 */
	File resolveModelPath(String path, ExecutionResult result);

	/**
	 * Returns true if the model path starts with the 'modulepath:' prefix.
	 * 
	 * @param path
	 *            Model path.
	 * 
	 * @return true if the model path starts with the 'modulepath:' prefix.
	 */
	boolean startsWithModulePathPrefix(String path);

	/**
	 * Returns true if the model path starts with the 'modules:' prefix.
	 * 
	 * @param path
	 *            Model path.
	 * 
	 * @return true if the model path starts with the 'modules:' prefix.
	 */	
	boolean startsWithModulesPrefix(String path);
	
	
	/**
	 * Get the credential provider password file location where Pineapple should
	 * look for the file which contains the master password for symmetric encryption
	 * of credentials.
	 * 
	 * The default value for this location is
	 * <code>&#036;&#123;pineapple.home.dir&#125;&#047;conf&#047;
	 * credentialprovider.password&#125;<code> where
	 * <code>&#036;&#123;pineapple.home.dir&#125;<code> is the value of the system
	 * property
	 * 
	 * @return the file location where Pineapple should look for its password file
	 *         for symmetric encryption of credentials.
	 */
	public File getCredentialProviderMasterPasswordFile();

}
