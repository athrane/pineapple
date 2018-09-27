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

package com.alpha.pineapple.substitution;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.execution.ExecutionContextRepository;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoImpl;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.model.module.Module;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.session.Session;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of the class {@linkplain VariableSubstitutionProviderImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
		DirectoryTestExecutionListener.class })
public class VariableSubstitutionProviderIntegrationTest {

	/**
	 * Empty string.
	 */
	static final String EMPTY_STRING = "";

	/**
	 * Null string.
	 */
	static final String NULL_STRING = null;

	/**
	 * First Index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Object under test.
	 */
	@Resource
	VariableSubstitutionProvider variableSubstitutionProvider;

	/**
	 * Execution context repository.
	 */
	@Resource
	ExecutionContextRepository executionContextRepository;

	/**
	 * Object mother for module.
	 */
	ObjectMotherModule moduleMother;

	/**
	 * Execution result.
	 */
	ExecutionResult result;

	/**
	 * Mock session.
	 */
	Session session;

	/**
	 * Module descriptor (a variable source).
	 */
	Module module;

	/**
	 * Model (a variable source).
	 */
	Models model;

	/**
	 * Mock resource.
	 */
	com.alpha.pineapple.model.configuration.Resource sessionResource;

	/**
	 * Random variable name.
	 */
	String randomVarName;

	/**
	 * Random resource name.
	 */
	String randomResourceId;

	/**
	 * Random file name.
	 */
	String randomFileName;

	@Before
	public void setUp() throws Exception {
		randomVarName = RandomStringUtils.randomAlphabetic(10) + "-var";
		randomResourceId = RandomStringUtils.randomAlphabetic(10) + "-res";
		randomFileName = RandomStringUtils.randomAlphabetic(10) + "-.txt";

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create execution result
		result = new ExecutionResultImpl("Root result");

		// create session
		session = createMock(Session.class);

		// create resource
		sessionResource = createMock(com.alpha.pineapple.model.configuration.Resource.class);

		// create module object mother
		moduleMother = new ObjectMotherModule();

		createAndRegisterContextInContextRepository();
	}

	@After
	public void tearDown() throws Exception {
		variableSubstitutionProvider = null;
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
	 * Create property list with one variable. The name of the variable is random
	 * variable name #1
	 * 
	 * @param propertyValue
	 *            property value.
	 * 
	 * @return property list with one variable.
	 */
	List<Property> createPropertyListWithSingleProperty(String propertyValue) {
		List<Property> propertyList = new ArrayList<Property>();
		Property property = new Property();
		property.setKey(randomVarName);
		property.setValue(propertyValue);
		propertyList.add(property);
		return propertyList;
	}

	/**
	 * Complete session mock setup with no resource properties defined.
	 */
	void completeSessionMockSetupWithNoResourceProperties() {
		List<Property> propertyList = new ArrayList<Property>();
		expect(sessionResource.getProperty()).andReturn(propertyList).anyTimes();
		replay(sessionResource);
		EasyMock.expect(session.getResource()).andReturn(sessionResource).anyTimes();
		replay(session);
	}

	/**
	 * Complete session mock setup with resource properties defined.
	 * 
	 * @param propertyList
	 *            resource property list.
	 */
	void completeSessionMockSetupWithPropertyDefined(List<Property> propertyList) {
		expect(sessionResource.getId()).andReturn(randomResourceId).anyTimes();
		expect(sessionResource.getProperty()).andReturn(propertyList).anyTimes();
		replay(sessionResource);
		EasyMock.expect(session.getResource()).andReturn(sessionResource).anyTimes();
		replay(session);
	}

	/**
	 * Create context and register it in context repository. Context contains a
	 * module descriptor and model.
	 */
	@SuppressWarnings("unchecked")
	void createAndRegisterContextInContextRepository() {

		// create context with module descriptor and model
		Models model = moduleMother.createModelObjectWithEmptyVariables();
		Module module = moduleMother.createModuleObjectWithEmptyVariables();

		Context context = new ContextBase();
		context.put(CoreConstants.MODULE_KEY, module);
		context.put(CoreConstants.MODULE_MODEL_KEY, model);

		// create execution info
		String randomEnvironment = RandomStringUtils.randomAlphabetic(16);
		String randomOperation = RandomStringUtils.randomAlphabetic(16);
		ModuleInfo moduleInfo = EasyMock.createMock(ModuleInfo.class);
		EasyMock.replay(moduleInfo);

		// create and register execution info in context repository
		ExecutionInfo info = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation, result);
		executionContextRepository.register(info, context);
	}

