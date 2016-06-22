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

import com.alpha.pineapple.plugin.weblogic.deployment.model.Deployment;
import com.alpha.pineapple.plugin.weblogic.deployment.model.DeploymentArtifact;
import com.alpha.pineapple.plugin.weblogic.deployment.model.ObjectFactory;

/**
 * Implementation of the ObjectMother pattern, 
 * provides helper functions for unit testing by creating content for operations.
 */
public class ObjectMotherContent
{    
    /**
     * Object factory.
     */
	ObjectFactory objectFactory;    
        
    /**
	 * ObjectMotherContent constructor.
	 */
	public ObjectMotherContent() {
		super();

        // create test case factory
        objectFactory = new ObjectFactory();		
	}

	/**
     * Create empty Deployment document.
     * 
     * @return empty Deployment document.
     */
    public Deployment createEmptyDeployment() {
        
    	Deployment doc = objectFactory.createDeployment();
        return doc;        
    }

    
	/**
     * Create deployment document.
     * 
     * @param installer Installer path.
     * @param targetDirectory Target directory for the installation.
     * @param localJvm Location of local JVM.
     * 
     * @return deployment document.
     */    
	public Deployment createDeployment(String installer, String targetDirectory, String localJvm) {		
		Deployment doc = createEmptyDeployment();		
		DeploymentArtifact artifact = objectFactory.createDeploymentArtifact();
		doc.setArtifact(artifact);
		return doc;
	}
	
}
