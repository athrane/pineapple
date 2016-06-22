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

package com.alpha.pineapple.credential;

/**
 * Interface to represent information about a environment in a credential
 * provider.
 */
public interface EnvironmentInfo {
    /**
     * Get environment ID.
     * 
     * @return environment ID.
     */
    String getId();

    /**
     * Get description of the environment.
     * 
     * @return description.
     */
    String getDescription();

    /**
     * Return true if info contains credential info with requested ID.
     * 
     * @param id
     *            credential ID.
     * 
     * @return true if info contains credential info with requested ID.
     */
    boolean containsCredential(String id);

    /**
     * Get credential info. Returns null if credential isn't registered.
     * 
     * @param id
     *            credential ID.
     * 
     * @return credential info. Returns null if credential isn't registered.
     */
    CredentialInfo getCredential(String id);

    /**
     * Get credential info's.
     * 
     * @return credential info's.
     */
    CredentialInfo[] getCredentials();

    /**
     * Add credential.
     * 
     * Please notice that addition of a credential through this method doesn't
     * result in the environment configuration being persisted. In order to
     * create an persisted environment create the environment through the
     * credential provider.
     * 
     * @param credentialInfo
     *            credential to add.
     */
    void addCredential(CredentialInfo credentialInfo);

    /**
     * Delete credential.
     * 
     * Please notice that deletion of a credential through this method doesn't
     * result in the environment configuration being persisted. In order to
     * create an persisted environment create the environment through the
     * credential provider.
     * 
     * @param credentialInfo
     *            credential to delete.
     */
    void deleteCredential(CredentialInfo credentialInfo);

}
