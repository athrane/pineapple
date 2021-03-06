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

import static com.alpha.pineapple.web.WebApplicationConstants.COMPLETED_REPORT_CREATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_GLOBALCOMMAND_ARGS;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;

import reactor.function.Consumer;

import com.alpha.pineapple.web.event.EventDispatcher;

/**
 * Implementation of the {@linkplain EventListener} and {@linkplain Consumer}
 * interface which receives a Reactor event in the accept method.
 * 
 * The received Reactor event is transformed into a ZK event which is dispatched
 * to this class as ZK a event listener.
 * 
 * The purpose is to shift from the Reactor thread into ZK controlled threads.
 * 
 * The received ZK event is posted as global command with the APPLICATION scope.
 * The purpose is to notify the report panel view model that report creation is
 * completed and the view should be updated in all GUI's
 */
public class CreatedReportNotifierImpl implements EventListener<Event>, Consumer<reactor.event.Event<String>> {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Reactor-to-ZK event dispatcher.
	 */
	@Resource
	EventDispatcher eventDispatcher;

	@Override
	public void onEvent(Event evt) throws Exception {
		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, EventQueues.APPLICATION,
				COMPLETED_REPORT_CREATION_GLOBALCOMMAND, NULL_GLOBALCOMMAND_ARGS);
	}

	@Override
	public void accept(reactor.event.Event<String> t) {
		eventDispatcher.dispatchZkEvent(this);
	}

}
