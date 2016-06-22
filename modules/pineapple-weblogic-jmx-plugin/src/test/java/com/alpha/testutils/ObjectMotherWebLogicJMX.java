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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import weblogic.management.WebLogicMBean;
import weblogic.management.configuration.DomainMBean;
import weblogic.management.configuration.SecurityConfigurationMBean;
import weblogic.management.jmx.MBeanServerInvocationHandler;
import weblogic.management.mbeanservers.edit.EditServiceMBean;
import weblogic.management.security.RealmMBean;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;
import com.alpha.pineapple.plugin.weblogic.jmx.management.JMXManagmentException;
import com.alpha.pineapple.plugin.weblogic.jmx.session.JMXSession;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;

/**
 * Implementation of the ObjectMother pattern, provides helper method for testing JMX functionality using MBean servers
 * and MBeans in WebLogic server.
 * 
 */
public class ObjectMotherWebLogicJMX
{

    /**
     * First array index.
     */
	private static final int FIRST_INDEX = 0 ;

	/**
	 * Null parent. 
	 */
	static final ResolvedType NULL_PARENT = null;

	/**
	 * Start edit session with exclusive locks
	 */	
	static final boolean EXCLUSIVE_SESSION = false;

	/**
	 * Define time out for operations.
	 */		
	static final int TIMEOUT = 120000;

	/**
	 * Define exclusive time out for other users.
	 */			
	static final int WAIT_TIME = 60000;            	
	
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
		
    /**
     * ObjectMotherWebLogicJMX no-arg constructor.
     */
    public ObjectMotherWebLogicJMX()
    {
        super();
    }

