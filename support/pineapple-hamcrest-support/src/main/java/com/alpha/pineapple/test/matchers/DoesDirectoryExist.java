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
 * Matches if a file exists on disk and it is a directory.
 */
public class DoesDirectoryExist extends TypeSafeMatcher<File> {

	/**
	 * Last asserted directory.
	 */
	File lastDirectory;

	public boolean matchesSafely(File directory) {
	
		// store directory
		lastDirectory = directory;
		
        // test whether directory exists 
        if (!directory.exists()) return false;		
        if (!directory.isDirectory()) return false;
        return true;
	}

	public void describeTo(Description description) {
		 
		description.appendText("directory ");
		
		if(lastDirectory != null ) {
			description.appendValue(lastDirectory);
			description.appendText(" ");
		}
		
		description.appendText("exists");
	}
	
	
    @Override
	protected void describeMismatchSafely(File file, Description mismatchDescription) {
    	        
        // create description
    	mismatchDescription.appendText("directory ");    	
    	mismatchDescription.appendValue(file);
    	mismatchDescription.appendText(" doesn't exist");
	}

	@Factory
    public static Matcher<File> doesDirectoryExist() {
        return new DoesDirectoryExist();
    }
    
}
