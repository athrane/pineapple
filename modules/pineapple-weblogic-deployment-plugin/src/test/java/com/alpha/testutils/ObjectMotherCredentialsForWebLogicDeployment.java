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


package com.alpha.testutils;

import com.alpha.pineapple.model.configuration.Credential;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for unit testing c
 * lasses which uses credentials.
 */
public class ObjectMotherCredentialsForWebLogicDeployment 
{
    
    /**
     * Create credential for WebLogic deployment.
     * 
     * @return created credential.
     */ 
    public Credential createWebLogicDeploymentCredential()
    {
        // get attributes
        String identifier = WeblogicDeploymentPluginTestConstants.credentialIdWeblogicDeployment;
        String user = WeblogicDeploymentPluginTestConstants.userWeblogic;
        String password = WeblogicDeploymentPluginTestConstants.passwordWeblogic;

        // create
        Credential credential = new Credential();
        credential.setId( identifier );
        credential.setUser( user );
        credential.setPassword( password );
        return credential;
    }
}
