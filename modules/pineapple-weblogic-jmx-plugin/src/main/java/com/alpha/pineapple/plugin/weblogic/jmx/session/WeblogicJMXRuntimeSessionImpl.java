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


package com.alpha.pineapple.plugin.weblogic.jmx.session;

import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.DEFAULT_JMX_PROTOCOL;

import java.io.IOException;
import java.util.Hashtable;

import javax.annotation.Resource;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.metadata.MetadataRepository;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory.JmxProtool;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactoryFactory;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;
import com.alpha.pineapple.session.SessionException;

/**
 * Implementation of the WeblogicJMXRuntimeSession interface.
 */
public class WeblogicJMXRuntimeSessionImpl implements WeblogicJMXRuntimeSession
{

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
    
    /**
     * MBean meta data repository.
     */
    @Resource
    MetadataRepository mbeanMetadataRepository;        

    /**
     * JMX Service URL factory factory.
     */
    @Resource
    JmxServiceUrlFactoryFactory jmxServiceUrlFactoryFactory;        
    
    /**
     * Resource.
     */
    com.alpha.pineapple.model.configuration.Resource resource;

    /**
     * Resource credential.
     */
    Credential credential;
    
    /**
     * MBeanServer connection.
     */
    MBeanServerConnection connection;

    /**
     * JMX server connector.
     */
    JMXConnector connector;

    
    public void connect( com.alpha.pineapple.model.configuration.Resource resource, Credential credential ) throws SessionConnectException {
        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { resource, credential.getId() };    	        	
            String message = messageProvider.getMessage("wjrs.connect_start", args );
            logger.debug( message );            
    	}
        
	    // store in fields
	    this.credential = credential;
	    this.resource = resource;
        
        try
        {            
            // create resource property getter
            ResourcePropertyGetter getter = new ResourcePropertyGetter( resource );
            
            // get resource attributes            
            String host = getter.getProperty( "host" );
            int port = Integer.parseInt( getter.getProperty( "port" ) );
            String urlPath = getter.getProperty( "url-path" );
			String protocol = getter.getProperty( "protocol", DEFAULT_JMX_PROTOCOL );
            
            // get credential attributes
            String password = credential.getPassword();
            String user = credential.getUser();

            // do connect            
            connect( user, password, port, host, urlPath );
        }
        catch ( SessionConnectException e )
        {
        	// rethrow session exception        	
        	throw e;
        }        
        catch ( Exception e )
        {
            Object[] args = { resource.getId(), e };    	        	
            String message = messageProvider.getMessage("wjrs.connect_failure", args );
            throw new SessionConnectException( message, e );            
        }

        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { resource };    	        	
            String message = messageProvider.getMessage("wjrs.connect_completed", args );
            logger.debug( message );            
    	}                
    }

    public void connect( String user, String password, int port, String host, String urlPath ) throws SessionConnectException {
        connect( DEFAULT_JMX_PROTOCOL, user, password, port, host, urlPath );    	
    }

    public void connect( String protocol, String user, String password, int port, String host, String urlPath ) throws SessionConnectException {
        try
        {
            // log debug message
        	if(logger.isDebugEnabled()) {
                Object[] args = { user, host, port, urlPath };    	        	
                String message = messageProvider.getMessage("wjrs.connect_start2", args );
                logger.debug( message );            
        	}

    		// resolve service URL factory
    		JmxProtool protocolAsEnum = JmxProtool.valueOf(protocol.toUpperCase());
    		JmxServiceUrlFactory jmxServiceUrlFactory = jmxServiceUrlFactoryFactory.getInstance(protocolAsEnum);

            // create service URL and connection properties 
            JMXServiceURL serviceURL = jmxServiceUrlFactory.createEditMBeanServerUrl(protocolAsEnum, host, port); 
            Hashtable<String, String> props = jmxServiceUrlFactory.createConnectionProperties(user, password);

            // log debug message
        	if(logger.isDebugEnabled()) {
                Object[] args = { serviceURL };    	        	
                String message = messageProvider.getMessage("wjrs.service_url_info", args );
                logger.debug( message );            
        	}
            
            // connect
            connector = JMXConnectorFactory.connect( serviceURL, props );
            connection = connector.getMBeanServerConnection();

            // log debug message
        	if(logger.isDebugEnabled()) {
                Object[] args = { serviceURL };    	        	
                String message = messageProvider.getMessage("wjrs.connect_completed2", args );
                logger.debug( message );            
        	}

        }
        catch ( Exception e )
        {
            Object[] args = { user, e };    	        	
            String message = messageProvider.getMessage("wjrs.connect_failure2", args );
            throw new SessionConnectException( message, e );            
        }
    	
    }
    
    

	public void connect(MBeanServerConnection connection) {
		this.connection = connection;		
	}

	
	public void disconnect() throws SessionDisconnectException
    {
        // exit if not connected.
        if ( !isConnected() ) {
            logger.error( "disconnect() invoked without being connected." );
            return;
        }

        try
        {
            // dispose of object names

            // close connection
            connector.close();

            // dispose
            connection = null;
            connector = null;
        }
        catch ( IOException e )
        {
            Object[] args = { e };    	        	
            String message = messageProvider.getMessage("wjrs.disconnect_failure", args );
            throw new SessionDisconnectException( message, e );            
        }
    }
    
	public com.alpha.pineapple.model.configuration.Resource getResource() {
		return this.resource;
	}

	public Credential getCredential() {
		return this.credential;
	}
    
    public MBeanServerConnection getMBeanServerConnection()
    {

        return this.connection;
    }

    public boolean isConnected()
    {

        return ( connection != null );
    }

    public boolean isSessionConfiguredForEditing()
    {

        return true;
    }

    /**
     * Factory method.
     * 
     * @return new WeblogicJMXEditSession object.
     */
    @Deprecated
    public static WeblogicJMXRuntimeSession getInstance()
    {
        return new WeblogicJMXRuntimeSessionImpl();
    }

}
