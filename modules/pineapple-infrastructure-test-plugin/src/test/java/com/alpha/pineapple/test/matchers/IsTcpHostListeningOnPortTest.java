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

import org.easymock.EasyMock;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the {@link IsTcpHostListeningOnPort} class.
 */
public class IsTcpHostListeningOnPortTest {

	/**
	 * Object under test.
	 */
	@SuppressWarnings("unchecked")
	Matcher matcher;

	/**
	 * Mock description.
	 */
	Description description;

	@Before
	public void setUp() throws Exception {

		// create mock description
		description = EasyMock.createMock(Description.class);

	}

	@After
	public void tearDown() throws Exception {
		matcher = null;
		description = null;
	}

	/**
	 * Test that matcher creates a description.
	 */
	@Test
	public void testDescribeTo() {

		String host = "localhost";
		Integer port = new Integer(7);

		// initialize matcher
		matcher = IsTcpHostListeningOnPort.isTcpHostListeningOnPort(host);

		// invoke matcher to store host and port
		matcher.matches(port);

		// complete mock description setup
		EasyMock.expect(description.appendText((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andReturn(description);
		EasyMock.expect(description.appendValue(host));
		EasyMock.expectLastCall().andReturn(description);
		EasyMock.expect(description.appendText((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andReturn(description);
		EasyMock.expect(description.appendValue(port));
		EasyMock.expectLastCall().andReturn(description);
		EasyMock.replay(description);

		// invoke matcher
		matcher.describeTo(description);

		// test
		EasyMock.verify(description);

	}

	/**
	 * Test that matcher creates a mismatch description .
	 */
	@Test
	public void testDescribeMismatch() {

		String host = "localhost";
		Integer port = new Integer(7);

		// initialize matcher
		matcher = IsTcpHostListeningOnPort.isTcpHostListeningOnPort(host);

		// invoke matcher to store host and port
		matcher.matches(port);

		// complete mock description setup
		EasyMock.expect(description.appendText((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andReturn(description);
		EasyMock.expect(description.appendValue(port));
		EasyMock.expectLastCall().andReturn(description);
		EasyMock.expect(description.appendText((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andReturn(description);
		EasyMock.expect(description.appendValue((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andReturn(description);
		EasyMock.replay(description);

		// invoke matcher
		matcher.describeMismatch(port, description);

		// test
		EasyMock.verify(description);
	}

}
