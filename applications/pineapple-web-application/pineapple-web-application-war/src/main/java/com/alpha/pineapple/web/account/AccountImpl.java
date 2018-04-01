/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2016 Allan Thrane Andersen..
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
package com.alpha.pineapple.web.account;

/**
 * Default implementation of the {@linkplain Account} interface.
 */
public class AccountImpl implements Account {

	/**
	 * User name.
	 */
	String username;

	/**
	 * AccountImpl constructor.
	 * 
	 * @param username
	 *            user name.
	 */
	public AccountImpl(String username) {
		super();
		this.username = username;
	}

	@Override
	public String getUsername() {
		return username;
	}

}
