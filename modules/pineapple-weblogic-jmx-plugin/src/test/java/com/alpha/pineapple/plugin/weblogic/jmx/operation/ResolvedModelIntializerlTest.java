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


package com.alpha.pineapple.plugin.weblogic.jmx.operation;

import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.WLDF_RESOURCE_ATTRIBUTE;
import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.WLDF_SYSTEMRESOURCE_ATTRIBUTE;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.xmlbeans.SchemaProperty;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;
import com.alpha.pineapple.plugin.weblogic.jmx.model.xmlbeans.XmlBeansModelAccessor;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.oracle.xmlns.weblogic.domain.DomainDocument;
import com.oracle.xmlns.weblogic.domain.DomainType;
import com.oracle.xmlns.weblogic.weblogicDiagnostics.WldfResourceDocument;
import com.oracle.xmlns.weblogic.weblogicDiagnostics.WldfResourceType;

/**
 * Unit test of the class @link {@link ResolvedModelIntializerImpl}. 
 */
public class ResolvedModelIntializerlTest {

    /**
     * Object under test.
     */
	ResolvedModelIntializerImpl initializer;
	
    /**
     * Mock XMLBean model access object.
     */
    XmlBeansModelAccessor modelAccessor;

    /**
     * Mock session object.
     */    
	WeblogicJMXEditSession session;

	/**
	 * Initialization result.
	 */
	ResolvedType result;

	/**
	 * Random domain name.
	 */
	String randomDomainName;

	/**
	 * Mock Domain XMLBean
	 */
	DomainType domainXmlBean;

	/**
	 * Mock WLDF Resource XMLBean
	 */
	WldfResourceType wldfResourceXmlBean;
	
	/**
	 * Mock Domain XMLBean schema property.
	 */
	SchemaProperty schemaProperty;

	/**
	 * Mock Domain MBean object name. 
	 */
	ObjectName domainMBeanObjName;
	
	@Before
	public void setUp() throws Exception {
		
    	randomDomainName = RandomStringUtils.randomAlphabetic(10);
		
		// create initializer
		initializer = new ResolvedModelIntializerImpl();

        // create mock session  
        session = EasyMock.createMock( WeblogicJMXEditSession.class );        
		
        // create mock model accessor 
        modelAccessor = EasyMock.createMock( XmlBeansModelAccessor.class );        
            
        // inject model acccessor 
        ReflectionTestUtils.setField( initializer, "modelAccessor", modelAccessor, XmlBeansModelAccessor.class  );		
	}
	
	@After
	public void tearDown() throws Exception {
		initializer = null;
		modelAccessor = null;
		session = null;
		result = null;
		domainXmlBean = null;
		schemaProperty = null;
		domainMBeanObjName = null;
		wldfResourceXmlBean = null;
	}

	void completeMockSessionSetup() throws Exception {
		EasyMock.expect( session.getDomainMBeanObjectName() ).andReturn( domainMBeanObjName );
		EasyMock.replay(session);
	}
	
	void completeMockSchemaPropertySetup() {
		schemaProperty = EasyMock.createMock( SchemaProperty.class );
		EasyMock.replay(schemaProperty );
	}

	void completeMockDomainXmlBeanSetup() {
		domainXmlBean = EasyMock.createMock( DomainType.class );
		EasyMock.expect( domainXmlBean.getName() ).andReturn( randomDomainName );
		EasyMock.replay(domainXmlBean);
	}

	void completeMockWldfResourceXmlBeanSetup() {
		wldfResourceXmlBean = EasyMock.createMock( WldfResourceType.class );
		EasyMock.expect( wldfResourceXmlBean.getName() ).andReturn( randomDomainName ).times(2);
		EasyMock.replay(wldfResourceXmlBean);
	}

