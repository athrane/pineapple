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

package com.alpha.pineapple.test.matchers;

import static com.alpha.pineapple.test.matchers.PineappleMatchers.mapContainsKekys;

import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.alpha.pineapple.plugin.net.http.HeaderMap;

/**
 * Matches if a array of HTTP headers contains expected header and values.
 * 
 * <p>
 * The matcher succeeds if: 1) The header array contains the expected number of
 * headers. 2) The header array contains the expected header names. 3) The
 * headers in the array contains the expected values.
 * </p>
 */
public class IsHeadersContaining extends TypeSafeMatcher<Header[]> {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Expected HTTP headers.
	 */
	HeaderMap expectedHeaders;

	/**
	 * IsHeadersContaining constructor.
	 * 
	 * @param expectedHeaders
	 *            Array of expected HTTP headers.
	 */
	public IsHeadersContaining(Header[] expectedHeaders) {
		this.expectedHeaders = createHeaderMap(expectedHeaders);

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Initialized IsHeadersContaining with <");
			message.append(ReflectionToStringBuilder.toString(expectedHeaders));
			message.append(">.");
			logger.debug(message.toString());
		}
	}

	/**
	 * IsHeadersContaining constructor.
	 * 
	 * @param expectedHeaders
	 *            Map of header name and values..
	 */
	public IsHeadersContaining(Map<String, String> expectedHeaders) {
		this(createHeaderArray(expectedHeaders));
	}

	@Override
	protected boolean matchesSafely(Header[] headers) {

		// succeeds if expected headers is empty
		if (expectedHeaders.isEmpty())
			return true;

		// create headers map
		HeaderMap headerMap = createHeaderMap(headers);

		// fails if the map contains different key sets
		Matcher<Map<String, Header>> keysMatcher = mapContainsKekys(expectedHeaders.keySet());
		if (!keysMatcher.matches(headerMap))
			return false;

		// test values
		return (isValuesEqual(headerMap));
	}

	public void describeTo(Description description) {
		description.appendText("headers is containing ");
		description.appendValue(expectedHeaders);
		description.appendText(" ");
	}

	@Override
	protected void describeMismatchSafely(Header[] headers, Description mismatchDescription) {

		// create headers map
		HeaderMap actualHeaders = createHeaderMap(headers);

		// get actual keys
		Set<String> actualKeys = actualHeaders.keySet();

		boolean isPastFirst = false;

		// iterate over the expected keys
		mismatchDescription.appendText("didn't contain expected headers ");
		for (String key : expectedHeaders.keySet()) {
			if (isPastFirst) {
				mismatchDescription.appendText(", ");
			}
			mismatchDescription.appendValue(key);
			isPastFirst = true;
		}

		// iterate over the actual keys
		isPastFirst = false;
		mismatchDescription.appendText("but contained headers ");
		for (String key : actualHeaders.keySet()) {
			if (isPastFirst) {
				mismatchDescription.appendText(", ");
			}

			StringBuilder message = new StringBuilder();
			message.append(key);
			message.append("=");
			message.append(actualHeaders.get(key));
			mismatchDescription.appendValue(StringUtils.chomp(message.toString()));
			isPastFirst = true;
		}
	}

	/**
	 * Create header map
	 * 
	 * @param headers
	 *            Array of headers.
	 * 
	 * @return Map with headers.
	 */
	HeaderMap createHeaderMap(Header[] headers) {

		// create map
		HeaderMap headerMap = new HeaderMap();

		// iterate over the headers
		for (Header header : headers) {
			// add header
			headerMap.put(header.getName(), header);

		}
		return headerMap;
	}

	/**
	 * Create header array
	 * 
	 * @param Map
	 *            of header name and values..
	 * 
	 * @return Array of headers..
	 */
	static Header[] createHeaderArray(Map<String, String> headers) {

		Header[] headersArray = new Header[headers.size()];

		int index = 0;
		for (String key : headers.keySet()) {
			headersArray[index] = new Header(key, headers.get(key));
			index++;
		}

		return headersArray;
	}

	/**
	 * Returns true if the same key maps to the same value in the two maps. Return
	 * false if a least one key map to different values in the two maps.
	 * 
	 * @param actualHeaders
	 *            Map of actual HTTP headers.
	 * 
	 * @return true if the same key maps to the same value in the two maps. Return
	 *         false if a least one key map to different values in the two maps.
	 */
	boolean isValuesEqual(HeaderMap actualHeaders) {

		// get the key sets
		Set<String> map1Keys = expectedHeaders.keySet();

		// iterate over the key to compare the values
		for (String key : map1Keys) {

			// get values
			Header value1 = expectedHeaders.get(key);
			Header value2 = actualHeaders.get(key);

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Comparing header with key <");
				message.append(key);
				message.append("> and expected <");
				message.append(ReflectionToStringBuilder.toString(value1));
				message.append("> vs. actual <");
				message.append(ReflectionToStringBuilder.toString(value2));
				message.append(">.");
				logger.debug(message.toString());
			}

			// compare the values
			if (!value1.equals(value2)) {

				// skip comparison if values isn't equal
				return false;
			}
		}

		return true;
	}

	@Factory
	public static Matcher<Header[]> contains(Header[] expectedHeaders) {
		return new IsHeadersContaining(expectedHeaders);
	}

	@Factory
	public static Matcher<Header[]> contains(Map<String, String> expectedHeaders) {
		return new IsHeadersContaining(expectedHeaders);
	}

}
