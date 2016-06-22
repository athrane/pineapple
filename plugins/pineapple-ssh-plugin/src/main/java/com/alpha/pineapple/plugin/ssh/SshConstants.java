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


package com.alpha.pineapple.plugin.ssh;

import com.alpha.pineapple.plugin.ssh.model.Ssh;


/**
 * SSH Constants.
 */
public interface SshConstants {
	
	/**
	 * Legal content types supported by plugin operations.
	 */
	public static final Class<?>[] LEGAL_CONTENT_TYPES = { Ssh.class };
	
	/**
	 * Default port.
	 */
	public static final String DEFAULT_PORT = "22";
	
	/**
	 * Default connect timeout.
	 */
	public static final String DEFAULT_TIMEOUT = "5000";

	/**
	 * Default strict host key checking.
	 */	
	public static final String DEFAULT_STRICT_HOSTKEY_CHECKING = "no";

	/**
	 * Default pause (in ms) to while waiting for remote operate to generate output.
	 */
	public static final int EXECUTION_PAUSE = 100;

    /**
     * Value which defines when chmod is disabled.
     */
    public static final int DISABLE_CHMOD = -1;;
	
}

