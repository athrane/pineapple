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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginSession;
import com.alpha.pineapple.plugin.agent.AgentConstants;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;

/**
 * Implementation of the <code>AgentSession</code> interface.
 */
@PluginSession
public class AgentSessionImpl implements AgentSession {

	/**
	 * Null request.
	 */
	final static Object NULL_REQUEST = null;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Agent resource.
	 */
	com.alpha.pineapple.model.configuration.Resource resource;

	/**
	 * Resource credential.
	 */
	Credential credential;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Resource property getter.
	 */
	@Resource
	ResourcePropertyGetter propertyGetter;

	/**
	 * Rest template.
	 */
	@Resource
	RestTemplate restTemplate;

	/**
	 * Agent host.
	 */
	String host;

	/**
	 * Agent port.
	 */
	int port;

	/**
	 * AgentSessionImpl no-arg constructor.
	 * 
	 * @throws Exception
	 *             If Session creation fails.
	 */
	public AgentSessionImpl() throws Exception {
		super();
	}

	public void connect(com.alpha.pineapple.model.configuration.Resource resource, Credential credential)
			throws SessionConnectException {
		// validate parameters
		Validate.notNull(resource, "resource is undefined.");
		Validate.notNull(credential, "credential is undefined.");

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { resource, credential.getId() };
			String message = messageProvider.getMessage("as.connect_start", args);
			logger.debug(message);
		}

		// store in fields
		this.credential = credential;
		this.resource = resource;

