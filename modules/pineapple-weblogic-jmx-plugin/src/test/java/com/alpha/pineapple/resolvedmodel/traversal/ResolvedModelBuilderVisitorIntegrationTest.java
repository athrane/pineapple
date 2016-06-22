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


package com.alpha.pineapple.resolvedmodel.traversal;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.python.parser.ast.If;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedCollection;
import com.alpha.pineapple.resolvedmodel.ResolvedObject;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherWebLogicJMX;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;
import com.alpha.testutils.WJPIntTestConstants;


/**
 * Integration test of the class <code>ResolvedModelBuilderVisitorImpl</code>.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class ResolvedModelBuilderVisitorIntegrationTest {

	/**
	 * Null parent resolved object.
	 */
    static final String NULL_PARENT = null;

	/**
	 * Null name.
	 */
    static final String[] NULL_NAMES = {};
    
	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * WeblogicJMXSession object mother.
     */
    ObjectMotherWeblogicJmxSession sessionMother;

    /**
     * WeblogicJMXSession object mother.
     */
    ObjectMotherWebLogicJMX jmxMother;
    
    /**
     * Object mother for the WebLogic domain model.
     */
    ObjectMotherContent contentMother;
    
    /**
     * Object under test.
     */
    @Resource(name="xmlBeansModelBuilderVisitor")
    ResolvedModelVisitor visitor;

	/**
	 * Execution result.
	 */
	ExecutionResult result;

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
		
    /***
     * JMX edit session.
     */
    WeblogicJMXEditSession session;
    
	@Before
	public void setUp() throws Exception {
		
        // create session mother
        sessionMother = new ObjectMotherWeblogicJmxSession();

        // create content mother
        contentMother = new ObjectMotherContent();
                
        // create JMX mother        
        jmxMother = new ObjectMotherWebLogicJMX();    
        
        // create execution result
        result = new ExecutionResultImpl( "Root result" );
        
        // create session
        session = sessionMother.createConnectedWlsJmxEditSession();
        session.startEdit();        
        
		// set session
		visitor.setSession(session);        
	}

	@After
	public void tearDown() throws Exception {		
        sessionMother = null;
        contentMother = null;
        jmxMother = null;	
        result =  null;
        
        // disconnect session
        if (session != null) {
        	if (session.isConnected()) {
        		session.disconnect();
        	}
        }        
        session = null;
	}
	  
	
    /**
     * Test that object can be looked up from the context.
     */
    @Test
    public void testCanGetInstanceFromContext()
    {
        assertNotNull( visitor );
    }    

    
    /**
     * Test that builder can be build a named security realm.
     */
    @Test
    public void testBuildNamedRealm() throws Exception
    {
		// create resolved collection
        ResolvedParticipant primary = contentMother.createRealmResolvedParticipant("myrealm");            
        ResolvedParticipant secondary = jmxMother.createRealmResolvedParticipant("myrealm", session);
        ResolvedType parent = null;
        ResolvedObject resolvedRoot = ResolvedTypeImpl.createResolvedObject( parent, primary, secondary);
        
        // build
        visitor.visit(resolvedRoot, result);
        result.setState(ExecutionResult.ExecutionState.COMPUTED);
                               
        // test
        // execution result
        assertEquals(ExecutionResult.ExecutionState.SUCCESS, result.getState());
        assertEquals(0, result.getChildren().length);

        // resolved children
        assertEquals(WJPIntTestConstants.numberRealmAttributes, resolvedRoot.getChildren().length);                                   
    }        

    /**
     * Test that builder can build authentication provider collection.
     */
    @Test
    public void testBuildAuthenticationProviderCollection() throws Exception
    {
		// create resolved collection
        ResolvedParticipant primary = contentMother.createAuthenticationProviderCollectionResolvedParticipant( NULL_NAMES ,"myrealm");            
        ResolvedParticipant secondary = jmxMother.createAuthenticationProviderCollection_ResolvedParticipant(session);
        ResolvedType parent = null;
        ResolvedCollection resolvedRoot = ResolvedTypeImpl.createResolvedCollection(parent, primary, secondary);
        
        // build
        visitor.visit(resolvedRoot, result);
        result.setState(ExecutionResult.ExecutionState.COMPUTED);
                               
        // test
        // execution result
        assertEquals(ExecutionResult.ExecutionState.SUCCESS, result.getState());
        assertEquals(0, result.getChildren().length);

        // resolved children
        assertEquals(2, resolvedRoot.getChildren().length);                                   
    }        

    /**
     * Test that authentication provider collection contains the expected resolved types,
     * where the primary model contains a unnamed authentication provider.
     */
    @Test
    public void testAuthenticationProviderCollectionWithUnnamedProvider() throws Exception
    {
		// create resolved collection
		String[] providerNames = new String[] { null };			
        ResolvedParticipant primary = contentMother.createAuthenticationProviderCollectionResolvedParticipant( providerNames ,"myrealm");            
        ResolvedParticipant secondary = jmxMother.createAuthenticationProviderCollection_ResolvedParticipant(session);
        ResolvedType parent = null;
        ResolvedCollection resolvedRoot = ResolvedTypeImpl.createResolvedCollection(parent, primary, secondary);
        
        // build
            visitor.visit(resolvedRoot, result);
            result.setState(ExecutionResult.ExecutionState.COMPUTED);
 
            // test 
        // resolved children
        ResolvedType[] children = resolvedRoot.getChildren();

        // test DefaultIdentityAsserter
        ResolvedType child = children[0];
        assertTrue(child instanceof ResolvedObject);
        assertTrue(child.getPrimaryParticipant().isResolutionSuccesful());
        assertFalse(child.getSecondaryParticiant().isResolutionSuccesful());            
        
        // test null
        child = children[1];
        assertTrue(child instanceof ResolvedObject);
        assertEquals("DefaultIdentityAsserter", child.getPrimaryParticipant().getName());
        assertFalse(child.getPrimaryParticipant().isResolutionSuccesful());
        assertTrue(child.getSecondaryParticiant().isResolutionSuccesful());            
        

        // test DefaultAuthenticator
        child = children[2];
        assertTrue(child instanceof ResolvedObject);
        assertEquals("DefaultAuthenticator", child.getPrimaryParticipant().getName());
        assertFalse(child.getPrimaryParticipant().isResolutionSuccesful());
        assertTrue(child.getSecondaryParticiant().isResolutionSuccesful());                    
    }        

    /**
     * Test that authentication provider collection contains the expected resolved types,
     * where the primary model contains a two named authentication providers.
     * 
     * @throws If test fails.
     */
    @Test
    public void testAuthenticationProviderCollectionWithNamedProviders() throws Exception
    {
		// create resolved collection
		String[] providerNames = new String[] { "DefaultIdentityAsserter", "DefaultAuthenticator"};
        ResolvedParticipant primary = contentMother.createAuthenticationProviderCollectionResolvedParticipant( providerNames ,"myrealm");            
        ResolvedParticipant secondary = jmxMother.createAuthenticationProviderCollection_ResolvedParticipant(session);
        ResolvedType parent = null;
        ResolvedCollection resolvedRoot = ResolvedTypeImpl.createResolvedCollection(parent, primary, secondary);
                
        // build
        visitor.visit(resolvedRoot, result);
        result.setState(ExecutionResult.ExecutionState.COMPUTED);
 
        // test 
        // resolved children
        ResolvedType[] children = resolvedRoot.getChildren();

        // test DefaultIdentityAsserter
        ResolvedType child = children[0];
        assertTrue(child instanceof ResolvedObject);
        assertTrue(child.getPrimaryParticipant().isResolutionSuccesful());
        assertEquals("DefaultIdentityAsserter", child.getPrimaryParticipant().getName());            

        // test DefaultAuthenticator
        child = children[1];
        assertTrue(child instanceof ResolvedObject);
        assertTrue(child.getPrimaryParticipant().isResolutionSuccesful());
        assertEquals("DefaultAuthenticator", child.getPrimaryParticipant().getName());                        
    }        
    
}
