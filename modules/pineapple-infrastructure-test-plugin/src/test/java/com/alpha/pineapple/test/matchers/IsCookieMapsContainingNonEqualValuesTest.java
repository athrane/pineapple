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

import java.util.ArrayList;

import org.apache.commons.httpclient.Cookie;
import org.easymock.EasyMock;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the {@link IsCookieMapsContainingEqualValues} class.
 */
public class IsCookieMapsContainingNonEqualValuesTest {

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

		matcher = IsCookieMapsContainingNonEqualValues.containsNonEqualValues();

		// create mock description
		description = EasyMock.createMock(Description.class);
	}

	@After
	public void tearDown() throws Exception {
		matcher = null;
		description = null;
	}

	/**
	 * create empty cookie array of size zero.
	 * 
	 * @return empty cookie array of size zero.
	 */
	Cookie[] createEmptyCookieArray() {
		return new Cookie[0];
	}

	/**
	 * create uninitialized cookie array of some size.
	 *
	 * @param size
	 *            array size.
	 * 
	 * @return cookie array of some size.
	 */
	Cookie[] createCookieArray(int size) {
		return new Cookie[size];
	}

	/**
	 * Test that empty collection succeeds.
	 */
	@Test
	public void testSucceedsWithEmptyCollection() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// test
		assertTrue(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with one empty map succeeds.
	 */
	@Test
	public void testSucceedsWithOneEmptyMap() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie map
		cookieCollection.add(createEmptyCookieArray());

		// test
		assertTrue(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with two empty maps succeeds.
	 */
	@Test
	public void testSucceedsWithTwoEmptyMaps() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie map
		cookieCollection.add(createEmptyCookieArray());
		cookieCollection.add(createEmptyCookieArray());

		// test
		assertTrue(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with two maps, containing one identical cookie, fails.
	 */
	@Test
	public void testFailsWithTwoMapsWithOneIdenticalCookie() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		Cookie[] cookies1 = createCookieArray(1);
		cookies1[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		Cookie[] cookies2 = createCookieArray(1);
		cookies2[0] = new Cookie("some-domain", "cookie-name", "cookie-value");

		cookieCollection.add(cookies1);
		cookieCollection.add(cookies2);

		// test
		assertFalse(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with two maps, containing two identical cookies, fails.
	 */
	@Test
	public void testFailsWithTwoMapsWithTwoIdenticalCookies() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		Cookie[] cookies1 = createCookieArray(2);
		cookies1[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		cookies1[1] = new Cookie("some-domain", "cookie-name2", "cookie-value2");
		Cookie[] cookies2 = createCookieArray(2);
		cookies2[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		cookies2[1] = new Cookie("some-domain", "cookie-name2", "cookie-value2");

		cookieCollection.add(cookies1);
		cookieCollection.add(cookies2);

		// test
		assertFalse(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with three maps, containing two identical cookies,
	 * fails.
	 */
	@Test
	public void testFailsWithThreeMapsWithTwoIdenticalCookies() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		Cookie[] cookies1 = createCookieArray(2);
		cookies1[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		cookies1[1] = new Cookie("some-domain", "cookie-name2", "cookie-value2");
		Cookie[] cookies2 = createCookieArray(2);
		cookies2[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		cookies2[1] = new Cookie("some-domain", "cookie-name2", "cookie-value2");
		Cookie[] cookies3 = createCookieArray(2);
		cookies3[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		cookies3[1] = new Cookie("some-domain", "cookie-name2", "cookie-value2");

		cookieCollection.add(cookies1);
		cookieCollection.add(cookies2);
		cookieCollection.add(cookies3);

		// test
		assertFalse(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with two maps, containing one cookie, with different
	 * values succeeds.
	 */
	@Test
	public void testSucceedsWithTwoMapsWithOneCookieWithDifferentValues() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		Cookie[] cookies1 = createCookieArray(1);
		cookies1[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		Cookie[] cookies2 = createCookieArray(1);
		cookies2[0] = new Cookie("some-domain", "cookie-name", "another-cookie-value");

		cookieCollection.add(cookies1);
		cookieCollection.add(cookies2);

		// test
		assertTrue(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with two maps, containing two cookies, with different
	 * values succeeds.
	 */
	@Test
	public void testSucceedsWithTwoMapsWithTwoCookiesWithDifferentValues() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		Cookie[] cookies1 = createCookieArray(2);
		cookies1[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		cookies1[1] = new Cookie("some-domain", "cookie-name2", "cookie-value2");

		Cookie[] cookies2 = createCookieArray(2);
		cookies2[0] = new Cookie("some-domain", "cookie-name", "another-cookie-value");
		cookies2[1] = new Cookie("some-domain", "cookie-name2", "another-cookie-value2");

		cookieCollection.add(cookies1);
		cookieCollection.add(cookies2);

		// test
		assertTrue(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with two maps, containing two cookies, with one which
	 * have different value succeeds.
	 */
	@Test
	public void testSucceedsWithTwoMapsWithTwoCookiesWithDifferentValues2() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		Cookie[] cookies1 = createCookieArray(2);
		cookies1[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		cookies1[1] = new Cookie("some-domain", "cookie-name2", "cookie-value2");

		Cookie[] cookies2 = createCookieArray(2);
		cookies2[0] = new Cookie("some-domain", "cookie-name", "another-cookie-value");
		cookies2[1] = new Cookie("some-domain", "cookie-name2", "cookie-value2");

		cookieCollection.add(cookies1);
		cookieCollection.add(cookies2);

		// test
		assertTrue(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with two maps, containing one cookie, with empty ("")
	 * values fails.
	 */
	@Test
	public void testFailsWithTwoMapsWithOneCookieWithEmptyValues() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		Cookie[] cookies1 = createCookieArray(1);
		cookies1[0] = new Cookie("some-domain", "cookie-name", "");
		Cookie[] cookies2 = createCookieArray(1);
		cookies2[0] = new Cookie("some-domain", "cookie-name", "");

		cookieCollection.add(cookies1);
		cookieCollection.add(cookies2);

		// test
		assertFalse(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with two maps, each containing one different cookie,
	 * fails.
	 */
	@Test
	public void testFailsWithTwoMapsWithOneDifferentCookie() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		Cookie[] cookies1 = createCookieArray(1);
		cookies1[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		Cookie[] cookies2 = createCookieArray(1);
		cookies2[0] = new Cookie("some-domain", "another-cookie-name", "cookie-value");

		cookieCollection.add(cookies1);
		cookieCollection.add(cookies2);

		// test
		assertFalse(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with two maps, each containing one different cookie,
	 * fails.
	 */
	@Test
	public void testFailsWithTwoMapsWithOneDifferentCookie2() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		Cookie[] cookies1 = createCookieArray(2);
		cookies1[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		cookies1[1] = new Cookie("some-domain", "cookie-name2", "cookie-value2");
		Cookie[] cookies2 = createCookieArray(2);
		cookies2[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		cookies2[1] = new Cookie("some-domain", "another-cookie-name", "cookie-value2");

		cookieCollection.add(cookies1);
		cookieCollection.add(cookies2);

		// test
		assertFalse(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with two maps, of different size, fails.
	 */
	@Test
	public void testFailsWithTwoMapsOfdifferentSize() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		Cookie[] cookies1 = createCookieArray(1);
		cookies1[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		Cookie[] cookies2 = createCookieArray(2);
		cookies2[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		cookies2[1] = new Cookie("some-domain", "cookie-name2", "cookie-value2");

		cookieCollection.add(cookies1);
		cookieCollection.add(cookies2);

		// test
		assertFalse(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with two maps, of different size, fails.
	 */
	@Test
	public void testFailsWithTwoMapsOfdifferentSize2() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		Cookie[] cookies1 = createCookieArray(1);
		cookies1[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		Cookie[] cookies2 = createCookieArray(2);
		cookies2[0] = new Cookie("some-domain", "cookie-name", "cookie-value");
		cookies2[1] = new Cookie("some-domain", "cookie-name2", "cookie-value2");

		cookieCollection.add(cookies2);
		cookieCollection.add(cookies1);

		// test
		assertFalse(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with one map, which is null, fails.
	 */
	@Test
	public void testFailsWithOneMapWhichIsNull() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		cookieCollection.add(null);

		// test
		assertFalse(matcher.matches(cookieCollection));
	}

	/**
	 * Test that collection with two maps, which is null, fails.
	 */
	@Test
	public void testFailsWithTwoMapsWhichIsNull() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		cookieCollection.add(null);
		cookieCollection.add(null);

		// test
		assertFalse(matcher.matches(cookieCollection));
	}

	/**
	 * Test that mismatch description can be created for one map which is null.
	 */
	@Test
	public void testCanDescribeMismatchForOneMapWhichIsNull() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		cookieCollection.add(null);

		// describe mismatch
		matcher.describeMismatch(cookieCollection, this.description);
	}

	/**
	 * Test that mismatch description can be created for two maps which is null.
	 */
	@Test
	public void testCanDescribeMismatchForTwoMapsWhichIsNull() {

		// initialize cookie collection
		ArrayList<Cookie[]> cookieCollection = new ArrayList<Cookie[]>();

		// add cookie maps
		cookieCollection.add(null);
		cookieCollection.add(null);

		// describe mismatch
		matcher.describeMismatch(cookieCollection, this.description);
	}

	/**
	 * Test that matcher creates a description.
	 */
	@Test
	public void testDescribeTo() {

		// complete mock description setup
		EasyMock.expect(description.appendText((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andReturn(description);
		EasyMock.replay(description);

		// invoke matcher
		matcher.describeTo(description);

		// test
		EasyMock.verify(description);

	}

}
