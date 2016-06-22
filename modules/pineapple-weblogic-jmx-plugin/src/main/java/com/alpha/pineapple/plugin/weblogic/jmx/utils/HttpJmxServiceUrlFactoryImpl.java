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

import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.EDIT_SERVER_URLPATH;
import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.WEBLOGIC_PROTOCOL_PROVIDER_PACKAGES;

import java.util.Hashtable;

import javax.annotation.Resource;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Implementation of the {@linkplain JmxServiceUrlFactory}
 * for creation of HTTP based service URLs.
 */
public class HttpJmxServiceUrlFactoryImpl implements JmxServiceUrlFactory {

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
	
	@Override
	public Hashtable<String, String> createConnectionProperties(String user, String password) {
        Hashtable<String, String> h = new Hashtable<String, String>();
        h.put( Context.SECURITY_PRINCIPAL, user );
        h.put( Context.SECURITY_CREDENTIALS, password );
        h.put( JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, WEBLOGIC_PROTOCOL_PROVIDER_PACKAGES );
        return h;
	}

	@Override
    public JMXServiceURL createEditMBeanServerUrl(JmxProtool protocol, String host, int port) throws Exception {
		if((protocol != JmxProtool.HTTP) && (protocol != JmxProtool.HTTPS)) {			
        	Object[] args = { protocol };    	        	
        	String message = messageProvider.getMessage("hjsuf.error", args );        	
			throw new IllegalArgumentException(message);
		}		
        return new JMXServiceURL( protocol.toString(), host, port, EDIT_SERVER_URLPATH);				
	}		
}
