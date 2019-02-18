/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2016 Allan Thrane Andersen..
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

package com.alpha.pineapple.web.zk.viewmodel;

import static com.alpha.pineapple.web.WebApplicationConstants.COMPLETED_ACTIVITY_CREATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.COMPLETED_OPERATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.EXECUTE_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.NOTIFICATION_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_GLOBALCOMMAND_ARGS;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_SCOPE;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_DURATION;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_LOCATION;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_STYLE;
import static com.alpha.pineapple.web.WebApplicationConstants.SELECT_WORKSPACE_TAB_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.START_OPERATION_ENVIRONMENT_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.START_OPERATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.START_OPERATION_MODULE_INFO_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.START_OPERATION_OPERATION_ARG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Messagebox;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoNotFoundException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.web.activity.ActivityRepository;
import com.alpha.pineapple.web.event.consumer.ResultNotificationNotifierImpl;
import com.alpha.pineapple.web.model.ExecutionResultProperty;
import com.alpha.pineapple.web.model.SessionState;
import com.alpha.pineapple.web.spring.rest.ExecuteController;

/**
 * ZK view model for the execution panel.
 */
public class ExecutionPanel {

	/**
	 * Null models list.
	 */
	static final Map<String, String> NULL_MODELS_MAP = new HashMap<String, String>();

	/**
	 * Null root execution result.
	 */
	static final ExecutionResult NULL_ROOT_RESULT = new ExecutionResultImpl("Default Root Node");

	/**
	 * Null root tree node.
	 */
	static final DefaultTreeNode<ExecutionResult> NULL_ROOT_NODE = new DefaultTreeNode<ExecutionResult>(
			NULL_ROOT_RESULT, new ArrayList<DefaultTreeNode<ExecutionResult>>());

	/**
	 * Null tree model.
	 */
	static final DefaultTreeModel<ExecutionResult> NULL_TREE_MODEL = new DefaultTreeModel<ExecutionResult>(
			NULL_ROOT_NODE);

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Session state.
	 */
	@WireVariable
	SessionState sessionState;

	/**
	 * Message provider for I18N support.
	 */
	@WireVariable
	MessageProvider webMessageProvider;

	/**
	 * Spring REST execution controller.
	 */
	@WireVariable
	ExecuteController executeController;

	/**
	 * Activity repository.
	 */
	@WireVariable
	ActivityRepository activityRepository;

	/**
	 * Tree node execution result state converter.
	 */
	@WireVariable
	Converter<String, Object, Component> treeNodeStateConverter;

	/**
	 * Tree node execution result start time converter.
	 */
	@WireVariable
	Converter<String, Object, Component> treeNodeStartTimeConverter;

	/**
	 * Tree node execution result description style converter.
	 */
	@WireVariable
	Converter<String, Object, Component> treeNodeDescriptionStyleConverter;

	/**
	 * Tree node execution result state icon converter.
	 */
	@WireVariable
	Converter<String, Object, Component> treeNodeStateIconConverter;
	
	/**
	 * Tree model.
	 */
	DefaultTreeModel<ExecutionResult> treeModel;

	/**
	 * Selected execution result in tree.
	 */
	DefaultTreeNode<ExecutionResult> selectedTreeNode;

	/**
	 * Messages from selected result stored in a list.
	 */
	List<ExecutionResultProperty> messages;

	/**
	 * Result mapping between execution results and tree nodes.
	 */
	HashMap<ExecutionResult, DefaultTreeNode<ExecutionResult>> resultMapping;

	/**
	 * Details state, whether they are expanded or collapsed.
	 */
	boolean isDetailsCollapsed = false;

	/**
	 * Initialize view model.
	 */
	@Init
	public void init() {

		// assign null tree
		treeModel = NULL_TREE_MODEL;

		// set collapse state
		isDetailsCollapsed = false;
	}

	/**
	 * Get tree node state converter.
	 * 
	 * @return tree node state converter.
	 */
	public Converter<String, Object, Component> getTreeNodeStateConverter() {
		return treeNodeStateConverter;
	}

