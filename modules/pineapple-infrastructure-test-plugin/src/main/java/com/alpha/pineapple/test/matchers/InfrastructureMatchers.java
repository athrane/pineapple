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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import com.alpha.pineapple.plugin.net.http.CookieMapPair;
import com.alpha.pineapple.plugin.net.http.HttpInvocationSequence;
import com.alpha.pineapple.plugin.net.http.HttpInvocationsSet;

/**
 *
 * Helper class which is provides static factory methods for easy access to
 * matcher instances.
 * 
 * Inspired by the {@link CoreMatchers}.
 */
public class InfrastructureMatchers {

	@Factory
	public static Matcher<HttpInvocationSequence> isSequenceEmpty() {
		return IsSequenceEmpty.isSequenceEmpty();
	}

	@Factory
	public static <T> Matcher<HttpInvocationsSet> isSetEmpty() {
		return IsSetEmpty.isSetEmpty();
	}

	@Factory
	public static Matcher<CookieMapPair> cookieMapsPairOfEqualSize() {
		return IsCookieMapsPairOfEqualSize.equalSize();
	}

	@Factory
	public static Matcher<CookieMapPair> cookieMapsPairContainingEqualKeys() {
		return IsCookieMapsPairContainingEqualKeys.containsEqualKeys();
	}

	@Factory
	public static Matcher<CookieMapPair> cookieMapsPairContainingEqualValues() {
		return IsCookieMapsPairContainingEqualValues.containsEqualValues();
	}

	@Factory
	public static Matcher<CookieMapPair> cookieMapsPairContainingNonEqualValues() {
		return IsCookieMapsPairContainingNonEqualValues.containsNonEqualValues();
	}

	@Factory
	public static Matcher<Iterable<Cookie[]>> cookieMapsContainsEqualValues() {
		return IsCookieMapsContainingEqualValues.containsEqualValues();
	}

	@Factory
	public static Matcher<Iterable<Cookie[]>> cookieMapsContainsNonEqualValues() {
		return IsCookieMapsContainingNonEqualValues.containsNonEqualValues();
	}

	@Factory
	public static Matcher<Iterable<Header[]>> isHeaderCollectionContaining(Header[] expectedHeaders) {
		return IsHeadersCollectionContaining.contains(expectedHeaders);
	}

	@Factory
	public static Matcher<Iterable<Header[]>> isHeaderCollectionContaining(Map<String, String> expectedHeaders) {
		return IsHeadersCollectionContaining.contains(expectedHeaders);
	}

	@Factory
	public static Matcher<Header[]> isHeadersContaining(Header[] expectedHeaders) {
		return IsHeadersContaining.contains(expectedHeaders);
	}

	@Factory
	public static Matcher<Header[]> isHeadersContaining(Map<String, String> expectedHeaders) {
		return IsHeadersContaining.contains(expectedHeaders);
	}

	@Factory
	public static Matcher<Header[]> isHeadersNotContaining(String[] expectedHeaders) {
		return IsHeadersNotContaining.doesntContain(expectedHeaders);
	}

	@Factory
	public static Matcher<Integer> tcpHostListensOnPort(String host) {
		return IsTcpHostListeningOnPort.isTcpHostListeningOnPort(host);
	}

	@Factory
	public static Matcher<String> isHostResolvableToIpAddress() {
		return IsHostResolvableToIpAddress.isHostResolvableToIpAddress();
	}

	@Factory
	public static Matcher<String> isHostResolvableToSpecificIpAddress(String ipAddress) {
		return IsHostResolvableToSpecificIpAddress.isHostResolvableToSpecificIpAddress(ipAddress);
	}

	@Factory
	public static Matcher<String> isIpAddressResolvableToHost(String ipAddress) {
		return IsIpAddressResolvableToHost.isHostResolvableToIpAddress(ipAddress);
	}

}
