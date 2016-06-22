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



/**
 * Defines an ordered sequence of HTTP invocation results.
 */
public interface HttpInvocationSequence
{
    
    /**
     * Append an invocation result to the end of the sequence
     *  
     * @param result the invocation result to add.
     */
    public void appendResult( HttpInvocationResult result );
    
    /**
     * Return the invocation sequence as an array.
     * 
     * @return The invocation sequence as an array.
     */
    public HttpInvocationResult[] getSequence();
    
    /**
     * Get the sequence name.
     * 
     * @return the sequence name.
     */
    public String getName();

    /**
     * Returns iterator which supports iteration over the results in the sequence.
     * 
     * @param propertyInfo Property info which defines the property which there should
     * be iterated over. 
     * 
     * @return iterator which supports iteration over the results in the sequence.
     */
	public Iterable<Object> getIntraSequencePropertyIterator( ResponsePropertyInfo propertyInfo );
    
}
