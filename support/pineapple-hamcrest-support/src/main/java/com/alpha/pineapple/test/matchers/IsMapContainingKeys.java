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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matches if a map contains an expected pair of keys.
 */
public class IsMapContainingKeys<K, V> extends TypeSafeMatcher<Map<K, V>> {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Expected Map keys.
	 */
	Set<K> expectedKeys;

	/**
	 * IsMapContainingKeys constructor.
	 * 
	 * @param expectedKeys
	 *            Map with expected set of keys.
	 */
	public IsMapContainingKeys(Set<K> expectedKeys) {
		this.expectedKeys = expectedKeys;
	}

	@Override
	protected boolean matchesSafely(Map<K, V> map) {

		// get actual keys
		Set<K> actualKeys = map.keySet();

		// match that the actual key set contains AT LEAST the expected keys
		return actualKeys.containsAll(expectedKeys);
	}

	public void describeTo(Description description) {
		description.appendText("isMapContainingKeys(");
		description.appendValue(expectedKeys);
		description.appendText(")");
	}

	@Override
	protected void describeMismatchSafely(Map<K, V> map, Description mismatchDescription) {

		// create set to old missing keys
		Set<K> missingKeys = new HashSet<K>();

		// get actual keys
		Set<K> actualKeys = map.keySet();

		// iterate over the expected keys
		for (K key : expectedKeys) {

			// add key if is is missing
			if (!actualKeys.contains(key)) {
				missingKeys.add(key);
			}
		}

		String[] keySetArray = missingKeys.toArray(new String[missingKeys.size()]);
		String keysAsString = ReflectionToStringBuilder.toString(keySetArray);

		// create description
		mismatchDescription.appendText("didnt contain the expected keys ");
		mismatchDescription.appendText(keysAsString);
	}

	@Factory
	public static <K, V> Matcher<Map<K, V>> containsKekys(Set<K> expectedKeys) {
		return new IsMapContainingKeys<K, V>(expectedKeys);
	}

}
