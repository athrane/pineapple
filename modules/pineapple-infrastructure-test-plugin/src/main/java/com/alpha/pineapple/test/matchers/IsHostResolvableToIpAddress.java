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

import java.net.InetAddress;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.xbill.DNS.Address;

/**
 * Matches if a TCP host name can be resolved to any IP address, e.g. a forward
 * DNS lookup.
 */
public class IsHostResolvableToIpAddress extends TypeSafeMatcher<String> {

	/**
	 * Last recorded exception.
	 */
	Exception lastException;

	/**
	 * Last recorded resolved IP address.
	 */
	String lastIpAddress;

	/**
	 * Last recorded resolved host name.
	 */
	String lastHost;

	public boolean matchesSafely(String host) {

		try {
			// store input
			lastHost = host;
			lastException = null;
			lastIpAddress = null;

			// get actual IP address
			InetAddress inetAddress = Address.getByName(host);
			String ipAddress = inetAddress.getHostAddress();

			// store input
			lastIpAddress = ipAddress;

			// assert
			if (ipAddress == null)
				return false;
			if (ipAddress.length() == 0)
				return false;

			return true;
		} catch (Exception e) {
			// record exception
			lastException = e;

			// fail test
			return false;
		}

	}

	public void describeTo(Description description) {
		description.appendText("host ");
		description.appendValue(lastHost);
		description.appendText(" could resolve to IP address ");
		description.appendValue(lastIpAddress);
	}

	@Override
	protected void describeMismatchSafely(String item, Description mismatchDescription) {
		// create description
		mismatchDescription.appendText("failed to resolve host name ");
		mismatchDescription.appendValue(item);
		mismatchDescription.appendText(" to IP address ");

		if (lastException != null) {
			mismatchDescription.appendText("due to exception ");
			mismatchDescription.appendValue(lastException.toString());
		}

	}

	/**
	 * Return last resolved IP address as string.
	 * 
	 * @return last resolved IP address as string.
	 */
	public String getLastIpAddress() {
		return lastIpAddress;
	}

	@Factory
	public static Matcher<String> isHostResolvableToIpAddress() {
		return new IsHostResolvableToIpAddress();
	}

}
