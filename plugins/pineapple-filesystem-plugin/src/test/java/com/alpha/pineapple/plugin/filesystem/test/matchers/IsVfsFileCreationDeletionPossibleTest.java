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


package com.alpha.pineapple.plugin.filesystem.test.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.NameScope;
import org.easymock.EasyMock;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.plugin.filesystem.session.FileSystemSession;
import com.alpha.pineapple.session.SessionException;

/**
 * Unit test of the {@link IsVfsFileCreationDeletionPossible} class.
 */
public class IsVfsFileCreationDeletionPossibleTest {

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
	 * Mock file system session.
	 */
	FileSystemSession session;

	/**
	 *  Mock file object.
	 */
	FileObject fileObject;
	
	@Before
	public void setUp() throws Exception {
				
		// create mock session 
		session = EasyMock.createMock( FileSystemSession.class);

		// create mock file object
		fileObject = EasyMock.createMock( FileObject.class);
				
		// create matcher		
		matcher = IsVfsFileCreationDeletionPossible.isVfsFileCreationDeletionPossible(session);			
	}

	@After
	public void tearDown() throws Exception {		
		matcher = null;
		session = null;
		fileObject = null;
	}

	/**
	 * Test positive match if file object can be created and deleted.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testIsPositiveMatchIfFileObjectCanBeCreatedAndDeleted() throws Exception  {
				
		String path = "/some-path";

		// complete mock set setup		
		FileObject ChildFileObject = EasyMock.createMock( FileObject.class);
		EasyMock.expect( ChildFileObject.exists()).andReturn(false);
		ChildFileObject.createFolder();
		EasyMock.expect( ChildFileObject.exists()).andReturn(true);
		EasyMock.expect( ChildFileObject.delete()).andReturn(true);
		EasyMock.expect( ChildFileObject.exists()).andReturn(false);
		EasyMock.replay( ChildFileObject );
		
		// complete mock set setup
		EasyMock.expect( fileObject.resolveFile( EasyMock.isA(String.class), EasyMock.isA(NameScope.class) )).andReturn(ChildFileObject);			
		EasyMock.replay( fileObject );		
		
		// complete mock set setup
		EasyMock.expect( session.resolveFile(path)).andReturn(fileObject);	
		EasyMock.replay( session );		
		
		// test
		assertTrue(matcher.matches( path ));		

		// verify mocks
		EasyMock.verify( session );
		EasyMock.verify( fileObject );		
		EasyMock.verify( ChildFileObject );		
	}

	/**
	 * Test negative match if test folder already exists.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testIsNegativeMatchIfTestFolderAlreadyExists() throws Exception  {
				
		String path = "/some-path";

		// complete mock set setup		
		FileObject ChildFileObject = EasyMock.createMock( FileObject.class);
		EasyMock.expect( ChildFileObject.exists()).andReturn(true);
		EasyMock.replay( ChildFileObject );
		
		// complete mock set setup
		EasyMock.expect( fileObject.resolveFile( EasyMock.isA(String.class), EasyMock.isA(NameScope.class) )).andReturn(ChildFileObject);			
		EasyMock.replay( fileObject );		
		
		// complete mock set setup
		EasyMock.expect( session.resolveFile(path)).andReturn(fileObject);	
		EasyMock.replay( session );		
		
		// test
		assertFalse(matcher.matches( path ));		

		// verify mocks
		EasyMock.verify( session );
		EasyMock.verify( fileObject );		
		EasyMock.verify( ChildFileObject );		
	}

	/**
	 * Test negative match if test folder file can't
	 * be created.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testIsNegativeMatchIfTestFolderCantBeCreated() throws Exception  {
				
		String path = "/some-path";

		// complete mock set setup		
		FileObject ChildFileObject = EasyMock.createMock( FileObject.class);
		EasyMock.expect( ChildFileObject.exists()).andReturn(false);
		ChildFileObject.createFolder();
		EasyMock.expect( ChildFileObject.exists()).andReturn(false);
		EasyMock.replay( ChildFileObject );
		
		// complete mock set setup
		EasyMock.expect( fileObject.resolveFile( EasyMock.isA(String.class), EasyMock.isA(NameScope.class) )).andReturn(ChildFileObject);			
		EasyMock.replay( fileObject );		
		
		// complete mock set setup
		EasyMock.expect( session.resolveFile(path)).andReturn(fileObject);	
		EasyMock.replay( session );		
		
		// test
		assertFalse(matcher.matches( path ));		

		// verify mocks
		EasyMock.verify( session );
		EasyMock.verify( fileObject );		
		EasyMock.verify( ChildFileObject );		
	}

	/**
	 * Test negative match if test folder file can't
	 * be deleted again.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testIsNegativeMatchIfTestFolderCantBeDeleted() throws Exception  {
				
		String path = "/some-path";

		// complete mock set setup
		FileObject ChildFileObject = EasyMock.createMock( FileObject.class);
		EasyMock.expect( ChildFileObject.exists()).andReturn(false);
		ChildFileObject.createFolder();
		EasyMock.expect( ChildFileObject.exists()).andReturn(true);
		EasyMock.expect( ChildFileObject.delete()).andReturn(true);
		EasyMock.expect( ChildFileObject.exists()).andReturn(true);
		EasyMock.replay( ChildFileObject );
		
		// complete mock set setup
		EasyMock.expect( fileObject.resolveFile( EasyMock.isA(String.class), EasyMock.isA(NameScope.class) )).andReturn(ChildFileObject);			
		EasyMock.replay( fileObject );		
		
		// complete mock set setup
		EasyMock.expect( session.resolveFile(path)).andReturn(fileObject);	
		EasyMock.replay( session );		
		
		// test
		assertFalse(matcher.matches( path ));		

		// verify mocks
		EasyMock.verify( session );
		EasyMock.verify( fileObject );		
		EasyMock.verify( ChildFileObject );		
	}
	

	/**
	 * Test negative match if session throws exception.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testIsNegativeMatchIfSessionThrowsException() throws Exception  {
				
		String path = "/some-path";

		// complete mock set setup
		EasyMock.replay( fileObject );		
		
		// complete mock set setup
		EasyMock.expect( session.resolveFile(path)).andThrow(new SessionException("..."));	
		EasyMock.replay( session );		
		
		// test
		assertFalse(matcher.matches( path ));		

		// verify mocks
		EasyMock.verify( session );
		EasyMock.verify( fileObject );		
	}
	
}
