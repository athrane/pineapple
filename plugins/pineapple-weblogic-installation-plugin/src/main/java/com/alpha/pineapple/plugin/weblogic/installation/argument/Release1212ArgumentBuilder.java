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


package com.alpha.pineapple.plugin.weblogic.installation.argument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.alpha.javautils.ExecutableNameUtils;



/**
 * Helper class which build arguments list for products which 
 * are installed using the universal. installer. 
 * 
 * Documentation:
 * http://docs.oracle.com/middleware/1212/core/OUIRF/silent.htm
 * http://docs.oracle.com/middleware/1212/core/OUIRF/response_file.htm
 */
public class Release1212ArgumentBuilder {

    /**
     * The builder result.
     */
    List<String> argumentList;
        
    /**
     * Start building argument list. 
     */
    public Release1212ArgumentBuilder() {
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
    public void addSingleArgument( String argument ) {
        this.argumentList.add( argument );        
    }
    
    /**
     * Add silent mode argument.
     */
    public void addSilentModeArgument( ) {
        addSingleArgument("-silent" );
    }


    /**
     * Add uninstall argument.
     */
    public void addUninstallationArgument( ) {
        addSingleArgument("-deinstall" );
    }
        
    /**
     * Add debug argument.
     */
    public void addDebugArgument() {
        addSingleArgument("-debug" );
    }

    /**
     * Add response file argument.
     */
    public void addResponseFileArgument(File responseFile) {
    	// add single quotes ' if path contains spaces
    	String value = responseFile.getAbsolutePath();    	
    	value = ExecutableNameUtils.escapeExecutable(value);    	
    	
    	// add arguments
        addSingleArgument("-response" );
        addSingleArgument( value);
    }

    /**
     * Add response file argument.
     */
    public void addJreArgument(File jreLocation) {
        addSingleArgument("-jreLoc" );
        addSingleArgument( jreLocation.getAbsolutePath() );
    }

    /**
     * Add wait argument.
     */
    public void addWaitForCompletionArgument() {
        addSingleArgument("-waitforcompletion" );
    }
        
    /**
     * Add jar argument for jar archive based installation.
     *  
     * @param installerArchive name of the archive file.
     */
	public void addInstallerArchive(String installerArchive) {
		addSingleArgument("-jar");		
		addSingleArgument(installerArchive);
	}

    /**
     * Add inventory location wait argument.
     */
    public void addInventoryLocationArgument() {
		addSingleArgument("-invPtrLoc");		
		addSingleArgument("/tmp/oraInst.loc");
    }
	
}
