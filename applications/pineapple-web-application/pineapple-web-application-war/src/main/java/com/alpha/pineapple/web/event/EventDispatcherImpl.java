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
package com.alpha.pineapple.web.event;

import static com.alpha.pineapple.web.WebApplicationConstants.ATTR_DESKTOP_EVENT_DISPATCHER;

import java.util.HashSet;
import java.util.Set;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import com.alpha.pineapple.web.zk.asynctask.event.GenericEvent;

import reactor.function.Consumer;

/**
 * Implementation of the {@linkplain EventDispatcher} interface which
 * initializes the reactor {@linkplain Consumer} instances and implements the
 * mapping from the Reactor threads to ZK threads.
 * 
 * A consumer instance receives reactor events which are transformed and
 * dispatched as ZK events to themselves where they play the role of ZK event
 * listener, through the implementation of the {@linkplain EventListener}
 * interface.
 * 
 * The purpose is to shift from the Reactor thread into ZK controlled threads.
 * 
 * The event dispatcher will dispatch events to all registered desktops. A
 * registered desktop is unregistered when the desktop is destroyed.
 */
public class EventDispatcherImpl implements EventDispatcher {

	/**
	 * Generic event used for dispatch ZK event which doesn't carry any arguments.
	 */
	final static GenericEvent ZK_GENERIC_EVENT = new GenericEvent();

	/**
	 * Registered desktop's.
	 */
	Set<Desktop> desktops = new HashSet<Desktop>();

	@Override
	public void register(Desktop desktop) {
		// register desktop and register dispatcher with desktop
		if (!desktops.contains(desktop))
			desktops.add(desktop);
		desktop.setAttribute(ATTR_DESKTOP_EVENT_DISPATCHER, this);
	}

	@Override
	public void unregister(Desktop desktop) {
		if (desktops.contains(desktop))
			desktops.remove(desktop);
	}

	@Override
	public void dispatchZkEvent(Event zkEvent, EventListener<Event> eventListener) {
		if (!areDesktopsDefined()) {
			return;

		}

		// schedule events
		for (Desktop desktop : desktops) {
			Executions.schedule(desktop, eventListener, zkEvent);
		}
	}

	@Override
	public void dispatchZkEvent(EventListener<Event> eventListener) {
		dispatchZkEvent(ZK_GENERIC_EVENT, eventListener);
	}

	/**
	 * Returns true if desktops are defined.
	 * 
	 * @return true if desktop are defined.
	 */
	boolean areDesktopsDefined() {
		return (!desktops.isEmpty());
	}

}
