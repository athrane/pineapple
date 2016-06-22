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

import java.io.File;

import javax.annotation.Resource;

import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.web.model.SystemInfoModel;
import com.alpha.pineapple.web.spring.rest.SystemController;

/**
 * ZK view model for the system info page on the configuration panel.
 */
public class SystemInfoPanel {

    /**
     * System controller.
     */
    @WireVariable
    SystemController systemController;

    /**
     * System info model.
     */
    @WireVariable
    SystemInfoModel systemInfoModel;

    /**
     * Get Pineapple initialization status.
     * 
     * @return Pineapple initialization status.
     */
    public String getStatus() {
	try {
	    return systemController.getSimpleInitializationStatus();
	} catch (Exception e) {
	    return e.getMessage();
	}
    }

    /**
     * Get Pineapple version.
     * 
     * @return Pineapple version.
     */
    public String getVersion() {
	return systemController.getVersion();
    }

    /**
     * Get Pineapple home directory.
     * 
     * @return Pineapple home directory.
     */
    public String getHomeDirectory() {
	return systemInfoModel.getHomeDirectory();
    }

    /**
     * Get Pineapple modules directory.
     * 
     * @return Pineapple modules directory.
     */
    public String getModulesDirectory() {
	return systemInfoModel.getModulesDirectory();
    }

    /**
     * Get Pineapple reports directory.
     * 
     * @return Pineapple reports directory.
     */
    public String getReportsDirectory() {
	return systemInfoModel.getReportsDirectory();
    }

    /**
     * Get Pineapple temp. directory.
     * 
     * @return Pineapple temp. directory.
     */
    public String getTempDirectory() {
	return systemInfoModel.getTempDirectory();
    }

}
