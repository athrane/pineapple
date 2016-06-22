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


package com.alpha.pineapple.plugin.weblogic.jmx.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import weblogic.management.mbeanservers.edit.EditServiceMBean;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxUtils;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;


/**
 * Unit test of the JmxUtils class.
 */
public class JmxUtilsIntegrationTest
{

    /**
     * Name of edit service MBean.
     */
    private static final String EDIT_SERVICE_MBEAN = "com.bea:Name=EditService,Type=weblogic.management.mbeanservers.edit.EditServiceMBean";

    /**
     * Object under test. 
     */
    JmxUtils jmxFactory;

	/**
	 * JMX session.
	 */
	WeblogicJMXEditSession session;
    
    /**
     * WeblogicJMXSession object mother.
     */
    ObjectMotherWeblogicJmxSession sessionMother;
        
    @Before
    public void setUp() throws Exception
    {
        jmxFactory = new JmxUtils();
        
        // create session mother
        sessionMother = new ObjectMotherWeblogicJmxSession();
    
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();                
    }

    @After
    public void tearDown() throws Exception
    {
        jmxFactory = null;
    }

    /**
     * Test that local JMX proxy can be created.
     */
    @Test
    public void testCanCreateProxy()
    {        
        try
        {
            // get connection
            MBeanServerConnection connection = session.getMBeanServerConnection();

            // initialize object name
            ObjectName name;
            name = new ObjectName( EDIT_SERVICE_MBEAN );

            // define proxy interface
            Class<EditServiceMBean> interfaceClass = EditServiceMBean.class;

            // create proxy
            EditServiceMBean editServiceMBean;
            editServiceMBean = jmxFactory.createMBeanProxy( connection, name, interfaceClass );

            // test
            assertNotNull( editServiceMBean );

            // clean up
            session.disconnect();
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

}
