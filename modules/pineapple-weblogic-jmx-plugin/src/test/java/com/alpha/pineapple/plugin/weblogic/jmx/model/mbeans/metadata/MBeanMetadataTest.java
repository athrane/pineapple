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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;

/**
 * Unit test of the MBeanMetadata class.
 */
public class MBeanMetadataTest {

	/**
	 * Empty string.
	 */
    static final String EMPTY_STRING = "";

	/**
     * Object under test.
     */
	MBeanMetadataImpl mbeanMetadata;
	
    /**
     * MBeanInfo mock.
     */
    MBeanInfo mbeanInfo;
	
    /**
     * Descriptor mock.
     */
    Descriptor descriptor;

    /**
     * Random name.
     */
	String randomName;

    /**
     * Random type.
     */
	String randomType;

    /**
     * Random name.
     */
	String randomName2;

    /**
     * Random type.
     */
	String randomType2;
	
    /**
     * Random creator.
     */
	String randomCreator;

    /**
     * Random destroyer.
     */
	String randomDestroyer;
	
    /**
     * MBeanAttributeInfo mock.
     */
    MBeanAttributeInfo attributeInfo;	
    
	@Before
	public void setUp() throws Exception {

		// create random names
		randomName = RandomStringUtils.randomAlphabetic(10);
		randomType = RandomStringUtils.randomAlphabetic(10);
		randomName2 = RandomStringUtils.randomAlphabetic(10);
		randomType2 = RandomStringUtils.randomAlphabetic(10);				
		randomCreator = "create" + RandomStringUtils.randomAlphabetic(10);		
		randomDestroyer = "destroy" + RandomStringUtils.randomAlphabetic(10);		
				
        // create mock 
        mbeanInfo = createMock( MBeanInfo.class );
        
        // create mock 
        attributeInfo = createMock( MBeanAttributeInfo.class );
        
        // create mock 
        descriptor = EasyMock.createMock( Descriptor.class );		        
	}

	@After
	public void tearDown() throws Exception {
		
		mbeanMetadata = null;
		descriptor = null;
	}

