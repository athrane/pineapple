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


package com.alpha.pineapple.jsr88.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.TargetModuleID;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.jsr88.command.result.AvailableModulesResult;
import com.alpha.testutils.MockDeploymentManager;

public class GetAvailableModulesCommandTest
{

    /**
     * Command under test.
     */
    Command command;

    @Before
    public void setUp() throws Exception
    {
        command = new GetAvailableModulesCommand();        
    }
    
    @After
    public void tearDown() throws Exception
    {
        command = null;
    }

    /**
     * Test that command can be run on mock deployment manager.
     * 
     * @throws Exception
     */
    @Test
    public void testCanGetResultObjectFromMockDeploymentManager() throws Exception
    {
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetAvailableModulesCommand.MANAGER_KEY, manager  );
        
        // execute command        
        command.execute( context );
        
        // get result
        AvailableModulesResult result;
        result = (AvailableModulesResult) context.get( GetAvailableModulesCommand.RESULT_KEY );

        // test
        assertNotNull(result);
        
        // release manager
        manager.release();
    }

    
    /**
     * Test that result object is defined in context after execution.
     * 
     * @throws Exception
     */
    @Test
    public void testResultObjectIsDefinedInContextAfterExecution() throws Exception
    {
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetAvailableModulesCommand.MANAGER_KEY, manager  );
        
        // execute command        
        command.execute( context );
        
        // get result
        AvailableModulesResult result;
        result = (AvailableModulesResult) context.get( GetAvailableModulesCommand.RESULT_KEY );

        // test
        assertNotNull(result);
    }
        
    /**
     * Test that command fails if manager key is undefined in context. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfManagerKeyIsUndefinedInContext() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();                

        // execute command
        command.execute( context );
    }

    /**
     * Test that command fails if manager value is undefined in context. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfManagerValueIsUndefinedInContext() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();                

        // add null value
        context.put( GetAvailableModulesCommand.MANAGER_KEY, null );        
        
        // execute command
        command.execute( context );
    }
    
    
    /**
     * Test that modules can be retrieved from local WebLogic server.
     * @throws Exception
     */
    @Test
    public void testCanGetWarTypeModules() throws Exception
    {
        // create deployment manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
               
        // add mock war module to manager
        manager.addMockWarModule( "mock-war-name" );
                
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetAvailableModulesCommand.MANAGER_KEY, manager );

        // create get deployment manager command
        command = new GetAvailableModulesCommand();
        
        // execute command
        command.execute( context );       
        
        // get result
        AvailableModulesResult result;
        result = (AvailableModulesResult) context.get( GetAvailableModulesCommand.RESULT_KEY );

        // get war modules
        TargetModuleID[] modules = result.getModules( ModuleType.WAR);
        
        // test 
        assertEquals( 1, modules.length );
        
        // release manager
        manager.release();
    }

    /**
     * Test that the result object is initialized with an empty array for
     * module type of which no instances exists in the application server.
     * @throws Exception
     */
    @Test
    public void testReturnsEmptyArrayForNonexistingModuleType() throws Exception
    {
        // create deployment manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
               
        // add mock war module to manager
        manager.addMockWarModule( "mock-war-name" );
                
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetAvailableModulesCommand.MANAGER_KEY, manager );

        // create get deployment manager command
        command = new GetAvailableModulesCommand();
        
        // execute command
        command.execute( context );       
        
        // get result
        AvailableModulesResult result;
        result = (AvailableModulesResult) context.get( GetAvailableModulesCommand.RESULT_KEY );

        // get ear modules
        TargetModuleID[] modules = result.getModules( ModuleType.EAR);
        
        // test 
        assertEquals( 0, modules.length );
        
        // release manager
        manager.release();
    }    
    
}
