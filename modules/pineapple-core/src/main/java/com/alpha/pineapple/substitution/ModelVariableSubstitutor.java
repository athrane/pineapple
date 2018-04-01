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

package com.alpha.pineapple.substitution;

import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.model.module.Module;
import com.alpha.pineapple.model.module.model.Models;

/**
 * Interface for variable substitution in module models.
 */
public interface ModelVariableSubstitutor {

	/**
	 * Create object which returns variable substituted values for string
	 * attributes.
	 * 
	 * The properties of the resource are used for resolution of variables. The
	 * variables defined in the model are used for resolution of variables.
	 * 
	 * The variables defined in the model takes presence over those defined in the
	 * resource, i.e. if var a="r" is defined in the resource and a="m" is defined
	 * in the model then the variable "a" will be resolve to the value "m".
	 *
	 * @param module
	 *            module descriptor model.
	 * @param model
	 *            module model.
	 * @param resource
	 *            resource object.
	 * @param targetObject
	 *            target object whose string attributes (and those of any child
	 *            objects) are processed for variables.
	 * 
	 * @throws VariableSubstitutionException
	 *             if initialization of variables from the session fails.
	 */
	<T> T createObjectWithSubstitution(Module module, Models model, Resource resource, T targetObject)
			throws VariableSubstitutionException;

}
