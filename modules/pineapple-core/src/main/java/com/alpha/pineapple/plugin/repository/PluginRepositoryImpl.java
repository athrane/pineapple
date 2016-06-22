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

package com.alpha.pineapple.plugin.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.oxm.Unmarshaller;

import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginInitializationFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.session.NullSessionImpl;
import com.alpha.pineapple.session.Session;

/**
 * Implementation of the {@linkplain PluginRuntimeRepository} interface.
 * 
 * The plugin repository services to manage registered plugins.
 */
public class PluginRepositoryImpl implements PluginRuntimeRepository {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Plugin candidate scanner.
     */
    @Resource
    PluginCandidateScanner pluginCandidateScanner;

    /**
     * Plugin initializer.
     */
    @Resource
    PluginInitializer pluginInitializer;

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Plugin definition container.
     */
    HashMap<String, PluginInfo> pluginInfos;

    @Override
    public void initialize(ExecutionResult executionResult, String[] pluginIds)
	    throws PluginInitializationFailedException {
	// validate parameters
	Validate.notNull(executionResult, "executionResult is undefined.");
	Validate.notNull(pluginIds, "pluginIds is undefined.");

	// log debug message
	if (logger.isDebugEnabled()) {
	    String message = messageProvider.getMessage("pr.initialize_start");
	    logger.debug(message);
	}

	ExecutionResult repositoryResult = createExecutionResultForRepository(executionResult, pluginIds);

	// create plugin info map
	pluginInfos = new HashMap<String, PluginInfo>();
	String currentPluginId = null;

	try {

	    for (String pluginId : pluginIds) {
		currentPluginId = pluginId;
		initializePlugin(repositoryResult, pluginId);
	    }

	    repositoryResult.completeAsComputed(messageProvider, "pr.initialize_success");

	    // log debug message
	    if (logger.isDebugEnabled()) {
		String message = messageProvider.getMessage("pr.initialize_success");
		logger.debug(message);
	    }

	} catch (Exception e) {
	    Object[] args = { currentPluginId, e.getMessage() };
	    repositoryResult.completeAsError(messageProvider, "pr.initialize_error", args, e);
	}
    }

    public void initializePlugin(ExecutionResult executionResult, String pluginId)
	    throws PluginInitializationFailedException {

	// validate parameters
	Validate.notNull(pluginId, "pluginId is undefined.");
	Validate.notEmpty(pluginId, "pluginId is empty.");
	Validate.notNull(executionResult, "executionResult is undefined.");

	ExecutionResult pluginResult = createExecutionResultForPlugin(executionResult, pluginId);

	// do component scan for plugin
	String[] pluginIds = { pluginId };
	ApplicationContext pluginContext = pluginCandidateScanner.scanForPlugins(pluginIds);

	// get plugin candidates stored in context
	String[] candidateNames = pluginContext.getBeanDefinitionNames();

	// iterate over the beans
	for (String beanId : candidateNames) {

	    // if bean is a plugin class then process it
	    if (isPluginClass(beanId)) {
		// get plugin class
		Object pluginClass = pluginContext.getBean(beanId);

		// create and store plugin info object
		PluginInfo pluginInfo = pluginInitializer.initializePlugin(pluginClass);
		this.pluginInfos.put(pluginInfo.getPluginId(), pluginInfo);

		// set state and exit
		setResultToSuccessfulInitialization(pluginResult, pluginInfo);
		return;
	    }
	}

	// handle case where no candidate beans where plugin classes
	setResultToFailedInitialization(pluginResult, pluginId);
    }

    /**
     * Return true if bean is registered in the context as a plugin candidate.
     * 
     * @param beanId
     *            Id of the bean to validate the name of.
     * 
     * @return true if bean is registered in the context as a plugin candidate.
     */
    boolean isPluginClass(String beanId) {
	return StringUtils.startsWith(beanId, "plugin:");
    }

