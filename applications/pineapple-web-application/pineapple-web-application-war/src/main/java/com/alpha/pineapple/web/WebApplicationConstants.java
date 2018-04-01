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

package com.alpha.pineapple.web;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventQueues;

/**
 * Web application constants.
 */
public interface WebApplicationConstants {

	/**
	 * Reactor topic used to for forwarding core component execution result
	 * notifications in events.
	 */
	public static final String REACTOR_TOPIC_SERVICE_NOTIFICATION = "/webapp/service/execution-result-notification";

	/**
	 * Reactor topic used to for posting created report events.
	 */
	public static final String REACTOR_TOPIC_SERVICE_CREATED_REPORT = "/webapp/service/created-report";

	/**
	 * Reactor event used to signal report creation is completed.
	 */
	public static final String REACTOR_EVENT_CREATED_REPORT = "created-report";

	/**
	 * Queue name used to publish global commands.
	 */
	public static final String PINEAPPLE_ZK_QUEUE = "pineapple-queue";

	/**
	 * Scope used to published global commands.
	 */
	public static final String PINEAPPLE_ZK_SCOPE = EventQueues.SESSION;

	/**
	 * ZUL file for error message modal window.
	 */
	public static final String ERROR_MESSAGE_MODAL_ZUL = "error-message-modal.zul";

	/**
	 * ZUL file for reset credential password open report modal window.
	 */
	public static final String RESET_CREDENTIAL_PASSWORD_MODAL_ZUL = "reset-credential-password-modal.zul";

	/**
	 * ZUL file for create resource property modal window.
	 */
	public static final String CREATE_RESOURCE_PROPERTY_MODAL_ZUL = "add-resource-property-modal.zul";

	/**
	 * ZUL file for create model modal window.
	 */
	public static final String CREATE_MODEL_MODAL_ZUL = "add-model-modal.zul";

	/**
	 * ZUL file for select module modal window.
	 */
	public static final String OPENMODULE_MODAL_ZUL = "open-module-modal.zul";

	/**
	 * ZUL file for create scheduled operation modal window.
	 */
	public static final String CREATE_SCHEDULED_OPERATION_MODAL_ZUL = "add-scheduled-operation-modal.zul";

	/**
	 * ZUL file for delete scheduled operation modal window.
	 */
	public static final String DELETE_SCHEDULED_OPERATION_MODAL_ZUL = "delete-scheduled-operation-modal.zul";

	/**
	 * ZUL file for delete all scheduled operations modal window.
	 */
	public static final String DELETE_ALL_SCHEDULED_OPERATIONS_MODAL_ZUL = "delete-all-scheduled-operations-modal.zul";

	/**
	 * ZUL file for delete all reports modal window.
	 */
	public static final String DELETE_ALL_REPORTS_MODAL_ZUL = "delete-all-reports-modal.zul";

	/**
	 * NULL component arguments.
	 */
	public static final HashMap<String, Object> NULL_COMPONENT_ARGS = new HashMap<String, Object>();

	/**
	 * NULL parent window.
	 */
	public static final Component NULL_PARENT_WINDOW = null;

	/**
	 * Desktop attribute containing the Reactor-to-ZK event dispatcher.
	 */
	public static final String ATTR_DESKTOP_EVENT_DISPATCHER = "reactor-to-ZK-event-dispatcher";

	/**
	 * Execute operation global command name.
	 */
	public static final String START_OPERATION_GLOBALCOMMAND = "startOperation";

	/**
	 * Cancel operation global command name.
	 */
	public static final String CANCEL_OPERATION_GLOBALCOMMAND = "cancelOperation";

	/**
	 * Completed result execution global command name.
	 */
	public static final String COMPLETED_OPERATION_GLOBALCOMMAND = "completedOperation";

	/**
	 * Execution update global command name.
	 */
	public static final String EXECUTION_NOTIFICATION_GLOBALCOMMAND = "executionNotification";

	/**
	 * Completed report creation global command name.
	 */
	public static final String COMPLETED_REPORT_CREATION_GLOBALCOMMAND = "completedReportCreation";

	/**
	 * Completed report creation global command name.
	 */
	public static final String COMPLETED_ACTIVITY_CREATION_GLOBALCOMMAND = "completedActivityCreation";

	/**
	 * Create model (start) global command name.
	 */
	public static final String CREATE_MODEL_GLOBALCOMMAND = "createModel";

