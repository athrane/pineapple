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

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;

import com.alpha.pineapple.plugin.weblogic.jmx.utils.JmxServiceUrlFactory.JmxProtool;

/**
 * Factory for creation of JMX Service URL factory.
 */
public class JmxServiceUrlFactoryFactory {

	/**
	 * Factory for creation of connection properties for the T3 and T3S protocols.
	 */
	@Resource
	JmxServiceUrlFactory t3JmxServiceUrlFactory;

	/**
	 * Factory for creation of connection properties for the IIOP and IIOPS protocols.
	 */
	@Resource
	JmxServiceUrlFactory iiopJmxServiceUrlFactory;

	/**
	 * Factory for creation of connection properties for the RMI protocol.
	 */
	@Resource
	JmxServiceUrlFactory rmiJmxServiceUrlFactory;

	/**
	 * Factory for creation of connection properties for the HTTP protocol.
	 */
	@Resource
	JmxServiceUrlFactory httpJmxServiceUrlFactory;

	/**
	 * Factory for creation of connection properties for the IIOP and IIOPS protocols
	 * using JDK classes only.
	 */
	@Resource
	JmxServiceUrlFactory jdkIiopJmxServiceUrlFactory;

	/**
	 * Factory for creation of connection properties for the RMI protocol
	 * using JDK classes only.
	 */
	@Resource
	JmxServiceUrlFactory jdkRmiJmxServiceUrlFactory;
	
	/**
	 * Create JMX Service URL factory to support creation
	 * of URLs for particular protocol.
	 * 
	 * @param protocol protocol to create factory for.
	 *  
	 * @return JMX Service URL factory for particular protocol.
	 */	
	public JmxServiceUrlFactory getInstance(JmxProtool protocol) {
		Validate.notNull(protocol, "protocol argument is undefined");
		
		switch (protocol) {		
			case HTTP: return httpJmxServiceUrlFactory;
			case HTTPS: return httpJmxServiceUrlFactory;
			case IIOP: return iiopJmxServiceUrlFactory;
			case IIOPS: return iiopJmxServiceUrlFactory;
			case RMI: return rmiJmxServiceUrlFactory;
			case T3: return t3JmxServiceUrlFactory;
			case T3S: return t3JmxServiceUrlFactory;			
			case JDK_IIOP: return jdkIiopJmxServiceUrlFactory;
			case JDK_RMI: return jdkRmiJmxServiceUrlFactory;			
			default: throw new UnsupportedOperationException( "Support for protocol [" + protocol +"] isn't implemented yet.");			
		}
		
	}
}
