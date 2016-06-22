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


package com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.javautils.StringUtils;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant.ValueState;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolver;
import com.alpha.pineapple.session.Session;

/**
 * Implementation of the <code>ModelResolver</code> interface which 
 * can resolve attributes from MBeans and represent resolved
 * object by object name.
 */
public class MBeansObjectNameBasedModelResolverImpl implements ModelResolver
{

	/**
	 * Null value for unsuccessful collection value resolutions.
	 */
    static final String NULL_VALUE = null;
	
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * MBean model access object.
     */
    @Resource( name = "mbeansModelAccessor" )
    MBeansModelAccessor modelAccessor;
    
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * WebLogic JMX session.
     */
    WeblogicJMXEditSession session; 
    
    
    public void setSession(Session session) {
		this.session = (WeblogicJMXEditSession) session;				
	}

	/**
     * @see com.alpha.pineapple.resolvedmodel.traversal.ModelResolver#resolveAttribute(java.lang.String,
     *      com.alpha.pineapple.resolvedmodel.ResolvedParticipant) <p/> 
     */    
    public ResolvedParticipant resolveAttribute( String attributeName, ResolvedParticipant participant )
    {
        // declare variables for access in catch clauses
        String attributeID = null;
        MBeanAttributeInfo attributeInfo = null;
        Object attributeValue = null;
    	
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
    		Object[] args = { attributeName, participant };        	
        	logger.debug( messageProvider.getMessage("mbonbmr.resolveattribute_start", args ) );        	
        }  
                        
