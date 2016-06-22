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

import static com.alpha.pineapple.web.WebApplicationConstants.COMPLETED_ACTIVITY_CREATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.CREATE_MODEL_MODAL_ZUL;
import static com.alpha.pineapple.web.WebApplicationConstants.MODEL_NAME_KEY_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.LOAD_MODULE_MODULE_INFO_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_COMPONENT_ARGS;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_GLOBALCOMMAND_ARGS;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_PARENT_WINDOW;
import static com.alpha.pineapple.web.WebApplicationConstants.OPENMODULE_MODAL_ZUL;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_SCOPE;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_DURATION;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_LOCATION;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_STYLE;
import static com.alpha.pineapple.web.WebApplicationConstants.SELECT_WORKSPACE_TAB_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.ZK_MEDIA_CHARSET;
import static com.alpha.pineapple.web.WebApplicationConstants.ZK_MEDIA_CONTENT_TYPE;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.web.activity.ActivityRepository;
import com.alpha.pineapple.web.event.consumer.OpenModuleActivityInvokerImpl;
import com.alpha.pineapple.web.model.Model;
import com.alpha.pineapple.web.model.ModelImpl;
import com.alpha.pineapple.web.model.SessionState;
import com.alpha.pineapple.web.model.SessionState.WorkspaceTabs;
import com.alpha.pineapple.web.spring.rest.ModuleController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * ZK view model for the module panel.
 */
public class ModulePanel {

    /**
     * XML model suffix.
     */
    static final String MODEL_SUFFIX = ".xml";

    /**
     * Fist environment index.
     */
    static final int FIRST_INDEX = 0;

    /**
     * Client side Java Script method invoked to create editor with document.
     */
    static final String CLIENTSIDE_JS_CREATEDOC = "createAceEditorWithDoc";

    /**
     * Client side Java Script method invoked to close editor.
     */
    static final String CLIENTSIDE_JS_CLOSEDOC = "closeAceEditor";

    /**
     * Client side Java Script method invoked to create editor with document.
     */
    static final String CLIENTSIDE_JS_REQUESTDOCTOSAVE = "requestDocumentToSave";

    /**
     * Null models list.
     */
    static final List<Model> NULL_MODELS_LIST = new ArrayList<Model>();

    /**
     * Null ZK view list model.
     */
    static final BindingListModelList<Model> NULL_LIST_MODEL = new BindingListModelList<Model>(NULL_MODELS_LIST, true);

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
     * Message provider for I18N support.
     */
    @WireVariable
    MessageProvider webMessageProvider;

    /**
     * Spring REST module controller.
     */
    @WireVariable
    ModuleController moduleController;

    /**
     * Activity repository.
     */
    @WireVariable
    ActivityRepository activityRepository;

    /**
     * Selected model.
     */
    Model selectedModel;

    /**
     * Model for model listbox.
     */
    BindingListModelList<Model> models;

    /**
     * State variable to trigger activation of modal window for confirmation of
     * model deletion.
     */
    boolean stateConfirmModelDeletion = false;

    /**
     * Initialize view model.
     */
    @Init
    public void init() {
	createViewModel();
    }

    /**
     * Create ZK view model.
     */
    void createViewModel() {

	ModuleInfo info = sessionState.getModuleInfo();
	if (info == null) {
	    models = NULL_LIST_MODEL;
	    return;
	}

	String[] environments = info.getModelEnvironments();
	if (environments == null) {
	    models = NULL_LIST_MODEL;
	    return;
	}
	List<Model> list = new ArrayList<Model>();
	for (String environment : environments) {
	    Model model = new ModelImpl(environment);
	    list.add(model);
	}
	models = new BindingListModelList<Model>(list, true);
    }

    /**
     * Set selected model in ZK view.
     * 
     * @param model
     *            to set.
     */
    public void setSelectedModel(Model model) {
	selectedModel = model;

	// set environment on session state
	if (model == null)
	    sessionState.setEnvironment(null);
	else
	    sessionState.setEnvironment(model.getEnvironment());

	// clear state
	stateConfirmModelDeletion = false;
    }

    /**
     * Get selected model in ZK view. Will return null is no model is set.
     * 
     * @return model info.
     */
    public Model getSelectedModel() {
	return selectedModel;
    }

    /**
     * Get models in selected model in ZK view.
     * 
     * @return model info.
     */
    public List<Model> getModels() {
	createViewModel();
	return models;
    }

    /**
     * Returns true if a module is open.
     * 
     * @return true if a module is open.
     */
    public boolean getModuleIsOpen() {
	return (sessionState.getModuleInfo() != null);
    }

