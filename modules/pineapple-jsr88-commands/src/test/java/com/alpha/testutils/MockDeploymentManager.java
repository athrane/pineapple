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


package com.alpha.testutils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.shared.DConfigBeanVersionType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.InvalidModuleException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.status.ProgressObject;

public class MockDeploymentManager implements DeploymentManager
{

    /**
     * Available modules.
     */
    public HashMap<ModuleType, ArrayList<TargetModuleID>> modules;

    /**
     * Targets in domain.
     */
    public ArrayList<Target> targets;

    /**
     * Field is set to true if the release method is invoked.
     */
    public boolean isReleased;

    /**
     * MockDeploymentManager constructor.
     */
    public MockDeploymentManager()
    {
        super();
        this.isReleased = false;
        this.modules = new HashMap<ModuleType, ArrayList<TargetModuleID>>();
        this.targets = new ArrayList<Target>();
    }

    public DeploymentConfiguration createConfiguration( DeployableObject arg0 ) throws InvalidModuleException
    {
        return null;
    }

    public ProgressObject distribute( Target[] arg0, File arg1, File arg2 ) throws IllegalStateException
    {
        return null;
    }

    public ProgressObject distribute( Target[] arg0, InputStream arg1, InputStream arg2 ) throws IllegalStateException
    {
        return null;
    }

    public TargetModuleID[] getAvailableModules( ModuleType type, Target[] targets )
        throws TargetException, IllegalStateException
    {
        // look up modules by type
        ArrayList<TargetModuleID> availableModules;
        availableModules = modules.get( type );        
        
        // if null then create it
        if( availableModules == null ) {
            availableModules =  new ArrayList<TargetModuleID>();
        }
        
        // create empty array
        TargetModuleID[] modulesArray;
        modulesArray = new TargetModuleID[availableModules.size()];

        // populate array
        return availableModules.toArray( modulesArray );
        
    }

    public Locale getCurrentLocale()
    {
        return null;
    }

    public DConfigBeanVersionType getDConfigBeanVersion()
    {
        return null;
    }

    public Locale getDefaultLocale()
    {
        return null;
    }

    public TargetModuleID[] getNonRunningModules( ModuleType arg0, Target[] arg1 )
        throws TargetException, IllegalStateException
    {
        return null;
    }

    public TargetModuleID[] getRunningModules( ModuleType arg0, Target[] arg1 )
        throws TargetException, IllegalStateException
    {
        return null;
    }

    public Locale[] getSupportedLocales()
    {
        return null;
    }

    public Target[] getTargets() throws IllegalStateException
    {
        // create empty array
        Target[] targetArray = new Target[targets.size()];

        // populate array
        return targets.toArray( targetArray );
    }

    public boolean isDConfigBeanVersionSupported( DConfigBeanVersionType arg0 )
    {
        return false;
    }

    public boolean isLocaleSupported( Locale arg0 )
    {
        return false;
    }

    public boolean isRedeploySupported()
    {
        return false;
    }

    public ProgressObject redeploy( TargetModuleID[] arg0, File arg1, File arg2 )
        throws UnsupportedOperationException, IllegalStateException
    {
        return null;
    }

    public ProgressObject redeploy( TargetModuleID[] arg0, InputStream arg1, InputStream arg2 )
        throws UnsupportedOperationException, IllegalStateException
    {
        return null;
    }

    public void release()
    {
        this.isReleased = true;
    }

    public void setDConfigBeanVersion( DConfigBeanVersionType arg0 ) throws DConfigBeanVersionUnsupportedException
    {
    }

    public void setLocale( Locale arg0 ) throws UnsupportedOperationException
    {
    }

    public ProgressObject start( TargetModuleID[] arg0 ) throws IllegalStateException
    {
        return null;
    }

    public ProgressObject stop( TargetModuleID[] arg0 ) throws IllegalStateException
    {
        return null;
    }

    public ProgressObject undeploy( TargetModuleID[] arg0 ) throws IllegalStateException
    {
        return null;
    }

    public ProgressObject distribute( Target[] arg0, ModuleType arg1, InputStream arg2, InputStream arg3 )
        throws IllegalStateException
    {
        return null;
    }

    /**
     * Add mock war module to mock deployment manager.
     * 
     * @param mockModuleName Name of mock war module.
     */
    public void addMockWarModule( String mockModuleName )
    {
        addMockWarModule( mockModuleName, null );
    }

    /**
     * Add mock war module to mock deployment manager. Module is target to
     * named target.
     * 
     * @param mockModuleName Name of mock war module.
     * @param target Target The target that the module is deployed to. 
     */
    public void addMockWarModule( String mockModuleName, String targetName )
    {
        // create target description
        StringBuilder desc = new StringBuilder();
        desc.append( targetName );
        desc.append( " description" );
        
        // create target
        Target mockTarget;
        mockTarget = new MockTarget(desc.toString(), targetName );
        
        // create target module
        TargetModuleID mockModule;
        mockModule = new MockTargetModuleID( mockModuleName, mockTarget );
        
        // get war modules list
        ArrayList<TargetModuleID> warModules;
        warModules = modules.get( ModuleType.WAR );
        
        // if null then create it
        if( warModules == null ) {
            warModules =  new ArrayList<TargetModuleID>();
        }
        
        // add module to war modules list
        warModules.add( mockModule );
        
        // store war modules list
        modules.put( ModuleType.WAR, warModules );
    }
    
}
