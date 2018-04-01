/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
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

package com.alpha.pineapple.module;

import java.io.File;

/**
 * Interface to represent information about a module in the module repository.
 */
public interface ModuleInfo {

	/**
	 * Get Id of module.
	 * 
	 * @return Id of module.
	 */
	public String getId();

	/**
	 * Get the environments for which the module contains module models.
	 * 
	 * @return the environments for which the module contains module models.
	 */
	public String[] getModelEnvironments();

	/**
	 * Return true if module contains module model for requested environment.
	 * 
	 * @return true if module contains module model for requested environment.
	 */
	public boolean containsModel(String environment);

	/**
	 * Get directory where module is located.
	 * 
	 * @return directory where module is located.
	 */
	public File getDirectory();

	/**
	 * Returns true if the module descriptor is defined.
	 * 
	 * @return true if the module descriptor is defined.
	 */
	public boolean isDescriptorDefined();

	/**
	 * Add environment to module, e.g. a module model.
	 * 
	 * Please notice that adding an environment doesn't result in the creation of a
	 * module model since this interface is a meta data interface.
	 * 
	 * @param environment
	 *            added to the module.
	 */
	public void addEnvironment(String environment);

	/**
	 * Delete environment from module e.g. a module model.
	 * 
	 * Please notice that deleting an environment doesn't result in the deletion of
	 * a module model since this interface is a meta data interface.
	 * 
	 * @param environment
	 *            deleted from the module.
	 */
	public void deleteEnvironment(String environment);

}
