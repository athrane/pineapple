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
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.jrockit.installation.argument.R27ArgumentBuilder;
import com.alpha.pineapple.plugin.jrockit.installation.command.CreateR27SilentXmlCommand;
import com.alpha.pineapple.plugin.jrockit.installation.model.JRockitInstallation;

/**
 * Implementation of the {@link InstallationConfigurer} which
 * prepares the installation of the R27 installer.
 */
public class R27InstallationConfigurerImpl implements InstallationConfigurer {

	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	
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
     * R27 creation silent XML command.
     */
    @Resource
    Command createR27SilentXmlCommand;
    
    /**
     * R27 Argument Builder object.
     */   
    @Resource    
    R27ArgumentBuilder r27ArgumentBuilder;

    /**
     * 
     */    
	boolean isSilentCreationSuccessful;
            
	@SuppressWarnings("unchecked")
	public File createSilentXmlFile(JRockitInstallation model) {
		
    	// create description
    	String message = messageProvider.getMessage("r27ici.create_r27silentxml" );        	
    	
        // create context
        Context context = commandRunner.createContext();            
        
        // intialize context
        context.put(CreateR27SilentXmlCommand.TARGET_DIRECTORY, model.getR27().getTargetDirectory());
        context.put(CreateR27SilentXmlCommand.PUBLIC_JRE, new Boolean(model.getR27().isInstallPublicJre()));        
        
        // run test            
        commandRunner.run(createR27SilentXmlCommand, message, context);                        
        
        // store state
        isSilentCreationSuccessful = commandRunner.lastExecutionSucceeded();
        
        // get silent XML from context
        File file = (File) context.get(CreateR27SilentXmlCommand.SILENTXML_FILE);
		
		// return the file object
		return file;
	}

	
	public boolean isSilentXmlCreationSuccessful() {
		return isSilentCreationSuccessful;		
	}

	
	public String[] createArgumentList(File silentXmlFile, File silentLogFile) {
		r27ArgumentBuilder.buildArgumentList();
		r27ArgumentBuilder.addInstallationSilentModeArgument();
		r27ArgumentBuilder.addSilentXMLArgument(silentXmlFile);
		r27ArgumentBuilder.addLogArgument(silentLogFile);
		return r27ArgumentBuilder.getArgumentList();
	}

	
	public String[] createUninstallationArgumentList(File silentLogFile) {
		r27ArgumentBuilder.buildArgumentList();		
		r27ArgumentBuilder.addUninstallationSilentModeArgument();
		r27ArgumentBuilder.addLogArgument(silentLogFile);
		return r27ArgumentBuilder.getArgumentList();
	}

	
	public String getInstaller(JRockitInstallation model) {
		return model.getR27().getInstaller();		
	}
	
	
	public String getUninstaller(JRockitInstallation model) {
		return model.getR27().getUninstaller();
	}

	
	public File getJvmExecutablePath(JRockitInstallation model) {		
		return new File(model.getR27().getTargetDirectory());
	}

	
	public String getVersion() {
		return "BEA JRockit(R) (build R27";
	}
	
}
