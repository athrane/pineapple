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

import static reactor.event.selector.Selectors.$;
import static reactor.event.selector.Selectors.T;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.event.Event;
import reactor.function.Consumer;
import reactor.function.support.Boundary;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ExecutionResultNotificationImpl;

public class ReactorTest2 {

    class NotificationConsumer implements Consumer<Event<ExecutionResultNotification>> {

	@Override
	public void accept(Event<ExecutionResultNotification> t) {
	    logger.debug("DEBUG: accept: " + t);
	}

    }

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Object under test.
     */
    ExecutionResultNotification notification;

    /**
     * Mock execution result.
     */
    ExecutionResult result;

    /**
     * Reactor.
     */
    Reactor webAppReactor;

    /**
     * Reactor environment.
     */
    Environment environment;

    /**
     * Reactor boundary.
     */
    Boundary boundary;

    @Before
    public void setUp() throws Exception {

	// create mock result
	result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.replay(result);
	notification = ExecutionResultNotificationImpl.getInstance(result, ExecutionState.EXECUTING);

	environment = new Environment();
	webAppReactor = Reactors.reactor().env(environment).dispatcher(Environment.RING_BUFFER).get();

	boundary = new Boundary();
    }

    @After
    public void tearDown() {
	environment.shutdown();
    }

    @Test
    public void testSelectionByClass() {
	webAppReactor.on(T(ExecutionResultNotification.class), new NotificationConsumer());
	webAppReactor.notify(notification.getClass(), Event.wrap(notification));

	// Wait for all Consumers to have been called
	boundary.await();
    }

    @Test
    public void testSelectionByString() {
	String key = "ExecutionResultNotification";
	webAppReactor.on($(key), new NotificationConsumer());
	webAppReactor.notify(key, Event.wrap(notification));

	// Wait for all Consumers to have been called
	boundary.await();
    }

    @Test
    public void testSelectionByString2() {

	String uri = "/webapp/service/notification";
	webAppReactor.on($(uri), new NotificationConsumer());
	webAppReactor.notify(uri, Event.wrap(notification));

	// Wait for all Consumers to have been called
	boundary.await();
    }

}
