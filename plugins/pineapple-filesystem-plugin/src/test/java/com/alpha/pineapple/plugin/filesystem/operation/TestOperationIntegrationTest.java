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


package com.alpha.pineapple.plugin.filesystem.operation;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.plugin.filesystem.session.FileSystemSession;
import com.alpha.pineapple.session.Session;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Integration test for the <code>TestOperation</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.filesystem-config.xml" } )
public class TestOperationIntegrationTest {

	
    /**
     * Object under test.
     */
    @Resource
    TestOperation testOperation;

    /**
     * Object mother for the infrastructure model.
     */
    ObjectMotherContent contentMother;
    
	/**
	 * Mock session as the plugin doesn't uses any session.
	 */
	Session session;
		
	/**
	 * Execution result.
	 */
	ExecutionResult result;
	
    
	@Before
	public void setUp() throws Exception {
				
        // create mock session
        session = EasyMock.createMock( FileSystemSession.class );
		
        // create execution result
        result = new ExecutionResultImpl( "Root result" );
        
        // create content mother
        contentMother = new ObjectMotherContent();		        
	}

	@After
	public void tearDown() throws Exception {
		session = null;
		result = null;
		contentMother = null;
	}

    /**
     * Test that operation can be looked up from the context.
     */
    @Test
    public void testCanGetOperationFromContext()
    {
        assertNotNull( testOperation );
    }
	
	/**
	 * Test that the operation can execute with a minimal model.
	 */
	@Test
	public void testCanExecuteWithMinimalModel() throws Exception {

        // create content
        Object content = contentMother.createEmptyFilesystem();

        // invoke operation
        testOperation.execute( content, session, result );
        
	}
    
}
