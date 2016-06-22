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

package com.alpha.pineapple.plugin.agent.command;

import static com.alpha.pineapple.plugin.agent.AgentConstants.SCHEDULE_OPERATION_URI;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.agent.session.AgentSession;
import com.alpha.pineapple.plugin.agent.utils.RestResponseException;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which schedules an operation at Pineapple using the REST API.
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>name</code> defines the name of the scheduled operation. The type is
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>module</code> defines the name of the module scheduled for
 * execution. The type is <code>java.lang.String</code>.</li>
 * 
 * <li><code>environment</code> defines the name of the target environment. The
 * type is <code>java.lang.String</code>.</li>
 * 
 * <li><code>operation</code> defines the name of the operation. The type is
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>scheduling-expression</code> defines the scheduling expression
 * using Cron syntax. The type is <code>java.lang.String</code>.</li>
 * 
 * <li><code>description</code> defines a description of scheduled operation.
 * The type is <code>java.lang.String</code>.</li>
 * 
 * <li><code>session</code> defines the agent session used communicate with an
 * agent. The type is
 * <code>com.alpha.pineapple.plugin.agent.session.AgentSession</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is:
 * 
 * <ul>
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the test succeeded. If the test
 * failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the test fails due to an exception then the exception isn't caught,
 * but passed on the the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * </ul>
 * </p>
 */
public class ScheduleOperationCommand implements Command {

    /**
     * Key used to identify property in context: Name of the scheduled operation.
     */
    public static final String NAME_KEY = "name";

    /**
     * Key used to identify property in context: Module.
     */
    public static final String MODULE_KEY = "module";

    /**
     * Key used to identify property in context: Environment.
     */
    public static final String ENVIRONMENT_KEY = "environment";

    /**
     * Key used to identify property in context: Operation.
     */
    public static final String OPERATION_KEY = "operation";

    /**
     * Key used to identify property in context: Scheduling expression using
     * Cron syntax.
     */
    public static final String SCHEDULING_EXPRESSION_KEY = "scheduling-expression";

    /**
     * Key used to identify property in context: Description.
     */
    public static final String DESCRIPTION_KEY = "description";

    /**
     * Key used to identify property in context: plugin session object.
     */
    public static final String SESSION_KEY = "session";

    /**
     * Key used to identify property in context: Contains execution result
     * object,.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";

    /**
     * Scheduled operation name.
     */
    @Initialize(NAME_KEY)
    @ValidateValue(ValidationPolicy.NOT_EMPTY)
    String name;

    /**
     * Scheduled module.
     */
    @Initialize(MODULE_KEY)
    @ValidateValue(ValidationPolicy.NOT_EMPTY)
    String module;

    /**
     * Environment name.
     */
    @Initialize(ENVIRONMENT_KEY)
    @ValidateValue(ValidationPolicy.NOT_EMPTY)
    String environment;

    /**
     * Operation.
     */
    @Initialize(OPERATION_KEY)
    @ValidateValue(ValidationPolicy.NOT_EMPTY)
    String operation;

    /**
     * Scheduling expression.
     */
    @Initialize(SCHEDULING_EXPRESSION_KEY)
    @ValidateValue(ValidationPolicy.NOT_EMPTY)
    String schedulingExpression;

    /**
     * Description.
     */
    @Initialize(DESCRIPTION_KEY)
    @ValidateValue(ValidationPolicy.NOT_EMPTY)
    String description;

    /**
     * Plugin session.
     */
    @Initialize(SESSION_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    AgentSession session;

    /**
     * Defines execution result object.
     */
    @Initialize(EXECUTIONRESULT_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    ExecutionResult executionResult;

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    public boolean execute(Context context) throws Exception {
	// initialize command
	CommandInitializer initializer = new CommandInitializerImpl();
	initializer.initialize(context, this);

	try {

	    UriComponents uriComponents = UriComponentsBuilder.fromUriString(SCHEDULE_OPERATION_URI).build()
		    .expand(name, module, environment, operation, schedulingExpression, description).encode();
	    String serviceUrl = session.createServiceUrl(uriComponents.toUriString());
	    session.addServiceUrlMessage(serviceUrl, executionResult);

	    // create URL variables
	    MultiValueMap<String, Object> urlVariables = new LinkedMultiValueMap<String, Object>();

	    // post
	    session.httpPost(serviceUrl, urlVariables);

	    // complete result
	    executionResult.completeAsSuccessful(messageProvider, "soc.schedule_operation_completed");

	    return Command.CONTINUE_PROCESSING;

	} catch (RestResponseException e) {
	    executionResult.addMessage("HTTP Headers", e.getHeaders().toString());
	    executionResult.addMessage("HTTP Status Code", e.getStatusCode().toString());
	    executionResult.addMessage("HTTP Body", e.getBody());
	    executionResult.completeAsError(messageProvider, "soc.error", e);
	    return Command.CONTINUE_PROCESSING;


	} catch (Exception e) {
	    executionResult.completeAsError(messageProvider, "soc.error", e);
	    return Command.CONTINUE_PROCESSING;
	}

    }

}
