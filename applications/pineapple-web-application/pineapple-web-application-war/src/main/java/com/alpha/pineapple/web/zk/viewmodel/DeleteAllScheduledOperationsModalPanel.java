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

import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_ALL_SCHEDULED_OPERATIONS_CONFIRMED_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;

import org.apache.log4j.Logger;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.web.spring.rest.ScheduledOperationController;
import com.alpha.pineapple.web.zk.utils.ErrorMessageBoxHelper;

/**
 * ZK view model for the delete all scheduled operations panel (modal).
 */
public class DeleteAllScheduledOperationsModalPanel {

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
	 * Event handler for the command "delete".
	 * 
	 * The event is triggered from the "confirm" button menu. Deletes the scheduled
	 * operation.
	 * 
	 * Posts global command "deleteAllScheduledOperationsConfirmed" to update view.
	 * The global is posted to the APPLICATION queue to trigger updates in all GUI's
	 */
	@Command
	public void delete() {
		try {

			// delete all scheduled operations
			scheduledOperationController.deleteAll();

			// post global command with APPLICATION scope which triggers update
			// of the scheduled operations panel in all GUI's
			BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, EventQueues.APPLICATION,
					DELETE_ALL_SCHEDULED_OPERATIONS_CONFIRMED_GLOBALCOMMAND, null);

		} catch (Exception e) {
			errorMessageBoxHelper.showAndLogException(e);
		}

	}

	/**
	 * Event handler for the command "cancel".
	 * 
	 * The event is triggered from the "cancel" button menu which does nothing.
	 */
	@Command
	public void cancel() {
		// NO-OP
	}

}
