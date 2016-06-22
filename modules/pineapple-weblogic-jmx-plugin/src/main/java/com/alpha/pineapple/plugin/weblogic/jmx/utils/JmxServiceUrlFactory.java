/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2013 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.   
 */

package com.alpha.pineapple.plugin.weblogic.jmx.utils;

import java.util.Hashtable;

import javax.management.remote.JMXServiceURL;

/**
 * Factory for creation of {@linkplain JMXServiceURL} instances
 * and connection properties.
 */
public interface JmxServiceUrlFactory {

	/**
	 * Defines supported protocols for JMX communication.
	 */
	enum JmxProtool {
		T3,
		T3S,
		HTTP,
		HTTPS,
		IIOP,
		IIOPS,
		RMI,
		JDK_RMI,
		JDK_IIOP,
	}

    /**
     * Create JMX connection properties.
     * 
     * @param user user.
     * @param password password.
     * 
     * @return JMX connection properties object containing connection properties for accessing MBean server.
     */	
    public Hashtable<String, String> createConnectionProperties( String user, String password ); 
	    
    /**
     * Create JMX service URL to access remote MBean server.
     * 
     * @param protocol The protocol part of the URL. If null, defaults to jmxmp. 
     * @param host the host part of the URL. If null, defaults to the local host name. 
     * If it is a numeric IPv6 address, it can optionally be enclosed in square brackets [].
     * @param port the port part of the URL.  
     * 
     * @return JMX service URL to access remote MBean server.
     * 
     * @throws Exception if URL creation fails.
     */
    public JMXServiceURL createEditMBeanServerUrl(JmxProtool protocol, String host, int port) throws Exception;
            
}
