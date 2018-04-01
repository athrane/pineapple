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

package com.alpha.testutils.testplugins.exception.channeled;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.session.RetrySessionHandlerImpl;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionConnectException;

/**
 * Operation class for test plugin which throws channeled
 * {@linkplain SessionConnectException}. to simulate the behavior of the
 * {@linkplain RetrySessionHandlerImpl}.
 */
@PluginOperation(ThrowsSessionConnectExceptionOperationImpl.OPERATION_ID)
public class ThrowsSessionConnectExceptionOperationImpl implements Operation {

	public static final String OPERATION_ID = "throwSessionConnectException";

	@Override
	public void execute(Object content, Session session, ExecutionResult result) throws PluginExecutionFailedException {
		SessionConnectException e = new SessionConnectException(result.getDescription());
		throw new RuntimeException(e);
	}

}
