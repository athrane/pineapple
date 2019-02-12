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

package com.alpha.pineapple.i18n;

import static com.alpha.javautils.ArgumentUtils.notNull;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;

/**
 * Implementation of the <code>MessageProvider</code> interface which loads
 * messages from a property file using the JDK
 * <code>PropertyResourceBundle</code> implementation.
 */
public class PropertyFileMessageProviderImpl implements MessageProvider {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Resource bundle
	 */
	ResourceBundle messages;

	/**
	 * Current Locale
	 */
	Locale locale;

	/**
	 * Message format cache
	 */
	HashMap<String, MessageFormat> messageFormatCache;

	/**
	 * PropertyFileMessageProviderImpl constructor.
	 */
	public PropertyFileMessageProviderImpl() throws MessageProviderInitializationException {

		// get default locale
		locale = Locale.getDefault();

		// create message format cache
		messageFormatCache = new HashMap<String, MessageFormat>();
	}

	/**
	 * Set the base name
	 * 
	 * @param baseName
	 *            resource bundle base name which follows the basic ResourceBundle
	 *            convention of not specifying file extension, e.g. specifying
	 *            <code>xyz</code> will load the property bundle
	 *            <code>xyz.properties</code> from the class path.
	 * 
	 * @throws MessageProviderInitializationException
	 *             If message provider initialization fails.
	 */
	public void setBasename(String baseName) throws MessageProviderInitializationException {
		notNull(baseName, "baseName is undefined.");

		// define stream
		InputStream inStream = null;

		try {

			// create file name
			String fileName = createFileName(baseName);

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder debugMessage = new StringBuilder();
				debugMessage.append("Starting to load resource bundle [");
				debugMessage.append(fileName);
				debugMessage.append("].");
				logger.debug(debugMessage.toString());
			}

			// create stream
			inStream = this.getClass().getResourceAsStream(fileName);

			// create resource bundle
			messages = new PropertyResourceBundle(inStream);

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder debugMessage = new StringBuilder();
				debugMessage.append("Successfuly loaded resource bundle [");
				debugMessage.append(fileName);
				debugMessage.append("] into [");
				debugMessage.append(messages);
				debugMessage.append("].");
				logger.debug(debugMessage.toString());
			}

		} catch (Exception e) {

			// create error message
			StringBuilder message = new StringBuilder();
			message.append("Failed to initialize message provider with base name [ ");
			message.append(baseName);
			message.append("] due to exception [ ");
			message.append(StackTraceHelper.getStrackTrace(e));
			message.append("].");

			throw new MessageProviderInitializationException(message.toString(), e);

		} finally {

			try {

				// close stream
				if (inStream != null) {
					inStream.close();
				}

			} catch (Exception e) {

				// create error message
				StringBuilder message = new StringBuilder();
				message.append("Failed to initialize message provider with base name [ ");
				message.append(baseName);
				message.append("] due to exception [ ");
				message.append(StackTraceHelper.getStrackTrace(e));
				message.append("].");

				throw new MessageProviderInitializationException(message.toString(), e);
			}
		}
	}

	public String getMessage(String key, Object[] args) {

		// handle undefined args case
		if (args == null) {
			return getMessage(key);
		}

		// validate argument
		notNull(key, "key is undefined");

		// get message formatter
		MessageFormat formatter = getMessageFormat(key);

		// format string with args
		String value = formatter.format(args);

		return value;
	}

	public String getMessage(String key) {
		notNull(key, "key is undefined");
		String value = messages.getString(key);
		return value;
	}

	/**
	 * Create file name for resource bundle. The base name is prefixed with a "/"
	 * and postfixed with a ".properties".
	 * 
	 * @param baseName
	 *            Base name from which the file name is created.
	 * 
	 * @return file name for resource property file.
	 */
	String createFileName(String baseName) {
		notNull(baseName, "baseName is undefined");
		
		// create file name
		StringBuilder fileName = new StringBuilder();
		fileName.append("/");
		fileName.append(baseName);
		fileName.append(".properties");
		return fileName.toString();
	}

	/**
	 * Look up message formatter from cache. if formatter doesn't exist in cache the
	 * add to the cache.
	 * 
	 * @param key
	 *            Key which identifies message for which a formatter is created.
	 * 
	 * @return Message formatter for a message.
	 */
	MessageFormat getMessageFormat(String key) {
		notNull(key, "key is undefined");

		// get message format
		if (messageFormatCache.containsKey(key)) {
			return messageFormatCache.get(key);
		}

		// create message format
		MessageFormat formatter = new MessageFormat(messages.getString(key));
		formatter.setLocale(locale);

		// add format to cache
		messageFormatCache.put(key, formatter);

		return formatter;
	}

}
