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

import static com.alpha.pineapple.plugin.weblogic.scriptingtool.WeblogicScriptingToolConstants.PINEAPPLE_MODULEPATH_PROPERTY;
import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * unit test for {@linkplain SystemPropertiesArgumentBuilder} class.
 */
public class SystemPropertiesArgumentBuilderTest {

	/**
	 * Object under test.
	 */
	SystemPropertiesArgumentBuilder argumentBuilder;
	
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
		
		argumentBuilder  = new SystemPropertiesArgumentBuilder();		
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
	public void testAddModulePathArgument() {
		argumentBuilder.buildArgumentList();
		argumentBuilder.addModulePathArgument(randomArg);		
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertEquals(1,arguments.length);
		assertEquals(randomArg,arguments[0]);
	}

	@Test
	public void testAddDemoTrustArguments() {
		argumentBuilder.buildArgumentList();
		argumentBuilder.addDemoTrustArguments();		
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertEquals(3,arguments.length);
		assertEquals("-Dweblogic.security.TrustKeyStore=DemoTrust",arguments[0]);
		assertEquals("-Dssl.debug=true",arguments[1]);
		assertEquals("-Dweblogic.security.SSL.ignoreHostnameVerification=true",arguments[2]);		
	}

	@Test
	public void testAddPineappleArguments() {
		argumentBuilder.buildArgumentList();
		argumentBuilder.addPineappleArguments(randomFileArg);		
		String[] arguments = argumentBuilder.getArgumentList();
				
		StringBuilder arg =  new StringBuilder()
		.append("-D")
		.append(PINEAPPLE_MODULEPATH_PROPERTY)
		.append("=")
		.append(randomFileArg.getAbsolutePath());			
		
		// test 
		assertEquals(1,arguments.length);
		assertEquals(arg.toString(),arguments[0]);
	}

	/**
	 * Test path is escape with "" if the path contains spaces.
	 */
	@Test
	public void testAddPineappleArgumentsIsEscapedIfPathContainsSpaces() {
		
		String randomArg = RandomStringUtils.randomAlphabetic(10) + " " + RandomStringUtils.randomAlphabetic(10);
		File randomFileArg = new  File(randomArg);
		
		argumentBuilder.buildArgumentList();
		argumentBuilder.addPineappleArguments(randomFileArg);		
		String[] arguments = argumentBuilder.getArgumentList();
				
		StringBuilder arg =  new StringBuilder()
		.append("\"")		
		.append("-D")
		.append(PINEAPPLE_MODULEPATH_PROPERTY)
		.append("=")
		.append(randomFileArg.getAbsolutePath())
		.append("\"");		
				
		// test 
		assertEquals(1,arguments.length);
		assertEquals(arg.toString(),arguments[0]);
	}

	@Test
	public void testAddLinuxFastSecureRandomArguments() {
		argumentBuilder.buildArgumentList();
		argumentBuilder.addLinuxFastSecureRandomArguments();		
		String[] arguments = argumentBuilder.getArgumentList();
				
		// test 
		assertEquals(1,arguments.length);
		assertEquals("-Djava.security.egd=file:/dev/./urandom",arguments[0]);
	}
	
}
