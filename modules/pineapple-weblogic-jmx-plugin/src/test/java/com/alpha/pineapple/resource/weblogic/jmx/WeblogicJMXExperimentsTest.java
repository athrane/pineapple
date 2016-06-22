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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import weblogic.management.mbeanservers.MBeanTypeService;
import weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean;

import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxUtils;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;

public class WeblogicJMXExperimentsTest
{	
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

	/**
	 * WebLogic JMX session object mother.
	 */
	ObjectMotherWeblogicJmxSession sessionMother;
    
    /**
     * JMx security helper
     */
    JMXSecurityHelper helper;

    WeblogicJMXEditSession session;

    @Before
    public void setUp() throws Exception
    {
		// create session object mother;
		sessionMother = new ObjectMotherWeblogicJmxSession();

        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();
        
        helper = new JMXSecurityHelper(session);        
    }

    @After
    public void tearDown() throws Exception
    {

        if ( session != null )
        {
            if ( session.isConnected() )
                session.disconnect();
            session = null;
        }
    }



    /**
     * Test that WebLogic MBeanTypeService can initialized/accessed.
     */
    @Test
    public void testCanObtainMBeanTypeService() throws Exception
    {
        // start edit session
        session.startEdit();

        String vhName = "unittest1-vh";
        String[] vhHostnames = { "secureawaretest" };

        // jmx connection
        MBeanServerConnection connection;
        connection = session.getMBeanServerConnection();

        // get jmx factory
        JmxUtils jmxFactory;
        jmxFactory = new JmxUtils();

        // get type service object name
        ObjectName objectName;
        objectName = JmxUtils.getTypeServiceMBeanObjectName();

        // define proxy interface
        Class<MBeanTypeService> interfaceClass = MBeanTypeService.class;

        // get type service
        MBeanTypeService typeServiceProxy;
        typeServiceProxy = jmxFactory.createMBeanProxy( connection, objectName, interfaceClass );

        
        // test
        assertNotNull( typeServiceProxy );
        String expected = objectName.getCanonicalName();
        assertEquals( expected, typeServiceProxy.OBJECT_NAME );

        // save and active
        session.saveAndActivate();
    }


    /**
     * Test that WebLogic domain runtime service can initialized/accessed.
     */
    @Test
    public void testCanObtainDomainRuntimeService() throws Exception
    {
        // get mbean server connection
        MBeanServerConnection connection;
        connection = session.getMBeanServerConnection();

        // get domain runtime service object name
        ObjectName serviceObjectName;serviceObjectName = JmxUtils.getDomainRuntimeServiceObjectName();

        // define proxy interface
        Class<DomainRuntimeServiceMBean> interfaceClass = DomainRuntimeServiceMBean.class;

        // get runtime service
        DomainRuntimeServiceMBean runtimeServiceProxy;
        runtimeServiceProxy = JmxUtils.createMBeanProxy( connection, serviceObjectName, interfaceClass );

        // test
        assertNotNull( runtimeServiceProxy );
        String expected = serviceObjectName.getCanonicalName();
        assertEquals( expected, runtimeServiceProxy.OBJECT_NAME );
    }

    /**
     * Test that user can be created using Typed API.
     */
    @Test
    public void testCreateUserUsingTypedAPI() throws Exception
    {
        // define user
        String user = "test__user_createuser";
        String password = "weblogic";
        String description = "User created by WeblogicJMXExperimentsTest";

        // fail if user exists
        if ( helper.userExists( user ) )
            fail( "user already exists" );

        // create user
        helper.createUser( user, password, description );

        // test
        assertTrue( helper.userExists( user ) );

        // clean up
        helper.deleteUser( user );
    }

    /**
     * Test that user can be delete using Typed API.
     */
    @Test
    public void testDeleteUserUsingTypedAPI() throws Exception
    {
        // define user
        String user = "test_user_deleteuser";
        String password = "weblogic";
        String description = "User created by WeblogicJMXExperimentsTest";

        // fail if user exists
        if ( helper.userExists( user ) )
            fail( "user already exists" );

        // create user
        helper.createUser( user, password, description );

        // delete user
        helper.deleteUser( user );

        // test
        assertFalse( helper.userExists( user ) );

    }

    /**
     * Test that group can be created using Typed API.
     */
    @Test
    public void testCreateGroupUsingTypedAPI() throws Exception
    {
        // define group
        String group = "test_group_creategroup";
        String description = "Group created by WeblogicJMXExperimentsTest";

        // fail if group exists
        if ( helper.groupExists( group ) )
            fail( "group already exists" );

        // define group
        helper.createGroup( group, description );

        // test
        assertFalse( helper.userExists( group ) );

        // clean up
        helper.deleteGroup( group );
    }

    /**
     * Test that group can be deleted using Typed API.
     */
    @Test
    public void testDeleteGroupUsingTypedAPI() throws Exception
    {
        // define group
        String group = "test_group_deletegroup";
        String description = "Group created by WeblogicJMXExperimentsTest";

        // fail if group exists
        if ( helper.groupExists( group ) )
            fail( "group already exists" );

        // create group
        helper.createGroup( group, description );

        // delete group
        helper.deleteGroup( group );

        // test
        assertFalse( helper.userExists( group ) );
    }

    /**
     * Test that user can be added to group can be created using Typed API.
     */
    @Test
    public void testAddUserToGroupUsingTypedAPI() throws Exception
    {
        // define user
        String user = "test_user_addusertogroup";
        String password = "weblogic";
        String description = "User created by WeblogicJMXExperimentsTest";

        // fail if user exists
        if ( helper.userExists( user ) )
            fail( "user already exists" );

        // create user
        helper.createUser( user, password, description );

        // define group
        String group = "test_group_addusertogroup";
        String description2 = "Group created by WeblogicJMXExperimentsTest";

        // fail if group exists
        if ( helper.groupExists( group ) )
            fail( "group already exists" );

        // create group
        helper.createGroup( group, description2 );

        // add user to group
        helper.addUserToGroup( user, group );

        // test
        String member = user;
        boolean recursive = false;
        assertTrue( helper.isMemberOfGroup( group, member, recursive ) );

        // clean up
        helper.deleteGroup( group );
        helper.deleteUser( user );
    }

    /**
     * Test that group can be created using Typed API.
     */
    @Test
    public void testCreateRoleUsingTypedAPI() throws Exception
    {
        // define role
        String resourceId = "global-role";
        String name = "test_role_createrole";
        String expression = "Grp(testgroup)";

        // fail if role exists
        if ( helper.roleExists( resourceId, name ) )
            fail( "role already exists" );

        // create role
        helper.createRole( resourceId, name, expression );

        // test
        assertTrue( helper.roleExists( resourceId, name ) );

        // clean up;
        helper.deleteRole( resourceId, name );
    }

    /**
     * Test that domains can be listed.
     */
    @Test
    public void testCanListDomains() throws Exception
    {
        // get mbean server connection
        MBeanServerConnection connection;
        connection = session.getMBeanServerConnection();

        // get domains
        String[] domains = connection.getDomains();

        // list domains
        for ( String domain : domains )
        {
            logger.debug( "Domain=" + domain );
        }
    }

}
