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

import java.util.stream.Stream;

import com.alpha.pineapple.execution.scheduled.ScheduledOperationInfo;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperations;
import com.alpha.pineapple.model.module.info.Modules;
import com.alpha.pineapple.model.report.Report;
import com.alpha.pineapple.model.report.Reports;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.resource.EnvironmentInfo;
import com.alpha.pineapple.resource.ResourceInfo;

/**
 * Maps object from Pineapple core component to schema generated objects to
 * returned by REST web services.
 */
public interface RestResultMapper {

	/**
	 * Map single environment info to model objects.
	 * 
	 * @param environmentInfo
	 *            environment info.
	 * 
	 * @return environment configuration containing a single environment.
	 */
	Configuration mapEnvironment(EnvironmentInfo environmentInfo);

	/**
	 * Map collection of environments info's to model objects.
	 * 
	 * @param environmentInfos
	 *            collection of environments info's
	 * 
	 * @return environment configuration containing a collection of environments.
	 */
	Configuration mapResourceConfiguration(EnvironmentInfo[] environmentInfos);

	/**
	 * Map collection of environments info's to model objects.
	 * 
	 * @param environmentInfos
	 *            collection of environments info's
	 * 
	 * @return environment configuration containing a collection of environments.
	 */
	Configuration mapCredentialConfiguration(com.alpha.pineapple.credential.EnvironmentInfo[] environmentInfos);

	/**
	 * Map model credential into model object.
	 * 
	 * @param environment
	 *            environment ID.
	 * @param credential
	 *            model credential.
	 * 
	 * @return environment configuration containing a single credential.
	 */
	Configuration mapCredential(String environment, Credential credential);

	/**
	 * Map model resource into model object.
	 *
	 * @param environment
	 *            environment ID.
	 * @param resourceInfo
	 *            resource info.
	 * @return
	 */
	Configuration mapResource(String environment, ResourceInfo resourceInfo);

	/**
	 * Map stream of scheduled operation info's to model objects.
	 * 
	 * @param operations
	 *            stream of scheduled operation info's.
	 * 
	 * @return scheduled operation model containing mapped scheduled operations.
	 */
	ScheduledOperations mapScheduledOperations(Stream<ScheduledOperationInfo> operations);

	/**
	 * Map array of module info's to model objects.
	 * 
	 * @param infos
	 *            array of modules info's.
	 * 
	 * @return module model containing mapped module info's.
	 */
	Modules mapModules(ModuleInfo[] infos);

	/**
	 * Map stream of reports to model objects.
	 * 
	 * @param operations
	 *            stream of reports.
	 * 
	 * @return reports model containing reports.
	 */
	Reports mapReports(Stream<Report> reports);

}
