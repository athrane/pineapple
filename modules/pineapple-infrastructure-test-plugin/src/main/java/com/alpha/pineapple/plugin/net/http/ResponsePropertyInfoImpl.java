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

public class ResponsePropertyInfoImpl implements ResponsePropertyInfo {

	boolean isTestingInterSequences;

	boolean isTestingIntraSequence;

	Matcher<?> intraSequenceMatcher;

	Matcher<?> interSequenceMatcher;

	String name;

	String xpath;

	/**
	 * ResponsePropertyInfoImpl
	 * 
	 * @param intraSequenceMatcher
	 * @param interSequenceMatcher
	 */
	public ResponsePropertyInfoImpl(String name, String xpath, Matcher<?> intraSequenceMatcher,
			Matcher<?> interSequenceMatcher) {

		super();
		this.name = name;
		this.xpath = xpath;
		this.intraSequenceMatcher = intraSequenceMatcher;
		this.interSequenceMatcher = interSequenceMatcher;
	}

	public Matcher<?> getInterSequenceMatcher() {
		return interSequenceMatcher;
	}

	public Matcher<?> getIntraSequenceMatcher() {
		return intraSequenceMatcher;
	}

	public boolean isTestingInterSequences() {
		return (interSequenceMatcher != null);
	}

	public boolean isTestingIntraSequence() {
		return (intraSequenceMatcher != null);
	}

	public String getName() {
		return name;
	}

	public String getXPath() {
		return xpath;
	}

}
