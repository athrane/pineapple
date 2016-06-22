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


package com.alpha.pineapple.plugin.fusion.installation.model;

import java.io.File;

import javax.annotation.Resource;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.fusion.installation.argument.R11ArgumentBuilder;

/**
 * Implementation of the {@link AdfRuntimeMapper} which prepares the installation 
 * of the WebCenter Suite 11.x installer from the content of the model. 
 */
public class WebCenterR11MapperImpl implements ModelMapper {
	
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
        
    /**
     * Release 11.x argument builder.
     */   
    @Resource    
    R11ArgumentBuilder r11ArgumentBuilder;

    
	public String[] createArgumentList(File responseFile, File jreLocation) {
		r11ArgumentBuilder.buildArgumentList();
		r11ArgumentBuilder.addSilentModeArgument();
		r11ArgumentBuilder.addDebugArgument();
		r11ArgumentBuilder.addWaitForCompletionArgument();
		r11ArgumentBuilder.addResponseFileArgument(responseFile);
		r11ArgumentBuilder.addJreArgument(jreLocation);		
		return r11ArgumentBuilder.getArgumentList();
	}
	
	public String[] createUninstallationArgumentList(File responseFile, File jreLocation) {
		r11ArgumentBuilder.buildArgumentList();		
		r11ArgumentBuilder.addSilentModeArgument();		
		r11ArgumentBuilder.addUninstallationArgument();
		r11ArgumentBuilder.addDebugArgument();
		r11ArgumentBuilder.addWaitForCompletionArgument();
		r11ArgumentBuilder.addResponseFileArgument(responseFile);
		r11ArgumentBuilder.addJreArgument(jreLocation);				
		return r11ArgumentBuilder.getArgumentList();
	}
	
	public String getInstaller(FusionMiddlewareInstallation model) {
		return model.getWebcenter().getRelease11().getInstaller();		
	}
	
	public String getUninstaller(FusionMiddlewareInstallation model) {
		return model.getWebcenter().getRelease11().getUninstaller();
	}

	public File getTargetDirectory(FusionMiddlewareInstallation model) {
		return new File(model.getWebcenter().getRelease11().getTargetDirectory());		
	}

	public File getLocalJvm(FusionMiddlewareInstallation model) {
		return new File(model.getWebcenter().getRelease11().getLocalJvm());
	}	

	
}
