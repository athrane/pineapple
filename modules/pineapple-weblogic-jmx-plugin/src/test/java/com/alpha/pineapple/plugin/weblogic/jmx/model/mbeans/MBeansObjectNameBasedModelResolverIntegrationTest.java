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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant.ValueState;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolver;
import com.alpha.testutils.ObjectMotherWebLogicJMX;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;
import com.alpha.testutils.WJPIntTestConstants;

/**
 * Integration test for <code>MBeansObjectNameBasedModelResolverImpl</code>.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class MBeansObjectNameBasedModelResolverIntegrationTest
{

	/**
	 * First array index.
	 */
    static final int FIRST_INDEX = 0;

	/**
     * Object under test.
     */
    @Resource(name="mbeansObjectNameBasedModelResolver")
    ModelResolver resolver;
    
	/**
	 * JMX session.
	 */
	WeblogicJMXEditSession session;
    
    /**
     * WeblogicJMXSession object mother.
     */
    ObjectMotherWeblogicJmxSession sessionMother;
    
    /**
     * WebLogic JMX object mother.
     */
    ObjectMotherWebLogicJMX jmxMother;		
    
    /**
     * Random name.
     */
    String randomName;
    
    /**
     * Random name.
     */
    String randomVirtualHostName;

    /**
     * Random name.
     */
    String randomWorkManagerName;    
    
    @Before
    public void setUp() throws Exception
    {
    	randomName = RandomStringUtils.randomAlphabetic(10);
    	randomVirtualHostName = RandomStringUtils.randomAlphabetic(10) +"-vh";
    	randomWorkManagerName = RandomStringUtils.randomAlphabetic(10) +"-wm";
    	
        // create session mother
        sessionMother = new ObjectMotherWeblogicJmxSession();
    
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();        
        		
		// start edit mode
		session.startEdit();
        
		// create JMX mother
		jmxMother = new ObjectMotherWebLogicJMX();
        				
		// set session
		resolver.setSession(session);
    }

    @After
    public void tearDown() throws Exception
    {
		// delete stuff if it exists
		jmxMother.deleteVirtualHost(session, randomVirtualHostName);
		jmxMother.deleteWorkManager(session, randomWorkManagerName);
    	
		sessionMother = null;
		
		// destroy session
		if (session != null) {
			session.disconnect();
		}
		session = null;    	
		jmxMother = null;        
    }

    /**
     * Test that object can be looked up from the context.
     */
    @Test
    public void testCanGetResolverFromContext()
    {
        // test 
        assertNotNull( resolver );
    }    
    
    /**
     * Test that an integer attribute can be successfully resolved.
     */
    @Test
    public void testResolveIntegerAttribute()
    {    	    	
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createDomainObjName_ResolvedParticipant(session);    	
    	
    	// resolve attribute
    	ResolvedParticipant result = resolver.resolveAttribute("AdministrationPort", participant);
    	        	
        // test 
        assertNotNull( result );
        assertTrue( result.isResolutionSuccesful() );        
        assertEquals( "AdministrationPort", result.getName() );
        assertNotNull( result.getType() );
        assertTrue( result.getType() instanceof MBeanAttributeInfo );
        
        // get type 
        MBeanAttributeInfo info = (MBeanAttributeInfo) result.getType();  
        
        // test 
        assertEquals( Integer.class.getCanonicalName(), info.getType() );
        assertEquals( 9002, result.getValue() );        
    }    

    /**
     * Test that an string attribute can be successfully resolved.
     */
    @Test
    public void testResolveStringAttribute()
    {    	    	
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createDomainObjName_ResolvedParticipant(session);    	
    	
    	// resolve attribute
    	ResolvedParticipant result = resolver.resolveAttribute("Name", participant);
    	        	
        // test 
        assertNotNull( result );
        assertTrue( result.isResolutionSuccesful() );        
        assertEquals( "Name", result.getName() );
        assertNotNull( result.getType() );
        assertTrue( result.getType() instanceof MBeanAttributeInfo );

        // get type 
        MBeanAttributeInfo info = (MBeanAttributeInfo) result.getType();  
        
        // test         
        assertEquals( String.class.getCanonicalName(), info.getType() );
        assertEquals( WJPIntTestConstants.targetEnvironment, result.getValue() );        
    }    

    /**
     * Test that an boolean attribute can be successfully resolved.
     */
    @Test
    public void testResolveBooleanAttribute()
    {    	    	
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createDomainObjName_ResolvedParticipant(session);    	
    	
    	// resolve attribute
    	ResolvedParticipant result = resolver.resolveAttribute("ConsoleEnabled", participant);
    	        	
        // test 
        assertNotNull( result );
        assertTrue( result.isResolutionSuccesful() );        
        assertEquals( "ConsoleEnabled", result.getName() );
        assertNotNull( result.getType() );
        assertTrue( result.getType() instanceof MBeanAttributeInfo );

        // get type 
        MBeanAttributeInfo info = (MBeanAttributeInfo) result.getType();  
        
        // test                 
        assertEquals( Boolean.class.getCanonicalName(), info.getType() );
        assertEquals( true, result.getValue() );        
    }    

    /**
     * Test that an object name attribute can be successfully resolved.
     */
    @Test
    public void testResolveObjectNameAttribute()
    {    	    	
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createDomainObjName_ResolvedParticipant(session);    	
    	    	    	
    	// resolve attribute
    	ResolvedParticipant result = resolver.resolveAttribute("SecurityConfiguration", participant);
    	        	
        // test 
        assertNotNull( result );
        assertTrue( result.isResolutionSuccesful() );        
        assertEquals( "SecurityConfiguration", result.getName() );
        assertNotNull( result.getType() );
        assertTrue( result.getType() instanceof MBeanAttributeInfo );

        // get type 
        MBeanAttributeInfo info = (MBeanAttributeInfo) result.getType();  

        // test                         
        assertEquals( ObjectName.class.getCanonicalName(), info.getType() );
        assertEquals( "com.bea:Name="+WJPIntTestConstants.targetEnvironment+",Type=SecurityConfiguration", result.getValue().toString() );        
    }    

    /**
     * Test that an object name attribute can be successfully resolved.
     */
    @Test
    public void testResolveObjectNameAttributeWithLowerCase()
    {    	    	
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createDomainObjName_ResolvedParticipant(session);    	
    	    	    	
    	// resolve attribute
    	ResolvedParticipant result = resolver.resolveAttribute("securityconfiguration", participant);
    	        	
        // test 
        assertNotNull( result );
        assertTrue( result.isResolutionSuccesful() );        
        assertEquals( "SecurityConfiguration", result.getName() );
        assertNotNull( result.getType() );
        assertTrue( result.getType() instanceof MBeanAttributeInfo );

        // get type 
        MBeanAttributeInfo info = (MBeanAttributeInfo) result.getType();  

        // test                         
        assertEquals( ObjectName.class.getCanonicalName(), info.getType() );
        assertEquals( "com.bea:Name="+WJPIntTestConstants.targetEnvironment+",Type=SecurityConfiguration", result.getValue().toString() );        
    }    
    
    /**
     * Test that an object name attribute can be successfully resolved.
     */
    @Test
    public void testResolveObjectNameAttribute2()
    {    	    	
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createDomainObjName_ResolvedParticipant(session);    	
    	    	    	
    	// resolve attribute
    	ResolvedParticipant result = resolver.resolveAttribute("Jmx", participant);
    	        	
        // test 
        assertNotNull( result );
        assertTrue( result.isResolutionSuccesful() );        
        assertEquals( "JMX", result.getName() );
        assertNotNull( result.getType() );
        assertTrue( result.getType() instanceof MBeanAttributeInfo );

        // get type 
        MBeanAttributeInfo info = (MBeanAttributeInfo) result.getType();  

        // test                         
        assertEquals( ObjectName.class.getCanonicalName(), info.getType() );
        assertEquals( "com.bea:Name="+WJPIntTestConstants.targetEnvironment+",Type=JMX", result.getValue().toString() );        
    }    

    /**
     * Test that an object name attribute can be successfully resolved
     * for VirtualHost.VirtualHostNames, ...Names with an S, i.e. plural.
     * @throws Exception If test fails.
     */
    @Test
    public void testResolveObjectNameAttributeForVirtuaHostVirtuaHostNames() throws Exception
    {    	    	
		// create virtual host 
		jmxMother.createVirtualHost(session, randomVirtualHostName);

		// start edit session
		jmxMother.startEdit(session);		
				
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createVirtualHostObjName_ResolvedParticipant(session, randomVirtualHostName);
    	
    	    	    	
    	// resolve attribute
    	ResolvedParticipant result = resolver.resolveAttribute("VirtualHostNames", participant);
    	        	
        // test 
        assertNotNull( result );
        assertTrue( result.isResolutionSuccesful() );        
        assertEquals( "VirtualHostNames", result.getName() );
        assertNotNull( result.getType() );
        assertTrue( result.getType() instanceof MBeanAttributeInfo );
    }    
    

    /**
     * Test that an object name attribute can be successfully resolved
     * for VirtualHost.VirtualHostName, ...Names without an S, i.e singular.  
     * @throws Exception If test fails.
     */
    @Test
    public void testResolveObjectNameAttributeForVirtuaHostVirtuaHostName() throws Exception
    {    	    	
		// create virtual host 
		jmxMother.createVirtualHost(session, randomVirtualHostName);

		// start edit session
		jmxMother.startEdit(session);		
				
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createVirtualHostObjName_ResolvedParticipant(session, randomVirtualHostName);
    	    	    	    	
    	// resolve attribute
    	ResolvedParticipant result = resolver.resolveAttribute("VirtualHostName", participant);
    	        	
        // test 
        assertNotNull( result );
        assertTrue( result.isResolutionSuccesful() );        
        assertEquals( "VirtualHostNames", result.getName() );
        assertNotNull( result.getType() );
        assertTrue( result.getType() instanceof MBeanAttributeInfo );
    }    
    
    /**
     * Test that an object name attribute can be successfully resolved
     * with a null value in the MBean model (e.g. it is null).
     * Example: WorkManager.WorkManagerShutDownTrigger.
     */
    @Test
    public void testResolveObjectNameAttributeWithNullValue_ForWorkManagerShutDownTrigger() throws Exception
    {    	    	
		// create virtual host 
		jmxMother.createWorkManager(session, randomWorkManagerName);

		// start edit session
		jmxMother.startEdit(session);		
				
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createWorkManager_ResolvedParticipant(session, randomWorkManagerName);
    	
    	// resolve attribute
    	ResolvedParticipant result = resolver.resolveAttribute("WorkManagerShutdownTrigger", participant);
    	        	
        // test 
        assertNotNull( result );        
        assertTrue( result.isResolutionSuccesful() );        
        assertEquals( "WorkManagerShutdownTrigger", result.getName() );
        assertNotNull( result.getType() );
        assertTrue( result.getType() instanceof MBeanAttributeInfo );
        assertNull( result.getValue() );
        assertEquals( ValueState.NIL, result.getValueState());
    }    
    
    
    
    
    /**
     * Test that an collection attribute with component type object name can be successfully resolved.
     */
    @Test
    public void testResolveObjectNameCollectionAttribute()
    {    	    	
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createDomainObjName_ResolvedParticipant(session);    	
    	
    	// resolve attribute
    	ResolvedParticipant result = resolver.resolveAttribute("Servers", participant);
    	        	
        // test 
        assertNotNull( result );
        assertTrue( result.isResolutionSuccesful() );        
        assertEquals( "Servers", result.getName() );
        assertNotNull( result.getType() );
        assertTrue( result.getType() instanceof MBeanAttributeInfo );

        // get type 
        MBeanAttributeInfo info = (MBeanAttributeInfo) result.getType();  

        // test                                 
        assertEquals( ObjectName[].class.getName(), info.getType() );        
        ObjectName[] actualValue = (ObjectName[]) result.getValue();        
        assertEquals( 1, actualValue.length );        
    }    

    /**
     * Test that an collection attribute with component type object 
     * name can be successfully resolved.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testResolveObjectNameCollectionAttributeForAuthenticationProviders() throws Exception
    {    	    	
    	// create participant
    	ResolvedParticipant participant = jmxMother.createAuthenticationProviderCollection_ResolvedParticipant(session);    	
    	
    	// resolve attribute
    	HashMap<String, ResolvedParticipant> result = resolver.resolveCollectionAttributeValues(participant);
    	        	
        // test 
        assertNotNull( result );
        assertEquals( 2, result.size() );
        
        for (ResolvedParticipant collectionValue : result.values()) {
            assertTrue( collectionValue.isResolutionSuccesful() );
            assertNotNull( collectionValue.getType() );
            assertTrue( collectionValue.getType() instanceof MBeanAttributeInfo );
        }        
    }    
        
    /**
     * Test that the collection attribute values can can be successfully resolved.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testResolveObjectNameCollectionValuesWithOneEntry() throws Exception
    {    	    	
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createServersObjNameCollection_ResolvedParticipant(session);    	
    	
    	// resolve collection values
    	HashMap<String, ResolvedParticipant> result = resolver.resolveCollectionAttributeValues(participant);
    	        	
        // test 
        assertNotNull( result );        
        assertEquals( 1, result.size() );
        
        for (ResolvedParticipant collectionValue : result.values()) {
            assertTrue( collectionValue.isResolutionSuccesful() );
            assertNotNull( collectionValue.getType() );
            assertTrue( collectionValue.getType() instanceof MBeanAttributeInfo );
        }
    }    

    /**
     * Test that the first collection value is successfully resolved.
     * 
     * @throws Exception If test fails.
     */    
    @SuppressWarnings("unchecked")
	@Test
    public void testFirstCollectionValueIsSuccessfullyResolved() throws Exception
    {    	    	
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createServersObjNameCollection_ResolvedParticipant(session);    	
    	
    	// resolve collection values
    	HashMap<String, ResolvedParticipant> result = resolver.resolveCollectionAttributeValues(participant);
    	        	
        // test 
        assertNotNull( result );
        assertEquals( 1, result.size() );     

        for (ResolvedParticipant collectionValue : result.values()) {
            assertTrue( collectionValue.isResolutionSuccesful() );
            assertNotNull( collectionValue.getType() );
            assertTrue( collectionValue.getType() instanceof MBeanAttributeInfo );
        }
        
        // get values as array
        Collection<ResolvedParticipant> values = result.values();
        ResolvedParticipant[] array = (ResolvedParticipant[]) values.toArray(new ResolvedParticipant[values.size()]);
        
        // test first collection value
        ResolvedParticipant firstParticipant = array[FIRST_INDEX];        
        assertEquals(WJPIntTestConstants.administrationServerName, firstParticipant.getName());        
    }    
    
    
    /**
     * Test that the collection attribute values can can be successfully resolved.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testResolveObjectNameCollectionValuesWithZeroEntries() throws Exception
    {    	    	
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createMachinesObjNameCollection_ResolvedParticipant(session);    	
    	
    	// resolve collection values
    	HashMap<String, ResolvedParticipant> result = resolver.resolveCollectionAttributeValues(participant);
    	        	
        // test 
        assertNotNull( result );
        assertEquals( 0, result.size() );        
    }    

    /**
     * Test that an collection attribute with the name "Server" can be successfully resolved.
     * 
     * Domain.Server is the XML bean model name, versus 
     * Domain.Server which is the MBean model name.
     * 
     */
    @Test
    public void testResolveObjectNameServerAttribute()
    {    	    	
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createDomainObjName_ResolvedParticipant(session);    	
    	    	    	
    	// resolve attribute
    	ResolvedParticipant result = resolver.resolveAttribute("Server", participant);

        // test 
        assertNotNull( result );
        assertTrue( result.isResolutionSuccesful() );        
        assertEquals( "Servers", result.getName() );
        assertNotNull( result.getType() );
        assertTrue( result.getType() instanceof MBeanAttributeInfo );

        // get type 
        MBeanAttributeInfo info = (MBeanAttributeInfo) result.getType();  

        // test                                 
        assertEquals( ObjectName[].class.getName(), info.getType() );        
        ObjectName[] actualValue = (ObjectName[]) result.getValue();        
        assertEquals( 1, actualValue.length );            	
    }    

    /**
     * Test that an random attribute fails to be resolved
     * since it doesn't exist on the MBean.
     */
    @Test
    public void testFailsToResolveAttribute()
    {    	    	
    	// create participant with MBean
    	ResolvedParticipant participant = jmxMother.createDomainObjName_ResolvedParticipant(session);    	
    	
    	// resolve attribute
    	ResolvedParticipant result = resolver.resolveAttribute(randomName, participant);
    	        	
        // test 
        assertNotNull( result );
        assertFalse( result.isResolutionSuccesful() );        
        assertNull( result.getName() );
        assertNull( result.getType() );
        assertNull( result.getValue() );
        assertNotNull( result.getResolutionError() );        
        assertTrue( result.getResolutionError() instanceof ModelResolutionFailedException );
    }    

    
}
