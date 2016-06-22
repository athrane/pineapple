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


package com.alpha.pineapple.plugin.weblogic.installation.operation;


import static com.alpha.pineapple.plugin.weblogic.installation.WeblogicInstallationConstants.DEFAULT_PROCESS_TIMEOUT;
import static com.alpha.pineapple.plugin.weblogic.installation.WeblogicInstallationConstants.LEGAL_CONTENT_TYPES;
import static com.alpha.pineapple.plugin.weblogic.installation.WeblogicInstallationConstants.SILENT_INSTALL_LOG;
import static com.alpha.pineapple.plugin.weblogic.installation.command.CreateSilentXmlCommand.SILENTXML_FILE;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.ExecutableNameUtils;
import com.alpha.javautils.OperationUtils;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.weblogic.installation.command.CreateInstallResponseFileCommand;
import com.alpha.pineapple.plugin.weblogic.installation.command.CreateSilentXmlCommand;
import com.alpha.pineapple.plugin.weblogic.installation.model.Mapper;
import com.alpha.pineapple.plugin.weblogic.installation.model.MapperFactoryImpl;
import com.alpha.pineapple.plugin.weblogic.installation.model.UniversalInstallerMapper;
import com.alpha.pineapple.plugin.weblogic.installation.model.WeblogicInstallation;
import com.alpha.pineapple.plugin.weblogic.installation.utils.InstallationHelper;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.test.Asserter;

@PluginOperation( OperationNames.DEPLOY_CONFIGURATION )
public class DeployConfiguration implements Operation
{

	/**
	 * String array separator.
	 */
	static final String CHAR_SEPARATOR = ",";

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
     * Operation utilities.
     */
    @Resource
    OperationUtils operationUtils;

    /**
     * Installation helper.
     */
    @Resource
    InstallationHelper installationHelper;
    
    /**
     * Mapper factory.
     */
    @Resource
    MapperFactoryImpl mapperFactory;   
    
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
     * Execution result factory
     */
    @Resource
    ExecutionResultFactory executionResultFactory;   
        
    /**
     * Create release 12.1.2 response file command.
     */
    @Resource
    Command createRelease1212InstallResponseFileCommand;
    
