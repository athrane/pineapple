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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ObjectName;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.testutils.ObjectMotherWebLogicJMX;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;

/**
 * Integration test for <code>MBeansModelAccessorImpl</code>.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class MetadataRepositoryIntegrationTest
{
	/**
	 * First array index.
	 */
	static final int FIRST_INDEX = 0;

	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	
    /**
     * WeblogicJMXSession object mother.
     */
    ObjectMotherWeblogicJmxSession sessionMother;

    /**
     * WebLogic JMX object mother.
     */
    ObjectMotherWebLogicJMX jmxMother;		
    
    /**
     * Object under test.
     */
    @Resource()
    MetadataRepository mbeanMetadataRepository;
    
    /** 
     * WebLogic JMX session.
     */
    WeblogicJMXEditSession session;

    /**
     * Some random WorkManager shutdown trigger name.
     */
    String randomWorkManagerName;
    
    @Before
    public void setUp() throws Exception
    {
    	randomWorkManagerName = RandomStringUtils.randomAlphabetic(10)+"-wm";
    	
        // create session mother
        sessionMother = new ObjectMotherWeblogicJmxSession();
        
		// create session
		session = sessionMother.createConnectedWlsJmxEditSession();        
		
		// start edit mode
		session.startEdit();
		
		// create JMX mother
		jmxMother = new ObjectMotherWebLogicJMX();                		
    }

    @After
    public void tearDown() throws Exception
    {
		jmxMother.deleteWorkManager(session, randomWorkManagerName);
    	
    	sessionMother = null;
    	
    	if(session != null) {
    		session.disconnect();
    		session = null;
    	}
    }
    
    
    /**
     * Test that object can be looked up from the context.
     */
    @Test
    public void testCanGetInstanceFromContext()
    {
        assertNotNull( mbeanMetadataRepository );
    }

    
    /**
     * Test that domain meta data can be added to repository
     * and that "Name" attribute contains expected info.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testAttributeInfoContainsCorrectName() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "Name");
		
		// test
		assertNotNull(info);
		assertEquals("Name", info.getName());
		
    }
        
    /**
     * Test that repository contains added MBean info.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testContainsMBean() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// test 
		assertTrue(mbeanMetadataRepository.containsMBean(mbeanInfo));		
    }

    
    /**
     * Test that meta data can retrieved from repository by MBean info.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetMBeanMetaDataByMBeanInfo() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanMetadata metadata = mbeanMetadataRepository.getMBeanMetadata(mbeanInfo);
		
		// test
		assertNotNull(metadata );		
    }

    /**
     * Test that attribute info can be retrieved from repository.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetAttributeInfo() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "Name");
		
		// test
		assertNotNull(info);
		assertEquals("Name", info.getName());
		assertEquals("java.lang.String", info.getType());
		assertNotNull(info.getDescriptor());		
    }
    
    /**
     * Test that attribute info fails to be retrieved from repository.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testFailsToGetActiveAttributeInfo() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "Active");
		
		// test
		assertNull(info);
    }

    /**
     * Test that attribute info fails to be retrieved from repository.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testFailsToGetMsgIdPrefixCompatibilityEnabledAttributeInfo() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "MsgIdPrefixCompatibilityEnabled");
		
		// test
		assertNull(info);
    }
    
    
    /**
     * Test that attribute info can be retrieved from repository
     * for the attribute: Dommain.Name.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetAttributeInfo_ForDomainName() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "Name");
		
		// test
		assertNotNull(info);
		assertEquals("Name", info.getName());
		assertEquals("java.lang.String", info.getType());
		assertNotNull(info.getDescriptor());		
    }
    
    /**
     * Test that attribute info can be retrieved from repository
     * for the attribute: Domain.Servers.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetAttributeInfo_ForDomainServers() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "Servers");

		// test
		assertNotNull(info);
		assertEquals("Servers", info.getName());
		assertEquals(WebLogicMBeanConstants.OBJECTNAME_ARRAY_TYPE, info.getType());
		assertNotNull(info.getDescriptor());		
    }

    /**
     * Test that attribute info can be retrieved from repository
     * for the attribute: Domain.Server.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetAttributeInfo_ForDomainServer() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "Servers");

		// test
		assertNotNull(info);
		assertEquals("Servers", info.getName());
		assertEquals(WebLogicMBeanConstants.OBJECTNAME_ARRAY_TYPE, info.getType());
		assertNotNull(info.getDescriptor());		
    }

    /**
     * Test that attribute info can be retrieved from repository
     * for the attribute: Dommain.Jmx.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetAttributeInfo_ForJmx() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "Jmx");
		
		// test
		assertNotNull(info);
		assertEquals("JMX", info.getName());
		assertEquals("javax.management.ObjectName", info.getType());
		assertNotNull(info.getDescriptor());		
    }

    /**
     * Test that attribute info can be retrieved from repository
     * for the attribute: Dommain.Jmx with attribute name written with lower case. 
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetAttributeInfo_ForJmxWithLowerCase() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "jmx");
		
		// test
		assertNotNull(info);
		assertEquals("JMX", info.getName());
		assertEquals("javax.management.ObjectName", info.getType());
		assertNotNull(info.getDescriptor());		
    }

    /**
     * Test that attribute info can be retrieved from repository
     * for the attribute: Dommain.AppDeployment.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetAttributeInfo_ForAppDeployment() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "AppDeployment");
		
		// test
		assertNotNull(info);
		assertEquals("AppDeployments", info.getName());
		assertEquals(WebLogicMBeanConstants.OBJECTNAME_ARRAY_TYPE, info.getType());
		assertNotNull(info.getDescriptor());		
    }

    /**
     * Test that attribute info can be retrieved from repository
     * for the attribute: Dommain.DomainLibrary.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetAttributeInfo_ForDomainLibrary() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "DomainLibrary");
		
		// test
		assertNotNull(info);
		assertEquals("DomainLibraries", info.getName());
		assertEquals(WebLogicMBeanConstants.OBJECTNAME_ARRAY_TYPE, info.getType());
		assertNotNull(info.getDescriptor());		
    }

    /**
     * Test that attribute info can be retrieved from repository
     * for the attribute: Dommain.Library.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetAttributeInfo_ForLibrary() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "Library");
		
		// test
		assertNotNull(info);
		assertEquals("Libraries", info.getName());
		assertEquals(WebLogicMBeanConstants.OBJECTNAME_ARRAY_TYPE, info.getType());
		assertNotNull(info.getDescriptor());		
    }

    /**
     * Test that attribute info can be retrieved from repository
     * for the attribute: Dommain.WldfSystemResource.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetAttributeInfo_ForDomainWldfSystemResources() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "WLDFSystemResource");
		
		// test
		assertNotNull(info);
		assertEquals("WLDFSystemResources", info.getName());
		assertEquals(WebLogicMBeanConstants.OBJECTNAME_ARRAY_TYPE, info.getType());
		assertNotNull(info.getDescriptor());		
    }

    /**
     * Test that create method with one argument can be retrieved from repository
     * for the attribute: Domain.WldfSystemResource.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetCreateMethodWithOneArg_ForDomainWldfSystemResources() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanOperationInfo info = mbeanMetadataRepository.getCreateMethod(mbeanInfo, "WLDFSystemResource");
		
		// test
		assertNotNull(info);
		assertEquals("createWLDFSystemResource", info.getName());
		
		MBeanParameterInfo[] signature = info.getSignature();		
		assertNotNull(signature);
 		assertNotNull(info.getDescriptor());		
    }

    /**
     * Test that create method for attribute: Dommain.WldfSystemResource
     * has signature with single argument.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCreateMethodHasSingleArgSignature_ForDomainWldfSystemResources() throws Exception
    {
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( domainObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanOperationInfo info = mbeanMetadataRepository.getCreateMethod(mbeanInfo, "WLDFSystemResource");
		
		// test
		assertNotNull(info);
		assertEquals("createWLDFSystemResource", info.getName());
		
		MBeanParameterInfo[] signature = info.getSignature();
		
		assertEquals(1, signature.length);
    }

    /**
     * Test that attribute info can be retrieved from repository
     * for the attribute: WorkManager.WorkmanagerShutdownTrigger.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetAttributeInfo_ForWorkmanagerShutdownTrigger() throws Exception
    {
	   	// create work manager
    	ObjectName wmObjName = jmxMother.createWorkManager(session, randomWorkManagerName);
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( wmObjName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanAttributeInfo info = mbeanMetadataRepository.getAttributeInfo(mbeanInfo, "WorkManagerShutdownTrigger");
		
		// test
		assertNotNull(info);
		assertEquals("WorkManagerShutdownTrigger", info.getName());
		assertEquals(WebLogicMBeanConstants.OBJECTNAME_TYPE, info.getType());
		assertNotNull(info.getDescriptor());		
    }

    /**
     * Test that attribute info can be retrieved from repository
     * for the attribute: WorkManager.WorkmanagerShutdownTrigger.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanGetCreateMethodWithZeroArgs_ForWorkManagerShutdownTrigger() throws Exception
    {
	   	// create work manager
    	ObjectName objName = jmxMother.createWorkManager(session, randomWorkManagerName);
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( objName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data                                                 
		MBeanOperationInfo info = mbeanMetadataRepository.getCreateMethod(mbeanInfo, "WorkManagerShutdownTrigger");
				
		// test		
		assertNotNull(info);
		assertEquals("createWorkManagerShutdownTrigger", info.getName());

		MBeanParameterInfo[] signature = info.getSignature();		
		assertNotNull(signature);
		assertEquals(0, signature.length); // zero arguments		
		assertNotNull(info.getDescriptor());		
    }

    /**
     * Test that create method for attribute: WorkManager.WorkmanagerShutdownTrigger
     * has signature with zero arguments.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCreateMethodHasZeroArgSignature_ForWorkManagerShutdownTrigger() throws Exception
    {
	   	// create work manager
    	ObjectName objName = jmxMother.createWorkManager(session, randomWorkManagerName);
    	
		// get MBean meta data
        MBeanInfo mbeanInfo = session.getMBeanServerConnection().getMBeanInfo( objName );
       
        // add meta data
		mbeanMetadataRepository.addMBean(mbeanInfo);
					
		// get attribute meta data
		MBeanOperationInfo info = mbeanMetadataRepository.getCreateMethod(mbeanInfo, "WorkManagerShutdownTrigger");
		
		// test
		assertNotNull(info);
		assertEquals("createWorkManagerShutdownTrigger", info.getName());
		
		MBeanParameterInfo[] signature = info.getSignature();
		
		assertEquals(0, signature.length);
    }
    
}
