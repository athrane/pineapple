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


package com.alpha.pineapple.plugin.weblogic.jmx.operation;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.javautils.OperationUtils;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;
import com.alpha.pineapple.plugin.weblogic.jmx.session.JMXSession;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.traversal.ModelTraversalDirector;
import com.alpha.pineapple.resolvedmodel.traversal.ResolvedModelVisitor;
import com.alpha.pineapple.session.Session;
import com.alpha.testutils.ObjectMotherContent;
import com.oracle.xmlns.weblogic.domain.DomainDocument;

/**
 * Unit test of the <code>TestOperation</code> class.
 */
public class TestOperationTest
{
    
    /**
     * Object under test.
     */
    TestOperation operation;

    /**
     * Object mother for the WebLogic domain model.
     */
    ObjectMotherContent contentMother;

    /**
     * Mock WebLogic edit session object.
     */
    WeblogicJMXEditSession session;

    /**
     * Mock builder visitor.
     */
    ResolvedModelVisitor builderVisitor;      

    /**
     * Mock test visitor.
     */
    ResolvedModelVisitor testVisitor;      
        
    /**
     * Mock test director.
     */
    ModelTraversalDirector testDirector;      
    
	/**
	 * Mock execution result.
	 */
	ExecutionResult executionResult; 

    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;      

    /**
     * Mock operation utils.
     */
    OperationUtils operationUtils;      
        
    /**
     * Mock model initializer;
     */
    ResolvedModelInitializer modelInitializer;    
        
    @Before
    public void setUp() throws Exception
    {
        // create content mother
        contentMother = new ObjectMotherContent();

        // create mocks 
        builderVisitor = EasyMock.createMock( ResolvedModelVisitor.class );
        testVisitor = EasyMock.createMock( ResolvedModelVisitor.class );
        session = EasyMock.createMock( WeblogicJMXEditSession.class );
        messageProvider = EasyMock.createMock( MessageProvider.class );
        executionResult = EasyMock.createMock( ExecutionResult.class );
        testDirector = EasyMock.createMock( ModelTraversalDirector.class );
        modelInitializer = EasyMock.createMock( ResolvedModelInitializer.class );        
        operationUtils = EasyMock.createMock( OperationUtils .class );        
            
        // create operation
        operation = new TestOperation();

        // inject mocks 
        ReflectionTestUtils.setField( operation, "testDirector", testDirector);
        ReflectionTestUtils.setField( operation, "modelInitializer", modelInitializer);
        ReflectionTestUtils.setField( operation, "operationUtils", operationUtils);
        ReflectionTestUtils.setField( operation, "messageProvider", messageProvider);
        
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
        operation = null;
        contentMother = null;
        executionResult = null;
        messageProvider = null;
        builderVisitor = null;
        modelInitializer = null;
    }

    /**
     * Complete operation utils setup
     * 
     * @param content Content document.
     * 
     * @throws Exception If test fails.
     */
	void completeMockOperationUtilsSetup(Object content) throws Exception 
	{
		operationUtils.validateContentType(content, WebLogicMBeanConstants.LEGAL_CONTENT_TYPES);
		operationUtils.validateSessionType(session, WeblogicJMXEditSession.class );		
		EasyMock.replay( operationUtils );
	}
    
    
    /**
     * Complete builder visitor setup
     */
	void completeMockBuilderVisitorSetup() 
	{
		EasyMock.replay( builderVisitor );
	}

    /**
     * Complete test visitor setup
     */	
	void completeMockTestVisitorSetup() 
	{
		EasyMock.replay( testVisitor );
	}

    /**
     * Complete mock session setup.
     */
	void completeMockSessionSetup() throws Exception {
		session.startEdit();            
		session.saveAndActivate();
		EasyMock.replay( session );
	}
	
    /**
     * Complete model initializer setup
     * 
     * @param property resolved model root object.
     * 
     * @throws Exception if initialization fails. 
     */		
    void completeModelInitializerSetup(ResolvedType resolvedType) throws Exception {

    	EasyMock.expect( modelInitializer.initialize((Object) EasyMock.isA( Object.class ),
    			(WeblogicJMXEditSession) EasyMock.isA( WeblogicJMXEditSession.class ))).andReturn(resolvedType); 
    	EasyMock.replay(modelInitializer);
    }

    /**
     * Complete test director setup
     */			
	void completeMockTestDirectorSetup() throws Exception 
	{            
		testDirector.startTraversal(
				(Session) EasyMock.isA( Session.class ),
				(ResolvedType) EasyMock.isA( ResolvedType.class ),
				(ExecutionResult) EasyMock.isA( ExecutionResult.class ));            
		EasyMock.replay( testDirector );
	}
	
	
    /**
     * Test that operation can execute with no domain defined, only a defined domain document.
     * @throws Exception If test fails. 
     */
    @Test
    public void testCanExcuteWithNoDomain() throws Exception
    {
        // create content
        DomainDocument content = contentMother.createEmptyDomain();
                                 
        // complete mock visitor setup
        completeMockBuilderVisitorSetup();
        completeMockTestVisitorSetup();            
        
        // complete mock director setup       
        completeMockTestDirectorSetup();
                                    
		// complete execution result initialization
        executionResult.completeAsComputed(messageProvider, "to.completed", null, "to.failed", null);
		EasyMock.replay( executionResult );		
        
		// create mock resolved model root 
		ResolvedType resolvedType = EasyMock.createMock( ResolvedType.class);
		EasyMock.replay( resolvedType );
		
        // complete mock setups  
		completeModelInitializerSetup(resolvedType);
        completeMockSessionSetup();
		completeMockOperationUtilsSetup( content );
		
        // invoke operation
        operation.execute( content, session, executionResult );

        // verify mock objects
        EasyMock.verify( executionResult );
        EasyMock.verify( session );            
        EasyMock.verify( builderVisitor );            
        EasyMock.verify( testVisitor );
		EasyMock.verify( resolvedType );   
		EasyMock.verify( modelInitializer );
		EasyMock.verify( operationUtils);
    }

