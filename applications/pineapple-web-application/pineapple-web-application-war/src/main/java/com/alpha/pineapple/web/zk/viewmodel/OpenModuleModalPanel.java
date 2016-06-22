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

import static com.alpha.pineapple.web.WebApplicationConstants.LOAD_MODULE_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.LOAD_MODULE_MODULE_INFO_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_SCOPE;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_DURATION;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_LOCATION;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_STYLE;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.databind.BindingListModelList;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleRepository;
import com.alpha.pineapple.web.model.SessionState;
import com.alpha.pineapple.web.spring.rest.ModuleController;

/**
 * ZK view model for the open module panel (modal).
 */
public class OpenModuleModalPanel {

    /**
     * Readme.txt file name.
     */
    static final String READMETXT_FILENAME = "readme.txt";

    /**
     * Fist environment index.
     */
    static final int FIRST_INDEX = 0;

    /**
     * New line.
     */
    public static String NEW_LINE = System.getProperty("line.separator");

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Session state.
     */
    @WireVariable
    SessionState sessionState;

    /**
     * Module repository.
     */
    @WireVariable
    ModuleRepository moduleRepository;

    /**
     * Spring REST module controller.
     */
    @WireVariable
    ModuleController moduleController;

    /**
     * Message provider for I18N support.
     */
    @WireVariable
    MessageProvider webMessageProvider;

    /**
     * Module description.
     */
    String moduleDescription;

    /**
     * Selected module in the ZK view.
     */
    ModuleInfo selectedModuleInfo;

    /**
     * Set selected module in the ZK view.
     * 
     * @param info
     *            selected module in the ZK view.
     */
    public void setSelectedModule(ModuleInfo info) {
	selectedModuleInfo = info;
    }

    /**
     * Get selected module in ZK view.
     * 
     * @return selected module.
     */
    public ModuleInfo getSelectedModule() {
	return selectedModuleInfo;
    }

    /**
     * Get all module info's from module repository.
     * 
     * @return all module info's from module repository.
     */
    public List<ModuleInfo> getModules() {
	List<ModuleInfo> list = Arrays.asList(moduleRepository.getInfos());
	return new BindingListModelList<ModuleInfo>(list, true);
    }

    /**
     * Get module description.
     * 
     * @return module description.
     */
    public String getDescription() {
	if (moduleDescription == null)
	    return webMessageProvider.getMessage("omm.description_not_found_info");
	if (moduleDescription.isEmpty())
	    return webMessageProvider.getMessage("omm.description_empty_info");
	return moduleDescription;
    }

    /**
     * Event handler for selection of module in list box.
     * 
     * Will load the readme.txt description file into text box.
     */
    @Command
    public void loadDescription() {

	// clear description
	moduleDescription = null;

	// get module info
	ModuleInfo info = selectedModuleInfo;
	if (info == null)
	    return;

	// get module directory
	File moduleDirectory = info.getDirectory();
	if (moduleDirectory == null)
	    return;
	if (!moduleDirectory.exists())
	    return;
	if (!moduleDirectory.isDirectory())
	    return;

	try {
	    // get description file
	    File descriptionFile = new File(moduleDirectory, READMETXT_FILENAME);
	    if (!descriptionFile.exists())
		return;
	    if (!descriptionFile.isFile())
		return;

	    // load file adding CRLF's
	    StringBuilder description = new StringBuilder();
	    for (String line : FileUtils.readLines(descriptionFile)) {
		description.append(line);
		description.append(NEW_LINE);
	    }

	    // store
	    moduleDescription = description.toString();

	} catch (Exception e) {
	    Object[] args = { StackTraceHelper.getStrackTrace(e) };
	    String msg = webMessageProvider.getMessage("omm.load_description_failed", args);
	    logger.error(msg);
	    return;
	}
    }

    /**
     * Event handler for click on the open module button.
     * 
     * Will add post global command to load module in the module panel and close
     * modal window.
     */
    @Command
    public void openModule() {

	// post global command to trigger module update in module panel and
	// workspace panel view models
	Map<String, Object> args = new HashMap<String, Object>();
	args.put(LOAD_MODULE_MODULE_INFO_ARG, selectedModuleInfo);
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, LOAD_MODULE_GLOBALCOMMAND, args);
    }

    /**
     * Event handler for click on the cancel button.
     * 
     * Will close modal window.
     */
    @Command
    public void cancel() {
	// do nothing
    }

    /**
     * Event handler for click on the refresh modules button.
     * 
     * Will refresh modules repository and update list.
     */
    @Command
    @NotifyChange({ "modules", "description", "selectedModule" })
    public void refreshModules() {

	selectedModuleInfo = null;

	// refresh
	moduleController.refresh();

	// post notification
	Clients.showNotification(webMessageProvider.getMessage("omm.refresh_modules_info"), POST_STYLE, null,
		POST_LOCATION, POST_DURATION);

    }

}
