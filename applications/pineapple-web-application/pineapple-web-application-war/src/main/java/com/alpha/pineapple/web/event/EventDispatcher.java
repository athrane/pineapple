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

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import com.alpha.pineapple.web.zk.asynctask.event.GenericEvent;

/**
 * ZK event dispatcher.
 * 
 * Dispatches ZK events to registered ZK desktop's.
 */
public interface EventDispatcher {

    /**
     * Register ZK {@linkplain Desktop} with event dispatcher.
     * 
     * Registration of the ZK desktop allows the dispatcher to forward ZK events
     * to desktop.
     * 
     * @param desktop
     *            ZK desktop which is registered.
     */
    void register(Desktop desktop);

    /**
     * Unregister ZK {@linkplain Desktop} from event dispatcher.
     * 
     * Will clear the connection to the used ZK {@linkplain Desktop}. After a
     * reset, the dispatch will NOT dispatch ZK events since there is no target
     * desktop.
     * 
     * @param desktop
     *            ZK desktop.
     */
    void unregister(Desktop desktop);

    /**
     * Dispatch ZK event.
     * 
     * @param zkEvent
     *            which is dispatched.
     * @param eventListener
     *            target event listener to whom the event is dispatched.
     */
    void dispatchZkEvent(Event zkEvent, EventListener<Event> eventListener);

    /**
     * Dispatch ZK event.
     * 
     * A {@linkplain GenericEvent} is dispatched. The generic event carries no
     * arguments.
     * 
     * @param eventListener
     *            target event listener to whom the event is dispatched.
     */
    void dispatchZkEvent(EventListener<Event> eventListener);

}
