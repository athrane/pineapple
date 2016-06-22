/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2016 Allan Thrane Andersen.
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

package com.alpha.pineapple.command;

import static com.alpha.pineapple.CoreConstants.MSG_TRIGGER_RESOLUTION;

import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.admin.Administration;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.OperationTask;
import com.alpha.pineapple.execution.ResultRepository;
import com.alpha.pineapple.execution.trigger.OperationTriggerResolver;
import com.alpha.pineapple.execution.trigger.ResultTriggerResolver;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.module.model.AggregatedModel;
import com.alpha.pineapple.model.module.model.Trigger;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which implements execution of triggers defined in a aggregated module model.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * 
 * <li><code>aggregated-model</code> contains the aggregated model where the
 * triggers are defined. The type is
 * <code>import com.alpha.pineapple.model.module.model.AggregatedModel</code>.
 * </li>
 * 
 * <li><code>model-result</code> contains the execution result object for
 * aggregated module model where the triggers are defined. The type is
 * <code>com.alpha.pineapple.execution.ExecutionResult</code>.</li>
 * 
 * <li><code>execution-info</code> contains information about the operation
 * which should be executed. The type is
 * <code>com.alpha.pineapple.execution.ExecutionInfo</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in
 * the context:
 * 
 * <ul>
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the command succeeded. If the
 * command failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the command fails due to an exception then the exception isn't caught,
 * but passed on the the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * </ul>
 * </p>
 * .
 */
public class InvokeTriggersCommand implements Command {

    /**
     * Key used to identify property in context: Contains contains the execution
     * result object for aggregated module model where the triggers are defined.
     */
    public static final String MODEL_RESULT_KEY = "model-result";

    /**
     * Key used to identify property in context: Contains the aggregated model
     * where the triggers are defined
     */
    public static final String AGGREGATED_MODEL_KEY = "aggregated-model";

    /**
     * Key used to identify property in context: The execution info which
     * contains information about the operation which should be executed.
     */
    public static final String EXECUTION_INFO_KEY = CoreConstants.EXECUTION_INFO_KEY;

    /**
     * Key used to identify property in context: Contains execution result
     * object,.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Trigger resolver.
     */
    @Resource
    ResultTriggerResolver resultTriggerResolver;

    /**
     * Trigger resolver.
     */
    @Resource
    OperationTriggerResolver operationTriggerResolver;

    /**
     * Result repository.
     */
    @Resource
    ResultRepository resultRepository;

    /**
     * Administration provider.
     */
    @Resource
    Administration administrationProvider;

    /**
     * Aggregated model result object.
     */
    @Initialize(MODEL_RESULT_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    ExecutionResult modelResult;

    /**
     * Aggregated model.
     */
    @Initialize(AGGREGATED_MODEL_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    AggregatedModel model;

    /**
     * Execution info.
     */
    @Initialize(EXECUTION_INFO_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    ExecutionInfo executionInfo;

    /**
     * Defines execution result object.
     */
    @Initialize(EXECUTIONRESULT_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    ExecutionResult executionResult;

    public boolean execute(Context context) throws Exception {

	// initialize command
	CommandInitializer initializer = new CommandInitializerImpl();
	initializer.initialize(context, this);

	// resolve triggers
	Stream<Trigger> triggers = model.getTrigger().stream();
	Stream<Trigger> resolvedTriggers = resultTriggerResolver.resolve(triggers, modelResult.getState());
	resolvedTriggers = operationTriggerResolver.resolve(resolvedTriggers, executionInfo.getOperation());

	// execute triggers
	resolvedTriggers.forEach(this::executeTrigger);

	// add message if no triggers where executed
	if (executionResult.getNumberOfChildren() == 0) {
	    Object[] args = { modelResult.getState(), executionInfo.getOperation() };
	    String message = messageProvider.getMessage("itc.no_triggers_executed", args);
	    executionResult.addMessage(MSG_TRIGGER_RESOLUTION, message);
	}

	// compute state
	executionResult.completeAsComputed(messageProvider, "itc.succeed", null, "itc.failed", null);

	return Command.CONTINUE_PROCESSING;
    }

    /**
     * Execute trigger resolved from model.
     * 
     * @param trigger
     *            resolved trigger to execute.
     */
    void executeTrigger(Trigger trigger) {

	// get operation task
	OperationTask operationTask = administrationProvider.getOperationTask();

	// create description
	String description = generateTriggerDescription(trigger);

	// declare info objects
	ExecutionInfo executionInfo = null;

	try {

	    // execute trigger
	    executionInfo = operationTask.executeComposite(trigger.getOperation(), trigger.getEnvironment(),
		    trigger.getModule(), description, executionResult);

	} catch (Exception e) {

	    // declare result
	    ExecutionResult triggerResult = null;

	    // if execution info is undefined then add execution result for
	    // trigger to describe the error
	    if (executionInfo == null) {
		triggerResult = executionResult.addChild(description);
	    } else {
		triggerResult = executionInfo.getResult();
	    }

	    // terminate execute with execution
	    Object[] args = { e.getMessage() };
	    triggerResult.completeAsError(messageProvider, "itc.error", args, e);
	}
    }

    /**
     * Generate trigger description.
     * 
     * @param trigger
     *            trigger to invoke.
     * @return trigger description.
     */
    String generateTriggerDescription(Trigger trigger) {
	if (trigger.getName() != null) {
	    Object[] args = { trigger.getName() };
	    return messageProvider.getMessage("itc.named_trigger_info", args);
	}
	Object[] args = { trigger.getModule(), trigger.getEnvironment(), trigger.getOperation() };
	return messageProvider.getMessage("itc.unnamed_trigger_info", args);
    }

}
