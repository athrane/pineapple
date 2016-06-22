/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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

package com.alpha.pineapple.plugin.docker;

import com.alpha.pineapple.plugin.docker.model.Docker;
import com.alpha.pineapple.plugin.docker.operation.DeployConfiguration;

/**
 * Pineapple Docker Constants.
 */
public interface DockerConstants {

    /**
     * Legal content types supported by plugin operations.
     */
    public static final Class<?>[] LEGAL_CONTENT_TYPES = { Docker.class };

    /**
     * File name for TAR archive.
     */
    public static final String TAR_ARCHIVE = DeployConfiguration.class.getCanonicalName() + ".tar";

    /**
     * Package name for generated JAXB classes from Docker plugn schema.
     */
    public static final String PLUGIN_MODEL_PACKAGE = "com.alpha.pineapple.plugin.docker.model";
    
}
