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


package com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans;


import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.metadata.MetadataRepository;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.validation.ModelValidationFailedException;

/**
 * Unit test of the MBeansModelAccessorImpl class
 */
public class MBeansModelAccessorTest
{
      
	/**
	 * ID for secondary participant.
	 */	
	static final String SECONDARY_ID = "secondary-id";
	
    /**
     * Object under test.
     */
    MBeansModelAccessor accessor;

    /**
     * MBean Metadata repository.
     */
    MetadataRepository mbeanMetadataRepository;        
    
    /**
     * Message provider for I18N support.
     */
    MessageProvider messageProvider;        
     
    /**
     * Random name.
     */
	String randomName;

    /**
     * Random type.
     */
	String randomType;

    /**
     * Random type.
     */
	String randomType2;
	
    @Before
    public void setUp() throws Exception
    {
		// create random names
		randomName = RandomStringUtils.randomAlphabetic(10);		
		randomType = RandomStringUtils.randomAlphabetic(10);		
		randomType2 = RandomStringUtils.randomAlphabetic(10);		
    	
        // create mock repository
        mbeanMetadataRepository = EasyMock.createMock( MetadataRepository.class );
        
        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );

        // complete mock provider initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl();         
        EasyMock.expect( messageProvider.getMessage(
                    (String) EasyMock.isA( String.class ), 
                    (Object[]) EasyMock.anyObject()));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        EasyMock.replay(messageProvider);
        
        // create model accessor        
        accessor = new MBeansModelAccessorImpl();
        
        // inject message provider
        ReflectionTestUtils.setField( accessor, "messageProvider", messageProvider, MessageProvider.class );        
        
        // inject repository
        ReflectionTestUtils.setField( accessor, "mbeanMetadataRepository", mbeanMetadataRepository );        
    }

    @After
    public void tearDown() throws Exception
    {
        accessor = null;
        mbeanMetadataRepository = null;
    }

    /**
     * Test that accessor object can be created. 
     */
    @Test
    public void testCanCreateInstance() 
    {
        // test
        assertNotNull( accessor );            
    }

    
        
    /**
     * Test that String is considered a primitive. 
     */
    @Test
    public void testIsPrimitiveSucceedsWithString()
    {
        // setup    	
    	Object type = String.class;
		String value = "some string";
		ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( SECONDARY_ID, type, value);    	
    	
    	// test 
    	assertTrue(accessor.isPrimitive(participant));
    }

    /**
     * Test that boolean is considered a primitive. 
     */
    @Test
    public void testIsPrimitiveSucceedsWithBoolean()
    {
        // setup    	
    	Object type = boolean.class;
		boolean value = false;
		ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( SECONDARY_ID, type, value);    	
    	
    	// test 
    	assertTrue(accessor.isPrimitive(participant));
    }
    
    /**
     * Test that integer is considered a primitive. 
     */
    @Test
    public void testIsPrimitiveSucceedsWithInteger()
    {
        // setup    	
    	Object type = int.class;
		int value = 100;
		ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( SECONDARY_ID, type, value);    	
    	
    	// test 
    	assertTrue(accessor.isPrimitive(participant));
    }

    
    /**
     * Test that Object isn't considered a primitive. 
     */
    @Test
    public void testIsPrimitiveFailsWithObject()
    {
        // setup    	
    	Object type = Object.class;
		Object value = new Object();
		ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( SECONDARY_ID, type, value);    	
    	
    	// test 
    	assertFalse(accessor.isPrimitive(participant));
    }
    
}
