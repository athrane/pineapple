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
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 *
 * Helper class which provides static factory methods for
 * easy access to matcher instances.
 * 
 * Inspired by the {@link CoreMatchers}.  
 */
public class PineappleMatchers {

    @Factory
    public static <T> Matcher<Iterable<T>> isInLegalRange(Collection<T> range) {    	
        return IsInLegalRange.isInLegalRange(range);
    }

    @Factory
    public static <T> Matcher<Iterable<T>> matchingSingleValueInRange(Collection<T> range) {
    	return IsMatchingSingleValueInRange.matchingSingleValueInRange(range);
    }
    
    @Factory
    public static <T> Matcher<Iterable<T>> distributedAcrossRange(Collection<T> range) {   
    	return IsDistributedAcrossRange.distributedAcrossRange(range );
    }

            	
	@Factory
    public static Matcher<Object[]> isArrayEmpty() {
        return IsArrayEmpty.isArrayEmpty();
    }
	
	@Factory
    public static <K,V> Matcher<Map<K,V>> mapContainsKekys(Set<K> expectedKeys) {
        return IsMapContainingKeys.containsKekys(expectedKeys);
    }

	@Factory
    public static Matcher<File> doesFileExist() {
        return DoesFileExist.doesFileExist();
    }

	@Factory
    public static Matcher<File> doesDirectoryExist() {
        return DoesDirectoryExist.doesDirectoryExist();
    }

	@Factory
    public static Matcher<File> isFileSizeSmaller(long legalSize) {		
        return IsFileSizeSmaller.isFileSizeSmaller(legalSize);
    }
	
}
