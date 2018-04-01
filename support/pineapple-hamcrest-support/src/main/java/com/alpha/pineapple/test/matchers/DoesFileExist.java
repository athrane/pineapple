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
 * Matches if a file exists on disk and it is a file.
 */
public class DoesFileExist extends TypeSafeMatcher<File> {

	/**
	 * Last asserted file.
	 */
	File lastFile;

	public boolean matchesSafely(File file) {

		// store file
		lastFile = file;

		// test whether file exists
		if (!file.exists())
			return false;
		if (!file.isFile())
			return false;
		return true;
	}

	public void describeTo(Description description) {

		description.appendText("file ");

		if (lastFile != null) {
			description.appendValue(lastFile);
			description.appendText(" ");
		}

		description.appendText("exists");
	}

	@Override
	protected void describeMismatchSafely(File file, Description mismatchDescription) {

		// create description
		mismatchDescription.appendText("file ");
		mismatchDescription.appendValue(file);
		mismatchDescription.appendText(" doesn't exist");
	}

	@Factory
	public static Matcher<File> doesFileExist() {
		return new DoesFileExist();
	}

}
