/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2016 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.web.event.consumer;

import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_SCOPE;
import static com.alpha.pineapple.web.WebApplicationConstants.START_OPERATION_ENVIRONMENT_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.START_OPERATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.START_OPERATION_MODULE_INFO_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.START_OPERATION_OPERATION_ARG;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleRepository;
import com.alpha.pineapple.web.event.EventDispatcher;
import com.alpha.pineapple.web.model.ExecuteOperationActivity;

import reactor.function.Consumer;

/**
 * Implementation of the {@linkplain EventListener} and {@linkplain Consumer}
 * interface which receives a Reactor event in the accept method.
 * 
 * The received Reactor event is transformed into a ZK event which is dispatched
 * to this class as ZK a event listener.
 * 
 * The purpose is to shift from the Reactor thread into ZK controlled threads.
 * 
 * The received ZK event is posted as global command to notify the the execution
 * controller that is should execute the operation.
 */
public class ExecuteOperationActivityInvokerImpl
		implements EventListener<Event>, Consumer<reactor.event.Event<ExecuteOperationActivity>> {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Module repository.
	 */
	@Resource
	ModuleRepository moduleRepository;

	/**
	 * Reactor-to-ZK event dispatcher.
	 */
	@Resource
	EventDispatcher eventDispatcher;

	/**
	 * Execute operation activity.
	 */
	ExecuteOperationActivity activity;

	@Override
	public void onEvent(Event evt) throws Exception {

		// post global command to which triggers execution in execution panel
		// view model
		Map<String, Object> args = new HashMap<String, Object>();
		ModuleInfo moduleInfo = moduleRepository.get(activity.getModule());
		args.put(START_OPERATION_MODULE_INFO_ARG, moduleInfo);
		args.put(START_OPERATION_OPERATION_ARG, activity.getOperation());
		args.put(START_OPERATION_ENVIRONMENT_ARG, activity.getEnvironment());

		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, START_OPERATION_GLOBALCOMMAND, args);

		activity = null;
	}

	@Override
	public void accept(reactor.event.Event<ExecuteOperationActivity> t) {
		activity = t.getData();
		eventDispatcher.dispatchZkEvent(this);
	}

}
