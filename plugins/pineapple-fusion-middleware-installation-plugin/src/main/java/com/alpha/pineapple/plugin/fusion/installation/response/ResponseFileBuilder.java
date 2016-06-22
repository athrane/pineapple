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

package com.alpha.pineapple.plugin.fusion.installation.response;

import java.io.File;
import java.util.Collection;

public interface ResponseFileBuilder {

	/**
	 * Create response file for installation of product.
	 * 
	 * @param commonHomeDirectory Oracle Common home directory.
	 * @param middlewareHomeDirectory Middleware home directory.
	 * 
	 * @return response file for product.
	 */
	public Collection<String> getResponseForInstallation(File  commonHomeDirectory, File middlewareHomeDirectory); 

	/**
	 * Create response file for uninstallation of product.
	 * 
	 * @param commonHomeDirectory Oracle Common home directory.
	 * @param middlewareHomeDirectory Middleware home directory.
	 * 
	 * @return response file for product.
	 */
	public Collection<String> getResponseForUninstallation(); 
	
}
