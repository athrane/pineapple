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
import java.util.Collection;

import org.eclipse.jgit.lib.Ref;

import com.alpha.pineapple.session.Session;

/**
 * Session which provides access to a Git session.
 */
public interface GitSession extends Session {

	/**
	 * Get repository name.
	 * 
	 * @return repository name.
	 * 
	 * @throws Exception if resolution od repository name fails.
	 */
	String getRepositoryName() throws Exception;

	/**
	 * Clone remote repository.
	 * 
	 * @param branch repository branch.
	 * @param dest   local destination for repository
	 * 
	 * @throws Exception if clone fails
	 */
	void cloneRepository(String branch, File dest) throws Exception;

	/**
	 * Execute Git ls-remote.
	 * 
	 * @return a map from names to references in the remote repository.
	 * 
	 * @throws Exception if ls-remote fails.
	 */
	Collection<Ref> lsRemote() throws Exception;

}
