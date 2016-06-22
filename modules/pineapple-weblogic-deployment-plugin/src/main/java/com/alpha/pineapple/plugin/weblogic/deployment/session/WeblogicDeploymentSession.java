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


package com.alpha.pineapple.plugin.weblogic.deployment.session;

import java.util.List;

import javax.enterprise.deploy.spi.DeploymentManager;

import com.alpha.pineapple.jsr88.command.result.AvailableModulesResult;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionException;

/**
 * Interface for WebLogic deployment session.
 */
public interface WeblogicDeploymentSession extends Session
{

    /**
     * Name of protocol property.
     */
    public final String PROTOCOL_PROPERTY = "adminserver-protocol"; 

    /**
     * Name of listen address property.
     */
    public final String LISTENADDRESS_PROPERTY = "adminserver-listenaddress"; 

    /**
     * Name of listen port property.
     */
    public final String LISTENPORT_PROPERTY = "adminserver-listenport"; 

    /**
     * Name of time stamp property.
     */
    public final String TIMESTAMP_PROPERTY = "timestamp-enabled"; 
    
    /**
     * Invoke the deployer.
     * 
     * @param deployerArguments List of argument used to invoke the deployer.
     * 
     * @throws SessionException If deployer invocation fails. 
     */
    public void invokeDeployer(List<String> deployerArguments ) throws SessionException;
    
    /**
     * Returns true if time stamp usage is enabled for the plugin.
     * 
     * @return true if time stamp usage is enabled for the plugin.
     * 
     * @throws SessionException If property resolution fails.
     */
    public boolean isTimeStampEnabled() throws SessionException;

    /**
     * Get available J2EE modules in domain controlled by administration 
     * server.
     * 
     * @return Result object containing lists of available J2EE modules in 
     * domain controlled by administration server. 
     * 
     * @throws SessionException If module retrieval fails. 
     */
    public AvailableModulesResult getAvailableModules() throws SessionException;
    
    /**
     * Get connected JSR88 deployment manager.
     * 
     * @return connected JSR88 deployment manager.
     * 
     * @throws SessionException If deployment manager retrieval fails. 
     */
    public DeploymentManager getDeploymentManager() throws SessionException;
}
