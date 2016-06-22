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

import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.EDIT_SERVER_JNDI_NAME;
import static com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory.JmxProtool.HTTP;
import static com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory.JmxProtool.IIOP;
import static com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory.JmxProtool.JDK_IIOP;
import static com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory.JmxProtool.RMI;
import static com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory.JmxProtool.T3;
import static com.alpha.testutils.WJPIntTestConstants.MESSAGEPROVIDER_BEAN_ID;
import static com.alpha.testutils.WJPIntTestConstants.METADATAREPOSITOY_BEAN_ID;
import static com.alpha.testutils.WJPIntTestConstants.SERVICEURLFACTORYFACTORY_BEAN_ID;
import static com.alpha.testutils.WJPIntTestConstants.passwordWeblogic;
import static com.alpha.testutils.WJPIntTestConstants.userWeblogic;
import static com.alpha.testutils.WJPIntTestConstants.weblogicAdmPort;
import static com.alpha.testutils.WJPIntTestConstants.weblogicHostname;
import static com.alpha.testutils.WJPIntTestConstants.weblogicJmxPort;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.metadata.MetadataRepository;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSessionImpl;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXRuntimeSession;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXRuntimeSessionImpl;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory.JmxProtool;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactoryFactory;
import com.alpha.springutils.SpringBeansUtils;


/**
 * Implementation of the ObjectMother pattern, 
 * provides helper functions for unit testing WebLogic JMX sessions.
 */
