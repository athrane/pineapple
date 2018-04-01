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

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Resource;
import javax.net.ssl.X509TrustManager;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.net.http.HttpConfiguration;
import com.alpha.pineapple.plugin.net.http.HttpInvocationResult;
import com.alpha.pineapple.plugin.net.http.HttpInvocationResultImpl;
import com.alpha.pineapple.plugin.net.http.HttpInvocationSequence;
import com.alpha.pineapple.plugin.net.http.HttpInvocationSequenceImpl;
import com.alpha.pineapple.plugin.net.http.HttpInvocationsSet;
import com.alpha.pineapple.plugin.net.http.HttpInvocationsSetImpl;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfo;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet;
import com.alpha.pineapple.plugin.net.ssl.AcceptingSSLTrustManager;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which invokes an URL sequences with HTTP Get Method a specified number of
 * times. The result is stored in a {@link HttpInvocationsSet} object.
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
 * <li><code>requests</code> defines the number of times the URL sequence should
 * be invoked. The type is <code>int</code>.</li>
 * 
 * <li><code>reset</code> defines if HTTP session should reset after each
 * sequence invocation. The type is <code>boolean</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which is
 * used to report the result of the execution of the command. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * 
 * <li><code>response-property-info-set</code> defines a collection of response
 * properties which defines properties whose values is extracted from each HTTP
 * response and stored in each {@link HttpInvocationResult}. The type is
 * <code>com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in
 * the context:
 * <ul>
 * <li><code>results</code> contains an object containing the results of all the
 * sequences. The type is
 * <code>com.alpha.pineapple.plugin.net.http.HttpInvocationResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is:
 * <ul>
 * <li><code>results</code> contains an object containing the results of all the
 * sequences. The type is <code>HttpInvocationsSet</code>.</li>
 * 
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the test succeeded. If the test
 * failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the test fails due to an exception then the exception isn't caught,
 * but passed on the the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * </ul>
 * </p>
 * 
 * <B>Handling HTTPS connections</B>
 * 
 * <p>
 * The command uses the <A HREF="http://hc.apache.org/httpclient-3.x/"> Apache
 * HTTP CLient</A> as HTTP client. The HTTPClient is configured to used a
 * <code>SecureProtocolSocketFactory</code> implementation which accepts all SSL
 * certificates. The used implementation is {@link EasySSLProtocolSocketFactory}
 * configured with the trust manager {@link AcceptingSSLTrustManager} which
 * accepts all certificate chains. The consequence is that certificate
 * validation is disabled for SSL and all certificates are accepted.
 * </p>
 * 
 */
