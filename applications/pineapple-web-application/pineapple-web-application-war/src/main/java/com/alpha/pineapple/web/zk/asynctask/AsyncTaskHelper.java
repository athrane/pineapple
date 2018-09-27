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
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 */

package com.alpha.pineapple.web.zk.asynctask;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.springframework.scheduling.annotation.Async;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * Helper class for execution of asynchronous tasks {@linkplain AsyncTask} and
 * scheduling of ZK GUI events {@linkplain org.zkoss.zk.ui.event.Event}.
 */
@Deprecated
public class AsyncTaskHelper {

	/**
	 * NULL global command arguments.
	 */
	static final Map<String, Object> NULL_GLOBALCOMMAND_ARGS = new HashMap<String, Object>();

	/**
	 * Enable ZK server push.
	 * 
	 * @return server push enabled desktop.
	 */
	public Desktop enableServerPush() {
		Desktop desktop = Executions.getCurrent().getDesktop();
		desktop.enableServerPush(true);
		return desktop;
	}

	/**
	 * Asynchronous scheduling of ZK event.
	 * 
	 * @param event
	 *            ZK event to schedule.
	 * @param desktop
	 *            ZK desktop.
	 * @param eventListener
	 *            ZK eventListener, i.e. the ZK View in the form of a ZK composer.
	 */
	public void scheduleEvent(Event event, Desktop desktop, EventListener<Event> eventListener) {
		Validate.notNull(event);
		Validate.notNull(desktop);
		Validate.notNull(eventListener);

		// schedule event
		Executions.schedule(desktop, eventListener, event);
	}

	/**
	 * Execution asynchronous task.
	 * 
	 * @param task
	 *            Asynchronous tasks which is executed.
	 * @param desktop
	 *            ZK desktop.
	 * @param eventListener
	 *            ZK eventListener, i.e. the ZK View in the form of a ZK composer.
	 */
	@Async("asyncTaskExecutor")
	public void executeAsync(AsyncTask task, Desktop desktop, EventListener<Event> eventListener) {
		task.runAsync(desktop, eventListener);
	}

}
