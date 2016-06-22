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


package com.alpha.pineapple.maven;

/**
 * Definition of Interface which defines Maven constants.
 */
public interface PluginConstants
{

    /**
     * Group id for delegated execution plugin. 
     */
    public final String dePluginGroupID = "com.alpha.pineapple";
    
    /**
     * Group id for delegated execution plugin. 
     */    
    public final String dePluginArtifactId = "pineapple-maven-delegator-plugin";
        
    /**
     * Credentials file for Maven plugin.
     */
    public static final String CREDENTIALS_FILE = "credentials.xml";

    /**
     * Resources file for Maven plugin.
     */
    public static final String RESOURCES_FILE = "resources.xml";
            
    /**
     * JDK System property used to get user.home directory
     */
    public static final String USER_HOME = "user.home";
    
    /**
     * Name of Pineapple runtime directory in the user.home directory.  
     */
    public static final String PINEAPPLE_DIR = ".pineapple";

    /**
     * Name of Pineapple logging directory.  
     */    
    public final String LOGGING_DIR = "logs";

    /**
     * Name of Pineapple configuration directory.  
     */    
    public final String CONF_DIR = "conf";
    
}
