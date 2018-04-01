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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.ResultRepository;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.plugin.net.http.HttpConfiguration;
import com.alpha.pineapple.plugin.net.http.HttpConfigurationImpl;
import com.alpha.pineapple.plugin.net.http.HttpInvocationResult;
import com.alpha.pineapple.plugin.net.http.HttpInvocationSequence;
import com.alpha.pineapple.plugin.net.http.HttpInvocationsSet;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSetImpl;
import com.alpha.testutils.ObjectMotherHttpServer;

/**
 * Integration test for the <code>InvokeHttpGetMethodCommand</code> class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.plugin.net-config.xml" })
public class InvokeHttpGetCommandIntegrationTest {

	/**
	 * Don't follow URL.
	 */
	static final boolean DONT_FOLLOW = false;

	/**
	 * First array index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * TCP port for proxy server.
	 */
	static final int PROXY_PORT = 3080;

	/**
	 * TCP port for HTTP server.
	 */
	static final int HTTP_PORT = 3000;

	static final String PROXY_HOST = "trygproxy";

	/**
	 * URL array with single host.
	 */
	static final String[] HTTP_REMOTE_HOST = { "http://127.0.0.1:3000/sniffer" };

	/**
	 * URL array with single unknown host.
	 */
	static final String[] HTTP_UNKOWN_HOST = { "http://127.0.0.1:3000/pineapple" };

	/**
	 * Empty response property set.
	 */
	static final ResponsePropertyInfoSet EMPTY_SET = new ResponsePropertyInfoSetImpl();

	/**
	 * Object under test.
	 */
	@Resource(name = "invokeHttpGetMethodCommand")
	Command command;

	/**
	 * Chain context.
	 */
	Context context;

	/**
	 * HTTP server object mother
	 */
	ObjectMotherHttpServer httpServerMother;

	/**
	 * HTTP server.
	 */
	org.eclipse.jetty.server.Server httpServer;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Mock repository
	 */
	ResultRepository repository;

	/**
	 * HTTP Configuration object.
	 */
	HttpConfiguration httpConfig;

	@Before
	public void setUp() throws Exception {
		// create context
		context = new ContextBase();

		// create HTTP server object mother
		httpServerMother = new ObjectMotherHttpServer();

		// create HTTP server
		httpServer = httpServerMother.createHttpServer(httpServerMother.getHostIPAddress(true), HTTP_PORT);

		// create mock result repository
		repository = EasyMock.createMock(ResultRepository.class);

		// create execution result
		executionResult = new ExecutionResultImpl(repository, "Root result");

		// create HTTP configuration
		httpConfig = new HttpConfigurationImpl();
	}

	@After
	public void tearDown() throws Exception {
		command = null;
		context = null;
		httpServer.stop();
		httpServer.destroy();
		httpServer = null;
		httpServerMother = null;
		httpConfig = null;
	}

	/**
	 * Test HTTP Get can be executed with single URL.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSucceedsWithSingleUrl() {
		try {
			// configure HTTP configuration
			httpConfig.setHttpFollowRedirects(true);

			// setup parameters
			context.put(InvokeHttpGetMethodCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(InvokeHttpGetMethodCommand.URLS_KEY, HTTP_REMOTE_HOST);
			context.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, 1);
			context.put(InvokeHttpGetMethodCommand.RESET_KEY, DONT_FOLLOW);
			context.put(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, EMPTY_SET);
			context.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig);

			// execute command
			command.execute(context);

			// test
			assertNotNull(context.get(InvokeHttpGetMethodCommand.RESULTS_KEY));
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that the command returns a result set .
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSingleUrlReturnsResultSet() {
		try {
			// configure HTTP configuration
			httpConfig.setHttpFollowRedirects(true);

			// setup parameters
			context.put(InvokeHttpGetMethodCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(InvokeHttpGetMethodCommand.URLS_KEY, HTTP_REMOTE_HOST);
			context.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, 1);
			context.put(InvokeHttpGetMethodCommand.RESET_KEY, false);
			context.put(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, EMPTY_SET);
			context.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig);

			// execute command
			command.execute(context);

			// test
			Object result = context.get(InvokeHttpGetMethodCommand.RESULTS_KEY);
			assertTrue(result instanceof HttpInvocationsSet);
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that result set contains a single sequence.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSingleUrlReturnsSingleSequence() {
		try {
			// configure HTTP configuration
			httpConfig.setHttpFollowRedirects(true);

			// setup parameters
			context.put(InvokeHttpGetMethodCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(InvokeHttpGetMethodCommand.URLS_KEY, HTTP_REMOTE_HOST);
			context.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, 1);
			context.put(InvokeHttpGetMethodCommand.RESET_KEY, false);
			context.put(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, EMPTY_SET);
			context.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig);

			// execute command
			command.execute(context);

			// get sequences
			HttpInvocationsSet result = (HttpInvocationsSet) context.get(InvokeHttpGetMethodCommand.RESULTS_KEY);

			// test
			assertNotNull(result.getSequences());
			assertEquals(1, result.getSequences().length);
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that result set contains a single sequence with a response.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSingleUrlReturnsSingleSequenceWithSingleResponse() {
		try {
			// configure HTTP configuration
			httpConfig.setHttpFollowRedirects(true);

			// setup parameters
			context.put(InvokeHttpGetMethodCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(InvokeHttpGetMethodCommand.URLS_KEY, HTTP_REMOTE_HOST);
			context.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, 1);
			context.put(InvokeHttpGetMethodCommand.RESET_KEY, false);
			context.put(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, EMPTY_SET);
			context.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig);

			// execute command
			command.execute(context);

			// get sequence
			HttpInvocationsSet result = (HttpInvocationsSet) context.get(InvokeHttpGetMethodCommand.RESULTS_KEY);
			HttpInvocationSequence[] sequences = result.getSequences();
			HttpInvocationSequence sequence = sequences[FIRST_INDEX];

			// test
			assertNotNull(sequence);
			assertNotNull(sequence.getSequence());
			assertEquals(1, sequence.getSequence().length);
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that invocation of single URL returns HTTP status code 200.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSingleUrlReturnsStatusCode200() {
		try {
			// configure HTTP configuration
			httpConfig.setHttpFollowRedirects(true);

			// create response properties
			ResponsePropertyInfoSet set = new ResponsePropertyInfoSetImpl();
			Matcher<Object> matcher = CoreMatchers.anything();
			set.addProperty("some-property", "method/statusCode", matcher, matcher);

			// setup parameters
			context.put(InvokeHttpGetMethodCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(InvokeHttpGetMethodCommand.URLS_KEY, HTTP_REMOTE_HOST);
			context.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, 1);
			context.put(InvokeHttpGetMethodCommand.RESET_KEY, false);
			context.put(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, set);
			context.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig);

			// execute command
			command.execute(context);

			// get sequence
			HttpInvocationsSet result = (HttpInvocationsSet) context.get(InvokeHttpGetMethodCommand.RESULTS_KEY);
			HttpInvocationSequence[] sequences = result.getSequences();
			HttpInvocationSequence sequence = sequences[FIRST_INDEX];

			// get invocation results
			HttpInvocationResult[] results = sequence.getSequence();
			HttpInvocationResult firstResult = results[FIRST_INDEX];

			// test
			assertTrue(firstResult.containsProperty("some-property"));
			assertEquals(200, firstResult.getProperty("some-property"));
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that invocation of single unknown URI returns HTTP status code 404.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUnknownUrlReturnsStatusCode404() {
		try {
			// configure HTTP configuration
			httpConfig.setHttpFollowRedirects(true);

			// create response properties
			ResponsePropertyInfoSet set = new ResponsePropertyInfoSetImpl();
			Matcher<Object> matcher = CoreMatchers.anything();
			set.addProperty("some-property", "method/statusCode", matcher, matcher);

			// setup parameters
			context.put(InvokeHttpGetMethodCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(InvokeHttpGetMethodCommand.URLS_KEY, HTTP_UNKOWN_HOST);
			context.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, 1);
			context.put(InvokeHttpGetMethodCommand.RESET_KEY, false);
			context.put(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, set);
			context.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig);

			// execute command
			command.execute(context);

			// get sequence
			HttpInvocationsSet result = (HttpInvocationsSet) context.get(InvokeHttpGetMethodCommand.RESULTS_KEY);
			HttpInvocationSequence[] sequences = result.getSequences();
			HttpInvocationSequence sequence = sequences[FIRST_INDEX];

			// get invocation results
			HttpInvocationResult[] results = sequence.getSequence();
			HttpInvocationResult firstResult = results[FIRST_INDEX];

			// test
			assertTrue(firstResult.containsProperty("some-property"));
			assertEquals(404, firstResult.getProperty("some-property"));
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that HTTP status code can be extracted from HTTP invocation result.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanExtractHttpStatusCode() {
		try {
			// configure HTTP configuration
			httpConfig.setHttpFollowRedirects(true);

			// create response properties
			ResponsePropertyInfoSet set = new ResponsePropertyInfoSetImpl();
			Matcher<Object> matcher = CoreMatchers.anything();
			set.addProperty("some-property", "method/statusCode", matcher, matcher);

			// setup parameters
			context.put(InvokeHttpGetMethodCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(InvokeHttpGetMethodCommand.URLS_KEY, HTTP_REMOTE_HOST);
			context.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, 1);
			context.put(InvokeHttpGetMethodCommand.RESET_KEY, false);
			context.put(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, set);
			context.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig);

			// execute command
			command.execute(context);

			// get sequence
			HttpInvocationsSet result = (HttpInvocationsSet) context.get(InvokeHttpGetMethodCommand.RESULTS_KEY);
			HttpInvocationSequence[] sequences = result.getSequences();
			HttpInvocationSequence sequence = sequences[FIRST_INDEX];

			// get invocation results
			HttpInvocationResult[] results = sequence.getSequence();
			HttpInvocationResult firstResult = results[FIRST_INDEX];

			// test
			assertTrue(firstResult.containsProperty("some-property"));
			assertEquals(200, firstResult.getProperty("some-property"));
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that HTTP response headers can be extracted from HTTP invocation result.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanExtractHttpResponseHeaders() {
		try {
			// configure HTTP configuration
			httpConfig.setHttpFollowRedirects(true);

			// create response properties
			ResponsePropertyInfoSet set = new ResponsePropertyInfoSetImpl();
			Matcher<Object> matcher = CoreMatchers.anything();
			set.addProperty("some-property", "method/responseHeaders", matcher, matcher);

			// setup parameters
			context.put(InvokeHttpGetMethodCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(InvokeHttpGetMethodCommand.URLS_KEY, HTTP_REMOTE_HOST);
			context.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, 1);
			context.put(InvokeHttpGetMethodCommand.RESET_KEY, false);
			context.put(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, set);
			context.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig);

			// execute command
			command.execute(context);

			// get sequence
			HttpInvocationsSet result = (HttpInvocationsSet) context.get(InvokeHttpGetMethodCommand.RESULTS_KEY);
			HttpInvocationSequence[] sequences = result.getSequences();
			HttpInvocationSequence sequence = sequences[FIRST_INDEX];

			// get invocation results
			HttpInvocationResult[] results = sequence.getSequence();
			HttpInvocationResult firstResult = results[FIRST_INDEX];

			// test
			assertTrue(firstResult.containsProperty("some-property"));
			Object propertyValue = firstResult.getProperty("some-property");
			Matcher testMatcher = CoreMatchers.is(Header[].class);
			assertThat(propertyValue, testMatcher);
			Header[] headers = (Header[]) propertyValue;
			assertEquals(7, headers.length); // arbitrary number of headers

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that cookies can be extracted from HTTP client state.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanExtractCookies() {
		try {
			// configure HTTP configuration
			httpConfig.setHttpFollowRedirects(true);

			// create response properties
			ResponsePropertyInfoSet set = new ResponsePropertyInfoSetImpl();
			Matcher<Object> matcher = CoreMatchers.anything();
			set.addProperty("some-property", "state/cookies", matcher, matcher);

			// setup parameters
			context.put(InvokeHttpGetMethodCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(InvokeHttpGetMethodCommand.URLS_KEY, HTTP_REMOTE_HOST);
			context.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, 1);
			context.put(InvokeHttpGetMethodCommand.RESET_KEY, false);
			context.put(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, set);
			context.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig);

			// execute command
			command.execute(context);

			// get sequence
			HttpInvocationsSet result = (HttpInvocationsSet) context.get(InvokeHttpGetMethodCommand.RESULTS_KEY);
			HttpInvocationSequence[] sequences = result.getSequences();
			HttpInvocationSequence sequence = sequences[FIRST_INDEX];

			// get invocation results
			HttpInvocationResult[] results = sequence.getSequence();
			HttpInvocationResult firstResult = results[FIRST_INDEX];

			// test
			assertTrue(firstResult.containsProperty("some-property"));
			Object propertyValue = firstResult.getProperty("some-property");
			Matcher testMatcher = CoreMatchers.is(Cookie[].class);
			assertThat(propertyValue, testMatcher);
			Cookie[] cookies = (Cookie[]) propertyValue;
			assertEquals(1, cookies.length); // arbitrary number of cookies

		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that property can be extracted from HTTP Response body.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanExtractBodyProperty() {
		try {

			// configure HTTP configuration
			httpConfig.setHttpFollowRedirects(true);

			// create response properties
			ResponsePropertyInfoSet set = new ResponsePropertyInfoSetImpl();
			Matcher<Object> matcher = CoreMatchers.anything();
			set.addProperty("some-property", "body/html/body/system-properties/java.version", matcher, matcher);

			// setup parameters
			context.put(InvokeHttpGetMethodCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(InvokeHttpGetMethodCommand.URLS_KEY, HTTP_REMOTE_HOST);
			context.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, 1);
			context.put(InvokeHttpGetMethodCommand.RESET_KEY, false);
			context.put(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, set);
			context.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig);

			// execute command
			command.execute(context);

			// get sequence
			HttpInvocationsSet result = (HttpInvocationsSet) context.get(InvokeHttpGetMethodCommand.RESULTS_KEY);
			HttpInvocationSequence[] sequences = result.getSequences();
			HttpInvocationSequence sequence = sequences[FIRST_INDEX];

			// get invocation results
			HttpInvocationResult[] results = sequence.getSequence();
			HttpInvocationResult firstResult = results[FIRST_INDEX];

			// test
			assertTrue(firstResult.containsProperty("some-property"));
			Object propertyValue = firstResult.getProperty("some-property");
			Matcher testMatcher = CoreMatchers.is(String.class);
			assertThat(propertyValue, testMatcher);
			assertEquals("1.7.0_45", (String) propertyValue);
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

	/**
	 * Test that the execution result has correct state after successful execution.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testExecutionResultHasCorrectStateAfterSuccessfulExecution() {
		try {
			// configure HTTP configuration
			httpConfig.setHttpFollowRedirects(true);

			// setup parameters
			context.put(InvokeHttpGetMethodCommand.EXECUTIONRESULT_KEY, executionResult);
			context.put(InvokeHttpGetMethodCommand.URLS_KEY, HTTP_REMOTE_HOST);
			context.put(InvokeHttpGetMethodCommand.REQUESTS_KEY, 1);
			context.put(InvokeHttpGetMethodCommand.RESET_KEY, false);
			context.put(InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, EMPTY_SET);
			context.put(InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig);

			// execute command
			command.execute(context);

			// test
			assertEquals(ExecutionState.SUCCESS, executionResult.getState());
			assertEquals(0, executionResult.getChildren().length);
			assertEquals(5, executionResult.getMessages().size());
		} catch (Exception e) {
			fail(StackTraceHelper.getStrackTrace(e));
		}
	}

}
