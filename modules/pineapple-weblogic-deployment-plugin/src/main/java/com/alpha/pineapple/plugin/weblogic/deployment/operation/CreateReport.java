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


package com.alpha.pineapple.plugin.weblogic.deployment.operation;

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
import com.alpha.pineapple.plugin.weblogic.deployment.WebLogicDeploymentConstants;
import com.alpha.pineapple.plugin.weblogic.deployment.session.WeblogicDeploymentSession;
import com.alpha.pineapple.session.Session;

@PluginOperation(OperationNames.CREATE_REPORT)
public class CreateReport implements Operation
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
        	String message = messageProvider.getMessage("cr.start", args );
        	logger.debug( message );
        }
        
        // validate parameters
        operationUtils.validateContentType(content, WebLogicDeploymentConstants.LEGAL_CONTENT_TYPES);
        operationUtils.validateSessionType(session, WeblogicDeploymentSession.class );

        try
        {        
            
    		// compute result    		
    		result.completeAsComputed(messageProvider, "cr.completed", null, "cr.failed", null);        	            
        }
        catch ( Exception e )
        {
        	Object[] args = { StackTraceHelper.getStrackTrace( e ) };    	        	
        	String message = messageProvider.getMessage("cr.error", args );
            throw new PluginExecutionFailedException( message, e );        	
        }
    }
        
}
