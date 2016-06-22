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
 * Unit test of the class {@link IsDistributedAcrossRange}.
 */
public class DistributedAcrossRangeTest {
	
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
	 * Test that matcher succeeds
	 * with range zero values 
	 * and zero values in matched collection. 
	 */		
	@Test
	public void testSucceedsWithRangeOfZeroWithZeroMatchedValues() {

		// create legal range
		String[] range = new String[] {};

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] { };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}		
	
	/**
	 * Test that matcher succeeds
	 * with range of one value 
	 * and zero values in matched collection. 
	 */		
	@Test
	public void testSucceedsWithRangeOfOneWithZeroMatchedValues() {

		// create legal range
		String[] range = new String[] { "item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] { };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}	

	/**
	 * Test that matcher succeeds
	 * with range of two values 
	 * and zero values in matched collection. 
	 */		
	@Test
	public void testSucceedsWithRangeOfTwoWithZeroMatchedValues() {

		// create legal range
		String[] range = new String[] { "item1","item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] { };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}	
	
	/**
	 * Test that matcher succeeds
	 * with range of one value 
	 * and one value in matched collection with is defined in range. 
	 */		
	@Test
	public void testSucceedsWithRangeOfOneAndOneMatchedValues() {

		// create legal range
		String[] range = new String[] { "item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] {"item2" };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}
	
	/**
	 * Test that matcher succeeds
	 * with range of one value 
	 * and two values in matched collection with is defined in range. 
	 */		
	@Test
	public void testSucceedsWithRangeOfOneAndTwoMatchedValues() {

		// create legal range
		String[] range = new String[] { "item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] {"item2", "item2" };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}		
	
	/**
	 * Test that matcher succeeds
	 * with range of one value 
	 * and five values in matched collection with is defined in range. 
	 */		
	@Test
	public void testSucceedsWithRangeOfOneAndFiveMatchedValues() {

		// create legal range
		String[] range = new String[] { "item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] {"item2", "item2", "item2", "item2", "item2" };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}	

	
	/**
	 * Test that matcher succeeds
	 * with range of two value 
	 * and one value in matched collection with is defined in range. 
	 */		
	@Test
	public void testSucceedsWithRangeOfTwoAndOneValue() {

		// create legal range
		String[] range = new String[] { "item1", "item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] {"item1",  };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}	
	
	/**
	 * Test that matcher succeeds
	 * with range of one value 
	 * and two values in matched collection with is defined in range. 
	 */		
	@Test
	public void testSucceedsWithRangeOfTwoAndTwoMatchedValues() {

		// create legal range
		String[] range = new String[] { "item1","item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] {"item2", "item1" };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}	

	/**
	 * Test that matcher succeeds
	 * with range of two values 
	 * and one value in matched collection with is defined in range
	 * and distributed evenly. 
	 */		
	@Test
	public void testSucceedsWithRangeOfTwoAndOneValuesWhichIsDistributedEvenly() {

		// create legal range
		String[] range = new String[] { "item1","item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] { "item1"  };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}			

	/**
	 * Test that matcher succeeds
	 * with range of two values 
	 * and three value in matched collection with is defined in range
	 * and distributed evenly. 
	 */		
	@Test
	public void testSucceedsWithRangeOfTwoAndThreeValuesWhichIsDistributedEvenly() {

		// create legal range
		String[] range = new String[] { "item1","item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] { "item1", "item2", "item1" };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}			
	
	/**
	 * Test that matcher succeeds
	 * with range of two values 
	 * and four values in matched collection with is defined in range
	 * and distributed evenly. 
	 */		
	@Test
	public void testSucceedsWithRangeOfTwoAndFourValuesWhichIsDistributedEvenly() {

		// create legal range
		String[] range = new String[] { "item1","item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] {"item2", "item1", "item2", "item1" };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}		
	
	/**
	 * Test that matcher succeeds
	 * with range of two values 
	 * and five values in matched collection with is defined in range
	 * and distributed evenly. 
	 */		
	@Test
	public void testSucceedsWithRangeOfTwoAndFiveValuesWhichIsDistributedEvenly() {

		// create legal range
		String[] range = new String[] { "item1","item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] {"item2", "item1", "item2", "item1" };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}			
	
	/**
	 * Test that matcher succeeds
	 * with range of three values 
	 * and 12 values in matched collection with is defined in range
	 * and distributed evenly. 
	 */		
	@Test
	public void testSucceedsWithRangeOfThreeAndTwelveValueswhichIsDistributedEvenly() {

		// create legal range
		String[] range = new String[] { "item1","item2", "item3"  };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] {"item2", "item1", "item2", "item1",
				"item2", "item1", "item2", "item3",
				"item3", "item3", "item3", "item1" };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}		
	
	/**
	 * Test that matcher succeeds
	 * with range of three values 
	 * and 15 values in matched collection with is defined in range
	 * and distributed evenly. 
	 */		
	@Test
	public void testSucceedsWithRangeOfThreeAndFourteenValueswhichIsDistributedEvenly() {

		// create legal range
		String[] range = new String[] { "item1","item2", "item3"  };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] {"item2", "item1", "item3",  
				"item2", "item1", "item3",
				"item2", "item1", "item3",
				"item2", "item1", "item3",
				"item1", "item2" };		
		
		// test
		assertTrue(matcher.matches(Arrays.asList(actualValues)));		
	}			
	
	/**
	 * Test that matcher fails
	 * with range of one value 
	 * and one value in matched collection which isn't defined in range. 
	 */		
	@Test
	public void testFailsIfMatchedValueIsntInRange() {

		// create legal range
		String[] range = new String[] { "item1" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] {"item2" };		
		
		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));		
	}
	
	/**
	 * Test that matcher fails
	 * with range of one value 
	 * and two values in matched collection which isn't defined in range. 
	 */		
	@Test
	public void testFailsIfMatchedValuesIsntInRange() {

		// create legal range
		String[] range = new String[] { "item1" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] {"item2", "item3" };		
		
		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));		
	}		
	

	/**
	 * Test that matcher fails
	 * with range of two value 
	 * and three identical values in matched collection with is defined in range. 
	 */		
	@Test
	public void testFailsWithRangeOfTwoAndThreeIdenticalValues() {

		// create legal range
		String[] range = new String[] { "item1", "item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] {"item1", "item1", "item1"  };		
		
		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));		
	}	
	

	/**
	 * Test that matcher fails
	 * with range of one value 
	 * and one empty ("") value in matched collection. 
	 */		
	@Test
	public void testFailsWithRangeOfOneAndOneEmptyValue() {

		// create legal range
		String[] range = new String[] { "item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] { "" };		
		
		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));		
	}
	
	/**
	 * Test that matcher fails
	 * with range of one value 
	 * and two empty ("") values in matched collection. 
	 */		
	@Test
	public void testFailsWithRangeOfOneAndTwoEmptyValues() {

		// create legal range
		String[] range = new String[] { "item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] { "", "" };		
		
		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));		
	}	
	
	/**
	 * Test that matcher fails
	 * with range of one value 
	 * and one null value in matched collection. 
	 */		
	@Test
	public void testFailsWithRangeOfOneAndOneNullValue() {

		// create legal range
		String[] range = new String[] { "item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] { null };		
		
		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));		
	}
	
	/**
	 * Test that matcher fails
	 * with range of one value 
	 * and two null values in matched collection. 
	 */		
	@Test
	public void testFailsWithRangeOfOneAndTwoNullValues() {

		// create legal range
		String[] range = new String[] { "item2" };

		// create matcher with range		
		matcher = new IsDistributedAcrossRange<String>(Arrays.asList(range));		
		
		// create actual values
		String[] actualValues = new String[] { null, null };		
		
		// test
		assertFalse(matcher.matches(Arrays.asList(actualValues)));		
	}
	
}
