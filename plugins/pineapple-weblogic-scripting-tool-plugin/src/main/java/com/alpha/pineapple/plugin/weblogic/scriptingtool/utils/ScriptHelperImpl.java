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

import static com.alpha.pineapple.plugin.weblogic.scriptingtool.WeblogicScriptingToolConstants.LINUX_FAST_RANDOM_GENERATOR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;

import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.WeblogicScriptingToolConstants;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.argument.ArgumentBuilder;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.argument.SystemPropertiesArgumentBuilder;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.model.Wlst;
import com.alpha.pineapple.resource.ResourceException;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.pineapple.test.Asserter;
import com.alpha.pineapple.test.matchers.PineappleMatchers;


/**
 * implementation of the {@linkplain ScriptHelper} interface.
 */
public class ScriptHelperImpl implements ScriptHelper {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
	            
    /**
     * Runtime directory provider.
     */
    @Resource
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;
        
    /**
     * Execution info provider.
     */
    @Resource
    ExecutionInfoProvider coreExecutionInfoProvider;
    	
    /**
     * Pineapple matcher factory.
     */
    @Resource
    PineappleMatchers pineappleMatchers;
    
    /**
     * Java System properties.
     */
    @Resource
    Properties systemProperties;

    /**
     * Java system utilities.
     */
    @Resource
    SystemUtils systemUtils;
    
    /**
     * Argument builder.
     */
    @Resource
    ArgumentBuilder argumentBuilder;           

    /**
     * Argument builder.
     */
    @Resource
    SystemPropertiesArgumentBuilder systemPropertiesArgumentBuilder;           
    
	public boolean isModelContentDefined(Wlst model) {
		return ((model.getWlstScript() != null ));
	}
	
	
	public boolean isScriptValid(String script, Asserter asserter) {
		
		// validate script exists
		Matcher<File> fileMatcher = pineappleMatchers.doesFileExist();		
		File installerFile = new File(script);
    	Object[] args = { installerFile };    	        	
    	String description = messageProvider.getMessage("shi.assert_script_exists", args );
    	
    	// assert and return result
		asserter.assertObject(installerFile, fileMatcher, description);			
		
    	// add path info to execution result
		asserter.getLastAssertionResult().addMessage(messageProvider.getMessage("shi.assert_script_exists_info"), script );		
		
		return asserter.lastAssertionSucceeded();
	}    
	
	
	public boolean isPropertiesFileValid(String propertiesFileName, Asserter asserter) {
		
		// validate script exists
		Matcher<File> fileMatcher = pineappleMatchers.doesFileExist();		
		File propertiesFile = new File(propertiesFileName);
    	Object[] args = { propertiesFile };    	        	
    	String description = messageProvider.getMessage("shi.assert_properties_exists", args );
    	
    	// assert and return result
		asserter.assertObject(propertiesFile, fileMatcher, description);			
		
    	// add path info to execution result
		asserter.getLastAssertionResult().addMessage(messageProvider.getMessage("shi.assert_properties_exists_info"), propertiesFileName );		
		
		return asserter.lastAssertionSucceeded();
		
	}


	public boolean isWlstInvokerValid(File wlstInvoker, Asserter asserter) {
		
		// validate script exists
		Matcher<File> fileMatcher = pineappleMatchers.doesFileExist();		
    	Object[] args = { wlstInvoker };    	        	
    	String description = messageProvider.getMessage("shi.assert_invokerscript_exists", args );
    	
    	// assert and return result
		asserter.assertObject(wlstInvoker, fileMatcher, description);			
		
    	// add path info to execution result
		asserter.getLastAssertionResult().addMessage(messageProvider.getMessage("shi.assert_invokerscript_exists_info"), wlstInvoker.getAbsolutePath() );		
		
		return asserter.lastAssertionSucceeded();
	}
	
	
	public boolean isProductHomeValid(File productHomeDir, Asserter asserter) {
		
		// validate directory exists
		Matcher<File> fileMatcher = pineappleMatchers.doesDirectoryExist();		
    	Object[] args = { productHomeDir };    	        	
    	String description = messageProvider.getMessage("shi.assert_homedir_exists", args );
    	
    	// assert and return result
		asserter.assertObject(productHomeDir, fileMatcher, description);			
		
    	// add path info to execution result
		asserter.getLastAssertionResult().addMessage(messageProvider.getMessage("shi.assert_homedir_exists_info"), productHomeDir.getAbsolutePath() );		
		
		return asserter.lastAssertionSucceeded();
	}


