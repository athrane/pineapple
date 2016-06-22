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


package com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.metadata;

import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;

/**
 * Unit test of the MetadataRepository class.
 */
public class MetadataRepositoryTest {

	/**
	 * Empty string.
	 */
    static final String EMPTY_STRING = "";

	/**
     * Object under test.
     */
	MetadataRepository repository;
		
    /**
     * MBeanInfo mock.
     */
    MBeanInfo mbeanInfo;

    /**
     * MBeanAttributeInfo mock.
     */
    MBeanAttributeInfo attributeInfo;
    
    /**
     * Descriptor mock.
     */
    Descriptor descriptor;
    
    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;    
    
    /**
     * Random value.
     */
	String randomValue;

    /**
     * Random value.
     */
	String randomValue2;

    /**
     * Random value.
     */
	String randomValue3;
	
    /**
     * Random name.
     */
	String randomName;

    /**
     * Random name.
     */
	String randomName2;
	
    /**
     * Random type.
     */
	String randomType;
	
	@Before
	public void setUp() throws Exception {

		// create random names
		randomValue = RandomStringUtils.randomAlphabetic(10);
		randomValue2 = RandomStringUtils.randomAlphabetic(10);		
		randomValue3 = RandomStringUtils.randomAlphabetic(10);		
		randomName = RandomStringUtils.randomAlphabetic(10);		
		randomName2 = RandomStringUtils.randomAlphabetic(10);		
		randomType = RandomStringUtils.randomAlphabetic(10);		
		
        // create mock 
        repository = new MetadataRepositoryImpl();
        
        // create mock 
        mbeanInfo = createMock( MBeanInfo.class );		

        // create mock 
        attributeInfo = createMock( MBeanAttributeInfo.class );		
        
        // create mock 
        descriptor = EasyMock.createMock( Descriptor.class );		
        
        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
		
        // inject message source
        ReflectionTestUtils.setField( repository, "messageProvider", messageProvider );
        
        // complete mock source initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl(); 

        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class )));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject()));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        EasyMock.replay(messageProvider);                                                
	}

	@After
	public void tearDown() throws Exception {
		
		repository = null;
		mbeanInfo = null;
		descriptor = null;		
	}
	
			
    /**
     * Test that repository initially doesn't contain MBean info.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testDoesntInitiallyContainMBean() throws Exception
    {    	   
    	// complete MBeanInfo mock initialization    	
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor);
		replay(mbeanInfo);

    	// complete descriptor mock initialization		
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue);
		replay(descriptor);	
    	
		// test 
		assertFalse(repository.containsMBean(mbeanInfo));
						
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);
    }

    
    /**
     * Test that repository contains added MBean info
     * for MBean with zero attributes and operations
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testContainsMBeanWithZeroAttributesAndOperations() throws Exception
    {    	       
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] {};
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] {};

    	// complete MBeanInfo mock initialization		
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(3);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);		
		replay(mbeanInfo);
		    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(3);
		replay(descriptor);

    	// add MBean    	
    	repository.addMBean(mbeanInfo);
    	
		// test 
		assertTrue(repository.containsMBean(mbeanInfo));
						
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);
    }
    
	
    /**
     * Test that meta data can retrieved from repository.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testGetMBeanMetaData() throws Exception
    {
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] {};
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] {};

    	// complete MBeanInfo mock initialization		
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(3);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);		
		replay(mbeanInfo);
    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(3);
		replay(descriptor);

    	// add MBean    	
    	repository.addMBean(mbeanInfo);
    							
		// get attribute meta data
		MBeanMetadata metadata = repository.getMBeanMetadata(mbeanInfo);
		
		// test
		assertNotNull(metadata );		
    	
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);    						
    }
    
    /**
     * Test that meta data contains expected MBeanInfo.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testMBeanMetaDataContainsMBeanInfo() throws Exception
    {
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] {};
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] {};

    	// complete MBeanInfo mock initialization		
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(3);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);		
		replay(mbeanInfo);
    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(3);
		replay(descriptor);

    	// add MBean    	
    	repository.addMBean(mbeanInfo);
    							
		// get MBean meta data
		MBeanMetadata metadata = repository.getMBeanMetadata(mbeanInfo);
		
		// test
		assertNotNull(metadata );		
		assertEquals(mbeanInfo,metadata.getInfo());
    	
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);    						
    }
    
    /**
     * Test that get attribute info returns null for unknown attribute.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testGetAttributeInfoReturnsNull() throws Exception
    {
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] {};
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] {};

    	// complete MBeanInfo mock initialization		
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(4);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);		
		replay(mbeanInfo);
    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(4);
		replay(descriptor);

		// get attribute info 
		MBeanAttributeInfo info = repository.getAttributeInfo(mbeanInfo, randomName);
		
		// test
		assertNull(info);		
    	
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);    						
    }
   	
    /**
     * Test that get attribute info returns attribute info for known attribute.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testGetAttributeInfoReturnsAttributeInfo() throws Exception
    {
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] {};
    	
    	// create attribute info descriptor mock
        Descriptor attributeDescriptor = createMock(Descriptor.class );
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD))
			.andReturn(randomValue2)
			.times(2);
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD))
			.andReturn(randomValue3);			
        replay(attributeDescriptor);
    	
    	// create attribute info mock 
    	MBeanAttributeInfo attributeInfo = createMock(MBeanAttributeInfo.class );
		expect(attributeInfo.getDescriptor())
			.andReturn(attributeDescriptor);		
		expect(attributeInfo.getName())
			.andReturn(randomName)
			.times(2);		
		expect(attributeInfo.getType())
			.andReturn(randomType)		
			.times(2);
    	replay(attributeInfo);
    	
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] { attributeInfo };
		
    	// complete MBeanInfo mock initialization
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(4);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);				
		replay(mbeanInfo);	
    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(4);
		replay(descriptor);

		// get attribute info 
		MBeanAttributeInfo info = repository.getAttributeInfo(mbeanInfo, randomName);
		
		// test
		assertNotNull(info);		
		assertEquals(randomName, info.getName());
		assertEquals(randomType, info.getType());		
    	
		// test
		verify(attributeInfo);
		verify(attributeDescriptor);
		verify(mbeanInfo);
		EasyMock.verify(descriptor);    						
    }

    /**
     * Test that get create method returns null for unknown attribute.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testGetCreateMethodReturnsNull() throws Exception
    {
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] {};
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] {};

    	// complete MBeanInfo mock initialization		
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(3);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);		
		replay(mbeanInfo);
    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(3);
		replay(descriptor);

		// get operation info 		
		MBeanOperationInfo info = repository.getCreateMethod(mbeanInfo, randomName);
		
		// test
		assertNull(info);		
    	
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);    						
    }

    /**
     * Test that get create method returns operation info for known attribute
     * who do not contain a create method.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testGetCreateMethodReturnsNullForAttributeWithoutCreateMethod() throws Exception
    {
    	// create signature mock
    	MBeanParameterInfo param1 = createMock(MBeanParameterInfo.class );
    	replay(param1);
		MBeanParameterInfo[] singleArgSignature = { param1 };		
    	
    	// create operation info mock 
    	MBeanOperationInfo operationInfo = createMock(MBeanOperationInfo.class );
		expect(operationInfo.getName())
			.andReturn(randomName2);
		expect(operationInfo.getSignature())
			.andReturn(singleArgSignature);    			
    	replay(operationInfo);
    	
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] { operationInfo };
    	
    	// create attribute info descriptor mock
        Descriptor attributeDescriptor = createMock(Descriptor.class );
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD))
			.andReturn(randomValue2)
			.times(2);
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD))
			.andReturn(randomValue3);			
        replay(attributeDescriptor);
        
    	// create attribute info mock 
    	MBeanAttributeInfo attributeInfo = createMock(MBeanAttributeInfo.class );
		expect(attributeInfo.getDescriptor())
			.andReturn(attributeDescriptor);		
		expect(attributeInfo.getName())
			.andReturn(randomName);		
		expect(attributeInfo.getType())
			.andReturn(randomType);	
    	replay(attributeInfo);
    	
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] { attributeInfo };
		
    	// complete MBeanInfo mock initialization
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(3);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);				
		replay(mbeanInfo);	
    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(3);
		replay(descriptor);

		// get operation info 		
		MBeanOperationInfo info = repository.getCreateMethod(mbeanInfo, randomName);
		
		// test
		assertNull(info);				
    	
		// test
		verify(param1);
		verify(operationInfo);		
		verify(attributeInfo);
		verify(attributeDescriptor);
		verify(mbeanInfo);
		EasyMock.verify(descriptor);    						
    }

    /**
     * Test that get create method returns operation info for known attribute.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testGetCreateMethodReturnsOperationInfo() throws Exception
    {
    	// create signature mock
    	MBeanParameterInfo param1 = createMock(MBeanParameterInfo.class );
    	replay(param1);
		MBeanParameterInfo[] singleArgSignature = { param1 };
    	
    	// create operation info mock 
    	MBeanOperationInfo operationInfo = createMock(MBeanOperationInfo.class );
		expect(operationInfo.getName())
			.andReturn(randomValue2)
			.times(2);    	
		expect(operationInfo.getSignature())
		.andReturn(singleArgSignature);    					
    	replay(operationInfo);
    	
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] { operationInfo };
    	
    	// create attribute info descriptor mock
        Descriptor attributeDescriptor = createMock(Descriptor.class );
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD))
			.andReturn(randomValue2)
			.times(2);
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD))
			.andReturn(randomValue3);			
        replay(attributeDescriptor);
        
    	// create attribute info mock 
    	MBeanAttributeInfo attributeInfo = createMock(MBeanAttributeInfo.class );
		expect(attributeInfo.getDescriptor())
			.andReturn(attributeDescriptor);		
		expect(attributeInfo.getName())
			.andReturn(randomName);		
		expect(attributeInfo.getType())
			.andReturn(randomType);	
    	replay(attributeInfo);
    	
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] { attributeInfo };
		
    	// complete MBeanInfo mock initialization
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(3);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);				
		replay(mbeanInfo);	
    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(3);
		replay(descriptor);

		// get operation info 		
		MBeanOperationInfo info = repository.getCreateMethod(mbeanInfo, randomName);
		
		// test
		assertNotNull(info);		
		assertEquals(randomValue2, info.getName());		
    	
		// test
		verify(param1);
		verify(operationInfo);		
		verify(attributeInfo);
		verify(attributeDescriptor);
		verify(mbeanInfo);
		EasyMock.verify(descriptor);    						
    }

    /**
     * Test that get create method returns operation info for known attribute.
     * 
     * If the attribute has two create methods (with the identical names), 
     * one with a zero argument signature and one with a two argument signature 
     * then the zero argument version is stored/returned.    
     *  
     * @throws Exception If test fails.
     */
    @Test
    public void testGetCreateMethodReturnsOperationInfoForCreateMethodWithZeroArg() throws Exception
    {
    	// create signature mock
    	MBeanParameterInfo param1 = createMock(MBeanParameterInfo.class );
    	replay(param1);
    	MBeanParameterInfo param2 = createMock(MBeanParameterInfo.class );
    	replay(param2);
		MBeanParameterInfo[] zeroArgSignature = { };
		MBeanParameterInfo[] twoArgSignature = { param1, param2 };		
    	
    	// create operation info mock 
    	MBeanOperationInfo operationInfo = createMock(MBeanOperationInfo.class );
		expect(operationInfo.getName())
			.andReturn(randomValue2)
			.times(2);    	
		expect(operationInfo.getSignature())
			.andReturn(zeroArgSignature);    					
    	replay(operationInfo);

    	MBeanOperationInfo operationInfo2 = createMock(MBeanOperationInfo.class );
		expect(operationInfo2.getName())
			.andReturn(randomValue2);
    	expect(operationInfo2.getSignature())
			.andReturn(twoArgSignature);    					
    	replay(operationInfo2);
    	
    	
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] { operationInfo, operationInfo2 };
    	
    	// create attribute info descriptor mock
        Descriptor attributeDescriptor = createMock(Descriptor.class );
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD))
			.andReturn(randomValue2)
			.times(2);
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD))
			.andReturn(randomValue3);			
        replay(attributeDescriptor);
        
    	// create attribute info mock 
    	MBeanAttributeInfo attributeInfo = createMock(MBeanAttributeInfo.class );
		expect(attributeInfo.getDescriptor())
			.andReturn(attributeDescriptor);		
		expect(attributeInfo.getName())
			.andReturn(randomName);		
		expect(attributeInfo.getType())
			.andReturn(randomType);	
    	replay(attributeInfo);
    	
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] { attributeInfo };
		
    	// complete MBeanInfo mock initialization
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(3);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);				
		replay(mbeanInfo);	
    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(3);
		replay(descriptor);

		// get operation info 		
		MBeanOperationInfo info = repository.getCreateMethod(mbeanInfo, randomName);
		
		// test
		assertNotNull(info);		
		assertEquals(randomValue2, info.getName());		
    	
		// test
		verify(param1);
		verify(operationInfo);		
		verify(operationInfo2);		
		verify(attributeInfo);
		verify(attributeDescriptor);
		verify(mbeanInfo);
		EasyMock.verify(descriptor);    						
    }

    /**
     * Test that get create method returns operation info for known attribute.
     * 
     * If the attribute has two create methods (with the identical names), 
     * one with a single argument signature and one with a two argument signature 
     * then the single argument version is stored/returned.    
     *  
     * @throws Exception If test fails.
     */
    @Test
    public void testGetCreateMethodReturnsOperationInfoForCreateMethodWithSingleArg() throws Exception
    {
    	// create signature mock
    	MBeanParameterInfo param1 = createMock(MBeanParameterInfo.class );
    	replay(param1);
    	MBeanParameterInfo param2 = createMock(MBeanParameterInfo.class );
    	replay(param2);    	
		MBeanParameterInfo[] singleArgSignature = { param1 };
		MBeanParameterInfo[] twoArgSignature = { param1, param2 };		
    	
    	// create operation info mock 
    	MBeanOperationInfo operationInfo = createMock(MBeanOperationInfo.class );
		expect(operationInfo.getName())
			.andReturn(randomValue2)
			.times(2);    	
		expect(operationInfo.getSignature())
			.andReturn(singleArgSignature);    					
    	replay(operationInfo);

    	MBeanOperationInfo operationInfo2 = createMock(MBeanOperationInfo.class );
		expect(operationInfo2.getName())
			.andReturn(randomValue2);
    	expect(operationInfo2.getSignature())
			.andReturn(twoArgSignature);    					
    	replay(operationInfo2);
    	
    	
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] { operationInfo, operationInfo2 };
    	
    	// create attribute info descriptor mock
        Descriptor attributeDescriptor = createMock(Descriptor.class );
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD))
			.andReturn(randomValue2)
			.times(2);
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD))
			.andReturn(randomValue3);			
        replay(attributeDescriptor);
        
    	// create attribute info mock 
    	MBeanAttributeInfo attributeInfo = createMock(MBeanAttributeInfo.class );
		expect(attributeInfo.getDescriptor())
			.andReturn(attributeDescriptor);		
		expect(attributeInfo.getName())
			.andReturn(randomName);		
		expect(attributeInfo.getType())
			.andReturn(randomType);	
    	replay(attributeInfo);
    	
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] { attributeInfo };
		
    	// complete MBeanInfo mock initialization
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(3);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);				
		replay(mbeanInfo);	
    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(3);
		replay(descriptor);

		// get operation info 		
		MBeanOperationInfo info = repository.getCreateMethod(mbeanInfo, randomName);
		
		// test
		assertNotNull(info);		
		assertEquals(randomValue2, info.getName());		
    	
		// test
		verify(param1);
		verify(operationInfo);		
		verify(operationInfo2);		
		verify(attributeInfo);
		verify(attributeDescriptor);
		verify(mbeanInfo);
		EasyMock.verify(descriptor);    						
    }

    /**
     * Test that get create method returns operation info for known attribute.
     * 
     * If the attribute has three create methods (with the identical names), 
     * one with zero, one and two argument signature 
     * then the single argument version is stored/returned.    
     *  
     * @throws Exception If test fails.
     */
    @Test
    public void testGetCreateMethodPrefersCreateMethodWithSingleArg() throws Exception
    {
    	// create signature mock
    	MBeanParameterInfo param0 = createMock(MBeanParameterInfo.class );
    	replay(param0);
    	MBeanParameterInfo param1 = createMock(MBeanParameterInfo.class );
    	replay(param1);
    	MBeanParameterInfo param2 = createMock(MBeanParameterInfo.class );
    	replay(param2);
		MBeanParameterInfo[] zeroArgSignature = {};    	
		MBeanParameterInfo[] singleArgSignature = { param1 };
		MBeanParameterInfo[] twoArgSignature = { param1, param2 };		
    	
    	// create operation info mock 
    	MBeanOperationInfo operationInfo0 = createMock(MBeanOperationInfo.class );
		expect(operationInfo0.getName())
			.andReturn(randomValue2);
		expect(operationInfo0.getSignature())
			.andReturn(zeroArgSignature);
    	replay(operationInfo0);
		
		MBeanOperationInfo operationInfo1 = createMock(MBeanOperationInfo.class );
		expect(operationInfo1.getName())
			.andReturn(randomValue2)
			.times(2);    	
		expect(operationInfo1.getSignature())
			.andReturn(singleArgSignature)
			.times(2);
    	replay(operationInfo1);

    	MBeanOperationInfo operationInfo2 = createMock(MBeanOperationInfo.class );
		expect(operationInfo2.getName())
			.andReturn(randomValue2);
		expect(operationInfo2.getSignature())
			.andReturn(twoArgSignature);    					
    	replay(operationInfo2);
    	    	
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] { operationInfo0, operationInfo1, operationInfo2 };
    	
    	// create attribute info descriptor mock
        Descriptor attributeDescriptor = createMock(Descriptor.class );
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD))
			.andReturn(randomValue2)
			.times(2);
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD))
			.andReturn(randomValue3);			
        replay(attributeDescriptor);
        
    	// create attribute info mock 
    	MBeanAttributeInfo attributeInfo = createMock(MBeanAttributeInfo.class );
		expect(attributeInfo.getDescriptor())
			.andReturn(attributeDescriptor);		
		expect(attributeInfo.getName())
			.andReturn(randomName);		
		expect(attributeInfo.getType())
			.andReturn(randomType);	
    	replay(attributeInfo);
    	
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] { attributeInfo };
		
    	// complete MBeanInfo mock initialization
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(3);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);				
		replay(mbeanInfo);	
    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(3);
		replay(descriptor);

		// get operation info 		
		MBeanOperationInfo info = repository.getCreateMethod(mbeanInfo, randomName);
		
		// test
		assertNotNull(info);		
		assertEquals(randomValue2, info.getName());
		assertEquals(1, info.getSignature().length);		
    	
    	
		// test
		verify(param1);
		verify(operationInfo0);				
		verify(operationInfo1);		
		verify(operationInfo2);		
		verify(attributeInfo);
		verify(attributeDescriptor);
		verify(mbeanInfo);
		EasyMock.verify(descriptor);    						
    }

    /**
     * Test that get create method returns operation info for known attribute.
     * 
     * If the attribute has three create methods (with the identical names), 
     * one with zero, one and two argument signature 
     * then the single argument version is stored/returned.    
     *  
     * Compared to the unnumbered version of this method, the order of the
     * BeanOperationInfo's is reversed.
     *  
     * @throws Exception If test fails.
     */
    @Test
    public void testGetCreateMethodPrefersCreateMethodWithSingleArg2() throws Exception
    {
    	// create signature mock
    	MBeanParameterInfo param0 = createMock(MBeanParameterInfo.class );
    	replay(param0);
    	MBeanParameterInfo param1 = createMock(MBeanParameterInfo.class );
    	replay(param1);
    	MBeanParameterInfo param2 = createMock(MBeanParameterInfo.class );
    	replay(param2);
		MBeanParameterInfo[] zeroArgSignature = {};    	
		MBeanParameterInfo[] singleArgSignature = { param1 };
		MBeanParameterInfo[] twoArgSignature = { param1, param2 };		
    	
    	// create operation info mock 
    	MBeanOperationInfo operationInfo0 = createMock(MBeanOperationInfo.class );
		expect(operationInfo0.getName())
			.andReturn(randomValue2);
		expect(operationInfo0.getSignature())
			.andReturn(zeroArgSignature);
    	replay(operationInfo0);
		
		MBeanOperationInfo operationInfo1 = createMock(MBeanOperationInfo.class );
		expect(operationInfo1.getName())
			.andReturn(randomValue2)
			.times(2);    	
		expect(operationInfo1.getSignature())
			.andReturn(singleArgSignature)
			.times(2);    				
    	replay(operationInfo1);

    	MBeanOperationInfo operationInfo2 = createMock(MBeanOperationInfo.class );
		expect(operationInfo2.getName())
			.andReturn(randomValue2);
    	expect(operationInfo2.getSignature())
			.andReturn(twoArgSignature);
    	replay(operationInfo2);

    	// reversed order of BeanOperationInfo's
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] { operationInfo2, operationInfo1, operationInfo0 };
    	
    	// create attribute info descriptor mock
        Descriptor attributeDescriptor = createMock(Descriptor.class );
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD))
			.andReturn(randomValue2)
			.times(2);
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD))
			.andReturn(randomValue3);			
        replay(attributeDescriptor);
        
    	// create attribute info mock 
    	MBeanAttributeInfo attributeInfo = createMock(MBeanAttributeInfo.class );
		expect(attributeInfo.getDescriptor())
			.andReturn(attributeDescriptor);		
		expect(attributeInfo.getName())
			.andReturn(randomName);		
		expect(attributeInfo.getType())
			.andReturn(randomType);	
    	replay(attributeInfo);
    	
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] { attributeInfo };
		
    	// complete MBeanInfo mock initialization
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(3);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);				
		replay(mbeanInfo);	
    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(3);
		replay(descriptor);

		// get operation info 		
		MBeanOperationInfo info = repository.getCreateMethod(mbeanInfo, randomName);
		
		// test
		assertNotNull(info);		
		assertEquals(randomValue2, info.getName());
		assertEquals(1, info.getSignature().length);		
    	
    	
		// test
		verify(param1);
		verify(operationInfo0);				
		verify(operationInfo1);		
		verify(operationInfo2);		
		verify(attributeInfo);
		verify(attributeDescriptor);
		verify(mbeanInfo);
		EasyMock.verify(descriptor);    						
    }    
    
    /**
     * Test that get create method isn't stored for 
     * attribute whose create method has two arguments.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testGetCreateMethodFailsForAttributeWithTwoArgs() throws Exception
    {
    	// create signature mock
    	MBeanParameterInfo param1 = createMock(MBeanParameterInfo.class );
    	replay(param1);
    	MBeanParameterInfo param2 = createMock(MBeanParameterInfo.class );
    	replay(param2);    	
		MBeanParameterInfo[] twoArgSignature = { param1, param2 };
    	
    	// create operation info mock 
    	MBeanOperationInfo operationInfo = createMock(MBeanOperationInfo.class );
		expect(operationInfo.getName())
			.andReturn(randomValue2);
    	expect(operationInfo.getSignature())
			.andReturn(twoArgSignature);    					
    	replay(operationInfo);
    	
		MBeanOperationInfo[] operations = new MBeanOperationInfo[] { operationInfo };
    	
    	// create attribute info descriptor mock
        Descriptor attributeDescriptor = createMock(Descriptor.class );
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD))
			.andReturn(randomValue2)
			.times(2);
		expect(attributeDescriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD))
			.andReturn(randomValue3);			
        replay(attributeDescriptor);
        
    	// create attribute info mock 
    	MBeanAttributeInfo attributeInfo = createMock(MBeanAttributeInfo.class );
		expect(attributeInfo.getDescriptor())
			.andReturn(attributeDescriptor);		
		expect(attributeInfo.getName())
			.andReturn(randomName);		
		expect(attributeInfo.getType())
			.andReturn(randomType);	
    	replay(attributeInfo);
    	
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[] { attributeInfo };
		
    	// complete MBeanInfo mock initialization
		expect(mbeanInfo.getDescriptor())
			.andReturn(descriptor)
			.times(3);		
		expect(mbeanInfo.getAttributes())
			.andReturn(attributes);
		expect(mbeanInfo.getOperations())
			.andReturn(operations);				
		replay(mbeanInfo);	
    	
    	// complete descriptor mock initialization
		expect(descriptor.getFieldValue("Name"))
			.andReturn(randomValue)
			.times(3);
		replay(descriptor);

		// get operation info 		
		MBeanOperationInfo info = repository.getCreateMethod(mbeanInfo, randomName);
		
		// test
		assertNull(info);		
    	
		// test
		verify(param1);
		verify(param2);		
		verify(operationInfo);		
		verify(attributeInfo);
		verify(attributeDescriptor);
		verify(mbeanInfo);
		EasyMock.verify(descriptor);    						
    }

    
    
    
    
    /**
     * Test that repository return expected candidate type for Target attributes.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testReturnExpectedCandidateTypesForTargets() throws Exception
    {    	   
    	// complete MBeanInfo mock initialization    	
		expect(attributeInfo.getName()).andReturn("Targets");
		expect(attributeInfo.getType()).andReturn(WebLogicMBeanConstants.OBJECTNAME_ARRAY_TYPE);
		replay(attributeInfo);
    	
		// get types  
		String[] types = repository.resolveCandidateReferenceTypes(attributeInfo);		
						
		// test
		assertEquals(2, types.length);		
		
		// test
		verify(attributeInfo);
    }

    /**
     * Test that repository return expected candidate type for Target attributes.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testReturnExpectedCandidateTypesForTarget() throws Exception
    {    	   
    	// complete MBeanInfo mock initialization    	
		expect(attributeInfo.getName()).andReturn("Machine").times(2);
		expect(attributeInfo.getType()).andReturn(WebLogicMBeanConstants.OBJECTNAME_TYPE);
		replay(attributeInfo);
    	
		// get types  
		String[] types = repository.resolveCandidateReferenceTypes(attributeInfo);		
						
		// test
		assertEquals(1, types.length);		
		
		// test
		verify(attributeInfo);
    }
    
    /**
     * Test that repository throws exception for unknown attributes.
     * 
     * @throws Exception If test fails.
     */
    @Test(expected=UnsupportedOperationException.class)
    public void testThrowsExceptionForUnknownCandidateTypes() throws Exception
    {    	   
    	// complete MBeanInfo mock initialization    	
		expect(attributeInfo.getName()).andReturn(randomName).times(2);
		replay(attributeInfo);
    	
		// get types  
		String[] types = repository.resolveCandidateReferenceTypes(attributeInfo);								
    }
    
}