        try
        {        	        	        	
            // validate object name
            session.validateIsObjectName(participant.getValue());
            
            // get object name
            ObjectName objName = (ObjectName) participant.getValue();
            
            // get MBean attribute info
            attributeInfo = session.getAttributeInfo(objName, attributeName);
            
    		// validate MBean attribute was returned            
            session.validateAttributeInfoIsDefined(attributeInfo, attributeName, objName );
            
            // validate attribute is readable
            session.validateAttributeIsReadable(attributeInfo, objName );
            
    		// get attribute name
    		attributeID = attributeInfo.getName();
            
            // get attribute value
            attributeValue = session.getAttribute( objName, attributeID);
            
            // handle null object name case
            ResolvedParticipant result = null;
        	if (isNullObjectNameValue(attributeInfo, attributeValue)) {
	            result = ResolvedParticipantImpl.createSuccessfulResult( attributeID, attributeInfo, null, ValueState.NIL );         	                               
            } else {            	
	            // create successful resolution result
	            result = ResolvedParticipantImpl.createSuccessfulResult( attributeID, attributeInfo, attributeValue );            	
            }            

            // log debug message
            if ( logger.isDebugEnabled() )
            {
        		Object[] args = { result };        	
            	logger.debug( messageProvider.getMessage("mbonbmr.resolveattribute_completed", args ) );        	            	
            }

            return result;
        }
        catch ( Exception e )
        {        	
    		Object[] args = { attributeName, e };        	
        	String errorMessage = messageProvider.getMessage("mbonbmr.resolveattribute_error", args );
            ModelResolutionFailedException mre = new ModelResolutionFailedException( errorMessage, e );
        	              
            return ResolvedParticipantImpl.createUnsuccessfulResult( attributeID, attributeInfo, attributeValue, mre );
        }        
    }
    
    public String[] resolveAttributeNames( ResolvedParticipant participant ) throws ModelResolutionFailedException
    {
        // log debug message
        if ( logger.isDebugEnabled() )
        {
    		Object[] args = { StringUtils.toStringOneLine( participant ) };        	
        	logger.debug( messageProvider.getMessage("mbonbmr.resolveattributenames_start", args ) );        	
        }
        
        throw new UnsupportedOperationException("Method isn't supported.");        
    }

    public HashMap<String, ResolvedParticipant> resolveCollectionAttributeValues( ResolvedParticipant participant )
    {
        // log debug message
        if ( logger.isDebugEnabled() )
        {
    		Object[] args = { participant };        	
        	logger.debug( messageProvider.getMessage("mbonbmr.resolvecollectionvalues_start", args ) );        	
        }
        
        // create result map
        HashMap<String, ResolvedParticipant> result;
        result = new HashMap<String, ResolvedParticipant>();
        
        // get attribute value
        Object[] valueAsArray = (Object[]) participant.getValue();

        // validate type is MBeanAttributeInfo
        
        // get attribute type from collection
        MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) participant.getType();
        
        // iterate over the values
        for( Object singleValue : valueAsArray ) {
            
                // resolve attribute for array entry
                ResolvedParticipant resolvedAttribute = resolveSingleArrayAttribute( singleValue, attributeInfo );
                                                
                // add to result map
                result.put( resolvedAttribute.getName(), resolvedAttribute );                
        }
                   
        // log debug message
        if ( logger.isDebugEnabled() )
        {
    		Object[] args = { result };        	
        	logger.debug( messageProvider.getMessage("mbonbmr.resolvecollectionvalues_completed", args ) );        	
        }
        
        return result;
    }

    /**
     * Create a Resolved participant object from a single array entry.
     *  
     * @param valueObject The array entry from the the resolved participant is created.
     * @param attributeInfo MBean attribute info from the collection. 
     * 
     * @return resolved participant object created from a single array entry. If
     * no name could be obtained from the array entry then the toString() value is used. 
     */
    ResolvedParticipant resolveSingleArrayAttribute( Object valueObject, MBeanAttributeInfo attributeInfo  )
    {
        // declare variables for access in catch clauses
        String attributeName = null;;
        
        try
        {
        	// handle case where type is object name
        	if( valueObject instanceof ObjectName ) {

        		// type cast 
        		ObjectName objName = (ObjectName) valueObject;
        		
                // read name attribute from for array entry value            
                attributeName = session.getNameAttributeFromObjName(objName);
                                                
                // create resolution result with array entry value
                ResolvedParticipant resolvedAttribute = ResolvedParticipantImpl.createSuccessfulResult( attributeName, attributeInfo, objName );            
                
                return resolvedAttribute;                
                
        	} else {
        		// handle non object name case
        		
            	// create exception to describe resolution error
                Object[] args = { valueObject.toString() };    	        	
                String message = messageProvider.getMessage("mbonbmr.createnonobjectnamecollectionvalue_info", args );		            
                ModelResolutionFailedException e = new ModelResolutionFailedException(message);		            
            	
        		// create unsuccessful resolution result if value doesn't exist in collection  
                return ResolvedParticipantImpl.createUnsuccessfulResult( valueObject.toString(), attributeInfo, valueObject, e);        		
        	}
        }
        catch ( Exception e )
        {
        	// log error message
        	logger.error("DEBUG collection attribute failed with exception:" + StackTraceHelper.getStrackTrace(e));
        	        	
            // inserts an alternate name as a standard one couldn't be resolved.
            return ResolvedParticipantImpl.createUnsuccessfulResult( valueObject.toString(), attributeInfo, valueObject, e );            
        }
    }
    
    
	public ResolvedParticipant createNonExistingCollectionValue(String id, ResolvedParticipant parent) {
    	
    	// create exception to describe resolution error
        Object[] args = { id, parent.getValueAsSingleLineString() };    	        	
        String message = messageProvider.getMessage("mbonbmr.createnonexistingcollectionvalue_info", args );		            
        ModelResolutionFailedException e = new ModelResolutionFailedException(message);		            

        // get array attribute type
    	Object type = parent.getType(); 
    	
		// create unsuccessful resolution result if value doesn't exist in collection  
        return ResolvedParticipantImpl.createUnsuccessfulResult( id, type, NULL_VALUE, e);
				
	}

	
	public ResolvedType createResolvedType(ResolvedType parent, ResolvedParticipant primary, ResolvedParticipant secondary) {
		
		if(modelAccessor.isPrimitive( secondary )) {		
			return ResolvedTypeImpl.createResolvedPrimitive(parent, primary, secondary);
		}
		
		if(modelAccessor.isCollection( secondary ) ) {								
			return ResolvedTypeImpl.createResolvedCollection(parent, primary, secondary);									
		}
	
		if(modelAccessor.isEnum( secondary ) ) {
			return ResolvedTypeImpl.createResolvedEnum(parent, primary, secondary);
		}

		if(modelAccessor.isObject( secondary ) ) {
			return ResolvedTypeImpl.createResolvedObject(parent, primary, secondary);
		}
		
		return ResolvedTypeImpl.createResolvedType( parent, primary, secondary );            
	}    

	
    public void validateAttributeInfoIsDefined(MBeanAttributeInfo attributeInfo, String attributeName, ObjectName objName) throws ModelResolutionFailedException {
    	
    	// exit if attribute info is defined
    	if (attributeInfo != null) return;

		// signal failure to validate attribute info
        Object[] args = { attributeName, objName };    	        	
        String message = messageProvider.getMessage("wjes.attributeinfo_validation_failed", args );		            
        throw new ModelResolutionFailedException (message);		            
	}

    /**
     * Returns true if MBean attribute is an object name with the value null.
     * 
     * @param attributeInfo MBean attribute info.
     * @param attributeValue MBean value.
     * 
     * @return MBean attribute is an object name with the value null.
     */
	boolean isNullObjectNameValue(MBeanAttributeInfo attributeInfo, Object attributeValue) {
		return (attributeValue == null) && (session.isAttributeObjectNameReference(attributeInfo));
	}
    
}
