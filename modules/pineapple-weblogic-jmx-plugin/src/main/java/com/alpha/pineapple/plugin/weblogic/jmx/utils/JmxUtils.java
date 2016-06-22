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


package com.alpha.pineapple.plugin.weblogic.jmx.utils;

import java.io.IOException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;

/**
 * Utility class for JMX.
 */
public class JmxUtils
{
	
    /**
     * Logger object.
     */
    static Logger logger = Logger.getLogger( JmxUtils.class.getName() );

    /**
     * Create object name for query existence of an MBean.
     * 
     * @param type
     *            Type of MBean.
     * @param name
     *            Name of the MBean.
     * 
     * @return New object name for MBean.
     * 
     * @throws Exception if name creation fails
     */    
    public static ObjectName createQueryObjectName( String type, String name )
        throws MalformedObjectNameException, NullPointerException
    {

        // defines search domain
        String SEARCH_DOMAIN = "*:";
        // String SEARCH_DOMAIN = "com.bea:";

        // define WebLogic object name for query of configuration MBean.
        StringBuilder queryName = new StringBuilder();
        queryName.append( SEARCH_DOMAIN );

        // only add name if name isnt empty string
        if ( name.length() > 0 )
        {
            queryName.append( "Name=" );
            queryName.append( name );
        }

        // only add type if type isnt empty string
        if ( type.length() > 0 )
        {
            queryName.append( ",Type=" );
            queryName.append( type );
        }

        // add ??
        queryName.append( ",*" );

        // define object name
        ObjectName queryObjectName = new ObjectName( queryName.toString() );
        return queryObjectName;
    }
    
    /**
     * Return EditServiceMBean object name.
     * 
     * @return EditServiceMBean object name. 
     */
    public static ObjectName getEditServiceMBeanObjectName() throws MalformedObjectNameException, NullPointerException
    {

        ObjectName objectName;
        objectName = new ObjectName( WebLogicMBeanConstants.EDIT_SERVICE_OBJECTNAME );
        return objectName;
    }

    /**
     * Return DomainRuntimeServiceMBean object name.
     * 
     * @return DomainRuntimeServiceMBean object name. 
     */    
    public static ObjectName getDomainRuntimeServiceObjectName() throws MalformedObjectNameException, NullPointerException
    {

        ObjectName objectName;
        objectName = new ObjectName( WebLogicMBeanConstants.DOMAINRUNTIME_SERVICE_OBJECTNAME );
        return objectName;
    }
    
    /**
     * Return TypeServiceMBean object name.
     * 
     * @return TypeServiceMBean object name.
     * 
     * @throws NullPointerException
     * @throws MalformedObjectNameException
     */
    public static ObjectName getTypeServiceMBeanObjectName() throws MalformedObjectNameException, NullPointerException
    {

        ObjectName objectName;
        objectName = new ObjectName( WebLogicMBeanConstants.TYPE_SERVICE_OBJECTNAME );
        return objectName;
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
    public static <T> T createMBeanProxy( MBeanServerConnection connection, ObjectName objectName, Class<T> interfaceClass )
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
}
