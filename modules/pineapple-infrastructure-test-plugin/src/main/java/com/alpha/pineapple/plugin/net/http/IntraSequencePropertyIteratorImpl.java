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

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

/**
 * TODO Write class/interface description here!
 */
public class IntraSequencePropertyIteratorImpl implements Iterable<Object> {

	class IntraIteratorImpl implements Iterator<Object> {

		/**
		 * Logger object.
		 */
		Logger logger = Logger.getLogger(this.getClass().getName());

		Iterator<HttpInvocationResult> resultIterator;
		ResponsePropertyInfo propertyInfo;

		IntraIteratorImpl(ResponsePropertyInfo propertyInfo, Iterator<HttpInvocationResult> resultIterator) {
			this.propertyInfo = propertyInfo;
			this.resultIterator = resultIterator;
		}

		public boolean hasNext() {
			return resultIterator.hasNext();
		}

		public Object next() {

			// get result
			HttpInvocationResult result = resultIterator.next();

			// get property value
			Object value = result.getProperty(propertyInfo.getName());

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Iterator returned property value <");
				message.append(ReflectionToStringBuilder.toString(value));
				message.append(">.");
				logger.debug(message.toString());
			}

			return value;
		}

		public void remove() {
		}

	}

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * HTTP invocation sequence.
	 */
	HttpInvocationSequence sequence;

	/**
	 * Property info which controls the iteration.
	 */
	ResponsePropertyInfo propertyInfo;

	public IntraSequencePropertyIteratorImpl(ResponsePropertyInfo propertyInfo, HttpInvocationSequence sequence) {
		this.propertyInfo = propertyInfo;
		this.sequence = sequence;
	}

	public Iterator<Object> iterator() {

		// get results
		HttpInvocationResult[] results = this.sequence.getSequence();

		// create result iterator
		Iterator<HttpInvocationResult> resultIterator = Arrays.asList(results).iterator();

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Created intra sequence property iterator for property <");
			message.append(propertyInfo.getName());
			message.append("> which will iterate over sequence of <");
			message.append(results.length);
			message.append("> results.");
			logger.debug(message.toString());
		}

		return new IntraIteratorImpl(propertyInfo, resultIterator);
	}

}
