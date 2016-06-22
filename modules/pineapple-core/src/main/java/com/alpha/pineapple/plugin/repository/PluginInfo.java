/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.plugin.repository;

import org.springframework.context.ApplicationContext;

/**
 * Interface for plugin meta data.
 */
public interface PluginInfo {
    /**
     * Returns true if input marshalling is enabled for plugin.
     * 
     * @return true if input marshalling is enabled for plugin.
     */
    public boolean isInputMarshallingEnabled();

    /**
     * Set input marshalling state for for plugin.
     * 
     * @param inputMarshallingEnabled
     *            set input marshalling state for for plugin.
     */
    public void setInputMarshallingEnabled(boolean inputMarshallingEnabled);

    /**
     * Returns true if session handling is enabled for plugin.
     * 
     * @return true if session handling is enabled for plugin.
     */
    public boolean isSessionHandlingEnabled();

    /**
     * Set session handling state for for plugin.
     * 
     * @param sessionHandlingEnabled
     *            session handling state for for plugin.
     */
    public void setSessionHandlingEnabled(boolean sessionHandlingEnabled);

    /**
     * get unmarshaller ID.
     * 
     * @return unmarshaller ID.
     */
    public String getUnmarshallerId();

    /**
     * Set unmarshaller ID.
     * 
     * @param unmarshallerId
     *            unmarshaller ID.
     */
    public void setUnmarshallerId(String unmarshallerId);

    /**
     * Set plugin ID.
     * 
     * @param pluginId
     *            plugin Id.
     */
    public void setPluginId(String pluginId);

    /**
     * Get plugin Id.
     * 
     * @return plugin Id.
     */
    public String getPluginId();

    /**
     * Set configuration file name for plugin.
     * 
     * @param configFileName
     *            configuration file name for plugin.
     */
    public void setConfigFileName(String configFileName);

    /**
     * configuration file name for plugin.
     * 
     * @return configuration file name for plugin.
     */
    public String getConfigFileName();

    /**
     * Get Spring application context for plugin.
     * 
     * @return Spring application context for plugin.
     */
    public ApplicationContext getContext();

    /**
     * Set Spring application context for plugin.
     * 
     * @param context
     *            Spring application context for plugin.
     */
    public void setContext(ApplicationContext context);

    /**
     * Set session ID for plugin.
     * 
     * @param sessionId
     *            session ID for plugin.
     */
    public void setSessionId(String sessionId);

    /**
     * Get session ID for plugin.
     * 
     * @return session ID for plugin.
     */
    public String getSessionId();

}
