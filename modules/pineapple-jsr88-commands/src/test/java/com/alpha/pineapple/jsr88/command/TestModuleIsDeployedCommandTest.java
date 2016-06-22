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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.testutils.MockDeploymentManager;
import com.alpha.testutils.MockTarget;

public class TestModuleIsDeployedCommandTest
{

    /**
     * Command under test.
     */
    Command command;

    @Before
    public void setUp() throws Exception
    {
        command = new TestModuleIsDeployedCommand();        
    }
    
    @After
    public void tearDown() throws Exception
    {
        command = null;
    }
      
    /**
     * Test that command fails if description key is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfDescriptionKeyIsUndefinedInContext() throws Exception
    {
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // create target list
        String[] targets;
        targets = new String[1];
        targets[0] = "target1";
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, "deployed-app-1.0" );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        

        // execute command
        command.execute( context );
    }

    /**
     * Test that command fails if description value is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfDescriptionValueIsUndefinedInContext() throws Exception
    {
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // create target list
        String[] targets;
        targets = new String[1];
        targets[0] = "target1";
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, null );        
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, "deployed-app-1.0" );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        

        // execute command
        command.execute( context );
    }

    /**
     * Test that command fails if manager key is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfManagerKeyIsUndefinedInContext() throws Exception
    {        
        // create target list
        String[] targets;
        targets = new String[1];
        targets[0] = "target1";
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, "deployed-app-1.0" );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        

        // execute command
        command.execute( context );
    }
    
    
    /**
     * Test that command fails if manager value is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfManagerValueIsUndefinedInContext() throws Exception
    {        
        // create target list
        String[] targets;
        targets = new String[1];
        targets[0] = "target1";
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, null );        
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, "deployed-app-1.0" );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        

        // execute command
        command.execute( context );
    }

    /**
     * Test that command fails if module key is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfModuleKeyIsUndefinedInContext() throws Exception
    {
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // create target list
        String[] targets;
        targets = new String[1];
        targets[0] = "target1";
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                                               
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        

        // execute command
        command.execute( context );
    }

    /**
     * Test that command fails if module value is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfModuleValueIsUndefinedInContext() throws Exception
    {
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // create target list
        String[] targets;
        targets = new String[1];
        targets[0] = "target1";
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                                               
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                        
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, null );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        

        // execute command
        command.execute( context );
    }
    

    /**
     * Test that command fails if targets key is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfTargetsKeyIsUndefinedInContext() throws Exception
    {
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                                               
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );          
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, "deployed-app-1.0" );                

        // execute command
        command.execute( context );
    }

    
    /**
     * Test that command fails if targets value is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfTargetsValueIsUndefinedInContext() throws Exception
    {
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                                               
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );          
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, "deployed-app-1.0" );                
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, null );
        
        // execute command
        command.execute( context );
    }
    
        
    /**
     * Test that context contains message after execution.
     * @throws Exception
     */
    @Test
    public void testMessageIsDefinedInContextAfterExecution() throws Exception
    {
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // create target list
        String[] targets;
        targets = new String[1];
        targets[0] = "target1";
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                        
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, "deployed-app-1.0" );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        
        
        command.execute( context );
        
