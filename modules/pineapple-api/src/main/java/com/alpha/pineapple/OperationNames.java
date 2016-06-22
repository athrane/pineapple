/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
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

package com.alpha.pineapple;

/**
 * Interface defining names of the supported operations.
 */
public interface OperationNames {

    /**
     * Wild card operation name.
     */
    public static final String WILDCARD_OPERATION = "*";

    /**
     * Deploy configuration - operation name.
     */
    public static final String DEPLOY_CONFIGURATION = "deploy-configuration";

    /**
     * Test deployed configuration test - operation name.
     */
    public static final String TEST_DEPLOYED_CONFIGURATION = "test-deployed-configuration";

    /**
     * Deploy application - operation name.
     */
    public static final String DEPLOY_APPLICATION = "deploy-application";

    /**
     * Deploy application test - operation name.
     */
    public static final String TEST_DEPLOYED_APPLICATION = "test-deployed-application";

    /**
     * Start application - operation name.
     */
    public static final String START_APPLICATION = "start-application";

    /**
     * Start application test - operation name.
     */
    public static final String START_APPLICATION_TEST = "start-application-test";

    /**
     * Stop application - operation name.
     */
    public static final String STOP_APPLICATION = "stop-application";

    /**
     * Stop application test - operation name.
     */
    public static final String STOP_APPLICATION_TEST = "stop-application-test";

    /**
     * Undeploy application - operation name.
     */
    public static final String UNDEPLOY_APPLICATION = "undeploy-application";

    /**
     * Test undeployed application - operation name.
     */
    public static final String TEST_UNDEPLOYED_APPLICATION = "test-undeployed-application";

    /**
     * Undeploy configuration - operation name.
     */
    public static final String UNDEPLOY_CONFIGURATION = "undeploy-configuration";

    /**
     * Test undeployed configuration - operation name.
     */
    public static final String TEST_UNDEPLOYED_CONFIGURATION = "test-undeployed-configuration";

    /**
     * Test - operation name.
     */
    public static final String TEST = "test";

    /**
     * Create report - operation name.
     */
    public static final String CREATE_REPORT = "create-report";

}
