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

package com.alpha.pineapple.web.zk.controller;

import static com.alpha.pineapple.web.WebApplicationConstants.CLOSE_MODULE_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.CREATE_CREDENTIAL_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.CREATE_ENVIRONMENT_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.CREATE_MODEL_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.CREATE_RESOURCE_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.CREATE_SCHEDULED_OPERATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_ALL_REPORTS_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_CREDENTIAL_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_ENVIRONMENT_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_MODEL_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_RESOURCE_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_SCHEDULED_OPERATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DOWNLOAD_MODEL_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_GLOBALCOMMAND_ARGS;
import static com.alpha.pineapple.web.WebApplicationConstants.OPEN_MODULE_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_SCOPE;
import static com.alpha.pineapple.web.WebApplicationConstants.REFRESH_ENVIRONMENT_CONFIGURATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.SAVE_MODEL_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.SAVE_REPORT_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.SELECT_WORKSPACE_TAB_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.START_OPERATION_ENVIRONMENT_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.START_OPERATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.START_OPERATION_MODULE_INFO_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.START_OPERATION_OPERATION_ARG;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.web.model.SessionState;
import com.alpha.pineapple.web.model.SessionState.WorkspaceTabs;
import com.alpha.pineapple.web.report.ReportRepository;
import com.alpha.pineapple.web.spring.rest.SystemController;

/**
 * ZK controller for the main menu bar.
 */
public class Menu extends SelectorComposer<Component> {

    /**
     * Serial Version UID.
     */
    static final long serialVersionUID = 1755241176533984084L;

    /**
     * ZUL file for open report modal window.
     */
    static final String OPENREPORT_MODAL_ZUL = "open-report-modal.zul";

    /**
     * ZUL file for download report modal window.
     */
    static final String DOWNLOAD_REPORT_MODAL_ZUL = "download-report-modal.zul";

    /**
     * ZUL file for delete module modal window.
     */
    static final String DELETEMODULE_MODAL_ZUL = "delete-module-modal.zul";

    /**
     * ZUL file for upload module modal window.
     */
    static final String UPLOAD_MODULE_MODAL_ZUL = "upload-module-modal.zul";

    /**
     * NULL component arguments.
     */
    static final HashMap<String, Object> NULL_COMPONENT_ARGS = new HashMap<String, Object>();

    /**
     * NULL ZK parent for modal windows.
     */
    static final Component NULL_PARENT = null;

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Message provider for I18N support.
     */
    @WireVariable
    MessageProvider webMessageProvider;

    /**
     * Session state.
     */
    @WireVariable
    SessionState sessionState;

    /**
     * Report repository.
     */
    @WireVariable
    ReportRepository reportRepository;

    /**
     * System controller.
     */
    @WireVariable
    SystemController systemController;

    /**
     * ZK Window.
     */
    @Wire
    Window menuWindow;

    /**
     * ZK execute Menu.
     */
    @Wire
    Menuitem executeTestMenuitem;

