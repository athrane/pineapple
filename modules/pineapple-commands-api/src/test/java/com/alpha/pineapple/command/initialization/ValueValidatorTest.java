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

import static org.junit.Assert.fail;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.javautils.StackTraceHelper;

public class ValueValidatorTest {

	/**
	 * Test field used to testing. Field contains no annotation.
	 */
	String notAnnotatedField;

	/**
	 * Test field used to testing. Field contains annotation with no values defined.
	 */
	@ValidateValue({})
	String annotatedFieldWithNoValues;

	/**
	 * Test field used to testing. Field contains annotation with not null policy
	 * defined.
	 */
	@ValidateValue({ ValidationPolicy.NOT_NULL })
	String annotatedFieldWithNullPolicy;

	/**
	 * Test field used to testing. String field contains annotation with not empty
	 * policy defined.
	 */
	@ValidateValue({ ValidationPolicy.NOT_EMPTY })
	String annotatedStringFieldWithNotEmptyPolicy;

	/**
	 * Test field used to testing. Map field contains annotation with not empty
	 * policy defined.
	 */
	@ValidateValue({ ValidationPolicy.NOT_EMPTY })
	Map<?, ?> annotatedMapFieldWithNotEmptyPolicy;

	/**
	 * Test field used to testing. Collection field contains annotation with not
	 * empty policy defined.
	 */
	@ValidateValue({ ValidationPolicy.NOT_EMPTY })
	Collection<?> annotatedCollectionFieldWithNotEmptyPolicy;

	/**
	 * Test field used to testing. Object array field contains annotation with not
	 * empty policy defined.
	 */
	@ValidateValue({ ValidationPolicy.NOT_EMPTY })
	Object[] annotatedObjectArrayFieldWithNotEmptyPolicy;

	/**
	 * Test field used to testing. ArrayList field contains annotation with not
	 * empty policy defined.
	 */
	@ValidateValue({ ValidationPolicy.NOT_EMPTY })
	ArrayList<String> annotatedArrayListFieldWithNotEmptyPolicy;

	/**
	 * Test field used to testing. ArrayList field contains annotation with not
	 * empty policy defined.
	 */
	@ValidateValue({ ValidationPolicy.NOT_EMPTY })
	String[] annotatedStringArrayFieldWithNotEmptyPolicy;

	/**
	 * Test field used to testing. File field contains annotation with not empty
	 * policy defined.
	 */
	@ValidateValue({ ValidationPolicy.NOT_EMPTY })
	File annotatedFileFieldWithNotEmptyPolicy;