public class InvokeHttpGetMethodCommand implements Command {

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
	 * Key used to identify property in context: Contains an HttpInvocationsSet
	 * containing the results of the invocations.
	 */
	public static final String RESULTS_KEY = "results";

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
	 * Time out in milliseconds.
	 */
	static final int TIMEOUT_MS = 3000;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * XPath object.
	 */
	XPath xpath = XPathFactory.newInstance().newXPath();

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

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
	 * The invocation result set.
	 */
	@Initialize(RESPONSEPROPERTIES_SET_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ResponsePropertyInfoSet propertyInfoSet;

	/**
	 * The HTTP configuration.
	 */
	@Initialize(HTTPCONFIGURATION_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	HttpConfiguration httpConfiguration;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {

		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("ihgc.start"));
		}

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// create result set
		HttpInvocationsSet result = new HttpInvocationsSetImpl(urls, requests, reset, httpConfiguration.getProxyHost(),
				httpConfiguration.getProxyPort());

		// create HTTP client
		HttpClient client = createHttpClient();

		// execute the URL sequences
		for (int i = 0; i < this.requests; i++) {

			// create description
			String description = createURLSequenceDescrption(i);

			// execute a URL sequence
			HttpInvocationSequence sequence = executeURLSequence(description, client);

			// store sequence
			result.appendResult(sequence);

			// if reset flag is enabled, then create new HTTP client for next sequence
			if (this.reset) {
				client = createHttpClient();
			}
		}

		// save result
		context.put(RESULTS_KEY, result);

		// add additional execution info
		addExecutionInfo();

		// set as successful
		Object[] args = { result.getSequences().length, };
		executionResult.completeAsSuccessful(messageProvider, "ihgc.command_succeed", args);

		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("ihgc.completed"));
		}

		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Add additional execution info the execution result.
	 */
	void addExecutionInfo() {

		executionResult.addMessage(messageProvider.getMessage("ihgc.followredirects_info"),
				Boolean.toString(httpConfiguration.getHttpFollowRedirects()));

		executionResult.addMessage(messageProvider.getMessage("ihgc.tcpsockettimeout_info"),
				Integer.toString(httpConfiguration.getTcpSocketTimeout()));

		// create proxy value
		StringBuilder proxyAsString = new StringBuilder();
		if (isProxyDefined()) {
			proxyAsString.append(httpConfiguration.getProxyHost());
			proxyAsString.append(":");
			proxyAsString.append(httpConfiguration.getProxyPort());
		} else {
			proxyAsString.append("n/a");
		}
		executionResult.addMessage(messageProvider.getMessage("ihgc.proxy_info"), proxyAsString.toString());

		// create URL values
		StringBuilder urlsAsString = new StringBuilder();
		for (String url : this.urls) {
			urlsAsString.append(url);
			urlsAsString.append("\n");
		}
		executionResult.addMessage(messageProvider.getMessage("ihgc.urls_info"), urlsAsString.toString());
	}

	/**
	 * Create URL sequence description.
	 * 
	 * @param number
	 *            Sequence number.
	 * 
	 * @return URL sequence description.
	 */
	String createURLSequenceDescrption(int i) {
		StringBuilder message = new StringBuilder();
		message.append("Sequence");
		message.append(i + 1);
		return message.toString();
	}

	/**
	 * Execute a single URL sequence.
	 * 
	 * @param description
	 *            Description of the URL sequence.
	 * @param client
	 *            HTTP client
	 * 
	 * @return The result in a HTTP invocation sequence object.
	 * @throws Exception
	 *             if HTTP client creation fails.
	 */
	HttpInvocationSequence executeURLSequence(String description, HttpClient client) throws Exception {
		// create sequence
		HttpInvocationSequence sequence = new HttpInvocationSequenceImpl(description);

		// define count for logging purposes
		int counter = 0;

		for (String url : this.urls) {
			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Executing request <");
				message.append(description);
				message.append(":");
				message.append(counter + 1);
				message.append("/");
				message.append(this.urls.length);
				message.append("> with URL <");
				message.append(url);
				message.append(">.");
				logger.debug(message.toString());
			}

			// invoke the URL
			HttpInvocationResult invocationResult = invokeURL(client, url);

			// add invocation result to sequence
			sequence.appendResult(invocationResult);
		}

		return sequence;

	}

	HttpInvocationResult invokeURL(HttpClient client, String url) {
		// declare method
		HttpMethod method = null;

		try {
			// create get method
			method = new GetMethod(url);

			// set follow-redirect settings
			method.setFollowRedirects(httpConfiguration.getHttpFollowRedirects());

			// set socket time
			method.getParams().setSoTimeout(httpConfiguration.getTcpSocketTimeout());

			// execute HTTP get
			executeGetMethod(client, method, url);

			// get response content
			HttpInvocationResult invocationResult = getResponseContent(client, method);

			return invocationResult;

		} catch (Exception e) {
			// log error message
			StringBuilder message = new StringBuilder();
			message.append("Invoking URL failed with exception <");
			message.append(StackTraceHelper.getStrackTrace(e));
			message.append(">. Creating null result. ");
			logger.error(message.toString());

			// create null result
			HttpInvocationResult invocationResult = new HttpInvocationResultImpl();
			return invocationResult;

		} finally {

			if (method != null) {

				// clean up
				method.releaseConnection();
				method = null;
			}
		}
	}

	/**
	 * Create HTTP client object.
	 * 
	 * @return HTTP client object.
	 * @throws Exception
	 *             If SSL initialization fails.
	 */
	HttpClient createHttpClient() throws NoSuchAlgorithmException, Exception {
		// setup SSL
		initializeSSL();

		// create HTTP client
		HttpClient client = new HttpClient();

		// set time out
		client.getHttpConnectionManager().getParams().setConnectionTimeout(httpConfiguration.getTcpSocketTimeout());

		// set cookie policy
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

		// set strict adherence to HTTP protocol specification
		client.getParams().makeStrict();

		// set proxy settings
		if (isProxyDefined()) {
			HostConfiguration hostConfig = client.getHostConfiguration();
			ProxyHost proxy = new ProxyHost(httpConfiguration.getProxyHost(), httpConfiguration.getProxyPort());
			hostConfig.setProxyHost(proxy);
		}

		return client;
	}

	/**
	 * Initialize the SSL layer.
	 * 
	 * @throws KeyStoreException
	 *             If SSL initialization fails.
	 * @throws NoSuchAlgorithmException
	 *             If SSL initialization fails.
	 */
	void initializeSSL() throws NoSuchAlgorithmException, KeyStoreException {
		// initialize trust manager
		X509TrustManager manager;
		manager = new AcceptingSSLTrustManager();

		// initialize socket factory
		ProtocolSocketFactory sslFactory;
		sslFactory = new EasySSLProtocolSocketFactory(manager);

		// register with protocol
		Protocol easyhttps = new Protocol("https", sslFactory, 443);
		Protocol.registerProtocol("https", easyhttps);
	}

	/**
	 * Execute HTTP Get method.
	 * 
	 * @param client
	 *            HTTP client.
	 * @param method
	 *            HTTP Get method object.
	 * @param url
	 *            URL which is accessed.
	 * 
	 * @throws IOException
	 *             If invocation fails.
	 * @throws HttpException
	 *             If invocation fails.
	 */
	void executeGetMethod(HttpClient client, HttpMethod method, String url) throws IOException, HttpException {
		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Executing get method for URL <");
			message.append(url);
			message.append(">.");
			logger.debug(message.toString());
		}

		// execute get method
		client.executeMethod(method);
	}

	/**
	 * Get HTTP response content.
	 * 
	 * @param client
	 *            HTTP client object.
	 * @param method
	 *            HTTP get method object.
	 * @return Invocation result object which contains content of response.
	 */
	HttpInvocationResult getResponseContent(HttpClient client, HttpMethod method) {
		// create invocation result
		HttpInvocationResult invocationResult = new HttpInvocationResultImpl();

		// declare response
		String response = null;
		Document responseAsXml = null;

		try {
			// get response
			response = method.getResponseBodyAsString();

			// attempt to parse response as XML
			responseAsXml = parseResponseAsXml(response);

		} catch (Exception e) {

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Failed to get HTTP response from body. Will use null. ");
				logger.debug(message.toString());
			}
		}

		// get response properties
		ResponsePropertyInfo[] properties = propertyInfoSet.getProperties();

		// iterate over the response properties
		for (ResponsePropertyInfo property : properties) {

			// declare value
			Object propertyValue = null;

			// get property from response body in DOM
			if (isBodyXPathTarget(property)) {

				// get property from response body in DOM
				propertyValue = getPropertyFromBody(responseAsXml, property);

			} else if (isStateXPathTarget(property)) {

				// get property from HTTP state object
				propertyValue = getPropertyFromHttpState(client, property);

			} else if (isMethodXPathTarget(property)) {

				// get property from HTTP method object
				propertyValue = getPropertyFromHttpMethod(method, property);
			}

			// set value
			invocationResult.setPropertyValue(property.getName(), propertyValue);
		}

		return invocationResult;
	}

	/**
	 * Parse the response as XML.
	 * 
	 * @param response
	 *            The HTTP response.
	 * 
	 * @return XML document if response could be parsed as XML, otherwise null is
	 *         returned.
	 */
	Document parseResponseAsXml(String response) {
		try {

			// log debug message
			if (logger.isDebugEnabled()) {
				logger.debug(messageProvider.getMessage("ihgc.parse_xml_start"));
			}

			HtmlCleaner cleaner = new HtmlCleaner();
			CleanerProperties props = cleaner.getProperties();
			TagNode cleanedHtml = cleaner.clean(response);
			DomSerializer serializer = new DomSerializer(props);
			Document doc = serializer.createDOM(cleanedHtml);

			// log debug message
			if (logger.isDebugEnabled()) {
				logger.debug(messageProvider.getMessage("ihgc.parse_xml_success"));
			}

			return doc;

		} catch (Exception e) {

			// log debug message
			if (logger.isDebugEnabled()) {
				Object[] args = { e.toString() };
				logger.debug(messageProvider.getMessage("ihgc.parse_xml_error", args));
			}

			return null;
		}
	}

	/**
	 * Return true if proxy parameters are defined.
	 * 
	 * @return true if proxy parameters are defined.
	 */
	boolean isProxyDefined() {
		if (httpConfiguration.getProxyHost() == null)
			return false;
		if (httpConfiguration.getProxyHost().length() == 0)
			return false;
		return true;
	}

	/**
	 * Returns true if XPath target is the response body.
	 * 
	 * @param property
	 *            Response property info object describing the property.
	 * 
	 * @return true if XPath target is the response body.
	 */
	boolean isBodyXPathTarget(ResponsePropertyInfo propertyInfo) {
		return (propertyInfo.getXPath().startsWith("body/"));
	}

	/**
	 * Returns true if XPath target is the client state.
	 * 
	 * @param property
	 *            Response property info object describing the property.
	 * 
	 * @return true if XPath target is the client state.
	 */
	boolean isStateXPathTarget(ResponsePropertyInfo propertyInfo) {
		return (propertyInfo.getXPath().startsWith("state/"));
	}

	/**
	 * Returns true if XPath target is the method object.
	 * 
	 * @param property
	 *            Response property info object describing the property.
	 * 
	 * @return true if XPath target is the client state.
	 */
	boolean isMethodXPathTarget(ResponsePropertyInfo propertyInfo) {
		return (propertyInfo.getXPath().startsWith("method/"));
	}

	/**
	 * Get property from Response body.
	 * 
	 * @param document
	 *            DOM containing the response body.
	 * @param property
	 *            Response property info object describing the property.
	 * 
	 * @return property from Response body.
	 */
	Object getPropertyFromBody(Document document, ResponsePropertyInfo propertyInfo) {

		// get XPath except the body prefix
		String modifiedXPath = propertyInfo.getXPath().substring("body".length());

		// create debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Starting to get property from response body, using XPath expression < ");
			message.append(propertyInfo.getXPath());
			message.append(">.");
			logger.debug(message.toString());
		}

		try {

			// skip if doc is undefined
			if (document == null) {

				// create debug message
				if (logger.isDebugEnabled()) {
					StringBuilder message = new StringBuilder();
					message.append("Skipping getting property < ");
					message.append(propertyInfo.getXPath());
					message.append("> from response, as XML document is null. Will return 'null'.");
					logger.debug(message.toString());
				}

				return "null";
			}

			// run query
			String propertyValue = (String) xpath.evaluate(modifiedXPath, document, XPathConstants.STRING);

			if (propertyValue != null) {

				// create debug message
				if (logger.isDebugEnabled()) {
					StringBuilder message = new StringBuilder();
					message.append(
							"Successfully completed property value extraction from HTTP response body, using XPath expression < ");
					message.append(propertyInfo.getXPath());
					message.append("> which returned value <");
					message.append(ReflectionToStringBuilder.toString(propertyValue));
					message.append(">.");
					logger.debug(message.toString());
				}

				// if value is empty then return null
				if (propertyValue.equals("")) {

					// create debug message
					if (logger.isDebugEnabled()) {
						StringBuilder message = new StringBuilder();
						message.append(
								"Extracted property value from HTTP response body was empty string. Will return 'null'.");
						logger.debug(message.toString());
					}

					return "null";
				}

				return propertyValue;

			} else {

				// create debug message
				if (logger.isDebugEnabled()) {
					StringBuilder message = new StringBuilder();
					message.append(
							"Extracted property value from HTTP response body was null, using XPath expression < ");
					message.append(propertyInfo.getXPath());
					message.append(">. Will return 'null'.");
					logger.debug(message.toString());
				}

				return "null";
			}

		} catch (XPathExpressionException e) {

			// create message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Skipping getting property < ");
				message.append(propertyInfo.getXPath());
				message.append("> from response, as running XPath querty failed with exception <");
				message.append(e);
				message.append(">.");
				logger.debug(message.toString());
			}

			return "null";
		}
	}

	/**
	 * Get property from HTTP method object
	 * 
	 * @param method
	 *            HTTP method object.
	 * @param property
	 *            Response property info object describing the property.
	 * 
	 * @return property from HTTP method object
	 */
	Object getPropertyFromHttpMethod(HttpMethod method, ResponsePropertyInfo propertyInfo) {

		try {

			// create debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Starting to get property from HTTP method object, using XPath expression < ");
				message.append(propertyInfo.getXPath());
				message.append(">.");
				logger.debug(message.toString());
			}

			// get XPath except the prefix
			String modifiedXPath = propertyInfo.getXPath().substring("method/".length());

			// get property from state object
			Object propertyValue = PropertyUtils.getSimpleProperty(method, modifiedXPath);

			// create debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append(
						"Successfully completed property value extraction from HTTP method object, using XPath expression < ");
				message.append(propertyInfo.getXPath());
				message.append("> which returned value <");
				message.append(ReflectionToStringBuilder.toString(propertyValue));
				message.append(">.");
				logger.debug(message.toString());
			}

			return propertyValue;

		} catch (Exception e) {

			// create message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Skipping getting property < ");
				message.append(propertyInfo.getXPath());
				message.append("> from Http method, as running XPath querty failed with exception <");
				message.append(e);
				message.append(">. Will return 'null'.");
				logger.debug(message.toString());
			}

			return "null";
		}

	}

	/**
	 * Get property from HTTP state object
	 * 
	 * @param client
	 *            HTTP client object.
	 * @param property
	 *            Response property info object describing the property.
	 * 
	 * @return property from HTTP state object
	 */
	Object getPropertyFromHttpState(HttpClient client, ResponsePropertyInfo propertyInfo) {

		try {

			// create debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Starting to get property from HTTP state object, using XPath expression < ");
				message.append(propertyInfo.getXPath());
				message.append(">.");
				logger.debug(message.toString());
			}

			// get XPath except the prefix
			String modifiedXPath = propertyInfo.getXPath().substring("state/".length());

			// get state
			HttpState state = client.getState();

			// get object property
			Object propertyValue = PropertyUtils.getSimpleProperty(state, modifiedXPath);

			// create debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append(
						"Successfully completed property value extraction from HTTP state object, using XPath expression < ");
				message.append(propertyInfo.getXPath());
				message.append("> which returned value <");
				message.append(ReflectionToStringBuilder.toString(propertyValue));
				message.append(">.");
				logger.debug(message.toString());
			}

			return propertyValue;

		} catch (Exception e) {

			// create message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Skipping getting property < ");
				message.append(propertyInfo.getXPath());
				message.append("> from Http state, as running XPath querty failed with exception <");
				message.append(e);
				message.append(">. Will return 'null'.");
				logger.debug(message.toString());
			}

			return "null";
		}

	}
}
