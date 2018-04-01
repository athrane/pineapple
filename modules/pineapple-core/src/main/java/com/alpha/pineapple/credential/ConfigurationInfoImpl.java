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

package com.alpha.pineapple.credential;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.Validate;

/**
 * Implementation of the {@linkplain ConfigurationInfo} interface.
 */
public class ConfigurationInfoImpl implements ConfigurationInfo {

	/**
	 * Environment info's container.
	 */
	Map<String, EnvironmentInfo> environments;

	/**
	 * ConfigurationInfoImpl constructor.
	 * 
	 * @param environments
	 *            collection of environments info's.
	 */
	public ConfigurationInfoImpl(Map<String, EnvironmentInfo> environments) {
		Validate.notNull(environments, "infos is undefined.");
		this.environments = environments;
	}

	@Override
	public EnvironmentInfo[] getEnvironments() {
		Collection<EnvironmentInfo> values = environments.values();
		return (EnvironmentInfo[]) values.toArray(new EnvironmentInfo[values.size()]);
	}

	@Override
	public boolean containsEnvironment(String id) {
		return environments.containsKey(id);
	}

	@Override
	public EnvironmentInfo getEnvironment(String id) {
		if (!containsEnvironment(id))
			return null;
		return environments.get(id);
	}

	@Override
	public void addEnvironment(EnvironmentInfo environmentInfo) {
		Validate.notNull(environmentInfo, "environmentInfo is undefined.");
		if (containsEnvironment(environmentInfo.getId()))
			return;
		environments.put(environmentInfo.getId(), environmentInfo);
	}

	@Override
	public void deleteEnvironment(EnvironmentInfo environmentInfo) {
		Validate.notNull(environmentInfo, "environmentInfo is undefined.");
		if (!containsEnvironment(environmentInfo.getId()))
			return;
		environments.remove(environmentInfo.getId());
	}

}
