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
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import javax.management.MBeanAttributeInfo;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolver;
import com.alpha.testutils.ObjectMotherWebLogicJMX;

/**
 * Unit test of the <code>MBeansObjectNameBasedModelResolverImpl</code> class.
 */
public class MBeansObjectNameBasedModelResolverTest
{

    /**
     * object under test
     */
    ModelResolver resolver;

    /**
     * Model accessor
     */
    MBeansModelAccessor modelAccessor;

    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;    

    /**
     * Mock MBean attribute info.
     */
    MBeanAttributeInfo attributeInfo;    
    
    /**
     * WebLogic JMX object mother.
     */
    ObjectMotherWebLogicJMX jmxMother;		
                
    @Before
    public void setUp() throws Exception
    {
        // create resolver
        resolver = new MBeansObjectNameBasedModelResolverImpl();
    	
        // create mock accessor
        modelAccessor = EasyMock.createMock( MBeansModelAccessor.class );

        // inject model accessor into resolver
        ReflectionTestUtils.setField( resolver, "modelAccessor", modelAccessor );
        
        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
        
        // inject message source
        ReflectionTestUtils.setField( resolver, "messageProvider", messageProvider );
        
        // complete mock source initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl(); 
        
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject()));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        EasyMock.replay(messageProvider);
        
        // create mock attribute info
        attributeInfo = createMock( MBeanAttributeInfo.class );
        
		// create JMX mother
		jmxMother = new ObjectMotherWebLogicJMX();			
    }

    @After
    public void tearDown() throws Exception
    {
        modelAccessor = null;
        resolver = null;
    }

    /**
     * Test that resolver object can be created. Note: fields which are dependency injected by Spring isn't initialized.
     */
    @Test
    public void testCanCreateInstance()
    {
        // test
        assertNotNull( resolver );
    }
    
    /**
     * Test that resolver can resolved empty collection.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testResolveEmptyCollection() throws Exception
    {
    	// complete accessor mock initialization
    	EasyMock.replay(modelAccessor);

    	// complete attribute info mock initialization
    	replay(attributeInfo);
    	
    	// create participant
		String randomStr = RandomStringUtils.random(10);
    	ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult(randomStr, attributeInfo, ArrayUtils.EMPTY_OBJECT_ARRAY );
    	
		// resolve
        HashMap<String, ResolvedParticipant> result = resolver.resolveCollectionAttributeValues(participant);

        // test 
        assertNotNull(result);
        assertEquals(0,result.size());
        
        // test 
    	EasyMock.verify(modelAccessor);
    	verify(attributeInfo);    	
    }
    
}

