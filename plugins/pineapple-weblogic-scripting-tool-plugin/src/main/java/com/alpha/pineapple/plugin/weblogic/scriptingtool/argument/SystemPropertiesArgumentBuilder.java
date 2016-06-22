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


package com.alpha.pineapple.plugin.weblogic.scriptingtool.argument;

import static com.alpha.pineapple.plugin.weblogic.scriptingtool.WeblogicScriptingToolConstants.PINEAPPLE_MODULEPATH_PROPERTY;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.alpha.javautils.ExecutableNameUtils;

/**
 * Helper class which build Java system arguments list for WLST. 
 */
public class SystemPropertiesArgumentBuilder {

    /**
     * The builder result.
     */
    List<String> argumentList;
        
    /**
     * Start building argument list. 
     */
    public SystemPropertiesArgumentBuilder() {
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
     * Add module path argument.
     */
    public void addModulePathArgument(String modulePath)
    {
        addSingleArgument(modulePath);
    }

    /**
     * Add Demo trust arguments.
     */    
	public void addDemoTrustArguments() {		
		addSingleArgument("-Dweblogic.security.TrustKeyStore=DemoTrust" );
		addSingleArgument("-Dssl.debug=true" );
		addSingleArgument("-Dweblogic.security.SSL.ignoreHostnameVerification=true" );
	}

	/**
	 * Add Pineapple arguments.
	 * 
	 * @param modulePath Absolute path to module. 
	 */
	public void addPineappleArguments(File modulePath) {		
		StringBuilder arg =  new StringBuilder()
			.append("-D")
			.append(PINEAPPLE_MODULEPATH_PROPERTY)
			.append("=")
			.append(modulePath.getAbsolutePath());		
		
		// escape with "" if it contains spaces.
		String path = ExecutableNameUtils.escapePath(arg.toString());
		
		addSingleArgument(path);		
	}

	/**
	 * Add fast random generator for Linux arguments.  
	 */
	public void addLinuxFastSecureRandomArguments() {
		addSingleArgument("-Djava.security.egd=file:/dev/./urandom" );		
	}
    
    
}
