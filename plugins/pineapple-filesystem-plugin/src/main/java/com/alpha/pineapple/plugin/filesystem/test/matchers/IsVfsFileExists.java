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

package com.alpha.pineapple.plugin.filesystem.test.matchers;

import java.net.URL;

import org.apache.commons.vfs2.FileObject;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.alpha.pineapple.plugin.filesystem.session.FileSystemSession;

/**
 * Matches if VFS file object can be resolved and exists on the virtual file
 * system.
 */
public class IsVfsFileExists extends TypeSafeMatcher<String> {

	/**
	 * Last recorded exception.
	 */
	Exception lastException;

	/**
	 * File system session.
	 */
	FileSystemSession session;

	/**
	 * Last recorded path.
	 */
	Object lastPath;

	private URL lastUrl;

	/**
	 * IsVfsFileResolvable constructor.
	 * 
	 * @param session
	 *            File system session.
	 */
	IsVfsFileExists(FileSystemSession session) {
		this.session = session;
	}

	public boolean matchesSafely(String path) {

		try {
			// store input
			lastPath = path;
			lastException = null;

			// resolve file
			FileObject resolvedFile = session.resolveFile(path);

			// return result
			return (resolvedFile.exists());
		} catch (Exception e) {
			// record exception
			lastException = e;

			// fail test
			return false;
		}

	}

	public void describeTo(Description description) {
		description.appendText("file object ");
		description.appendValue(lastPath);
		description.appendText(" exists");
	}

	@Override
	protected void describeMismatchSafely(String item, Description mismatchDescription) {

		// create description due to exception
		if (lastException != null) {
			mismatchDescription.appendText("file object resolution failed due to exception ");
			mismatchDescription.appendValue(lastException.toString());
			return;
		}

		// create description
		mismatchDescription.appendText("file object ");
		mismatchDescription.appendValue(item);
		mismatchDescription.appendText(" doesn't exists at the target resource");
	}

	@Factory
	public static Matcher<String> isVfsFileExists(FileSystemSession session) {
		return new IsVfsFileExists(session);
	}

}