    /**
     * Returns the value of confirm model deletion flag. If the flag is true
     * then the model deletion modal windows is enabled.
     * 
     * @return value of confirm model deletion flag.
     */
    public boolean getConfirmModelDeletion() {
	return stateConfirmModelDeletion;
    }

    /**
     * Get module directory in ZK view.
     * 
     * @return module directory.
     */
    public String getModuleDirectory() {
	ModuleInfo info = sessionState.getModuleInfo();
	if (info == null)
	    return "n/a";

	File directory = info.getDirectory();
	if (directory == null)
	    return "n/a";

	return directory.getAbsolutePath();
    }

    /**
     * Event handler for selection of model in list box.
     * 
     * Will load the model, escapes the model and invokes a client side java
     * script which creates a editor instance with the document.
     */
    @Command
    @NotifyChange("moduleDirectory")
    public void loadSelectedModel() {

	if (getSelectedModel() == null)
	    return;

	// get module info
	ModuleInfo moduleInfo = sessionState.getModuleInfo();
	if (moduleInfo == null)
	    return;

	// get module directory
	File moduleDirectory = moduleInfo.getDirectory();
	if (moduleDirectory == null)
	    return;
	if (!moduleDirectory.exists())
	    return;
	if (!moduleDirectory.isDirectory())
	    return;

	// get model directory
	File modelDirectory = new File(moduleDirectory, "models");
	if (!modelDirectory.exists())
	    return;
	if (!modelDirectory.isDirectory())
	    return;

	// get model file
	String modelFileName = getSelectedModel().getEnvironment() + ".xml";
	File modelFile = new File(modelDirectory, modelFileName);
	if (!modelFile.exists())
	    return;
	if (!modelFile.isFile())
	    return;

	try {
	    // load model
	    String model = FileUtils.readFileToString(modelFile, "UTF-8");

	    // escape model for transport to client
	    String escapedModel = escapeModelForTransport(model);

	    // invoke java script to create editor with document
	    StringBuilder script = new StringBuilder().append(CLIENTSIDE_JS_CREATEDOC).append("(\'")
		    .append(escapedModel).append("\')");
	    Clients.evalJavaScript(script.toString());

	} catch (Exception e) {
	    Object[] args = { StackTraceHelper.getStrackTrace(e) };
	    String msg = webMessageProvider.getMessage("mp.load_model_failed", args);
	    logger.error(msg);
	    return;
	}
    }

    /**
     * Event handler for the global command "createModel". The event is
     * triggered from the menu controller and the module panel which posts the
     * global command.
     * 
     * Step 1) of creating a model. Will open a model window for model creation.
     */
    @GlobalCommand
    public void createModel() {
	Window modalWindow = null;

	// exit if module isn't open
	if (sessionState.getModuleInfo() == null) {
	    String message = webMessageProvider.getMessage("mp.create_model_not_open_failed");
	    Messagebox.show(message);
	    return;
	}

	try {
	    // add modal arguments
	    final HashMap<String, Object> arguments = new HashMap<String, Object>();
	    arguments.put("selectedModule", sessionState.getModuleInfo());

	    // open modal window
	    modalWindow = (Window) Executions.createComponents(CREATE_MODEL_MODAL_ZUL, NULL_PARENT_WINDOW, arguments);
	    modalWindow.doModal();

	} catch (Exception e) {

	    // show and log error message
	    logger.error(StackTraceHelper.getStrackTrace(e));
	    Object[] args = { e.getMessage() };
	    String message = webMessageProvider.getMessage("mp.create_model_failed", args);
	    Messagebox.show(message);

	    // detach window
	    if (modalWindow != null)
		modalWindow.detach();
	}

    }

    /**
     * Event handler for the global command "createModelConfirmed". The event is
     * triggered from the modal window for creating the model.
     * 
     * Step 2) of create a model. Will create model with returned environment
     * from the modal window The model is created in the core component using
     * REST API.
     * 
     * @param environment
     *            from modal window.
     */
    @GlobalCommand
    @NotifyChange("*")
    public void createModelConfirmed(@BindingParam(MODEL_NAME_KEY_ARG) String environment) {

	try {
	    // create new model
	    moduleController.createModel(sessionState.getModuleInfo().getId(), environment);

	} catch (Exception e) {
	    // show and log error message
	    logger.error(StackTraceHelper.getStrackTrace(e));
	    Messagebox.show(e.getMessage());
	    return;
	}

	// create ZK rendering model and add it to ZK model for module models
	Model guiModel = new ModelImpl(environment);
	models.add(guiModel);

	// set selected model
	setSelectedModel(guiModel);
    }

