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

import static com.alpha.pineapple.web.WebApplicationConstants.CREATE_RESOURCE_PROPERTY_CONFIRMED_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_SCOPE;
import static com.alpha.pineapple.web.WebApplicationConstants.RESOURCE_PROPERTY_KEY_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.RESOURCE_PROPERTY_VALUE_ARG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.model.configuration.Resource;

/**
 * ZK view model for the create resource property panel (modal).
 */
public class CreateResourcePropertyModalPanel {

    /**
     * Message provider for I18N support.
     */
    @WireVariable
    MessageProvider webMessageProvider;

    /**
     * Property key.
     */
    String key;

    /**
     * Property value.
     */
    String value;

    /**
     * Property status.
     */
    boolean propertyStatus;

    /**
     * Password status as string.
     */
    String propertyStatusAsString;

    /**
     * Properties.
     */
    Map<String, Property> propertiesMap;

    /**
     * Initialize view model.
     * 
     * @param selectedResource
     *            selected resource.
     */
    @Init
    public void init(@ExecutionArgParam("selectedResource") Resource selectedResource) {
	key = "";
	value = "";

	// create map of existing resource properties
	propertiesMap = new HashMap<String, Property>();
	initializeExistingResourcePropertyKeys(selectedResource);

	updateStatus();
    }

    /**
     * Initialize set of key of existing resource properties.
     * 
     * @param resource
     *            selected model resource.
     */
    void initializeExistingResourcePropertyKeys(Resource resource) {
	if (resource == null)
	    return;
	List<Property> properties = resource.getProperty();
	if (properties == null)
	    return;
	for (Property property : properties) {
	    if (propertiesMap.containsKey(property.getKey()))
		continue;
	    propertiesMap.put(property.getKey(), property);
	}
    }

    /**
     * Event handler for the 'updateStatus' command.
     * 
     * Calculates property status.
     */
    public void updateStatus() {

	// handle empty key
	if (key.isEmpty()) {
	    propertyStatusAsString = webMessageProvider.getMessage("crpmp.key_empty_info");
	    propertyStatus = true;
	    return;
	}

	// handle identical keys
	if (propertiesMap.containsKey(key)) {
	    propertyStatusAsString = webMessageProvider.getMessage("crpmp.key_already_exist_info");
	    propertyStatus = true;
	    return;
	}

	// handle empty value
	if (value.isEmpty()) {
	    propertyStatusAsString = webMessageProvider.getMessage("crpmp.value_empty_info");
	    propertyStatus = true;
	    return;
	}

	propertyStatusAsString = webMessageProvider.getMessage("crpmp.valid_property_info");
	propertyStatus = false;
    }

    /**
     * Event handler for onChanging and onFocus events from the propertyKey text
     * box. Will update the property status.
     * 
     * @param key
     *            property key from text box.
     */
    @Command
    @NotifyChange({ "propertyStatus", "propertyStatusAsString" })
    public void updatePropertyKey(@BindingParam("propertyKey") String key) {
	this.key = key;
	// recalculate status
	updateStatus();
    }

    /**
     * Event handler for onChanging and onFocus events from the propertyKey text
     * box. Will update the property status.
     * 
     * @param value
     *            property value.
     */
    @Command
    @NotifyChange({ "propertyStatus", "propertyStatusAsString" })
    public void updatePropertyValue(@BindingParam("propertyValue") String value) {
	this.value = value;
	updateStatus();
    }

    /**
     * Get property key.
     * 
     * @return property key.
     */
    public String getPropertyKey() {
	return key;
    }

    /**
     * Set property key.
     * 
     * @param key
     *            property key.
     */
    public void setPropertyKey(String key) {
	this.key = key;
    }

    /**
     * Get property value.
     * 
     * @return property value.
     */
    public String getPropertyValue() {
	return value;
    }

    /**
     * Set property value.
     * 
     * @param value
     *            property value.
     */
    public void setPropertyValue(String value) {
	this.value = value;
    }

    /**
     * Get property status as text.
     * 
     * @return property status as text.
     */
    public String getPropertyStatusAsString() {
	return propertyStatusAsString;
    }

    /**
     * Get property status as boolean.
     * 
     * @return false if the property can be created.
     */
    public boolean getPropertyStatus() {
	return propertyStatus;
    }

    /**
     * Event handler for the command "confirmResourceProperty".
     * 
     * The event is triggered from the "confirm" button menu which posts the
     * command.
     */
    @Command
    public void confirmResourceProperty() {

	// create command arguments with event
	Map<String, Object> args = new HashMap<String, Object>();
	args.put(RESOURCE_PROPERTY_KEY_ARG, key);
	args.put(RESOURCE_PROPERTY_VALUE_ARG, value);

	// post global command to trigger create resource property global
	// command
	BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE,
		CREATE_RESOURCE_PROPERTY_CONFIRMED_GLOBALCOMMAND, args);
    }

    /**
     * Event handler for the command "cancelResourceProperty".
     * 
     * The event is triggered from the "cancel" button menu which posts the
     * command.
     */
    @Command
    public void cancelResourceProperty() {
	// no operation
    }

}
