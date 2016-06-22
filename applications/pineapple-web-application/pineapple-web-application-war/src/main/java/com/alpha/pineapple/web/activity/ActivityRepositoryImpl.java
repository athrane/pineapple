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

package com.alpha.pineapple.web.activity;

import static com.alpha.pineapple.web.WebApplicationConstants.SIMPLE_DATE_FORMAT;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.web.account.Account;
import com.alpha.pineapple.web.model.Activity;
import com.alpha.pineapple.web.model.ActivityInfo;
import com.alpha.pineapple.web.model.ExecuteOperationActivity;
import com.alpha.pineapple.web.model.ObjectFactory;
import com.alpha.pineapple.web.model.OpenModuleActivity;
import com.alpha.pineapple.web.model.RefreshConfigurationActivity;
import com.alpha.pineapple.web.model.WebApplication;

/**
 * Implementation of the @link {@link ActivityRepository} interface. The
 * repository contains information about stored activities.
 */
public class ActivityRepositoryImpl implements ActivityRepository {

    /**
     * First list index.
     */
    static final int FIRST_INDEX = 0;

    /**
     * Logger object
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider webMessageProvider;

    /**
     * Web application object factory.
     */
    @Resource
    ObjectFactory webAppObjectFactory;

    /**
     * Web application state.
     */
    WebApplication appState;

    @Override
    public void initialize() {
	appState = webAppObjectFactory.createWebApplication();
	appState.setActivities(webAppObjectFactory.createActivitiesSequence());
    }

    @Override
    public void addOpenModuleActivity(Account account, String module) {
	Validate.notNull(account, "account is undefined");
	Validate.notNull(module, "module is undefined");
	Validate.notEmpty(module, "module is empty");

	// initialize info
	ActivityInfo info = webAppObjectFactory.createActivityInfo();
	setStartTime(info);
	info.setUser(account.getUsername());
	Object[] args = { account.getUsername(), module };
	String message = webMessageProvider.getMessage("ari.activity_open_module_info", args);
	info.setDescription(message);

	// initialize activity
	OpenModuleActivity activity = webAppObjectFactory.createOpenModuleActivity();
	activity.setInfo(info);
	activity.setModule(module);
	List<Activity> activitiesList = appState.getActivities().getActivities();
	activitiesList.add(FIRST_INDEX, activity);
    }

    @Override
    public void addRefreshConfigurationActivity(Account account) {
	Validate.notNull(account, "account is undefined");

	// initialize info
	ActivityInfo info = webAppObjectFactory.createActivityInfo();
	setStartTime(info);
	info.setUser(account.getUsername());
	Object[] args = { account.getUsername() };
	String message = webMessageProvider.getMessage("ari.activity_refresh_configuration_info", args);
	info.setDescription(message);

	// initialize activity
	RefreshConfigurationActivity activity = webAppObjectFactory.createRefreshConfigurationActivity();
	activity.setInfo(info);
	List<Activity> activitiesList = appState.getActivities().getActivities();
	activitiesList.add(FIRST_INDEX, activity);
    }

    @Override
    public void addExecuteOperationActivity(Account account, String module, String operation, String environment) {
	Validate.notNull(account, "account is undefined");
	Validate.notNull(module, "module is undefined");
	Validate.notEmpty(module, "module is empty");
	Validate.notNull(operation, "operation is undefined");
	Validate.notEmpty(operation, "operation is empty");
	Validate.notNull(environment, "environment is undefined");
	Validate.notEmpty(environment, "environment is empty");

	// initialize info
	ActivityInfo info = webAppObjectFactory.createActivityInfo();
	setStartTime(info);
	info.setUser(account.getUsername());
	Object[] args = { account.getUsername(), operation, module, environment };
	String message = webMessageProvider.getMessage("ari.activity_execute_operation_info", args);
	info.setDescription(message);

	// initialize activity
	ExecuteOperationActivity activity = webAppObjectFactory.createExecuteOperationActivity();
	activity.setInfo(info);
	activity.setModule(module);
	activity.setOperation(operation);
	activity.setEnvironment(environment);
	List<Activity> activitiesList = appState.getActivities().getActivities();
	activitiesList.add(FIRST_INDEX, activity);
    }

    @Override
    public Activity[] getActivities() {
	List<Activity> activitiesList = appState.getActivities().getActivities();
	Activity[] array = activitiesList.toArray(new Activity[activitiesList.size()]);
	return array;
    }

    /**
     * Add the start time and date to the activity info object.
     * 
     * @param info
     *            activity info object
     */
    void setStartTime(ActivityInfo info) {
	Date now = new Date();
	info.setStartTime(SIMPLE_DATE_FORMAT.format(now));
    }

}
