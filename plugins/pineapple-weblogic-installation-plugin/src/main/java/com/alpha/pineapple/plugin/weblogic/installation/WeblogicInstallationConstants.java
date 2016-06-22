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


package com.alpha.pineapple.plugin.weblogic.installation;

import com.alpha.pineapple.plugin.weblogic.installation.model.WeblogicInstallation;
import com.alpha.pineapple.plugin.weblogic.installation.operation.DeployConfiguration;
import com.alpha.pineapple.plugin.weblogic.installation.operation.UndeployConfiguration;

/**
 * WebLogic installation constants.
 */
public interface WeblogicInstallationConstants {
	
	/**
	 * Legal content types supported by plugin operations.
	 */
	public static final Class<?>[] LEGAL_CONTENT_TYPES = {WeblogicInstallation.class };
	
	/**
	 * Default process time out (ms).
	 */
	public static final int DEFAULT_PROCESS_TIMEOUT = 1000*60*10;

	/**
	 * Silent XML file name for installation. 
	 */
	static final String SILENT_INSTALL_XML = DeployConfiguration.class.getCanonicalName()+ ".xml";

	/**
	 * Silent log file name for installation. 
	 */
	public static final String SILENT_INSTALL_LOG = DeployConfiguration.class.getCanonicalName()+ ".log";

	/**
	 * Silent XML file name for uninstallation. 
	 */
	public static final String SILENT_UNINSTALL_XML = UndeployConfiguration.class.getCanonicalName()+ ".xml";

	/**
	 * Silent log file name for uninstallation. 
	 */
	public static final String SILENT_UNINSTALL_LOG = UndeployConfiguration.class.getCanonicalName()+ ".log";

	/**
	 * Response file for installation. 
	 */
	static final String RESPONSE_INSTALL_FILE = DeployConfiguration.class.getCanonicalName()+".rsp";

	/**
	 * Response file for uninstallation. 
	 */
	static final String RESPONSE_UNINSTALL_FILE = UndeployConfiguration.class.getCanonicalName()+".rsp";
	
}
