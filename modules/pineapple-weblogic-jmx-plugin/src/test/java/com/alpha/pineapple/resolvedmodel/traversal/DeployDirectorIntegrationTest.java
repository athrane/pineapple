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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherWebLogicJMX;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;
import com.alpha.testutils.WJPIntTestConstants;
import com.oracle.xmlns.weblogic.domain.DomainDocument;
import com.oracle.xmlns.weblogic.domain.SecurityConfigurationType;
import com.oracle.xmlns.weblogic.domain.ServerType;
import com.oracle.xmlns.weblogic.security.AuthenticationProviderType;
import com.oracle.xmlns.weblogic.security.RealmType;
import com.oracle.xmlns.weblogic.security.RoleMapperType;

/**
 * Integration test of the Spring configured bean named <code>deployDirector</code>
 * which is an instance of the class <code>PreOrderDirectorImpl</code>.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class DeployDirectorIntegrationTest {

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
    @Resource
    ModelTraversalDirector deployDirector;

	/**
	 * JMX session.
	 */
	WeblogicJMXEditSession session;
    
	/**
	 * Execution result.
	 */
	ExecutionResult result;
        
    /**
     * Resolved type.
     */
    ResolvedType resolvedType;
		
	@Before
	public void setUp() throws Exception {
		
        // create session mother
        sessionMother = new ObjectMotherWeblogicJmxSession();

        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();        
        
        // create content mother
        contentMother = new ObjectMotherContent();

        // create JMX mother        
        jmxMother = new ObjectMotherWebLogicJMX();
                
        // create execution result
        result = new ExecutionResultImpl( "Root result" );
        
		// start edit session 
		session.startEdit();		
	}

	@After
	public void tearDown() throws Exception {
		
		// destroy session
		if (session != null) {
			session.disconnect();
		}
		
		deployDirector = null;
        sessionMother = null;
        contentMother = null;
        jmxMother = null;
        result = null;
	}
	
	
    /**
     * Test that instance can be looked up from the context.
     */
    @Test
    public void testCanInstanceFromContext()
    {
        assertNotNull( deployDirector);
    }	
           
    /**
     * Test that builder can be invoked a minimal domain, i.e a named domain.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanExecuteWithMinimalDomain() throws Exception
    {
        // create content
        DomainDocument domainDoc = contentMother.createMinimalDomain( WJPIntTestConstants.targetEnvironment );
                    
        // create resolved type at the root of the models
        ResolvedParticipant primary = contentMother.createDomainResolvedParticipant(domainDoc);            
        ResolvedParticipant secondary = jmxMother.createDomainObjName_ResolvedParticipant(session); 
        ResolvedType parent = null;
        resolvedType = ResolvedTypeImpl.createResolvedObject(parent, primary, secondary);
                                                        
        // build
        deployDirector.startTraversal(session, resolvedType, result);
        
        // result from builder
        ExecutionResult builderResult = result.getChildren()[0];
        
        // test
        assertEquals(ExecutionResult.ExecutionState.SUCCESS, builderResult.getState());            
    }
           
    
    /**
     * Test that builder can be invoked a domain, wit a named server.
     * 
     * @throws Exception If test fails. 
     */
    @Test
    public void testCanExecuteWithDomainWithNamedServer() throws Exception
    {
    	// declare values
		String attributeName = "Server";
		String arryAttributeName = "admserver";
    	
        // create content
        DomainDocument domainDoc = contentMother.createMinimalDomain( WJPIntTestConstants.targetEnvironment );
        String domainName = domainDoc.getDomain().getName();
        
        // set attribute
		ServerType server = domainDoc.getDomain().addNewServer();
		server.setName(arryAttributeName);
        
        // create resolved type at the root of the models
        ResolvedParticipant primary = contentMother.createDomainResolvedParticipant(domainDoc);            
        ResolvedParticipant secondary = jmxMother.createDomainObjName_ResolvedParticipant(session); 
        ResolvedType parent = null;
        resolvedType = ResolvedTypeImpl.createResolvedObject(parent, primary, secondary);
                                                        
        // build
        deployDirector.startTraversal(session, resolvedType, result);
        
        // result from builder
        ExecutionResult builderResult = result.getChildren()[0];
        
        // test
        assertEquals(ExecutionResult.ExecutionState.SUCCESS, builderResult.getState());
    }    

    /**
     * Test that builder can be invoked a domain, wit a named security realm.
     * 
     * @throws Exception If test fails. 
     */
    @Test
    public void testCanExecuteWithDomainWithNamedSecurityRealm() throws Exception
    {
        // create content
        DomainDocument domainDoc = contentMother.createMinimalDomain( WJPIntTestConstants.targetEnvironment );
        
        // set attribute
        SecurityConfigurationType securityConfiguration = domainDoc.getDomain().addNewSecurityConfiguration();
        securityConfiguration.setName(WJPIntTestConstants.targetEnvironment );
        RealmType realm = securityConfiguration.addNewRealm();
        realm.setName("myrealm");            
                    
        // create resolved type at the root of the models
        ResolvedParticipant primary = contentMother.createDomainResolvedParticipant(domainDoc);            
        ResolvedParticipant secondary = jmxMother.createDomainObjName_ResolvedParticipant(session);
        ResolvedType parent = null;
        resolvedType = ResolvedTypeImpl.createResolvedObject(parent, primary, secondary);
                                                        
        // build
        deployDirector.startTraversal(session, resolvedType, result);
        
        // result from builder
        ExecutionResult builderResult = result.getChildren()[0];
        
        // test
        assertEquals(ExecutionResult.ExecutionState.SUCCESS, builderResult.getState());
        
        // get resolved type for security configuration
        ResolvedType securityConfigResolvedType = resolvedType.getChildByPrimaryId("SecurityConfiguration");

        // test
        assertNotNull(securityConfigResolvedType);
        assertTrue(securityConfigResolvedType.getSecondaryParticiant().isResolutionSuccesful());        
                
        // get resolved type for realm container 
        ResolvedType realmCollectionResolvedType = securityConfigResolvedType.getChildByPrimaryId("Realm");

        // test
        assertNotNull(realmCollectionResolvedType );
        assertTrue(realmCollectionResolvedType.getSecondaryParticiant().isResolutionSuccesful());
        
        // get resolved type for "myrealm" realm
        ResolvedType realmResolvedType = realmCollectionResolvedType.getChildByPrimaryId("myrealm");
        
        // test
        assertNotNull(realmResolvedType);
        assertTrue(realmResolvedType.getSecondaryParticiant().isResolutionSuccesful());                        
    }    
    
    /**
     * Test that builder can be invoked a domain, wit a unnamed authentication provider.
     * 
     * @throws Exception If test fails. 
     */
    @Test
    public void testCanExecuteWithDomainWithUnnamedAuthenticationProvider() throws Exception
    {
        // create content
        DomainDocument domainDoc = contentMother.createMinimalDomain( WJPIntTestConstants.targetEnvironment );
        
        // build domain
        SecurityConfigurationType securityConfiguration = domainDoc.getDomain().addNewSecurityConfiguration();
        RealmType realm = securityConfiguration.addNewRealm();
        realm.setName("myrealm");            
        AuthenticationProviderType authenticationProvider = realm.addNewAuthenticationProvider();                        
        
        // create resolved type at the root of the models
        ResolvedParticipant primary = contentMother.createDomainResolvedParticipant(domainDoc);            
        ResolvedParticipant secondary = jmxMother.createDomainObjName_ResolvedParticipant(session);
        ResolvedType parent = null;
        resolvedType = ResolvedTypeImpl.createResolvedObject(parent, primary, secondary);
                                                        
        // build
        deployDirector.startTraversal(session, resolvedType, result);
        
        // result from builder
        ExecutionResult builderResult = result.getChildren()[0];
        
        // test
        assertEquals(ExecutionResult.ExecutionState.SUCCESS, builderResult.getState());
        
        // get resolved type for security configuration
        ResolvedType securityConfigResolvedType = resolvedType.getChildByPrimaryId("SecurityConfiguration");

        // test
        assertNotNull(securityConfigResolvedType);
        assertTrue(securityConfigResolvedType.getSecondaryParticiant().isResolutionSuccesful());        
                
        // get resolved type for realm container 
        ResolvedType realmCollectionResolvedType = securityConfigResolvedType.getChildByPrimaryId("Realm");

        // test
        assertNotNull(realmCollectionResolvedType );
        assertTrue(realmCollectionResolvedType.getSecondaryParticiant().isResolutionSuccesful());
        
        // get resolved type for "myrealm" realm
        ResolvedType realmResolvedType = realmCollectionResolvedType.getChildByPrimaryId("myrealm");
        
        // test
        assertNotNull(realmResolvedType);
        assertTrue(realmResolvedType.getSecondaryParticiant().isResolutionSuccesful());
        
        // get resolved type for authentication provider
        ResolvedType providerResolvedType = realmResolvedType.getChildByPrimaryId("AuthenticationProvider");
        
        // test
        assertNotNull(providerResolvedType);
        assertTrue(providerResolvedType.getSecondaryParticiant().isResolutionSuccesful());                                
    }        

    /**
     * Test that builder can be invoked a domain, wit a unnamed role mapper.
     * 
     * @throws Exception If test fails. 
     */
    @Test
    public void testCanExecuteWithDomainWithUnnamedRoleMapper() throws Exception
    {
        // create content
        DomainDocument domainDoc = contentMother.createMinimalDomain( WJPIntTestConstants.targetEnvironment );
        
        // build domain
        SecurityConfigurationType securityConfiguration = domainDoc.getDomain().addNewSecurityConfiguration();
        securityConfiguration.setName(WJPIntTestConstants.targetEnvironment );
        RealmType realm = securityConfiguration.addNewRealm();
        realm.setName("myrealm");        
        RoleMapperType roleMapper = realm.addNewRoleMapper();                        
        
        // create resolved type at the root of the models
        ResolvedParticipant primary = contentMother.createDomainResolvedParticipant(domainDoc);   
        ResolvedParticipant secondary = jmxMother.createDomainObjName_ResolvedParticipant(session);
        ResolvedType parent = null;
        resolvedType = ResolvedTypeImpl.createResolvedObject(parent, primary, secondary);
                                                        
        // build
        deployDirector.startTraversal(session, resolvedType, result);
        
        // result from builder
        ExecutionResult builderResult = result.getChildren()[0];
        
        // test
        assertEquals(ExecutionResult.ExecutionState.SUCCESS, builderResult.getState());
        
        // get resolved type for security configuration
        ResolvedType securityConfigResolvedType = resolvedType.getChildByPrimaryId("SecurityConfiguration");

        // test
        assertNotNull(securityConfigResolvedType);
        assertTrue(securityConfigResolvedType.getSecondaryParticiant().isResolutionSuccesful());        
                
        // get resolved type for realm container 
        ResolvedType realmCollectionResolvedType = securityConfigResolvedType.getChildByPrimaryId("Realm");

        // test
        assertNotNull(realmCollectionResolvedType );
        assertTrue(realmCollectionResolvedType.getSecondaryParticiant().isResolutionSuccesful());
        
        // get resolved type for "myrealm" realm
        ResolvedType realmResolvedType = realmCollectionResolvedType.getChildByPrimaryId("myrealm");
        
        // test
        assertNotNull(realmResolvedType);
        assertTrue(realmResolvedType.getSecondaryParticiant().isResolutionSuccesful());
        
        // get resolved type for role mapper
        ResolvedType mapperResolvedType = realmResolvedType.getChildByPrimaryId("RoleMapper");
        
        // test
        assertNotNull(mapperResolvedType);
        assertTrue(mapperResolvedType.getSecondaryParticiant().isResolutionSuccesful());                        
        
    }        

    /**
     * Test that builder can be invoked a domain, with a named security configuration.
     * 
     * @throws Exception If test fails. 
     */
    @Test
    public void testCanExecuteWithDomainWithNamedSecurityConfiguration() throws Exception
    {
        // create content
        DomainDocument domainDoc = contentMother.createMinimalDomain( WJPIntTestConstants.targetEnvironment );
        
        // set attribute
        SecurityConfigurationType securityConfiguration = domainDoc.getDomain().addNewSecurityConfiguration();
        securityConfiguration.setName(WJPIntTestConstants.targetEnvironment );
                    
        // create resolved type at the root of the models
        ResolvedParticipant primary = contentMother.createDomainResolvedParticipant(domainDoc);            
        ResolvedParticipant secondary = jmxMother.createDomainObjName_ResolvedParticipant(session);
        ResolvedType parent = null;
        resolvedType = ResolvedTypeImpl.createResolvedObject(parent, primary, secondary);
                                                        
        // build        
        deployDirector.startTraversal(session, resolvedType, result);
                
        // result from builder
        ExecutionResult builderResult = result.getChildren()[0];
        
        // test
        assertEquals(ExecutionResult.ExecutionState.SUCCESS, builderResult.getState());
                
        // get resolved type for security configuration
        ResolvedType securityConfigResolvedType = resolvedType.getChildByPrimaryId("SecurityConfiguration");

        // test
        assertNotNull(securityConfigResolvedType);
        assertTrue(securityConfigResolvedType.getSecondaryParticiant().isResolutionSuccesful());        
    }    
    
}


