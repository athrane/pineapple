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

import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;

/**
 * Unit test of the AttributeMetadata class.
 */
public class AttributeMetadataTest {

	/**
	 * Empty string.
	 */
    static final String EMPTY_STRING = "";

	/**
     * Object under test.
     */
	AttributeMetadataImpl attributeMetadata;
	
    /**
     * MBeanAttributeInfo mock.
     */
    MBeanAttributeInfo attributeInfo;
	
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
     * Random creator.
     */
	String randomCreator;

    /**
     * Random destroyer.
     */
	String randomDestroyer;

    /**
     * Random text.
     */
	String randomText;

    /**
     * Random text.
     */
	String randomText2;

    /**
     * Random text.
     */
	String randomText3;
	
	@Before
	public void setUp() throws Exception {

		// create random names
		randomName = RandomStringUtils.randomAlphabetic(10);
		randomType = RandomStringUtils.randomAlphabetic(10);		
		randomCreator = "create" + RandomStringUtils.randomAlphabetic(10);		
		randomDestroyer = "destroy" + RandomStringUtils.randomAlphabetic(10);
		randomText = RandomStringUtils.randomAlphabetic(10);
		randomText2 = RandomStringUtils.randomAlphabetic(10);
		randomText3 = RandomStringUtils.randomAlphabetic(10);
		
        // create mock 
        attributeInfo = createMock( MBeanAttributeInfo.class );
        
        // create mock 
        descriptor = EasyMock.createMock( Descriptor.class );		        
	}

