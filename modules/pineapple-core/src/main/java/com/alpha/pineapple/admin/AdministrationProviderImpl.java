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

package com.alpha.pineapple.admin;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.OperationTask;
import com.alpha.pineapple.execution.ResultRepository;
import com.alpha.pineapple.execution.scheduled.ScheduledOperationRespository;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.module.ModuleRepository;
import com.alpha.pineapple.plugin.repository.PluginRepository;
import com.alpha.pineapple.resource.ResourceRepository;

/**
 * Default implementation of the {@linkplain Administration} and
 * {@linkplain AdministrationProvider} interface.
 */
public class AdministrationProviderImpl implements Administration, AdministrationProvider {

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
	 * Resource repository.
	 */
	@Resource
	ResourceRepository resourceRepository;

	/**
	 * Result repository.
	 */
	@Resource
	ResultRepository resultRepository;

	/**
	 * Module repository.
	 */
	@Resource
	ModuleRepository moduleRepository;

	/**
	 * Plugin repository.
	 */
	@Resource
	PluginRepository pluginRepository;

	/**
	 * Scheduled operation repository.
	 */
	@Resource
	ScheduledOperationRespository scheduledOperationRepository;

	/**
	 * Synchronous operation task.
	 */
	@Resource
	OperationTask operationTask;

	/**
	 * Credential provider.
	 * 
	 * This provider is not injected but initialized as part of the core component
	 * initialization process which will set the provider.
	 */
	CredentialProvider credentialProvider;

	@Override
	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	@Override
	public ResultRepository getResultRepository() {
		return resultRepository;
	}

	@Override
	public CredentialProvider getCredentialProvider() {
		return credentialProvider;
	}

	@Override
	public void setCredentialProvider(CredentialProvider provider) {

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { provider };
			String message = messageProvider.getMessage("api.set_credentialprovider_info", args);
			logger.debug(message);
		}

		credentialProvider = provider;
	}

	@Override
	public ModuleRepository getModuleRepository() {
		return moduleRepository;
	}

	@Override
	public PluginRepository getPluginRepository() {
		return pluginRepository;
	}

	@Override
	public OperationTask getOperationTask() {
		return operationTask;
	}

	@Override
	public ScheduledOperationRespository getScheduledOperationRespository() {
		return scheduledOperationRepository;
	}

}
