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


package com.alpha.pineapple.plugin.jrockit.installation.argument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Helper class which build arguments list for JRockit R27. 
 */
public class R27ArgumentBuilder {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * The builder result.
     */
    List<String> argumentList;
        
    /**
     * R27ArgumentBuilder constructor. 
     */
    public R27ArgumentBuilder() {
    }
    
    /**
     * Start building argument list. 
     */
    public void buildArgumentList() {

        // create the result
        argumentList = new ArrayList<String>(); 
    }
    
    /**
     * Return the result of the build process.
     * 
     * @return constructed argument list. 
     */
    public String[] getArgumentList() {
		String[] arguments = argumentList.toArray(new String[argumentList.size()] );
		return arguments;
    }
	
    /**
     * Add a single argument to the product.
     * 
     * @param argument The argument to add.
     */
    public void addSingleArgument( String argument )
    {
        this.argumentList.add( argument );        
    }
    
    /**
     * Add silent mode argument for installation.
     */
    public void addInstallationSilentModeArgument( )
    {
        addSingleArgument("-mode=silent" );
    }

    /**
     * Add silent mode argument for uninstallation.
     */
    public void addUninstallationSilentModeArgument( )
    {
        addSingleArgument("-silent" );
    }
    
    /**
     * Add silent XML argument.
     */
    public void addSilentXMLArgument(File silentXMLFile)
    {
        addSingleArgument("-silent_xml="+silentXMLFile );
    }
    
    /**
     * Add log argument.
     */
    public void addLogArgument(File silentLogFile)
    {
        addSingleArgument("-log=" + silentLogFile);
    }
    
}
