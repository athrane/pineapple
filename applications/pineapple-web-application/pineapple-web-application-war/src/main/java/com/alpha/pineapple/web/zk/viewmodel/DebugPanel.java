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

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.alpha.pineapple.web.model.DebugInfoModel;

/**
 * ZK view model for the debug panel.
 */
public class DebugPanel {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Debug info model.
	 */
	@WireVariable
	DebugInfoModel debugInfoModel;

	/**
	 * Get debug model as a map.
	 * 
	 * @return debug model.
	 */
	public Map<String, String> getDebugModel() {
		return debugInfoModel.getDebugInfoModel();
	}

}
