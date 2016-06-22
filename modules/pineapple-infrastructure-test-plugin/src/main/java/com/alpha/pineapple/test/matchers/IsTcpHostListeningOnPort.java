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

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matches if a TCP host listens on a specific port.
 */
public class IsTcpHostListeningOnPort extends TypeSafeMatcher<Integer> {

    /**
     * Socket timeout in milliseconds.
     */
    static final int SOCKET_TIMEOUT = 1000;
	
	/**
	 * TCP host name.
	 */
	String host;

	/**
	 * Last recorded exception.
	 */
	Exception lastException; 

	/**
	 * Last recorded port.
	 */	
	Integer lastPort;

	/**
	 * IsTcpHostListeningOnPort constructor.
	 * 
	 * @param host TCP host name.
	 */
	public IsTcpHostListeningOnPort(String host) {
		this.host = host;		
	}
		
	public boolean matchesSafely(Integer port) {

        // store port
        lastPort = port;
		
    	// declare socket
    	Socket socket = null; 
    	    	    	    	
        try
        {              	        	        	
            // create end point
            InetSocketAddress endpoint = new InetSocketAddress( host, port.intValue());
            
            // create socket
            socket = new Socket();
            
            // connect 
            socket.connect( endpoint, SOCKET_TIMEOUT);
            
            // assert
            return (socket.isConnected());
        }
        catch ( ConnectException e) {
        	
        	// record exception
        	this.lastException =  e;
        	
        	// fail test
        	return false;
        }
        catch ( Exception e )
        {
        	// record exception
        	this.lastException =  e;
        	
        	// fail test
        	return false;
        } 
        finally {
        
        	if (socket != null ) {
        		try {
        			
                    // close socket
					socket.close();
					
				} catch (IOException e) {
					
		        	// record exception
		        	this.lastException =  e;
		        	
		        	// fail test
		        	return false;
				}
        	}                                	
        }
		
	}

	public void describeTo(Description description) {
		description.appendText("TCP connection to ");		
		description.appendValue(host);
		description.appendText(" on port ");		
		description.appendValue(lastPort);					
		
	}
	
	@Override
	protected void describeMismatchSafely(Integer item, Description mismatchDescription) {
        // create description
    	mismatchDescription.appendText("failed to connect on port ");    	
    	mismatchDescription.appendValue(item.intValue());	
    	
    	if (lastException != null) {
    		mismatchDescription.appendText(" due to exception ");    		
    		mismatchDescription.appendValue(lastException.toString());
    	}
    	    	
	}

	@Factory
    public static Matcher<Integer> isTcpHostListeningOnPort(String host) {
        return new IsTcpHostListeningOnPort(host);
    }
    
}
