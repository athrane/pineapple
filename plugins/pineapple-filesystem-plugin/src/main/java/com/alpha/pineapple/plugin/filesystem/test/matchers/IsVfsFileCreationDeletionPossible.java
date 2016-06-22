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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.NameScope;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.alpha.pineapple.plugin.filesystem.session.FileSystemSession;

/**
 * 
 */
public class IsVfsFileCreationDeletionPossible extends TypeSafeMatcher<String> {
	
	/**
	 * Time stamp format.
	 */
	static final String TIMESTAMP_FORMAT = "yyyyMMdd-HHmmss";

	/**
	 * Time stamp date format.
	 */
	DateFormat timestampFormat;
	
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

	Boolean isCreated;

	private Boolean isDeleted;

	private Boolean alreadyExists;
	
	/**
	 * IsVfsFileResolvable constructor.
	 * @param session File system session.
	 */
	IsVfsFileCreationDeletionPossible(FileSystemSession session) {				
		this.session = session;

		// create date format
		timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);		
	}

	public boolean matchesSafely(String path) {
		    	    	    	    	
        try
        {
            // store input
        	lastPath = path;
        	lastException = null;            
    
        	// reset state
        	isCreated = null;
        	isDeleted = null;
        	alreadyExists = null;
        	
    		// create directory name
    		StringBuilder testDirectoryName = new StringBuilder();
    		testDirectoryName.append("bypineapple");
    		testDirectoryName.append(timestampFormat.format(new Date()));
        	        	
        	// resolve file
        	FileObject resolvedFile = session.resolveFile(path);        	
        	FileObject childFile = resolvedFile.resolveFile(testDirectoryName.toString(), NameScope.CHILD );
        	
        	// fail if child exists
        	alreadyExists = new Boolean(childFile.exists());
        	if (alreadyExists.booleanValue()) return false;        	
        	
        	// create folder
        	childFile.createFolder();        	
        	isCreated = new Boolean(childFile.exists());

        	// fail if child isn't created
        	if(!isCreated.booleanValue()) return false;
        	
        	// delete folder
        	childFile.delete();
        	isDeleted = new Boolean(!childFile.exists());
        	
        	// fail if child isn't deleted
        	return isDeleted.booleanValue();
        }
        catch ( Exception e )
        {
        	// record exception
        	lastException =  e;
        	
        	// fail test
        	return false;
        } 		
	}

	public void describeTo(Description description) {
		description.appendText("current session can create and delete a sub folder at ");
		description.appendValue( lastPath );		
	}
	
	@Override
	protected void describeMismatchSafely(String item, Description mismatchDescription) {
        // create description due to exception
    	if (lastException != null) {
    		mismatchDescription.appendText("create and delete operation failed due to exception ");    		
    		mismatchDescription.appendValue(lastException.toString());    		
    		return;
    	} 
		
    	if((isCreated == null) || (!isCreated)){
            // create description
        	mismatchDescription.appendText("current session failed to create a sub folder at ");    	
        	mismatchDescription.appendValue(item);    	
        	mismatchDescription.appendText(" at the target resource");
        	return;
    	}

    	if((isDeleted == null) || (!isDeleted)) {
            // create description
        	mismatchDescription.appendText("current session failed to delete the sub folder at ");    	
        	mismatchDescription.appendValue(item);    	
        	mismatchDescription.appendText(" at the target resource");
        	return;
    	}
    	
        // create description
    	mismatchDescription.appendText("current session doesn't have write access to ");    	
    	mismatchDescription.appendValue(item);    	
    	mismatchDescription.appendText(" at the target resource");    	    	    	    			
	}
	
	@Factory
    public static Matcher<String> isVfsFileCreationDeletionPossible(FileSystemSession session) {
        return new IsVfsFileCreationDeletionPossible(session);
    }
    
}
