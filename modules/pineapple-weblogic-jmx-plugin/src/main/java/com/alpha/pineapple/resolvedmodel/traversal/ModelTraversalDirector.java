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

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.session.Session;

/**
 * Directs traversal of two models.
 */
public interface ModelTraversalDirector {

    /**
     * Start traversal.
     *
     * @param session Session accessed during the traversal.  
     * @param type Resolved type which are roots of the models which should be traversed.
     * @param parentResult Execution result for parent type. The parent result must be argumented
     * with a new execution result which track how the traversal proceeds of the current type proceeds.
     *  
     * @throws Exception If Traversal fails.
     */
    public void startTraversal (Session session, ResolvedType type, ExecutionResult parentResult) throws Exception;
	
    /**
     * Traverse model recursively.
     *  
     * @param type Resolved type which are roots of the models which should be traversed.
     * @param parentResult Execution result for parent type. The parent result must be argumented
     * with a new execution result which track how the traversal proceeds of the current type proceeds.
     *  
     * @throws Exception If Traversal fails.
     */
    public void traverse (ResolvedType type, ExecutionResult parentResult) throws Exception;
    	
}

