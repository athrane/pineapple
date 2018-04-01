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

package com.alpha.pineapple.plugin.net.command;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.net.http.HttpConfiguration;
import com.alpha.pineapple.plugin.net.http.HttpInvocationsSet;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which which tests the behavior of HTTP host(s) using the HTTP Get operation.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>urls</code> defines the URL sequence which should be accessed. The
 * type is <code>java.lang.String[]</code>.</li>
 * 
 * <li><code>requests</code> defines the number of times the URL which be
 * invoked. The type is <code>int</code>.</li>
 * 
 * <li><code>reset</code> defines if HTTP session should reset after each
 * sequence invocation. The type is <code>boolean</code>.</li>
 * 
 * <li><code>http-configuration</code> contains configuration of the HTTP client
 * with settings for TCP, HTTP and proxy server. The type is
 * <code>com.alpha.pineapple.plugin.net.http.HttpConfiguration</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is:
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
public class TestHttpCommand implements Command {
	/**
	 * Key used to identify property in context: Defines the URL sequence which
	 * should be accessed.
	 */
	public static final String URLS_KEY = "urls";

	/**
	 * Key used to identify property in context: Defines the number of times the URL
	 * which be invoked.
	 */
	public static final String REQUESTS_KEY = "requests";

	/**
	 * Key used to identify property in context: Defines if HTTP session should
	 * reset after each sequence invocation.
	 */
	public static final String RESET_KEY = "reset";

	/**
	 * Key used to identify property in context: response property info set.
	 */
	public static final String RESPONSEPROPERTIES_SET_KEY = "response-property-info-set";

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Key used to identify property in context: Contains an HttpConfiguration
	 * containing the configuration of the HTTP client.
	 */
	public static final String HTTPCONFIGURATION_KEY = "http-configuration";

	/**
	 * First array index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * URL sequence which should be accessed.
	 */
	@Initialize(URLS_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	String[] urls;

	/**
	 * The number of times the URL sequence should be requested.
	 */
	@Initialize(REQUESTS_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	int requests;

	/**
	 * Defines if HTTP session should reset after each sequence invocation
	 */
	@Initialize(RESET_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	boolean reset;

	/**
	 * The response properties set.
	 */
	@Initialize(RESPONSEPROPERTIES_SET_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	ResponsePropertyInfoSet propertyInfoSet;

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	/**
	 * The HTTP configuration.
	 */
	@Initialize(HTTPCONFIGURATION_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	HttpConfiguration httpConfiguration;

	/**
	 * Test response properties command.
	 */
	@Resource
	Command testResponsePropertiesCommand;

	/**
	 * Invoke HTTP Get command.
	 */
	@Resource
	Command invokeHttpGetMethodCommand;

	/**
	 * Command runner
	 */
	@Resource
	CommandRunner runner;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	public boolean execute(Context context) throws Exception {
		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("thc.start"));
		}

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// configure command runner with execution result
		runner.setExecutionResult(executionResult);

		// run test
		doTest(context);

		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("thc.completed"));
		}

		// set successful result
		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Do test.
	 * 
	 * @param context
	 *            Command context.
	 * 
	 * @throws Exception
	 *             If test execution fails.
	 */
	void doTest(Context context) throws Exception {
		// invoke HTTP get method
		HttpInvocationsSet results = invokeHttpGet();

		// test response properties
		runResponsePropertiesTest(results);

		// compute result
		executionResult.completeAsComputed(messageProvider, "thc.succeed", null, "thc.failed", null);
	}

	/**
	 * Invoke HTTP Get Method.
	 * 
	 * @return HTTP invocation result set.
	 */
	@SuppressWarnings("unchecked")
	HttpInvocationsSet invokeHttpGet() {

		// create context
		Context context = runner.createContext();

		// add parameters to context
		context.put(InvokeHttpGetMethodCommand.URLS_KEY, this.urls);
		context.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, this.requests);
		context.put(InvokeHttpGetMethodCommand.RESET_KEY, this.reset);
		context.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfiguration);
		context.put(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, propertyInfoSet);

		// create description
		String description = messageProvider.getMessage("thc.invoke_http_get");

		// run test
		runner.run(invokeHttpGetMethodCommand, description, context);

		// get command result
		HttpInvocationsSet results;
		results = (HttpInvocationsSet) context.get(InvokeHttpGetMethodCommand.RESULTS_KEY);

		return results;
	}

	/**
	 * Run response properties test.
	 * 
	 * @param results
	 *            HTTP invocation result set.
	 */
	@SuppressWarnings("unchecked")
	void runResponsePropertiesTest(HttpInvocationsSet results) {
		// create context
		Context context = runner.createContext();

		// setup context
		context.put(TestResponsePropertiesCommand.INVOCATIONRESULTS_SET_KEY, results);
		context.put(TestResponsePropertiesCommand.RESPONSEPROPERTIES_SET_KEY, propertyInfoSet);

		// create description
		String description = messageProvider.getMessage("thc.invoke_responseprops_test");

		// run test
		runner.run(testResponsePropertiesCommand, description, context);
	}

}