        // get manager
        Object value = context.get( TestModuleIsDeployedCommand.MESSAGE_KEY );
        assertTrue( value instanceof String );       
    }

    /**
     * Test that context contains test result after execution.
     * 
     * @throws Exception
     */
    @Test
    public void testTestResultIsDefinedInContextAfterExecution() throws Exception
    {
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // create target list
        String[] targets;
        targets = new String[1];
        targets[0] = "target1";
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                        
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, "deployed-app-1.0" );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        
        
        command.execute( context );
        
        // get manager
        Object value = context.get( TestModuleIsDeployedCommand.RESULT_KEY );
        assertTrue( value instanceof Boolean );       
    }

    /**
     * Test that execution of the test commands succeeds if module is deployed 
     * to zero targets.
     * 
     * @throws Exception
     */
    @Test
    public void testTestSuccededIfModuleIsDeployedToZeroTargets() throws Exception
    {
        final String TARGET = "target1";
        final String WAR_MODULE = "deployed-app-1.0";
        
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // add targets to mock manager
        manager.targets.add( new MockTarget(TARGET, "target1 description") );

        // create empty target list for test command
        String[] targets;
        targets = new String[0];        
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                        
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, WAR_MODULE );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        
        
        command.execute( context );
        
        // get manager
        Boolean value = (Boolean) context.get( TestModuleIsDeployedCommand.RESULT_KEY );
        assertTrue( value);                
    }
    
    /**
     * Test that execution of the test commands succeeds if module is deployed 
     * to single expected target.
     * 
     * @throws Exception
     */
    @Test
    public void testTestSuccededIfModuleIsDeployedToSingleTarget() throws Exception
    {
        final String TARGET = "target1";
        final String WAR_MODULE = "deployed-app-1.0";
        
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // add targets to mock manager
        manager.targets.add( new MockTarget(TARGET, "target1 description") );

        // add deployment to mock manager
        manager.addMockWarModule( WAR_MODULE, TARGET );
        
        // create target list for test command
        String[] targets;
        targets = new String[1];        
        targets[0] = TARGET;
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                        
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, WAR_MODULE );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        
        
        command.execute( context );
        
        // get manager
        Boolean value = (Boolean) context.get( TestModuleIsDeployedCommand.RESULT_KEY );
        assertTrue( value);                
    }
    
    /**
     * Test that execution of the test commands succeeds if module is deployed 
     * to multiple expected targets.
     * 
     * @throws Exception
     */
    @Test
    public void testTestSuccededIfModuleIsDeployedToMultipleTargets() throws Exception
    {
        final String TARGET1 = "target1";
        final String TARGET2 = "target2";
        final String WAR_MODULE = "deployed-app-1.0";
        
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // add targets to mock manager
        manager.targets.add( new MockTarget(TARGET1, "target1 description" ) );
        manager.targets.add( new MockTarget(TARGET2, "target2 description" ) );

        // add deployment to mock manager
        manager.addMockWarModule( WAR_MODULE, TARGET1 );
        manager.addMockWarModule( WAR_MODULE, TARGET2 );        
        
        // create target list for test command
        String[] targets;
        targets = new String[2];        
        targets[0] = TARGET1;
        targets[1] = TARGET2;        
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                        
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, WAR_MODULE );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        
        
        command.execute( context );
        
        // get manager
        Boolean value = (Boolean) context.get( TestModuleIsDeployedCommand.RESULT_KEY );
        assertTrue( value);                
    }
    
    /**
     * Test that execution of the test commands succeeds if module is deployed 
     * to expected target but different targets exists in domain.
     * 
     * @throws Exception
     */
    @Test
    public void testTestSuccededIfModuleIsDeployedAndOtherTargetsExists() throws Exception
    {
        final String TARGET1 = "target1";
        final String TARGET2 = "target2";        
        final String WAR_MODULE = "deployed-app-1.0";
        
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // add targets to mock manager
        manager.targets.add( new MockTarget(TARGET1, "target1 description") );
        manager.targets.add( new MockTarget(TARGET2, "target2 description") );        

        // add deployment to mock manager
        manager.addMockWarModule( WAR_MODULE, TARGET1 );
        
        // create target list for test command
        String[] targets;
        targets = new String[1];        
        targets[0] = TARGET1;
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                        
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, WAR_MODULE );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        
        
        command.execute( context );
        
        // get manager
        Boolean value = (Boolean) context.get( TestModuleIsDeployedCommand.RESULT_KEY );
        assertTrue( value);                
    }

    /**
     * Test that execution of the test commands succeeds if module is deployed 
     * to expected target but different targets exists in domain. module is 
     * deployed to target2. 
     * 
     * @throws Exception
     */
    @Test
    public void testTestSuccededIfModuleIsDeployedAndOtherTargetsExists2() throws Exception
    {
        final String TARGET1 = "target1";
        final String TARGET2 = "target2";        
        final String WAR_MODULE = "deployed-app-1.0";
        
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // add targets to mock manager
        manager.targets.add( new MockTarget(TARGET1, "target1 description") );
        manager.targets.add( new MockTarget(TARGET2, "target2 description") );        

        // add deployment to mock manager, target 2
        manager.addMockWarModule( WAR_MODULE, TARGET2 );
        
        // create target list for test command
        String[] targets;
        targets = new String[1];        
        targets[0] = TARGET2;
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                        
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, WAR_MODULE );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        
        
        command.execute( context );
        
        // get manager
        Boolean value = (Boolean) context.get( TestModuleIsDeployedCommand.RESULT_KEY );
        assertTrue( value);                
    }

    
    /**
     * Test that execution of the test commands fails if module isn't deployed 
     * to expected targets:
     * 
     * existing targets = t1,t2. 
     * expected targets = t1,t2.
     * actual targets = t1.
     * 
     * @throws Exception
     */
    @Test
    public void testTestFailsIfModuleTargetsDoesntMatch1() throws Exception
    {
        final String TARGET1 = "t1";
        final String TARGET2 = "t2";
        final String WAR_MODULE = "deployed-app-1.0";
        
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // add existing targets to mock manager
        manager.targets.add( new MockTarget(TARGET1, "target1 description" ) );
        manager.targets.add( new MockTarget(TARGET2, "target2 description" ) );

        // add actual targets (to to mock manager) 
        manager.addMockWarModule( WAR_MODULE, TARGET1 );       
                
        // create expected targets (list to test command) 
        String[] targets;
        targets = new String[2];        
        targets[0] = TARGET1;
        targets[1] = TARGET2;        
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                        
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, WAR_MODULE );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        
        
        command.execute( context );
        
        // get manager
        Boolean value = (Boolean) context.get( TestModuleIsDeployedCommand.RESULT_KEY );
        assertFalse( value);                
    }

    
    /**
     * Test that execution of the test commands fails if module isn't deployed 
     * to expected targets:
     * 
     * existing targets = t1,t2.  
     * expected targets = t1.
     * actual targets = t1,t2.
     * 
     * @throws Exception
     */
    @Test
    public void testTestFailsIfModuleTargetsDoesntMatch2() throws Exception
    {
        final String TARGET1 = "t1";
        final String TARGET2 = "t2";
        final String WAR_MODULE = "deployed-app-1.0";
        
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // add existing targets to mock manager
        manager.targets.add( new MockTarget(TARGET1, "target1 description" ) );
        manager.targets.add( new MockTarget(TARGET2, "target2 description" ) );

        // add actual targets (to to mock manager) 
        manager.addMockWarModule( WAR_MODULE, TARGET1 );       
        manager.addMockWarModule( WAR_MODULE, TARGET2 );        
        
        // create expected targets (list to test command) 
        String[] targets;
        targets = new String[1];        
        targets[0] = TARGET1;       
        
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, "some test description" );                        
        context.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );                
        context.put( TestModuleIsDeployedCommand.MODULE_KEY, WAR_MODULE );        
        context.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets);        
        
        command.execute( context );
        
        // get manager
        Boolean value = (Boolean) context.get( TestModuleIsDeployedCommand.RESULT_KEY );
        assertFalse( value);                
    }
    
}
