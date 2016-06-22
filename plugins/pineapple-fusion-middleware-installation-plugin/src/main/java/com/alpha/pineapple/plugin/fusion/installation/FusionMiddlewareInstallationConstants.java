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


package com.alpha.pineapple.plugin.fusion.installation;

import com.alpha.pineapple.plugin.fusion.installation.model.FusionMiddlewareInstallation;
import com.alpha.pineapple.plugin.fusion.installation.operation.DeployConfiguration;
import com.alpha.pineapple.plugin.fusion.installation.operation.UndeployConfiguration;

/**
 * Fusion Middleware installation constants.
 */
public interface FusionMiddlewareInstallationConstants {
	
	/**
	 * Legal content types supported by plugin operations.
	 */
	public static final Class<?>[] LEGAL_CONTENT_TYPES = {FusionMiddlewareInstallation.class };
	
	/**
	 * Default process time out (ms).
	 */
	public static final int DEFAULT_PROCESS_TIMEOUT = 1000*60*10;

	/**
	 * Response file for installation. 
	 */
	static final String RESPONSE_INSTALL_FILE = "com.alpha.pineapple.plugin.fusion.installation.install.rsp";

	/**
	 * Response file for uninstallation. 
	 */
	static final String RESPONSE_UNINSTALL_FILE = "com.alpha.pineapple.plugin.fusion.installation.uninstall.rsp";
	
	/**
	 * Silent log file name for installation. 
	 */
	public static final String SILENT_INSTALL_LOG = DeployConfiguration.class.getCanonicalName()+ ".log";

	/**
	 * Oracle Common home directory name. 
	 */
    static final String ORACLE_COMMON_HOME_DIR = "oracle_common";
	
}
