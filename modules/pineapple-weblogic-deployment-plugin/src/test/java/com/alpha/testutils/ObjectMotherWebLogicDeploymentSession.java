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


package com.alpha.testutils;

import static com.alpha.testutils.WeblogicDeploymentPluginTestConstants.MESSAGEPROVIDER_BEAN_ID;
import static com.alpha.testutils.WeblogicDeploymentPluginTestConstants.RESOURCE_PROPERTY_GETTER_BEAN_ID;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.plugin.weblogic.deployment.session.WeblogicDeploymentSession;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionException;
import com.alpha.springutils.SpringBeansUtils;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for 
 * integration test of the class {@link WeblogicDeploymentSessionImpl}.
 */
@Deprecated
public class ObjectMotherWebLogicDeploymentSession
{

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );    

    /**
     * Message provider for I18N support.
     */
    MessageProvider messageProvider;        
    
    /**
     * Resource getter object.
     */
    ResourcePropertyGetter propertyGetter;    
    
    /**
     * Object mother Resource.
     */
    ObjectMotherResource resourceMother;

    /**
     * Object mother for credentials.
     */
    ObjectMotherCredentialsForWebLogicDeployment credentialsMother;

    /**
     * Spring application context.
     * Used to inject dependencies in created sessions.  
     */
	ApplicationContext applicationContext;
           
	
	
    /**
     * ObjectMotherWebLogicDeploymentSession constructor.
     * 
     * @param applicationContext Spring application context.
     */
    public ObjectMotherWebLogicDeploymentSession(ApplicationContext applicationContext) {
    	this.applicationContext = applicationContext;
    	
        // create object mother
        resourceMother = new ObjectMotherResource();

        // create credentials object mother
        credentialsMother = new ObjectMotherCredentialsForWebLogicDeployment();    	
	}

	/**
     * Create mock weblogic.Deployer instance.
     * 
     * @return mock weblogic.Deployer instance.
     */
    public MockWebLogicDeployer createMockDeployer() {
        try
        {
            return new MockWebLogicDeployer();
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
            return null;
        }
    }    
    
    /**
     * Create unconnected WebLogic deployment session.
     * 
     * @return unconnected WebLogic deployment session.
     */
    public WeblogicDeploymentSession createUnconnectedWebLogicDeploymentSession() {

        // create session
        WeblogicDeploymentSessionImpl session = new WeblogicDeploymentSessionImpl();
    	
		// get message provider
		messageProvider = (MessageProvider) SpringBeansUtils.getBean(applicationContext, MESSAGEPROVIDER_BEAN_ID);
    	
        // inject 
        ReflectionTestUtils.setField( session, "messageProvider", messageProvider );    	

		// get resource property getter
        propertyGetter = (ResourcePropertyGetter) SpringBeansUtils.getBean(applicationContext, RESOURCE_PROPERTY_GETTER_BEAN_ID);
    	
        // inject 
        ReflectionTestUtils.setField( session, "propertyGetter", propertyGetter );    	
        
        return session;
    }
    
    /**
     * Create WebLogic deployment session which is connected to local WebLogic.
     * 
     * @return WebLogic deployment session which is connected to local WebLogic.
     * @throws SessionException If test fails.
     */
    public Session createConnectedWebLogicDeploymentSession() throws SessionException {
       
        // create session        
        Session session = createUnconnectedWebLogicDeploymentSession();
        
        // setup parameters
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put( WeblogicDeploymentSession.LISTENADDRESS_PROPERTY, WeblogicDeploymentPluginTestConstants.weblogicHostname );            
        properties.put( WeblogicDeploymentSession.LISTENPORT_PROPERTY, Integer.toString( WeblogicDeploymentPluginTestConstants.weblogicPort ));            
        properties.put( WeblogicDeploymentSession.PROTOCOL_PROPERTY, WeblogicDeploymentPluginTestConstants.weblogicProtocol );            
        Resource resource = resourceMother.createResourceWithProperties( properties );            
        Credential credential = credentialsMother.createWebLogicDeploymentCredential();

        // test            
        session.connect( resource, credential );
        return session;            
    }    
}
