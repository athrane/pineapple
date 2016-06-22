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
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.resolvedmodel.ResolvedCollection;
import com.alpha.pineapple.resolvedmodel.ResolvedEnum;
import com.alpha.pineapple.resolvedmodel.ResolvedObject;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedPrimitive;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;
import com.alpha.pineapple.resolvedmodel.UnresolvedType;
import com.alpha.pineapple.session.Session;

/**
 * Implementation of the <code>ResolvedModelVisitor </code>
 * which can build a resolved model based on the resolution
 * of model entities from the primary resolver
 * 
 * All visit methods return null. This visitor doesn't return
 * any products.   
 */
public class ResolvedModelBuilderVisitorImpl implements ResolvedModelVisitor {

	/**
	 * Null value for unsuccessful collection value resolutions.
	 */
    static final String NULL_VALUE = null;

	/**
	 * Null type for unsuccessful collection value resolutions.
	 */    
	static final Object NULL_TYPE = null;

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
     * Model resolver for the XMLBeans model.
     */
    ModelResolver primaryResolver;

    /**
     * Model resolver for the MBeans model.
     */
    ModelResolver secondaryResolver;
    
    /**
     * Session.
     */
    Session session; 
    
    /**
     * XmlBeansTraversalDirectorImpl constructor.
     * 
     * @param primaryResolver Primary model resolver.
     * @param secondaryResolver Secondary model resolver.
     */
    public ResolvedModelBuilderVisitorImpl( ModelResolver primaryResolver, 
    		ModelResolver secondaryResolver )
    {
        super();
        this.primaryResolver = primaryResolver;
        this.secondaryResolver = secondaryResolver;
    }
    	
	public void setSession(Session session) {
		this.session = session; 
		
		// set session on secondary resolver
		secondaryResolver.setSession(session);		
	}
    
