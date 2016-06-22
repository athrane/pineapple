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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Unit test of the {@link DoesDirectoryExist} class.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( DirectoryTestExecutionListener.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.hamcrest-config.xml" } )
public class DoesDirectoryExistTest {

	/**
	 * Object under test.
	 */
	@SuppressWarnings("unchecked")
	Matcher matcher;
	
	/**
     * Current test directory.
     */
	File testDirectory;
		
	/**
	 * Mock description.
	 */
	Description description;

	/**
	 * Test directory.
	 */
	File directory;
	
	/**
	 * Random directory name.
	 */
	String randomDirName;

	/**
	 * Random file name.
	 */	
	String randomFileName;
	
	@Before
	public void setUp() throws Exception {
		
		matcher = DoesDirectoryExist.doesDirectoryExist();
		
		// create mock description
		description = EasyMock.createMock( Description.class);
	
        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// initialize random file name
		randomFileName = RandomStringUtils.randomAlphabetic(10);
		
		// initialize directory
		randomDirName = RandomStringUtils.randomAlphabetic(10);				
		directory = new File( testDirectory, randomDirName);        
	}

	@After
	public void tearDown() throws Exception {		
		matcher = null;
		description = null;
		testDirectory = null;
	}

	/**
	 * Test that existing directory is a positive match
	 * @throws IOException if test fails.
	 */
	@Test
	public void testExistingDirectoryIsPositiveMatch() throws IOException {

		// initialize directory
		FileUtils.forceMkdir(directory);
				
		// test
		assertTrue(matcher.matches( directory ));		
	}

	/**
	 * Test non existing directory is negative match.
	 */
	@Test
	public void testNonExistingDirectoryIsNegativeMatch() {

		// initialize file
		File file = new File(this.testDirectory, "unknwon-dir");		
				
		// test
		assertFalse(matcher.matches( file ));		
	}
	
	/**
	 * Test file is negative match.
	 * 
	 * @throws Exception If test fails
	 */
	@Test
	public void testFileIsNegativeMatch() throws Exception {

		// initialize file
		File file = new File(this.testDirectory, randomFileName);

		// create file content
		Collection<String> lines = new ArrayList<String>();
		lines.add("<hello-pineapple />");
		
		// write the file
		FileUtils.writeLines(file, lines);				
				
		// create child test file 
		assertFalse(matcher.matches( file ));				
	}
	
	
	/**
	 * Test that matcher creates a description.
	 */
	@Test
	public void testDescribeTo() {
	
		// complete mock description setup
		EasyMock.expect( description.appendText( (String) EasyMock.isA( String.class) ) );
		EasyMock.expectLastCall().andReturn( description );
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

		// initialize file
		File file = new File("filename");
					
		// complete mock description setup
		EasyMock.expect( description.appendText( (String) EasyMock.isA( String.class) ) );
		EasyMock.expectLastCall().andReturn( description );
		EasyMock.expect( description.appendValue( file ));		
		EasyMock.expectLastCall().andReturn( description );
		EasyMock.expect( description.appendText( (String) EasyMock.isA( String.class) ) );
		EasyMock.expectLastCall().andReturn( description );		
		EasyMock.replay( description );		
		
		// invoke matcher
		matcher.describeMismatch( file, description);
		
		// test
		EasyMock.verify(description);
	}

}
