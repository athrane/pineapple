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

import java.util.Iterator;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Iterator which supports iteration over the first property from each sequence.
 */
public class InterSequencePropertyIteratorImpl implements Iterable<Object> {

	class InterIteratorImpl implements Iterator<Object> {

	    /**
	     * Logger object.
	     */
	    Logger logger = Logger.getLogger( this.getClass().getName() );
		
		/**
		 * first array index.
		 */
	    private static final int FIRST_INDEX = 0;
		
		/**
		 * HTTP invocation sequences.
		 */
		HttpInvocationSequence[] sequences;
		
		/**
		 * Index.
		 */
		int index;

		/**
		 * Property info which controls the iteration.
		 */		
		ResponsePropertyInfo propertyInfo;
		
		public InterIteratorImpl(ResponsePropertyInfo propertyInfo, HttpInvocationSequence[] sequences) {
			this.propertyInfo = propertyInfo;
			this.sequences = sequences;
			this.index = 0;
		}
		
		public boolean hasNext() {
			return (index < sequences.length);
		}

		public Object next() {
			
			// get sequence
			HttpInvocationSequence sequence = sequences[index];
			
			// get result
			HttpInvocationResult result = sequence.getSequence()[FIRST_INDEX];
			
			// get property value
			Object value = result.getProperty(propertyInfo.getName());

			// increment index
			index++;
			
	        // log debug message
	        if ( logger.isDebugEnabled() ) {
	            StringBuilder message = new StringBuilder();
	            message.append( "Iterator returned property value <" );
	            message.append( ReflectionToStringBuilder.toString( value) );
	            message.append( ">." );            
	            logger.debug( message.toString() );        	        	
	        }	        
	        
	        return value;
		}

		public void remove() {
		}
				
	}
	
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	
	/**
	 * HTTP invocation result set.
	 */
	HttpInvocationsSet resultset;

	/**
	 * Property info which controls the iteration.
	 */
	ResponsePropertyInfo propertyInfo;
		
	/**
	 * InterSequencePropertyIteratorImpl constructor.
	 * 
     * @param propertyInfo Property info which defines the property which there should
     * be iterated over.  
	 * 
	 * @param resultSet HTTP invocation result which is iterated over.  
	 */
	public InterSequencePropertyIteratorImpl( ResponsePropertyInfo propertyInfo, HttpInvocationsSet resultSet) {		
		this.propertyInfo = propertyInfo;		
		this.resultset = resultSet;
	}

	public Iterator<Object> iterator() {
		
		// get sequences
		HttpInvocationSequence[] sequences = this.resultset.getSequences();

        // log debug message
        if ( logger.isDebugEnabled() ) {
            StringBuilder message = new StringBuilder();
            message.append( "Created inter sequence property iterator for property <" );
            message.append( propertyInfo.getName() );
            message.append( "> which will iterate over sequence of <" );
            message.append( sequences.length );        
            message.append( "> results." );            
            logger.debug( message.toString() );        	        	
        }		
		
		return new InterIteratorImpl(propertyInfo, sequences);
	}

	
}
