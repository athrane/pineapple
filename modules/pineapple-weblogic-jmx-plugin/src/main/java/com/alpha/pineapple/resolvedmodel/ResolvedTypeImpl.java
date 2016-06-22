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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.resolvedmodel.traversal.ResolvedModelVisitor;

/**
 * Implementation of the <code>ResolvedType</code> interface which represents
 * a resolved type with no type information.
 */
public class ResolvedTypeImpl implements ResolvedType
{
    
	static final String[] logExcludes = {"logger", "parent","children" };
	
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
  
    /**
     * Primary participant in resolved type.
     */
    ResolvedParticipant primaryParticipant;

    /**
     * Secondary participant in resolved type.
     */    
    ResolvedParticipant secondaryParticipant;

    /**
     * Parent pair.
     */
    ResolvedType parent;    
    
    /**
     * Resolved children.
     */
    Collection<ResolvedType> children;
    
    /**
     * ResolvedTypeImpl constructor for pair with undefined parent. 
     * 
     * @param primaryParticipant Primary participant in resolved type.
     * @param secondaryParticipant Secondary participant in resolved type.
     */
    ResolvedTypeImpl( ResolvedParticipant primaryParticipant, ResolvedParticipant secondaryParticipant )
    {
        this (null, primaryParticipant, secondaryParticipant);
    }    

    /**
     * ResolvedTypeImpl constructor. 
     *
     * @param parent Resolved parent type. 
     * @param primaryParticipant Primary participant in resolved type.
     * @param secondaryParticipant Secondary participant in resolved type.
     */
    ResolvedTypeImpl( ResolvedType parent, ResolvedParticipant primaryParticipant, ResolvedParticipant secondaryParticipant )
    {
        super();
        
        Validate.notNull(primaryParticipant, "primaryParticipant is undefined");
        Validate.notNull(secondaryParticipant, "secondaryParticipant is undefined");
        
        this.parent = parent;
        this.primaryParticipant = primaryParticipant;
        this.secondaryParticipant = secondaryParticipant;
        this.children = new ArrayList<ResolvedType>();
    }    
    
    public ResolvedParticipant getPrimaryParticipant()
    {
        return primaryParticipant;
    }

    public ResolvedParticipant getSecondaryParticiant()
    {
        return secondaryParticipant;
    }

    
    public ResolvedType getParent()
    {
        return parent;
    }

    public boolean isRoot()
    {
        return (this.parent == null);
    }

    @Override
    public boolean equals( Object obj )
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

	public Object accept(ResolvedModelVisitor visitor, ExecutionResult result) {
    	return visitor.visit(this, result);		
	}
    
	public void addChild(ResolvedType resolvedChild) {
		this.children.add(resolvedChild);		
	}
		
    public ResolvedType[] getChildren() {    	
		return children.toArray(new ResolvedType[children.size()]);
	}
            
	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toStringExclude( this, logExcludes );                
    }

    /**
     * Create resolved type.
     * 
     * @param parent Resolved parent type.
     * @param primary Primary participant in resolved type.
     * @param secondary Secondary participant in resolved type.
     */
    public static ResolvedType createResolvedType( ResolvedType parent, ResolvedParticipant primary, ResolvedParticipant secondary ) {
        return new ResolvedTypeImpl(parent, primary, secondary);
    }
        
    /**
     * Create resolved type which represent objects with no parents 
     * and default name 'root'. The type of the objects is used as 
     * type for the resolved objects.
     * 
     * @param primary Primary object in resolved type.
     * @param secondary Secondary object in resolved type.
     */
    public static ResolvedObject createResolvedObject( Object primary, Object secondary ) {
        
        return createResolvedObject( "root", primary, secondary );        
    }

    /**
     * Create resolved type which represent objects with no parents. 
     * The type of the objects is used as type for the resolved objects.  
     * 
     * @param rootName Root name for both participants. 
     * @param primary Primary object in resolved type.
     * @param secondary Secondary object in resolved type.
     */
    public static ResolvedObject createResolvedObject( String rootName, Object primary, Object secondary ) {

    	// validate
        Validate.notNull(primary, "primary is undefined");
        Validate.notNull(secondary, "secondary is undefined");
    	
        // create resolved participant
        Class<? extends Object> primaryType = primary.getClass();
        ResolvedParticipant primaryAttribute = ResolvedParticipantImpl.createSuccessfulResult( rootName, primaryType, primary );

        // create resolved attribute
        Class<? extends Object> secondaryType = secondary.getClass();
        ResolvedParticipant secondaryAttribute = ResolvedParticipantImpl.createSuccessfulResult( rootName, secondaryType, secondary );
        
        return new ResolvedObject(null,primaryAttribute, secondaryAttribute);
    }
    
    
    /**
     * Create resolved type which represents an object.
     * 
     * @param parent Resolved parent type.
     * @param primary Primary participant in resolved type.
     * @param secondary Secondary participant in resolved type.
     */
    public static ResolvedObject createResolvedObject( ResolvedType parent, ResolvedParticipant primary, ResolvedParticipant secondary ) {
        
        return new ResolvedObject( parent, primary, secondary );
    }
    
    
    /**
     * Create resolved type which represents collection.
     * 
     * @param parent Resolved parent type.
     * @param primary Primary participant in resolved type.
     * @param secondary Secondary participant in resolved type.
     */
    public static ResolvedCollection createResolvedCollection( ResolvedType parent, ResolvedParticipant primary, ResolvedParticipant secondary ) {
        
        return new ResolvedCollection( parent, primary, secondary );
    }
        
    /**
     * Create resolved type which represents enumeration.
     * 
     * @param parent Resolved parent type.
     * @param primary Primary participant in resolved type.
     * @param secondary Secondary participant in resolved type.
     */
    public static ResolvedEnum createResolvedEnum( ResolvedType parent, ResolvedParticipant primary, ResolvedParticipant secondary ) {
        
        return new ResolvedEnum( parent, primary, secondary );
    }

    /**
     * Create resolved type which represents primitive.
     * 
     * @param parent Resolved parent type.
     * @param primary Primary participant in resolved type.
     * @param secondary Secondary participant in resolved type.
     */
    public static ResolvedPrimitive createResolvedPrimitive( ResolvedType parent, ResolvedParticipant primary, ResolvedParticipant secondary ) {
        
        return new ResolvedPrimitive( parent, primary, secondary );
    }

    /**
     * Create type which represents an unresolved type due to an exception.
     * 
     * @param parent Resolved parent type.
     * @param exception Resolution exception.
     */    
	public static ResolvedType createUnresolvedType(ResolvedType parent, Exception exception) {
		
        // create participant which contain error
        ResolvedParticipant participant= ResolvedParticipantImpl.createUnsuccessfulResult(null, null, null, exception);		
        
		return new UnresolvedType(parent, participant );
	}

	public boolean containsChildWithPrimaryId(String id) {

		// search for the child with the requested id
		for ( ResolvedType child : children ) {
			
			// get primary
			ResolvedParticipant primary = child.getPrimaryParticipant();
		
			// compare
			if (primary.getName() == id) {				
				return true;
			}
		}
		
		return false;
	}

	public ResolvedType getChildByPrimaryId(String id) {
		
		// search for the child with the requested id
		for ( ResolvedType child : children ) {

			// get primary
			ResolvedParticipant primary = child.getPrimaryParticipant();
		
			// compare
			if (primary.getName() == id) {
				
				return child;
			}
		}
		
		return null;
	}

	public void updateSecondaryParticipant(ResolvedParticipant secondary) {
		secondaryParticipant = secondary;		
	}   	
	
}
