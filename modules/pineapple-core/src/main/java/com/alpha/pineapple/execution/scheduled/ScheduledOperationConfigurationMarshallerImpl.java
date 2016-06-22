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

import static com.alpha.pineapple.CoreConstants.OPERATIONS_FILE;
import static com.alpha.pineapple.execution.ExecutionResult.MSG_MESSAGE;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Resource;

import com.alpha.pineapple.command.CommandFacade;
import com.alpha.pineapple.command.CommandFacadeException;
import com.alpha.pineapple.exception.LoadConfigurationFailedException;
import com.alpha.pineapple.exception.SaveConfigurationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.execution.scheduled.ObjectFactory;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperation;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperations;

/**
 * Implementation of the {@linkplain ScheduledOperationConfigurationMarshaller}.
 */
public class ScheduledOperationConfigurationMarshallerImpl implements ScheduledOperationConfigurationMarshaller {

    /**
     * Object factory.
     */
    @Resource
    ObjectFactory scheduledOperationModelObjectFactory;

    /**
     * Runtime directory resolver.
     */
    @Resource
    RuntimeDirectoryProvider runtimeDirectoryProvider;

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Command facade.
     */
    @Resource
    CommandFacade commandFacade;

    @Override
    public void save(ExecutionResult result, ScheduledOperations operations) {

	// resolve file
	File confDirectory = runtimeDirectoryProvider.getConfigurationDirectory();
	File operationsFile = new File(confDirectory, OPERATIONS_FILE);

	try {
	    commandFacade.saveJaxbObjects(operationsFile, operations, result);

	} catch (CommandFacadeException e) {
	    ExecutionResult failedResult = e.getResult();

	    // create and throw exception
	    String stackTraceMessage = failedResult.getMessages().get(ExecutionResult.MSG_STACKTRACE);
	    Object[] args = { stackTraceMessage };
	    String errorMessage = messageProvider.getMessage("socm.save_operations_failed", args);
	    throw new SaveConfigurationFailedException(errorMessage);
	}

    }

    @Override
    public ScheduledOperations load(ExecutionResult result) {

	// resolve file
	File confDirectory = runtimeDirectoryProvider.getConfigurationDirectory();
	File operationsFile = new File(confDirectory, OPERATIONS_FILE);

	// return empty configuration if no file exist.
	if (!operationsFile.exists()) {

	    // add message and return empty configuration
	    Object[] args = { operationsFile };
	    String message = messageProvider.getMessage("socm.load_operations_nofile_info", args);
	    result.addMessage(MSG_MESSAGE, message);

	    return scheduledOperationModelObjectFactory.createScheduledOperations();
	}

	try {
	    Object configuration = commandFacade.loadJaxbObjects(operationsFile, ScheduledOperations.class, result);
	    return ScheduledOperations.class.cast(configuration);

	} catch (CommandFacadeException e) {
	    ExecutionResult failedResult = e.getResult();

	    // create and throw exception
	    String stackTraceMessage = failedResult.getMessages().get(ExecutionResult.MSG_STACKTRACE);
	    Object[] args = { stackTraceMessage };
	    String errorMessage = messageProvider.getMessage("socm.load_operations_failed", args);
	    throw new LoadConfigurationFailedException(errorMessage);
	}
    }

    @Override
    public ScheduledOperations map(Stream<ScheduledOperationInfo> infos) {
	ScheduledOperations operations = scheduledOperationModelObjectFactory.createScheduledOperations();
	List<ScheduledOperation> operationsList = operations.getScheduledOperation();
	infos.map(info -> info.getOperation()).forEach(operationsList::add);
	return operations;
    }

}