    /**
     * Event handler for the global command "deleteModel". The event is
     * triggered from the menu controller and the module panel which posts the
     * global command.
     * 
     * Step 1) of deleting a model. Will show deletion confirmation dialog.
     */
    @GlobalCommand
    @NotifyChange({ "confirmModelDeletion" })
    public void deleteModel() {

	// exit if model isn't selected
	if (selectedModel == null) {
	    String message = webMessageProvider.getMessage("mp.delete_model_not_selected_failed");
	    Messagebox.show(message);
	    return;
	}

	// set confirm deletion state to open modal confirmation window
	stateConfirmModelDeletion = true;
    }

    /**
     * Event handler for the global command "deleteModelConfirmed". The event is
     * triggered from the confirm deletion modal window.
     * 
     * Step 2) of deleting a model. Will close the modal window and delete the
     * model.
     */
    @GlobalCommand
    @NotifyChange("*")
    public void deleteModelConfirmed() {

	// clear confirm deletion state to close modal window
	stateConfirmModelDeletion = false;

	try {

	    // delete model
	    moduleController.deleteModel(sessionState.getModuleInfo().getId(), selectedModel.getEnvironment());

	} catch (Exception e) {

	    // show and log error message
	    logger.error(StackTraceHelper.getStrackTrace(e));
	    Object[] args = { e.getMessage() };
	    String message = webMessageProvider.getMessage("mp.delete_model_failed", args);
	    Messagebox.show(message);
	    return;
	}

	// clear selected model
	setSelectedModel(null);

	// create new view model
	createViewModel();

	// invoke java script to request document to be closed.
	StringBuilder script = new StringBuilder().append(CLIENTSIDE_JS_CLOSEDOC).append("()");
	Clients.evalJavaScript(script.toString());

	// post notification
	Clients.showNotification(webMessageProvider.getMessage("mp.delete_model_info"), POST_STYLE, null, POST_LOCATION,
		POST_DURATION);
    }

    /**
     * Event handler for the command "deleteModelCancelled". The event is
     * triggered from the confirm deletion modal window.
     * 
     * Will close the modal window and abort the deletion.
     */
    @Command
    @NotifyChange({ "confirmModelDeletion" })
    public void deleteModelCancelled() {

	// clear confirm deletion state to close modal window
	stateConfirmModelDeletion = false;
    }

    /**
     * Event handler for the global command "saveModel". The event is triggered
     * from the menu bar as a global command.
     * 
     * The event handler will start the process of saving a document by invoking
     * a client side java script which requests the document from the editor for
     * saving.
     */
    @GlobalCommand
    public void saveModel() {

	// exit if model isn't selected
	if (selectedModel == null) {
	    String message = webMessageProvider.getMessage("mp.save_model_not_selected_failed");
	    Messagebox.show(message);
	    return;
	}

	// invoke java script to request document to be saved.
	StringBuilder script = new StringBuilder().append(CLIENTSIDE_JS_REQUESTDOCTOSAVE).append("()");
	Clients.evalJavaScript(script.toString());
    }

