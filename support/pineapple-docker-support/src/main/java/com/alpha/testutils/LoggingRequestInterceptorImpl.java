/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
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
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.testutils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of the {@linkplain ClientHttpRequestInterceptor} interface
 * which supports logging of HTTP request/response sent and received by
 * {@linkplain RestTemplate}.
 */
public class LoggingRequestInterceptorImpl implements ClientHttpRequestInterceptor {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		handldeRequest(request, body);

		ClientHttpResponse response = execution.execute(request, body);

		// log response
		CachingClientHttpResponseWrapper bufferedResponse = new CachingClientHttpResponseWrapper(response);
		handleResponse(bufferedResponse);
		return bufferedResponse;
	}

	/**
	 * Handles request messages for logging.
	 * 
	 * @param request
	 *            HTTP request.
	 * @param request
	 *            body.
	 * @throws IOException
	 *             if logging fails
	 */
	void handldeRequest(HttpRequest request, byte[] body) {
		String bodyAsString = new String(body, Charset.forName("UTF-8"));
		logger.debug("HTTP Request body: " + bodyAsString);
	}

	/**
	 * Handles response messages for logging.
	 * 
	 * @param response
	 *            cached HTTP response.
	 * @throws IOException
	 *             if logging fails
	 */
	void handleResponse(CachingClientHttpResponseWrapper response) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("HTTP Response status code: " + response.getStatusCode());
			logger.debug("HTTP Response text: " + response.getStatusText());
			logger.debug("HTTP Response body: " + response.getBodyContent());
		}
	}

	/**
	 * Response wrapper implementation of {@link ClientHttpResponse} that reads the
	 * message body into memory for caching, thus allowing for multiple invocations
	 * of {@link #getBody()}.
	 */
	class CachingClientHttpResponseWrapper implements ClientHttpResponse {

		final ClientHttpResponse response;
		byte[] body;

		CachingClientHttpResponseWrapper(ClientHttpResponse response) {
			this.response = response;
		}

		public HttpStatus getStatusCode() throws IOException {
			return this.response.getStatusCode();
		}

		public int getRawStatusCode() throws IOException {
			return this.response.getRawStatusCode();
		}

		public String getStatusText() throws IOException {
			return this.response.getStatusText();
		}

		public HttpHeaders getHeaders() {
			return this.response.getHeaders();
		}

		public InputStream getBody() throws IOException {
			if (this.body == null) {
				if (response.getBody() != null) {
					this.body = FileCopyUtils.copyToByteArray(response.getBody());
				} else {
					body = new byte[] {};
				}
			}
			return new ByteArrayInputStream(this.body);
		}

		public String getBodyContent() throws IOException {
			if (this.body == null) {
				getBody();
			}

			return new String(body, Charset.forName("UTF-8"));
		}

		public void close() {
			this.response.close();
		}
	}
}
