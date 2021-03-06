/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.execution.scheduled;

/**
 * Exception used to signal scheduled operation wasn't found in scheduled
 * operation repository.
 */
public class ScheduledOperationNotFoundException extends RuntimeException {

	/**
	 * ScheduledOperationNotFoundException constructor.
	 * 
	 * @param message
	 *            error message.
	 */
	public ScheduledOperationNotFoundException(String message) {
		super(message);
	}

	/**
	 * Serial version ID.
	 */
	static final long serialVersionUID = -6585532255652765155L;

}
