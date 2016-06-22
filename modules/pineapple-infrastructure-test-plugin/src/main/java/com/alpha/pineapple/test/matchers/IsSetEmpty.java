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

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.alpha.pineapple.plugin.net.http.HttpInvocationSequence;
import com.alpha.pineapple.plugin.net.http.HttpInvocationsSet;

/**
 * Matches if a {@link HttpInvocationsSet} is empty.
 */
public class IsSetEmpty extends TypeSafeMatcher<HttpInvocationsSet> {

	public boolean matchesSafely(HttpInvocationsSet set) {
		
        // get sequences
        HttpInvocationSequence[] sequences = set.getSequences();

        // test whether set is empty 
        return (sequences.length == 0);		
	}

	public void describeTo(Description description) {
		description.appendText("a empty HTTP result set");
	}
	
	
    @Override
	protected void describeMismatchSafely(HttpInvocationsSet set, Description mismatchDescription) {
    	
        // get sequences    	
        HttpInvocationSequence[] sequencesAsArray = set.getSequences();
        
        // create description
    	mismatchDescription.appendText("contained ");    	
    	mismatchDescription.appendValue(sequencesAsArray.length);
    	mismatchDescription.appendText(" sequences");
	}

	@Factory
    public static <T> Matcher<HttpInvocationsSet> isSetEmpty() {
        return new IsSetEmpty();
    }
    
}
