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


package com.alpha.pineapple.resource.weblogic.jmx;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import weblogic.management.configuration.DomainMBean;
import weblogic.management.configuration.SecurityConfigurationMBean;
import weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean;
import weblogic.management.security.RealmMBean;
import weblogic.management.security.authentication.AuthenticationProviderMBean;
import weblogic.management.security.authentication.GroupEditorMBean;
import weblogic.management.security.authentication.GroupRemoverMBean;
import weblogic.management.security.authentication.UserEditorMBean;
import weblogic.management.security.authorization.RoleEditorMBean;
import weblogic.management.security.authorization.RoleMapperMBean;

import com.alpha.pineapple.plugin.weblogic.jmx.management.JMXManagmentException;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxUtils;

public class JMXSecurityHelper
{

    // resource id for global role
    public static final String GLOBAL_ROLE = "global-role";

    // WebLogic role mapper
    public static final String ROLEMAPPER_NAME = "XACMLRoleMapper";

    // WebLogic authentication provider
    public static final String AUTHENTICATIONPROVIDER_NAME = "DefaultAuthenticator";

    // WebLogic domain runtime MBean server
    public static final String MBEANSERVER = "weblogic.management.mbeanservers.domainruntime";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * JMX Factory
     */
    JmxUtils jmxFactory;


    /**
     * WebLogic JMX session
     */
    WeblogicJMXEditSession session;

    /**
     * JMXSecurityHelper constructor.
     * 
     * @param session Weblogic JMX edit session. 
     * 
     */
    JMXSecurityHelper(WeblogicJMXEditSession  session)
    {
        this.session = session;
    	
        // create jmx factory
        jmxFactory = new JmxUtils();
    }


    /**
     * Get security realm MBean.
     * 
     * @return Get security realm MBean.
     * 
     * @throws Exception If MBean retrival fails.
     */
    RealmMBean getRealmMBean() throws Exception
    {
        // get mbean server connection
        MBeanServerConnection connection;
        connection = session.getMBeanServerConnection();

        // get domain runtime service object name
        ObjectName serviceObjectName;
        serviceObjectName = JmxUtils.getDomainRuntimeServiceObjectName();

        // define proxy interface
        Class<DomainRuntimeServiceMBean> interfaceClass = DomainRuntimeServiceMBean.class;

        // get runtime service
        DomainRuntimeServiceMBean runtimeServiceProxy;
        runtimeServiceProxy = jmxFactory.createMBeanProxy( connection, serviceObjectName, interfaceClass );

        // get domain mbean
        DomainMBean domainMBean;
        domainMBean = runtimeServiceProxy.getDomainConfiguration();

        // get security config
        SecurityConfigurationMBean securityConfigMBean;
        securityConfigMBean = domainMBean.getSecurityConfiguration();

        // get default realm
        RealmMBean realmMBean;
        realmMBean = securityConfigMBean.getDefaultRealm();
        return realmMBean;
    }

    /**
     * Get authentication provider form security realm.
     * 
     * @param realmMBean
     *            Security realm MBean.
     * @return authentication provider form security realm.
     * 
     * @throws JMXManagmentException
     *             if authentication provider wans't found.
     */
    AuthenticationProviderMBean getAuthenticationProvider( RealmMBean realmMBean ) throws JMXManagmentException
    {

        // get authentication providers
        AuthenticationProviderMBean[] providers;
        providers = realmMBean.getAuthenticationProviders();

        // get requested authentication provider
        AuthenticationProviderMBean requestedProvider = null;
        for ( AuthenticationProviderMBean provider : providers )
        {
            String name = provider.getName();
            if ( name.equalsIgnoreCase( AUTHENTICATIONPROVIDER_NAME ) )
            {
                requestedProvider = provider;
                break;
            }
        }

        // throw exception if provider not found
        if ( requestedProvider == null )
        {
            String message = "Authentication provider not found.";
            throw new JMXManagmentException( message );
        }

        return requestedProvider;
    }

