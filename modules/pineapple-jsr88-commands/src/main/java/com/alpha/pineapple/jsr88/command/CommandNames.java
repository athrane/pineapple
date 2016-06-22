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

/**
 * Interface which define the catalog names of the available Chain Commands.
 */
public interface CommandNames
{
    /**
     * Catalog name for JSR-88 commands.
     */
    public static final String CATALOG = "com.alpha.pineapple.jsr88.command";
        
    /**
     * Catalog name for the target-exists command.
     */
    public static final String TARGET_EXISTS = "test-target-exists";

    /**
     * Catalog name for the module-is-deployed command.
     */
    public static final String MODULE_IS_DEPLOYED = "test-module-is-deployed";

    /**
     * Catalog name for the module-is--not-deployed command.
     */
    public static final String MODULE_ISNT_DEPLOYED = "test-module-is-not-deployed";
        
}
