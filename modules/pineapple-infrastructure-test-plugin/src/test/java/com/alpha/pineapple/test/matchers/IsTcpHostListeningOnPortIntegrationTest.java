/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
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

package com.alpha.pineapple.test.matchers;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.eclipse.jetty.server.Server;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.testutils.ObjectMotherHttpServer;

/**
 * Integration test of the {@link IsTcpHostListeningOnPort} class.
 */
public class IsTcpHostListeningOnPortIntegrationTest {

	/**
	 * TCP port for HTTP server.
	 */
	static final int HTTP_PORT = 3000;

	/**
	 * Object under test.
	 */
	@SuppressWarnings("unchecked")
	Matcher matcher;

	/**
	 * HTTP server object mother
	 */
	ObjectMotherHttpServer httpServerMother;

	/**
	 * HTTP server.
	 */
	Server httpServer;

	@Before
	public void setUp() throws Exception {

		// create HTTP server object mother
		httpServerMother = new ObjectMotherHttpServer();

		// create HTTP server
		httpServer = httpServerMother.createHttpServer(httpServerMother.getHostIPAddress(true), HTTP_PORT);
	}

	@After
	public void tearDown() throws Exception {
		httpServer.stop();
		httpServer = null;
		httpServerMother = null;
		matcher = null;
	}

	/**
	 * Test that matcher succeeds.
	 */
	@Test
	public void testSucceedsListeningToLocalHostOn3000() {

		// get host IP address
		String hostIpAddress = httpServerMother.getHostIPAddress(true);

		// initialize matcher
		matcher = IsTcpHostListeningOnPort.isTcpHostListeningOnPort(hostIpAddress);

		// invoke matcher to store host and port
		assertTrue(matcher.matches(new Integer(HTTP_PORT)));
	}

}
