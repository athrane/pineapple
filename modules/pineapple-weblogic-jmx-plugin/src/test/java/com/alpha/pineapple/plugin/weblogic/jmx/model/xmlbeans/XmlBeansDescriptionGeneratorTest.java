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


package com.alpha.pineapple.plugin.weblogic.jmx.model.xmlbeans;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.namespace.QName;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.xmlbeans.SchemaProperty;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.resolvedmodel.ResolvedCollection;
import com.alpha.pineapple.resolvedmodel.ResolvedEnum;
import com.alpha.pineapple.resolvedmodel.ResolvedObject;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedPrimitive;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.UnresolvedType;
import com.alpha.pineapple.resolvedmodel.traversal.ResolvedModelVisitor;

/**
 * Unit test of the class <code>XmlBeansDescriptionGeneratorImpl</code>.
 */
public class XmlBeansDescriptionGeneratorTest {

    /**
     * Object under test.
     */
    ResolvedModelVisitor generator;
    
    /**
     * Mock Schema property
     */
	SchemaProperty schemaProperty;

    /**
     * Mock primary participant
     */	
	ResolvedParticipant primary;

    /**
     * Mock secondary participant
     */	
	ResolvedParticipant secondary;
	
	/**
	 * Random name.
	 */
	String randomName;

	/**
	 * Random value.
	 */
	String randomValue;

	/**
	 * Random name.
	 */
	String randomName2;

	/**
	 * Random value.
	 */
	String randomValue2;
	
	@Before
	public void setUp() throws Exception {
        randomName = RandomStringUtils.randomAlphabetic(10);
        randomName2 = RandomStringUtils.randomAlphabetic(10);
        randomValue = RandomStringUtils.randomAlphabetic(10);        
        randomValue2 = RandomStringUtils.randomAlphabetic(10);
		
        // create generator
        generator = new XmlBeansDescriptionGeneratorImpl();
        
	}

	@After
	public void tearDown() throws Exception {
		generator = null;
		schemaProperty = null;
		primary = null;
	}

    /**
     * Complete schema property setup
     */	
	void completeMockSchemaPropertySetup(QName schemapropertyName) {
		schemaProperty = EasyMock.createMock( SchemaProperty .class);
		EasyMock.expect( schemaProperty.getName()).andReturn(schemapropertyName);
		EasyMock.replay(schemaProperty);
	}

    /**
     * Complete primary participant setup
     */		
	void completeMockPrimaryParticipantForObjects(String name) {
		primary = EasyMock.createMock( ResolvedParticipant.class);	
		EasyMock.expect( primary.getName()).andReturn( name );		
		EasyMock.expect( primary.getType()).andReturn( schemaProperty ).times(2);
		EasyMock.replay(primary );		
	}

    /**
     * Complete primary participant setup
     */		
	void completeMockPrimaryParticipantForPrimitive(String name, String value) {
		primary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect( primary.getName()).andReturn( name );
		EasyMock.expect( primary.getValue()).andReturn( value );				
		EasyMock.replay(primary );		
	}

    /**
     * Complete primary participant setup
     */		
	void completeMockPrimaryParticipantForPrimitive(String name, String value, Object type) {
		primary = EasyMock.createMock( ResolvedParticipant.class);	
		EasyMock.expect( primary.getName()).andReturn( name );
		EasyMock.expect( primary.getValue()).andReturn( value );
		EasyMock.replay(primary );		
	}
	
    /**
     * Complete secondary participant setup
     */		
	void completeMockSecondaryParticipantForPrimitive(String name, String value) {
		secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.replay(secondary);		
	}

    /**
     * Complete secondary participant setup
     */		
	void completeMockSecondaryParticipantForObjects(String name) {
		secondary = EasyMock.createMock( ResolvedParticipant.class);	
		EasyMock.replay(secondary );		
	}

