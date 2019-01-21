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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import com.alpha.pineapple.docker.DockerConstants;
import com.alpha.pineapple.docker.model.rest.ContainerConfiguration;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.plugin.PluginSession;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;
import com.alpha.pineapple.session.SessionException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of the <code>DockerSession</code> interface.
 */
@PluginSession
public class DockerSessionImpl implements DockerSession {

	/**
	 * Null request.
	 */
	final static Object NULL_REQUEST = null;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Docker resource.
	 */
	com.alpha.pineapple.model.configuration.Resource resource;

	/**
	 * Resource credential.
	 */
	Credential credential;

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "dockerMessageProvider")
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
	 * Rest template.
	 */
	@Resource
	RestTemplate restTemplateWithoutMessageConverters;

	/**
	 * Jackson object mapper.
	 */
	@Resource(name = "jacksonObjectMapperForRestTemplateWithoutMessageConverters")
	ObjectMapper jacksonObjectMapper;

	/**
	 * Docker host.
	 */
	String host;

	/**
	 * Docker port.
	 */
	int port;

	/**
	 * DockerSessionImpl no-arg constructor.
	 * 
	 * @throws Exception
	 *             If session creation fails.
	 */
	public DockerSessionImpl() throws Exception {
		super();
	}

	/**
	 * DockerSessionImpl constructor.
	 * 
	 * @param messageProvider
	 *            I18N message provider.
	 * @param propertyGetter
	 *            resource property getter.
	 * @param restTemplate
	 *            REST Template with message converters.
	 * @param restTemplateWithoutMessageConverters
	 *            REST Template without message converters.
	 * @param objectMapper
	 *            Jackson object mapper, used by REST Template without message converters.
	 * 
	 * @throws Exception
	 *             If session creation fails.
	 */
	public DockerSessionImpl(MessageProvider messageProvider, ResourcePropertyGetter propertyGetter,
			RestTemplate restTemplate, RestTemplate restTemplateWithoutMessageConverters, ObjectMapper objectMapper)
			throws Exception {

		this.messageProvider = messageProvider;
		this.propertyGetter = propertyGetter;
		this.restTemplate = restTemplate;
		this.restTemplateWithoutMessageConverters = restTemplateWithoutMessageConverters;
		this.jacksonObjectMapper = objectMapper;
	}

	public void connect(com.alpha.pineapple.model.configuration.Resource resource, Credential credential)
			throws SessionConnectException {
		// validate parameters
		Validate.notNull(resource, "resource is undefined.");
		Validate.notNull(credential, "credential is undefined.");

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { resource, credential.getId() };
			String message = messageProvider.getMessage("ds.connect_start", args);
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
			int port = Integer.parseInt(getter.getProperty("port", DockerConstants.DEFAULT_PORT));
			int connectTimeOut = Integer
					.parseInt(getter.getProperty("timeout", DockerConstants.DEFAULT_CONNECT_TIMEOUT));

			// get credential attributes
			String user = credential.getUser();
			String password = credential.getPassword();

			connect(host, port, user, password, connectTimeOut);
		} catch (Exception e) {

			Object[] args = { resource.getId(), e };
			String message = messageProvider.getMessage("ds.connect_failure", args);
			throw new SessionConnectException(message, e);
		}

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { resource.getId() };
			String message = messageProvider.getMessage("ds.connect_completed", args);
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
			String message = messageProvider.getMessage("ds.connect_start2", args);
			logger.debug(message);
		}

		try {
			// set fields
			this.host = host;
			this.port = port;

			// assert host is alive
			Map<String, String> uriVariables = new HashMap<String, String>();
			// Version version = httpGetForObject(VERSION_URI, uriVariables,
			// Version.class);

		} catch (Exception e) {

			Object[] args = { host, port, user, e };
			String message = messageProvider.getMessage("ds.connect_failure2", args);
			throw new SessionConnectException(message, e);
		}

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { host, port, user };
			String message = messageProvider.getMessage("ds.connect_completed2", args);
			logger.debug(message);
		}
	}

	public void disconnect() throws SessionDisconnectException {

		// exit if not connected.
		if (!isConnected()) {
			logger.error(messageProvider.getMessage("ds.disconnect_notconnected"));
			return;
		}
	}

	@Override
	public void httpPost(String uriPath, Map<String, String> uriVariables) {
		httpPostForObject(uriPath, uriVariables, String.class);
	}

	@Override
	public <T> T httpPostForObject(String uriPath, ContainerConfiguration request, Class<T> responseType) {
		String serviceUri = createServiceUrl(uriPath);
		Map<String, String> urlVariables = new HashMap<String, String>();
		setJsonContentType(urlVariables);
		URI expandedServiceUri = expandServiceUri(urlVariables, serviceUri);
		return restTemplate.postForObject(expandedServiceUri, request, responseType);
	}

	@Override
	public <T> T httpPostForObject(String uriPath, Map<String, String> uriVariables, Class<T> responseType) {
		String serviceUri = createServiceUrl(uriPath);
		setJsonContentType(uriVariables);
		URI expandedServiceUri = expandServiceUri(uriVariables, serviceUri);
		return restTemplate.postForObject(expandedServiceUri, NULL_REQUEST, responseType);
	}

	@Override
	public <T> T[] httpPostForObjectWithMultipleRootElements(String uriPath, Map<String, String> uriVariables,
			Class<T[]> responseType) throws SessionException {

		return httpPostForObjectWithMultipleRootElements(uriPath, uriVariables, NULL_REQUEST, responseType,
				MediaType.APPLICATION_JSON_VALUE);
	}

	@Override
	public <T> T[] httpPostForObjectWithMultipleRootElements(String uriPath, Map<String, String> uriVariables,
			Object request, Class<T[]> responseType, String contentType) throws SessionException {

		String serviceUri = createServiceUrl(uriPath);
		setContentType(uriVariables, contentType);
		URI expandedServiceUri = expandServiceUri(uriVariables, serviceUri);

		// invoke post
		// result is requested as string since Docker returns multiple JSON root elements
		String resultAsString = restTemplateWithoutMessageConverters.postForObject(expandedServiceUri, request,
				String.class);

		// transforms string with multiple JSON root elements into JSON array
		String jsonArrayAsString = transformStringToJsonArray(resultAsString);
		
		try {

			// parse string to JSON
			//jacksonObjectMapper = new ObjectMapper();
			T[] array = jacksonObjectMapper.readValue(jsonArrayAsString, responseType);
			return array;

		} catch (Exception e) {

			Object[] args = { serviceUri, jsonArrayAsString, e.getMessage() };
			String message = messageProvider.getMessage("ds.parse_json_failure", args);
			throw new SessionException(message, e);
		}
	}

	@Override
	public <T> T httpGetForObject(String uriPath, Map<String, String> uriVariables, Class<T> responseType) {
		String serviceUri = createServiceUrl(uriPath);
		setJsonContentType(uriVariables);
		URI expandedServiceUri = expandServiceUri(uriVariables, serviceUri);
		return restTemplate.getForObject(expandedServiceUri, responseType);
	}

	@Override
	public void httpDelete(String uriPath, Map<String, String> uriVariables) {
		String serviceUri = createServiceUrl(uriPath);
		setJsonContentType(uriVariables);
		URI expandedServiceUri = expandServiceUri(uriVariables, serviceUri);
		restTemplate.delete(expandedServiceUri);
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

	/**
	 * Expand service URI with variables.
	 * 
	 * @param uriVariables
	 *            URI variables.
	 * @param serviceUri
	 *            Service URI to expand.
	 * 
	 * @return expanded service URI.
	 */
	URI expandServiceUri(Map<String, String> uriVariables, String serviceUri) {
		UriTemplate uriTemplate = new UriTemplate(serviceUri);
		URI expandedServiceUri = uriTemplate.expand(uriVariables);

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { expandedServiceUri };
			String message = messageProvider.getMessage("ds.expanded_serviceuri_info", args);
			logger.debug(message);
		}

		return expandedServiceUri;
	}

	@Override
	public String getHostName() {
		return new StringBuilder().append("http://").append(host).append(":").append(Integer.toString(port)).toString();
	}

	/**
	 * Transform string into JSON array.
	 * 
	 * The string is transformed in three steps: 1) The string is decorated with
	 * JSON array delimiters; [..] 2) All carriage returns and new line chars are
	 * removed. 3) A comma is added between every array entry.
	 * 
	 * @param resultAsString
	 *            string which is transformed.
	 * 
	 * @return transformed string.
	 */
	String transformStringToJsonArray(String resultAsString) {
		// transform root elements into an JSON array - decorate with [..]
		String jsonArrayAsString = new StringBuilder().append("[").append(resultAsString).append("]").toString();

		// remove carriage return and new line
		jsonArrayAsString = jsonArrayAsString.replaceAll("\\r|\\n", "");

		// add command between each root element
		jsonArrayAsString = StringUtils.replace(jsonArrayAsString, "}{", "},{");
		return jsonArrayAsString;
	}

	/**
	 * Set the JSON content.
	 * 
	 * @param uriVariables
	 *            URI variables map use to invoke the HTTP client.
	 */
	void setJsonContentType(Map<String, String> uriVariables) {
		setContentType(uriVariables, MediaType.APPLICATION_JSON_VALUE);
	}

	/**
	 * Set request content type.
	 * 
	 * @param uriVariables
	 *            URI variables map use to invoke the HTTP client.
	 * @param content
	 *            content type to be set.
	 */
	void setContentType(Map<String, String> uriVariables, String contentType) {
		uriVariables.put(DockerConstants.CONTENT_TYPE_KEY, contentType);
	}

}
