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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matches if the values in a collection are distributed
 * across a set of legal range of values.
 * 
 * The matcher succeeds if the values is precisely 
 * distributed among the values in the range. 
 */
public class IsDistributedAcrossRange<T> extends TypeSafeMatcher<Iterable<T>> {
	
    /**
     * Range of legal values.
     */
	final Collection<T> range;	    
    
    /**
     * Expected number of instances in range.
     */
	double expectedInstancesPrItem;	
	
	/**
	 * DistributedAcrossRange constructor.
	 * 
	 * @param range Collection containing range of legal values. 
	 */
	public IsDistributedAcrossRange(Collection<T> range) {		
		this.range = range;		
	}

	public boolean matchesSafely(Iterable<T> items) {	
		
		// exit if iterable doesn't contain any items
		if (Matchers.emptyIterable().matches(items)) {
			return true;
		}
		
		// exit if values isn't in legal range
		if (!PineappleMatchers.isInLegalRange(range).matches(items)) {
			return false;
		}

		// initialize distribution map
		Map<T, Integer> loadDistribution = initializeDistributionMap();
		        
        // calculate distribution
		int numberItems = calculateDistribution(items, loadDistribution);
		
    	// calculate remainder
    	double remainder = ((double) numberItems % (double) range.size());
    	
    	// calculate quotinent
		double quotinent = numberItems - remainder;
    	
    	// decide if remainder is multiplum
    	boolean isMultiplum = (remainder == 0); 

    	// calculate expected load
    	double expectedLoad = (quotinent / (double) range.size());
    	
        // test actual load distribution 
        for ( T key : loadDistribution.keySet() ) {
        	
        	// get load 
        	int load = loadDistribution.get(key);
        	
        	// test the load
        	if (!isExpectedLoad(isMultiplum, expectedLoad, load)) return false;
        }                                       

        // return success
        return true;				
	}

	/**
	 * Return true if the load is expected.
	 *  
	 * @param isMultiplum Is true if number of entries is multiplum of the range.
	 * @param expectedLoad The expected load for each entry in the range. 
	 * @param load The actual load for the current entry in the range.
	 * 
	 * @return true if the actual load is expected.
	 */
	boolean isExpectedLoad(boolean isMultiplum, double expectedLoad,
			int load) {
		if (isMultiplum) {
			return (load == expectedLoad);
		} else {        	
			double expectedMinLoad = expectedLoad;
			double expectedMaxLoad = expectedLoad + 1.0;
			return (load == expectedMinLoad ) || (load == expectedMaxLoad );        		
		}
	}

    public void describeTo(Description description) {
        description.appendText("distributedAcrossRange(")
                .appendValue(range)
                .appendText(")");
    }
	
    @Override
	protected void describeMismatchSafely(Iterable<T> items, Description mismatchDescription) {
    	
		// if values isn't in legal range get failure message from matcher		
    	Matcher<Iterable<T>> rangeMatcher = PineappleMatchers.isInLegalRange(range);    	    	
		if (!rangeMatcher.matches(items)) {
			rangeMatcher.describeMismatch(items, mismatchDescription);			
			return;
		}
    	    	    	
    	mismatchDescription.appendText("contained the distribution");
    	
		// initialize distribution map
		Map<T, Integer> loadDistribution = initializeDistributionMap();
		        
        // calculate distribution
		int numberItems = calculateDistribution(items, loadDistribution);
		
        // test actual load distribution 
        for ( T key : loadDistribution.keySet() ) {
        	
        	// get load 
        	int load = loadDistribution.get(key);
        	
        	mismatchDescription.appendText(key.toString());
        	mismatchDescription.appendText("=");
        	mismatchDescription.appendText((String) Double.toString(load / (double) numberItems));
        	mismatchDescription.appendText(", ");        	
        }                                           	    					
	}

	/**
	 * Calculate load distribution
	 * 
	 * @param items Collection of entries which is distributed to node. 
	 * @param map Map of legal nodes.
	 * 
	 * @return Number if entries.
	 */
	int calculateDistribution(Iterable<T> items, Map<T, Integer> map) {
		
        // initialize counter
        int numberItems = 0;

        // iterate over the items
        for (T item : items) {
        
        	// register load entry 
        	int load = map.get( item );        	
        	map.put( item, load + 1 );
        	
        	// increment counter
            numberItems++;
    	}
		return numberItems;
	}

	/**
	 * Initialize distribution map
	 * 
	 * @return Initialized distribution map.
	 */
	Map<T, Integer> initializeDistributionMap() {
        Map<T, Integer> map = new HashMap<T, Integer>();
        for ( T rangeItem : range ) {
            map.put( rangeItem, 0 );
        }
		return map;
	}		    
        
    @Factory
    public static <T> Matcher<Iterable<T>> distributedAcrossRange(Collection<T> legalValues) {
        return new IsDistributedAcrossRange<T>(legalValues);
    }
    
}
