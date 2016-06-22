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


package com.alpha.pineapple.plugin.weblogic.jmx.operation;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.OperationUtils;
import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.traversal.ModelTraversalDirector;
import com.alpha.pineapple.session.Session;

@PluginOperation( OperationNames.TEST )
public class TestOperation implements Operation
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
     * Traversal director which tests configuration.
     */
    @Resource
    ModelTraversalDirector testDirector;
        
    /**
     * Resolved model initializer. 
     */
    @Resource
    ResolvedModelInitializer modelInitializer;
    
    public void execute( Object content, Session session, ExecutionResult result ) throws PluginExecutionFailedException
    {
        // validate parameters
        Validate.notNull( content, "content is undefined." );
        Validate.notNull( session, "session is undefined." );
        Validate.notNull( result, "result is undefined." );        

        // log debug message
        if ( logger.isDebugEnabled() )
        {        	
        	Object[] args = { content.getClass().getName(), content };    	        	
        	String message = messageProvider.getMessage("to.start", args );
        	logger.debug( message );
        }

        // validate parameters
        operationUtils.validateContentType(content, WebLogicMBeanConstants.LEGAL_CONTENT_TYPES );
        operationUtils.validateSessionType(session, WeblogicJMXEditSession.class );
                            
        try
        {
            // initialize edit session
            WeblogicJMXEditSession editSession = initializeEditsession( session );

            // declare resolve type
            ResolvedType resolvedRoot = modelInitializer.initialize(content, editSession);
        	
            // recursive traversal of the model
            testDirector.startTraversal( editSession, resolvedRoot, result );

            // save changes
    		editSession.saveAndActivate();		    		
            
    		// compute result    		
    		result.completeAsComputed(messageProvider, "to.completed", null, "to.failed", null);    		
        }
        catch ( Exception e )
        {
        	Object[] args = { StackTraceHelper.getStrackTrace( e ) };    	        	
        	String message = messageProvider.getMessage("to.error", args );
            throw new PluginExecutionFailedException( message, e );        	
        }
    }
   
	/**
     * Initialized the session, by setting the edit session into edit mode.
     * 
     * @param session The session object.
     * 
     * @return initialized edit session.
     * 
     * @throws Exception if Initialization fails.
     */
    WeblogicJMXEditSession initializeEditsession( Session session ) throws PluginExecutionFailedException
    {
        try
        {
            // type cast to edit session
            WeblogicJMXEditSession editSession = (WeblogicJMXEditSession) session;

            // start edit mode
            editSession.startEdit();
            
            return editSession;
        }
        catch ( Exception e )
        {
        	Object[] args = { e.toString() };
        	String message = messageProvider.getMessage("to.initialize_session_failed", args);
            throw new PluginExecutionFailedException( message, e );
        }
    }

}
