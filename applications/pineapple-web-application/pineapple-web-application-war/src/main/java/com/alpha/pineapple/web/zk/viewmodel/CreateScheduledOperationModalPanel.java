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

import static com.alpha.pineapple.web.WebApplicationConstants.CREATE_SCHEDULED_OPERATION_CONFIRMED_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.jasypt.salt.RandomSaltGenerator;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperation;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperations;
import com.alpha.pineapple.model.module.info.Model;
import com.alpha.pineapple.model.module.info.Module;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.web.spring.rest.ModuleController;
import com.alpha.pineapple.web.spring.rest.ScheduledOperationController;
import com.alpha.pineapple.web.zk.utils.ErrorMessageBoxHelper;

/**
 * ZK view model for the create scheduled operation panel (modal).
 */
public class CreateScheduledOperationModalPanel {

	/**
	 * Message provider for I18N support.
	 */
	@WireVariable
	MessageProvider webMessageProvider;

	/**
	 * Spring REST scheduled operation controller.
	 */
	@WireVariable
	ScheduledOperationController scheduledOperationController;

	/**
	 * Spring REST module controller.
	 */
	@WireVariable
	ModuleController moduleController;

	/**
	 * Error message box helper.
	 */
	@WireVariable
	ErrorMessageBoxHelper errorMessageBoxHelper;

	/**
	 * Name.
	 */
	String name;

	/**
	 * Selected module.
	 */
	Module module;

	/**
	 * Selected operation.
	 */
	String operation;

	/**
	 * Selected model.
	 */
	Model model;

	/**
	 * Description.
	 */
	String description;

	/**
	 * Scheduling expression.
	 */
	String cron;

	/**
	 * Property status.
	 */
	boolean creationStatus;

	/**
	 * Creation status as string.
	 */
	String creationStatusAsString;

	/**
	 * List of default operations
	 */
	ArrayList<String> defaultOperations;

	/**
	 * Set of name for the current scheduled operations.
	 */
	Set<String> names;

	/**
	 * Initialize view model and create default values.
	 */
	@Init
	public void init() {

		// get list of names of current scheduled operations
		ScheduledOperations operations = scheduledOperationController.getScheduledOperations();
		names = operations.getScheduledOperation().stream().map(ScheduledOperation::getName)
				.collect(Collectors.toSet());

		// set default values
		defaultOperations = new ArrayList<String>();
		defaultOperations.add(OperationNames.DEPLOY_CONFIGURATION);
		defaultOperations.add(OperationNames.UNDEPLOY_CONFIGURATION);
		defaultOperations.add(OperationNames.TEST);
		defaultOperations.add(OperationNames.CREATE_REPORT);
		name = RandomStringUtils.randomAlphabetic(4).toLowerCase();
		description = webMessageProvider.getMessage("csomp.default_description");
		cron = webMessageProvider.getMessage("csomp.default_cron");
		updateStatus();
	}

	/**
	 * Event handler for the 'updateStatus' command.
	 * 
	 * Calculates creation status.
	 */
	public void updateStatus() {

		// handle empty name
		if ((name == null) || (name.isEmpty())) {
			creationStatusAsString = webMessageProvider.getMessage("csomp.name_undefined_info");
			creationStatus = true;
			return;
		}

		// validate name is unique
		if (names.contains(name)) {
			creationStatusAsString = webMessageProvider.getMessage("csomp.name_notunique_info");
			creationStatus = true;
			return;
		}

		// handle empty module
		if (module == null) {
			creationStatusAsString = webMessageProvider.getMessage("csomp.module_undefined_info");
			creationStatus = true;
			return;
		}

		// handle undefined model
		if (model == null) {
			creationStatusAsString = webMessageProvider.getMessage("csomp.model_undefined_info");
			creationStatus = true;
			return;
		}

		// handle undefined model operation
		if ((operation == null) || (operation.isEmpty())) {
			creationStatusAsString = webMessageProvider.getMessage("csomp.operation_undefined_info");
			creationStatus = true;
			return;
		}

		// handle undefined cron
		if ((cron == null) || (cron.isEmpty())) {
			creationStatusAsString = webMessageProvider.getMessage("csomp.cron_undefined_info");
			creationStatus = true;
			return;
		}

		// handle undefined description
		if ((description == null) || (description.isEmpty())) {
			creationStatusAsString = webMessageProvider.getMessage("csomp.description_undefined_info");
			creationStatus = true;
			return;
		}

		creationStatusAsString = webMessageProvider.getMessage("csomp.scheduledoperation_valid_info");
		creationStatus = false;
	}

