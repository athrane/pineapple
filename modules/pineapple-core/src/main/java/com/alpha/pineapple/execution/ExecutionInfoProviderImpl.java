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

import org.apache.commons.chain.Context;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Implementation of the {@link ExecutionInfoProvider} interface.
 */
public class ExecutionInfoProviderImpl implements ExecutionInfoProvider {

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
	 * Execution context repository.
	 */
	@Resource
	ExecutionContextRepository executionContextRepository;

	public ExecutionInfo get(ExecutionResult result) {
		Validate.notNull(result, "result is undefined.");

		// get context
		Context context = executionContextRepository.get(result);

		// throw exception if context is undefined
		if (context == null) {
			String message = messageProvider.getMessage("eip.context.not_found_error");
			throw new ExecutionInfoNotFoundException(message);
		}

		// return info if defined in context
		if (context.containsKey(CoreConstants.EXECUTION_INFO_KEY)) {
			return (ExecutionInfo) context.get(CoreConstants.EXECUTION_INFO_KEY);
		}

		// throw exception
		String message = messageProvider.getMessage("eip.info.not_found_error");
		throw new ExecutionInfoNotFoundException(message);
	}

}
