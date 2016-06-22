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


package com.alpha.pineapple.plugin.weblogic.deployment.session;

import static org.junit.Assert.assertEquals;

import static org.easymock.classextension.EasyMock.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import weblogic.Deployer;

import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherCredentialsForWebLogicDeployment;
import com.alpha.testutils.ObjectMotherResource;
import com.alpha.testutils.ObjectMotherWebLogicDeploymentSession;
import com.alpha.testutils.WeblogicDeploymentPluginTestConstants;

public class WeblogicDeploymentSessionImplTest
{
    
    /**
     * Deployer test argument
     */
    static final String TEST_OPERATION = "TEST-OPERATION";

    /**
     * Deployer test argument
     */    
    static final String TEST_OPERATION2 = "TEST-OPERATION2";

    
    /**
     * Object under test.
     */
    WeblogicDeploymentSession session;

    /**
     * Object mother Resource.
     */
    ObjectMotherResource resourceMother;

    /**
     * Object mother for credentials.
     */
    ObjectMotherCredentialsForWebLogicDeployment credentialsMother;
    
    /**
     * Object mother for session.
     */
    ObjectMotherWebLogicDeploymentSession sessionMother;

    /**
     * Weblogic.deployer object.
     */
    Deployer deployer;    
    
    @Before
    public void setUp() throws Exception
    {    	
        // create object mother
        resourceMother = new ObjectMotherResource();

        // create credentials object mother
        credentialsMother = new ObjectMotherCredentialsForWebLogicDeployment();

        // create session mother
        sessionMother = new ObjectMotherWebLogicDeploymentSession();

        // create session
        session = sessionMother.createUnconnectedWebLogicDeploymentSession();
        
        // create mock deployer
        deployer = createMock( Deployer.class);
        
        // inject asserter
        ReflectionTestUtils.setField( session, "deployer", deployer);
    }

    @After
    public void tearDown() throws Exception
    {
        deployer = null;
        sessionMother = null;
        credentialsMother = null;
        resourceMother = null;
        session = null;
    }
            
    
    /**
     * Rejects undefined resource.
     * 
     * @throws Exception if test fails. 
     */
    @Test( expected = IllegalArgumentException.class )
    public void testConnectRejectsUndefinedResource() throws Exception 
    {
        // setup parameters
        Resource resource = null;
        Credential credential = credentialsMother.createWebLogicDeploymentCredential();

        // test
        session.connect( resource, credential );
    }

    /**
     * Rejects undefined credential.
     * @throws Exception if test fails. 
 
     */
    @Test( expected = IllegalArgumentException.class )
    public void testConnectRejectsUndefinedCredential() throws Exception
    {
        // setup parameters
        Resource resource = resourceMother.createResourceWithNoProperties();            
        Credential credential = null;

        // test
        session.connect( resource, credential );
    }

    /**
     * Rejects undefined arguments lists.
     * @throws Exception if test fails.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testInvokeRejectsUndefinedArguments() throws Exception
    {
        // setup parameters
        Resource resource = resourceMother.createResourceWithNoProperties();            
        Credential credential = credentialsMother.createWebLogicDeploymentCredential();

        // connect
        session.connect( resource, credential );
        
        // test 
        session.invokeDeployer( null );
    }
    

    /**
     * invoke will fail if session isn't connected.
     * @throws Exception if test fails.
     */
    @Test( expected = IllegalStateException.class )
    public void testInvokeFailsIfNotConnected() throws Exception
    {
        final String OPERATION_ARG1 = "OP1";
        
        // setup parameters
        List<String> argumentsList = new ArrayList<String>();            
        argumentsList.add( OPERATION_ARG1 );
        
        // - skip connect
        
        // test 
        session.invokeDeployer( argumentsList );                 
    }

    
    /**
     * Can execute connect.
     * @throws Exception if test fails.
     */
    @Test
    public void testCanConnect() throws Exception
    {
        // setup parameters
        Resource resource = resourceMother.createResourceWithNoProperties();            
        Credential credential = credentialsMother.createWebLogicDeploymentCredential();

        // test
        session.connect( resource, credential );
    }
    
    /**
     * Can execute disconnect, after a connect.
     * @throws Exception if test fails.
     */    
    @Test
    public void testCanDisconnectAfterConnect() throws Exception
    {
        // setup parameters
        Resource resource = resourceMother.createResourceWithNoProperties();            
        Credential credential = credentialsMother.createWebLogicDeploymentCredential();

        // connect
        session.connect( resource, credential );
        
        // test
        session.disconnect();
    }

    /**
     * Can execute disconnect, without an initial connect.
     * @throws Exception if test fails.
     */    
    @Test
    public void testCanDisconnectWithoutConnect() throws Exception
    {
        // skip connect
        
        // test
        session.disconnect();           
    }

