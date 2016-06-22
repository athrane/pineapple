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

import java.util.MissingResourceException;

/**
 * Interface for resolution of resource bundle messages.
 * 
 * The message provider supports resolution of compound messages. Parameters are
 * defined in a compound text message in resource bundles by a brace which
 * encloses an integer, starting from zero, e.g. {0}. When a message is resolved
 * the parameters are supplied by an object array where the first value is
 * substituted with {0} in the message and so forth. For more information abound
 * compound messages consult the <a href=
 * "http://java.sun.com/docs/books/tutorial/i18n/format/messageFormat.html">Java
 * I18N documentation</a>.
 * 
 */
public interface MessageProvider {

    /**
     * Get resolve message from resource bundle with compound message values
     * substituted into the resolved message.
     * 
     * @param key
     *            Message key in resource bundle.
     * @param args
     *            Array of compound message values. If the args array is null
     *            then no value Substitution is attempted.
     * @return message with compound message values substituted into the
     *         resolved message.
     * 
     * @throws MissingResourceException
     *             if no message with the key could be found.
     * @throws IllegalArgumentException
     *             if the key is undefined.
     */
    public String getMessage(String key, Object[] args);

    /**
     * Get resolve message from resource bundle.
     * 
     * @param key
     *            Message key in resource bundle.
     * @return resolved message.
     * 
     * @throws MissingResourceException
     *             if no message with the key could be found.
     * @throws IllegalArgumentException
     *             if the key is undefined.
     */
    public String getMessage(String key);

}
