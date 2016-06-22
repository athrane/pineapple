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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.reflection.GetterMethodMatcher;
import com.alpha.pineapple.plugin.weblogic.jmx.reflection.MethodUtils;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;
import com.oracle.xmlns.weblogic.domain.DomainType;
import com.oracle.xmlns.weblogic.domain.ServerType;

/**
 * Unit test of the <code>XmlBeansModelAccessorImpl</code> class.
 */
public class XmlBeansModelAccessorTest
{

	/**
	 * ID for primary participant.
	 */
	static final String PRIMARY_ID = "primary-id";

	/**
	 * ID for secondary participant.
	 */	
	static final String SECONDARY_ID = "secondary-id";
	
	/**
	 * A null value used in tests.
	 */
    static final Object NULL_VALUE = null;
	
    /**
     * Enum class used for unit test.
     */
    @SuppressWarnings( "serial" )
    static final class XMLBeansTestEnum extends StringEnumAbstractBase
    {

        //static final Enum BY_SIZE = Enum.forString( "bySize" );

        //static final Enum BY_TIME = Enum.forString( "byTime" );

        //static final Enum NONE = Enum.forString( "none" );

        protected XMLBeansTestEnum( String s, int i )
        {
            super( s, i );
        }

    }

    /**
     * object under test
     */
    XmlBeansModelAccessor accessor;

    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;    
    
    /**
     * Mock method utility.
     */
    MethodUtils methodUtils;

    /**
     * Mock getter method matcher .
     */
    GetterMethodMatcher matcher;

    /**
     * Mock schema type for attributes.
     */
    SchemaType schemaType;     

    /**
     * Mock schema property for attributes.
     */
    SchemaProperty schemaProperty;     
    
