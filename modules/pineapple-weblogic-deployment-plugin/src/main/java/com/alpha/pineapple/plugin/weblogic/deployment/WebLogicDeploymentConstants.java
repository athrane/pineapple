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


package com.alpha.pineapple.plugin.weblogic.deployment;

import com.alpha.pineapple.plugin.weblogic.deployment.model.Deployment;

/**
 * WebLogic JMX and MBean constants.
 */
public interface WebLogicDeploymentConstants {

	
	/**
	 * Legal content types supported by plugin operations.
	 */
	public static final Class<?>[] LEGAL_CONTENT_TYPES = {Deployment.class };
	
	/**
	 * Default process time out (ms).
	 */
	public static final int DEFAULT_PROCESS_TIMEOUT = 1000*60*10;

    /**
     * Name of protocol property.
     */
    public final String PROTOCOL_PROPERTY = "adminserver-protocol"; 

    /**
     * Name of listen address property.
     */
    public final String LISTENADDRESS_PROPERTY = "adminserver-listenaddress"; 

    /**
     * Name of listen port property.
     */
    public final String LISTENPORT_PROPERTY = "adminserver-listenport"; 

    /**
     * Name of time stamp property.
     */
    public final String TIMESTAMP_PROPERTY = "timestamp-enabled"; 
	
}
