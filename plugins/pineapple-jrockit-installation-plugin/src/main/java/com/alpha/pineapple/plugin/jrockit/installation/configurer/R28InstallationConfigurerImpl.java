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


package com.alpha.pineapple.plugin.jrockit.installation.configurer;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.jrockit.installation.argument.R28ArgumentBuilder;
import com.alpha.pineapple.plugin.jrockit.installation.command.CreateR28SilentXmlCommand;
import com.alpha.pineapple.plugin.jrockit.installation.model.JRockitInstallation;

/**
 * Implementation of the {@link InstallationConfigurer} which
 * prepares the installation of the R28 installer.
 */
public class R28InstallationConfigurerImpl implements InstallationConfigurer {
	
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Command runner
     */
    @Resource
    CommandRunner commandRunner;   
    
    /**
     * R28 creation silent XML command.
     */
    @Resource
    Command createR28SilentXmlCommand;
    
    /**
     * R28 Argument Builder object.
     */   
    @Resource    
    R28ArgumentBuilder r28ArgumentBuilder;
            
	@SuppressWarnings("unchecked")
	public File createSilentXmlFile(JRockitInstallation model) {
    	// create description
    	String message = messageProvider.getMessage("r28ici.create_r28silentxml" );        	
    	
        // create context
        Context context = commandRunner.createContext();            
    	
        // intialize context
        context.put(CreateR28SilentXmlCommand.TARGET_DIRECTORY, model.getR28().getTargetDirectory());
        context.put(CreateR28SilentXmlCommand.PUBLIC_JRE, new Boolean(model.getR28().isInstallPublicJre()));        
        
        // run test            
        commandRunner.run(createR28SilentXmlCommand, message, context);                        
		
        // get silent XML from context
        File file = (File) context.get(CreateR28SilentXmlCommand.SILENTXML_FILE);
		
		// return the file object
		return file;
	}

	
	public boolean isSilentXmlCreationSuccessful() {
		return commandRunner.lastExecutionSucceeded();		
	}

	public String[] createArgumentList(File silentXmlFile, File silentLogFile) {
		r28ArgumentBuilder.buildArgumentList();
		r28ArgumentBuilder.addSilentModeArgument();
		r28ArgumentBuilder.addSilentXMLArgument(silentXmlFile);
		r28ArgumentBuilder.addLogArgument(silentLogFile);
		return r28ArgumentBuilder.getArgumentList();
	}
	
	public String[] createUninstallationArgumentList(File silentLogFile) {
		r28ArgumentBuilder.buildArgumentList();
		r28ArgumentBuilder.addUninstallationSilentModeArgument();
		r28ArgumentBuilder.addLogArgument(silentLogFile);
		return r28ArgumentBuilder.getArgumentList();
	}
	
	public String getInstaller(JRockitInstallation model) {
		return model.getR28().getInstaller();		
	}
	
	public String getUninstaller(JRockitInstallation model) {
		return model.getR28().getUninstaller();
	}

	public File getJvmExecutablePath(JRockitInstallation model) {
		return new File(model.getR28().getTargetDirectory());		
	}	

	public String getVersion() {
		return "Oracle JRockit(R) (build R28";
	}
			
}
