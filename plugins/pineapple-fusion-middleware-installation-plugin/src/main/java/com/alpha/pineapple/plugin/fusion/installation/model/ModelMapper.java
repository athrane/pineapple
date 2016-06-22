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


package com.alpha.pineapple.plugin.fusion.installation.model;

import java.io.File;

/**
 * Strategy of installation of a particular installer release.
 */
public interface ModelMapper {
	
	/**
	 * Create argument list for installer.
	 * 
	 * @param responseFile Response file.
	 * @param jreLocation JRE location. 
	 * 
	 * @return argument list for installer.
	 */
	String[] createArgumentList(File responseFile, File jreLocation);

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
	String getInstaller(FusionMiddlewareInstallation model);

	/**
	 * Return uninstaller path.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return installer path.
	 */
	String getUninstaller(FusionMiddlewareInstallation model);
	
	/**
	 * Returns path to installed product.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return path to installed product.
	 */
	File getTargetDirectory(FusionMiddlewareInstallation model);

	/**
	 * Returns path to local JVM.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return path to local JVM.
	 */
	File getLocalJvm(FusionMiddlewareInstallation model);

}
