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


package com.alpha.pineapple.plugin.fusion.installation.utils;

import java.io.File;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.plugin.fusion.installation.model.FusionMiddlewareInstallation;
import com.alpha.pineapple.plugin.fusion.installation.model.ModelMapper;

/**
 * Common installation functionality for installation of WebLogic. 
 */
public interface InstallationHelper {

	/**
	 * Returns true if product is already installed. 
	 * 
	 * @param model Plugin model.
	 * @param mapper Installation mapper.
	 * @param result ExecutionResult result where the assertion result is added.
	 * as a child result. 
	 * 
	 * @return true if the product is installed.
	 */
	public boolean isProductInstalled( FusionMiddlewareInstallation model, ModelMapper mapper, ExecutionResult result );	
	
	/**
	 * Returns true if a Application Development Runtime model is defined.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return true if a Application Development Runtime model is defined.
	 */
	public boolean isAppDevRuntimeModelContentDefined(FusionMiddlewareInstallation model);
	
	/**
	 * Returns true if a WebCenter model is defined.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return true if a WebCenter model is defined.
	 */
	public boolean isWebCenterModelContentDefined(FusionMiddlewareInstallation model);

	/**
	 * Returns true if a SOA Suite model is defined.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return true if a SOA Suite model is defined.
	 */
	public boolean isSoaSuiteModelContentDefined(FusionMiddlewareInstallation model);

	/**
	 * Returns true if a Service Bus model is defined.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return true if a Service Bus model is defined.
	 */
	public boolean isServiceBusModelContentDefined(FusionMiddlewareInstallation model);
	
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
	 * Delete installation log files in the Oracle Universal Installer (OUI) inventory log directory.
	 * 
	 * The OUI stores installation/uninstallation log files in
	 * <code>C:&#047;Program Files&#047;Oracle&#047;Inventory&#047;Logs<code> on Windows computers 
	 * and in the <code>ORACLE_HOME&#047;/Inventory&#047;Logs<code> on UNIX computers.
 	 *   
	 * @param logDir Log directory. 
	 * @param result ExecutionResult result where the deletion result is added.
	 */
	public void deleteInstallationLogFiles(File logDir, ExecutionResult result);	

	/**
	 * Attach installation log files in the Oracle Universal Installer inventory log directory.
	 * 
	 * The OUI stores installation/uninstallation log files in
	 * <code>C:&#047;Program Files&#047;Oracle&#047;Inventory&#047;Logs<code> on Windows computers 
	 * and in the <code>ORACLE_HOME&#047;/Inventory&#047;Logs<code> on UNIX computers. 
	 *  
	 * @param logDir Log directory. 
	 * @param result ExecutionResult result where the logs are attached to.
	 */
	public void attachInstallationLogFiles(File logDir, ExecutionResult result);
	
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

}
