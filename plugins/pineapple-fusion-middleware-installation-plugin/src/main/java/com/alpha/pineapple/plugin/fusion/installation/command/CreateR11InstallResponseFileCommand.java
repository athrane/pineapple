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


package com.alpha.pineapple.plugin.fusion.installation.command;

import static com.alpha.pineapple.plugin.fusion.installation.FusionMiddlewareInstallationConstants.RESPONSE_INSTALL_FILE;

import java.io.File;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.fusion.installation.response.ResponseFileBuilder;
import com.alpha.pineapple.test.AssertionHelper;

/**
 * <p>Implementation of the <code>org.apache.commons.chain.Command</code>  interface which 
 * creates a response file for installation of Fusion Middleware products.
 * 
 * <p>Precondition for execution of the command is definition of these keys in 
 * the context:
 * 
 * <ul>
 * <li><code>target-directory</code> defines the directory where the product is installed.
 * The type is <code>java.io.File</code>.</li>
 *
 * <li><code>execution-result</code> contains execution result object which collects
 * information about the execution of the test. The type is 
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>   
 * </ul>
 * </p>      
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in the context:
 * <ul>
 * <li><code>response-file</code> contains a file object which represents the created 
 * response file . The type is <code>java.io.File</code>.</li>
 * </ul>
 * </p> 
 * 
 * <p>Postcondition after execution of the command is: 
 * <ul> 
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the test succeeded. If the 
 * command failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the command fails due to an exception then the exception isn't caught, 
 * but passed on to the invoker whose responsibility it is to catch it and update 
 * the <code>ExecutionResult</code> with the state <code>ExecutionState.ERROR</code>.
 * </li>
 * </ul>  
 * </p>        
 */

public class CreateR11InstallResponseFileCommand implements Command {
		
	/**
     * System independent newline character.
     */	
	public static final String NEWLINE_CHAR = System.getProperty("line.separator");	
	
    /**
     * Key used to identify property in context: Target directory where product is installed.
     */
    public static final String TARGET_DIRECTORY = "target-directory";
    
    /**
     * Key used to identify property in context: Contains execution result object.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";

    /**
     * Key used to identify property in context: Contains returned response file.
     */    
	public static final Object RESPONSE_FILE = "response-file";
    
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * Target directory where JRockit is installed.
     */
    @Initialize( TARGET_DIRECTORY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )    
    File targetDirectory;
    
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
     * Runtime directory provider.
     */
    @Resource
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;
    
    /**
     * Hamcrest assertion helper
     */
    @Resource
    AssertionHelper assertionHelper;
         
    /**
     * Response file builder for product.
     * 
     * Property isn't set by auto wiring using the resource annotation
     * but instead, a setter method is used. 
     */
    ResponseFileBuilder responseFileBuilder;
    
    /**
     * Setter used by Spring to set the builder.
     * 
     * @param responseFileBuilder Product specific builder.
     */
    public void setResponseFileBuilder(ResponseFileBuilder responseFileBuilder) {
		this.responseFileBuilder = responseFileBuilder;
	}


	public boolean execute( Context context ) throws Exception 
    {        	
        // initialize command
        CommandInitializer initializer =  new CommandInitializerImpl();
        initializer.initialize( context, this );
                
        // create response file 
        createResponseFile( context );        
        
        return Command.CONTINUE_PROCESSING;
    }

    
    /**
	 * Create response file.
     * 
     * @param context
     *            Command context.
     * 
     * @throws Exception
     *             If execution fails.
     */
    void createResponseFile( Context context ) throws Exception
    {    	    	
		// get temporary directory
		File tempDirectory = coreRuntimeDirectoryProvider.getTempDirectory();
				    
		// create file object		
		File responseFile = new File(tempDirectory, RESPONSE_INSTALL_FILE);

		// delete file if it exists
		if(responseFile.exists()) {
			boolean succeded = responseFile.delete();
			
			// handle failure to delete file
			if(!succeded) {
		    	Object[] args = { responseFile };
		    	executionResult.completeAsFailure(messageProvider, "cr11irfc.delete_oldresponsefile_failed", args);
		    	return;				
			}			
		}
		
		// Oracle Common home is interpreted as the target directory 		
		File commonHomeDirectory = targetDirectory;
		
		// Middleware home is interpreted as the parent directory
		File middlewareHomeDirectory = commonHomeDirectory.getParentFile();				

		// create silent XML file
		Collection<String> lines = responseFileBuilder.getResponseForInstallation(commonHomeDirectory, middlewareHomeDirectory);
				
		// write the file
		FileUtils.writeLines(responseFile, lines);		
		
		// assert whether file exists		
		ExecutionResult result = assertionHelper.assertFileExist(responseFile, 
				messageProvider, 
				"cr11irfc.assert_responsefile_exists", 
				executionResult);
		
		// handle positive case
		if (result.getState() == ExecutionState.SUCCESS ) {
			
			// add file to context
			context.put(RESPONSE_FILE, responseFile);

			// complete as success
	    	Object[] args = { responseFile };
	    	executionResult.completeAsSuccessful(messageProvider, "cr11irfc.succeed", args);
	    	return;							
		}
				
		// add null to context
		context.put(RESPONSE_FILE, null);
		
		// complete as failure
    	Object[] args2 = { responseFile };
    	executionResult.completeAsFailure(messageProvider, "cr11irfc.failed", args2);		
    }    
            
}
