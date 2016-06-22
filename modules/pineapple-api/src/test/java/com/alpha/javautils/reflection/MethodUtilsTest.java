/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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

import static com.alpha.javautils.reflection.MethodUtils.classIsDeclaredInPackage;
import static com.alpha.javautils.reflection.MethodUtils.classIsDeclaredInPackages;
import static com.alpha.javautils.reflection.MethodUtils.invokeMethodWithNoArgs;
import static com.alpha.javautils.reflection.MethodUtils.isMethodWithNoParameters;
import static com.alpha.javautils.reflection.MethodUtils.methodNameStartsWithGet;
import static com.alpha.javautils.reflection.MethodUtils.methodNameStartsWithIs;
import static com.alpha.javautils.reflection.MethodUtils.methodReturnsArray;
import static com.alpha.javautils.reflection.MethodUtils.methodReturnsPrimitiveBoolean;
import static com.alpha.javautils.reflection.MethodUtils.resolveGetterMethods;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.management.NotificationBroadcaster;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import weblogic.management.configuration.DomainMBean;

/**
 * Unit test of the <code>MethodUtilsImpl</code> class.
 */
public class MethodUtilsTest {

    /**
     * Model package for WebLogic MBeans.
     */
    static final String[] MODEL_PACKAGES = { "weblogic.management.configuration", "weblogic.management.security",
	    "weblogic.management.security.audit", "weblogic.management.security.authentication",
	    "weblogic.management.security.authorization", "weblogic.management.security.credential",
	    "weblogic.management.security.pk" };

    /**
     * Method matcher object.
     */
    GetterMethodMatcher matcher;

