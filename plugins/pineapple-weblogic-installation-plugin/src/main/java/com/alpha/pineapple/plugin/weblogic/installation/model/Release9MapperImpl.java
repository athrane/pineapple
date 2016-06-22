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


package com.alpha.pineapple.plugin.weblogic.installation.model;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;

import com.alpha.pineapple.plugin.weblogic.installation.argument.Release9ArgumentBuilder;

/**
 * Implementation of the {@link Mapper} which prepares the installation 
 * of the WebLogic 9.x installer from the content of the model. 
 */
public class Release9MapperImpl implements Mapper {
	    
    /**
     * Release 9.x create silent XML command.
     */
    @Resource
    Command createRelease9SilentXmlCommand;    
    		
    /**
     * Release 9.x argument builder.
     */   
    @Resource    
    Release9ArgumentBuilder release9ArgumentBuilder;

	@Override
	public Command getCreateInstallReponseCommand() {		
		return createRelease9SilentXmlCommand;
	}    
            	
	public String[] createArgumentList(File silentXmlFile, File silentLogFile) {
		release9ArgumentBuilder.buildArgumentList();
		release9ArgumentBuilder.addSilentModeArgument();
		release9ArgumentBuilder.addSilentXMLArgument(silentXmlFile);
		release9ArgumentBuilder.addLogArgument(silentLogFile);
		return release9ArgumentBuilder.getArgumentList();
	}

	public String[] createArgumentListForArchive(String installerArchive,File silentXmlFile, File silentLogFile) {
		release9ArgumentBuilder.buildArgumentList();
		release9ArgumentBuilder.addInstallerArchive(installerArchive);		
		release9ArgumentBuilder.addSilentModeArgument();
		release9ArgumentBuilder.addSilentXMLArgument(silentXmlFile);
		release9ArgumentBuilder.addLogArgument(silentLogFile);
		return release9ArgumentBuilder.getArgumentList();
	}
		
	public String[] createUninstallationArgumentList(File silentLogFile) {
		release9ArgumentBuilder.buildArgumentList();
		release9ArgumentBuilder.addUninstallationSilentModeArgument();
		release9ArgumentBuilder.addLogArgument(silentLogFile);
		return release9ArgumentBuilder.getArgumentList();
	}

	
	public String getInstaller(WeblogicInstallation model) {
		return model.getRelease9().getInstaller();		
	}
	
	
	public String getUninstaller(WeblogicInstallation model) {
		return model.getRelease9().getUninstaller();
	}

	
	public File getTargetDirectory(WeblogicInstallation model) {		
		return new File(model.getRelease9().getTargetDirectory());
	}
		
	public File getLocalJvm( WeblogicInstallation model) {
		// some nonsense path is return since it is required by mapper interface
		// but not used by wls92.		
		return new File("null-path-since-jvm-isnt-specified-for-wls92");
	}
	
}
