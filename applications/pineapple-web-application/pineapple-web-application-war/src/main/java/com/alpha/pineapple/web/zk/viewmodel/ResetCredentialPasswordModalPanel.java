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

import static com.alpha.pineapple.web.WebApplicationConstants.CREDENTIAL_PASSWORD_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_SCOPE;
import static com.alpha.pineapple.web.WebApplicationConstants.RESET_CREDENTIAL_PASSWORD_CONFIRMED_GLOBALCOMMAND;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.alpha.pineapple.i18n.MessageProvider;

/**
 * ZK view model for the reset credential password panel (modal).
 */
public class ResetCredentialPasswordModalPanel {

	/**
	 * Message provider for I18N support.
	 */
	@WireVariable
	MessageProvider webMessageProvider;

	/**
	 * New password.
	 */
	String newPassword;

	/**
	 * Confirm password.
	 */
	String confirmedPassword;

	/**
	 * Password status.
	 */
	boolean passwordStatus;

	/**
	 * Password status as string.
	 */
	String passwordStatusAsString;

	/**
	 * Initialize view model.
	 */
	@Init
	public void init() {
		newPassword = "";
		confirmedPassword = "";
		updateStatus();
	}

	/**
	 * Event handler for the 'updateStatus' command.
	 * 
	 * Calculates password status.
	 */
	public void updateStatus() {

		// handle different passwords
		if (!newPassword.equals(confirmedPassword)) {
			passwordStatusAsString = webMessageProvider.getMessage("rcpmp.passwords_doesnt_match_info");
			passwordStatus = true;
			return;
		}

		// handle empty passwords
		if (newPassword.isEmpty()) {
			passwordStatusAsString = webMessageProvider.getMessage("rcpmp.passwords_empty_info");
			passwordStatus = true;
			return;
		}

		passwordStatusAsString = webMessageProvider.getMessage("rcpmp.passwords_match_info");
		passwordStatus = false;
	}

	/**
	 * Event handler for onChanging and onFocus events from the newPassword text
	 * box. Will update the password status.
	 * 
	 * @param event
	 *            event from text box.
	 */
	@Command
	@NotifyChange({ "passwordStatus", "passwordStatusAsString" })
	public void updateNewPassword(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {

		// get password if from update event
		if (event instanceof InputEvent) {

			// type cast
			InputEvent inputEvent = (InputEvent) event;

			// set password
			newPassword = inputEvent.getValue();
		}

		// recalculate status
		updateStatus();
	}

	/**
	 * Event handler for onChaning and onFocus events from the confirmedPassword
	 * text box. Will update the password status.
	 * 
	 * @param event
	 *            event from text box.
	 */
	@Command
	@NotifyChange({ "passwordStatus", "passwordStatusAsString" })
	public void updateConfirmedPassword(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {

		// get password if from update event
		if (event instanceof InputEvent) {

			// type cast
			InputEvent inputEvent = (InputEvent) event;

			// set password
			confirmedPassword = inputEvent.getValue();
		}

		// recalculate status
		updateStatus();
	}

	/**
	 * Get new password.
	 * 
	 * @return new password.
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * Set new password.
	 * 
	 * @param password
	 *            new password.
	 */
	public void setNewPassword(String password) {
		newPassword = password;
	}

	/**
	 * Get confirmed password.
	 * 
	 * @return confirmed password.
	 */
	public String getConfirmedPassword() {
		return confirmedPassword;
	}

	/**
	 * Set confirmed password.
	 * 
	 * @param password
	 *            confirmed password.
	 */
	public void setConfirmedPassword(String password) {
		confirmedPassword = password;
	}

	/**
	 * Get password status as text.
	 * 
	 * @return confirmed password.
	 */
	public String getPasswordStatusAsString() {
		return passwordStatusAsString;
	}

	/**
	 * Get password status as boolean.
	 * 
	 * @return false if the password are matching.
	 */
	public boolean getPasswordStatus() {
		return passwordStatus;
	}

	/**
	 * Event handler for the command "confirmResetPassword".
	 * 
	 * The event is triggered from the "confirm" button menu which posts the
	 * command.
	 */
	@Command
	public void confirmResetPassword() {

		// create command arguments with event
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(CREDENTIAL_PASSWORD_ARG, newPassword);

		// post global command to trigger selection of module tab on workspace
		// panel
		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE,
				RESET_CREDENTIAL_PASSWORD_CONFIRMED_GLOBALCOMMAND, args);

		// clear passwords
		newPassword = "";
		confirmedPassword = "";
	}

	/**
	 * Event handler for the command "cancelResetPassword".
	 * 
	 * The event is triggered from the "cancel" button menu which posts the command.
	 */
	@Command
	public void cancelResetPassword() {
		// clear passwords
		newPassword = "";
		confirmedPassword = "";
	}

}