    public Operation getOperation(String pluginId, String operationId) {
	// log debug message
	if (logger.isDebugEnabled()) {
	    Object[] args = { pluginId, operationId };
	    String message = messageProvider.getMessage("pr.getoperation_start", args);
	    logger.debug(message);
	}

	// get plugin info
	PluginInfo pluginInfo = getPluginInfo(pluginId);

	// get plugin context
	ApplicationContext pluginContext = pluginInfo.getContext();

	// create operation id
	String finalOperationId = new StringBuilder().append(PluginOperation.class.getName()).append(":")
		.append(operationId).toString();

	// attempt to resolve operation with id
	if (pluginContext.containsBean(finalOperationId)) {
	    // get operation from context
	    Operation operation = (Operation) pluginContext.getBean(finalOperationId);

	    // log debug message
	    if (logger.isDebugEnabled()) {
		Object[] args = { operation };
		String message = messageProvider.getMessage("pr.getoperation_completed", args);
		logger.debug(message);
	    }

	    // return operation
	    return operation;
	}

	// create wild card operation id
	finalOperationId = new StringBuilder().append(PluginOperation.class.getName()).append(":")
		.append(OperationNames.WILDCARD_OPERATION).toString();

	// attempt to resolve operation with wild card id
	if (pluginContext.containsBean(finalOperationId)) {
	    // get operation from context
	    Operation operation = (Operation) pluginContext.getBean(finalOperationId);

	    // log debug message
	    if (logger.isDebugEnabled()) {
		Object[] args = { operation };
		String message = messageProvider.getMessage("pr.getoperation_wildcard_completed", args);
		logger.debug(message);
	    }

	    // return operation
	    return operation;
	}

	// throw exception
	Object[] args = { operationId, pluginId, getSupportedOperations(pluginInfo) };
	String message = messageProvider.getMessage("pr.getoperation_not_supported_failure", args);
	throw new OperationNotSupportedException(message);
    }

    public Unmarshaller getPluginUnmarshaller(String pluginId) throws PluginExecutionFailedException {
	// log debug message
	if (logger.isDebugEnabled()) {
	    Object[] args = { pluginId };
	    String message = messageProvider.getMessage("pr.getunmarshaller_start", args);
	    logger.debug(message);
	}

	// validate plugin is defined
	if (!pluginInfos.containsKey(pluginId)) {
	    Object[] args = { pluginId };
	    String message = messageProvider.getMessage("pr.getunmarshaller_unknownplugin_error", args);
	    throw new PluginExecutionFailedException(message);
	}

	// get plugin info
	PluginInfo pluginInfo = pluginInfos.get(pluginId);

	// get plugin context
	ApplicationContext pluginContext = pluginInfo.getContext();

	// get unmarshaller
	Unmarshaller unmarshaller = (Unmarshaller) pluginContext.getBean(pluginInfo.getUnmarshallerId());

	// log debug message
	if (logger.isDebugEnabled()) {
	    Object[] args = { unmarshaller };
	    String message = messageProvider.getMessage("pr.getunmarshaller_completed", args);
	    logger.debug(message);
	}

	return unmarshaller;
    }

    /**
     * @see com.alpha.pineapple.plugin.repository.PluginRuntimeRepository#getSession(java.lang.String)
     */
    public Session getSession(String pluginId) {
	// log debug message
	if (logger.isDebugEnabled()) {
	    Object[] args = { pluginId };
	    String message = messageProvider.getMessage("pr.getsession_start", args);
	    logger.debug(message);
	}

	// get plugin info
	PluginInfo pluginInfo = this.pluginInfos.get(pluginId);

	// if session handling is disabled then return null object
	if (!pluginInfo.isSessionHandlingEnabled()) {

	    // log debug message
	    if (logger.isDebugEnabled()) {
		Object[] args = { pluginId };
		String message = messageProvider.getMessage("pr.getsession_null", args);
		logger.debug(message);
	    }

	    return new NullSessionImpl();
	}

	// get plugin context
	ApplicationContext pluginContext = pluginInfo.getContext();

	// get session
	Session session = (Session) pluginContext.getBean(pluginInfo.getSessionId());

	// log debug message
	if (logger.isDebugEnabled()) {
	    Object[] args = { session };
	    String message = messageProvider.getMessage("pr.getsession_completed", args);
	    logger.debug(message);
	}

	return session;
    }

