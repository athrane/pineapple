/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2016 Allan Thrane Andersen.
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

package com.alpha.pineapple.command;

import static com.alpha.pineapple.CoreConstants.MSG_CONTINUATION;
import static com.alpha.pineapple.CoreConstants.MSG_ENVIRONMENT;
import static com.alpha.pineapple.CoreConstants.MSG_MODULE;
import static com.alpha.pineapple.CoreConstants.MSG_MODULE_FILE;
import static com.alpha.pineapple.CoreConstants.MSG_OPERATION;
import static com.alpha.pineapple.CoreConstants.MSG_RESOURCE_RESOLUTION;
import static com.alpha.pineapple.CoreConstants.MSG_TRIGGER_RESOLUTION;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.springframework.oxm.Unmarshaller;
import org.w3c.dom.Element;

import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.OperationResolver;
import com.alpha.pineapple.execution.ResourceResolver;
import com.alpha.pineapple.execution.ResultRepository;
import com.alpha.pineapple.execution.continuation.ContinuationPolicy;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.module.Module;
import com.alpha.pineapple.model.module.model.AggregatedModel;
import com.alpha.pineapple.model.module.model.Content;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.activation.PluginActivator;
import com.alpha.pineapple.resource.ResourceInfo;
import com.alpha.pineapple.resource.ResourceRepository;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;
import com.alpha.pineapple.substitution.ModelVariableSubstitutor;
import com.alpha.pineapple.substitution.VariableSubstitutionException;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which implements execution of a operation on a module.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>module</code> contains the root object of the unmarshalled objects.
 * The type is <code>com.alpha.pineapple.model.module.Module</code>.</li>
 * 
 * <li><code>module-model</code> contains the root object of the unmarshalled
 * objects. The type is
 * <code>com.alpha.pineapple.model.module.model.Models</code>.</li>
 * 
 * <li><code>environment</code> contains the name of target environment in which
 * operation should be executed in. The type is <code>java.lang.String</code>.
 * </li>
 *
 * <li><code>execution-info</code> contains information about the operation
 * which should be executed. The type is
 * <code>com.alpha.pineapple.execution.ExecutionInfo</code>.</li>
 * 
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is no changes in the context but
 * execution of the operation.
 * </p>
 * .
 */
public class InvokePluginsCommand implements Command {

	/**
	 * Key used to identify property in context: Contains load module descriptor.
	 */
	public static final String MODULE_KEY = CoreConstants.MODULE_KEY;

	/**
	 * Key used to identify property in context: Contains loaded Pineapple module
	 * model.
	 */
	public static final String MODULE_MODEL_KEY = CoreConstants.MODULE_MODEL_KEY;

	/**
	 * Key used to identify property in context: The execution info which contains
	 * information about the operation which should be executed.
	 */
	public static final String EXECUTION_INFO_KEY = CoreConstants.EXECUTION_INFO_KEY;

	/**
	 * Null module model.
	 */
	static final Object NULL_MODULE_MODEL = null;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Plugin activator.
	 */
	@Resource
	PluginActivator pluginActivator;

	/**
	 * Target resource resolver.
	 */
	@Resource
	ResourceResolver resourceResolver;

	/**
	 * Target operation resolver.
	 */
	@Resource
	OperationResolver operationResolver;

	/**
	 * Model variable substitutor.
	 */
	@Resource
	ModelVariableSubstitutor modelVariableSubstitutor;

	/**
	 * Resource repository.
	 */
	@Resource
	ResourceRepository resourceRepository;

	/**
	 * Result repository.
	 */
	@Resource
	ResultRepository resultRepository;

	/**
	 * Command facade.
	 */
	@Resource
	CommandFacade commandFacade;

