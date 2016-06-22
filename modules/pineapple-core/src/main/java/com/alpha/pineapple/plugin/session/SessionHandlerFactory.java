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

package com.alpha.pineapple.plugin.session;

import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.plugin.Operation;

/**
 * Interface for {@linkplain SessionHandlerImpl} factory class.
 */
public interface SessionHandlerFactory {

    /**
     * SessionHandler factory method.
     * 
     * Session handler is created with Spring dependencies and then initialized
     * with required Pineapple data objects (e.g. {@linkplain Resource} and
     * {@linkplain Credential} and {@linkplain Operation} which is proxied by
     * the session handler.
     * 
     * @param resource
     *            Resource object.
     * @param credential
     *            Credential object.
     * @param operation
     *            Operation object.
     */
    public Operation getInstance(Resource resource, Credential credential, Operation operation);

}
