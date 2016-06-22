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
 */

package com.alpha.pineapple.plugin.weblogic.installation.response;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Product response file builder for WebLogic 9.x 
 */
public class Release9ResponseFileBuilder implements ResponseFileBuilder {

	/**
     * System independent newline character.
     */	
	public static final String NEWLINE_CHAR = System.getProperty("line.separator");	
		
	public Collection<String> getResponseForInstallation(File targetDirectory, File localJvm) {
		
		Collection<String> lines = new ArrayList<String>();		
		lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		lines.add(NEWLINE_CHAR);						
		lines.add("<bea-installer>");
		lines.add(NEWLINE_CHAR);								
		lines.add("  <input-fields>");
		lines.add(NEWLINE_CHAR);								
		lines.add("     <data-value name=\"USER_INSTALL_DIR\" value=\""+targetDirectory+"\" />");		
		lines.add(NEWLINE_CHAR);								
		lines.add("  </input-fields>");
		lines.add(NEWLINE_CHAR);								
		lines.add("</bea-installer>");		
		lines.add(NEWLINE_CHAR);								
		return lines;
	}
	
}