	/**
	 * Module object.
	 */
	@Initialize(MODULE_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	Module module;

	/**
	 * Module model object.
	 */
	@Initialize(MODULE_MODEL_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	Models moduleModel;

	/**
	 * Execution info.
	 */
	@Initialize(EXECUTION_INFO_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionInfo executionInfo;

	public boolean execute(Context context) throws Exception {
		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("ipc.start"));
		}

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// get execution result object for operation
		ExecutionResult rootResult = executionInfo.getResult();
		setContinueOnFailureDirective(rootResult);

		// add some meta data for reports
		rootResult.addMessage(MSG_OPERATION, executionInfo.getOperation());
		rootResult.addMessage(MSG_ENVIRONMENT, executionInfo.getEnvironment());
		rootResult.addMessage(MSG_MODULE, executionInfo.getModuleInfo().getId());
		rootResult.addMessage(MSG_MODULE_FILE, executionInfo.getModuleInfo().getDirectory().toString());

		// execute models
		executeModels(rootResult);

		// compute state
		rootResult.completeAsComputed(messageProvider, "ipc.succeed", null, "ipc.failed", null);

		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("ipc.completed"));
		}

		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Execute module models.
	 * 
	 * @param rootResult
	 *            Root execution result for operation.
	 */
	void executeModels(ExecutionResult rootResult) {

		// iterate over models
		for (AggregatedModel model : moduleModel.getModel()) {

			// enforce continuation policy
			if (!rootResult.getContinuationPolicy().continueExecution()) {
				Object[] args = { getModelDescription(model) };
				String message = messageProvider.getMessage("ipc.contination_policy_enforcement_info", args);
				rootResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
				return;
			}

			// resolve and iterate over the target resources
			String[] resolvedTargetResources = resolvedTargetResources(rootResult, model);

			// resolve target operation restriction
			if (operationResolver.restrictOperationFromExecution(executionInfo.getOperation(),
					model.getTargetOperation(), rootResult))
				continue;

			// iterate over the target resources
			for (String resolvedTargetResource : resolvedTargetResources) {
				executeModel(rootResult, resolvedTargetResource, model);
			}
		}
	}

	/**
	 * Execute module model.
	 * 
	 * @param result
	 *            Execution result for execution of models.
	 * @param targetResource
	 *            Target resource.
	 * @param model
	 *            Module model
	 */
	void executeModel(ExecutionResult result, String targetResource, AggregatedModel model) {

		// if input marshalling isn't supported by plugin then execute without a
		// model
		if (!isInputMarshallingEnabledForPlugin(targetResource)) {

			// log debug message
			if (logger.isDebugEnabled()) {
				logger.debug(messageProvider.getMessage("ipc.execute_without_unmarshall_start"));
			}

			// do execute
			ExecutionResult modelResult = excuteWithPlugin(result, targetResource, NULL_MODULE_MODEL, model);

			// execute trigger
			executeTriggers(model, modelResult, result);

			return;
		}

		// define unmarshaller
		Unmarshaller unmarshaller;

		try {
			// get unmarshaller
			unmarshaller = pluginActivator.getUnmarshaller(executionInfo.getEnvironment(), targetResource);

		} catch (PluginExecutionFailedException e) {

			// complete with error and exit
			result.completeAsError(messageProvider, "ipc.execute_model_getunmarshaller_failed", e);
			return;
		}

		// get model content
		Content content = model.getContent();

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { content.getAny().size() };
			logger.debug(messageProvider.getMessage("ipc.model_roots_info", args));
		}

