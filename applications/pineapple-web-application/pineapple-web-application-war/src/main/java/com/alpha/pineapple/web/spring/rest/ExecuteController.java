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

import static com.alpha.pineapple.web.WebApplicationConstants.LOCATION_HEADER;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_EXECUTE_CANCEL_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_EXECUTE_EXECUTE_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_EXECUTE_STATUS_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_EXECUTE_STATUS_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_EXECUTE_URI;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriTemplate;

import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.admin.Administration;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoNotFoundException;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ResultRepository;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.ExecutionResultMapper;
import com.alpha.pineapple.model.execution.Results;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleNotFoundException;
import com.alpha.pineapple.module.ModuleRepository;

/**
 * Execute REST web service controller.
 *
 */
@Controller
@RequestMapping(REST_EXECUTE_URI)
public class ExecuteController {

    /**
     * Empty string.
     */
    static final String EMPTY_STRING = "";

    /**
     * Index of the first result. The index starts at 0
     */
    static final int FIRST_MAPPED_RESULT_INDEX = 0;

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
     * Execution result model mapper.
     */
    @Resource
    ExecutionResultMapper resultMapper;

    /**
     * Execution Info container
     */
    HashMap<String, ExecutionInfo> executionInfos = new HashMap<String, ExecutionInfo>();

    /**
     * Execution index container. The index container contain an index for each
     * execution. Each index defines last result returned to client as part of
     * an update request.
     */
    Map<ExecutionInfo, Integer> executionIndexMap = new HashMap<ExecutionInfo, Integer>();

