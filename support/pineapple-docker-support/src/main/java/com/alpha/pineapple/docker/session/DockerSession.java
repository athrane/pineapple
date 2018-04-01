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

package com.alpha.pineapple.docker.session;

import java.util.Map;

import com.alpha.pineapple.docker.model.rest.ContainerConfiguration;
import com.alpha.pineapple.docker.utils.RestResponseException;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionException;

/**
 * Session which provides access to a remote Docker daemon session.
 */
public interface DockerSession extends Session {

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
	 * @throws SessionException
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
	 * Invoke HTTP POST operation. Operation will be invoked with content type
	 * "application/json".
	 * 
	 * @param uriPath
	 *            URI path for service including template variables.
	 * @param request
	 *            HTTP request type.
	 * @param responseType
	 *            response type.
	 * 
	 * @return marshalled response object.
	 * 
	 * @throws RestResponseException
	 *             if invocation fails.
	 */
	public <T> T httpPostForObject(String uriPath, ContainerConfiguration request, Class<T> responseType)
			throws RestResponseException;

	/**
	 * Invoke HTTP POST operation. Operation will be invoked with content type
	 * "application/json".
	 * 
	 * @param uriPath
	 *            URI path for service including template variables.
	 * @param uriVariables
	 *            URI variables to be expanded by template.
	 * 
	 * @throws RestResponseException
	 *             if invocation fails.
	 */
	public void httpPost(String uriPath, Map<String, String> uriVariables) throws RestResponseException;

	/**
	 * Invoke HTTP POST operation. Operation will be invoked with content type
	 * "application/json".
	 * 
	 * @param uriPath
	 *            URI path for service including template variables.
	 * @param uriVariables
	 *            URI variables to be expanded by template.
	 * @param responseType
	 *            response type.
	 * 
	 * @return marshalled response object.
	 * 
	 * @throws RestResponseException
	 *             if invocation fails.
	 */
	public <T> T httpPostForObject(String uriPath, Map<String, String> uriVariables, Class<T> responseType)
			throws RestResponseException;

	/**
	 * Invoke HTTP POST operation with processing of the result which contains
	 * multiple JSON root elements. Operation will be invoked with content type
	 * "application/json".
	 * 
	 * @param uriPath
	 *            URI path for service including template variables.
	 * @param uriVariables
	 *            URI variables to be expanded by template.
	 * @param responseType
	 *            response type which must be defined as an array.
	 * 
	 * @return marshalled array of response objects.
	 * 
	 * @throws SessionException
	 *             parsing the JSON result fails.
	 * @throws RestResponseException
	 *             if invocation fails.
	 */
	public <T> T[] httpPostForObjectWithMultipleRootElements(String uriPath, Map<String, String> uriVariables,
			Class<T[]> responseType) throws SessionException;

	/**
	 * Invoke HTTP POST operation with processing of the result which contains
	 * multiple JSON root elements.
	 * 
	 * @param uriPath
	 *            URI path for service including template variables.
	 * @param uriVariables
	 *            URI variables to be expanded by template.
	 * @param responseType
	 *            response type which must be defined as an array.
	 * @param contentType
	 *            the content type that the operation will be invoked with.
	 * 
	 * @return marshalled array of response objects.
	 * 
	 * @throws SessionException
	 *             parsing the JSON result fails.
	 * @throws RestResponseException
	 *             if invocation fails.
	 */
	public <T> T[] httpPostForObjectWithMultipleRootElements(String uriPath, Map<String, String> uriVariables,
			Object request, Class<T[]> responseType, String contentType) throws SessionException;

	/**
	 * Invoke HTTP GET operation.
	 * 
	 * @param uriPath
	 *            URI path for service including template variables.
	 * @param uriVariables
	 *            URI variables to be expanded by template.
	 * @param responseType
	 *            response type.
	 * 
	 * @return marshalled response object.
	 * 
	 * @throws RestResponseException
	 *             if invocation fails.
	 */
	public <T> T httpGetForObject(String uriPath, Map<String, String> uriVariables, Class<T> responseType)
			throws RestResponseException;

	/**
	 * Invoke HTTP DELETEoperation.
	 * 
	 * @param uriPath
	 *            URI path for service including template variables.
	 * @param uriVariables
	 *            URI variables to be expanded by template.
	 * 
	 * @throws RestResponseException
	 *             if invocation fails.
	 */
	public void httpDelete(String uriPath, Map<String, String> uriVariables) throws RestResponseException;

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

}
