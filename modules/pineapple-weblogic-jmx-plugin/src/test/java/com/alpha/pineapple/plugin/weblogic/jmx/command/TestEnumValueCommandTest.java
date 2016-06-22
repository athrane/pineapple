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


package com.alpha.pineapple.plugin.weblogic.jmx.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.model.xmlbeans.XmlBeansModelAccessor;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;
import com.oracle.xmlns.weblogic.domain.LogFileType.RotationType.Enum;

public class TestEnumValueCommandTest
{
    
    /**
     * Enum class used for unit test.
     */
    @SuppressWarnings( "serial" )
    static final class XMLBeansTestEnum extends StringEnumAbstractBase
    {
        static final Enum BY_SIZE = Enum.forString( "bySize" );
        static final Enum BY_TIME = Enum.forString( "byTime" );
        static final Enum NONE = Enum.forString( "none" );

        protected XMLBeansTestEnum( String s, int i )
        {
            super( s, i );
        }
    }            
    
    
    static final IntEnum CHOICE_0 = IntEnum.forString( "0" );
    static final IntEnum CHOICE_1 = IntEnum.forString( "1" );
    
    static final class IntEnum extends org.apache.xmlbeans.StringEnumAbstractBase
    {
        /**
         * Returns the enum value for a string, or null if none.
         */
        public static IntEnum forString(java.lang.String s)
            { return (IntEnum)table.forString(s); }
        /**
         * Returns the enum value corresponding to an int, or null if none.
         */
        public static IntEnum forInt(int i)
            { return (IntEnum)table.forInt(i); }
        
        private IntEnum(java.lang.String s, int i)
            { super(s, i); }
        
        static final int INT_CHOICE_0 = 1;
        static final int INT_CHOICE_1 = 2;
        
        public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
            new org.apache.xmlbeans.StringEnumAbstractBase.Table
        (
            new IntEnum[]
            {
                new IntEnum("0", INT_CHOICE_0 ),
                new IntEnum("1", INT_CHOICE_1 )            		
            }
        );
        private static final long serialVersionUID = 1L;
        private java.lang.Object readResolve() { return forInt(intValue()); } 
    }
        
    /**
     * Context.
     */
    Context context;
    
    /**
     * Resolved type.
     */
    ResolvedType resolvedType;

    /**
     * Object under test.
     */
    Command command;

	/**
	 * Mock execution result.
	 */
	ExecutionResult executionResult; 
    
    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;

    /**
     * Mock XMLBean model accessor.
     */
    XmlBeansModelAccessor xmlBeansModelAccessor;
	
    /**
     * Mock schema property.
     */
    SchemaProperty schemaProperty;

