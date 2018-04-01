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

import org.apache.commons.httpclient.Cookie;
import org.apache.log4j.Logger;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.alpha.pineapple.plugin.net.http.CookieMap;
import com.alpha.pineapple.plugin.net.http.CookieMapPair;

/**
 * Matches if a collection of cookie maps contains equal values.
 * 
 * <p>
 * The matcher succeeds if: 1) All maps contains the same number of keys. 2) All
 * maps contains the same keys names. 3) A key maps to the same value in all
 * maps.
 * </p>
 */
public class IsCookieMapsContainingEqualValues extends TypeSafeMatcher<Iterable<Cookie[]>> {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * First map stored in iteration.
	 */
	CookieMap firstMap;

	/**
	 * Signals whether this is this first iteration.
	 */
	boolean isFirstIteration;

	/**
	 * Last record cookies.
	 */
	Iterable<Cookie[]> lastCookies;

	@Override
	protected boolean matchesSafely(Iterable<Cookie[]> cookiesCollection) {

		// store cookies for description
		this.lastCookies = cookiesCollection;

		// set iteration
		firstMap = null;
		isFirstIteration = true;

		// create matcher
		Matcher<CookieMapPair> matcher = InfrastructureMatchers.cookieMapsPairContainingEqualValues();

		// iterate over the values
		for (Cookie[] cookies : cookiesCollection) {

			// exit if cookie array is null
			if (cookies == null)
				return false;

			// create cookie map
			CookieMap cookieMap = createCookieMap(cookies);

			// store first value encountered in the iteration
			if (isFirstIteration) {

				firstMap = cookieMap;
				isFirstIteration = false;

				continue;
			}

			// match cookie maps
			if (!matcher.matches(new CookieMapPair(firstMap, cookieMap)))
				return false;
		}

		// return success
		return true;
	}

	/**
	 * Create cookie map
	 * 
	 * @param cookies
	 *            Array of cookies.
	 * 
	 * @return Map with cookies.
	 */
	CookieMap createCookieMap(Cookie[] cookies) {

		// create map
		CookieMap cookieMap = new CookieMap();

		// iterate over the cookies
		for (Cookie cookie : cookies) {
			// add cookie values
			cookieMap.put(cookie.getName(), cookie);
		}
		return cookieMap;
	}

	public void describeTo(Description description) {
		description.appendText("cookie maps contains equal values");
	}

	@Override
	protected void describeMismatchSafely(Iterable<Cookie[]> cookiesCollection, Description mismatchDescription) {

		// set iteration
		firstMap = null;
		isFirstIteration = true;

		// create matcher
		Matcher<CookieMapPair> matcher = InfrastructureMatchers.cookieMapsPairContainingEqualValues();

		// iterate over the values
		for (Cookie[] cookies : cookiesCollection) {

			// if cookie array is null then report that mismatch
			if (cookies == null) {
				// create description
				mismatchDescription.appendText("contained null cookie map");
				return;
			}

			// create cookie map
			CookieMap cookieMap = createCookieMap(cookies);

			// store first value encountered in the iteration
			if (isFirstIteration) {

				firstMap = cookieMap;
				isFirstIteration = false;

				continue;
			}

			// create cookie maps pair
			CookieMapPair pair = new CookieMapPair(firstMap, cookieMap);

			// match cookie maps
			if (!matcher.matches(pair)) {
				matcher.describeMismatch(pair, mismatchDescription);
				return;
			}
		}

	}

	@Factory
	public static Matcher<Iterable<Cookie[]>> containsEqualValues() {
		return new IsCookieMapsContainingEqualValues();
	}

}