    @Before
    public void setUp() throws Exception {
	// create mock matcher
	matcher = EasyMock.createMock(GetterMethodMatcher.class);

    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test classIsDeclaredInPackage method fails if class isn't declared in
     * package.
     */
    @Test
    public void testClassIsDeclaredInPackageFails() {
	Class<?> classObject = String.class;
	String packageName = Method.class.getPackage().getName();
	assertFalse(classIsDeclaredInPackage(classObject, packageName));
    }

    /**
     * Test classIsDeclaredInPackage succeeds if class is declared in package.
     */
    @Test
    public void testClassIsDeclaredInPackageSucceeds() {
	Class<?> classObject = String.class;
	String packageName = classObject.getPackage().getName();
	assertTrue(classIsDeclaredInPackage(classObject, packageName));
    }

    /**
     * Test classIsDeclaredInPackages method fails if class isn't declared in
     * any of the supplied packages.
     */
    @Test
    public void testClassIsDeclaredInPackagesFails() {
	Class<?> classObject = String.class;
	String[] packageNames = { Method.class.getPackage().getName(), "uggi.package" };
	assertFalse(classIsDeclaredInPackages(classObject, packageNames));
    }

    /**
     * Test classIsDeclaredInPackages succeeds if class is declared in one of
     * the supplied packages.
     */
    @Test
    public void testClassIsDeclaredInPackagesSucceeds() {
	Class<?> classObject = String.class;
	String[] packageNames = { "uggi.package", classObject.getPackage().getName(), "buggi.package" };
	assertTrue(classIsDeclaredInPackages(classObject, packageNames));
    }

    /**
     * Test classIsDeclaredInPackages fails if class isn't declared in one of
     * the supplied packages.
     */
    @Test
    public void testClassIsDeclaredInPackagesFailsWithJavaLangReflectProxy() {
	Class<?> classObject = Proxy.class;
	assertFalse(classIsDeclaredInPackages(classObject, MODEL_PACKAGES));
    }

    /**
     * Test classIsDeclaredInPackages fails if class isn't declared in one of
     * the supplied packages.
     */
    @Test
    public void testClassIsDeclaredInPackagesFailsWithNotificationBroadcaster() {
	Class<?> classObject = NotificationBroadcaster.class;
	assertFalse(classIsDeclaredInPackages(classObject, MODEL_PACKAGES));
    }

    /**
     * Test methodNameStartsWithGet succeeds if method starts with 'get'.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testMethodNameStartsWithGetSucceeds() throws Exception {
	// test type
	class SimpleType {
	    public int getXX() {
		return 1;
	    }
	}

	Class<?> classObject = SimpleType.class;
	Method method;
	method = classObject.getMethod("getXX", null);

	// test
	assertTrue(methodNameStartsWithGet(method));
    }

    /**
     * Test methodNameStartsWithGet fails if method starts with 'Get', ie.
     * Capital 'G'.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testMethodNameStartsWithGetFailsIfNameIsntLowercase() throws Exception {
	// test type
	class SimpleType {
	    public int GetXX() {
		return 1;
	    }
	}

	Class<?> classObject = SimpleType.class;
	Method method;
	method = classObject.getMethod("GetXX", null);

	// test
	assertFalse(methodNameStartsWithGet(method));
    }

    /**
     * Test methodNameStartsWithGet succeeds if method starts with 'is'.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testMethodNameStartsWithIsSucceeds() throws Exception {
	// test type
	class SimpleType {
	    public int isXX() {
		return 1;
	    }
	}

	Class<?> classObject = SimpleType.class;
	Method method;
	method = classObject.getMethod("isXX", null);

	// test
	assertTrue(methodNameStartsWithIs(method));
    }

    /**
     * Test methodNameStartsWithGet fails if method starts with 'Get', ie.
     * Capital 'I'.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testMethodNameStartsWithIsFailsIfNameIsntLowercase() throws Exception {
	// test type
	class SimpleType {
	    public int IsXX() {
		return 1;
	    }
	}

	Class<?> classObject = SimpleType.class;
	Method method;
	method = classObject.getMethod("IsXX", null);

	// test
	assertFalse(methodNameStartsWithIs(method));
    }

    /**
     * Test methodReturnsPrimitiveBoolean succeeds if method return a primitive
     * boolean.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testMethodReturnsPrimitiveBooleanSucceeds() throws Exception {
	// test type
	class SimpleType {
	    public boolean isXX() {
		return true;
	    }
	}

	Class<?> classObject = SimpleType.class;
	Method method;
	method = classObject.getMethod("isXX", null);

	// test
	assertTrue(methodReturnsPrimitiveBoolean(method));
    }

    /**
     * Test methodReturnsPrimitiveBoolean fails if method doesn't return a
     * primitive boolean.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testMethodReturnsPrimitiveBooleanFails() throws Exception {
	// test type
	class SimpleType {
	    public Boolean isXX() {
		return new Boolean(true);
	    }

	    public int isYY() {
		return 1;
	    }

	    public String isZZ() {
		return "some-string";
	    }
	}

	Class<?> classObject = SimpleType.class;
	Method method = classObject.getMethod("isXX", null);
	Method method2 = classObject.getMethod("isYY", null);
	Method method3 = classObject.getMethod("isZZ", null);

	// test
	assertFalse(methodReturnsPrimitiveBoolean(method));
	assertFalse(methodReturnsPrimitiveBoolean(method2));
	assertFalse(methodReturnsPrimitiveBoolean(method3));
    }

    /**
     * test resolveGetterMethods method rejects undefined target object.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResolveMethodRejectsundefinedTargetObject() {
	// invoke to provoke exception
	resolveGetterMethods(null, "some-attribute-name", matcher);
    }

    /**
     * test resolveGetterMethods method rejects undefined attribute name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResolveMethodRejectsundefinedAttributeName() {
	// invoke to provoke exception
	resolveGetterMethods("a-target-object", null, matcher);
    }

    /**
     * test resolveGetterMethods method rejects undefined matcher.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResolveMethodRejectsundefinedMatcher() {
	// invoke to provoke exception
	resolveGetterMethods("a-target-object", "some-attribute-name", null);
    }

    /**
     * test resolveGetterMethods method returns empty method array if attribute
     * name is empty string.
     */
    @Test
    public void testResolveMethodReturnsEmptyArrayIfAttributeNameIsEmpty() {
	// resolve methods
	Method[] result = resolveGetterMethods("a-target-object", "", matcher);

	// test
	assertNotNull(result);
	assertEquals(0, result.length);
    }

    /**
     * test resolveGetterMethods method returns empty method array if matcher
     * doesn't match any methods.
     */
    @Test
    public void testResolveMethodReturnsEmptyArrayIfMatcherDoesntMatchAnyMethods() {
	// setup match mock
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(false).anyTimes();
	EasyMock.replay(matcher);

	// resolve methods
	Method[] result = resolveGetterMethods("a-target-object", "some-attribute-name", matcher);

	// test
	assertNotNull(result);
	assertEquals(0, result.length);

	// verify mock
	EasyMock.verify(matcher);
    }

    /**
     * test resolveGetterMethods method returns empty method array if matcher
     * match any methods on a simple type but with an attribute name which isn't
     * defined on the object.
     */
    @Test
    public void testResolveMethodReturnsEmptyArrayIfMatcherMatchAllOnSimpleType() {
	// declare type
	class SimpleType {
	}

	// setup match mock
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("hashcode");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("getClass");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("wait");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("wait");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("wait");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("equals");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("notify");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("notifyAll");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("toString");
	EasyMock.replay(matcher);

	// resolve methods
	Method[] result = resolveGetterMethods(new SimpleType(), "some-attribute-name", matcher);

	// test
	assertNotNull(result);
	assertEquals(0, result.length);

	// verify mock
	EasyMock.verify(matcher);

    }

    /**
     * test resolveGetterMethods method returns empty method array if matcher
     * match any methods on a simple type but with an attribute name which is
     * defined on the object, in this case 'hashcode' is used as attribute name.
     */
    @Test
    public void testResolveMethodCanMatchAttributeOnSimpleType() {
	// declare type
	class SimpleType {
	}

	// setup match mock
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("hashcode");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("getClass");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("wait");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("wait");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("wait");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("equals");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("notify");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("notifyAll");
	EasyMock.expect(matcher.isMatch(EasyMock.isA(Method.class))).andReturn(true);
	EasyMock.expect(matcher.resolveAttributeFromGetterMethod(EasyMock.isA(Method.class))).andReturn("toString");
	EasyMock.replay(matcher);

	// resolve methods
	Method[] result = resolveGetterMethods(new SimpleType(), "hashcode", matcher);

	// test
	assertNotNull(result);
	assertEquals(1, result.length);

	// verify mock
	EasyMock.verify(matcher);
    }

    /**
     * Test methodReturnsArray succeeds if method return an array.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testMethodReturnsArraySucceeds() throws Exception {
	// test type
	class SimpleType {
	    public String[] getXX() {
		return new String[1];
	    }
	}

	Class<?> classObject = SimpleType.class;
	Method method;
	method = classObject.getMethod("getXX", null);

	// test
	assertTrue(methodReturnsArray(method));
    }

    /**
     * Test methodReturnsPrimitiveBoolean fails if method doesn't return an
     * array..
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testMethodReturnsArrayFails() throws NoSuchMethodException, Exception {
	// test type
	class SimpleType {
	    public Boolean getXX() {
		return new Boolean(true);
	    }

	    public int getYY() {
		return 1;
	    }

	    public String getZZ() {
		return "some-string";
	    }
	}

	Class<?> classObject = SimpleType.class;
	Method method = classObject.getMethod("getXX", null);
	Method method2 = classObject.getMethod("getYY", null);
	Method method3 = classObject.getMethod("getZZ", null);

	// test
	assertFalse(methodReturnsArray(method));
	assertFalse(methodReturnsArray(method2));
	assertFalse(methodReturnsArray(method3));
    }

    /**
     * Test isMethodWithNoParameters method fails if method takes parameters.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testIsMethodWithNoParametersFails() throws Exception {
	// test type
	class SimpleType {
	    public Boolean getXX(int p1) {
		return new Boolean(true);
	    }

	    public int getYY(String p1, Object p2) {
		return 1;
	    }
	}

	Class<?> classObject = SimpleType.class;
	Method method = classObject.getMethod("getXX", new Class[] { Integer.TYPE });
	Method method2 = classObject.getMethod("getYY", new Class[] { String.class, Object.class });

	// test
	assertFalse(isMethodWithNoParameters(method));
	assertFalse(isMethodWithNoParameters(method2));
    }

    /**
     * Test isMethodWithNoParameters method succeeds if method takes parameters.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testIsMethodWithNoParametersSucceeds() throws Exception {
	// test type
	class SimpleType {
	    public Boolean getXX() {
		return new Boolean(true);
	    }
	}

	Class<?> classObject = SimpleType.class;
	Method method = classObject.getMethod("getXX", null);

	// test
	assertTrue(isMethodWithNoParameters(method));

    }

    /**
     * Test InvokeMethodWithNoArgs method succeeds on method with no arguments.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testInvokeMethodWithNoArgsSucceeds() throws Exception {
	final Boolean xxValue = new Boolean(true);

	// test type
	class SimpleType {
	    public Boolean getXX() {
		return xxValue;
	    }
	}

	SimpleType instance = new SimpleType();
	Class<?> classObject = instance.getClass();
	Method method = classObject.getMethod("getXX", null);

	// invoke
	Object result = invokeMethodWithNoArgs(instance, method);

	// test
	assertNotNull(result);
	assertEquals(xxValue, result);

    }

    /**
     * Test InvokeMethodWithNoArgs method succeeds on method with arguments.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test(expected = NoSuchMethodException.class)
    public void testInvokeMethodWithNoArgsFailsWithOneArgMethod() throws Exception {
	final Boolean xxValue = new Boolean(true);

	// test type
	class SimpleType {
	    public Boolean getXX(int p) {
		return xxValue;
	    }
	}

	SimpleType instance = new SimpleType();
	Class<?> classObject = instance.getClass();
	Method method = classObject.getMethod("getXX", null);

	// invoke
	Object result = invokeMethodWithNoArgs(instance, method);

	// test
	assertNotNull(result);
	assertEquals(xxValue, result);

    }

}
