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

package com.alpha.pineapple.plugin.net.operation;

import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.command.CommandException;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.infrastructure.model.AccessUncPathTest;
import com.alpha.pineapple.plugin.infrastructure.model.DnsResolutionTest;
import com.alpha.pineapple.plugin.infrastructure.model.FtpServerActiveTest;
import com.alpha.pineapple.plugin.infrastructure.model.FtpServerContainsDirectoryTest;
import com.alpha.pineapple.plugin.infrastructure.model.FtpServerCreateDirectoryTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpConfiguration;
import com.alpha.pineapple.plugin.infrastructure.model.HttpHeaderTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpRedirectTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpStatusCodeTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpTest;
import com.alpha.pineapple.plugin.infrastructure.model.Infrastructure;
import com.alpha.pineapple.plugin.infrastructure.model.LoadBalancingTest;
import com.alpha.pineapple.plugin.infrastructure.model.SessionStickynessTest;
import com.alpha.pineapple.plugin.infrastructure.model.TcpConnectionTest;
import com.alpha.pineapple.plugin.net.model.Mapper;
import com.alpha.pineapple.session.Session;

@PluginOperation(OperationNames.TEST)
public class TestOperation implements Operation {
	/**
	 * Null server array
	 */
	static final String[] NULL_SERVER_ARRAY = { "null" };

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * DNS resolution test command.
	 */
	@Resource
	Command testDnsResolutionCommand;

	/**
	 * DNS forward resolution test command.
	 */
	@Resource
	Command testDnsForwardResolutionCommand;

	/**
	 * DNS reverse resolution test command.
	 */
	@Resource
	Command testDnsReverseResolutionCommand;

	/**
	 * UNC path test command.
	 */
	@Resource
	Command testUncPathCommand;

	/**
	 * TCP connection test command.
	 */
	@Resource
	Command testTcpConnectionCommand;

	/**
	 * HTTP test command.
	 */
	@Resource
	Command testHttpCommand;

	/**
	 * Command runner
	 */
	@Resource
	CommandRunner commandRunner;

	/**
	 * Model mapper object.
	 */
	@Resource
	Mapper mapper;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	public void execute(Object content, Session session, ExecutionResult executionResult)
			throws PluginExecutionFailedException {
		// validate parameters
		Validate.notNull(content, "content is undefined.");
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(executionResult, "executionResult is undefined.");

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { content.getClass().getName(), content };
			String message = messageProvider.getMessage("to.start", args);
			logger.debug(message);
		}

		// throw exception if required type isn't available
		if (!(content instanceof Infrastructure)) {
			Object[] args = { Infrastructure.class };
			String message = messageProvider.getMessage("to.content_typecast_failed", args);
			throw new PluginExecutionFailedException(message);
		}

		// configure command runner with execution result
		commandRunner.setExecutionResult(executionResult);