	public String resolveModulePath(ExecutionResult result, String installer) {
				
		// exit if prefix isn't present
		if (!coreRuntimeDirectoryProvider.startsWithModulePathPrefix(installer)) return installer;
			
		// get module info 
		ExecutionInfo executionInfo = coreExecutionInfoProvider.get(result);
		ModuleInfo moduleInfo = executionInfo.getModuleInfo();
		
		// resolve path
		File resolvedPath = coreRuntimeDirectoryProvider.resolveModelPath(installer, moduleInfo);			
		installer = resolvedPath.getAbsolutePath();

		return installer;
	}

	
	public String getModulePath(ExecutionResult result ) {
		
		// get module info 
		ExecutionInfo executionInfo = coreExecutionInfoProvider.get(result);
		ModuleInfo moduleInfo = executionInfo.getModuleInfo();
		
		// get path
		return moduleInfo.getDirectory().getAbsolutePath();
	}

	
	public boolean isPropertiesFileDefined(Wlst model) {
		return (model.getWlstScript().getPropertiesFile() != null );
	}

	
	public File getWlstInvoker(File homeDirectory) {
				
		// get OS specific invoker		
		String invokerName = null;
		if(systemUtils.isWindowsOperatingSystem(systemProperties)) {
			invokerName = "wlst.cmd";			
		} else {
			invokerName = "wlst.sh";
		}
		
		// create absolute path
		File commonDir = new File(homeDirectory, "common");    	    	
		File binDir = new File(commonDir, "bin");    	    	    	    	
		File invoker = new File(binDir, invokerName);
		return invoker;						
	}	
	
	public String[] createArguments(String script, String properties) {		
		argumentBuilder.buildArgumentList();
		argumentBuilder.addSkipScanningArgument();
		
		// add properties file
		if( properties != null) {
			argumentBuilder.addLoadPropertiesArgument();					
			argumentBuilder.addSingleArgument(properties);			
		}

		// add script file		
		argumentBuilder.addSingleArgument(script);										
				
		String[] arguments = argumentBuilder.getArgumentList();				

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = {Arrays.toString(arguments) };			
			logger.debug(messageProvider.getMessage("shi.arguments_info", args));
		}
		