    /**
     * Complete secondary participant setup
     */		
	void completeMockSecondaryParticipantForPrimitive(String name, String value, Object type) {
		secondary = EasyMock.createMock( ResolvedParticipant.class);	
		EasyMock.replay(secondary);		
	}
	
	
	@Test
	public void testVisitResolvedCollection() {				
		QName schemapropertyName = new QName("localPart.value");
		String expected = randomName+":"+schemapropertyName.getLocalPart()+"[]";
		
		// complete mock setups
		completeMockSchemaPropertySetup(schemapropertyName);		
		completeMockPrimaryParticipantForObjects(randomName);
		completeMockSecondaryParticipantForObjects(randomName2);
						
		ResolvedCollection resolved = createMock( ResolvedCollection.class );	
		EasyMock.expect( resolved.getPrimaryParticipant() ).andReturn( primary );
		replay( resolved );
		
		// visit
		Object description = generator.visit(resolved, null);
		
		// test
		assertNotNull(description);		
		assertEquals(String.class, description.getClass() );
		assertEquals(expected, (String) description );
	
		// verify mock objects
		EasyMock.verify( schemaProperty );		
		verify( resolved ); 	
	    EasyMock.verify( primary );
	    EasyMock.verify( secondary );
	}


	@Test
	public void testVisitResolvedEnum() {
		String expected = randomName+"="+randomValue;
		
		// complete mock setups		
		completeMockPrimaryParticipantForPrimitive(randomName, randomValue);
		completeMockSecondaryParticipantForPrimitive(randomName2, randomValue2);
						
		ResolvedEnum resolved = createMock( ResolvedEnum.class );	
		EasyMock.expect( resolved.getPrimaryParticipant() ).andReturn( primary  );
		replay( resolved );
		
		// visit
		Object description = generator.visit(resolved, null);
		
		// test
		assertNotNull(description);		
		assertEquals(String.class, description.getClass() );
		assertEquals(expected, (String) description );
	
		// verify mock objects		
		verify( resolved ); 	
	    EasyMock.verify( primary );
	    EasyMock.verify( secondary );	    
	}

	@Test
	public void testVisitResolvedObject() {		
		QName schemapropertyName = new QName("localPart.value");
		String expected = randomName+":"+schemapropertyName.getLocalPart();
		
		// complete mock setups
		completeMockSchemaPropertySetup(schemapropertyName);		
		completeMockPrimaryParticipantForObjects(randomName);
		completeMockSecondaryParticipantForObjects(randomName2);
						
		ResolvedObject resolved = createMock( ResolvedObject.class );	
		EasyMock.expect( resolved.getPrimaryParticipant() ).andReturn( primary );
		replay( resolved );
		
		// visit
		Object description = generator.visit(resolved, null);
		
		// test
		assertNotNull(description);		
		assertEquals(String.class, description.getClass() );
		assertEquals(expected, (String) description );
	
		// verify mock objects
		EasyMock.verify( schemaProperty );		
		verify( resolved ); 	
	    EasyMock.verify( primary );
	    EasyMock.verify( secondary );
	}

	@Test
	public void testVisitResolvedPrimitive() {		
		String expected = randomName+"="+randomValue;
		
		// complete mock setups
		completeMockPrimaryParticipantForPrimitive(randomName,randomValue);
		completeMockSecondaryParticipantForPrimitive(randomName2,randomValue2);
						
		ResolvedPrimitive resolved = createMock( ResolvedPrimitive.class );	
		EasyMock.expect( resolved.getPrimaryParticipant() ).andReturn( primary );
		replay( resolved );
		
		// visit
		Object description = generator.visit(resolved, null);
		
		// test
		assertNotNull(description);		
		assertEquals(String.class, description.getClass() );
		assertEquals(expected, (String) description );
	
		// verify mock objects		
		verify( resolved ); 	
	    EasyMock.verify( primary );
	    EasyMock.verify( secondary );
	}

