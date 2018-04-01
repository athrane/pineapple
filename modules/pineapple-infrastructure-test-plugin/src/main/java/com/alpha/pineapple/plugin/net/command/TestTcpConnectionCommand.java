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

import static com.alpha.pineapple.test.matchers.PineappleMatchers.isArrayEmpty;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.test.Asserter;
import com.alpha.pineapple.test.matchers.InfrastructureMatchers;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which asserts whether a TCP host listens on a specified set of ports.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>host</code> defines the name of the host. The type is
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>ports</code> defines the list of port number. The type is
 * <code>int[]</code>.</li>
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
public class TestTcpConnectionCommand implements Command {

	/**
	 * Socket timeout in milliseconds.
	 */
	static final int SOCKET_TIMEOUT = 1000;

	/**
	 * Key used to identify property in context: Name of the host.
	 */
	public static final String HOST_KEY = "host";

	/**
	 * Key used to identify property in context: Defines the port number.
	 */
	public static final String PORTS_KEY = "ports";

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Port numbers.
	 */
	@Initialize(PORTS_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	int[] ports;

	/**
	 * Host name.
	 */
	@Initialize(HOST_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	String host;

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	/**
	 * Hamcrest Matcher asserter.
	 */
	@Resource
	Asserter asserter;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	public boolean execute(Context context) throws Exception {
		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("ttcc.start"));
		}

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// configure asserter with execution result
		asserter.setExecutionResult(executionResult);

		// run test
		doTest(context);

		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("ttcc.completed"));
		}

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
	@SuppressWarnings("unchecked")
	void doTest(Context context) {
		// create matcher
		Matcher hostIsResolvableMatcher = InfrastructureMatchers.isHostResolvableToIpAddress();

		// test for empty set
		String message = messageProvider.getMessage("ttcc.zero_size_ports_info");
		asserter.assertWithoutCollectingExecutionResult(ports, isArrayEmpty(), message);

		// complete test as successful if sequence is empty
		if (asserter.lastAssertionSucceeded()) {
			asserter.completeTestAsSuccessful(messageProvider, "ttcc.zero_size_ports_succeed", null);
			return;
		}

		// create assertion description
		Object[] args = { host };
		message = messageProvider.getMessage("ttcc.assert_forward_dns_info", args);

		// assert object
		asserter.assertObject(this.host, hostIsResolvableMatcher, message);

		// create matchers
		Matcher tcpConnectionMatcher = InfrastructureMatchers.tcpHostListensOnPort(host);

		// assert ports
		for (int port : this.ports) {

			// create assertion description
			Object[] args2 = { host, Integer.toString(port) };
			message = messageProvider.getMessage("ttcc.assert_host_info", args2);

			// assert object
			asserter.assertObject(new Integer(port), tcpConnectionMatcher, message);
		}

		// compute result
		executionResult.completeAsComputed(messageProvider, "ttcc.succeed", null, "ttcc.failed", null);
	}
}