	@After
	public void tearDown() throws Exception {
		
		attributeMetadata = null;
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

	void completeDescriptorMockInitializationWithNoCreatorAndDestroyer() {
		EasyMock.expect(descriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD))
			.andReturn(null)
			.times(2);
		EasyMock.expect(descriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD))
			.andReturn(null);				
		EasyMock.expect(descriptor.getFieldValue(WebLogicMBeanConstants.INTERFACE_DESCRIPTOR_FIELD))
			.andReturn(null);		
		EasyMock.replay(descriptor);
	}

	void completeDescriptorMockInitializationWithNoCreatorAndDestroyerButWithInfClass() {
		EasyMock.expect(descriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD))
			.andReturn(null)
			.times(2);
		EasyMock.expect(descriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD))
			.andReturn(null);				
		EasyMock.expect(descriptor.getFieldValue(WebLogicMBeanConstants.INTERFACE_DESCRIPTOR_FIELD))
			.andReturn(randomText+"."+randomText2+"."+randomText3+"MBean");		
		EasyMock.replay(descriptor);
	}
	
	
	void completeAttributeInfoMockInitialization() {
		expect(attributeInfo.getDescriptor()).andReturn(descriptor);
		expect(attributeInfo.getName()).andReturn(randomName);
		expect(attributeInfo.getType()).andReturn(randomType);		
		replay(attributeInfo);
	}
	
	/**
	 * Can create attribute meta data.
	 */
	@Test
	public void testCanCreateInstance() {		
		completeDescriptorMockInitialization();		
		completeAttributeInfoMockInitialization();
		
		// create meta data
		attributeMetadata = new AttributeMetadataImpl(attributeInfo);
				
		// test
		verify(attributeInfo);
		EasyMock.verify(descriptor);
	}


	/**
	 * Test that attribute meta data returns correct name.
	 */
	@Test
	public void testName() {
		completeDescriptorMockInitialization();		
		completeAttributeInfoMockInitialization();
	
		// create meta data
		attributeMetadata = new AttributeMetadataImpl(attributeInfo);

		// test 
		assertEquals(randomName, attributeMetadata.getName());
		
		// test
		verify(attributeInfo);
		EasyMock.verify(descriptor);
	}

	/**
	 * Test that attribute meta data returns correct create method.
	 */
	@Test
	public void testCreatorMethod() {
		completeDescriptorMockInitialization();		
		completeAttributeInfoMockInitialization();
	
		// create meta data
		attributeMetadata = new AttributeMetadataImpl(attributeInfo);

		// test 
		assertEquals(randomCreator, attributeMetadata.getCreateMethod());
		
		// test
		verify(attributeInfo);
		EasyMock.verify(descriptor);
	}

	/**
	 * Test that attribute meta data returns correct destroy method.
	 */
	@Test
	public void testDestroyerMethod() {
		completeDescriptorMockInitialization();		
		completeAttributeInfoMockInitialization();
	
		// create meta data
		attributeMetadata = new AttributeMetadataImpl(attributeInfo);

		// test 
		assertEquals(randomDestroyer, attributeMetadata.getDestroyMethod());
		
		// test
		verify(attributeInfo);
		EasyMock.verify(descriptor);
	}

	/**
	 * Test that attribute meta data returns correct type.
	 */
	@Test
	public void testType() {
		completeDescriptorMockInitialization();		
		completeAttributeInfoMockInitialization();
	
		// create meta data
		attributeMetadata = new AttributeMetadataImpl(attributeInfo);

		// test 
		assertEquals(randomType, attributeMetadata.getType());
		
		// test
		verify(attributeInfo);
		EasyMock.verify(descriptor);
	}

	/**
	 * Test that attribute meta data returns correct alternate name.
	 */
	@Test
	public void testAlternateName() {
		completeDescriptorMockInitialization();		
		completeAttributeInfoMockInitialization();
	
		// create meta data
		attributeMetadata = new AttributeMetadataImpl(attributeInfo);

		// 
		String alternateName = StringUtils.substringAfter(randomCreator, "create");						
		
		// test 
		assertEquals(alternateName, attributeMetadata.getAlternateName());
		
		// test
		verify(attributeInfo);
		EasyMock.verify(descriptor);
	}

	/**
	 * Test that attribute meta data returns correct MBeanInfo.
	 */
	@Test
	public void testGetMBeanInfo() {
		completeDescriptorMockInitialization();		
		completeAttributeInfoMockInitialization();
	
		// create meta data
		attributeMetadata = new AttributeMetadataImpl(attributeInfo);

		// test 
		assertEquals(attributeInfo, attributeMetadata.getInfo());
		
		// test
		verify(attributeInfo);
		EasyMock.verify(descriptor);
	}

	/**
	 * Can create attribute meta data with no creator and destroyer info
	 */
	@Test
	public void testCanCreateInstanceWithNoCreatorAndDestroyerInfo() {		
		completeDescriptorMockInitializationWithNoCreatorAndDestroyer();		
		completeAttributeInfoMockInitialization();
		
		// create meta data
		attributeMetadata = new AttributeMetadataImpl(attributeInfo);
				
		// test
		verify(attributeInfo);
		EasyMock.verify(descriptor);
	}

	/**
	 * Test that attribute meta data returns correct create method
	 * for attribute with no creator/destroyer method.
	 */
	@Test
	public void testCreatorMethodForAttributeWithNoCreatorAndDestroyerInfo() {
		completeDescriptorMockInitializationWithNoCreatorAndDestroyer();		
		completeAttributeInfoMockInitialization();
	
		// create meta data
		attributeMetadata = new AttributeMetadataImpl(attributeInfo);

		// test 
		assertEquals(EMPTY_STRING, attributeMetadata.getCreateMethod());
		
		// test
		verify(attributeInfo);
		EasyMock.verify(descriptor);
	}

	/**
	 * Test that attribute meta data returns correct create method
	 * for attribute with no creator/destroyer method.
	 */
	@Test
	public void testDestroyerMethodForAttributeWithNoCreatorAndDestroyerInfo() {
		completeDescriptorMockInitializationWithNoCreatorAndDestroyer();		
		completeAttributeInfoMockInitialization();
	
		// create meta data
		attributeMetadata = new AttributeMetadataImpl(attributeInfo);

		// test 
		assertEquals(EMPTY_STRING, attributeMetadata.getDestroyMethod());
		
		// test
		verify(attributeInfo);
		EasyMock.verify(descriptor);
	}

	/**
	 * Test that attribute meta data returns correct alternate name
	 * for attribute with no creator/destroyer method. 
	 */
	@Test
	public void testAlternateNameForAttributeWithNoCreatorAndDestroyerInfo() {
		completeDescriptorMockInitializationWithNoCreatorAndDestroyer();		
		completeAttributeInfoMockInitialization();
	
		// create meta data
		attributeMetadata = new AttributeMetadataImpl(attributeInfo);

		// test 
		assertEquals(EMPTY_STRING, attributeMetadata.getAlternateName());
		
		// test
		verify(attributeInfo);
		EasyMock.verify(descriptor);
	}

	/**
	 * Test that attribute meta data returns correct alternate name
	 * for attribute with no creator/destroyer method
	 * but with a interface class name. 
	 */
	@Test
	public void testAlternateNameForAttributeWithNoCreatorInfoButWithInfClass() {
		completeDescriptorMockInitializationWithNoCreatorAndDestroyerButWithInfClass();		
		completeAttributeInfoMockInitialization();
	
		// create meta data
		attributeMetadata = new AttributeMetadataImpl(attributeInfo);

		// test 
		assertEquals(randomText3, attributeMetadata.getAlternateName());
		
		// test
		verify(attributeInfo);
		EasyMock.verify(descriptor);
	}
	
	
}