    /**
     * Get role mapper MBean.
     * 
     * @param realmMBean
     *            Security realm MBean.
     * 
     * @return role mapper MBean.
     * @throws JMXManagmentException
     *             if role mapper wasn't found.
     */
    RoleMapperMBean getRoleMapper( RealmMBean realmMBean ) throws JMXManagmentException
    {

        // get role mappers
        RoleMapperMBean[] mappers;
        mappers = realmMBean.getRoleMappers();

        // get requested authentication provider
        RoleMapperMBean requestedMapper = null;
        for ( RoleMapperMBean mapper : mappers )
        {
            String name = mapper.getName();
            if ( name.equalsIgnoreCase( ROLEMAPPER_NAME ) )
            {
                requestedMapper = mapper;
                break;
            }
        }

        // throw exception if provider not found
        if ( requestedMapper == null )
        {
            String message = "Authentication provider not found.";
            throw new JMXManagmentException( message );
        }

        return requestedMapper;
    }

    /**
     * Create user.
     * 
     * @param user
     *            User name.
     * @param password
     *            Password.
     * @param description
     *            Description of user.
     * 
     * @throws JMXManagmentException
     *             if user creation fails.
     */
    public void createUser( String user, String password, String description ) throws JMXManagmentException
    {

        try
        {
            // get security realm
            RealmMBean realmMBean = getRealmMBean();

            // get authentication provider
            AuthenticationProviderMBean provider;
            provider = getAuthenticationProvider( realmMBean );

            // typecast provider to user editor mbean interface
            UserEditorMBean userEditor;
            userEditor = (UserEditorMBean) provider;

            // create user
            userEditor.createUser( user, password, description );

            // log message
            StringBuilder message = new StringBuilder();
            message.append( "Created user <" );
            message.append( user );
            message.append( ">." );
            logger.debug( message.toString() );

        }
        catch ( Exception e )
        {
            throw new JMXManagmentException( "User creation failed.", e );
        }
    }

    /**
     * delete user.
     * 
     * @param user
     *            User name.
     * 
     * @throws JMXManagmentException
     *             if user deletion fails.
     */
    public void deleteUser( String user ) throws JMXManagmentException
    {
        try
        {
            // get security realm
            RealmMBean realmMBean = getRealmMBean();

            // get authentication provider
            AuthenticationProviderMBean provider;
            provider = getAuthenticationProvider( realmMBean );

            // typecast provider to user editor mbean interface
            UserEditorMBean userEditor;
            userEditor = (UserEditorMBean) provider;

            // create user
            userEditor.removeUser( user );

            // log message
            StringBuilder message = new StringBuilder();
            message.append( "Deleted user <" );
            message.append( user );
            message.append( ">." );
            logger.debug( message.toString() );

        }
        catch ( Exception e )
        {
            throw new JMXManagmentException( "User deletion failed.", e );
        }
    }

    /**
     * Query whether user exists.
     * 
     * @param user
     *            User name.
     * 
     * @return Returns true if user exists.
     * 
     * @throws JMXManagmentException
     *             if user read fails.
     */
    public boolean userExists( String user ) throws JMXManagmentException
    {

        try
        {
            // get security realm
            RealmMBean realmMBean = getRealmMBean();

            // get authentication provider
            AuthenticationProviderMBean provider;
            provider = getAuthenticationProvider( realmMBean );

            // typecast provider to user editor mbean interface
            UserEditorMBean userEditor;
            userEditor = (UserEditorMBean) provider;

            // read user
            boolean result = userEditor.userExists( user );

            // log message
            StringBuilder message = new StringBuilder();
            message.append( "User <" );
            message.append( user );
            message.append( "> exists query result = " );
            message.append( result );
            logger.debug( message.toString() );

            // return result
            return result;

        }
        catch ( Exception e )
        {
            throw new JMXManagmentException( "User query failed.", e );
        }
    }

    /**
     * Create group
     * 
     * @param group
     *            Group name.
     * @param description
     *            Description of group.
     * 
     * @throws JMXManagmentException
     */
    public void createGroup( String group, String description ) throws JMXManagmentException
    {

        try
        {

            // get security realm
            RealmMBean realmMBean = getRealmMBean();

            // get authentication provider
            AuthenticationProviderMBean provider;
            provider = getAuthenticationProvider( realmMBean );

            // typecast provider to group editor mbean interface
            GroupEditorMBean groupEditor;
            groupEditor = (GroupEditorMBean) provider;

            // create group
            groupEditor.createGroup( group, description );

            // log message
            StringBuilder message = new StringBuilder();
            message.append( "Created group <" );
            message.append( group );
            message.append( ">." );
            logger.debug( message.toString() );

        }
        catch ( Exception e )
        {
            throw new JMXManagmentException( "Group creation failed.", e );
        }
    }

