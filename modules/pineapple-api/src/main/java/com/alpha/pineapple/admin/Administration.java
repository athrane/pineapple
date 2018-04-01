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

package com.alpha.pineapple.admin;

import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.OperationTask;
import com.alpha.pineapple.execution.ResultRepository;
import com.alpha.pineapple.execution.scheduled.ScheduledOperationRespository;
import com.alpha.pineapple.plugin.repository.PluginRepository;
import com.alpha.pineapple.resource.ResourceRepository;
import com.alpha.pineapple.module.ModuleRepository;;

/**
 * Pineapple Core administration interface. This is the main interface for
 * execution of administration tasks, e.g. configuration of the core component.
 */
public interface Administration {

	/**
	 * Get resource repository used for resource management of the environment
	 * configuration.
	 * 
	 * @return resource repository.
	 */
	ResourceRepository getResourceRepository();

	/**
	 * Get result repository used for execution management.
	 * 
	 * @return result repository.
	 */
	ResultRepository getResultRepository();

	/**
	 * Get credential provider used for credential management.
	 * 
	 * @return credential provider used for credential management.
	 */
	CredentialProvider getCredentialProvider();

	/**
	 * Set credential provider used for credential management.
	 * 
	 * @param provider
	 *            credential provider used for credential management.
	 */
	void setCredentialProvider(CredentialProvider provider);

	/**
	 * Get module repository used for execution management.
	 * 
	 * @return module repository.
	 */
	ModuleRepository getModuleRepository();

	/**
	 * Get synchronous operation task used for execution management.
	 */
	OperationTask getOperationTask();

	/**
	 * Get plugin repository used for plugin management.
	 * 
	 * @return plugin repository.
	 */
	PluginRepository getPluginRepository();

	/**
	 * Get scheduled operation repository used for management of scheduled
	 * operations.
	 * 
	 * @return scheduled operation repository.
	 */
	ScheduledOperationRespository getScheduledOperationRespository();

}
