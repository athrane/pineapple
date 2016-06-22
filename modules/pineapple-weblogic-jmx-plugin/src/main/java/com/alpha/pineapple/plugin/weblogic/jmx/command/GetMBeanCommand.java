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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

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
import com.alpha.pineapple.plugin.weblogic.jmx.management.WeblogicJMXNamingUtils;
import com.alpha.pineapple.plugin.weblogic.jmx.session.JMXSession;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface which retrieves an MBean in an
 * WebLogic MBean Server. Based on the supplied reference to an an existing MBean (through an ObjectName) the command
 * can retrieve an child MBean if the MBean exposes a get&lt;child-MBean-type&gt;(..) method or a
 * lookup&lt;child-MBean-type&gt;(..) method.
 * </p>
 *   
 * <p>
 * Precondition for execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>jmx-session</code> defines WebLogic JMX session to operate on. The type is
 * <code>com.alpha.pineapple.plugin.weblogic.jmx.session.JMXSession</code>.</li>
 * 
 * <li><code>mbean-name</code> defines object name of an existing parent MBean from which the child
 * MBean retrieved. If the parent MBean name is null then the MBean server is searched directly for the (child) MBean.
 * The type is <code>javax.management.ObjectName</code>.</li>
 * 
 * <li><code>child-mbean-name</code> defines the name of the child MBean which is retrieved. The type is
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>child-mbean-type</code> defines MBean type of the child MBean which is retrieved. The type is
 * <code>java.lang.String</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>retrieved-mbean</code> contains the object name of the
 * retrieved MBean. The type is <code>javax.management.ObjectName</code>.</li>
 * </ul>
 * </p>
 */
public class GetMBeanCommand implements Command
{

    /**
     * Key used to identify property in context: WebLogic JMX session to operate on.
     */
    public static final String JMXSESSION_KEY = "jmx-session";

    /**
     * Key used to identify property in context: Object name of the existing MBean on which the command retrieves a
     * child MBean.
     */
    public static final String MBEAN_NAME_KEY = "mbean-name";

    /**
     * Key used to identify property in context: Name of the child MBean which is retrieved.
     */
    public static final String CHILD_MBEAN_NAME_KEY = "child-mbean-name";

    /**
     * Key used to identify property in context: MBean type of the child MBean which is retrieved.
     */
    public static final String CHILD_MBEAN_TYPE_KEY = "child-mbean-type";

    /**
     * Key used to identify property in context: The object name of the
     * retrieved MBean.
     */
    public static final String RETRIEVED_MBEAN_KEY = "retrieved-mbean";

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
     * Object name of parent MBean from which the MBean should be retrieved.
     */
    @Initialize( MBEAN_NAME_KEY )            
    ObjectName parentObjectName;

    /**
     * Name of the MBean which should be retrieved.
     */
    @Initialize( CHILD_MBEAN_NAME_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )                
    String childName;

    /**
     * Type of the MBean which should be retrieved.
     */
    @Initialize( CHILD_MBEAN_TYPE_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )                    
    String childType;

    /**
     * JMX MBean server connection.
     */
    MBeanServerConnection connection;