    /**
     * Create group
     * 
     * @param group
     *            Group name.
     * @throws JMXManagmentException
     */
    public void deleteGroup( String group ) throws JMXManagmentException
    {
        try
        {

            // get security realm
            RealmMBean realmMBean = getRealmMBean();

            // get authentication provider
            AuthenticationProviderMBean provider;
            provider = getAuthenticationProvider( realmMBean );

            // typecast provider to group remover mbean interface
            GroupRemoverMBean groupEditor;
            groupEditor = (GroupRemoverMBean) provider;

            // delete group
            groupEditor.removeGroup( group );

            // log message
            StringBuilder message = new StringBuilder();
            message.append( "Deleted group <" );
            message.append( group );
            message.append( ">." );
            logger.debug( message.toString() );

        }
        catch ( Exception e )
        {
            throw new JMXManagmentException( "Group deletion failed.", e );
        }

    }

    /**
     * Query whether group exists.
     * 
     * @param group
     *            Group name.
     * 
     * @return Returns true if group exists.
     * 
     * @throws JMXManagmentException
     *             if group read fails.
     */
    public boolean groupExists( String group ) throws JMXManagmentException
    {

        try
        {
            // get security realm
            RealmMBean realmMBean = getRealmMBean();

            // get authentication provider
            AuthenticationProviderMBean provider;
            provider = getAuthenticationProvider( realmMBean );

            // typecast provider to group editor mbean interface
            GroupEditorMBean groupEditor;
            groupEditor = (GroupEditorMBean) provider;

            // read group
            boolean result = groupEditor.groupExists( group );

            // log message
            StringBuilder message = new StringBuilder();
            message.append( "Group <" );
            message.append( group );
            message.append( "> exists query result = " );
            message.append( result );
            logger.debug( message.toString() );

            // return result
            return result;

        }
        catch ( Exception e )
        {
            throw new JMXManagmentException( "Group query failed.", e );
        }
    }

    public void addUserToGroup( String user, String group ) throws JMXManagmentException
    {

        try
        {

            // get security realm
            RealmMBean realmMBean = getRealmMBean();

            // get authentication provider
            AuthenticationProviderMBean provider;
            provider = getAuthenticationProvider( realmMBean );

            // typecast provider to group editor mbean interface
            GroupEditorMBean groupEditor;
            groupEditor = (GroupEditorMBean) provider;

            // add user to group
            groupEditor.addMemberToGroup( group, user );

            // log message
            StringBuilder message = new StringBuilder();
            message.append( "Added user <" );
            message.append( user );
            message.append( "> to group <" );
            message.append( group );
            message.append( ">." );
            logger.debug( message.toString() );

        }
        catch ( Exception e )
        {
            throw new JMXManagmentException( "Adding user to group failed.", e );
        }
    }

    /**
     * Queries whether a user or a group is member of a group.
     * 
     * @param group
     *            The group which is searched for membership.
     * @param member
     *            The user or group for which membership is searched.
     * @param recursive
     *            If set to true the the search extends to any groups which are mebers of the searched group.
     * 
     * @return Returns true if member belong to group.
     * @throws JMXManagmentException
     *             if membership search fails.
     */
    public boolean isMemberOfGroup( String group, String member, boolean recursive ) throws JMXManagmentException
    {
        try
        {
            // get security realm
            RealmMBean realmMBean = getRealmMBean();

            // get authentication provider
            AuthenticationProviderMBean provider;
            provider = getAuthenticationProvider( realmMBean );

            // typecast provider to group editor mbean interface
            GroupEditorMBean groupEditor;
            groupEditor = (GroupEditorMBean) provider;

            // read group
            boolean result = groupEditor.isMember( group, member, recursive );

            // log message
            StringBuilder message = new StringBuilder();
            message.append( "Query for member <" );
            message.append( member );
            message.append( "> membership of group <" );
            message.append( group );
            message.append( "> result = " );
            message.append( result );
            logger.debug( message.toString() );

            // return result
            return result;

        }
        catch ( Exception e )
        {
            throw new JMXManagmentException( "Group membership query failed.", e );
        }

    }