    /**
     * Event handler for the onClick event for the "Open Module" menu item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #openModuleMenuitem")
    public void openModule(Event evt) {

	// post global command to trigger selection of module tab on workspace
	// panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);

	// post global command to which triggers opening the model in module
	// panel view model.
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, OPEN_MODULE_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Close Module" menu item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #closeModuleMenuitem")
    public void closeModule(Event evt) {

	// post global command to which triggers saving the model in module
	// panel view model.
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, CLOSE_MODULE_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Upload Module" menu item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #uploadModuleMenuitem")
    public void uploadModule(Event evt) {
	Window modalWindow = null;

	try {
	    // open modal module selection window
	    modalWindow = (Window) Executions.createComponents(UPLOAD_MODULE_MODAL_ZUL, menuWindow,
		    NULL_COMPONENT_ARGS);
	    modalWindow.doModal();

	    // request focus on the module tab
	    sessionState.setRequestTabFocus(WorkspaceTabs.MODULE);

	    // post global command to trigger selection of module tab on
	    // workspace panel
	    BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		    NULL_GLOBALCOMMAND_ARGS);

	} catch (Exception e) {

	    // log error message
	    Object[] args = { StackTraceHelper.getStrackTrace(e) };
	    String message = webMessageProvider.getMessage("mc.upload_module_failed", args);
	    logger.error(message);

	    // detach window
	    if (modalWindow != null)
		modalWindow.detach();
	}
    }

    /**
     * Event handler for the onClick event for the "Delete Module" menu item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #deleteModuleMenuitem")
    public void deleteModule(Event evt) {
	Window modalWindow = null;

	try {
	    // open modal module selection window
	    modalWindow = (Window) Executions.createComponents(DELETEMODULE_MODAL_ZUL, menuWindow, NULL_COMPONENT_ARGS);
	    modalWindow.doModal();

	    // request focus on the module tab
	    sessionState.setRequestTabFocus(WorkspaceTabs.MODULE);

	    // post global command to trigger selection of module tab on
	    // workspace panel
	    BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		    NULL_GLOBALCOMMAND_ARGS);

	} catch (Exception e) {

	    // log error message
	    Object[] args = { StackTraceHelper.getStrackTrace(e) };
	    String message = webMessageProvider.getMessage("mc.delete_module_failed", args);
	    logger.error(message);

	    // detach window
	    if (modalWindow != null)
		modalWindow.detach();
	}
    }

    /**
     * Event handler for the onClick event for the "Create Model" menu item.
     * 
     * Post global command to trigger model creation in the module panel view
     * model.
     * 
     * Global command has to be posted "manually" in this event handler since
     * composer can't trigger global commands in .ZUL page.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #createModelMenuitem")
    public void createModel(Event evt) {

	// post global command to which triggers model creation in module panel
	// view model.
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, CREATE_MODEL_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Save Model" menu item.
     * 
     * Post global command to trigger saving the model in the module panel view
     * model.
     * 
     * Global command has to be posted "manually" in this event handler since
     * composer can't trigger global commands in .ZUL page.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #saveModelMenuitem")
    public void saveModel(Event evt) {

	// post global command to which triggers saving the model in module
	// panel view model.
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SAVE_MODEL_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Download Model" menu item.
     * 
     * Post global command to trigger download the model into the browser from
     * the module panel view model.
     * 
     * Global command has to be posted "manually" in this event handler since
     * composer can't trigger global commands in .ZUL page.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #downloadModelMenuitem")
    public void downloadModel(Event evt) {

	// post global command to which triggers model download in module panel
	// view model.
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, DOWNLOAD_MODEL_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Delete Model" menu item.
     * 
     * Post global command to trigger model deletion of the selected model in
     * the module panel view model. Global command has to be posted "manually"
     * in this event handler since composer can't trigger global commands in
     * .ZUL page.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #deleteModelMenuitem")
    public void deleteModel(Event evt) {

	// post global command to which triggers model creation in module panel
	// view model.
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, DELETE_MODEL_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Run Test" menu item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #executeTestMenuitem")
    public void executeTest(Event evt) {
	executeOperation(OperationNames.TEST);
    }

    /**
     * Event handler for the onClick event for the "Deploy Configuration" menu
     * item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #executeDeployConfigurationMenuitem")
    public void executeDeployConfiguration(Event evt) {
	executeOperation(OperationNames.DEPLOY_CONFIGURATION);
    }

    /**
     * Event handler for the onClick event for the "Deploy Application" menu
     * item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #executeDeployApplicationMenuitem")
    public void executeDeployApplication(Event evt) {
	executeOperation(OperationNames.DEPLOY_APPLICATION);
    }

    /**
     * Event handler for the onClick event for the "Start Application" menu
     * item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #executeStartApplicationMenuitem")
    public void executeStartApplication(Event evt) {
	executeOperation(OperationNames.START_APPLICATION);
    }

    /**
     * Event handler for the onClick event for the "Stop Application" menu item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #executeStopApplicationMenuitem")
    public void executeStopApplication(Event evt) {
	executeOperation(OperationNames.STOP_APPLICATION);
    }

    /**
     * Event handler for the onClick event for the "Undeploy Application" menu
     * item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #executeUndeployApplicationMenuitem")
    public void executeUndeployApplication(Event evt) {
	executeOperation(OperationNames.UNDEPLOY_APPLICATION);
    }

    /**
     * Event handler for the onClick event for the "Undeploy Configuration" menu
     * item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #executeUndeployConfigurationMenuitem")
    public void executeUndeployConfiguration(Event evt) throws InterruptedException {
	executeOperation(OperationNames.UNDEPLOY_CONFIGURATION);
    }

    /**
     * Event handler for the onClick event for the "Create Report" menu item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #executeCreateReportMenuitem")
    public void executeCreateReport(Event evt) throws InterruptedException {
	executeOperation(OperationNames.CREATE_REPORT);
    }

    /**
     * Event handler for the onClick event for the "Create Scheduled Operation"
     * menu item.
     * 
     * Post global command to trigger scheduled operation creation in the
     * scheduled operation panel view model.
     * 
     * Global command has to be posted "manually" in this event handler since
     * composer can't trigger global commands in .ZUL page.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #createScheduledOperationMenuitem")
    public void createScheduledOperation(Event evt) {

	// request focus on the scheduled operation tab
	sessionState.setRequestTabFocus(WorkspaceTabs.SCHEDULED_OPERATIONS);

	// post global command to trigger selection of scheduled operations tab
	// on workspace panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);

	// post global command to which triggers scheduled operation creation in
	// scheduled operation panel view model.
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, EventQueues.APPLICATION,
		CREATE_SCHEDULED_OPERATION_GLOBALCOMMAND, NULL_GLOBALCOMMAND_ARGS);

    }

    /**
     * Event handler for the onClick event for the "Delete Scheduled Operation"
     * menu item.
     * 
     * Post global command to trigger scheduled operation deletion in the
     * scheduled operation panel view model.
     * 
     * Global command has to be posted "manually" in this event handler since
     * composer can't trigger global commands in .ZUL page.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #deleteScheduledOperationMenuitem")
    public void deleteScheduledOperation(Event evt) {

	// request focus on the scheduled operation tab
	sessionState.setRequestTabFocus(WorkspaceTabs.SCHEDULED_OPERATIONS);

	// post global command to trigger selection of scheduled operations tab
	// on workspace panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);

	// post global command to which triggers scheduled operation creation in
	// scheduled operation panel view model.
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, EventQueues.APPLICATION,
		DELETE_SCHEDULED_OPERATION_GLOBALCOMMAND, NULL_GLOBALCOMMAND_ARGS);

    }

    /**
     * Event handler for the onClick event for the "Download report" menu item.
     * 
     * Post global command to trigger saving the report in the report panel view
     * model. Global command has to be posted "manually" in this event handler
     * since composer can't trigger global commands in .ZUL page.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #downloadReportMenuitem")
    public void downloadReport(Event evt) {

	// request focus on the execution tab
	sessionState.setRequestTabFocus(SessionState.WorkspaceTabs.REPORT);

	// post global command to trigger selection of report tab on workspace
	// panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);

	// post global command to which triggers saving the report in report
	// panel view model.
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, EventQueues.APPLICATION, SAVE_REPORT_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Delete all reports" menu
     * item.
     * 
     * Post global command to trigger deletion of all reports in the report
     * panel view model. Global command has to be posted "manually" in this
     * event handler since composer can't trigger global commands in .ZUL page.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #deleteAllReportsMenuitem")
    public void deleteAllReports(Event evt) {

	// request focus on the execution tab
	sessionState.setRequestTabFocus(SessionState.WorkspaceTabs.REPORT);

	// post global command to trigger selection of report tab on workspace
	// panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);

	// post global command to which triggers deletion of the reports in
	// report
	// panel view model.
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, EventQueues.APPLICATION, DELETE_ALL_REPORTS_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Create Resource" menu item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #createResourceMenuitem")
    public void createResource(Event evt) {

	// request focus on the configuration tab
	sessionState.setRequestTabFocus(WorkspaceTabs.CONFIGURATION);

	// post global command to trigger selection of tab on workspace panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);

	// post global command to trigger update at view model
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, CREATE_RESOURCE_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Delete Resource" menu item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #deleteResourceMenuitem")
    public void deleteResource(Event evt) {

	// request focus on the configuration tab
	sessionState.setRequestTabFocus(WorkspaceTabs.CONFIGURATION);

	// post global command to trigger selection of tab on workspace panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);

	// post global command to trigger update at view model
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, DELETE_RESOURCE_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Create Credential" menu
     * item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #createCredentialMenuitem")
    public void createCredential(Event evt) {

	// request focus on the configuration tab
	sessionState.setRequestTabFocus(WorkspaceTabs.CONFIGURATION);

	// post global command to trigger selection of tab on workspace panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);

	// post global command to trigger update at view model
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, CREATE_CREDENTIAL_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Delete Credential" menu
     * item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #deleteCredentialMenuitem")
    public void deleteCredential(Event evt) {

	// request focus on the configuration tab
	sessionState.setRequestTabFocus(WorkspaceTabs.CONFIGURATION);

	// post global command to trigger selection of tab on workspace panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);

	// post global command to trigger update at view model
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, DELETE_CREDENTIAL_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Create Environment" menu
     * item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #createEnvironmentMenuitem")
    public void createEnvironment(Event evt) {

	// request focus on the configuration tab
	sessionState.setRequestTabFocus(WorkspaceTabs.CONFIGURATION);

	// post global command to trigger selection of tab on workspace panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);

	// post global command to trigger update at view model
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, CREATE_ENVIRONMENT_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Delete Environment" menu
     * item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #deleteEnvironmentMenuitem")
    public void deleteEnvironment(Event evt) {

	// request focus on the configuration tab
	sessionState.setRequestTabFocus(WorkspaceTabs.CONFIGURATION);

	// post global command to trigger selection of tab on workspace panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);

	// post global command to trigger update at view model
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, DELETE_ENVIRONMENT_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the
     * "Refresh Environment Configuration" menu item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #refreshEnvironmentConfigurationMenuitem")
    public void refreshEnvironmentConfiguration(Event evt) {

	// request focus on the configuration tab
	sessionState.setRequestTabFocus(WorkspaceTabs.CONFIGURATION);

	// post global command to trigger selection of tab on workspace panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);

	// post global command to trigger update at view model
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE,
		REFRESH_ENVIRONMENT_CONFIGURATION_GLOBALCOMMAND, NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "Enable debug info" menu
     * item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #enableDebugInfoMenuitem")
    public void enableDebugInfo(Event evt) {

	// toggle debug state
	sessionState.setDebugInfoEnabled(!sessionState.isDebugInfoEnabled());

	// request focus on the debug tab
	if (sessionState.isDebugInfoEnabled()) {
	    sessionState.setRequestTabFocus(WorkspaceTabs.DEBUG);
	} else {
	    sessionState.setRequestTabFocus(WorkspaceTabs.MODULE);
	}

	// post global command to trigger selection of tab on workspace panel
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the onClick event for the "About Pineapple" menu item.
     * 
     * @param evt
     *            Event object.
     */
    @Listen("onClick = #aboutPineappleMenuitem")
    public void aboutPineapple(Event evt) {
	Messagebox.show(systemController.getVersion());
    }

    /**
     * Execute operation if session state is ready for execution of operation.
     * 
     * @return false if validate fails.
     */
    void executeOperation(String operation) {

	// validate module is opened
	if (sessionState.getModuleInfo() == null) {
	    String message = webMessageProvider.getMessage("mc.no_module_opened_failed");
	    Messagebox.show(message);
	    return;
	}

	// validate model is selected
	if ((sessionState.getEnvironment() == null) || (sessionState.getEnvironment().isEmpty())) {
	    String message = webMessageProvider.getMessage("mc.no_model_selected_failed");
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
	args.put(START_OPERATION_OPERATION_ARG, sessionState.getLastExecutedOperation());
	args.put(START_OPERATION_ENVIRONMENT_ARG, sessionState.getEnvironment());
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, START_OPERATION_GLOBALCOMMAND, args);
    }

}
