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

import static org.junit.Assert.assertNotNull;

import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.TargetModuleID;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.jsr88.command.result.AvailableModulesResult;

public class GetAvailableModulesCommandIntegrationTest
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
     * Helper method which get deployment manager from local WebLogic.
     * @return deployment manager from local WebLogic.
     * 
     * @throws Exception
     */
    DeploymentManager getLocalWebLogicDeploymentManager() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, "weblogic" );
        context.put( GetDeploymentManagerCommand.PASSWORD_KEY, "weblogic" );                
        context.put( GetDeploymentManagerCommand.URI_KEY, "deployer:WebLogic:t3://127.0.0.1:7001" );    
        context.put( GetDeploymentManagerCommand.FACTORY_KEY, "weblogic.deploy.api.spi.factories.internal.DeploymentFactoryImpl" );
        
        // create get deployment manager command
        command = new GetDeploymentManagerCommand();
        
        // execute command
        command.execute( context );     
        
        // get manager
        DeploymentManager manager;
        manager = (DeploymentManager) context.get( GetDeploymentManagerCommand.MANAGER_KEY );
        return manager;
    }

    /**
     * Helper method which get deployment manager from local Glassfish.
     * @return deployment manager from local Glassfish.
     * 
     * @throws Exception
     */
    DeploymentManager getLocalGlassfishDeploymentManager() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();
        context.put( GetDeploymentManagerCommand.USER_KEY, "admin" );
        context.put( GetDeploymentManagerCommand.PASSWORD_KEY, "adminadmin" );                
        context.put( GetDeploymentManagerCommand.URI_KEY, "deployer:Sun:AppServer::localhost:4848" );            
        context.put( GetDeploymentManagerCommand.FACTORY_KEY, "com.sun.enterprise.deployapi.SunDeploymentFactory" );
        
        // create get deployment manager command
        command = new GetDeploymentManagerCommand();
        
        // execute command
        command.execute( context );     
        
        // get manager
        DeploymentManager manager;
        manager = (DeploymentManager) context.get( GetDeploymentManagerCommand.MANAGER_KEY );
        return manager;
    }
        
    /**
     * Test that modules can be retrieved from local WebLogic server.
     * @throws Exception
     */
    @Test
    public void testCanGetResultObjectFromLocalWebLogic() throws Exception
    {
        // get deployment manager
        DeploymentManager manager = getLocalWebLogicDeploymentManager();
                
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

        // test
        assertNotNull(result);
        
        // release manager
        manager.release();
    }

    /**
     * Test that modules can be retrieved from local Glassfish server.
     * @throws Exception
     */
    @Test
    public void testCanGetResultObjectFromLocalGlassfish() throws Exception
    {
        // get deployment manager
        DeploymentManager manager = getLocalGlassfishDeploymentManager();
                
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

        // test
        assertNotNull(result);
        
        // release manager
        manager.release();
    }

    /**
     * Test that modules can be retrieved from local WebLogic server.
     * @throws Exception
     */
    @Test
    public void testCanGetWarTypeModulesFromLocalWebLogic() throws Exception
    {
        // get deployment manager
        DeploymentManager manager = getLocalWebLogicDeploymentManager();
                
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
    
        // test        
        TargetModuleID[] modules = result.getModules( ModuleType.WAR);
        assertNotNull(modules);        
    
        // release manager
        manager.release();
    }    
    
}
