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

import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.traversal.strategy.TraversalStrategy;
import com.alpha.pineapple.session.Session;

/**
 * Implementation of the <code>ModelTraversalDirector</code> which support
 * a combined pre-order and post-order traversal of an resolved model.
 */
public class DefaultTraversalDirectorImpl implements ModelTraversalDirector {

	/**
	 * Null execution result used with the description generator. 
	 */
	ExecutionResult NULL_RESULT = null;	
	
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
     * Traversal strategy. 
     * Configured by constructor injection.
     */
    TraversalStrategy strategy;    

    /**
     * Resolved model visitors
     * Configured by constructor injection. 
     */
    ResolvedModelVisitor[] postOrderVisitors;

    /**
     * Resolved model visitors
     * Configured by constructor injection. 
     */
    ResolvedModelVisitor[] preOrderVisitors;
    
    /**
     * Description generating visitor which can generate descriptions from resolved objects.
     */
    ResolvedModelVisitor descriptionGenerator;
        
    /**
     * DefaultModelTraversalDirectorImpl constructor.
     *  
     * @param visitors Collection of resolved model visitors which will visit each traversed nodes
     * in the order they have in the array. 
     * @param strategy Traversal strategy used to determine whether traversal should
     * continue on a resolved model node.
     * @param descriptionGenerator Description generating visitor which can generate 
     * descriptions from resolved objects.
     */
    public DefaultTraversalDirectorImpl(ResolvedModelVisitor[] preOrderVisitors,
    		ResolvedModelVisitor[] postOrderVisitors,
    		TraversalStrategy strategy, 
    		ResolvedModelVisitor descriptionGenerator ) {
    	this.preOrderVisitors = preOrderVisitors;
    	this.postOrderVisitors = postOrderVisitors;    	    	
    	this.strategy = strategy;
    	this.descriptionGenerator = descriptionGenerator;
    }
    
	public void traverse(ResolvedType type, ExecutionResult parentResult) throws Exception {
		
        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { type };    	        	
        	String message = messageProvider.getMessage("dtd.start", args );
        	logger.debug( message );
        }
				
        // declare execution result for this type 
        ExecutionResult currentResult = null;
        
        try {
            // visit only if strategy directs it 
            if(!strategy.continueTraversal( type )) return;
     
            // create execution result
            String description = (String) type.accept( descriptionGenerator, NULL_RESULT);
            currentResult = parentResult.addChild(description);
     
    		// visit the current resolved object pre-order
            for (ResolvedModelVisitor visitor : preOrderVisitors) {
            	type.accept(visitor, currentResult);
            }
            
            // traverse children
    		for ( ResolvedType childType: type.getChildren() ) {
    			traverse(childType, currentResult );
    		}
                                                                
    		// visit the current resolved object post-order
            for (ResolvedModelVisitor visitor : postOrderVisitors) {
            	type.accept(visitor, currentResult);
            }
    		    		
    		// if result isn't executing then exit     		            
            if (!currentResult.isExecuting()) return;            
            
            // if result is executing then compute its state
        	Object[] args = { type };
        	currentResult.completeAsComputed(messageProvider, "dtd.completed", args);        	            	
    		    		
        } catch (Exception e) {

			// log development debug message
        	if (logger.isDebugEnabled()) logger.debug("DEBUG DEBUG //: " + StackTraceHelper.getStrackTrace(e));        	
        	
        	// if result isn't executing then exit
            if (!currentResult.isExecuting()) return;
            
            // if result is executing then compute its state
            currentResult.completeAsError(messageProvider, "dtd.error", null, e);                
        }        
	}

	@Override
	public void startTraversal(Session session, ResolvedType type, ExecutionResult parentResult) throws Exception {

		// set session on visitors		
        for (ResolvedModelVisitor visitor : preOrderVisitors) {
        	visitor.setSession(session);
        }

		// set session on visitors		
        for (ResolvedModelVisitor visitor : postOrderVisitors) {
        	visitor.setSession(session);
        }
        
		// start traversal
		traverse(type, parentResult);
	}
	
}
