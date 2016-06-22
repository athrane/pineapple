/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.resource;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Resource;

/**
 * Interface for loading an environment configuration (as XML) from disk into
 * info objects. And versus visa.
 */
public interface ResourceConfigurationMarshaller {

    /**
     * Save resources configuration.
     * 
     * The configuration directory is resolved from the runtime directory
     * provider. The configuration is saved in the default resources
     * configuration file resources.xml.
     * 
     * @param executionResult
     *            A child execution result will be added to this result. The
     *            child result will reflects the out come of the save operation.
     *            If the save operation fails the state of the result object
     *            will be failure or error and will contain a stack trace
     *            message.
     * @param configuration
     *            resource configuration which is saved.
     */

    void save(ExecutionResult executionResult, Configuration configuration);

    /**
     * Map resources configuration for saving.
     * 
     * @param info
     *            configuration info which is mapped.
     * 
     * @return resources configuration which can be marshalled.
     */
    Configuration map(ConfigurationInfo info);

    /**
     * Map resources configuration to info objects.
     * 
     * @param configuration
     *            configuration which is mapped.
     * 
     * @return resources configuration represented by info objects.
     */
    ConfigurationInfo map(Configuration configuration);

    /**
     * Map to resource info to resource.
     * 
     * @param resourceInfo
     *            resource info object.
     * 
     * @return mapped resource.
     */
    @Deprecated
    Resource mapToResource(ResourceInfo resourceInfo);

}
