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
 * Interface for module model. Used by ZK to render a model in the model panel.
 * 
 * The model object is introduced to avoid the issue PINEAPPLE-269: Loading a
 * new module results in a class cast exception from the execution panel.
 */
public interface Model {

	/**
	 * Set environment.
	 * 
	 * @param environment
	 *            environment name.
	 */
	public void setEnvironment(String environment);

	/**
	 * Get environment.
	 * 
	 * @return environment.
	 */
	public String getEnvironment();
}
