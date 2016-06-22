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

import static org.junit.Assert.assertTrue;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.testutils.MockDeploymentManager;

public class ReleaseDeploymentManagerCommandTest
{

    /**
     * Command under test.
     */
    Command command;

    @Before
    public void setUp() throws Exception
    {
        command = new ReleaseDeploymentManagerCommand();        
    }
    
    @After
    public void tearDown() throws Exception
    {
        command = null;
    }
        
    @Test
    public void testCanReleaseManager() throws Exception
    {
        // create manager
        MockDeploymentManager manager;
        manager =  new MockDeploymentManager();
        
        // create context
        Context context;
        context = new ContextBase();
        context.put( ReleaseDeploymentManagerCommand.MANAGER_KEY, manager  );
        
        command.execute( context );
        
        // test that release is invoked
        assertTrue(manager.isReleased);
    }

    /**
     * Test that command fails if manager key is undefined. 
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
     * Test that command fails if manager value is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfManagerValueIsUndefinedInContext() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();                
        context.put( ReleaseDeploymentManagerCommand.MANAGER_KEY, null );        

        // execute command
        command.execute( context );
    }
   
}
