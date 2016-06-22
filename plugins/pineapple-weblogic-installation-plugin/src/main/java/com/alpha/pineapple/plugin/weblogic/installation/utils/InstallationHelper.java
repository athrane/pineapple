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


package com.alpha.pineapple.plugin.weblogic.installation.utils;

import java.io.File;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.plugin.weblogic.installation.model.Mapper;
import com.alpha.pineapple.plugin.weblogic.installation.model.WeblogicInstallation;
import com.alpha.pineapple.session.ProcessExecutionSession;

/**
 * Common installation functionality for installation of WebLogic. 
 */
public interface InstallationHelper {

	/**
	 * Returns true if either a Release 9.x or Release 10.x model is defined.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return true if either a WLS9 or WLS10 model is defined.
	 */
	public boolean isModelContentDefined(WeblogicInstallation model);
		
    /**
	 * Asserts whether the installer path points to a valid file. 
	 * 
	 * The result of the of the assertion is added as an execution result to
	 * current execution result object graph. 
	 * 
	 * @param installer Path to the installer.
	 * @param result ExecutionResult result where the assertion result is added
	 * as a child result.
	 * 
	 * @return True if the installer exists as an file.
	 */
	public boolean isInstallerValid(File installer, ExecutionResult result );

	/**
	 * Create log file for silent installation. the log file will
	 * be located in the temporary directory used by Pineapple
	 *
	 * @param logFileName name of the log file.
	 * 
	 * @return File object which represents silent log file
	 * located in the temporary directory used by Pineapple
	 */	
	public File createSilentLogFile(String logFileName);
		
	/**
	 * Returns true if WebLogic is already installed. 
	 * 
	 * @param session Plugin session.
	 * @param result Execution result.
	 * @param targetDirectory  target installation directory.
	 * 
	 * @return true if the WebLogic is installed.
	 */
	public boolean isProductInstalled(ProcessExecutionSession session, ExecutionResult result, File targetDirectory );
	
	/**
	 * Resolve module path. 
	 * 
	 * @param result Execution result used to look up execution info.
	 * 
	 * @param installer Unresolved installer path.
	 *  
	 * @return Resolved installer path where 'modulepath' prefix is resolved to
	 * physical module directory.
	 */
	public String resolveModulePath(ExecutionResult result, String installer);

	/**
	 * return true if release 12.1.2 is defined.
	 * 
	 * @param model installation model.
	 * 
	 * @return true if release 12.1.2 is defined.
	 */
	public boolean isRelease1212ModelContentDefined(WeblogicInstallation model);	
}
