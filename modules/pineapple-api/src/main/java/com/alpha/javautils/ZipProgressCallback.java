/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2019 Allan Thrane Andersen.
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

package com.alpha.javautils;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

/**
 * Callback interface used by the {@linkplain ZipUtils} class to signal progress
 * during operations.
 */
public interface ZipProgressCallback {

	/**
	 * Invoked when a new ZIP archive entry has been processed, e.g. either added or
	 * unzipped.
	 * 
	 * @return Archive entry processed.
	 */
	public void entryProcessed(ZipArchiveEntry entry);
}
