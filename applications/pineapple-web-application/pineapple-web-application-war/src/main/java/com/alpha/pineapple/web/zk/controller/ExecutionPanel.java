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

import static com.alpha.pineapple.web.WebApplicationConstants.COLLAPSEEXAPAND_EXECUTIONRESULT_TREE_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;

import java.util.Collection;

import org.zkoss.bind.GlobalCommandEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.ui.select.annotation.Subscribe;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

/**
 * ZK controller for the execution panel.
 */
public class ExecutionPanel extends SelectorComposer<Window> {

	/**
	 * Serial Version UID.
	 */
	static final long serialVersionUID = 6470043898269443180L;

	/**
	 * Tree.
	 */
	@Wire
	Tree tree;

	/**
	 * Tree state, whether is it expanded or collapsed.
	 */
	boolean isTreeCollapsed = false;

	/**
	 * ZK doAfterCompose method.
	 * 
	 * @param comp
	 *            ZK Window.
	 */
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		isTreeCollapsed = true;
	}

	/**
	 * Event handler for global command "startOperation" and
	 * "collapseExpandExecutionResultTree" subscribes to queue "pineapple-queue".
	 * 
	 * @param evt
	 *            global command event.
	 */
	@Subscribe(value = PINEAPPLE_ZK_QUEUE, scope = EventQueues.SESSION)
	public void processEvent(Event evt) {

		if (evt instanceof GlobalCommandEvent) {
			String command = ((GlobalCommandEvent) evt).getCommand();

			// execute if global command is "collapseExpandExecutionResultTree"
			if (COLLAPSEEXAPAND_EXECUTIONRESULT_TREE_GLOBALCOMMAND.equals(command)) {
				collapseExpandExecutionResultTree();
			}

		}
	}

	/**
	 * Return true if tree is collapsed.
	 * 
	 * @return true if tree are collapsed.
	 */
	boolean isTreeCollapsed() {
		return isTreeCollapsed;
	}

	/**
	 * Collapse or expand execution result tree.
	 */
	void collapseExpandExecutionResultTree() {
		if (tree == null)
			return;

		// clear selection
		tree.clearSelection();

		// get tree item
		Collection<Treeitem> treeItems = tree.getItems();
		Treeitem[] itemsArray = treeItems.toArray(new Treeitem[treeItems.size()]);

		// set state
		boolean setOpen = isTreeCollapsed();
		isTreeCollapsed = !isTreeCollapsed();

		for (Treeitem item : itemsArray) {
			item.setOpen(setOpen);
		}
	}

}