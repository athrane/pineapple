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

import org.apache.log4j.Logger;

import com.alpha.pineapple.plugin.jrockit.installation.model.JRockitInstallation;
import com.alpha.pineapple.plugin.jrockit.installation.model.ObjectFactory;
import com.alpha.pineapple.plugin.jrockit.installation.model.R27Installation;
import com.alpha.pineapple.plugin.jrockit.installation.model.R28Installation;

/**
 * Implementation of the ObjectMother pattern, 
 * provides helper functions for unit testing by creating content for operations.
 */
public class ObjectMotherContent
{
	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
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
        objectFactory = new com.alpha.pineapple.plugin.jrockit.installation.model.ObjectFactory();		
	}

	/**
     * Create empty JRockit Installation document.
     * 
     * @return empty JRockit Installation document.
     */
    public JRockitInstallation createEmptyJRockitInstallation() {
        
    	JRockitInstallation doc = objectFactory.createJRockitInstallation();
        return doc;        
    }

	/**
     * Create R27 JRockit Installation document.
     * 
     * @param installer Installer path.
     * @param targetDirectory Target directory for the installation.
     * 
     * @return R27 JRockit Installation document.
     */    
	public Object createR27JRockitInstallation(String installer, String targetDirectory) {		
		JRockitInstallation doc = createEmptyJRockitInstallation();		
		R27Installation r27 = objectFactory.createR27Installation();
		r27.setInstaller(installer);
		r27.setInstallPublicJre(false);
		r27.setTargetDirectory(targetDirectory);
		doc.setR27(r27);		
		return doc;
	}

	/**
     * Create R28 JRockit Installation document.
     * 
     * @param installer Installer path.
     * @param targetDirectory Target directory for the installation.
     * 
     * @return R27 JRockit Installation document.
     */    	
	public Object createR28JRockitInstallation(String installer, String targetDirectory) {
		JRockitInstallation doc = createEmptyJRockitInstallation();		
		R28Installation r28 = objectFactory.createR28Installation();
		r28.setInstaller(installer);
		r28.setInstallPublicJre(false);
		r28.setTargetDirectory(targetDirectory);
		doc.setR28(r28);		
		return doc;
	}
	
}
