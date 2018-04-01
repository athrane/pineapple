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

import static com.alpha.pineapple.CoreConstants.FILE_ENCODING_UTF8;

import java.io.File;
import java.io.IOException;
import java.security.Provider;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.config.PBEConfig;
import org.jasypt.exceptions.EncryptionInitializationException;
import org.jasypt.salt.SaltGenerator;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;

/**
 * Implementation of the {@linkplain PBEConfig} interface which support loading
 * of Jasypt password from a UTF-8 encoded password file.
 * 
 * If no password exists then a new password is generated and stored in a
 * password file. The password file is then loaded.
 *
 */
public class FileBasedPasswordPBEConfigImpl implements PBEConfig {

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
	 * Runtime directory provider.
	 */
	@Resource
	RuntimeDirectoryProvider runtimeDirectoryProvider;

	String algorithm = null;
	Integer keyObtentionIterations = null;
	SaltGenerator saltGenerator = null;
	String providerName = null;
	Provider provider = null;
	Integer poolSize = null;

	/**
	 * <p>
	 * Sets a value for the encryption algorithm
	 * </p>
	 * <p>
	 * This algorithm has to be supported by your JCE provider and, if this provider
	 * supports it, you can also specify <i>mode</i> and <i>padding</i> for it, like
	 * <tt>ALGORITHM/MODE/PADDING</tt>.
	 * </p>
	 * <p>
	 * Determines the result of: {@link #getAlgorithm()}
	 * </p>
	 * 
	 * @param algorithm
	 *            the name of the algorithm to be used
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Sets the password to be used for encryption.
	 * <p>
	 * Determines the result of: {@link #getPassword()}
	 * </p>
	 * 
	 * @param password
	 *            the password to be used.
	 */
	public void setPassword(String password) {
		// no operation
	}

	/**
	 * Sets the number of hashing iterations applied to obtain the encryption key.
	 * <p>
	 * Determines the result of: {@link #getKeyObtentionIterations()}
	 * </p>
	 * 
	 * @param keyObtentionIterations
	 *            the number of iterations.
	 */
	public void setKeyObtentionIterations(Integer keyObtentionIterations) {
		this.keyObtentionIterations = keyObtentionIterations;
	}

	/**
	 * Sets the number of hashing iterations applied to obtain the encryption key.
	 * <p>
	 * Determines the result of: {@link #getKeyObtentionIterations()}
	 * </p>
	 * 
	 * @since 1.4
	 * 
	 * @param keyObtentionIterations
	 *            the number of iterations.
	 */
	public void setKeyObtentionIterations(String keyObtentionIterations) {
		if (keyObtentionIterations != null) {
			try {
				this.keyObtentionIterations = new Integer(keyObtentionIterations);
			} catch (NumberFormatException e) {
				throw new EncryptionInitializationException(e);
			}
		} else {
			this.keyObtentionIterations = null;
		}
	}

	/**
	 * <p>
	 * Sets the salt generator.
	 * </p>
	 * <p>
	 * If not set, null will returned.
	 * </p>
	 * <p>
	 * Determines the result of: {@link #getSaltGenerator()}
	 * </p>
	 * 
	 * @param saltGenerator
	 *            the salt generator.
	 */
	public void setSaltGenerator(SaltGenerator saltGenerator) {
		this.saltGenerator = saltGenerator;
	}

	/**
	 * <p>
	 * Sets the salt generator.
	 * </p>
	 * <p>
	 * If not set, null will returned.
	 * </p>
	 * <p>
	 * Determines the result of: {@link #getSaltGenerator()}
	 * </p>
	 *
	 * @since 1.4
	 * 
	 * @param saltGeneratorClassName
	 *            the name of the salt generator class.
	 */
	public void setSaltGeneratorClassName(String saltGeneratorClassName) {
		if (saltGeneratorClassName != null) {
			try {
				Class<?> saltGeneratorClass = Class.forName(saltGeneratorClassName);
				this.saltGenerator = (SaltGenerator) saltGeneratorClass.newInstance();
			} catch (Exception e) {
				throw new EncryptionInitializationException(e);
			}
		} else {
			this.saltGenerator = null;
		}
	}

