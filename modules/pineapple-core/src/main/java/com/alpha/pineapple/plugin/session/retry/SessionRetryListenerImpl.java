/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
* Copyright (C) 2007-2015 Allan Thrane Andersen.
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 *   
 *    LoggingRetryListener.java created by AllanThrane on 20/06/2014 
 *    File revision  $Revision: 0.0 $
 */

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

package com.alpha.pineapple.plugin.session.retry;

import static com.alpha.pineapple.CoreConstants.MSG_SESSION;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.session.SessionConnectException;

/**
 * Implementation of the {@linkplain ResultCapturingRetryListener} interface
 * which registers failed session connect and disconnect in execution result and
 * logs.
 */
public class SessionRetryListenerImpl implements SessionRetryListener {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Result which captures retry information.
	 */
	ExecutionResult result;

	@Override
	public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {
		// no-op
	}

	@Override
	public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {

		// get exception
		Object[] args = { throwable.getMessage() };
		String message = null;

		// handle session connection error
		if (throwable instanceof SessionConnectException) {
			message = messageProvider.getMessage("srl.session_connect_error", args);
		} else {
			message = messageProvider.getMessage("srl.default_error", args);
		}

		// add result message
		if (result == null)
			return;
		result.addMessage(MSG_SESSION, message);
		return;
	}

	@Override
	public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
		return true; // proceed with first retry
	}

	@Override
	public void setResult(ExecutionResult result) {
		this.result = result;
	}

}
