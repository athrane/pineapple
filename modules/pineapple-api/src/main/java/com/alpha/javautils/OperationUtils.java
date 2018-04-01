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

package com.alpha.javautils;

import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.session.Session;

/**
 * Operation utility functions.
 */
public interface OperationUtils {

	/**
	 * Validate the content object can be cast to one of a set of legal types. An
	 * exception is thrown if the validation fails.
	 * 
	 * @param content
	 *            The content object whose type is validated.
	 * @param legalTypes
	 *            Set of legal values.
	 * 
	 * @throws PluginExecutionFailedException
	 *             if content validation fails.
	 */
	public void validateContentType(Object content, Class<?>[] legalTypes) throws PluginExecutionFailedException;

	/**
	 * Validate the session can cast to expected type.
	 * 
	 * @param session
	 *            The session object whose type is validated.
	 * @param legalType
	 *            The expected session type.
	 * 
	 * @throws PluginExecutionFailedException
	 *             if session type validation fails.
	 */
	public void validateSessionType(Session session, Class<?> legalType) throws PluginExecutionFailedException;

}
