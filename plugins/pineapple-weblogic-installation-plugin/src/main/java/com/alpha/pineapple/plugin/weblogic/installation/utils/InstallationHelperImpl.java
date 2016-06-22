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

import javax.annotation.Resource;

import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.plugin.weblogic.installation.model.Mapper;
import com.alpha.pineapple.plugin.weblogic.installation.model.WeblogicInstallation;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.pineapple.test.AssertionHelper;


/**
 * implementation of the {@linkplain InstallationHelper} interface.
 */
public class InstallationHelperImpl implements InstallationHelper {
        
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
     * Assertion helper.
     */
    @Resource    
    AssertionHelper assertionHelper;
        
	public boolean isInstallerValid(File installer, ExecutionResult result ) {
		
		// assert installer is valid
		ExecutionResult assertionResult = assertionHelper.assertFileExist(
				installer, 
				messageProvider, 
				"ihi.assert_installer_exists", 
				result );						
				
    	// add installer path info to execution result
    	String messageHeader = messageProvider.getMessage("ihi.assert_installer_exists_info");
    	assertionResult.addMessage(messageHeader, installer.getAbsolutePath() );		
		
		return (assertionResult.isSuccess()); 				
	}    
	
	public boolean isProductInstalled(ProcessExecutionSession session, ExecutionResult result, File targetDirectory  ) {

		// assert production installation directory is valid
		ExecutionResult assertionResult = assertionHelper.assertDirectoryExist(
				targetDirectory, 
				messageProvider, 
				"ihi.assert_weblogic_installed" );
		
    	// create description		
    	String message = messageProvider.getMessage("ihi.assert_weblogic_installed" );        	
		
        // create execution result to hold result of product installation test
        ExecutionResult isInstalledResult = result.addChild(message);        
		    	
        // update execution result with result of product installation test
		if (assertionResult.isSuccess()) {
			isInstalledResult.completeAsSuccessful(
					messageProvider, 
					"ihi.assert_weblogic_installed_succeed");
		} else {
			isInstalledResult.completeAsSuccessful(
					messageProvider, 
					"ihi.assert_weblogic_installed_failed");
		}
    			
    	// add installation directory info to execution result
    	String messageHeader = messageProvider.getMessage("ihi.assert_weblogic_installed_info");
    	isInstalledResult.addMessage(messageHeader, targetDirectory.getAbsolutePath() );		
		
		return assertionResult.isSuccess();				
	}	
	
	
	public boolean isModelContentDefined(WeblogicInstallation model) {
		return ((model.getRelease9() != null ) || (model.getRelease10() != null ) || (model.getRelease12() != null ));				
	}
	
	
	public File createSilentLogFile(String logFileName) {
		
		// get temporary directory
		File tempDirectory = coreRuntimeDirectoryProvider.getTempDirectory();

		// create file object		
		File silentLogFile = new File( tempDirectory, logFileName);
		
		// if the file exists then delete it
		if( silentLogFile.exists()) {
			boolean isDeleted = silentLogFile.delete();
		}

		// handle failure to delete the file
		
		// return the file object
		return silentLogFile;		
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

	@Override
	public boolean isRelease1212ModelContentDefined(WeblogicInstallation model) {
		return ((model.getRelease1212() != null ));								
	}

	
}