	void completeDescriptorMockInitialization() {
		EasyMock.expect(descriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD))
			.andReturn(randomCreator)
			.times(2);
		EasyMock.expect(descriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD))
			.andReturn(randomDestroyer);		
		EasyMock.replay(descriptor);		
	}

	
	void completeMBeanInfoMockInitialization(MBeanAttributeInfo[] attributes, MBeanOperationInfo[] operations ) {
		expect(mbeanInfo.getAttributes()).andReturn(attributes);
		expect(mbeanInfo.getOperations()).andReturn(operations);
		replay(mbeanInfo);
	}
	
	void completeAttributeInfoMockInitialization() {
		expect(attributeInfo.getDescriptor()).andReturn(descriptor);
		expect(attributeInfo.getName()).andReturn(randomName);
		expect(attributeInfo.getType()).andReturn(randomType);		
		replay(attributeInfo);
	}
		
	
	/**
	 * Can create meta data.
	 */
	@Test
	public void testCanCreateInstance() {	
		MBeanAttributeInfo[] attributes = {};	
		MBeanOperationInfo[] operations = {};
		
		// complete mock initializations
		EasyMock.replay(descriptor);		
		completeMBeanInfoMockInitialization(attributes, operations);
		
		// create meta data
		mbeanMetadata = new MBeanMetadataImpl(mbeanInfo);
				
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);
	}


	/**
	 * Can get attribute by name
	 */
	@Test
	public void testCanGetAttributeByName() {	
		MBeanAttributeInfo[] attributes = { attributeInfo};	
		MBeanOperationInfo[] operations = {};
	
		completeAttributeInfoMockInitialization();
		completeDescriptorMockInitialization();		
		completeMBeanInfoMockInitialization(attributes, operations);
		
		// create meta data
		mbeanMetadata = new MBeanMetadataImpl(mbeanInfo);
				
		// test
		AttributeMetadata attribute = mbeanMetadata.getAttribute(randomName);
		assertNotNull(attribute);
		assertNotNull(attribute.getInfo());
		assertEquals(randomName, attribute.getName());

		
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);
		verify(attributeInfo);
	}


	/**
	 * Can get attribute by name.
	 */
	@Test
	public void testCanGetAttributeByAlternateName() {	
		MBeanAttributeInfo[] attributes = { attributeInfo};	
		MBeanOperationInfo[] operations = {};
	
		completeAttributeInfoMockInitialization();
		completeDescriptorMockInitialization();		
		completeMBeanInfoMockInitialization(attributes, operations);
		
		// create meta data
		mbeanMetadata = new MBeanMetadataImpl(mbeanInfo);
				
		// compute alternate name
		String alternateName = StringUtils.substringAfter(randomCreator, "create"); 
				
		// test
		AttributeMetadata attribute = mbeanMetadata.getAttribute(alternateName);				
		assertNotNull(attribute);
		assertNotNull(attribute.getInfo());
		assertEquals(randomName, attribute.getName());		
		assertEquals(alternateName, attribute.getAlternateName());
		
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);
		verify(attributeInfo);
	}

	/**
	 * Can get attribute by name with lower case.
	 */
	@Test
	public void testCanGetAttributeByNameWithLowerCase() {	
		MBeanAttributeInfo[] attributes = { attributeInfo};	
		MBeanOperationInfo[] operations = {};
	
		completeAttributeInfoMockInitialization();
		completeDescriptorMockInitialization();		
		completeMBeanInfoMockInitialization(attributes, operations);
		
		// create meta data
		mbeanMetadata = new MBeanMetadataImpl(mbeanInfo);
				
		// lower case name
		String lowerCaseName = randomName.toLowerCase();
		
		// test
		AttributeMetadata attribute = mbeanMetadata.getAttribute(lowerCaseName );
		assertNotNull(attribute);
		assertNotNull(attribute.getInfo());
		assertEquals(randomName, attribute.getName());

		
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);
		verify(attributeInfo);
	}

	/**
	 * Can get attribute by name with lower case.
	 */
	@Test
	public void testCanGetAttributeByNameWithUpperCase() {	
		MBeanAttributeInfo[] attributes = { attributeInfo};	
		MBeanOperationInfo[] operations = {};
	
		completeAttributeInfoMockInitialization();
		completeDescriptorMockInitialization();		
		completeMBeanInfoMockInitialization(attributes, operations);
		
		// create meta data
		mbeanMetadata = new MBeanMetadataImpl(mbeanInfo);
				
		// upper case name
		String upperCaseName = randomName.toUpperCase();
		
		// test
		AttributeMetadata attribute = mbeanMetadata.getAttribute(upperCaseName );
		assertNotNull(attribute);
		assertNotNull(attribute.getInfo());
		assertEquals(randomName, attribute.getName());

		
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);
		verify(attributeInfo);
	}

	/**
	 * Can get attribute by name.
	 */
	@Test
	public void testCanGetAttributeByAlternateNameWithLowerCase() {	
		MBeanAttributeInfo[] attributes = { attributeInfo};	
		MBeanOperationInfo[] operations = {};
	
		completeAttributeInfoMockInitialization();
		completeDescriptorMockInitialization();		
		completeMBeanInfoMockInitialization(attributes, operations);
		
		// create meta data
		mbeanMetadata = new MBeanMetadataImpl(mbeanInfo);
				
		// compute alternate name with lower case name
		String alternateName = StringUtils.substringAfter(randomCreator, "create"); 
		String lowerCaseName = alternateName.toLowerCase();
		
		// test
		AttributeMetadata attribute = mbeanMetadata.getAttribute(lowerCaseName);				
		assertNotNull(attribute);
		assertNotNull(attribute.getInfo());
		assertEquals(randomName, attribute.getName());		
		assertEquals(alternateName, attribute.getAlternateName());
		
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);
		verify(attributeInfo);
	}

	/**
	 * Test that attribute doesn't overwrite alternate name already in use.
	 */
	@Test
	public void testAlternateNameDoesntOverwrite() {

        // create mock
        Descriptor descriptor2 = EasyMock.createMock( Descriptor.class );		        
		EasyMock.expect(descriptor2.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD))
			.andReturn(randomCreator)
			.times(2);
		EasyMock.expect(descriptor2.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD))
			.andReturn(randomDestroyer);		
		EasyMock.replay(descriptor2);		
		
		
        // create mock 
		MBeanAttributeInfo attributeInfo2 = createMock( MBeanAttributeInfo.class );
		expect(attributeInfo2.getDescriptor()).andReturn(descriptor2);
		expect(attributeInfo2.getName()).andReturn(randomName2);
		expect(attributeInfo2.getType()).andReturn(randomType2);		
		replay(attributeInfo2);
		
		MBeanAttributeInfo[] attributes = { attributeInfo, attributeInfo2};	
		MBeanOperationInfo[] operations = {};
	
		completeAttributeInfoMockInitialization();
		completeDescriptorMockInitialization();		
		completeMBeanInfoMockInitialization(attributes, operations);
		
		// create meta data
		mbeanMetadata = new MBeanMetadataImpl(mbeanInfo);
						
		// test
		AttributeMetadata attribute = mbeanMetadata.getAttribute(randomName);				
		assertNotNull(attribute);
		assertNotNull(attribute.getInfo());		
		assertEquals(randomName, attribute.getName());
		
		AttributeMetadata attribute2 = mbeanMetadata.getAttribute(randomName2);				
		assertNotNull(attribute2);
		assertNotNull(attribute2.getInfo());		
		assertEquals(randomName2, attribute2.getName());
		
		assertFalse(attribute.getName().equals(attribute2.getName()));		
		assertEquals(attribute.getAlternateName(), attribute2.getAlternateName());
		
		// only three entries since no alternate name was stored for the second attribute.
		assertEquals(3,mbeanMetadata.attributes.size());		
		
		
		// test
		verify(mbeanInfo);
		EasyMock.verify(descriptor);
		EasyMock.verify(descriptor2);		
		verify(attributeInfo);
		verify(attributeInfo2);
	}
	
	
}
