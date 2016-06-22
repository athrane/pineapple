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
 * Product response file builder for WebLogic 12.1.2 for Oracle universal installer.
 * 
 * http://docs.oracle.com/middleware/1212/core/OUIRF/response_file.htm#A1099047
 */
public class Release1212ResponseFileBuilder implements UniversalInstallerResponseFileBuilder {

	/**
     * System independent newline character.
     */	
	public static final String NEWLINE_CHAR = System.getProperty("line.separator");	
		
	public Collection<String> getResponseForInstallation(File oracleHomeDirectory, File localJvm) {			
		Collection<String> lines = new ArrayList<String>();
		lines.add("[ENGINE]");
		lines.add(NEWLINE_CHAR);				
		lines.add("#DO NOT CHANGE THIS.");
		lines.add("Response File Version=1.0.0.0.0");		
		lines.add(NEWLINE_CHAR);				
		lines.add("[GENERIC]");				
		lines.add(NEWLINE_CHAR);
		lines.add("#The oracle home location. This can be an existing Oracle Home or a new Oracle Home.");				
		lines.add("#The plugin sets this as the parent directory of the WebLogic installation directory.");
		lines.add("ORACLE_HOME="+oracleHomeDirectory.getAbsolutePath());				
		lines.add(NEWLINE_CHAR);
		lines.add("#Set this variable value to the Installation Type selected. e.g. WebLogic Server, Coherence, Complete with Examples.");
		lines.add("INSTALL_TYPE=WebLogic Server");
		lines.add(NEWLINE_CHAR);
		lines.add("#Provide the My Oracle Support Username. If you wish to ignore Oracle Configuration Manager configuration provide empty string for user name.");
		lines.add("MYORACLESUPPORT_USERNAME=");
		lines.add(NEWLINE_CHAR);
		lines.add("#Provide the My Oracle Support Password");
		lines.add("MYORACLESUPPORT_PASSWORD=");
		lines.add(NEWLINE_CHAR);
		lines.add("#Set this to true if you wish to decline the security updates. Setting this to true and providing empty string for My Oracle Support username will ignore the Oracle Configuration Manager configuration");
		lines.add("DECLINE_SECURITY_UPDATES=true");
		lines.add(NEWLINE_CHAR);
		lines.add("#Set this to true if My Oracle Support Password is specified");
		lines.add("SECURITY_UPDATES_VIA_MYORACLESUPPORT=false");
		lines.add(NEWLINE_CHAR);
		lines.add("#Provide the Proxy Host");
		lines.add("#PROXY_HOST=");
		lines.add(NEWLINE_CHAR);
		lines.add("#Provide the Proxy Port");
		lines.add("#PROXY_PORT=");		
		lines.add(NEWLINE_CHAR);
		lines.add("#Provide the Proxy Username");
		lines.add("#PROXY_USER=");		
		lines.add(NEWLINE_CHAR);
		lines.add("#Provide the Proxy Password");
		lines.add("#PROXY_PWD=");		
		lines.add(NEWLINE_CHAR);
		lines.add("#Type String (URL format) Indicates the OCM Repeater URL which should be of the format [scheme[Http/Https]]://[repeater host]:[repeater port]");
		lines.add("#COLLECTOR_SUPPORTHUB_URL=");				
		return lines;
	}

	@Override
	public Collection<String> getResponseForUninstallation(File oracleHomeDirectory) {
		Collection<String> lines = new ArrayList<String>();
		lines.add("[ENGINE]");
		lines.add(NEWLINE_CHAR);				
		lines.add("#DO NOT CHANGE THIS.");
		lines.add("Response File Version=1.0.0.0.0");		
		lines.add(NEWLINE_CHAR);				
		lines.add("[GENERIC]");				
		lines.add(NEWLINE_CHAR);
		lines.add("#This will be blank when there is nothing to be de-installed in distribution level");				
		lines.add("SELECTED_DISTRIBUTION=WebLogic Server~12.1.2.0.0");
		lines.add(NEWLINE_CHAR);
		lines.add("#The oracle home location. This can be an existing Oracle Home or a new Oracle Home");				
		lines.add("ORACLE_HOME="+oracleHomeDirectory.getAbsolutePath());				
		return lines;
	}
	
	
}
