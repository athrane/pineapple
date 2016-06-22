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

import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.NamespaceManager;
import org.apache.xmlbeans.impl.values.XmlObjectBase;
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
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolver;

/**
 * Unit test of the <code>XmlBeansModelAccessorImpl</code> class.  
 */
public class XmlBeansModelResolverTest
{

    // test type
    class SimpleType extends XmlObjectBase
    {
		private static final long serialVersionUID = 1L;
		String xx = null;
		String[] yy = null;
    	
    	SimpleType(String xx) {
    		this.xx = xx; 
    	}

    	SimpleType(String[] yy) {
    		this.yy = yy; 
    	}
    	
        public String getXX()
        {
            return xx;
        }

        public String[] getYY()
        {
            return yy;
        }
        
        
		@Override
		protected String compute_text(NamespaceManager nsm) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected boolean equal_to(XmlObject xmlobj) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public SchemaType schemaType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void set_nil() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void set_text(String text) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected int value_hash_code() {
			// TODO Auto-generated method stub
			return 0;
		}
    }
	
    /**
     * Object under test
     */
    ModelResolver resolver;
        
    /**
     * Mock  Model accessor
     */
    XmlBeansModelAccessor modelAccessor;

    /**
     * Mock Getter method matcher.
     */
    GetterMethodMatcher matcher;

    /**
     * Mock  Schema type for attribute.
     */
    SchemaType schemaType;     

    /**
     * Mock Schema property for attribute.
     */
    SchemaProperty schemaProperty;     

    /**
     * Mock Schema property for model object.
     */
    SchemaProperty modelObjectSchemaProperty;     
    
    /**
     * Mock resolved participant for model object. 
     */
    ResolvedParticipant modelObjectParticipant;
    
    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;    
    
    @Before
    public void setUp() throws Exception
    {        
        // create mock accessor
        modelAccessor = EasyMock.createMock( XmlBeansModelAccessor.class );   

        // create mock method matcher
        matcher = EasyMock.createMock( GetterMethodMatcher.class );   
        
        // create mock schema type
        schemaType = EasyMock.createMock( SchemaType.class );

        // create mock schema property
        schemaProperty = EasyMock.createMock( SchemaProperty.class );
        
        // create mock schema property
        modelObjectSchemaProperty = EasyMock.createMock( SchemaProperty.class );
                
        // create mock model object participant 
        modelObjectParticipant = EasyMock.createMock( ResolvedParticipant.class );
        
        // create resolver
        resolver = new XmlBeansModelResolverImpl();
               
        // inject accessor into resolver 
        ReflectionTestUtils.setField( resolver, "modelAccessor", modelAccessor, XmlBeansModelAccessor.class );
        
        // inject matcher into resolver 
        ReflectionTestUtils.setField( resolver, "matcher", matcher, GetterMethodMatcher.class );
        
        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
        
        // inject message source
        ReflectionTestUtils.setField( resolver, "messageProvider", messageProvider , MessageProvider.class );
        
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
        resolver = null;
        modelAccessor = null;
        resolver = null;
        schemaType = null;
        schemaProperty = null;
        modelObjectParticipant = null;
        modelObjectSchemaProperty = null;
    }

