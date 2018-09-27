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

package com.alpha.pineapple.execution;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Implementation of the {@linkplain ExecutionContextRepository} interface.
 */
public class ExecutionContextRepositoryImpl implements ExecutionContextRepository {

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
	 * Collection of current contexts.
	 */
	Map<ExecutionResult, Context> contexts = new HashMap<ExecutionResult, Context>();

	@Override
	public void register(ExecutionInfo info, Context context) {
		Validate.notNull(context, "context is undefined");
		Validate.notNull(info, "info is undefined");

		ExecutionResult result = info.getResult();
		contexts.put(result, context);
	}

	@Override
	public void unregister(Context context) {
		Validate.notNull(context, "context is undefined");

		if (!contexts.containsValue(context))
			return;
		contexts.remove(context);
	}

	@Override
	public Context get(ExecutionInfo info) {
		Validate.notNull(info, "info is undefined");

		ExecutionResult result = info.getResult();
		if (!contexts.containsKey(result))
			return null;
		return contexts.get(result);
	}

	@Override
	public Context get(ExecutionResult result) {
		Validate.notNull(result, "result is undefined");

		// if context is defined return it
		if (contexts.containsKey(result)) {
			return contexts.get(result);
		}

		// exit if result is root result
		if (result.isRoot())
			return null;

		// try to resolve info using the parent result
		return get(result.getParent());
	}

}
