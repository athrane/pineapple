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


package com.alpha.pineapple.plugin.fusion.installation.operation;

import static com.alpha.pineapple.plugin.fusion.installation.FusionMiddlewareInstallationConstants.DEFAULT_PROCESS_TIMEOUT;
import static com.alpha.pineapple.plugin.fusion.installation.FusionMiddlewareInstallationConstants.LEGAL_CONTENT_TYPES;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.ExecutableNameUtils;
import com.alpha.javautils.OperationUtils;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.fusion.installation.command.CreateR11UninstallResponseFileCommand;
import com.alpha.pineapple.plugin.fusion.installation.model.FusionMiddlewareInstallation;
import com.alpha.pineapple.plugin.fusion.installation.model.MapperFactoryImpl;
import com.alpha.pineapple.plugin.fusion.installation.model.ModelMapper;
import com.alpha.pineapple.plugin.fusion.installation.utils.InstallationHelper;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.test.Asserter;

@PluginOperation( OperationNames.UNDEPLOY_CONFIGURATION )
public class UndeployConfiguration implements Operation
{

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
     * Create response file command.
     */
    @Resource
    Command createAppDevRuntimeR11UninstallResponseFileCommand;

    /**
     * Create response file command.
     */    
    @Resource
    Command createWebCenterR11UninstallResponseFileCommand;

    /**
     * Create response file command.
     */    
    @Resource
    Command createSoaSuiteR11UninstallResponseFileCommand;

