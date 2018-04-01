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

package com.alpha.javautils;

import java.util.Random;

/**
 * Helper class for network operations.
 */
public class NetworkUtils {

	/**
	 * Maximum legal IP port.
	 */
	static final int MAX_LEGAL_IP_PORT = 65535;

	/**
	 * Minimum legal IP port.
	 */
	static final int MIN_LEGAL_IP_PORT = 1;

	/**
	 * Error message for port number validation.
	 */
	static final String ILLEGAL_PORT_TEXT = "portNumber is illegal port number. Legal values are between 1 to 65535";

	/**
	 * Validate that IP port number is a valid, i.e. an integer between 1 to 65535.
	 * 
	 * @param portNumber
	 *            IP port number.
	 * 
	 * @throws IllegalArgumentException
	 *             if port number is invalid.
	 */
	public static void validatePort(int portNumber) throws IllegalArgumentException {
		if (!isValidPort(portNumber))
			throw new IllegalArgumentException(ILLEGAL_PORT_TEXT);
	}

	/**
	 * Returns true if IP port number is a valid, i.e. an integer between 1 to
	 * 65535.
	 * 
	 * @param portNumber
	 *            IP port number.
	 * 
	 * @return returns true if port number is an valid IP port.
	 */
	public static boolean isValidPort(int portNumber) {
		if (portNumber < MIN_LEGAL_IP_PORT)
			return false;
		if (portNumber > MAX_LEGAL_IP_PORT)
			return false;
		return true;
	}

	/**
	 * Return a random legal port, i.e. an integer between 1 to 65535.
	 * 
	 * @param random
	 *            random object.
	 * @return Return a random legal port.
	 */
	public static int getRandomPort(Random random) {
		return random.nextInt(MAX_LEGAL_IP_PORT) + 1;
	}

}