		try {
			// create resource property getter
			ResourcePropertyGetter getter = new ResourcePropertyGetter(resource);

			// get resource attributes
			String host = getter.getProperty("host");
			int port = Integer.parseInt(getter.getProperty("port", AgentConstants.DEFAULT_PORT));
			int connectTimeOut = Integer.parseInt(getter.getProperty("timeout", AgentConstants.DEFAULT_TIMEOUT));

			// get credential attributes
			String user = credential.getUser();
			String password = credential.getPassword();

			connect(host, port, user, password, connectTimeOut);
		} catch (Exception e) {

			Object[] args = { resource.getId(), e };
			String message = messageProvider.getMessage("as.connect_failure", args);
			throw new SessionConnectException(message, e);
		}

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { resource.getId() };
			String message = messageProvider.getMessage("as.connect_completed", args);
			logger.debug(message);
		}

	}

	@Override
	public void connect(String host, int port, String user, String password, int timeOut)
			throws SessionConnectException {
		Validate.notNull(host, "host is undefined.");
		Validate.notNull(port, "port is undefined.");
		Validate.notNull(user, "user is undefined.");
		Validate.notNull(password, "password is undefined.");
		Validate.notNull(timeOut, "timeOut is undefined.");

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { user, host, port };
			String message = messageProvider.getMessage("as.connect_start2", args);
			logger.debug(message);
		}

		try {
			// set fields
			this.host = host;
			this.port = port;

			// assert host is alive

		} catch (Exception e) {

			Object[] args = { host, port, user, e };
			String message = messageProvider.getMessage("as.connect_failure2", args);
			throw new SessionConnectException(message, e);
		}

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { host, port, user };
			String message = messageProvider.getMessage("as.connect_completed2", args);
			logger.debug(message);
		}
	}

	public void disconnect() throws SessionDisconnectException {

		// exit if not connected.
		if (!isConnected()) {
			logger.error(messageProvider.getMessage("as.disconnect_notconnected"));
			return;
		}
	}

	@Override
	public <T> T httpGetForObject(String urlPath, Class<T> responseType) {
		String serviceUrl = createServiceUrl(urlPath);

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { serviceUrl };
			String message = messageProvider.getMessage("as.get_serviceurl_info", args);
			logger.debug(message);
		}

		// invoke get
		return restTemplate.getForObject(serviceUrl, responseType);
	}

	@Override
	public void httpPost(String urlPath, MultiValueMap<String, Object> vars) {
		String serviceUrl = createServiceUrl(urlPath);

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { serviceUrl };
			String message = messageProvider.getMessage("as.post_serviceurl_info", args);
			logger.debug(message);
		}

		// invoke post
		restTemplate.postForObject(serviceUrl, NULL_REQUEST, String.class, vars);
	}

	@Override
	public URI httpPostForLocation(String serviceUrl, String contentType) throws Exception {

		// create arguments
		Map<String, String> urlVariables = new HashMap<String, String>();
		urlVariables.put(AgentConstants.CONTENT_TYPE_KEY, contentType);

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { serviceUrl };
			String message = messageProvider.getMessage("as.post_serviceurl_info", args);
			logger.debug(message);
		}

		// invoke post
		URI location = restTemplate.postForLocation(serviceUrl, NULL_REQUEST, urlVariables);

		// validate location
		validateLocation(serviceUrl, location);

		// log debug message
		if (logger.isDebugEnabled()) {
			String locationAsString = null;
			if (location != null)
				locationAsString = location.toASCIIString();
			Object[] args = { locationAsString };
			String message = messageProvider.getMessage("as.location_info", args);
			logger.debug(message);
		}

		return location;
	}

	@Override
	public void httpPost(String urlPath, HttpEntity<Object> request) {
		String serviceUrl = createServiceUrl(urlPath);

		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setBufferRequestBody(false);
		restTemplate.setRequestFactory(factory);

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { serviceUrl };
			String message = messageProvider.getMessage("as.post_serviceurl_info", args);
			logger.debug(message);
		}

		// invoke post
		restTemplate.postForObject(serviceUrl, request, String.class);
	}

	@Override
	public void httpPost(String urlPath) {
		String serviceUrl = createServiceUrl(urlPath);

		if (logger.isDebugEnabled()) {
			Object[] args = { serviceUrl };
			String message = messageProvider.getMessage("as.post_serviceurl_info", args);
			logger.debug(message);
		}

		restTemplate.postForObject(serviceUrl, NULL_REQUEST, String.class);
	}

	@Override
	public void httpDelete(String urlPath) {
		String serviceUrl = createServiceUrl(urlPath);

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { serviceUrl };
			String message = messageProvider.getMessage("as.delete_serviceurl_info", args);
			logger.debug(message);
		}

		// create arguments
		Map<String, String> urlVariables = new HashMap<String, String>();

		// invoke delete
		restTemplate.delete(serviceUrl, urlVariables);
	}

	@Override
	public void httpDelete(String urlPath, Map<String, String> urlVariables) {
		String serviceUrl = createServiceUrl(urlPath);

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { serviceUrl };
			String message = messageProvider.getMessage("as.delete_serviceurl_info", args);
			logger.debug(message);
		}

		// invoke delete
		restTemplate.delete(serviceUrl, urlVariables);
	}

	public com.alpha.pineapple.model.configuration.Resource getResource() {
		return this.resource;
	}

	public Credential getCredential() {
		return this.credential;
	}

	public boolean isConnected() {
		return (restTemplate != null);
	}

	@Override
	public String createServiceUrl(String urlPath) {
		String serviceUrl = new StringBuilder().append("http://").append(host).append(":")
				.append(Integer.toString(port)).append(urlPath).toString();
		return serviceUrl;
	}

	@Override
	public String getHostName() {
		return new StringBuilder().append("http://").append(host).append(":").append(Integer.toString(port)).toString();
	}

	@Override
	public void addServiceUrlMessage(String serviceUrl, ExecutionResult result) {
		Object[] args = { serviceUrl };
		String key = messageProvider.getMessage("as.agent_communication_info_key");
		String message = messageProvider.getMessage("as.execution_info", args);
		result.addMessage(key, message);
		return;
	}

	/**
	 * Validate location. Throws exception if returned location is null.
	 * 
	 * @param serviceUrl
	 *            service URL used for error message.
	 * @param location
	 *            location to validate.
	 * 
	 * @throws Execution
	 *             if validation fails.
	 */
	void validateLocation(String serviceUrl, URI location) throws Exception {
		if (location != null)
			return;
		Object[] args = { serviceUrl };
		String message = messageProvider.getMessage("as.location_validation_error", args);
		throw new PluginExecutionFailedException(message);
	}

}
