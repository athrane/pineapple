/*******************************************************************************
* Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2016 Allan Thrane Andersen..
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

package com.alpha.pineapple.web.model;

import java.io.File;

import javax.annotation.Resource;

import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;

public class SystemInfoModel {

	/**
	 * Runtime directory resolver.
	 */
	@Resource
	RuntimeDirectoryProvider runtimeDirectoryResolver;

	/**
	 * Get Pineapple home directory.
	 * 
	 * @return Pineapple home directory.
	 */
	public String getHomeDirectory() {
		if (runtimeDirectoryResolver == null)
			return "n/a";
		File dir = runtimeDirectoryResolver.getHomeDirectory();
		if (dir == null)
			return "n/a";
		return dir.toString();
	}

	/**
	 * Get Pineapple modules directory.
	 * 
	 * @return Pineapple modules directory.
	 */
	public String getModulesDirectory() {
		if (runtimeDirectoryResolver == null)
			return "n/a";
		File dir = runtimeDirectoryResolver.getModulesDirectory();
		if (dir == null)
			return "n/a";
		return dir.toString();
	}

	/**
	 * Get Pineapple reports directory.
	 * 
	 * @return Pineapple reports directory.
	 */
	public String getReportsDirectory() {
		if (runtimeDirectoryResolver == null)
			return "n/a";
		File dir = runtimeDirectoryResolver.getReportsDirectory();
		if (dir == null)
			return "n/a";
		return dir.toString();
	}

	/**
	 * Get Pineapple temp. directory.
	 * 
	 * @return Pineapple temp. directory.
	 */
	public String getTempDirectory() {
		if (runtimeDirectoryResolver == null)
			return "n/a";
		File dir = runtimeDirectoryResolver.getTempDirectory();
		if (dir == null)
			return "n/a";
		return dir.toString();
	}

}
