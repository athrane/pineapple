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

/**
 * Interface which define the catalog names of the available Chain Commands.
 */
@Deprecated
public interface CommandNames
{
    /**
     * Catalog name for infrastructure commands.
     */
    public static final String CATALOG = "com.alpha.pineapple.plugin.weblogic.jmx.command";
        
    /**
     * Name of the get MBean command.
     */
    public static final String GET_MBEAN_COMMAND = "get-mbean";

    /**
     * Name of the get MBean attribute command.
     */
    public static final String GET_MBEAN_ATTRIBUTE_COMMAND = "get-mbean-attribute";

    /**
     * Name of the create MBean command.
     */
    public static final String CREATE_MBEAN_COMMAND = "create-mbean";

    /**
     * Name of the set MBean attribute command.
     */
    public static final String SET_MBEAN_ATTRIBUTE_COMMAND = "set-mbean-attribute";

    /**
     * Name of the MBean exits test command.
     */    
	public static final String TEST_MBEAN_EXIST_COMMAND = "test-mbean-exists";

    /**
     * Name of the MBean attribute value test command.
     */    
	public static final String TEST_MBEAN_ATTRIBUTE_VALUE_COMMAND = "test-mbean-attribute-value";

    /**
     * Name of the attribute value test command.
     */    
    public static final String TEST_ATTRIBUTE_VALUE_COMMAND = "test-attribute-value";

    /**
     * Name of the object identity test command.
     */    
    public static final String TEST_OBJECT_IDENTITY_COMMAND = "test-object-identity";

    /**
     * Name of the array attribute value test command.
     */    
    public static final String TEST_ARRAY_ATTRIBUTE_VALUE_COMMAND = "test-array-attribute-value";        

    /**
     * Name of the enum value test command.
     */    
    public static final String TEST_ENUM_VALUE_COMMAND = "test-enum-value";
    
}