	/**
	 * Object under test.
	 */
	ValueValidator validator;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		validator = null;
	}

	/**
	 * Create value validator instance.
	 * 
	 * @return value validator instance.
	 */
	ValueValidator createValidator() {
		return new ValueValidator();
	}

	/**
	 * Test that value validator instance can be created.
	 */
	@Test
	public void testCanCreateInstance() {
		try {
			validator = createValidator();
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validator rejects undefined field.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedField() {
		try {
			// create initializer
			validator = createValidator();

			// setup objects
			Object value = "some string value";
			Field field = null;

			// force exception
			validator.validateValue(value, null);

			fail("Test should never reach here.");
		} catch (CommandInitializationFailedException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validator can validate with no annotation defined.
	 */
	@Test
	public void testCanValidateFieldWithNoAnnotation() {
		try {
			// create initializer
			validator = createValidator();

			// setup objects
			Object value = "some string value";
			Field field = this.getClass().getDeclaredField("notAnnotatedField");

			// force exception
			validator.validateValue(value, field);
		} catch (CommandInitializationFailedException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validator can validate field with annotation defined which contains
	 * an empty value set.
	 * 
	 */
	@Test
	public void testCanValidateFieldWithEmptyAnnotationValueSet() {
		try {
			// create initializer
			validator = createValidator();

			// setup objects
			Object value = "some string value";
			Field field = this.getClass().getDeclaredField("annotatedFieldWithNoValues");

			// force exception
			validator.validateValue(value, field);
		} catch (CommandInitializationFailedException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validator can validate field with annotation defined which contains
	 * an {@link ValidationPolicy.NOT_NULL} policy defined.
	 * 
	 */
	@Test
	public void testCanValidateFieldWithNotNullPolicyDefined() {
		try {
			// create initializer
			validator = createValidator();

			// setup objects
			Object value = "some string value";
			Field field = this.getClass().getDeclaredField("annotatedFieldWithNullPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (CommandInitializationFailedException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_NULL} policy fails if
	 * field value is null.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 * 
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testNotNullPolicyValidationFailsIfFieldIsNull() throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects
			Object value = null;
			Field field = this.getClass().getDeclaredField("annotatedFieldWithNullPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test validation of {@link String} field with
	 * {@link ValidationPolicy.NOT_EMPTY} policy defined.
	 */
	@Test
	public void testCanValidateStringFieldWithNotEmptyPolicyDefined() {
		try {
			// create initializer
			validator = createValidator();

			// setup objects
			Object value = "some string value";
			Field field = this.getClass().getDeclaredField("annotatedStringFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (CommandInitializationFailedException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy fails if
	 * field value is empty string.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testNotEmptyPolicyValidationFailsIfStringFieldIsEmpty() throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - empty string value
			String value = "";
			Field field = this.getClass().getDeclaredField("annotatedStringFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test validation of {@link Map} field with {@link ValidationPolicy.NOT_EMPTY}
	 * policy defined.
	 */
	@Test
	public void testCanValidateMapFieldWithNotEmptyPolicyDefined() {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - create non empty map
			Map value = new HashMap<String, String>();
			value.put("key", "value");
			Field field = this.getClass().getDeclaredField("annotatedMapFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (CommandInitializationFailedException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy fails if
	 * field value is empty map.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testNotEmptyPolicyValidationFailsIfMapFieldIsEmpty() throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - empty string value
			Map value = new HashMap<String, String>();
			Field field = this.getClass().getDeclaredField("annotatedMapFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test validation of {@link Collection} field with
	 * {@link ValidationPolicy.NOT_EMPTY} policy defined.
	 */
	@Test
	public void testCanValidateCollectionFieldWithNotEmptyPolicyDefined() {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - create non empty collection
			Collection value = new ArrayList<String>();
			value.add("some value");
			Field field = this.getClass().getDeclaredField("annotatedCollectionFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (CommandInitializationFailedException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy fails if
	 * field value is empty collection.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testNotEmptyPolicyValidationFailsIfCollectionFieldIsEmpty()
			throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - empty string value
			Collection value = new ArrayList<String>();
			Field field = this.getClass().getDeclaredField("annotatedCollectionFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test validation of {@link Object[]} field with
	 * {@link ValidationPolicy.NOT_EMPTY} policy defined.
	 */
	@Test
	public void testCanValidateObjectArrayFieldWithNotEmptyPolicyDefined() {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - create non empty object array
			Object[] value = new Object[1];
			value[0] = "some object value";
			Field field = this.getClass().getDeclaredField("annotatedObjectArrayFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (CommandInitializationFailedException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy fails if
	 * field value is empty object array.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testNotEmptyPolicyValidationFailsIfObjectArrayFieldIsEmpty()
			throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - empty object array
			Object[] value = new Object[0];
			Field field = this.getClass().getDeclaredField("annotatedObjectArrayFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test validation of {@link ArrayList} field with
	 * {@link ValidationPolicy.NOT_EMPTY} policy defined.
	 */
	@Test
	public void testCanValidateArrayListFieldWithNotEmptyPolicyDefined() {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - create non empty collection
			ArrayList<String> value = new ArrayList<String>();
			value.add("some value");
			Field field = this.getClass().getDeclaredField("annotatedArrayListFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (CommandInitializationFailedException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy fails if
	 * field value is empty ArrayList.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testNotEmptyPolicyValidationFailsIfArrayListFieldIsEmpty() throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - empty string value
			Collection value = new ArrayList<String>();
			Field field = this.getClass().getDeclaredField("annotatedArrayListFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy fails if
	 * field value is null.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testNotEmptyPolicyValidationFailsIfArrayListFieldIsNull() throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - empty string value
			Collection value = null;
			Field field = this.getClass().getDeclaredField("annotatedArrayListFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy fails if
	 * field value is null.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testNotEmptyPolicyValidationFailsIfStringArrayFieldIsNull()
			throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - empty string value
			String[] value = null;
			Field field = this.getClass().getDeclaredField("annotatedStringArrayFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy fails if is
	 * string array contains zero elements.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testNotEmptyPolicyValidationFailsIfStringArrayFieldContainsZeroElements()
			throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - empty string value
			String[] value = new String[0];

			Field field = this.getClass().getDeclaredField("annotatedStringArrayFieldWithNotEmptyPolicy");

			// validate
			validator.validateValue(value, field);

		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy succeeds if
	 * is string array contains one element.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test
	public void testNotEmptyPolicyValidationSucceedsIfStringArrayFieldContainsOneElement()
			throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - empty string value
			String[] value = new String[1];
			value[0] = "item0";

			Field field = this.getClass().getDeclaredField("annotatedStringArrayFieldWithNotEmptyPolicy");

			// validate
			validator.validateValue(value, field);

		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy succeeds if
	 * is string array contains two elements.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test
	public void testNotEmptyPolicyValidationSucceedsIfStringArrayFieldContainsTwoElements()
			throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - empty string value
			String[] value = new String[2];
			value[0] = "item0";
			value[1] = "item1";

			Field field = this.getClass().getDeclaredField("annotatedStringArrayFieldWithNotEmptyPolicy");

			// validate
			validator.validateValue(value, field);

		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test validation of {@link File} field with {@link ValidationPolicy.NOT_EMPTY}
	 * policy defined.
	 */
	@Test
	public void testCanValidateFileFieldWithNotEmptyPolicyDefined() {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - create file
			File value = new File("c:\\temp");
			Field field = this.getClass().getDeclaredField("annotatedFileFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (CommandInitializationFailedException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy fails if
	 * field value is null.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testNotEmptyPolicyValidationFailsIfFileFieldIsNull() throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - null file
			File value = null;
			Field field = this.getClass().getDeclaredField("annotatedArrayListFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy fails if
	 * field value contains empty file name, i.e. "".
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testNotEmptyPolicyValidationFailsIfFileFieldIsEmpty() throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - null file
			File value = new File("");
			Field field = this.getClass().getDeclaredField("annotatedArrayListFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy fails if
	 * field value contains empty file name, i.e. "".
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testNotEmptyPolicyValidationFailsIfFileFieldIsEmpty2() throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - null file
			File value = new File("", "");
			Field field = this.getClass().getDeclaredField("annotatedArrayListFieldWithNotEmptyPolicy");

			// force exception
			validator.validateValue(value, field);
		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy succeeds if
	 * parent is empty and child is defined.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test
	public void testNotEmptyPolicyValidationSucceedsIfFileFieldContainsEmptyParentAndDefinedChild()
			throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - empty parent, defined child
			File value = new File("", "child");
			Field field = this.getClass().getDeclaredField("annotatedStringArrayFieldWithNotEmptyPolicy");

			// validate
			validator.validateValue(value, field);

		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that validation of {@link ValidationPolicy.NOT_EMPTY} policy succeeds if
	 * parent is defined and empty child.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	@Test
	public void testNotEmptyPolicyValidationSucceedsIfFileFieldContainsDefinedParentAndEmptyChild()
			throws CommandInitializationFailedException {
		try {
			// create initializer
			validator = createValidator();

			// setup objects - empty parent, defined child
			File value = new File("parent", "");
			Field field = this.getClass().getDeclaredField("annotatedStringArrayFieldWithNotEmptyPolicy");

			// validate
			validator.validateValue(value, field);

		} catch (SecurityException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		} catch (NoSuchFieldException e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

}
