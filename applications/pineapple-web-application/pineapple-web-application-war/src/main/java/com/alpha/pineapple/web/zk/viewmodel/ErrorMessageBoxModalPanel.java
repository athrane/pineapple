/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2016 Allan Thrane Andersen..
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

package com.alpha.pineapple.web.zk.viewmodel;

import static com.alpha.pineapple.web.WebApplicationConstants.ERROR_MESSAGE_MODAL_EXCEPTION_ARG;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * ZK view model for the error modal operation panel (modal).
 */
public class ErrorMessageBoxModalPanel {

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider webMessageProvider;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Exception which is reported.
	 */
	Exception exception;

	/**
	 * Initialize view model.
	 * 
	 * @param exception
	 *            exception to be reported.
	 */
	@Init
	public void init(@ExecutionArgParam(ERROR_MESSAGE_MODAL_EXCEPTION_ARG) Exception exception) {
		this.exception = exception;
	}

	/**
	 * Get description.
	 * 
	 * @return description.
	 */
	public String getDescription() {
		if (exception == null)
			return "n/a";
		if (exception.getMessage() == null)
			return "n/a";
		if (exception.getMessage().isEmpty())
			return "n/a";
		return exception.getMessage();
	}

	/**
	 * Get stack trace from exception.
	 * 
	 * @return stack trace from exception.
	 */
	public String getStacktrace() {
		if (exception == null)
			return "n/a";
		String stacktrace = StackTraceHelper.getStrackTrace(exception);
		if (stacktrace == null)
			return "n/a";
		if (stacktrace.isEmpty())
			return "n/a";
		return stacktrace;
	}

	/**
	 * Event handler for the command "confirm".
	 * 
	 * The event is triggered from the "confirm" button menu.
	 */
	@Command
	public void confirm() {
		// NO-OP
	}

}