public class ObjectMotherWeblogicJmxSession {
	
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );    

    /**
     * Spring application context, used to lookup the Spring initialized beans.
     */
	ApplicationContext context;

    /**
     * Message provider for I18N support.
     */
    MessageProvider messageProvider;        
        
    /**
     * MBean meta data repository.
     */
    MetadataRepository mbeanMetadataRepository;        

    /**
     * JMX Service URL factory factory.
     */
    JmxServiceUrlFactoryFactory jmxServiceUrlFactoryFactory;        
    
	/**
	 * ObjectMotherWeblogicJmxSession constructor.
	 */
	public ObjectMotherWeblogicJmxSession() {
		
		// get application context
		context = SpringBeansUtils.getApplicationContext(WJPIntTestConstants.JMX_SPRING_CONFIG);
		
		// get beans
		messageProvider = (MessageProvider) SpringBeansUtils.getBean(context, MESSAGEPROVIDER_BEAN_ID);
		mbeanMetadataRepository = (MetadataRepository) SpringBeansUtils.getBean(context, METADATAREPOSITOY_BEAN_ID);
		jmxServiceUrlFactoryFactory = (JmxServiceUrlFactoryFactory) SpringBeansUtils.getBean(context, SERVICEURLFACTORYFACTORY_BEAN_ID);		
	}


    /**
     * Create connected WebLogic JMX Edit session. 
     * 
     * @return connected WebLogic JMX Edit session.
     */
	public WeblogicJMXEditSession createConnectedWlsJmxEditSession() {

		// declare session
        WeblogicJMXEditSession session;
        
        try
        {
            // create session
            session = new WeblogicJMXEditSessionImpl();
            
            // inject beans
            ReflectionTestUtils.setField( session, "messageProvider", messageProvider);    
            ReflectionTestUtils.setField( session, "mbeanMetadataRepository", mbeanMetadataRepository );        
            ReflectionTestUtils.setField( session, "jmxServiceUrlFactoryFactory", jmxServiceUrlFactoryFactory );        
            
            // connect
            session.connect(userWeblogic, passwordWeblogic, weblogicAdmPort, weblogicHostname, EDIT_SERVER_JNDI_NAME );         
            return session;
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
            return null;
        }		
	}

    /**
     * Create connected WebLogic JMX Edit session
     * 
     * @param protocol protocol used to establish connection.
     * @param port TCP port where connection is established.    
     * 
     * @return connected WebLogic JMX Edit session.
     */
	WeblogicJMXEditSession createConnectedWlsJmxEditSession(JmxProtool protocol, int port) {
		
        // declare session
        WeblogicJMXEditSession session;
        
        try
        {
            // create session
            session = new WeblogicJMXEditSessionImpl();
            
            // inject beans
            ReflectionTestUtils.setField( session, "messageProvider", messageProvider);    
            ReflectionTestUtils.setField( session, "mbeanMetadataRepository", mbeanMetadataRepository );        
            ReflectionTestUtils.setField( session, "jmxServiceUrlFactoryFactory", jmxServiceUrlFactoryFactory );        
            
            // connect
            session.connect(protocol.toString(), userWeblogic, passwordWeblogic, port, weblogicHostname, EDIT_SERVER_JNDI_NAME );         
            return session;
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
            return null;
        }		
	}
	
		
    /**
     * Create connected WebLogic JMX Edit session 
     * on the administration server listen port 
     * using WebLogic T3 protocol.
     * 
     * @return connected WebLogic JMX Edit session.
     */
	public WeblogicJMXEditSession createConnectedWlsJmxEditSessionUsingWlsT3Protocol() {
		return createConnectedWlsJmxEditSession(T3, weblogicAdmPort);
	}

    /**
     * Create connected WebLogic JMX Edit session 
     * on the administration server listen port 
     * using WebLogic HTTP protocol.
     * 
     * @return connected WebLogic JMX Edit session.
     */
	public WeblogicJMXEditSession createConnectedWlsJmxEditSessionUsingWlsHttpProtocol() {
		return createConnectedWlsJmxEditSession(HTTP, weblogicAdmPort);	
	}

    /**
     * Create connected WebLogic JMX Edit session 
     * on the administration server listen port 
     * using WebLogic IIOP protocol.
     * 
     * @return connected WebLogic JMX Edit session.
     */
	public WeblogicJMXEditSession createConnectedWlsJmxEditSessionUsingWlsIiopProtocol() {
		return createConnectedWlsJmxEditSession(IIOP, weblogicAdmPort);	
	}

    /**
     * Create connected WebLogic JMX Edit session 
     * on the administration server listen port 
     * using WebLogic RMI protocol.
     * 
     * @return connected WebLogic JMX Edit session.
     */
	public WeblogicJMXEditSession createConnectedWlsJmxEditSessionUsingWlsRmiProtocol() {
		return createConnectedWlsJmxEditSession(RMI, weblogicAdmPort);	
	}

    /**
     * Create connected WebLogic JMX Edit session 
     * on the administration server listen port 
     * using the JDK IIOP protocol.
     * 
     * @return connected WebLogic JMX Edit session.
     */
	public WeblogicJMXEditSession createConnectedWlsJmxEditSessionUsingJdkIiopProtocol() {
		return createConnectedWlsJmxEditSession(JDK_IIOP, weblogicAdmPort);	
	}

    /**
     * Create connected WebLogic JMX Edit session 
     * on the JMX connector listen port 
     * using the JDK RMI protocol.
     * 
     * @return connected WebLogic JMX Edit session.
     */
	public WeblogicJMXEditSession createConnectedWlsJmxEditSessionUsingJdkRmiProtocol() {
		return createConnectedWlsJmxEditSession(JmxProtool.JDK_RMI, weblogicJmxPort);	
	}
	
    /**
     * Create connected WebLogic JMX Runtime session. 
     * 
     * @return connected WebLogic JMX Runtime session.
     */	
	public WeblogicJMXRuntimeSession createConnectedWlsJmxRuntimeSession() {

        // declare session
        WeblogicJMXRuntimeSession session;
        
        try
        {
            // create session
            session = new WeblogicJMXRuntimeSessionImpl();
            
            // inject beans
            ReflectionTestUtils.setField( session, "messageProvider", messageProvider);    
            ReflectionTestUtils.setField( session, "mbeanMetadataRepository", mbeanMetadataRepository );        
            ReflectionTestUtils.setField( session, "jmxServiceUrlFactoryFactory", jmxServiceUrlFactoryFactory );        
            
            // connect
            session.connect(userWeblogic, passwordWeblogic, weblogicAdmPort, weblogicHostname, EDIT_SERVER_JNDI_NAME );         
            return session;
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
            return null;
        }		
	}
	
	
}
