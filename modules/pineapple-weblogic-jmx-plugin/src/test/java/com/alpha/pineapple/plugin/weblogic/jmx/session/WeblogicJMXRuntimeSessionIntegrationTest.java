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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.management.MBeanServerConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.session.SessionDisconnectException;
import com.alpha.testutils.ObjectMotherWebLogicJMX;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;

/**
 * Integration test for WeblogicJMXEditSession.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class WeblogicJMXRuntimeSessionIntegrationTest
{
	/**
	 * Object under test. 
	 */
    WeblogicJMXRuntimeSession session;

    /**
     * WeblogicJMXSession object mother.
     */
    ObjectMotherWeblogicJmxSession sessionMother;
    
    /**
     * WebLogic JMX object mother.
     */
    ObjectMotherWebLogicJMX jmxMother;		

    @Before
    public void setUp() throws Exception
    {
        // create session mother
        sessionMother = new ObjectMotherWeblogicJmxSession();

        // create session      
        session = sessionMother.createConnectedWlsJmxRuntimeSession();
        
		// create JMX mother
		jmxMother = new ObjectMotherWebLogicJMX();                		        
    }

    @After
    public void tearDown() throws Exception
    {
    	if(session != null) {    		
    		if (session.isConnected()) {
    	    	session.disconnect();
    		}    		
    	}    	
    	
        session.disconnect();
        session = null;
    }


    /**
     * Test that connection is defined after connect.
     */
    @Test
    public void testJmxConnectionIsDefinedAfterConnect()
    {
        MBeanServerConnection connection = session.getMBeanServerConnection();
        assertNotNull( connection );
    }

    /**
     * Test that IsConnected() returns true after connect.
     */
    @Test
    public void testIsConnectedQueryReturnsTrueAfterConnect()
    {
        assertTrue( session.isConnected() );
    }

    /**
     * Test that IsConnected() returns false after connect.
     * 
     * @throws Exception If test fails. 
     */
    @Test
    public void testIsConnectedQueryReturnsFalseAfterDisconnect() throws Exception
    {
        // test
        assertTrue( session.isConnected() );
        
        // disconnect
        session.disconnect();

        // test
        assertFalse( session.isConnected() );        
    }
    
    /**
     * Test that connected JMX session can disconnect from WebLogic.
     * 
     * @throws SessionDisconnectException if test fails.
     */
    @Test
    public void testDisconnectFromWeblogic() throws SessionDisconnectException {
    	session.disconnect();
    }

}
