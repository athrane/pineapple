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
import static com.alpha.pineapple.plugin.weblogic.installation.WeblogicInstallationConstants.SILENT_UNINSTALL_LOG;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

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
import com.alpha.pineapple.plugin.weblogic.installation.model.Mapper;
import com.alpha.pineapple.plugin.weblogic.installation.model.MapperFactoryImpl;
import com.alpha.pineapple.plugin.weblogic.installation.model.UniversalInstallerMapper;
import com.alpha.pineapple.plugin.weblogic.installation.model.WeblogicInstallation;
import com.alpha.pineapple.plugin.weblogic.installation.utils.InstallationHelper;
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
     * Execution result factory
     */
    @Resource
    ExecutionResultFactory executionResultFactory;   
                
                                   
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
            // type cast to WebLogic installation model object
        	WeblogicInstallation model = (WeblogicInstallation) content;
            
            // type cast to process execution session 
            ProcessExecutionSession externalProcessSession = (ProcessExecutionSession) session; 

            // register execution result with command runner
            commandRunner.setExecutionResult(executionResult);
            
            // register execution result with asserter
            asserter.setExecutionResult(executionResult);

            // run installer 
            runUninstaller(model, externalProcessSession, executionResult);
            runRelease1212Uninstaller(model, externalProcessSession, executionResult);
            
            // compute execution state from children
            executionResult.completeAsComputed(messageProvider, "uc.completed", null, "uc.failed", null );    		                        
        }
        catch ( Exception e )
        {
        	Object[] args = { e.toString() };    	        	
            executionResult.completeAsError(messageProvider, "uc.error", args, e);
        }
    }

    /**
     * Run uninstaller for release 12.1.2 (which uses the universal installer).
     *  
     * @param model Plugin model.
     * @param session Plugin session.
     * @param result execution result.
     * 
     * @throws Exception if execution fails. 
     */
	void runRelease1212Uninstaller(WeblogicInstallation model,
			ProcessExecutionSession session,
			ExecutionResult result) {

		/**
		
		//exit if no model content is defined.
		if (!installationHelper.isModelContentDefined(model)) return;
		
		// get mapper for current installer
		UniversalInstallerMapper mapper = mapperFactory.getRelease1212Mapper(model); 		

		// exit if WebLogic isn't already installed
		if (!installationHelper.isProductInstalled(session, result, mapper.getTargetDirectory(model)) ) { 
			
			// add message			
			return;
		}
						
		// get uninstaller from model 
		String uninstaller = mapper.getUninstaller(model); 
		
		// resolve module path
		uninstaller = installationHelper.resolveModulePath(result, uninstaller);
		
		// assert installer is valid
		if(!installationHelper.isInstallerValid(new File(uninstaller), result )) return;
		
		// escape installer path if required
		uninstaller = ExecutableNameUtils.escapeExecutable(uninstaller);
		
		// create argument list for WebLogic uninstallation
		String[] arguments = mapper.createUninstallationArgumentList( 

		// get description
    	String description2 = messageProvider.getMessage("uc.execute" );
		
		// execute external process    	
		session.execute(uninstaller , arguments, DEFAULT_PROCESS_TIMEOUT, description2, result);
		
		**/
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
	void runUninstaller(WeblogicInstallation model, ProcessExecutionSession session, ExecutionResult result) throws IOException {
		
		//exit if no model content is defined.
		if (!installationHelper.isModelContentDefined(model)) return;
		
		// get mapper for current installer
		Mapper mapper = mapperFactory.getMapper(model); 		

		// exit if WebLogic isn't already installed
		if (!installationHelper.isProductInstalled(session, result, mapper.getTargetDirectory(model)) ) { 
			
			// add message			
			return;
		}
						
		// create silent log file
		File silentLogFile = installationHelper.createSilentLogFile( SILENT_UNINSTALL_LOG ); 
		
		// get uninstaller from model 
		String uninstaller = mapper.getUninstaller(model); 
		
		// resolve module path
		uninstaller = installationHelper.resolveModulePath(result, uninstaller);
		
		// assert installer is valid
		if(!installationHelper.isInstallerValid(new File(uninstaller), result )) return;
		
		// escape installer path if required
		uninstaller = ExecutableNameUtils.escapeExecutable(uninstaller);
		
		// create argument list for WebLogic uninstallation
		String[] arguments = mapper.createUninstallationArgumentList(silentLogFile); 

		// get description
    	String description2 = messageProvider.getMessage("uc.execute" );
		
		// execute external process    	
		session.execute(uninstaller , arguments, DEFAULT_PROCESS_TIMEOUT, description2, result);
	}

}
