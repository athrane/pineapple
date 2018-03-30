/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2016 Allan Thrane Andersen..
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
 *   
 */

package com.alpha.pineapple.web.context;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.javautils.SystemUtils;

/**
 * Unit test of the {@linkplain PineappleHomeInitializer} class.
 */
public class PineappleHomeInitializerTest {

    /**
     * subject under test.
     * 
     */
    PineappleHomeInitializer initializer;

    /**
     * Mock Java System properties.
     */
    Properties systemProperties;

    /**
     * Mock Java system utilities.
     */
    SystemUtils systemUtils;

    /**
     * Mock servlet context event.
     */
    ServletContextEvent sce;

    /**
     * Random value.
     */
    String randomValue;

    /**
     * Random value.
     */
    String randomValue2;

    /**
     * Random value.
     */
    String randomSystemProperty;

    /**
     * Random value.
     */
    String randomSystemProperty2;

    @Before
    public void setUp() throws Exception {

	randomValue = RandomStringUtils.randomAlphabetic(10);
	randomValue2 = RandomStringUtils.randomAlphabetic(10);
	randomSystemProperty = RandomStringUtils.randomAlphabetic(10);
	randomSystemProperty2 = RandomStringUtils.randomAlphabetic(10);

	// create mock event
	sce = createMock(ServletContextEvent.class);

	// create mock properties
	systemProperties = createMock(Properties.class);

	// create mock system utils
	systemUtils = createMock(SystemUtils.class);

	// create initializer
	initializer = new PineappleHomeInitializer(systemUtils, systemProperties);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test initialization when pineapple home is defined in advance.
     */
    @Test
    public void testInitializationWhenPineappleIsDefined() {

	// complete mock setup
	replay(sce);

	// complete mock setup
	expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(true);
	replay(systemUtils);

	// complete mock setup
	replay(systemProperties);

	// test
	initializer.contextInitialized(sce);

	// test
	verify(sce);
	verify(systemUtils);
	verify(systemProperties);
    }

    /**
     * Test initialization when pineapple home isn't defined and web.xml returns
     * a null value.
     */
    @Test
    public void testInitializationWhenWebXmlValueIsNull() {

	String expected = randomValue2 + "/.pineapple";

	// complete mock setup
	ServletContext context = createMock(ServletContext.class);
	expect(context.getInitParameter(PineappleHomeInitializer.PARAM_NAME)).andReturn(null);
	replay(context);

	// complete mock setup
	expect(sce.getServletContext()).andReturn(context);
	replay(sce);

	// complete mock setup
	expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
	expect(systemUtils.getSystemProperty("user.home", systemProperties)).andReturn(randomValue2);
	replay(systemUtils);

	// complete mock setup
	expect(systemProperties.setProperty(eq(PineappleHomeInitializer.PARAM_NAME), eq(expected))).andReturn(expected);
	replay(systemProperties);

	// test
	initializer.contextInitialized(sce);

	// test
	verify(sce);
	verify(context);
	verify(systemUtils);
	verify(systemProperties);
    }

    /**
     * Test initialization when pineapple home isn't defined and web.xml returns
     * a value.
     */
    @Test
    public void testInitializationWhenWebXmlValueIsDefined() {

	String expected = randomValue2 + "/.pineapple";
	String webXmlValue = "${user.home}/.pineapple";

	// complete mock setup
	ServletContext context = createMock(ServletContext.class);
	expect(context.getInitParameter(PineappleHomeInitializer.PARAM_NAME)).andReturn(webXmlValue);
	replay(context);

	// complete mock setup
	expect(sce.getServletContext()).andReturn(context);
	replay(sce);

	// complete mock setup
	expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
	expect(systemUtils.getSystemProperty("user.home", systemProperties)).andReturn(randomValue2);
	replay(systemUtils);

	// complete mock setup
	expect(systemProperties.setProperty(eq(PineappleHomeInitializer.PARAM_NAME), eq(expected))).andReturn(expected);
	replay(systemProperties);

	// test
	initializer.contextInitialized(sce);

	// test
	verify(sce);
	verify(context);
	verify(systemUtils);
	verify(systemProperties);
    }

    /**
     * Test initialization when pineapple home isn't defined and web.xml returns
     * a value.
     */
    @Test
    public void testInitializationWhenWebXmlValueIsDefined2() {

	String expected = randomValue2 + "/.pineapple";
	String webXmlValue = "${" + randomSystemProperty + "}/.pineapple";

	// complete mock setup
	ServletContext context = createMock(ServletContext.class);
	expect(context.getInitParameter(PineappleHomeInitializer.PARAM_NAME)).andReturn(webXmlValue);
	replay(context);

	// complete mock setup
	expect(sce.getServletContext()).andReturn(context);
	replay(sce);

	// complete mock setup
	expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
	expect(systemUtils.getSystemProperty(randomSystemProperty, systemProperties)).andReturn(randomValue2);
	replay(systemUtils);

	// complete mock setup
	expect(systemProperties.setProperty(eq(PineappleHomeInitializer.PARAM_NAME), eq(expected))).andReturn(expected);
	replay(systemProperties);

	// test
	initializer.contextInitialized(sce);

	// test
	verify(sce);
	verify(context);
	verify(systemUtils);
	verify(systemProperties);
    }

    /**
     * Test initialization when pineapple home isn't defined and web.xml returns
     * a value.
     */
    @Test
    public void testInitializationWhenWebXmlValueReturnsMultipleVariables() {

	String expected = randomValue2 + "/" + randomValue + "/.pineapple";
	String webXmlValue = "${" + randomSystemProperty + "}/${" + randomSystemProperty2 + "}/.pineapple";

	// complete mock setup
	ServletContext context = createMock(ServletContext.class);
	expect(context.getInitParameter(PineappleHomeInitializer.PARAM_NAME)).andReturn(webXmlValue);
	replay(context);

	// complete mock setup
	expect(sce.getServletContext()).andReturn(context);
	replay(sce);

	// complete mock setup
	expect(systemUtils.isPineappleHomeDefined(systemProperties)).andReturn(false);
	expect(systemUtils.getSystemProperty(randomSystemProperty, systemProperties)).andReturn(randomValue2);
	expect(systemUtils.getSystemProperty(randomSystemProperty2, systemProperties)).andReturn(randomValue);
	replay(systemUtils);

	// complete mock setup
	expect(systemProperties.setProperty(eq(PineappleHomeInitializer.PARAM_NAME), eq(expected))).andReturn(expected);
	replay(systemProperties);

	// test
	initializer.contextInitialized(sce);

	// test
	verify(sce);
	verify(context);
	verify(systemUtils);
	verify(systemProperties);
    }

}
