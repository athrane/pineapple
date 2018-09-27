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

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.alpha.pineapple.plugin.net.http.HeaderMap;

/**
 * Matches if that an array of HTTP headers doesn't contain a set of headers
 * with specified names.
 * 
 * <p>
 * The matcher succeeds if: The header array doesn't ANY contain headers with
 * the "non-expected" header names. The match will fail if the array contains
 * ANY of the non expected header names.
 * </p>
 */
public class IsHeadersNotContaining extends TypeSafeMatcher<Header[]> {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Not expected HTTP headers.
	 */
	String[] notExpectedHeaders;

	/**
	 * IsHeadersContaining constructor.
	 * 
	 * @param notExpectedHeaders
	 *            Array of not expected HTTP headers names.
	 */
	public IsHeadersNotContaining(String[] notExpectedHeaders) {
		this.notExpectedHeaders = notExpectedHeaders;

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Initialized IsHeadersNotContaining with <");
			message.append(ReflectionToStringBuilder.toString(notExpectedHeaders));
			message.append(">.");
			logger.debug(message.toString());
		}

	}

	@Override
	protected boolean matchesSafely(Header[] headers) {

		// succeeds if expected headers is empty
		if (notExpectedHeaders.length == 0)
			return true;

		// create headers map
		HeaderMap headerMap = createHeaderMap(headers);

		// fails if the map contains any of the non expected key sets
		for (String nonExpectedHeader : notExpectedHeaders) {

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Validation if non expected header <");
				message.append(nonExpectedHeader);
				message.append("> is present.");
				logger.debug(message.toString());
			}

			if (headerMap.containsKey(nonExpectedHeader))
				return false;
		}

		// signal success
		return true;
	}

	public void describeTo(Description description) {
		description.appendText("IsHeadersNotContaining (");
		description.appendValue(notExpectedHeaders);
		description.appendText(")");
	}

	@Override
	protected void describeMismatchSafely(Header[] headers, Description mismatchDescription) {

		// create headers map
		HeaderMap headerMap = createHeaderMap(headers);

		boolean isPastFirst = false;

		// iterate over the header to find which not-expected headers are present
		for (String nonExpectedHeader : notExpectedHeaders) {

			if (isPastFirst) {
				mismatchDescription.appendText(", ");
			}

			if (headerMap.containsKey(nonExpectedHeader)) {
				mismatchDescription.appendText("contain an non-expected header");
				mismatchDescription.appendValue(nonExpectedHeader);
			}

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

	@Factory
	public static Matcher<Header[]> doesntContain(String[] expectedHeaders) {
		return new IsHeadersNotContaining(expectedHeaders);
	}

}
