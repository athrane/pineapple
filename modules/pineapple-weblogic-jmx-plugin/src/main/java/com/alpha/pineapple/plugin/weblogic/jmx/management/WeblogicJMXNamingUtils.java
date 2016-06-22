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


package com.alpha.pineapple.plugin.weblogic.jmx.management;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxUtils;

/**
 * Utility class, which can produce JMX object names for WebLogic.
 */
@Deprecated
public class WeblogicJMXNamingUtils
{
    
    /**
     * Logger object.
     */
    static Logger logger = Logger.getLogger( WeblogicJMXNamingUtils.class.getName() );

    /**
     * JMX factory object.
     */
    static JmxUtils jmxFactory = new JmxUtils();;        
        
    /**
     * Create object name for MBean.
     * 
     * @param type
     *            Type of MBean.
     * @param name
     *            Name of the MBean.
     * 
     * @return New object name for MBean.
     * 
     * @throws NullPointerException TODO: doucment
     * @throws MalformedObjectNameException TODO: doucment
     */    
    public static ObjectName createObjectName( String type, String name )
        throws MalformedObjectNameException, NullPointerException
    {
        // define WebLogic object name for configuration MBean.
        StringBuilder objectNameAsString = new StringBuilder();
        objectNameAsString.append( "com.bea:" );
        objectNameAsString.append( "Name=" );
        objectNameAsString.append( name );
        objectNameAsString.append( ",Type=" );
        objectNameAsString.append( type );

        return new ObjectName( objectNameAsString.toString() );
    }


    /**
     * Create object name for query existence of an MBean.
     * 
     * @param type
     *            Type of MBean.
     * @param name
     *            Name of the MBean.
     * @param domain The name of the search domain.
     * @param properties MBean properties or just a wildcard like ",*".            
     * 
     * @return New object name for MBean.
     * 
     * @throws NullPointerException TODO: doucment
     * @throws MalformedObjectNameException TODO: doucment
     */    
    public static ObjectName createQueryObjectName( String type, String name, String domain, String properties )
        throws MalformedObjectNameException, NullPointerException
    {

        // define WebLogic object name for query of configuration MBean.
        StringBuilder queryName = new StringBuilder();
        queryName.append( domain );

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

        // only add properties if properties isnt empty string
        if ( properties.length() > 0 )
        {
            //queryName.append( ",*" );
            queryName.append( properties );
        }

        // define object name
        ObjectName queryObjectName = new ObjectName( queryName.toString() );
        return queryObjectName;
    }
    
    /**
     * Create object name for query existence of an MBean 
     * in the "com.bea:" JMX domain.
     * 
     * @param type
     *            Type of MBean.
     * @param name
     *            Name of the MBean.
     * @param domain The name of the search domain.
     * @param properties MBean properties or just a wildcard like ",*".            
     * 
     * @return New object name for MBean.
     * 
     * @throws NullPointerException TODO: doucment
     * @throws MalformedObjectNameException TODO: doucment
     */    
    public static ObjectName createQueryObjectNameInBeaDomain( String type, String name, String properties )
        throws MalformedObjectNameException, NullPointerException
    {
        return createQueryObjectName( type, name, WebLogicMBeanConstants.BEA_JMX_DOMAIN, properties );
    }    
    
    /**
     * Create object name for query existence of an MBean
     * in the "Security:" JMX domain.
     * 
     * @param type
     *            Type of MBean.
     * @param name
     *            Name of the MBean.
     * @param domain The name of the search domain.
     * @param properties MBean properties or just a wildcard like ",*".            
     * 
     * @return New object name for MBean.
     * 
     * @throws NullPointerException TODO: doucment
     * @throws MalformedObjectNameException TODO: doucment
     */    
    public static ObjectName createQueryObjectNameInSecurityDomain( String type, String name, String properties )
        throws MalformedObjectNameException, NullPointerException
    {
        return createQueryObjectName( type, name, WebLogicMBeanConstants.SECURITY_JMX_DOMAIN, properties );
    }    
                 
}
