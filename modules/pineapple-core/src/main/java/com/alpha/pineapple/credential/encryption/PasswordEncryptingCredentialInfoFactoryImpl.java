/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.credential.encryption;

import static com.alpha.pineapple.CoreConstants.ENCRYPTED_PREFIX;

import javax.annotation.Resource;

import org.jasypt.encryption.pbe.PBEStringEncryptor;

import com.alpha.pineapple.credential.CredentialInfo;
import com.alpha.pineapple.credential.CredentialInfoFactory;
import com.alpha.pineapple.credential.CredentialInfoImpl;

/**
 * Implementation of the {@linkplain CredentialInfoFactory} which creates
 * credentials with encrypted passwords.
 */
public class PasswordEncryptingCredentialInfoFactoryImpl implements CredentialInfoFactory {

	/**
	 * Text encryptor.
	 */
	@Resource
	PBEStringEncryptor textEncryptor;

	@Override
	public CredentialInfo createCredentialInfo(String id, String user, String password) {
		String encryptedPassword = new StringBuilder().append(ENCRYPTED_PREFIX).append(textEncryptor.encrypt(password))
				.toString();
		return new CredentialInfoImpl(id, user, encryptedPassword);
	}

}
