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

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
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

@PluginOperation( OperationNames.TEST )
public class TestOperation implements Operation
{

	/**
	 * Message containing captured standard out.
	 */
	static final String STANDARD_OUT = "Standard Out";

	/**
	 * Version argument.
	 */
	static final String VERSION_ARG = "-version";

	/**
	 * Default process time out (ms).
	 */
	static final int DEFAULT_PROCESS_TIMEOUT = 1000*60*10;
	
	/**
	 * First list index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * Second list index.
	 */
	static final int SECOND_INDEX = 1;
	
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
     * Command runner
     */
    @Resource
    CommandRunner commandRunner;   

    /**
     * Hamcrest asserter.
     */
    @Resource
    Asserter asserter;
    
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
     * Test JVM is installed command.
     */
    @Resource
    Command testJvmIsInstalledCommand;
    
    /**
     * Pineapple matcher factory.
     */
    @Resource
    PineappleMatchers pineappleMatchers;
                       
               
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
        	String message = messageProvider.getMessage("to.start", args );
        	logger.debug( message );
        }

        // throw exception if required type isn't available
        if ( !( content instanceof JRockitInstallation ) )
        {
        	Object[] args = { JRockitInstallation.class };    	        	
        	String message = messageProvider.getMessage("to.content_typecast_failed", args );
            throw new PluginExecutionFailedException( message );
        }

        // throw exception if required type isn't available
        if ( !( session instanceof ProcessExecutionSession ) )
        {
        	Object[] args = { ProcessExecutionSession .class };    	        	
        	String message = messageProvider.getMessage("to.session_typecast_failed", args );
            throw new PluginExecutionFailedException( message );
        }
                
        // configure command runner with execution result
        commandRunner.setExecutionResult( executionResult );
        
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
                                    
            // run tests
            runTests(model, externalProcessSession, executionResult);
                                    
            // compute execution state from children
            executionResult.completeAsComputed(messageProvider, "to.completed", null, "to.failed", null );    		                        
        }
        catch ( Exception e )
        {
        	Object[] args = { e.toString() };    	        	
        	String message = messageProvider.getMessage("to.error", args );
            throw new PluginExecutionFailedException( message, e );
        }
    }

    /**
     * Run Tests. 
     *  
     * @param model Plugin model.
     * @param session Plugin session.
     * @param executionResult execution result.
     * 
     * @throws IOException if execution fails. 
     */
	@SuppressWarnings("unchecked")
	void runTests(JRockitInstallation model, ProcessExecutionSession session, ExecutionResult executionResult) {

		//exit if no model content is defined.
		if (!isModelContentDefined(model)) return;
		
		// get installer configurer for current installer
		InstallationConfigurer installationConfigurer = getInstallerConfigurer(model); 		
								
    	// create description
    	String message = messageProvider.getMessage("to.execute" );        	
    	
        // create context
        Context context = commandRunner.createContext();            
        
        // initialize context
        context.put(TestJvmIsInstalledCommand.JVM_EXECUTABLE_DIR_KEY, installationConfigurer.getJvmExecutablePath(model) );
        context.put(TestJvmIsInstalledCommand.SESSION_KEY, session );
        context.put(TestJvmIsInstalledCommand.VERSION_KEY, installationConfigurer.getVersion());
        
        // run test            
        commandRunner.run(testJvmIsInstalledCommand, message, context);                        
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
			
}
