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

import java.util.Arrays;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the class {@link IsMatchingSingleValueInRange}.
 */
public class MatchingSingleValueInRangeTest {

	/**
	 * Object under test.
	 */
	@SuppressWarnings("unchecked")
	Matcher matcher;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		matcher = null;
	}

	/**
	 * Test that matcher succeeds with range zero values and zero values in matched
	 * collection.
	 */
	@Test
	public void testSucceedsWithRangeOfZeroWithZeroMatchedValues() {

		// create legal range
		String[] range = new String[] {};

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] {};

		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher succeeds with range of one value and zero values in matched
	 * collection.
	 */
	@Test
	public void testSucceedsWithRangeOfOneWithZeroMatchedValues() {

		// create legal range
		String[] range = new String[] { "item2" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] {};

		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher succeeds with range of two values and zero values in
	 * matched collection.
	 */
	@Test
	public void testSucceedsWithRangeOfTwoZeroMatchedValues() {

		// create legal range
		String[] range = new String[] { "item1", "item2" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] {};

		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher succeeds with range of one value and one value in matched
	 * collection with is defined in range.
	 */
	@Test
	public void testSucceedsWithRangeOfOneAndOneMatchedValues() {

		// create legal range
		String[] range = new String[] { "item2" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] { "item2" };

		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher succeeds with range of two different values and zero values
	 * in matched collection.
	 */
	@Test
	public void testSucceedsWithRangeOfTwoAndZeroMatchedValues() {

		// create legal range
		String[] range = new String[] { "item1", "item2" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] {};

		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher succeeds with range of two different values and one value
	 * in matched collection with is defined in range.
	 */
	@Test
	public void testSucceedsWithRangeOfTwoAndOneMatchedValues() {

		// create legal range
		String[] range = new String[] { "item1", "item2" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] { "item1" };

		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher succeeds with range of two different values and two
	 * identical values in matched collection with is defined in range.
	 */
	@Test
	public void testSucceedsWithRangeOfTwoAndTwoIdenticalMatchedValues() {

		// create legal range
		String[] range = new String[] { "item1", "item2" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] { "item1", "item1" };

		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher succeeds with range of two different values and two
	 * identical values in matched collection with is defined in range.
	 */
	@Test
	public void testSucceedsWithRangeOfTwoAndTwoIdenticalMatchedValues2() {

		// create legal range
		String[] range = new String[] { "item1", "item2" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] { "item2", "item2" };

		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher fails with range of zero values and two different values in
	 * matched collection with is defined in range.
	 */
	@Test
	public void testFailsWithRangeOfZeroAndTwoDifferentMatchedValues() {

		// create legal range
		String[] range = new String[] {};

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] { "item1", "item2" };

		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher fails with range of one value and one value in matched
	 * collection which isn't defined in range.
	 */
	@Test
	public void testFailsIfMatchedValueIsntInRange() {

		// create legal range
		String[] range = new String[] { "item1" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] { "item2" };

		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher fails with range of one value and two values in matched
	 * collection which isn't defined in range.
	 */
	@Test
	public void testFailsIfMatchedValuesIsntInRange() {

		// create legal range
		String[] range = new String[] { "item1" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] { "item2", "item3" };

		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher fails with range of one value and two different values in
	 * matched collection with is defined in range.
	 */
	@Test
	public void testFailsWithRangeOfOneAndTwoDifferentMatchedValues() {

		// create legal range
		String[] range = new String[] { "item2" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] { "item1", "item2" };

		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher fails with range of one value and two different values in
	 * matched collection with is defined in range.
	 */
	@Test
	public void testFailsWithRangeOfOneAndThreeDifferentMatchedValues() {

		// create legal range
		String[] range = new String[] { "item2" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] { "item1", "item2", "item1" };

		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher fails with range of two different values and two identical
	 * values in matched collection with is defined in range.
	 */
	@Test
	public void testFailsWithRangeOfTwoAndTwoDifferentMatchedValues() {

		// create legal range
		String[] range = new String[] { "item1", "item2" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] { "item1", "item2" };

		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));
	}

	/**
	 * Test that matcher fails with range of two different values and two identical
	 * values in matched collection with is defined in range.
	 */
	@Test
	public void testFailsWithRangeOfTwoAndTwoDifferentMatchedValues2() {

		// create legal range
		String[] range = new String[] { "item1", "item2", "item3" };

		// create matcher with range
		matcher = new IsMatchingSingleValueInRange<String>(Arrays.asList(range));

		// create actual values
		String[] actualValues = new String[] { "item1", "item2" };

		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));
	}

}
