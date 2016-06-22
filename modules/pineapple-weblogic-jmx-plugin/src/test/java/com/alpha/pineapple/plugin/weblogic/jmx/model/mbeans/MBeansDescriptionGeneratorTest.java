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

import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.*;

import static com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.MBeansDescriptionGeneratorImpl.COLLECTION_NOTATION;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;
import com.alpha.pineapple.resolvedmodel.ResolvedCollection;
import com.alpha.pineapple.resolvedmodel.ResolvedObject;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedType;

/**
 * Unit test of the {@link MBeansDescriptionGeneratorImpl} class. 
 */
public class MBeansDescriptionGeneratorTest {

	/**
	 * Class under test.
	 */
	MBeansDescriptionGeneratorImpl visitor;
	
	/**
	 * Mock resolved type.
	 */
	ResolvedType resolvedType;

	/**
	 * Mock resolved object.
	 */
	ResolvedObject resolvedObject;

	/**
	 * Mock resolved collection.
	 */
	ResolvedCollection resolvedCollection;
	
	/**
	 * Mock execution result.
	 */	
	ExecutionResult executionResult;
	
    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;    
	
    /**
     * Random name.
     */
	String randomName;

    /**
     * Random name.
     */
	String randomName2;

    /**
     * Random name.
     */
	String randomName3;

	/**
     * Random type.
     */
	String randomType;

	@Before
	public void setUp() throws Exception {

		// create random names
		randomName = RandomStringUtils.randomAlphabetic(10);	
		randomName2= RandomStringUtils.randomAlphabetic(10);	
		randomName3= RandomStringUtils.randomAlphabetic(10);		
		randomType = RandomStringUtils.randomAlphabetic(10);
		
        // create execution result
        executionResult = EasyMock.createMock( ExecutionResult.class );
		
        // create resolved types 
        resolvedType = EasyMock.createMock( ResolvedType.class );
        resolvedObject = createMock( ResolvedObject.class );
        resolvedCollection = createMock( ResolvedCollection.class );
        		
		// create visitor
		visitor = new MBeansDescriptionGeneratorImpl();

        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
		
        // inject message source
        ReflectionTestUtils.setField( visitor, "messageProvider", messageProvider );
        
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
	}

	/**
	 * Test that correct name is generated for resolved type 
	 * where the type of the secondary participant is null.
	 */
	@Test
	public void testVisitResolvedTypeContainingNullType() {

		// complete mock object name
		ObjectName objName = createMock( ObjectName.class);
		replay(objName);
				
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getType()).andReturn(null);		
		EasyMock.expect(secondary.getValueAsSingleLineString()).andReturn(randomName2);
		EasyMock.replay(secondary);
				
		// complete mock resolved type setup
		EasyMock.expect(resolvedType.getSecondaryParticiant()).andReturn(secondary);
		EasyMock.replay(resolvedType); 
		
		// complete mock execution result setup
		EasyMock.replay(executionResult); 
		
		// invoke
		Object actual = visitor.visit(resolvedType, executionResult);
		
		// test
		assertNotNull(actual);
		assertTrue(actual instanceof String);
		
		String actualAsString = (String) actual;
		
		// test 
		assertEquals(randomName2, actualAsString);
		