    /**
     * Test that deployer is invoked with correct connection
     * arguments from the session object.
     *   
     * @throws Exception if test fails.
     */
    @Test
    public void testInvokedWithCorrectConnectionArgument() throws Exception
    {
        // create session with mock deployer
        deployer = sessionMother.createMockDeployer();
        session =  sessionMother.createConnectedWebLogicDeploymentSession( deployer );
        
        // setup parameters
        List<String> argumentsList = new ArrayList<String>();                        
        argumentsList.add( TEST_OPERATION );
         
        // invoke deployer
        session.invokeDeployer( argumentsList );
        
        // test
        assertEquals( "-adminurl", deployer.arguments[0] );
    }

    
    /**
     * Test that deployer is invoked with correct connection 
     * value from the session object.
     *   
     * @throws Exception if test fails.
     */
    @Test
    public void testInvokedWithCorrectConnectionValue() throws Exception
    {
        // create session with mock deployer
        deployer = sessionMother.createMockDeployer();
        session =  sessionMother.createConnectedWebLogicDeploymentSession( deployer );
        
        // setup parameters
        List<String> argumentsList = new ArrayList<String>();                        
        argumentsList.add( TEST_OPERATION );
         
        // invoke deployer
        session.invokeDeployer( argumentsList );
        
        // test - second argument is expected to be Protocol://Host:Port
        StringBuilder message = new StringBuilder();
        message.append( WeblogicDeploymentPluginTestConstants.weblogicProtocol );
        message.append( "://" );
        message.append( WeblogicDeploymentPluginTestConstants.weblogicHostname  );
        message.append( ":" );
        message.append( WeblogicDeploymentPluginTestConstants.weblogicPort );
        assertEquals( message.toString(), deployer.arguments[1] );                                    
    }
    
    
    /**
     * Test that deployer is invoked with correct credential
     * arguments from the session object.  
     * @throws Exception if test fails.
     */
    @Test
    public void testInvokedWithCorrectCredentialArgumentUser() throws Exception
    {
        // create session with mock deployer
        deployer = sessionMother.createMockDeployer();
        session =  sessionMother.createConnectedWebLogicDeploymentSession( deployer );
        
        // setup parameters
        List<String> argumentsList = new ArrayList<String>();                        
        argumentsList.add( TEST_OPERATION );
         
        // invoke deployer
        session.invokeDeployer( argumentsList );
        
        // test
        assertEquals( "-username", deployer.arguments[2] );
    }

    /**
     * Test that deployer is invoked with correct credential
     * arguments from the session object.
     *   
     * @throws Exception if test fails.
     */
    @Test
    public void testInvokedWithCorrectCredentialValueUser() throws Exception
    {
        // create session with mock deployer
        deployer = sessionMother.createMockDeployer();
        session =  sessionMother.createConnectedWebLogicDeploymentSession( deployer );
        
        // setup parameters
        List<String> argumentsList = new ArrayList<String>();                        
        argumentsList.add( TEST_OPERATION );
         
        // invoke deployer
        session.invokeDeployer( argumentsList );
        
        // test
        assertEquals( WeblogicDeploymentPluginTestConstants.userWeblogic, deployer.arguments[3] );
    }
    
    /**
     * Test that deployer is invoked with correct credential
     * arguments from the session object.
     *   
     * @throws Exception if test fails. 
     */
    @Test
    public void testInvokedWithCorrectCredentialArgumentPassword() throws Exception
    {
        // create session with mock deployer
        deployer = sessionMother.createMockDeployer();
        session =  sessionMother.createConnectedWebLogicDeploymentSession( deployer );
        
        // setup parameters
        List<String> argumentsList = new ArrayList<String>();                        
        argumentsList.add( TEST_OPERATION );
         
        // invoke deployer
        session.invokeDeployer( argumentsList );
        
        // test
        assertEquals( "-password", deployer.arguments[4] );
    }
    
    /**
     * Test that deployer is invoked with correct credential
     * arguments from the session object.
     *   
     * @throws Exception if test fails.
     */
    @Test
    public void testInvokedWithCorrectCredentialValuePassword() throws Exception
    {
        // create session with mock deployer
        deployer = sessionMother.createMockDeployer();
        session =  sessionMother.createConnectedWebLogicDeploymentSession( deployer );
        
        // setup parameters
        List<String> argumentsList = new ArrayList<String>();                        
        argumentsList.add( TEST_OPERATION );
         
        // invoke deployer
        session.invokeDeployer( argumentsList );
        
        // test
        assertEquals( WeblogicDeploymentPluginTestConstants.passwordWeblogic, deployer.arguments[5] );
    }

    /**
     * Test that deployer is invoked with correct common
     * arguments from the session object.
     *   
     * @throws Exception if test fails.
     */
    @Test
    public void testInvokedWithCorrectCommonArguments() throws Exception
    {
        // create session with mock deployer
        deployer = sessionMother.createMockDeployer();
        session =  sessionMother.createConnectedWebLogicDeploymentSession( deployer );
        
        // setup parameters
        List<String> argumentsList = new ArrayList<String>();                        
        argumentsList.add( TEST_OPERATION );
         
        // invoke deployer
        session.invokeDeployer( argumentsList );
        
        // test
        assertEquals( "-debug", deployer.arguments[7] );
        assertEquals( "-noexit", deployer.arguments[8] );
        assertEquals( "-remote", deployer.arguments[9] );            
        assertEquals( "-verbose", deployer.arguments[10] );            
    }

