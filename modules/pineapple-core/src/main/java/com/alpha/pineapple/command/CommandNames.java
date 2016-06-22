/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.command;

/**
 * Interface which define the catalog names of the available Chain Commands in
 * the pineapple core project.
 */
@Deprecated
public interface CommandNames {

    /**
     * Catalog name for core commands.
     */
    public static final String CATALOG = "com.alpha.pineapple.command";

    /**
     * Catalog name for the load-configuration command.
     */
    public static final String LOAD_CONFIGURATION = "load-configuration";

    /**
     * Catalog name for the load-combined-configuration command.
     */
    public static final String LOAD_COMBINED_CONFIGURATION = "load-combined-configuration";

    /**
     * Catalog name for the process-configuration command.
     */
    public static final String PROCESS_CONFIGURATION = "process-configuration";

    /**
     * Catalog name for the initialize-plugin-activator command.
     */
    public static final String INITIALIZE_PLUGINACTIVATOR = "initialize-plugin-activator";

    /**
     * Catalog name for the initialize-plugin-activator command.
     */
    public static final String INITIALIZE_PLUGINACTIVATOR_V2 = "initialize-plugin-activator-v2";

    /**
     * Catalog name for the unmarshall-jaxb-objects command.
     */
    public static final String UNMARSHALL_JAXB_OBJECTS = "unmarshall-jaxb-objects";

    /**
     * Catalog name for the load-environment-configuration command.
     */
    public static final String LOAD_ENVIRONMENT_CONFIGURATION = "load-environment-configuration";

    /**
     * Catalog name for the load-module command.
     */
    public static final String LOAD_MODULE = "load-module";

    /**
     * Catalog name for the load-module-model command.
     */
    public static final String LOAD_MODULE_MODEL = "load-module-model";

}
