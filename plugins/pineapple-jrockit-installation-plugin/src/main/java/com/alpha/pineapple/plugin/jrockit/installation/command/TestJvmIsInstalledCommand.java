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


package com.alpha.pineapple.plugin.jrockit.installation.command;

import java.io.File;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;

import com.alpha.javautils.ExecutableNameUtils;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.pineapple.test.Asserter;
import com.alpha.pineapple.test.matchers.PineappleMatchers;

/**
 * <p>Implementation of the <code>org.apache.commons.chain.Command</code>  interface which 
 * tests whether JVM is correctly installed.</p>
 * 
 * <p>Precondition for execution of the command is definition of these keys in 
 * the context:
 * 
 * <ul>
 * <li><code>jvm-executable-dir</code> Defines the absolute path to the JVM executable. The type is 
 * <code>java.io.File</code>.</li>
 * 
 * <li><code>external-process-session</code> defines external process execution session. The type is 
 * <code>com.alpha.pineapple.session.ProcessExecutionSession</code>.</li>
 * 
 * <li><code>version-info</code> defines JVM version info. The type is 
 * <code>java.lang.String</code>.</li>
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
 * <li>No additional keys defined.</li>
 * </ul>
 * </p> 
 * 
 * <p>Postcondition after execution of the command is: 
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
public class TestJvmIsInstalledCommand implements Command {

	/**
	 * Version argument.
	 */
	static final String VERSION_ARG = "-version";
	
	/**
	 * Default process time out (ms).
	 */
	static final int DEFAULT_PROCESS_TIMEOUT = 1000*60*10;
	
	/**
	 * Second list index.
	 */
	static final int SECOND_INDEX = 1;

	/**
	 * Message containing captured standard out.
	 */
	static final String STANDARD_OUT = "Standard Out";
	
    /**
     * Key used to identify property in context: Defines the absolute path to the JVM executable. 
     */
    public static final String JVM_EXECUTABLE_DIR_KEY = "jvm-executable-dir";

    /**
     * Key used to identify property in context: Contains execution result object.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";

    /**
     * Key used to identify property in context: External process execution session. 
     */
    public static final String SESSION_KEY = "external-process-session";

    /**
     * Key used to identify property in context: JVM version info. 
     */
    public static final String VERSION_KEY = "version-info";
    
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * Defines the absolute path to the JVM executable.
     */
    @Initialize( JVM_EXECUTABLE_DIR_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )        
    File jvmExecutableDir;

    /**
     * Defines the external process execution session.
     */
    @Initialize( SESSION_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )        
    ProcessExecutionSession session;

    /**
     * Defines the JVM version info.
     */
    @Initialize( VERSION_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )        
    String versionInfo;
    
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
     * Hamcrest asserter.
     */
    @Resource
    Asserter asserter;

    /**
     * Pineapple matcher factory.
     */
    @Resource
    PineappleMatchers pineappleMatchers;
        
    public boolean execute( Context context ) throws Exception 
    {    
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("tjiic.start" ) );        	
        }
    	
        // initialize command
        CommandInitializer initializer =  new CommandInitializerImpl();
        initializer.initialize( context, this );
        
        // register execution result with asserter
        asserter.setExecutionResult(executionResult);
        
        // create XML
        runTests( context );        

        // compute execution state from children
        executionResult.completeAsComputed(messageProvider, "tjiic.completed", null, "tjiic.failed", null );    		                        		
                
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("tjiic.completed" ) );        	
        }
        
        return Command.CONTINUE_PROCESSING;
    }

    
    /**
	 * Run Tests.
     * 
     * @param context
     *            Command context.
     * 
     * @throws Exception
     *             If execution fails.
     */
    void runTests( Context context ) throws Exception
    {
    
		// create JVM executable 
		File jvmExecutable = ExecutableNameUtils.resolveJavaExecutableWithExtension(jvmExecutableDir);

		// assert JVM executable is valid
		if(!isJvmExecutableValid(jvmExecutable)) return;
				
		// create argument list for test
		String[] arguments = {VERSION_ARG}; 

		// get description
    	Object[] args2 = { jvmExecutableDir, ReflectionToStringBuilder.toString(arguments) };    	        	
    	String description = messageProvider.getMessage("tjiic.execute", args2 );

		// create JVM executable without extension 
		File jvmExecutable2 = ExecutableNameUtils.resolveJavaExecutable(jvmExecutableDir);
		
		// escape JVM executable path if required
		jvmExecutable2 = ExecutableNameUtils.escapeExecutable(jvmExecutable2);		
		    	    	
		// execute external process    	
		session.execute(jvmExecutable2.toString(), arguments, DEFAULT_PROCESS_TIMEOUT, description, executionResult);
		
		// validate version info
		validateJvmVersionInfo(executionResult);		
    }    

	/**
	 * Asserts whether the JVM executable path points to a valid file. 
	 * 
	 * The result of the of the assertion is added as an execution result to
	 * current execution result object graph. 
	 * 
	 * @param jvmExecutable Path to the JVM executable. 
	 * 
	 * @return True if the JVM executable exists as an file.
	 */
	boolean isJvmExecutableValid(File jvmExecutable) {
		
		// validate JVM executable exists
		Matcher<File> fileMatcher = pineappleMatchers.doesFileExist();	
    	Object[] args = { jvmExecutable };    	        	
    	String description = messageProvider.getMessage("tjiic.assert_jvmexe_exists", args );
    	
    	// assert and return result
		asserter.assertObject(jvmExecutable, fileMatcher, description);							
		return asserter.lastAssertionSucceeded();
	}    
	
	/**
	 * Asserts whether the JVM executable returned correct version info. 
	 * 
	 * The result of the of the assertion is added as an execution result to
	 * current execution result object graph. 
	 * 
	 * @param executionResult Execution result for the operation.   
	 */	
	void validateJvmVersionInfo(ExecutionResult executionResult) {
		
		// get child result
		ExecutionResult[] childResults = executionResult.getChildren();
		ExecutionResult childResult = childResults[SECOND_INDEX]; 
		
		// exit if child wasn't successful
		if( childResult.getState() != ExecutionState.SUCCESS ) return;
		
		// get standard out
		Map<String, String> messages = childResult.getMessages();
				
		// get message
		String message = messages.get(STANDARD_OUT);						
			
		// validate JVM return correct version info 
		Matcher<String> stringMatcher = StringContains.containsString(versionInfo);	
    	Object[] args = { versionInfo };    	        	
    	String description = messageProvider.getMessage("tjiic.assert_jvmversion_exists", args );
    	
    	// assert 
		asserter.assertObject(message, stringMatcher, description);											
	}
	
}
