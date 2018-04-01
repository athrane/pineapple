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

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.alpha.pineapple.plugin.net.http.HttpInvocationResult;
import com.alpha.pineapple.plugin.net.http.HttpInvocationSequence;

/**
 * Matches if a {@link HttpInvocationSequence} is empty.
 */
public class IsSequenceEmpty extends TypeSafeMatcher<HttpInvocationSequence> {

	public boolean matchesSafely(HttpInvocationSequence sequence) {

		// get invocation result sequence
		HttpInvocationResult[] sequenceAsArray = sequence.getSequence();

		// test whether sequence is empty
		return (sequenceAsArray.length == 0);

	}

	public void describeTo(Description description) {
		description.appendText("a empty HTTP result sequence.");
	}

	@Override
	protected void describeMismatchSafely(HttpInvocationSequence sequence, Description mismatchDescription) {

		// get invocation result sequence
		HttpInvocationResult[] sequenceAsArray = sequence.getSequence();

		mismatchDescription.appendText("contained");
		mismatchDescription.appendValue(sequenceAsArray.length);
		mismatchDescription.appendText(" entries in sequence]");
	}

	@Factory
	public static <T> Matcher<HttpInvocationSequence> isSequenceEmpty() {
		return new IsSequenceEmpty();
	}

}
