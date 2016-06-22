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


package com.alpha.pineapple.plugin.net.http;

import java.util.ArrayList;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;


/**
 * Implementation of the <code>HttpInvocationsSet</code> 
 */
public class HttpInvocationsSetImpl implements HttpInvocationsSet 
{

    /**
     * Ordered list sequence of HTTP invocation sequences.
     */
    ArrayList<HttpInvocationSequence> sequences;
    
    /**
     * URL sequence which should be accessed.
     */
    public String[] urls;

    /**
     * The number of times the URL sequence should be requested.
     */
    public int requests;

    /**
     * Defines whether the HTTP client / session should be reset before each sequence.
     */
    public boolean reset;

    /**
     * Defines optional proxy host.
     */           
    public String proxyHost;

    /**
     * Defines optional proxy port.
     */        
    public int proxyPort;
    
    
    /** 
     * HttpInvocationSequence constructor.
     * 
     * @param urls URL sequence which should be accessed.
     * @param requests The number of times the URL sequence should be requested.
     * @param reset Whether the HTTP client (and it's session state) should be reset before each sequence
     * @param proxyHost Optional proxy host.    
     * @param proxyPort Optional proxy port.
     */
    public HttpInvocationsSetImpl(String[] urls, int requests, boolean reset, String proxyHost, int proxyPort)
    {
        super();
        this.urls = urls;
        this.requests = requests;
        this.reset = reset;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.sequences = new ArrayList<HttpInvocationSequence>();
    }

    /** 
     * HttpInvocationSequence no-arg constructor.
     * 
     */
    public HttpInvocationsSetImpl()
    {
    	this( new String[0], 0, true, "", 0 );
    }    
    
    /**
     * Append an invocation sequence to the end of the set.
     *  
     * @param result the invocation sequence to add.
     */
    public void appendResult( HttpInvocationSequence sequence ) {
        this.sequences.add( sequence );
    }
    
    /**
     * Return the invocation sequences as an array.
     * 
     * @return The invocation sequences as an array.
     */
    public HttpInvocationSequence[] getSequences() {
        return sequences.toArray( new HttpInvocationSequence[sequences.size()] );
    }
    
	public Iterable<Object> getInterSequencePropertyIterator( ResponsePropertyInfo propertyInfo ) {		
		return new InterSequencePropertyIteratorImpl(propertyInfo, this);
	}
        
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString( this );
    }    
    
}
