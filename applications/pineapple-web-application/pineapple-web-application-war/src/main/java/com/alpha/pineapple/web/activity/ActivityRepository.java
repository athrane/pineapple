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

import com.alpha.pineapple.web.account.Account;
import com.alpha.pineapple.web.model.Activity;

/**
 * Interface for activity repository. The activity repository is used to store
 * activities.
 */
public interface ActivityRepository {

    /**
     * Initialize repository
     */
    void initialize();

    /**
     * Add open module activity.
     * 
     * @param account
     *            the user which conducted the activity.
     * @param module
     *            which was attempted to be opened.
     */
    void addOpenModuleActivity(Account account, String module);

    /**
     * Add refresh configuration activity.
     * 
     * @param account
     *            the user which conducted the activity.
     */
    void addRefreshConfigurationActivity(Account account);

    /**
     * Add execute operation activity.
     * 
     * @param account
     *            the user which conducted the activity.
     * @param module
     *            which operation was invoked on.
     * @param operation
     *            invoked operation.
     * @param environment
     *            environment for which operation was invoked.
     * 
     */
    void addExecuteOperationActivity(Account account, String module, String operation, String environment);

    /**
     * Get list of activities in repository. The activities are returned with
     * the newest activity first.
     * 
     * @return array of activities in repository.
     */
    Activity[] getActivities();

}
