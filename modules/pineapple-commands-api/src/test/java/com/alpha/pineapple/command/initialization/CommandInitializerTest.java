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

package com.alpha.pineapple.command.initialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.junit.*;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.command.InitializeAndValidateAnnontatedCommand;
import com.alpha.pineapple.command.InitializeAnnotatedWithStringArraysCommand;
import com.alpha.pineapple.command.MultipleInitializeAnnontatedCommand;
import com.alpha.pineapple.command.InitializeAnnotatedCommand;
import com.alpha.pineapple.command.NullCommand;
import com.alpha.pineapple.command.InitializeAnnontatedWithEmptyValueCommand;

public class CommandInitializerTest {

	/**
	 * Object under test.
	 */
	CommandInitializer initializer;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		initializer = null;
	}

	/**
	 * Create command initializer instance.
	 * 
	 * @return command initializer instance.
	 */
	CommandInitializer createInitializer() {
		return new CommandInitializerImpl();
	}

	/**
	 * Test that command initializer instance can be created.
	 */
	@Test
	public void testCanCreateInstance() {
		try {
			initializer = createInitializer();
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer rejects undefined context.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedContext() {
		try {
			// create initializer
			initializer = createInitializer();

			// setup objects
			Command command = new NullCommand();

			// force exception
			initializer.initialize(null, command);

			fail("Test should never reach here.");
		} catch (CommandInitializationFailedException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer rejects undefined command.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedCommand() {
		try {
			// create initializer
			initializer = createInitializer();

			// setup objects
			Context context = new ContextBase();

			// force exception
			initializer.initialize(context, null);

			fail("Test should never reach here.");
		} catch (CommandInitializationFailedException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize command with no annotations.
	 */
	@Test
	public void testCanInitializeCommandWithoutAnnotations() {
		try {
			// create initializer
			initializer = createInitializer();

			// setup objects
			Context context = new ContextBase();
			Command command = new NullCommand();

			// force exception
			initializer.initialize(context, command);

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize command with single annotation.
	 */
	@Test
	public void testCanInitializeCommandWithSingleInitializeAnnotation() {
		try {
			// create initializer
			initializer = createInitializer();

			// setup objects
			Context context = new ContextBase();
			String actualValue = "some value";
			context.put(InitializeAnnotatedCommand.CONTEXT_KEY, actualValue);

			InitializeAnnotatedCommand command = new InitializeAnnotatedCommand();

			// force exception
			initializer.initialize(context, command);

			// test
			assertEquals(command.annotatedField, actualValue);

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize command with multiple annotations.
	 */
	@Test
	public void testCanInitializeCommandWithMultipleInitializeAnnotations() {
		try {
			// create initializer
			initializer = createInitializer();

			// setup objects
			Context context = new ContextBase();
			context.put(MultipleInitializeAnnontatedCommand.CONTEXT_KEY1, "value1");
			context.put(MultipleInitializeAnnontatedCommand.CONTEXT_KEY2, 1001);

			Command command = new MultipleInitializeAnnontatedCommand();

			// force exception
			initializer.initialize(context, command);
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize command with empty annotation value.
	 */
	@Test
	public void testCanInitializeCommandWithEmptyIniitalizeAnnotationValue() {
		try {
			// create initializer
			initializer = createInitializer();

			// setup objects
			Context context = new ContextBase();
			context.put(InitializeAnnontatedWithEmptyValueCommand.CONTEXT_KEY, "some value");

			Command command = new InitializeAnnontatedWithEmptyValueCommand();

			// force exception
			initializer.initialize(context, command);
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}

	}

	/**
	 * Test that initialization fails if annotation value is empty.
	 * 
	 * @throws Exception
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testInitializationFailsIfInitializeAnnotationValueIsntDefinedInContext() throws Exception {
		// create initializer
		initializer = createInitializer();

		// setup objects
		Context context = new ContextBase();
		// InitializeAnnotatedCommand.CONTEXT_KEY isn't add to provoke exception

		Command command = new InitializeAnnotatedCommand();

		// force exception
		initializer.initialize(context, command);
	}

	/**
	 * Test that initialization fails if type of context object and setter method
	 * isn't compatible.
	 * 
	 * @throws Exception
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testInitializationFailsIfTypesIsntCompatible() throws Exception {
		// create initializer
		initializer = createInitializer();

		// setup objects
		Context context = new ContextBase();

		// add object with which can't convert to string as the setter on
		// InitializeAnnontatedWithEmptyValueCommand expects
		context.put(InitializeAnnotatedCommand.CONTEXT_KEY, new ArrayList<Command>());

		Command command = new InitializeAnnotatedCommand();

		// force exception
		initializer.initialize(context, command);
	}

	/**
	 * Test that initializer can initialize command with single annotation and
	 * validation annotation.
	 */
	@Test
	public void testCanInitializeCommandWithSingleInitializeAndValidateAnnotation() {
		try {
			// create initializer
			initializer = createInitializer();

			// setup objects
			Context context = new ContextBase();
			context.put(InitializeAndValidateAnnontatedCommand.CONTEXT_KEY, "some value");

			Command command = new InitializeAndValidateAnnontatedCommand();

			// force exception
			initializer.initialize(context, command);

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize string field with null value.
	 */
	@Test
	public void testCanInitializeStringFieldWithNull() {
		try {
			// create initializer
			initializer = createInitializer();

			// setup objects
			Context context = new ContextBase();
			context.put(InitializeAnnotatedCommand.CONTEXT_KEY, null);

			Command command = new InitializeAnnotatedCommand();

			// force exception
			initializer.initialize(context, command);

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize int field with null value.
	 */
	@Test
	public void testCanInitializeIntFieldWithNull() {
		try {
			// create initializer
			initializer = createInitializer();

			// setup objects
			Context context = new ContextBase();
			context.put(MultipleInitializeAnnontatedCommand.CONTEXT_KEY1, "value1");
			context.put(MultipleInitializeAnnontatedCommand.CONTEXT_KEY2, null);

			Command command = new MultipleInitializeAnnontatedCommand();

			// force exception
			initializer.initialize(context, command);

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize int field with string object containing
	 * an integer, i.e. "5".
	 */
	@Test
	public void testCanInitializeIntFieldWithStringObject() {
		try {
			// create initializer
			initializer = createInitializer();

			// setup objects
			Context context = new ContextBase();
			context.put(MultipleInitializeAnnontatedCommand.CONTEXT_KEY1, "value1");
			context.put(MultipleInitializeAnnontatedCommand.CONTEXT_KEY2, "5");

			Command command = new MultipleInitializeAnnontatedCommand();

			// force exception
			initializer.initialize(context, command);

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize command with single annotation on field
	 * of type String[] with array containing two elements.
	 */
	@Test
	public void testCanInitializeStringArrayFieldWithArrayContainingTwoElements() {
		try {
			// create initializer
			initializer = createInitializer();

			// create value
			String[] value = new String[2];
			value[0] = "item1";
			value[0] = "item2";

			// setup objects
			Context context = new ContextBase();
			context.put(InitializeAnnotatedWithStringArraysCommand.CONTEXT_KEY, value);

			InitializeAnnotatedWithStringArraysCommand command;
			command = new InitializeAnnotatedWithStringArraysCommand();

			// force exception
			initializer.initialize(context, command);

			// get actual value
			String[] actualValue = command.annotatedStringArrayField;

			// test
			assertEquals(value, actualValue);
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize command with single annotation on field
	 * of type String[] with array containing a single element.
	 */
	@Test
	public void testCanInitializeStringArrayFieldWithStringContainingOneElement() {
		try {
			// create initializer
			initializer = createInitializer();

			// create value
			String[] expectedValue = new String[2];
			expectedValue[0] = "item1";
			expectedValue[0] = "item2";

			// setup objects
			Context context = new ContextBase();
			context.put(InitializeAnnotatedWithStringArraysCommand.CONTEXT_KEY, expectedValue);

			InitializeAnnotatedWithStringArraysCommand command;
			command = new InitializeAnnotatedWithStringArraysCommand();

			// force exception
			initializer.initialize(context, command);

			// get actual value
			String[] actualValue = command.annotatedStringArrayField;

			// test
			assertEquals(expectedValue, actualValue);
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize command with single annotation on field
	 * of type String[] with array containing zero elements.
	 */
	@Test
	public void testCanInitializeStringArrayFieldWithStringContainingZeroElements() {
		try {
			// create initializer
			initializer = createInitializer();

			// create value
			String[] expectedValue = new String[0];

			// setup objects
			Context context = new ContextBase();
			context.put(InitializeAnnotatedWithStringArraysCommand.CONTEXT_KEY, expectedValue);

			InitializeAnnotatedWithStringArraysCommand command;
			command = new InitializeAnnotatedWithStringArraysCommand();

			// force exception
			initializer.initialize(context, command);

			// get actual value
			String[] actualValue = command.annotatedStringArrayField;

			// test
			assertEquals(expectedValue, actualValue);
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize command with single annotation on field
	 * of type String[] with null value.
	 */
	@Test
	public void testCanInitializeStringArrayFieldWithNull() {
		try {
			// create initializer
			initializer = createInitializer();

			// setup objects
			Context context = new ContextBase();
			context.put(InitializeAnnotatedWithStringArraysCommand.CONTEXT_KEY, null);

			InitializeAnnotatedWithStringArraysCommand command;
			command = new InitializeAnnotatedWithStringArraysCommand();

			// force exception
			initializer.initialize(context, command);

			// get actual value
			String[] actualValue = command.annotatedStringArrayField;

			// expected value is null
			String[] expectedValue = null;

			// test
			assertEquals(expectedValue, actualValue);
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize command with single annotation on field
	 * of type String[] with a String object.
	 */
	@Test
	public void testCanInitializeStringArrayFieldWithStringObject() {
		try {
			// create initializer
			initializer = createInitializer();

			// create value
			String expectedValue = new String("balthier");

			// expected field value
			String[] expectedFieldValue = new String[1];
			expectedFieldValue[0] = expectedValue;

			// setup objects
			Context context = new ContextBase();
			context.put(InitializeAnnotatedWithStringArraysCommand.CONTEXT_KEY, expectedValue);

			InitializeAnnotatedWithStringArraysCommand command;
			command = new InitializeAnnotatedWithStringArraysCommand();

			// force exception
			initializer.initialize(context, command);

			// get actual value
			String[] actualValue = command.annotatedStringArrayField;

			// test
			assertEquals(expectedFieldValue, actualValue);
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that initializer can initialize command with single annotation on field
	 * of type String[] with a String object containing a comma separated list.
	 */
	@Test
	public void testCanInitializeStringArrayFieldWithStringObjectContainingCommaSeparatedList() {
		try {
			// create initializer
			initializer = createInitializer();

			// create value
			String expectedValue = new String("balthier, yuna, jain");

			// expected field value
			String[] expectedFieldValue = new String[3];
			expectedFieldValue[0] = "balthier";
			expectedFieldValue[1] = "yuna";
			expectedFieldValue[2] = "jain";

			// setup objects
			Context context = new ContextBase();
			context.put(InitializeAnnotatedWithStringArraysCommand.CONTEXT_KEY, expectedValue);

			InitializeAnnotatedWithStringArraysCommand command;
			command = new InitializeAnnotatedWithStringArraysCommand();

			// force exception
			initializer.initialize(context, command);

			// get actual value
			String[] actualValue = command.annotatedStringArrayField;

			// test
			assertEquals(expectedFieldValue, actualValue);
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

}