	/**
	 * Create model confirmed global command name.
	 */
	public static final String CREATE_MODEL_CONFIRMED_GLOBALCOMMAND = "createModelConfirmed";

	/**
	 * Save model global command name.
	 */
	public static final String SAVE_MODEL_GLOBALCOMMAND = "saveModel";

	/**
	 * Download model global command name.
	 */
	public static final String DOWNLOAD_MODEL_GLOBALCOMMAND = "downloadModel";

	/**
	 * Delete model (start) global command name.
	 */
	public static final String DELETE_MODEL_GLOBALCOMMAND = "deleteModel";

	/**
	 * Select tab on workspace global command name.
	 */
	public static final String SELECT_WORKSPACE_TAB_GLOBALCOMMAND = "selectWorkspaceTab";

	/**
	 * Save report global command name.
	 */
	public static final String SAVE_REPORT_GLOBALCOMMAND = "saveReport";

	/**
	 * Delete all reports global command name.
	 */
	public static final String DELETE_ALL_REPORTS_GLOBALCOMMAND = "deleteAllReports";

	/**
	 * Delete all reports confirmed global command name.
	 */
	public static final String DELETE_ALL_REPORTS_CONFIRMED_GLOBALCOMMAND = "deleteAllReportsConfirmed";

	/**
	 * Open module global command name.
	 */
	public static final String OPEN_MODULE_GLOBALCOMMAND = "openModule";

	/**
	 * Load module global command name.
	 */
	public static final String LOAD_MODULE_GLOBALCOMMAND = "loadModule";

	/**
	 * Close module global command name.
	 */
	public static final String CLOSE_MODULE_GLOBALCOMMAND = "closeModule";

	/**
	 * Upload module global command name.
	 */
	public static final String UPLOAD_MODULE_GLOBALCOMMAND = "uploadModule";

	/**
	 * Unpacked file entry global command name.
	 */
	public static final String UNPACKED_MODULE_ENTRY_GLOBALCOMMAND = "unpackedModuleEntry";

	/**
	 * File unpack update global command name.
	 */
	public static final String FILE_UNPACK_UPDATE_GLOBALCOMMAND = "fileUnpackUpdate";

	/**
	 * Create environment global command name.
	 */
	public static final String CREATE_ENVIRONMENT_GLOBALCOMMAND = "createEnvironment";

	/**
	 * Delete environment global command name.
	 */
	public static final String DELETE_ENVIRONMENT_GLOBALCOMMAND = "deleteEnvironment";

	/**
	 * Create resource global command name.
	 */
	public static final String CREATE_RESOURCE_GLOBALCOMMAND = "createResource";

	/**
	 * Delete resource global command name.
	 */
	public static final String DELETE_RESOURCE_GLOBALCOMMAND = "deleteResource";

	/**
	 * Create credential global command name.
	 */
	public static final String CREATE_CREDENTIAL_GLOBALCOMMAND = "createCredential";

	/**
	 * Delete credential global command name.
	 */
	public static final String DELETE_CREDENTIAL_GLOBALCOMMAND = "deleteCredential";

	/**
	 * Refresh environment configuration global command name.
	 */
	public static final String REFRESH_ENVIRONMENT_CONFIGURATION_GLOBALCOMMAND = "refreshEnvironmentConfiguration";

	/**
	 * Refresh environment configuration global command name.
	 */
	public static final String COLLAPSEEXAPAND_EXECUTIONRESULT_TREE_GLOBALCOMMAND = "collapseExpandExecutionResultTree";

	/**
	 * Reset credential password global command name.
	 */
	public static final String RESET_CREDENTIAL_PASSWORD_CONFIRMED_GLOBALCOMMAND = "resetCredentialPasswordConfirmed";

	/**
	 * Create resource property global command name.
	 */
	public static final String CREATE_RESOURCE_PROPERTY_CONFIRMED_GLOBALCOMMAND = "createResourcePropertyConfirmed";

	/**
	 * Create scheduled operation global command name.
	 */
	public static final String CREATE_SCHEDULED_OPERATION_GLOBALCOMMAND = "createScheduledOperation";

	/**
	 * Create scheduled operation confirmed global command name.
	 */
	public static final String CREATE_SCHEDULED_OPERATION_CONFIRMED_GLOBALCOMMAND = "createScheduledOperationConfirmed";

