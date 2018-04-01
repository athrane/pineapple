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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;

import java.util.ArrayList;
import java.util.Collection;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matches if the values in a collection are matching a single value in a legal
 * range of values.
 */
public class IsMatchingSingleValueInRange<T> extends TypeSafeMatcher<Iterable<T>> {

	/**
	 * Range of legal values.
	 */
	final Collection<T> range;

	/**
	 * First value stored in iteration.
	 */
	T firstValue;

	/**
	 * Whether last test collection was empty
	 */
	boolean collectionWasEmpty;

	/**
	 * Whether matcher has been invoked.
	 */
	boolean hasBeenInvoked = false;

	/**
	 * IsMatchingSingleValueInRange constructor.
	 * 
	 * @param range
	 *            Collection containing range of legal values.
	 */
	public IsMatchingSingleValueInRange(Collection<T> range) {
		this.range = range;
	}

	@Override
	protected boolean matchesSafely(Iterable<T> items) {

		// reset flags
		collectionWasEmpty = false;
		hasBeenInvoked = true;

		// exit if iterable doesn't contain any items
		if (Matchers.emptyIterable().matches(items)) {
			collectionWasEmpty = true;
			return true;
		}

		// exit if values isn't in legal range
		if (!PineappleMatchers.isInLegalRange(range).matches(items)) {
			return false;
		}

		// get first value
		firstValue = items.iterator().next();

		// assert all values are equal
		if (!everyItem(equalTo(firstValue)).matches(items)) {
			return false;
		}

		// return success
		return true;
	}

	public void describeTo(Description description) {

		// create message if matcher found empty collection
		if (collectionWasEmpty) {
			description.appendText("every item is matching single value in range ");
			description.appendValue(range);
			description.appendText(" as collection was empty");
			return;
		}

		// create message if matcher has been invoked
		if (hasBeenInvoked) {
			description.appendText("every item is matching single value ");
			description.appendValue(firstValue);
			description.appendText(" in range ");
			description.appendValue(range);

			// clear flag
			hasBeenInvoked = false;

			return;
		}

		// create message if matcher hasn't been invoked
		description.appendText("every item is matching single value ");
		description.appendText(" in range ");
		description.appendValue(range);
	}

	@Override
	protected void describeMismatchSafely(Iterable<T> items, Description mismatchDescription) {

		// create illegal values
		Collection<T> illegalValues = new ArrayList<T>();

		// iterate over the values
		for (T item : items) {
			// collect all of the encountered values.
			if (!illegalValues.contains(item)) {
				illegalValues.add(item);
			}
		}

		// create text
		mismatchDescription.appendText("the items contained the illegal values ");
		mismatchDescription.appendValue(illegalValues);
	}

	@Factory
	public static <T> Matcher<Iterable<T>> matchingSingleValueInRange(Collection<T> legalValues) {
		return new IsMatchingSingleValueInRange<T>(legalValues);
	}

}
