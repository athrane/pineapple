/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2013 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package com.alpha.pineapple.plugin.weblogic.installation.response;

import java.io.File;
import java.util.Collection;

public interface UniversalInstallerResponseFileBuilder {

	/**
	 * Create response file for installation of product.
	 * 
	 * @param oacleHomeDirectory Oracle home directory. The parent directory 
	 * of the WebLogic target directory.
	 * @param localJvm Local JVM directory..
	 * 
	 * @return response file for product.
	 */
	public Collection<String> getResponseForInstallation(File oacleHomeDirectory, File localJvm);
	
	/**
	 * Create response file for uninstallation of product.
	 * 
	 * @param oracleHomeDirectory Oracle home directory. The parent directory 
	 * of the WebLogic target directory.
	 * 
	 * @return response file for product.
	 */
	public Collection<String> getResponseForUninstallation(File oracleHomeDirectory); 
	
}
