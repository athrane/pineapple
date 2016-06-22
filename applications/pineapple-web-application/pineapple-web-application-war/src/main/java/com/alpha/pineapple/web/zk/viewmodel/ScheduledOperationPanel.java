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
import static com.alpha.pineapple.web.WebApplicationConstants.CREATE_SCHEDULED_OPERATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.CREATE_SCHEDULED_OPERATION_MODAL_ZUL;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_ALL_SCHEDULED_OPERATIONS_CONFIRMED_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_ALL_SCHEDULED_OPERATIONS_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_ALL_SCHEDULED_OPERATIONS_MODAL_ZUL;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_SCHEDULED_OPERATION_CONFIRMED_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_SCHEDULED_OPERATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_SCHEDULED_OPERATION_MODAL_ZUL;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_GLOBALCOMMAND_ARGS;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_PARENT_WINDOW;
import static org.zkoss.zk.ui.Executions.createComponents;

import java.util.Collection;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperation;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperations;
import com.alpha.pineapple.web.model.SessionState;
import com.alpha.pineapple.web.spring.rest.ScheduledOperationController;
import com.alpha.pineapple.web.zk.utils.ErrorMessageBoxHelper;

/**
 * ZK view model for the scheduled operations panel.
 */
public class ScheduledOperationPanel {

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
     * Spring REST scheduled operation controller.
     */
    @WireVariable
    ScheduledOperationController scheduledOperationController;

    /**
     * Error message box helper.
     */
    @WireVariable
    ErrorMessageBoxHelper errorMessageBoxHelper;

    /**
     * Initialize view model.
     */
    @Init
    public void init() {
    }

    /**
     * Get selected scheduled operation in ZK view.
     * 
     * @return selected scheduled operation.
     */
    public ScheduledOperation getSelectedScheduledOperation() {
	return sessionState.getScheduledOperation();
    }

    /**
     * Set selected scheduled operation in the ZK view.
     * 
     * @param report
     *            Selected operation in the ZK view.
     */
    public void setSelectedScheduledOperation(ScheduledOperation operation) {
	sessionState.setScheduledOperation(operation);
    }

    /**
     * Get all scheduled operations.
     * 
     * @return all scheduled operations.
     */
    public Collection<ScheduledOperation> getScheduledOperations() {
	ScheduledOperations operations = scheduledOperationController.getScheduledOperations();
	return operations.getScheduledOperation();
    }

    /**
     * Event handler for the global command "createScheduledOperation". The
     * event is triggered from the menu controller and the scheduled operation
     * panel which posts the global command.
     * 
     * Step 1) of creating a scheduled operation. Will open a modal window for
     * scheduled operation creation. The operation is created in the core
     * component using REST API.
     */
    @GlobalCommand(CREATE_SCHEDULED_OPERATION_GLOBALCOMMAND)
    public void createScheduledOperation() {
	Window modalWindow = null;

	try {
	    // open modal window
	    modalWindow = (Window) createComponents(CREATE_SCHEDULED_OPERATION_MODAL_ZUL, NULL_PARENT_WINDOW,
		    NULL_GLOBALCOMMAND_ARGS);
	    modalWindow.doModal();

	} catch (Exception e) {

	    // show and log error message
	    errorMessageBoxHelper.showAndLogException(e);

	    // detach window
	    if (modalWindow != null)
		modalWindow.detach();
	}
    }

    /**
     * Event handler for the global command "createScheduledOperationConfirmed".
     * The event is triggered from the modal window after creation of the
     * scheduled operation.
     * 
     * Step 2) of creating a scheduled operation. Will update the view model.
     */
    @GlobalCommand(CREATE_SCHEDULED_OPERATION_CONFIRMED_GLOBALCOMMAND)
    @NotifyChange("scheduledOperations")
    public void createOperationConfirmed() {
	// NO-OP, other than updating the view.
    }

    /**
     * Event handler for the global command "deleteScheduledOperation". The
     * event is triggered from the menu controller and the scheduled operation
     * panel which posts the global command.
     * 
     * Step 1) of deleting a scheduled operation. Will open a modal window for
     * confirmation. The operation is deleted in the core component using REST
     * API.
     */
    @GlobalCommand(DELETE_SCHEDULED_OPERATION_GLOBALCOMMAND)
    public void deleteScheduledOperation() {
	Window modalWindow = null;

	try {

	    // exit if model isn't selected
	    if (sessionState.getScheduledOperation() == null) {
		String message = webMessageProvider.getMessage("sop.delete_operation_not_selected_failed");
		Messagebox.show(message);
		return;
	    }

	    // open modal window
	    modalWindow = (Window) createComponents(DELETE_SCHEDULED_OPERATION_MODAL_ZUL, NULL_PARENT_WINDOW,
		    NULL_GLOBALCOMMAND_ARGS);
	    modalWindow.doModal();

	} catch (Exception e) {
	    errorMessageBoxHelper.showAndLogException(e);

	    // detach window
	    if (modalWindow != null)
		modalWindow.detach();
	}
    }

    /**
     * Event handler for the global command "deleteScheduledOperationConfirmed".
     * The event is triggered from the modal window after deletion of the
     * scheduled operation.
     * 
     * Step 2) of deleting a scheduled operation. Will update the view model.
     */
    @GlobalCommand(DELETE_SCHEDULED_OPERATION_CONFIRMED_GLOBALCOMMAND)
    @NotifyChange("scheduledOperations")
    public void deleteOperationConfirmed() {
	// NO-OP, other than updating the view.
    }

    /**
     * Event handler for the global command "deleteAllScheduledOperations". The
     * event is triggered from the menu controller and the scheduled operation
     * panel which posts the global command.
     * 
     * Step 1) of deletion all scheduled operations. Will open a modal window
     * for confirmation. All operations are deleted in the core component using
     * REST API.
     */
    @GlobalCommand(DELETE_ALL_SCHEDULED_OPERATIONS_GLOBALCOMMAND)
    public void deleteAllScheduledOperations() {
	Window modalWindow = null;

	try {

	    // open modal window
	    modalWindow = (Window) createComponents(DELETE_ALL_SCHEDULED_OPERATIONS_MODAL_ZUL, NULL_PARENT_WINDOW,
		    NULL_GLOBALCOMMAND_ARGS);
	    modalWindow.doModal();

	} catch (Exception e) {
	    errorMessageBoxHelper.showAndLogException(e);

	    // detach window
	    if (modalWindow != null)
		modalWindow.detach();
	}
    }

    /**
     * Event handler for the global command
     * "deleteAllScheduledOperationsConfirmed". The event is triggered from the
     * modal window after deletion of all scheduled operations.
     * 
     * Step 2) of deleting all scheduled operations. Will update the view model.
     */
    @GlobalCommand(DELETE_ALL_SCHEDULED_OPERATIONS_CONFIRMED_GLOBALCOMMAND)
    @NotifyChange("scheduledOperations")
    public void deleteAllOperationsConfirmed() {
	// NO-OP, other than updating the view.
    }

    /**
     * Command for rendering selected scheduled operation
     */
    @Command
    public void showDetailsForSelectedScheduledOperation() {
    }

}
