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


package com.alpha.pineapple.plugin.weblogic.jmx.model.xmlbeans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicXmlBeanConstants;
import com.alpha.pineapple.plugin.weblogic.jmx.reflection.GetterMethodMatcher;
import com.alpha.pineapple.plugin.weblogic.jmx.reflection.MethodUtils;
import com.oracle.xmlns.weblogic.domain.DomainType;

/**
 * Unit test of the <code>XmlBeansGetterMethodMatcherImpl</code> class.  
 */
public class XmlBeansGetterMethodMatcherTest
{

    
    /**
     * Object under test.
     */
    GetterMethodMatcher matcher;
    
    /**
     * Method utility.
     */
    MethodUtils methodUtils;
    
    
    @Before
    public void setUp() throws Exception
    {
        // create mock utility
        methodUtils = EasyMock.createMock( MethodUtils.class );
        
        matcher = new XmlBeansGetterMethodMatcherImpl();
        
        // inject method utility into matcher
        ReflectionTestUtils.setField( matcher, "methodUtils", methodUtils, MethodUtils.class );                
    }

    @After
    public void tearDown() throws Exception
    {
        matcher = null;
        methodUtils = null;        
    }

    /**
     * Test that the match fails if the the method isn't declared
     * by a class in the XMLBean model package. 
     */
    @Test
    public void testFailsMatchIfMethodIsntDeclaredInModelPackage()
    {          
        try
        {
            // get a method from the string class            
            Method method = String.class.getMethod( "getBytes", null );
            Class<?> expectedClass = method.getDeclaringClass();
            String expectedPackage = WebLogicXmlBeanConstants.MODEL_PACKAGE;
            
            // setup method utility mock
            EasyMock.expect( methodUtils.classIsDeclaredInPackage( expectedClass, expectedPackage )).andReturn( false );            
            EasyMock.replay( methodUtils );
                        
            // test
            assertFalse( matcher.isMatch( method ));
            
            // Verify mock object
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace(  e ));
        }                                
    }

    /**
     * Test that the match fails if the the method is declared
     * by a class in the XMLBean model package but it dosn't
     * start with 'get'. 
     */
    @Test
    public void testFailsMatchIfMethodDoesntStartWithGet()
    {          
        try
        {
            // get a method from the domain class            
            Method method = DomainType.class.getMethod( "addNewCluster", null );
            Class<?> expectedClass = method.getDeclaringClass();
            String expectedPackage = WebLogicXmlBeanConstants.MODEL_PACKAGE;
            
            // setup method utility mock
            EasyMock.expect( methodUtils.classIsDeclaredInPackage( expectedClass, expectedPackage )).andReturn( true );            
            EasyMock.expect( methodUtils.isMethodWithNoParameters( method)).andReturn( true );            
            EasyMock.replay( methodUtils );
                        
            // test
            assertFalse( matcher.isMatch( method ));
            
            // Verify mock object
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace(  e ));
        }                                
    }

    /**
     * Test that the match fails if the the method is declared
     * by a class in the XMLBean model package, but takes parameters.
     */
    @Test
    public void testFailsMatchIfMethodTakesParamenters()
    {          
        try
        {
            // get a method from the domain class            
            Method method = DomainType.class.getMethod( "getAppDeploymentArray", null );
            Class<?> expectedClass = method.getDeclaringClass();
            String expectedPackage = WebLogicXmlBeanConstants.MODEL_PACKAGE;
            
            // setup method utility mock
            EasyMock.expect( methodUtils.classIsDeclaredInPackage( expectedClass, expectedPackage )).andReturn( true );            
            EasyMock.expect( methodUtils.isMethodWithNoParameters( method)).andReturn( false );            
            EasyMock.replay( methodUtils );
                        
            // test
            assertFalse( matcher.isMatch( method ));
            
            // Verify mock object
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace(  e ));
        }                                
    }
    
    
    /**
     * Test that the match succeeds if the the method is declared
     * by a class in the XMLBean model package and starts with 'get'
     * with no arguments..
     */
    @Test
    public void testMatchSucceeds()
    {          
        try
        {
            // get a method from the domain type class            
            Method method = DomainType.class.getMethod( "getDomainVersion", null );
            Class<?> expectedClass = method.getDeclaringClass();
            String expectedPackage = WebLogicXmlBeanConstants.MODEL_PACKAGE;
            
            // setup method utility mock
            EasyMock.expect( methodUtils.classIsDeclaredInPackage( expectedClass, expectedPackage )).andReturn( true );            
            EasyMock.expect( methodUtils.isMethodWithNoParameters( method)).andReturn( true );            
            EasyMock.replay( methodUtils );
                        
            // test
            assertTrue( matcher.isMatch( method ));
            
            // Verify mock object
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace(  e ));
        }                                
    }

    
    
    /**
     * Test attribute name can be resolved from getter method. 
     */
    @Test
    public void testResolveAttributeFromGetterMethod()
    {
        try
        {
            // get a method from the domain type class            
            Method method = DomainType.class.getMethod( "getDomainVersion", null );
                                    
            // resolve attribute
            String attribute = matcher.resolveAttributeFromGetterMethod( method );
            
            // test
            assertEquals( "DomainVersion", attribute );            
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace(  e ));
        }                                
    }

    /**
     * Test attribute name can be resolved from getter method
     * which ends with Array. 
     */
    @Test
    public void testResolveAttributeFromGetterMethodendingWithArray()
    {
        try
        {
            // get a method from the domain type class            
            Method method = DomainType.class.getMethod( "getServerArray", null );
                                    
            // resolve attribute
            String attribute = matcher.resolveAttributeFromGetterMethod( method );
            
            // test
            assertEquals( "Server", attribute );            
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace(  e ));
        }                                
    }
    
}
