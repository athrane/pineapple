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

import com.alpha.pineapple.plugin.net.http.CookieMapPair;

/**
 * Matches if a pair of cookies maps contains key sets of equal size.
 */
public class IsCookieMapsPairOfEqualSize extends TypeSafeMatcher<CookieMapPair> {

	public void describeTo(Description description) {
		description.appendText("pair of cookie maps of equal size");
	}

	@Override
	protected void describeMismatchSafely(CookieMapPair pair, Description mismatchDescription) {

		// get sizes
		int size1 = pair.getFirstMap().keySet().size();
		int size2 = pair.getSecondMap().keySet().size();

		// create description
		mismatchDescription.appendText("contained pair of cookie maps of size ");
		mismatchDescription.appendValue(size1);
		mismatchDescription.appendText("and ");
		mismatchDescription.appendValue(size2);
	}

	@Override
	protected boolean matchesSafely(CookieMapPair pair) {

		// get sizes
		int size1 = pair.getFirstMap().keySet().size();
		int size2 = pair.getSecondMap().keySet().size();

		// do match
		return (size1 == size2);
	}

	@Factory
	public static Matcher<CookieMapPair> equalSize() {
		return new IsCookieMapsPairOfEqualSize();
	}

}
