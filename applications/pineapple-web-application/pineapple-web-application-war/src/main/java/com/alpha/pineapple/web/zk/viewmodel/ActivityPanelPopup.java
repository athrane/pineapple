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

import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import reactor.core.Reactor;
import reactor.event.Event;

import com.alpha.pineapple.web.activity.ActivityRepository;
import com.alpha.pineapple.web.model.Activity;
import com.alpha.pineapple.web.model.SessionState;

/**
 * ZK view model for the activity popup panel.
 */
public class ActivityPanelPopup {

    /**
     * Session state.
     */
    @WireVariable
    SessionState sessionState;

    /**
     * Activity repository.
     */
    @WireVariable
    ActivityRepository activityRepository;

    /**
     * Web application reactor.
     */
    @WireVariable
    Reactor webAppReactor;

    /**
     * Activities.
     */
    List<Activity> activityModel;

    /**
     * Initialize view model.
     */
    @Init
    public void init() {
	createModel();
    }

    /**
     * initialize activity model.
     */
    void createModel() {
	Activity[] activities = activityRepository.getActivities();
	activityModel = Arrays.asList(activities);
    }

    /**
     * Set selected activity in the ZK view.
     * 
     * @param info
     *            selected module in the ZK view.
     */
    public void setSelectedActivity(Activity activity) {
	sessionState.setActivity(activity);
    }

    /**
     * Get selected activity in ZK view.
     * 
     * @return selected activity.
     */
    public Activity getSelectedActivity() {
	return sessionState.getActivity();
    }

    /**
     * Return registered activities.
     * 
     * @return array of registered activities.
     */
    public List<Activity> getActivities() {
	return activityModel;
    }

    /**
     * Event handler for selection of activity in list box.
     * 
     * Will post the selected activity on the activity topic for a consumer to
     * re-perform the activity.
     */
    @Command
    public void performActivity() {
	Activity activity = sessionState.getActivity();
	webAppReactor.notify(activity.getClass(), Event.wrap(activity));
    }

    /**
     * Event handler for the global command "completedActivityCreation". The
     * event is triggered from the {@linkplain ModulePanel},
     * {@linkplain EnvironmentConfigurationPanel} and the
     * {@linkplain ExecutionPanel} view models which posts the global command.
     * 
     * The event handler notify the MVVM binder that the activity model is
     * updated.
     */
    @GlobalCommand
    @NotifyChange("*")
    public void completedActivityCreation() {
	createModel();
    }

}
