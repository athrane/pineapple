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
 *   
 */

package com.alpha.javautils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Unit test for the {@linkplain SystemUtils} class.
 */
public class SystemUtilsTest {

    /**
     * Mock Java System properties.
     */
    Properties systemProperties;

    /**
     * Random value.
     */
    String randomValue;

    /**
     * Object under test.
     */
    SystemUtils systemUtils;

    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;

    @Before
    public void setUp() throws Exception {

	randomValue = RandomStringUtils.randomAlphabetic(10);

	// create mock properties
	systemProperties = createMock(Properties.class);

	// create utils
	systemUtils = new SystemUtils();

	// create mock provider
	messageProvider = EasyMock.createMock(MessageProvider.class);

	// inject message provider
	ReflectionTestUtils.setField(systemUtils, "messageProvider", messageProvider);

	// complete mock source initialization
	IAnswer<String> answer = new MessageProviderAnswerImpl();
	EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
	EasyMock.expectLastCall().andAnswer(answer).anyTimes();
	EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class),
		(Object[]) EasyMock.isA(Object[].class)));
	EasyMock.expectLastCall().andAnswer(answer).anyTimes();
	EasyMock.replay(messageProvider);

    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Fails if system properties is undefined.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIsWindowsOperatingSystemFailsIfSystemPropertiesIsUndefined() {

	// complete mock setup
	replay(systemProperties);

	// invoke
	systemUtils.isWindowsOperatingSystem(null);

	// test
	verify(systemProperties);
    }

    /**
     * Returns true if OS is windows.
     */
    @Test
    public void testIsWindowsOperatingSystemSucceeds() {

	// complete mock setup
	expect(systemProperties.getProperty("os.name")).andReturn(systemUtils.OS_NAME_WINDOWS_PREFIX);
	replay(systemProperties);

	// test
	assertTrue(systemUtils.isWindowsOperatingSystem(systemProperties));

	// test
	verify(systemProperties);
    }

    /**
     * Returns false if OS isn't windows.
     */
    @Test
    public void testIsWindowsOperatingSystemFails() {

	// complete mock setup
	expect(systemProperties.getProperty("os.name")).andReturn(randomValue);
	replay(systemProperties);

	// test
	assertFalse(systemUtils.isWindowsOperatingSystem(systemProperties));

	// test
	verify(systemProperties);
    }

    /**
     * Fails if system properties is undefined.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIsPineappleHomeDefinedFailsIfsystemPropertiesIsUndefined() {

	// complete mock setup
	replay(systemProperties);

	// invoke
	systemUtils.isPineappleHomeDefined(null);

	// test
	verify(systemProperties);
    }

    /**
     * Returns true if Pineapple home system property is defined.
     */
    @Test
    public void testIsPineappleHomeDefinedSucceeds() {

	// complete mock setup
	expect(systemProperties.getProperty(systemUtils.PINEAPPLE_HOMEDIR)).andReturn(randomValue);
	replay(systemProperties);

	// test
	assertTrue(systemUtils.isPineappleHomeDefined(systemProperties));

	// test
	verify(systemProperties);
    }

    /**
     * Returns fails if Pineapple home system property is defined.
     */
    @Test
    public void testIsPineappleHomeDefinedFails() {

	// complete mock setup
	expect(systemProperties.getProperty(systemUtils.PINEAPPLE_HOMEDIR)).andReturn(null);
	replay(systemProperties);

	// test
	assertFalse(systemUtils.isPineappleHomeDefined(systemProperties));

	// test
	verify(systemProperties);
    }

    /**
     * Fails if system properties is undefined.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetSystemPropertyFailsIfSystemPropertiesIsUndefined() {

	// complete mock setup
	replay(systemProperties);

	// invoke
	systemUtils.getSystemProperty(randomValue, null);

	// test
	verify(systemProperties);
    }

    /**
     * Fails if property is undefined.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetSystemPropertyFailsIfPropertyIsUndefined() {

	// complete mock setup
	replay(systemProperties);

	// invoke
	systemUtils.getSystemProperty(null, systemProperties);

	// test
	verify(systemProperties);
    }

    /**
     * Fails if property throws security exception .
     */
    @Test
    public void testGetSystemPropertyFailsIfSecurityExceptionIsThrown() {

	// complete mock setup
	Throwable exception = new SecurityException();
	expect(systemProperties.getProperty(randomValue)).andThrow(exception);
	replay(systemProperties);

	// invoke
	assertEquals(null, systemUtils.getSystemProperty(randomValue, systemProperties));

	// test
	verify(systemProperties);
    }

}
