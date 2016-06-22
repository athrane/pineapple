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


package com.alpha.pineapple.plugin.weblogic.jmx.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Integration test for the <code>TestAttributeValueCommand</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class TestAttributeValueCommandIntegrationTest {

    /**
     * Object under test.
     */
    @Resource
    Command testAttributeValueCommand;
    
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
        
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

    /**
     * Test that command can be looked up from the context.
     */
    @Test
    public void testCanGetInstanceFromContext()
    {
        assertNotNull( testAttributeValueCommand );
    }

    /**
     * Test that correct failure message can be resolved correctly from the message source. 
     */
    @Test
    public void testResolveCorrectMessageForFailure()
    {
		Object[] args = { "XXX", "YYY" };    		
		
		// create messages
    	String message = messageProvider.getMessage("tavc.not_equal_failed", args );    	    	
    	
    	String expected = "Test failed, because the attribute values was not equal [XXX] vs. [YYY].";
    	
    	// assert
    	assertEquals(expected , message);
    	    	
    }
       
    /**
     * Test that correct failure message can be resolved correctly from the message source. 
     */
    @Test
    public void testResolveCorrectMessageForUnresolvedPrimaryParticipant()
    {
		Object[] args = { "XXX" };    		
		
		// create messages		
    	String message = messageProvider.getMessage("tavc.primary_resolution_failed", args );    	    	
    	
    	String expected = "Test failed, because resolution of primary participant failed with exception [XXX].";
    	
    	// assert
    	assertEquals(expected , message);
    	    	
    }

    /**
     * Test that correct failure message can be resolved correctly from the message source. 
     */
    @Test
    public void testResolveCorrectMessageForUnresolvedSecondaryParticipant()
    {
		Object[] args = { "XXX" };    		
		
		// create messages		
    	String message = messageProvider.getMessage("tavc.secondary_resolution_failed", args );    	    	
    	
    	String expected = "Test failed, because resolution of secondary participant failed with exception [XXX].";
    	
    	// assert
    	assertEquals(expected , message);
    	    	
    }
    
}
