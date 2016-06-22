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
import com.alpha.pineapple.resolvedmodel.ResolvedCollection;
import com.alpha.pineapple.resolvedmodel.ResolvedEnum;
import com.alpha.pineapple.resolvedmodel.ResolvedObject;
import com.alpha.pineapple.resolvedmodel.ResolvedPrimitive;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.UnresolvedType;
import com.alpha.pineapple.session.Session;

/**
 * Interface for visitor which can visit an resolved model.  
 */
public interface ResolvedModelVisitor
{
    /**
     * Configure visitor with a Pineapple session object which enables the visitor 
     * to access a Pineapple resource if needed. 
     * 
     * @param session Session object.
     */
    public void setSession(Session session);    
	
    /**
     * Visit resolved type.
     * 
     * @param resolvedType Resolved type which couldn't be resolved to a 
     * more specific type.
     * 
     * @return Optional object if visitor produces a product. 
     */
    public Object visit(ResolvedType resolvedType, ExecutionResult result);
    
    /**
     * Visit resolved object.
     * 
     * @param resolvedObject Type resolved to object.
     * 
     * @return Optional object if visitor produces a product. 
     */
    public Object visit(ResolvedObject resolvedObject, ExecutionResult result);
    
    /**
     * Visit resolved enumeration.
     * 
     * @param resolvedEnum Type resolved to enumeration.
     * 
     * @return Optional object if visitor produces a product. 
     */
    public Object visit(ResolvedEnum resolvedEnum, ExecutionResult result);

    /**
     * Visit resolved collection.
     * 
     * @param resolvedCollection Type resolved to collection.
     * 
     * @return Optional object if visitor produces a product. 
     */
    public Object visit(ResolvedCollection resolvedCollection, ExecutionResult result);

    /**
     * Visit resolved primitive.
     * 
     * @param resolvedPrimitive Type resolved to primitive.
     * 
     * @return Optional object if visitor produces a product. 
     */
    public Object visit(ResolvedPrimitive resolvedPrimitive, ExecutionResult result);

    /**
     * Visit unresolved type.
     * 
     * @param unresolved Unresolved type due to exception.
     * 
     * @return Optional object if visitor produces a product. 
     */
    public Object visit(UnresolvedType unresolved, ExecutionResult result);
    
}
