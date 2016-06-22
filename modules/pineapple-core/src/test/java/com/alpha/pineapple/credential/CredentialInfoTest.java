/*******************************************************************************

 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.credential;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the class {@link CredentialInfoImpl }.
 */
public class CredentialInfoTest {
    /**
     * Object under test.
     */
    CredentialInfo info;

    /**
     * Random ID.
     */
    String randomId;

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
	randomId = RandomStringUtils.randomAlphabetic(10);
	randomUser = RandomStringUtils.randomAlphabetic(10);
	randomPassword = RandomStringUtils.randomAlphabetic(10);
    }

    @After
    public void tearDown() throws Exception {
	info = null;
    }

    /**
     * Constructor test, i.e. that cache entry can be created.
     */
    @Test
    public void testCanCreateInstance() {
	info = new CredentialInfoImpl(randomId, randomUser, randomPassword);

	// test
	assertNotNull(info);
    }

    /**
     * Constructor rejects undefined id.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsUndefinedId() {
	info = new CredentialInfoImpl(null, randomUser, randomPassword);
    }

    /**
     * Constructor rejects undefined user.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsUndefinedUser() {
	info = new CredentialInfoImpl(randomId, null, randomPassword);
    }

    /**
     * Constructor rejects undefined password.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsUndefinedPassword() {
	info = new CredentialInfoImpl(randomId, randomUser, null);
    }

}
