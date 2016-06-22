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


package com.alpha.pineapple.plugin.weblogic.deployment.argument;

import java.io.File;

/**
 * Locator for deployable files.
 */
public interface FileLocator
{
    /**
     * Locate a file set based on file suffix.
     * 
     * @param suffix file suffix to search for, e.g. "xml".
     * @param searchDirectory The directory where the search 
     * should be conducted.
     * 
     * @return Array of found file names.
     */
    String[] locateFileSet( String suffix, File searchDirectory );
    
    /**
     * Locate a set of sub directories.
     * 
     * @param searchDirectory The directory where the search 
     * should be conducted.
     * 
     * @return Array of found sub directory names.
     */    
    String[] locateSubDirectories( File searchDirectory );
    
    /**
     * Validate whether file exists in directory.
     * 
     * @param searchDirectory The directory where the file should be located.
     * @param fileName File name to search for.
     * 
     * @return True if file exists.
     */    
    boolean fileExists( File searchDirectory, String fileName );
    
}