    public PluginInfo getPluginInfo(String pluginId) throws PluginNotFoundException {
	if (!pluginInfos.containsKey(pluginId)) {
	    Object[] args = { pluginId, pluginId };
	    String message = messageProvider.getMessage("pr.getpluginid_notfound_failure", args);
	    throw new PluginNotFoundException(message);
	}
	return this.pluginInfos.get(pluginId);
    }

    /**
     * Return list of supported operations.
     * 
     * @param pluginInfo
     *            plugin info.
     * 
     * @return list of supported operations.
     */
    String getSupportedOperations(PluginInfo pluginInfo) {
	ApplicationContext context = pluginInfo.getContext();
	String[] beanNames = context.getBeanDefinitionNames();
	ArrayList<String> operationNames = new ArrayList<String>();

	for (String beanName : beanNames) {
	    if (beanName.startsWith(PluginOperation.class.getName())) {
		operationNames.add(StringUtils.substringAfter(beanName, ":"));
	    }
	}
	return StringUtils.join(operationNames, ",");
    }

    /**
     * Create execution result for initialization of plugin repository.
     * 
     * @param result
     *            parent result.
     * @param pluginIds
     *            plugin candidate list.
     * 
     * @return execution plugin repository result.
     */
    ExecutionResult createExecutionResultForRepository(ExecutionResult result, String[] pluginIds) {
	String message = messageProvider.getMessage("pr.initialize_plugin_repository_info");
	ExecutionResult pluginResult = result.addChild(message);
	String header = messageProvider.getMessage("pr.initialize_plugin_candidates_header");
	pluginResult.addMessage(header, Arrays.toString(pluginIds));
	return pluginResult;
    }

    /**
     * Create execution result for initialization of plugin.
     * 
     * @param result
     *            plugin repository result.
     * @param pluginId
     *            plugin ID.
     * 
     * @return execution result for initialization of plugin.
     */
    ExecutionResult createExecutionResultForPlugin(ExecutionResult result, String pluginId) {
	Object[] args = { pluginId };
	String message = messageProvider.getMessage("pr.initialize_plugin_info", args);
	ExecutionResult pluginResult = result.addChild(message);
	return pluginResult;
    }

    /**
     * Set plugin result to successful initialization.
     * 
     * @param pluginResult
     *            result to register successful state for.
     * @param pluginInfo
     *            plugin info.
     */
    void setResultToSuccessfulInitialization(ExecutionResult pluginResult, PluginInfo pluginInfo) {
	String header1 = messageProvider.getMessage("pr.initialize_plugin_configfile_header");
	pluginResult.addMessage(header1, pluginInfo.getConfigFileName());
	String header2 = messageProvider.getMessage("pr.initialize_plugin_unmarshalling_header");
	pluginResult.addMessage(header2, Boolean.toString(pluginInfo.isInputMarshallingEnabled()));
	String header3 = messageProvider.getMessage("pr.initialize_plugin_session_header");
	pluginResult.addMessage(header3, Boolean.toString(pluginInfo.isSessionHandlingEnabled()));
	pluginResult.completeAsSuccessful(messageProvider, "pr.initialize_plugin_success");
    }

    /**
     * Set plugin result to failed initialization.
     * 
     * @param pluginResult
     *            result to register successful failed for.
     * @param pluginId
     *            plugin ID.
     */
    void setResultToFailedInitialization(ExecutionResult pluginResult, String pluginId) {
	Object[] args = { pluginId };
	pluginResult.completeAsFailure(messageProvider, "pr.initialize_plugin_failure", args);
    }

}