		try {
			// type cast to infrastructure model object
			Infrastructure model = (Infrastructure) content;

			// put HTTP configurations into a map
			HashMap<String, HttpConfiguration> httpConfigMap = new HashMap<String, HttpConfiguration>();
			for (HttpConfiguration config : model.getHttpConfiguration()) {
				httpConfigMap.put(config.getId(), config);
			}

			// run tests
			runTcpConnectionTests(model);
			runForwardDnsResolutionTests(model);
			runReverseDnsResolutionTests(model);
			runDnsResolutionTests(model);
			runHttpTests(model, httpConfigMap);
			runStickynessTests(model, httpConfigMap);
			runLoadBalancingTests(model, httpConfigMap);
			runHttpStatusCodeTests(model, httpConfigMap);
			runHttpRedirectTests(model, httpConfigMap);
			runHttpHeaderTests(model, httpConfigMap);
			runFtpServerActiveTests(model);
			runFtpServerContainsDirectoryTests(model);
			runFtpServerCreateDirectoryTests(model);
			runAccessUncPathTests(model);

			// compute execution state from children
			executionResult.completeAsComputed(messageProvider, "to.completed", null, "to.failed", null);
		} catch (Exception e) {
			Object[] args = { StackTraceHelper.getStrackTrace(e) };
			String message = messageProvider.getMessage("to.error", args);
			throw new PluginExecutionFailedException(message, e);
		}
	}

	void runHttpTests(Infrastructure model, HashMap<String, HttpConfiguration> httpConfigMap) throws CommandException {
		for (HttpTest test : model.getHttpTest()) {
			// create description
			Object[] args = { test.getDescription() };
			String message = messageProvider.getMessage("to.http_info", args);

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapHttpTest(test, context, httpConfigMap);

			// run test
			commandRunner.run(testHttpCommand, message, context);
		}
	}

	void runStickynessTests(Infrastructure model, HashMap<String, HttpConfiguration> httpConfigMap)
			throws CommandException {
		for (SessionStickynessTest test : model.getSessionStickynessTest()) {
			// create description
			Object[] args = { test.getDescription() };
			String message = messageProvider.getMessage("to.stickyness_info", args);

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapStickynessTest(test, context, httpConfigMap);

			// run test
			commandRunner.run(testHttpCommand, message, context);
		}
	}

	void runLoadBalancingTests(Infrastructure model, HashMap<String, HttpConfiguration> httpConfigMap)
			throws CommandException {
		for (LoadBalancingTest test : model.getLoadBalancingTest()) {
			// create description
			Object[] args = { test.getDescription() };
			String message = messageProvider.getMessage("to.loadbalancing_info", args);

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapLoadBalancingTest(test, context, httpConfigMap);

			// run test
			commandRunner.run(testHttpCommand, message, context);
		}
	}

	void runHttpStatusCodeTests(Infrastructure model, HashMap<String, HttpConfiguration> httpConfigMap)
			throws CommandException {
		for (HttpStatusCodeTest test : model.getHttpStatuscodeTest()) {
			// create description
			Object[] args = { test.getDescription() };
			String message = messageProvider.getMessage("to.statuscode_info", args);

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapHttpStatusCodeTest(test, context, httpConfigMap);

			// run test
			commandRunner.run(testHttpCommand, message, context);
		}
	}

	void runHttpRedirectTests(Infrastructure model, HashMap<String, HttpConfiguration> httpConfigMap)
			throws CommandException {
		for (HttpRedirectTest test : model.getHttpRedirectTest()) {
			// create description
			Object[] args = { test.getDescription() };
			String message = messageProvider.getMessage("to.redirect_info", args);

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapHttpRedirectTest(test, context, httpConfigMap);

			// run test
			commandRunner.run(testHttpCommand, message, context);
		}
	}

	void runHttpHeaderTests(Infrastructure model, HashMap<String, HttpConfiguration> httpConfigMap)
			throws CommandException {

		for (HttpHeaderTest test : model.getHttpHeaderTest()) {
			// create description
			Object[] args = { test.getDescription() };
			String message = messageProvider.getMessage("to.header_info", args);

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapHttpHeaderTest(test, context, httpConfigMap);

			// run test
			commandRunner.run(testHttpCommand, message, context);
		}
	}

	void runDnsResolutionTests(Infrastructure model) throws CommandException {
		for (DnsResolutionTest test : model.getDnsResolutionTest()) {
			// create description
			Object[] args = { test.getDescription() };
			String message = messageProvider.getMessage("to.resolve_dns_info", args);

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapDnsResolutionTest(test, context);

			// run test
			commandRunner.run(testDnsResolutionCommand, message, context);
		}
	}

	void runForwardDnsResolutionTests(Infrastructure model) throws CommandException {
		for (DnsResolutionTest test : model.getDnsForwardResolutionTest()) {
			// create description
			Object[] args = { test.getDescription() };
			String message = messageProvider.getMessage("to.resolve_dns_info", args);

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapDnsForwardResolutionTest(test, context);

			// run test
			commandRunner.run(testDnsForwardResolutionCommand, message, context);
		}
	}

	void runReverseDnsResolutionTests(Infrastructure model) throws CommandException {
		for (DnsResolutionTest test : model.getDnsReverseResolutionTest()) {
			// create description
			Object[] args = { test.getDescription() };
			String message = messageProvider.getMessage("to.resolve_dns_info", args);

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapDnsReverseResolutionTest(test, context);

			// run test
			commandRunner.run(testDnsReverseResolutionCommand, message, context);
		}
	}

	void runAccessUncPathTests(Infrastructure model) throws CommandException {

		for (AccessUncPathTest test : model.getAccessUncPathTest()) {
			// create description
			StringBuilder description = new StringBuilder();
			description.append("Access UNC path test [");
			description.append(test.getDescription());
			description.append("]");

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapAccessUncPathTest(test, context);

			// run test
			commandRunner.run(testUncPathCommand, description.toString(), context);
		}
	}

	void runFtpServerActiveTests(Infrastructure model) throws CommandException {

		for (FtpServerActiveTest test : model.getFtpServerActiveTest()) {
			// create description
			StringBuilder description = new StringBuilder();
			description.append("FTP server is active test [");
			description.append(test.getDescription());
			description.append("]");

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapFtpServerActiveTest(test, context);

			// run test
			// commandRunner.run(testResolveNameToIpAddressCommand, description.toString(),
			// context);
		}
	}

	void runFtpServerContainsDirectoryTests(Infrastructure model) throws CommandException {

		for (FtpServerContainsDirectoryTest test : model.getFtpServerContainsDirectoryTest()) {
			// create description
			StringBuilder description = new StringBuilder();
			description.append("FTP server contains directory test [");
			description.append(test.getDescription());
			description.append("]");

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapFtpServerContainsDirectoryTest(test, context);

			// run test
			// commandRunner.run(testResolveNameToIpAddressCommand, description.toString(),
			// context);
		}
	}

	void runFtpServerCreateDirectoryTests(Infrastructure model) throws CommandException {

		for (FtpServerCreateDirectoryTest test : model.getFtpServerCreateDirectoryTest()) {
			// create description
			StringBuilder description = new StringBuilder();
			description.append("FTP server create/delete test [");
			description.append(test.getDescription());
			description.append("]");

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapFtpServerCreateDirectoryTest(test, context);

			// run test
			// commandRunner.run(testResolveNameToIpAddressCommand, description.toString(),
			// context);
		}
	}

	void runTcpConnectionTests(Infrastructure model) throws CommandException {
		for (TcpConnectionTest test : model.getTcpConnectionTest()) {
			// create description
			Object[] args = { test.getDescription() };
			String message = messageProvider.getMessage("to.tcp_connection_info", args);

			// create context
			Context context = commandRunner.createContext();

			// map model content to context
			mapper.mapTcpConnectionTest(test, context);

			// run test
			commandRunner.run(testTcpConnectionCommand, message, context);
		}
	}

}
