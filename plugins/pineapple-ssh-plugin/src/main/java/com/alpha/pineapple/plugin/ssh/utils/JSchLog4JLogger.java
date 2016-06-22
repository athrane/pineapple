/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.plugin.ssh.utils;

import com.jcraft.jsch.Logger;

/**
 * Implementation of the JSCH {@linkplain Logger} which bridges 
 * log events to Log4j.
 */
public class JSchLog4JLogger implements Logger {

	/**
     * Log4j logger object.
     */
	org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( this.getClass().getName() );
	
	@Override
	public boolean isEnabled(int arg0) {
		return true;
	}

	@Override
	public void log(int level, String message) {
		switch (level) {
			case DEBUG:
				logger.debug(message);
		        break;
		    case INFO:
				logger.info(message);
		        break;
		    case WARN:
				logger.warn(message);
		        break;
		    case ERROR:
				logger.error(message);
		        break;
		    case FATAL:
				logger.fatal(message);
		        break;
		}
	}

}
