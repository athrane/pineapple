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
 * Helper class which collects two cookie maps into a pair
 * which is used for matching by a matcher.  
 */
public class CookieMapPair {

	/**
	 * Cookie map #1.
	 */		
	CookieMap  map1;

	/**
	 * Cookie map #2.
	 */		
	CookieMap map2;
	
	/**
	 * CookieMapPair constructor.
	 * 
	 * @param map1 First map in pair.
	 * @param map2 Second map in pair.
	 */
	public CookieMapPair(CookieMap map1, CookieMap map2) {
		this.map1 = map1;
		this.map2 = map2;
	}
	
	/**
	 * Return first map in pair.
	 * 
	 * @return first map in pair.
	 */
	public CookieMap getFirstMap() {
		return this.map1;
	}

	/**
	 * Return second map in pair.
	 * 
	 * @return second map in pair.
	 */		
	public CookieMap getSecondMap() {
		return this.map2;
	}	
}
