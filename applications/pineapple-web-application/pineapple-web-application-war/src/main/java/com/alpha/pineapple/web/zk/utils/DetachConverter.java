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

package com.alpha.pineapple.web.zk.utils;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

/**
 * Implementation of the {@linkplain Converter} interface to detach a modal
 * window.
 * 
 * For more information: http://www.zkoss.org/forum/listComment/19038
 * http://tracker.zkoss.org/browse/ZK-986
 */
public class DetachConverter implements Converter, Serializable {

	private static final long serialVersionUID = 1463169907348730644L;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public Object coerceToUi(Object val, Component component, BindContext ctx) {

		Boolean b = (Boolean) Classes.coerce(Boolean.class, val);
		if (b != null && b.booleanValue()) {
			Event evt = new Event("onPostDetach", component);
			component.addEventListener(evt.getName(), _listener);
			Events.postEvent(evt);
		}
		return IGNORED_VALUE;
	}

	static private PostDetachListener _listener = new PostDetachListener();

	static class PostDetachListener implements EventListener<Event>, Serializable {

		/**
		 * Logger object.
		 */
		Logger logger = Logger.getLogger(this.getClass().getName());

		private static final long serialVersionUID = 1L;

		@Override
		public void onEvent(Event event) throws Exception {

			Component comp = event.getTarget();
			comp.removeEventListener(event.getName(), this);
			comp.detach();
		}
	}

	@Override
	public Object coerceToBean(Object val, Component component, BindContext ctx) {
		return val;
	}

}