	/**
	 * Delete scheduled operation global command name.
	 */
	public static final String DELETE_SCHEDULED_OPERATION_GLOBALCOMMAND = "deleteScheduledOperation";

	/**
	 * Delete scheduled operation confirmed global command name.
	 */
	public static final String DELETE_SCHEDULED_OPERATION_CONFIRMED_GLOBALCOMMAND = "deleteScheduledOperationConfirmed";

	/**
	 * Delete all scheduled operations global command name.
	 */
	public static final String DELETE_ALL_SCHEDULED_OPERATIONS_GLOBALCOMMAND = "deleteAllScheduledOperations";

	/**
	 * Delete all scheduled operations confirmed global command name.
	 */
	public static final String DELETE_ALL_SCHEDULED_OPERATIONS_CONFIRMED_GLOBALCOMMAND = "deleteAllScheduledOperationsConfirmed";

	/**
	 * NULL global command arguments.
	 */
	public static final Map<String, Object> NULL_GLOBALCOMMAND_ARGS = new HashMap<String, Object>();

	/**
	 * Media argument to the upload module command.
	 */
	public static final String MEDIA_ARG = "media";

	/**
	 * UnpackedEntryEvent argument to the unpacked file entry command.
	 */
	public static final String UNPACKED_ENTRY_EVENT_ARG = "unpackedEntryEvent";

	/**
	 * FileUnpackUpdateEvent argument to the file unpack update command.
	 */
	public static final String FILE_UNPACK_EVENT_ARG = "fileUnpackUpdateEvent";

	/**
	 * Module info argument to the load module command.
	 */
	public static final String LOAD_MODULE_MODULE_INFO_ARG = "module-info";

	/**
	 * Event argument to the execution event command.
	 */
	public static final String EXECUTE_ARG = "executeEvent";

	/**
	 * Event argument to the executionNotification event command.
	 */
	public static final String NOTIFICATION_ARG = "notification";

	/**
	 * Module info argument to the start operation global command.
	 */
	public static final String START_OPERATION_MODULE_INFO_ARG = "module-info";

	/**
	 * Operation argument to the start operation global command.
	 */
	public static final String START_OPERATION_OPERATION_ARG = "operation";

	/**
	 * Environment argument to the start operation global command.
	 */
	public static final String START_OPERATION_ENVIRONMENT_ARG = "environment";

	/**
	 * Password argument to reset credential password global command.
	 */
	public static final String CREDENTIAL_PASSWORD_ARG = "credentialPassword";

	/**
	 * Key argument to create resource property global command.
	 */
	public static final String RESOURCE_PROPERTY_KEY_ARG = "resourcePropertyKey";

	/**
	 * Value argument to create resource property global command.
	 */
	public static final String RESOURCE_PROPERTY_VALUE_ARG = "resourcePropertyValue";

	/**
	 * Name argument to create model global command.
	 */
	public static final String MODEL_NAME_KEY_ARG = "modelName";

	/**
	 * Exception argument to show exception model window .
	 */
	static final String ERROR_MESSAGE_MODAL_EXCEPTION_ARG = "exception";

	/**
	 * Post duration in ms.
	 */
	public static final int POST_DURATION = 5000;

	/**
	 * Post style.
	 */
	public static final String POST_STYLE = "info";

	/**
	 * Post location.
	 */
	public static final String POST_LOCATION = "top_right";

	/**
	 * Configuration REST service URI.
	 */
	public static final String REST_CONFIGURATION_URI = "/api/configuration";

	/**
	 * Configuration REST refresh service path.
	 */
	public static final String REST_CONFIGURATION_REFRESH_PATH = "/refresh";

	/**
	 * Configuration REST refresh service URI.
	 */
	public static final String REST_CONFIGURATION_REFRESH_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_REFRESH_PATH;

	/**
	 * Configuration REST create environment service path.
	 */
	public static final String REST_CONFIGURATION_CREATE_ENVIRONMENT_PATH = "/environment/{environment}/description/{description}";

	/**
	 * Configuration REST create environment service URI.
	 */
	public static final String REST_CONFIGURATION_CREATE_ENVIRONMENT_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_CREATE_ENVIRONMENT_PATH;

	/**
	 * Configuration REST update environment service path.
	 */
	public static final String REST_CONFIGURATION_UPDATE_ENVIRONMENT_PATH = "/environment/{environment}";

