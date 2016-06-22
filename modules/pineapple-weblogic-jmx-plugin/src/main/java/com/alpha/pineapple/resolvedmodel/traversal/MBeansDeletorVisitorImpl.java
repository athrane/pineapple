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


package com.alpha.pineapple.resolvedmodel.traversal;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.command.DeleteMBeanCommand;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedCollection;
import com.alpha.pineapple.resolvedmodel.ResolvedEnum;
import com.alpha.pineapple.resolvedmodel.ResolvedObject;
import com.alpha.pineapple.resolvedmodel.ResolvedPrimitive;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.UnresolvedType;
import com.alpha.pineapple.session.Session;

/**
 * Implementation of the <code>ResolvedModelVisitor </code>
 * which can delete MBeans from the content of an resolved model.  
 */
public class MBeansDeletorVisitorImpl implements ResolvedModelVisitor {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	
    /**
     * Command runner
     */
    @Resource
    CommandRunner runner;        
        
    /**
     * Delete MBean command.
     */
    @Resource
    Command deleteMBeanCommand;
    
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;        

    /**
     * WebLogic JMX session.
     */
    WeblogicJMXEditSession jmxSession; 
    
    /**
     * MBeansDeletorVisitorImpl constructor. 
     */
    public MBeansDeletorVisitorImpl() {
    }
            
	public void setSession(Session session) {
		jmxSession = (WeblogicJMXEditSession)  session;		
	}

			
	public Object visit(ResolvedType resolvedType, ExecutionResult result) {
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object visit(ResolvedObject resolvedObject, ExecutionResult result) {
		
        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resolvedObject };    	        	
        	String message = messageProvider.getMessage("mbdv.visit_object", args );
        	logger.debug( message );
        }
    	        
        // create context
        Context context = runner.createContext();
        
        // setup context        
        context.put( DeleteMBeanCommand.JMXSESSION_KEY, jmxSession );
        context.put( DeleteMBeanCommand.RESOLVED_TYPE, resolvedObject );

        // run test            
        return runner.run(deleteMBeanCommand, result, context );        
	}

	@SuppressWarnings("unchecked")
	public Object visit(ResolvedEnum resolvedEnum, ExecutionResult result) {
		
	    // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resolvedEnum };    	        	
        	String message = messageProvider.getMessage("mbdv.visit_enum", args );
        	logger.debug( message );
        }
        
        return null;        
    }
	

	public Object visit(ResolvedCollection resolvedCollection, ExecutionResult result) {

	    // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resolvedCollection };    	        	
        	String message = messageProvider.getMessage("mbdv.visit_collection", args );
        	logger.debug( message );
        }
        
        return null;
	}

	@SuppressWarnings("unchecked")
	public Object visit(ResolvedPrimitive resolvedPrimitive, ExecutionResult result) {
		
        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resolvedPrimitive };    	        	
        	String message = messageProvider.getMessage("mbdv.visit_primitive", args );
        	logger.debug( message );
        }

        return null;
	}

	public Object visit(UnresolvedType unresolved, ExecutionResult result) {
		return null;
	}

}
