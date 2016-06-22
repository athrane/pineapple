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


package com.alpha.pineapple.plugin.jrockit.installation.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.test.Asserter;
import com.alpha.pineapple.test.matchers.PineappleMatchers;

/**
 * <p>Implementation of the <code>org.apache.commons.chain.Command</code>  interface which 
 * creates a silent XML file for JRockit R27 installation as described here:
 * http://download.oracle.com/docs/cd/E13150_01/jrockit_jvm/jrockit/releases/R27/install/install.html#wp1046131.</p>
 * 
 * <p>Precondition for execution of the command is definition of these keys in 
 * the context:
 * 
 * <ul>
 * <li><code>target-directory</code> defines target directory where JRockit is installed. The type is 
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>install-public-jre</code> defines whether the JVM should installed
 * as a public JRE. The type is<code>java.lang.Boolean</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which collects
 * information about the execution of the test. The type is 
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>   
 * </ul>
 * </p>      
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in the context:
 * <ul>
 * <li><code>silent-xml</code> contains a file object which represents the created 
 * silent XML file. The type is <code>java.io.File</code>.</li>
 * </ul>
 * </p> 
 * 
 * <p>Postcondition after execution of the command is: 
 * <ul> 
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the command succeeded. If the 
 * command failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the command fails due to an exception then the exception isn't caught, 
 * but passed on to the invoker whose responsibility it is to catch it and update 
 * the <code>ExecutionResult</code> with the state <code>ExecutionState.ERROR</code>.
 * </li>
 * </ul>  
 * </p>        
 */

public class CreateR27SilentXmlCommand implements Command {

	/**
	 * Silent XML file name. 
	 */
	static final String SILENTINSTALL_JROCKIT_XML = CreateR27SilentXmlCommand.class.getCanonicalName()+ ".xml";
	
    /**
     * Key used to identify property in context: Target directory where JRockit is installed.
     */
    public static final String TARGET_DIRECTORY = "target-directory";

    /**
     * Key used to identify property in context: Defines whether the JVM should installed as a public JRE. 
     */
    public static final String PUBLIC_JRE = "install-public-jre";

    /**
     * Key used to identify property in context: Contains execution result object.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";

    /**
     * Key used to identify property in context: Contains returned silent XML file.
     */    
	public static final Object SILENTXML_FILE = "silent-xml";
    
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * Target directory where JRockit is installed.
     */
    @Initialize( TARGET_DIRECTORY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )    
    String targetDirectory;

    /**
     * Defines whether the JVM should installed as a public JRE.
     */
    @Initialize( PUBLIC_JRE )
    @ValidateValue( ValidationPolicy.NOT_NULL )        
    Boolean isPublicJre;

    /**
     * Defines execution result object.
     */
    @Initialize( EXECUTIONRESULT_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )    
    ExecutionResult executionResult;

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
    
    /**
     * Runtime directory provider.
     */
    @Resource
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;
    
    /**
     * Hamcrest asserter.
     */
    @Resource
    Asserter asserter;

    /**
     * Pineapple matcher factory.
     */
    @Resource
    PineappleMatchers pineappleMatchers;
                
    public boolean execute( Context context ) throws Exception 
    {    
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("cr27sxc.start" ) );        	
        }
    	
        // initialize command
        CommandInitializer initializer =  new CommandInitializerImpl();
        initializer.initialize( context, this );
        
        // register execution result with asserter
        asserter.setExecutionResult(executionResult);
        
        // create XML
        createSilentXml( context );        

        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("cr27sxc.completed" ) );        	
        }
        
        return Command.CONTINUE_PROCESSING;
    }

    
    /**
	 * Create silent XML file for R27.
     * 
     * @param context
     *            Command context.
     * 
     * @throws Exception
     *             If execution fails.
     */
    void createSilentXml( Context context ) throws Exception
    {
		// get temporary directory
		File tempDirectory = coreRuntimeDirectoryProvider.getTempDirectory();
    	    	
		// create file object		
		File silentXMLFile = new File(tempDirectory , SILENTINSTALL_JROCKIT_XML);

		// delete file if it exists
		if(silentXMLFile.exists()) {
			boolean succeded = silentXMLFile.delete();
			
			// handle failure to delete file
			if(!succeded) {
		    	Object[] args = { silentXMLFile };
		    	executionResult.completeAsFailure(messageProvider, "cr27sxc.delete_oldsilentxml_failed", args);
		    	return;				
			}			
		}
		
		// write silent XML file 
		Collection<String> lines = new ArrayList<String>();
		lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		lines.add("<bea-installer>");
		lines.add("  <input-fields>");
		lines.add("     <data-value name=\"USER_INSTALL_DIR\" value=\""+targetDirectory+"\" />");		
		lines.add("     <data-value name=\"INSTALL_PUBLIC_JRE\" value=\""+isPublicJre.toString()+"\" />");		
		lines.add("  </input-fields>");
		lines.add("</bea-installer>");		
		
		// write the file
		FileUtils.writeLines(silentXMLFile, lines);				
		
		// create file exist matcher
		Matcher<File> fileMatcher = pineappleMatchers.doesFileExist();
		
		// assert whether file exists
    	Object[] args = { silentXMLFile };    	        	
    	String description = messageProvider.getMessage("cr27sxc.assert_silentxml_exists", args );		
		asserter.assertObject(silentXMLFile, fileMatcher, description);

		// handle positive case
		if (asserter.lastAssertionSucceeded()) {
			
			// add file to context
			context.put(SILENTXML_FILE, silentXMLFile);

			// complete as success
	    	Object[] args2 = { silentXMLFile };
	    	executionResult.completeAsSuccessful(messageProvider, "cr27sxc.succeed", args);
	    	return;							
		}
				
		// add null to context
		context.put(SILENTXML_FILE, null);
		
		// complete as failure
    	Object[] args2 = { silentXMLFile };
    	executionResult.completeAsFailure(messageProvider, "cr27sxc.failed", args);		
    }    
            
}
