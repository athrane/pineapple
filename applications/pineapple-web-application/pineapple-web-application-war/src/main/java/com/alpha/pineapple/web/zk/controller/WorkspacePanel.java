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

import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;
import static com.alpha.pineapple.web.WebApplicationConstants.SELECT_WORKSPACE_TAB_GLOBALCOMMAND;

import org.apache.log4j.Logger;
import org.zkoss.bind.GlobalCommandEvent;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkmax.ui.select.annotation.Subscribe;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.web.event.EventDispatcher;
import com.alpha.pineapple.web.model.SessionState;

/**
 * ZK controller for the workspace panel.
 */
public class WorkspacePanel extends SelectorComposer<Window> {

	/**
	 * Serial Version UID.
	 */
	static final long serialVersionUID = 6470043898269443180L;

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
	 * Event dispatcher.
	 */
	@WireVariable
	EventDispatcher eventDispatcher;

	/**
	 * Session state.
	 */
	@WireVariable
	SessionState sessionState;

	/**
	 * ZK Desktop set and enabled by server push.
	 */
	Desktop desktop;

	/**
	 * ZK module tab.
	 */
	@Wire
	Tab moduleTab;

	/**
	 * ZK execution tab.
	 */
	@Wire
	Tab executionTab;

	/**
	 * ZK report tab.
	 */
	@Wire
	Tab reportTab;

	/**
	 * ZK configuration tab.
	 */
	@Wire
	Tab configurationTab;

	/**
	 * ZK debug tab.
	 */
	@Wire
	Tab debugTab;

	/**
	 * ZK scheduled operation tab.
	 */
	@Wire
	Tab schedulingTab;

	/**
	 * ZK doAfterCompose method.
	 * 
	 * @param comp
	 *            ZK Window.
	 */
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		// enable server push
		desktop = Executions.getCurrent().getDesktop();

		// register desktop with event dispatcher
		eventDispatcher.register(desktop);
	}

	/**
	 * Set requested tab in focus.
	 */
	void setTabInFocus() {
		// set tab in focus
		switch (sessionState.getRequestTabFocus()) {
		case NONE:
			return;
		case MODULE:
			moduleTab.setSelected(true);
			return;
		case DEBUG:
			debugTab.setSelected(true);
			return;
		case EXECUTION:
			executionTab.setSelected(true);
			return;
		case REPORT:
			reportTab.setSelected(true);
			return;
		case CONFIGURATION:
			configurationTab.setSelected(true);
			return;
		case SCHEDULED_OPERATIONS:
			schedulingTab.setSelected(true);
			return;
		default:
			return;
		}
	}

	/**
	 * Sets debug visibility depending on the session state.
	 */
	void toogleDebugTab() {
		debugTab.setVisible(sessionState.isDebugInfoEnabled());
	}

	/**
	 * Event handler global command "selectWorkspaceTab" subscribes to queue
	 * "pineapple-queue".
	 * 
	 * @param evt
	 *            global command event.
	 */
	@Subscribe(value = PINEAPPLE_ZK_QUEUE, scope = EventQueues.SESSION)
	public void selectWorkspaceTab(Event evt) {

		if (evt instanceof GlobalCommandEvent) {
			String command = ((GlobalCommandEvent) evt).getCommand();

			// execute of global command is "selectWorkspaceTab"
			if (SELECT_WORKSPACE_TAB_GLOBALCOMMAND.equals(command)) {
				toogleDebugTab();
				setTabInFocus();
			}
		}
	}

}
