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
 * Pair of corresponding entities in traversal of two models.  
 */
public interface ResolvedType
{

    /**
     * Returns the primary traversal participant.
     * 
     * @return the primary traversal participant.
     */
    ResolvedParticipant getPrimaryParticipant();

    /**
     * Returns the secondary traversal participant.
     * 
     * @return the secondary traversal participant.
     */    
    ResolvedParticipant getSecondaryParticiant();

    /**
     * Get parent. Is null if pair is root objects in a traversal.
     *    
     * @return Get parent pair.
     */
    ResolvedType getParent();
    
    /**
     * Returns true is pair is parent in traversal.
     * 
     * @return true is pair is parent in traversal.
     */
    boolean isRoot();
    
    /**
     * Accept visitor from visitor object.
     * 
     * @param visitor visitor object.
     * 
     * @return Optional product generated by visitor.
     */
    public Object accept(ResolvedModelVisitor visitor, ExecutionResult result);

    /**
     * Return children as an array.
     *  
     * @return children as an array.
     */
	ResolvedType[] getChildren();
	
	/**
	 * Return true if the type contains a child whose
	 * primary participant contains the queried ID. 
	 * 
	 * @param id Id of the primary participant.
	 * 
	 * @return true if the type contains a child whose
	 * primary participant contains the queried ID.
	 */
	boolean containsChildWithPrimaryId(String id);
	
	/**
	 * Return child whose whose primary participant contains the queried ID. If no child
	 * can be found then null is returned. 
	 * 
	 * @param id Id of the primary participant.
	 * 
	 * @return child whose primary participant contains the queried ID. If no child
	 * can be found then null is returned.
	 * 
	 */
	ResolvedType getChildByPrimaryId(String id);
	
	/**
	 * Update secondary participant.
	 * 
	 * @param secondary Secondary participant which should replace 
	 * the existing participant.
	 * 
	 */
	void updateSecondaryParticipant(ResolvedParticipant secondary);
	
}
