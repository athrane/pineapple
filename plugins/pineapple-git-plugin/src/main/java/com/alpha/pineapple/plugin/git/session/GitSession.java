/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2019 Allan Thrane Andersen..
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

package com.alpha.pineapple.plugin.git.session;

import java.io.File;

import com.alpha.pineapple.session.Session;

/**
 * Session which provides access to a Git session.
 */
public interface GitSession extends Session {

	/**
	 * Clone remote repository
	 * 
	 * @param uri    repository URI.
	 * @param branch repository branch.
	 * @param dest   local destination for repository
	 * 
	 * @throws Exception if clone fails
	 */
	void cloneRepository(String uri, String branch, File dest) throws Exception;

}
