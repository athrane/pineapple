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
 * Define the results of a set of HTTP invocation sequences. 
 */
public interface HttpInvocationsSet
{
    
    /**
     * Append an invocation sequence to the end of the set.
     *  
     * @param result the invocation sequence to add.
     */
    public void appendResult( HttpInvocationSequence sequence );
    
    /**
     * Return the invocation sequences as an array.
     * 
     * @return The invocation sequences as an array.
     */
    public HttpInvocationSequence[] getSequences();

    /**
     * Returns iterator which supports iteration over the first property from each sequence.
     * 
     * @param propertyInfo Property info which defines the property which there should
     * be iterated over.  
     * 
     * @return iterator which supports iteration over the first property from each sequence.
     */
	public Iterable<Object> getInterSequencePropertyIterator(ResponsePropertyInfo propertyInfo);
        
}
