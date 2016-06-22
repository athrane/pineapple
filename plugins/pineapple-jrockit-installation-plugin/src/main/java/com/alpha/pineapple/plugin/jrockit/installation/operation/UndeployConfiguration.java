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


package com.alpha.pineapple.plugin.jrockit.installation.operation;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;

import com.alpha.javautils.ExecutableNameUtils;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.jrockit.installation.command.TestJvmIsInstalledCommand;
import com.alpha.pineapple.plugin.jrockit.installation.configurer.InstallationConfigurer;
import com.alpha.pineapple.plugin.jrockit.installation.model.JRockitInstallation;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.test.Asserter;
import com.alpha.pineapple.test.matchers.PineappleMatchers;

@PluginOperation( OperationNames.UNDEPLOY_CONFIGURATION )
public class UndeployConfiguration implements Operation
{
	
	/**
	 * Default process time out (ms).
	 */
	static final int DEFAULT_PROCESS_TIMEOUT = 1000*60*10;

	/**
	 * Silent XML file name. 
	 */
	static final String SILENTINSTALL_JROCKIT_XML = UndeployConfiguration.class.getCanonicalName()+ ".xml";

	/**
	 * Silent log file name. 
	 */
	static final String SILENTINSTALL_JROCKIT_LOG = UndeployConfiguration.class.getCanonicalName()+ ".log";
	
	/**
	 * First list index.
	 */
	static final int FIRST_INDEX= 0;
	
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
     * Hamcrest asserter.
     */
    @Resource
    Asserter asserter;

    /**
     * Command runner
     */
    @Resource
    CommandRunner commandRunner;   

    /**
     * Command runner for validation of already installed JVM.
     */
    @Resource
    CommandRunner isolatedCommandRunner;   

    /**
     * Execution result factory
     */
    @Resource
    ExecutionResultFactory executionResultFactory;   

    /**
     * Pineapple matcher factory.
     */
    @Resource
    PineappleMatchers pineappleMatchers;
    
    /**
     * R27 installation configurer object.
     */
    @Resource
    InstallationConfigurer r27InstallationConfigurer;   
    
    /**
     * R28 installation configurer object.
     */
    @Resource
    InstallationConfigurer r28InstallationConfigurer;       
        
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
     * Test JVM is installed command.
     */
    @Resource
    Command testJvmIsInstalledCommand;
                                   
    public void execute( Object content, Session session, ExecutionResult executionResult ) throws PluginExecutionFailedException
    {
        // validate parameters
        Validate.notNull( content, "content is undefined." );
        Validate.notNull( session, "session is undefined." );
        Validate.notNull( executionResult, "executionResult is undefined." );        
    	
        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { content.getClass().getName(), content };    	        	
        	String message = messageProvider.getMessage("uc.start", args );
        	logger.debug( message );
        }

        // throw exception if required type isn't available
        if ( !( content instanceof JRockitInstallation ) )
        {
        	Object[] args = { JRockitInstallation.class };    	        	
        	String message = messageProvider.getMessage("uc.content_typecast_failed", args );
            throw new PluginExecutionFailedException( message );
        }

        // throw exception if required type isn't available
        if ( !( session instanceof ProcessExecutionSession ) )
        {
        	Object[] args = { ProcessExecutionSession.class };    	        	
        	String message = messageProvider.getMessage("uc.session_typecast_failed", args );
            throw new PluginExecutionFailedException( message );
        }
                
