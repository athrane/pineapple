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


package com.alpha.pineapple.plugin.net.http;

import java.util.HashMap;

import org.apache.commons.httpclient.Header;

/**
 * Contains the result of a single HTTP Get method.
 */
public interface HttpInvocationResult
{	
	/**
	 * Get HTTP status code.
	 *  
	 */
	@Deprecated
    public int getStatusCode();

	/**
	 * Set HTTP status code. 
	 */
	@Deprecated	
    public void setStatusCode(int code);
        
    /**
     * Get host.
     *  
     */
	@Deprecated	
    public String getHost();
    
    /**
     * Set host. 
     */
	@Deprecated	
    public void setHost(String host);
    
    /**
     * Get environment.
     */
	@Deprecated	
    public String getEnvironment();

    /**
     * Set environment.
     */
	@Deprecated	
    public void setEnvironment(String environment);
    
    /**
     * Get location.
     */
	@Deprecated	
    public String getLocation();

    /**
     * Set location.
     */
	@Deprecated	
    public void setLocation(String location);
    
    /**
     * Get server.
     */
	@Deprecated	
    public String getServer();

    /**
     * Set server.
     */
	
    public void setServer(String server);
    
    /**
     * Get cookies.
     */
    public HashMap<String, String> getCookies();

    /**
     * Set cookies.
     */
    public void setCookies(HashMap<String, String> cookies);
        
    /**
     * Get HTTP response headers.
     */
    public Header[] getResponseHeaders();

    /**
     * set HTTP response headers.
     */
    public void setResponseHeaders(Header[] headers);
    
    /**
     * Return true if the result contains an property with the requested name.
     * 
     * @param name Name of requested property.
     * 
     * @return True if the result contains an property with the requested name.
     */
	public boolean containsProperty(String name);

	/**
	 * Return property value from property with requested name. 
	 * If no property with the requested name exists, then null is returned.
	 * 
	 * @param name Name of requested property.
	 * 
	 * @return Value of property with requested name.
	 */
	public Object getProperty(String name);

	/**
	 * Set property value.
	 * 
	 * @param name Name of the property whose value is set.
	 * 
	 * @param value The value of the property.
	 */
	public void setPropertyValue(String name, Object value);	   
	
}
