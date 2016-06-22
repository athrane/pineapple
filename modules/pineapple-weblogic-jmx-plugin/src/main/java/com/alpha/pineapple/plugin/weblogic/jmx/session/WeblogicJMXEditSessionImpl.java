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

import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.CONFIGURATION_MANAGER_ATTRIBUTE;
import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.DEFAULT_JMX_PROTOCOL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import javax.annotation.Resource;
import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.log4j.Logger;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.plugin.PluginSession;
import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;
import com.alpha.pineapple.plugin.weblogic.jmx.management.JMXManagmentException;
import com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.metadata.MetadataRepository;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory.JmxProtool;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactoryFactory;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxUtils;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;
import com.alpha.pineapple.resolvedmodel.validation.ModelValidationFailedException;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;
import com.alpha.pineapple.session.SessionException;

/**
 * Implementation of the WeblogicJMXEditSession interface.
 */
@PluginSession
public class WeblogicJMXEditSessionImpl implements WeblogicJMXEditSession
{

	/**
	 * MBean name attribute ID. 
	 */
	static final String NAME_ATRRIBUTE_ID = "Name";
	
	/**
	 * Method signature for the startEdit() method.
	 */
	static final String[] STARTEDIT_METHOD_SIGNATURE = new String[] { "java.lang.Integer", "java.lang.Integer", "java.lang.Boolean" };
		
	/**
	 * Don't create edit session with exclusion lock.
	 */
	static final boolean NO_EXCLUSIVE_LOCK = false;	

	/**
	 * Edit session wait time out.
	 */
	static final int WAIT_TIMEOUT = 60000;
	
	/**
	 * Edit session time out.
	 */	
	static final int TIME_OUT = 120000;        
    	
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
    
    /**
     * EditServiceMBean object name.
     */
    ObjectName editServiceObjName;

    /**
     * CongfigurationManagerMBean object name.
     */
    ObjectName congfigManagerObjName;
    
    /**
     * DomainMBean object name.
     */
    ObjectName domainObjName;
    
    
    /**
     * Get the object name for the ConfigurationManagerMBean.
     * 
     * @param connection MBean Server connection.
     * 
     * @param editServiceObjectName Object name for the edit service MBean.
     * 
     * @return the object name for the ConfigurationManager MBean.
     * 
     * @throws Exception If object name retrieval fails. 
     */
    ObjectName getConfigurationManagerObjectName( MBeanServerConnection connection,ObjectName editServiceObjectName ) throws Exception
    {
        ObjectName objectName = (ObjectName) connection.getAttribute( editServiceObjectName, CONFIGURATION_MANAGER_ATTRIBUTE );
        return objectName;
    }
        