    /**
     * Start execution of operation.
     * 
     * @param module
     *            name of the module used as input to operation.
     * @param operation
     *            the operation which should be executed.
     * @param environment
     *            the environment for which a model within the module should be
     *            executed.
     * 
     * @return response entity which contains Location header with URI of new
     *         resource.
     * 
     * @throws ModuleNotFoundException
     *             if module isn't found. The exception is handled by the spring
     *             exception handler.
     */
    @RequestMapping(value = REST_EXECUTE_EXECUTE_PATH, method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> execute(@PathVariable String module, @PathVariable String operation,
	    @PathVariable String environment) throws ModuleNotFoundException {

	String executionId = executeInternal(module, operation, environment);

	// add location header
	URI uri = new UriTemplate(REST_EXECUTE_STATUS_URI).expand(executionId);
	HttpHeaders responseHeaders = new HttpHeaders();
	responseHeaders.set(LOCATION_HEADER, uri.toASCIIString());
	return new ResponseEntity<String>(EMPTY_STRING, responseHeaders, HttpStatus.CREATED);
    }

    /**
     * Start execution of operation.
     * 
     * @param module
     *            name of the module used as input to operation.
     * @param operation
     *            the operation which should be executed.
     * @param environment
     *            the environment for which a model within the module should be
     *            executed.
     * 
     * @return execution ID.
     * 
     * @throws ModuleNotFoundException
     *             if module isn't found.
     */
    public String executeInternal(String module, String operation, String environment) throws ModuleNotFoundException {

	// get module repository
	Administration admin = coreComponent.getAdministration();
	ModuleRepository moduleRepository = admin.getModuleRepository();

	// get module info
	ModuleInfo moduleInfo = moduleRepository.get(module);

	// execute operation
	ExecutionInfo executionInfo = coreComponent.executeOperation(operation, environment, moduleInfo.getId());
	
	// store executionInfo
	String executionId = Integer.toString(executionInfo.hashCode());
	executionInfos.put(executionId, executionInfo);

	return executionId;
    }

    /**
     * Start execution info.
     * 
     * @param id
     *            the ID of executing operation. Used to identify execution
     *            result tree. 
     * @return execution info.
     * 
     * @throws ExecutionInfoNotFoundException
     *             if operation ID isn't known. The exception is handled by the
     *             spring exception handler.
     */
    public ExecutionInfo getExecutionInfo(String id) throws ExecutionInfoNotFoundException {
	// if execution info isn't defined then throw exception
	// - exception is handled by spring exception handler
	if (!executionInfos.containsKey(id)) {
	    Object[] args = { id };
	    String message = webMessageProvider.getMessage("ec_unknown_executioninfo_failure", args);
	    throw new ExecutionInfoNotFoundException(message);
	}
	return executionInfos.get(id);
    }

    /**
     * Delete operation status.
     * 
     * Delete status of executing or completed operation.
     * 
     * If the operation ID is known then a HTTP 200 (OK) is returned with a
     * sequence of model results. If the operation ID isn't known the a HTTP 404
     * (Not Found) is returned along with a error message.
     * 
     * @param id
     *            the ID of executing operation. Used to identify execution
     *            result tree.
     * 
     * @throws ExecutionInfoNotFoundException
     *             if operation ID isn't known. The exception is handled by the
     *             spring exception handler.
     */
    @RequestMapping(value = REST_EXECUTE_STATUS_PATH, method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteOperationStatus(@PathVariable String id) throws ExecutionInfoNotFoundException {
	// if execution info isn't defined then throw exception
	// - exception is handled by spring exception handler
	if (!executionInfos.containsKey(id)) {
	    Object[] args = { id };
	    String message = webMessageProvider.getMessage("ec_unknown_executioninfo_failure", args);
	    throw new ExecutionInfoNotFoundException(message);
	}

	// delete
	executionInfos.remove(id);
    }

    /**
     * Get operation status.
     * 
     * Returns status of executing or completed operation. The service returns a
     * sequence of execution results which are generated since the last
     * invocation of the service.
     * 
     * @param id
     *            the operation ID of the executing operation. Used to identify
     *            execution result tree.
     * 
     * @return If the operation ID is known then a HTTP 200 (OK) is returned
     *         with a sequence of model results. If the operation ID isn't known
     *         the a HTTP 404 (Not Found) is returned along with a error
     *         message.
     * 
     * @throws ExecutionInfoNotFoundException
     *             if operation ID isn't known. The exception is handled by the
     *             spring exception handler.
     */
    @RequestMapping(value = REST_EXECUTE_STATUS_PATH, method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Results getOperationStatus(@PathVariable String id) throws ExecutionInfoNotFoundException {
	ExecutionResultNotification[] notifications = getOperationStatusInternal(id);
	return resultMapper.mapNotificationsToModel(notifications);
    }

    /**
     * Get operation status.
     * 
     * Returns status of executing or completed operation. The service returns a
     * sequence of execution results which are generated since the last
     * invocation of the service.
     * 
     * @param id
     *            the operation ID of the executing operation. Used to identify
     *            execution result tree.
     * 
     * @return If the operation ID is known then a sequence of execution result
     *         notifications are returned. If the operation ID isn't known the
     *         an exception is thrown.
     * 
     * @throws ExecutionInfoNotFoundException
     *             if operation ID is unknown. The exception is handled by the
     *             spring exception handler.
     */
    public ExecutionResultNotification[] getOperationStatusInternal(String id) throws ExecutionInfoNotFoundException {

	// if execution info isn't defined then throw exception
	if (!executionInfos.containsKey(id)) {
	    Object[] args = { id };
	    String message = webMessageProvider.getMessage("ec_unknown_executioninfo_failure", args);
	    throw new ExecutionInfoNotFoundException(message);
	}

	// get execution info
	ExecutionInfo executionInfo = executionInfos.get(id);

	// get result repository
	Administration admin = coreComponent.getAdministration();
	ResultRepository resultRepository = admin.getResultRepository();

	// resolve indexes
	int firstIndex = resolveFirstIndex(executionInfo);
	int lastIndex = resultRepository.getCurrentResultIndex(executionInfo);

	// get notifications in sequence
	ExecutionResultNotification[] notifications = resultRepository.getResultSequence(executionInfo, firstIndex,
		lastIndex);

	// update index
	updateFirstIndex(executionInfo, lastIndex);

	return notifications;
    }

    /**
     * Cancel execution of operation.
     * 
     * @param id
     *            the operation ID of the executing operation. Used to identify
     *            execution result tree.
     * 
     * @return If the operation ID is known then a sequence of execution result
     *         notifications are returned. If the operation ID isn't known the
     *         an exception is thrown.
     * 
     * @throws ExecutionInfoNotFoundException
     *             if operation ID is unknown. The exception is handled by the
     *             spring exception handler.
     */
    @RequestMapping(value = REST_EXECUTE_CANCEL_PATH, method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void cancelOperation(@PathVariable String id) throws ExecutionInfoNotFoundException {

	// if execution info isn't defined then throw exception
	if (!executionInfos.containsKey(id)) {
	    Object[] args = { id };
	    String message = webMessageProvider.getMessage("ec_unknown_executioninfo_failure", args);
	    throw new ExecutionInfoNotFoundException(message);
	}

	// get execution info and cancel
	ExecutionInfo executionInfo = executionInfos.get(id);
	coreComponent.cancelOperation(executionInfo);
    }

    /**
     * Resolve the index of the first result to return in the result set. If no
     * index is defined for the execution then zero is returned.
     * 
     * @param info
     *            execution info.
     * 
     * @return index of the first result to return in the result set. If no
     *         index is defined for the execution then zero is returned.
     */
    int resolveFirstIndex(ExecutionInfo info) {

	// get stored index
	if (executionIndexMap.containsKey(info))
	    return executionIndexMap.get(info);

	// if no value is store then handle it as first update
	return FIRST_MAPPED_RESULT_INDEX;
    }

    /**
     * Updates the first index for the next update query.
     * 
     * @param info
     *            execution info for which the index is updated.
     * @param indexValue
     *            the current index.
     */
    void updateFirstIndex(ExecutionInfo executionInfo, int indexValue) {
	executionIndexMap.put(executionInfo, Integer.valueOf(indexValue));
    }

    /**
     * Exception handler for handling unknown execution info.
     * 
     * @param e
     *            execution info not found exception.
     * @param response
     *            HTTP response.
     * 
     * @return resource not found HTTP status code and error message.
     */
    @ExceptionHandler(ExecutionInfoNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(ExecutionInfoNotFoundException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling unknown module..
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

}
