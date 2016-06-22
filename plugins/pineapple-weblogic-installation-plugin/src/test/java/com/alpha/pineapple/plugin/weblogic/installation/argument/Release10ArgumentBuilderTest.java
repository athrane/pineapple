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

package com.alpha.pineapple.plugin.weblogic.installation.argument;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@linkplain Release10ArgumentBuilder} class.
 */
public class Release10ArgumentBuilderTest {

	/**
	 * Object under test.
	 */
	Release10ArgumentBuilder argumentBuilder;
	
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
		randomFileArg = new File(RandomStringUtils.randomAlphabetic(10));
		
		argumentBuilder  = new Release10ArgumentBuilder();				
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
	public void testAddSilentModeArgument() {
		argumentBuilder.buildArgumentList();
		argumentBuilder.addSilentModeArgument();
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertEquals(1,arguments.length);
		assertEquals("-mode=silent",arguments[0]);
	}
	

	@Test
	public void testAddUninstallationSilentModeArgument() {
		argumentBuilder.buildArgumentList();
		argumentBuilder.addUninstallationSilentModeArgument();
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertEquals(1,arguments.length);
		assertEquals("-mode=silent",arguments[0]);
	}

	@Test
	public void testAddSilentXMLArgument() {
		argumentBuilder.buildArgumentList();
		argumentBuilder.addSilentXMLArgument(randomFileArg);
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertEquals(1,arguments.length);
		assertEquals("-silent_xml="+randomFileArg,arguments[0]);
	}

	@Test
	public void testAddLogArgument() {
		argumentBuilder.buildArgumentList();
		argumentBuilder.addLogArgument(randomFileArg);
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertEquals(1,arguments.length);
		assertEquals("-log="+randomFileArg,arguments[0]);
	}

	@Test
	public void testAddInstallerArchive() {
		argumentBuilder.buildArgumentList();
		argumentBuilder.addInstallerArchive(randomArg);
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertEquals(2,arguments.length);
		assertEquals("-jar",arguments[0]);
		assertEquals(randomArg,arguments[1]);		
	}

}
