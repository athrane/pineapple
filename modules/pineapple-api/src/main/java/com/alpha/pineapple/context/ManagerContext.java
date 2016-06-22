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

package com.alpha.pineapple.context;

import org.apache.commons.chain.Context;

/**
 * Interface for context used by the application.
 */
@Deprecated
public interface ManagerContext extends Context {

    /**
     * Key used to identify property in context: Versioned name of the current
     * Pineapple module.
     */
    public static final String MODULE_ID_KEY = "module-id";

    /**
     * Key used to identify property in context: Defines the runtime root
     * directory where the Pineapple modules can be found.
     */
    public static final String RUNTIME_ROOT_DIR_KEY = "runtime-root-directory";

    /**
     * Key used to identify property in context: Contains the target environment
     * a management operation.
     */
    public static final String ENVIRONMENT_KEY = "environment";

    /**
     * Key used to identify property in context: Contains credential provider
     * instance which contains security information used to access resources.
     */
    public static final String CREDENTIAL_PROVIDER_KEY = "credential-provider";

    /**
     * Key used to identify property in context: Contains resources
     * configuration object.
     */
    public static final String RESOURCES_KEY = "resources";

    /**
     * Key used to identify property in context: Name of the current management
     * operation.
     */
    public static final String OPERATION_KEY = "operation";

}
