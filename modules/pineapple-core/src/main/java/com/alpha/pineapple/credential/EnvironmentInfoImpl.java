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

import java.util.Collection;
import java.util.TreeMap;

/**
 * Implementation of the {@link EnvironmentInfo} interface.
 */
public class EnvironmentInfoImpl implements EnvironmentInfo {

    /**
     * Environment ID.
     */
    String id;

    /**
     * Description.
     */
    String description;

    /**
     * Resource info's.
     */
    TreeMap<String, CredentialInfo> credentialInfos;

    /**
     * EnvironmentInfoImpl constructor.
     * 
     * @param description
     *            environment description.
     * @param credentialInfos
     *            credential info's.
     */
    public EnvironmentInfoImpl(String id, String description, TreeMap<String, CredentialInfo> credentialInfos) {
	super();
	this.id = id;
	this.description = description;
	this.credentialInfos = credentialInfos;
    }

    @Override
    public String getId() {
	return id;
    }

    @Override
    public String getDescription() {
	return description;
    }

    @Override
    public boolean containsCredential(String id) {
	return credentialInfos.containsKey(id);
    }

    @Override
    public CredentialInfo getCredential(String id) {
	if (!containsCredential(id))
	    return null;
	return credentialInfos.get(id);
    }

    @Override
    public CredentialInfo[] getCredentials() {
	Collection<CredentialInfo> values = credentialInfos.values();
	return (CredentialInfo[]) values.toArray(new CredentialInfo[values.size()]);
    }

    @Override
    public void addCredential(CredentialInfo credentialInfo) {
	if (containsCredential(credentialInfo.getId()))
	    return;
	credentialInfos.put(credentialInfo.getId(), credentialInfo);
    }

    @Override
    public void deleteCredential(CredentialInfo credentialInfo) {
	if (!containsCredential(credentialInfo.getId()))
	    return;
	credentialInfos.remove(credentialInfo.getId());
    }

}
