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

package com.alpha.testutils;

import java.util.List;

import com.alpha.pineapple.plugin.composite.execution.model.Composite;
import com.alpha.pineapple.plugin.composite.execution.model.CompositeExecution;
import com.alpha.pineapple.plugin.composite.execution.model.ObjectFactory;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for
 * unit testing by creating content for operations.
 */
public class ObjectMotherContent {

	/**
	 * Docker object factory.
	 */
	ObjectFactory dockerFactory;

	/**
	 * ObjectMotherContent constructor.
	 */
	public ObjectMotherContent() {
		dockerFactory = new ObjectFactory();
	}

	/**
	 * Create empty composite execution document.
	 * 
	 * @return empty composite execution document.
	 */
	public CompositeExecution createEmptyCompositeExecutionModel() {
		return dockerFactory.createCompositeExecution();
	}

	/**
	 * Create composite.
	 * 
	 * @param name
	 *            composite name.
	 * 
	 * @return image command.
	 */
	public Composite createCompositeCommand(String name) {
		Composite composite = dockerFactory.createComposite();
		composite.setName(name);
		return composite;
	}

	/**
	 * Create composite execution document with composite command.
	 * 
	 * @param name
	 *            composite name.
	 * 
	 * @return composite execution document with composite command.
	 */
	public CompositeExecution createCompositeExecutionWithSingleComposite(String name) {
		CompositeExecution model = createEmptyCompositeExecutionModel();
		List<Composite> modules = model.getModule();
		modules.add(createCompositeCommand(name));
		return model;
	}

	/**
	 * Create composite execution document with two composite commands.
	 * 
	 * @param name
	 *            composite name.
	 * @param name2
	 *            composite name #2.
	 * 
	 * @return composite execution document with two composite commands.
	 */
	public CompositeExecution createCompositeExecutionWithTwoComposites(String name, String name2) {
		CompositeExecution model = createEmptyCompositeExecutionModel();
		List<Composite> modules = model.getModule();
		modules.add(createCompositeCommand(name));
		modules.add(createCompositeCommand(name2));
		return model;
	}

}
