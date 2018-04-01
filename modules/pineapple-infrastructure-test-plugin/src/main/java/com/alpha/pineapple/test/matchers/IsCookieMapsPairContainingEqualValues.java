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
import java.util.Collection;
import java.util.Set;

import org.apache.commons.httpclient.Cookie;
import org.apache.log4j.Logger;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import com.alpha.pineapple.plugin.net.http.CookieMap;
import com.alpha.pineapple.plugin.net.http.CookieMapPair;

/**
 * Matches if a pair of cookie maps contains equal values.
 * 
 * <p>
 * The matcher succeeds if: 1) The two maps contains the same number of keys. 2)
 * The two maps contains the same keys names. 3) A key maps to the same value in
 * both maps.
 * </p>
 */
public class IsCookieMapsPairContainingEqualValues extends TypeSafeMatcher<CookieMapPair> {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected boolean matchesSafely(CookieMapPair pair) {

		// fails if sizes are different
		if (!InfrastructureMatchers.cookieMapsPairOfEqualSize().matches(pair))
			return false;

		// succeeds if maps are empty
		if (Matchers.empty().matches(pair.getFirstMap().keySet()))
			return true;

		// fails if the maps contains different key sets
		if (!InfrastructureMatchers.cookieMapsPairContainingEqualKeys().matches(pair))
			return false;

		// test values
		return (isValuesEqual(pair));
	}

	public void describeTo(Description description) {
		description.appendText("pair of cookie maps containing equal values");
	}

	@Override
	protected void describeMismatchSafely(CookieMapPair pair, Description mismatchDescription) {

		// if sizes are different get failure message from matcher
		Matcher<CookieMapPair> sizeMatcher = InfrastructureMatchers.cookieMapsPairOfEqualSize();
		if (!sizeMatcher.matches(pair)) {
			sizeMatcher.describeMismatch(pair, mismatchDescription);
			return;
		}

		// if keys are different get failure message from matcher
		Matcher<CookieMapPair> keysMatcher = InfrastructureMatchers.cookieMapsPairContainingEqualKeys();
		if (!keysMatcher.matches(pair)) {
			keysMatcher.describeMismatch(pair, mismatchDescription);
			return;
		}

		// compute asymmetric difference for values from map1 and map2
		Cookie[] values1 = computeDifference(pair.getFirstMap(), pair.getSecondMap());
		String values1AsString = createDescription(values1);

		// compute asymmetric difference for values from map1 and map2
		Cookie[] values2 = computeDifference(pair.getSecondMap(), pair.getFirstMap());
		String values2AsString = createDescription(values2);

		// create message
		mismatchDescription.appendText("contained cookie maps with different values, ");
		mismatchDescription.appendText("Map #1 contains the values [");
		mismatchDescription.appendText(values1AsString);
		mismatchDescription.appendText("] which isn't found in map #2, and map #2 contains the values [");
		mismatchDescription.appendText(values2AsString);
		mismatchDescription.appendText("] which isn't found in map #1");
	}

	/**
	 * Returns true if the same key maps to the same value in the two maps. Return
	 * false if a least one key map to different values in the two maps.
	 * 
	 * @param pair
	 *            Pair of cookie maps.
	 * 
	 * @return true if the same key maps to the same value in the two maps. Return
	 *         false if a least one key map to different values in the two maps.
	 */
	boolean isValuesEqual(CookieMapPair pair) {

		// get the key sets
		Set<String> map1Keys = pair.getFirstMap().keySet();

		// iterate over the key to compare the values
		for (String key : map1Keys) {

			// get values
			String value1 = pair.getFirstMap().get(key).getValue();
			String value2 = pair.getSecondMap().get(key).getValue();
			;

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Comparing cookies with key <");
				message.append(key);
				message.append("> and values <");
				message.append(value1);
				message.append("> vs. <");
				message.append(value2);
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

	/**
	 * Computes the difference between two cookie maps, by removing all the cookies
	 * from map #1 which is found is map #2. A cookie is removed from map #1 if a
	 * cookie with identical name is found in map #2, and the values of the two
	 * cookies are equal.
	 * 
	 * @param map1
	 *            Cookie map #1.
	 * @param map2
	 *            Cookie map #2.
	 * 
	 * @return Array of cookies from Map #1 whose values are different from the
	 *         corresponding cookie in map #2.
	 */
	Cookie[] computeDifference(CookieMap map1, CookieMap map2) {

		// create result list
		Collection<Cookie> result = new ArrayList<Cookie>();

		// get the key sets
		Set<String> map1Keys = map1.keySet();

		// iterate over the key to compare the values
		for (String key : map1Keys) {

			// get values
			String value1 = map1.get(key).getValue();
			String value2 = map2.get(key).getValue();

			// compare the values
			if (!value1.equals(value2)) {

				// add cookie
				result.add(map1.get(key));
			}
		}

		// return as array
		return result.toArray(new Cookie[result.size()]);
	}

	/**
	 * Create description of a collection of cookies.
	 * 
	 * @param cookies
	 *            collection of cookies.
	 * 
	 * @return description of a collection of cookies.
	 */
	String createDescription(Cookie[] cookies) {

		boolean isFirstIteration = true;
		StringBuilder message = new StringBuilder();

		// iterate over the cookies
		for (Cookie cookie : cookies) {

			if (isFirstIteration) {
				isFirstIteration = false;
			} else {
				message.append(", ");
			}

			message.append(cookie.getName());
			message.append("=");
			message.append(cookie.getValue());
		}

		return message.toString();
	}

	@Factory
	public static Matcher<CookieMapPair> containsEqualValues() {
		return new IsCookieMapsPairContainingEqualValues();
	}

}
