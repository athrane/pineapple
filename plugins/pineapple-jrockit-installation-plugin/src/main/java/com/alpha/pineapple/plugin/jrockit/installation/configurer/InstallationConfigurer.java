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


package com.alpha.pineapple.plugin.jrockit.installation.configurer;

import java.io.File;
import java.io.IOException;

import com.alpha.pineapple.plugin.jrockit.installation.model.JRockitInstallation;

/**
 * Strategy of installation of a particular installer release.
 */
public interface InstallationConfigurer {

	/**
	 * Create silent XML file for installer.
	 * 
	 * @param model Plugn model.
	 * 
	 * @return File object which represents the silent XML file. 
	 * 
	 * @throws IOException if file creation fails.
	 */
	File createSilentXmlFile(JRockitInstallation model);	
	
	/**
	 * Create argument list for installer.
	 * 
	 * @param silentXmlFile Silent XML file.
	 * @param silentLogFile Silent log file.
	 * 
	 * @return argument list for installer.
	 */
	String[] createArgumentList(File silentXmlFile, File silentLogFile);

	/**
	 * Create argument list for uninstaller.
	 * 
	 * @param silentLogFile Silent log file.
	 * 
	 * @return argument list for uninstaller.
	 */	
	String[] createUninstallationArgumentList(File silentLogFile);			
	
	/**
	 * Return installer path.
	 * 
	 * @param model Plugn model.
	 * 
	 * @return installer path.
	 */
	String getInstaller(JRockitInstallation model);

	/**
	 * Return uninstaller path.
	 * 
	 * @param model Plugn model.
	 * 
	 * @return installer path.
	 */
	String getUninstaller(JRockitInstallation model);
	
	/**
	 * Return true if creation of silent XML was successful.
	 * 
	 * @return true if creation of silent XML was successful.
	 */
	boolean isSilentXmlCreationSuccessful();

	/**
	 * Returns path to installed JVM.
	 * 
	 * @param model Plugn model.
	 * 
	 * @return path to installed JVM.
	 */
	File getJvmExecutablePath(JRockitInstallation model);

	/**
	 * Return version info.
	 *  
	 * @return version info.
	 */
	String getVersion();
	
}
