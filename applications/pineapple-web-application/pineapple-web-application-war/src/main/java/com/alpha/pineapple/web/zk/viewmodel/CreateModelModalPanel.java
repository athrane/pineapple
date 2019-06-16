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

import static com.alpha.pineapple.web.WebApplicationConstants.CREATE_MODEL_CONFIRMED_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.MODEL_NAME_KEY_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_SCOPE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.module.ModuleInfo;

/**
 * ZK view model for the create model panel (modal).
 */
public class CreateModelModalPanel {

	/**
	 * Message provider for I18N support.
	 */
	@WireVariable
	MessageProvider webMessageProvider;

	/**
	 * Model environment.
	 */
	String modelName;

	/**
	 * Property status.
	 */
	boolean modelStatus;

	/**
	 * Password status as string.
	 */
	String modelStatusAsString;

	/**
	 * Existing models.
	 */
	Set<String> models;

	/**
	 * Initialize view model.
	 * 
	 * @param moduleInfo selected module info.
	 */
	@Init
	public void init(@ExecutionArgParam("selectedModule") ModuleInfo moduleInfo) {
		modelName = "";

		// create set of existing environments
		models = new HashSet<String>();
		initializeExistingModels(moduleInfo);

		updateStatus();
	}

	/**
	 * Initialize set of existing models.
	 * 
	 * @param info module info.
	 */
	void initializeExistingModels(ModuleInfo info) {
		if (info == null)
			return;
		String[] environments = info.getModelEnvironments();
		if (environments == null)
			return;
		for (String environment : environments) {
			models.add(environment);
		}
	}

	/**
	 * Event handler for the 'updateStatus' command.
	 * 
	 * Calculates model status.
	 */
	public void updateStatus() {

		// handle empty key
		if (modelName.isEmpty()) {
			modelStatusAsString = webMessageProvider.getMessage("cmmp.name_empty_info");
			modelStatus = true;
			return;
		}

		// handle identical keys
		if (models.contains(modelName)) {
			modelStatusAsString = webMessageProvider.getMessage("cmmp.name_already_exist_info");
			modelStatus = true;
			return;
		}

		modelStatusAsString = webMessageProvider.getMessage("cmmp.valid_name_info");
		modelStatus = false;
	}

	/**
	 * Event handler for onChanging and onFocus events from the modelName text box.
	 * Will update the model status.
	 * 
	 * @param name model name text box.
	 */
	@Command
	@NotifyChange({ "modelStatus", "modelStatusAsString" })
	public void updateModelName(@BindingParam("name") String name) {
		this.modelName = name;
		updateStatus();
	}

	/**
	 * Get model name.
	 * 
	 * @return model name.
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * Set model Name.
	 * 
	 * @param name model name.
	 */
	public void setModelName(String name) {
		this.modelName = name;
	}

	/**
	 * Get model status as text.
	 * 
	 * @return model status as text.
	 */
	public String getModelStatusAsString() {
		return modelStatusAsString;
	}

	/**
	 * Get model status as boolean.
	 * 
	 * @return false if the model can be created.
	 */
	public boolean getModelStatus() {
		return modelStatus;
	}

	/**
	 * Event handler for the command "confirmModel".
	 * 
	 * The event is triggered from the "confirm" button menu which posts the
	 * command.
	 */
	@Command
	public void confirmModel() {

		// create command arguments with event
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(MODEL_NAME_KEY_ARG, modelName);

		// post global command to trigger create model global command
		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, CREATE_MODEL_CONFIRMED_GLOBALCOMMAND, args);
	}

	/**
	 * Event handler for the command "cancelModel".
	 * 
	 * The event is triggered from the "cancel" button menu which does nothing.
	 */
	@Command
	public void cancelModel() {
		// no operation
	}

}
