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

package com.alpha.pineapple.web.zk.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;

/**
 * Implementation of the {@linkplain Converter} interface which converts a
 * string based {@linkplain ExecutionState} into a color.
 */
public class ExecutionStateStyleConverter implements Converter<String, Object, Component> {

	@Override
	public Object coerceToBean(String arg0, Component arg1, BindContext arg2) {
		return IGNORED_VALUE;
	}

	@Override
	public String coerceToUi(Object arg0, Component arg1, BindContext arg2) {
		if (arg0 == null)
			return "INTERNAL ERROR";
		final String result = (String) arg0;
		switch (ExecutionState.valueOf(result)) {
		case FAILURE:
			return "color:red";
		case ERROR:
			return "color:red";
		default:
			return "color:black";
		}
	}

}
