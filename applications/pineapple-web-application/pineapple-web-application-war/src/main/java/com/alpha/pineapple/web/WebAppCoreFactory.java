/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2016 Allan Thrane Andersen..
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

package com.alpha.pineapple.web;

import static com.alpha.pineapple.web.WebApplicationConstants.REACTOR_TOPIC_SERVICE_CREATED_REPORT;
import static com.alpha.pineapple.web.WebApplicationConstants.REACTOR_TOPIC_SERVICE_NOTIFICATION;
import static reactor.event.selector.Selectors.$;
import static reactor.event.selector.Selectors.T;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

import com.alpha.pineapple.CoreException;
import com.alpha.pineapple.CoreFactory;
import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ResultListener;
import com.alpha.pineapple.web.activity.ActivityRepository;
import com.alpha.pineapple.web.event.WebAppResultListenerImpl;
import com.alpha.pineapple.web.event.consumer.CreatedReportNotifierImpl;
import com.alpha.pineapple.web.event.consumer.ExecuteOperationActivityInvokerImpl;
import com.alpha.pineapple.web.event.consumer.OpenModuleActivityInvokerImpl;
import com.alpha.pineapple.web.event.consumer.ResultNotificationNotifierImpl;
import com.alpha.pineapple.web.model.ExecuteOperationActivity;
import com.alpha.pineapple.web.model.OpenModuleActivity;
import com.alpha.pineapple.web.report.ReportRepository;

/**
 * Factory class for Pineapple core component.
 */
public class WebAppCoreFactory {

	/**
	 * Logger object
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Pineapple core factory.
	 */
	@Resource
	CoreFactory coreFactory;

	/**
	 * Web application result listener.
	 */
	@Resource
	ResultListener webAppResultListener;

	/**
	 * Reactor.
	 */
	@Resource
	Reactor webAppReactor;

	/**
	 * Activity repository.
	 */
	@Resource
	ActivityRepository activityRepository;

	/**
	 * Report repository.
	 */
	@Resource
	ReportRepository reportRepository;

	/**
	 * Create report reactor consumer.
	 */
	@Resource
	Consumer<Event<ExecutionResultNotification>> createReport;

	/**
	 * Combined reactor consumer and ZK event listener.
	 */
	@Resource
	CreatedReportNotifierImpl createdReportNotification;

	/**
	 * Combined reactor consumer and ZK event listener.
	 */
	@Resource
	OpenModuleActivityInvokerImpl openModuleActivityInvoker;

	/**
	 * Combined reactor consumer and ZK event listener.
	 */
	@Resource
	ExecuteOperationActivityInvokerImpl executeOperationActivityInvoker;

	/**
	 * Combined reactor consumer and ZK event listener.
	 */
	@Resource
	ResultNotificationNotifierImpl resultNotificationNotifier;

	/**
	 * Create Pineapple core instance.
	 * 
	 * Registers Reactor consumers and configures the core component with a result
	 * listener {@linkplain WebAppResultListenerImpl} which forwards Pineapple
	 * events {@linkplain ExecutionResultNotification} as Reactor events.
	 *
	 * @return new Pineapple core instance.
	 * 
	 * @throws CoreException
	 *             If core instance creation fails.
	 */
	public PineappleCore createCore() throws CoreException {

		// register Reactor consumers
		webAppReactor.getConsumerRegistry().clear();
		webAppReactor.on($(REACTOR_TOPIC_SERVICE_NOTIFICATION), createReport);
		webAppReactor.on($(REACTOR_TOPIC_SERVICE_NOTIFICATION), resultNotificationNotifier);
		webAppReactor.on($(REACTOR_TOPIC_SERVICE_CREATED_REPORT), createdReportNotification);
		webAppReactor.on(T(OpenModuleActivity.class), openModuleActivityInvoker);
		webAppReactor.on(T(ExecuteOperationActivity.class), executeOperationActivityInvoker);

		// initialize activity repository
		activityRepository.initialize();

		// create core and register event listener
		ResultListener[] listeners = { webAppResultListener };
		PineappleCore coreComponent = coreFactory.createCore(listeners);

		// initialize report repository
		ExecutionResult result = new ExecutionResultImpl("Initialize report repository");
		reportRepository.initialize(result);

		return coreComponent;
	}

}
