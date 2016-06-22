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


package com.alpha.pineapple.plugin.weblogic.scriptingtool.utils;

import java.io.File;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.model.Wlst;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.pineapple.test.Asserter;

/**
 * Common installation functionality for execution of WLST script. 
 */
public interface ScriptHelper {

	/**
	 * Returns true if either a WLST model is defined.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return true if model is defined.
	 */
	public boolean isModelContentDefined(Wlst model);
			
    /**
	 * Asserts whether the script path points to a valid file. 
	 * 
	 * The result of the of the assertion is added as an execution result to
	 * current execution result object graph. 
	 * 
	 * @param script Path to the script.
	 * @param asserter Asserter object.
	 * 
	 * @return True if the script exists as an file.
	 */
	public boolean isScriptValid(String script, Asserter asserter);

    /**
	 * Asserts whether path points to a valid properties file. 
	 * 
	 * The result of the of the assertion is added as an execution result to
	 * current execution result object graph. 
	 * 
	 * @param script Path to the properties files.
	 * @param asserter Asserter object.
	 * 
	 * @return True if the file exists.
	 */	
	public boolean isPropertiesFileValid(String propertiesFileName, Asserter asserter);
	
    /**
	 * Asserts whether the path points to a valid WLST invoker script file. 
	 * 
	 * The result of the of the assertion is added as an execution result to
	 * current execution result object graph. 
	 * 
	 * @param script Path to the script.
	 * @param asserter Asserter object.
	 * 
	 * @return True if the script exists as an file.
	 */	
	public boolean isWlstInvokerValid(File wlstInvoker, Asserter asserter);	

    /**
	 * Asserts whether the path points to a valid home directory. 
	 * 
	 * The result of the of the assertion is added as an execution result to
	 * current execution result object graph. 
	 * 
	 * @param script Path to the home directory.
	 * @param asserter Asserter object.
	 * 
	 * @return True if the directory exists.
	 */		
	public boolean isProductHomeValid(File productHomeDir, Asserter asserter);
	
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
	 * Resolve module path. 
	 * 
	 * @param result Execution result used to look up execution info.
	 *  
	 * @return module path as absolute module directory.
	 */	
	public String getModulePath(ExecutionResult result );

	/**
	 * Returns true if properties file is defined in model.
	 *  
	 * @param model Plugin model.
	 * 
	 * @return true if properties file is defined in model.
	 */
	public boolean isPropertiesFileDefined(Wlst model);

	/**
	 * Get absolute path to OS specific WLST invoker script.
	 * 
	 * @param homeDir Product home directory.
	 * 
	 * @return Absolute path to WLST invocation script. If OS is windows
	 * then "HOME_DIR/common/bin/wlst.cmd" is returned. 
	 * Otherwise "HOME_DIR/common/bin/wlst.sh" is returned. 
	 */
	public File getWlstInvoker(File homeDirectory);

    /**
	 * Create temporary WLST invoker-invoker script,
	 * which invokes the provided WLST invoker script 
	 * (e.g. wlst.sh og wlst.cmd). 
	 * 
     * @param wlst Absolute path to the WLST invoker script 
	 * 
     * @param systemPropsArguments Argument list of system properties.
     * 
     * @throws Exception If script creation fails.
     */
    public File createWlstInvokerScript(File wlst, String[] systemPropsArguments) throws Exception;
	
    /**
     * Create argument list for WLST script execution.
     * 
     * @param script WLST script file name.
     * @param properties WLST properties file name.
     */	
	public String[] createArguments(String script, String properties);

    /**
     * Create argument list of system properties for WLST script execution.
     * 
     * @param useDemoTrust Define whether SSL Demo trust should be enabled.
     * @param useFastRandomOnLinux Define whether the fast secure random 
     * generator "/dev/./urandom" should be used on Linux. 
     * @param result execution result. 
     */	
	public String[] createSystemPropertiesArguments(boolean useDemoTrust, boolean useFastRandomOnLinux, ExecutionResult result);

	/**
	 * Parse resource whether fast random generator should be used for Linux.
	 * 
	 * @param session session used to parse resource.
	 * 
	 * @return whether fast random generator should be used for Linux.
	 */
	public boolean getFastRandomGeneratorForLinuxProperty(ProcessExecutionSession session);
	
}
