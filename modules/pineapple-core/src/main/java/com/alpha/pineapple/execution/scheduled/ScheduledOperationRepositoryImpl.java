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

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.execution.OperationTask;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.execution.scheduled.ObjectFactory;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperation;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperations;

/**
 * Implementation of the {@linkplain ScheduledOperationRespository} for
 * management of scheduled operation.
 */
public class ScheduledOperationRepositoryImpl implements ScheduledOperationRespository {

    /**
     * Object factory.
     */
    @Resource
    ObjectFactory scheduledOperationModelObjectFactory;

    /**
     * Execution result factory.
     */
    @Resource
    ExecutionResultFactory executionResultFactory;

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Operation scheduler.
     */
    @Resource
    ThreadPoolTaskScheduler operationScheduler;

    /**
     * Synchronous operation task.
     */
    @Resource
    OperationTask operationTask;

    /**
     * scheduled operation marshaller.
     */
    @Resource
    ScheduledOperationConfigurationMarshaller scheduledOperationConfigurationMarshaller;

    /**
     * Collection of scheduled operations.
     */
    Map<String, ScheduledOperationInfo> operations = new ConcurrentHashMap<String, ScheduledOperationInfo>();

    /**
     * Comparator for returning sorted stream of
     * {@linkplain ScheduledOperationInfo}.
     */
    Comparator<ScheduledOperationInfo> byName = (e1, e2) -> e1.getOperation().getName()
	    .compareTo(e2.getOperation().getName());

    /**
     * Create scheduled task.
     * 
     * @param operation
     *            operation to execute.
     * @param environment
     *            target environment to execution operation in.
     * @param module
     *            target module.
     * 
     * @return scheduled task.
     */
    Runnable createScheduledTask(String operation, String environment, String module) {
	return new ScheduledOperationTask(operation, environment, module, operationTask);
    }
    
    @Override
    public void initialize(ExecutionResult result) {

	// delete without saving
	operations.keySet().forEach(name -> doDelete(name));

	// load configuration
	String message = messageProvider.getMessage("sori.initialize_info");	
	ExecutionResult childResult = result.addChild(message);	
	load(childResult);
	
	Object[] args = { this.operations.size() };
	childResult.completeAsComputed(messageProvider, "sori.initialize_completed", args);
    }

    @Override
    public ScheduledOperationInfo create(String name, String module, String operation, String environment,
	    String description, String cron) {
	Validate.notNull(name, "name is undefined");
	Validate.notEmpty(name, "name is empty");
	Validate.notNull(operation, "operation is undefined");
	Validate.notEmpty(operation, "operation is empty");
	Validate.notNull(environment, "environment is undefined");
	Validate.notEmpty(environment, "environment is empty");
	Validate.notNull(module, "module is undefined");
	Validate.notEmpty(module, "module is empty");
	Validate.notNull(description, "description is undefined");
	Validate.notNull(cron, "cron is undefined");
	Validate.notEmpty(cron, "cron is empty");

	// create operation
	ScheduledOperation scheduledOperation = scheduledOperationModelObjectFactory.createScheduledOperation();
	scheduledOperation.setName(name);
	scheduledOperation.setOperation(operation);
	scheduledOperation.setEnvironment(environment);
	scheduledOperation.setModule(module);
	scheduledOperation.setDescription(description);
	scheduledOperation.setCron(cron);

	return create(scheduledOperation);
    }

    @Override
    public ScheduledOperationInfo create(ScheduledOperation operation) {
	Validate.notNull(operation, "operation is undefined");

	// validate name is unique
	if (operations.containsKey(operation.getName())) {
	    Object[] args = { operation.getName() };
	    String message = messageProvider.getMessage("sori.add_operation_alreadyexist_failed", args);
	    throw new ScheduledOperationAlreadyExistsException(message);
	}

	// TODO: module is valid
	// TODO: validate model/environment

	// schedule job with the scheduler
	Trigger cronTrigger = new CronTrigger(operation.getCron());
	Runnable task = createScheduledTask(operation.getOperation(), operation.getEnvironment(),
		operation.getModule());
	ScheduledFuture<?> future = operationScheduler.schedule(task, cronTrigger);

	// create info
	ScheduledOperationInfo info = new ScheduledOperationInfoImpl(operation, future);

	// store jobs
	operations.put(operation.getName(), info);

	save();
	return info;
    }

    @Override
    public void delete(String name) {
	Validate.notNull(name, "name is undefined");
	Validate.notEmpty(name, "name is empty");

	if (!operations.containsKey(name)) {
	    Object[] args = { name };
	    String message = messageProvider.getMessage("sori.delete_operation_notfound_failed", args);
	    throw new ScheduledOperationNotFoundException(message);
	}

	doDelete(name);
	save();
    }

    /**
     * Do actual deletion without saving the result.
     * 
     * @param name
     *            name of operation to delete. No validations on name are
     *            performed prior to deletion.
     */
    void doDelete(String name) {
	// delete from scheduler by canceling the task
	ScheduledOperationInfo operation = operations.get(name);
	operation.cancel();

	// delete operation from repository
	operations.remove(name);
    }

    @Override
    public void deleteAll() {
	operations.keySet().forEach(operation -> delete(operation));
    }

    @Override
    public Stream<ScheduledOperationInfo> getOperations() {
	return operations.values().stream().sorted(byName);
    }

    /**
     * Save repository.
     */
    void save() {
	ScheduledOperations operations = scheduledOperationConfigurationMarshaller.map(getOperations());
	ExecutionResult result = executionResultFactory.startExecution("Save scheduled operations");
	scheduledOperationConfigurationMarshaller.save(result, operations);
    }

    /**
     * Load repository.
     * 
     * @param result execution result for capture the outcome of the operation.
     */
    void load(ExecutionResult result) {
	ScheduledOperations modelOperations = scheduledOperationConfigurationMarshaller.load(result);
	modelOperations.getScheduledOperation().forEach(this::create);
    }

}
