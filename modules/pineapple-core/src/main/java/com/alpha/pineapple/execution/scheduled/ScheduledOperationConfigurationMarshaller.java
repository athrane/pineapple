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
package com.alpha.pineapple.execution.scheduled;

import java.util.stream.Stream;

import com.alpha.pineapple.exception.SaveConfigurationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperations;

/**
 * Interface for loading and saving configuration scheduled operations (as XML)
 * from/to disk to objects.
 */
public interface ScheduledOperationConfigurationMarshaller {

	/**
	 * Save configuration of scheduled operations.
	 * 
	 * The configuration directory is resolved from the runtime directory provider.
	 * The configuration is saved in the default scheduled operations configuration
	 * file scheduled-operations.xml.
	 * 
	 * @param executionResult
	 *            A child execution result will be added to this result. The child
	 *            result will reflects the out come of the save operation. If the
	 *            save operation fails the state of the result object will be
	 *            failure or error and will contain a stack trace message.
	 * @param operations
	 *            scheduled operations model which is saved.
	 * 
	 * @throws SaveConfigurationFailedException
	 *             if save fails.
	 */

	void save(ExecutionResult executionResult, ScheduledOperations operations);

	/**
	 * Load configuration of scheduled operations. If no configuration file exist
	 * then an empty configuration is returned.
	 * 
	 * @param executionResult
	 *            A child execution result will be added to this result. The child
	 *            result will reflects the out come of the save operation. If the
	 *            save operation fails the state of the result object will be
	 *            failure or error and will contain a stack trace message.
	 * @return loaded scheduled operations model.
	 * 
	 * @throws LoadConfigurationFailedException
	 *             if save fails.
	 */
	ScheduledOperations load(ExecutionResult executionResult);

	/**
	 * Map scheduled operation info's for saving.
	 * 
	 * @param infos
	 *            stream of {@linkplain ScheduledOperationInfo which are mapped.
	 * 
	 * @return operations scheduled operation configuration which can be marshalled.
	 */
	ScheduledOperations map(Stream<ScheduledOperationInfo> infos);

}
