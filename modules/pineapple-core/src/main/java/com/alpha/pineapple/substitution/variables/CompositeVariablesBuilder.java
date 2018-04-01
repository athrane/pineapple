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

package com.alpha.pineapple.substitution.variables;

/**
 * Interface for variables builder which can build variables from multiple
 * combined variable builders.
 * 
 * When building the {@linkplain Variables} instance, then variables from the
 * first registered builder are added first, then variables from the second
 * registered builder are added. And so on.
 * 
 * If a builder defines a variable which is already defined through the
 * processing of an earlier builder (maybe with the same value or not) then it
 * ignored, e.g. a variable takes presence from the order of which the builders
 * are registered.
 * 
 * Builders are added by name. When the {@linkplain Variables} instance is built
 * then all variables in a builder will also be registered with the builder name
 * as a prefix. Example: If a builder is registered with the name "model" and it
 * defines two variables v1=a and v2=b. Then the building will result in the
 * registration of four variables: model.v1=a, v1=a, model.v2=b and v2=b.
 *
 * Prefixed variables are processed for all builder builders prior to processing
 * non-prefixed variables.
 */
public interface CompositeVariablesBuilder extends VariablesBuilder {

	/**
	 * Register named variable builder.
	 * 
	 * @param name
	 *            builder name.
	 * @param builder
	 *            builder.
	 */
	public void addBuilder(String name, VariablesBuilder builder);

}