	/**
	 * Test that provider can be looked up from the context.
	 */
	@Test
	public void testGetProviderFromContext() {
		assertNotNull(variableSubstitutionProvider);
	}

	/**
	 * Test that empty string can be processed.
	 * 
	 * @throws VariableSubstitutionException
	 *             if processing fails.
	 */
	@Test
	public void testSubstituteEmptyString() throws VariableSubstitutionException {
		String substitutedStr = variableSubstitutionProvider.substitute(EMPTY_STRING, result);

		// test
		assertEquals(EMPTY_STRING, substitutedStr);
		assertTrue(result.isExecuting()); // parent result is still executing
		assertEquals(1, result.getChildren().length); // one substitution result
		// is defined
		assertTrue(result.getChildren()[FIRST_INDEX].isSuccess()); // substitution
		// result is
		// successful
		assertEquals(0, result.getChildren()[FIRST_INDEX].getChildren().length); // substitution
		// result
		// has
		// no
		// children
	}

	/**
	 * Test that empty string can be processed. With session included.
	 * 
	 * @throws VariableSubstitutionException
	 *             if processing fails.
	 */
	@Test
	public void testSubstituteEmptyString_WithSession() throws VariableSubstitutionException {
		completeSessionMockSetupWithNoResourceProperties();

		// invoke
		String actual = variableSubstitutionProvider.substitute(EMPTY_STRING, session, result);

		// test
		assertEquals(EMPTY_STRING, actual);
		verify(session);
		verify(sessionResource);
		assertEquals(EMPTY_STRING, actual);
		assertTrue(result.isExecuting()); // parent result is still executing
		assertEquals(1, result.getChildren().length); // one substitution result
		// is defined
		assertTrue(result.getChildren()[FIRST_INDEX].isSuccess()); // substitution
		// result is
		// successful
		assertEquals(0, result.getChildren()[FIRST_INDEX].getChildren().length); // substitution
		// result
		// has
		// no
		// children
	}

	/**
	 * Test that variable defined as a resource property can be substituted. With
	 * session included.
	 * 
	 * @throws VariableSubstitutionException
	 *             if processing fails.
	 */
	@Test
	public void testSubstituteVariableDefinedAsResourceProperty_WithSession() throws VariableSubstitutionException {
		String variable = createRandomVariablefromVarNameOne();
		String variableValue = RandomStringUtils.randomAlphabetic(10);
		List<Property> propertyList = createPropertyListWithSingleProperty(variableValue);

		completeSessionMockSetupWithPropertyDefined(propertyList);

		// invoke
		String actual = variableSubstitutionProvider.substitute(variable, session, result);

		// test
		assertEquals(variableValue, actual);
		assertTrue(result.isExecuting()); // parent result is still executing
		assertEquals(1, result.getChildren().length); // one substitution result
		// is defined
		assertTrue(result.getChildren()[FIRST_INDEX].isSuccess()); // substitution
		// result is
		// successful
		assertEquals(0, result.getChildren()[FIRST_INDEX].getChildren().length); // substitution
		// result
		// has
		// no
		// children
		verify(session);
		verify(sessionResource);
	}

