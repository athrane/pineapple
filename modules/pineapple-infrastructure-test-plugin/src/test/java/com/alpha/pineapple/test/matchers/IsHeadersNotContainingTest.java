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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.httpclient.Header;
import org.easymock.EasyMock;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the {@link IsHeadersNotContaining} class.
 */
public class IsHeadersNotContainingTest {

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
	 * Matcher succeeds if expected headers is empty and actual headers is empty.
	 */
	@Test
	public void testSucceedsWithEmptyExpectedHeadersAndNoActualsHeaders() {

		// create headers
		String[] expectedHeaders = new String[0];
		Header[] actualheaders = new Header[0];

		// create matcher
		matcher = new IsHeadersNotContaining(expectedHeaders);

		// test
		assertTrue(matcher.matches(actualheaders));
	}

	/**
	 * Matcher succeeds if expected headers is empty and actual headers contains one
	 * entry.
	 */
	@Test
	public void testSucceedsWithEmptyExpectedHeadersAndOneActualsHeader() {

		// create headers
		String[] expectedHeaders = new String[0];
		Header[] actualheaders = new Header[1];
		actualheaders[0] = new Header("name", "value");

		// create matcher
		matcher = new IsHeadersNotContaining(expectedHeaders);

		// test
		assertTrue(matcher.matches(actualheaders));
	}

	/**
	 * Matcher succeeds if expected headers is empty and actual headers contains two
	 * entries.
	 */
	@Test
	public void testSucceedsWithEmptyExpectedHeadersAndTwoActualsHeaders() {

		// create headers
		String[] expectedHeaders = new String[0];
		Header[] actualheaders = new Header[2];
		actualheaders[0] = new Header("name", "value");
		actualheaders[1] = new Header("name2", "value2");

		// create matcher
		matcher = new IsHeadersNotContaining(expectedHeaders);

		// test
		assertTrue(matcher.matches(actualheaders));
	}

	/**
	 * Matcher succeeds if expected headers contains one entry and actual headers
	 * contains zero entries.
	 */
	@Test
	public void testSucceedsWithOneExpectedHeadersAndZeroActualsHeaders() {

		// create headers
		String[] expectedHeaders = new String[1];
		expectedHeaders[0] = "name";
		Header[] actualheaders = new Header[0];

		// create matcher
		matcher = new IsHeadersNotContaining(expectedHeaders);

		// test
		assertTrue(matcher.matches(actualheaders));
	}

	/**
	 * Matcher succeeds if expected headers contains two entries and actual headers
	 * contains zero entries.
	 */
	@Test
	public void testSucceedsWithTwoExpectedHeadersAndZeroActualsHeaders() {

		// create headers
		String[] expectedHeaders = new String[2];
		expectedHeaders[0] = "name";
		expectedHeaders[1] = "name2";
		Header[] actualheaders = new Header[0];

		// create matcher
		matcher = new IsHeadersNotContaining(expectedHeaders);

		// test
		assertTrue(matcher.matches(actualheaders));
	}

	/**
	 * Matcher fails if expected headers contains one entry and actual headers
	 * contains one entry where one has expected name and value.
	 */
	@Test
	public void testFailsWithOneExpectedHeadersAndOneActualsHeaders() {

		// create headers
		String[] expectedHeaders = new String[1];
		expectedHeaders[0] = "name";
		Header[] actualheaders = new Header[1];
		actualheaders[0] = new Header("name", "value");

		// create matcher
		matcher = new IsHeadersNotContaining(expectedHeaders);

		// test
		assertFalse(matcher.matches(actualheaders));
	}

	/**
	 * Matcher fails if expected headers contains one entry and actual headers
	 * contains two entries where one has expected name and value.
	 */
	@Test
	public void testFailsWithOneExpectedHeadersAndTwoActualsHeaders() {

		// create headers
		String[] expectedHeaders = new String[1];
		expectedHeaders[0] = "name";
		Header[] actualheaders = new Header[2];
		actualheaders[0] = new Header("name", "value");
		actualheaders[1] = new Header("name2", "value2");

		// create matcher
		matcher = new IsHeadersNotContaining(expectedHeaders);

		// test
		assertFalse(matcher.matches(actualheaders));
	}

	/**
	 * Matcher fails if expected headers contains two entries and actual headers
	 * contains two entries where two has expected name and value.
	 */
	@Test
	public void testFailsWithTwoExpectedHeadersAndTwoActualsHeaders() {

		// create headers
		String[] expectedHeaders = new String[2];
		expectedHeaders[0] = "name";
		expectedHeaders[1] = "name2";
		Header[] actualheaders = new Header[2];
		actualheaders[0] = new Header("name", "value");
		actualheaders[1] = new Header("name2", "value2");

		// create matcher
		matcher = new IsHeadersNotContaining(expectedHeaders);

		// test
		assertFalse(matcher.matches(actualheaders));
	}

	/**
	 * Matcher fails if expected headers contains two entries and actual headers
	 * contains three entries where two has expected name and value.
	 */
	@Test
	public void testFailsWithTwoExpectedHeadersAndThreeActualsHeaders() {

		// create headers
		String[] expectedHeaders = new String[2];
		expectedHeaders[0] = "name";
		expectedHeaders[1] = "name2";
		Header[] actualheaders = new Header[3];
		actualheaders[0] = new Header("name", "value");
		actualheaders[1] = new Header("name1", "value1");
		actualheaders[2] = new Header("name2", "value2");

		// create matcher
		matcher = new IsHeadersNotContaining(expectedHeaders);

		// test
		assertFalse(matcher.matches(actualheaders));
	}

}
