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

import javax.management.MBeanServerConnection;

import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionException;

/**
 * A JMX session presents a JMX connection to a MBean server. 
 * The JMX session is used to manage the MBean located in the MBean Server.
 */
public interface JMXSession extends Session
{

    /**
     * Connect to MBean server.
     * 
     * @param user
     *            User name used to authenticate identity with MBean server.
     * @param password
     *            Password used to authenticate identity with MBean server.
     * @param port
     *            TCP port used to access MBean server.
     * @param host
     *            Host name used to access MBean server.
     * @param urlPath
     *            URL path used to access MBean server.
     * 
     * @throws SessionException If connect operation fails.
     */
    public void connect( String user, String password, int port, String host, String urlPath )throws SessionConnectException;

    /**
     * Connect to MBean server.
     * 
     * @param protocol network protocol used to connec to server.
     * @param user User name used to authenticate identity with MBean server.
     * @param password Password used to authenticate identity with MBean server.
     * @param port TCP port used to access MBean server.
     * @param host Host name used to access MBean server.
     * @param urlPath URL path used to access MBean server.
     * 
     * @throws SessionException If connect operation fails.
     */
    public void connect( String protocol, String user, String password, int port, String host, String urlPath )throws SessionConnectException;
    
    /**
     * Connect to MBean server.
     * 
     * @param MBean Server connection.
     */
    public void connect( MBeanServerConnection connection);
    
    /**
     * Returns true if JMX session is connected to an MBean server.
     * 
     * @return true if JMX session is connected to an MBean server.
     */
    public boolean isConnected();

    /**
     * Validate whether the JMX session is ready for editing of the MBeans. Returns true if the session is connected and
     * an edit session is activated.
     * 
     * @return True if the JMX Session is ready for MBean editing.
     */
    public boolean isSessionConfiguredForEditing();

    /**
     * Returns JMX MBeanServerConnection instance for the current session.
     * 
     * @return JMX MBeanServerConnection instance for the current session.
     */
    public MBeanServerConnection getMBeanServerConnection();
    
}
