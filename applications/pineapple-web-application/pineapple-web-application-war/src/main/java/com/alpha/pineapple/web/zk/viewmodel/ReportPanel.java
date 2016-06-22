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

import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_ALL_REPORTS_CONFIRMED_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_ALL_REPORTS_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.DELETE_ALL_REPORTS_MODAL_ZUL;
import static com.alpha.pineapple.web.WebApplicationConstants.HTML_REPORT_FILE;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_GLOBALCOMMAND_ARGS;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_PARENT_WINDOW;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_DURATION;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_LOCATION;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_STYLE;
import static com.alpha.pineapple.web.WebApplicationConstants.SAVE_REPORT_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.ZK_MEDIA_CHARSET;
import static com.alpha.pineapple.web.WebApplicationConstants.ZK_MEDIA_CONTENT_TYPE;
import static org.zkoss.zk.ui.Executions.createComponents;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.report.Report;
import com.alpha.pineapple.model.report.Reports;
import com.alpha.pineapple.web.event.consumer.CreatedReportNotifierImpl;
import com.alpha.pineapple.web.model.SessionState;
import com.alpha.pineapple.web.spring.rest.ReportController;
import com.alpha.pineapple.web.zk.utils.ErrorMessageBoxHelper;

/**
 * ZK view model for the report panel.
 */
public class ReportPanel {

    /**
     * Report file name.
     */
    static final String REPORT_HTML_FILENAME = "basic-report.html";

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
     * Spring REST report controller.
     */
    @WireVariable
    ReportController reportController;

    /**
     * Message provider for I18N support.
     */
    @WireVariable
    MessageProvider webMessageProvider;

    /**
     * Error message box helper.
     */
    @WireVariable
    ErrorMessageBoxHelper errorMessageBoxHelper;

    /**
     * executionState style converter.
     */
    @WireVariable
    Converter<String, Object, Component> executionStateStyleConverter;

    /**
     * executionState text converter.
     */
    @WireVariable
    Converter<String, Object, Component> executionStateTextConverter;

    /**
     * Loaded media.
     */
    AMedia media;

    /**
     * Initialize view model.
     */
    @Init
    public void init() {
    }

    /**
     * Get ExecutionState style converter.
     * 
     * @return converter.
     */
    public Converter<String, Object, Component> getExecutionStateStyleConverter() {
	return executionStateStyleConverter;
    }

    /**
     * Get ExecutionState text converter.
     * 
     * @return converter.
     */
    public Converter<String, Object, Component> getExecutionStateTextConverter() {
	return executionStateTextConverter;
    }

    /**
     * Set selected report in the ZK view.
     * 
     * @param report
     *            Selected report in the ZK view.
     */
    public void setSelectedReport(Report report) {
	sessionState.setReport(report);
    }

    /**
     * Get selected report in ZK view.
     * 
     * @return selected report.
     */
    public Report getSelectedReport() {
	return sessionState.getReport();
    }

    /**
     * Get all reports.
     * 
     * @return all reports.
     */
    public Collection<Report> getReports() {
	Reports reports = reportController.getReports();
	return reports.getReport();
    }

    /**
     * Get loaded media.
     * 
     * @return loaded media.
     */
    public AMedia getMedia() {
	return media;
    }

    /**
     * Event handler for selection of report in list box.
     * 
     * Will load the report as an IFrame.
     */
    @Command
    public void loadSelectedReport() {

	// get selected report
	Report report = sessionState.getReport();

	// exit if no report is selected
	if (report == null)
	    return;

	try {
	    // get report file
	    File reportHtmlFile = new File(report.getDirectory(), REPORT_HTML_FILENAME);
	    media = new AMedia(reportHtmlFile, ZK_MEDIA_CONTENT_TYPE, ZK_MEDIA_CHARSET);

	} catch (Exception e) {
	    Object[] args = { StackTraceHelper.getStrackTrace(e) };
	    String msg = webMessageProvider.getMessage("rp.load_report_failed", args);
	    logger.error(msg);
	    return;
	}
    }

    /**
     * Event handler for the global command "completedReportCreation". The event
     * is triggered from the {@linkplain CreatedReportNotifierImpl} Reactor
     * consumer which posts the global command.
     * 
     * The event handler will create a new reports model and notify the MVVM
     * binder that the reports model is updated.
     */
    @GlobalCommand
    @NotifyChange("reports")
    public void completedReportCreation() {
    }

    /**
     * Event handler for the global command "saveReport". The event is triggered
     * from the menu bar as a global command.
     * 
     * The event handler will save the selected report at the client side, e.g.
     * the browser.
     */
    @GlobalCommand(SAVE_REPORT_GLOBALCOMMAND)
    public void saveReport() {

	// get selected report
	Report report = sessionState.getReport();

	// exit if no report is selected
	if (report == null) {
	    Messagebox.show(webMessageProvider.getMessage("rp.no_report_selected_info"),
		    webMessageProvider.getMessage("pineapple.msgbox_title"), Messagebox.OK, Messagebox.INFORMATION);

	    return;
	}

	// declare file
	File reportFile = null;

	try {
	    // get report file
	    reportFile = new File(report.getDirectory(), HTML_REPORT_FILE);

	    // create ZK media representation of HTML report
	    AMedia media = new AMedia(reportFile, ZK_MEDIA_CONTENT_TYPE, ZK_MEDIA_CHARSET);

	    // create download file name
	    String downloadFileName = new StringBuilder().append("pineapple-").append(report.getId()).append(".html")
		    .toString();

	    // save
	    Filedownload.save(media.getReaderData(), ZK_MEDIA_CONTENT_TYPE, downloadFileName);

	    // post notification that report was saved successfully
	    Clients.showNotification(webMessageProvider.getMessage("rp.download_report_info"), POST_STYLE, null,
		    POST_LOCATION, POST_DURATION);

	} catch (

	FileNotFoundException e)

	{

	    // log error message
	    Object[] args = { reportFile };
	    String message = webMessageProvider.getMessage("rp.download_report_failed", args);
	    logger.error(message);

	    // show error in message box
	    Messagebox.show(message, webMessageProvider.getMessage("pineapple.msgbox_title"), Messagebox.OK,
		    Messagebox.ERROR);
	}
    }

    /**
     * Event handler for the global command "deleteAllReports". The event is
     * triggered from the menu controller and the reports panel which posts the
     * global command.
     * 
     * Step 1) of deletion all reports. Will open a modal window for
     * confirmation. All operations are deleted in the core component using REST
     * API.
     */
    @GlobalCommand(DELETE_ALL_REPORTS_GLOBALCOMMAND)
    public void deleteAllReports() {
	Window modalWindow = null;

	try {

	    // open modal window
	    modalWindow = (Window) createComponents(DELETE_ALL_REPORTS_MODAL_ZUL, NULL_PARENT_WINDOW,
		    NULL_GLOBALCOMMAND_ARGS);
	    modalWindow.doModal();

	} catch (Exception e) {
	    errorMessageBoxHelper.showAndLogException(e);

	    // detach window
	    if (modalWindow != null)
		modalWindow.detach();
	}
    }

    /**
     * Event handler for the global command "deleteAllReportsConfirmed". The
     * event is triggered from the modal window after deletion of all scheduled
     * operations.
     * 
     * Step 2) of deleting all reports. Will update the view model.
     */
    @GlobalCommand(DELETE_ALL_REPORTS_CONFIRMED_GLOBALCOMMAND)
    @NotifyChange("reports")
    public void deleteAllReportsConfirmed() {
	// NO-OP, other than updating the view.
    }
}
