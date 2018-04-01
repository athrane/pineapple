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
package com.alpha.pineapple.web.event;

import static com.alpha.pineapple.web.WebApplicationConstants.REACTOR_TOPIC_SERVICE_NOTIFICATION;

import javax.annotation.Resource;

import reactor.core.Reactor;
import reactor.event.Event;

import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ResultListener;

/**
 * Web application result listener which implements the
 * {@linkplain ResultListener} interface to receive result notifications from
 * the Pineapple core component.
 * 
 * Received events are forwarded to {@linkplain Reactor} as events. The events
 * are posted as result notifications.
 */
public class WebAppResultListenerImpl implements ResultListener {

	/**
	 * Web application reactor.
	 */
	@Resource
	Reactor webAppReactor;

	/**
	 * Post event with result notification as key.
	 */
	@Override
	public void notify(ExecutionResultNotification notification) {
		webAppReactor.notify(REACTOR_TOPIC_SERVICE_NOTIFICATION, Event.wrap(notification));
	}

}