	/**
	 * Get tree node state icon converter.
	 * 
	 * @return tree node state icon converter.
	 */
	public Converter<String, Object, Component> getTreeNodeStateIconConverter() {
		return treeNodeStateIconConverter;
	}
	
	/**
	 * Get tree node start time converter.
	 * 
	 * @return tree node start time converter.
	 */
	public Converter<String, Object, Component> getTreeNodeStartTimeConverter() {
		return treeNodeStartTimeConverter;
	}

	/**
	 * Get tree node description style converter.
	 * 
	 * @return tree node start time converter.
	 */
	public Converter<String, Object, Component> getTreeNodeDescriptionStyleConverter() {
		return treeNodeDescriptionStyleConverter;
	}

	/**
	 * Get tree model in ZK view.
	 * 
	 * @return tree model.
	 */
	public DefaultTreeModel<ExecutionResult> getTreeModel() {
		return treeModel;
	}

	/**
	 * Set selected tree node in ZK view.
	 * 
	 * @param node
	 *            to set.
	 */
	public void setSelectedExecutionResult(DefaultTreeNode<ExecutionResult> node) throws Exception {
		selectedTreeNode = node;
	}

	/**
	 * Get selected tree node in ZK view. Will return null is no result is set.
	 * 
	 * @return selected result.
	 */
	public DefaultTreeNode<ExecutionResult> getSelectedExecutionResult() {
		return selectedTreeNode;
	}

	/**
	 * Get messages for selected result.
	 * 
	 * @return model info.
	 */
	public List<ExecutionResultProperty> getMessages() {
		return messages;
	}

	/**
	 * Generate details for selected execution result tree item. Creates a list box
	 * model with messages from the execution result backing the selected tree node.
	 */
	@Command
	@NotifyChange("messages")
	public void showDetailsForSelectedItem() {
		// clear details
		messages = new ArrayList<ExecutionResultProperty>();

		// handle null case
		if (!isTreeNodeSelected())
			return;

		// get messages
		ExecutionResult result = selectedTreeNode.getData();
		Map<String, String> resultMessageMap = result.getMessages();

		// create model
		SortedSet<String> sortedKeys = new TreeSet<String>(resultMessageMap.keySet());
		for (String key : sortedKeys) {
			String value = resultMessageMap.get(key);
			ExecutionResultProperty resultProperty = new ExecutionResultProperty(key, value);
			messages.add(resultProperty);
		}
	}

	/**
	 * Event handler for the global command "executionNotification".
	 * 
	 * The event is triggered from the reactor consumer
	 * {@linkplain ResultNotificationNotifierImpl} which posts the global command.
	 * 
	 * The event handler will dispatch the forwarded event to one of the visitor
	 * methods and then notify the MVVM binder that the tree is updated.
	 * 
	 * @param evt
	 *            event argument forwarded from the reactor consumer.
	 */
	@GlobalCommand
	@NotifyChange({ "treeModel", "lastExecutedOperationStatus" })
	public void executionNotification(@BindingParam(NOTIFICATION_ARG) ExecutionResultNotification notification) {

		// exit if notification is null
		if (notification == null)
			return;

		// exit if result is null
		if (notification.getResult() == null)
			return;

		// only update view if notification belong to the current execution
		ExecutionResult rootResult = notification.getResult().getRootResult();

		// get execution info
		ExecutionInfo executionInfo = getExecutionId();

		// exit if execution info isn't defined
		if (executionInfo == null)
			return;

		// get root execution result from execution info
		ExecutionResult executionResult = executionInfo.getResult();

		// exit if root result doesn't match the root result from
		// execution info
		if (executionResult != rootResult)
			return;

		// do GUI update for start of operation
		if (isExecutionStarting(notification)) {
			startOperation(notification);
			return;
		}

		// do GUI update for start of execution result
		if (isResultExecuting(notification)) {
			StartExecution(notification);
			return;
		}

		// do GUI update for completion of operation
		if (isExecutionCompleted(notification)) {
			completeOperation(notification);
			return;
		}

	}

