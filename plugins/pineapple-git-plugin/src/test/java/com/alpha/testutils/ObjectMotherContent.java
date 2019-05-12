/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2019 Allan Thrane Andersen..
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

package com.alpha.testutils;

import java.util.List;

import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.plugin.git.model.CloneRepository;
import com.alpha.pineapple.plugin.git.model.Git;
import com.alpha.pineapple.plugin.git.model.GitCommand;
import com.alpha.pineapple.plugin.git.model.ObjectFactory;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for
 * unit testing by creating content for operations.
 */
public class ObjectMotherContent {

	/**
	 * Docker object factory.
	 */
	ObjectFactory gitFactory;

	/**
	 * Object mother for resources.
	 */
	ObjectMotherResource resourceMother;

	/**
	 * ObjectMotherContent constructor.
	 */
	public ObjectMotherContent() {
		gitFactory = new ObjectFactory();
		resourceMother = new ObjectMotherResource();

	}

	/**
	 * Create empty Git document.
	 * 
	 * @return empty Git document.
	 */
	public Git createEmptyGitModel() {
		return gitFactory.createGit();
	}

	/**
	 * Create clone repository command.
	 * 
	 * @param branch      repository branch
	 * @param destination local destination.
	 * 
	 * @return image command.
	 */
	public CloneRepository createCloneCommand(String branch, String destination) {
		CloneRepository command = gitFactory.createCloneRepository();
		command.setBranch(branch);
		command.setDestination(destination);
		return command;
	}

	/**
	 * Create Git document with clone command.
	 * 
	 * @param branch repository branch
	 * @param dest   local destination.
	 * 
	 * @return Docker document with image command.
	 */
	public Git createGitModelWithCloneCommand(String branch, String dest) {
		Git model = createEmptyGitModel();
		List<GitCommand> cmds = model.getCommands();
		cmds.add(createCloneCommand(branch, dest));
		return model;
	}

	/**
	 * Create Git Resoure.
	 * 
	 * @param uri resource URI.
	 * 
	 * @return Git resource with the URI property defined.
	 */
	public Resource createResource(String uri) {
		Resource resource = resourceMother.createResourceWithNoProperties();
		var property = new Property();
		property.setKey("uri");
		property.setValue(uri);
		resource.getProperty().add(property);
		return resource;
	}

}