    /**
     * Create role.
     * 
     * @param resourceId
     *            Id of resource which should be scoped by the rule. If parameter is null, then the role is scope by
     *            WebLogic as global. If the value of the parameter is "globale-role" then the role is also created as a
     *            global role.
     * @param name
     *            name of role.
     * @param expression
     *            Role expression, i.e. Grp(testgroup)
     * @throws JMXManagmentException
     *             if role creation fails.
     */
    public void createRole( String resourceId, String name, String expression ) throws JMXManagmentException
    {

        try
        {
            // get security realm
            RealmMBean realmMBean = getRealmMBean();

            // get role mapper
            RoleMapperMBean mapper = getRoleMapper( realmMBean );

            // typecast mapper to role editor mbean interface
            RoleEditorMBean roleEditor;
            roleEditor = (RoleEditorMBean) mapper;

            // determine if role should be created with global scope
            resourceId = setResourceScope( resourceId );

            // create role
            roleEditor.createRole( resourceId, name, expression );

            // log message
            StringBuilder message = new StringBuilder();
            message.append( "Created role <" );
            message.append( name );
            message.append( "> with expression <" );
            message.append( expression );
            message.append( "> for resource <" );
            message.append( resourceId );
            message.append( ">." );
            logger.debug( message.toString() );

        }
        catch ( Exception e )
        {
            throw new JMXManagmentException( "Role creation failed.", e );
        }
    }

    /**
     * Queries whether a role exists.
     * 
     * @param resourceId
     *            Id of resource which should be scoped by the rule. If parameter is null, then the role is scope by
     *            WebLogic as global. If the value of the parameter is "globale-role" then the role is also interpreted
     *            as a global role.
     * @param name
     *            Name of role.
     * 
     * @return Returns true if role exists.
     * @throws JMXManagmentException
     *             If role query fails.
     */
    public boolean roleExists( String resourceId, String name ) throws JMXManagmentException
    {

        try
        {
            // get security realm
            RealmMBean realmMBean = getRealmMBean();

            // get role mapper
            RoleMapperMBean mapper = getRoleMapper( realmMBean );

            // typecast mapper to role editor mbean interface
            RoleEditorMBean roleEditor;
            roleEditor = (RoleEditorMBean) mapper;

            // determine if role should be created with global scope
            resourceId = setResourceScope( resourceId );

            // read role
            boolean result = roleEditor.roleExists( resourceId, name );

            // log message
            // log message
            StringBuilder message = new StringBuilder();
            message.append( "Query for role with resource id <" );
            message.append( resourceId );
            message.append( "> and name <" );
            message.append( name );
            message.append( "> result = " );
            message.append( result );
            logger.debug( message.toString() );

            return result;

        }
        catch ( Exception e )
        {
            throw new JMXManagmentException( "Role query failed.", e );
        }

    }

    /**
     * Determine if role should be created with global scope.
     * 
     * @param resourceId
     *            resource Identifier of role.
     * 
     * @return null if role should be created with global scope.
     */
    String setResourceScope( String resourceId )
    {
        // determine if role should be created with global scope
        if ( resourceId != null )
        {
            if ( resourceId.equalsIgnoreCase( GLOBAL_ROLE ) )
            {
                resourceId = null;

                // log message
                StringBuilder message = new StringBuilder();
                message.append( "Role resource id <" );
                message.append( resourceId );
                message.append( "> interpreted as global role." );
                logger.debug( message.toString() );

                // set global scope
                resourceId = null;
            }
        }
        return resourceId;
    }

    /**
     * Delete role.
     * 
     * @param resourceId
     *            Id of resource which should be scoped by the rule. If parameter is null, then the role is scope by
     *            WebLogic as global. If the value of the parameter is "globale-role" then the role is also created as a
     *            global role.
     * @param name
     *            name of role.
     * @throws JMXManagmentException
     *             if role deletion fails.
     * 
     * @throws JMXManagmentException
     *             if role deletion fails.
     */
    public void deleteRole( String resourceId, String name ) throws JMXManagmentException
    {

        try
        {
            // get security realm
            RealmMBean realmMBean = getRealmMBean();

            // get role mapper
            RoleMapperMBean mapper = getRoleMapper( realmMBean );

            // typecast mapper to role editor mbean interface
            RoleEditorMBean roleEditor;
            roleEditor = (RoleEditorMBean) mapper;

            // determine if role should be created with global scope
            resourceId = setResourceScope( resourceId );

            // create role
            roleEditor.removeRole( resourceId, name );

            // log message
            StringBuilder message = new StringBuilder();
            message.append( "Deleted role <" );
            message.append( name );
            message.append( "> for resource <" );
            message.append( resourceId );
            message.append( ">." );
            logger.debug( message.toString() );

        }
        catch ( Exception e )
        {
            throw new JMXManagmentException( "Role deletion failed.", e );
        }

    }

}