    /**
     * Event handler for the global command "openModule". The event is triggered
     * from the menu controller and the module panel which posts the global
     * command.
     * 
     * Step 1) of opening a module. Will open a model window for module
     * selection.
     */
    @GlobalCommand
    public void openModule() {
	Window modalWindow = null;

	try {
	    // open modal module selection window
	    modalWindow = (Window) Executions.createComponents(OPENMODULE_MODAL_ZUL, NULL_PARENT_WINDOW,
		    NULL_COMPONENT_ARGS);
	    modalWindow.doModal();

	    // request focus on the module tab
	    sessionState.setRequestTabFocus(WorkspaceTabs.MODULE);

	    // post global command to trigger selection of module tab on
	    // workspace panel
	    BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, SELECT_WORKSPACE_TAB_GLOBALCOMMAND,
		    NULL_GLOBALCOMMAND_ARGS);

	} catch (Exception e) {

	    // log error message
	    Object[] args = { StackTraceHelper.getStrackTrace(e) };
	    String message = webMessageProvider.getMessage("mp.open_module_failed", args);
	    logger.error(message);

	    // detach window
	    if (modalWindow != null)
		modalWindow.detach();
	}
    }

    /**
     * Event handler for the global command "loadModule". The event is triggered
     * from the {@linkplain OpenModuleModalPanel and}
     * {@linkplain OpenModuleActivityInvokerImpl} which posts the global
     * command.
     * 
     * The event handler will load the module, notify the MVVM binder that all
     * properties needs to be refreshed in the view and post a global command to
     * request an update of the activity listbox and the home panel.
     */
    @GlobalCommand
    @NotifyChange("*")
    public void loadModule(@BindingParam(LOAD_MODULE_MODULE_INFO_ARG) ModuleInfo moduleInfo) {

	// set chosen module
	sessionState.setModuleInfo(moduleInfo);

	// select first model in module
	String[] environments = moduleInfo.getModelEnvironments();
	if (environments.length == 0) {
	    sessionState.setEnvironment(null);
	} else {
	    sessionState.setEnvironment(environments[FIRST_INDEX]);
	}

	// create model for loaded module
	createViewModel();

	// set first model item as selected
	if (models.getSize() == 0)
	    return;
	Model firstmodel = models.getElementAt(0);
	selectedModel = firstmodel;
	models.addToSelection(firstmodel);

	// load selected model
	loadSelectedModel();

	// register open module activity
	String moduleId = sessionState.getModuleInfo().getId();
	activityRepository.addOpenModuleActivity(sessionState.getAccount(), moduleId);

	// post global command to trigger update of the activity list on the
	// home tab
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, EventQueues.APPLICATION,
		COMPLETED_ACTIVITY_CREATION_GLOBALCOMMAND, NULL_GLOBALCOMMAND_ARGS);
    }

    /**
     * Event handler for the global command "closeModule". The event is
     * triggered from the menu bar as a global command.
     * 
     * The event handler will start the process of saving a document by invoking
     * a client side java script which requests the document from the editor for
     * saving.
     */
    @GlobalCommand
    @NotifyChange("*")
    public void closeModule() {

	// clear selected module and model
	setSelectedModel(null);
	sessionState.setModuleInfo(null);

	// invoke java script to request document to be closed.
	StringBuilder script = new StringBuilder().append(CLIENTSIDE_JS_CLOSEDOC).append("()");
	Clients.evalJavaScript(script.toString());
    }

    /**
     * Event handler for the global command "downloadModel". The event is
     * triggered from the menu bar as a global command.
     * 
     * The event handler will save the selected model at the client side, e.g.
     * the browser.
     */
    @GlobalCommand
    public void downloadModel() {

	// exit if model isn't selected
	if (selectedModel == null) {
	    String message = webMessageProvider.getMessage("mp.download_model_not_selected_failed");
	    Messagebox.show(message);
	    return;
	}

	// declare file
	File modelFile = null;

	try {
	    // get model file
	    ModuleInfo info = sessionState.getModuleInfo();
	    modelFile = createModelFile(info, selectedModel.getEnvironment());

	    // create ZK media representation of model file
	    AMedia media = new AMedia(modelFile, ZK_MEDIA_CONTENT_TYPE, ZK_MEDIA_CHARSET);

	    // save
	    Filedownload.save(media.getReaderData(), ZK_MEDIA_CONTENT_TYPE, modelFile.getName());

	    // post notification that model was saved successfully
	    Clients.showNotification(webMessageProvider.getMessage("mp.download_model_info"), POST_STYLE, null,
		    POST_LOCATION, POST_DURATION);

	} catch (FileNotFoundException e) {

	    // log error message
	    Object[] args = { modelFile };
	    String message = webMessageProvider.getMessage("mp.download_model_failed", args);
	    logger.error(message);

	    // show error in message box
	    Messagebox.show(message, webMessageProvider.getMessage("pineapple.msgbox_title"), Messagebox.OK,
		    Messagebox.ERROR);
	}
    }

    /**
     * Return escaped model suitable for transport to client as java script
     * parameter. Escape \, ", and control codes (anything less than U+0020).
     * 
     * @param model
     *            model.
     * 
     * @return Return escaped model.
     */
    String escapeModelForTransport(String model) {
	Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	String escapedModel = gson.toJson(model);

	// remove enclosing double quotes added to by JSON escaping
	return escapedModel.substring(1, escapedModel.length() - 1);
    }

    /**
     * Create model file name for saving a model.
     * 
     * @param info
     *            module info.
     * @param environment
     *            Model name.
     * 
     * @return File object defining the absolute path to the model file.
     * 
     */
    File createModelFile(ModuleInfo info, String environment) {
	StringBuilder fileName = new StringBuilder();
	fileName.append(environment);
	fileName.append(MODEL_SUFFIX);

	// create file
	File modelsDirectory = new File(info.getDirectory(), "models");
	return new File(modelsDirectory, fileName.toString());
    }

}
