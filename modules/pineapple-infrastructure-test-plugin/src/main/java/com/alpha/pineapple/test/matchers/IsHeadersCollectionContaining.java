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

import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matches if a collection of HTTP header arrays contains expected header and
 * values.
 * 
 * <p>
 * The matcher succeeds if: 1) All header arrays in the collection contains the
 * expected number of headers. 2) All header arrays in collection contains the
 * expected header names. 3) A header has the same value in all header
 * collections.
 * </p>
 */
public class IsHeadersCollectionContaining extends TypeSafeMatcher<Iterable<Header[]>> {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Expected HTTP headers.
	 */
	Header[] expectedHeaders;

	/**
	 * IsHeadersCollectionContaining constructor.
	 * 
	 * @param expectedHeaders
	 *            Array of expected headers.
	 */
	public IsHeadersCollectionContaining(Header[] expectedHeaders) {
		this.expectedHeaders = expectedHeaders;
	}

	/**
	 * IsHeadersCollectionContaining constructor.
	 * 
	 * @param expectedHeaders
	 *            Map of expected headers.
	 */
	public IsHeadersCollectionContaining(Map<String, String> expectedHeaders) {

		// create header array
		Header[] headersArray = new Header[expectedHeaders.size()];
		int index = 0;
		for (String key : expectedHeaders.keySet()) {
			headersArray[index] = new Header(key, expectedHeaders.get(key));
			index++;
		}

		this.expectedHeaders = headersArray;

		// log debug message
		if (logger.isDebugEnabled()) {

			StringBuilder message = new StringBuilder();
			message.append("Initialized IsHeadersCollectionContaining with <");
			message.append(ReflectionToStringBuilder.toString(headersArray));
			message.append(">.");
			logger.debug(message.toString());
		}

	}

	@Override
	protected boolean matchesSafely(Iterable<Header[]> headersCollection) {

		// create headers matcher
		Matcher<Header[]> headersMatcher = InfrastructureMatchers.isHeadersContaining(expectedHeaders);

		// create iterating matcher
		Matcher<Iterable<Header[]>> everyMatcher = CoreMatchers.everyItem(headersMatcher);

		// match
		return everyMatcher.matches(headersCollection);
	}

	public void describeTo(Description description) {
		description.appendText("header collection is containing ");
		description.appendValue(expectedHeaders);
		description.appendText(" ");
	}

	@Override
	protected void describeMismatchSafely(Iterable<Header[]> headersCollection, Description mismatchDescription) {

		// create matcher
		Matcher<Header[]> matcher = InfrastructureMatchers.isHeadersContaining(expectedHeaders);

		// iterate over the values
		for (Header[] headers : headersCollection) {

			// handle mismatch
			if (!matcher.matches(headersCollection)) {
				matcher.describeMismatch(headers, mismatchDescription);
				return;
			}
		}
	}

	@Factory
	public static Matcher<Iterable<Header[]>> contains(Header[] expectedHeaders) {
		return new IsHeadersCollectionContaining(expectedHeaders);
	}

	@Factory
	public static Matcher<Iterable<Header[]>> contains(Map<String, String> expectedHeaders) {
		return new IsHeadersCollectionContaining(expectedHeaders);
	}

}