	/**
	 * Test that variable defined as a resource property can be substituted. With
	 * session included. The value of the property is the empty string.
	 * 
	 * @throws VariableSubstitutionException
	 *             if processing fails.
	 */
	@Test
	public void testSubstituteVariableDefinedAsResourcePropertyWithEmptyValue_WithSession()
			throws VariableSubstitutionException {
		String variable = createRandomVariablefromVarNameOne();
		String variableValue = EMPTY_STRING;
		List<Property> propertyList = createPropertyListWithSingleProperty(variableValue);

		completeSessionMockSetupWithPropertyDefined(propertyList);

		// invoke
		String actual = variableSubstitutionProvider.substitute(variable, session, result);

		// test
		assertEquals(variableValue, actual);
		assertTrue(result.isExecuting()); // parent result is still executing
		assertEquals(1, result.getChildren().length); // one substitution result
		// is defined
		assertTrue(result.getChildren()[FIRST_INDEX].isSuccess()); // substitution
		// result is
		// successful
		assertEquals(0, result.getChildren()[FIRST_INDEX].getChildren().length); // substitution
		// result
		// has
		// no
		// children
		verify(session);
		verify(sessionResource);
	}

	/**
	 * Test that variable defined as a resource property with value null isn't
	 * substituted. With session included. The value of the property is null.
	 * 
	 * @throws VariableSubstitutionException
	 *             if processing fails.
	 */
	@Test
	public void testSubstituteVariableDefinedAsResourcePropertyWithNullValue_WithSession()
			throws VariableSubstitutionException {
		String variable = createRandomVariablefromVarNameOne();
		String variableValue = null;
		List<Property> propertyList = createPropertyListWithSingleProperty(variableValue);

		completeSessionMockSetupWithPropertyDefined(propertyList);

		// invoke
		String actual = variableSubstitutionProvider.substitute(variable, session, result);

		// test
		assertEquals(variable, actual);
		assertTrue(result.isExecuting()); // parent result is still executing
		assertEquals(1, result.getChildren().length); // one substitution result
		// is defined
		assertTrue(result.getChildren()[FIRST_INDEX].isSuccess()); // substitution
		// result is
		// successful
		assertEquals(0, result.getChildren()[FIRST_INDEX].getChildren().length); // substitution
		// result
		// has
		// no
		// children
		verify(session);
		verify(sessionResource);
	}