	/**
	 * Configuration REST update environment service URI.
	 */
	public static final String REST_CONFIGURATION_UPDATE_ENVIRONMENT_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_UPDATE_ENVIRONMENT_PATH;

	/**
	 * Configuration REST delete environment service path.
	 */
	public static final String REST_CONFIGURATION_DELETE_ENVIRONMENT_PATH = "/environment/{environment}";

	/**
	 * Configuration REST delete environment service URI.
	 */
	public static final String REST_CONFIGURATION_DELETE_ENVIRONMENT_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_DELETE_ENVIRONMENT_PATH;

	/**
	 * Configuration REST create resource service path.
	 */
	public static final String REST_CONFIGURATION_CREATE_RESOURCE_PATH = "/environment/{environment}/resource/{resource}/pluginid/{pluginid}/credentialidref/{credentialidref}";

	/**
	 * Configuration REST create resource service URI.
	 */
	public static final String REST_CONFIGURATION_CREATE_RESOURCE_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_CREATE_RESOURCE_PATH;

	/**
	 * Configuration REST update resource service path.
	 */
	public static final String REST_CONFIGURATION_UPDATE_RESOURCE_PATH = "/environment/{environment}/resource/{resource}";

	/**
	 * Configuration REST update resource service URI.
	 */
	public static final String REST_CONFIGURATION_UPDATE_RESOURCE_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_UPDATE_RESOURCE_PATH;

	/**
	 * Configuration REST delete resource service path.
	 */
	public static final String REST_CONFIGURATION_DELETE_RESOURCE_PATH = "/environment/{environment}/resource/{resource}";

	/**
	 * Configuration REST delete resource service URI.
	 */
	public static final String REST_CONFIGURATION_DELETE_RESOURCE_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_DELETE_RESOURCE_PATH;

	/**
	 * Configuration REST create resource property service path.
	 */
	public static final String REST_CONFIGURATION_CREATE_PROPERTY_PATH = "/environment/{environment}/resource/{resource}/key/{key}/value/{value}";

	/**
	 * Configuration REST create resource property service URI.
	 */
	public static final String REST_CONFIGURATION_CREATE_PROPERTY_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_CREATE_PROPERTY_PATH;

	/**
	 * Configuration REST delete resource property service path.
	 */
	public static final String REST_CONFIGURATION_DELETE_PROPERTY_PATH = "/environment/{environment}/resource/{resource}/key/{key}";

	/**
	 * Configuration REST delete resource property service URI.
	 */
	public static final String REST_CONFIGURATION_DELETE_PROPERTY_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_DELETE_PROPERTY_PATH;

	/**
	 * Configuration REST get resource configuration service path.
	 */
	public static final String REST_CONFIGURATION_GET_RESOURCES_PATH = "/resources";

	/**
	 * Configuration REST get resource configuration service URI.
	 */
	public static final String REST_CONFIGURATION_GET_RESOURCES_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_GET_RESOURCES_PATH;

	/**
	 * Configuration REST get credential service path.
	 */
	public static final String REST_CONFIGURATION_GET_CREDENTIAL_PATH = "/environment/{environment}/credential/{credential}";

	/**
	 * Configuration REST get credential service URI.
	 */
	public static final String REST_CONFIGURATION_GET_CREDENTIAL_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_GET_CREDENTIAL_PATH;

	/**
	 * Configuration REST create credential service path.
	 */
	public static final String REST_CONFIGURATION_CREATE_CREDENTIAL_PATH = "/environment/{environment}/credential/{credential}/user/{user}/password/{password}";

	/**
	 * Configuration REST create credential service URI.
	 */
	public static final String REST_CONFIGURATION_CREATE_CREDENTIAL_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_CREATE_CREDENTIAL_PATH;

	/**
	 * Configuration REST update credential service path.
	 */
	public static final String REST_CONFIGURATION_UPDATE_CREDENTIAL_PATH = "/environment/{environment}/credential/{credential}";

	/**
	 * Configuration REST update credential service URI.
	 */
	public static final String REST_CONFIGURATION_UPDATE_CREDENTIAL_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_UPDATE_CREDENTIAL_PATH;

	/**
	 * Configuration REST delete credential service path.
	 */
	public static final String REST_CONFIGURATION_DELETE_CREDENTIAL_PATH = "/environment/{environment}/credential/{credential}";