	void completeMockDomainMBeanObjectNameSetup() {
		domainMBeanObjName = org.easymock.classextension.EasyMock.createMock( ObjectName.class );
		org.easymock.classextension.EasyMock.expect( 
			domainMBeanObjName.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY))
				.andReturn( randomDomainName );
		org.easymock.classextension.EasyMock.replay(domainMBeanObjName );
	}
	
	/**
	 * Test initializer can dispatch to Domain document.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testInitializeCanDispatchToDomain() throws Exception {

		completeMockDomainMBeanObjectNameSetup();
		completeMockSessionSetup();
		completeMockSchemaPropertySetup();		
		completeMockDomainXmlBeanSetup();
		
		// define content  
		DomainDocument domainDoc = org.easymock.classextension.EasyMock.createMock( DomainDocument.class );
		org.easymock.classextension.EasyMock.expect(domainDoc.getDomain() ).andReturn(domainXmlBean);
		org.easymock.classextension.EasyMock.replay(domainDoc );
		
		// complete accessor setup
		EasyMock.expect(modelAccessor.getSchemaPropertyByName( ResolvedModelIntializerImpl.DOMAIN_SCHEMA_PROPERTY_NAME , domainDoc)).andReturn(schemaProperty);
		EasyMock.replay(modelAccessor);		
				
		// invoke with content as object type to trigger dispatch
		result = initializer.initialize((Object) domainDoc, session);		
		
		// test 
		assertNotNull(result);
				
		// verify mocks
		EasyMock.verify(session);
		EasyMock.verify(modelAccessor);
		EasyMock.verify(schemaProperty);
		EasyMock.verify(domainXmlBean);
		org.easymock.classextension.EasyMock.verify(domainMBeanObjName);
		org.easymock.classextension.EasyMock.verify(domainDoc);		
	}

	/**
	 * Test that returned domain model contains expected values
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testDomainModelContainExpectedValues() throws Exception {

		completeMockDomainMBeanObjectNameSetup();
		completeMockSessionSetup();
		completeMockSchemaPropertySetup();		
		completeMockDomainXmlBeanSetup();
		
		// define content  
		DomainDocument domainDoc = org.easymock.classextension.EasyMock.createMock( DomainDocument.class );
		org.easymock.classextension.EasyMock.expect(domainDoc.getDomain() ).andReturn(domainXmlBean);
		org.easymock.classextension.EasyMock.replay(domainDoc );
		
		// complete accessor setup
		EasyMock.expect(modelAccessor.getSchemaPropertyByName( ResolvedModelIntializerImpl.DOMAIN_SCHEMA_PROPERTY_NAME , domainDoc)).andReturn(schemaProperty);
		EasyMock.replay(modelAccessor);		
				
		// invoke with content as object type to trigger dispatch
		result = initializer.initialize((Object) domainDoc, session);		
		
		// test 
		assertEquals(randomDomainName, result.getPrimaryParticipant().getName());
		assertEquals(schemaProperty, result.getPrimaryParticipant().getType());
		assertEquals(domainXmlBean, result.getPrimaryParticipant().getValue());
		assertTrue(result.getPrimaryParticipant().isResolutionSuccesful());				
		assertEquals(randomDomainName, result.getSecondaryParticiant().getName());
		assertEquals(domainMBeanObjName.getClass(), result.getSecondaryParticiant().getType());
		assertEquals(domainMBeanObjName, result.getSecondaryParticiant().getValue());		
		assertTrue(result.getSecondaryParticiant().isResolutionSuccesful());
				
		// verify mocks
		EasyMock.verify(session);
		EasyMock.verify(modelAccessor);
		EasyMock.verify(schemaProperty);
		EasyMock.verify(domainXmlBean);
		org.easymock.classextension.EasyMock.verify(domainMBeanObjName);
		org.easymock.classextension.EasyMock.verify(domainDoc);		
	}
	
	
	/**
	 * Test initializer can dispatch to WLDF Resource document.
	 * 
	 * @throws Exception If test fails.
	 */	
	@Test
	public void testInitializeCanDispatchToWldf() throws Exception {

		// complete mock WLDFResource MBean setup		
		ObjectName wldfResourceObjName = createMock(ObjectName.class);		
		replay(wldfResourceObjName);
		
		// complete mock WLDFSystemResource MBean setup	
		ObjectName wldfSystemResourceObjName = createMock(ObjectName.class);
		replay(wldfSystemResourceObjName);

		MBeanAttributeInfo info = createMock(MBeanAttributeInfo.class);
		replay(info);
		
		// complete mock session setup				
		EasyMock.expect( session.getDomainMBeanObjectName() ).andReturn( domainMBeanObjName );
		EasyMock.expect( session.findMBean( WLDF_SYSTEMRESOURCE_ATTRIBUTE, randomDomainName)).andReturn(wldfSystemResourceObjName);		
		EasyMock.expect( session.getAttributeInfo(wldfSystemResourceObjName, WLDF_RESOURCE_ATTRIBUTE)).andReturn(info);
		EasyMock.expect( session.getAttribute(wldfSystemResourceObjName, WLDF_RESOURCE_ATTRIBUTE)).andReturn(wldfResourceObjName);				
		EasyMock.replay(session);
				
		// complete mock DomainMBean object name setup
		domainMBeanObjName = org.easymock.classextension.EasyMock.createMock( ObjectName.class );
		org.easymock.classextension.EasyMock.replay(domainMBeanObjName );
		
		completeMockSchemaPropertySetup();		
		completeMockWldfResourceXmlBeanSetup();
		
		// define content  
		WldfResourceDocument wldfResourceDoc = org.easymock.classextension.EasyMock.createMock( WldfResourceDocument.class );
		org.easymock.classextension.EasyMock.expect(wldfResourceDoc.getWldfResource() ).andReturn(wldfResourceXmlBean).times(2);
		org.easymock.classextension.EasyMock.replay(wldfResourceDoc );
		
		// complete accessor setup
		EasyMock.expect(modelAccessor.getSchemaPropertyByName( ResolvedModelIntializerImpl.WLDFRESOURCE_SCHEMA_PROPERTY_NAME , wldfResourceDoc)).andReturn(schemaProperty);
		EasyMock.replay(modelAccessor);				
		
		// invoke with content as object type to trigger dispatch
		result = initializer.initialize((Object) wldfResourceDoc, session);		
		
		// test 
		assertNotNull(result);
		
		// verify mocks
		EasyMock.verify(session);
		EasyMock.verify(modelAccessor);
		EasyMock.verify(schemaProperty);
		EasyMock.verify(wldfResourceXmlBean);
		org.easymock.classextension.EasyMock.verify(domainMBeanObjName);
		verify(wldfResourceDoc);
		verify(wldfSystemResourceObjName);		
		verify(wldfResourceObjName);
		verify(info);
	}

	/**
	 * Test that returned WLDF model contains expected values
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testWldfModelContainExpectedValues() throws Exception {

		// complete mock WLDFResource MBean setup		
		ObjectName wldfResourceObjName = createMock(ObjectName.class);		
		replay(wldfResourceObjName);
		
		// complete mock WLDFSystemResource MBean setup	
		ObjectName wldfSystemResourceObjName = createMock(ObjectName.class);
		replay(wldfSystemResourceObjName);

		MBeanAttributeInfo info = createMock(MBeanAttributeInfo.class);
		replay(info);
		
		// complete mock session setup				
		EasyMock.expect( session.getDomainMBeanObjectName() ).andReturn( domainMBeanObjName );
		EasyMock.expect( session.findMBean( WLDF_SYSTEMRESOURCE_ATTRIBUTE, randomDomainName)).andReturn(wldfSystemResourceObjName);		
		EasyMock.expect( session.getAttributeInfo(wldfSystemResourceObjName, WLDF_RESOURCE_ATTRIBUTE)).andReturn(info);
		EasyMock.expect( session.getAttribute(wldfSystemResourceObjName, WLDF_RESOURCE_ATTRIBUTE)).andReturn(wldfResourceObjName);				
		EasyMock.replay(session);
				
		// complete mock DomainMBean object name setup
		domainMBeanObjName = org.easymock.classextension.EasyMock.createMock( ObjectName.class );
		org.easymock.classextension.EasyMock.replay(domainMBeanObjName );
		
		completeMockSchemaPropertySetup();		
		completeMockWldfResourceXmlBeanSetup();
		
		// define content  
		WldfResourceDocument wldfResourceDoc = org.easymock.classextension.EasyMock.createMock( WldfResourceDocument.class );
		org.easymock.classextension.EasyMock.expect(wldfResourceDoc.getWldfResource() ).andReturn(wldfResourceXmlBean).times(2);
		org.easymock.classextension.EasyMock.replay(wldfResourceDoc );
		
		// complete accessor setup
		EasyMock.expect(modelAccessor.getSchemaPropertyByName( ResolvedModelIntializerImpl.WLDFRESOURCE_SCHEMA_PROPERTY_NAME , wldfResourceDoc)).andReturn(schemaProperty);
		EasyMock.replay(modelAccessor);				
		
		// invoke with content as object type to trigger dispatch
		result = initializer.initialize((Object) wldfResourceDoc, session);		
		
		// test 
		assertEquals(randomDomainName, result.getPrimaryParticipant().getName());
		assertEquals(schemaProperty, result.getPrimaryParticipant().getType());
		assertEquals(wldfResourceXmlBean, result.getPrimaryParticipant().getValue());
		assertTrue(result.getPrimaryParticipant().isResolutionSuccesful());				
		assertEquals(randomDomainName, result.getSecondaryParticiant().getName());
		assertEquals(info, result.getSecondaryParticiant().getType());
		assertEquals(wldfResourceObjName, result.getSecondaryParticiant().getValue());		
		assertTrue(result.getSecondaryParticiant().isResolutionSuccesful());
		
		// verify mocks
		EasyMock.verify(session);
		EasyMock.verify(modelAccessor);
		EasyMock.verify(schemaProperty);
		EasyMock.verify(wldfResourceXmlBean);
		org.easymock.classextension.EasyMock.verify(domainMBeanObjName);
		verify(wldfResourceDoc);
		verify(wldfSystemResourceObjName);		
		verify(wldfResourceObjName);
		verify(info);		
	}
		
}
