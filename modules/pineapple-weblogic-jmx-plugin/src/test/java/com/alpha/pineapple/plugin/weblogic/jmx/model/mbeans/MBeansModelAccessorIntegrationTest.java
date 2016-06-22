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

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.testutils.ObjectMotherWebLogicJMX;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;

/**
 * Integration test for <code>MBeansModelAccessorImpl</code>.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class MBeansModelAccessorIntegrationTest
{
	/**
	 * GetName method name. 
	 */
	static final String GETNAME_METHOD = "getName";

	/**
	 * Attribute named "Name".
	 */
	static final String ATTRIBUTE_NAME = "Name";

	/**
	 * First array index.
	 */
	static final int FIRST_INDEX = 0;

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
    @Resource(name="mbeansModelAccessor")
    MBeansModelAccessor accessor;

    /**
     * Some random WLDF name.
     */
	String randomWldfName;
    
    /** 
     * WebLogic JMX session.
     */
    WeblogicJMXEditSession session;
    
    @Before
    public void setUp() throws Exception
    {
    	randomWldfName = RandomStringUtils.randomAlphabetic(10)+"-wldf";
    	
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
    	// delete WLDF System Resource
    	jmxMother.deleteWldfSystemResource(session, randomWldfName);
    	
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
    public void testCanGetAccessorFromContext()
    {
        assertNotNull( accessor );
    }

            
    
}