    /**
     * Holds the state of the last response file creation. 
     */
	boolean isResponseFileCreationSuccessful;
        
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
        	String message = messageProvider.getMessage("dc.start", args );
        	logger.debug( message );
        }

        // validate parameters
        operationUtils.validateContentType(content, LEGAL_CONTENT_TYPES);
        operationUtils.validateSessionType(session, ProcessExecutionSession.class );
                        
        try
        {        	
            // type cast to WebLogic installation model object
        	WeblogicInstallation model = (WeblogicInstallation) content;
            
            // type cast to process execution session 
            ProcessExecutionSession externalProcessSession = (ProcessExecutionSession) session; 

            // register execution result with command runner
            commandRunner.setExecutionResult(executionResult);
            
            // register execution result with asserter
            asserter.setExecutionResult(executionResult);

            // run installer 
            runInstaller(model, externalProcessSession, executionResult);
            runRelease1212Installer(model, externalProcessSession, executionResult);
            
            // compute execution state from children
            executionResult.completeAsComputed(messageProvider, "dc.completed", null, "dc.failed", null );    		                        
        }
        catch ( Exception e ) {
        	Object[] args = { e.toString() };    	        	
            executionResult.completeAsError(messageProvider, "dc.error", args, e);
        }
    }


    /**
     * Run installer for release 12.1.2 (which uses the universal installer).
     *  
     * @param model Plugin model.
     * @param session Plugin session.
     * @param result execution result.
     * 
     * @throws Exception if execution fails. 
     */
	void runRelease1212Installer(WeblogicInstallation model, ProcessExecutionSession session, ExecutionResult result) throws IOException {
		
		//exit if no model content is defined.
		if (!installationHelper.isRelease1212ModelContentDefined(model)) return;
		
		// get mapper for current installer
		UniversalInstallerMapper mapper = mapperFactory.getRelease1212Mapper(model); 		

		runUniversalInstaller(model, session, result, mapper, createRelease1212InstallResponseFileCommand);		
	}
    
    /**
     * Run installer for release 9.2 to 12.1.1. 
     *  
     * @param model Plugin model.
     * @param session Plugin session.
     * @param result execution result.
     * 
     * @throws Exception if execution fails. 
     */
	void runInstaller(WeblogicInstallation model, ProcessExecutionSession session, ExecutionResult result) throws Exception {

		// declare arguments
		String[] arguments = null;
		
		//exit if no model content is defined.
		if (!installationHelper.isModelContentDefined(model)) return;
		
		// get mapper for current installer
		Mapper mapper = mapperFactory.getMapper(model); 		

		// exit if WebLogic is already installed
		if (installationHelper.isProductInstalled(session, result, mapper.getTargetDirectory(model)) ) { 
			
			// add message			
			return;
		}
		
		// get installer from model 
		String installer = mapper.getInstaller(model); 
		
		// resolve module path
		installer = installationHelper.resolveModulePath(result, installer);
		
		// assert installer is valid
		if(!installationHelper.isInstallerValid(new File(installer), result )) return;
		
		// escape installer archive path if required
		installer = ExecutableNameUtils.escapeExecutable(installer);

		// create silent log file
		File silentLogFile = installationHelper.createSilentLogFile( SILENT_INSTALL_LOG ); 
		
		// create response file
		Command command = mapper.getCreateInstallReponseCommand();
		File silentXmlFile = createXmlInstallResponseFile(model, mapper, command); 
		
		// exit if creation of silent XML failed.
		if(!isResponseFileCreationSuccessful) return;
		
		if (isBinaryInstaller(installer)) {

			// escape installer path if required
			installer = ExecutableNameUtils.escapeExecutable(installer);
			
			// create argument list for WebLogic installation
			arguments = mapper.createArgumentList(silentXmlFile, silentLogFile); 			
			
		} else {
			//handle jar installer
			
			// store archive name
			String installerArchive = installer;
			
			// get JVM  directory
			File jvmFile = ExecutableNameUtils.resolveJavaExecutable(mapper.getLocalJvm(model));
			
			// set java as installer
			installer = prefixExecutableWithDot(jvmFile);
			
			// create argument list for WebLogic installation
			arguments = mapper.createArgumentListForArchive(installerArchive, silentXmlFile, silentLogFile);			
		}						
		
		// delete installation log files
		//installationHelper.deleteInstallationLogFiles(model, result);		
				
		// get description    	        	
    	String description2 = messageProvider.getMessage("dc.execute" );
		
		// execute external process    	
		session.execute(installer , arguments, DEFAULT_PROCESS_TIMEOUT, description2, result);
		
		// attach installation log files
		//installationHelper.attachInstallationLogFiles(model, result);		
		
		// delete installation log files
		//installationHelper.deleteInstallationLogFiles(model, result);		
		
		// --------------------------------
				
		// @TODO: validate JVM		
	}

    /**
     * Run Oracle universal installer for release 12.1.2 +. 
     *  
     * @param model Plugin model.
     * @param session Plugin session.
     * @param result execution result.
     * @param command create response file command.
     * 
     * @throws IOException if execution fails. 
     */
	void runUniversalInstaller(WeblogicInstallation model, 
			ProcessExecutionSession session, 
			ExecutionResult result,
			UniversalInstallerMapper mapper,
			Command createInstallResponseCommand) throws IOException {

		// exit if WebLogic is already installed
		if (installationHelper.isProductInstalled(session, result, mapper.getTargetDirectory(model)) ) { 
			
			// add message			
			return;
		}
		
		// get installer from model 
		String installer = mapper.getInstaller(model); 
		
		// resolve module path
		installer = installationHelper.resolveModulePath(result, installer);
		
		// assert installer is valid
		if(!installationHelper.isInstallerValid(new File(installer), result )) return;
		
		// escape installer archive path if required
		installer = ExecutableNameUtils.escapeExecutable(installer);

		// create response file
		File responseFile = createInstallResponseFile(model, mapper, createInstallResponseCommand); 
		
		// exit if creation of response file failed.
		if(!isResponseFileCreationSuccessful) return;

		// store archive name
		String installerArchive = installer;
		
		// get JVM  directory
		File jvmFile = ExecutableNameUtils.resolveJavaExecutable(mapper.getLocalJvm(model));
		
		// set java as installer
		installer = prefixExecutableWithDot(jvmFile);
		
		// create argument list for WebLogic installation
		String[] arguments = mapper.createArgumentListForArchive(installerArchive, responseFile, jvmFile);			
		
		// get description    	        	
    	String description2 = messageProvider.getMessage("dc.execute" );
		
		// execute external process    	
		session.execute(installer , arguments, DEFAULT_PROCESS_TIMEOUT, description2, result);
		
		// attach installation log files
		//installationHelper.attachInstallationLogFiles(model, result);		
		
		// delete installation log files
		//installationHelper.deleteInstallationLogFiles(model, result);		
				
	}
	
	/**
	 * Create Xml response file for product (for release 9.2 - 12.1.1).
	 * 
     * @param model Plugin model.
	 * @param mapper Model mapper.
	 * @param command Response file creator command for specific product.
	 * 
	 * @return response file for product.
	 */
	@SuppressWarnings("unchecked")
	public File createXmlInstallResponseFile(WeblogicInstallation model, Mapper mapper, Command command) {
		
    	// create description
    	String message = messageProvider.getMessage("dc.create_install_responsefile" );        	
    	
        // create context
        Context context = commandRunner.createContext();            
    	
        // initialize context        
        context.put(CreateSilentXmlCommand.TARGET_DIRECTORY, mapper.getTargetDirectory(model));
        context.put(CreateSilentXmlCommand.LOCAL_JVM, mapper.getLocalJvm(model));                
        
        // run test            
        commandRunner.run(command, message, context);                        
		
        // get silent XML from context
        File file = (File) context.get(SILENTXML_FILE);
        		
        // store state
        isResponseFileCreationSuccessful = commandRunner.lastExecutionSucceeded();
        
		// return the file object
		return file;
	}
	
	/**
	 * Create response file for Oracle universal installer (for release 12.1.2+).
	 * 
     * @param model Plugin model.
	 * @param mapper Model mapper.
	 * @param command Response file creator command for specific product.
	 * 
	 * @return response file for product.
	 */
	@SuppressWarnings("unchecked")
	public File createInstallResponseFile(WeblogicInstallation model, 
			UniversalInstallerMapper mapper, 
			Command command) {
		
    	// create description
    	String message = messageProvider.getMessage("dc.create_install_responsefile" );        	
    	
        // create context
        Context context = commandRunner.createContext();            
    	
        // initialize context        
		context.put(CreateInstallResponseFileCommand.TARGET_DIRECTORY, mapper.getTargetDirectory(model));                
        
        // run test            
        commandRunner.run(command, message, context);                        
		
        // get response file from context
        File file = (File) context.get(CreateInstallResponseFileCommand.RESPONSE_FILE);
		
        // store state
        isResponseFileCreationSuccessful = commandRunner.lastExecutionSucceeded();
        
		// return the file object
		return file;
	}
	
	
	/**
	 * If OS is Linux then executable path is prefixed with "../" 
	 * since the installer is run from the java temp directory.
	 * @param file Path to executable.
	 * 
	 * @return If OS is Linux then executable path is prefixed with "./"
	 *  otherwise the absolution path of the executable is returned.
	 */
	String prefixExecutableWithDot(File file) {		
		String absolutePathAsString = file.getAbsolutePath();		
				
		// return absolute path if Os isn't Linux
		if (!SystemUtils.IS_OS_LINUX) return absolutePathAsString; 
		
		// prefix
		StringBuilder dotExecutable = new StringBuilder();		
		dotExecutable.append("..");		
		if (!absolutePathAsString.startsWith("/")) dotExecutable.append("./");
		dotExecutable.append(absolutePathAsString);			
		return dotExecutable.toString();
	}

	/**
	 * Returns true if installer is binary installer, e.g. .exe or .bin
	 * 
	 * @param installer Installer file name.
	 * 
	 * @return true if if installer is binary installer, e.g. .exe or .bin
	 */
	boolean isBinaryInstaller(String installer) {
		if (installer.endsWith(".exe")) return true;
		if (installer.endsWith(".bin")) return true;
		return false;
	}
					        
}
