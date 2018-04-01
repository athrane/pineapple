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

/**
 * Matches if a array is empty.
 */
public class IsArrayEmpty extends TypeSafeMatcher<Object[]> {

	public boolean matchesSafely(Object[] array) {

		// test whether array is empty
		return (array.length == 0);
	}

	public void describeTo(Description description) {
		description.appendText("a empty array");
	}

	@Override
	protected void describeMismatchSafely(Object[] array, Description mismatchDescription) {

		// create description
		mismatchDescription.appendText("contained ");
		mismatchDescription.appendValue(array.length);
		mismatchDescription.appendText(" entries");
	}

	@Factory
	public static Matcher<Object[]> isArrayEmpty() {
		return new IsArrayEmpty();
	}

}
