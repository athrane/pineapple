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

import javax.enterprise.deploy.spi.DeploymentManager;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;

public class GetDeploymentManagerCommandTest
{

    /**
     * Command under test.
     */
    Command command;

    @Before
    public void setUp() throws Exception
    {
        command = new GetDeploymentManagerCommand();        
    }
    
    @After
    public void tearDown() throws Exception
    {
        command = null;
    }

    /**
     * Test that deployment manager can be retrieved from local Weblogic.
     * @throws Exception
     */
    @Test
    public void testCanGetDeploymentManagerFromLocalWebLogic() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, "weblogic" );
        context.put( GetDeploymentManagerCommand.PASSWORD_KEY, "weblogic" );                
        context.put( GetDeploymentManagerCommand.URI_KEY, "deployer:WebLogic:t3://127.0.0.1:7001" );    
        context.put( GetDeploymentManagerCommand.FACTORY_KEY, "weblogic.deploy.api.spi.factories.internal.DeploymentFactoryImpl" );
        
        command.execute( context );     
        
        // release manager
        Object value = context.get( GetDeploymentManagerCommand.MANAGER_KEY );        
        DeploymentManager manager;
        manager = (DeploymentManager) value;
        manager.release();        
    }
    
    /**
     * Test that deployment manager can be retrieved from local Glassfish.
     * @throws Exception
     */
    @Test
    public void testCanGetDeploymentManagerFromLocalGlassfish() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, "admin" );
        context.put( GetDeploymentManagerCommand.PASSWORD_KEY, "adminadmin" );                
        context.put( GetDeploymentManagerCommand.URI_KEY, "deployer:Sun:AppServer::localhost:4848" );            
        context.put( GetDeploymentManagerCommand.FACTORY_KEY, "com.sun.enterprise.deployapi.SunDeploymentFactory" );
        
        command.execute( context );     
        
        // release manager
        Object value = context.get( GetDeploymentManagerCommand.MANAGER_KEY );        
        DeploymentManager manager;
        manager = (DeploymentManager) value;
        manager.release();
        
    }

    /**
     * Test that command fails user key is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfUserKeyIsUndefinedInContext() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, null );
        context.put( GetDeploymentManagerCommand.PASSWORD_KEY, "weblogic" );                
        context.put( GetDeploymentManagerCommand.URI_KEY, "deployer:WebLogic:t3://127.0.0.1:7001" );    
        context.put( GetDeploymentManagerCommand.FACTORY_KEY, "weblogic.deploy.api.spi.factories.internal.DeploymentFactoryImpl" );
                

        // execute command
        command.execute( context );
    }
    
    /**
     * Test that command fails user value is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfUserValueIsUndefinedInContext() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, null );
        context.put( GetDeploymentManagerCommand.PASSWORD_KEY, "weblogic" );                
        context.put( GetDeploymentManagerCommand.URI_KEY, "deployer:WebLogic:t3://127.0.0.1:7001" );    
        context.put( GetDeploymentManagerCommand.FACTORY_KEY, "weblogic.deploy.api.spi.factories.internal.DeploymentFactoryImpl" );
                
        // execute command
        command.execute( context );
    }

    /**
     * Test that command fails if password key is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfPasswordKeyIsUndefinedInContext() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, "admin" );                
        context.put( GetDeploymentManagerCommand.URI_KEY, "deployer:Sun:AppServer::localhost:4848" );            
        context.put( GetDeploymentManagerCommand.FACTORY_KEY, "com.sun.enterprise.deployapi.SunDeploymentFactory" );

        // execute command
        command.execute( context );
    }
        
    /**
     * Test that command fails if password value is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfPasswordValueIsUndefinedInContext() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, "admin" );
        context.put( GetDeploymentManagerCommand.PASSWORD_KEY, null );                
        context.put( GetDeploymentManagerCommand.URI_KEY, "deployer:Sun:AppServer::localhost:4848" );            
        context.put( GetDeploymentManagerCommand.FACTORY_KEY, "com.sun.enterprise.deployapi.SunDeploymentFactory" );

        // execute command
        command.execute( context );
    }

    /**
     * Test that command fails if uri key is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfUriKeyIsUndefinedInContext() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, "admin" );
        context.put( GetDeploymentManagerCommand.PASSWORD_KEY, "adminadmin" );                            
        context.put( GetDeploymentManagerCommand.FACTORY_KEY, "com.sun.enterprise.deployapi.SunDeploymentFactory" );

        // execute command
        command.execute( context );
    }
        
    /**
     * Test that command fails if uri value is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfUriValueIsUndefinedInContext() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, "admin" );
        context.put( GetDeploymentManagerCommand.PASSWORD_KEY, "adminadmin" );                
        context.put( GetDeploymentManagerCommand.URI_KEY, null );            
        context.put( GetDeploymentManagerCommand.FACTORY_KEY, "com.sun.enterprise.deployapi.SunDeploymentFactory" );

        // execute command
        command.execute( context );
    }

    /**
     * Test that command fails if factory key is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfFactoryKeyIsUndefinedInContext() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, "admin" );
        context.put( GetDeploymentManagerCommand.PASSWORD_KEY, "adminadmin" );                
        context.put( GetDeploymentManagerCommand.URI_KEY, "deployer:Sun:AppServer::localhost:4848" );            

        // execute command
        command.execute( context );
    }

    /**
     * Test that command fails if factory value is undefined. 
     */
    @Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfFactoryValueIsUndefinedInContext() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, "admin" );
        context.put( GetDeploymentManagerCommand.PASSWORD_KEY, "adminadmin" );                
        context.put( GetDeploymentManagerCommand.URI_KEY, "deployer:Sun:AppServer::localhost:4848" );            
        context.put( GetDeploymentManagerCommand.FACTORY_KEY, null );

        // execute command
        command.execute( context );
    }
        
    /**
     * Test that context contains initialized deployment manager after execution.
     * @throws Exception
     */
    @Test
    public void testDeploymentmanagerIsDefinedInContextAfterExecution() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, "weblogic" );
        context.put( GetDeploymentManagerCommand.PASSWORD_KEY, "weblogic" );                
        context.put( GetDeploymentManagerCommand.URI_KEY, "deployer:WebLogic:t3://127.0.0.1:7001" );    
        context.put( GetDeploymentManagerCommand.FACTORY_KEY, "weblogic.deploy.api.spi.factories.internal.DeploymentFactoryImpl" );
        
        command.execute( context );
        
        // get manager
        Object value = context.get( GetDeploymentManagerCommand.MANAGER_KEY );
        assertTrue( value instanceof DeploymentManager );        

        // release manager
        DeploymentManager manager;
        manager = (DeploymentManager) value;
        manager.release();
    }
    
}