	public Object visit(ResolvedObject resolvedObject, ExecutionResult result) {
		
        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resolvedObject};    	        	
        	String message = messageProvider.getMessage("rmbv.visit_object", args );
        	logger.debug( message );
        }
        
		try {
				
	        // resolve attribute names for primary participant (XML Bean)
	        String[] attributeNames = primaryResolver.resolveAttributeNames( resolvedObject.getPrimaryParticipant() );
	
	        // iterate over the attribute names
	        for ( String attributeName : attributeNames )
	        {
	            // declare resolved child
	            ResolvedType resolvedChild = null;
	
	            // resolve primary attribute (XMLBean)
	            ResolvedParticipant primary = primaryResolver.resolveAttribute( attributeName, resolvedObject.getPrimaryParticipant() );
	
	            // resolve corresponding secondary attribute (MBean)
	            ResolvedParticipant secondary = secondaryResolver.resolveAttribute( attributeName, resolvedObject.getSecondaryParticiant() );
	            
				// create resolved child using the primary resolver if primary was successful	        	 
				if(primary.isResolutionSuccesful()) {
					// log development debug message
					if(logger.isDebugEnabled()) logger.debug("DEBUG // primary is succesful");						
		            resolvedChild = primaryResolver.createResolvedType(resolvedObject, primary, secondary);
		            
				} else if(secondary.isResolutionSuccesful()) {					
				    // create resolved child using the secondary resolver if primary wasn't successful and secondary was successful					
					// log development debug message
					if(logger.isDebugEnabled()) logger.debug("DEBUG // secondary is succesful");											
					resolvedChild = secondaryResolver.createResolvedType(resolvedObject, primary, secondary); 		    	
					
				} else {
					// log development debug message
					if(logger.isDebugEnabled()) logger.debug("DEBUG // handling unsuccessful resolutions");	        			        		        		
					
			    	// create exception to describe resolution error
			        Object[] args = { primary.getResolutionErrorAsSingleLineString(), secondary.getResolutionErrorAsSingleLineString() };    	        	
			        String message = messageProvider.getMessage("rmbv.resolvedobject_failed", args );		            
			        ModelResolutionFailedException e = new ModelResolutionFailedException(message);		            	        		
			        resolvedChild = ResolvedTypeImpl.createUnresolvedType(resolvedObject, e);	        							
				}
												
	            // log debug message
	            if ( logger.isDebugEnabled() )
	            {
	            	Object[] args = { resolvedChild};    	        	
	            	String message = messageProvider.getMessage("rmbv.created_resolvedobject", args );
	            	logger.debug( message );
	            }
	            
	            // add child to parent
	            resolvedObject.addChild(resolvedChild);	            
	        }	
	        
            // return null
            return null;
                        
		} catch (Exception e) {

            // terminate resolution of object with error
        	Object[] args = { e.toString() };
            result.completeAsError(messageProvider, "rmbv.resolvedobject_error", args, e);
			            
            // return null
            return null;
		}        
	}

	public Object visit(ResolvedEnum resolvedEnum, ExecutionResult result) {
		
        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resolvedEnum};    	        	
        	String message = messageProvider.getMessage("rmbv.visit_enum", args );
        	logger.debug( message );
        }
        
        // return null
        return null;
	}
	
	public Object visit(ResolvedCollection resolvedCollection, ExecutionResult result) {

        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resolvedCollection};    	        	
        	String message = messageProvider.getMessage("rmbv.visit_collection", args );
        	logger.debug( message );
        }
        
        try {
        	        	
	        // resolve collection into separate resolved attributes (XMLBean)
	        HashMap<String, ResolvedParticipant> primaryCollectionValues;
	        primaryCollectionValues = primaryResolver.resolveCollectionAttributeValues( resolvedCollection.getPrimaryParticipant() );

        	logger.debug("DEBUG // primary collection values :" +  primaryCollectionValues );	            
	        	        
	        // resolve collection into separate resolved attributes (MBean)
	        HashMap<String, ResolvedParticipant> secondaryCollectionValues;
	        secondaryCollectionValues = secondaryResolver.resolveCollectionAttributeValues( resolvedCollection.getSecondaryParticiant() );
	
        	logger.debug("DEBUG // secondary collection values :" +  secondaryCollectionValues );	            
	        
	        // create combined key set from values
	        Set<String> combinedKeySet = new HashSet<String>();
	        combinedKeySet.addAll(primaryCollectionValues.keySet());
	        combinedKeySet.addAll(secondaryCollectionValues.keySet());
	        	        
	        // sort the set
	        //List<String> sortedKeys = new ArrayList<String>(combinedKeySet);
	        //Collections.sort(sortedKeys);
	        
	        // iterate over the combined key set
	        for ( String id : combinedKeySet ) {

	        	// resolve collection entry
	        	ResolvedType resolvedChild = resolveCollectionEntry(resolvedCollection, primaryCollectionValues, secondaryCollectionValues, id);
	        	
	    		// log debug message
	    		if ( logger.isDebugEnabled() )
	    		{
	    			Object[] args = { resolvedChild};    	        	
	    			String message = messageProvider.getMessage("rmbv.created_resolvedobject", args );
	    			logger.debug( message );
	    		}
	    		
	    		// add child to parent
	    		resolvedCollection.addChild(resolvedChild);	        	
	        }
	        
            // return null
            return null;
	        
        } catch (Exception e) {
			
        	logger.debug("DEBUG // collection value resolution error :" +  e );	            
            
            // terminate resolution of collection values with error
        	Object[] args = { e.toString() };
            result.completeAsError(messageProvider, "rmbv.resolvedcollection_error", args, e);
        	            
            // return null
            return null;
		}        
	}


	public Object visit(ResolvedPrimitive resolvedPrimitive, ExecutionResult result) {

        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resolvedPrimitive};    	        	
        	String message = messageProvider.getMessage("rmbv.visit_primitive", args );
        	logger.debug( message );
        }
                
        // return null
        return null;
	}	
	
	public Object visit(UnresolvedType unresolved, ExecutionResult result) {

        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { unresolved};    	        	
        	String message = messageProvider.getMessage("rmbv.visit_unresolved", args );
        	logger.debug( message );
        }
        
        // return null
        return null;
	}
		
	public Object visit(ResolvedType resolvedType, ExecutionResult result) {

        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { resolvedType };    	        	
        	String message = messageProvider.getMessage("rmbv.visit_resolved", args );
        	logger.debug( message );
        }
                
        // return null
        return null;
	}
	

	/**
	 * Resolve collection entry defined by a key in the combined key set.
	 * As the key taken from the combined set there is no guarantee that 
	 * key can be resolved from either the primary or secondary resolver.   
	 *   
	 * @param resolvedCollection Collection of resolved collection entries where
	 * the resolved entry is added to.   
	 * @param primaryCollectionValues Map of resolved primary values.
	 * @param secondaryCollectionValues Map of resolved secondary values.
	 * @param id Key which identifies the entry.
	 */
	ResolvedType resolveCollectionEntry(ResolvedCollection resolvedCollection,
			HashMap<String, ResolvedParticipant> primaryCollectionValues,
			HashMap<String, ResolvedParticipant> secondaryCollectionValues,
			String id) {
		
		logger.debug("DEBUG // processing id: " +  id );	        	
		
		// get resolved attribute from primary values (XMLBean)
		ResolvedParticipant primary = getPrimaryCollectionValue( id, primaryCollectionValues, resolvedCollection );
		logger.debug("DEBUG // resolved primary participant: " +  primary );
			            
		// get resolved attribute from secondary values (MBean)
		ResolvedParticipant secondary = getSecondaryCollectionValue( id, secondaryCollectionValues, resolvedCollection );
		logger.debug("DEBUG // resolved secondary participant: " +  secondary);
		
		try {
			
			// create resolved child using the primary resolver if primary was successful	        	 
			if(primary.isResolutionSuccesful()) {
				logger.debug("DEBUG // primary is succesful");
				return primaryResolver.createResolvedType(resolvedCollection, primary, secondary);        			        			        					
			} 

		    // create resolved child using the secondary resolver if primary wasn't successful and secondary was successful
			if(secondary.isResolutionSuccesful()) {
		    	logger.debug("DEBUG // secondary is succesful");
				return secondaryResolver.createResolvedType(resolvedCollection, primary, secondary); 		    	
			} 

	    	logger.debug("DEBUG // handling unsuccessful resolutions");	        			        		        		
			
	    	// create exception to describe resolution error
	        Object[] args = { id, primaryCollectionValues.keySet(), secondaryCollectionValues.keySet() };    	        	
	        String message = messageProvider.getMessage("rmbv.resolvedcollection_failed2", args );		            
	        ModelResolutionFailedException e = new ModelResolutionFailedException(message);		            	        		
			return ResolvedTypeImpl.createUnresolvedType(resolvedCollection, e);	        		
						
		} catch (Exception e) {

	    	// create exception to describe resolution error
	        Object[] args = { id, e.toString() };    	        	
	        String message = messageProvider.getMessage("rmbv.resolvedcollection_error", args );		            
	        ModelResolutionFailedException mrfe = new ModelResolutionFailedException(message, e);		            	        		
			return ResolvedTypeImpl.createUnresolvedType(resolvedCollection, mrfe);	        					
		}
	}

	/**
	 * get resolved value from primary collection.  
	 * 
	 * @param id Id to identify the value in the collection.
	 * @param collectionValues Map of collection values identified by an unique id.
	 *  
	 * @return resolved value from the primary collection. If the value couldn't be found 
	 * in the collection then a unsuccessful resolution result is returned. 
	 */
	ResolvedParticipant getPrimaryCollectionValue(String id, HashMap<String, ResolvedParticipant> collectionValues, ResolvedCollection resolvedCollection ) {

    	// return collection value if defined
    	if (collectionValues.containsKey(id)) {    		
    		return collectionValues.get( id );    		
    	}

    	// get primary participant
    	ResolvedParticipant primary = resolvedCollection.getPrimaryParticipant();
    	
    	// create non existing collection value to insert into resolved participant
    	return primaryResolver.createNonExistingCollectionValue(id, primary );    	
	}		

	/**
	 * get resolved value from secondary collection.  
	 * 
	 * @param id Id to identify the value in the collection.
	 * @param collectionValues Map of collection values identified by an unique id.
	 *  
	 * @return resolved value from the secondary collection. If the value couldn't be found 
	 * in the collection then a unsuccessful resolution result is returned. 
	 */
	ResolvedParticipant getSecondaryCollectionValue(String id, HashMap<String, ResolvedParticipant> collectionValues, ResolvedCollection resolvedCollection ) {

    	// return collection value if defined
    	if (collectionValues.containsKey(id)) {    		
    		return collectionValues.get( id );    		
    	}

    	// get secondary participant
    	ResolvedParticipant secondary = resolvedCollection.getSecondaryParticiant();
  	  	 
    	// create non existing collection value to insert into resolved participant
    	return secondaryResolver.createNonExistingCollectionValue(id, secondary );    	
	}		
	
}
