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

package com.alpha.pineapple.execution;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;

import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Implementation of the {@link OperationTask} interface which supports an
 * asynchronous task execution.
 * 
 * Please notice that only the method execute(ExecutionInfo info) support
 * asynchronous execution of an operation. The other two methods throws an
 * {@linkplain UnsupportedOperationException}.
 */
public class AsyncOperationTaskImpl implements OperationTask {

	/**
	 * Synchronous operation task.
	 */
	@Resource
	OperationTask operationTask;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	@Async
	@Override
	public void execute(ExecutionInfo info) {
		operationTask.execute(info);
	}

	@Override
	public ExecutionInfo execute(String operation, String environment, String module) {
		String message = messageProvider.getMessage("aot.unspported_method");
		throw new UnsupportedOperationException(message);
	}

	@Override
	public ExecutionInfo executeComposite(String operation, String environment, String module, String description,
			ExecutionResult result) {
		String message = messageProvider.getMessage("aot.unspported_method");
		throw new UnsupportedOperationException(message);
	}

}
