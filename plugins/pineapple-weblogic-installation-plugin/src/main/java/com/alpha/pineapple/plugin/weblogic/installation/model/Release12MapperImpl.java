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

import com.alpha.pineapple.plugin.weblogic.installation.argument.Release12ArgumentBuilder;

/**
 * Implementation of the {@link Mapper} which prepares the installation 
 * of the WebLogic 12.1.1 installer from the content of the model. 
 */
public class Release12MapperImpl implements Mapper {
	    
    /**
     * Release 12.1.2 create silent XML command.
     */
    @Resource
    Command createRelease12SilentXmlCommand;
    
    /**
     * Release 12.1.2 argument builder.
     */   
    @Resource    
    Release12ArgumentBuilder release12ArgumentBuilder;
            
	@Override
	public Command getCreateInstallReponseCommand() {		
		return createRelease12SilentXmlCommand;
	}
    
	public String[] createArgumentList(File silentXmlFile, File silentLogFile) {
		release12ArgumentBuilder.buildArgumentList();
		release12ArgumentBuilder.addSilentModeArgument();
		release12ArgumentBuilder.addSilentXMLArgument(silentXmlFile);
		release12ArgumentBuilder.addLogArgument(silentLogFile);
		return release12ArgumentBuilder.getArgumentList();
	}
	
	public String[] createArgumentListForArchive(String installerArchive,File silentXmlFile, File silentLogFile) {
		release12ArgumentBuilder.buildArgumentList();
		release12ArgumentBuilder.addInstallerArchive(installerArchive);
		release12ArgumentBuilder.addSilentModeArgument();
		release12ArgumentBuilder.addSilentXMLArgument(silentXmlFile);
		release12ArgumentBuilder.addLogArgument(silentLogFile);
		return release12ArgumentBuilder.getArgumentList();
	}


	public String[] createUninstallationArgumentList(File silentLogFile) {
		release12ArgumentBuilder.buildArgumentList();		
		release12ArgumentBuilder.addUninstallationSilentModeArgument();
		release12ArgumentBuilder.addLogArgument(silentLogFile);
		return release12ArgumentBuilder.getArgumentList();
	}
	
	public String getInstaller(WeblogicInstallation model) {
		return model.getRelease12().getInstaller();		
	}
	
	public String getUninstaller(WeblogicInstallation model) {
		return model.getRelease12().getUninstaller();
	}

	public File getTargetDirectory(WeblogicInstallation model) {
		return new File(model.getRelease12().getTargetDirectory());		
	}	
			
	public File getLocalJvm( WeblogicInstallation model) {
		return new File(model.getRelease12().getLocalJvm());
	}
	
}
