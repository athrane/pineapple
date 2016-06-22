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
package com.alpha.pineapple.web.zk.lifecycle;

import static com.alpha.pineapple.web.WebApplicationConstants.ATTR_DESKTOP_EVENT_DISPATCHER;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.util.DesktopCleanup;

import com.alpha.pineapple.web.event.EventDispatcher;

/**
 * Implementation of the {@linkplain DesktopCleanup} interface which unregisters
 * ZK desktop from event dispatcher and removes event dispatcher from ZK
 * desktop.
 * 
 * The life cycle event listener is configured in zk.xml through the definition
 * of a listener-class element.
 */
public class LifecycleEventListener implements DesktopCleanup {

    @Override
    public void cleanup(Desktop desktop) throws Exception {

	// invalidate session
	Session session = desktop.getSession();
	session.invalidate();

	// unregister desktop from event dispatcher
	if (!desktop.hasAttribute(ATTR_DESKTOP_EVENT_DISPATCHER))
	    return;
	EventDispatcher dispatcher = (EventDispatcher) desktop.getAttribute(ATTR_DESKTOP_EVENT_DISPATCHER);
	dispatcher.unregister(desktop);

	// remove attribute from desktop
	desktop.removeAttribute(ATTR_DESKTOP_EVENT_DISPATCHER);
    }

}
