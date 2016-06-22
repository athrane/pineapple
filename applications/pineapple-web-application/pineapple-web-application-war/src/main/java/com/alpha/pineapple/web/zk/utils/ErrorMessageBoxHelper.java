/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.web.zk.utils;

import static com.alpha.pineapple.web.WebApplicationConstants.ERROR_MESSAGE_MODAL_EXCEPTION_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.ERROR_MESSAGE_MODAL_ZUL;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_PARENT_WINDOW;
import static org.zkoss.zk.ui.Executions.createComponents;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.web.spring.rest.ScheduledOperationController;

/**
 * Helper class for showing a modal error message with details about an
 * exception.
 */
public class ErrorMessageBoxHelper {

    /**
     * Spring REST scheduled operation controller.
     */
    @WireVariable
    ScheduledOperationController scheduledOperationController;

    /**
     * Message provider for I18N support.
     */
    @WireVariable
    MessageProvider webMessageProvider;

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Log and show modal message box with exception.
     * 
     * @param e
     *            exception to be reported.
     */
    public void showAndLogException(Exception e) {
	Window modalWindow = null;

	try {
	    // log error
	    logger.error(StackTraceHelper.getStrackTrace(e));

	    // add parameters
	    Map<String, Object> args = new HashMap<>();
	    args.put(ERROR_MESSAGE_MODAL_EXCEPTION_ARG, e);

	    // open modal window
	    modalWindow = (Window) createComponents(ERROR_MESSAGE_MODAL_ZUL, NULL_PARENT_WINDOW, args);
	    modalWindow.doModal();

	} catch (Exception e2) {

	    // show and log error message
	    logger.error(StackTraceHelper.getStrackTrace(e2));
	    Object[] args = { e2.getMessage() };
	    String message = webMessageProvider.getMessage("embh.show_error_message_failed", args);
	    Messagebox.show(message);

	    // detach window
	    if (modalWindow != null)
		modalWindow.detach();
	}
    }

}
