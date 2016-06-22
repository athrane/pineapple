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

import org.apache.commons.httpclient.Cookie;
import org.easymock.EasyMock;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.plugin.net.http.CookieMap;
import com.alpha.pineapple.plugin.net.http.CookieMapPair;

/**
 * Unit test of the {@link IsCookieMapsPairContainingEqualValues} class.
 */
public class IsCookieMapsPairContainingEqualValuesTest {

	/**
	 * Cookie domain.
	 */
	final static String DOMAIN = "domain";
	
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
				
		matcher = IsCookieMapsPairContainingEqualValues.containsEqualValues();
		
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
	public void testSucceedsWithTwoEmptyMaps() {

		// initialize cookie maps
		CookieMap map1 = new CookieMap();
		CookieMap map2 = new CookieMap();
				
		// initialize cookie maps pair  
		CookieMapPair pair = new CookieMapPair(map1, map2);		
				
		// test
		assertTrue(matcher.matches( pair ));		
	}

	/**
	 * Test that two maps with one entry identical succeeds.
	 */
	@Test
	public void testSucceedsWithTwoMapsContainingOneIdenticalEntry() {

		// initialize cookie maps
		CookieMap map1 = new CookieMap();
		map1.put("key1", new Cookie(DOMAIN,"key1","value1" ));
		CookieMap map2 = new CookieMap();
		map2.put("key1", new Cookie(DOMAIN,"key1","value1" ));
		
		// initialize cookie maps pair  
		CookieMapPair pair = new CookieMapPair(map1, map2);		
				
		// test
		assertTrue(matcher.matches( pair ));		
	}

	/**
	 * Test that two maps with three identical entries succeeds.
	 */
	@Test
	public void testSucceedsWithTwoMapsContainingThreeIdentialEntries() {

		// initialize cookie maps
		CookieMap map1 = new CookieMap();
		map1.put("key1", new Cookie(DOMAIN,"key1","value1" ));
		map1.put("key2", new Cookie(DOMAIN,"key2","value2" ));
		map1.put("key3", new Cookie(DOMAIN,"key3","value3" ));				
		CookieMap map2 = new CookieMap();
		map2.put("key1", new Cookie(DOMAIN,"key1","value1" ));
		map2.put("key2", new Cookie(DOMAIN,"key2","value2" ));
		map2.put("key3", new Cookie(DOMAIN,"key3","value3" ));				
		
		// initialize cookie maps pair  
		CookieMapPair pair = new CookieMapPair(map1, map2);		
				
		// test
		assertTrue(matcher.matches( pair ));        
	}

	/**
	 * Test that two maps with zero and one entries fails.
	 */
	@Test
	public void testFailsWithTwoMapsContainingZeroAndOneEntries() {

		// initialize cookie maps
		CookieMap map1 = new CookieMap();
		CookieMap map2 = new CookieMap();
		map2.put("key1", new Cookie(DOMAIN,"key1","value1" ));
		
		// initialize cookie maps pair  
		CookieMapPair pair = new CookieMapPair(map1, map2);		
				
		// test
		assertFalse(matcher.matches( pair ));        
	}	

	/**
	 * Test that two maps with one and two entries fails.
	 */
	@Test
	public void testFailsWithTwoMapsContainingOneAndTwoEntries() {

		// initialize cookie maps
		CookieMap map1 = new CookieMap();
		map1.put("key1", new Cookie(DOMAIN,"key1","value1" ));
		CookieMap map2 = new CookieMap();
		map2.put("key1", new Cookie(DOMAIN,"key1","value1" ));
		map2.put("key2", new Cookie(DOMAIN,"key2","value2" ));
		
		// initialize cookie maps pair  
		CookieMapPair pair = new CookieMapPair(map1, map2);		
				
		// test
		assertFalse(matcher.matches( pair ));        
	}	
	
	
	/**
	 * Test that two maps with one different entry values fails.
	 */
	@Test
	public void testFailsWithTwoMapsContainingOneDifferentEntry() {

		// initialize cookie maps
		CookieMap map1 = new CookieMap();
		map1.put("key1", new Cookie(DOMAIN,"key1","some-value" ));
		CookieMap map2 = new CookieMap();
		map2.put("key1", new Cookie(DOMAIN,"key1","another-value" ));
		
		// initialize cookie maps pair  
		CookieMapPair pair = new CookieMapPair(map1, map2);		
				
		// test
		assertFalse(matcher.matches( pair ));		
	}
	
	/**
	 * Test that two maps with three different entry values fails.
	 */
	@Test
	public void testFailsWithTwoMapsContainingThreeDifferentEntries() {

		// initialize cookie maps
		CookieMap map1 = new CookieMap();
		map1.put("key1", new Cookie(DOMAIN,"key1","some-value" ));
		map1.put("key2", new Cookie(DOMAIN,"key2","some-value" ));
		map1.put("key3", new Cookie(DOMAIN,"key3","some-value" ));				
		CookieMap map2 = new CookieMap();
		map2.put("key1", new Cookie(DOMAIN,"key1","another-value" ));
		map2.put("key2", new Cookie(DOMAIN,"key2","another-value" ));
		map2.put("key3", new Cookie(DOMAIN,"key3","another-value" ));				
		
		// initialize cookie maps pair  
		CookieMapPair pair = new CookieMapPair(map1, map2);		
				
		// test
		assertFalse(matcher.matches( pair ));		
	}

	/**
	 * Test that two maps with three different entry values fails.
	 */
	@Test
	public void testFailsWithTwoMapsContainingThreeDifferentEntries2() {

		// initialize cookie maps
		CookieMap map1 = new CookieMap();
		map1.put("key1", new Cookie(DOMAIN,"key1","value1" ));
		map1.put("key2", new Cookie(DOMAIN,"key2","value2" ));
		map1.put("key3", new Cookie(DOMAIN,"key3","value3" ));				
		CookieMap map2 = new CookieMap();
		map2.put("key1", new Cookie(DOMAIN,"key1","value4" ));
		map2.put("key2", new Cookie(DOMAIN,"key2","value5" ));
		map2.put("key3", new Cookie(DOMAIN,"key3","value6" ));				
		
		// initialize cookie maps pair  
		CookieMapPair pair = new CookieMapPair(map1, map2);		
				
		// test
		assertFalse(matcher.matches( pair ));		
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

	
}
