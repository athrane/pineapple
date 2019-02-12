/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2013 Allan Thrane Andersen..
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
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package com.alpha.javautils;

import static com.alpha.javautils.ArgumentUtils.notNull;

import java.util.Properties;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alpha.pineapple.i18n.MessageProvider;

/**
 * <p>
 * Helper class for <code>java.lang.System</code>.
 * </p>
 **/
public class SystemUtils {

	/**
	 * OS name system property name
	 */
	public static final String OS_NAME = "os.name";

	/**
	 * User name system property name
	 */
	public static final String USER_NAME = "user.name";

	/**
	 * User home system property name.
	 */
	public static final String USER_HOME = "user.home";

	/**
	 * temporary IO directory system property name.
	 */
	public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

	/**
	 * Pineapple home directory system property name.
	 */
	public static final String PINEAPPLE_HOMEDIR = "pineapple.home.dir";

	/**
	 * Pineapple credential provider master password file system property name.
	 */
	public static final String PINEAPPLE_CREDENTIALPROVIDER_PASSWORD_FILE = "pineapple.credentialprovider.password.file";

	/**
	 * The prefix String for all Windows OS.
	 */
	static final String OS_NAME_WINDOWS_PREFIX = "Windows";

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "apiMessageProvider")
	MessageProvider messageProvider;

	/**
	 * Logger object
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Returns true if OS is Windows.
	 *
	 * @param systemProperties
	 *            Java system properties.
	 * 
	 * @return true if OS is Windows.
	 */
	public boolean isWindowsOperatingSystem(Properties systemProperties) {
		String osName = getSystemProperty(OS_NAME, systemProperties);
		return (osName.startsWith(OS_NAME_WINDOWS_PREFIX));
	}

	/**
	 * Returns true if the system property "pineapple.home.dir" is defined.
	 * Otherwise null is returned.
	 * 
	 * @return true if the system property "pineapple.home.dir" is defined.
	 *         Otherwise null is returned.
	 */
	public boolean isPineappleHomeDefined(Properties systemProperties) {
		return (getSystemProperty(PINEAPPLE_HOMEDIR, systemProperties) != null);
	}

	/**
	 * Returns true if the system property
	 * "pineapple.credentialprovider.password.dir" is defined. Otherwise null is
	 * returned.
	 * 
	 * @return true if the system property
	 *         "pineapple.credentialprovider.password.dir" is defined. Otherwise
	 *         null is returned.
	 */
	public boolean isPineappleCredentialProviderPasswordHomeDefined(Properties systemProperties) {
		return (getSystemProperty(PINEAPPLE_CREDENTIALPROVIDER_PASSWORD_FILE, systemProperties) != null);
	}

	/**
	 * <p>
	 * Gets a System property. Returns null if the property is undefined or access
	 * is restricted.
	 *
	 * <p>
	 * If a <code>SecurityException</code> is caught, the return value is null and a
	 * error message is written to the log.
	 * </p>
	 * 
	 * @param property
	 *            the system property name
	 * 
	 * @return the system property value or <code>null</code> if a security problem
	 *         occurs
	 */
	public String getSystemProperty(String property, Properties systemProperties) {
		notNull(property, "property is undefined.");
		notNull(systemProperties, "systemProperties is undefined.");

		try {
			return systemProperties.getProperty(property);
		} catch (SecurityException e) {
			Object[] args = { property, e.toString() };
			String message = messageProvider.getMessage("su.get_systemproperty_error", args);
			logger.error(message);
			return null;
		}
	}

}
