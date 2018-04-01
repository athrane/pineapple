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

package com.alpha.pineapple.plugin.filesystem.session;

import org.apache.commons.vfs2.FileObject;

import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionException;

/**
 * Session which provides access to a virtual filesystem.
 */
public interface FileSystemSession extends Session {
	/**
	 * Resolves file object in the remote virtual file system.
	 * 
	 * @param path
	 *            Path to the file/directory in the remote virtual file system.
	 * 
	 * @return Resolved VFS file object.
	 * @throws SessionException
	 *             If file resolution fails.
	 */
	FileObject resolveFile(String path) throws SessionException;
}