		// iterate over elements
		for (Element element : content.getAny()) {

			// declare model
			Object moduleModel = null;

			try {

				// create source and unmarshall
				Source domSource = new DOMSource(element);
				moduleModel = unmarshaller.unmarshal(domSource);

			} catch (Exception e) {

				// create execution result description
				Object[] args = { targetResource };
				String description = messageProvider.getMessage("ipc.operation_info_onfailure", args);

				// create execution result object for model execution and
				// complete with error
				ExecutionResult modelResult = result.addChild(description);
				Object[] args2 = { e.getMessage() };
				modelResult.completeAsError(messageProvider, "ipc.execute_model_failed", args2, e);
				return;
			}

			// do execute
			ExecutionResult modelResult = excuteWithPlugin(result, targetResource, moduleModel, model);

			// execute trigger
			executeTriggers(model, modelResult, result);

		}
	}

	/**
	 * Execute module model with plugin.
	 * 
	 * @param result
	 *            Execution result for execution of models.
	 * @param targetResource
	 *            Target resource.
	 * @param unmarshallModelContent
	 *            module model unmarshalled content
	 * @param AggregatedModel
	 *            model module object.
	 * 
	 * @return execution result for executed model.
	 **/
	ExecutionResult excuteWithPlugin(ExecutionResult result, String targetResource, Object unmarshallModelContent,
			AggregatedModel moduleModel) {

		// create execution result object for model execution
		Object[] args = { getModelDescription(moduleModel), targetResource };
		String description = messageProvider.getMessage("ipc.operation_info", args);
		ExecutionResult modelResult = result.addChild(description);

		try {

			// get operation object
			Operation operation = pluginActivator.getOperation(executionInfo.getEnvironment(), targetResource,
					executionInfo.getOperation());

			// get uninitialized session object
			Session uninitializedSession = pluginActivator.getSession(executionInfo.getEnvironment(), targetResource);

			// create variable substituted model
			Object substitutedModel = createVariableSubstitutedModel(targetResource, unmarshallModelContent,
					moduleModel);

			// invoke operation (and initialize session)
			operation.execute(substitutedModel, uninitializedSession, modelResult);

			// force to error since operation didn't set the state
			if (modelResult.isExecuting()) {
				modelResult.completeAsFailure(messageProvider, "ipc.operation_didnt_setstate_failed");
			}

			return modelResult;

		} catch (RuntimeException e) {

			// capture channels exceptions from the SessionHandlerImpl class
			// through Operation.execute method
			// SessionConnectException, SessionDisconnectException and
			// PluginExecutionFailedException are channeled.

			// get channeled exception
			Exception channeledException = (Exception) e.getCause();

			// handle session error
			if (channeledException instanceof SessionConnectException) {

				// complete with error
				Object[] args2 = { channeledException.getMessage() };
				modelResult.completeAsError(messageProvider, "ipc.session_connect_failed", args2, channeledException);
				return modelResult;
			}

			// handle session error
			if (channeledException instanceof SessionDisconnectException) {

				// complete with error
				Object[] args2 = { channeledException.getMessage() };
				modelResult.completeAsError(messageProvider, "ipc.session_disconnect_failed", args2,
						channeledException);
				return modelResult;
			}

			// handle plugin error
			if (channeledException instanceof PluginExecutionFailedException) {

				// complete with error
				Object[] args2 = { channeledException.getMessage() };
				modelResult.completeAsError(messageProvider, "ipc.execute_plugin_failed", args2, channeledException);
				return modelResult;
			}

			// handle validation error
			if (channeledException instanceof IllegalArgumentException) {

				// complete with error
				Object[] args2 = { channeledException.getMessage() };
				modelResult.completeAsError(messageProvider, "ipc.execute_plugin_failed", args2, channeledException);
				return modelResult;
			}

			// handle true runtime exceptions (e.g. not channeled)
			// complete with error
			Object[] args2 = { e.getMessage() };
			modelResult.completeAsError(messageProvider, "ipc.execute_model_failed", args2, e);
			return modelResult;

		} catch (Exception e) {

			// general purpose exception handler

			// complete with error
			Object[] args2 = { e.getMessage() };
			modelResult.completeAsError(messageProvider, "ipc.execute_model_failed", args2, e);
			return modelResult;
		}

	}

	/**
	 * Return true if resource is valid, i.e defined in the target environment.
	 * 
	 * @param resource
	 *            resource to validate.
	 * 
	 * @return true if resource is valid, i.e defined in the target environment.
	 */
	boolean isResourceValid(String resource) {
		String environment = executionInfo.getEnvironment();
		return resourceRepository.contains(environment, resource);
	}

	/**
	 * Set the continue-on-failure directive on the root result continuation policy.
	 * 
	 * @param rootResult
	 *            root result to update the continuation policy on.
	 */
	void setContinueOnFailureDirective(ExecutionResult rootResult) {
		ContinuationPolicy policy = rootResult.getContinuationPolicy();
		if (!moduleModel.isContinue())
			policy.disableContinueOnFailure();
		else
			policy.enableContinueOnFailure();

		// add message
		Object[] args = { policy.isContinueOnFailure() };
		String message = messageProvider.getMessage("ipc.contination_policy_info", args);
		rootResult.addMessage(MSG_CONTINUATION, message);
	}

	/**
	 * Get module model description.
	 * 
	 * @param moduleModel
	 *            module model object.
	 * 
	 * @return module model description.
	 */
	String getModelDescription(AggregatedModel moduleModel) {
		if (moduleModel.getDescription() == null)
			return messageProvider.getMessage("ipc.no_description_available");
		if (moduleModel.getDescription().isEmpty())
			return messageProvider.getMessage("ipc.no_description_available");
		return moduleModel.getDescription();
	}

	/**
	 * Create variable substituted model.
	 * 
	 * @param targetResource
	 *            target resource.
	 * @param targetModel
	 *            target model.
	 * @param model
	 *            module model.
	 * 
	 * @return target model prepared for variable substitution.
	 * 
	 * @throws VariableSubstitutionException
	 *             if variable substitution fails.
	 */
	Object createVariableSubstitutedModel(String targetResource, Object targetModel, AggregatedModel model)
			throws VariableSubstitutionException {
		if (!model.isSubstituteVariables())
			return targetModel;

		// get resource
		ResourceInfo resourceInfo = resourceRepository.get(executionInfo.getEnvironment(), targetResource);
		com.alpha.pineapple.model.configuration.Resource resource = resourceInfo.getResource();

		// resolve
		return modelVariableSubstitutor.createObjectWithSubstitution(module, moduleModel, resource, targetModel);
	}

	/**
	 * Resolved target resources.
	 * 
	 * @param rootResult
	 *            root operation result.
	 * @param model
	 *            module model.
	 * 
	 * @return list of resolved target resource(s).
	 * 
	 */
	String[] resolvedTargetResources(ExecutionResult rootResult, AggregatedModel model) {

		// resolve target resources
		String targetResource = model.getTargetResource();
		String environment = executionInfo.getEnvironment();
		String[] resolvedTargetResources = resourceResolver.resolve(targetResource, environment);

		// add resolved target resources message
		Object[] args = { targetResource, Arrays.toString(resolvedTargetResources) };
		String message = messageProvider.getMessage("ipc.target_info", args);
		rootResult.addMessage(MSG_RESOURCE_RESOLUTION, message);
		return resolvedTargetResources;
	}

	/**
	 * Return true if input marshalling is enabled for plugin.
	 * 
	 * @param targetResource
	 *            target resource.
	 * 
	 * @return true if input marshalling is enabled for plugin.
	 */
	boolean isInputMarshallingEnabledForPlugin(String targetResource) {
		return pluginActivator.isInputMarshallingEnabled(executionInfo.getEnvironment(), targetResource);
	}

	/**
	 * Execute triggers in model.
	 * 
	 * @param aggregatedModel
	 *            aggregated model.
	 * @param modelResult
	 *            execution result for executed model. Used for reading the
	 *            execution result to match trigger resolution directive to
	 *            determine whether a trigger should be executed.
	 * @param result
	 *            execution result. The execution result for the execution of the
	 *            triggers are added as a child result.
	 */
	void executeTriggers(AggregatedModel aggregatedModel, ExecutionResult modelResult, ExecutionResult result) {

		// skip trigger execution if no triggers are defined.
		if (aggregatedModel.getTrigger().isEmpty()) {

			// add skip message
			// add resolved target resources message
			String message = messageProvider.getMessage("ipc.no_triggers_defined_info");
			result.addMessage(MSG_TRIGGER_RESOLUTION, message);
			return;
		}

		try {
			commandFacade.executeTriggers(aggregatedModel, executionInfo, modelResult, result);

		} catch (CommandFacadeException e) {
			// NO-OP,
			// since exception (i.e the stack trace and the state) is already
			// captured in execution result.
			// additional logic should only be put here if model execution
			// should be aborted due to trigger failure.
		}
	}

}
