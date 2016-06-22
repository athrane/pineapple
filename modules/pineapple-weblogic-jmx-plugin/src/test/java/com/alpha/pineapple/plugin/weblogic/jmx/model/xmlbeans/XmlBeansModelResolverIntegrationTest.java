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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolver;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.WJPIntTestConstants;
import com.oracle.xmlns.weblogic.domain.DomainDocument;
import com.oracle.xmlns.weblogic.domain.DomainType;
import com.oracle.xmlns.weblogic.domain.ServerType;

/**
 * Integration test for <code>XmlBeansModelResolverImpl</code>.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class XmlBeansModelResolverIntegrationTest
{

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	
    /**
     * Object mother for the WebLogic domain model.
     */
    ObjectMotherContent contentMother;

    /**
     * Object under test.
     */
    @Resource( name = "xmlBeansModelResolver" )
    ModelResolver resolver;

    @Before
    public void setUp() throws Exception
    {
        // create content mother
        contentMother = new ObjectMotherContent();
    }

    @After
    public void tearDown() throws Exception
    {
        contentMother = null;
    }

    
    /**
     * Get schema property for attribute from XMLBean.
     * 
     * @param attributeName Name of the attribute.
     * @param xmlBean XMLBean which contains attribute.
     * @return schema property for attribute defined on XMLBean.
     */
	public SchemaProperty getSchemaPropertyByName(String attributeName, XmlObject xmlBean) {
		
        // get schema type
        SchemaType schemaType = xmlBean.schemaType();
				
        // get schema properties (elements and attributes)
        SchemaProperty[] schemaProperties = schemaType.getProperties();

        // iterate to get schema property
        for ( SchemaProperty property : schemaProperties )
        {
            if ( property.getJavaPropertyName().equals( attributeName ) )
            {
                return property;
            }
        }

        return null;
	}
    
    /**
     * Test that object can be looked up from the context.
     */
    @Test
    public void testCanGetResolverFromContext()
    {
        assertNotNull( resolver );
    }

    /**
     * Test that attribute names can be resolve from domain type.
     */
    @Test
    public void testCanResolveAttributeNamesFromDomain()
    {
        try
        {

            // create domain instance
            DomainDocument domainDoc = contentMother.createEmptyDomain();
            DomainType domain = domainDoc.addNewDomain();

            // get schema property
            SchemaProperty schemaProperty = domainDoc.schemaType().getProperties()[0];

            ResolvedParticipant attribute;
			attribute = ResolvedParticipantImpl.createSuccessfulResult( "some-id", schemaProperty, domain );

            // resolve
            String[] result = resolver.resolveAttributeNames( attribute );

            // test
            assertNotNull( result );

        }
        catch ( ModelResolutionFailedException e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }

    /**
     * Test attribute set contain correct number of attributes.
     */
    @Test
    public void testResolvedCorrectNumberOfAttributesFromDomain()
    {
        try
        {

            // create domain instance
            DomainDocument domainDoc = contentMother.createEmptyDomain();
            DomainType domain = domainDoc.addNewDomain();

            // get schema property
            SchemaProperty schemaProperty = domainDoc.schemaType().getProperties()[0];

            ResolvedParticipant attribute =
                ResolvedParticipantImpl.createSuccessfulResult( "some-id", schemaProperty, domain );

            // resolve
            String[] result = resolver.resolveAttributeNames( attribute );

            // test
            assertEquals( WJPIntTestConstants.numberOf1035SchemaProperties, result.length );

        }
        catch ( ModelResolutionFailedException e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }
    
    /**
     * Test that primitive attribute can be resolve from domain type.
     */
    @Test
    public void testCanResolveAttributeWhichIsDefinedPrimitive()
    {
    	// declare values
        String adminServerName = "some-name";
		String attributeName = "AdminServerName";
    	
        // create domain instance
        DomainDocument domainDoc = contentMother.createEmptyDomain();
        DomainType domain = domainDoc.addNewDomain();
        
        // set attribute
		domain.setAdminServerName(adminServerName);

		// create resolve participant for domain
		SchemaProperty domainSchemaProperty = getSchemaPropertyByName("Domain", domainDoc);		
		ResolvedParticipant domainParticipant = ResolvedParticipantImpl.createSuccessfulResult("domain", domainSchemaProperty , domain);		
		
        // resolve
		ResolvedParticipant attribute;
		attribute = resolver.resolveAttribute(attributeName, domainParticipant);
		
		System.out.println(attribute.getResolutionErrorAsSingleLineString());		

        // test
        assertNotNull( attribute );
        assertTrue(attribute.isResolutionSuccesful());        
        assertEquals(attributeName, attribute.getName());
        assertEquals(adminServerName, attribute.getValue());        
        assertTrue(attribute.getType() instanceof SchemaProperty);        
        assertEquals(null, attribute.getResolutionError());            
        assertEquals(ResolvedParticipant.ValueState.SET, attribute.getValueState());        
    }    

    /**
     * Test that primitive attribute can be resolve from domain type.
     */
    @Test
    public void testCanResolveAttributeWhichIsUndefinedPrimitive()
    {
    	// declare values
		String attributeName = "AdminServerName";
    	
        // create domain instance
        DomainDocument domainDoc = contentMother.createEmptyDomain();
        DomainType domain = domainDoc.addNewDomain();
        
        // dont set attribute 

		// create resolve participant for domain
		SchemaProperty domainSchemaProperty = getSchemaPropertyByName("Domain", domainDoc);		
		ResolvedParticipant domainParticipant = ResolvedParticipantImpl.createSuccessfulResult("domain", domainSchemaProperty , domain);		
        
        // resolve
		ResolvedParticipant attribute;
		attribute = resolver.resolveAttribute(attributeName, domainParticipant);

        // test
        assertNotNull( attribute );
        assertTrue(attribute.isResolutionSuccesful());        
        assertEquals(attributeName, attribute.getName());
        assertEquals(null, attribute.getValue());        
        assertTrue(attribute.getType() instanceof SchemaProperty);        
        assertEquals(null, attribute.getResolutionError());            
        assertEquals(ResolvedParticipant.ValueState.FAILED, attribute.getValueState());        
    }    
    
    
    /**
     * Test that array attribute can be resolve from domain type,
     * which contains zero entries.
     */
    @Test
    public void testCanResolveAttributeWhichIsUndefinedArrayWithZeroEntries()
    {
    	// declare values
		String attributeName = "Server";
    	
        // create domain instance
        DomainDocument domainDoc = contentMother.createEmptyDomain();
        DomainType domain = domainDoc.addNewDomain();
        
        // set attribute
        // no attributes 

		// create resolve participant for domain
		SchemaProperty domainSchemaProperty = getSchemaPropertyByName("Domain", domainDoc);		
		ResolvedParticipant domainParticipant = ResolvedParticipantImpl.createSuccessfulResult("domain", domainSchemaProperty , domain);		
        
        // resolve
		ResolvedParticipant attribute;
		attribute = resolver.resolveAttribute(attributeName, domainParticipant);
		
        // test 
        assertNotNull( attribute );
        assertTrue(attribute.isResolutionSuccesful());                
        assertEquals(attributeName, attribute.getName());    
        assertEquals(null, attribute.getValue() );                
        assertTrue(attribute.getType() instanceof SchemaProperty);         
        assertEquals(null, attribute.getResolutionError());    
        assertEquals(ResolvedParticipant.ValueState.NIL, attribute.getValueState());        
    }    

    /**
     * Test that empty array attribute can be resolve from domain type to correct type.
     */
    @Test
    public void testUndefinedCollectionWithZeroEntriesIsResolvedToCorrectType()
    {
    	// declare values
		String attributeName = "Server";
    	
        // create domain instance
        DomainDocument domainDoc = contentMother.createEmptyDomain();
        DomainType domain = domainDoc.addNewDomain();
        
        // set attribute
        // no attributes 

		// create resolve participant for domain
		SchemaProperty domainSchemaProperty = getSchemaPropertyByName("Domain", domainDoc);		
		ResolvedParticipant domainParticipant = ResolvedParticipantImpl.createSuccessfulResult("domain", domainSchemaProperty , domain);		
        
        // resolve
		ResolvedParticipant attribute;
		attribute = resolver.resolveAttribute(attributeName, domainParticipant);
		
        // test - the basics 
        assertNotNull( attribute );
        assertTrue(attribute.isResolutionSuccesful());                
        assertEquals(attributeName, attribute.getName());    
        assertEquals(null, attribute.getValue() );                
        assertTrue(attribute.getType() instanceof SchemaProperty);         
        assertEquals(null, attribute.getResolutionError());    
        assertEquals(ResolvedParticipant.ValueState.NIL, attribute.getValueState());                
    }        
    
    
    
    /**
     * Test that array attribute can be resolve from domain type,
     * which contains single entry
     */
    @Test
    public void testCanResolveCollectionWhichIsDefinedArrayWithOneEntry()
    {
    	// declare values
		String attributeName = "Server";
    	
        // create domain instance
        DomainDocument domainDoc = contentMother.createEmptyDomain();
        DomainType domain = domainDoc.addNewDomain();
        
        // set attribute
		ServerType server = domain.addNewServer();
		server.setName("server-name");

		// create resolve participant for domain
		SchemaProperty domainSchemaProperty = getSchemaPropertyByName("Domain", domainDoc);		
		ResolvedParticipant domainParticipant = ResolvedParticipantImpl.createSuccessfulResult("domain", domainSchemaProperty , domain);		
		
        // resolve
		ResolvedParticipant attribute;
		attribute = resolver.resolveAttribute(attributeName, domainParticipant);
		
        // test
		Object value = attribute.getValue(); 
        assertNotNull( attribute );
        assertTrue(attribute.isResolutionSuccesful());                
        assertEquals(attributeName, attribute.getName());
        assertTrue(value instanceof ServerType[] );        
        assertEquals(1, ((ServerType[]) value).length );        
        assertTrue(attribute.getType() instanceof SchemaProperty);        
        assertEquals(null, attribute.getResolutionError());    
        assertEquals(ResolvedParticipant.ValueState.SET, attribute.getValueState());        
    }    

    /**
     * Test that array attribute can be resolve from domain type,
     * which contains two entries.
     */
    @Test
    public void testCanResolveAttributeWhichIsDefinedArrayWithTwoEntries()
    {
    	// declare values
		String attributeName = "Server";
    	
        // create domain instance
        DomainDocument domainDoc = contentMother.createEmptyDomain();
        DomainType domain = domainDoc.addNewDomain();
        
        // set attribute
		ServerType server = domain.addNewServer();
		server.setName("server-name");
		ServerType server2 = domain.addNewServer();
		server2.setName("server-name2");
		
		// create resolve participant for domain
		SchemaProperty domainSchemaProperty = getSchemaPropertyByName("Domain", domainDoc);		
		ResolvedParticipant domainParticipant = ResolvedParticipantImpl.createSuccessfulResult("domain", domainSchemaProperty , domain);		
		
        // resolve
		ResolvedParticipant attribute;
		attribute = resolver.resolveAttribute(attributeName, domainParticipant);
		
        // test
		Object value = attribute.getValue(); 
        assertNotNull( attribute );
        assertTrue(attribute.isResolutionSuccesful());                
        assertEquals(attributeName, attribute.getName());
        assertTrue(value instanceof ServerType[] );        
        assertEquals(2, ((ServerType[]) value).length );        
        assertTrue(attribute.getType() instanceof SchemaProperty);        
        assertEquals(null, attribute.getResolutionError());    
        assertEquals(ResolvedParticipant.ValueState.SET, attribute.getValueState());

    }    

    /**
     * Test that array attribute value can be resolve from domain type,
     * which contains zero entries.
     */
    @Test
    public void testCanResolveCollectionAttributeValueFromDomainWithZeroEntries()
    {    	
		try {
	    	// declare values
			String attributeName = "Server";
	    	
	        // create domain instance
	        DomainDocument domainDoc = contentMother.createEmptyDomain();
	        DomainType domain = domainDoc.addNewDomain();
	        
	        // set attribute
	        // no attributes 

			// create resolve participant for domain
			SchemaProperty domainSchemaProperty = getSchemaPropertyByName("Domain", domainDoc);		
			ResolvedParticipant domainParticipant = ResolvedParticipantImpl.createSuccessfulResult("domain", domainSchemaProperty , domain);		
	        
	        // first, resolve the array attribute
			ResolvedParticipant attribute;
			attribute = resolver.resolveAttribute(attributeName, domainParticipant);
			
	        // second, resolve the array attribute value		
			HashMap<String, ResolvedParticipant> values;			
			values = resolver.resolveCollectionAttributeValues(attribute);
		
	        // test				 
	        assertNotNull( values );
	        assertEquals(0, values.size() );        
			
		} catch (ModelResolutionFailedException e) {
			fail (StackTraceHelper.getStrackTrace(e));
		}		
    }        
    
    /**
     * Test that array attribute value can be resolve from domain type,
     * which contains single entry
     */
    @Test
    public void testCanResolveCollectionAttributeValueFromDomainWithOneEntry()
    {		
		try {
	    	// declare values
			String attributeName = "Server";
			String arryAttributeName = "server-name";
	    	
	        // create domain instance
	        DomainDocument domainDoc = contentMother.createEmptyDomain();
	        DomainType domain = domainDoc.addNewDomain();
	        
	        // set attribute
			ServerType server = domain.addNewServer();
			server.setName(arryAttributeName);

			// create resolve participant for domain
			SchemaProperty domainSchemaProperty = getSchemaPropertyByName("Domain", domainDoc);		
			ResolvedParticipant domainParticipant = ResolvedParticipantImpl.createSuccessfulResult("domain", domainSchemaProperty , domain);		
			
	        // first, resolve the array attribute
			ResolvedParticipant attribute;
			attribute = resolver.resolveAttribute(attributeName, domainParticipant);
						
	        // second, resolve the array attribute value		
			HashMap<String, ResolvedParticipant> values;			
			values = resolver.resolveCollectionAttributeValues(attribute);
			
	        // test				 
	        assertNotNull( values );
	        assertEquals(1, values.size() );              
	        assertTrue(values.containsKey(arryAttributeName));                
	        
	        // get resolve attribute from array
	        ResolvedParticipant arrayAttribute = values.get(arryAttributeName);

	        // test
	        assertNotNull( arrayAttribute );
	        assertTrue(arrayAttribute.isResolutionSuccesful());        	        
	        assertEquals(arryAttributeName, arrayAttribute.getName());        
	        assertTrue(arrayAttribute.getType() instanceof SchemaProperty );        
	        assertEquals(null, arrayAttribute.getResolutionError());    
	        assertEquals(ResolvedParticipant.ValueState.SET, arrayAttribute.getValueState());
	        assertNotNull(arrayAttribute.getValue());
			
		} catch (ModelResolutionFailedException e) {
			fail (StackTraceHelper.getStrackTrace(e));
		}		
    }    
    
}
