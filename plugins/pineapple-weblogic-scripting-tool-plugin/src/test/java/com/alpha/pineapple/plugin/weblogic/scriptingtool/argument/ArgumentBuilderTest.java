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

package com.alpha.pineapple.plugin.weblogic.scriptingtool.argument;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@linkplain ArgumentBuilder} class.
 */
public class ArgumentBuilderTest {

	/**
	 * Object under test.
	 */
	ArgumentBuilder argumentBuilder;
	
	/**
	 * Random argument
	 */
	String randomArg;

	/**
	 * Random file
	 */
	File randomFileArg;
	
	@Before
	public void setUp() throws Exception {
	
		randomArg = RandomStringUtils.randomAlphabetic(10);
		randomFileArg = new  File(RandomStringUtils.randomAlphabetic(10));
		
		argumentBuilder  = new ArgumentBuilder();		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBuildArgumentListIsntNull() {					
		argumentBuilder.buildArgumentList();
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertNotNull(arguments);
	}

	@Test
	public void testBuildArgumentListIsIntiallyEmpty() {					
		argumentBuilder.buildArgumentList();
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertEquals(0,arguments.length);
	}
		
	@Test
	public void testAddSingleArgument() {
		argumentBuilder.buildArgumentList();
		argumentBuilder.addSingleArgument(randomArg);
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertEquals(1,arguments.length);
	}

	@Test
	public void testAddLoadPropertiesArgument() {
		argumentBuilder.buildArgumentList();
		argumentBuilder.addLoadPropertiesArgument();
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertEquals(1,arguments.length);
		assertEquals("-loadProperties",arguments[0]);
	}

	@Test
	public void testAddSkipScanningArgument() {
		argumentBuilder.buildArgumentList();
		argumentBuilder.addSkipScanningArgument();
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertEquals(1,arguments.length);
		assertEquals("-skipWLSModuleScanning",arguments[0]);
	}


}
