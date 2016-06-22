/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.credential;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Implementation of the {@link CredentialInfo} interface.
 */
public class CredentialInfoImpl implements CredentialInfo {
    /**
     * Credential ID.
     */
    String id;

    /**
     * user.
     */
    String user;

    /**
     * password.
     */
    String password;

    /**
     * CredentialInfoImpl constructor.
     * 
     * @param id
     *            resource ID.
     * @param user
     *            user name.
     * @param password
     *            password.
     */
    public CredentialInfoImpl(String id, String user, String password) {
	// validate parameters
	Validate.notNull(id, "id is undefined.");
	Validate.notNull(user, "user is undefined.");
	Validate.notNull(password, "password is undefined.");
	this.id = id;
	this.user = user;
	this.password = password;
    }

    @Override
    public String getId() {
	return id;
    }

    @Override
    public String getUser() {
	return user;
    }

    @Override
    public String getPassword() {
	return password;
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }

}