    /**
     * Get dynamic proxy for Edit Service MBean from WebLogic JMX session which is connected to a WebLogic Edit MBean
     * Server.
     * 
     * @param session
     *            JMX session which is connected to a WebLogic Edit MBean server.
     * 
     * @return dynamic proxy for Edit Service MBean.
     */
    public EditServiceMBean getWebLogicEditServiceMBean( JMXSession session )
    {    	
        try
        {
            // get jmx connection
            MBeanServerConnection connection;
            connection = session.getMBeanServerConnection();

            // get edit service mbean
            return this.getEditServiceMBean( connection );

        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );

            // test case should have failed before this point is reached.
            return null;
        }
    }

    /**
     * Returns current instance of EditService MBean from the Edit MBean server on the administration server. The method
     * returns a local proxy.
     * 
     * @param connection
     *            MBean server connection from where the proxy should be created.
     * 
     * @return current instance of EditService MBean from the Edit MBean server on the administration server.
     * 
     * @throws Exception if MBean retrival fails.
     */
    public EditServiceMBean getEditServiceMBean( MBeanServerConnection connection ) throws Exception
    {

        // initialize object name
        ObjectName name = new ObjectName( WebLogicMBeanConstants.EDIT_SERVICE_OBJECTNAME );

        // define proxy interface
        Class<EditServiceMBean> interfaceClass = EditServiceMBean.class;

        // create proxy
        EditServiceMBean mbean;
        mbean = (EditServiceMBean) this.createMBeanProxy( connection, name, interfaceClass );

        return mbean;
    }

    /**
     * Returns new MBean proxy for MBean instance.
     * 
     * @param <T>
     * @param connection
     *            MBean server connection from where the proxy should be created.
     * @param objectName
     *            Object name for the MBean for whom a proxy should be created.
     * @param interfaceClass
     *            Interface implemented by the MBean.
     * @return new MBean proxy for MBean instance.
     * @throws InstanceNotFoundException
     * @throws IOException
     */
    public <T> T createMBeanProxy( MBeanServerConnection connection, ObjectName objectName, Class<T> interfaceClass )
        throws InstanceNotFoundException, IOException
    {

        // throw exception if proxied mbean doesnt exist
        connection.getObjectInstance( objectName );

        // create proxy
        Object returnedObject;
        returnedObject = MBeanServerInvocationHandler.newProxyInstance( connection, objectName, interfaceClass, false );

        // type cast and return
        T result = interfaceClass.cast( returnedObject );

        // log debug message
        StringBuilder message = new StringBuilder();
        message.append( "Successfully created dynamic proxy for MBean " );
        message.append( "with object name <" );
        message.append( objectName );
        message.append( "> and type <" );
        message.append( interfaceClass );
        message.append( ">." );
        logger.debug( message.toString() );

        return result;
    }
        
    /**
     * The method will start an edit session with the edit MBean server and return the corresponding 
     * domain MBean from WebLogic Edit MBean Server. It is the responsibility of the client to 
     * release the acquired edit lock. For example by invoking {@link #saveAndActivate(JMXSession)}.
     * 
     * @param session WebLogic JMX Edit session which is connected to a WebLogic Edit MBean server.
     */
    public void startEdit( WeblogicJMXEditSession session )
    {
        // test connection
        assertTrue(session.isConnected());

        try
        {
            // start edit if not active 
            if(!session.isSessionConfiguredForEditing()) {
            	session.startEdit();
            }
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
            // test case should have failed before this point is reached.
        }
    }

        
    /**
     * Save and activates changes in an active edit session. The method will release the edit lock required held by the
     * edit session.
     * 
     * @param session WebLogic JMX Edit session which is connected to a WebLogic Edit MBean server.
     */
    public void saveAndActivate( WeblogicJMXEditSession session )
    {
        // log debug message
        logger.debug( "Starting to save & activate edit session." );

        // test connection
        assertTrue(session.isConnected());
        
        try
        {
        	session.saveAndActivate();
        	            
            // log debug message
            logger.debug( "Successfully saved and activated edit session." );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    
    /**
     * Find attribute info for MBean attribute. If the attribute is defined 
     * then an {@link MBeanAttributeInfo} object is returned. Otherwise the 
     * method returns null.
     *   
     * @param connection MBeanServer connection.     
     * @param objName MBean object name which is searched for attributes.
     * @param attributeID Name of the MBean attribute.  
     * 
     * @return Return an {@link MBeanAttributeInfo} object if the attribute is found. 
     * Otherwise the method returns null.
     * 
     * @throws Exception If attribute retrieval fails.
     */        
    public MBeanAttributeInfo findAttributeInfo( MBeanServerConnection connection, ObjectName objName, String attributeID ) throws Exception
    {    	    	
    	// log debug message
    	if (logger.isDebugEnabled()) logger.debug("Searching for attribute info for attribute [" + attributeID + "]");
    	 
		// get MBean meta data
        MBeanInfo mbeanInfo = connection.getMBeanInfo( objName );

        // get attribute info's
        MBeanAttributeInfo[] attributeInfos = mbeanInfo.getAttributes();

        // define result holder
        MBeanAttributeInfo foundAttributeInfo = null;

        // locate attribute
        for ( MBeanAttributeInfo attributeInfo : attributeInfos )
        {        	
        	// log debug message
        	if (logger.isDebugEnabled()) logger.debug(
        			"..located attribute info for attribute ["
        		    + attributeInfo.getName()
        		    + ":"
        		    + attributeInfo.getType()
        		    + "]");
        	
			// compare attribute names
            if ( attributeInfo.getName().equalsIgnoreCase( attributeID ) )
            {
                // store found attribute
                foundAttributeInfo = attributeInfo;

            	// log debug message
            	if (logger.isDebugEnabled()) logger.debug("Found attribute info for attribute [" + attributeInfo.getName() + "]");
                
                // exit loop
                break;
            }
        }
        
        if (foundAttributeInfo != null ) return foundAttributeInfo;
        
        // if null then fail
        fail ("Fail to find requsted attribute info for: " + attributeID);        
        return null;
    }    


    /**
     * get attribute value on MBean.
     * 
     * @param connection MBean server connection.
     * @param objName MBean object name which is searched for attributes.
     * @param attributeID Name of the MBean attribute.  
 
     * @throws JMXManagmentException If getting attribute value from MBean fails.
     */
    public Object getAttribute( MBeanServerConnection connection, ObjectName objName, String attributeID ) throws Exception	
    {
    	// get value
        Object value = connection.getAttribute( objName, attributeID );
        
    	// log debug message
    	if (logger.isDebugEnabled()) {
    		logger.debug("Found attribute value [" + value + "] for attribute [" + attributeID + "]");
    	}
                        
        return value;
    }

    /**
     * get attribute value on MBean using a (very limited) XPath query. The query is executed
     * with the domain object name as root.
     * 
     * Queries must start with leading "/", e.g. "/SecurityConfiguration".
     * 
     * @param session JMX session.
     * @param query Query expression, e.g. "/SecurityConfiguration" or "/SecurityConfiguration/Realms". 
 
     * @throws JMXManagmentException If getting attribute value from MBean fails.
     */
    public Object getAttributeByXPath( WeblogicJMXEditSession session, String query ) throws Exception	
    {
    	// start edit mode if not ready in  effect.
    	startEdit(session);
    	
    	// get domain MBean
    	ObjectName objName = session.getDomainMBeanObjectName();
    	
    	// create JMX XPath and execute query
    	JmxXPath jmxXPath = new JmxXPath (session.getMBeanServerConnection());
    	Object result = jmxXPath.evaluate(objName, query);
    	
    	// log debug message
    	if (logger.isDebugEnabled()) {
    		logger.debug("...found attribute attribute [" + result + "] by XPath [" + query + "]");
    	}
    	
    	return result;
    }
    
    
    /**
     * get attribute value on MBean using a (very limited) XPath query.
     * 
     * Queries must start with leading "/", e.g. "/SecurityConfiguration".
     * 
     * @param session JMX session.
     * @param objName MBean object name which is searched for attributes.
     * @param query Query expression, e.g. "/SecurityConfiguration" or "/SecurityConfiguration/Realms". 
 
     * @throws JMXManagmentException If getting attribute value from MBean fails.
     */
    public Object getAttributeByXPath( WeblogicJMXEditSession session, ObjectName objName, String query ) throws Exception	
    {
    	// create JMX XPath
    	JmxXPath jmxXPath = new JmxXPath (session.getMBeanServerConnection());
    	
    	// execute query
    	return jmxXPath.evaluate(objName, query);    	    	
    }

	/**
	 * Parse XPath query result Object Name existence.
	 * 
	 * @param queryResult Path query result to test.
	 * 
	 * @return Returns true if query result contains an ObjectName array with 
	 * object names or the query result contains a single ObjectName. 
	 */
	public boolean xpathQueryResultContainObjectName(Object queryResult) {		
		
    	// test null case
    	if(queryResult == null ) {
            return false;
    	}
    	
    	// test array case
    	if (queryResult instanceof ObjectName[] ) {
    		ObjectName[] queryResultAsObjNameArray = (ObjectName[]) queryResult;  
        	return (queryResultAsObjNameArray.length != 0 );      	
    	}
    	
    	// test object name case
    	if (queryResult instanceof ObjectName ) {
            return true;    		
    	}
    	
    	return false;
	}
    
    
	void printMBeanOperationInfos(WeblogicJMXEditSession session, ObjectName objName) throws Exception {
        MBeanInfo mbeanInfo = session.getMBeanInfo(objName );
        MBeanOperationInfo[] infos = mbeanInfo.getOperations();
        for ( MBeanOperationInfo info : infos)  {        	
        	if (logger.isDebugEnabled()) logger.debug("..located op info for attribute ["
        		    + info.getName()
        		    + ":"
        		    + info.getReturnType()
        		    + "]");
        }
	}
	    
	/**
     * Create resolved participant for unnamed MBean attribute using the untyped JMX API.
     * 
     * Please notice that the MBean attribute ID is the name of the attribute on 
     * the MBean, e.g. SecurityConfiguration or ListenAddress. It is not the value of
     * the Name attribute.
     * 
     * @param session JMX session 
     * @param attributeId MBean attribute ID.  
     * @param parentObjName Object name of MBean which contains the attribute.
     *  
     * @return resolved participant for MBean attribute using the untyped JMX API with the values:
     * name = attribute id
     * type = attribute info
     * value = object name
     * 
     * @throws Exception If creation fails.
     */
	public ResolvedParticipant createUnamedObjName_ResolvedParticipant(WeblogicJMXEditSession session, String attributeId, ObjectName parentObjName) throws Exception {
		
		// get connection
		MBeanServerConnection connection = session.getMBeanServerConnection();
		
		// get server MBean attribute info
		MBeanAttributeInfo attributeInfo = findAttributeInfo( connection, parentObjName, attributeId );
       		           
        // get attribute value
        Object objName = getAttribute(connection, parentObjName, attributeId);
                        
		// create participant
		return ResolvedParticipantImpl.createSuccessfulResult( attributeId, attributeInfo, objName );
	}

	/**
     * Create resolved participant for named MBean attribute using the untyped JMX API.
     *  
     * @param attributeName MBean attribute name.  
     * @param info MBean attribute info.  
     * @param objName Object name of then MBean.
     *  
     * @return resolved participant for MBean attribute using the untyped JMX API.
     * 
     * @throws Exception If creation fails.
     */
	public ResolvedParticipant createNamedObjName_ResolvedParticipant(String attributeName, MBeanAttributeInfo info, ObjectName objName) throws Exception {		
		return ResolvedParticipantImpl.createSuccessfulResult( attributeName, info, objName );
	}

	
	/**
	 * Create resolved participant which contains:
	 * Name = Value of the name field on the resolved MBean.
	 * value = MBean object name which is an attribute on the Domain MBean.
	 * type = MBeanAttributeInfo for the MBean.
	 *    
	 * @param session JMX session.
	 * @param name Value of the name field on the resolved MBean.
	 * @param attributeID Name of the attribute, e.g. Server, VirtualHosts.
	 * 
	 * @return resolved participant 
	 * 
	 * @throws Exception if test fails.
	 */
	ResolvedParticipant createObjectNameForDomainAttribute_ResolvedParticipant(WeblogicJMXEditSession session, String name, final String attributeID) throws Exception {
		
		final String query = "/" + attributeID + "[@Name='"+ name + "']";		
		
		// validate session is in edit mode
		assertTrue(session.isEditSessionActive() );
		
		// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
    	// get virtual host
    	ObjectName objName = (ObjectName ) getAttributeByXPath(session, query );    	
		
		// get server MBean attribute info
		MBeanAttributeInfo attributeInfo = findAttributeInfo( session.getMBeanServerConnection(), domainObjName, attributeID );
    	
		return ResolvedParticipantImpl.createSuccessfulResult( name, attributeInfo, objName );
	}
	
	
	
	/**
     * Create resolved participant for MBean attribute using the untyped JMX API
     * whose resolution failed because the an instance with queried name doesn't exist.
     *    
     * @param attributeName MBean name, i.e. the value of the "name" attribute on the MBean.
     * @param attrbitueInfo MBean attribute info for the attribute which contains type information.
     *  
     * @return failed resolved participant for MBean attribute using the untyped JMX API.
     * 
     * @throws Exception If creation fails.
     */
	public ResolvedParticipant createFailedNamedObjName_ResolvedParticipant(String attributeName, MBeanAttributeInfo attributeInfo) throws Exception {
		       		           		
		// create exception 
		Exception e = new ModelResolutionFailedException("some-message");
		
		// create participant
		return ResolvedParticipantImpl.createUnsuccessfulResult(attributeName, attributeInfo, null, e);
		
	}
	

    /**
     * Validates whether MBean exists as an attribute on the Domain MBean.
     * 
     * @param session JMX session.
     * @param typePlural Type of the MBean in plural form to test existence for, e.g. "VirtualHosts". 
     * @param name Name of the MBean to test existence for.
     * 
     * @return True if the MBean exists.
     * 
     * @throws Exception If test fails.
     */
	public boolean mbeanExistsAsDomainAttribute( WeblogicJMXEditSession session, String typePlural, String name ) throws Exception {

        // start edit mode
    	startEdit(session);
            	    	
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();

    	return mbeanExistsAsAttribute(session, domainObjName, typePlural, name);
	}

    /**
     * Validates whether MBean exists as an attribute on an parent MBean.
     * 
     * @param session JMX session.
     * @param objName Parent MBean.
     * @param typePlural Type of the MBean in plural form to test existence for, e.g. "VirtualHosts". 
     * @param name Name of the MBean to test existence for.
     * 
     * @return True if the MBean exists.
     * 
     * @throws Exception If test fails.
     */
	public boolean mbeanExistsAsAttribute( WeblogicJMXEditSession session, ObjectName objName, String typePlural, String name ) throws Exception {

		// create query string
		final String query = "/"+typePlural+"[@Name='"+ name + "']";
		
        // log debug message
        logger.debug( "Starting to validate existence of MBean with name=" + name  +" and type=" + typePlural);
	
        // start edit mode
    	startEdit(session);
            	    	
		// test 
		assertNotNull("ObjectName not defined.", objName);
    	
    	// get object name
    	Object queryResult = getAttributeByXPath(session, objName, query );
    	
    	// test 
    	boolean exists = xpathQueryResultContainObjectName(queryResult);
    	
        // log debug message
    	if( exists) {
            logger.debug( "MBean with name=" + name  +" and type=" + typePlural + " exists");    		
    	} else {
            logger.debug( "No MBean exists with name=" + name  +" and type=" + typePlural);    		    		
    	}
        
        // return result
        return exists;                    	
	}
	
    /**
     * Create MBean as an attribute on the Domain MBean.
     * 
     * @param session JMX session.
     * @param typeSingular MBean type in singular, e.g. "VirtualHost".
     * @param name Name of the MBean which should be created.
     * 
     * @return Object name for create MBean.
     * 
     * @throws Exception If test fails. 
     */
    public ObjectName createMBeanAsDomainAttribute( WeblogicJMXEditSession session, String typeSingular, String name ) throws Exception
    {                
        // start edit mode    	
    	startEdit(session);
    	
		// get root object in MBeans model
        ObjectName domainObjName = session.getDomainMBeanObjectName();

		// test 
		assertNotNull("Domain ObjectName not defined.", domainObjName);
        
        return createMBeanAsAttribute(session, domainObjName, typeSingular, name);		
    }

    
    /**
     * Create MBean as an attribute on parent MBean.
     * 
     * @param session JMX session.
     * @param objName Parent MBean where MBean should be created as attribute. 
     * @param typeSingular MBean type in singular, e.g. "VirtualHost".
     * @param name Name of the MBean which should be created.
     * 
     * @return Object name for create MBean.
     * 
     * @throws Exception If test fails. 
     */
    public ObjectName createMBeanAsAttribute( WeblogicJMXEditSession session, ObjectName objName, String typeSingular, String name ) throws Exception
    {                
        // start edit mode    	
    	startEdit(session);
    	
		// test 
		assertNotNull("ObjectName not defined.", objName);
		
		// get factory method for attribute
		MBeanOperationInfo operationInfo = session.getCreateMethod(objName, typeSingular);
		
		// test 
		assertNotNull("Operation info no defined for: " + typeSingular, operationInfo);
		
		// package parameters
        Object[] params = new Object[] { name };
        
		// invoke method
		ObjectName newObjName = (ObjectName) session.invokeMethod(objName, operationInfo, params );
		
        // save and activate
        saveAndActivate( session );

        // log debug message
        logger.debug( "Successfully created MBean with name=" + name  +" and type=" + typeSingular );

        return newObjName;        
    }
    
    
    /**
     * Delete MBean which (hopefully) exists as an attribute on the Domain MBean.
     * 
     * @param session JMX session.
     * @param typeSingular MBean type in singular, e.g. "VirtualHost".
     * @param typePlural Type of the MBean in plural form, e.g. "VirtualHosts".   
     * @param name Name of the MBean which should be deleted.
     * 
     * @return Object name for create MBean.
     * 
     * @throws Exception If test fails. 
     */
    public void deleteMBeanAsDomainAttribute( WeblogicJMXEditSession session, String typeSingular, String typePlural, String name ) throws Exception
    {    	
		// create query string
		final String query = "/"+typePlural+"[@Name='"+ name + "']";
    	    	
        // start edit session
    	startEdit(session);
                    	    	
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();

		// test 
		assertNotNull("Domain ObjectName not defined.", domainObjName);

    	deleteMBeanAsAttribute(session, domainObjName, typeSingular, typePlural, name);
    }
    
    /**
     * Delete MBean which (hopefully) exists as an attribute on a parent MBean.
     * 
     * @param session JMX session.
     * @param objName Parent MBean where MBean should be created as attribute.  
     * @param typeSingular MBean type in singular, e.g. "VirtualHost".
     * @param typePlural Type of the MBean in plural form, e.g. "VirtualHosts".   
     * @param name Name of the MBean which should be deleted.
     * 
     * @return Object name for create MBean.
     * 
     * @throws Exception If test fails. 
     */
    public void deleteMBeanAsAttribute( WeblogicJMXEditSession session, ObjectName objName, String typeSingular, String typePlural, String name ) throws Exception
    {    	
		// create query string
		final String query = "/"+typePlural+"[@Name='"+ name + "']";
    	    	
        // start edit session
    	startEdit(session);
                    	    	
		// test 
		assertNotNull("ObjectName not defined.", objName);
    	
    	// get object name
    	ObjectName objNameToBeDeleted = (ObjectName ) getAttributeByXPath(session, objName, query );
    	
		// test 
		assertNotNull("ObjectName not defined for attribute.", objNameToBeDeleted);
    	
		// get factory method for attribute
		MBeanOperationInfo operationInfo = session.getDeleteMethod(objName, typeSingular);
    			
		// test 
		assertNotNull("Operation info not defined for: " + typeSingular, operationInfo);
    	
		// package parameters
        Object[] params = new Object[] { objNameToBeDeleted };
		
		// invoke method
		session.invokeMethod(objName, operationInfo, params );    		
    	    	
        // save and activate
        saveAndActivate( session );

        // log debug message
        logger.debug( "Successfully deleted MBean with name=" + name  +" and type=" + typeSingular );
    }

        
    /**
     * Create an WLDF System Resource.
     * 
     * @param session JMX Session.
     * @param name Name of the WLDF system resource which is created.
     * 
     * @return Object name for WLDFSystemResource MBean.
     */
    public ObjectName createWldfSystemResource( WeblogicJMXEditSession session, String name ) throws Exception
    {
        // delete it if it already exists
        if (mbeanExistsAsDomainAttribute(session, "WLDFSystemResources", name)) deleteWldfSystemResource(session, name);
        return createMBeanAsDomainAttribute(session, "WLDFSystemResource", name);
    }

    
    /**
     * Delete WLDF System Resource.
     * 
     * @param session JMX Session.
     * @param name Name of the WLDF System Resource which is deleted.
     */
    public void deleteWldfSystemResource( WeblogicJMXEditSession session, String name ) throws Exception
    {
        if (!mbeanExistsAsDomainAttribute(session, "WLDFSystemResources", name)) {
            logger.debug( "Skipping deletion since no MBean exists with name=" + name );
            return;
        }        
        deleteMBeanAsDomainAttribute(session, "WLDFSystemResource", "WLDFSystemResources", name);    	    	
    }
        
    /**
     * Create WLDF watch notification. 
     * 
     * @param session JMX session.  
     * @param wldfSystemResource WLDF system resource name.
     * @param wldfNotificationName WLDF notification name.
     * 
     * @throws Exception If test fails.
     */	
	public void createWldfWatchNotificationObjName(WeblogicJMXEditSession session, String wldfSystemResource, String wldfNotificationName) throws Exception {
		
        // start edit mode    	
    	startEdit(session);

		// get WLDF system resource named wldfName
		final String query = "/WLDFSystemResources[@Name='"+ wldfSystemResource + "']/WLDFResource/WatchNotification";
    	    	
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
    	// get notification
    	ObjectName objName = (ObjectName ) getAttributeByXPath(session, domainObjName, query );
    	
    	// get MBean operation info
    	MBeanOperationInfo operationInfo = session.getCreateMethod(objName, "SNMPNotification");
        	
		// package parameters
        Object[] params = new Object[] { wldfNotificationName };
    	    	
    	// create notification
        session.invokeMethod(objName, operationInfo, params);
	}

    
    /**
     * Create an virtual host.
     * 
     * @param session JMX session.
     * @param name Name of the virtual host which is created.
     * 
     * @return Object name for virtual host.
     * 
     * @throws Exception If test fails. 
     */
    public ObjectName createVirtualHost( WeblogicJMXEditSession session, String name ) throws Exception
    {
        if ( mbeanExistsAsDomainAttribute(session, "VirtualHosts", name)) deleteVirtualHost(session, name);
        return createMBeanAsDomainAttribute(session, "VirtualHost", name); 
    }
        
    /**
     * delete an virtual host.
     * 
     * @param session JMX session.
     * @param name of the virtual host which is created.
     *            
     * @throws Exception If test fails.            
     */
    public void deleteVirtualHost( WeblogicJMXEditSession session, String name ) throws Exception
    {
        if (!mbeanExistsAsDomainAttribute(session, "VirtualHosts", name)) {
            logger.debug( "Skipping deletion since no MBean exists with name=" + name );
            return;
        }        
        deleteMBeanAsDomainAttribute(session, "VirtualHost", "VirtualHosts", name);    	
    }

    
    /**
     * Create a server.
     * 
     * @param session JMX session.
     * @param name Name of the server which is created.
     * 
     * @return Object name for server.
     * 
     * @throws Exception If test fails. 
     */
    public ObjectName createServer( WeblogicJMXEditSession session, String name ) throws Exception
    {
        if ( mbeanExistsAsDomainAttribute(session, "Servers", name)) deleteServer(session, name);        
        return createMBeanAsDomainAttribute(session, "Server", name);         
    }
    
    /**
     * Delete a server.
     * 
     * @param session JMX session.
     * @param name of the server which is created.
     * 
     * @throws Exception If test fails. 
     */
    public void deleteServer( WeblogicJMXEditSession session, String name ) throws Exception
    {
        if (!mbeanExistsAsDomainAttribute(session, "Servers", name)) {
            logger.debug( "Skipping deletion since no MBean exists with name=" + name );
            return;
        }        
        deleteMBeanAsDomainAttribute(session, "Server", "Servers", name);
    }

        
    /**
     * Create a machine.
     * 
     * @param session JMX session.
     * @param name Name of the server which is created.
     * 
     * @return ObjectName for machine.
     * 
     * @throws Exception If test fails. 
     */
    public ObjectName createMachine( WeblogicJMXEditSession session, String name ) throws Exception
    {
        if ( mbeanExistsAsDomainAttribute(session, "Machines", name)) deleteMachine(session, name);        
        return createMBeanAsDomainAttribute(session, "Machine", name);         
    }
    
    
    /**
     * Delete a machine.
     * 
     * @param session JMX session.
     * @param name of the server which is created.
     * 
     * @throws Exception If test fails. 
     */
    public void deleteMachine( WeblogicJMXEditSession session, String name ) throws Exception
    {
        if (!mbeanExistsAsDomainAttribute(session, "Machines", name)) {
            logger.debug( "Skipping deletion since no MBean exists with name=" + name );
            return;
        }        
        deleteMBeanAsDomainAttribute(session, "Machine", "Machines", name);
    }

    /**
     * Create an work manager.
     * 
     * @param session JMX session.
     * @param name Name of the work manager which is created.
     * 
     * @return Object name for work manager.
     * 
     * @throws Exception If test fails. 
     */
    public ObjectName createWorkManager( WeblogicJMXEditSession session, String name ) throws Exception
    {
    	ObjectName objName = (ObjectName) getAttributeByXPath(session, "/SelfTuning");
		assertNotNull("SelfTuning ObjectName is not defined.", objName);
    	if (mbeanExistsAsAttribute(session, objName, "WorkManagers", name)) deleteWorkManager(session, name); 
        return createMBeanAsAttribute(session, objName, "WorkManager", name);
    }
    
    /**
     * Delete a work manager.
     * 
     * @param session JMX session.
     * @param name of the work manager which is deleted.
     * 
     * @throws Exception If test fails. 
     */
    public void deleteWorkManager( WeblogicJMXEditSession session, String name ) throws Exception
    {
    	ObjectName objName = (ObjectName) getAttributeByXPath(session, "/SelfTuning");
		assertNotNull("SelfTuning ObjectName is not defined.", objName);
    	if (!mbeanExistsAsAttribute(session, objName, "WorkManagers", name)) {
            logger.debug( "Skipping deletion since no MBean exists with name=" + name );
            return;    		
    	}
    	deleteMBeanAsAttribute(session, objName, "WorkManager", "WorkManagers", name);
    }
        
    /**
     * Return the object name for a MBean.
     * 
     * @param mbean MBean.
     * 
     * @return object name for a MBean.
     */
    @Deprecated
    public ObjectName getObjectName( WebLogicMBean mbean )
    {
        return mbean.getObjectName();
    }
    
    /**
     * Get dynamic proxy for WebLogic DomainMBean.
     * 
     * @param editSession WebLogic JMX edit session.
     * 
     * @return dynamic proxy for WebLogic DomainMBean.
     * 
     * @throws Exception If test fails.
     */
    @Deprecated
    public DomainMBean getDomainMBean( WeblogicJMXEditSession editSession ) throws Exception 
    {   
    	// get JMX connection
    	MBeanServerConnection connection = editSession.getMBeanServerConnection();
    	
    	// get Domain MBean object name
    	ObjectName domainObjName = editSession.getDomainMBeanObjectName();
    	
        // create DomainMBean proxy
        DomainMBean domainMBean = createMBeanProxy( connection, domainObjName, DomainMBean.class );
    	    	    	    	
    	// assert not null
    	assertNotNull(domainMBean);
    	
    	return domainMBean;
    }
    
    /**
     * Create resolved participant which contains security realm MBean.
     *  
     * @param realmName Realm name. 
     * @param session JMX session. 
     * 
     * @return resolved participant which contains security realm MBean.
     * 
     * @throws Exception If test fails. 
     */	
    @Deprecated
	public ResolvedParticipant createRealmResolvedParticipant(String realmName, WeblogicJMXEditSession session ) throws Exception {

        // get realm
        DomainMBean domainMBean = getDomainMBean( session);
        SecurityConfigurationMBean securityConfigMBean = domainMBean.getSecurityConfiguration();
        RealmMBean realmMBean = securityConfigMBean.getRealms()[FIRST_INDEX];        
		
        // get type 
		Class<?> mbeanType =  realmMBean.getClass();                
		           
		// create participant
		return ResolvedParticipantImpl.createSuccessfulResult( realmName, mbeanType, realmMBean );
	}	

    /**
     * Create resolved participant which contains authentication provider object name collection.
     *  
     * @param session JMX session. 
     * 
     * @return resolved participant which contains authentication provider object name collection.
     * 
     * @throws Exception If test fails. 
     */	
	public ResolvedParticipant createAuthenticationProviderCollection_ResolvedParticipant(WeblogicJMXEditSession session ) throws Exception {
		
		// validate session is in edit mode
		assertTrue(session.isEditSessionActive() );
    	
    	final String query = "/SecurityConfiguration/Realms[0]";
    	
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
    	// get realm
    	ObjectName realmObjName = (ObjectName ) getAttributeByXPath(session, domainObjName, query );    	

		// get AuthenticationProviders attribute on realm MBean
		return createUnamedObjName_ResolvedParticipant(session, "AuthenticationProviders", realmObjName);
	}	
		
    /**
     * Create resolved participant which contains domain MBean object name.
     * 
     * Default is to use MBeanAttribute as type for MBeans, but since Domain is the root object
     * then it isn't an attribute on any MBean, so for this special case the type will be ObjectName.
     *  
     * @param session JMX session which should be in edit mode. 
     * 
     * @return resolved participant which contains domain MBean object name.
     */	
	public ResolvedParticipant createDomainObjName_ResolvedParticipant(WeblogicJMXEditSession session) {
		
		// validate session is in edit mode
		assertTrue(session.isEditSessionActive() );
		
		// get domain object name
		ObjectName objName = session.getDomainMBeanObjectName();

		// get domain type as object name
		Object mbeanType = ObjectName.class;

		// get name as the key property name of the MBean 
		String mbeanName = objName.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY);
		
		// create participant
		return ResolvedParticipantImpl.createSuccessfulResult( mbeanName, mbeanType, objName );
	}
	
    /**
     * Create resolved participant which contains VirtuaHost
     * which is set and has the properties:
     * 1) Name = name argument
     * 2) Type = MBeanAttributeInfo with info about VirtualHost.  
     * 3) Value = Virtual Host object name. 
     * 4) Value state = set
     *   
     * @param session JMX session which should be in edit mode.
     * @param name Name of VirtualHost MBean. 
     * 
     * @return resolved participant which contains VirtualHost as object name.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedParticipant createVirtualHostObjName_ResolvedParticipant(WeblogicJMXEditSession session, String name ) throws Exception {
    	return createObjectNameForDomainAttribute_ResolvedParticipant(session, name, "VirtualHosts");		
	}

	
    /**
     * Create resolved participant which contains VirtuaHost
     * which is set and has the properties:
     * 1) Name = name argument
     * 2) Type = MBeanAttributeInfo with info about VirtualHost.  
     * 3) Value = Server object name. 
     * 4) Value state = set
     *   
     * @param session JMX session which should be in edit mode.
     * @param name Name of Server MBean. 
     * 
     * @return resolved participant which contains VirtualHost as object name.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedParticipant createServerObjName_ResolvedParticipant(WeblogicJMXEditSession session, String name ) throws Exception {
    	return createObjectNameForDomainAttribute_ResolvedParticipant(session, name, "Servers");
	}
	

    /**
     * Create resolved participant which contains Machine
     * which is set and has the properties:
     * 1) Name = name argument
     * 2) Type = MBeanAttributeInfo with info about Machine.  
     * 3) Value = Machine object name. 
     * 4) Value state = set
     *   
     * @param session JMX session which should be in edit mode.
     * @param name Name of Machine MBean. 
     * 
     * @return resolved participant which contains Machine as object name.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedParticipant createMachineObjName_ResolvedParticipant(WeblogicJMXEditSession session, String name ) throws Exception {
    	return createObjectNameForDomainAttribute_ResolvedParticipant(session, name, "Machines");
	}

    /**
     * Create resolved participant which contains SelfTuning
     * which is set and has the properties:
     * 1) Name = name argument
     * 2) Type = MBeanAttributeInfo with info about SelfTuning.  
     * 3) Value = Machine object name. 
     * 4) Value state = set
     *   
     * @param session JMX session which should be in edit mode.
     * @param name Name of SelfTuning MBean. 
     * 
     * @return resolved participant which contains SelfTuning as object name.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedParticipant createSelfTuningObjName_ResolvedParticipant(WeblogicJMXEditSession session, String name ) throws Exception {
    	return createObjectNameForDomainAttribute_ResolvedParticipant(session, name, "SelfTuning");
	}
	
	
    /**
     * Create resolved participant which contains Servers attribute on Domain MBean. 
     * The attribute is a collection (array) of object names.
     *  
     * @param session JMX session. 
     * 
     * @return resolved participant which contains Servers attribute on Domain MBean. 
     * The attribute is a collection (array) of object names.
     */	
	public ResolvedParticipant createServersObjNameCollection_ResolvedParticipant(WeblogicJMXEditSession session) throws Exception {
		
		// validate session is in edit mode
		assertTrue(session.isEditSessionActive() );		
		
		// get domain object name
		ObjectName domainObjName = session.getDomainMBeanObjectName();
		
		// get Servers attribute on Domain MBean
		return createUnamedObjName_ResolvedParticipant(session, "Servers", domainObjName);
	}

    /**
     * Create resolved participant which contains VirtualHost attribute on Domain MBean. 
     * The attribute is a collection (array) of object names.
     *  
     * @param session JMX session. 
     * 
     * @return resolved participant which contains VirtualHost attribute on Domain MBean. 
     * The attribute is a collection (array) of object names.
     */	
	public ResolvedParticipant createVirtualHostObjNameCollection_ResolvedParticipant(WeblogicJMXEditSession session) throws Exception {
		
		// validate session is in edit mode
		assertTrue(session.isEditSessionActive() );		
		
		// get domain object name
		ObjectName domainObjName = session.getDomainMBeanObjectName();
		
		// get Virtual Hosts attribute on Domain MBean
		return createUnamedObjName_ResolvedParticipant(session, "VirtualHosts", domainObjName);
	}

		
    /**
     * Create resolved participant which contains Machines attribute on Domain MBean. 
     * The attribute is a collection (array) of object names.
     *  
     * @param session JMX session. 
     * 
     * @return resolved participant which contains Machines attribute on Domain MBean.  
     * The attribute is a collection (array) of object names.
     */	
	public ResolvedParticipant createMachinesObjNameCollection_ResolvedParticipant(WeblogicJMXEditSession session) throws Exception {
		
		// validate session is in edit mode
		assertTrue(session.isEditSessionActive() );		
		
		// get domain object name
		ObjectName domainObjName = session.getDomainMBeanObjectName();
		
		// get Machines attribute on Domain MBean
		return createUnamedObjName_ResolvedParticipant(session, "Machines", domainObjName);
	}
	
    /**
     * Create resolved participant which contains WLDF System Resource.
     *  
     * @param session JMX session.   
     * @param name WLDF System resource name.
     * 
     * @return resolved participant which contains WLDF System Resource.
     * @throws Exception If test fails
     */	
	public ResolvedParticipant createWldfSystemResource_ResolvedParticipant(WeblogicJMXEditSession session, String name ) throws Exception {
    	return createObjectNameForDomainAttribute_ResolvedParticipant(session, name, "WLDFSystemResources");    	
	}
	
    /**
     * Create resolved participant which contains WLDF Resource.
     *  
     * @param session JMX session.   
     * @param name WLDF resource name.
     * 
     * @return resolved participant which contains WLDF Resource.
     * @throws Exception If test fails
     */	
	public ResolvedParticipant createWldfResource_ResolvedParticipant(WeblogicJMXEditSession session, String name ) throws Exception {
		final String query = "/WLDFSystemResources[@Name='"+ name + "']";

		// validate session is in edit mode
		assertTrue(session.isEditSessionActive() );
    	    	
    	// get WLDF System resource
    	ObjectName objName = (ObjectName ) getAttributeByXPath(session, query );    	
		
		// create participant
		return createUnamedObjName_ResolvedParticipant(session, "WLDFResource", objName);

	}
	
    /**
     * Create resolved participant which contains WLDF Harvester as object name.
     *  
     * @param session JMX session.  
     * @param wldfSystemResource WLDF system resource. 
     * @param name WLDF System resource name.
     * 
     * @return resolved participant which contains WLDF Harvester as object name.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedParticipant createWldfHarvesterObjName_ResolvedParticipant(WeblogicJMXEditSession session, String name ) throws Exception {
		final String query = "/WLDFSystemResources[@Name='"+ name + "']/WLDFResource";
		
		// validate session is in edit mode
		assertTrue(session.isEditSessionActive() );
    	    	
    	// get WLDF resource
    	ObjectName objName = (ObjectName ) getAttributeByXPath(session, query );    	
		
		// create participant
		return createUnamedObjName_ResolvedParticipant(session, "Harvester", objName);
	}
	
	
    /**
     * Create resolved participant which contains WLDF watch notification as object name.
     *  
     * @param session JMX session.  
     * @param name WLDF System resource name.
     * 
     * @return resolved participant which contains WLDF watch notification as object name.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedParticipant createWldfWatchNotificationObjName_ResolvedParticipant(WeblogicJMXEditSession session, String name ) throws Exception {
		final String query = "/WLDFSystemResources[@Name='"+ name + "']/WLDFResource";
		
		// validate session is in edit mode
		assertTrue(session.isEditSessionActive() );
    	    	
    	// get WLDF resource
    	ObjectName objName = (ObjectName ) getAttributeByXPath(session, query );    	
		
		// create participant
		return createUnamedObjName_ResolvedParticipant(session, "WatchNotification", objName);
	}
	
    /**
     * Create resolved participant which contains WorkManagers attribute on SelfTuning MBean. 
     * The attribute is a collection (array) of object names.
     *  
     * @param session JMX session. 
     * @param name SelfTuning name.
     * 
     * @return resolved participant which contains WorkManagers attribute on SelfTuning MBean.  
     * The attribute is a collection (array) of object names.
     */	
	public ResolvedParticipant createWorkManagerObjNameCollection_ResolvedParticipant(WeblogicJMXEditSession session, String name ) throws Exception {
		
		// validate session is in edit mode
		assertTrue(session.isEditSessionActive() );
    	
    	final String query = "/SelfTuning[@Name='"+ name + "']";
    	
    	// get SelfTuning
    	ObjectName selfTuningObjName = (ObjectName ) getAttributeByXPath(session, query );    	

		// create participant
		return createUnamedObjName_ResolvedParticipant(session, "WorkManagers", selfTuningObjName);		
	}
	
    /**
     * Create resolved participant which contains WorkManager as object name. 
     *  
     * @param session JMX session. 
     * @param name WorkManager name.
     * 
     * @return resolved participant which contains WorkManager as object name.  
     */	
	public ResolvedParticipant createWorkManager_ResolvedParticipant(WeblogicJMXEditSession session, String name ) throws Exception {
		
		// validate session is in edit mode
		assertTrue(session.isEditSessionActive() );
    	
    	final String query = "/SelfTuning/WorkManagers[@Name='"+ name+ "']";
    	
    	// get work manager 
    	ObjectName objName = (ObjectName ) getAttributeByXPath(session, query );    	

		// get self tuning object name
		ObjectName stObjName = (ObjectName) getAttributeByXPath(session, "/SelfTuning" );
				
		// get server MBean attribute info
		MBeanAttributeInfo attributeInfo = findAttributeInfo( session.getMBeanServerConnection(), stObjName, "WorkManagers");    	
		return ResolvedParticipantImpl.createSuccessfulResult( name, attributeInfo, objName );
	}
	
	
	/**
	 * Create null resolved participant which contains nothing.
	 *  
	 * @return null resolved participant which contains nothing.
	 */
	public ResolvedParticipant createNullResolvedParticipant() {		
		return ResolvedParticipantImpl.createSuccessfulResult(null, null, null);		
	}
	
	/**
	 * Create resolved participant which contains a basic string.
	 * 
	 * @param name Participant name.
	 * @param value Participant value.
	 *  
	 * @return resolved participant which contains a basic string.
	 */
	public ResolvedParticipant createStringResolvedParticipant(String name, String value ) {
		return ResolvedParticipantImpl.createSuccessfulResult(name, String.class, value );		
	}

	/**
	 * Create resolved participant which contains a string array.
	 * 
	 * @param name Participant name.
	 * @param value Participant value.
	 *  
	 * @return resolved participant which contains a string array.
	 */
	public ResolvedParticipant createStringArrayResolvedParticipant(String name, String[] value ) {
		return ResolvedParticipantImpl.createSuccessfulResult(name, String[].class, value );		
	}
	
	
	/**
	 * Create resolved participant which contains a boolean.
	 * 
	 * @param name Participant name.
	 * @param value Participant value.
	 *  
	 * @return resolved participant which contains a boolean.
	 */
	public ResolvedParticipant createBooleanResolvedParticipant(String name, boolean value ) {
		return ResolvedParticipantImpl.createSuccessfulResult(name, boolean.class, value );		
	}
	
	/**
	 * Create resolved participant which contains a int.
	 * 
	 * @param name Participant name.
	 * @param value Participant value.
	 *  
	 * @return resolved participant which contains a basic string.
	 */
	public ResolvedParticipant createIntResolvedParticipant(String name, int value ) {
		return ResolvedParticipantImpl.createSuccessfulResult(name, int.class, value );		
	}
	
	/**
	 * Create resolved participant which contains a random string.
	 *  
	 * @return resolved participant which contains a random string.
	 */
	public ResolvedParticipant createRandomStringResolvedParticipant() {		
		String randomStr = RandomStringUtils.random(10);
		String randomStr2 = RandomStringUtils.random(10);
		return ResolvedParticipantImpl.createSuccessfulResult(randomStr, String.class, randomStr2 );		
	}

		
    /**
     * Create resolved type which contains: 
     * primary participant is just a basic string,
     * secondary participant is a resolved Domain MBean as Object name.
     *  
     * @param domainName Domain name.
     * @param session JMX session. 
     * 
     * @return resolved type which contains resolved Domain MBean in secondary participant.
     */	
	public ResolvedType createDomainObjName_ResolvedType(String domainName, WeblogicJMXEditSession session) {

		ResolvedParticipant primary = createStringResolvedParticipant("domain", domainName);
		ResolvedParticipant secondary = createDomainObjName_ResolvedParticipant(session);
		return ResolvedTypeImpl.createResolvedType(NULL_PARENT, primary, secondary);		 						
	}

    /**
     * Create resolved type which contains: 
     * primary participant is just a null participant.
     * secondary participant is a resolved WLDF Harvester as object name.
     *  
     * @param session JMX session.    
     * @param wldfName WLDF System resource name.
     * 
     * @return resolved type which contains resolved WLDF Harvester (as object name) in secondary participant.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedType createWldfHarvesterObjName_ResolvedType(WeblogicJMXEditSession session, String wldfName ) throws Exception {
		ResolvedParticipant primary = createNullResolvedParticipant();
		ResolvedParticipant secondary = createWldfHarvesterObjName_ResolvedParticipant(session, wldfName );
		
		// test 
		assertTrue(secondary.isResolutionSuccesful());
		assertTrue(secondary.getValue() instanceof ObjectName);
		
		return ResolvedTypeImpl.createResolvedType(NULL_PARENT, primary, secondary);		
	}


    /**
     * Create resolved type which contains: 
     * primary participant is just a basic string,
     * secondary participant is a resolved WDLF system resource MBean as object name.
     *  
     * @param session JMX session.
     * @param name WLDF resource name.  
     * 
     * @return resolved type which contains resolved WDLF resource MBean in secondary participant.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedType createWldfResourceObjName_ResolvedType(WeblogicJMXEditSession session, String name) throws Exception {				
		ResolvedParticipant primary = createNullResolvedParticipant();
		ResolvedParticipant secondary = createWldfResource_ResolvedParticipant(session, name);
		
		// test 
		assertTrue(secondary.isResolutionSuccesful());
		assertTrue(secondary.getValue() instanceof ObjectName);
		
		return ResolvedTypeImpl.createResolvedType(NULL_PARENT, primary, secondary);		
	}
	
    /**
     * Create resolved type which contains: 
     * primary participant is just a basic string,
     * secondary participant is a resolved WDLF system resource MBean as object name.
     *  
     * @param session JMX session.
     * @param name WLDF system resource name.  
     * 
     * @return resolved type which contains resolved WDLF system resource MBean in secondary participant.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedType createWldfSystemResourceObjName_ResolvedType(WeblogicJMXEditSession session, String name) throws Exception {				
		ResolvedParticipant primary = createNullResolvedParticipant();
		ResolvedParticipant secondary = createWldfSystemResource_ResolvedParticipant(session, name);
		
		// test 
		assertTrue(secondary.isResolutionSuccesful());
		assertTrue(secondary.getValue() instanceof ObjectName);
		
		return ResolvedTypeImpl.createResolvedType(NULL_PARENT, primary, secondary);		
	}

	
    /**
     * Create resolved type which contains: 
     * primary participant is just a basic string,
     * secondary participant is a resolved WDLF Watch MBean as object name.
     *  
     * @param session JMX session.
     * @param wldfSystemResource WLDF system resource name.
     * 
     * @return resolved type which contains resolved WDLF Watch notification MBean in secondary participant.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedType createWldfWatchObjName_ResolvedType(WeblogicJMXEditSession session, String wldfSystemResource) throws Exception {				
		ResolvedParticipant primary = createNullResolvedParticipant();
		ResolvedParticipant secondary = createWldfWatchNotificationObjName_ResolvedParticipant(session, wldfSystemResource);
		
		// test 
		assertTrue(secondary.isResolutionSuccesful());
		assertTrue(secondary.getValue() instanceof ObjectName);
		
		return ResolvedTypeImpl.createResolvedType(NULL_PARENT, primary, secondary);		
	}
	
	
    /**
     * Create resolved type which contains: 
     * primary participant is just a basic string,
     * secondary participant is a resolved VirtualHost MBean as object name.
     *  
     * @param session JMX session.
     * @param name VirtualHost name.  
     * 
     * @return resolved type which contains resolved Domain MBean in secondary participant.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedType createVirtualHostObjName_ResolvedType(WeblogicJMXEditSession session, String name) throws Exception {				
		ResolvedParticipant primary = createNullResolvedParticipant();
		ResolvedParticipant secondary = createVirtualHostObjName_ResolvedParticipant(session, name);
		
		// test 
		assertTrue(secondary.isResolutionSuccesful());
		assertTrue(secondary.getValue() instanceof ObjectName);
		
		return ResolvedTypeImpl.createResolvedType(NULL_PARENT, primary, secondary);		
	}


    /**
     * Create resolved type which contains: 
     * primary participant is just a basic string,
     * secondary participant is a resolved Server MBean as object name.
     *  
     * @param session JMX session. 
     * @param name Server name.  
     * 
     * @return resolved type which contains resolved Server MBean in secondary participant.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedType createServerObjName_ResolvedType(WeblogicJMXEditSession session, String name) throws Exception {				
		ResolvedParticipant primary = createNullResolvedParticipant();
		ResolvedParticipant secondary = createServerObjName_ResolvedParticipant(session, name);
		
		// test 
		assertTrue(secondary.isResolutionSuccesful());
		assertTrue(secondary.getValue() instanceof ObjectName);
		
		return ResolvedTypeImpl.createResolvedType(NULL_PARENT, primary, secondary);		
	}

    /**
     * Create resolved type which contains: 
     * primary participant is just a basic string,
     * secondary participant is a resolved Machine MBean as object name.
     *  
     * @param session JMX session. 
     * @param name Machine name.  
     * 
     * @return resolved type which contains resolved Machine MBean in secondary participant.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedType createMachineObjName_ResolvedType(WeblogicJMXEditSession session, String name) throws Exception {				
		ResolvedParticipant primary = createNullResolvedParticipant();
		ResolvedParticipant secondary = createMachineObjName_ResolvedParticipant(session, name);
		
		// test 
		assertTrue(secondary.isResolutionSuccesful());
		assertTrue(secondary.getValue() instanceof ObjectName);
		
		return ResolvedTypeImpl.createResolvedType(NULL_PARENT, primary, secondary);		
	}
		
    /**
     * Create resolved type which contains: 
     * primary participant is just a basic string,
     * secondary participant is a resolved SelfTuning MBean as object name.
     *  
     * @param session JMX session. 
     * @param name SelfTuning name.  
     * 
     * @return resolved type which contains resolved SelfTuning MBean in secondary participant.
     * 
     * @throws Exception If test fails.
     */	
	public ResolvedType createSelfTuningObjName_ResolvedType(WeblogicJMXEditSession session, String name) throws Exception {				
		ResolvedParticipant primary = createNullResolvedParticipant();
		ResolvedParticipant secondary = createSelfTuningObjName_ResolvedParticipant(session, name);
		
		// test 
		assertTrue(secondary.isResolutionSuccesful());
		assertTrue(secondary.getValue() instanceof ObjectName);
		
		return ResolvedTypeImpl.createResolvedType(NULL_PARENT, primary, secondary);		
	}
	
}
