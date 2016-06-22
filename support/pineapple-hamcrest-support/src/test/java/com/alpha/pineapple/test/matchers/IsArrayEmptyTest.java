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

import org.easymock.EasyMock;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the {@link IsArrayEmpty} class.
 */
public class IsArrayEmptyTest {

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
				
		matcher = IsArrayEmpty.isArrayEmpty();
		
		// create mock description
		description = EasyMock.createMock( Description.class);
		
	}

	@After
	public void tearDown() throws Exception {		
		matcher = null;
		description = null;
	}

	/**
	 * Test that empty array is positive match.
	 */
	@Test
	public void testEmptyArrayIsPositiveMatch() {

		// initialize array
		Object[] objectArray = {};
				
		// test
		assertTrue(matcher.matches( objectArray ));		
	}

	/**
	 * Test that array with one entry is negative match.
	 */
	@Test
	public void testArrayWithOneEntryIsNegativeMatch() {

		// initialize array
		Object[] objectArray = { new Integer(10) };		
				
		// test
		assertFalse(matcher.matches( objectArray ));		
	}
	
	/**
	 * Test that array with two entries is negative match.
	 */
	@Test
	public void testArrayWithTwoEntriesIsNegativeMatch() {

		// initialize array
		Object[] objectArray = { new Integer(10), new Integer(20)  };		
				
		// test
		assertFalse(matcher.matches( objectArray ));		
	}
		
	/**
	 * Test that matcher creates a description.
	 */
	@Test
	public void testDescribeTo() {
	
		// complete mock description setup
		EasyMock.expect( description.appendText( (String) EasyMock.isA( String.class) ) );
		EasyMock.expectLastCall().andReturn( description );
		EasyMock.replay( description );		
		
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

		// initialize array
		Object[] objectArray = { new Integer(10), new Integer(20)  };		
					
		// complete mock description setup
		EasyMock.expect( description.appendText( (String) EasyMock.isA( String.class) ) );
		EasyMock.expectLastCall().andReturn( description );
		EasyMock.expect( description.appendValue( 2 ));		
		EasyMock.expectLastCall().andReturn( description );
		EasyMock.expect( description.appendText( (String) EasyMock.isA( String.class) ) );
		EasyMock.expectLastCall().andReturn( description );		
		EasyMock.replay( description );		
		
		// invoke matcher
		matcher.describeMismatch( objectArray, description);
		
		// test
		EasyMock.verify(description);
	}

}
