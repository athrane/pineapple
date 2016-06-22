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

import com.alpha.pineapple.plugin.net.http.HttpInvocationSequence;
import com.alpha.pineapple.plugin.net.http.HttpInvocationsSet;

/**
 * Unit test of the {@link IsSetEmpty} class.
 */
public class IsSetEmptyTest {

	/**
	 * Object under test.
	 */
	@SuppressWarnings("unchecked")
	Matcher matcher;
	
	/**
	 * Mock description.
	 */
	Description description;

	/**
	 * Mock HTTP result set.
	 */
	HttpInvocationsSet set; 
	
	@Before
	public void setUp() throws Exception {
				
		matcher = IsSetEmpty.isSetEmpty();
		
		// create mock description
		description = EasyMock.createMock( Description.class);
		
		// create mock set
		set = EasyMock.createMock( HttpInvocationsSet.class);
		
	}

	@After
	public void tearDown() throws Exception {		
		matcher = null;
		description = null;
		set = null;
	}

	/**
	 * Test that empty set is positive match.
	 */
	@Test
	public void testEmptySetIsPositiveMatch() {

		// initialize sequences
		HttpInvocationSequence[] emptySequences = new HttpInvocationSequence[0] ;
		
		
		// complete mock set setup
		EasyMock.expect( set.getSequences() );	
		EasyMock.expectLastCall().andReturn( emptySequences );
		EasyMock.replay( set );		
		
		// test
		assertTrue(matcher.matches( set ));		
		EasyMock.verify( set );

	}

	/**
	 * Test that set with one entry is negative match.
	 */
	@Test
	public void testSetWithOneSequenceIsNegativeMatch() {

		// initialize sequences
		HttpInvocationSequence[] sequences = new HttpInvocationSequence[1] ;
		
		
		// complete mock set setup
		EasyMock.expect( set.getSequences() );	
		EasyMock.expectLastCall().andReturn( sequences );
		EasyMock.replay( set );		
		
		// test
		assertFalse(matcher.matches( set ));		
		EasyMock.verify( set );
	}
	
	/**
	 * Test that set with two entries is negative match.
	 */
	@Test
	public void testSetWithTwoSequencesIsNegativeMatch() {

		// initialize sequences
		HttpInvocationSequence[] sequences = new HttpInvocationSequence[2] ;
		
		
		// complete mock set setup
		EasyMock.expect( set.getSequences() );	
		EasyMock.expectLastCall().andReturn( sequences );
		EasyMock.replay( set );		
		
		// test
		assertFalse(matcher.matches( set ));		
		EasyMock.verify( set );

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

		// initialize sequences
		HttpInvocationSequence[] sequences = new HttpInvocationSequence[2] ;
				
		// complete mock set setup
		EasyMock.expect( set.getSequences() );	
		EasyMock.expectLastCall().andReturn( sequences );
		EasyMock.replay( set );		
		
		// complete mock description setup
		EasyMock.expect( description.appendText( (String) EasyMock.isA( String.class) ) );
		EasyMock.expectLastCall().andReturn( description );
		EasyMock.expect( description.appendValue( 2 ));		
		EasyMock.expectLastCall().andReturn( description );
		EasyMock.expect( description.appendText( (String) EasyMock.isA( String.class) ) );
		EasyMock.expectLastCall().andReturn( description );
		
		EasyMock.replay( description );		
		
		// invoke matcher
		matcher.describeMismatch( set, description);
		
		// test
		EasyMock.verify(description);
		EasyMock.verify(set);

	}

}
