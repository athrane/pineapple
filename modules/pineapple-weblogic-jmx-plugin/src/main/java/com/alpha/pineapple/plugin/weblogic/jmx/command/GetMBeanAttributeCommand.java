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


package com.alpha.pineapple.plugin.weblogic.jmx.command;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.command.CommandException;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.plugin.weblogic.jmx.management.JMXManagmentException;
import com.alpha.pineapple.plugin.weblogic.jmx.session.JMXSession;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface which retrieves an attribute from a
 * MBean.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>jmx-session</code> defines WebLogic JMX session to operate on. The type is
 * <code>com.alpha.pineapple.plugin.weblogic.jmx.session.JMXSession</code>.</li>
 * 
 * <li><code>mbean-name</code> defines Object name of the MBean from which the command gets the MBean attribute.
 * The type is <code>javax.management.ObjectName</code>.</li>
 * 
 * <li><code>mbean-attribute-name</code> defines the name of the attribute which is retrieved. The type is
 * <code>java.lang.String</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>mbean-attribute-value</code> contains the value of the MBean attribute. The type is
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>mbean-attribute-type</code> contains the type of the MBean attribute. The type is
 * <code>java.lang.String</code>.</li>
 * </ul>
 * </p>
 */

public class GetMBeanAttributeCommand implements Command
{

    /**
     * Key used to identify property in context: JMX session to operate on.
     */
    public static final String JMXSESSION_KEY = GetMBeanCommand.JMXSESSION_KEY;

    /**
     * Key used to identify property in context: Name of the MBean to work on.
     */
    public static final String MBEAN_NAME_KEY = GetMBeanCommand.MBEAN_NAME_KEY;

    /**
     * Key used to identify property in context: The name of the MBean attribute.
     */
    public static final String MBEAN_ATTRIBUTE_NAME_KEY = "mbean-attribute-name";

    /**
     * Key used to identify property in context: The value of the MBean attribute is stored as a String in this
     * property.
     */
    public static final String MBEAN_ATTRIBUTE_VALUE_KEY = "mbean-attribute-value";

    /**
     * Key used to identify property in context: The type of the MBean attribute is stored in this property.
     */
    public static final String MBEAN_ATTRIBUTE_TYPE_KEY = "mbean-attribute-type";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * JMX session.
     */
    @Initialize( JMXSESSION_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )
    JMXSession jmxSession;

    /**
     * MBean object name.
     */
    @Initialize( MBEAN_NAME_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )
    ObjectName objName;

    /**
     * MBean attribute name.
     */
    @Initialize( MBEAN_ATTRIBUTE_NAME_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )
    String attributeID;

    public boolean execute( Context context ) throws Exception
    {

        // log debug message
        logger.info( "Starting to get MBean attribute." );

        // initialize command
        CommandInitializer initializer = new CommandInitializerImpl();
        initializer.initialize( context, this );

        try
        {

            // get JMX connection
            MBeanServerConnection connection;
            connection = jmxSession.getMBeanServerConnection();

            // find meta data for attribute
            MBeanAttributeInfo attributeInfo;
            attributeInfo = findAttributeInfo( connection );

            // validate attribute
            validateAttribute( attributeInfo );

            // get attribute value
            Object attributeValue = getAttributeValue( connection );

            // convert type exposed by MBean attribute to string
            String valueAsString = convertAttributeValue( attributeValue );

            // store value in context
            context.put( this.MBEAN_ATTRIBUTE_VALUE_KEY, valueAsString );

            // store type in context
            context.put( this.MBEAN_ATTRIBUTE_TYPE_KEY, attributeInfo.getType() );

            // log debug message
            StringBuilder message = new StringBuilder();
            message.append( "Sucessfully read attribute with name <" );
            message.append( attributeID );
            message.append( "> and value <" );
            message.append( attributeValue );
            message.append( "> on MBean <" );
            message.append( objName );
            message.append( ">." );
            logger.debug( message.toString() );

            // return successful result
            return Command.CONTINUE_PROCESSING;

        }
        catch ( Exception e )
        {
            // log error message
            StringBuilder message = new StringBuilder();
            message.append( "Get-MBean-attribute command failed which exception <" );
            message.append( StackTraceHelper.getStrackTrace( e ) );
            message.append( ">. Command failed." );

            // throw exception
            throw new CommandException( message.toString() );
        }
    }

    /**
     * Find attribute info for MBean attribute. If the attribute is defined then an {@link MBeanAttributeInfo} object is
     * returned. Otherwise the method returns null.
     * 
     * @param
     * @return Return an {@link MBeanAttributeInfo} object if the attribute is found. Otherwise the method returns null.
     * 
     * @throws JMXManagmentException
     *             If attribute retrieval failed.
     */
    MBeanAttributeInfo findAttributeInfo( MBeanServerConnection connection ) throws JMXManagmentException
    {

        try
        {
            // get MBean meta data
            MBeanInfo mbeanInfo;
            mbeanInfo = connection.getMBeanInfo( objName );

            // get attribute infos
            MBeanAttributeInfo[] attributeInfos;
            attributeInfos = mbeanInfo.getAttributes();

            // define result holder
            MBeanAttributeInfo foundAttributeInfo = null;

            // locate attribute
            for ( MBeanAttributeInfo attributeInfo : attributeInfos )
            {

                // compare attribute names
                if ( attributeInfo.getName().equals( attributeID ) )
                {

                    // store found attribute
                    foundAttributeInfo = attributeInfo;

                    // exit loop
                    break;
                }
            }

            return foundAttributeInfo;

        }
        catch ( Exception e )
        {
            throw new JMXManagmentException( "ModelAttribute retrieval failed.", e );
        }
    }

    /**
     * Validate the attribute, i.e control that the attribute info is defined and that the attribute is writable.
     * 
     * @param attributeInfo
     *            ModelAttribute meta data object.
     * 
     * @throws JMXManagmentException
     *             if attribute validation fails.
     */
    void validateAttribute( MBeanAttributeInfo attributeInfo ) throws JMXManagmentException
    {

        // validate that attribute was found
        if ( attributeInfo == null )
        {
            // create error message
            StringBuilder message = new StringBuilder();
            message.append( "Could not find attribute with name <" );
            message.append( attributeID );
            message.append( ">." );
            throw new JMXManagmentException( message.toString() );
        }

        // validate attribute is readable
        if ( !attributeInfo.isReadable() )
        {
            // create error message
            StringBuilder message = new StringBuilder();
            message.append( "MBean attribute with name <" );
            message.append( attributeID );
            message.append( "> isn't readable." );
            throw new JMXManagmentException( message.toString() );
        }
    }

    /**
     * Convert attribute value from type defined by MBean attribute to {@link String}.
     * 
     * @param attributeValue
     *            MBean attribute value.
     * 
     * @return Value converted from to the type defined by MBean attribute meta data to string.
     * 
     * @throws JMXManagmentException
     *             If conversion fails.
     */
    String convertAttributeValue( Object attributeValue ) throws JMXManagmentException
    {
        String value;
        value = ConvertUtils.convert( attributeValue );

        return value;
    }

    /**
     * get attribute value on MBean.
     * 
     * @param connection
     *            MBean server connection.
     * 
     * @throws JMXManagmentException
     *             If getting attribute value from MBean fails.
     */
    Object getAttributeValue( MBeanServerConnection connection ) throws JMXManagmentException
    {
        try
        {
            return connection.getAttribute( objName, attributeID );
        }
        catch ( Exception e )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Getting attribute value failed." );
            throw new JMXManagmentException( message.toString(), e );
        }
    }

}