    public void connect( com.alpha.pineapple.model.configuration.Resource resource, Credential credential ) throws SessionConnectException {
        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { resource, credential.getId() };    	        	
            String message = messageProvider.getMessage("wjes.connect_start", args );
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
            connect( protocol, user, password, port, host, urlPath );
        }
        catch ( SessionConnectException e )
        {
        	// rethrow session exception        	
        	throw e;
        }        
        catch ( Exception e )
        {
            Object[] args = { resource.getId(), e };    	        	
            String message = messageProvider.getMessage("wjes.connect_failure", args );
            throw new SessionConnectException( message, e );            
        }        
        
        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { resource };    	        	
            String message = messageProvider.getMessage("wjes.connect_completed", args );
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
                String message = messageProvider.getMessage("wjes.connect_start2", args );
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
                String message = messageProvider.getMessage("wjes.service_url_info", args );
                logger.debug( message );            
        	}
                        
            // connect
            connector = JMXConnectorFactory.connect( serviceURL, props );
            connection = connector.getMBeanServerConnection();

            // log debug message
        	if(logger.isDebugEnabled()) {
                Object[] args = { serviceURL };    	        	
                String message = messageProvider.getMessage("wjes.connect_completed2", args );
                logger.debug( message );            
        	}
        }
        catch ( Exception e )
        {
            Object[] args = { user, e };    	        	
            String message = messageProvider.getMessage("wjes.connect_failure2", args );
            throw new SessionConnectException( message, e );            
        }
    }

                
    @Override
	public void connect(MBeanServerConnection connection) {
    	this.connection = connection;
	}
    
	public void disconnect() throws SessionDisconnectException {

        // exit if not connected.
        if ( !isConnected() )
        {
            logger.error( "disconnect() invoked without being connected." );
            return;
        }

        try
        {
            // dispose of object names
            domainObjName = null;
            congfigManagerObjName = null;
            editServiceObjName = null;

            // close connection
            connector.close();

            // dispose
            connection = null;
            connector = null;
        }
        catch ( IOException e )
        {
            Object[] args = { e };    	        	
            String message = messageProvider.getMessage("wjes.disconnect_failure", args );
            throw new SessionDisconnectException( message, e );            
        }
    }

	public com.alpha.pineapple.model.configuration.Resource getResource() {
		return this.resource;
	}

	public Credential getCredential() {
		return this.credential;
	}
        
    public boolean isConnected()
    {
        return ( connection != null );
    }

    public void startEdit() throws Exception
    {        	
        // exit if not connected.
        if ( !isConnected() )
        {
            logger.error( "startEdit() invoked without being connected." );
            return;
        }

        // if session is already in edit mode then exit
        if( isEditSessionActive() ) {
        	return;
        }

        // log debug message
    	if(logger.isDebugEnabled()) {    	        	
            String message = messageProvider.getMessage("wjes.startedit_start");
            logger.debug( message );            
    	}
    	
        // get EditServiceMBean object name
        editServiceObjName = JmxUtils.getEditServiceMBeanObjectName();
        
        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { editServiceObjName };    	        	
            String message = messageProvider.getMessage("wjes.connect_editservice", args );
            logger.debug( message );            
    	}

        // get ConfigurationManagerMBean object name
        congfigManagerObjName = getConfigurationManagerObjectName( connection, editServiceObjName );
        
        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { editServiceObjName };    	        	
            String message = messageProvider.getMessage("wjes.connect_configmgr", args );
            logger.debug( message );            
    	}

        // start edit
        // define parameters
        Object[] params = new Object[] { new Integer( WAIT_TIMEOUT ), new Integer( TIME_OUT ), new Boolean(NO_EXCLUSIVE_LOCK) };
        
        // invoke
        domainObjName = (ObjectName) connection.invoke( congfigManagerObjName, WebLogicMBeanConstants.STARTEDIT_METHOD_NAME, params, STARTEDIT_METHOD_SIGNATURE );

        // validate DomainMBean object name is returned, otherwise exception
        if ( domainObjName == null )
        {
            throw new JMXManagmentException( "Failed to obtain edit lock." );
        }

        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { domainObjName };    	        	
            String message = messageProvider.getMessage("wjes.connect_domain", args );
            logger.debug( message );            
    	}
                        
        // log debug message
    	if(logger.isDebugEnabled()) {    	        	
            String message = messageProvider.getMessage("wjes.startedit_completed");
            logger.debug( message );            
    	}
    }

    
    public void saveAndActivate() throws Exception
    {
        // exit if not connected.
        if ( !isConnected() )
        {
            logger.error( "saveAndActivate() invoked without being connected." );
            return;
        }

        // log initial progress
    	if(logger.isDebugEnabled()) {    	        	                
    		logger.debug( "Initiating save & activate." );
    	}

        // save changes
        String operationName = "save";
        Object[] params = null;
        String[] signature = null;
        connection.invoke( congfigManagerObjName, operationName, params, signature );

        // log debug message
    	if(logger.isDebugEnabled()) {    	        	        
    		logger.debug( "saveAndActivate(): changes saved." );
    	}

        // activate changes
        int timeout = 120000;
        operationName = "activate";
        params = new Object[] { new Long( timeout ) };
        signature = new String[] { "java.lang.Long" };
        ObjectName task = (ObjectName) connection.invoke( congfigManagerObjName, operationName, params, signature );

        // log debug message
    	if(logger.isDebugEnabled()) {    	        	        
            logger.debug( "saveAndActivate(): changes activated." );
    	}
                
        // list changes in activation task
        String attributeName = "Changes";
        Object[] changes = (Object[]) connection.getAttribute( task, attributeName );
        
    	if(logger.isDebugEnabled()) {    	        	        
            logger.debug( "changes=" + changes.toString() );
            logger.debug( "changes.length=" + changes.length );
            int size = (int) changes.length;
            for ( int i = 0; i < size; i++ )
            {
                logger.debug( "Changes activated: " + changes[i].toString() );
            }            
    	}

        // clean Domain MBean
        this.domainObjName = null;
        
        // log final progress
    	if(logger.isDebugEnabled()) {    	        	                
    		logger.debug( "Completed save & activate." );
    	}    	
    }
    
    public MBeanServerConnection getMBeanServerConnection() {
        return this.connection;
    }
    

    public ObjectName getDomainMBeanObjectName()
    {
        // exit if not connected
        if ( !isConnected() )
        {
            logger.error( "getDomainMBeanObjectName() invoked without being connected." );
            return null;
        }

        // exit if edit session isn't active
        if ( !isEditSessionActive() )
        {
            String message = "getDomainMBeanObjectName() invoked without edit session active.";
            logger.error( message );
            return null;
        }

        return this.domainObjName;
    }
    
	public MBeanInfo getMBeanInfo(ObjectName objName) throws Exception {
		validateIsConnected();
		return connection.getMBeanInfo( objName );
	}
	
	public MBeanAttributeInfo getAttributeInfo( ObjectName objName, String attributeName ) throws Exception {
        MBeanInfo mbeanInfo = getMBeanInfo(objName );
        return mbeanMetadataRepository.getAttributeInfo(mbeanInfo, attributeName);
    }
      
    public Object getAttribute( ObjectName objName, String attributeID ) throws Exception {
		validateIsConnected();
        return connection.getAttribute( objName, attributeID );
    }    
    
	public void setAttribute(ObjectName objName, MBeanAttributeInfo attributeInfo, Object value) throws Exception {
		validateIsConnected();
		
		// get attribute name
		String name = attributeInfo.getName();
		
        // set the attribute value
        Attribute attribute = new Attribute( name, value );
        connection.setAttribute( objName, attribute );		
	}
	
	public ObjectName findMBean(String type, String name ) throws Exception
    {
        // define query object
        QueryExp query = null;

        // define object name
        ObjectName queryObjectName;
        queryObjectName = JmxUtils.createQueryObjectName( type, name );

        // log debug message
        if ( logger.isDebugEnabled() ) {
        	logger.debug( "Query objectname: " + queryObjectName );            	
        }

        // execute query
        Set<?> result = connection.queryNames( queryObjectName, query );
        
        // log debug message
        if ( logger.isDebugEnabled() ) {
            logger.debug( "Number of matches returned=" + result.size() );            	
        }

        // create null object if no match is found
        if ( result.size() == 0 )
        {
            // log debug message
            if ( logger.isDebugEnabled() ) {
                logger.debug( "No match found, will return null." );            	
            }

            // return null
            return null;
        }

        // log message if more that one matches is found
        if ( result.size() > 1 )
        {
            // log debug message
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Found more that one match. Returning first MBean found." );            	
            }
        }

        
        // return object name
        ObjectName foundObjectName = (ObjectName) result.iterator().next();
        // log debug message
        if ( logger.isDebugEnabled() ) {
            logger.debug( "found object name=" + foundObjectName );            	
        }
        
        return foundObjectName;
    }
       
    public String getNameAttributeFromObjName( ObjectName objName ) throws Exception {
        
    	// get attribute info
    	MBeanAttributeInfo attributeInfo = getAttributeInfo( objName, NAME_ATRRIBUTE_ID );
    	
		// validate MBean attribute was returned            
        validateAttributeInfoIsDefined(attributeInfo, NAME_ATRRIBUTE_ID, objName );

        // get attribute value        
        Object attributeValue = getAttribute( objName, NAME_ATRRIBUTE_ID);

    	return (String) attributeValue;
    }    
    

	public ObjectName[] convertReferenceToObjectNames(MBeanAttributeInfo info, Object attributeValue) throws Exception {

		// get candidate types
		String[] candidateTypes = mbeanMetadataRepository.resolveCandidateReferenceTypes(info);
		
	    // convert value to string
        String valueAsStr = attributeValue.toString();
		
		// convert value to string array
        String[] valueAsArray = (String[]) ConvertUtils.convert(valueAsStr, String[].class );				
		
		// declare results set
		ArrayList<ObjectName> result = new ArrayList<ObjectName>();

		// iterate over the candidate types
		for (String candidateType : candidateTypes) {
			
			// iterate over the value
			for (String name : valueAsArray ) {
				ObjectName objName = findMBean(candidateType, name );
				
				if(objName != null) {
					result.add(objName);
				}
			}			
		}
		
		// convert to array
		return result.toArray(new ObjectName[result.size()]);		
	}
    
	public Object invokeMethod(ObjectName objName, MBeanOperationInfo operationInfo, Object[] params) throws Exception {
		validateIsConnected();
		
		// get signature
		MBeanParameterInfo[] mbeanSignature = operationInfo.getSignature();
		
		// iterate over the signature to prepare for invocation
		ArrayList<String> signatureContainer = new ArrayList<String>();		
		for ( MBeanParameterInfo parameterInfo : mbeanSignature) {
			signatureContainer.add(parameterInfo.getType());
		}				
		
		// signature to array
		String[] signature = signatureContainer.toArray(new String[signatureContainer.size()] );
        
		// invoke
		return connection.invoke( objName, operationInfo.getName(), params, signature );		
	}
    
	
	public MBeanOperationInfo getCreateMethod(ObjectName objName, String attributeName) throws Exception {
		validateIsConnected();
		
		// get parent MBean info
		MBeanInfo mbeanInfo = connection.getMBeanInfo(objName);		
		
		// get create method from repository
		return mbeanMetadataRepository.getCreateMethod(mbeanInfo, attributeName);
	}	
		
	
	public MBeanOperationInfo getDeleteMethod(ObjectName objName, String attributeName) throws Exception {
		validateIsConnected();
		
		// get parent MBean info
		MBeanInfo mbeanInfo = connection.getMBeanInfo(objName);		
		
		// get destructor method from repository
		return mbeanMetadataRepository.getDestroyMethod(mbeanInfo, attributeName);
	}

	
	public boolean isEditSessionActive()
    {
        // return false if connect prerequisite isn't present.
        if ( !isConnected() )
            return false;

        // edit session is active if the domain object name is defined
        return ( domainObjName != null );
    }

    public boolean isSessionConfiguredForEditing()
    {
        return isEditSessionActive();
    }

    /**
     * Validate that session is connected, otherwise an illegal state exception is thrown.
     * 
     * @throws Exception if session isn't connected. 
     */
    void validateIsConnected() throws Exception {
    	if ( isConnected() ) return;
    	
    	// throw exception if session isn't connected
        String message = messageProvider.getMessage("wjes.not_connected_failure");		                        	
    	throw new IllegalStateException(message);    	
    }
        
    public void validateAttributeInfoIsDefined(MBeanAttributeInfo attributeInfo, String attributeName, ObjectName objName) throws ModelResolutionFailedException {
    	
    	// exit if attribute info is defined
    	if (attributeInfo != null) return;

		// signal failure to validate attribute info
        Object[] args = { attributeName, objName };    	        	
        String message = messageProvider.getMessage("wjes.attributeinfo_validation_failed", args );		            
        throw new ModelResolutionFailedException (message);		            
	}
    
    
	public void validateAttributeIsReadable(MBeanAttributeInfo attributeInfo, ObjectName objName) throws ModelResolutionFailedException {

		// exit if attribute is readable
        if ( attributeInfo.isReadable() ) return;

		// signal failure to validate attribute info
        Object[] args = { attributeInfo.getName(), objName };    	        	
        String message = messageProvider.getMessage("wjes.attribute_readable_validation_failed", args );		            
        throw new ModelResolutionFailedException (message);		            		
	}
		

	public void validateIsObjectName(Object value) throws ModelValidationFailedException {

    	// exit if successful value can be type cast
    	if (ObjectName.class.isInstance(value)) return;
    	
		// signal failure to validate type cast
    	Object[] args = { value };    	        	    	
    	String message = messageProvider.getMessage("wjes.objectname_validation_failed", args );
        throw new ModelValidationFailedException( message );		    	    					
	}
        
	
	public void validateIsMBeanAttributeInfo(Object value) throws ModelValidationFailedException {
		
    	// exit if successful value can be type cast
    	if (MBeanAttributeInfo.class.isInstance(value)) return;
    	
		// signal failure to validate type cast
    	Object[] args = { value };    	        	    	
    	String message = messageProvider.getMessage("wjes.attributeinfo_validation_failed", args );
        throw new ModelValidationFailedException( message );		    	    							
	}

	
	public boolean isAttributeObjectNameReference(MBeanAttributeInfo info) {		
		return (info.getType().equals(WebLogicMBeanConstants.OBJECTNAME_TYPE));
	}

	public boolean isAttributeObjectNameArrayReference(MBeanAttributeInfo info) {		
		return (info.getType().equals(WebLogicMBeanConstants.OBJECTNAME_ARRAY_TYPE));
	}
	
	/**
     * Factory method.
     * 
     * @return new WeblogicJMXEditSession object.
     */
    @Deprecated
    public static WeblogicJMXEditSession getInstance()
    {
        return new WeblogicJMXEditSessionImpl();
    }

}