    @Before
    public void setUp() throws Exception
    {        
        // create context
        context = new ContextBase();
        
        // create command        
        command = new TestEnumValueCommand();
        
        
        executionResult = EasyMock.createMock( ExecutionResult.class );

     // create mock SchemaProperty 
		schemaProperty = EasyMock.createMock( SchemaProperty.class );
        EasyMock.replay( schemaProperty );        

        // create mock accessor
        xmlBeansModelAccessor = EasyMock.createMock( XmlBeansModelAccessor.class );
                
        // inject message source
        ReflectionTestUtils.setField( command, "xmlBeansModelAccessor", xmlBeansModelAccessor, XmlBeansModelAccessor.class );
        
        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
                
        // inject message source
        ReflectionTestUtils.setField( command, "messageProvider", messageProvider, MessageProvider.class );
        
        // complete mock source initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl(); 
        
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject()));        
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        EasyMock.expect( messageProvider.getMessage((String) EasyMock.isA( String.class )));        
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();
        
        EasyMock.replay(messageProvider);        
    }

    @After
    public void tearDown() throws Exception
    {
        command = null;
        resolvedType = null;
        context = null;
        messageProvider = null;
        executionResult = null;    
        schemaProperty = null;
    }

    /**
     * Test that two identical enums are compared correct.
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSuccessfullyComparesTwoStringEnums() throws Exception
    {    	
		// complete execution result initialization
    	executionResult.completeAsSuccessful((MessageProvider) EasyMock.isA( MessageProvider.class ),
    										 (String) EasyMock.isA( String.class ),
    										 (Object[]) EasyMock.isA( Object[].class ));
		EasyMock.replay( executionResult );		
    			
        // initialize resolved type            
        Enum primaryEnum = XMLBeansTestEnum.BY_SIZE;              
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( "enum", schemaProperty, primaryEnum );                       
        ResolvedParticipant sp = ResolvedParticipantImpl.createSuccessfulResult( "enum", String.class, "bySize" );
        resolvedType =  ResolvedTypeImpl.createResolvedType( null, pp, sp);
     
    	// complete mock accessor
    	EasyMock.expect(xmlBeansModelAccessor.isEnum(pp)).andReturn(true);
    	EasyMock.replay(xmlBeansModelAccessor);
        
        // initialize context
        context.put(TestEnumValueCommand.RESOLVED_TYPE, resolvedType );        
		context.put(TestEnumValueCommand.EXECUTIONRESULT_KEY, executionResult );
        
        // execute command             
        command.execute(context);
        
		// verify mocks
		EasyMock.verify(executionResult);		
		EasyMock.verify(xmlBeansModelAccessor);
		EasyMock.verify(schemaProperty);
    }

    
    /**
     * Test that two identical enums are compared correct.
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSuccessfullyComparesTwoIntegerEnums() throws Exception
    {    	
		// complete execution result initialization
    	executionResult.completeAsSuccessful((MessageProvider) EasyMock.isA( MessageProvider.class ),
    										 (String) EasyMock.isA( String.class ),
    										 (Object[]) EasyMock.isA( Object[].class ));
		EasyMock.replay( executionResult );		
    	
        // initialize resolved type            
		IntEnum primaryEnum = CHOICE_0;    
        
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( "enum", schemaProperty, primaryEnum );
        ResolvedParticipant sp = ResolvedParticipantImpl.createSuccessfulResult( "enum", int.class, "0" );        
        resolvedType =  ResolvedTypeImpl.createResolvedType( null, pp, sp);
     
    	// complete mock accessor
    	EasyMock.expect(xmlBeansModelAccessor.isEnum(pp)).andReturn(true);
    	EasyMock.replay(xmlBeansModelAccessor);
        
        // initialize context
        context.put(TestEnumValueCommand.RESOLVED_TYPE, resolvedType );        
		context.put(TestEnumValueCommand.EXECUTIONRESULT_KEY, executionResult );
        
        // execute command             
        command.execute(context);
        
		// verify mocks
		EasyMock.verify(executionResult);		
		EasyMock.verify(xmlBeansModelAccessor);
		EasyMock.verify(schemaProperty);
    }
    
    
    
    
    /**
     * Test that two null values are compared correct.
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSuccessfullyComparesTwoNullValues() throws Exception
    {    	
		// complete execution result initialization
    	executionResult.completeAsSuccessful((MessageProvider) EasyMock.isA( MessageProvider.class ),
    										 (String) EasyMock.isA( String.class ));
		EasyMock.replay( executionResult );		
    	
        // initialize resolved type            
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( "enum", schemaProperty, null );                       
        ResolvedParticipant sp = ResolvedParticipantImpl.createSuccessfulResult( "enum", String.class, null );
        resolvedType =  ResolvedTypeImpl.createResolvedType( null, pp, sp);
     
    	// complete mock accessor
    	EasyMock.expect(xmlBeansModelAccessor.isEnum(pp)).andReturn(true);
    	EasyMock.replay(xmlBeansModelAccessor);
        
        // initialize context
        context.put(TestEnumValueCommand.RESOLVED_TYPE, resolvedType );
		context.put(TestEnumValueCommand.EXECUTIONRESULT_KEY, executionResult );
        
        // execute command             
        command.execute(context);                        
        
		// verify mocks
		EasyMock.verify(executionResult);		
		EasyMock.verify(xmlBeansModelAccessor);
		EasyMock.verify(schemaProperty);
    }

    
    
    /**
     * Test that two different enums are compared correct.
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfStringEnumsAreDifferent() throws Exception
    {    	
		// complete execution result initialization
    	executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
				 (String) EasyMock.isA( String.class ), 
				 (Object[]) EasyMock.isA( Object[].class ));
		EasyMock.replay( executionResult );		
    	
        // initialize resolved type            
		Enum primaryEnum = XMLBeansTestEnum.BY_SIZE;
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( "enum", schemaProperty, primaryEnum );                       
        ResolvedParticipant sp = ResolvedParticipantImpl.createSuccessfulResult( "enum", String.class, "byTime" );
        resolvedType =  ResolvedTypeImpl.createResolvedType( null, pp, sp);
     
    	// complete mock accessor
    	EasyMock.expect(xmlBeansModelAccessor.isEnum(pp)).andReturn(true);
    	EasyMock.replay(xmlBeansModelAccessor);
        
        // initialize context
        context.put(TestEnumValueCommand.RESOLVED_TYPE, resolvedType );        
		context.put(TestEnumValueCommand.EXECUTIONRESULT_KEY, executionResult );

        // execute command             
        command.execute(context);
        
		// verify mocks
		EasyMock.verify(executionResult);		
		EasyMock.verify(xmlBeansModelAccessor);
		EasyMock.verify(schemaProperty);
    }    

    /**
     * Test that the test fails if value of primary participant is 
     * null and the value of secondary isn't.
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfPrimaryParticipantValueIsNull() throws Exception
    {    	
		// complete execution result initialization
    	executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
				 (String) EasyMock.isA( String.class ), 
				 (Object[]) EasyMock.isA( Object[].class ));
		EasyMock.replay( executionResult );		
    	        	
        // initialize resolved type            
		Enum primaryEnum = XMLBeansTestEnum.BY_SIZE;
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( "enum", schemaProperty , null );                       
        ResolvedParticipant sp = ResolvedParticipantImpl.createSuccessfulResult( "enum", String.class, "bySize" );
        resolvedType =  ResolvedTypeImpl.createResolvedType( null, pp, sp);
     
    	// complete mock accessor
    	EasyMock.expect(xmlBeansModelAccessor.isEnum(pp)).andReturn(true);
    	EasyMock.replay(xmlBeansModelAccessor);
        
        // initialize context
        context.put(TestEnumValueCommand.RESOLVED_TYPE, resolvedType );        
		context.put(TestEnumValueCommand.EXECUTIONRESULT_KEY, executionResult );

        // execute command             
        command.execute(context);
                    
		// verify mocks
		EasyMock.verify(executionResult);		
		EasyMock.verify(xmlBeansModelAccessor);
		EasyMock.verify(schemaProperty);
    }    

    
    /**
     * Test that the test fails if value of secondary participant is 
     * null and the value of primary isn't.
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfSecondaryParticipantValueIsNull() throws Exception
    {    	
		// complete execution result initialization
    	executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
				 (String) EasyMock.isA( String.class ), 
				 (Object[]) EasyMock.isA( Object[].class ));
		EasyMock.replay( executionResult );		
    	        	
        // initialize resolved type            
		Enum primaryEnum = XMLBeansTestEnum.BY_SIZE;
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( "enum", schemaProperty, primaryEnum );                       
        ResolvedParticipant sp = ResolvedParticipantImpl.createSuccessfulResult( "enum", String.class, null );
        resolvedType =  ResolvedTypeImpl.createResolvedType( null, pp, sp);
     
    	// complete mock accessor
    	EasyMock.expect(xmlBeansModelAccessor.isEnum(pp)).andReturn(true);
    	EasyMock.replay(xmlBeansModelAccessor);
        
        // initialize context
        context.put(TestEnumValueCommand.RESOLVED_TYPE, resolvedType );        
		context.put(TestEnumValueCommand.EXECUTIONRESULT_KEY, executionResult );

        // execute command             
        command.execute(context);
        
		// verify mocks
		EasyMock.verify(executionResult);		
		EasyMock.verify(xmlBeansModelAccessor);
		EasyMock.verify(schemaProperty);
    }    
    
    

    /**
     * Test that the test fails if the primary participant wasn't resolved successfully.
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfPrimaryParticipantWasntSuccessfullyResolved() throws Exception
    {
    	// complete mock accessor
    	EasyMock.replay(xmlBeansModelAccessor);
    	
		// complete execution result initialization
    	executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
				 (String) EasyMock.isA( String.class ), 
				 (Object[]) EasyMock.isA( Object[].class ));
		EasyMock.replay( executionResult );		
        
        // initialize resolved type            
        Exception exception = new Exception("unsuccesfully resolution exception");
        ResolvedParticipant pp = ResolvedParticipantImpl.createUnsuccessfulResult( "enum", null, null, exception );
        ResolvedParticipant sp = ResolvedParticipantImpl.createSuccessfulResult( "enum", String.class, "sec-enum-value");
        resolvedType =  ResolvedTypeImpl.createResolvedType( null, pp, sp);
        
        // initialize context
        context.put(TestEnumValueCommand.RESOLVED_TYPE, resolvedType );        
		context.put(TestEnumValueCommand.EXECUTIONRESULT_KEY, executionResult );

        // execute command             
        command.execute(context);
        
		// verify mocks
		EasyMock.verify(executionResult);		
		EasyMock.verify(xmlBeansModelAccessor);
    }

    /**
     * Test that the test fails if the secondary participant wasn't resolved successfully.
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfSecondaryParticipantWasntSuccessfullyResolved() throws Exception
    {
    	// complete mock accessor
    	EasyMock.replay(xmlBeansModelAccessor);
    	
		// complete execution result initialization
    	executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
				 (String) EasyMock.isA( String.class ), 
				 (Object[]) EasyMock.isA( Object[].class ));
		EasyMock.replay( executionResult );		
        
        // initialize resolved type
        Exception exception = new Exception("unsuccesfully resolution exception");
        Enum primaryEnum = XMLBeansTestEnum.BY_SIZE;
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( "enum", schemaProperty, primaryEnum  );            
        ResolvedParticipant sp = ResolvedParticipantImpl.createUnsuccessfulResult( "enum", null, null, exception );
        resolvedType =  ResolvedTypeImpl.createResolvedType( null, pp, sp);
        
        // initialize context
        context.put(TestEnumValueCommand.RESOLVED_TYPE, resolvedType );        
		context.put(TestEnumValueCommand.EXECUTIONRESULT_KEY, executionResult );

        // execute command             
        command.execute(context);            
        
		// verify mocks
		EasyMock.verify(executionResult);		
		EasyMock.verify(xmlBeansModelAccessor);
		EasyMock.verify(schemaProperty);
    }
    
    /**
     * Test that the test fails if the primary participant isn't XMLBeans enum.
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfPrimaryParticipantIsntXmlBeansEnum() throws Exception
    {    	
		// complete execution result initialization
    	executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
				 (String) EasyMock.isA( String.class ), 
				 (Object[]) EasyMock.isA( Object[].class ));
		EasyMock.replay( executionResult );		
    	
        // initialize resolved type
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( "enum", String.class, "pri-enum-value");            
        ResolvedParticipant sp = ResolvedParticipantImpl.createSuccessfulResult( "enum", String.class, "sec-enum-value");
        resolvedType =  ResolvedTypeImpl.createResolvedType( null, pp, sp);
     
    	// complete mock accessor
    	EasyMock.expect(xmlBeansModelAccessor.isEnum(pp)).andReturn(false);
    	EasyMock.replay(xmlBeansModelAccessor);
        
        // initialize context
        context.put(TestEnumValueCommand.RESOLVED_TYPE, resolvedType );        
		context.put(TestEnumValueCommand.EXECUTIONRESULT_KEY, executionResult );

        // execute command             
        command.execute(context);           
        
		// verify mocks
		EasyMock.verify(executionResult);		
		EasyMock.verify(xmlBeansModelAccessor);
    }
    
    /**
     * Test that the test fails if the secondary participant isn't String.
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfSecondaryParticipantIsntString() throws Exception
    {    	
		// complete execution result initialization
    	executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
				 (String) EasyMock.isA( String.class ), 
				 (Object[]) EasyMock.isA( Object[].class ));
		EasyMock.replay( executionResult );		
        
        // initialize resolved type
		Enum primaryEnum = XMLBeansTestEnum.BY_SIZE;
        ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( "enum", schemaProperty, primaryEnum  );                       
        ResolvedParticipant sp = ResolvedParticipantImpl.createSuccessfulResult( "enum", schemaProperty, primaryEnum  );            
        resolvedType =  ResolvedTypeImpl.createResolvedType( null, pp, sp);
     
    	// complete mock accessor
    	EasyMock.expect(xmlBeansModelAccessor.isEnum(pp)).andReturn(true);
    	EasyMock.replay(xmlBeansModelAccessor);
        
        // initialize context
        context.put(TestEnumValueCommand.RESOLVED_TYPE, resolvedType );        
		context.put(TestEnumValueCommand.EXECUTIONRESULT_KEY, executionResult );

        // execute command             
        command.execute(context);                                   
        
		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(xmlBeansModelAccessor);
		EasyMock.verify(schemaProperty);
    }    
    
}
