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

package com.alpha.pineapple.credential;

import java.io.File;

import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;

/**
 * Interface for loading an credential configuration (as XML) from disk into
 * info objects. And versus visa.
 */
public interface CredentialConfigurationMarshaller {

    /**
     * Save credential configuration.
     * 
     * The configuration directory is resolved from the runtime directory
     * provider. The configuration is saved in the default resources
     * configuration file credentials.xml.
     * 
     * @param configuration
     *            credentials configuration which is saved.
     * 
     * @throws SaveConfigurationFailedException
     *             if save fails.
     */

    void save(Configuration configuration) throws SaveConfigurationFailedException;

    /**
     * Load credential configuration.
     * 
     * @param credentialsFile
     *            credentials file which is loaded.
     * 
     * @return credential configuration which is loaded.
     * 
     * @throws CredentialsFileNotFoundException
     *             if file wasn't found.
     */
    Configuration load(File credentialsFile) throws CredentialsFileNotFoundException;

    /**
     * Map resources configuration for saving.
     * 
     * @param info
     *            configuration info which is mapped.
     * 
     * @return credential configuration which can be marshalled.
     */
    Configuration map(ConfigurationInfo info);

    /**
     * Map resources configuration to info objects.
     * 
     * @param configuration
     *            configuration which is mapped.
     * 
     * @return credential configuration represented by info objects.
     */
    ConfigurationInfo map(Configuration configuration);

    /**
     * Map to credential info to credential.
     * 
     * @param credentialInfo
     *            credential info object.
     * 
     * @return mapped credential.
     */
    Credential mapToCredential(CredentialInfo credentialInfo);

}