    @Before
    public void setUp() throws Exception
    {
        // create mock utility
        methodUtils = EasyMock.createMock( MethodUtils.class );

        // create mock matcher
        matcher = EasyMock.createMock( GetterMethodMatcher.class );

        // create mock schema type
        schemaType = EasyMock.createMock( SchemaType.class );

        // create mock schema property
        schemaProperty = EasyMock.createMock( SchemaProperty.class );
        
        // create model accessor
        accessor = new XmlBeansModelAccessorImpl();

        // inject method utility into accessor
        ReflectionTestUtils.setField( accessor, "methodUtils", methodUtils, MethodUtils.class );

        // inject method matcher into accessor
        ReflectionTestUtils.setField( accessor, "matcher", matcher, GetterMethodMatcher.class );

        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
        
        // inject message source
        ReflectionTestUtils.setField( accessor, "messageProvider", messageProvider , MessageProvider.class );
        
        // complete mock source initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl(); 
        
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject()));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        EasyMock.replay(messageProvider);                                
    }

    @After
    public void tearDown() throws Exception
    {
        accessor = null;
        methodUtils = null;
        matcher = null;
        schemaType = null;
        schemaProperty = null;
    }

    /**
     * Test that accessor object can be created.
     */
    @Test
    public void testCanCreateInstance()
    {
        try
        {
            // test
            assertNotNull( accessor );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test that method fails if parent participant type 
     * isn't <code>SchemaProperty</code>
     */
    @Test(expected = ModelResolutionFailedException.class)
    public void testIsCollectionMethodFailsIfParentParticipantTypeIsntSchemaProperty() throws Exception
    {
        // complete mock object setup
        EasyMock.replay( schemaProperty);        
        EasyMock.replay( schemaType );
    	
        // setup
    	Object type = Integer.class;
		Integer value = new Integer( 0 );
		ResolvedParticipant parent  = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, type, value);
		ResolvedParticipant child = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, value);    	

        // invoke to provoke exception
        accessor.isCollection( parent, child );
    }
    
    /**
     * Test that method fails if child participant type 
     * isn't <code>SchemaProperty</code>
     */
    @Test(expected = ModelResolutionFailedException.class)
    public void testIsCollectionMethodFailsIfChildParticipantTypeIsntSchemaProperty() throws Exception
    {
        // complete mock object setup
        EasyMock.replay( schemaProperty);        
        EasyMock.replay( schemaType );
    	
        // setup
    	Object type = Integer.class;
		Integer value = new Integer( 0 );
		ResolvedParticipant parent  = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, value);
		ResolvedParticipant child = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, type, value);    	

        // invoke to provoke exception
        accessor.isCollection( parent, child );
    }    
    
 

    /**
     * test that method returns false if child type is a simple type.
     */
    @Test
    public void testIsCollectionMethodReturnFalseIfChildTypeIsSimpleType() throws Exception
    {
        // complete mock object setup   	
        EasyMock.replay( schemaProperty );        
        EasyMock.replay( schemaType );

        // create mock schema types
        SchemaType schemaType2 = EasyMock.createMock( SchemaType.class );
        SchemaProperty schemaProperty2 = EasyMock.createMock( SchemaProperty.class );
        EasyMock.expect( schemaProperty2.getType() ).andReturn( schemaType2 );
        EasyMock.expect( schemaType2.isSimpleType() ).andReturn( true );
        EasyMock.replay( schemaProperty2 );        
        EasyMock.replay( schemaType2 );
                 
        // setup
        ResolvedParticipant parent = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );
        ResolvedParticipant child = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty2, NULL_VALUE );

        // test
        assertFalse( accessor.isCollection( parent, child ));
        
        // verify mock object
        EasyMock.verify( schemaType );        
        EasyMock.verify( schemaProperty );        
        EasyMock.verify( schemaType2 );        
        EasyMock.verify( schemaProperty2 );                
    }    
    
    /**
     * test that method returns false if child type doesn't extend Java array.
     */
    @Test
    public void testIsCollectionMethodReturnFalseIfChildTypeDoesntExtendJavaArray() throws Exception
    {
        // complete mock object setup   	
        EasyMock.replay( schemaProperty );        
        EasyMock.replay( schemaType );

        // create mock schema types
        SchemaType schemaType2 = EasyMock.createMock( SchemaType.class );
        SchemaProperty schemaProperty2 = EasyMock.createMock( SchemaProperty.class );
        EasyMock.expect( schemaProperty2.getType() ).andReturn( schemaType2 );
        EasyMock.expect( schemaProperty2.extendsJavaArray() ).andReturn( false );
        EasyMock.expect( schemaType2.isSimpleType() ).andReturn( false );
        EasyMock.replay( schemaProperty2 );        
        EasyMock.replay( schemaType2 );
                 
        // setup
        ResolvedParticipant parent = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );
        ResolvedParticipant child = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty2, NULL_VALUE );

        // test
        assertFalse( accessor.isCollection( parent, child ));
        
        // verify mock object
        EasyMock.verify( schemaType );        
        EasyMock.verify( schemaProperty );        
        EasyMock.verify( schemaType2 );        
        EasyMock.verify( schemaProperty2 );                
    }        
    
    /**
     * test that method returns false if parent and child type isn't a simple type
     * and they are equal.
     */
    @Test
    public void testIsCollectionMethodReturnFalseIfTypesAreEqual() throws Exception
    {
        // complete mock object setup
        EasyMock.expect( schemaProperty.getType() ).andReturn( schemaType );
        EasyMock.expect( schemaProperty.extendsJavaArray() ).andReturn( false );        
        EasyMock.expect( schemaType.isSimpleType() ).andReturn( false);
        EasyMock.replay( schemaProperty );        
        EasyMock.replay( schemaType );
                 
        // setup
        ResolvedParticipant parent = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );
        ResolvedParticipant child = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );

        // test
        assertFalse( accessor.isCollection( parent, child ));
        
        // verify mock object
        EasyMock.verify( schemaType );        
        EasyMock.verify( schemaProperty );        
    }        

    /**
     * test that method returns false if parent and child type isn't a simple type
     * and they aren't equal.
     */
    @Test
    public void testIsCollectionMethodReturnTrueIfTypesAreDifferent() throws Exception
    {
        // complete mock object setup            
        EasyMock.replay( schemaProperty );        
        EasyMock.replay( schemaType );

        // create mock schema types
        SchemaType schemaType2 = EasyMock.createMock( SchemaType.class );
        SchemaProperty schemaProperty2 = EasyMock.createMock( SchemaProperty.class );
        EasyMock.expect( schemaProperty2.getType() ).andReturn( schemaType2 );
        EasyMock.expect( schemaProperty2.extendsJavaArray() ).andReturn( true);
        EasyMock.expect( schemaType2.isSimpleType() ).andReturn( false );
        EasyMock.replay( schemaProperty2 );        
        EasyMock.replay( schemaType2 );
                 
        // setup
        ResolvedParticipant parent = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );
        ResolvedParticipant child = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty2, NULL_VALUE );

        // test
        assertTrue( accessor.isCollection( parent, child ));
        
        // verify mock object
        EasyMock.verify( schemaType );        
        EasyMock.verify( schemaProperty );        
        EasyMock.verify( schemaType2 );        
        EasyMock.verify( schemaProperty2 );                
    }           
    
  
       

    /**
     * Test that method fails if primary participant type 
     * isn't <code>SchemaProperty</code>
     */
    @Test(expected = ModelResolutionFailedException.class)
    public void testIsEnumMethodFailsIfPrimaryParticipantTypeIsntSchemaProperty() throws Exception
    {
        // setup    	
    	Object type = Integer.class;
		Integer value = new Integer( 0 );
		ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, type, value);    	

        // invoke to provoke exception
        accessor.isEnum( participant );
    	
    }
    
    
    /**
     * test that method returns true if type contains enumeration values.
     */
    @Test
    public void testIsEnumMethodReturnTrueIfTypeReturnEnumValues() throws Exception
    {
        XmlAnySimpleType[] enumValues = new XmlAnySimpleType[0];        
        // complete mock object setup
        EasyMock.expect( schemaProperty.getType() ).andReturn( schemaType );
        EasyMock.replay( schemaProperty);
        EasyMock.expect( schemaType.isSimpleType() ).andReturn( true );        
        EasyMock.expect( schemaType.getEnumerationValues() ).andReturn( enumValues );        
        EasyMock.replay( schemaType );
                
        // setup
		ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );    	

        // test
        assertTrue( accessor.isEnum( participant ) );
        
        // verify mock object
        EasyMock.verify( schemaType );        
        EasyMock.verify( schemaProperty );                
    }
    
    /**
     * test that method returns false on object.
     */
    @Test
    public void testIsEnumMethodReturnFalseIfTypeDoesntReturnEnumValues() throws Exception
    {        
        // complete mock object setup
        EasyMock.expect( schemaProperty.getType() ).andReturn( schemaType );
        EasyMock.replay( schemaProperty);    
        EasyMock.expect( schemaType.isSimpleType() ).andReturn( true );        
        EasyMock.expect( schemaType.getEnumerationValues() ).andReturn( null );        
        EasyMock.replay( schemaType );
                
        
        // setup
		ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );    	

        // test
        assertFalse( accessor.isEnum( participant ) );
        
        // verify mock object
        EasyMock.verify( schemaType );        
        EasyMock.verify( schemaProperty );                
    }

    /**
     * test that method returns false if schema type isn't a simple type
     */
    @Test
    public void testIsEnumMethodReturnFalseIfTypeIsntSimpleType() throws Exception
    {        
        // complete mock object setup
        EasyMock.expect( schemaProperty.getType() ).andReturn( schemaType );
        EasyMock.replay( schemaProperty);    
        EasyMock.expect( schemaType.isSimpleType() ).andReturn( false );                
        EasyMock.replay( schemaType );
                
        
        // setup
		ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );    	

        // test
        assertFalse( accessor.isEnum( participant ) );
        
        // verify mock object
        EasyMock.verify( schemaType );        
        EasyMock.verify( schemaProperty );                
    }
    
    /**
     * Test that method fails if primary participant type 
     * isn't <code>SchemaType</code>
     */
    @Test(expected = ModelResolutionFailedException.class)
    public void testIsPrimitiveMethodFailsIfPrimaryParticipantTypeIsntSchemaType() throws Exception
    {
        // setup    	
    	Object type = Integer.class;
		Integer value = new Integer( 0 );
		ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, type, value);    	

        // invoke to provoke exception
        accessor.isPrimitive( participant );
    }
        
    /**
     * Test that method returns true if schema type is simple type.
     */
    @Test
    public void testIsPrimitiveMethodReturnTrueIfSchemaTypeIsSimpleType() throws Exception
    {
        // complete mock object setup
        EasyMock.expect( schemaProperty.getType() ).andReturn( schemaType );
        EasyMock.replay( schemaProperty);        
        EasyMock.expect( schemaType.isSimpleType() ).andReturn( true );
        EasyMock.expect( schemaType.getEnumerationValues() ).andReturn( null );        
        EasyMock.replay( schemaType );
                
        // setup
		ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );    	

        // test
        assertTrue( accessor.isPrimitive( participant ) );
        
        // verify mock object
        EasyMock.verify( schemaType );        
        EasyMock.verify( schemaProperty );        
    }
    
    /**
     * Test that method returns false if schema type isn't simple type.
     */
    @Test
    public void testIsPrimitiveMethodReturnFalseIfSchemaTypeIsntSimpleType() throws Exception
    {
        // complete mock object setup
        EasyMock.expect( schemaProperty.getType() ).andReturn( schemaType );
        EasyMock.replay( schemaProperty);        
        EasyMock.expect( schemaType.isSimpleType() ).andReturn( false);        
        EasyMock.replay( schemaType );
                
        // setup    	
		ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );    	
        
        // test
        assertFalse( accessor.isPrimitive( participant ) );
        
        // verify mock object
        EasyMock.verify( schemaType );        
        EasyMock.verify( schemaProperty );        
    }
    
    /**
     * Test that method returns false if schema type isn't simple type.
     */
    @Test
    public void testIsPrimitiveMethodReturnFalseIfSchemaTypeReturnsEnumerationValues() throws Exception
    {
        // complete mock object setup
        XmlAnySimpleType[] enumValues = new XmlAnySimpleType[0];        
        EasyMock.expect( schemaProperty.getType() ).andReturn( schemaType );
        EasyMock.replay( schemaProperty);        
        EasyMock.expect( schemaType.isSimpleType() ).andReturn( true ); 
        EasyMock.expect( schemaType.getEnumerationValues() ).andReturn( enumValues );                
        EasyMock.replay( schemaType );
                
        // setup
		ResolvedParticipant participant = ResolvedParticipantImpl.createSuccessfulResult( PRIMARY_ID, schemaProperty, NULL_VALUE );    	

        // test
        assertFalse( accessor.isPrimitive( participant ) );
        
        // verify mock object
        EasyMock.verify( schemaType );        
        EasyMock.verify( schemaProperty );        
    }
    
    /**
     * Test that single getter method can be resolved.
     */
    @Test
    public void testCanResolveSingleGetterMethod()
    {
        try
        {
            // complete mock object setup
            Object modelObject = null;
            String attributeName = null;
            Method actualMethod = String.class.getMethod( "getBytes", null );
            Method[] getterMethods = new Method[] { actualMethod };

            // complete mock object setup
            EasyMock.expect( methodUtils.resolveGetterMethods( modelObject, attributeName, matcher ) ).andReturn( getterMethods );
            EasyMock.replay( methodUtils );

            // test
            Method getterMethod = accessor.resolveGetterMethod( attributeName, modelObject );
            assertEquals( getterMethod, actualMethod );

            // Verify mock object
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test that two getter methods can be resolved,
     * which is that the method without any parameters is returned. 
     */
    @Test
    public void testCanResolveTwoGetterMethods()
    {
        try
        {            
            // complete mock object setup
            Object modelObject = null;
            String attributeName = null;                       
            Method actualMethod = String.class.getMethod( "getBytes", null ); // add method with no parameters
            Method actualMethod2 = String.class.getMethod( "getBytes", String.class); // add method with one parameters            
            Method[] getterMethods = new Method[] { actualMethod, actualMethod2 };

            // complete mock object setup
            EasyMock.expect( methodUtils.resolveGetterMethods( modelObject, attributeName, matcher ) ).andReturn(getterMethods );
            EasyMock.replay( methodUtils );

            // test
            Method getterMethod = accessor.resolveGetterMethod( attributeName, modelObject );
            assertEquals( getterMethod, actualMethod );

            // verify mock object
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    
    /**
     * Test that accessor can get model object. 
     */
    @Test
    public void testGetModelObject()
    {
        final String xxValue = "xx-value";
        
        // declare test type 
        class SomeType {
            
            public String getXX() { return xxValue; }
        }
        
        try
        {
            // setup objects 
            Object targetObject = new SomeType(); 
            Method getXXMethod = SomeType.class.getMethod( "getXX", null );                        
            
            // setup method utility mock
            EasyMock.expect( methodUtils.isMethodWithNoParameters( getXXMethod )).andReturn( true );            
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, getXXMethod )).andReturn( xxValue );            
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            Object result = accessor.getModelObject( targetObject, getXXMethod );
                        
            // test
            assertNotNull( result );
            assertEquals( xxValue, result );
            
            // verify mocks
            EasyMock.verify( matcher );
            EasyMock.verify( methodUtils );
            
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }               
    
    /**
     * Test that <code>GetModelObject</code> fails if 
     * getter method has arguments
     * . 
     * @throws Exception  
     */
    @Test(expected = ModelResolutionFailedException .class )
    public void testGetModelObjectFailsIfGetterHasArguments() throws SecurityException, Exception
    {
        final String xxValue = "xx-value";
        
        // declare test type 
        class SomeType {
            
            public String getXX(int p) { return xxValue; }
        }
        
            // setup objects 
            Object targetObject = new SomeType(); 
            Method getXXMethod = SomeType.class.getMethod( "getXX", new Class[] {Integer.TYPE} );                        
            
            // setup method utility mock
            EasyMock.expect( methodUtils.isMethodWithNoParameters( getXXMethod )).andReturn( false );            
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, getXXMethod )).andReturn( xxValue );            
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            Object result = accessor.getModelObject( targetObject, getXXMethod );
                        
            // verify mocks - never invoked due to exception
            //EasyMock.verify( matcher );
            //EasyMock.verify( methodUtils );
            
        
    }          

    /**
     * Test that <code>GetModelObject</code> fails if 
     * reflection layer throws exception
     * . 
     * @throws Exception  
     */
    @Test(expected = ModelResolutionFailedException .class )
    public void testGetModelObjectFailsIfMethodUtilsThrowsException() throws SecurityException, Exception
    {
        final String xxValue = "xx-value";
        
        // declare test type 
        class SomeType {
            
            public String getXX(int p) { return xxValue; }
        }
        
            // setup objects 
            Object targetObject = new SomeType(); 
            Method getXXMethod = SomeType.class.getMethod( "getXX", new Class[] {Integer.TYPE} );                        
            
            // setup method utility mock
            EasyMock.expect( methodUtils.isMethodWithNoParameters( getXXMethod )).andReturn( false );            
            Throwable exception = new IllegalArgumentException();
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, getXXMethod )).andThrow( exception );            
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            Object result = accessor.getModelObject( targetObject, getXXMethod );
                        
            // verify mocks - never invoked due to exception
            //EasyMock.verify( matcher );
            //EasyMock.verify( methodUtils );                    
    }          
    
    /**
     * Test that <code>GetModelObject</code> fails if 
     * reflection layer throws exception
     * . 
     * @throws Exception  
     */
    @Test(expected = ModelResolutionFailedException .class )
    public void testGetModelObjectFailsIfMethodUtilsThrowsException2() throws SecurityException, Exception
    {
        final String xxValue = "xx-value";
        
        // declare test type 
        class SomeType {
            
            public String getXX(int p) { return xxValue; }
        }
        
            // setup objects 
            Object targetObject = new SomeType(); 
            Method getXXMethod = SomeType.class.getMethod( "getXX", new Class[] {Integer.TYPE} );                        
            
            // setup method utility mock
            EasyMock.expect( methodUtils.isMethodWithNoParameters( getXXMethod )).andReturn( false );            
            Throwable exception = new IllegalAccessException();
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, getXXMethod )).andThrow( exception );            
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            Object result = accessor.getModelObject( targetObject, getXXMethod );
                        
            // verify mocks - never invoked due to exception
            //EasyMock.verify( matcher );
            //EasyMock.verify( methodUtils );                    
    }
    
    /**
     * Test that <code>GetModelObject</code> fails if 
     * reflection layer throws exception
     * . 
     * @throws Exception  
     */
    @Test(expected = ModelResolutionFailedException .class )
    public void testGetModelObjectFailsIfMethodUtilsThrowsException3() throws SecurityException, Exception
    {
        final String xxValue = "xx-value";
        
        // declare test type 
        class SomeType {
            
            public String getXX(int p) { return xxValue; }
        }
        
            // setup objects 
            Object targetObject = new SomeType(); 
            Method getXXMethod = SomeType.class.getMethod( "getXX", new Class[] {Integer.TYPE} );                        
            
            // setup method utility mock
            EasyMock.expect( methodUtils.isMethodWithNoParameters( getXXMethod )).andReturn( false );            
            Throwable exception = new InvocationTargetException( new Throwable());
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, getXXMethod )).andThrow( exception );            
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            Object result = accessor.getModelObject( targetObject, getXXMethod );
                        
            // verify mocks - never invoked due to exception
            //EasyMock.verify( matcher );
            //EasyMock.verify( methodUtils );                    
    }          
    
    /**
     * Test that accessor can determine if attribute value is set. 
     */
    @Test
    public void testIsValueSetReturnTrueOnSetBooleanAttribute()
    {
        try
        {
            // setup objects 
            DomainType targetObject = DomainType.Factory.newInstance();            
            targetObject.setAdministrationPortEnabled( true );
            Method isSetMethod = targetObject.getClass().getMethod( "isSetAdministrationPortEnabled", null );
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("AdministrationPortEnabled", targetObject.schemaType()); 
            
            // setup method utility mock
            EasyMock.expect( methodUtils.getMethod( targetObject, "isSetAdministrationPortEnabled")).andReturn( isSetMethod );
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, isSetMethod )).andReturn( true );            
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            boolean result = accessor.isValueSet( attributeType, targetObject);
                        
            // test
            assertTrue( result );
            
            // verify mocks
            EasyMock.verify( matcher );
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }
    
    /**
     * Test that accessor can determine if attribute value is set. 
     */
    @Test
    public void testIsValueSetReturnTrueOnSetIntAttribute()
    {
        try
        {
            // setup objects 
            DomainType targetObject = DomainType.Factory.newInstance();            
            targetObject.setAdministrationPort( 999 );
            Method isSetMethod = targetObject.getClass().getMethod( "isSetAdministrationPort", null );
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("AdministrationPort", targetObject.schemaType());
            
            // setup method utility mock
            EasyMock.expect( methodUtils.getMethod( targetObject, "isSetAdministrationPort")).andReturn( isSetMethod );
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, isSetMethod )).andReturn( true );            
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            boolean result = accessor.isValueSet( attributeType, targetObject);
                        
            // test
            assertTrue( result );
            
            // verify mocks
            EasyMock.verify( matcher );
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }    
    
    /**
     * Test that accessor can determine if array attribute value is set. 
     */
    @Test
    public void testIsValueSetReturnTrueOnSetArrayAttribute()
    {
        try
        {
            // setup objects 
            DomainType targetObject = DomainType.Factory.newInstance();
            ServerType[] serverArray = new ServerType[] { ServerType.Factory.newInstance() };
            targetObject.setServerArray( serverArray );            
            Method isSetMethod = targetObject.getClass().getMethod( "sizeOfServerArray", null );
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("Server", targetObject.schemaType());                        
            
            // setup method utility mock
            EasyMock.expect( methodUtils.getMethod( targetObject, "sizeOfServerArray")).andReturn( isSetMethod );
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, isSetMethod )).andReturn( 1 );            
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            boolean result = accessor.isValueSet( attributeType , targetObject);
                        
            // test
            assertTrue( result );
            
            // verify mocks
            EasyMock.verify( matcher );
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }
    

    /**
     * Test that accessor can determine if attribute value isn't set. 
     */
    @Test
    public void testIsValueSetReturnFalseOnUnsetBooleanAttribute()
    {
        try
        {
            // setup objects 
            DomainType targetObject = DomainType.Factory.newInstance();    
            Method isSetMethod = targetObject.getClass().getMethod( "isSetAdministrationPortEnabled", null );
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("AdministrationPortEnabled", targetObject.schemaType() );
                        
            
            // setup method utility mock
            EasyMock.expect( methodUtils.getMethod( targetObject, "isSetAdministrationPortEnabled")).andReturn( isSetMethod );
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, isSetMethod )).andReturn( false );            
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            boolean result = accessor.isValueSet( attributeType, targetObject);
                        
            // test
            assertFalse ( result );
            
            // verify mocks
            EasyMock.verify( matcher );
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }
    
    /**
     * Test that accessor can determine if array attribute value isn't set. 
     */
    @Test
    public void testIsValueSetReturnFalseOnUnsetArrayAttribute()
    {
        try
        {
            // setup objects 
            DomainType targetObject = DomainType.Factory.newInstance();
            ServerType[] serverArray = new ServerType[0];
            targetObject.setServerArray( serverArray );            
            Method isSetMethod = targetObject.getClass().getMethod( "sizeOfServerArray", null );
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("Server", targetObject.schemaType() );            
            
            // setup method utility mock
            EasyMock.expect( methodUtils.getMethod( targetObject, "sizeOfServerArray")).andReturn( isSetMethod );
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, isSetMethod )).andReturn( 0 );            
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            boolean result = accessor.isValueSet( attributeType , targetObject);
                        
            // test
            assertFalse( result );
            
            // verify mocks
            EasyMock.verify( matcher );
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }                  
    
    /**
     * Test that correct is set method name is created.
     */
    @Test
    public void testCreateIsSetMethodNameIsCorrectForNonArray()
    {
        try
        {
            // setup objects        	
            DomainType targetObject = DomainType.Factory.newInstance();        	
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("AdministrationPortEnabled", targetObject.schemaType() );
            
            // type cast to get access to methods with default scope  
            XmlBeansModelAccessorImpl accessorImpl = (XmlBeansModelAccessorImpl) accessor;
            
            // create name
            String result = accessorImpl.createIsSetMethodName( attributeType );
            
            // test
            assertEquals( "isSetAdministrationPortEnabled", result );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }    

    /**
     * Test that correct is set method name is created.
     */
    @Test
    public void testCreateIsSetMethodNameIsCorrectForArray()
    {
        try
        {
            DomainType targetObject = DomainType.Factory.newInstance();        	
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("Server", targetObject.schemaType());
        	
            // type cast to get access to methods with default scope  
            XmlBeansModelAccessorImpl accessorImpl = (XmlBeansModelAccessorImpl) accessor;
            
            // create name
            String result = accessorImpl.createIsSetMethodName( attributeType );
            
            // test
            assertEquals( "sizeOfServerArray", result );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }    
        
 
    /**
     * Test that accessor can determine if attribute has default value defined. 
     */
    @Test
    public void testIsDefaultValueDefinedReturnTrueOnSetBooleanAttribute()
    {
        try
        {
            // setup objects 
            DomainType targetObject = DomainType.Factory.newInstance();            
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("AdministrationPortEnabled", targetObject.schemaType() );            
            
            // setup method utility mock
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            boolean result = accessor.isDefaultValueDefined( attributeType, targetObject);
                        
            // test
            assertTrue( result );
            
            // verify mocks
            EasyMock.verify( matcher );
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }    
    
    /**
     * Test that accessor can determine if attribute has default value defined. 
     */
    @Test
    public void testIsDefaultValueDefinedReturnFalseOnArrayAttribute()
    {
        try
        {
            // setup objects 
            DomainType targetObject = DomainType.Factory.newInstance();            
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("Server", targetObject.schemaType() );            
            
            // setup method utility mock
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            boolean result = accessor.isDefaultValueDefined( attributeType, targetObject);
                        
            // test
            assertFalse( result );
            
            // verify mocks
            EasyMock.verify( matcher );
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }        
    
    
    /**
     * Test that accessor can determine if attribute value is nil. 
     */
    @Test
    public void testIsNilReturnFalseOnNonNillableBooleanAttribute()
    {
        try
        {
            // setup objects 
            DomainType targetObject = DomainType.Factory.newInstance();
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("AdministrationPortEnabled", targetObject.schemaType() );            
            
            // setup method utility mock
            EasyMock.replay( methodUtils );
            EasyMock.replay( matcher );
            
            // get isNil state 
            boolean result = accessor.isNil( attributeType, targetObject);
                        
            // test
            assertFalse( result );
            
            // verify mocks
            EasyMock.verify( matcher );
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }
    
    /**
     * Test that accessor can determine if attribute value is nil. 
     */
    @Test
    public void testIsNilReturnFalseOnNonNilButNillableStringAttribute()
    {
        try
        {
            // setup objects 
            DomainType targetObject = DomainType.Factory.newInstance();            
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("RootDirectory", targetObject.schemaType());            
            Method isNilMethod = targetObject.getClass().getMethod( "isNilRootDirectory", null );            
            
            // setup method utility mock
            EasyMock.expect( methodUtils.getMethod( targetObject, "isNilRootDirectory")).andReturn( isNilMethod );
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, isNilMethod )).andReturn( false );
            EasyMock.replay( methodUtils );            
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            boolean result = accessor.isNil( attributeType, targetObject);
                        
            // test
            assertEquals( targetObject.isNilRootDirectory(), result );
            
            // verify mocks
            EasyMock.verify( matcher );
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }    

    /**
     * Test that accessor can determine if attribute value is nil. 
     */
    @Test
    public void testIsNilReturnTrueOnNilStringAttribute()
    {
        try
        {
            // setup objects 
            DomainType targetObject = DomainType.Factory.newInstance();
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("RootDirectory", targetObject.schemaType() );                        
            targetObject.setNilRootDirectory();
            Method isNilMethod = targetObject.getClass().getMethod( "isNilRootDirectory", null );            
            
            // setup method utility mock
            EasyMock.expect( methodUtils.getMethod( targetObject, "isNilRootDirectory")).andReturn( isNilMethod );
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, isNilMethod )).andReturn( true );
            EasyMock.replay( methodUtils );            
            EasyMock.replay( matcher );
            
            // attempt to get model object on SomeType instance
            boolean result = accessor.isNil( attributeType, targetObject);
                        
            // test
            assertEquals( targetObject.isNilRootDirectory(), result );
            
            // verify mocks
            EasyMock.verify( matcher );
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }    
    
    /**
     * Test that accessor can determine if attribute value is nil. 
     */
    @Test
    public void testIsNilReturnTrueOnEmptyArrayAttribute()
    {
        try
        {
            // setup objects 
            DomainType targetObject = DomainType.Factory.newInstance();
            SchemaProperty attributeType = accessor.getSchemaPropertyByName("Server", targetObject.schemaType() );                                    
            Method isNilMethod = targetObject.getClass().getMethod( "sizeOfServerArray", null );
            ServerType[] serverArray = new ServerType[0];
            
            
            // setup method utility mock
            EasyMock.expect( methodUtils.getMethod( targetObject, "sizeOfServerArray")).andReturn( isNilMethod );
            EasyMock.expect( methodUtils.invokeMethodWithNoArgs( targetObject, isNilMethod )).andReturn( 0 );
            EasyMock.replay( methodUtils );            
            EasyMock.replay( matcher );
            
            // invoke to see if attribute is nil 
            boolean result = accessor.isNil( attributeType , targetObject);
                        
            // test
            assertEquals( true , result );
            
            // verify mocks
            EasyMock.verify( matcher );
            EasyMock.verify( methodUtils );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }    
    
    /**
     * Test that schema property is return for defined attribute.
     */
    @Test
    public void testGetSchemaPropertyByNameReturnsDefinedObject() {

    	// setup
        DomainType targetObject = DomainType.Factory.newInstance();
        
        // get property
        SchemaProperty sp = accessor.getSchemaPropertyByName("AdministrationPort", targetObject.schemaType() );
    	
		// test
		assertNotNull(sp);
		assertEquals("AdministrationPort", sp.getJavaPropertyName());		
    }
    
    /**
     * Test that null is return for undefined attribute.
     */
    @Test
    public void testGetSchemaPropertyByNameReturnsNull() {

    	// setup
        DomainType targetObject = DomainType.Factory.newInstance();
        
        // get property
        SchemaProperty sp = accessor.getSchemaPropertyByName("PortalToMars", DomainType.type );
    	
		// test
		assertTrue(sp == null);	
    }
    
    /**
     * Test that schema property is return for defined attribute.
     */
    @Test
    public void testGetSchemaPropertyByNameReturnsDefinedObject2() {

    	// setup
        DomainType targetObject = DomainType.Factory.newInstance();
        
        // get property
        SchemaProperty sp = accessor.getSchemaPropertyByName("AdministrationPort", targetObject );
    	
		// test
		assertNotNull(sp);
		assertEquals("AdministrationPort", sp.getJavaPropertyName());		
    }
        
}
