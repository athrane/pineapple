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

package com.alpha.easymockutils;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Implementation of the @link {@link IAnswer} interface which returns the key
 * and arguments used to invoke a message provider.
 */
public class MessageProviderAnswerImpl implements IAnswer<String> {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Will log the content if the arguments that the mock message provider is
	 * invoked with and return the key as answer.
	 * 
	 * @return The message source key as answer.
	 * 
	 * @see org.easymock.IAnswer#answer()
	 */
	public String answer() throws Throwable {

		// get current args
		Object[] args = EasyMock.getCurrentArguments();

		if (logger.isDebugEnabled()) {

			// create message
			StringBuilder message = new StringBuilder();
			message.append("Message provider invoked with key <");
			message.append(args[0]);
			message.append(">");

			if ((args.length > 1) && (args[1] != null)) {
				message.append(" and args <");
				message.append(ReflectionToStringBuilder.toString(args[1]));
				message.append(">");
			}

			message.append(".");

			// log message
			logger.debug(message.toString());
		}

		// return key as answer
		String key = (String) args[0];
		return key;
	}

}
