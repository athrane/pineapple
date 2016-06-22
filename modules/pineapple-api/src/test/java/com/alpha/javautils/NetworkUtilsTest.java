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

package com.alpha.javautils;

import static com.alpha.javautils.NetworkUtils.*;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the {@linkplain NetworkUtils} class.
 */
public class NetworkUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIsValidIpPortReturnsTrueForMinValidPort() {
	assertTrue(NetworkUtils.isValidPort(MIN_LEGAL_IP_PORT));
    }

    @Test
    public void testIsValidIpPortReturnsTrueForMaxValidPort() {
	assertTrue(NetworkUtils.isValidPort(MAX_LEGAL_IP_PORT));
    }

    @Test
    public void testIsValidIpPortReturnsFalseForInvalidPort() {
	assertFalse(NetworkUtils.isValidPort(MIN_LEGAL_IP_PORT - 1));
    }

    @Test
    public void testIsValidIpPortReturnsFalseForInvalidPort2() {
	assertFalse(NetworkUtils.isValidPort(MAX_LEGAL_IP_PORT + 1));
    }

    @Test
    public void testValidateIpPortWithMinValidPort() {
	NetworkUtils.validatePort(MIN_LEGAL_IP_PORT);
    }

    @Test
    public void testValidateIpPortWithMaxValidPort() {
	NetworkUtils.validatePort(MAX_LEGAL_IP_PORT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateIpPortWithInvalidPort() {
	NetworkUtils.validatePort(MIN_LEGAL_IP_PORT - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateIpPortWithInvalidPort2() {
	NetworkUtils.validatePort(MAX_LEGAL_IP_PORT + 1);
    }

}
