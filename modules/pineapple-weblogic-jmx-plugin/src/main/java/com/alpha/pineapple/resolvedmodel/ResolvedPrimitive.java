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


package com.alpha.pineapple.resolvedmodel;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.resolvedmodel.traversal.ResolvedModelVisitor;

/**
 * Resolved type which is a primitive. 
 */
public class ResolvedPrimitive extends ResolvedTypeImpl {

    /**
     * ResolvedPrimitive constructor. 
     *
     * @param parent Resolved parent type.
     * @param primaryParticipant Primary participant in resolved type.
     * @param secondaryParticipant Secondary participant in resolved type.
     */
    ResolvedPrimitive( ResolvedType parent, ResolvedParticipant primaryParticipant, ResolvedParticipant secondaryParticipant )
    {
        super(parent, primaryParticipant, secondaryParticipant);
    }	
    
    @Override
	public Object accept(ResolvedModelVisitor visitor, ExecutionResult result) {
    	return visitor.visit(this, result);
	}    
    
}
