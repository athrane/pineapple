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

import com.alpha.pineapple.plugin.weblogic.installation.model.ObjectFactory;
import com.alpha.pineapple.plugin.weblogic.installation.model.Release10Installation;
import com.alpha.pineapple.plugin.weblogic.installation.model.Release12Installation;
import com.alpha.pineapple.plugin.weblogic.installation.model.Release9Installation;
import com.alpha.pineapple.plugin.weblogic.installation.model.WeblogicInstallation;

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
        objectFactory = new com.alpha.pineapple.plugin.weblogic.installation.model.ObjectFactory();		
	}

	/**
     * Create empty WebLogic Installation document.
     * 
     * @return empty WebLogic Installation document.
     */
    public WeblogicInstallation createEmptyWeblogicInstallation() {
        
    	WeblogicInstallation doc = objectFactory.createWeblogicInstallation();
        return doc;        
    }

	/**
     * Create WebLogic 9.x Installation document.
     * 
     * @param installer Installer path.
     * @param targetDirectory Target directory for the installation.
     * 
     * @return WebLogic 9.x installation document.
     */    
	public Object createRelease9WeblogicInstallation(String installer, String targetDirectory) {		
		WeblogicInstallation doc = createEmptyWeblogicInstallation();		
		Release9Installation r9 = objectFactory.createRelease9Installation();
		r9.setInstaller(installer);
		r9.setTargetDirectory(targetDirectory);
		doc.setRelease9(r9);		
		return doc;
	}

	/**
     * Create WebLogic 10.x Installation document.
     * 
     * @param installer Installer path.
     * @param targetDirectory Target directory for the installation.
     * @param localJvm Location of local JVM.
     * 
     * @return WebLogic 10.x installation document.
     */    
	public Object createRelease10WeblogicInstallation(String installer, String targetDirectory, String localJvm) {		
		WeblogicInstallation doc = createEmptyWeblogicInstallation();		
		Release10Installation r10 = objectFactory.createRelease10Installation();
		r10.setInstaller(installer);
		r10.setTargetDirectory(targetDirectory);
		r10.setLocalJvm(localJvm);
		doc.setRelease10(r10);		
		return doc;
	}

	/**
     * Create WebLogic 12.x Installation document.
     * 
     * @param installer Installer path.
     * @param targetDirectory Target directory for the installation.
     * @param localJvm Location of local JVM.
     * 
     * @return WebLogic 12.x installation document.
     */    
	public Object createRelease12WeblogicInstallation(String installer, String targetDirectory, String localJvm) {
		WeblogicInstallation doc = createEmptyWeblogicInstallation();		
		Release12Installation r12 = objectFactory.createRelease12Installation();
		r12.setInstaller(installer);
		r12.setTargetDirectory(targetDirectory);
		r12.setLocalJvm(localJvm);
		doc.setRelease12(r12);		
		return doc;
	}
	
}