    public boolean execute( Context context ) throws Exception
    {
        // log debug message
        if ( logger.isDebugEnabled() )
        {
            logger.info( "Starting to get MBean." );
        }

        // initialize command
        CommandInitializer initializer =  new CommandInitializerImpl();
        initializer.initialize( context, this );

        try
        {
            // get JMX connection
            connection = jmxSession.getMBeanServerConnection();

            // create WebLogic meta data helper
            //MBeanMetaDataHelper helper = new MBeanMetaDataHelper( connection );

            // parent is undefined, search for MBean directly in MBean server.            
            if (isParentUndefined()) 
            {
                return getMBeanWithUndefinedParentMBean( context );
            }

            // parent is defined, search for MBean using parent
            // attempt to retrieve child using reflection

            // get method on MBean which provides access to the child MBean type
            //Method[] methods = helper.getChildMBeanAcessorMethods( parentObjectName, childType );

            // select the getter method which should be used to retrieve the MBean
            //Method getMethod = helper.selectAcessorMethodToInvoke( methods, childName );
            Method getMethod = null;

            // get MBean
            ObjectName childObjName;
            childObjName = getMbean( connection, getMethod, parentObjectName, childType, childName );

            // validate object name is defined, i.e. MBean was retrieved
            if ( childObjName == null )
            {
                // log error message
                StringBuilder message = new StringBuilder();
                message.append( "The requested child MBean with ID <" );
                message.append( childName );
                message.append( "> and type <" );
                message.append( childType );
                message.append( "> was not found on MBean <" );
                message.append( parentObjectName );
                message.append( ">. " );
                message.append( "Command failed." );
                logger.debug( message.toString() );

                // flag command as failed and exit.
                return Command.PROCESSING_COMPLETE;
            }

            // log debug message
            if ( logger.isDebugEnabled() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "Successfully got MBean with object name <" );
                message.append( childObjName );
                message.append( "> using the method <" );
                message.append( getMethod );
                message.append( "> on existing MBean <" );
                message.append( parentObjectName );
                message.append( ">." );
                logger.debug( message.toString() );
            }

            // store MBean in context
            context.put( this.RETRIEVED_MBEAN_KEY, childObjName );

            // return result
            return Command.CONTINUE_PROCESSING;

        }
        catch ( Exception e )
        {

            // log error message
            StringBuilder message = new StringBuilder();
            message.append( "Get MBean command failed which exception <" );
            message.append( StackTraceHelper.getStrackTrace( e ) );
            message.append( ">. Command failed." );

            // throw exception
            throw new CommandException( message.toString() );
        }

    }

    /**
     * Return true if parent object name is undefined.
     * @return true if parent object name is undefined.
     */
    boolean isParentUndefined()
    {
        return (this.parentObjectName == null);
    }

    /**
     * Handle case where the parent MBean object name is undefined. The context is either updated with a object name if
     * an MBean could be found. Other wise the context is updated with a null value.
     * 
     * @param context
     *            The command context, where the result is stored.
     * 
     * @return Chain command execution result.
     * 
     * @throws JMXManagmentException
     *             If getting the MBean fails.
     */
    private boolean getMBeanWithUndefinedParentMBean( Context context ) throws JMXManagmentException
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "Parent MBean object name not defined, will search for MBean in MBean server." );
        }

        // find MBean
        ObjectName objName = searchForMBean();

        // if MBean was found, then return it.
        if ( objName != null )
        {
            // log debug message
            if ( logger.isDebugEnabled() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "Successfully found MBean with object name <" );
                message.append( objName );
                message.append( ">." );
                logger.debug( message.toString() );
            }

            // store MBean in context
            context.put( this.RETRIEVED_MBEAN_KEY, objName );

            // flag command as complete and exit.
            return Command.CONTINUE_PROCESSING;

        }
        else
        {

            // log error message
            StringBuilder message = new StringBuilder();
            message.append( "Failed to get MBean. Command failed." );
            logger.error( message.toString() );

            // store null in context
            context.put( this.RETRIEVED_MBEAN_KEY, null );

            // flag command as failed and exit.
            return Command.PROCESSING_COMPLETE;
        }
    }

    /**
     * Invoke operation on parent MBean to get child MBean.
     * 
     * @param connection
     * @param method
     * @param objName
     * @param type
     * @param name
     * @return
     * @throws Exception
     * @throws InstanceNotFoundException
     * @throws MBeanException
     * @throws ReflectionException
     * @throws IOException
     */
    ObjectName getMbean( MBeanServerConnection connection, Method method, ObjectName objName, String type, String name )
        throws Exception, InstanceNotFoundException, MBeanException, ReflectionException, IOException
    {
        // invoke getter
        if ( method.getName().startsWith( "get" ) )
        {
            ObjectName mbean;
            mbean = (ObjectName) connection.getAttribute( objName, type );
            return mbean;
        }

        // invoke lookup
        if ( method.getName().startsWith( "lookup" ) )
        {

            // init operation name
            String operationName = method.getName();

            // init parameters
            Object[] params = new Object[] { name };
            String[] signature = new String[] { "java.lang.String" };

            // invoke lookupXX(..) on MBean
            ObjectName mbean;
            mbean = (ObjectName) connection.invoke( objName, operationName, params, signature );

            return mbean;
        }

        // Create error message
        StringBuilder message = new StringBuilder();
        message.append( "Unknown accesor method <" );
        message.append( method.getName() );
        message.append( ">. MBean retrival failed." );
        throw new IllegalArgumentException( message.toString() );
    }

    /**
     * Search for MBean in current MBeanServer.
     * 
     * @return Object name to found MBean. If no MBEan was found then null is returned.
     * 
     * @throws IllegalStateException
     *             if method is invoked after JMX session has disconnected from the mbean server.
     * 
     * @throws JMXManagmentException
     *             if MBean finding fails.
     */
    public ObjectName searchForMBean() throws JMXManagmentException
    {
            // define object name
            ObjectName queryObjectName;
            
            try
            {                
                // create query object name for BEA domain
                queryObjectName = WeblogicJMXNamingUtils.createQueryObjectNameInBeaDomain( childType, childName, ",*" ); 
                
                // do search
                ObjectName result = searchForMBean(queryObjectName);
                
                // if the search succeeded the return the result
                if( result != null ) return result;                

                
                // create query object name for BEA domain
                queryObjectName = WeblogicJMXNamingUtils.createQueryObjectNameInBeaDomain( childType, childName, "" ); 
                
                // do search
                result = searchForMBean(queryObjectName);
                
                // if the search succeeded the return the result
                if( result != null ) return result;                
                                
                // create query object name for security domain
                queryObjectName = WeblogicJMXNamingUtils.createQueryObjectNameInSecurityDomain( childType, childName, ",*" );
                
                // do search
                result = searchForMBean(queryObjectName);
                
                // if the search succeeded the return the result
                if( result != null ) return result;                

                // create query object name for security domain
                queryObjectName = WeblogicJMXNamingUtils.createQueryObjectNameInSecurityDomain( childType, childName, "" );
                
                // do search
                result = searchForMBean(queryObjectName);
                
                // if the search succeeded the return the result
                if( result != null ) return result;                

                // create query object name for security domain
                queryObjectName = WeblogicJMXNamingUtils.createQueryObjectNameInSecurityDomain( "", childName, "" );
                
                // do search
                result = searchForMBean(queryObjectName);
                
                // if the search succeeded the return the result
                if( result != null ) return result;                

                
                return null;
            }
            catch ( Exception e )
            {
                // throw exception if interfaces couldn't be resolved
                StringBuilder message =  new StringBuilder();
                message.append( "MBean search for <" );
                message.append( this.childName );
                message.append( "> with type <" );
                message.append( this.childName );                
                message.append( "> failed due to exception <"  );
                message.append( StackTraceHelper.getStrackTrace( e ) );
                message.append( ">."  );            
                throw new JMXManagmentException(message.toString(), e);            
            }
    }

    /**
     * Search for MBean in current MBeanServer.
     *
     * @param queryObjectName the object to search for.
     * 
     * @return Object name to found MBean. If no MBEan was found then null is returned.
     * 
     * @throws IllegalStateException
     *             if method is invoked after JMX session has disconnected from the MBean server.
     * 
     * @throws JMXManagmentException
     *             if MBean finding fails.
     */
    public ObjectName searchForMBean(ObjectName queryObjectName ) throws JMXManagmentException
    {

        // exit if not connected
        if ( !this.jmxSession.isConnected() )
        {
            String mesage = "Search for MBean failed, session isn't connected.";
            throw new IllegalStateException( mesage );
        }

        try
        {
            // log debug message
            if ( logger.isDebugEnabled() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "Starting to search for MBean with object name <" );
                message.append( queryObjectName );
                message.append( ">." );
                logger.debug( message.toString() );
            }

            // define query object
            QueryExp query = null;
                        
            // execute query
            Set result;
            result = connection.queryNames( queryObjectName, query );

            // log debug message
            if ( logger.isDebugEnabled() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "Number of matches found <" );
                message.append( result.size() );
                message.append( ">." );
                logger.debug( message.toString() );
            }

            // return null if no match is found
            if ( result.size() == 0 )
            {
                // log debug message
                if ( logger.isDebugEnabled() )
                {
                    StringBuilder message = new StringBuilder();
                    message.append( "No match found, will return null." );
                    logger.debug( message.toString() );
                }

                return null;
            }

            // log message if more that one matches is found
            if ( result.size() > 1 )
            {
                // log debug message
                if ( logger.isDebugEnabled() )
                {
                    StringBuilder message = new StringBuilder();
                    message.append( "Found more that one match. " );
                    message.append( "Returning first MBean found." );
                    logger.warn( message.toString() );
                }
            }

            // return first object name
            ObjectName foundObjectName;
            foundObjectName = (ObjectName) result.iterator().next();
            return foundObjectName;
        }
        catch ( IOException e )
        {
            throw new JMXManagmentException( "Search for MBean failed.", e );
        }
        catch ( NullPointerException e )
        {
            throw new JMXManagmentException( "Search for MBean failed", e );
        }
    }
    
}