    /**
     * Test that resolver object can be created.
     * Note: fields which are dependency injected by Spring isn't initialized. 
     */
    @Test
    public void testCanCreateInstance()
    {
        try
        {
            // test
            assertNotNull( resolver );            
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }    

    /**
     * Test that attribute resolution rejects model object which isn't 
     * of type <code>XmlObject</code>.
     */
    @Test
    public void testResolveAttributeRejectsModelObjectWhichIsntXmlObject()
    {        
        try
        {
            // setup objects
            Object modelObject = "a string model object"; 
            String attributeName = "an attribute which doesn't exist";

            // setup accessor mock
            EasyMock.expect(modelAccessor.getSchemaPropertyByName(attributeName, schemaProperty)).andReturn(modelObjectSchemaProperty);            
            EasyMock.replay( modelAccessor );
            
            // setup schema property mock
            EasyMock.replay( schemaProperty );
            
            // set mock model object participant
            EasyMock.expect(modelObjectParticipant.getType()).andReturn(schemaProperty).times(2);
            EasyMock.expect(modelObjectParticipant.getValue()).andReturn(modelObject).times(2);
            EasyMock.replay( modelObjectParticipant );

            // resolve attribute
            ResolvedParticipant result = resolver.resolveAttribute( attributeName, modelObjectParticipant );

            // test
            assertEquals( null, result.getValue() ); 
            assertEquals( modelObjectSchemaProperty, result.getType() );            
            assertEquals( attributeName, result.getName() );
            assertFalse( result.isResolutionSuccesful() );            
            assertEquals( ResolvedParticipant.ValueState.FAILED, result.getValueState() );            

            // verify mocks
            EasyMock.verify( modelAccessor );
            EasyMock.verify( schemaProperty );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }

    /**
     * Test that attribute resolution fails resolution of
     * object whose resolution failed but its type information was preserved.
     * The object is defined as such:
     * name is defined, 
     * exception is defined,
     * type is defined,
     * value is null.
     */
    @Test
    public void testResolveAttributeFailsResolutionFromParticipantWhoseResolutionFailed()
    {        
        try
        {
            // setup objects
            Object modelObject = null; 
            String attributeName = "an attribute which doesn't exist";

            // setup accessor mock
            EasyMock.expect(modelAccessor.getSchemaPropertyByName(attributeName, schemaProperty)).andReturn(modelObjectSchemaProperty);
            EasyMock.replay( modelAccessor );
            
            // setup schema property mock
            EasyMock.replay( schemaProperty );
            
            // set mock model object participant
            EasyMock.expect(modelObjectParticipant.getType()).andReturn(schemaProperty).times(2);
            EasyMock.expect(modelObjectParticipant.getValue()).andReturn(null).times(2);                        
            EasyMock.replay( modelObjectParticipant );

            // resolve attribute
            ResolvedParticipant result = resolver.resolveAttribute( attributeName, modelObjectParticipant );

            // test
            assertEquals( null, result.getValue() ); 
            assertEquals( modelObjectSchemaProperty, result.getType() );            
            assertEquals( attributeName, result.getName() );
            assertFalse( result.isResolutionSuccesful() );            
            assertEquals( ResolvedParticipant.ValueState.FAILED, result.getValueState() );            

            // verify mocks
            EasyMock.verify( modelAccessor );
            EasyMock.verify( schemaProperty );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }
    
    
    /**
     * Test that attribute can be resolved.
     */
    @Test
    public void testResolveAttributeWithSetValue()
    {
        // value return by the attribute 'XX' from the test type
        final String xxValue = "XX-value";
        
        try
        {
            // setup objects
        	SimpleType modelObject = new SimpleType(xxValue);
            String attributeName = "XX";
            Method getXXMethod = SimpleType.class.getMethod( "getXX", null );

            // setup accessor mock
            EasyMock.expect( modelAccessor.resolveGetterMethod( attributeName, modelObject ) ).andReturn( getXXMethod );
            EasyMock.expect( modelAccessor.getSchemaPropertyByName(attributeName, modelObjectSchemaProperty ) ).andReturn( schemaProperty );                        
            EasyMock.expect( modelAccessor.isValueSet( schemaProperty, modelObject ) ).andReturn( true );            
            EasyMock.expect( modelAccessor.getModelObject( modelObject, getXXMethod ) ).andReturn( xxValue );            
            EasyMock.expect( modelAccessor.isMethodEncrypted( getXXMethod )  ).andReturn( false );            
            EasyMock.replay( modelAccessor );
            
            // setup schema property mock
            EasyMock.replay( schemaProperty );

            // setup model object schema property mock
            EasyMock.replay( modelObjectSchemaProperty );
                        
            // set mock model object participant
            EasyMock.expect(modelObjectParticipant.getValue()).andReturn(modelObject).times(2);
            EasyMock.expect(modelObjectParticipant.getType()).andReturn(modelObjectSchemaProperty).times(2);
            EasyMock.replay( modelObjectParticipant );

            // resolve attribute
            ResolvedParticipant result = resolver.resolveAttribute( attributeName, modelObjectParticipant );

            // test
            assertEquals( xxValue, result.getValue() ); 
            assertEquals( schemaProperty, result.getType() );            
            assertEquals( attributeName, result.getName() );
            assertTrue( result.isResolutionSuccesful() );            
            assertEquals( ResolvedParticipant.ValueState.SET, result.getValueState() );            

            // verify mocks
            EasyMock.verify( modelAccessor );
            EasyMock.verify( schemaProperty );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }

    /**
     * Test that attribute can be resolved.
     */
    @Test
    public void testResolveAttributeWithDefaultValue()
    {
        // value return by the attribute 'XX' from the test type
        final String xxValue = "XX-value";
        
        try
        {
            // setup objects
        	SimpleType modelObject = new SimpleType(xxValue);
            String attributeName = "XX";             
            Method getXXMethod = SimpleType.class.getMethod( "getXX", null );
            Class<? extends Object> attributeType = getXXMethod.getReturnType();
            String defaultValue = "default-value";
            
            // setup accessor mock
            EasyMock.expect( modelAccessor.resolveGetterMethod( attributeName, modelObject ) ).andReturn( getXXMethod );
            EasyMock.expect( modelAccessor.getSchemaPropertyByName(attributeName, modelObjectSchemaProperty ) ).andReturn( schemaProperty );                                    
            EasyMock.expect( modelAccessor.isValueSet( schemaProperty, modelObject ) ).andReturn( false);            
            EasyMock.expect( modelAccessor.isDefaultValueDefined( schemaProperty, modelObject ) ).andReturn( true);                   
            EasyMock.expect( modelAccessor.getDefaultValue( schemaProperty, modelObject ) ).andReturn( defaultValue);                        
            EasyMock.expect( modelAccessor.isMethodEncrypted( getXXMethod )  ).andReturn( false );            
            EasyMock.replay( modelAccessor );

            // setup schema property mock
            EasyMock.replay( schemaProperty );

            // setup model object schema property mock
            EasyMock.replay( modelObjectSchemaProperty );
            
            // set mock model object participant
            EasyMock.expect(modelObjectParticipant.getValue()).andReturn(modelObject).times(2);
            EasyMock.expect(modelObjectParticipant.getType()).andReturn(modelObjectSchemaProperty).times(2);
            EasyMock.replay( modelObjectParticipant );

            // resolve attribute
            ResolvedParticipant result = resolver.resolveAttribute( attributeName, modelObjectParticipant );

            // test
            assertEquals( defaultValue, result.getValue() ); 
            assertEquals( schemaProperty, result.getType() );            
            assertEquals( attributeName, result.getName() );            
            assertTrue( result.isResolutionSuccesful() );
            assertEquals( ResolvedParticipant.ValueState.DEFAULT, result.getValueState() );            

            // verify mocks
            EasyMock.verify( modelAccessor );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }
    
    
    
    /**
     * Test that resolver return unsuccessful resolution if attribute can't
     * be found on MBean.
     */
    @Test
    public void testResolverReturnUnsuccessfulResolutionIfAttributeIsUnknown()
    {
        // value return by the attribute 'XX' from the test type
        final String xxValue = "XX-value";
        
        try
        {
            // setup objects
        	SimpleType modelObject = new SimpleType(xxValue);
            String attributeName = "YY"; // unknown attribute which is attempted to be resolved.
            Method getXXMethod = SimpleType.class.getMethod( "getXX", null );

            // setup accessor mock
            EasyMock.expect( modelAccessor.getSchemaPropertyByName(attributeName, modelObjectSchemaProperty ) ).andReturn( null );                        
            EasyMock.replay( modelAccessor );

            // setup model object schema property mock
            EasyMock.replay( modelObjectSchemaProperty );
            
            // set mock model object participant
            EasyMock.expect(modelObjectParticipant.getValue()).andReturn(modelObject).times(2);
            EasyMock.expect(modelObjectParticipant.getType()).andReturn(modelObjectSchemaProperty).times(2);
            EasyMock.replay( modelObjectParticipant );

            // resolve attribute
            ResolvedParticipant result = resolver.resolveAttribute( attributeName, modelObjectParticipant );

            // test
            assertEquals( null, result.getValue() ); 
            assertEquals( null, result.getType() );            
            assertEquals( attributeName, result.getName() );
            assertEquals( ModelResolutionFailedException.class, result.getResolutionError().getClass() );

            // verify mocks
            EasyMock.verify( modelAccessor );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }    

    /**
     * Test that method fails if primary participant type 
     * isn't <code>SchemaProperty</code>
     */
    @Test(expected = ModelResolutionFailedException.class)
    public void testResolveAttributeNamesMethodFailsIfAttributeTypeIsntSchemaProperty() throws Exception
    {
        // setup
        ResolvedType pair = ResolvedTypeImpl.createResolvedObject( new Integer( 12 ), new Integer( 0 ) );

        // invoke to provoke exception
        resolver.resolveAttributeNames( pair.getPrimaryParticipant() );
    }
    
    
    /**
     * Test that attribute names can be resolved
     * for type with a no attributes.
     */
    @Test
    public void testResolveAttributeNamesForZeroAttributes()
    {
        try
        {
            // setup objects
            ResolvedParticipant attribute = ResolvedParticipantImpl.createSuccessfulResult( "some-id", schemaProperty, 0 );
            SchemaProperty[] properties = new SchemaProperty[0];            
            
            // setup mock objects            
            EasyMock.expect( schemaProperty.getType() ).andReturn( schemaType );
            EasyMock.replay( schemaProperty);        
            EasyMock.expect( schemaType.getProperties() ).andReturn( properties );        
            EasyMock.replay( schemaType );
                                   
            // resolve attribute
            String[] result = resolver.resolveAttributeNames( attribute );

            // test
            assertEquals( 0, result.length );             

            // verify mock object
            EasyMock.verify( schemaType );        
            EasyMock.verify( schemaProperty );        
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }    
    
    
    /**
     * Test that attribute names can be resolved
     * for type with a single attribute.
     */
    @Test
    public void testResolveAttributeNamesForSingleAttribute()
    {
        try
        {
            // setup objects
            ResolvedParticipant attribute = ResolvedParticipantImpl.createSuccessfulResult( "some-id", schemaProperty, 0 );
            SchemaProperty property1 = EasyMock.createMock( SchemaProperty.class );
            String propertyName1 = "property-name-1";
            SchemaProperty[] properties = new SchemaProperty[] { property1 };            
            
            // setup mock objects            
            EasyMock.expect( schemaProperty.getType() ).andReturn( schemaType );
            EasyMock.replay( schemaProperty);        
            EasyMock.expect( schemaType.getProperties() ).andReturn( properties );        
            EasyMock.replay( schemaType );
            EasyMock.expect( property1.getJavaPropertyName() ).andReturn( propertyName1);
            EasyMock.replay( property1 );        
                                   
            // resolve attribute
            String[] result = resolver.resolveAttributeNames( attribute );

            // test
            assertEquals( 1, result.length );             
            assertEquals( propertyName1, result[0]);            

            // verify mock object
            EasyMock.verify( schemaType );        
            EasyMock.verify( schemaProperty );        
            EasyMock.verify( property1 );            
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }    

    /**
     * Test that attribute names can be resolved
     * for type with multiple attributes.
     */
    @Test
    public void testResolveAttributeNamesForMutipleAttributes()
    {
        try
        {
            // setup objects
            ResolvedParticipant attribute = ResolvedParticipantImpl.createSuccessfulResult( "some-id", schemaProperty, 0 );
            SchemaProperty property1 = EasyMock.createMock( SchemaProperty.class );
            String propertyName1 = "property-name-1";
            SchemaProperty property2 = EasyMock.createMock( SchemaProperty.class );
            String propertyName2 = "property-name-2";            
            SchemaProperty[] properties = new SchemaProperty[] { property1, property2 };            
            
            // setup mock objects            
            EasyMock.expect( schemaProperty.getType() ).andReturn( schemaType );
            EasyMock.replay( schemaProperty);        
            EasyMock.expect( schemaType.getProperties() ).andReturn( properties );        
            EasyMock.replay( schemaType );
            EasyMock.expect( property1.getJavaPropertyName() ).andReturn( propertyName1);
            EasyMock.replay( property1 );        
            EasyMock.expect( property2.getJavaPropertyName() ).andReturn( propertyName2);
            EasyMock.replay( property2 );        
                                   
            // resolve attribute
            String[] result = resolver.resolveAttributeNames( attribute );

            // test
            assertEquals( 2, result.length );             
            assertEquals( propertyName1, result[0]);            
            assertEquals( propertyName2, result[1]);            

            
            // verify mock object
            EasyMock.verify( schemaType );        
            EasyMock.verify( schemaProperty );        
            EasyMock.verify( property1 );
            EasyMock.verify( property2 );                        
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }    

    /**
     * Test that content of attribute which is an array 
     * can be resolved.
     */
    @Test
    public void testResolveCollectionAttributeValuesOnArrayWithOneEntry()
    {
        // value return by the attribute 'YY' from the test type
        String arrayEntry1 = "entry1";
        final String[] xxValue = { arrayEntry1 };
        
        try
        {
            // setup objects
        	SimpleType modelObject = new SimpleType(xxValue);
            String attributeName = "YY";
            Method getXXMethod = SimpleType.class.getMethod( "getYY", null );            
            ResolvedParticipant attribute = ResolvedParticipantImpl.createSuccessfulResult( attributeName, schemaProperty, xxValue );            

            // setup accessor mock            
            EasyMock.expect( modelAccessor.getNameAttributeFromModelObject( arrayEntry1 )).andReturn( "entry1-name" );
            EasyMock.replay( modelAccessor );
            
            // setup schema property mock
            EasyMock.replay( schemaProperty );
                        
            // resolve array content
            HashMap<String, ResolvedParticipant> result = resolver.resolveCollectionAttributeValues( attribute);

            // test
            assertNotNull( result );
            assertEquals( 1, result.size() );
            assertTrue( result.keySet().contains( "entry1-name" ) );                        
            assertEquals( "entry1-name" , result.get( "entry1-name" ).getName() );
            assertEquals( schemaProperty, result.get( "entry1-name" ).getType() );            
            assertEquals( "entry1", result.get( "entry1-name" ).getValue() );            
            
            // verify mocks
            EasyMock.verify( modelAccessor );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }
    
    /**
     * Test that content of attribute which is an array can be resolved.
     */
    @Test
    public void testResolveCollectionAttributeValuesOnArrayWithTwoEntries()
    {
        // value return by the attribute 'YY' from the test type
        String arrayEntry1 = "entry1";
        String arrayEntry2 = "entry2";
        final String[] xxValue = { arrayEntry1, arrayEntry2 };
        
        try
        {
            // setup objects
        	SimpleType modelObject = new SimpleType(xxValue);
            String attributeName = "YY";
            Method getXXMethod = SimpleType.class.getMethod( "getYY", null );            
            ResolvedParticipant attribute = ResolvedParticipantImpl.createSuccessfulResult( attributeName, schemaProperty, xxValue );            

            // setup accessor mock
            
            EasyMock.expect( modelAccessor.getNameAttributeFromModelObject( arrayEntry1 )).andReturn( "entry1-name" );
            EasyMock.expect( modelAccessor.getNameAttributeFromModelObject( arrayEntry2 )).andReturn( "entry2-name" );            
            EasyMock.replay( modelAccessor );

            // setup schema property mock
            EasyMock.replay( schemaProperty );
            
            // resolve array content
            HashMap<String, ResolvedParticipant> result = resolver.resolveCollectionAttributeValues( attribute);

            // test
            assertNotNull( result );
            assertEquals( 2, result.size() );
            assertTrue( result.keySet().contains( "entry1-name" ) );            
            assertTrue( result.keySet().contains( "entry2-name" ) );            
            assertEquals( "entry1-name" , result.get( "entry1-name" ).getName() );
            assertEquals( schemaProperty, result.get( "entry1-name" ).getType() );            
            assertEquals( "entry1", result.get( "entry1-name" ).getValue() );            
            
            // verify mocks
            EasyMock.verify( modelAccessor );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }

    
    
    /**
     * Test that content of attribute which is an empty array can be resolved.
     */
    @Test
    public void testResolveCollectionAttributeValuesOnEmptyArray()
    {
        // value return by the attribute 'YY' from the test type
        final String[] xxValue = {}; // empty array
        
        try
        {        	
            // setup objects
        	SimpleType modelObject = new SimpleType(xxValue);
            String attributeName = "YY";
            Method getXXMethod = SimpleType.class.getMethod( "getYY", null );            
            ResolvedParticipant attribute = ResolvedParticipantImpl.createSuccessfulResult( attributeName, schemaProperty, xxValue );            

            // setup accessor mock            
            EasyMock.replay( modelAccessor );
            
            // setup schema property mock
            EasyMock.replay( schemaProperty );
                        
            // resolve array content
            HashMap<String, ResolvedParticipant> result = resolver.resolveCollectionAttributeValues( attribute);

            // test
            assertNotNull( result );
            assertEquals( 0, result.size() );
            assertNotNull( result.keySet() );            
            
            // verify mocks
            EasyMock.verify( modelAccessor );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }

    /**
     * Test that content of attribute which is an array
     * where the resolution failed can be resolved.
     */
    @Test
    public void testResolveCollectionAttributeValuesWithFailedResolution()
    {
        // value return by the attribute 'YY' from the test type
        final String[] nullValue = null; // null array
        
        try
        {        	
            // setup objects
            String attributeName = "YY";
            Exception exception = new ModelResolutionFailedException("some message");
			ResolvedParticipant attribute = ResolvedParticipantImpl.createUnsuccessfulResult(attributeName, schemaProperty, nullValue, exception);            

            // setup accessor mock            
            EasyMock.replay( modelAccessor );
            
            // setup schema property mock
            EasyMock.replay( schemaProperty );
                        
            // resolve array content
            HashMap<String, ResolvedParticipant> result = resolver.resolveCollectionAttributeValues( attribute);

            // test
            assertNotNull( result );
            assertEquals( 0, result.size() );
            assertNotNull( result.keySet() );            
            
            // verify mocks
            EasyMock.verify( modelAccessor );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }
    
}
