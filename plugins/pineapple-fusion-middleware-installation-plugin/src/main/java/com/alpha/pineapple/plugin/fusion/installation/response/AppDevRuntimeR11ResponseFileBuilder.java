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

package com.alpha.pineapple.plugin.fusion.installation.response;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Product response file builder for Application Development Runtime. 
 */
public class AppDevRuntimeR11ResponseFileBuilder implements ResponseFileBuilder {

	/**
     * System independent newline character.
     */	
	public static final String NEWLINE_CHAR = System.getProperty("line.separator");	
	
	
	public Collection<String> getResponseForInstallation(
			File commonHomeDirectory, File middlewareHomeDirectory) {
		Collection<String> lines = new ArrayList<String>();
		lines.add("[ENGINE]");
		lines.add(NEWLINE_CHAR);				
		lines.add("#DO NOT CHANGE THIS.");
		lines.add("Response File Version=1.0.0.0.0");		
		lines.add(NEWLINE_CHAR);				
		lines.add("[GENERIC]");				
		lines.add(NEWLINE_CHAR);		
		lines.add("#Set this to true if you wish to specify a directory where latest updates are downloaded.");				
		lines.add(NEWLINE_CHAR);
		lines.add("SPECIFY_DOWNLOAD_LOCATION=false");				
		lines.add(NEWLINE_CHAR);				
		lines.add("#Provide the Oracle Home location. The location has to be the immediate child under the specified Middleware Home location.");		
		lines.add("ORACLE_HOME="+commonHomeDirectory.getAbsolutePath());				
		lines.add(NEWLINE_CHAR);
		lines.add("#Provide existing Middleware Home location.");		
		lines.add("MIDDLEWARE_HOME="+middlewareHomeDirectory.getAbsolutePath());
		lines.add(NEWLINE_CHAR);		
		lines.add("[SYSTEM]");		
		lines.add(NEWLINE_CHAR);		
		lines.add("[APPLICATIONS]");		
		lines.add(NEWLINE_CHAR);		
		lines.add("[RELATIONSHIPS]");		
		lines.add(NEWLINE_CHAR);				
		return lines;
	}

	public Collection<String> getResponseForUninstallation() {
		Collection<String> lines = new ArrayList<String>();		
		lines.add("[ENGINE]");
		lines.add(NEWLINE_CHAR);				
		lines.add("#DO NOT CHANGE THIS.");
		lines.add("Response File Version=1.0.0.0.0");		
		lines.add(NEWLINE_CHAR);				
		lines.add("[GENERIC]");				
		lines.add(NEWLINE_CHAR);	
		lines.add("#Identifies the if the Instance deinstallation is valid or not");	
		lines.add("DEINSTALL_IN_ASINSTANCE_MODE=false");			
		lines.add(NEWLINE_CHAR);			
		lines.add("[SYSTEM]");		
		lines.add(NEWLINE_CHAR);		
		lines.add("[APPLICATIONS]");		
		lines.add(NEWLINE_CHAR);		
		lines.add("[RELATIONSHIPS]");		
		lines.add(NEWLINE_CHAR);				
		return lines;		
	}

	
}
