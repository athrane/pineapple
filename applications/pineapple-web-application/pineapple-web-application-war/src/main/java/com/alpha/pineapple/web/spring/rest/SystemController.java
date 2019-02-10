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

package com.alpha.pineapple.web.spring.rest;

import static com.alpha.pineapple.web.WebApplicationConstants.REST_SYSTEM_SIMPLE_STATUS_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_SYSTEM_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_SYSTEM_VERSION_PATH;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * System REST web service controller.
 *
 */
@Controller
@RequestMapping(REST_SYSTEM_URI)
public class SystemController {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider webMessageProvider;

	/**
	 * Pineapple core component.
	 */
	@Resource
	PineappleCore coreComponent;

	/**
	 * Get simple initialization status.
	 * 
	 * @return Get the initialization status of Pineapple. The status is returned in
	 *         a single human readable string which states whether Pineapple the
	 *         core component) was initialized successfully or with errors. If the
	 *         core component isn't initialized then an exception is thrown.
	 * 
	 * @throws IllegalStateException
	 *             if the core component isn't initialized.
	 * @throws IllegalArgumentException
	 *             if the core component isn't initialized successfully.
	 * 
	 *             The exception is handled by the spring exception handler.
	 */
	@RequestMapping(value = REST_SYSTEM_SIMPLE_STATUS_PATH, method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String getSimpleInitializationStatus() {
		String status = coreComponent.getInitializationInfoAsString();

		// if status isn't defined then throw exception
		if (status == null) {
			String message = webMessageProvider.getMessage("sc_core_not_initialized_failure");
			throw new IllegalStateException(message);
		}

		// if status isn't successful then throw exception
		if (!status.contains("successful")) {
			throw new IllegalArgumentException(status);
		}

		return status;
	}

	/**
	 * Get Version.
	 * 
	 * @return Get Pineapple version. Get version information about Pineapple. The
	 *         information is returned in a single human readable string.
	 */
	@RequestMapping(value = REST_SYSTEM_VERSION_PATH, method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String getVersion() {
		return coreComponent.toString();
	}

	/**
	 * Exception handler for uninitialized core component.
	 * 
	 * @param e
	 *            illegal state exception.
	 * @param response
	 *            HTTP response.
	 * 
	 * @return resource not found HTTP status code and error message.
	 */
	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	public String handleException(IllegalStateException e, HttpServletResponse response) {
		return e.getMessage();
	}

	/**
	 * Exception handler for unsuccessful initialized core component.
	 * 
	 * @param e
	 *            illegal argument exception.
	 * @param response
	 *            HTTP response.
	 * 
	 * @return internal server error HTTP status code and error message.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public String handleException(IllegalArgumentException e, HttpServletResponse response) {
		return e.getMessage();
	}

}
