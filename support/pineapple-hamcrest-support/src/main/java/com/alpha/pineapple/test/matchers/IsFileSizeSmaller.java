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

import java.io.File;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matches if a file size is smaller than excepted size.
 */
public class IsFileSizeSmaller extends TypeSafeMatcher<File> {

	/**
	 * Last asserted file.
	 */
	File lastFile;

	/**
	 * Maximum legal size in bytes.
	 */
	long legalSizeBytes;

	/**
	 * IsFileSizeSmaller constructor.
	 * 
	 * @param legalSzie
	 *            in bytes.
	 */
	public IsFileSizeSmaller(long legalsize) {
		this.legalSizeBytes = legalsize;
	}

	public boolean matchesSafely(File file) {
		return (file.length() < legalSizeBytes);
	}

	public void describeTo(Description description) {
		description.appendText("file size is smaller than ");
		description.appendText(Long.toString(legalSizeBytes));
		description.appendText(" bytes");
	}

	@Override
	protected void describeMismatchSafely(File file, Description mismatchDescription) {
		mismatchDescription.appendText("file size isn't smaller than");
		mismatchDescription.appendText(Long.toString(legalSizeBytes));
		mismatchDescription.appendText(" bytes");
	}

	@Factory
	public static Matcher<File> isFileSizeSmaller(long legalSize) {
		return new IsFileSizeSmaller(legalSize);
	}

}
