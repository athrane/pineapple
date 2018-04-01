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

import com.alpha.pineapple.model.execution.scheduled.ScheduledOperation;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.web.account.Account;
import com.alpha.pineapple.model.report.Report;

/**
 * Session state of a single user of the web application.
 */
public interface SessionState {

	/**
	 * Defines the tabs which can request focus in the workspace
	 */
	enum WorkspaceTabs {
		MODULE, EXECUTION, REPORT, CONFIGURATION, SCHEDULED_OPERATIONS, DEBUG, NONE,
	}

	/**
	 * Get module info. Can return null is no module info is set.
	 * 
	 * @return module info.
	 */
	public ModuleInfo getModuleInfo();

	/**
	 * Set module info.
	 * 
	 * @param info
	 *            Module info to set.
	 */
	public void setModuleInfo(ModuleInfo info);

	/**
	 * 
	 * @return
	 */
	public String getOperation();

	/**
	 * 
	 * @param operation
	 */
	public void setOperation(String operation);

	/**
	 * Get selected environment. Will return null if no environment is selected.
	 * 
	 * @return selected environment.
	 */
	public String getEnvironment();

	/**
	 * Set selected environment.
	 * 
	 * @param environment
	 *            selected environment.
	 */
	public void setEnvironment(String environment);

	/**
	 * Set selected report.
	 * 
	 * @param report
	 *            selected report.
	 */
	public void setReport(Report report);

	/**
	 * get selected report. Return null if no report is selected.
	 * 
	 * @return selected report.
	 */
	public Report getReport();

	/**
	 * Return true if debug information should be shown in the application.
	 * 
	 * @return true if debug information should be shown in the application.
	 */
	public boolean isDebugInfoEnabled();

	/**
	 * Set whether debug information should be shown in the application.
	 * 
	 * @return value which is set to true if debug information should be shown in
	 *         the application.
	 */
	public void setDebugInfoEnabled(boolean value);

	/**
	 * Set workspace tab which requests focus.
	 * 
	 * @param tab
	 *            workspace tab which requests focus.
	 */
	public void setRequestTabFocus(WorkspaceTabs tab);

	/**
	 * Get workspace tab which requests focus.
	 * 
	 * @return workspace tab which requests focus.
	 */
	public WorkspaceTabs getRequestTabFocus();

	/**
	 * Store last executed operation.
	 * 
	 * The current operation is stored as the last executed operation.
	 */
	public void setLastExecutedOperation();

	/**
	 * Return name of last executed operation.
	 * 
	 * @return name of last executed operation. If no operation has been executed
	 *         previously then an empty is returned.
	 */
	public String getLastExecutedOperation();

	/**
	 * Set execution ID.
	 * 
	 * @param id
	 *            execution ID.
	 */
	public void setExecutionId(String id);

	/**
	 * Get execution ID.
	 * 
	 * @return execution ID.
	 */
	public String getExecutionId();

	/**
	 * Set selected activity.
	 * 
	 * @param activity
	 *            selected activity.
	 */
	public void setActivity(Activity activity);

	/**
	 * Get selected activity.
	 * 
	 * @return return selected activity. If no activity is selected then null is
	 *         returned.
	 */
	public Activity getActivity();

	/**
	 * Get user account.
	 * 
	 * @return user account.
	 */
	public Account getAccount();

	/**
	 * Get selected scheduled operation.
	 * 
	 * @return selected scheduled operation.
	 */
	public ScheduledOperation getScheduledOperation();

	/**
	 * Set selected scheduled operation.
	 * 
	 * @param scheduledOperation
	 *            selected scheduled operation.
	 */
	public void setScheduledOperation(ScheduledOperation scheduledOperation);

}
