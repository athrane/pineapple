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

import static com.alpha.pineapple.web.WebApplicationConstants.REST_SCHEDULE_DELETE_OPERATIONS_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_SCHEDULE_DELETE_OPERATION_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_SCHEDULE_GET_OPERATIONS_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_SCHEDULE_SCHEDULE_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_SCHEDULE_URI;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.admin.Administration;
import com.alpha.pineapple.execution.scheduled.ScheduledOperationAlreadyExistsException;
import com.alpha.pineapple.execution.scheduled.ScheduledOperationNotFoundException;
import com.alpha.pineapple.execution.scheduled.ScheduledOperationRespository;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperations;
import com.alpha.pineapple.module.ModuleNotFoundException;
import com.alpha.pineapple.web.model.RestResultMapper;

/**
 * Schedule operation REST web service controller.
 *
 */
@Controller
@RequestMapping(REST_SCHEDULE_URI)
public class ScheduledOperationController {

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
     * REST result mapper.
     */
    @Resource
    RestResultMapper restResultMapper;

    /**
     * Schedule operation for execution
     * 
     * @param name
     *            name of the scheduled operation. Must be unique.
     * @param module
     *            name of the module used as input to operation.
     * @param environment
     *            the environment for which a model within the module should be
     *            executed.
     * @param operation
     *            the operation which should be executed.
     * @param cron
     *            CRON scheduling expression.
     * @param description
     *            description of the scheduled operation which should be
     *            executed.
     * 
     * @throws ModuleNotFoundException
     *             if module isn't found. The exception is handled by the spring
     *             exception handler.
     */
    @RequestMapping(value = REST_SCHEDULE_SCHEDULE_PATH, method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable String name, @PathVariable String module, @PathVariable String environment,
	    @PathVariable String operation, @PathVariable String cron, @PathVariable String description)
		    throws ModuleNotFoundException {
	Administration admin = coreComponent.getAdministration();
	ScheduledOperationRespository repository = admin.getScheduledOperationRespository();
	repository.create(name, module, operation, environment, description, cron);
    }

    /**
     * Get scheduled operations.
     * 
     * @return collection of scheduled operations and HTTP 200 (OK).
     */
    @RequestMapping(value = REST_SCHEDULE_GET_OPERATIONS_PATH, method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ScheduledOperations getScheduledOperations() {
	Administration admin = coreComponent.getAdministration();
	ScheduledOperationRespository repository = admin.getScheduledOperationRespository();
	return this.restResultMapper.mapScheduledOperations(repository.getOperations());
    }

    /**
     * Delete scheduled operation.
     * 
     * @param name
     *            name of the scheduled operation.
     * 
     * @throws ScheduledOperationsFileNotFoundException
     *             if scheduled operation isn't found. The exception is handled
     *             by the spring exception handler.
     */
    @RequestMapping(value = REST_SCHEDULE_DELETE_OPERATION_PATH, method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable String name) {
	Administration admin = coreComponent.getAdministration();
	ScheduledOperationRespository repository = admin.getScheduledOperationRespository();
	repository.delete(name);
    }

    /**
     * Delete all scheduled operations.
     */
    @RequestMapping(value = REST_SCHEDULE_DELETE_OPERATIONS_PATH, method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteAll() {
	Administration admin = coreComponent.getAdministration();
	ScheduledOperationRespository repository = admin.getScheduledOperationRespository();
	repository.deleteAll();
    }

    /**
     * Exception handler for handling unknown module.
     * 
     * @param e
     *            module not found exception.
     * @param response
     *            HTTP response.
     * 
     * @return module not found HTTP status code and error message.
     */
    @ExceptionHandler(ModuleNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(ModuleNotFoundException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling illegal arguments.
     * 
     * @param e
     *            illegal argument exception.
     * @param response
     *            HTTP response.
     * 
     * @return illegal argument HTTP status code and error message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(IllegalArgumentException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling scheduled operation already exists
     * exception.
     * 
     * @param e
     *            scheduled operation already exists exception.
     * @param response
     *            HTTP response.
     * 
     * @return scheduled operation already exists HTTP status code and error
     *         message.
     */
    @ExceptionHandler(ScheduledOperationAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(ScheduledOperationAlreadyExistsException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling scheduled operation not found exception.
     * 
     * @param e
     *            scheduled operation not found exception.
     * @param response
     *            HTTP response.
     * 
     * @return scheduled operation not found HTTP status code and error message.
     */
    @ExceptionHandler(ScheduledOperationNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(ScheduledOperationNotFoundException e, HttpServletResponse response) {
	return e.getMessage();
    }

}
