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


package com.alpha.pineapple.plugin.weblogic.scriptingtool.operation;

import static com.alpha.pineapple.plugin.weblogic.scriptingtool.WeblogicScriptingToolConstants.DEFAULT_PROCESS_TIMEOUT;
import static com.alpha.pineapple.plugin.weblogic.scriptingtool.WeblogicScriptingToolConstants.LEGAL_CONTENT_TYPES;
import static com.alpha.pineapple.plugin.weblogic.scriptingtool.WeblogicScriptingToolConstants.PRODUCT_HOME_RESOURCE_PROPERTY;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.ExecutableNameUtils;
import com.alpha.javautils.OperationUtils;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.model.Wlst;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.model.WlstScript;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.utils.ScriptHelper;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.test.Asserter;

@PluginOperation( OperationNames.DEPLOY_CONFIGURATION )
public class DeployConfiguration implements Operation
{
		
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
     * Operation utilities.
     */
    @Resource
    OperationUtils operationUtils;
    
    /**
     * Hamcrest asserter.
     */
    @Resource
    Asserter asserter;
    
    /**
     * Script helper.
     */
    @Resource
    ScriptHelper scriptHelper;          
    
    /**
     * Resource property getter.
     */
    @Resource    
    ResourcePropertyGetter resourcePropertyGetter; 
        
    public void execute( Object content, Session session, ExecutionResult executionResult ) throws PluginExecutionFailedException
    {
        // validate parameters
        Validate.notNull( content, "content is undefined." );
        Validate.notNull( session, "session is undefined." );
        Validate.notNull( executionResult, "executionResult is undefined." );        
    	
        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { content.getClass().getName(), content };    	        	
        	String message = messageProvider.getMessage("dc.start", args );
        	logger.debug( message );
        }

        // validate parameters
        operationUtils.validateContentType(content, LEGAL_CONTENT_TYPES);
        operationUtils.validateSessionType(session, ProcessExecutionSession.class );
                        
        try
        {        	
            // type cast to WLST model object
        	Wlst model = (Wlst) content;
            
            // type cast to process execution session 
        	ProcessExecutionSession wlstSession = (ProcessExecutionSession) session; 

            // register execution result with asserter
            asserter.setExecutionResult(executionResult);

            // register resource with property getter
            resourcePropertyGetter.setResource(session.getResource());
            
            // run script
            runScript(model, wlstSession, executionResult);
                                    
            // compute execution state from children
            executionResult.completeAsComputed(messageProvider, "dc.completed", null, "dc.failed", null );    		                        
        }
        catch ( Exception e )
        {
        	Object[] args = { e.toString() };    	        	
        	String message = messageProvider.getMessage("dc.error", args );
            throw new PluginExecutionFailedException( message, e );
        }
    }

    /**
     * Run script. 
     *  
     * @param model Plugin model.
     * @param session Plugin session.
     * @param result execution result.
     * 
     * @throws Exception if execution fails. 
     */
	void runScript(Wlst model, ProcessExecutionSession session, ExecutionResult result) throws Exception {
		
		//exit if no model content is defined.
		if (!scriptHelper.isModelContentDefined(model)) return;

		// get product home from resource
		String productHome = resourcePropertyGetter.getProperty( PRODUCT_HOME_RESOURCE_PROPERTY );
		File productHomeDir = new File(productHome); 			

		// assert product home is valid
		if(!scriptHelper.isProductHomeValid(productHomeDir, asserter)) return;
		
		// get model content
		String propertiesFileName = null;		
		WlstScript wlstScript = model.getWlstScript();		
		String scriptFileName = wlstScript.getFile();
		boolean isDemoTrustEnabled = wlstScript.isDemoTrustEnabled();				
		boolean useFastRandomGenerator = scriptHelper.getFastRandomGeneratorForLinuxProperty(session);  
		
		// resolve module path
		scriptFileName = scriptHelper.resolveModulePath(result, scriptFileName);

		// assert script is valid
		if(!scriptHelper.isScriptValid(scriptFileName, asserter)) return;
		
		// resolve module path		
		if (scriptHelper.isPropertiesFileDefined(model)) {
			propertiesFileName = wlstScript.getPropertiesFile();
			propertiesFileName = scriptHelper.resolveModulePath(result, propertiesFileName);
			
			// assert properties file is valid			
			if(!scriptHelper.isPropertiesFileValid(propertiesFileName, asserter)) return;			
		}	

		// create argument list of system properties for script invocation
		String[] systemPropsArguments = scriptHelper.createSystemPropertiesArguments(isDemoTrustEnabled, useFastRandomGenerator, result); 			
		
		// log system arguments @TODO 
		
		// get provided OS specific wlst.xx script from within home directory
		File wlst = scriptHelper.getWlstInvoker(productHomeDir);
		
		// create temporary WLST invoker script
		File wlstInvoker = scriptHelper.createWlstInvokerScript(wlst, systemPropsArguments);
		
		// assert WLST invoker is valid		
		if(!scriptHelper.isWlstInvokerValid(wlst, asserter)) return;
		
		// escape installer path if required
		String wlstInvokerAsStr = ExecutableNameUtils.escapeExecutable(wlstInvoker.getAbsolutePath());
		
		// create argument list for script invocation
		String[] arguments = scriptHelper.createArguments(scriptFileName, propertiesFileName); 			
		
		// get description    	        	
    	String description = messageProvider.getMessage("dc.execute" );
		
		// execute external process    	
		session.execute(wlstInvokerAsStr , arguments, DEFAULT_PROCESS_TIMEOUT, description, result);				
	}
										
}