	/**
	 * <p>
	 * Sets the name of the security provider to be asked for the encryption
	 * algorithm. This provider should be already registered.
	 * </p>
	 * <p>
	 * If both the <tt>providerName</tt> and <tt>provider</tt> properties are set,
	 * only <tt>provider</tt> will be used, and <tt>providerName</tt> will have no
	 * meaning for the encryptor object.
	 * </p>
	 * <p>
	 * If not set, null will be returned.
	 * </p>
	 * <p>
	 * Determines the result of: {@link #getProviderName()}
	 * </p>
	 * 
	 * @since 1.3
	 * 
	 * @param providerName
	 *            the name of the security provider.
	 */
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	/**
	 * <p>
	 * Sets the security provider to be used for obtaining the encryption algorithm.
	 * This method is an alternative to both {@link #setProviderName(String)} and
	 * {@link #setProviderClassName(String)} and they should not be used altogether.
	 * The provider specified with {@link #setProvider(Provider)} does not have to
	 * be registered beforehand, and its use will not result in its being
	 * registered.
	 * </p>
	 * <p>
	 * If both the <tt>providerName</tt> and <tt>provider</tt> properties are set,
	 * only <tt>provider</tt> will be used, and <tt>providerName</tt> will have no
	 * meaning for the encryptor object.
	 * </p>
	 * <p>
	 * If not set, null will be returned.
	 * </p>
	 * <p>
	 * Determines the result of: {@link #getProvider()}
	 * </p>
	 * 
	 * @since 1.3
	 * 
	 * @param provider
	 *            the security provider object.
	 */
	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	/**
	 * <p>
	 * Sets the security provider to be used for obtaining the encryption algorithm.
	 * This method is an alternative to both {@link #setProviderName(String)} and
	 * {@link #setProvider(Provider)} and they should not be used altogether. The
	 * provider specified with {@link #setProviderClassName(String)} does not have
	 * to be registered beforehand, and its use will not result in its being
	 * registered.
	 * </p>
	 * <p>
	 * If both the <tt>providerName</tt> and <tt>provider</tt> properties are set,
	 * only <tt>provider</tt> will be used, and <tt>providerName</tt> will have no
	 * meaning for the encryptor object.
	 * </p>
	 * <p>
	 * If not set, null will be returned.
	 * </p>
	 * <p>
	 * Determines the result of: {@link #getProvider()}
	 * </p>
	 * 
	 * @since 1.4
	 * 
	 * @param providerClassName
	 *            the name of the security provider class.
	 */
	public void setProviderClassName(String providerClassName) {
		if (providerClassName != null) {
			try {
				Class<?> providerClass = Class.forName(providerClassName);
				this.provider = (Provider) providerClass.newInstance();
			} catch (Exception e) {
				throw new EncryptionInitializationException(e);
			}
		} else {
			this.provider = null;
		}
	}

	public String getAlgorithm() {
		return this.algorithm;
	}

	public String getPassword() {

		// get password file
		File passwordFile = runtimeDirectoryProvider.getCredentialProviderMasterPasswordFile();

		// if file doesn't exits generate and save password file
		if (!passwordFile.exists()) {

			try {
				// generate and save password
				String generatedPassword = String.valueOf(UUID.randomUUID());
				FileUtils.writeStringToFile(passwordFile, generatedPassword, FILE_ENCODING_UTF8);

				// log message
				logInfoMessagePasswordFileCreation(passwordFile);

			} catch (IOException e) {
				// create error message and throw exception
				Object[] args = { passwordFile, e.getCause() };
				String message = messageProvider.getMessage("mpepbec.save_password_error", args);
				throw new LoadCredentialProviderMasterPasswordFailedException(message, e);
			}
		}

		// load and return password
		try {
			return FileUtils.readFileToString(passwordFile, FILE_ENCODING_UTF8);

		} catch (IOException e) {
			// create error message and throw exception
			Object[] args = { passwordFile, e.getCause() };
			String message = messageProvider.getMessage("mpepbec.load_password_error", args);
			throw new LoadCredentialProviderMasterPasswordFailedException(message, e);
		}
	}

	public Integer getKeyObtentionIterations() {
		return this.keyObtentionIterations;
	}

	public SaltGenerator getSaltGenerator() {
		return this.saltGenerator;
	}

	public String getProviderName() {
		return this.providerName;
	}

	public Provider getProvider() {
		return this.provider;
	}

	@Override
	public Integer getPoolSize() {
		return this.poolSize;
	}

	/**
	 * Log info message.
	 * 
	 * @param passwordFile
	 *            location of created password file.
	 */
	void logInfoMessagePasswordFileCreation(File passwordFile) {
		Object[] args = { passwordFile };
		String message = messageProvider.getMessage("mpepbec.save_password_info", args);
		logger.info(message);
	}

	/**
	 * Log info message.
	 * 
	 * @param passwordFile
	 *            location of loaded password file.
	 */
	void logInfoMessageLoadPasswordFile(File passwordFile) {
		Object[] args = { passwordFile };
		String message = messageProvider.getMessage("mpepbec.load_password_info", args);
		logger.info(message);
	}

}
