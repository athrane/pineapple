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

package com.alpha.pineapple.command.initialization;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

/**
 * Command initializer which maps the content of a Chain context to fields in a
 * Chain command instance.
 */
public interface CommandInitializer {
	/**
	 * Initialize command by mapping the content of defined keys in the context to
	 * fields in the command object. The mapping is controlled by the usage of the
	 * annotation {@link @Initialize}.
	 * 
	 * @param context
	 *            The context object to map values from.
	 * @param command
	 *            The command object where the values are mapped into fields.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If the context doesn't contains the key defined by an instance of
	 *             the annotation {@link @Initialize}.
	 */
	public void initialize(Context context, Command command) throws CommandInitializationFailedException;
}