	/**
	 * Lookup execution info execution controller using the execution ID from the
	 * session state.
	 * 
	 * If the execution ID can't be resolved to an execution info then null is
	 * returned.
	 * 
	 * @return execution info from execution ID. If the execution info isn't defined
	 *         then null is returned.
	 */
	ExecutionInfo getExecutionId() {

		String executionID = sessionState.getExecutionId();
		try {
			return executeController.getExecutionInfo(executionID);
		} catch (ExecutionInfoNotFoundException e) {
			return null;
		}

	}

	/**
	 * Event handler for the global command "startOperation".
	 * 
	 * The event is triggered from {@linkplain ExecuteOperationActivityInvokerImpl},
	 * {@linkplain Menu} and this class.
	 * 
	 * The event handler will start the asynchronous execution of an operation and
	 * then notify the MVVM binder that the tree is updated.
	 * 
	 * @param operation
	 *            operation name from the command arguments
	 * @param environment
	 *            environment from the command arguments
	 * @param module
	 *            info module info from the command arguments
	 * 
	 */
	@GlobalCommand
	@NotifyChange({ "treeModel", "lastExecutedOperationStatus" })
	public void startOperation(@BindingParam(START_OPERATION_OPERATION_ARG) String operation,
			@BindingParam(START_OPERATION_ENVIRONMENT_ARG) String environment,
			@BindingParam(START_OPERATION_MODULE_INFO_ARG) ModuleInfo moduleInfo) {

		// execute operation
		String executionId = executeController.executeInternal(moduleInfo.getId(), operation, environment);

		// store execution ID
		sessionState.setExecutionId(executionId);

		// register execute operation activity
		activityRepository.addExecuteOperationActivity(sessionState.getAccount(), moduleInfo.getId(), operation,
				environment);

		// post global command to trigger update of the activity list on
		// the home tab
		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, EventQueues.APPLICATION,
				COMPLETED_ACTIVITY_CREATION_GLOBALCOMMAND, NULL_GLOBALCOMMAND_ARGS);

	}

	/**
	 * Event handler for the command "reexecuteLastOperation". The event is
	 * triggered from the "Execute Last" button on the execution panel which posts
	 * the command.
	 * 
	 * The event handler will post the global command to trigger execution of the
	 * operation in the execution panel controller.
	 */
	@Command
	public void reexecuteLastOperation() {

		// post global command to which triggers execution in execution panel
		// controller
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(START_OPERATION_MODULE_INFO_ARG, sessionState.getModuleInfo());
		args.put(START_OPERATION_OPERATION_ARG, sessionState.getLastExecutedOperation());
		args.put(START_OPERATION_ENVIRONMENT_ARG, sessionState.getEnvironment());
		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, START_OPERATION_GLOBALCOMMAND, args);
	}

	/**
	 * Event handler for the command "executeTestOperation". The event is triggered
	 * from the "Execute" button on the execution panel which posts the command.
	 * 
	 * The event handler will post the global command to trigger execution of the
	 * operation in the execution panel controller.
	 */
	@Command
	public void executeTestOperation() {
		executeOperation(OperationNames.TEST);
	}

	/**
	 * Event handler for the command "executeDeployConfigurationOperation". The
	 * event is triggered from the "Execute" button on the execution panel which
	 * posts the command.
	 * 
	 * The event handler will post the global command to trigger execution of the
	 * operation in the execution panel controller.
	 */
	@Command
	public void executeDeployConfigurationOperation() {
		executeOperation(OperationNames.DEPLOY_CONFIGURATION);
	}

	/**
	 * Event handler for the command "executeUndeployConfigurationOperation". The
	 * event is triggered from the "Execute" button on the execution panel which
	 * posts the command.
	 * 
	 * The event handler will post the global command to trigger execution of the
	 * operation in the execution panel controller.
	 */
	@Command
	public void executeUndeployConfigurationOperation() {
		executeOperation(OperationNames.UNDEPLOY_CONFIGURATION);
	}

	/**
	 * Event handler for the command "executeCreateReportOperation". The event is
	 * triggered from the "Execute" button on the execution panel which posts the
	 * command.
	 * 
	 * The event handler will post the global command to trigger execution of the
	 * operation in the execution panel controller.
	 */
	@Command
	public void executeCreateReportOperation() {
		executeOperation(OperationNames.CREATE_REPORT);
	}

	/**
	 * Event handler for the command "cancelOperation". The event is triggered from
	 * the "Cancel Operation" button on the execution panel which posts the command.
	 * 
	 * The event handler will post the global command to trigger execution of the
	 * operation in the execution panel controller.
	 */
	@Command
	public void cancelOperation() {

		try {
			executeController.cancelOperation(sessionState.getExecutionId());

		} catch (Exception e) {
			// show and log error message
			logger.error(StackTraceHelper.getStrackTrace(e));
			Messagebox.show(e.getMessage());
			return;
		}
	}

	/**
	 * Create tree model. Tree is created with a null root node which must be
	 * replace when execution starts.
	 * 
	 * @return tree model.
	 */
	DefaultTreeModel<ExecutionResult> createTreeModel() {

		// TODO: determine of result is required or null is fine?

		ExecutionResult result = new ExecutionResultImpl("Default Root Node");
		DefaultTreeNode<ExecutionResult> rootNode = createTreeNode(result);

		// create tree model
		treeModel = new DefaultTreeModel<ExecutionResult>(rootNode);
		return treeModel;
	}

	/**
	 * Create tree node with execution result as data object.
	 * 
	 * @param result
	 *            Execution result which is inserted as data object in the node.
	 * 
	 * @return tree node with execution result as data object.
	 */
	DefaultTreeNode<ExecutionResult> createTreeNode(ExecutionResult result) {
		return new DefaultTreeNode<ExecutionResult>(result, new ArrayList<DefaultTreeNode<ExecutionResult>>());
	}

	/**
	 * Return true if a tree node is selected.
	 * 
	 * @return true if a tree node is selected.
	 */
	boolean isTreeNodeSelected() {
		return selectedTreeNode != null;
	}

	/**
	 * Return true if details are collapsed.
	 * 
	 * @return true if details are collapsed.
	 */
	boolean isDetailsCollapsed() {
		return isDetailsCollapsed;
	}

	/**
	 * Start operation.
	 * 
	 * @param notification
	 *            result notification.
	 */
	void startOperation(ExecutionResultNotification notification) {

		// initialize models
		resultMapping = new HashMap<ExecutionResult, DefaultTreeNode<ExecutionResult>>();
		selectedTreeNode = null;
		createTreeModel();
		showDetailsForSelectedItem();

		// get root node in model
		DefaultTreeNode<ExecutionResult> rootNode = (DefaultTreeNode<ExecutionResult>) treeModel.getRoot();

		// add mapping for the root node
		resultMapping.put(notification.getResult(), rootNode);

		// create new node with result and add it to the root node
		ExecutionResult result = notification.getResult();
		DefaultTreeNode<ExecutionResult> node = createTreeNode(result);
		resultMapping.put(result, node);
		rootNode.add(node);
	}

	/**
	 * Start execution.
	 * 
	 * @param notification
	 *            result notification.
	 */
	void StartExecution(ExecutionResultNotification notification) {

		// get result
		ExecutionResult result = notification.getResult();

		// get parent node
		DefaultTreeNode<ExecutionResult> parentNode = resultMapping.get(result.getParent());

		// if parent node is undefined then skip adding this node
		// parent node can be undefined if event belongs to earlier non
		// visualized execution
		if (parentNode == null) {

			// log warning with result description
			Map<String, String> resultMessages = result.getMessages();
			String description = resultMessages.get(ExecutionResult.MSG_MESSAGE);
			Object[] args = { description };
			logger.warn(webMessageProvider.getMessage("ep.skipped_tree_node", args));
			return;
		}

		// create new node with execution result and add it to the tree
		DefaultTreeNode<ExecutionResult> node = createTreeNode(result);
		resultMapping.put(result, node);
		parentNode.add(node);
	}

	/**
	 * Complete operation.
	 * 
	 * @param notification
	 *            result notification.
	 */
	void completeOperation(ExecutionResultNotification notification) {

		// clear mapping
		resultMapping.clear();

		try {
			// delete operation status
			executeController.deleteOperationStatus(sessionState.getExecutionId());
		} catch (ExecutionInfoNotFoundException e) {
			logger.error(StackTraceHelper.getStrackTrace(e));
		}

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ep.execution_info"), POST_STYLE, null, POST_LOCATION,
				POST_DURATION);

		// post global command to trigger report update in report panel view
		// model
		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, COMPLETED_OPERATION_GLOBALCOMMAND,
				NULL_GLOBALCOMMAND_ARGS);

	}

	/**
	 * Return the disable status for the rerun last executed operation button.
	 * 
	 * @return true if the button should be disabled.
	 */
	public boolean getLastExecutedOperationStatus() {
		String operation = sessionState.getLastExecutedOperation();
		if (operation == null)
			return true;
		if (operation.isEmpty())
			return true;
		return false;
	}

	/**
	 * Execute operation if session state is ready for execution of operation.
	 * 
	 * @return false if validate fails.
	 */
	void executeOperation(String operation) {

		// validate module is opened
		if (sessionState.getModuleInfo() == null) {
			String message = webMessageProvider.getMessage("ep.no_module_opened_failed");
			Messagebox.show(message);
			return;
		}

		// validate model is selected
		if ((sessionState.getEnvironment() == null) || (sessionState.getEnvironment().isEmpty())) {
			String message = webMessageProvider.getMessage("ep.no_model_selected_failed");
			Messagebox.show(message);
			return;
		}

		// set operation
		sessionState.setOperation(operation);

		// store last executed command
		sessionState.setLastExecutedOperation();

		// request focus on the execution tab
		sessionState.setRequestTabFocus(SessionState.WorkspaceTabs.EXECUTION);

		// post global command to trigger selection of execute tab on workspace
		// panel
		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
				NULL_GLOBALCOMMAND_ARGS);

		// post global command to which triggers execution in execution panel
		// controller
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(START_OPERATION_MODULE_INFO_ARG, sessionState.getModuleInfo());
		args.put(START_OPERATION_OPERATION_ARG, sessionState.getOperation());
		args.put(START_OPERATION_ENVIRONMENT_ARG, sessionState.getEnvironment());
		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, START_OPERATION_GLOBALCOMMAND, args);
	}

	/**
	 * Return true if execution is starting, i.e. the root result signals that it is
	 * running.
	 * 
	 * @param notification
	 *            The notification execution result to test.
	 * 
	 * @return true if execution is starting, i.e. the root result signals that it
	 *         is running.
	 */
	boolean isExecutionStarting(ExecutionResultNotification notification) {
		if (!notification.getResult().isRoot())
			return false;
		return (isResultExecuting(notification));
	}

	/**
	 * Return true if execution state is running for a execution result.
	 * 
	 * @param result
	 *            The execution result to test.
	 * 
	 * @return true if execution state is running for a execution result.
	 */
	boolean isResultExecuting(ExecutionResult result) {
		return (result.isExecuting());
	}

	/**
	 * Return true if execution state is running for a execution result.
	 * 
	 * @param ern
	 *            execution notification containing result to test.
	 * 
	 * @return true if execution state is running for a execution result.
	 */
	boolean isResultExecuting(ExecutionResultNotification ern) {
		return (ern.getState().equals(ExecutionResult.ExecutionState.EXECUTING));
	}

	/**
	 * Return true if execution is completed, i.e. the root result signals that it
	 * isn't running anymore.
	 * 
	 * @param ern
	 *            execution notification containing result to test.
	 * 
	 * @return true if execution is completed, i.e. the root result signals that it
	 *         isn't running anymore.
	 */
	boolean isExecutionCompleted(ExecutionResultNotification ern) {
		if (!ern.getResult().isRoot())
			return false;
		return (!isResultExecuting(ern));
	}

}
