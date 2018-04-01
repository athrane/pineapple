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

package com.alpha.javautils.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.javautils.StackTraceHelper;

public class ReflectionHelperTest {
	/**
	 * Class with no single argument constructor which a String Type.
	 */
	class ClassWithNoSingleArgStringConstructor {

	}

	/**
	 * Class with setter method.
	 */
	class ClassWithSetter {
		boolean isSetterInvoked = false;

		int primitiveIntField = 0;

		String lamp;

		String[] stringArray;

		public void setLamp(String lamp) {
			this.lamp = lamp;
			isSetterInvoked = true;
		}
	}

	/**
	 * Object under test.
	 */
	ReflectionHelper helper;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		helper = null;
	}

	/**
	 * Create reflection helper instance.
	 * 
	 * @return reflection helper instance.
	 */
	ReflectionHelper createReflectionHelper() {
		return new ReflectionHelper();
	}

	/**
	 * Test that reflection helper instance can be created.
	 */
	@Test
	public void testCanCreateInstance() {
		try {
			helper = createReflectionHelper();
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that helper rejects undefined class name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateObject1RejectsUndefinedClassName() {
		// create helper
		helper = createReflectionHelper();

		// force exception
		helper.createObject(null);

		fail("Test should never reach here.");
	}

	/**
	 * Test that helper rejects empty class name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateObject1RejectsEmptyClassName() {
		// create helper
		helper = createReflectionHelper();

		// force exception
		helper.createObject("");

		fail("Test should never reach here.");
	}

	/**
	 * Test that instance creation fails for non existing class name.
	 */
	@Test(expected = InstantiationError.class)
	public void testCreateObject1FailsForNonExistingClassName() {
		// create helper
		helper = createReflectionHelper();

		// force exception
		helper.createObject("some non existing class name");

		fail("Test should never reach here.");
	}

	/**
	 * Test that helper can create object.
	 */
	@Test
	public void testCreateObject1CanCreateInstance() {
		// create helper
		helper = createReflectionHelper();

		// force exception
		Object result = helper.createObject(String.class.getName());
		assertNotNull(result);
	}

	/**
	 * Test that helper can create object with correct type.
	 */
	@Test
	public void testCreateObject1CanCreateInstanceWithCorrectType() {
		// create helper
		helper = createReflectionHelper();

		// force exception
		Object result = helper.createObject(String.class.getName());
		assertTrue(result instanceof String);
	}

	/**
	 * Test that helper rejects undefined class name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateObject2RejectsUndefinedClassName() {
		// create helper
		helper = createReflectionHelper();

		// force exception
		helper.createObject(null, "some argument");

		fail("Test should never reach here.");
	}

	/**
	 * Test that helper rejects undefined argument.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateObject2RejectsUndefinedConstructorArgument() {
		// create helper
		helper = createReflectionHelper();

		// force exception
		helper.createObject(String.class, null);

		fail("Test should never reach here.");
	}

	/**
	 * Test that instance creation fails class with no constructor which takes a
	 * single string as argument.
	 */
	@Test(expected = InstantiationError.class)
	public void testCreateObject2FailsForClassWithNoSingleArgStringConstructor() {
		// create helper
		helper = createReflectionHelper();

		// force exception
		helper.createObject(ClassWithNoSingleArgStringConstructor.class, "..");

		fail("Test should never reach here.");
	}

	/**
	 * Test that helper can create object.
	 */
	@Test
	public void testCreateObject2CanCreateInstance() {
		// create helper
		helper = createReflectionHelper();

		// force exception
		Object result = helper.createObject(String.class, "some value");
		assertNotNull(result);
	}

	/**
	 * Test that helper can create object with correct type.
	 */
	@Test
	public void testCreateObject2CanCreateInstanceWithCorrectType() {
		// create helper
		helper = createReflectionHelper();

		// force exception
		Object result = helper.createObject(String.class, "some value");
		assertTrue(result instanceof String);
	}

	/**
	 * Test that helper rejects undefined field name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateSetterMethodNameRejectsUndefinedClassName() {
		// create helper
		helper = createReflectionHelper();

		// force exception
		helper.createSetterMethodName(null);

		fail("Test should never reach here.");
	}

	/**
	 * Can create setter method name.
	 */
	@Test
	public void testCreateSetterMethodName() {
		// create helper
		helper = createReflectionHelper();

		// create name
		String result = helper.createSetterMethodName("bird");

		// expected value
		String expected = "setBird";

		// test
		assertEquals(expected, result);
	}

	/**
	 * Can create setter method name with multiple capital letters
	 */
	@Test
	public void testCreateSetterMethodName2() {
		// create helper
		helper = createReflectionHelper();

		// create name
		String result = helper.createSetterMethodName("blackBird");

		// expected value
		String expected = "setBlackBird";

		// test
		assertEquals(expected, result);
	}

	/**
	 * Can get setter method from object.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCanGetSetterMethod() throws SecurityException, Exception {
		// create helper
		helper = createReflectionHelper();

		// create field name
		String fieldName = "setLamp";

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("lamp");

		// get setter method
		Method method = helper.getSetterMethod(targetObject, field);

		// test
		assertNotNull(method);
	}

	/**
	 * Get setter method from object returns correct named method.
	 */
	@Test
	public void testGetSetterMethodReturnCorrectMethod() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create field name
		String setterName = "setLamp";

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("lamp");

		// get setter method
		Method method = helper.getSetterMethod(targetObject, field);

		// test
		assertEquals(setterName, method.getName());
	}

	/**
	 * Test that helper rejects undefined target object.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInvokeSetterRejectsUndefinedTargetObject() throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException, Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("lamp");

		// setter parameter
		String setterParameter = "some value";

		// force exception
		helper.invokeSetterMethod(null, field, setterParameter);

		fail("Test should never reach here.");
	}

	/**
	 * Test that helper rejects undefined field.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInvokeSetterRejectsUndefinedField() throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException, Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("lamp");

		// setter parameter
		String setterParameter = "some value";

		// force exception
		helper.invokeSetterMethod(targetObject, null, setterParameter);

		fail("Test should never reach here.");
	}

	/**
	 * Test that helper accepts undefined setter parameter.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvokeSetterAcceptsUndefinedSetterParam() throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException, Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("lamp");

		// setter parameter
		String setterParameter = null;

		// force exception
		helper.invokeSetterMethod(targetObject, field, setterParameter);
	}

	/**
	 * Test that helper invoke setter method actually invokes the method.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvokeSetterMethodInvokesCorrectMethod() throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException, Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("lamp");

		// setter parameter
		String setterParameter = "some value";

		// test initial state
		assertFalse(targetObject.isSetterInvoked);

		// force exception
		helper.invokeSetterMethod(targetObject, field, setterParameter);

		// test
		assertTrue(targetObject.isSetterInvoked);
	}

	/**
	 * Test that helper invoke setter method actually invokes the method.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvokeSetterMethodSetsCorrectValue() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("lamp");

		// setter parameter
		String setterParameter = "some value";

		// force exception
		helper.invokeSetterMethod(targetObject, field, setterParameter);

		// test
		assertEquals(setterParameter, targetObject.lamp);
	}

	/**
	 * Test that helper rejects undefined target object.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetFieldValueRejectsUndefinedTargetObject() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("lamp");

		// field value
		String fieldValue = "some value";

		// force exception
		helper.setFieldValue(null, field, fieldValue);

		fail("Test should never reach here.");
	}

	/**
	 * Test that helper rejects undefined field.
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetFieldValueRejectsUndefinedField() throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException, Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("lamp");

		// field value
		String fieldValue = "some value";

		// set field
		helper.setFieldValue(targetObject, null, fieldValue);

		fail("Test should never reach here.");
	}

	/**
	 * Test that helper set field value can set String value on String field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFieldValueCanSetStringFieldWithStringObject() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("lamp");

		// field value
		String fieldValue = "some value";

		// set field
		helper.setFieldValue(targetObject, field, fieldValue);

		// test
		assertEquals(fieldValue, targetObject.lamp);
	}

	/**
	 * Test that helper set field value can set null on String[] field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFieldValueCanSetStringArrayFieldWithNull() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("stringArray");

		// field value
		String[] fieldValue = null;

		// set field
		helper.setFieldValue(targetObject, field, fieldValue);

		// test
		assertEquals(fieldValue, targetObject.stringArray);
	}

	/**
	 * Test that helper set field value can set String array with zero elements on
	 * String[] field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFieldValueCanSetStringArrayFieldWithArrayContainingZeroElements() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("stringArray");

		// field value
		String[] fieldValue = new String[0];

		// set field
		helper.setFieldValue(targetObject, field, fieldValue);

		// test
		assertEquals(fieldValue, targetObject.stringArray);
	}

	/**
	 * Test that helper set field value can set String array with one element on
	 * String[] field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFieldValueCanSetStringArrayFieldWithArrayContainingOneElement() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("stringArray");

		// field value
		String[] fieldValue = new String[1];
		fieldValue[0] = "item1";

		// set field
		helper.setFieldValue(targetObject, field, fieldValue);

		// test
		assertEquals(fieldValue, targetObject.stringArray);
	}

	/**
	 * Test that helper set field value can set String array with two elements on
	 * String[] field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFieldValueCanSetStringArrayFieldWithArrayContainingTwoElements() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("stringArray");

		// field value
		String[] fieldValue = new String[2];
		fieldValue[0] = "item1";
		fieldValue[0] = "item2";

		// set field
		helper.setFieldValue(targetObject, field, fieldValue);

		// test
		assertEquals(fieldValue, targetObject.stringArray);
	}

	/**
	 * Test that helper set field value can set String object on String[] field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFieldValueCanSetStringArrayFieldWithStringObject() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("stringArray");

		// field value
		String fieldValue = "Yuna";

		// set field
		helper.setFieldValue(targetObject, field, fieldValue);

		// expected field value
		String[] expectedValue = new String[1];
		expectedValue[0] = fieldValue;

		// test
		assertEquals(expectedValue, targetObject.stringArray);
	}

	/**
	 * Test that helper set field value can set String object which contains a comma
	 * separated list on String[] field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFieldValueCanSetStringArrayFieldWithStringObjectContainingCommaSeparatedList() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("stringArray");

		// field value
		String fieldValue = "Yuna, Jain";

		// expected field value
		String[] expectedValue = new String[2];
		expectedValue[0] = "Yuna";
		expectedValue[1] = "Jain";

		// set field
		helper.setFieldValue(targetObject, field, fieldValue);

		// test
		assertEquals(expectedValue, targetObject.stringArray);
	}

	/**
	 * Test that helper set field value method can set String value containing a
	 * comma separated list on String field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFieldValueCanSetStringFieldWithStringObjectContainingCommaSeparatedList() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("lamp");

		// field value
		String fieldValue = "item1, item2, item3";

		// set field
		helper.setFieldValue(targetObject, field, fieldValue);

		// test
		assertEquals(fieldValue, targetObject.lamp);
	}

	/**
	 * Test that set field value can set primitive integer value on int field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFieldValueCanSetIntFieldWithPrimitiveIntValue() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("primitiveIntField");

		// field value
		int fieldValue = 1001;

		// set field
		helper.setFieldValue(targetObject, field, fieldValue);

		// test
		assertEquals(fieldValue, targetObject.primitiveIntField);
	}

	/**
	 * Test that set field value can set String object containing an integer, e.g.
	 * "5" on int field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFieldValueCanSetIntFieldWithStringObject() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("primitiveIntField");

		// field value
		String fieldValue = "1001";

		// set field
		helper.setFieldValue(targetObject, field, fieldValue);

		// expected field value
		int expectedValue = 1001;

		// test
		assertEquals(expectedValue, targetObject.primitiveIntField);
	}

	/**
	 * Test that set field value method skips setting null value on primitive
	 * integer field.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFieldValueSkipsSettingNullOnPrimitiveIntField() throws Exception {
		// create helper
		helper = createReflectionHelper();

		// create target object
		ClassWithSetter targetObject = new ClassWithSetter();

		// get field
		Field field = targetObject.getClass().getDeclaredField("primitiveIntField");

		// field value
		int fieldValue = 0;

		// set field
		helper.setFieldValue(targetObject, field, null);

		// test, notice that field value is initially 0.
		assertEquals(fieldValue, targetObject.primitiveIntField);
	}

}