		return arguments;
	}
	

	public String[] createSystemPropertiesArguments(boolean useDemoTrust, boolean useFastRandomOnLinux, ExecutionResult result) {		
		systemPropertiesArgumentBuilder.buildArgumentList();
		
		// add Demo trust				
		if (useDemoTrust) {
			systemPropertiesArgumentBuilder.addDemoTrustArguments();			
		}

		// add fast random generator				
		if (useFastRandomOnLinux) {
			systemPropertiesArgumentBuilder.addLinuxFastSecureRandomArguments();			
		}
		
		// add pineapple arguments
		File modulePath = new File(getModulePath(result));	    		
		systemPropertiesArgumentBuilder.addPineappleArguments(modulePath);
				
		String[] arguments = systemPropertiesArgumentBuilder.getArgumentList();				

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = {Arrays.toString(arguments) };			
			logger.debug(messageProvider.getMessage("shi.arguments_info", args));
		}
		
		return arguments;
	}
	
	
    public File createWlstInvokerScript(File wlst, String[] systemPropsArguments) throws Exception
    {
		// create file object		
		File invokerScripFile = createWlstInvokerFileName();

		// delete file if it exists
		if(invokerScripFile.exists()) {
			boolean succeded = invokerScripFile.delete();
			
			// handle failure to delete file
			if(!succeded) {
		    	Object[] args = { invokerScripFile };
		    	String message=messageProvider.getMessage("shi.delete_oldscript_failed", args);
		    	throw new IOException(message);
			}			
		}

		// create file content and write it
		Collection<String> lines = createInvokerFileContent(wlst, systemPropsArguments);		
		FileUtils.writeLines(invokerScripFile, lines);				
		
		// set file permissions
		invokerScripFile.setExecutable(true);
		invokerScripFile.setReadable(true);
		invokerScripFile.setWritable(true);		
		
	    return invokerScripFile;							
    }


    /**
     * Create OS specific content for temporary WLST invoker script.
     * 
     * @param wlst Absolute path to WLST script (e.g wlst.cmd or wlst.sh). 
     * @param systemPropsArguments Collection of system properties which need to
     * be set on the JVM level as part on the WLSt invocation.
     * 
     * @return OS specific content for temporary WLST invoker script.
     */
	Collection<String> createInvokerFileContent(File wlst, String[] systemPropsArguments) {
	
		// convert to string
		String systemProps = StringUtils.join(systemPropsArguments, ' ');
		
		// create file for Windows		
		if(systemUtils.isWindowsOperatingSystem(systemProperties)) {
			Collection<String> lines = new ArrayList<String>();
			lines.add("@ECHO OFF");
			lines.add("SETLOCAL");
			lines.add("");		
			lines.add("@REM Set system properties");
			lines.add("SET WLST_PROPERTIES=%WLST_PROPERTIES% " + systemProps.toString());
			lines.add("");				
			lines.add("@REM Delegate to the main script...");		
			lines.add("SET WLST_SCRIPT=" + wlst.getAbsolutePath());
			lines.add("CALL \"%WLST_SCRIPT%\" %*");
			return lines;			
		}

		// create file for other OS
		Collection<String> lines = new ArrayList<String>();
		lines.add("#!/bin/sh");
		lines.add("");		
		lines.add("# Set system properties");
		lines.add("WLST_PROPERTIES=\"${WLST_PROPERTIES} " +  systemProps.toString() + "\"");
		lines.add("export WLST_PROPERTIES");				
		lines.add("");				
		lines.add("# Delegate to the main script...");		
		lines.add("WLST_SCRIPT='" + wlst.getAbsolutePath() + "'");
		lines.add("\"${WLST_SCRIPT}\" \"$@\"");
		return lines;
	}	
	
	/**
	 * Create OS specific file name for temporary WLST invoker script. 
	 * 
	 * The temporary script is placed in the temp.
	 * directory resolved from runtime directory provider.
	 * 
	 * If the OS is Windows the name of the script is the package name
	 * of the WLST plugin with the post fix ".cmd". Otherwise the name
	 * is the package name of the WLST plugin with the post fix ".sh".
	 *  
	 * @return Create OS specific file name for temporary WLST invoker 
	 * script.   
	 */
	File createWlstInvokerFileName() {
		
		// get OS specific invoker		
		String invokerName = WeblogicScriptingToolConstants.class.getPackage().getName();
		if(systemUtils.isWindowsOperatingSystem(systemProperties)) {
			invokerName += ".cmd";			
		} else {
			invokerName += ".sh";
		}
		
		// create absolute path
		File tempDir = coreRuntimeDirectoryProvider.getTempDirectory();;    	    	    	    	    	    	
		File invoker = new File(tempDir, invokerName);
		return invoker;						
	}


	@Override
	public boolean getFastRandomGeneratorForLinuxProperty(ProcessExecutionSession session) {		
		
		try {
			String value = session.getResourceProperty(LINUX_FAST_RANDOM_GENERATOR);
			if (value == null) return false; 
			if (value.isEmpty()) return false;			
			return Boolean.parseBoolean(value);
			
		} catch (ResourceException e) {
			// exception is interpreted as no defined
			return false;
		}
	}	
	
	
	
	
}
