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


package com.alpha.pineapple.plugin.weblogic.jmx.command;

import static org.junit.Assert.fail;

import javax.management.ObjectName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import weblogic.management.configuration.VirtualHostMBean;

import com.alpha.pineapple.context.ManagerContext;
import com.alpha.pineapple.plugin.weblogic.jmx.session.JMXSession;
import com.alpha.springutils.NameAwareTestExecutionListener;
import com.alpha.testutils.ObjectMotherWebLogicJMX;

@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( NameAwareTestExecutionListener .class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class SetMBeanAttributeCommandTest
{

    String testName;

    ManagerContext context;

    ObjectMotherWebLogicJMX jmxMother;

    JMXSession session;

    ObjectName objName;

    VirtualHostMBean vhMBean;

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    
    

    /**
     * Test that command can set attribute of type String.
     */
    @Test
    public void testCanSetStringAttribute()
    {
    	fail();
    }

    /**
     * Test that command can set attribute of type String Array, i.e. String[].
     */
    @Test
    public void testCanSetStringsArrayAttribute3()
    {
    	fail();
    }

    /**
     * Test that command can set attribute of type String Array, i.e. String[].
     */
    @Test
    public void testCanSetStringsArrayAttribute1()
    {
    	fail();    	
    }

    /**
     * Test that command can set attribute of type Integer.
     */
    @Test
    public void testCanSetIntegerAttribute()
    {
    	fail();    	
    }

    /**
     * Test that command fails if attribute can't be found.
     */
    @Test
    public void testFailsIfAttributeCantBeFound()
    {
    	fail();    	
    }

    /**
     * Test that command fails if MbBan can't be found.
     */
    @Test
    public void testFailsIfMBeanCantBeFound()
    {
    	fail();    	
    }

    /**
     * Test that command fails if Mbean attribute isn't writable.
     */
    @Test
    public void testFailsIfAttributeIsntWritable()
    {
    	fail();    	
    }

    /**
     * Test that command fails if MBean attribute isn't writable.
     */
    @Test
    public void testFailsIfAttributeValuesInContextIsntString()
    {
    	fail();    	
    }

}
