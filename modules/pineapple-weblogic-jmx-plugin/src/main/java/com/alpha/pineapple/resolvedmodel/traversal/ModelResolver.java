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

import java.util.HashMap;

import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.session.Session;

/**
 * Resolves entities from models.
 */
public interface ModelResolver
{

    /**
     * Configure resolver with a Pineapple session object which enables the resolver 
     * to access a Pineapple resource if needed. 
     * 
     * @param session Session object.
     */
    public void setSession(Session session);    
	
    /**
     * Resolve attributes from an resolved attribute. Returns an array containing 
     * attributes names for the resolved attribute.
     *  
     * @param participant Resolve participant to resolve attribute names for.
     * 
     * @return Array containing attributes names for the resolved attribute.
     * 
     * @throws ModelResolutionFailedException If name resolution fails. 
     */
    public String[] resolveAttributeNames( ResolvedParticipant participant ) throws ModelResolutionFailedException;


    /**
     * Resolve attribute from attribute name. If attribute is resolved
     * then is returned as a <code>ResolvedParticipant</code>.
     *  
     * @param attributeName Attribute name which should be resolved.
     * @param participant Resolved participant containing the named attribute which should be resolved.
     * 
     * @return The result of the attribute resolution.
     */
    public ResolvedParticipant resolveAttribute( String attributeName, ResolvedParticipant participant );
    
    /**
     * Resolve the value of an resolved participant whose type is an collection. The content
     * of the collection is resolved into a <code>ResolvedParticipant</code> for each entry/value 
     * in the collection.
     *  
     * @param participant The resolved participant whose value is an collection.
     *  
     * @return HashMap which contains an entry as an resolved participant for 
     * each collection entry/value in the original resolved collection. The id identifies
     * the entry in some meaningful way, i.e. a name or an index. But it depends 
     * on the underlying model.
     * 
     * @throws ModelResolutionFailedException If resolution fails.
     */
    public HashMap<String, ResolvedParticipant> resolveCollectionAttributeValues( ResolvedParticipant participant ) throws ModelResolutionFailedException;    
    
	/**
	 * Create a resolved participant to represent a non existing entry in a collection. 
	 * The ID is equal to the supplied id argument.
	 * The type is resolved from the type of the parent resolved collection. 
	 * The value state is set to FAILED.
	 * The value is set to null.      
	 *   
	 * @param id Id of the entry/value if it would have existed. 
	 * @param parent Parent resolved type which is a collection which doesn't contain the entry to be created. 
	 * 
	 * @return resolved participant to represent a non existing entry in a collection. The purpose of the result is 
	 * to aid the possible creation of the entry in the model.  
	 */
	public ResolvedParticipant createNonExistingCollectionValue( String id, ResolvedParticipant parent);
    
	/**
	 * Create resolved type based on the parent resolved type and the primary and secondary participants.
	 * 
	 * @param parent Parent resolved type.
	 * @param primary Primary participant.
	 * @param secondary Secondary participant.
	 * 
	 * @return resolved type based on the parent resolved type and the primary and secondary participants.
	 */	
	public ResolvedType createResolvedType(ResolvedType parent, ResolvedParticipant primary, ResolvedParticipant secondary );       
    	
}
