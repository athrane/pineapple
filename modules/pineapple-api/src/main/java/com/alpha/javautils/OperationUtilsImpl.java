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

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.session.Session;

/**
 * Operation of the {@link OperationUtils } interface.
 */
public class OperationUtilsImpl implements OperationUtils {

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "apiMessageProvider")
	MessageProvider messageProvider;

	public void validateContentType(Object content, Class<?>[] legalTypes) throws PluginExecutionFailedException {

		// declare class name
		String currentClassNameString = null;

		try {

			// iterate over the legal content types
			for (Class<?> legalType : legalTypes) {

				// get class name
				String classNameString = legalType.getName();

				// store name for error handling
				currentClassNameString = classNameString;

				// exit if there is a match
				if (Class.forName(classNameString).isInstance(content))
					return;
			}

		} catch (Exception e) {
			Object[] args = { currentClassNameString };
			String message = messageProvider.getMessage("ou.content_typecast_error", args);
			throw new PluginExecutionFailedException(message, e);
		}

		// signal failure to validate any of the types
		Object[] args = { ArrayUtils.toString(legalTypes) };
		String message = messageProvider.getMessage("ou.content_typecast_failed", args);
		throw new PluginExecutionFailedException(message);
	}

	public void validateSessionType(Session session, Class<?> legalType) throws PluginExecutionFailedException {

		// declare class name
		String currentClassNameString = null;

		try {

			// get class name
			String classNameString = legalType.getName();

			// store name for error handling
			currentClassNameString = classNameString;

			// exit if there is a match
			if (Class.forName(classNameString).isInstance(session))
				return;

		} catch (Exception e) {
			Object[] args = { currentClassNameString };
			String message = messageProvider.getMessage("ou.session_typecast_error", args);
			throw new PluginExecutionFailedException(message, e);
		}

		// signal failure to validate any of the types
		Object[] args = { ArrayUtils.toString(legalType) };
		String message = messageProvider.getMessage("ou.session_typecast_failed", args);
		throw new PluginExecutionFailedException(message);
	}

}
