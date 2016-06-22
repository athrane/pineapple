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

package com.alpha.pineapple.i18n;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * unit test of the class <code>PropertyFileMessageProviderImpl</code>.
 */
public class PropertyFileMessageProviderTest {

    /**
     * resource bundle name.
     */
    static final String BASE_NAME_1 = "test1";

    /**
     * resource bundle name.
     */
    static final String NOEXISTING_BASE_NAME = "non.existing";

    /**
     * Object under test.
     */
    PropertyFileMessageProviderImpl provider;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
	provider = null;
    }

    /**
     * Test that instance can be created.
     * 
     * @throws MessageProviderInitializationException
     *             If test fails.
     */
    @Test
    public void testCanCreateInstance() throws MessageProviderInitializationException {

	provider = new PropertyFileMessageProviderImpl();
    }

    /**
     * Test that resource bundle can be initialized.
     * 
     * @throws MessageProviderInitializationException
     *             If test fails.
     */
    @Test
    public void testCanInitializeResourceBundle() throws MessageProviderInitializationException {

	provider = new PropertyFileMessageProviderImpl();
	provider.setBasename(BASE_NAME_1);
    }

    /**
     * Test that provider initialization fails if property file doesn't exist.
     * 
     * @throws MessageProviderInitializationException
     *             If test fails.
     */
    @Test(expected = MessageProviderInitializationException.class)
    public void testFailsIfPropertyFileDoesntExist() throws MessageProviderInitializationException {

	provider = new PropertyFileMessageProviderImpl();
	provider.setBasename(NOEXISTING_BASE_NAME);
    }

    /**
     * Test that provider initialization fails if property file is null.
     * 
     * @throws MessageProviderInitializationException
     *             If test fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFailsIfPropertyFileIsUndefined() throws MessageProviderInitializationException {

	provider = new PropertyFileMessageProviderImpl();
	provider.setBasename(null);
    }

    /**
     * Test that message with no argument can be resolved.
     * 
     * @throws MessageProviderInitializationException
     */
    @Test
    public void testCanGetMessageWithNoArgs() throws MessageProviderInitializationException {

	// create provider
	provider = new PropertyFileMessageProviderImpl();
	provider.setBasename(BASE_NAME_1);

	// get key1 from bundle
	String value = provider.getMessage("key1");

	// test
	assertEquals("value1", value);
    }

    /**
     * Test that getMessage() method throws exception if key is null.
     * 
     * @throws MessageProviderInitializationException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCanGetMessageWithNoArgsFailsIfKeyIsNull() throws Exception {

	// create provider
	provider = new PropertyFileMessageProviderImpl();
	provider.setBasename(BASE_NAME_1);

	// get key1 from bundle
	String value = provider.getMessage(null);
    }

    /**
     * Test that getMessage() method throws exception if key is null.
     * 
     * @throws MessageProviderInitializationException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCanGetMessageFailsIfKeyIsNull() throws Exception {

	// create provider
	provider = new PropertyFileMessageProviderImpl();
	provider.setBasename(BASE_NAME_1);

	// get key1 from bundle
	String value = provider.getMessage(null, null);
    }

    /**
     * Test that getMessage() method throws exception if key is null.
     * 
     * @throws MessageProviderInitializationException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCanGetMessageFailsIfKeyIsNull2() throws Exception {

	// create provider
	provider = new PropertyFileMessageProviderImpl();
	provider.setBasename(BASE_NAME_1);

	// get key1 from bundle
	Object[] args = { "param0", "param1" };
	String value = provider.getMessage(null, args);
    }

    /**
     * Test that message with single argument can be resolved.
     * 
     * @throws MessageProviderInitializationException
     */
    @Test
    public void testCanGetMessageWithOneArg() throws MessageProviderInitializationException {

	// create provider
	provider = new PropertyFileMessageProviderImpl();
	provider.setBasename(BASE_NAME_1);

	// get key1 from bundle
	Object[] args = { "XXX" };
	String value = provider.getMessage("key2", args);

	// test
	assertEquals("value is [XXX].", value);
    }

    /**
     * Test that message with two arguments can be resolved.
     * 
     * @throws MessageProviderInitializationException
     */
    @Test
    public void testCanGetMessageWithTwoArgs() throws MessageProviderInitializationException {

	// create provider
	provider = new PropertyFileMessageProviderImpl();
	provider.setBasename(BASE_NAME_1);

	// get key1 from bundle
	Object[] args = { "XXX", "YYY" };
	String value = provider.getMessage("key3", args);

	// test
	assertEquals("value is [XXX] and [YYY].", value);
    }

    /**
     * Test that message with three arguments can be resolved.
     * 
     * @throws MessageProviderInitializationException
     */
    @Test
    public void testCanGetMessageWithThreergs() throws MessageProviderInitializationException {

	// create provider
	provider = new PropertyFileMessageProviderImpl();
	provider.setBasename(BASE_NAME_1);

	// get key1 from bundle
	Object[] args = { "XXX", "YYY", "ZZZ" };
	String value = provider.getMessage("key4", args);

	// test
	assertEquals("value is [XXX], [YYY] and [ZZZ].", value);
    }

}
