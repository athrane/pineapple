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


package com.alpha.pineapple.plugin.weblogic.jmx.session;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
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
import com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.MBeansModelAccessor;
import com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.metadata.MetadataRepository;
import com.alpha.pineapple.resolvedmodel.validation.ModelValidationFailedException;

/**
 * Unit test for WeblogicJMXEditSession.
 */
public class WeblogicJMXEditSessionTest
{

	/**
	 * Object under test.
	 */
    WeblogicJMXEditSessionImpl session;

    /**
     * Message provider for I18N support.
     */
    MessageProvider messageProvider;        
    
    /**
     * MBean server connection.
     */
    MBeanServerConnection connection; 
    
    /**
     * MBean meta data repository.
     */
    MetadataRepository mbeanMetadataRepository;        
    
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

    	// create mock MBean server connection
		connection = EasyMock.createMock( MBeanServerConnection.class );
		
    	// create session
    	session = new WeblogicJMXEditSessionImpl();

		// connect
    	session.connect(connection);

        // create mock repository
        mbeanMetadataRepository = EasyMock.createMock( MetadataRepository.class );
    	
        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
    	
        // complete mock provider initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl();         
        EasyMock.expect( messageProvider.getMessage(
                    (String) EasyMock.isA( String.class )));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject()));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        
        EasyMock.replay(messageProvider);
                
        // inject message provider
        ReflectionTestUtils.setField( session, "messageProvider", messageProvider );    
        
        // inject repository
        ReflectionTestUtils.setField( session, "mbeanMetadataRepository", mbeanMetadataRepository );        
            	
    }

    @After
    public void tearDown() throws Exception
    {
    	connection = null;
        session = null;
    }

    /**
     * Create unconnected session.
     */
	void createUnconnectedSession() {
    	session = new WeblogicJMXEditSessionImpl();
                
        // inject message provider
        ReflectionTestUtils.setField( session, "messageProvider", messageProvider );
	}
    
    
    /**
     * Test that session object can be created.
     */
    @Test
    public void testCanCreateJmxSession()
    {
		// test
		assertNotNull(session);            
    }

    /**
     * Test that is-connected query returns false initially.
     */
    @Test
    public void testIsConnectedQueryReturnsFalseInitially()
    {
    	createUnconnectedSession();        
    	
    	// test
        assertFalse( session.isConnected() );
    }

    /**
     * Test that DomainMBean object name is null if JMX session isn't connected.
     */
    @Test
    public void testDomainMBeanIsUndefinedIfSessionIsntConnected()
    {
    	createUnconnectedSession();        
    	
    	// test    	
        assertNull( session.getDomainMBeanObjectName() );
    }

    /**
     * Test initially is connection validation fails.
     * 
     * @throws Exception If test fails.
     */
    @Test(expected=IllegalStateException.class)
    public void testInitiallyFailsIsConnectedValidation() throws Exception
    {
    	createUnconnectedSession();        
    	
		// test
		session.validateIsConnected();            
    }

    /**
     * Test is connected validation succeeds if session is connected.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testIsConnectedValidation() throws Exception
    {
    	// complete mock MBean server connection initialization
		EasyMock.replay(connection);
		    	
		// test
		session.validateIsConnected();
		
        // test 
        EasyMock.verify(connection);		
    }
    
    
    /**
     * Test invoke method with no parameters.
     *  
     * @throws Exception If test fails.
     */
    @Test
    public void testInvokeMethodWithNoParams() throws Exception
    {    	
    	// invocation result
		Object result = createMock( ObjectName.class);
		    					
        // create object name mock		
		ObjectName objName = createMock( ObjectName.class);
		replay(objName);

		MBeanParameterInfo[] paramInfo = {};
		
        // create MBeanOperationInfo mock
		MBeanOperationInfo operationInfo = createMock( MBeanOperationInfo.class );
		expect( operationInfo.getName())
			.andReturn(randomName);		
		expect( operationInfo.getSignature())
			.andReturn(paramInfo);		
		replay(operationInfo );
		
		// a few arrays
		Object[] params = {};
		String[] signatureArray = {};
		
        // complete MBeanServerConnection mock initialization
		EasyMock.expect(connection.invoke(
			EasyMock.eq(objName),
			EasyMock.eq(randomName),
			EasyMock.eq(params),EasyMock.aryEq(signatureArray)))
			.andReturn(result);						
		EasyMock.replay(connection);
		
		// validate
		session.invokeMethod(objName, operationInfo, params);
    	
        // test 
    	verify(objName);        
    	verify(operationInfo);
        EasyMock.verify(connection);
    }

    /**
     * Test invoke method with one parameter.
     *  
     * @throws Exception If test fails.
     */
    @Test
    public void testInvokeMethodWithOneParam() throws Exception
    {
    	// invocation result
		Object result = createMock( ObjectName.class);
		    					
        // create object name mock		
		ObjectName objName = createMock( ObjectName.class);
		replay(objName);

		// create MBean parameter info mock
		MBeanParameterInfo paramInfo1 = createMock( MBeanParameterInfo.class );
		expect(paramInfo1.getType())
			.andReturn(randomType);
		replay(paramInfo1);		
		MBeanParameterInfo[] paramInfo = { paramInfo1 };
		
        // create MBeanOperationInfo mock
		MBeanOperationInfo operationInfo = createMock( MBeanOperationInfo.class );
		expect( operationInfo.getName())
			.andReturn(randomName);		
		expect( operationInfo.getSignature())
			.andReturn(paramInfo);		
		replay(operationInfo );
		
		// a few arrays
		Object[] params = { "aValue" };
		String[] signatureArray = {randomType };
		
        // complete MBeanServerConnection mock initialization
		EasyMock.expect(connection.invoke(
			EasyMock.eq(objName),
			EasyMock.eq(randomName),
			EasyMock.eq(params),EasyMock.aryEq(signatureArray)))
			.andReturn(result);						
		EasyMock.replay(connection);
		
		// validate
		session.invokeMethod(objName, operationInfo, params);
    	
        // test 
    	verify(objName);        
    	verify(operationInfo);
        verify(paramInfo1);    	
        EasyMock.verify(connection);
        verify(paramInfo1);        
    }

    /**
     * Test invoke method with two parameters.
     *  
     * @throws Exception If test fails.
     */
    @Test
    public void testInvokeMethodWithTwoParams() throws Exception
    {
    	// invocation result
		Object result = createMock( ObjectName.class);
		    					
        // create object name mock		
		ObjectName objName = createMock( ObjectName.class);
		replay(objName);

		// create MBean parameter info mock
		MBeanParameterInfo paramInfo1 = createMock( MBeanParameterInfo.class );
		expect(paramInfo1.getType())
			.andReturn(randomType);
		replay(paramInfo1);
		MBeanParameterInfo paramInfo2 = createMock( MBeanParameterInfo.class );
		expect(paramInfo2.getType())
			.andReturn(randomType2);
		replay(paramInfo2);		
		
		MBeanParameterInfo[] paramInfo = { paramInfo1, paramInfo2 };
		
        // create MBeanOperationInfo mock
		MBeanOperationInfo operationInfo = createMock( MBeanOperationInfo.class );
		expect( operationInfo.getName())
			.andReturn(randomName);		
		expect( operationInfo.getSignature())
			.andReturn(paramInfo);		
		replay(operationInfo );
		
		// a few arrays
		Object[] params = { "aValue", "aValue2" };
		String[] signatureArray = {randomType, randomType2 };
		
        // complete MBeanServerConnection mock initialization
		EasyMock.expect(connection.invoke(
			EasyMock.eq(objName),
			EasyMock.eq(randomName),
			EasyMock.eq(params),EasyMock.aryEq(signatureArray)))
			.andReturn(result);						
		EasyMock.replay(connection);
		
		// validate
		session.invokeMethod(objName, operationInfo, params);
    	
        // test 
    	verify(objName);        
    	verify(operationInfo);
        verify(paramInfo1);    	
        verify(paramInfo2);        
        EasyMock.verify(connection);
        verify(paramInfo1);        
    }

    /**
     * Test that attribute info can be retrieved.
     *  
     * @throws Exception If test fails.
     */
    @Test
    public void testFindAttributeInfo() throws Exception
    {
		String attributeName = RandomStringUtils.randomAlphabetic(10);

    	// create object name mock    	
		ObjectName objName = createMock( ObjectName.class );
    	replay(objName);
		
    	// create MBean info mock    	
    	MBeanInfo mbeanInfo = createMock( MBeanInfo.class );
    	replay(mbeanInfo);

    	// create attribute info mock    	
    	MBeanAttributeInfo attributeInfo = createMock( MBeanAttributeInfo.class );
    	replay(attributeInfo);
    	
    	// create connection mock
		EasyMock.expect( connection.getMBeanInfo( objName ))
    		.andReturn(mbeanInfo);
    	EasyMock.replay(connection);
    			    	
    	// complete initializations
		EasyMock.expect( mbeanMetadataRepository.getAttributeInfo(mbeanInfo, attributeName))
			.andReturn(attributeInfo);        
        EasyMock.replay(mbeanMetadataRepository);
    	
		// get info
		MBeanAttributeInfo info = session.getAttributeInfo(objName, attributeName);
    	
        // test
        assertNotNull( info );
        assertEquals(attributeInfo,info);

        // test 
    	EasyMock.verify(connection);
    	verify(objName);        
    	verify(mbeanInfo);
        EasyMock.verify(mbeanMetadataRepository);    	
    }
    
    /**
     * Test object name type can be validated.
     *  
     * @throws Exception If test fails.
     */
    @Test
    public void testObjectNameValidation() throws Exception
    {
    	// create object name mock    	
		ObjectName objName = createMock( ObjectName.class );
    	replay(objName);

    	// complete initializations
        EasyMock.replay(mbeanMetadataRepository);
    	
		// validate
		session.validateIsObjectName(objName);
    	
        // test 
    	verify(objName);        
        EasyMock.verify(mbeanMetadataRepository);
    }
    
    
    /**
     * Test object name validation fails
     * if object isn't of ObjectName type.
     *  
     * @throws Exception If test fails.
     */
    @Test(expected = ModelValidationFailedException.class)
    public void testObjectNameValidationFailsIfParamIsntObjectName() throws Exception
    {
    	// create mock of my self    	
		MBeansModelAccessor aMock = createMock( MBeansModelAccessor.class );
    	replay(aMock);

    	// complete initializations
        EasyMock.replay(mbeanMetadataRepository);
    	
		// validate
		session.validateIsObjectName(aMock);
    	
        // test 
    	verify(aMock);        
        EasyMock.verify(mbeanMetadataRepository);
    }
    
}