        try
        {        	
            // type cast to JRockit installation model object
        	JRockitInstallation model = (JRockitInstallation) content;
            
            // type cast to process execution session 
            ProcessExecutionSession externalProcessSession = (ProcessExecutionSession) session; 

            // register execution result with command runner
            commandRunner.setExecutionResult(executionResult);
            
            // register execution result with asserter
            asserter.setExecutionResult(executionResult);

            // run installer 
            runUninstaller(model, externalProcessSession, executionResult);
                                    
            // compute execution state from children
            executionResult.completeAsComputed(messageProvider, "uc.completed", null, "uc.failed", null );    		                        
        }
        catch ( Exception e )
        {
        	Object[] args = { e.toString() };    	        	
        	String message = messageProvider.getMessage("uc.error", args );
            throw new PluginExecutionFailedException( message, e );
        }
    }

    /**
     * Run uninstaller. 
     *  
     * @param model Plugin model.
     * @param session Plugin session.
     * @param result execution result.
     * 
     * @throws IOException if execution fails. 
     */
	void runUninstaller(JRockitInstallation model, ProcessExecutionSession session, ExecutionResult result) throws IOException {
		
		//exit if no model content is defined.
		if (!isModelContentDefined(model)) return;
		
		// get installer configurer for current installer
		InstallationConfigurer configurer = getInstallerConfigurer(model); 		

		// exit if JVM isn't already installed
		if (!isJvmInstalled(model, session, result, configurer) ) { 
		
			// add message			
			return;
		}
				
		// create silent log file
		File silentLogFile = createSilentLogFile(); 
		
		// get uninstaller from model 
		String uninstaller = configurer.getUninstaller(model); 
		
		// resolve module path
		uninstaller = resolveModulePath(result, uninstaller);
		
		// assert uninstaller is valid
		if(!isUninstallerValid(uninstaller)) return;
		
		// escape installer path if required
		uninstaller = ExecutableNameUtils.escapeExecutable(uninstaller);
		
		// create argument list for JVM uninstallation
		String[] arguments = configurer.createUninstallationArgumentList(silentLogFile); 

		// get description
    	String description2 = messageProvider.getMessage("uc.execute" );
		
		// execute external process    	
		session.execute(uninstaller , arguments, DEFAULT_PROCESS_TIMEOUT, description2, result);
	}

	
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
	String resolveModulePath(ExecutionResult result, String installer) {
				
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
	
	/**
	 * Returns true is JVM is already installed. 
	 * 
	 * @param model Plugin model.
	 * @param session Plugin session.
	 * @param result Execution result.
	 * @param configurer Installation configurer.
	 * 
	 * @return true if the JVM is installed.
	 */
	@SuppressWarnings("unchecked")
	boolean isJvmInstalled(JRockitInstallation model, ProcessExecutionSession session, ExecutionResult result, InstallationConfigurer configurer ) {

    	// create description		
    	String message = messageProvider.getMessage("uc.assert_jvm_installed" );        	

        // create execution result to hold result of JVM installation test
        ExecutionResult isJvmInstalledResult = result.addChild(message);        

    	// create isolated execution result
    	ExecutionResult isolatedResult = executionResultFactory.startExecution(message);
    	
    	// set execution result
    	isolatedCommandRunner.setExecutionResult(isolatedResult);
    	
    	// add JVM path info to execution result
    	isJvmInstalledResult.addMessage(
    			messageProvider.getMessage("uc.assert_jvm_installed_info"),
    			configurer.getJvmExecutablePath(model).toString() );
    	
        // create context
        Context context = isolatedCommandRunner.createContext();            
        
        // initialize context
        context.put(TestJvmIsInstalledCommand.JVM_EXECUTABLE_DIR_KEY, configurer.getJvmExecutablePath(model) );
        context.put(TestJvmIsInstalledCommand.SESSION_KEY, session );
        context.put(TestJvmIsInstalledCommand.VERSION_KEY, configurer.getVersion());
        
        // run test            
        isolatedCommandRunner.run(testJvmIsInstalledCommand, message, context);                        
		        
        // update main execution result with result of JVM installation test
		if (isolatedCommandRunner.lastExecutionSucceeded()) {
			isJvmInstalledResult.completeAsSuccessful(messageProvider, "uc.assert_jvm_installed_succeed");
		} else {
			isJvmInstalledResult.completeAsSuccessful(messageProvider, "uc.assert_jvm_installed_failed");
		}
		
		// return result
		return isolatedCommandRunner.lastExecutionSucceeded();
	}

	/**
	 * Initialize installer configurer for current installer release.
	 * 
	 * @param model Plugin model. 
	 * 
	 * @return initialized installer configurer for current installer release.
	 */
	InstallationConfigurer getInstallerConfigurer(JRockitInstallation model) {
		
		if (model.getR27() != null ) return r27InstallationConfigurer;  		
		if (model.getR28() != null ) return r28InstallationConfigurer;
		return null;
	}

	/**
	 * Asserts whether the uninstaller path points to a valid file. 
	 * 
	 * The result of the of the assertion is added as an execution result to
	 * current execution result object graph. 
	 * 
	 * @param installer Path to the installer.
	 * 
	 * @return True if the installer exists as an file.
	 */
	boolean isUninstallerValid(String installer) {
		// validate installer exists
		Matcher<File> fileMatcher = pineappleMatchers.doesFileExist();		
		File installerFile = new File(installer);
    	Object[] args = { installerFile };    	        	
    	String description = messageProvider.getMessage("uc.assert_uninstaller_exists", args );
    	
    	// assert and return result
		asserter.assertObject(installerFile, fileMatcher, description);
		
    	// add uninstaller path info to execution result
		asserter.getLastAssertionResult().addMessage(
    			messageProvider.getMessage("uc.assert_uninstaller_exists_info"), installer );		
		
		return asserter.lastAssertionSucceeded();
	}
		
	/**
	 * Returns true if either a R27 or R28 model is defined.
	 * 
	 * @param model Plugin model.
	 * 
	 * @return true if either a R27 or R28 model is defined.
	 */
	boolean isModelContentDefined(JRockitInstallation model) {
		return ((model.getR27() != null ) || (model.getR28() != null ));				
	}
	
	/**
	 * Create log file for silent installation.
	 * 
	 * @return File object which represents silent log file.
	 */
	
	File createSilentLogFile() {
		
		// get temporary directory
		File tempDirectory = coreRuntimeDirectoryProvider.getTempDirectory();
		
		// create file object		
		File silentLogFile = new File(tempDirectory , SILENTINSTALL_JROCKIT_LOG);
		
		// if the file exists then delete it
		if( silentLogFile.exists()) {
			boolean isDeleted = silentLogFile.delete();
		}

		// handle failure to delete the file
		
		// return the file object
		return silentLogFile;
	}
		        
}
