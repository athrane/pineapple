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

import static com.alpha.pineapple.web.WebApplicationConstants.FILE_UNPACK_EVENT_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.MEDIA_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_SCOPE;
import static com.alpha.pineapple.web.WebApplicationConstants.UNPACKED_ENTRY_EVENT_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.UPLOAD_MODULE_GLOBALCOMMAND;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Messagebox;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.web.model.UnpackedEntry;
import com.alpha.pineapple.web.zk.asynctask.event.FileUnpackUpdateEvent;
import com.alpha.pineapple.web.zk.asynctask.event.UnpackedEntryEvent;

/**
 * ZK view model for the upload model panel (modal).
 */
public class UploadModuleModalPanel {

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
     * Runtime directory resolver.
     */
    @WireVariable
    RuntimeDirectoryProvider runtimeDirectoryProvider;

    /**
     * List of unpacked entries.
     */
    List<UnpackedEntry> entries;

    /**
     * Binding list.
     */
    BindingListModelList<UnpackedEntry> entriesBindingList;

    /**
     * Module name.
     */
    String moduleName;

    /**
     * Destination directory.
     */
    String destinationDirectory;

    /**
     * Number of current ZIP entry.
     */
    int zipCurrentEntry;

    /**
     * Total number of ZIP entries.
     */
    int zipTotalEntries;

    /**
     * Number of KB unpacked in current ZIP entry.
     */
    long fileCurrentEntry;

    /**
     * Total number of KB in current ZIP entry.
     */
    long fileTotalEntries;

    /**
     * Progress of UNZIP process.
     */
    int zipProgress;

    /**
     * Progress of UNZIP of current ZIP entry.
     */
    int fileProgress;

    /**
     * Initialize view model.
     */
    @Init
    public void init() {
	moduleName = "n/a";
	destinationDirectory = "n/a";
	zipCurrentEntry = 0;
	zipTotalEntries = 0;
	fileCurrentEntry = 0;
	fileTotalEntries = 0;
	fileProgress = 0;
	zipProgress = 0;
	entries = new ArrayList<UnpackedEntry>();
	entriesBindingList = new BindingListModelList<UnpackedEntry>(entries, true);
    }

    /**
     * Get module name in ZK view.
     * 
     * @return module name.
     */
    public String getModuleName() {
	return moduleName;
    }

    /**
     * Get destination directory in ZK view.
     * 
     * @return destination directory.
     */
    public String getDestinationDirectory() {
	return destinationDirectory;
    }

    /**
     * Get number of current ZIP entry.
     * 
     * @return number of current ZIP entry.
     */
    public String getZipCurrentEntry() {
	return String.valueOf(zipCurrentEntry);
    }

    /**
     * Get total number ZIP entries.
     * 
     * @return total number ZIP entries.
     */
    public String getZipTotalEntries() {
	return String.valueOf(zipTotalEntries);
    }

    /**
     * Get ZIP progress in percentage.
     * 
     * @return ZIP progress in percentage.
     */
    public int getZipProgress() {
	return zipProgress;
    }

    /**
     * Get number of KB's unpacked of current ZIP entry.
     * 
     * @return number of KB's unpacked of current ZIP entry.
     */
    public String getFileCurrentEntry() {
	return String.valueOf(fileCurrentEntry);
    }

    /**
     * Get total number of KB in current ZIP entry.
     * 
     * @return total number of KB in current ZIP entry.
     */
    public String getFileTotalEntries() {
	return String.valueOf(fileTotalEntries);
    }

    /**
     * Get unpack progress in percentage of current ZIP entry.
     * 
     * @return unpack progress in percentage of current ZIP entry.
     */
    public int getFileProgress() {
	return fileProgress;
    }

    /**
     * Get list of unpacked entries in ZK view.
     * 
     * @return list of unpacked entries in ZK view.
     */
    public List<UnpackedEntry> getUnpackedEntries() {
	return this.entriesBindingList;
    }

    /**
     * Event handler for click on the upload module button.
     * 
     * Will validate state of uploaded zipped media, updated the GUI and post a
     * global to trigger the unzip process in the composer.
     */
    @Command("uploadModule")
    @NotifyChange("*")
    public void uploadModule(BindContext ctx) {

	// clear all values
	init();

	// get media
	UploadEvent event = (UploadEvent) ctx.getTriggerEvent();
	Media uploadedMedia = event.getMedia();

	// exit if file is null
	if (uploadedMedia == null) {
	    Messagebox.show(webMessageProvider.getMessage("ummc.file_not_selected"),
		    webMessageProvider.getMessage("ummc.msgbox_title"), Messagebox.OK, Messagebox.INFORMATION);
	    return;
	}

	// exit if file isn't ZIP
	if (!uploadedMedia.getName().endsWith(".zip")) {
	    Messagebox.show(webMessageProvider.getMessage("ummc.not_zip_postfix_info"),
		    webMessageProvider.getMessage("ummc.msgbox_title"), Messagebox.OK, Messagebox.INFORMATION);
	    return;
	}

	// exit if file isn't binary
	if (!uploadedMedia.isBinary()) {
	    Messagebox.show(webMessageProvider.getMessage("ummc.not_binary_info"),
		    webMessageProvider.getMessage("ummc.msgbox_title"), Messagebox.OK, Messagebox.INFORMATION);
	    return;
	}

	// set values
	moduleName = uploadedMedia.getName();
	destinationDirectory = runtimeDirectoryProvider.getModulesDirectory().getAbsolutePath();

	// create command arguments and add media for composer
	Map<String, Object> args = new HashMap<String, Object>();
	args.put(MEDIA_ARG, uploadedMedia);

	// post global command to trigger asynchronous unpack task at composer
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, UPLOAD_MODULE_GLOBALCOMMAND, args);
    }

    /**
     * Event handler for click on the close button.
     * 
     * Will do nothing, since the purpose it to trigger the converter defined in
     * the view (.zul) to close the modal dialog by detaching it.
     * 
     */
    @Command
    public void close() {
	// clear all values
	init();
    }

    /**
     * Event handler for the global command "unpackedModuleEntry". The event is
     * triggered from the upload module modal panel controller which posts the
     * global command.
     * 
     * The event handler will add the unpacked entry to the list of unpacked
     * entries and notify the MVVM binder that the view model is updated.
     */
    @GlobalCommand
    @NotifyChange("*")
    public void unpackedModuleEntry(@BindingParam(UNPACKED_ENTRY_EVENT_ARG) UnpackedEntryEvent evt) {

	// create unpacked entry and add it to model
	UnpackedEntry entry = new UnpackedEntry(evt.getEntryName(), evt.getEntryType(),
		String.valueOf(evt.getEntrySize()));

	zipCurrentEntry = evt.getCurrent();
	zipTotalEntries = evt.getTotal();
	zipProgress = evt.getPercentage();
	this.entries.add(entry);
    }

    /**
     * Event handler for the global command "fileUnpackUpdate". The event is
     * triggered from the upload module modal panel controller which posts the
     * global command.
     * 
     * The event handler will notify the MVVM binder that the view model is
     * updated.
     */
    @GlobalCommand
    @NotifyChange("*")
    public void fileUnpackUpdate(@BindingParam(FILE_UNPACK_EVENT_ARG) FileUnpackUpdateEvent evt) {
	fileCurrentEntry = evt.getCurrent();
	fileTotalEntries = evt.getTotal();
	fileProgress = evt.getPercentage();
    }

}
