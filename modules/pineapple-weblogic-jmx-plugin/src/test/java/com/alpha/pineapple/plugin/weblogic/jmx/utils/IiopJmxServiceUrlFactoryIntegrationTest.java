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

import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.EDIT_SERVER_JNDI_NAME;
import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import javax.annotation.Resource;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory.JmxProtool;

/**
 * Integration test of the {@linkplain IiopJmxServiceUrlFactoryImpl} class. 
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class IiopJmxServiceUrlFactoryIntegrationTest {

	/**
	 * Subject under test. 
	 */
	@Resource(name="iiopJmxServiceUrlFactory")
	JmxServiceUrlFactory factory;
	
	/**
	 * Random host.
	 */
	String randomHost;

	/**
	 * Random port.
	 */
	int randomPort;

	/**
	 * Random user.
	 */
	String randomUser;

	/**
	 * Random password.
	 */
	String randomPassword;
	
	@Before
	public void setUp() throws Exception {		
		randomHost = RandomStringUtils.randomAlphanumeric(10);
		randomPort = RandomUtils.nextInt(65000);		
		randomUser = RandomStringUtils.randomAlphanumeric(10);
		randomPassword = RandomStringUtils.randomAlphanumeric(10);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test connection properties contains expected amount of properties.
	 */
	@Test
	public void testConnectionPropertiesContainsExpectedNumberProperties() {
		Hashtable<String, String> props = factory.createConnectionProperties(randomUser, randomPassword);
		assertEquals(3, props.size());		
		assertEquals(randomUser, props.get(Context.SECURITY_PRINCIPAL));
		assertEquals(randomPassword, props.get(Context.SECURITY_CREDENTIALS));
	}

	/**
	 * Test service URL is created as expected for IIOP.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testCreateEditMBeanServerUrlForIIOP() throws Exception {
		StringBuilder expected = new StringBuilder()
			.append("service:jmx:")
			.append("iiop")
			.append("://")
			.append(randomHost)
			.append(":")
			.append(randomPort)
			.append("/jndi/")
			.append(EDIT_SERVER_JNDI_NAME);		
		
		JMXServiceURL url = factory.createEditMBeanServerUrl(JmxProtool.IIOP, randomHost, randomPort);				
		assertEquals(url.toString(), expected.toString());
	}

	/**
	 * Test service URL is created as expected for IIOPS.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testCreateEditMBeanServerUrlForIIOPS() throws Exception {
		StringBuilder expected = new StringBuilder()
			.append("service:jmx:")
			.append("iiops")
			.append("://")
			.append(randomHost)
			.append(":")
			.append(randomPort)
			.append("/jndi/")
			.append(EDIT_SERVER_JNDI_NAME);		
		
		JMXServiceURL url = factory.createEditMBeanServerUrl(JmxProtool.IIOPS, randomHost, randomPort);				
		assertEquals(url.toString(), expected.toString());
	}
	
	/**
	 * Test service URL is creation fails for other protocols.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testFailsCreateEditMBeanServerUrlForOtherProtocol() throws Exception {
		factory.createEditMBeanServerUrl(JmxProtool.HTTP, randomHost, randomPort);
	}
	
}