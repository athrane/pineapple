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


package com.alpha.pineapple.plugin.agent;

import javax.xml.bind.JAXBElement;


/**
 * Pineapple Agent Constants.
 */
public interface AgentConstants {
	
	/**
	 * Legal content types supported by plugin operations.
	 */
	public static final Class<?>[] LEGAL_CONTENT_TYPES = { JAXBElement.class };
	
	/**
	 * Default port.
	 */
	public static final String DEFAULT_PORT = "8080";
	
	/**
	 * Default connect timeout milli seconds
	 */
	public static final String DEFAULT_TIMEOUT = "5000";
	
    /**
	 * ZIP file postfix.
	 */
	public static final String ZIP_FILE_POSTFIX = ".zip";
	
	/**
	 * Distribute module REST service URI.
	 */
	public static final String DISTRIBUTE_MODULE_URI = "/api/module";			

	/**
	 * Distribute module REST service URI.
	 */
	public static final String DELETE_MODULE_URI = "/api/module/{module}";			
	
	/**
	 * Distribute module REST service file part.
	 */
	public static final String DISTRIBUTE_MODULE_FILE_PART = "file";

	/**
	 * Execute operation REST service URI. 
	 */
	public static final String EXECUTE_MODULE_URI = "/api/execute/module/{module}/operation/{operation}/environment/{environment}";
		
	/**
	 * Refresh environment configuration REST service URI. 
	 */
	static final String REFRESH_ENVIRONMENT_CONFIGURATION_URI = "/api/configuration/refresh";
	
	/**
	 * Create environment REST service URI. 
	 */
	public static final String CREATE_ENVIRONMENT_URI = "/api/configuration/environment/{environment}/description/{description}";
	
	/**
	 * Schedule operation REST service URI. 
	 */
	public static final String SCHEDULE_OPERATION_URI = "/api/schedule/name/{name}/module/{module}/environment/{environment}/operation/{operation}/cron/{cron}/description/{description}";

	/**
	 * Delete scheduled operation REST service URI. 
	 */
	public static final String DELETE_SCHEDULED_OPERATION_URI = "/api/schedule/name/{name}";

	/**
	 * Delete all scheduled operations REST service URI. 
	 */
	public static final String DELETE_ALL_SCHEDULED_OPERATIONS_URI = "/api/schedule/operations";
	
	/**
	 * Text/HTML Content value.
	 */
	public static final String CONTENT_TYPE_TEXT_HTML = "text/html";

	/**
	 * Content type key.
	 */
	public static final String CONTENT_TYPE_KEY = "Content-Type";

	/**
	 * Polling delay between getting status updates from agents in milli seconds.
	 */
	public static final int OPERATION_STATUS_POLLING_DELAY = 50;
	
}