	/**
	 * Test that variable defined in a file as a resource property can be
	 * substituted. With session included.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCreateSubstitutedFileWithVariableDefinedAsResourceProperty_WithSession() throws Exception {
		String variable = createRandomVariablefromVarNameOne();
		String variableValue = RandomStringUtils.randomAlphabetic(10);
		List<Property> propertyList = createPropertyListWithSingleProperty(variableValue);

		// create source string
		String source = new StringBuilder().append(variable).toString();

		// create local test file
		File localFile = new File(testDirectory, randomFileName);
		FileUtils.write(localFile, source);

		completeSessionMockSetupWithPropertyDefined(propertyList);

		// invoke
		File actualfile = variableSubstitutionProvider.createSubstitutedFile(localFile, session, result);

		// load file
		String actual = FileUtils.readFileToString(actualfile);

		// test
		assertEquals(variableValue, actual);
		assertTrue(result.isExecuting()); // parent result is still executing
		assertEquals(1, result.getChildren().length); // one substitution result
		// is defined
		assertTrue(result.getChildren()[FIRST_INDEX].isSuccess()); // substitution
		// result is
		// successful
		assertEquals(2, result.getChildren()[FIRST_INDEX].getChildren().length); // substitution
		// result
		// has
		// two
		// children

		verify(session);
		verify(sessionResource);
	}

	/**
	 * Test that variable defined in a file as a resource property can be
	 * substituted. With session included.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCreateSubstitutedFileWithEmptyFile_WithSession() throws Exception {
		String variableValue = RandomStringUtils.randomAlphabetic(10);
		List<Property> propertyList = createPropertyListWithSingleProperty(variableValue);

		// create local test file
		File localFile = new File(testDirectory, randomFileName);
		FileUtils.write(localFile, EMPTY_STRING);

		completeSessionMockSetupWithPropertyDefined(propertyList);

		// invoke
		File actualfile = variableSubstitutionProvider.createSubstitutedFile(localFile, session, result);

		// load file
		String actual = FileUtils.readFileToString(actualfile);

		// test
		assertEquals(EMPTY_STRING, actual);
		assertTrue(result.isExecuting()); // parent result is still executing
		assertEquals(1, result.getChildren().length); // one substitution result
		// is defined
		assertTrue(result.getChildren()[FIRST_INDEX].isSuccess()); // substitution
		// result is
		// successful
		assertEquals(2, result.getChildren()[FIRST_INDEX].getChildren().length); // substitution
		// result
		// has
		// two
		// children
		verify(session);
		verify(sessionResource);
	}

	/**
	 * Test that file substitution fails if file doesn't exists.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = SourceFileValidationFailureException.class)
	public void testCreateSubstitutedFileFailsIfFileDoesntExists() throws Exception {

		// don't create local test file
		File localFile = new File(testDirectory, randomFileName);

		// complete mock setup
		replay(sessionResource);
		replay(session);

		// invoke
		variableSubstitutionProvider.createSubstitutedFile(localFile, session, result);

		// test
		verify(session);
		verify(sessionResource);
	}

	/**
	 * Test that when file substitution fails if file doesn't exists then
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testValidationResultReflectsFailedFileSubstitutionIfFileDoesntExists() throws Exception {

		// don't create local test file
		File localFile = new File(testDirectory, randomFileName);

		// complete mock setup
		replay(sessionResource);
		replay(session);

		// invoke and capture exception
		try {
			variableSubstitutionProvider.createSubstitutedFile(localFile, session, result);

			// fail if no exception is thrown
			fail();
		} catch (Exception e) {
			// pass through
		}

		// test
		assertTrue(result.isExecuting()); // parent result is still executing
		assertEquals(1, result.getChildren().length); // one substitution result
		// is defined
		assertEquals(ExecutionState.ERROR, result.getChildren()[FIRST_INDEX].getState()); // substitution
		// result
		// is
		// successful
		assertEquals(1, result.getChildren()[FIRST_INDEX].getChildren().length); // substitution
		// result
		// has
		// one
		// child

		verify(session);
		verify(sessionResource);
	}

	/**
	 * Test that variable defined in a file as a resource property can be
	 * substituted. With session included.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test(expected = SourceFileValidationFailureException.class)
	public void testCreateSubstitutedFileFailsIfFileIsToLarge() throws Exception {

		// create local test file
		File localFile = new File(testDirectory, randomFileName);

		// create file bigger then the maximum size
		RandomAccessFile f = new RandomAccessFile(localFile, "rw");
		f.setLength(VariableSubstitutionProviderImpl.DEFAULT_MAXIMUM_SIZE + 100);
		f.close();

		// complete mock setup
		replay(sessionResource);
		EasyMock.expect(session.getResource()).andReturn(sessionResource);
		replay(session);

		// invoke
		File actualfile = variableSubstitutionProvider.createSubstitutedFile(localFile, session, result);

		// load file
		FileUtils.readFileToString(actualfile);

		// test
		verify(session);
		verify(sessionResource);

	}

	/**
	 * Test that variable defined in a file as a resource property can be
	 * substituted. With session included.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testValidationResultReflectsFailedSubstitutedFileFailsIfFileIsToLarge() throws Exception {

		// create local test file
		File localFile = new File(testDirectory, randomFileName);

		// create file bigger then the maximum size
		RandomAccessFile f = new RandomAccessFile(localFile, "rw");
		f.setLength(VariableSubstitutionProviderImpl.DEFAULT_MAXIMUM_SIZE + 100);
		f.close();

		// complete mock setup
		replay(sessionResource);
		replay(session);

		// invoke and capture exception
		try {
			variableSubstitutionProvider.createSubstitutedFile(localFile, session, result);

			// fail if no exception is thrown
			fail();
		} catch (Exception e) {
			// pass through
		}

		// test
		assertTrue(result.isExecuting()); // parent result is still executing
		assertEquals(1, result.getChildren().length); // one substitution result
		// is defined
		assertEquals(ExecutionState.ERROR, result.getChildren()[FIRST_INDEX].getState()); // substitution
		// result
		// is
		// successful
		assertEquals(2, result.getChildren()[FIRST_INDEX].getChildren().length); // substitution
		// result
		// has
		// two
		// children
		verify(session);
		verify(sessionResource);
	}

}
