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
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

/**
 * Matches if a IP address can be resolved to an expected TCP host name,
 * e.g. a reverse DNS lookup.  
 */
public class IsIpAddressResolvableToHost extends TypeSafeMatcher<String> {
	
	/**
	 * Last recorded exception.
	 */
	Exception lastException;
	
	/**
	 * Last recorded resolved IP address.
	 */	
	String lastIpAddress;

	/**
	 * Last recorded host name.
	 */		
	String lastHost;
	
	/**
	 * Last recorded resolved host name.
	 */		
	String[] lastResolvedHosts;
		
	/**
	 * IP address.
	 */
	String ipAddress;
	
	/**
	 * IsIpAddressResolvableToHost constructor.
	 * 
	 * @param ipAddress IP address.
	 */
	public IsIpAddressResolvableToHost(String ipAddress) {
		this.ipAddress = ipAddress;		
	}
	
	public boolean matchesSafely(String host) {
		    	    	    	    	
		
        try
        {
        	// save input
        	lastException = null;
        	lastHost = host;
        	
        	// create DNS resolver
            Resolver res = new ExtendedResolver();
        	
            // create query
        	Name name = ReverseMap.fromAddress(ipAddress);
            int type = Type.PTR;
            int dclass = DClass.IN;
            Record rec = Record.newRecord(name, type, dclass);
            Message query = Message.newQuery(rec);
            
            // execute query
            Message response = res.send(query);

            // get answers
            Record[] answers = response.getSectionArray(Section.ANSWER);
            
            // assert
            if (answers.length == 0) return false;
            
            
            // create array to collect resolved hosts
            lastResolvedHosts = new String[answers.length];            
            
            // iterate over the answers
            int index = 0;
            for (Record answer : answers ) {
            	
            	// get host
            	String actualHost = answer.rdataToString();

            	
            	// assert
            	if(actualHost.equalsIgnoreCase(host)) {
            		return true;
            	} else {

            		// create partial host name with dot
            		StringBuilder partialHost = new StringBuilder();
            		partialHost.append(host);
            		partialHost.append(".");
            		
            		// attempt partial comparison
            		if (actualHost.startsWith(partialHost.toString())) {
            			return true;
            		}
            	}
            	
            	// collect resolved host
            	lastResolvedHosts[index] = actualHost;
            	index++;
            }
                	
            return false;
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
		description.appendText("resolution of IP address ");
		description.appendValue( ipAddress  );
		description.appendText(" to host ");
		description.appendValue( lastHost );		
	}
	
	@Override
	protected void describeMismatchSafely(String item, Description mismatchDescription) {
		
        // create description
    	if(lastException == null) {
        	mismatchDescription.appendText(" resolved to host(s) ");    	    	
        	mismatchDescription.appendValue( lastResolvedHosts );    		
    	} else {
    		mismatchDescription.appendText(" failed due to exception ");    		
    		mismatchDescription.appendValue(lastException.toString());
    	}    	    	
	}

	@Factory
    public static Matcher<String> isHostResolvableToIpAddress(String ipAddress) {
        return new IsIpAddressResolvableToHost(ipAddress);
    }
    
}
