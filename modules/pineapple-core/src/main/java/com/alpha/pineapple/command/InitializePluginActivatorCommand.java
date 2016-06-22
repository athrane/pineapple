/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.plugin.activation.PluginActivator;
import com.alpha.pineapple.plugin.repository.PluginRuntimeRepository;
import com.alpha.pineapple.resource.ResourceRepository;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which initializes the plugin activator class. The plugin activator is
 * initialized with resource implementation info and a credential provider.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>credential-provider</code> Contains a credential provider instance.
 * The type is <code>com.alpha.pineapple.credential.CredentialProvider</code>.
 * </li>
 * 
 * <li><code>resources</code> Contains a environment configuration object which
 * contains defined resources. The type is
 * <code>com.alpha.pineapple.model.configuration.Configuration</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * 
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the command succeeded. If the
 * command failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the command fails due to an exception then the exception isn't caught,
 * but passed on the the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in
 * the context:
 * 
 * <ul>
 * <li><code>plugin-activator</code> contains initialized plugin activator
 * instance. The type is <code>com.alpha.pineapple.plugin.PluginActivator</code>
 * .</li>
 * </ul>
 * </p>
 */
public class InitializePluginActivatorCommand implements Command {
    /**
     * Key used to identify property in context: Contains execution result
     * object,.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";

    /**
     * Key used to identify property in context: contains credential provider
     * instance.
     */
    public static final String CREDENTIAL_PROVIDER_KEY = "credential-provider";

    /**
     * Key used to identify property in context: contains resources
     * configuration object.
     */
    public static final String RESOURCES_KEY = "resources";

    /**
     * Key used to identify property in context: the plugin activator instance
     * which commands uses to access external resources.
     */
    public static final String PLUGIN_ACTIVATOR_KEY = "plugin-activator";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Plugin repository
     */
    @Resource
    PluginRuntimeRepository pluginRepository;

    /**
     * Resource repository
     */
    @Resource
    ResourceRepository resourceRepository;

    /**
     * plugin activator.
     */
    @Resource
    PluginActivator pluginActivator;

    /**
     * Credential provider.
     */
    @Initialize(CREDENTIAL_PROVIDER_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    CredentialProvider provider;

    /**
     * Environment configuration which contains resources.
     */
    @Initialize(RESOURCES_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    Configuration envConfiguration;

    /**
     * Defines execution result object.
     */
    @Initialize(EXECUTIONRESULT_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    ExecutionResult executionResult;

    @SuppressWarnings("unchecked")
    public boolean execute(Context context) throws Exception {

	// initialize command
	CommandInitializer initializer = new CommandInitializerImpl();
	initializer.initialize(context, this);

	try {
	    // initialize resource repository
	    resourceRepository.initialize(envConfiguration);

	    // get plugin id's
	    String[] pluginIds = resourceRepository.getPluginIds();

	    // initialize plugin repository
	    pluginRepository.initialize(executionResult, pluginIds);

	    // initialize plugin activator
	    pluginActivator.initialize(provider, resourceRepository, pluginRepository);

	    // add plugin activator to context
	    context.put(PLUGIN_ACTIVATOR_KEY, pluginActivator);

	    // compute execution state from children
	    executionResult.completeAsComputed(messageProvider, "ipac.succeed", null, "ipac.failed", null);

	    return Command.CONTINUE_PROCESSING;

	} catch (Exception e) {

	    // complete operation with error
	    executionResult.completeAsError(messageProvider, "ipac.error", e);

	    return Command.CONTINUE_PROCESSING;
	}
    }

}
