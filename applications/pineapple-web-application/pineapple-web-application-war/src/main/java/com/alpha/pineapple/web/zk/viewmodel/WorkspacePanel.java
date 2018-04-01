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

import org.apache.log4j.Logger;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.web.account.Account;
import com.alpha.pineapple.web.model.SessionState;

/**
 * ZK view model for the workspace panel.
 */
public class WorkspacePanel {

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
	 * Get module name in ZK view. Will return null is no module is set.
	 * 
	 * @return module information.
	 */
	public String getModuleName() {
		ModuleInfo info = sessionState.getModuleInfo();
		if (info == null)
			return "No module opened.";
		;
		return info.getId();
	}

	/**
	 * Get model name in ZK view. Will return null is no model is set.
	 * 
	 * @return module information.
	 */
	public String getModelName() {
		String model = sessionState.getEnvironment();
		if ((model == null) || model.isEmpty())
			return "No model selected.";
		return model;
	}

	/**
	 * Get user name in ZK view. Will return "n/a" is no user is set.
	 * 
	 * @return module information.
	 */
	public String getUserName() {
		Account account = sessionState.getAccount();
		if (account == null)
			return "n/a";
		String userProperty = account.getUsername();
		if (userProperty == null)
			return "n/a";
		if (userProperty.isEmpty())
			return "n/a";
		return userProperty;
	}

	/**
	 * Get host name in ZK view. Will return "n/a" is no host is set.
	 * 
	 * @return module information.
	 */
	public String getHostName() {
		Session zkSession = Sessions.getCurrent();
		String localAddress = zkSession.getLocalAddr();
		if (localAddress == null)
			return "n/a";
		if (localAddress.isEmpty())
			return "n/a";
		return localAddress;
	}

	/**
	 * Event handler for the global command "loadModule". The event is triggered
	 * from the open module modal view model which posts the global command.
	 * 
	 * The event handler will notify the MVVM binder that all properties needs to be
	 * refreshed in the view.
	 */
	@GlobalCommand
	@NotifyChange("*")
	public void loadModule() {
		// do nothing
	}

	/**
	 * Event handler for the global command "closeModule". The event is triggered
	 * from the menu controller which posts the global command.
	 * 
	 * The event handler will notify the MVVM binder that all properties needs to be
	 * refreshed in the view.
	 */
	@GlobalCommand
	@NotifyChange("*")
	public void closeModule() {
		// do nothing
	}

	/**
	 * Event handler for the global command "loadModel". The event is triggered from
	 * the module panel view (.zul) which posts the global command on selection of a
	 * model.
	 * 
	 * The event handler will notify the MVVM binder that the model name property
	 * needs to be refreshed in the view.
	 */
	@GlobalCommand
	@NotifyChange("modelName")
	public void loadModel() {
		// do nothing
	}

	/**
	 * Event handler for the global command "deleteModelConfirmed". The event is
	 * triggered from the module panel modal view (.zul) which posts the global
	 * command on confirmed deletion of a a model.
	 * 
	 * The event handler will notify the MVVM binder that the model name property
	 * needs to be refreshed in the view.
	 */
	@GlobalCommand
	@NotifyChange("modelName")
	public void deleteModelConfirmed() {
		// do nothing
	}
}