		// Verify mocks	
		EasyMock.verify(executionResult);
		EasyMock.verify(resolvedType);
		EasyMock.verify(secondary);
		verify(objName);		
	}

	/**
	 * Test that correct name is generated for resolved object
	 * where the type of the secondary participant is null.
	 */
	@Test
	public void testVisitResolvedObjectContainingNullType() {
		
		// complete mock object name
		ObjectName objName = createMock( ObjectName.class);
		replay(objName);
				
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getType()).andReturn(null);		
		EasyMock.expect(secondary.getValueAsSingleLineString()).andReturn(randomName2);
		EasyMock.replay(secondary);
				
		// complete mock resolved type setup
		expect(resolvedObject.getSecondaryParticiant()).andReturn(secondary);
		replay(resolvedObject); 
		
		// complete mock execution result setup
		EasyMock.replay(executionResult); 
		
		// invoke
		Object actual = visitor.visit(resolvedObject, executionResult);
		
		// test
		assertNotNull(actual);
		assertTrue(actual instanceof String);
		
		String actualAsString = (String) actual;
		
		// test 
		assertEquals(randomName2, actualAsString);
		
		// Verify mocks	
		EasyMock.verify(executionResult);
		verify(resolvedObject);
		EasyMock.verify(secondary);
		verify(objName);		
	}
	
	
	/**
	 * Test that correct name is generated for resolved type 
	 * where the type of the secondary participant is ObjectName.
	 */
	@Test
	public void testVisitResolvedTypeContainingObjectNameType() {

		// complete mock object name
		ObjectName objName = createMock( ObjectName.class);
		expect(objName.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY)).andReturn(randomType);
		replay(objName);
				
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getName()).andReturn(randomName);		
		EasyMock.expect(secondary.getValue()).andReturn(objName);		
		EasyMock.expect(secondary.getType()).andReturn(ObjectName.class);		
		EasyMock.replay(secondary);
				
		// complete mock resolved type setup
		EasyMock.expect(resolvedType.getSecondaryParticiant()).andReturn(secondary);
		EasyMock.replay(resolvedType); 
		
		// complete mock execution result setup
		EasyMock.replay(executionResult); 
		
		// invoke
		Object actual = visitor.visit(resolvedType, executionResult);
		
		// test
		assertNotNull(actual);
		assertTrue(actual instanceof String);
		
		String actualAsString = (String) actual;
		
		// test 
		assertEquals(randomName+":"+randomType, actualAsString);
		
		// Verify mocks	
		EasyMock.verify(executionResult);
		EasyMock.verify(resolvedType);
		EasyMock.verify(secondary);
		verify(objName);		
	}
	
	/**
	 * Test that correct name is generated for resolved object 
	 * where the type of the secondary participant is ObjectName.
	 */
	@Test
	public void testVisitResolvedObjectContainingObjectNameType() {

		// complete mock object name
		ObjectName objName = createMock( ObjectName.class);
		expect(objName.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY)).andReturn(randomType);
		replay(objName);
				
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getName()).andReturn(randomName);		
		EasyMock.expect(secondary.getValue()).andReturn(objName);		
		EasyMock.expect(secondary.getType()).andReturn(ObjectName.class);		
		EasyMock.replay(secondary);
				
		// complete mock resolved type setup
		expect(resolvedObject.getSecondaryParticiant()).andReturn(secondary);
		replay(resolvedObject); 
		
		// complete mock execution result setup
		EasyMock.replay(executionResult); 
		
		// invoke
		Object actual = visitor.visit(resolvedObject, executionResult);
		
		// test
		assertNotNull(actual);
		assertTrue(actual instanceof String);
		
		String actualAsString = (String) actual;
		
		// test 
		assertEquals(randomName+":"+randomType, actualAsString);
		
		// Verify mocks	
		EasyMock.verify(executionResult);
		verify(resolvedObject);
		EasyMock.verify(secondary);
		verify(objName);		
	}
	
	
	/**
	 * Test that correct name is generated for resolved type
	 * where the type of the secondary participant is AttributeInfo
	 * which represents an object of type ObjectName 
	 */
	@Test
	public void testVisitResolvedTypeWithObjectNameTypeRepresentedByAttributeInfo() {
		
		// complete mock object name
		ObjectName objName = createMock( ObjectName.class);
		expect(objName.getKeyProperty(OBJNAME_TYPE_KEYPROPERTY)).andReturn(OBJECTNAME_TYPE);
		expect(objName.getKeyProperty(OBJNAME_NAME_KEYPROPERTY)).andReturn(randomName);
		replay(objName);
		
		// complete mock attribute info setup
		MBeanAttributeInfo attributeInfo = createMock( MBeanAttributeInfo.class);
		expect(attributeInfo.getType()).andReturn(OBJECTNAME_TYPE);		
		replay(attributeInfo);
		
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getValue()).andReturn(objName);		
		EasyMock.expect(secondary.getType()).andReturn(attributeInfo);		
		EasyMock.replay(secondary);
				
		// complete mock resolved type setup
		EasyMock.expect(resolvedType.getSecondaryParticiant()).andReturn(secondary);
		EasyMock.replay(resolvedType); 
		
		// complete mock execution result setup
		EasyMock.replay(executionResult); 
		
		// invoke
		Object actual = visitor.visit(resolvedType, executionResult);
		
		// test
		assertNotNull(actual);
		assertTrue(actual instanceof String);
		
		String actualAsString = (String) actual;
		
		// test 
		assertEquals(randomName+":"+OBJECTNAME_TYPE, actualAsString);
		
		// Verify mocks	
		verify(attributeInfo);
		EasyMock.verify(executionResult);
		EasyMock.verify(resolvedType);
		EasyMock.verify(secondary);
		verify(objName);		
	}
	
	/**
	 * Test that correct name is generated for resolved type
	 * where the type of the secondary participant is AttributeInfo
	 * which represents an object of type ObjectName 
	 */
	@Test
	public void testVisitResolvedObjectWithObjectNameTypeRepresentedByAttributeInfo() {
		
		// complete mock object name
		ObjectName objName = createMock( ObjectName.class);
		expect(objName.getKeyProperty(OBJNAME_TYPE_KEYPROPERTY)).andReturn(OBJECTNAME_TYPE);
		expect(objName.getKeyProperty(OBJNAME_NAME_KEYPROPERTY)).andReturn(randomName);
		replay(objName);
		
		// complete mock attribute info setup
		MBeanAttributeInfo attributeInfo = createMock( MBeanAttributeInfo.class);
		expect(attributeInfo.getType()).andReturn(OBJECTNAME_TYPE);		
		replay(attributeInfo);
		
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getValue()).andReturn(objName);		
		EasyMock.expect(secondary.getType()).andReturn(attributeInfo);		
		EasyMock.replay(secondary);
				
		// complete mock resolved type setup
		expect(resolvedObject.getSecondaryParticiant()).andReturn(secondary);
		replay(resolvedObject); 
		
		// complete mock execution result setup
		EasyMock.replay(executionResult); 
		
		// invoke
		Object actual = visitor.visit(resolvedObject, executionResult);
		
		// test
		assertNotNull(actual);
		assertTrue(actual instanceof String);
		
		String actualAsString = (String) actual;
		
		// test 
		assertEquals(randomName+":"+OBJECTNAME_TYPE, actualAsString);
		
		// Verify mocks	
		verify(attributeInfo);
		EasyMock.verify(executionResult);
		verify(resolvedObject);
		EasyMock.verify(secondary);
		verify(objName);		
	}

		
	/**
	 * Test that correct name is generated for resolved type 
	 * where the type of the secondary participant is AttributeInfo
	 * which represents an object of type ObjectName array.
	 */
	@Test
	public void testVisitResolvedTypeWithObjectNameTypeArrayRepresentedByAttributeInfo() {
		
		// complete mock object name
		ObjectName objName = createMock( ObjectName.class);
		replay(objName);
		
		// complete mock attribute info setup
		MBeanAttributeInfo attributeInfo = createMock( MBeanAttributeInfo.class);
		expect(attributeInfo.getType()).andReturn(OBJECTNAME_ARRAY_TYPE).times(2);		
		replay(attributeInfo);
		
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getName()).andReturn(randomName);
		EasyMock.expect(secondary.getType()).andReturn(attributeInfo);		
		EasyMock.replay(secondary);
				
		// complete mock resolved type setup
		EasyMock.expect(resolvedType.getSecondaryParticiant())
			.andReturn(secondary)
			.times(2);
		EasyMock.replay(resolvedType); 
		
		// complete mock execution result setup
		EasyMock.replay(executionResult); 
		
		// invoke
		Object actual = visitor.visit(resolvedType, executionResult);
		
		// test
		assertNotNull(actual);
		assertTrue(actual instanceof String);
		
		String actualAsString = (String) actual;
		
		// test 
		assertEquals(randomName+COLLECTION_NOTATION, actualAsString);
		
		// Verify mocks	
		verify(attributeInfo);		
		EasyMock.verify(executionResult);
		EasyMock.verify(resolvedType);
		EasyMock.verify(secondary);
		verify(objName);		
	}

	/**
	 * Test that correct name is generated for resolved collection
	 * where the type of the secondary participant is AttributeInfo
	 * which represents an object of type ObjectName array.
	 */
	@Test
	public void testVisitResolvedCollectionWithObjectNameTypeArrayRepresentedByAttributeInfo() {
		
		// complete mock object name
		ObjectName objName = createMock( ObjectName.class);
		replay(objName);
		
		// complete mock attribute info setup
		MBeanAttributeInfo attributeInfo = createMock( MBeanAttributeInfo.class);
		replay(attributeInfo);
		
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		expect(secondary.getName()).andReturn(randomName);
		EasyMock.replay(secondary);
				
		// complete mock resolved type setup
		expect(resolvedCollection.getSecondaryParticiant()).andReturn(secondary);
		replay(resolvedCollection); 
		
		// complete mock execution result setup
		EasyMock.replay(executionResult); 
		
		// invoke
		Object actual = visitor.visit(resolvedCollection, executionResult);
		
		// test
		assertNotNull(actual);
		assertTrue(actual instanceof String);
		
		String actualAsString = (String) actual;
		
		// test 
		assertEquals(randomName+COLLECTION_NOTATION, actualAsString);
		
		// Verify mocks	
		verify(attributeInfo);		
		EasyMock.verify(executionResult);
		verify(resolvedCollection);
		EasyMock.verify(secondary);
		verify(objName);		
	}
	
}
