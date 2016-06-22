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

import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.TargetModuleID;

/**
 * Result object returned by then {@GetAvailableModulesCommand}. Contains
 * available modules in application server sorted by type.
 */
public interface AvailableModulesResult
{

    /**
     * Set modules of specific type.
     * 
     * @param type Module type.
     * @param modules Set of modules to add.
     */
    public void setModules( final ModuleType type, final TargetModuleID[] modules );

    /**
     * Get modules by type.
     * 
     * @param type Module type to return.
     * 
     * @return Array of modules by designated type.
     */
    public TargetModuleID[] getModules( final ModuleType type );
    
    /**
     * Search in contained modules for modules which starts with specified 
     * string. if no matching module names are found then an empty array is 
     * returned.  
     * 
     * @param name Module name to search for.
     * 
     * @return Array of {@link TargetModuleID} which contains module names 
     * which starts with specified string. 
     */
    public TargetModuleID[] findModulesStartingWith(final String name );    

    /**
     * Search in contained modules for modules which starts with specified 
     * string. if no matching module names are found then an empty array is 
     * returned.  
     * 
     * @param name Module name to search for.
     * 
     * @return Array of {@link TargetModuleID} which contains module names 
     * which starts with specified string. 
     */
    public TargetModuleID[] findModules(final String name );    
    
}
