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

/**
 * Strategy of installation of a WebLogic using the Oracle Universal Installer.
 */
public interface UniversalInstallerMapper {
	
	/**
	 * Create argument list for archive based installer (e.g. jar).
	 * 
	 * @param installerArchive installer archive file.
	 * @param responseFile Response file.
	 * @param jreLocation JRE location. 
	 * 
	 * @return argument list for installer.
	 */
	String[] createArgumentListForArchive(String installerArchive, File responseFile, File jreLocation);

	/**
	 * Create argument list for uninstaller.
	 * 
	 * @param responseFile Response file.
	 * @param jreLocation JRE location. 
	 * 
	 * @return argument list for installer.
	 */
	String[] createUninstallationArgumentList(File responseFile, File jreLocation);			
	
	/**
	 * Return installer path.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return installer path.
	 */
	String getInstaller(WeblogicInstallation model);

	/**
	 * Return uninstaller path.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return installer path.
	 */
	String getUninstaller(WeblogicInstallation model);
	
	/**
	 * Returns path to installed product.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return path to installed product.
	 */
	File getTargetDirectory(WeblogicInstallation model);

	/**
	 * Returns path to local JVM.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return path to local JVM.
	 */
	File getLocalJvm(WeblogicInstallation model);

}
