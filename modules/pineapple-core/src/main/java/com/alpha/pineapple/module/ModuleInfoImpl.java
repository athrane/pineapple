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

package com.alpha.pineapple.module;

import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Implementation of the { {@link ModuleInfo} interface.
 */
public class ModuleInfoImpl implements ModuleInfo {

	/**
	 * Null module info.
	 */
	static final ModuleInfo nullModuleInfo = new ModuleInfoImpl("null");

	/**
	 * Id module.
	 */
	String id;

	/**
	 * Set of supported environments for which the module contains a module model.
	 */
	SortedSet<String> environments;

	/**
	 * Module directory
	 */
	File directory;

	/**
	 * Defined whether module descriptor is defined.
	 */
	boolean isDescriptorDefined;

	/**
	 * ModuleInfoImpl no-arg constructor.
	 */
	ModuleInfoImpl() {
		this.environments = new TreeSet<String>();
		this.isDescriptorDefined = false;
	}

	/**
	 * ModuleInfoImpl no-arg constructor.
	 */
	ModuleInfoImpl(String id) {
		this();
		setId(id);
	}

	public String getId() {
		return id;
	}

	/**
	 * Set Id of module.
	 * 
	 * @param name
	 *            Id of module.
	 */
	void setId(String id) {
		this.id = id;
	}

	/**
	 * Set module directory.
	 * 
	 * @param directory
	 *            module directory.
	 */
	void setDirectory(File directory) {
		this.directory = directory;
	}

	public void addEnvironment(String environment) {
		this.environments.add(environment);
	}

	public void deleteEnvironment(String environment) {
		if (!environments.contains(environment))
			return;
		environments.remove(environment);
	}

	public String[] getModelEnvironments() {
		return environments.toArray(new String[environments.size()]);
	}

	public boolean containsModel(String environment) {
		return environments.contains(environment);
	}

	public File getDirectory() {
		return directory;
	}

	public void setIsDescriptorDefined(boolean isDescriptorDefined) {
		this.isDescriptorDefined = isDescriptorDefined;
	}

	public boolean isDescriptorDefined() {
		return this.isDescriptorDefined;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	/**
	 * Factory method for creation of module info instances.
	 * 
	 * @param id
	 *            Id of module.
	 * @param environments
	 *            Collection of environments for which models exist.
	 * @param isDefined
	 *            Signals whether the module descriptor is defined for the module.
	 * @param directory
	 *            Directory where the module is located.
	 * 
	 * @return module info which describes a module in the repository.
	 */
	public static ModuleInfo getInstance(String id, String[] environments, boolean isDefined, File directory) {
		ModuleInfoImpl info = new ModuleInfoImpl();

		// set id
		info.setId(id);

		// set directory
		info.setDirectory(directory);

		// set whether descriptor is defined
		info.setIsDescriptorDefined(isDefined);

		// set environments
		for (String environment : environments) {
			info.addEnvironment(environment);
		}

		return info;
	}

	/**
	 * Factory method for creation of null module info instances.
	 * 
	 * @return null module info.
	 */
	public static ModuleInfo getNullInstance() {
		return nullModuleInfo;
	}

}
