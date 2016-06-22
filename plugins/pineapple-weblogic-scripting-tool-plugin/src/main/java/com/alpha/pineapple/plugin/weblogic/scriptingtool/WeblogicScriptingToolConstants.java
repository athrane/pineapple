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


package com.alpha.pineapple.plugin.weblogic.scriptingtool;

import com.alpha.pineapple.plugin.weblogic.scriptingtool.model.Wlst;

/**
 * WebLogic scripting tool constants.
 */
public interface WeblogicScriptingToolConstants {
	
	/**
	 * Legal content types supported by plugin operations.
	 */
	public static final Class<?>[] LEGAL_CONTENT_TYPES = {Wlst.class };
		
	/**
	 * Default process time out (ms).
	 */
	public static final int DEFAULT_PROCESS_TIMEOUT = 1000*60*10;
	
	/**
	 * WebLogic home directory resource property.
	 */
	public static final String PRODUCT_HOME_RESOURCE_PROPERTY = "home-directory";	
	
	/**
	 * Enabling secure fast random generator for Linux resource property. 
	 */
	static final String LINUX_FAST_RANDOM_GENERATOR = "enable-linux-fast-random-generator";
	
	/**
	 * WebLogic WLST class.
	 */
	static final String WEBLOGIC_WLST_CLASS = "weblogic.WLST";

	/**
	 * Environment variable used to capture Ant result.
	 */
    static final String PINEAPPLE_ANT_RESULT = "pineapple.ant.result";

	/**
	 * Environment variable used to capture Ant error out.
	 */    
	static final String PINEAPPLE_ANT_ERROR = "pineapple.ant.error";

	/**
	 * Environment variable used to capture Ant standard out.
	 */    	
	static final String PINEAPPLE_ANT_OUTPUT = "pineapple.ant.output";

	/**
	 * WebLogic Home system property.
	 */
	static final String WEBLOGIC_HOME_PROPERTY = "weblogic.Home";

	/**
	 * WebLogic product properties system property.
	 */	
	static final String PRODUCT_PROPERTIES_PROPERTY = "prod.props.file";

	/**
	 * Pineapple module path system property.
	 */
	static final String PINEAPPLE_MODULEPATH_PROPERTY = "pineapple.module.path";

	/**
	 * Pineapple WebLogic Home path system property.
	 */
	static final String PINEAPPLE_WEBLOGIC_HOME_PROPERTY = "pineapple.weblogic.home.path";
	
	/**
	 * WLST execution timeout.
	 */	
	static final long WLST_EXECUTION_TIMEOUT = 120000; 
	
	/**
	 * WebLogic jar file name.
	 */
	static final String WEBLOGIC_JAR = "weblogic.jar";
		
}
