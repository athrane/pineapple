/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.module;

/**
 * Exception class for signaling undefined model in module repository.
 */
public class ModelNotFoundException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4848432043385964993L;

	/**
	 * ModelNotFoundException constructor.
	 * 
	 * @param message
	 *            error message.
	 * @param cause
	 *            cause of the exception.
	 */
	public ModelNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * ModelNotFoundException constructor.
	 * 
	 * @param message
	 *            error message.
	 */
	public ModelNotFoundException(String message) {
		super(message);
	}

}
