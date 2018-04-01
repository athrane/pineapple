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

package com.alpha.pineapple.web.model;

/**
 * Implementation of the {@linkplain Model} interface.
 */
public class ModelImpl implements Model {

	/**
	 * Environment name.
	 */
	public String environment;

	/**
	 * ModelImpl constructor.
	 */
	public ModelImpl() {
	}

	public ModelImpl(String environment) {
		this.environment = environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getEnvironment() {
		return environment;
	}
}
