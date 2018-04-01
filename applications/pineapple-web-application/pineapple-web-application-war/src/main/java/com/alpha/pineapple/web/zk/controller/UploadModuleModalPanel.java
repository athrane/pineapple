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

import static com.alpha.pineapple.web.WebApplicationConstants.FILE_UNPACK_EVENT_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.FILE_UNPACK_UPDATE_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.MEDIA_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_SCOPE;
import static com.alpha.pineapple.web.WebApplicationConstants.UNPACKED_ENTRY_EVENT_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.UNPACKED_MODULE_ENTRY_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.UPLOAD_MODULE_GLOBALCOMMAND;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.GlobalCommandEvent;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkmax.ui.select.annotation.Subscribe;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.web.model.SessionState;
import com.alpha.pineapple.web.zk.asynctask.AsyncTaskHelper;
import com.alpha.pineapple.web.zk.asynctask.UnpackModuleTask;
import com.alpha.pineapple.web.zk.asynctask.event.FileUnpackUpdateEvent;
import com.alpha.pineapple.web.zk.asynctask.event.UnpackModuleTaskEvent;
import com.alpha.pineapple.web.zk.asynctask.event.UnpackedEntryEvent;
import com.alpha.pineapple.web.zk.asynctask.event.visitor.UnpackModuleTaskEventVisitor;

/**
 * ZK controller for the upload module panel (modal).
 */
public class UploadModuleModalPanel extends SelectorComposer<Window>
		implements UnpackModuleTaskEventVisitor, EventListener<Event> {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1L;

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
	 * Asynchronous unpack module task.
	 */
	@WireVariable
	UnpackModuleTask unpackModuleTask;

	/**
	 * Asynchronous task execution helper.
	 */
	@WireVariable
	AsyncTaskHelper asyncTaskHelper;

	/**
	 * Runtime directory resolver.
	 */
	@WireVariable
	RuntimeDirectoryProvider runtimeDirectoryResolver;

	/**
	 * ZK Desktop set and enabled by server push.
	 */
	Desktop desktop;

	/**
	 * ZK button.
	 */
	@Wire
	Button closeButton;

	/**
	 * ZK button.
	 */
	@Wire
	Button uploadButton;

	/**
	 * ZK doAfterCompose method.
	 * 
	 * @param comp
	 *            ZK Window.
	 */
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		// enable server push
		desktop = asyncTaskHelper.enableServerPush();
	}

	/**
	 * Event handler for global command "uploadModule" subscribes to queue
	 * "pineapple-queue".
	 * 
	 * @param evt
	 *            global command event.
	 */
	@Subscribe(value = PINEAPPLE_ZK_QUEUE, scope = EventQueues.SESSION)
	public void executeOperation(Event evt) {

		if (evt instanceof GlobalCommandEvent) {
			String command = ((GlobalCommandEvent) evt).getCommand();

			// execute of global command is "uploadModule"
			if (UPLOAD_MODULE_GLOBALCOMMAND.equals(command)) {

				// get command media argument
				Map<String, Object> args = ((GlobalCommandEvent) evt).getArgs();
				Media uploadedMedia = (Media) args.get(MEDIA_ARG);

				// disable buttons
				uploadButton.setDisabled(true);
				closeButton.setDisabled(true);

				// invoke pineapple operation asynchronously
				unpackModuleTask.setMedia(uploadedMedia);
				asyncTaskHelper.executeAsync(unpackModuleTask, this.desktop, (EventListener<Event>) this);
			}
		}
	}

	/**
	 * Generic event handler for asynchronous task which dispatch event to specific
	 * event handler.
	 */
	@Override
	public void onEvent(Event evt) throws Exception {
		// TODO: throw error if type cast fails

		// dispatch event
		((UnpackModuleTaskEvent) evt).accept(this);
	}

	@Override
	public void visit(FileUnpackUpdateEvent evt) {

		// create command arguments with event
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(FILE_UNPACK_EVENT_ARG, evt);

		// post global command to trigger update at view model
		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, FILE_UNPACK_UPDATE_GLOBALCOMMAND, args);
	}

	/**
	 * Event handler for the unpacked entry event which signals that a file entry
	 * have been unpacked from the zip archive.
	 */
	@Override
	public void visit(UnpackedEntryEvent evt) {

		// enabled close button if done
		if (evt.getPercentage() == 100) {
			uploadButton.setDisabled(false);
			closeButton.setDisabled(false);
		}

		// create command arguments with event
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(UNPACKED_ENTRY_EVENT_ARG, evt);

		// post global command to trigger update at view model
		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, UNPACKED_MODULE_ENTRY_GLOBALCOMMAND, args);
	}

}
