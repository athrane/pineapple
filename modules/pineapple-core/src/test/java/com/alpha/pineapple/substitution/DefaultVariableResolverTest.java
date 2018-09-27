/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.substitution;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.substitution.variables.DefaultVariablesImpl;
import com.alpha.pineapple.substitution.variables.Variables;

/**
 * Unit test of the class {@linkplain DefaultVariableResolverImpl}.
 */
public class DefaultVariableResolverTest {

	/**
	 * Empty string.
	 */
	static final String EMPTY_STRING = "";

	/**
	 * Null string.
	 */
	static final String NULL_STRING = null;

	/**
	 * Object under test.
	 */
	VariableResolver resolver;

	/**
	 * Random variable name.
	 */
	String randomVarName;

	/**
	 * Random variable name.
	 */
	String randomVarName2;

	/**
	 * Random variable value.
	 */
	String randomVarValue;

	/**
	 * Random variable value.
	 */
	String randomVarValue2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		randomVarName = RandomStringUtils.randomAlphabetic(10) + "-var";
		randomVarName2 = RandomStringUtils.randomAlphabetic(10) + "-var";
		randomVarValue = RandomStringUtils.randomAlphabetic(10);
		randomVarValue2 = RandomStringUtils.randomAlphabetic(10);

		// create resolver
		resolver = new DefaultVariableResolverImpl();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Create random variable from random variable name #1.
	 * 
	 * @return
	 */
	String createRandomVariablefromVarNameOne() {
		String randomVar = new StringBuilder().append("${").append(randomVarName).append("}").toString();
		return randomVar;
	}

	/**
	 * Create random variable from random variable name #2.
	 * 
	 * @return
	 */
	String createRandomVariablefromVarNameTwo() {
		String randomVar = new StringBuilder().append("${").append(randomVarName2).append("}").toString();
		return randomVar;
	}

	/**
	 * Test that empty string can be processed.
	 */
	@Test
	public void testSubstituteEmptyString() {
		// create map
		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomVarName, randomVarValue);

		// create variables and resolve
		Variables variables = new DefaultVariablesImpl(varMap);
		String actual = resolver.resolve(variables, EMPTY_STRING);

		// test
		assertEquals(EMPTY_STRING, actual);
	}

	/**
	 * Test that variable can be processed.
	 */
	@Test
	public void testSubstituteVariable() {
		String source = createRandomVariablefromVarNameOne();

		// create map
		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomVarName, randomVarValue);

		// create variables and resolve
		Variables variables = new DefaultVariablesImpl(varMap);
		String actual = resolver.resolve(variables, source);

		// test
		assertEquals(randomVarValue, actual);
	}

	/**
	 * Test that variable which occurs twice can be processed.
	 */
	@Test
	public void testSubstituteVariableWhichOccursTwice() {
		String variable = createRandomVariablefromVarNameOne();

		// create source string
		String source = new StringBuilder().append(variable).append(variable).toString();

		// create map
		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomVarName, randomVarValue);

		// create variables and resolve
		Variables variables = new DefaultVariablesImpl(varMap);
		String actual = resolver.resolve(variables, source);

		// test
		assertEquals(2, StringUtils.countMatches(actual, randomVarValue));
	}

	/**
	 * Test that variable can be processed which doesn't occur.
	 */
	@Test
	public void testSubstituteVariableWhichDoesntOccur() {

		// create source string
		String source = RandomStringUtils.randomAlphabetic(100);

		// create map
		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomVarName, randomVarValue);

		// create variables and resolve
		Variables variables = new DefaultVariablesImpl(varMap);
		String actual = resolver.resolve(variables, source);

		// test
		assertEquals(0, StringUtils.countMatches(actual, randomVarValue));
	}

	/**
	 * Test that two variables can be processed. The variables have different name
	 * and values.
	 */
	@Test
	public void testSubstituteTwoVariables() {
		String variable = createRandomVariablefromVarNameOne();
		String variable2 = createRandomVariablefromVarNameTwo();

		// create source string
		String source = new StringBuilder().append(variable).append(variable2).toString();

		// create map
		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomVarName, randomVarValue);
		varMap.put(randomVarName2, randomVarValue2);

		// create variables and resolve
		Variables variables = new DefaultVariablesImpl(varMap);
		String actual = resolver.resolve(variables, source);

		// test
		assertEquals(1, StringUtils.countMatches(actual, randomVarValue));
		assertEquals(1, StringUtils.countMatches(actual, randomVarValue2));
	}

	/**
	 * Test that two variables can be processed. The variables have different name
	 * and but identical values.
	 */
	@Test
	public void testSubstituteTwoVariablesWithIdentialValues() {
		String variable = createRandomVariablefromVarNameOne();
		String variable2 = createRandomVariablefromVarNameTwo();

		// create source string
		String source = new StringBuilder().append(variable).append(variable2).toString();

		// create map
		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomVarName, randomVarValue);
		varMap.put(randomVarName2, randomVarValue);

		// create variables and resolve
		Variables variables = new DefaultVariablesImpl(varMap);
		String actual = resolver.resolve(variables, source);

		// test
		assertEquals(2, StringUtils.countMatches(actual, randomVarValue));
	}

	/**
	 * Test that variable with null key is ignored for substitution.
	 */
	@Test
	public void testVariableWithNullKeyIsntSubstituted() {
		String variable = NULL_STRING;

		// create source string
		String source = new StringBuilder().append(variable).toString();

		// create map
		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomVarName, randomVarValue);

		// create variables and resolve
		Variables variables = new DefaultVariablesImpl(varMap);
		String actual = resolver.resolve(variables, source);

		// test
		assertEquals(0, StringUtils.countMatches(actual, randomVarValue));
	}

	/**
	 * Test that variable with empty key is ignored for substitution.
	 */
	@Test
	public void testVariableWithEmptyKeyIsntSubstituted() {
		String variable = EMPTY_STRING;

		// create source string
		String source = new StringBuilder().append(variable).toString();

		// create map
		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomVarName, randomVarValue);

		// create variables and resolve
		Variables variables = new DefaultVariablesImpl(varMap);
		String actual = resolver.resolve(variables, source);

		// test
		assertEquals(0, StringUtils.countMatches(actual, randomVarValue));
	}

	/**
	 * Test that variable with null value is ignored for substitution.
	 */
	@Test
	public void testVariableWithNullValueIsntSubstituted() {
		String variable = createRandomVariablefromVarNameOne();

		// create source string
		String source = new StringBuilder().append(variable).toString();

		// create map
		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomVarName, NULL_STRING);

		// create variables and resolve
		Variables variables = new DefaultVariablesImpl(varMap);
		String actual = resolver.resolve(variables, source);

		// test
		assertEquals(0, StringUtils.countMatches(actual, randomVarValue));
	}

	/**
	 * Test that variable with empty value is substituted.
	 */
	@Test
	public void testVariableWithEmptyValueIsSubstituted() {
		String variable = createRandomVariablefromVarNameOne();

		// create source string
		String source = new StringBuilder().append(variable).toString();

		// create map
		Map<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomVarName, EMPTY_STRING);

		// create variables and resolve
		Variables variables = new DefaultVariablesImpl(varMap);
		String actual = resolver.resolve(variables, source);

		// test
		assertEquals(0, StringUtils.countMatches(actual, randomVarValue));
		assertEquals(EMPTY_STRING, actual);
	}

}