	@Test
	public void testVisitResolvedType() {
		QName schemapropertyName = new QName("localPart.value");
		String expected = randomName+":"+schemapropertyName.getLocalPart();
		
		// complete mock setups
		completeMockSchemaPropertySetup(schemapropertyName);		
		completeMockPrimaryParticipantForObjects(randomName);
		completeMockSecondaryParticipantForObjects(randomName2);
						
		ResolvedType resolved = createMock( ResolvedType.class );	
		EasyMock.expect( resolved.getPrimaryParticipant() ).andReturn( primary  );
		replay( resolved );
		
		// visit
		Object description = generator.visit(resolved, null);
		
		// test
		assertNotNull(description);		
		assertEquals(String.class, description.getClass() );
		assertEquals(expected, (String) description );
	
		// verify mock objects
		EasyMock.verify( schemaProperty );		
		verify( resolved ); 	
	    EasyMock.verify( primary );
	    EasyMock.verify( secondary);
	}

	@Test
	public void testVisitUnresolvedType() {
		QName schemapropertyName = new QName("localPart.value");
		String expected = randomName+":"+schemapropertyName.getLocalPart();
		
		// complete mock setups
		completeMockSchemaPropertySetup(schemapropertyName);		
		completeMockPrimaryParticipantForObjects(randomName);
		completeMockSecondaryParticipantForObjects(randomName2);
						
		UnresolvedType resolved = createMock( UnresolvedType .class );	
		EasyMock.expect( resolved.getPrimaryParticipant() ).andReturn( primary );
		replay( resolved );
		
		// visit
		Object description = generator.visit(resolved, null);
		
		// test
		assertNotNull(description);		
		assertEquals(String.class, description.getClass() );
		assertEquals(expected, (String) description );
	
		// verify mock objects
		EasyMock.verify( schemaProperty );		
		verify( resolved ); 	
	    EasyMock.verify( primary );
	    EasyMock.verify( secondary );
	}

	/**
	 * Failed participant contains a name, and null value and null type.
	 */
	@Test
	public void testVisitResolvedEnumWithResolutionFailed() { 		
		String value = null;
		Object type = null;
		String expected = randomName+"=null";
		
		// complete mock setups				
		completeMockPrimaryParticipantForPrimitive(randomName, value, type );
		completeMockSecondaryParticipantForPrimitive(randomName2, value, type );
								
		ResolvedEnum resolved = createMock( ResolvedEnum.class );	
		EasyMock.expect( resolved.getPrimaryParticipant() ).andReturn( primary );
		replay( resolved );
				
		// visit
		Object description = generator.visit(resolved, null);
		
		// test
		assertNotNull(description);		
		assertEquals(String.class, description.getClass() );
		assertEquals(expected, (String) description );
	
		// verify mock objects	
		verify( resolved ); 	
	    EasyMock.verify( primary );
	    EasyMock.verify( secondary );
	}

	/**
	 * Failed participant contains a name, and null value and null type.
	 */
	@Test
	public void testVisitResolvedPrimitiveWithResolutionFailed() { 		
		String value = null;
		Object type = null;
		String expected = randomName+"=null";
		
		// complete mock setups				
		completeMockPrimaryParticipantForPrimitive(randomName, value, type );
		completeMockSecondaryParticipantForPrimitive(randomName2, value, type );			
						
		ResolvedPrimitive resolved = createMock( ResolvedPrimitive.class );	
		EasyMock.expect( resolved.getPrimaryParticipant() ).andReturn( primary );
		replay( resolved );
		
		// visit
		Object description = generator.visit(resolved, null);
		
		// test
		assertNotNull(description);		
		assertEquals(String.class, description.getClass() );
		assertEquals(expected, (String) description );
	
		// verify mock objects	
		verify( resolved ); 	
	    EasyMock.verify( primary );
	    EasyMock.verify( secondary );	    
	}
	
}
