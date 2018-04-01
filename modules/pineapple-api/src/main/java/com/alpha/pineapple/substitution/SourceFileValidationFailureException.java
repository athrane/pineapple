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

package com.alpha.pineapple.substitution;

/**
 * Exception class for signaling errors during validation of the fitness of a
 * source file for variable substitution.
 */
public class SourceFileValidationFailureException extends VariableSubstitutionException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4848432043385964993L;

	/**
	 * SourceFileValidationFailureException constructor.
	 * 
	 * @param message
	 *            Error message.
	 * @param cause
	 *            Cause of the exception.
	 */
	public SourceFileValidationFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * SourceFileValidationFailureException constructor.
	 * 
	 * @param message
	 *            error message.
	 */
	public SourceFileValidationFailureException(String message) {
		super(message);
	}

}
