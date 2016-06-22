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


package com.alpha.pineapple.plugin.weblogic.jmx.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.management.NotificationBroadcaster;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import weblogic.management.configuration.DomainMBean;

import com.alpha.javautils.StackTraceHelper;

/**
 * Unit test of the <code>MethodUtilsImpl</code> class.
 */
public class MethodUtilsTest
{

    /**
     * Model package for WebLogic MBeans.
     */
    static final String[] MODEL_PACKAGES =
        { "weblogic.management.configuration", "weblogic.management.security", "weblogic.management.security.audit",
            "weblogic.management.security.authentication", "weblogic.management.security.authorization",
            "weblogic.management.security.credential", "weblogic.management.security.pk" };

    /**
     * object under test
     */
    MethodUtils methodUtils;

    /**
     * Method matcher object.
     */
    GetterMethodMatcher matcher;

    @Before
    public void setUp() throws Exception
    {
        // create mock matcher
        matcher = EasyMock.createMock( GetterMethodMatcher.class );

        // create method utils
        methodUtils = new MethodUtilsImpl();
    }

    @After
    public void tearDown() throws Exception
    {
        methodUtils = null;
    }

    /**
     * Test classIsDeclaredInPackage method fails if class isn't declared in package.
     */
    @Test
    public void testClassIsDeclaredInPackageFails()
    {
        Class<?> classObject = String.class;
        String packageName = Method.class.getPackage().getName();

        // test
        assertFalse( methodUtils.classIsDeclaredInPackage( classObject, packageName ) );
    }

    /**
     * Test classIsDeclaredInPackage succeeds if class is declared in package.
     */
    @Test
    public void testClassIsDeclaredInPackageSucceeds()
    {
        Class<?> classObject = String.class;
        String packageName = classObject.getPackage().getName();

        // test
        assertTrue( methodUtils.classIsDeclaredInPackage( classObject, packageName ) );
    }

    /**
     * Test classIsDeclaredInPackages method fails if class isn't declared in any of the supplied packages.
     */
    @Test
    public void testClassIsDeclaredInPackagesFails()
    {
        Class<?> classObject = String.class;
        String[] packageNames = { Method.class.getPackage().getName(), "uggi.package" };

        // test
        assertFalse( methodUtils.classIsDeclaredInPackages( classObject, packageNames ) );
    }

    /**
     * Test classIsDeclaredInPackages succeeds if class is declared in one of the supplied packages.
     */
    @Test
    public void testClassIsDeclaredInPackagesSucceeds()
    {
        Class<?> classObject = String.class;
        String[] packageNames = { "uggi.package", classObject.getPackage().getName(), "buggi.package" };

        // test
        assertTrue( methodUtils.classIsDeclaredInPackages( classObject, packageNames ) );
    }

    /**
     * Test classIsDeclaredInPackages succeeds if class is declared in one of the supplied packages.
     */
    @Test
    public void testClassIsDeclaredInPackagesSucceedsWithWeblogicMBean()
    {
        Class<?> classObject = DomainMBean.class;

        // test
        assertTrue( methodUtils.classIsDeclaredInPackages( classObject, MODEL_PACKAGES ) );
    }

    /**
     * Test classIsDeclaredInPackages succeeds if class is declared in one of the supplied packages.
     */
    @Test
    public void testClassIsDeclaredInPackagesSucceedsWithWeblogicMBeanAsDynamicProxy()
    {
        // create mock invocation handler
        InvocationHandler handler;
        handler = EasyMock.createMock( InvocationHandler.class );

        // define interfaces
        Class[] interfaces = new Class[] { DomainMBean.class };

        // get class loader
        ClassLoader classLoader = DomainMBean.class.getClassLoader();

        // create proxy
        DomainMBean mbeanProxy = (DomainMBean) Proxy.newProxyInstance( classLoader, interfaces, handler );

        // test
        assertTrue( methodUtils.classIsDeclaredInPackages( mbeanProxy.getClass(), MODEL_PACKAGES ) );
    }

