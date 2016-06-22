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


package com.alpha.pineapple.plugin.agent.command;

import static com.alpha.pineapple.plugin.agent.AgentConstants.DISTRIBUTE_MODULE_FILE_PART;
import static com.alpha.pineapple.plugin.agent.AgentConstants.DISTRIBUTE_MODULE_URI;
import static com.alpha.pineapple.plugin.agent.AgentConstants.ZIP_FILE_POSTFIX;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alpha.javautils.ZipProgressCallback;
import com.alpha.javautils.ZipUtils;
import com.alpha.pineapple.admin.AdministrationProvider;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleRepository;
import com.alpha.pineapple.plugin.agent.session.AgentSession;

/**
 * <p>Implementation of the <code>org.apache.commons.chain.Command</code> interface which 
 * distributes a module to a remote Pineapple instance. 
 * 
 * The module must exists in the module repository.
 * 
 * The module is zipped prior to uploading.
 * 
 * <p>Precondition for execution of the command is definition of these keys in 
 * the context:
 * 
 * <ul>
 * <li><code>module</code> defines the name of the module which is distributed. The type is 
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>session</code> defines the agent session used communicate with an agent. 
 * The type is <code>com.alpha.pineapple.plugin.agent.session.AgentSession</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which collects
 * information about the execution of the test. The type is 
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>   
 * </ul>
 * </p>      
 * 
 * <p>Postcondition after execution of the command is: 
 * 
 * <ul> 
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the test succeeded. If the 
 * test failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the test fails due to an exception then the exception isn't caught, 
 * but passed on the the invoker whose responsibility it is to catch it and update 
 * the <code>ExecutionResult</code> with the state <code>ExecutionState.ERROR</code>.
 * </li>
 * </ul>  
 * </p>           
 */
public class DistributeModuleCommand implements Command, ZipProgressCallback {

    /**
     * Key used to identify property in context: Name of the module.
     */
    public static final String MODULE_KEY = "module";

    /**
     * Key used to identify property in context: plugin session object.
     */    
	public static final String SESSION_KEY = "session";
    
    /**
     * Key used to identify property in context: Contains execution result object,.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";
    
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
        
    /**
     * Module name.
     */
    @Initialize( MODULE_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )        
    String module;

    /**
     * Plugin session.
     */
    @Initialize( SESSION_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )        
    AgentSession session;
    
    /**
     * Defines execution result object.
     */
    @Initialize( EXECUTIONRESULT_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )    
    ExecutionResult executionResult;
                
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * ZIP utility.
     */
    @Resource
    ZipUtils zipUtils;
    
    /**
     * Core administration provider.
     */
    @Resource    
    AdministrationProvider coreAdministrationProvider;
    
    /**
     * Runtime directory provider.
     */
    @Resource
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;

    /**
     * Execution result for module compression.
     */
	ExecutionResult compressResult;
    
    
    public boolean execute( Context context ) throws Exception
    {        
        // initialize command
        CommandInitializer initializer = new CommandInitializerImpl();
        initializer.initialize( context, this );
        
        // distribute module
        doDistribute( context );
        
        return Command.CONTINUE_PROCESSING;        
    }

    /**
     * Distribute module.
     * 
     * @param context Command context.
     * 
     * @throws Exception If distribution fails.
     */    
	void doDistribute( Context context )
    {    	
		// validate module exists
    	if(!validateModuleExists()) return;
   
    	try {
	    	// get module directory
	    	ModuleRepository moduleRepository = coreAdministrationProvider.getModuleRepository();
	    	ModuleInfo info = moduleRepository.get(module);
	    	File moduleDirectory = info.getDirectory();
	    	executionResult.addMessage("Module Directory", moduleDirectory.getAbsolutePath() );
	    		    	
	    	// create archive file name
	    	String archiveFileName = new StringBuilder()
	    		.append(module)
	    		.append(ZIP_FILE_POSTFIX)
	    		.toString();    			
	    	
	    	// compress module
			File archiveFile = new File( coreRuntimeDirectoryProvider.getTempDirectory(), archiveFileName);
			if(!compressModule(moduleDirectory, archiveFile)) return;	    	
			if(!uploadModule(archiveFile)) return;		
					
			// complete result
			executionResult.completeAsSuccessful(messageProvider, "dmc.distribute_module_completed");

    	} catch(Exception e) {			
    		Object[] args = {e.getMessage() };
    		executionResult.completeAsError(messageProvider, "dmc.error", args, e);
		}										
   }

    /**
     * Validate module is registered in module repository.
     *  
     * @return true if module is registered in module repository.
     */
	boolean validateModuleExists() {
		
		// create execution result for validation
		String description = messageProvider.getMessage("dmc.distribute_module_validate_module_info");
		ExecutionResult result = executionResult.addChild(description);
		result.addMessage("Module", module);
		
		// get module repository			
		ModuleRepository moduleRepository = coreAdministrationProvider.getModuleRepository();
		
		// validate module exists					
		if (moduleRepository.contains(module)) {
			result.completeAsSuccessful(messageProvider, "dmc.distribute_module_validate_module_completed");
			return true;
		}

		// set ass failed
		result.completeAsFailure(messageProvider, "dmc.distribute_module_validate_module_failed");
		executionResult.completeAsFailure(messageProvider, "dmc.distribute_module_validate_module_failed");
		return false;
	}
    
	/**
	 * Compress module into temporary directory.
	 * 
	 * @param moduleDirectory module directory which is compressed.  
	 * @param archiveFile file object for target archive file. 
	 * 
	 * @return true if module compression succeeds.
	 */
	boolean compressModule(File moduleDirectory, File archiveFile) {
		
		String description = messageProvider.getMessage("dmc.distribute_module_compress_info");
		compressResult = executionResult.addChild(description);
		compressResult.addMessage("Archive File", archiveFile.getAbsolutePath() );		
		
		try {
			// compress 
			zipUtils.zipFolder(moduleDirectory, archiveFile, this );
			
			compressResult.completeAsSuccessful(messageProvider, "dmc.distribute_module_compress_completed");
			return true;
			
		} catch(Exception e) {			
			compressResult.completeAsError(messageProvider, "dmc.distribute_module_compress_error", e);
			executionResult.completeAsError(messageProvider, "dmc.distribute_module_compress_error2", e);
			return false;			
		}
	}

		
	@Override
	public void entryProcessed(ZipArchiveEntry entry) {		
		if(entry == null) return;

		// calculate size
		String size = null;
		if (entry.getSize() == -1 ) size = "0";
		else size = Long.toString(entry.getSize());
		
		// create message
		Object[] args = {entry.getName(), size};
		String message = messageProvider.getMessage("dmc.compress_entry_info", args);		

		// add message
		compressResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
	}

	/**
	 * Upload module.
	 * 
	 * @param archiveFile archive file.
	 * 
	 * @return true if upload succeeds. 
	 */
	boolean uploadModule(File archiveFile) {
		
		try {

			// upload compressed module
			FileSystemResource fsResource = new FileSystemResource(archiveFile);
			MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
			parts.add(DISTRIBUTE_MODULE_FILE_PART, fsResource);
			
			// create HTTP headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			HttpEntity<Object> request = new HttpEntity<Object>(parts, headers);

			// post
			session.httpPost(DISTRIBUTE_MODULE_URI, request);
			
			return true;
		
		} catch(Exception e) {
			Object[] args = { e.getMessage() };			
			executionResult.completeAsError(messageProvider, "dmc.distribute_module_upload_error", args, e);
			return false;			
		}
		
	}
		
}
