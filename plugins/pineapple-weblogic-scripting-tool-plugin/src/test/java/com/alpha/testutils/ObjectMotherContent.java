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

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.alpha.pineapple.plugin.weblogic.scriptingtool.model.ObjectFactory;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.model.Wlst;

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
        objectFactory = new com.alpha.pineapple.plugin.weblogic.scriptingtool.model.ObjectFactory();		
	}

	/**
     * Create empty WLST document.
     * 
     * @return empty WLST document.
     */
    public Wlst createEmptyWlst() {
        
    	Wlst doc = objectFactory.createWlst();
        return doc;        
    }

	/**
     * Create WLST document with script.
     * 
     * @param scriptFileName Name of script.
     * 
     * @return WLST document with script.
     */
    public Wlst createWlstDocumentWithNoProperties( String scriptFileName ) {
        
    	Wlst doc = createEmptyWlst();    	
        doc.setWlstScript(objectFactory.createWlstScript());
        doc.getWlstScript().setFile( scriptFileName );    	
        return doc;        
    }

	/**
     * Create WLST document with script and properties.
     * 
     * @param scriptFileName Name of script.
     * @param scriptFileName Name of properties.
     * 
     * @return WLST document with script and properties..
     */
    public Wlst createWlstDocument( String scriptFileName, String propertiesFileName  ) {
        
    	Wlst doc = createEmptyWlst();    	
        doc.setWlstScript(objectFactory.createWlstScript());
        doc.getWlstScript().setFile( scriptFileName );
        doc.getWlstScript().setPropertiesFile( propertiesFileName );    	        
        return doc;        
    }
    
    
    /**
     * Write hello world Python script.
     * 
     * @param scriptDirectory Directory where script is written.
     * 
     * @return File object describing where script is written.
     * 
     * @throws Exception If writing script fails.
     */
    public File writeHelloWorldScript(File scriptDirectory) throws Exception {
    	
		// create script file name
		File scriptFile = new File(scriptDirectory, "script.py" ); 
		
		// create script data
		StringBuffer fileData = new StringBuffer();
		fileData.append("def main():\n");
		fileData.append("    print ('Hello world!')\n");
		fileData.append("\n");		
		fileData.append("main()\n");		
				
		// write simple script
		FileUtils.writeStringToFile(scriptFile, fileData.toString());
    	
		return scriptFile;
    }
    
    /**
     * Write hello world Python script which prints the module path from
     * a system property 
     * 
     * @param scriptDirectory Directory where script is written.
     * 
     * @return File object describing where script is written.
     * 
     * @throws Exception If writing script fails.
     */
    public File writeModulePathSystemPropertyScript(File scriptDirectory) throws Exception {
    	
		// create script file name
		File scriptFile = new File(scriptDirectory, "script.py" ); 
		
		// create script data
		StringBuffer fileData = new StringBuffer();
		fileData.append("import java\n");
		fileData.append("\n");		
		fileData.append("def main():\n");
		fileData.append("    print java.lang.System.getProperty('pineapple.module.path')\n");
		fileData.append("\n");		
		fileData.append("main()\n");		
				
		// write simple script
		FileUtils.writeStringToFile(scriptFile, fileData.toString());
    	
		return scriptFile;
    }
    
}