    /**
     * Create response file command.
     */    
    @Resource
    Command createServiceBusR11UninstallResponseFileCommand;
    
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
        	String message = messageProvider.getMessage("uc.start", args );
        	logger.debug( message );
        }

        // validate parameters
        operationUtils.validateContentType(content, LEGAL_CONTENT_TYPES);
        operationUtils.validateSessionType(session, ProcessExecutionSession.class );
                
        try
        {        	
            // type cast to Fusion Middleware installation model object
        	FusionMiddlewareInstallation model = (FusionMiddlewareInstallation) content;
            
            // type cast to process execution session 
            ProcessExecutionSession externalProcessSession = (ProcessExecutionSession) session; 

            // register execution result with command runner
            commandRunner.setExecutionResult(executionResult);
            
            // register execution result with asserter
            asserter.setExecutionResult(executionResult);
            
            // run installer 
            runAppDevRuntimeInstaller(model, externalProcessSession, executionResult);
                                    
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
     * Run installer. 
     *  
     * @param model Plugin model.
     * @param session Plugin session.
     * @param result execution result.
     * 
     * @throws IOException if execution fails. 
     */
	void runAppDevRuntimeInstaller(FusionMiddlewareInstallation model, ProcessExecutionSession session, ExecutionResult result) throws IOException {
		
		//exit if no model content is defined.
		if (!installationHelper.isAppDevRuntimeModelContentDefined(model)) return;
		
		// get mapper for current installer
		ModelMapper mapper = mapperFactory.getApplicationDevelopmentRuntimeMapper(model); 		

		runInstaller(model, session, result, mapper, createAppDevRuntimeR11UninstallResponseFileCommand);		
	}


    /**
     * Run installer. 
     *  
     * @param model Plugin model.
     * @param session Plugin session.
     * @param result execution result.
     * 
     * @throws IOException if execution fails. 
     */
	void runWebCenterInstaller(FusionMiddlewareInstallation model, ProcessExecutionSession session, ExecutionResult result) throws IOException {
		
		//exit if no model content is defined.
		if (!installationHelper.isWebCenterModelContentDefined(model)) return;
		
		// get mapper for current installer
		ModelMapper mapper = mapperFactory.getWebCenterMapper(model); 		

		runInstaller(model, session, result, mapper, createWebCenterR11UninstallResponseFileCommand);		
	}

	
    /**
     * Run installer. 
     *  
     * @param model Plugin model.
     * @param session Plugin session.
     * @param result execution result.
     * 
     * @throws IOException if execution fails. 
     */
	void runSoaSuiteInstaller(FusionMiddlewareInstallation model, ProcessExecutionSession session, ExecutionResult result) throws IOException {
		
		//exit if no model content is defined.
		if (!installationHelper.isWebCenterModelContentDefined(model)) return;
		
		// get mapper for current installer
		ModelMapper mapper = mapperFactory.getWebCenterMapper(model); 		

		runInstaller(model, session, result, mapper, createSoaSuiteR11UninstallResponseFileCommand);		
	}
	

    /**
     * Run installer. 
     *  
     * @param model Plugin model.
     * @param session Plugin session.
     * @param result execution result.
     * 
     * @throws IOException if execution fails. 
     */
	void runServiceBusInstaller(FusionMiddlewareInstallation model, ProcessExecutionSession session, ExecutionResult result) throws IOException {
		
		//exit if no model content is defined.
		if (!installationHelper.isWebCenterModelContentDefined(model)) return;
		
		// get mapper for current installer
		ModelMapper mapper = mapperFactory.getWebCenterMapper(model); 		

		runInstaller(model, session, result, mapper, createServiceBusR11UninstallResponseFileCommand);		
	}

	/** 
     * Run installer.
     *  
     * @param model Plugin model.
     * @param session Plugin session.
     * @param result execution result.
	 * @param mapper Model mapper.
	 * @param createInstallResponseCommand Create installre response command.  
	 */
	void runInstaller(FusionMiddlewareInstallation model,
			ProcessExecutionSession session, 
			ExecutionResult result,
			ModelMapper mapper, 
			Command createInstallResponseCommand) {

		// exit if product isn't installed
		if (!installationHelper.isProductInstalled(model, mapper, result) ) { 
			
			// add message			
			return;
		}
		
		// get installer archive from model 
		//String installerArchive = mapper.getInstallerArchive(model); 
		
		// assert installer archive is valid
		//if(!installationHelper.isInstallerArchiveValid(installerArchive, asserter)) return;
		
		// escape installer archive path if required
		//installerArchive = ExecutableNameUtils.escapeExecutable(installerArchive);

		// get installer from model 
		String installer = mapper.getUninstaller(model); 
		
		// resolve module path
		installer = installationHelper.resolveModulePath(result, installer);
		
		// assert installer is valid
		if(!installationHelper.isInstallerValid(new File(installer), result )) return;
		
		// escape installer archive path if required
		installer = ExecutableNameUtils.escapeExecutable(installer);
				
		// create response file
		File responseFile = createUninstallResponseFile(model, mapper, createInstallResponseCommand); 
		
		// exit if creation of silent XML failed.
		if(!isResponseFileCreationSuccessful) return;
								
		// delete installation log files
		//installationHelper.deleteInstallationLogFiles(model, result);		
		
		// get path to local JVM
		File localJvm = mapper.getLocalJvm(model);
		
		// create argument list for installation
		String[] arguments = mapper.createArgumentList(responseFile, localJvm); 
		
		// get description    	        	
    	String description2 = messageProvider.getMessage("uc.execute" );
		
		// execute external process    	
		session.execute(installer , arguments, DEFAULT_PROCESS_TIMEOUT, description2, result);
		
		// attach installation log files
		//installationHelper.attachInstallationLogFiles(model, result);		
		
		// delete installation log files
		//installationHelper.deleteInstallationLogFiles(model, result);		
	}
	
	/**
	 * Create response file for product.
     * @param model Plugin model.
	 * @param mapper Model mapper.
	 * @param command Response file creator command for specific product.
	 * 
	 * @return response file for product.
	 */
	@SuppressWarnings("unchecked")
	public File createUninstallResponseFile(FusionMiddlewareInstallation model, 
			ModelMapper mapper, 
			Command command) {
		
    	// create description
    	String message = messageProvider.getMessage("uc.create_uninstall_responsefile" );        	
    	
        // create context
        Context context = commandRunner.createContext();            
    	        
        // run test            
        commandRunner.run(command, message, context);                        
		
        // get silent XML from context
        File file = (File) context.get(CreateR11UninstallResponseFileCommand.RESPONSE_FILE);
		
        // store state
        isResponseFileCreationSuccessful = commandRunner.lastExecutionSucceeded();
        
		// return the file object
		return file;				
	}
	    
}
