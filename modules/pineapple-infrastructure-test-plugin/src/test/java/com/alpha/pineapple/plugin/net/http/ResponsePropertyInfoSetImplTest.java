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

package com.alpha.pineapple.plugin.net.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the {@link ResponsePropertyInfoSetImpl} class.
 */
public class ResponsePropertyInfoSetImplTest {

	/**
	 * First array index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * Object under test.
	 */
	ResponsePropertyInfoSet set;

	@Before
	public void setUp() throws Exception {
		set = new ResponsePropertyInfoSetImpl();
	}

	@After
	public void tearDown() throws Exception {
		set = null;
	}

	/**
	 * Test that object can be created.
	 */
	@Test
	public void testResponsePropertyInfoSetImpl() {
		assertNotNull(set);
	}

	/**
	 * That a set with no registered properties returns an empty array.
	 */
	@Test
	public void testSetWithNoPropertiesReturnEmptyArray() {
		assertEquals(0, set.getProperties().length);
	}

	/**
	 * That a set with a single registered properties returns an array of size one.
	 */
	@Test
	public void testSetWithOnePropertyReturnArrayOfSizeOne() {

		set.addProperty("name", "xpath", CoreMatchers.anything(), CoreMatchers.anything());

		// test
		assertEquals(1, set.getProperties().length);
	}

	/**
	 * That a set with a two registered properties returns an array of size two.
	 */
	@Test
	public void testSetWithTwoPropertiesReturnArrayOfSizeTwo() {

		set.addProperty("name", "xpath", CoreMatchers.anything(), CoreMatchers.anything());
		set.addProperty("name2", "xpath", CoreMatchers.anything(), CoreMatchers.anything());

		// test
		assertEquals(2, set.getProperties().length);
	}

	/**
	 * That registered properties returns correct attributes.
	 */
	@Test
	public void testPropertyIsRegisteredWithCorrectAttributes() {

		Matcher<Object> intra = CoreMatchers.nullValue();
		Matcher<Object> inter = CoreMatchers.anything();
		String name = "some-name";
		String xpath = "some-xpath";
		set.addProperty(name, xpath, intra, inter);

		ResponsePropertyInfo[] properties = set.getProperties();
		ResponsePropertyInfo property = properties[FIRST_INDEX];

		// test
		assertEquals(inter, property.getInterSequenceMatcher());
		assertEquals(intra, property.getIntraSequenceMatcher());
		assertEquals(name, property.getName());
		assertEquals(xpath, property.getXPath());
	}

	/**
	 * That a set rejects an undefined name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedName() {
		set.addProperty(null, "xpath", CoreMatchers.anything(), CoreMatchers.anything());
	}

	/**
	 * That a set rejects an undefined XPath.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedXPath() {
		set.addProperty("name", null, CoreMatchers.anything(), CoreMatchers.anything());
	}

	/**
	 * That a set accepts an undefined intra matcher.
	 */
	public void testAcceptUndefinedIntraMatcher() {

		Matcher<Object> intra = null;
		Matcher<Object> inter = CoreMatchers.anything();
		set.addProperty("name", "xpath", intra, inter);

		ResponsePropertyInfo[] properties = set.getProperties();
		ResponsePropertyInfo property = properties[FIRST_INDEX];

		// test
		assertEquals(inter, property.getInterSequenceMatcher());
		assertEquals(intra, property.getIntraSequenceMatcher());

	}

	/**
	 * That a set accepts an undefined inter matcher.
	 */
	public void testAcceptsUndefinedInterMatcher() {

		Matcher<Object> intra = CoreMatchers.anything();
		Matcher<Object> inter = null;
		set.addProperty("name", "xpath", intra, inter);

		ResponsePropertyInfo[] properties = set.getProperties();
		ResponsePropertyInfo property = properties[FIRST_INDEX];

		// test
		assertEquals(inter, property.getInterSequenceMatcher());
		assertEquals(intra, property.getIntraSequenceMatcher());
	}

	/**
	 * That a set accepts two properties with identical names.
	 */
	@Test
	public void testAcceptsTwoPropertiesWithIdenticalNames() {

		set.addProperty("name", "xpath", CoreMatchers.anything(), CoreMatchers.anything());
		set.addProperty("name", "xpath", CoreMatchers.anything(), CoreMatchers.anything());

		// test
		assertEquals(2, set.getProperties().length);
	}

}