	/**
	 * Event handler for onChanging and onFocus events from the name text box. Will
	 * update the creation status.
	 * 
	 * @param event
	 *            event from text box.
	 */
	@Command
	@NotifyChange({ "creationStatus", "creationStatusAsString" })
	public void updateName(@BindingParam("name") String name) {
		this.name = name;
		updateStatus();
	}

	/**
	 * Event handler for onChanging and onFocus events from the description text
	 * box. Will update the creation status.
	 * 
	 * @param event
	 *            event from text box.
	 */
	@Command
	@NotifyChange({ "creationStatus", "creationStatusAsString" })
	public void updateDescription(@BindingParam("description") String description) {
		this.description = description;
		updateStatus();
	}

	/**
	 * Event handler for onChanging and events from the cron text box. Will update
	 * the creation status.
	 * 
	 * @param cron
	 *            cron scheduling expression.
	 */
	@Command
	@NotifyChange({ "creationStatus", "creationStatusAsString" })
	public void updateCron(@BindingParam("cron") String cron) {
		this.cron = cron;
		updateStatus();
	}

	/**
	 * Get modules.
	 * 
	 * @return list of modules.
	 */
	public List<Module> getModules() {
		return moduleController.getModules().getModule();
	}

	/**
	 * Set module.
	 * 
	 * @param module
	 *            module.
	 */
	@NotifyChange({ "creationStatus", "creationStatusAsString", "models" })
	public void setModule(Module module) {
		this.module = module;
		updateStatus();
	}

	/**
	 * Get models.
	 * 
	 * @return list of models for selected module.
	 */
	public List<Model> getModels() {
		if (module == null)
			return Collections.<Model>emptyList();
		return module.getModel();
	}

	/**
	 * Set model.
	 * 
	 * @param model
	 *            model.
	 */
	@NotifyChange({ "creationStatus", "creationStatusAsString" })
	public void setModel(Model model) {
		this.model = model;
		updateStatus();
	}

	/**
	 * Get operations.
	 * 
	 * @return list of operations.
	 */
	public List<String> getOperations() {
		return defaultOperations;
	}

	/**
	 * Set operation name.
	 * 
	 * @param name
	 *            operation name.
	 */
	@NotifyChange({ "creationStatus", "creationStatusAsString", "models" })
	public void setOperation(String operation) {
		this.operation = operation;
		updateStatus();
	}

	/**
	 * Get name.
	 * 
	 * @return name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set name.
	 * 
	 * @param name
	 *            name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get description.
	 * 
	 * @return description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set description.
	 * 
	 * @param description
	 *            description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get scheduling expression.
	 * 
	 * @return scheduling expression.
	 */
	public String getCron() {
		return cron;
	}

	/**
	 * Set scheduling expression.
	 * 
	 * @param cron
	 *            scheduling expression.
	 */
	public void setCron(String cron) {
		this.cron = cron;
	}

	/**
	 * Get creation status as text.
	 * 
	 * @return creation status as text.
	 */
	public String getCreationStatusAsString() {
		return creationStatusAsString;
	}

	/**
	 * Get creation status as boolean.
	 * 
	 * @return false if the scheduled operation can be created.
	 */
	public boolean getCreationStatus() {
		return creationStatus;
	}

	/**
	 * Event handler for the command "confirmOperation".
	 * 
	 * The event is triggered from the "confirm" button menu. Creates the scheduled
	 * operation.
	 * 
	 * Posts global command "createScheduledOperationConfirmed" to update view. The
	 * global is posted to the APPLICATION queue to trigger updates in all GUI's
	 */
	@Command
	public void confirmOperation() {
		try {

			// create new operation
			scheduledOperationController.create(name, module.getId(), model.getId(), operation, cron, description);

			// post global command with APPLICATION scope which triggers update
			// of the scheduled operations panel in all GUI's
			BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, EventQueues.APPLICATION,
					CREATE_SCHEDULED_OPERATION_CONFIRMED_GLOBALCOMMAND, null);

		} catch (Exception e) {
			errorMessageBoxHelper.showAndLogException(e);
		}

	}

	/**
	 * Event handler for the command "cancelOperation".
	 * 
	 * The event is triggered from the "cancel" button menu which does nothing.
	 */
	@Command
	public void cancelOperation() {
		// NO-OP
	}

}
