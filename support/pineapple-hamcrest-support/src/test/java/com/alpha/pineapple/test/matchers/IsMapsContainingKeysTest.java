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

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the {@link IsMapContainingKeys} class.
 */
public class IsMapsContainingKeysTest {

	/**
	 * A little test class.
	 */
	class Header {
		
		String id;
		String value;

		Header(String id, String value) {
			this.id = id;
			this.value = value;
		}
	}
	
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
		description = EasyMock.createMock( Description.class);
		
	}

	@After
	public void tearDown() throws Exception {		
		matcher = null;
		description = null;
	}

	/**
	 * Test that two empty maps succeeds.
	 */
	@Test
	public void testSucceedsWithEmptyMaps() {

		// initialize cookie maps
		Map<String, Header> expected = new HashMap<String, Header>(); 
		Map<String, Header> actual = new HashMap<String, Header>(); 
		
		// create matcher 
		matcher = new IsMapContainingKeys<String, Header>(expected.keySet());
							
		// test
		assertTrue(matcher.matches( actual ));		
	}

	/**
	 * Test that test succeeds with 
	 * an empty expected map and 
	 * one entry in the actual map.
	 */
	@Test
	public void testSucceedsWithEmptyExpectedMapAndOneActualEntry() {

		// initialize cookie maps
		Map<String, Header> expected = new HashMap<String, Header>(); 
		Map<String, Header> actual = new HashMap<String, Header>();
		actual.put("name", new Header("name", "value"));
		
		// create matcher 
		matcher = new IsMapContainingKeys<String, Header>(expected.keySet());
							
		// test
		assertTrue(matcher.matches( actual ));		
	}

	/**
	 * Test that test succeeds with 
	 * an empty expected map and 
	 * two entries in the actual map.
	 */
	@Test
	public void testSucceedsWithEmptyExpectedMapAndTwoActualEntries() {

		// initialize cookie maps
		Map<String, Header> expected = new HashMap<String, Header>(); 
		Map<String, Header> actual = new HashMap<String, Header>();
		actual.put("name", new Header("name", "value"));
		actual.put("name2", new Header("name2", "value2"));
		
		// create matcher 
		matcher = new IsMapContainingKeys<String, Header>(expected.keySet());
							
		// test
		assertTrue(matcher.matches( actual ));		
	}

	/**
	 * Test that test succeeds with 
	 * an one entry in expected map and 
	 * one entry in the actual map.
	 * Where the entries are identical.
	 */
	@Test
	public void testSucceedsWithOneExpectedValueAndOneActualValue() {

		// initialize cookie maps
		Map<String, Header> expected = new HashMap<String, Header>();
		expected.put("name", new Header("name", "value"));			
		
		Map<String, Header> actual = new HashMap<String, Header>();
		actual.put("name", new Header("name", "value"));
		
		// create matcher 
		matcher = new IsMapContainingKeys<String, Header>(expected.keySet());
							
		// test
		assertTrue(matcher.matches( actual ));		
	}

	/**
	 * Test that test fails with 
	 * an one entry in expected map and 
	 * one entry in the actual map.
	 * Where the entries have different keys. 
	 */
	@Test
	public void testFailsWithOneExpectedValueAndOneActualValue() {

		// initialize cookie maps
		Map<String, Header> expected = new HashMap<String, Header>();
		expected.put("name", new Header("name", "value"));			
		
		Map<String, Header> actual = new HashMap<String, Header>();
		actual.put("another-name", new Header("name", "value"));
		
		// create matcher 
		matcher = new IsMapContainingKeys<String, Header>(expected.keySet());
							
		// test
		assertFalse(matcher.matches( actual ));		
	}

	/**
	 * Test that test fails with 
	 * an one entry in expected map and 
	 * two entries in the actual map.
	 * Where one entry have identical keys. 
	 */
	@Test
	public void testSucceedsWithOneExpectedValueAndTwoActualValues() {

		// initialize cookie maps
		Map<String, Header> expected = new HashMap<String, Header>();
		expected.put("name", new Header("name", "value"));			
		
		Map<String, Header> actual = new HashMap<String, Header>();
		actual.put("name", new Header("name", "value"));			
		actual.put("another-name", new Header("name", "value"));
		
		// create matcher 
		matcher = new IsMapContainingKeys<String, Header>(expected.keySet());
							
		// test
		assertTrue(matcher.matches( actual ));		
	}

	/**
	 * Test that test fails with 
	 * an one entry in expected map and 
	 * three entries in the actual map.
	 * Where one entry have identical keys. 
	 */
	@Test
	public void testSucceedsWithOneExpectedValueAndThreeActualValues() {

		// initialize cookie maps
		Map<String, Header> expected = new HashMap<String, Header>();
		expected.put("name", new Header("name", "value"));			
		
		Map<String, Header> actual = new HashMap<String, Header>();
		actual.put("name", new Header("name", "value"));			
		actual.put("another-name", new Header("name", "value"));
		actual.put("another-name2", new Header("name", "value"));
		
		// create matcher 
		matcher = new IsMapContainingKeys<String, Header>(expected.keySet());
							
		// test
		assertTrue(matcher.matches( actual ));		
	}

	/**
	 * Test that test fails with 
	 * an two entries in expected map and 
	 * two entries in the actual map.
	 * Where two entries have identical keys. 
	 */
	@Test
	public void testSucceedsWithTwoExpectedValueAndTwoActualValues() {

		// initialize cookie maps
		Map<String, Header> expected = new HashMap<String, Header>();
		expected.put("name", new Header("name", "value"));			
		expected.put("name2", new Header("name", "value"));		
		
		Map<String, Header> actual = new HashMap<String, Header>();
		actual.put("name", new Header("name", "value"));			
		actual.put("name2", new Header("name", "value"));
		
		// create matcher 
		matcher = new IsMapContainingKeys<String, Header>(expected.keySet());
							
		// test
		assertTrue(matcher.matches( actual ));		
	}

	/**
	 * Test that test fails with 
	 * an two entries in expected map and 
	 * three entries in the actual map.
	 * Where two entries have identical keys. 
	 */
	@Test
	public void testSucceedsWithTwoExpectedValueAndThreeActualValues() {

		// initialize cookie maps
		Map<String, Header> expected = new HashMap<String, Header>();
		expected.put("name", new Header("name", "value"));			
		expected.put("name2", new Header("name", "value"));		
		
		Map<String, Header> actual = new HashMap<String, Header>();
		actual.put("name", new Header("name", "value"));			
		actual.put("name2", new Header("name", "value"));
		actual.put("name3", new Header("name", "value"));		
		
		// create matcher 
		matcher = new IsMapContainingKeys<String, Header>(expected.keySet());
							
		// test
		assertTrue(matcher.matches( actual ));		
	}
	
}
