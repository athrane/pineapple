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


package com.alpha.pineapple.plugin.weblogic.jmx.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;

/**
 * Integration test for the <code>TestObjectIdentityCommand</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class TestObjectIdentityCommandIntegrationTest {

    /**
     * Context.
     */
    Context context;
    
    /**
     * Resolved type.
     */
    ResolvedType resolvedType;

	/**
	 * Mock execution result.
	 */
	ExecutionResult executionResult; 
	
    /**
     * Object under test.
     */
    @Resource
    Command testObjectIdentityCommand;
                    
	@Before
	public void setUp() throws Exception {
		
        // create context
        context = new ContextBase();

        // create result
        executionResult = new ExecutionResultImpl("root");        
	}

	@After
	public void tearDown() throws Exception {
		testObjectIdentityCommand = null;
        resolvedType = null;
        context = null;
        executionResult = null;		
	}

    /**
     * Test that command can be looked up from the context.
     */
    @Test
    public void testCanGetInstanceFromContext()
    {
        assertNotNull( testObjectIdentityCommand );
    }

    /**
     * Test that two identical strings are compared correct.
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCanTestTwoIdenticalStrings() throws Exception
    {
        // initialize resolved type
        String pp = "gogo";
        String sp = "gogo";            
        resolvedType =  ResolvedTypeImpl.createResolvedObject( pp, sp );
        
        // initialize context
        context.put(TestObjectIdentityCommand.RESOLVED_TYPE, resolvedType );
		context.put(TestObjectIdentityCommand.EXECUTIONRESULT_KEY, executionResult );            

        // execute command             
		testObjectIdentityCommand.execute(context);
    	
        // test
		assertFalse(executionResult.isExecuting());
		assertTrue(executionResult.isRoot());
		assertEquals(ExecutionResult.ExecutionState.SUCCESS, executionResult.getState());		
    }
    
    /**
     * Test that two identical strings are compared correct.
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfTestIsSuccessfulButChildTestFailed() throws Exception
    {
        // initialize resolved type
        String pp = "gogo";
        String sp = "gogo";            
        resolvedType =  ResolvedTypeImpl.createResolvedObject( pp, sp );
       
        // add failed child
        ExecutionResult childResult = executionResult.addChild("child");
        childResult.setState(ExecutionResult.ExecutionState.FAILURE);
        
        // initialize context
        context.put(TestObjectIdentityCommand.RESOLVED_TYPE, resolvedType );
		context.put(TestObjectIdentityCommand.EXECUTIONRESULT_KEY, executionResult );            

        // execute command             
		testObjectIdentityCommand.execute(context);
    	
        // test
		assertFalse(executionResult.isExecuting());
		assertTrue(executionResult.isRoot());
		assertEquals(ExecutionResult.ExecutionState.FAILURE, executionResult.getState());		
    }
    
    
    
}
