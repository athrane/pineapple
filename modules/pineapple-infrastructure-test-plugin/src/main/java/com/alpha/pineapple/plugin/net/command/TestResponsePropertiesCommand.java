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

import static com.alpha.pineapple.test.matchers.InfrastructureMatchers.isSetEmpty;

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
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.net.http.HttpInvocationResult;
import com.alpha.pineapple.plugin.net.http.HttpInvocationSequence;
import com.alpha.pineapple.plugin.net.http.HttpInvocationsSet;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfo;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet;
import com.alpha.pineapple.test.Asserter;

/**
 * <p>
 * Implementation of the <code>com.alpha.pineapple.command.test.Command</code>
 * interface which which asserts a {@link ResponsePropertyInfoSet} against the
 * content of a {@link HttpInvocationsSet}.
 * </p>
 * 
 * <p>
 * Each {@link ResponsePropertyInfo} can contains two assertions which are
 * tested against the {@link HttpInvocationResult } objects contained in the
 * {@link HttpInvocationsSet}.
 * 
 * <p>
 * If the HTTP invocation set is empty or contains a single sequence, then the
 * test is successful by default.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>execution-result</code> contains execution result object which is
 * used to report the result of the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * 
 * <li><code>invocation-result-set</code> HTTP invocation result set which the
 * test is run against. The type is <code>HttpInvocationsSet</code>.</li>
 * 
 * <li><code>response-property-info-set</code> defines a collection of response
 * properties which which are asserted against the content of the
 * {@link HttpInvocationsSet}. The type is
 * <code>com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet</code>.</li>
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
public class TestResponsePropertiesCommand implements Command {

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Key used to identify property in context: invocation result set which is
	 * tested.
	 */
	public static final String INVOCATIONRESULTS_SET_KEY = "invocation-result-set";

	/**
	 * Key used to identify property in context: response property info set.
	 */
	public static final String RESPONSEPROPERTIES_SET_KEY = "response-property-info-set";

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	/**
	 * The invocation result set.
	 */
	@Initialize(INVOCATIONRESULTS_SET_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	HttpInvocationsSet resultSet;

	/**
	 * The response properties set.
	 */
	@Initialize(RESPONSEPROPERTIES_SET_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	ResponsePropertyInfoSet propertyInfoSet;

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
	 * Hamcrest Matcher asserter.
	 */
	@Resource
	Asserter asserter;

	public boolean execute(Context context) throws Exception {
		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("trp.start"));
		}

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// configure asserter with execution result
		asserter.setExecutionResult(executionResult);

		// run test
		doTest();

		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("trp.completed"));
		}

		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Perform the actual test.
	 */
	void doTest() {
		String message;

		// test for empty set
		message = messageProvider.getMessage("trp.zero_size_sequence_info");
		asserter.assertWithoutCollectingExecutionResult(resultSet, isSetEmpty(), message);

		// complete test as successful if sequence is empty
		if (asserter.lastAssertionSucceeded()) {
			asserter.completeTestAsSuccessful(messageProvider, "trp.zero_size_sequence_succeed", null);
			return;
		}

		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("trp.zero_size_set_failed"));
		}

		// test properties
		testProperties();

		// compute execution state from children
		executionResult.setState(ExecutionState.COMPUTED);

		// handle test outcome
		if (executionResult.getState() == ExecutionState.SUCCESS) {
			Object[] args = { resultSet.getSequences().length };
			message = messageProvider.getMessage("trp.succeed", args);
		} else {
			ExecutionResult[] failedTests = executionResult.getChildrenWithState(ExecutionState.FAILURE);
			ExecutionResult[] errorTests = executionResult.getChildrenWithState(ExecutionState.ERROR);
			Object[] args = { failedTests.length, errorTests.length };
			message = messageProvider.getMessage("trp.failed", args);
		}

		// store the message
		executionResult.addMessage("Message", message);
	}

	/**
	 * Test defined properties.
	 */
	void testProperties() {

		// get properties
		ResponsePropertyInfo[] properties = propertyInfoSet.getProperties();

		// iterate over the response properties
		for (ResponsePropertyInfo propertyInfo : properties) {
			testIntraSequence(propertyInfo);
			testInterSequence(propertyInfo);
		}
	}

	/**
	 * 
	 * @param propertyInfo
	 */
	@SuppressWarnings("unchecked")
	void testIntraSequence(ResponsePropertyInfo propertyInfo) {

		// exit if property shouldn't be tested within each sequence
		if (!propertyInfo.isTestingIntraSequence())
			return;

		// iterate over the result sequences
		for (HttpInvocationSequence sequence : resultSet.getSequences()) {
			// create sequence iterator
			Iterable<Object> actual = sequence.getIntraSequencePropertyIterator(propertyInfo);

			// create assertion description
			Object[] args = { propertyInfo.getName(), propertyInfo.getIntraSequenceMatcher(), sequence.getName() };
			String message = messageProvider.getMessage("trp.intra_sequence_info", args);

			// get matcher
			Matcher matcher = propertyInfo.getIntraSequenceMatcher();

			// assert object
			asserter.assertObject(actual, matcher, message);
		}
	}

	/**
	 * 
	 * @param propertyInfo
	 */
	@SuppressWarnings("unchecked")
	void testInterSequence(ResponsePropertyInfo propertyInfo) {

		// exit if property shouldn't be tested across sequences
		if (!propertyInfo.isTestingInterSequences())
			return;

		// create sequence iterator
		Iterable<Object> actual = resultSet.getInterSequencePropertyIterator(propertyInfo);

		String name = "Sequence generated from first result from each sequence";
		// create assertion description
		Object[] args = { propertyInfo.getName(), propertyInfo.getInterSequenceMatcher(), name };
		String message = messageProvider.getMessage("trp.inter_sequence_info", args);

		// get matcher
		Matcher matcher = propertyInfo.getInterSequenceMatcher();

		// assert object
		asserter.assertObject(actual, matcher, message);
	}

}
