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


package com.alpha.pineapple.jsr88.command.result;

import java.util.ArrayList;
import java.util.HashMap;

import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.TargetModuleID;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Implementation of the {@link AvailableModulesResult} interface. 
 */
public class AvailableModulesResultImpl implements AvailableModulesResult
{

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Contained modules.
     */
    HashMap<ModuleType, TargetModuleID[]> modules;
            
    /**
     * AvailableModulesResultImpl constructor.
     */
    public AvailableModulesResultImpl()
    {
        super();
        this.modules = new HashMap<ModuleType, TargetModuleID[]>();
    }

    public void setModules( final ModuleType type, final TargetModuleID[] modules )
    {
        // validate parameters
        Validate.notNull( type, "type is undefined." );
        Validate.notNull( modules, "modules is undefined." );                
        
        this.modules.put( type, modules );        
    }

    public TargetModuleID[] getModules( final ModuleType type )
    {
        // validate parameters
        Validate.notNull( type, "type is undefined." );
        
        return this.modules.get( type );
    }


    public TargetModuleID[] findModulesStartingWith( String name )
    {
        // log debug message
        if( logger.isDebugEnabled()) {
            
            // create debug message
            StringBuilder message = new StringBuilder();
            message.append( "Will located module starting with <" );
            message.append( name );
            message.append( ">." );

            // log debug message
            logger.debug( message.toString() );            
        }
        
        // create result object
        ArrayList<TargetModuleID> result = new ArrayList<TargetModuleID>();
        
        // search ear modules
        TargetModuleID[] modules = getModules( ModuleType.EAR );
        for (TargetModuleID currentModule: modules ) {
            
            // get id
            String id = currentModule.getModuleID();
            
            // compare name
            if (id.startsWith( name )) {
                result.add( currentModule );
            }            
        }
        
        // search war modules
        modules = getModules( ModuleType.WAR );
        for (TargetModuleID currentModule: modules ) {
            
            // get id
            String id = currentModule.getModuleID();
            
            // compare name
            if (id.startsWith( name )) {
                result.add( currentModule );
            }            
        }

        // search ejb modules
        modules = getModules( ModuleType.EJB );
        for (TargetModuleID currentModule: modules ) {
            
            // get id
            String id = currentModule.getModuleID();
            
            // compare name
            if (id.startsWith( name )) {
                result.add( currentModule );
            }            
        }

        // search car modules
        modules = getModules( ModuleType.CAR );
        for (TargetModuleID currentModule: modules ) {
            
            // get id
            String id = currentModule.getModuleID();
            
            // compare name
            if (id.startsWith( name )) {
                result.add( currentModule );
            }            
        }

        // search rar modules
        modules = getModules( ModuleType.RAR );
        for (TargetModuleID currentModule: modules ) {
            
            // get id
            String id = currentModule.getModuleID();
            
            // compare name
            if (id.startsWith( name )) {
                result.add( currentModule );
            }            
        }
        
        return result.toArray( new TargetModuleID[result.size()] );
    }

    public TargetModuleID[] findModules( String name )
    {
        // log debug message
        if( logger.isDebugEnabled()) {
            
            // create debug message
            StringBuilder message = new StringBuilder();
            message.append( "Will located module with name <" );
            message.append( name );
            message.append( ">." );

            // log debug message
            logger.debug( message.toString() );            
        }
        
        // create result object
        ArrayList<TargetModuleID> result = new ArrayList<TargetModuleID>();
        
        // search ear modules
        TargetModuleID[] modules = getModules( ModuleType.EAR );
        for (TargetModuleID currentModule: modules ) {
            
            // get id
            String id = currentModule.getModuleID();
            
            // compare name
            if (id.equals( name )) {
                result.add( currentModule );
            }            
        }
        
        // search war modules
        modules = getModules( ModuleType.WAR );
        for (TargetModuleID currentModule: modules ) {
            
            // get id
            String id = currentModule.getModuleID();
            
            // compare name
            if (id.equals( name )) {
                result.add( currentModule );
            }            
        }

        // search ejb modules
        modules = getModules( ModuleType.EJB );
        for (TargetModuleID currentModule: modules ) {
            
            // get id
            String id = currentModule.getModuleID();
            
            // compare name
            if (id.equals( name )) {
                result.add( currentModule );
            }            
        }

        // search car modules
        modules = getModules( ModuleType.CAR );
        for (TargetModuleID currentModule: modules ) {
            
            // get id
            String id = currentModule.getModuleID();
            
            // compare name
            if (id.equals( name )) {
                result.add( currentModule );
            }            
        }

        // search rar modules
        modules = getModules( ModuleType.RAR );
        for (TargetModuleID currentModule: modules ) {
            
            // get id
            String id = currentModule.getModuleID();
            
            // compare name
            if (id.equals( name )) {
                result.add( currentModule );
            }            
        }
        
        return result.toArray( new TargetModuleID[result.size()] );
    }
    
    
}
