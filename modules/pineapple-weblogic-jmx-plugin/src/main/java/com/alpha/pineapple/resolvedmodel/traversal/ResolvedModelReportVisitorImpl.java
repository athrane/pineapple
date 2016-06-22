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
import com.alpha.pineapple.plugin.weblogic.jmx.command.ReportCommand;
import com.alpha.pineapple.resolvedmodel.ResolvedCollection;
import com.alpha.pineapple.resolvedmodel.ResolvedEnum;
import com.alpha.pineapple.resolvedmodel.ResolvedObject;
import com.alpha.pineapple.resolvedmodel.ResolvedPrimitive;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.UnresolvedType;
import com.alpha.pineapple.session.Session;

/**
 * Implementation of the <code>ResolvedModelVisitor </code>
 * which can generate a report from the content of the secondary participants
 * of a resolved model.  
 */
public class ResolvedModelReportVisitorImpl implements ResolvedModelVisitor {

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
     * Report command.
     */
    @Resource
    Command reportCommand;
    
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;        

    
    /**
     * ResolvedModelTesterImpl constructor. 
     */
    public ResolvedModelReportVisitorImpl() {
    }
    
	public void setSession(Session session) {
		// ignores session for now.	
	}
    
	public Object visit(ResolvedType resolvedType, ExecutionResult result) {
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object visit(ResolvedObject resolvedObject, ExecutionResult result) {
		    	        
        // create context
        Context context = runner.createContext();
        
        // setup context
        context.put( ReportCommand.RESOLVED_TYPE, resolvedObject );

        // run test            
        return runner.run(reportCommand, result, context );        
	}

	@SuppressWarnings("unchecked")
	public Object visit(ResolvedEnum resolvedEnum, ExecutionResult result) {
		    	
        // create context
        Context context = runner.createContext();
        
        // setup context
        context.put( ReportCommand.RESOLVED_TYPE, resolvedEnum );

        // run test            
        return runner.run( reportCommand, result, context);
    }
	

	@SuppressWarnings("unchecked")
	public Object visit(ResolvedCollection resolvedCollection, ExecutionResult result) {
        
        // create context
        Context context = runner.createContext();
        
        // setup context
        context.put( ReportCommand.RESOLVED_TYPE, resolvedCollection);

        // run test            
        return runner.run( reportCommand, result, context);
	}

	@SuppressWarnings("unchecked")
	public Object visit(ResolvedPrimitive resolvedPrimitive, ExecutionResult result) {
		
        // create context
        Context context = runner.createContext();
        
        // setup context
        context.put( ReportCommand.RESOLVED_TYPE, resolvedPrimitive );

        // run test            
        return runner.run( reportCommand, result, context);
	}

	@SuppressWarnings("unchecked")
	public Object visit(UnresolvedType unresolved, ExecutionResult result) {

        // create context
        Context context = runner.createContext();
        
        // setup context
        context.put( ReportCommand.RESOLVED_TYPE, unresolved);

        // run test            
        return runner.run( reportCommand, result, context);		
	}

}
