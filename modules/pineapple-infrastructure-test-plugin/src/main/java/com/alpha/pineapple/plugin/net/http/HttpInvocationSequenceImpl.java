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
 * Implementation of the <code>HttpInvocationSequence</code> interface.
 */
public class HttpInvocationSequenceImpl implements HttpInvocationSequence {
	/**
	 * Ordered sequence of HTTP invocation results.
	 */
	ArrayList<HttpInvocationResult> sequence;

	/**
	 * Sequence name.
	 */
	String name;

	/**
	 * HttpInvocationSequence no-arg constructor.
	 */
	public HttpInvocationSequenceImpl(String name) {
		super();
		this.sequence = new ArrayList<HttpInvocationResult>();
		this.name = name;
	}

	/**
	 * Append an invocation result to the end of the sequence
	 * 
	 * @param result
	 *            the invocation result to add.
	 */
	public void appendResult(HttpInvocationResult result) {
		this.sequence.add(result);
	}

	/**
	 * Return the invocation sequence as an array.
	 * 
	 * @return The invocation sequence as an array.
	 */
	public HttpInvocationResult[] getSequence() {
		return sequence.toArray(new HttpInvocationResult[sequence.size()]);
	}

	/**
	 * Get the sequence name.
	 * 
	 * @return the sequence name.
	 */
	public String getName() {
		return this.name;
	}

	public Iterable<Object> getIntraSequencePropertyIterator(ResponsePropertyInfo propertyInfo) {
		return new IntraSequencePropertyIteratorImpl(propertyInfo, this);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