    /**
     * Test that deployer is invoked with test operation 
     * in the correct place.
     *   
     * @throws Exception if test fails.
     */
    @Test
    public void testInvokedWithCorrectlyPlacedVisitorArgument() throws Exception
    {
        // create session with mock deployer
        deployer = sessionMother.createMockDeployer();
        session =  sessionMother.createConnectedWebLogicDeploymentSession( deployer );
        
        // setup parameters
        List<String> argumentsList = new ArrayList<String>();                        
        argumentsList.add( TEST_OPERATION );
         
        // invoke deployer
        session.invokeDeployer( argumentsList );
        
        // test
        assertEquals( TEST_OPERATION, deployer.arguments[6] );
    }

    /**
     * Test that multiple visitor arguments are added to the
     * deployer arguments correctly.
     *   
     * @throws Exception if test fails.
     */
    @Test
    public void testMultipleVisitorArgumentsAreAddedToDeployerArgumentsCorrectly() throws Exception
    {
        // create session with mock deployer
        deployer = sessionMother.createMockDeployer();
        session =  sessionMother.createConnectedWebLogicDeploymentSession( deployer );
        
        // setup parameters
        List<String> argumentsList = new ArrayList<String>();                        
        argumentsList.add( TEST_OPERATION );
        argumentsList.add( TEST_OPERATION2 );
        
         
        // invoke deployer
        session.invokeDeployer( argumentsList );
        
        // test
        assertEquals( TEST_OPERATION, deployer.arguments[6] );
        assertEquals( TEST_OPERATION2, deployer.arguments[7] );            
    }

    /**
     * Test that time stamp value is false if the property isn't defined in 
     * resource definition. 
     * @throws Exception if test fails. 
     */
    @Test
    public void testTimeStampValueIsFalseIfPropertyIsUndefined() throws Exception
    {
        // setup parameters
        Resource resource = resourceMother.createResourceWithNoProperties();            
        Credential credential = credentialsMother.createWebLogicDeploymentCredential();

        // connect
        session.connect( resource, credential );
        
        // get time stamp value
        boolean actual = session.isTimeStampEnabled();
        
        // test
        assertEquals( false, actual );
    }

    /**
     * Test that time stamp value is false is property values is false in 
     * resource definition.
     *  
     * @throws Exception if test fails.
     */
    @Test
    public void testTimeStampValueIsFalseIfPropertyValueIsFalse() throws Exception
    {
        final String PROPERTY_VALUE = "false";
        
        // setup properties
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put( WeblogicDeploymentSession.TIMESTAMP_PROPERTY, PROPERTY_VALUE );
        
        // setup parameters
        Resource resource = resourceMother.createResourceWithProperties( properties );            
        Credential credential = credentialsMother.createWebLogicDeploymentCredential();

        // connect
        session.connect( resource, credential );
        
        // get time stamp value
        boolean actual = session.isTimeStampEnabled();
        
        // test
        assertEquals( false, actual );
    }

    /**
     * Test that time stamp value is false is property values is nonsese in 
     * resource definition.
     *  
     * @throws Exception if test fails.
     */
    @Test
    public void testTimeStampValueIsFalseIfPropertyValueIsNonsense() throws Exception
    {
        final String PROPERTY_VALUE = "nonsense-value";
        
        // setup properties
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put( WeblogicDeploymentSession.TIMESTAMP_PROPERTY, PROPERTY_VALUE );
        
        // setup parameters
        Resource resource = resourceMother.createResourceWithProperties( properties );            
        Credential credential = credentialsMother.createWebLogicDeploymentCredential();

        // connect
        session.connect( resource, credential );
        
        // get time stamp value
        boolean actual = session.isTimeStampEnabled();
        
        // test
        assertEquals( false, actual );
    }
    
    /**
     * Test that time stamp value is true is property values is true in 
     * resource definition.
     *  
     * @throws Exception if test fails.
     */
    @Test
    public void testTimeStampValueIsTrueIfPropertyValueIsTrue() throws Exception
    {
        final String PROPERTY_VALUE = "true";
        
        // setup properties
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put( WeblogicDeploymentSession.TIMESTAMP_PROPERTY, PROPERTY_VALUE );
        
        // setup parameters
        Resource resource = resourceMother.createResourceWithProperties( properties );            
        Credential credential = credentialsMother.createWebLogicDeploymentCredential();

        // connect
        session.connect( resource, credential );
        
        // get time stamp value
        boolean actual = session.isTimeStampEnabled();
        
        // test
        assertEquals( true, actual );
    }
    
}
