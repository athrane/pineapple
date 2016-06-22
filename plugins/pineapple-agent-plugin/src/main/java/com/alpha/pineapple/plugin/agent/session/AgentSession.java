/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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

package com.alpha.pineapple.plugin.agent.session;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionConnectException;

/**
 * Session which provides access to a remote Pineapple agent session.
 */
public interface AgentSession extends Session {

    /**
     * Connect to host using HTTP and password authentication.
     * 
     * @param host
     *            host to connect to.
     * @param port
     *            TCP port to connect to.
     * @param user
     *            user name.
     * @param password
     *            password.
     * @param timeOut
     *            connection time out in ms.
     * 
     * @throws SessionConnectException
     *             if connecting fails.
     */
    void connect(String host, int port, String user, String password, int timeOut) throws SessionConnectException;

    /**
     * Returns true session is connected to an host using SSH.
     * 
     * @return true session is connected to an host using SSH.
     */
    public boolean isConnected();

    /**
     * Invoke HTTP GET operation.
     * 
     * @param <T>
     * 
     * @param urlPath
     *            URL path for service including template variables.
     * @param responseType
     *            Type that returned object is converted to.
     * 
     * @return result returned by get operation.
     */
    public <T> T httpGetForObject(String urlPath, Class<T> responseType);

    /**
     * Invoke HTTP POST operation.
     * 
     * @param urlPath
     *            URL path for service including template variables.
     * @param urlVariables
     *            URL variables to be expanded by template.
     */
    void httpPost(String urlPath, MultiValueMap<String, Object> urlVariables);

    /**
     * Invoke HTTP POST operation and return location header value.
     * 
     * @param serviceUrl
     *            expanded service URL for service.
     * @param content
     *            type content type.
     * 
     * @return location header value as a URI.
     */
    URI httpPostForLocation(String serviceUrl, String contentType) throws Exception;

    /**
     * Invoke HTTP POST operation.
     * 
     * @param urlPath
     *            URL path for service including template variables.
     * @param request
     *            HTTP request.
     */
    void httpPost(String urlPath, HttpEntity<Object> request);

    /**
     * Invoke HTTP POST operation.
     * 
     * @param urlPath
     *            URL path for service.
     */
    void httpPost(String serviceUrl);

    /**
     * Invoke HTTP Delete operation.
     * 
     * @param serviceUrl
     *            expanded service URL for service.
     */
    void httpDelete(String serviceUrl) throws Exception;

    /**
     * Invoke HTTP Delete operation.
     * 
     * @param urlPath
     *            URL path for service including template variables.
     * @param urlVariables
     *            URL variables to be expanded by template.
     */
    void httpDelete(String urlPath, Map<String, String> urlVariables) throws Exception;

    /**
     * Get host name and port.
     * 
     * @return host name and port.
     */
    String getHostName();

    /**
     * Create service URL for REST service.
     * 
     * @param urlPath
     *            URL path which is appended to the URL.
     * 
     * @return service URL for REST service.
     */
    String createServiceUrl(String urlPath);

    /**
     * Add service URL message to execution result.
     * 
     * @param serviceUrl
     *            service URL.
     * @param result
     *            execution result to add the information to.
     * 
     */
    void addServiceUrlMessage(String serviceUrl, ExecutionResult result);

}
