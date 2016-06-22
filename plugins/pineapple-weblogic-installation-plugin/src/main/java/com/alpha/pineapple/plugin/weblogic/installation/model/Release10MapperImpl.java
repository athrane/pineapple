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

import com.alpha.pineapple.plugin.weblogic.installation.argument.Release10ArgumentBuilder;

/**
 * Implementation of the {@link Mapper} which prepares the installation 
 * of the WebLogic 10.x installer from the content of the model. 
 */
public class Release10MapperImpl implements Mapper {
	    
    /**
     * Release 10.x create silent XML command.
     */
    @Resource
    Command createRelease10SilentXmlCommand;
    
    /**
     * Release 10.x argument builder.
     */   
    @Resource    
    Release10ArgumentBuilder release10ArgumentBuilder;
    
	@Override
	public Command getCreateInstallReponseCommand() {		
		return createRelease10SilentXmlCommand;
	}

	public String[] createArgumentList(File silentXmlFile, File silentLogFile) {
		release10ArgumentBuilder.buildArgumentList();
		release10ArgumentBuilder.addSilentModeArgument();
		release10ArgumentBuilder.addSilentXMLArgument(silentXmlFile);
		release10ArgumentBuilder.addLogArgument(silentLogFile);
		return release10ArgumentBuilder.getArgumentList();
	}
	
	public String[] createArgumentListForArchive(String installerArchive,File silentXmlFile, File silentLogFile) {
		release10ArgumentBuilder.buildArgumentList();
		release10ArgumentBuilder.addInstallerArchive(installerArchive);
		release10ArgumentBuilder.addSilentModeArgument();
		release10ArgumentBuilder.addSilentXMLArgument(silentXmlFile);
		release10ArgumentBuilder.addLogArgument(silentLogFile);
		return release10ArgumentBuilder.getArgumentList();
	}


	public String[] createUninstallationArgumentList(File silentLogFile) {
		release10ArgumentBuilder.buildArgumentList();		
		release10ArgumentBuilder.addUninstallationSilentModeArgument();
		release10ArgumentBuilder.addLogArgument(silentLogFile);
		return release10ArgumentBuilder.getArgumentList();
	}
	
	public String getInstaller(WeblogicInstallation model) {
		return model.getRelease10().getInstaller();		
	}
	
	public String getUninstaller(WeblogicInstallation model) {
		return model.getRelease10().getUninstaller();
	}

	public File getTargetDirectory(WeblogicInstallation model) {
		return new File(model.getRelease10().getTargetDirectory());		
	}	
			
	public File getLocalJvm( WeblogicInstallation model) {
		return new File(model.getRelease10().getLocalJvm());
	}
	
}
