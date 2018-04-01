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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the <code>ClassUtils</code> class.
 */
public class ClassUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStringIsValidClass() {
		assertTrue(ClassUtils.isValidClass(String.class.getName()));
	}

	@Test
	public void testClassUtilsIsValidClass() {
		assertTrue(ClassUtils.isValidClass(ClassUtils.class.getName()));
	}

	@Test
	public void testIsValidClassFailsIfNameIsNull() {
		assertFalse(ClassUtils.isValidClass(null));
	}

	@Test
	public void testIsValidClassFailsIfNameIsEmpty() {
		assertFalse(ClassUtils.isValidClass(""));
	}

	@Test
	public void testIsValidClassFailsIfClassDoesntExist() {
		assertFalse(ClassUtils.isValidClass("somenonexistingclass"));
	}

}
