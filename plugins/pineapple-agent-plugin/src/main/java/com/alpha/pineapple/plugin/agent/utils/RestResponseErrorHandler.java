/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of the {@linkplain ResponseErrorHandler} interface to provide
 * a customized error handler for exceptions returned by
 * {@linkplain RestTemplate}.
 */
public class RestResponseErrorHandler implements ResponseErrorHandler {

	/**
	 * Default error handler.
	 */
	@Resource
	ResponseErrorHandler defaultResponseErrorHandler;

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return defaultResponseErrorHandler.hasError(response);
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = response.getStatusCode();
		String body = IOUtils.toString(response.getBody());
		HttpHeaders headers = response.getHeaders();
		RestResponseException exception = new RestResponseException(statusCode, body, headers);
		throw exception;
	}

}
