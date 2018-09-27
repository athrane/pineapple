/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2014 Allan Thrane Andersen..
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
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.javautils;

import static org.junit.Assert.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the class {@linkplain ClassUtils}.
 */
public class StringUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRemoveEnclosingBracketsReturnsNull() {
		assertNull(StringUtils.removeEnclosingBrackets(null));
	}

	@Test
	public void testRemoveEnclosingBracketsReturnsSourceForNonBracketInput() {
		String value = RandomStringUtils.randomAlphanumeric(10);
		assertEquals(value, StringUtils.removeEnclosingBrackets(value));
	}

	@Test
	public void testRemoveEnclosingBracketsReturnsSourceForInputWithStartingBracketOnly() {
		String value = "[" + RandomStringUtils.randomAlphanumeric(10);
		assertEquals(value, StringUtils.removeEnclosingBrackets(value));
	}

	@Test
	public void testRemoveEnclosingBracketsReturnsSourceForInputWithEndingBracketOnly() {
		String value = RandomStringUtils.randomAlphanumeric(10) + "]";
		assertEquals(value, StringUtils.removeEnclosingBrackets(value));
	}

	@Test
	public void testRemoveEnclosingBracketsReturnsResuilyWithRemovedBrackets() {
		String value = RandomStringUtils.randomAlphanumeric(10);
		String bracketedValue = "[" + value + "]";
		assertEquals(value, StringUtils.removeEnclosingBrackets(bracketedValue));
	}

}
