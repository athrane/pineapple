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

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.installation.argument.Release1212ArgumentBuilder;

/**
 * Implementation of the {@link Mapper} which prepares the installation 
 * of the WebLogic 12.1.2 installer from the content of the model. 
 */
public class Release1212MapperImpl implements UniversalInstallerMapper {
	
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
    
    /**
     * Release 12.1.2 argument builder.
     */   
    @Resource    
    Release1212ArgumentBuilder release1212ArgumentBuilder;
                
	public String[] createArgumentListForArchive(String installerArchive, File responseFile, File jreLocation) {
		release1212ArgumentBuilder.buildArgumentList();
		release1212ArgumentBuilder.addInstallerArchive(installerArchive);		
		release1212ArgumentBuilder.addSilentModeArgument();
		release1212ArgumentBuilder.addDebugArgument();
		release1212ArgumentBuilder.addWaitForCompletionArgument();
		release1212ArgumentBuilder.addResponseFileArgument(responseFile);
		release1212ArgumentBuilder.addJreArgument(jreLocation);
		release1212ArgumentBuilder.addInventoryLocationArgument();		
		return release1212ArgumentBuilder.getArgumentList();
	}
	
	@Override
	public String[] createUninstallationArgumentList(File responseFile, File jreLocation) {
		release1212ArgumentBuilder.buildArgumentList();		
		release1212ArgumentBuilder.addSilentModeArgument();		
		release1212ArgumentBuilder.addUninstallationArgument();
		release1212ArgumentBuilder.addDebugArgument();
		release1212ArgumentBuilder.addWaitForCompletionArgument();
		release1212ArgumentBuilder.addResponseFileArgument(responseFile);
		release1212ArgumentBuilder.addJreArgument(jreLocation);				
		return release1212ArgumentBuilder.getArgumentList();
	}

	public String getInstaller(WeblogicInstallation model) {
		return model.getRelease1212().getInstaller();		
	}
	
	public String getUninstaller(WeblogicInstallation model) {
		return model.getRelease1212().getUninstaller();
	}

	public File getTargetDirectory(WeblogicInstallation model) {
		return new File(model.getRelease1212().getTargetDirectory());		
	}	
			
	public File getLocalJvm( WeblogicInstallation model) {
		return new File(model.getRelease1212().getLocalJvm());
	}
	
}
