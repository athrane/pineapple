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

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.PBEStringEncryptor;

import com.alpha.pineapple.credential.ConfigurationInfo;
import com.alpha.pineapple.credential.CredentialConfigurationMarshaller;
import com.alpha.pineapple.credential.CredentialInfo;
import com.alpha.pineapple.credential.CredentialInfoFactory;
import com.alpha.pineapple.credential.CredentialsFileNotFoundException;
import com.alpha.pineapple.credential.EnvironmentInfo;
import com.alpha.pineapple.credential.EnvironmentInfoImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.ObjectFactory;

/**
 * Implementation of the {@linkplain CredentialConfigurationMarshaller}
 * interface which support encryption and decryption of passwords.
 */
public class PasswordEncryptingCredentialConfigurationMarshallerImpl implements CredentialConfigurationMarshaller {

	/**
	 * JAXB object factory.
	 */
	ObjectFactory factory = new ObjectFactory();

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Credential configuration marshaller.
	 */
	@Resource
	CredentialConfigurationMarshaller defaultCredentialConfigurationMarshaller;

	/**
	 * Credential info factory.
	 */
	@Resource
	CredentialInfoFactory credentialInfoFactory;

	/**
	 * Text encryptor.
	 */
	@Resource
	PBEStringEncryptor textEncryptor;

	@Override
	public void save(Configuration configuration) {
		defaultCredentialConfigurationMarshaller.save(configuration);
	}

	@Override
	public Configuration load(File credentialsFile) throws CredentialsFileNotFoundException {
		return defaultCredentialConfigurationMarshaller.load(credentialsFile);
	}

	@Override
	public Configuration map(ConfigurationInfo info) {
		return defaultCredentialConfigurationMarshaller.map(info);
	}

	@Override
	public ConfigurationInfo map(Configuration configuration) {

		ConfigurationInfo info = defaultCredentialConfigurationMarshaller.map(configuration);

		// initializeEncryptorIfRequired();

		EnvironmentInfo[] environments = info.getEnvironments();
		if (environments == null)
			return info;

		// iterate over credentials
		boolean saveConfiguration = false;
		for (EnvironmentInfo environmentInfo : environments) {

			// validate encrypted password
			if (validateEncryptedPasswords(environmentInfo)) {
				saveConfiguration = true;
			}

			// encrypt non encrypted password
			if (processUnencryptedPasswords(environmentInfo)) {
				saveConfiguration = true;
			}
		}

		// exit if there is no need to save
		if (!saveConfiguration)
			return info;

		// save
		Configuration updatedConfiguration = map(info);
		defaultCredentialConfigurationMarshaller.save(updatedConfiguration);
		return info;
	}

	/**
	 * Use encryptor to encrypt random text to force encryptor initialization if
	 * required.
	 */
	void initializeEncryptorIfRequired() {

		// decrypt random password to force initialization
		textEncryptor.encrypt(RandomStringUtils.randomAlphabetic(10));
	}

	/**
	 * Process environment and validate encrypted password. Returns true if
	 * environment contained password which failed validation
	 * 
	 * Encrypted password are validated by decrypting them. If decryption fails then
	 * the password is treated as an unencrypted and is encrypted.
	 * 
	 * @param environmentInfo
	 *            environment info.
	 * 
	 * @return Returns true if environment contained password which failed
	 *         validation. Passwords which failed validation is encrypted.
	 */
	boolean validateEncryptedPasswords(EnvironmentInfo environmentInfo) {
		boolean decryptionFailed = false;

		// exit if no credentials are defined
		CredentialInfo[] credentials = environmentInfo.getCredentials();
		if (credentials == null)
			return false;

		// iterate over credentials
		for (CredentialInfo credentialInfo : credentials) {

			// skip non prefixed passwords
			if (!isPasswordValuePrefixed(credentialInfo))
				continue;

			// validate password can be decrypted successfully
			if (isPasswordEncrypted(credentialInfo))
				continue;

			// create credential with encrypted password
			String password = credentialInfo.getPassword();
			password = password.substring(ENCRYPTED_PREFIX.length());
			CredentialInfo info = credentialInfoFactory.createCredentialInfo(credentialInfo.getId(),
					credentialInfo.getUser(), password);

			// update environment info with updated credential
			EnvironmentInfoImpl environmentInfoImpl = (EnvironmentInfoImpl) environmentInfo;
			environmentInfoImpl.deleteCredential(credentialInfo);
			environmentInfoImpl.addCredential(info);

			logInfoMessageForEncryptionOfPrefixedPassword(credentialInfo, environmentInfo);

			decryptionFailed = true;
		}
		return decryptionFailed;
	}

