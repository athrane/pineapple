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


import com.alpha.pineapple.plugin.fusion.installation.model.AppDevRuntimeInstallation;
import com.alpha.pineapple.plugin.fusion.installation.model.AppDevRuntimeRelease11Installation;
import com.alpha.pineapple.plugin.fusion.installation.model.FusionMiddlewareInstallation;
import com.alpha.pineapple.plugin.fusion.installation.model.ObjectFactory;

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
        objectFactory = new com.alpha.pineapple.plugin.fusion.installation.model.ObjectFactory();		
	}

	/**
     * Create empty Fusion Middleware Installation document.
     * 
     * @return empty Fusion Middleware Installation document.
     */
    public FusionMiddlewareInstallation createEmptyFusionInstallation() {
        
    	FusionMiddlewareInstallation doc = objectFactory.createFusionMiddlewareInstallation();
        return doc;        
    }

    
	/**
     * Create Application Development Runtime 11.x Installation document.
     * 
     * @param installer Installer path.
     * @param targetDirectory Target directory for the installation.
     * @param localJvm Location of local JVM.
     * 
     * @return Application Development Runtime 11.x installation document.
     */    
	public Object createApplicationDevelopementRuntimeRelease11Installation(String installer, String targetDirectory, String localJvm) {		
		FusionMiddlewareInstallation doc = createEmptyFusionInstallation();
		AppDevRuntimeInstallation appDevRuntime = objectFactory.createAppDevRuntimeInstallation();
		doc.setApplicationDevelopmentRuntime(appDevRuntime);
		AppDevRuntimeRelease11Installation r11 = objectFactory.createAppDevRuntimeRelease11Installation();
		r11.setInstaller(installer);
		r11.setTargetDirectory(targetDirectory);
		r11.setLocalJvm(localJvm);
		appDevRuntime.setRelease11(r11);
		return doc;
	}
	
}
