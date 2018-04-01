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
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.alpha.pineapple.plugin.net.http.CookieMapPair;

/**
 * Matches if a pair of cookies maps contains equal set of keys.
 */
public class IsCookieMapsPairContainingEqualKeys extends TypeSafeMatcher<CookieMapPair> {

	public void describeTo(Description description) {
		description.appendText("pair of cookie maps with equal key sets");
	}

	@Override
	protected void describeMismatchSafely(CookieMapPair pair, Description mismatchDescription) {

		// create copy of key set for map1
		Set<String> keySet1 = new HashSet<String>();
		keySet1.addAll(pair.getFirstMap().keySet());

		// create copy of key set for map2
		Set<String> keySet2 = new HashSet<String>();
		keySet2.addAll(pair.getSecondMap().keySet());

		// compute asymmetric difference for key set 1 and key set 2
		keySet1.removeAll(keySet2);
		String[] keySet1Array = keySet1.toArray(new String[keySet1.size()]);
		String keys1AsString = ReflectionToStringBuilder.toString(keySet1Array);

		// compute asymmetric difference for key set 2 and key set 1
		keySet2.removeAll(keySet1);
		String[] keySet2Array = keySet2.toArray(new String[keySet2.size()]);
		String keys2AsString = ReflectionToStringBuilder.toString(keySet2Array);

		// create description
		mismatchDescription.appendText("contained cookie maps with different key sets, ");
		mismatchDescription.appendText("map #1 contains the keys");
		mismatchDescription.appendText(keys1AsString);
		mismatchDescription.appendText("which isn't found in map #2, and map #2 contains the keys");
		mismatchDescription.appendText(keys2AsString);
		mismatchDescription.appendText("which isn't found in map #1");
	}

	@Override
	protected boolean matchesSafely(CookieMapPair pair) {

		// get the key sets
		Set<String> map1Keys = pair.getFirstMap().keySet();
		Set<String> map2Keys = pair.getSecondMap().keySet();

		// compare the sets
		return (map1Keys.containsAll(map2Keys));
	}

	@Factory
	public static Matcher<CookieMapPair> containsEqualKeys() {
		return new IsCookieMapsPairContainingEqualKeys();
	}

}
