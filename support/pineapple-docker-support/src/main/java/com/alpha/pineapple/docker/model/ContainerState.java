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
package com.alpha.pineapple.docker.model;

import com.alpha.pineapple.docker.model.rest.InspectedContainer;
import com.alpha.pineapple.docker.model.rest.InspectedContainerState;

/**
 * Enumeration which defines the Docker container states used by the project.
 */
public enum ContainerState {
    RUNNING("running"), PAUSED("paused"), STOPPED("stopped"), RESTARTING("restarting"), OOM("oom");

    /**
     * Enum name.
     */
    String name;

    /**
     * ContainerState constructor.
     * 
     * @param name
     *            name of the state.
     */
    ContainerState(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return name;
    }

    /**
     * Return the container state as enum from an inspected container.
     * 
     * @param inspectedContainer
     *            inspected container to return the state from.
     * @return the container state as enum from an inspected container
     */
    public static ContainerState getContainerStateFromInspectedContainer(InspectedContainer inspectedContainer) {
	InspectedContainerState actualState = inspectedContainer.getState();
	if (actualState.isPaused())
	    return PAUSED;
	if (actualState.isRunning())
	    return RUNNING;
	if (actualState.isRestarting())
	    return RESTARTING;
	if (actualState.isOomKilled())
	    return OOM;
	return STOPPED;
    }

}
