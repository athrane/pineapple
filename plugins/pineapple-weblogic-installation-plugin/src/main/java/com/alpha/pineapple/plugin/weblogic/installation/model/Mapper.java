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


package com.alpha.pineapple.plugin.weblogic.installation.model;

import java.io.File;

import org.apache.commons.chain.Command;

/**
 * Strategy of installation of a particular installer release.
 */
public interface Mapper {
	
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
	String getInstaller(WeblogicInstallation model);

	/**
	 * Return uninstaller path.
	 * 
	 * @param model Plugn model.
	 * 
	 * @return installer path.
	 */
	String getUninstaller(WeblogicInstallation model);
	
	/**
	 * Returns path to installed JVM.
	 * 
	 * @param model Plugn model.
	 * 
	 * @return path to installed JVM.
	 */
	File getTargetDirectory(WeblogicInstallation model);
	
	/**
	 * Get local JVM path. 
	 * 
	 * @return path to local JVM.
	 */
	File getLocalJvm(WeblogicInstallation model);

	/**
	 * TODO: document
	 * 
	 * @param installerArchive
	 * @param silentXmlFile
	 * @param silentLogFile
	 * @return
	 */
	String[] createArgumentListForArchive(String installerArchive, File silentXmlFile, File silentLogFile);

	/**
	 * Returns command which can create installation response file.
	 *  
	 * @return command which can create installation response file.
	 */
	Command getCreateInstallReponseCommand();
}