	/**
	 * Process environment and encrypt unencrypted password. Returns true if
	 * environment contained password which wasn't encrypted.
	 * 
	 * If an unencrypted password is encountered, e.g. without the prefix, then the
	 * password encrypted.
	 * 
	 * @param environmentInfo
	 *            environment info.
	 * 
	 * @return Returns true if environment contained password which wasn't
	 *         encrypted. Unencrypted passwords are encrypted.
	 */
	boolean processUnencryptedPasswords(EnvironmentInfo environmentInfo) {
		boolean decryptionFailed = false;

		// exit if no credentials are defined
		CredentialInfo[] credentials = environmentInfo.getCredentials();
		if (credentials == null)
			return false;

		// iterate over credentials
		for (CredentialInfo credentialInfo : credentials) {

			// skip non prefixed passwords
			if (isPasswordValuePrefixed(credentialInfo))
				continue;

			// create credential with encrypted password
			CredentialInfo info = credentialInfoFactory.createCredentialInfo(credentialInfo.getId(),
					credentialInfo.getUser(), credentialInfo.getPassword());

			// update environment info with updated credential
			EnvironmentInfoImpl environmentInfoImpl = (EnvironmentInfoImpl) environmentInfo;
			environmentInfoImpl.deleteCredential(credentialInfo);
			environmentInfoImpl.addCredential(info);

			logInfoMessageForEncryptionOfPassword(credentialInfo, environmentInfo);

			decryptionFailed = true;
		}
		return decryptionFailed;
	}

	/**
	 * Returns true if password is encrypted, e.g. value can be decrypted
	 * successfully.
	 *
	 * @param credentialInfo
	 *            info to test.
	 * 
	 * @return true if password is encrypted, e.g. value can be decrypted
	 *         successfully.
	 */
	boolean isPasswordEncrypted(CredentialInfo credentialInfo) {
		String password = credentialInfo.getPassword();
		password = password.substring(ENCRYPTED_PREFIX.length());
		try {
			textEncryptor.decrypt(password);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Returns true if password value is prefixed with "encrypted:".
	 * 
	 * @param credentialInfo
	 *            info to test.
	 * 
	 * @return true if password value is prefixed with "encrypted:".
	 */
	boolean isPasswordValuePrefixed(CredentialInfo credentialInfo) {
		String password = credentialInfo.getPassword();
		if (password == null)
			return false;
		return password.startsWith(ENCRYPTED_PREFIX);
	}

	/**
	 * Implementation note:
	 * 
	 * Map credential info to credential. Prefix is removed and then password is
	 * decrypted prior to adding it to the created credential.
	 */
	@Override
	public Credential mapToCredential(CredentialInfo credentialInfo) {
		Credential credential = factory.createCredential();
		credential.setId(credentialInfo.getId());
		credential.setUser(credentialInfo.getUser());
		String password = credentialInfo.getPassword();
		password = password.substring(ENCRYPTED_PREFIX.length());
		credential.setPassword(textEncryptor.decrypt(password));
		return credential;
	}

	/**
	 * Log info message.
	 * 
	 * @param credentialInfo
	 *            credential info.
	 * @param environmentInfo
	 *            environment info.
	 */
	void logInfoMessageForEncryptionOfPrefixedPassword(CredentialInfo credentialInfo, EnvironmentInfo environmentInfo) {
		Object[] args = { credentialInfo.getId(), environmentInfo.getId() };
		String message = messageProvider.getMessage("peccm.encrypt_prefixed_password_info", args);
		logger.info(message);
	}

	/**
	 * Log info message.
	 * 
	 * @param credentialInfo
	 *            credential info.
	 * @param environmentInfo
	 *            environment info.
	 */
	void logInfoMessageForEncryptionOfPassword(CredentialInfo credentialInfo, EnvironmentInfo environmentInfo) {
		Object[] args = { credentialInfo.getId(), environmentInfo.getId() };
		String message = messageProvider.getMessage("peccm.encrypt_password_info", args);
		logger.info(message);
	}

}
