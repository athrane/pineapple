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

import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicXmlBeanConstants.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.oracle.xmlns.weblogic.domain.DomainDocument;
import com.oracle.xmlns.weblogic.domain.DomainType;
import com.oracle.xmlns.weblogic.domain.LogType;

/**
 * Integration test for <code>XmlBeansModelAccessorImpl</code>.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class XmlBeansModelAccessorIntegrationTest
{

	/**
	 * ID for primary participant.
	 */
	static final String PRIMARY_ID = "primary-id";

	/**
	 * A null value used in tests.
	 */
    static final Object NULL_VALUE = null;
    
	/**
     * Object under test.
     */
    @Resource(name="xmlBeansModelAccessor")
    XmlBeansModelAccessor accessor;
    
    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Test that object can be looked up from the context.
     */
    @Test
    public void testCanGetInstanceFromContext()
    {
        assertNotNull( accessor );
    }
    
    
    /**
     * test that method returns true on int.
     */
    @Test
    public void testIsPrimitiveMethodReturnTrueOnInt() throws Exception
    {        
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"administration-port");        
        SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                
        
        // setup
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE  );

        // test
        assertTrue( accessor.isPrimitive( pp ) );
    }
    
    /**
     * test that method returns true on String.
     */
    @Test
    public void testIsPrimitiveMethodReturnTrueOnString() throws Exception
    {
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"name");        
        SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                
        
        // setup
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );

        // test
        assertTrue( accessor.isPrimitive( pp ) );
    }

    /**
     * test that method returns true on boolean.
     */
    @Test
    public void testIsPrimitiveMethodReturnTrueOnBoolean() throws Exception
    {
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"administration-port-enabled");        
        SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                        
        
        // setup
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );

        // test
        assertTrue( accessor.isPrimitive( pp ) );
    }

    /**
     * test that method returns false on Object.
     */
    @Test
    public void testIsPrimitiveMethodReturnFalseOnObject() throws Exception
    {
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"security-configuration");        
        SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                        
        
		// setup
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );

        // test
        assertFalse( accessor.isPrimitive( pp ));
    }
    
    /**
     * test that method returns false on XMLBeans Enum.
     */
    @Test
    public void testIsPrimitiveMethodReturnFalseOnXMLBeansEnum() throws Exception
    {
        LogType logObject = LogType.Factory.newInstance();
        SchemaType logSchemaType = logObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"rotation-type");        
        SchemaProperty schemaProperty = logSchemaType.getElementProperty( qname );                
        
        // setup
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );

        // test
        assertFalse( accessor.isPrimitive( pp ) );
    }
    
    /**
     * test that method returns false on array.
     */
    @Test
    public void testIsPrimitiveMethodReturnFalseOnServerTypeArray() throws Exception
    {        
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"server");        
        SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                
        
        // setup
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );

        // test
        assertFalse( accessor.isPrimitive( pp ) );
    }
    
    /**
     * test that method returns true on XMLBeans Enum..
     */
    @Test
    public void testIsEnumMethodReturnTrueOnXMLBeansEnum() throws Exception
    {
        LogType logObject = LogType.Factory.newInstance();
        SchemaType logSchemaType = logObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"rotation-type");        
        SchemaProperty schemaProperty = logSchemaType.getElementProperty( qname );                
        
        // setup
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );
                
        // test
        assertTrue( accessor.isEnum( pp ));
    }

    /**
     * test that method returns false on Object.
     */
    @Test
    public void testIsEnumMethodReturnFalseOnServerTypeArray() throws Exception
    {
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"server");        
        SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                
        
        // setup
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );
                
        // test
        assertFalse( accessor.isEnum( pp ));
    }

    /**
     * test that method returns false on Object.
     */
    @Test
    public void testIsEnumMethodReturnFalseOnObject2() throws Exception
    {
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"security-configuration");        
        SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                
        
        // setup
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );
                
        // test
        assertFalse( accessor.isEnum( pp ));
    }

   /**
    * test that method returns false on string.
    */
   @Test
   public void testIsEnumMethodReturnFalseOnString() throws Exception
   {
       DomainType domainObject= DomainType.Factory.newInstance();
       SchemaType domainSchemaType = domainObject.schemaType();        
       QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"name");        
       SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                
       
       // setup
       ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );
               
       // test
       assertFalse( accessor.isEnum( pp ));
   }

   /**
    * test that method returns false on int.
    */
   @Test
   public void testIsEnumMethodReturnFalseOnInt() throws Exception
   {
       DomainType domainObject= DomainType.Factory.newInstance();
       SchemaType domainSchemaType = domainObject.schemaType();        
       QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"administration-port");        
       SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                
       
       // setup
       ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );
               
       // test
       assertFalse( accessor.isEnum( pp ));
   }
   
    /**
     * test that method returns true on array.
     */
    @Test
    public void testIsCollectionMethodReturnTrueOnServerTypeArray() throws Exception
    {        
        DomainDocument domainDoc = DomainDocument.Factory.newInstance();
        SchemaType domainDocSchemaType = domainDoc.schemaType();        
        QName domainQName = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"domain");           
        SchemaProperty parentSchemaProperty = domainDocSchemaType.getElementProperty( domainQName );        
    	
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"server");                
        SchemaProperty childSchemaProperty = domainSchemaType.getElementProperty( qname );                        
        
        // setup
        ResolvedParticipant parent = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, parentSchemaProperty , NULL_VALUE );
        ResolvedParticipant child = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, childSchemaProperty, NULL_VALUE );        

        // test
        assertTrue( accessor.isCollection( parent, child) );
    }


    /**
     * test that method returns false on object.
     */
    @Test
    public void testIsCollectionMethodReturnFalseOnObject() throws Exception
    {            	
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"server");                
        SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                        
        
        // setup
        ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );        

        // test
        assertFalse( accessor.isCollection( participant, participant) );
    }
    
    
    /**
     * test that method returns false on object.
     */
    @Test
    public void testIsCollectionMethodReturnFalseOnObject2() throws Exception
    {        
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"security-configuration");        
        SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                
        
        // setup
        ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );

        // test
        assertFalse( accessor.isCollection( participant, participant) );
    }
    
    /**
     * test that method returns false on string.
     */
    @Test
    public void testIsCollectionMethodReturnFalseOnString() throws Exception
    {
        DomainDocument domainDoc = DomainDocument.Factory.newInstance();
        SchemaType domainDocSchemaType = domainDoc.schemaType();        
        QName domainQName = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"domain");           
        SchemaProperty parentSchemaProperty = domainDocSchemaType.getElementProperty( domainQName );        
    	
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"name");        
        SchemaProperty childSchemaProperty = domainSchemaType.getElementProperty( qname );                
        
        // setup
        ResolvedParticipant parent = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, parentSchemaProperty, NULL_VALUE );        
        ResolvedParticipant child = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, childSchemaProperty, NULL_VALUE );
                
        // test
        assertFalse( accessor.isCollection( parent, child ));
    }
    
    /**
     * test that method returns false on XMLBeans Enum.
     */
    @Test
    public void testIsCollectionMethodReturnFalseOnXMLBeansEnum() throws Exception
    {
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname1 = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"log");        
        SchemaProperty parentSchemaProperty = domainSchemaType.getElementProperty( qname1 );                    	
    	
        LogType logObject = LogType.Factory.newInstance();
        SchemaType logSchemaType = logObject.schemaType();        
        QName qname2 = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"rotation-type");        
        SchemaProperty childSchemaProperty = logSchemaType.getElementProperty( qname2 );                
        
        // setup
        ResolvedParticipant parent = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, parentSchemaProperty, NULL_VALUE );        
        ResolvedParticipant child = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, childSchemaProperty, NULL_VALUE );

        // test
        assertFalse( accessor.isCollection( parent, child ));
    }
    
    
    /**
     * test that method returns false on array.
     */
    @Test
    public void testIsObjectMethodReturnFalseOnServerTypeArray() throws Exception
    {        
        DomainDocument domainDoc = DomainDocument.Factory.newInstance();
        SchemaType domainDocSchemaType = domainDoc.schemaType();        
        QName domainQName = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"domain");           
        SchemaProperty parentSchemaProperty = domainDocSchemaType.getElementProperty( domainQName );        
    	
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"server");                
        SchemaProperty childSchemaProperty = domainSchemaType.getElementProperty( qname );                        
        
        // setup
        ResolvedParticipant parent = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, parentSchemaProperty , NULL_VALUE );
        ResolvedParticipant child = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, childSchemaProperty, NULL_VALUE );        

        // test
        assertFalse( accessor.isObject( parent, child) );
    }    
    
    
    /**
     * test that method returns false on object.
     */
    @Test
    public void testIsObjectMethodReturnFalseOnObject() throws Exception
    {            	
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"server");                
        SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                        
        
        // setup
        ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );        

        // test
        assertTrue( accessor.isObject( participant, participant) );
    }
    
    
    /**
     * test that method returns false on object.
     */
    @Test
    public void testIsObjectMethodReturnFalseOnObject2() throws Exception
    {        
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"security-configuration");        
        SchemaProperty schemaProperty = domainSchemaType.getElementProperty( qname );                
        
        // setup
        ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );

        // test
        assertTrue( accessor.isObject( participant, participant) );
    }    
    
    /**
     * test that method returns false on string.
     */
    @Test
    public void testIsObjectMethodReturnFalseOnString() throws Exception
    {
        DomainDocument domainDoc = DomainDocument.Factory.newInstance();
        SchemaType domainDocSchemaType = domainDoc.schemaType();        
        QName domainQName = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"domain");           
        SchemaProperty parentSchemaProperty = domainDocSchemaType.getElementProperty( domainQName );        
    	
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"name");        
        SchemaProperty childSchemaProperty = domainSchemaType.getElementProperty( qname );                
        
        // setup
        ResolvedParticipant parent = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, parentSchemaProperty, NULL_VALUE );        
        ResolvedParticipant child = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, childSchemaProperty, NULL_VALUE );
                
        // test
        assertFalse( accessor.isObject( parent, child ));
    }    
    
    /**
     * test that method returns false on XMLBeans enum.
     */
    @Test
    public void testIsObjectMethodReturnFalseOnXMLBeansEnum() throws Exception
    {
        DomainType domainObject= DomainType.Factory.newInstance();
        SchemaType domainSchemaType = domainObject.schemaType();        
        QName qname1 = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"log");        
        SchemaProperty parentSchemaProperty = domainSchemaType.getElementProperty( qname1 );                    	
    	
        LogType logObject = LogType.Factory.newInstance();
        SchemaType logSchemaType = logObject.schemaType();        
        QName qname2 = new QName(XMLNS_WEBLOGIC_DOMAIN_URI,"rotation-type");        
        SchemaProperty childSchemaProperty = logSchemaType.getElementProperty( qname2 );                
        
        // setup
        ResolvedParticipant parent = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, parentSchemaProperty, NULL_VALUE );        
        ResolvedParticipant child = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, childSchemaProperty, NULL_VALUE );

        // test
        assertFalse( accessor.isObject( parent, child ));
    }    
    
}