	/**
	 * Configuration REST delete credential service URI.
	 */
	public static final String REST_CONFIGURATION_DELETE_CREDENTIAL_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_DELETE_CREDENTIAL_PATH;

	/**
	 * Configuration REST get credential configuration service path.
	 */
	public static final String REST_CONFIGURATION_GET_CREDENTIALS_PATH = "/credentials";

	/**
	 * Configuration REST get credential configuration service URI.
	 */
	public static final String REST_CONFIGURATION_GET_CREDENTIALS_URI = REST_CONFIGURATION_URI
			+ REST_CONFIGURATION_GET_CREDENTIALS_PATH;

	/**
	 * Module REST service URI.
	 */
	public static final String REST_MODULE_URI = "/api/module";

	/**
	 * Module REST refresh service path.
	 */
	public static final String REST_MODULE_REFRESH_PATH = "/refresh";

	/**
	 * Module REST upload service path.
	 */
	public static final String REST_MODULE_UPLOAD_PATH = "";

	/**
	 * Module REST delete service path.
	 */
	public static final String REST_MODULE_DELETE_PATH = "/{module}";

	/**
	 * Module REST refresh service URI.
	 */
	public static final String REST_MODULE_REFRESH_URI = REST_MODULE_URI + REST_MODULE_REFRESH_PATH;

	/**
	 * Module REST delete service URI.
	 */
	public static final String REST_MODULE_DELETE_URI = REST_MODULE_URI + REST_MODULE_DELETE_PATH;

	/**
	 * Model REST delete service path.
	 */
	public static final String REST_MODULE_DELETE_MODEL_PATH = "/{module}/environment/{environment}";

	/**
	 * Model REST delete service URI.
	 */
	public static final String REST_MODULE_DELETE_MODEL_URI = REST_MODULE_URI + REST_MODULE_DELETE_MODEL_PATH;

	/**
	 * Model REST create service path.
	 */
	public static final String REST_MODULE_CREATE_MODEL_PATH = "/{module}/environment/{environment}";

	/**
	 * Model REST create service URI.
	 */
	public static final String REST_MODULE_CREATE_MODEL_URI = REST_MODULE_URI + REST_MODULE_CREATE_MODEL_PATH;

	/**
	 * Module REST get modules service path.
	 */
	public static final String REST_MODULE_GET_MODULES_PATH = "/modules";

	/**
	 * Module REST get modules service URI.
	 */
	public static final String REST_MODULE_GET_MODULES_URI = REST_MODULE_URI + REST_MODULE_GET_MODULES_PATH;

	/**
	 * Schedule REST service URI.
	 */
	public static final String REST_SCHEDULE_URI = "/api/schedule";

	/**
	 * Schedule REST schedule service path.
	 */
	public static final String REST_SCHEDULE_SCHEDULE_PATH = "/name/{name}/module/{module}/environment/{environment}/operation/{operation}/cron/{cron}/description/{description}";

	/**
	 * Schedule REST schedule service URI.
	 */
	public static final String REST_SCHEDULE_SCHEDULE_URI = REST_SCHEDULE_URI + REST_SCHEDULE_SCHEDULE_PATH;

	/**
	 * Schedule REST get scheduled operations service path.
	 */
	public static final String REST_SCHEDULE_GET_OPERATIONS_PATH = "/operations";

	/**
	 * Schedule REST get scheduled operations service URI.
	 */
	public static final String REST_SCHEDULE_GET_OPERATIONS_URI = REST_SCHEDULE_URI + REST_SCHEDULE_GET_OPERATIONS_PATH;

	/**
	 * Schedule REST delete scheduled operation service path.
	 */
	public static final String REST_SCHEDULE_DELETE_OPERATION_PATH = "/{name}";

	/**
	 * Schedule REST delete scheduled operation service URI.
	 */
	public static final String REST_SCHEDULE_DELETE_OPERATION_URI = REST_SCHEDULE_URI
			+ REST_SCHEDULE_DELETE_OPERATION_PATH;

	/**
	 * Schedule REST delete scheduled operations service path.
	 */
	public static final String REST_SCHEDULE_DELETE_OPERATIONS_PATH = "/operations";

	/**
	 * Schedule REST delete scheduled operations service URI.
	 */
	public static final String REST_SCHEDULE_DELETE_OPERATIONS_URI = REST_SCHEDULE_URI
			+ REST_SCHEDULE_DELETE_OPERATIONS_PATH;

