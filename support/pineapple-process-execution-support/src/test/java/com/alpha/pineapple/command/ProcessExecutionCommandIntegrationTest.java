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


package com.alpha.pineapple.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.testutils.ObjectMotherScript;

/**
 * Unit test of the {@link ProcessExecutionCommand} class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.process.execution-config.xml" })
public class ProcessExecutionCommandIntegrationTest {

	/**
	 * Expected number of results.
	 */
    static final int EXPECTED_RESULTS = 6;
	
	/**
	 * NULL time out value.
	 */
	static final Long NULL_TIMEOUT = null;
	
	/**
	 * Object under test.
	 */
	@Resource
	Command processExecutionCommand;

	/**
	 * Test script directory
	 */
	File testScriptsDir = new File(SystemUtils.getUserDir(), "/src/test/resources/");

	/**
	 * command context;
	 */
	ContextBase context;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;    
	
	/**
	 * Script mother. 
	 */
	ObjectMotherScript mother = new ObjectMotherScript(); 
	
	@Before
	public void setUp() throws Exception {
		
		// context
		context = new ContextBase();
		
        // create execution result
		executionResult = new ExecutionResultImpl( "Root result" );		
	}

	@After
	public void tearDown() throws Exception {
		context = null;
	}

	/**
	 * Test that command can be looked up from the context.
	 */
	@Test
	public void testCanGetCommandFromContext() {
		assertNotNull(processExecutionCommand);
	}

	/**
	 * Test that process can be executed.
	 * 
	 * @throws Exception If test fails
	 */
	@Test
	public void testCanExecuteProcess() throws Exception {
		
		File testscript = mother.resolveOsSpecificScriptName(testScriptsDir + "/test");				
		String[] arguments = {};		
		
    	// create execution result        	
    	ExecutionResult childResult = executionResult.addChild("Execute external process.");        	
				
		// configure context
		context.put(ProcessExecutionCommand.EXECUTABLE_KEY, testscript.toString());
		context.put(ProcessExecutionCommand.ARGUMENTS_KEY, arguments);
		context.put(ProcessExecutionCommand.TIMEOUT_KEY, NULL_TIMEOUT);		
		context.put(ProcessExecutionCommand.EXECUTIONRESULT_KEY, childResult );
		
		// execute command
		processExecutionCommand.execute(context);
				
        // test
        assertEquals( ExecutionState.SUCCESS, childResult.getState());
        assertEquals( EXPECTED_RESULTS, childResult.getMessages().size());
		
        Map<String, String> messages = childResult.getMessages();		
        assertEquals("TEST..", messages.get("Standard Out")); 
	}
	
	/**
	 * Test that process can be execution fails if
	 * executable is unknown
	 * 
	 * @throws Exception If test fails
	 */
	@Test
	public void testCanExecutionFailsIfExecutableIsUnknown() throws Exception {
		
		File testscript = mother.resolveOsSpecificScriptName(testScriptsDir + "/unknown-test");				
		String[] arguments = {};		
		
    	// create execution result        	
    	ExecutionResult childResult = executionResult.addChild("Execute external process.");        	
				
		// configure context
		context.put(ProcessExecutionCommand.EXECUTABLE_KEY, testscript.toString());
		context.put(ProcessExecutionCommand.ARGUMENTS_KEY, arguments);
		context.put(ProcessExecutionCommand.TIMEOUT_KEY, NULL_TIMEOUT);				
		context.put(ProcessExecutionCommand.EXECUTIONRESULT_KEY, childResult );
		
		// execute command
		processExecutionCommand.execute(context);
				
        // test
        assertEquals( ExecutionState.ERROR, childResult.getState());
        assertEquals( EXPECTED_RESULTS, childResult.getMessages().size());
		
        Map<String, String> messages = childResult.getMessages();		
        assertEquals("", messages.get("Standard Out")); 
	}

	/**
	 * Test that process can be executed with single argument
	 * 
	 * @throws Exception If test fails
	 */
	@Test
	public void testCanExecuteProcessWithSingleArgument() throws Exception {
		
		File testscript = mother.resolveOsSpecificScriptName(testScriptsDir + "/test");				
		String[] arguments = {"ARG1" };		
		
    	// create execution result        	
    	ExecutionResult childResult = executionResult.addChild("Execute external process.");        	
				
		// configure context
		context.put(ProcessExecutionCommand.EXECUTABLE_KEY, testscript.toString());
		context.put(ProcessExecutionCommand.ARGUMENTS_KEY, arguments);
		context.put(ProcessExecutionCommand.TIMEOUT_KEY, NULL_TIMEOUT);				
		context.put(ProcessExecutionCommand.EXECUTIONRESULT_KEY, childResult );
		
		// execute command
		processExecutionCommand.execute(context);
				
        // test
        assertEquals( ExecutionState.SUCCESS, childResult.getState());
        assertEquals( EXPECTED_RESULTS, childResult.getMessages().size());
		
        Map<String, String> messages = childResult.getMessages();		
        assertEquals("TEST..ARG1", messages.get("Standard Out")); 
	}

	/**
	 * Test that process is killed after 5000ms if 
	 * no time out value is defined.
	 * 
	 * @throws Exception If test fails
	 */
	@Test
	public void testProcessIsKilledAfter5000msByDefault() throws Exception {
		
		File testscript = mother.resolveOsSpecificScriptName(testScriptsDir + "/forever");				
		String[] arguments = {};		
		
    	// create execution result        	
    	ExecutionResult childResult = executionResult.addChild("Execute external process.");        	
				
		// configure context
		context.put(ProcessExecutionCommand.EXECUTABLE_KEY, testscript.toString());
		context.put(ProcessExecutionCommand.ARGUMENTS_KEY, arguments);
		context.put(ProcessExecutionCommand.TIMEOUT_KEY, NULL_TIMEOUT);				
		context.put(ProcessExecutionCommand.EXECUTIONRESULT_KEY, childResult );
		
		// execute command
		processExecutionCommand.execute(context);
				
        // test
        assertEquals( ExecutionState.ERROR, childResult.getState());
        assertEquals( EXPECTED_RESULTS, childResult.getMessages().size());
		
        Map<String, String> messages = childResult.getMessages();		
        assertEquals("x.\r\nx.\r\nx.\r\nx.\r\nx.", messages.get("Standard Out")); 
	}

	/**
	 * Test that process is killed after 5000ms if 
	 * 0 ms out value is defined.
	 * 
	 * @throws Exception If test fails
	 */
	@Test
	public void testProcessIsKilledAfter5000msIf0msIsDefined() throws Exception {
		
		File testscript = mother.resolveOsSpecificScriptName(testScriptsDir + "/forever");				
		String[] arguments = {};		
		
    	// create execution result        	
    	ExecutionResult childResult = executionResult.addChild("Execute external process.");        	
				
		// configure context
		context.put(ProcessExecutionCommand.EXECUTABLE_KEY, testscript.toString());
		context.put(ProcessExecutionCommand.ARGUMENTS_KEY, arguments);
		context.put(ProcessExecutionCommand.TIMEOUT_KEY, new Long(0));				
		context.put(ProcessExecutionCommand.EXECUTIONRESULT_KEY, childResult );
		
		// execute command
		processExecutionCommand.execute(context);
				
        // test
        assertEquals( ExecutionState.ERROR, childResult.getState());
        assertEquals( EXPECTED_RESULTS, childResult.getMessages().size());
		
        Map<String, String> messages = childResult.getMessages();		
        assertEquals("x.\r\nx.\r\nx.\r\nx.\r\nx.", messages.get("Standard Out")); 
	}
	
	
	/**
	 * Test that process is killed after 1000ms if 
	 * time out value is defined.
	 * 
	 * @throws Exception If test fails
	 */
	@Test
	public void testProcessIsKilledAfter1000ms() throws Exception {
		
		File testscript = mother.resolveOsSpecificScriptName(testScriptsDir + "/forever");				
		String[] arguments = {};		
		
    	// create execution result        	
    	ExecutionResult childResult = executionResult.addChild("Execute external process.");        	
				
		// configure context
		context.put(ProcessExecutionCommand.EXECUTABLE_KEY, testscript.toString());
		context.put(ProcessExecutionCommand.ARGUMENTS_KEY, arguments);
		context.put(ProcessExecutionCommand.TIMEOUT_KEY, new Long(1000));				
		context.put(ProcessExecutionCommand.EXECUTIONRESULT_KEY, childResult );
		
		// execute command
		processExecutionCommand.execute(context);
				
        // test
        assertEquals( ExecutionState.ERROR, childResult.getState());
        assertEquals( EXPECTED_RESULTS, childResult.getMessages().size());
		
        Map<String, String> messages = childResult.getMessages();		
        assertEquals("x.", messages.get("Standard Out")); 
	}

	/**
	 * Test that process is killed after 5000ms if 
	 * time out value is defined.
	 * 
	 * @throws Exception If test fails
	 */
	@Test
	public void testProcessIsKilledAfter5000ms() throws Exception {
		
		File testscript = mother.resolveOsSpecificScriptName(testScriptsDir + "/forever");				
		String[] arguments = {};		
		
    	// create execution result        	
    	ExecutionResult childResult = executionResult.addChild("Execute external process.");        	
				
		// configure context
		context.put(ProcessExecutionCommand.EXECUTABLE_KEY, testscript.toString());
		context.put(ProcessExecutionCommand.ARGUMENTS_KEY, arguments);
		context.put(ProcessExecutionCommand.TIMEOUT_KEY, new Long(5000));				
		context.put(ProcessExecutionCommand.EXECUTIONRESULT_KEY, childResult );
		
		// execute command
		processExecutionCommand.execute(context);
				
        // test
        assertEquals( ExecutionState.ERROR, childResult.getState());
        assertEquals( EXPECTED_RESULTS, childResult.getMessages().size());
		
        Map<String, String> messages = childResult.getMessages();		
        assertEquals("x.\r\nx.\r\nx.\r\nx.\r\nx.", messages.get("Standard Out"));  
	}
	
}
