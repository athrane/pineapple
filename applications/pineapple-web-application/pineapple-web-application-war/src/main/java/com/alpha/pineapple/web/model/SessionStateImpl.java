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

package com.alpha.pineapple.web.model;

import org.apache.log4j.Logger;

import com.alpha.pineapple.model.execution.scheduled.ScheduledOperation;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.web.account.Account;
import com.alpha.pineapple.web.account.AccountImpl;
import com.alpha.pineapple.model.report.Report;

/**
 * Contains local session state for a single user.
 */
public class SessionStateImpl implements SessionState {

	/**
	 * The empty string.
	 */
	static final String EMPTY_STRING = "";

	/**
	 * Module Info.
	 */
	ModuleInfo moduleInfo;

	/**
	 * Current selected operation.
	 */
	String operation = EMPTY_STRING;

	/**
	 * Current selected environment.
	 */
	String environment = EMPTY_STRING;

	/**
	 * Last executed operation
	 */
	String lastExecutedOperation = EMPTY_STRING;

	/**
	 * Selected report.
	 */
	Report report;

	/**
	 * Define tab which request focus in the workspace.
	 */
	WorkspaceTabs tabRequestingFocus = WorkspaceTabs.MODULE;

	/**
	 * Defines whether debug information should be shown in the application.
	 */
	boolean isDebugInfoEnabled = false;

	/**
	 * Execution ID
	 */
	String executionId;

	/**
	 * Selected user activity.
	 */
	Activity activity;

	/**
	 * Selected scheduled operation.
	 */
	ScheduledOperation scheduledOperation;

	/**
	 * User account.
	 */
	AccountImpl account;

	public SessionStateImpl() {
		String username = System.getProperty("user.name");
		account = new AccountImpl(username);
	}

	public ModuleInfo getModuleInfo() {
		return moduleInfo;
	}

	public void setModuleInfo(ModuleInfo info) {
		this.moduleInfo = info;
		this.environment = EMPTY_STRING;
		this.operation = EMPTY_STRING;
		this.report = null;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	@Override
	public Report getReport() {
		return report;
	}

	@Override
	public boolean isDebugInfoEnabled() {
		return isDebugInfoEnabled;
	}

	@Override
	public void setDebugInfoEnabled(boolean value) {
		isDebugInfoEnabled = value;
	}

	@Override
	public void setRequestTabFocus(WorkspaceTabs tab) {
		this.tabRequestingFocus = tab;
	}

	@Override
	public WorkspaceTabs getRequestTabFocus() {
		return tabRequestingFocus;
	}

	@Override
	public void setLastExecutedOperation() {
		lastExecutedOperation = operation;
	}

	@Override
	public String getLastExecutedOperation() {
		return lastExecutedOperation;
	}

	@Override
	public void setExecutionId(String id) {
		this.executionId = id;
	}

	@Override
	public String getExecutionId() {
		return executionId;
	}

	@Override
	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	@Override
	public Activity getActivity() {
		return this.activity;
	}

	@Override
	public Account getAccount() {
		return account;
	}

	@Override
	public ScheduledOperation getScheduledOperation() {
		return scheduledOperation;
	}

	@Override
	public void setScheduledOperation(ScheduledOperation scheduledOperation) {
		this.scheduledOperation = scheduledOperation;
	}

}
