/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2016 Allan Thrane Andersen.
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

package com.alpha.pineapple;

/**
 * Definition of core component constants.
 */
public interface CoreConstants {

	/**
	 * UTF-8 file encoding.
	 */
	public static final String FILE_ENCODING_UTF8 = "UTF-8";

	/**
	 * Default name for modules directory.
	 */
	public static final String MODULES_DIR = "modules";

	/**
	 * Default name for configuration directory.
	 */
	public static final String CONF_DIR = "conf";

	/**
	 * Default name for reports directory.
	 */
	public static final String REPORTS_DIR = "reports";

	/**
	 * Default name for pineapple directory.
	 */
	public static final String PINEAPPLE_DIR = ".pineapple";

	/**
	 * Module path expression constant.
	 */
	public String MODULEPATH_EXP = "modulepath:";

	/**
	 * Modules directory expression constant.
	 */
	public String MODULERS_EXP = "modules:";
	
	/**
	 * Default file name for credentials file.
	 */
	public static final String CREDENTIALS_FILE = "credentials.xml";

	/**
	 * Default file name resources configuration file.
	 */
	public static final String RESOURCE_FILE = "resources.xml";

	/**
	 * Default file name for scheduled operations configuration file.
	 */
	public static final String OPERATIONS_FILE = "scheduled-operations.xml";

	/**
	 * File name for module file.
	 */
	public static final String MODULE_FILENAME = "module.xml";

	/**
	 * Wild card environment ID.
	 */
	public static final String WILDCARD_ENVIRONMENT_ID = "*";

	/**
	 * Execution capacity of current and past executions in the result repository.
	 */
	public static final int EXECUTION_HISTORY_CAPACITY = 5;

	/**
	 * key used to store load module model in execution context.
	 */
	public static final String MODULE_MODEL_KEY = "module-model";

	/**
	 * key used to store load module descriptor in execution context.
	 */
	public static final String MODULE_KEY = "module";

	/**
	 * key used to store execution info in execution context.
	 */
	public static final String EXECUTION_INFO_KEY = "execution-info";

	/**
	 * Wildcard operation ID for restriction of operations.
	 */
	public static final String WILDCARD_OPERATION = "*";

	/**
	 * Prefix for encrypted credential passwords.
	 */
	public static final String ENCRYPTED_PREFIX = "encrypted:";

	/**
	 * Credential provider default file name constant.
	 */
	public static final String CRDENTIALPROVIDER_PASSWORD_FILE = "credentialprovider.password";

	/**
	 * Execution result key used to messages concerning operation session handling
	 * info.
	 */
	public static final String MSG_SESSION = "Session";

	/**
	 * Execution result key used to messages identifying the operation name.
	 */
	public static final String MSG_OPERATION = "Operation";

	/**
	 * Execution result key used to messages identifying the environment.
	 */
	public static final String MSG_ENVIRONMENT = "Environment";

	/**
	 * Execution result key used to messages identifying the module.
	 */
	public static final String MSG_MODULE = "Module";

	/**
	 * Execution result key used to messages identifying the module file.
	 */
	public static final String MSG_MODULE_FILE = "Module file";

	/**
	 * Execution result key used to messages identifying the continuation policy.
	 */
	public static final String MSG_CONTINUATION = "Continuation";

	/**
	 * Execution result key used to messages identifying the resource resolution.
	 */
	public static final String MSG_RESOURCE_RESOLUTION = "Resource Resolution";

	/**
	 * Execution result key used to messages concerning trigger resolution.
	 */
	public static final String MSG_TRIGGER_RESOLUTION = "Trigger Resolution";

	/**
	 * Definition of wild card result in trigger.
	 */
	public String TRIGGER_WILDCARD_OPERATION = "*";

	/**
	 * Definition of wild card operation in trigger.
	 */
	public String TRIGGER_WILDCARD_RESULT = "*";

}
