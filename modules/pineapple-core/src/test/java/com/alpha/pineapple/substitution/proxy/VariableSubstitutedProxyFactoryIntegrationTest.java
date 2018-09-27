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

package com.alpha.pineapple.substitution.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Environment;
import com.alpha.pineapple.model.configuration.Environments;
import com.alpha.pineapple.substitution.DefaultVariableResolverImpl;
import com.alpha.pineapple.substitution.VariableResolver;
import com.alpha.pineapple.substitution.variables.DefaultVariablesImpl;
import com.alpha.pineapple.substitution.variables.Variables;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Unit test of the class {@linkplain VariableSubstitutedProxyFactoryImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class VariableSubstitutedProxyFactoryIntegrationTest {

	/**
	 * Test class with no no-arg constructor, e.g. class has public n-arg
	 * constructor.
	 */
	class TestClassWithPublicOneArgConstructor {
		String value;

		public TestClassWithPublicOneArgConstructor(String value) {
			this.value = value;
		}

		String getValue() {
			return this.value;
		}
	}

	/**
	 * Test class with no no-arg constructor, e.g. class has private n-arg
	 * constructor.
	 */
	class TestClassWithPrivateOneArgConstructor {
		String value;

		TestClassWithPrivateOneArgConstructor(String value) {
			this.value = value;
		}

		String getValue() {
			return this.value;
		}
	}

	/**
	 * Test class with no-arg constructor, e.g. class has public no-arg constructor.
	 */
	class TestClassWithPublicNoArgConstructor {

		private TestClassWithPublicNoArgConstructor() {
		}

		String getValue(String value) {
			return value;
		}
	}

	/**
	 * Test class with no-arg constructor, e.g. class has private no-arg
	 * constructor.
	 */
	class TestClassWithPrivateNoArgConstructor {

		private TestClassWithPrivateNoArgConstructor() {
		}

		String getValue(String value) {
			return value;
		}
	}

	/**
	 * First index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * Variable substituted proxy factory Factory.
	 */
	@Resource
	ObjectFactory<VariableSubstitutedProxyFactory> variableSubstitutedProxyFactoryFactory;

	/**
	 * Subject under test.
	 */
	VariableSubstitutedProxyFactory variableSubstitutedProxyFactory;

	/**
	 * Variable resolver.
	 */
	VariableResolver resolver;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Random key.
	 */
	String randomKey;

	/**
	 * Random value.
	 */
	String randomValue;

	/**
	 * Random ID.
	 */
	String randomId;

	/**
	 * Random description.
	 */
	String randomDescription;

	@Before
	public void setUp() throws Exception {
		randomKey = RandomStringUtils.randomAlphabetic(10);
		randomValue = RandomStringUtils.randomAlphabetic(10);
		randomId = RandomStringUtils.randomAlphabetic(10);
		randomDescription = RandomStringUtils.randomAlphabetic(10);

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// create resolver
		resolver = new DefaultVariableResolverImpl();

		// factotry
		variableSubstitutedProxyFactory = variableSubstitutedProxyFactoryFactory.getObject();
		assertNotNull(variableSubstitutedProxyFactory);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Create variables and initialize factory.
	 */
	void createVariablesAndInitializeFactory() {

		// set variable map
		HashMap<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomKey, randomValue);
		Variables variables = new DefaultVariablesImpl(varMap);
		variableSubstitutedProxyFactory.initialize(variables);
	}

	/**
	 * Test that factory factory can be gotten from context.
	 */
	@Test
	public void testCanGetFactoryFactoryfromContext() {
		assertNotNull(variableSubstitutedProxyFactoryFactory);
	}

	/**
	 * Test factory throws exception if it is initialized with null variables.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFactoryThrowsExceptionIfInitializedWithNullVariables() {
		variableSubstitutedProxyFactory.initialize(null);
	}

	/**
	 * Test factory throws exception if it isn't initialized with variables.
	 */
	@Test(expected = IllegalStateException.class)
	public void testFactoryThrowsExceptionIfNotInitializedWithVariables() {
		ProxiedTestClass testObject = new ProxiedTestClass();

		// invoke to trigger exception
		variableSubstitutedProxyFactory.decorateWithProxy(testObject);
	}

	/**
	 * Test factory can decorate test class.
	 */
	@Test
	public void testFactoryReturnsDefinedProxy() {
		ProxiedTestClass testObject = new ProxiedTestClass();

		// set variable map
		HashMap<String, String> varMap = new HashMap<String, String>();
		Variables variables = new DefaultVariablesImpl(varMap);
		variableSubstitutedProxyFactory.initialize(variables);

		// decorate
		ProxiedTestClass proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);

		// test
		assertNotNull(proxiedObject);
	}

	/**
	 * Test factory returns null when invoked with null.
	 */
	@Test
	public void testFactoryReturnsNullforNullObject() {

		// set variable map
		HashMap<String, String> varMap = new HashMap<String, String>();
		Variables variables = new DefaultVariablesImpl(varMap);
		variableSubstitutedProxyFactory.initialize(variables);

		// decorate
		ProxiedTestClass proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(null);

		// test
		assertNull(proxiedObject);
	}

	/**
	 * Test that integer object is returned unmodified.
	 */
	@Test
	public void testFinalObjectIsntDecoratedWithProxy() {
		ProxiedTestClass testObject = new ProxiedTestClass();

		// set variable map
		HashMap<String, String> varMap = new HashMap<String, String>();
		Variables variables = new DefaultVariablesImpl(varMap);
		variableSubstitutedProxyFactory.initialize(variables);

		// decorate
		ProxiedTestClass proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);

		Integer intValue = new Integer(RandomUtils.nextInt());
		Integer proxiedValue = (Integer) proxiedObject.getObject(intValue);

		// test
		assertEquals(intValue, proxiedValue);
		assertEquals(intValue.hashCode(), proxiedValue.hashCode());
	}

	/**
	 * Test that integer object is returned unmodified.
	 */
	@Test
	public void testFinalObjectIsntDecoratedWithProxy2() {
		Integer testObject = new Integer(RandomUtils.nextInt());

		// set variable map
		HashMap<String, String> varMap = new HashMap<String, String>();
		Variables variables = new DefaultVariablesImpl(varMap);
		variableSubstitutedProxyFactory.initialize(variables);

		// decorate
		Integer proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);

		// test
		assertEquals(testObject, proxiedObject);
		assertEquals(testObject.hashCode(), proxiedObject.hashCode());
	}

	/**
	 * Test that child object of type string object is returned unmodified if it
	 * doesn't contain any variables.
	 */
	@Test
	public void testChildObjectOfTypeStringIsReturnedIdenticalIfItDoesntContainVars() {
		ProxiedTestClass testObject = new ProxiedTestClass();

		// set variable map
		HashMap<String, String> varMap = new HashMap<String, String>();
		Variables variables = new DefaultVariablesImpl(varMap);
		variableSubstitutedProxyFactory.initialize(variables);

		// decorate
		ProxiedTestClass proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);

		String strValue = RandomStringUtils.randomAlphabetic(16);
		String proxiedValue = (String) proxiedObject.getObject(strValue);

		// test
		assertEquals(strValue, proxiedValue);
		assertEquals(strValue.hashCode(), proxiedValue.hashCode());
	}

	/**
	 * Test that child object of type string object is returned unmodified if it
	 * doesn't contain any variables.
	 */
	@Test
	public void testChildObjectOfTypeStringIsReturnedIdenticalIfItDoesntContainVars2() {
		ProxiedTestClass testObject = new ProxiedTestClass();

		// set variable map
		HashMap<String, String> varMap = new HashMap<String, String>();
		Variables variables = new DefaultVariablesImpl(varMap);
		variableSubstitutedProxyFactory.initialize(variables);

		// decorate
		ProxiedTestClass proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);

		String strValue = RandomStringUtils.randomAlphabetic(16);
		String proxiedValue = proxiedObject.getString(strValue);

		// test
		assertEquals(strValue, proxiedValue);
		assertEquals(strValue.hashCode(), proxiedValue.hashCode());
	}

	/**
	 * Test that child object of type string object is processed if it contains
	 * variables.
	 */
	@Test
	public void testChildObjectOfTypeStringIsSubstitutedIfItContainsVars() {
		ProxiedTestClass testObject = new ProxiedTestClass();

		// create string for substitution
		String subStr = new StringBuilder().append("${").append(randomKey).append("}").toString();

		// set variable map
		HashMap<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomKey, randomValue);
		Variables variables = new DefaultVariablesImpl(varMap);
		variableSubstitutedProxyFactory.initialize(variables);

		// decorate
		ProxiedTestClass proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);

		String proxiedValue = proxiedObject.getString(subStr);

		// test
		assertEquals(randomValue, proxiedValue);
	}

	/**
	 * Test that string supplied as argument to the factory returns processed
	 * string.
	 */
	@Test
	public void testProxyedStringIsSubstituted() {

		// create string for substitution
		String subStr = new StringBuilder().append("${").append(randomKey).append("}").toString();

		// set variable map
		HashMap<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomKey, randomValue);
		Variables variables = new DefaultVariablesImpl(varMap);
		variableSubstitutedProxyFactory.initialize(variables);

		// decorate
		String proxiedValue = variableSubstitutedProxyFactory.decorateWithProxy(subStr);

		// test
		assertEquals(randomValue, proxiedValue);
	}

	/**
	 * Test that string supplied as argument to the factory is returned unmodified
	 * if it doesn't contain any variables.
	 */
	@Test
	public void testProxyedStringIsReturnIdenticalIfItDoesntContainVars() {

		// create string for substitution
		String strValue = RandomStringUtils.randomAlphabetic(16);

		// set variable map
		HashMap<String, String> varMap = new HashMap<String, String>();
		varMap.put(randomKey, randomValue);
		Variables variables = new DefaultVariablesImpl(varMap);
		variableSubstitutedProxyFactory.initialize(variables);

		// decorate
		String proxiedValue = variableSubstitutedProxyFactory.decorateWithProxy(strValue);

		// test
		assertEquals(strValue, proxiedValue);
		assertEquals(strValue.hashCode(), proxiedValue.hashCode());
	}

	/**
	 * Test that child object is decorated with proxy.
	 */
	@Test
	public void testChildObjectIsDecoratedWithProxy() {
		ProxiedTestClass testObject = new ProxiedTestClass();
		ProxiedTestClass childObject = new ProxiedTestClass();

		// set variable map
		HashMap<String, String> varMap = new HashMap<String, String>();
		Variables variables = new DefaultVariablesImpl(varMap);
		variableSubstitutedProxyFactory.initialize(variables);

		// decorate
		ProxiedTestClass proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);

		// get decorate child
		ProxiedTestClass proxiedChildObject = proxiedObject.getObject(childObject);

		// test
		assertNotNull(proxiedChildObject);
		assertFalse(childObject.hashCode() != proxiedChildObject.hashCode());
	}

	/**
	 * Test that child object of type string is substituted.
	 */
	@Test
	public void testChildObjectofTypeStringIsSubstituted() {
		ProxiedTestClass testObject = new ProxiedTestClass();

		createVariablesAndInitializeFactory();
		ProxiedTestClass proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);
		ProxiedTestClass proxiedChildObject = proxiedObject.getChildObject();

		// create string for substitution
		String subStr = new StringBuilder().append("${").append(randomKey).append("}").toString();

		// test
		assertEquals(randomValue, proxiedChildObject.getString(subStr));
	}

	/**
	 * Test that element value can be accessed.
	 */
	@Test
	public void testStringElementValuesCanBeAccessed() {

		createVariablesAndInitializeFactory();
		Configuration testObject = envConfigMother.createEnvConfigWithSingleEnvironment(randomId, randomDescription);
		Configuration proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);

		// get decorate children
		Environments proxiedEnvironments = proxiedObject.getEnvironments();
		assertNotNull(proxiedEnvironments);
		List<Environment> proxiedEnvList = proxiedEnvironments.getEnvironment();
		assertNotNull(proxiedEnvList);
		Environment proxiedEnv = proxiedEnvList.get(FIRST_INDEX);
		assertNotNull(proxiedEnv);
		assertEquals(randomId, proxiedEnv.getId());
		assertEquals(randomDescription, proxiedEnv.getDescription());
	}

	/**
	 * Test that element values are substituted.
	 */
	@Test
	public void testStringElementValuesIsSubstituted() {
		// create string for substitution
		String parameterizedValue = new StringBuilder().append("${").append(randomKey).append("}").toString();

		createVariablesAndInitializeFactory();
		Configuration testObject = envConfigMother.createEnvConfigWithSingleEnvironment(parameterizedValue,
				parameterizedValue);
		Configuration proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);

		// get decorate children
		Environments proxiedEnvironments = proxiedObject.getEnvironments();
		assertNotNull(proxiedEnvironments);
		List<Environment> proxiedEnvList = proxiedEnvironments.getEnvironment();
		assertNotNull(proxiedEnvList);
		Environment proxiedEnv = proxiedEnvList.get(FIRST_INDEX);
		assertNotNull(proxiedEnv);
		assertEquals(randomValue, proxiedEnv.getId());
		assertEquals(randomValue, proxiedEnv.getDescription());
	}

	/**
	 * Test that element values isn't substituted if there is no match .
	 */
	@Test
	public void testStringElementValuesIsntSubstitutedifNoMatch() {
		// create string for substitution
		String parameterizedValue = new StringBuilder().append("${").append(randomKey).append("}").toString();

		// set empty variable map
		HashMap<String, String> varMap = new HashMap<String, String>();
		Variables variables = new DefaultVariablesImpl(varMap);
		variableSubstitutedProxyFactory.initialize(variables);

		Configuration testObject = envConfigMother.createEnvConfigWithSingleEnvironment(parameterizedValue,
				parameterizedValue);
		Configuration proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);

		// test
		Environments proxiedEnvironments = proxiedObject.getEnvironments();
		assertNotNull(proxiedEnvironments);
		List<Environment> proxiedEnvList = proxiedEnvironments.getEnvironment();
		assertNotNull(proxiedEnvList);
		Environment proxiedEnv = proxiedEnvList.get(FIRST_INDEX);
		assertNotNull(proxiedEnv);
		assertEquals(parameterizedValue, proxiedEnv.getId());
		assertEquals(parameterizedValue, proxiedEnv.getDescription());
	}

	/**
	 * Test that model with class with public n-arg constructor can be handled and
	 * proxied.
	 * 
	 * Class has public constructor which requires arguments.
	 */
	@Test
	public void testCreateProxyForClassWithPublicOneArgConstructor() {
		// create string for substitution
		String parameterizedValue = new StringBuilder().append("${").append(randomKey).append("}").toString();

		createVariablesAndInitializeFactory();
		TestClassWithPublicOneArgConstructor testObject = new TestClassWithPublicOneArgConstructor(parameterizedValue);
		TestClassWithPublicOneArgConstructor proxiedObject = variableSubstitutedProxyFactory
				.decorateWithProxy(testObject);

		// test
		assertNotNull(proxiedObject);
		assertEquals(parameterizedValue, testObject.getValue());
		assertEquals(randomValue, proxiedObject.getValue());

	}

	/**
	 * Test that model with class with public n-arg constructor can be handled and
	 * proxied. Example used: JaxbElement
	 */
	@Test
	public void testCreateProxyForClassWithPublicOneArgConstructor2() {
		// create string for substitution
		String parameterizedValue = new StringBuilder().append("${").append(randomKey).append("}").toString();

		createVariablesAndInitializeFactory();
		QName qName = new QName(randomDescription);
		JAXBElement<String> testObject = new JAXBElement<String>(qName, String.class, parameterizedValue);
		JAXBElement<String> proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);

		// test
		assertNotNull(proxiedObject);
		assertEquals(qName, testObject.getName());
		assertEquals(randomValue, proxiedObject.getValue());
	}

	/**
	 * Test that model with class with n-arg constructor can be handled and proxied.
	 * 
	 * Class has private constructor which requires arguments.
	 */
	@Test
	public void testCreateProxyForClassWithPrivateOneArgConstructor() {
		// create string for substitution
		String parameterizedValue = new StringBuilder().append("${").append(randomKey).append("}").toString();

		createVariablesAndInitializeFactory();
		TestClassWithPrivateOneArgConstructor testObject = new TestClassWithPrivateOneArgConstructor(
				parameterizedValue);
		TestClassWithPrivateOneArgConstructor proxiedObject = variableSubstitutedProxyFactory
				.decorateWithProxy(testObject);

		// test
		assertNotNull(proxiedObject);
		assertEquals(parameterizedValue, testObject.getValue());
		assertEquals(randomValue, proxiedObject.getValue());

	}

	/**
	 * Test that model with class with public no-arg constructor can be handled and
	 * proxied.
	 * 
	 * Class has public constructor which requires no arguments.
	 */
	@Test
	public void testCreateProxyForClassWithPublicNoArgConstructor() {
		// create string for substitution
		String parameterizedValue = new StringBuilder().append("${").append(randomKey).append("}").toString();

		createVariablesAndInitializeFactory();
		TestClassWithPublicNoArgConstructor testObject = new TestClassWithPublicNoArgConstructor();
		TestClassWithPublicNoArgConstructor proxiedObject = variableSubstitutedProxyFactory
				.decorateWithProxy(testObject);

		// test
		assertNotNull(proxiedObject);
		assertEquals(parameterizedValue, testObject.getValue(parameterizedValue));
		assertEquals(randomValue, proxiedObject.getValue(parameterizedValue));

	}

	/**
	 * Test that model with class with private no-arg constructor can be handled and
	 * proxied.
	 * 
	 * Class has private constructor which requires no arguments.
	 */
	@Test
	public void testCreateProxyForClassWithPrivateNoArgConstructor() {
		// create string for substitution
		String parameterizedValue = new StringBuilder().append("${").append(randomKey).append("}").toString();

		createVariablesAndInitializeFactory();
		TestClassWithPrivateNoArgConstructor testObject = new TestClassWithPrivateNoArgConstructor();
		TestClassWithPrivateNoArgConstructor proxiedObject = variableSubstitutedProxyFactory
				.decorateWithProxy(testObject);

		// test
		assertNotNull(proxiedObject);
		assertEquals(parameterizedValue, testObject.getValue(parameterizedValue));
		assertEquals(randomValue, proxiedObject.getValue(parameterizedValue));

	}

	/**
	 * Test that model with private class with no-arg constructor can be handled and
	 * proxied. Example used: {@linkplain ArrayList$Itr}
	 */
	@Test
	public void testCreateProxyForPrivateClassWithNoArgConstructor() {
		// create string for substitution
		String parameterizedValue = new StringBuilder().append("${").append(randomKey).append("}").toString();

		createVariablesAndInitializeFactory();
		Configuration testObject = envConfigMother.createEnvConfigWithSingleEnvironment(parameterizedValue,
				parameterizedValue);
		Configuration proxiedObject = variableSubstitutedProxyFactory.decorateWithProxy(testObject);

		// test
		Environments proxiedEnvironments = proxiedObject.getEnvironments();
		assertNotNull(proxiedEnvironments);
		List<Environment> proxiedEnvList = proxiedEnvironments.getEnvironment();
		assertNotNull(proxiedEnvList);
		for (Environment proxiedEnv : proxiedEnvList) {
			assertNotNull(proxiedEnv);
			assertEquals(randomValue, proxiedEnv.getId());
			assertEquals(randomValue, proxiedEnv.getDescription());
		}
	}

	/**
	 * Test that instance of operation works with proxy.
	 * 
	 * Class has public constructor which requires no arguments.
	 */
	@Test
	public void testInstanceOfOperationWorksWithCreatedProxyForClassWithPublicNoArgConstructor() {
		createVariablesAndInitializeFactory();
		TestClassWithPublicNoArgConstructor testObject = new TestClassWithPublicNoArgConstructor();
		TestClassWithPublicNoArgConstructor proxiedObject = variableSubstitutedProxyFactory
				.decorateWithProxy(testObject);

		// test
		assertNotNull(proxiedObject);
		assertTrue(proxiedObject instanceof TestClassWithPublicNoArgConstructor);
	}

}
