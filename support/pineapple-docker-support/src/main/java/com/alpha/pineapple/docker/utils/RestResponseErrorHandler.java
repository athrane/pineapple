/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.docker.utils;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of the {@linkplain ResponseErrorHandler} interface to provide
 * a customized error handler for exceptions returned by
 * {@linkplain RestTemplate}.
 */
public class RestResponseErrorHandler implements ResponseErrorHandler {

	/**
	 * Empty string.
	 */
	static final String EMPTY_STRING = "";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Default error handler.
	 */
	@Resource
	ResponseErrorHandler defaultResponseErrorHandler;

	/**
	 * Jackson object mapper.
	 */
	@Resource
	ObjectMapper jacksonObjectMapper;
	
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return defaultResponseErrorHandler.hasError(response);
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = response.getStatusCode();
		String body = IOUtils.toString(response.getBody());
		HttpHeaders headers = response.getHeaders();
		
		// parse body into message		
		String message = parseMessageFromHttpBody(body);

		// log
		logger.error("Status code: " + statusCode);
		logger.error("Body: " + body);
		logger.error("Headers: " + headers);
		logger.error("Message: " + message);		
		
		RestResponseException exception = new RestResponseException(statusCode, body, headers, message);
		throw exception;
	}

	/**
	 * Parse returned message in {@linkplain Error} object from Docker Rest API.
	 * 
	 * @param body HTTP response body.
	 * 
	 * @return parsed message with error object in HTTP body. 
	 * Returns an empty string if parsing of the Error object fails.
	 */
	String parseMessageFromHttpBody(String body) {
		try {
			// parse Error object from HTTP body
			Error error = jacksonObjectMapper.readValue(body, Error.class);			
			return error.getMessage();			
			
		} catch (Exception e) {			
			
			logger.error("Failed to parse HTTP body for Docker message, due to the error: " + e.getMessage());		

			// return empty string to signal failure to parse body
			return EMPTY_STRING;
		}
	}

}