	/**
	 * Report REST service URI.
	 */
	public static final String REST_REPORT_URI = "/api/report";

	/**
	 * Schedule REST get reports service path.
	 */
	public static final String REST_REPORT_GET_REPORTS_PATH = "/reports";

	/**
	 * Schedule REST get reports service URI.
	 */
	public static final String REST_REPORT_GET_REPORTS_URI = REST_REPORT_URI + REST_REPORT_GET_REPORTS_PATH;

	/**
	 * Schedule REST delete reports service path.
	 */
	public static final String REST_REPORT_DELETE_REPORTS_PATH = "/reports";

	/**
	 * Schedule REST delete reports service URI.
	 */
	public static final String REST_REPORT_DELETE_REPORTS_URI = REST_REPORT_URI + REST_REPORT_DELETE_REPORTS_PATH;

	/**
	 * Execute REST service URI.
	 */
	public static final String REST_EXECUTE_URI = "/api/execute";

	/**
	 * Execute REST execute service path.
	 */
	public static final String REST_EXECUTE_EXECUTE_PATH = "/module/{module}/operation/{operation}/environment/{environment}";

	/**
	 * Execute REST execute service URI.
	 */
	public static final String REST_EXECUTE_EXECUTE_URI = REST_EXECUTE_URI + REST_EXECUTE_EXECUTE_PATH;

	/**
	 * Execute REST get/delete operation status service path.
	 */
	public static final String REST_EXECUTE_STATUS_PATH = "/{id}";

	/**
	 * Execute REST get operation status service URI.
	 */
	public static final String REST_EXECUTE_STATUS_URI = REST_EXECUTE_URI + REST_EXECUTE_STATUS_PATH;

	/**
	 * Execute REST cancel operation service path.
	 */
	public static final String REST_EXECUTE_CANCEL_PATH = "/cancel/{id}";

	/**
	 * Execute REST get operation status service URI.
	 */
	public static final String REST_EXECUTE_CANCEL_URI = REST_EXECUTE_URI + REST_EXECUTE_CANCEL_PATH;

	/**
	 * System REST service URI.
	 */
	public static final String REST_SYSTEM_URI = "/api/system";

	/**
	 * System REST get simple initialization service path.
	 */
	public static final String REST_SYSTEM_SIMPLE_STATUS_PATH = "/status";

	/**
	 * System REST get simple initialization service URI.
	 */
	public static final String REST_SYSTEM_SIMPLE_STATUS_URI = REST_SYSTEM_URI + REST_SYSTEM_SIMPLE_STATUS_PATH;

	/**
	 * System REST get version service path.
	 */
	public static final String REST_SYSTEM_VERSION_PATH = "/version";

	/**
	 * System REST get version service URI.
	 */
	public static final String REST_SYSTEM_VERSION_URI = REST_SYSTEM_URI + REST_SYSTEM_VERSION_PATH;

	/**
	 * Location Header.
	 */
	public static final String LOCATION_HEADER = "Location";

	/**
	 * File REST service URI.
	 */
	public static final String REST_FILE_URI = "/api/file";

	/**
	 * ZK Media char set. Used by downloable assets.
	 */
	static final String ZK_MEDIA_CHARSET = "utf-8";

	/**
	 * ZK Media content type. Used by downloable assets.
	 */
	static final String ZK_MEDIA_CONTENT_TYPE = "text/html";

	/**
	 * File name for HTML reports.
	 */
	public static final String HTML_REPORT_FILE = "basic-report.html";

	/**
	 * Execution result key used to messages identifying the operation name.
	 */
	public static final String MSG_OPERATION = "Operation";

	/**
	 * Execution result key used to messages identifying the environment.
	 */
	public static final String MSG_ENVIRONMENT = "Environment";

	/**
	 * Execution result key used to messages identifying the module.
	 */
	public static final String MSG_MODULE = "Module";

	/**
	 * Execution result key used to messages identifying the module file.
	 */
	public static final String MSG_MODULE_FILE = "Module file";

	/**
	 * Simple date format used by the web application to format date-time.
	 */
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd/MM/yy");

	/**
	 * Default file name for the set of registered reports .
	 */
	public static final String REPORTS_FILE = "reports.xml";

}
