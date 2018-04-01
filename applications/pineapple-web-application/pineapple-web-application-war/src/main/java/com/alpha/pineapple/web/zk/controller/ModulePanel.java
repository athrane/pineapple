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

import static com.alpha.pineapple.web.WebApplicationConstants.POST_DURATION;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_LOCATION;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_STYLE;

import org.apache.log4j.Logger;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleRepository;
import com.alpha.pineapple.web.model.SessionState;

/**
 * ZK controller for the module panel.
 */
public class ModulePanel extends SelectorComposer<Window> {

	/**
	 * Serial Version UID.
	 */
	static final long serialVersionUID = 6470043898269443180L;

	/**
	 * JSON document field.
	 */
	static final String JSON_DOCUMENT_FIELD = "editor-document";

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
	 * Session state.
	 */
	@WireVariable
	ModuleRepository moduleRepository;

	/**
	 * ZK Window.
	 */
	@Wire
	Window modulePanelWindow;

	/**
	 * Event handler for the requestedDocumentToSave event from the client side
	 * (e.g. the java script module-panle.js).
	 * 
	 * @param evt
	 *            Event object.
	 */
	@Listen("onRequestedDocumentToSave = #modulePanelWindow")
	public void saveDocument(Event evt) {

		// get module info
		ModuleInfo info = sessionState.getModuleInfo();

		// get selected model
		String environment = sessionState.getEnvironment();

		// get updated model
		Object data = evt.getData();

		// exit save if model isn't an JSON object.
		if (!(data instanceof JSONObject)) {

			Object[] args = { environment, info.getId(), data.getClass() };
			String msg = webMessageProvider.getMessage("mp.save_model_failed", args);
			logger.error(msg);

			// message box to user
			String header = webMessageProvider.getMessage("mp.save_model_failed_header_msgbox", args);
			msg = webMessageProvider.getMessage("mp.save_model_failed_msgbox", args);
			Messagebox.show(msg, header, Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		// type cast to JSON object
		JSONObject jsonObject = (JSONObject) data;

		// get model
		String model = (String) jsonObject.get(JSON_DOCUMENT_FIELD);

		// save model
		moduleRepository.saveModel(info, environment, model);

		// post notification that model was saved successfully
		Clients.showNotification(webMessageProvider.getMessage("mp.save_model_info"), POST_STYLE, null, POST_LOCATION,
				POST_DURATION);
	}

}
