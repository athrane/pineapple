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

package com.alpha.pineapple.plugin.agent.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

/**
 * Exception class for signaling captured error from {@linkplain RestTemplate}.
 */
public class RestResponseException extends RuntimeException {

	/**
	 * HTTP status code.
	 */
	HttpStatus statusCode;

	/**
	 * HTTP body.
	 */
	String body;

	/**
	 * HTTP headers.
	 */
	HttpHeaders headers;

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4848432043385964993L;

	/**
	 * RestResponseException constructor.
	 * 
	 * @param statusCode
	 *            HTTP status code.
	 * @param body
	 *            HTTP body.
	 * @param headers
	 *            HTTP headers.
	 */
	public RestResponseException(HttpStatus statusCode, String body, HttpHeaders headers) {
		this.statusCode = statusCode;
		this.body = body;
		this.headers = headers;
	}

	/**
	 * Return HTTP headers.
	 * 
	 * @return HTTP headers.
	 */
	public HttpHeaders getHeaders() {
		return headers;
	}

	/**
	 * Return HTTP status code.
	 * 
	 * @return HTTP status code.
	 */
	public HttpStatus getStatusCode() {
		return statusCode;
	}

	/**
	 * Return HTTP body.
	 * 
	 * @return HTTP body.
	 */
	public String getBody() {
		return body;
	}

}