    /**
     * Test that operation can execute with a minimal domain, i.e a named domain.
     * @throws Exception If test fails.
     */
    @Test
    public void testCanExecuteWithMinimalDomain() throws Exception
    {
        // create content
        String domainName = "minimal-domain";
        DomainDocument content = contentMother.createMinimalDomain( domainName );
        
        // complete mock visitor setup
        completeMockBuilderVisitorSetup();
        completeMockTestVisitorSetup();            
        
        // complete mock director setup       
        completeMockTestDirectorSetup();
                
		// complete execution result initialization
        executionResult.completeAsComputed(messageProvider, "to.completed", null, "to.failed", null);
		EasyMock.replay( executionResult );		

		// create mock resolved model root 
		ResolvedType resolvedType = EasyMock.createMock( ResolvedType.class);
		EasyMock.replay( resolvedType );
		
        // complete mock setups  
		completeModelInitializerSetup(resolvedType);
        completeMockSessionSetup();
		completeMockOperationUtilsSetup(content);
		
        // invoke operation
        operation.execute( content, session, executionResult );

        // verify mock objects
        EasyMock.verify( session );            
        EasyMock.verify( builderVisitor );
        EasyMock.verify( executionResult);
		EasyMock.verify( resolvedType );   
		EasyMock.verify( modelInitializer );
		EasyMock.verify( operationUtils);    		
    }

    /**
     * Test that operation fails if the content is null.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testFailsIfContentIsUndefined() throws Exception
    {
        // invoke operation
        operation.execute( null, session, null );
    }

    /**
     * Test that operation fails if the execution result is null.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testFailsIfResultIsUndefined() throws Exception
    {
        // create content
        DomainDocument content = contentMother.createEmptyDomain();

        // invoke operation
        operation.execute( content, session, null );        
    }
        
    /**
     * Test that operation fails if the session is null.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testFailsIfSessionIsUndefined() throws Exception
    {
        // create content
        Object content = contentMother.createMinimalDomain( "minimal-domain" );

        // invoke operation
        operation.execute( content, null, executionResult );
    }

    /**
     * Test that operation fails if the content argument doesn't contain an object of the type
     * <code>DomainDocument</code>.
     */
    @Test( expected = PluginExecutionFailedException.class )
    public void testFailsIfContentDoesntContainObjectOfDomainDocumentType() throws Exception
    {
        // create content
        Object content = "a-string-object";

        // complete operation utils mock setups
		// expect call with wrong session and throw exception
		operationUtils.validateContentType(content, WebLogicMBeanConstants.LEGAL_CONTENT_TYPES);
		EasyMock.expectLastCall().andThrow(new PluginExecutionFailedException("some-message"));
		EasyMock.replay( operationUtils );
        
        // invoke operation
        operation.execute( content, session, executionResult );
    }

    /**
     * Test that operation fails if the session argument doesn't contain an object of the type
     * <code>WeblogicJMXEditSession</code>.
     */
    @Test( expected = PluginExecutionFailedException.class )
    public void testFailsIfSessionDoesntContainObjectOfEditSessionType() throws Exception
    {    	
        // create content
        Object content = contentMother.createMinimalDomain( "minimal-domain" );

        // create non edit-session
        JMXSession jmxSession = EasyMock.createMock( JMXSession.class );

        // complete operation utils mock setups  
		operationUtils.validateContentType(content, WebLogicMBeanConstants.LEGAL_CONTENT_TYPES);
		// expect call with wrong session and throw exception
		operationUtils.validateSessionType(jmxSession, WeblogicJMXEditSession.class );
		EasyMock.expectLastCall().andThrow(new PluginExecutionFailedException("some-message"));
		EasyMock.replay( operationUtils );
        
        // invoke operation
        operation.execute( content, jmxSession, executionResult );
    }

    /**
     * Test that operation fails if the session initialization fails..
     */
    @Test
    public void testFailsIfSessionInitializationFails() throws Exception
    {
        try {
        // create content
        Object content = contentMother.createMinimalDomain( "minimal-domain" );

        // complete mock object setup
        session.startEdit();
        EasyMock.expectLastCall().andThrow( new Exception("some-exception" ) );
        EasyMock.replay( session );
        
        // invoke operation
        operation.execute( content, session, executionResult );
        
        } catch (PluginExecutionFailedException e) {

            // verify mock
            EasyMock.verify( session );            
        }
    }

    /**
     * Test that operation fails if start editing mode fails.
     */
    @Test
    public void testFailsIfActivatingEditModeFails() throws Exception
    {
        
        try {
        // create content
        Object content = contentMother.createMinimalDomain( "minimal-domain" );
        
        session.startEdit();
        EasyMock.expectLastCall().andThrow( new Exception("some-exception" ) );
        EasyMock.replay( session );
        
        // invoke operation
        operation.execute( content, session, executionResult );
        
        } catch (PluginExecutionFailedException e) {

            // verify mock
            EasyMock.verify( session );
            
        }
    }
        
}
