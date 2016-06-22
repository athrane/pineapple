/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2013 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 */

package com.alpha.pineapple.plugin.weblogic.jmx.utils;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactoryFactory;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory.JmxProtool;

/**
 * Integration test of the {@linkplain JmxServiceUrlFactoryFactory} class. 
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class JmxServiceUrlFactoryFactoryIntegrationTest {

	/**
	 * Object under test.
	 */
	@Resource(name="jmxServiceUrlFactoryFactory")
	JmxServiceUrlFactoryFactory factory;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

    /**
     * Test that object can be looked up from the context.
     */
    @Test
    public void testCanGetFactoryFromContext()
    {
        assertNotNull( factory);
    }
	
    /**
     * Test that T3 factory can be created.  
     */
	@Test
	public void testGetT3Factory() {		
		JmxServiceUrlFactory urlFactory = factory.getInstance(JmxProtool.T3);
		assertNotNull( urlFactory );
	}

    /**
     * Test that T3S factory can be created.  
     */
	@Test
	public void testGetT3SFactory() {		
		JmxServiceUrlFactory urlFactory = factory.getInstance(JmxProtool.T3S);
		assertNotNull( urlFactory );
	}

    /**
     * Test that IIOP factory can be created.  
     */
	@Test
	public void testGetIiopFactory() {		
		JmxServiceUrlFactory urlFactory = factory.getInstance(JmxProtool.IIOP);
		assertNotNull( urlFactory );
	}

    /**
     * Test that IIOP factory can be created.  
     */
	@Test
	public void testGetIiopsFactory() {		
		JmxServiceUrlFactory urlFactory = factory.getInstance(JmxProtool.IIOPS);
		assertNotNull( urlFactory );
	}

    /**
     * Test that RMI factory can be created.  
     */
	@Test
	public void testGetRmiFactory() {		
		JmxServiceUrlFactory urlFactory = factory.getInstance(JmxProtool.RMI);
		assertNotNull( urlFactory );
	}

    /**
     * Test that HTTP factory can be created.  
     */
	@Test
	public void testGetHttpFactory() {		
		JmxServiceUrlFactory urlFactory = factory.getInstance(JmxProtool.HTTP);
		assertNotNull( urlFactory );
	}

    /**
     * Test that HTTPS factory can be created.  
     */
	@Test
	public void testGetHttpsFactory() {		
		JmxServiceUrlFactory urlFactory = factory.getInstance(JmxProtool.HTTPS);
		assertNotNull( urlFactory );
	}

    /**
     * Test that null value fails.  
     */
	@Test(expected = IllegalArgumentException.class)
	public void testGetFailsWithNullValue() {		
		factory.getInstance(null);
	}
	
}
