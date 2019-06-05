/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2019 Allan Thrane Andersen..
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

package com.alpha.pineapple.plugin.git;

import com.alpha.pineapple.plugin.git.model.Git;

/**
 * Pineapple Git Constants.
 */
public interface GitConstants {

	/**
	 * Legal content types supported by plugin operations.
	 */
	public static final Class<?>[] LEGAL_CONTENT_TYPES = { Git.class };

	/**
	 * Package name for generated JAXB classes from Git plguin schema.
	 */
	public static final String PLUGIN_MODEL_PACKAGE = "com.alpha.pineapple.plugin.git.model";

	/**
	 * Spring application context for the plugin .
	 */
	public static final String PLUGIN_APP_CONTEXT = "/com.alpha.pineapple.plugin.git-config.xml";

	/**
	 * Head branch identifier.
	 */
	public static final String BRANCH_HEAD = "HEAD"; 

	/**
	 * Master branch identifier.
	 */
	public static final String BRANCH_MASTER = "master"; 
	
	/**
	 * 1.0 branch identifier.
	 */
	public static final String BRANCH_1_0 = "1.0";

	/**
	 * Resource property.
	 */
	public String RESOURCE_PROPERTY_URI = "uri";

	/**
	 * Modules directory expression constant.
	 */
	public String MODULES_EXP = "modules:";

	/**
	 * Git repository suffix.
	 */
	public String GIT_REPO_SUFFIX = ".git"; 
	
}
