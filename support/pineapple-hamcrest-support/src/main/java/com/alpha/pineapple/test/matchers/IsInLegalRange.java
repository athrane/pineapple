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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matches if the values in a collection are within a legal range of values.
 */
public class IsInLegalRange<T> extends TypeSafeMatcher<Iterable<T>> {

    private final Collection<T> legalValues;	
	
	/**
	 * IsInLegalRange constructor.
	 * 
	 * @param range Collection containing range of legal values. 
	 */
	public IsInLegalRange(Collection<T> range) {		
		this.legalValues = range;		
	}

	@Override	
	public boolean matchesSafely(Iterable<T> item) {
		
		// iterate over the values
        for (T currentItem : item) {

        	// exit if values isn't defined in the legal range
        	if(!legalValues.contains( currentItem )) return false;
        }
        
        // return success
        return true;		
	}
		
    public void describeTo(Description description) {
        description.appendText("inLegalRange(")
                .appendValue(legalValues)
                .appendText(")");
    }
			
    @Override
	protected void describeMismatchSafely(Iterable<T> item, Description mismatchDescription) {
    	
    	// create illegal values
    	ArrayList<T> illegalValues = new ArrayList<T>();
    	
		// iterate over the values
        for (T currentItem : item) {

        	// if values isn't defined in the legal range add it to list
        	if(!legalValues.contains( currentItem )) {
        		illegalValues.add(currentItem);
        	}
        }
    	    	
    	mismatchDescription.appendText("contained the illegal values [");
    	mismatchDescription.appendText(ReflectionToStringBuilder.toString(illegalValues));
    	mismatchDescription.appendText("]");				
	}

	@Factory
    public static <T> Matcher<Iterable<T>> isInLegalRange(Collection<T> legalValues) {
        return new IsInLegalRange<T>(legalValues);
    }
    
}