    /**
     * Test classIsDeclaredInPackages fails if class isn't declared in one of the supplied packages.
     */
    @Test
    public void testClassIsDeclaredInPackagesFailsWithJavaLangReflectProxy()
    {
        Class<?> classObject = Proxy.class;

        // test
        assertFalse( methodUtils.classIsDeclaredInPackages( classObject, MODEL_PACKAGES ) );
    }

    /**
     * Test classIsDeclaredInPackages fails if class isn't declared in one of the supplied packages.
     */
    @Test
    public void testClassIsDeclaredInPackagesFailsWithNotificationBroadcaster()
    {
        Class<?> classObject = NotificationBroadcaster.class;

        // test
        assertFalse( methodUtils.classIsDeclaredInPackages( classObject, MODEL_PACKAGES ) );
    }

    /**
     * Test methodNameStartsWithGet succeeds if method starts with 'get'.
     */
    @Test
    public void testMethodNameStartsWithGetSucceeds()
    {
        // test type
        class SimpleType
        {
            public int getXX()
            {
                return 1;
            }
        }

        try
        {
            Class<?> classObject = SimpleType.class;
            Method method;
            method = classObject.getMethod( "getXX", null );

            // test
            assertTrue( methodUtils.methodNameStartsWithGet( method ) );

        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test methodNameStartsWithGet fails if method starts with 'Get', ie. Capital 'G'.
     */
    @Test
    public void testMethodNameStartsWithGetFailsIfNameIsntLowercase()
    {
        // test type
        class SimpleType
        {
            public int GetXX()
            {
                return 1;
            }
        }

        try
        {
            Class<?> classObject = SimpleType.class;
            Method method;
            method = classObject.getMethod( "GetXX", null );

            // test
            assertFalse( methodUtils.methodNameStartsWithGet( method ) );

        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test methodNameStartsWithGet succeeds if method starts with 'is'.
     */
    @Test
    public void testMethodNameStartsWithIsSucceeds()
    {
        // test type
        class SimpleType
        {
            public int isXX()
            {
                return 1;
            }
        }

        try
        {
            Class<?> classObject = SimpleType.class;
            Method method;
            method = classObject.getMethod( "isXX", null );

            // test
            assertTrue( methodUtils.methodNameStartsWithIs( method ) );

        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test methodNameStartsWithGet fails if method starts with 'Get', ie. Capital 'I'.
     */
    @Test
    public void testMethodNameStartsWithIsFailsIfNameIsntLowercase()
    {
        // test type
        class SimpleType
        {
            public int IsXX()
            {
                return 1;
            }
        }

        try
        {
            Class<?> classObject = SimpleType.class;
            Method method;
            method = classObject.getMethod( "IsXX", null );

            // test
            assertFalse( methodUtils.methodNameStartsWithIs( method ) );

        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test methodReturnsPrimitiveBoolean succeeds if method return a primitive boolean.
     */
    @Test
    public void testMethodReturnsPrimitiveBooleanSucceeds()
    {
        // test type
        class SimpleType
        {
            public boolean isXX()
            {
                return true;
            }
        }

        try
        {
            Class<?> classObject = SimpleType.class;
            Method method;
            method = classObject.getMethod( "isXX", null );

            // test
            assertTrue( methodUtils.methodReturnsPrimitiveBoolean( method ) );

        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test methodReturnsPrimitiveBoolean fails if method doesn't return a primitive boolean.
     */
    @Test
    public void testMethodReturnsPrimitiveBooleanFails()
    {
        // test type
        class SimpleType
        {
            public Boolean isXX()
            {
                return new Boolean( true );
            }

            public int isYY()
            {
                return 1;
            }

            public String isZZ()
            {
                return "some-string";
            }
        }

        try
        {
            Class<?> classObject = SimpleType.class;
            Method method = classObject.getMethod( "isXX", null );
            Method method2 = classObject.getMethod( "isYY", null );
            Method method3 = classObject.getMethod( "isZZ", null );

            // test
            assertFalse( methodUtils.methodReturnsPrimitiveBoolean( method ) );
            assertFalse( methodUtils.methodReturnsPrimitiveBoolean( method2 ) );
            assertFalse( methodUtils.methodReturnsPrimitiveBoolean( method3 ) );

        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * test resolveGetterMethods method rejects undefined target object.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testResolveMethodRejectsundefinedTargetObject()
    {
        // invoke to provoke exception
        methodUtils.resolveGetterMethods( null, "some-attribute-name", matcher );
    }

    /**
     * test resolveGetterMethods method rejects undefined attribute name.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testResolveMethodRejectsundefinedAttributeName()
    {
        // invoke to provoke exception
        methodUtils.resolveGetterMethods( "a-target-object", null, matcher );
    }

    /**
     * test resolveGetterMethods method rejects undefined matcher.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testResolveMethodRejectsundefinedMatcher()
    {
        // invoke to provoke exception
        methodUtils.resolveGetterMethods( "a-target-object", "some-attribute-name", null );
    }

    /**
     * test resolveGetterMethods method returns empty method array if attribute name is empty string.
     */
    @Test
    public void testResolveMethodReturnsEmptyArrayIfAttributeNameIsEmpty()
    {
        // resolve methods
        Method[] result = methodUtils.resolveGetterMethods( "a-target-object", "", matcher );

        // test
        assertNotNull( result );
        assertEquals( 0, result.length );
    }

    /**
     * test resolveGetterMethods method returns empty method array if matcher doesn't match any methods.
     */
    @Test
    public void testResolveMethodReturnsEmptyArrayIfMatcherDoesntMatchAnyMethods()
    {
        // setup match mock
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( false ).anyTimes();
        EasyMock.replay( matcher );

        // resolve methods
        Method[] result = methodUtils.resolveGetterMethods( "a-target-object", "some-attribute-name", matcher );

        // test
        assertNotNull( result );
        assertEquals( 0, result.length );

        // verify mock
        EasyMock.verify( matcher );
    }

    /**
     * test resolveGetterMethods method returns empty method array if matcher match any methods on a simple type but
     * with an attribute name which isn't defined on the object.
     */
    @Test
    public void testResolveMethodReturnsEmptyArrayIfMatcherMatchAllOnSimpleType()
    {
        // declare type
        class SimpleType
        {
        }

        // setup match mock
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn(
                                                                                                               "hashcode" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn(
                                                                                                               "getClass" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn( "wait" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn( "wait" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn( "wait" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn( "equals" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn( "notify" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn(
                                                                                                               "notifyAll" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn(
                                                                                                               "toString" );
        EasyMock.replay( matcher );

        // resolve methods
        Method[] result = methodUtils.resolveGetterMethods( new SimpleType(), "some-attribute-name", matcher );

        // test
        assertNotNull( result );
        assertEquals( 0, result.length );

        // verify mock
        EasyMock.verify( matcher );

    }

    /**
     * test resolveGetterMethods method returns empty method array if matcher match any methods on a simple type but
     * with an attribute name which is defined on the object, in this case 'hashcode' is used as attribute name.
     */
    @Test
    public void testResolveMethodCanMatchAttributeOnSimpleType()
    {
        // declare type
        class SimpleType
        {
        }

        // setup match mock
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn(
                                                                                                               "hashcode" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn(
                                                                                                               "getClass" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn( "wait" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn( "wait" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn( "wait" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn( "equals" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn( "notify" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn(
                                                                                                               "notifyAll" );
        EasyMock.expect( matcher.isMatch( EasyMock.isA( Method.class ) ) ).andReturn( true );
        EasyMock.expect( matcher.resolveAttributeFromGetterMethod( EasyMock.isA( Method.class ) ) ).andReturn(
                                                                                                               "toString" );
        EasyMock.replay( matcher );

        // resolve methods
        Method[] result = methodUtils.resolveGetterMethods( new SimpleType(), "hashcode", matcher );

        // test
        assertNotNull( result );
        assertEquals( 1, result.length );

        // verify mock
        EasyMock.verify( matcher );
    }

    /**
     * Test methodReturnsArray succeeds if method return an array.
     */
    @Test
    public void testMethodReturnsArraySucceeds()
    {
        // test type
        class SimpleType
        {
            public String[] getXX()
            {
                return new String[1];
            }
        }

        try
        {
            Class<?> classObject = SimpleType.class;
            Method method;
            method = classObject.getMethod( "getXX", null );

            // test
            assertTrue( methodUtils.methodReturnsArray( method ) );

        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test methodReturnsPrimitiveBoolean fails if method doesn't return an array..
     */
    @Test
    public void testMethodReturnsArrayFails()
    {
        // test type
        class SimpleType
        {
            public Boolean getXX()
            {
                return new Boolean( true );
            }

            public int getYY()
            {
                return 1;
            }

            public String getZZ()
            {
                return "some-string";
            }
        }

        try
        {
            Class<?> classObject = SimpleType.class;
            Method method = classObject.getMethod( "getXX", null );
            Method method2 = classObject.getMethod( "getYY", null );
            Method method3 = classObject.getMethod( "getZZ", null );

            // test
            assertFalse( methodUtils.methodReturnsArray( method ) );
            assertFalse( methodUtils.methodReturnsArray( method2 ) );
            assertFalse( methodUtils.methodReturnsArray( method3 ) );

        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test isMethodWithNoParameters method fails if method takes parameters.
     */
    @Test
    public void testIsMethodWithNoParametersFails()
    {
        // test type
        class SimpleType
        {
            public Boolean getXX( int p1 )
            {
                return new Boolean( true );
            }

            public int getYY( String p1, Object p2 )
            {
                return 1;
            }

        }

        try
        {

            Class<?> classObject = SimpleType.class;
            Method method = classObject.getMethod( "getXX", new Class[] { Integer.TYPE } );
            Method method2 = classObject.getMethod( "getYY", new Class[] { String.class, Object.class } );

            // test
            assertFalse( methodUtils.isMethodWithNoParameters( method ) );
            assertFalse( methodUtils.isMethodWithNoParameters( method2 ) );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }

    /**
     * Test isMethodWithNoParameters method succeeds if method takes parameters.
     */
    @Test
    public void testIsMethodWithNoParametersSucceeds()
    {
        // test type
        class SimpleType
        {
            public Boolean getXX()
            {
                return new Boolean( true );
            }
        }

        try
        {

            Class<?> classObject = SimpleType.class;
            Method method = classObject.getMethod( "getXX", null );

            // test
            assertTrue( methodUtils.isMethodWithNoParameters( method ) );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }

    /**
     * Test InvokeMethodWithNoArgs method succeeds on method with no arguments.
     */
    @Test
    public void testInvokeMethodWithNoArgsSucceeds()
    {
        final Boolean xxValue = new Boolean( true );

        // test type
        class SimpleType
        {
            public Boolean getXX()
            {
                return xxValue;
            }
        }

        try
        {
            SimpleType instance = new SimpleType();
            Class<?> classObject = instance.getClass();
            Method method = classObject.getMethod( "getXX", null );

            // invoke
            Object result = methodUtils.invokeMethodWithNoArgs( instance, method );

            // test
            assertNotNull( result );
            assertEquals( xxValue, result );

        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }

    /**
     * Test InvokeMethodWithNoArgs method succeeds on method with arguments.
     * @throws Exception  
     */
    @Test(expected = NoSuchMethodException.class )
    public void testInvokeMethodWithNoArgsFailsWithOneArgMethod() throws Exception
    {
        final Boolean xxValue = new Boolean( true );

        // test type
        class SimpleType
        {
            public Boolean getXX(int p)
            {
                return xxValue;
            }
        }

            SimpleType instance = new SimpleType();
            Class<?> classObject = instance.getClass();
            Method method = classObject.getMethod( "getXX", null );

            // invoke
            Object result = methodUtils.invokeMethodWithNoArgs( instance, method );

            // test
            assertNotNull( result );
            assertEquals( xxValue, result );

    }    
    
}
