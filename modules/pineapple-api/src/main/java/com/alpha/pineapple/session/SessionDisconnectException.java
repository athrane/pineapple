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

package com.alpha.pineapple.session;

/**
 * Exception class for signaling errors in sessions during disconnecting from a
 * resource.
 */
public class SessionDisconnectException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4848432043385964993L;

	/**
	 * SessionConnectionException constructor.
	 * 
	 * @param message
	 *            error message.
	 * @param cause
	 *            cause of the exception.
	 */
	public SessionDisconnectException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * SessionConnectionException constructor.
	 * 
	 * @param message
	 *            error message.
	 */
	public SessionDisconnectException(String message) {
		super(message);
	}

}
