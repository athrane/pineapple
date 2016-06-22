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
import com.alpha.pineapple.plugin.weblogic.jmx.command.TestAttributeValueCommand;
import com.alpha.pineapple.plugin.weblogic.jmx.command.TestCollectionValueCommand;
import com.alpha.pineapple.plugin.weblogic.jmx.command.TestEnumValueCommand;
import com.alpha.pineapple.plugin.weblogic.jmx.command.TestObjectIdentityCommand;
import com.alpha.pineapple.resolvedmodel.ResolvedCollection;
import com.alpha.pineapple.resolvedmodel.ResolvedEnum;
import com.alpha.pineapple.resolvedmodel.ResolvedObject;
import com.alpha.pineapple.resolvedmodel.ResolvedPrimitive;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.UnresolvedType;
import com.alpha.pineapple.session.Session;

/**
 * Implementation of the <code>ResolvedModelVisitor </code>
 * which can test the content of an resolved model.  
 */
public class ResolvedModelTesterVisitorImpl implements ResolvedModelVisitor {

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
     * Test object identity command.
     */
    @Resource
    Command testObjectIdentityCommand;

    /**
     * Test attribute value command.
      */
    @Resource
    Command testAttributeValueCommand;

    /**
     * Test collection value command.
     */
    @Resource
    Command testCollectionValueCommand;

    /**
     * test enum value command.
     */
    @Resource
    Command testEnumValueCommand;
    
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;        

    
    /**
     * ResolvedModelTesterImpl constructor. 
     */
    public ResolvedModelTesterVisitorImpl() {
    }
    
	public void setSession(Session session) {
		// ignores session for now.	
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
        	String message = messageProvider.getMessage("rmtv.visit_object", args );
        	logger.debug( message );
        }
    	        
        // create context
        Context context = runner.createContext();
        
        // setup context
        context.put( TestObjectIdentityCommand.RESOLVED_TYPE, resolvedObject );

        // run test            
        return runner.run(testObjectIdentityCommand, result, context );        
	}

	@SuppressWarnings("unchecked")
	public Object visit(ResolvedEnum resolvedEnum, ExecutionResult result) {
		
	    // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resolvedEnum };    	        	
        	String message = messageProvider.getMessage("rmtv.visit_enum", args );
        	logger.debug( message );
        }
    	
        // create context
        Context context = runner.createContext();
        
        // setup context
        context.put( TestEnumValueCommand.RESOLVED_TYPE, resolvedEnum );

        // run test            
        return runner.run( testEnumValueCommand, result, context);
    }
	

	@SuppressWarnings("unchecked")
	public Object visit(ResolvedCollection resolvedCollection, ExecutionResult result) {

	    // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resolvedCollection };    	        	
        	String message = messageProvider.getMessage("rmtv.visit_collection", args );
        	logger.debug( message );
        }
        
        // create context
        Context context = runner.createContext();
        
        // setup context
        context.put( TestCollectionValueCommand.RESOLVED_TYPE, resolvedCollection);

        // run test            
        return runner.run( testCollectionValueCommand, result, context);
	}

	@SuppressWarnings("unchecked")
	public Object visit(ResolvedPrimitive resolvedPrimitive, ExecutionResult result) {
		
        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resolvedPrimitive };    	        	
        	String message = messageProvider.getMessage("rmtv.visit_primitive", args );
        	logger.debug( message );
        }
        
        // create context
        Context context = runner.createContext();
        
        // setup context
        context.put( TestAttributeValueCommand.RESOLVED_TYPE, resolvedPrimitive );

        // run test            
        return runner.run( testAttributeValueCommand, result, context);
	}

	public Object visit(UnresolvedType unresolved, ExecutionResult result) {
		return null;
	}

}
