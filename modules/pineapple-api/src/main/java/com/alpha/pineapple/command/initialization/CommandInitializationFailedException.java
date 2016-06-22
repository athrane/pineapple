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

package com.alpha.pineapple.command.initialization;

import com.alpha.pineapple.command.CommandException;

/**
 * Exception class for signaling failures during command initialization.
 */
public class CommandInitializationFailedException extends CommandException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4848432043385964993L;

    /**
     * ContextValidationFailedException constructor.
     * 
     * @param message
     *            Error message.
     */
    public CommandInitializationFailedException(String message) {
	super(message);
    }

    /**
     * ContextValidationFailedException constructor.
     * 
     * @param message
     *            Error message.
     * @param cause
     *            Cause of the exception.
     */
    public CommandInitializationFailedException(String message, Throwable cause) {
	super(message, cause);
    }

}
