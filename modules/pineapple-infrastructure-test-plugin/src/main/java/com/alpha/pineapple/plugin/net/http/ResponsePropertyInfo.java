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

import org.hamcrest.Matcher;

/**
 * Defines an property which is tested on {@link HttpInvocationResult} in a
 * {@link HttpInvocationsSet}. 
 * 
 * The property can define invariants for two different kinds of sequences in a
 * {@link HttpInvocationsSet}: intra sequence invariants and inter sequence invariants.
 * 
 * Intra sequence invariants defines an invariant on all {@link HttpInvocationResult} 
 * from all the {@link HttpInvocationSequence} in a {@link HttpInvocationResult}.
 * 
 * Inter sequence invariants defines an invariant on the first {@link HttpInvocationResult} from
 * each {@link HttpInvocationSequence} in a {@link HttpInvocationsSet}.
 * 
 */
public interface ResponsePropertyInfo {

	/**
	 * Returns true if the matcher for testing a intra sequence invariant is defined.
	 *  
	 * @return true if the matcher for testing a intra sequence invariant is defined.
	 */
	boolean isTestingIntraSequence();	

	/**
	 * Returns true if the matcher for testing a inter sequence invariant is defined.
	 *  
	 * @return true if the matcher for testing a inter sequence invariant is defined.
	 */	
	boolean isTestingInterSequences();

	/**
	 * Returns the matcher for testing a intra sequence invariant is defined. If
	 * the matcher isn't defined then null is returned.
	 *  
	 * @return Returns the matcher for testing a intra sequence invariant is 
	 * defined. If the matcher isn't defined then null is returned.
	 */	
	Matcher<?> getIntraSequenceMatcher();

	/**
	 * Returns the matcher for testing a inter sequence invariant is defined. If
	 * the matcher isn't defined then null is returned.
	 *  
	 * @return Returns the matcher for testing a inter sequence invariant is 
	 * defined. If the matcher isn't defined then null is returned.
	 */		
	Matcher<?> getInterSequenceMatcher();

	/**
	 * Return XPath expression which should be use to extract the property values from a 
	 * HTTP response.
	 *  
	 * @return XPath expression which should be use to extract the property values from a 
	 * HTTP response.
	 */
	String getXPath();

	/**
	 * Get property name.
	 * 
	 * @return property name.
	 */
	String getName();
	
}
