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


package com.alpha.pineapple.plugin.weblogic.jmx.model.xmlbeans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

import com.alpha.javautils.StringUtils;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.reflection.GetterMethodMatcher;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant.ValueState;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolver;
import com.alpha.pineapple.session.Session;

/**
 * Implementation of the <code>ModelResolver</code> interface which can resolve attributes from an XMLBean.
 */
public class XmlBeansModelResolverImpl implements ModelResolver
{
	/**
	 * Null value for unsuccessful collection value resolutions.
	 */
    static final String NULL_VALUE = null;
	
    /**
     * Internal class used to transport multiple value during value resolution.
     */
    class ValueTransport
    {
    	/**
    	 * Resolved value.
    	 */
        Object value;

        /**
         * State of the resolved value.
         */
        ResolvedParticipant.ValueState state;
        
        /**
         * Contained exception if value resolution failed with an error.
         */
        ModelResolutionFailedException exception;
    }

    /**
     * Package name for generated XMLbeans from WebLogic schemas.
     */
    static final String MODEL_PACKAGE = "com.bea.ns.weblogic";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * XMLBean model access object.
     */
    @Resource( name = "xmlBeansModelAccessor" )
    XmlBeansModelAccessor modelAccessor;

    /**
     * XMLBean method matcher object
     */
    @Resource( name = "xmlBeansGetterMethodMatcher" )
    GetterMethodMatcher matcher;
    
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    
    public void setSession(Session session) {
    	// ignore session for now		
	}


	/**
     * @see com.alpha.pineapple.resolvedmodel.traversal.ModelResolver#resolveAttribute(java.lang.String, com.alpha.pineapple.resolvedmodel.ResolvedParticipant)
     *
     * <p> 
     * Returns the named attribute on an XMBean, by invoking the corresponding 'getXX()' on
     * the XMLBean for a named attribute 'XX'.
     * </p> 
     */
    public ResolvedParticipant resolveAttribute( String attributeName, ResolvedParticipant participant )
    {
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
    		Object[] args = { attributeName, StringUtils.toStringOneLine( participant ) };        	
        	logger.debug( messageProvider.getMessage("xbmr.resolveattribute_start", args ) );        	
        }        
        
        // exit if stored type isn't schema property  
        if (!(participant.getType() instanceof SchemaProperty )) {
        	
        	// if name is null, then generated null name
        	if (attributeName == null) {
        		attributeName ="null";
        	}
        	
            // create exception to describe the situation
    		Object[] args = { attributeName, participant.getType() };        	
        	String errorMessage = messageProvider.getMessage("xbmr.resolveattribute_type_failed", args );        	
            ModelResolutionFailedException e = new ModelResolutionFailedException( errorMessage );
            
            // create resolution result
            return ResolvedParticipantImpl.createUnsuccessfulResult( attributeName, null, null, e );        	
        }
        
        // get participant type
        SchemaProperty particantSchemaProperty = (SchemaProperty) participant.getType();

        // resolve attribute type
        SchemaProperty attributeType = modelAccessor.getSchemaPropertyByName(attributeName, particantSchemaProperty );                 

        // if no type where returned then return unsuccessful result
        if ( attributeType == null )
        {            	
            // create exception to describe the situation
    		Object[] args = { attributeName, StringUtils.toStringOneLine( participant.getValue() ) };        	
        	String errorMessage = messageProvider.getMessage("xbmr.resolveattribute_type_failed2", args );        	
            ModelResolutionFailedException e = new ModelResolutionFailedException( errorMessage );

            // create resolution result
            return ResolvedParticipantImpl.createUnsuccessfulResult( attributeName, null, null, e );
        }

        // exit if model object isn't XMLBean 
        if (!(participant.getValue() instanceof XmlObject)) {
        	
        	// if name is null, then generated null name
        	if (attributeName == null) {
        		attributeName ="null";
        	}
        	
            // create exception to describe the situation
    		Object[] args = { attributeName, participant.getValue()};        	
        	String errorMessage = messageProvider.getMessage("xbmr.resolveattribute_value_failed", args );        	
            ModelResolutionFailedException e = new ModelResolutionFailedException( errorMessage );

            // create resolution result
            return ResolvedParticipantImpl.createUnsuccessfulResult( attributeName, attributeType, null, e );        	
        }

        // type cast to XMLBean
        XmlObject xmlBean = (XmlObject) participant.getValue();
        
        
        // resolve getter method from attribute name
        Method getterMethod = modelAccessor.resolveGetterMethod( attributeName, xmlBean );

        // if no getter where found then return unsuccessful result
        if ( getterMethod == null )
        {            	
            // create exception to describe the situation
    		Object[] args = { attributeName, StringUtils.toStringOneLine( xmlBean) };        	
        	String errorMessage = messageProvider.getMessage("xbmr.resolveattribute_getter_failed", args );        	
            ModelResolutionFailedException e = new ModelResolutionFailedException( errorMessage );

            // create resolution result
            return ResolvedParticipantImpl.createUnsuccessfulResult( attributeName, null, null, e );
        }
                        
        // resolve attribute value
        ValueTransport transport = resolveAttributeValue( attributeName, attributeType, xmlBean, getterMethod );

        // create resolution result
        ResolvedParticipant result;
		result = ResolvedParticipantImpl.createSuccessfulResult( attributeName, attributeType, transport.value, transport.state );

        // if method is encrypted then process it
        if ( modelAccessor.isMethodEncrypted( getterMethod ) )
        {
            // create exception to describe the situation
            StringBuilder message = new StringBuilder();
            message.append( "Failed to resolve XMLBean attribute <" );
            message.append( attributeName );
            message.append( "> as encrypted method isn't supported/implemented yet for XMLBean <" );
            message.append( StringUtils.toStringOneLine( xmlBean ) );
            message.append( ">." );
            ModelResolutionFailedException e = new ModelResolutionFailedException( message.toString() );

            return ResolvedParticipantImpl.createUnsuccessfulResult( attributeName, attributeType, null, e );
        }

        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
    		Object[] args = { result };        	
        	logger.debug( messageProvider.getMessage("xbmr.resolveattribute_completed", args ) );        	
        }
        
        return result;    	
    }

            
    public String[] resolveAttributeNames( ResolvedParticipant attribute ) throws ModelResolutionFailedException
    {

        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
    		Object[] args = { StringUtils.toStringOneLine( attribute ) };        	
        	logger.debug( messageProvider.getMessage("xbmr.resolveattributenames_start", args ) );        	
        }
    	
        // create result list
        ArrayList<String> result = new ArrayList<String>();

        // fail if contained isn't a XMLBeans schema property
        if (!(attribute.getType() instanceof SchemaProperty )) {
    
            // create exception to describe the situation
    		Object[] args = { StringUtils.toStringOneLine( attribute ) };        	
        	String errorMessage = messageProvider.getMessage("xbmr.resolveattributenames_failed", args );        	
            ModelResolutionFailedException e = new ModelResolutionFailedException( errorMessage );
            
            // throw exception
            throw e;
        }
        
        // get schema property
        SchemaProperty schemaProperty = (SchemaProperty) attribute.getType();
        
        // get schema properties
        SchemaType schemaType = schemaProperty.getType();
        SchemaProperty[] schemaProperties = schemaType.getProperties();

        for ( SchemaProperty property : schemaProperties )
        {
            // get java name
            String name = property.getJavaPropertyName();

            // add to result
            result.add( name );
        }
        
        // sort by name
        Collections.sort(result);
        
        // convert to array
        String[] resultArray = result.toArray( new String[result.size()] );
        
        // log debug message
        if ( logger.isDebugEnabled() )
        {
    		Object[] args = { resultArray.length, ReflectionToStringBuilder.toString( resultArray ), StringUtils.toStringOneLine( attribute ) };        	
        	logger.debug( messageProvider.getMessage("xbmr.resolveattributenames_completed", args ) );        	            
        }

        // return as array
        return resultArray;
    }

    public HashMap<String, ResolvedParticipant> resolveCollectionAttributeValues( ResolvedParticipant attribute ) throws ModelResolutionFailedException
    {
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
    		Object[] args = { attribute };        	
        	logger.debug( messageProvider.getMessage("xbmr.resolvecollectionvalues_start", args ) );        	
        }
    	                        
        // throw exception if type isn't schema property
        if (!(attribute.getType() instanceof SchemaProperty)) {

            // create exception to unexpected type
    		Object[] args = { attribute };        	
        	String errorMessage = messageProvider.getMessage("xbmr.resolvecollectionvalues_failed", args );        	
            throw new ModelResolutionFailedException( errorMessage );        	
        }

        // get collection type                
        SchemaProperty arrayType = (SchemaProperty) attribute.getType();        
        
        // create result map
        HashMap<String, ResolvedParticipant> result;
        result = new HashMap<String, ResolvedParticipant>();

        // exit if value state is nil
        if (attribute.getValueState() == ValueState.NIL ) 
        {
        	return result;
        }

        // exit if value state is nil
        if (attribute.getValueState() == ValueState.FAILED ) 
        {
        	return result;
        }
        
        // get attribute value        
        Object[] valueAsArray = (Object[]) attribute.getValue();
                                
        // iterate over the values
        for ( Object singleValue : valueAsArray )
        {
            // resolve attribute for array entry
            ResolvedParticipant resolvedAttribute = resolveSingleCollectionAttribute( singleValue, arrayType );
            
            // add to result map
            result.put( resolvedAttribute.getName(), resolvedAttribute );
        }

        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
    		Object[] args = { result };        	
        	logger.debug( messageProvider.getMessage("xbmr.resolvecollectionvalues_completed", args ) );        	
        }
        
        return result;
    }

            
    public ResolvedType createResolvedType(ResolvedType parent, ResolvedParticipant primary, ResolvedParticipant secondary) {
    	
		try {
			
			if(modelAccessor.isPrimitive( primary ) ) {		
				return ResolvedTypeImpl.createResolvedPrimitive(parent, primary, secondary);
			}
			
			if(modelAccessor.isCollection( parent.getPrimaryParticipant(), primary ) ) {								
				return ResolvedTypeImpl.createResolvedCollection(parent, primary, secondary);									
			}
		
			if(modelAccessor.isEnum( primary ) ) {
				return ResolvedTypeImpl.createResolvedEnum(parent, primary, secondary);
			}
	
			if(modelAccessor.isObject( parent.getPrimaryParticipant(), primary ) ) {
				return ResolvedTypeImpl.createResolvedObject(parent, primary, secondary);
			}
			
			return ResolvedTypeImpl.createResolvedType( parent, primary, secondary );            
		
		} catch (ModelResolutionFailedException e) {
			
            return ResolvedTypeImpl.createUnresolvedType(parent, e);
		}        		    	
	}

	/**
     * Create a resolved participant object from a single collection entry.
     * 
     * @param valueObject The collection entry from which the resolved participant is created.
     * @param attributeType Schema property which contains type information for attribute. 
     * 
     * @return resolved participant object created from a single collection entry. 
     * If no name could be obtained from the collection entry then the toString() value is used.
     */
    ResolvedParticipant resolveSingleCollectionAttribute( Object valueObject, SchemaProperty attributeType )
    {
        // declare variable for access in catch clauses
        String attributeName = null;

        try
        {
            // create name for array entry value
            attributeName = modelAccessor.getNameAttributeFromModelObject( valueObject );

            // create resolution result with array entry value
            ResolvedParticipant resolvedAttribute;
            resolvedAttribute = ResolvedParticipantImpl.createSuccessfulResult( attributeName, attributeType, valueObject );

            return resolvedAttribute;
        }
        catch ( Exception e )
        {
            // inserts an alternate name as a standard one couldn't be resolved.
            String ValueAsString = valueObject.toString();
            return ResolvedParticipantImpl.createUnsuccessfulResult( ValueAsString, attributeType, valueObject, e );
        }
    }
        
    public ResolvedParticipant createNonExistingCollectionValue(String id, ResolvedParticipant parent) {
    	
    	// create exception to describe resolution error
        Object[] args = { id, parent.getValueAsSingleLineString() };    	        	
        String message = messageProvider.getMessage("xbmr.createnonexistingcollectionvalue_info", args );		            
        ModelResolutionFailedException e = new ModelResolutionFailedException(message);		            

        // reuse type from parent resolved collection. 
    	Object type = parent.getType();
    	
		// create unsuccessful resolution result if value doesn't exist in collection        
		return ResolvedParticipantImpl.createUnsuccessfulResult( id , type, NULL_VALUE , e );		
	}


	/**
     * Resolve the attribute value for an XMLBean based on the attribute characteristics.
     * 
     * @param attributeName Name of the attribute.
     * @param attributeType Type of the attribute.
     * @param xmlBean XMLBean which contains attribute.
     * @param getterMethod Getter method to get value with.
     * 
     * @return ValueTransport
     */
    ValueTransport resolveAttributeValue( String attributeName, SchemaProperty attributeType, XmlObject xmlBean, Method getterMethod ) 
    {
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
    		Object[] args = { attributeName, StringUtils.toStringOneLine( xmlBean ) };        	
        	logger.debug( messageProvider.getMessage("xbmr.resolvevalue_start", args ) );        	
        }
 
        // create transport object
        ValueTransport transport = new ValueTransport();

        try {
        	
	        // if value is set, get and return it
	        if ( modelAccessor.isValueSet( attributeType, xmlBean ) )
	        {
	            // set value and state
	            transport.value = modelAccessor.getModelObject( xmlBean, getterMethod );
	            transport.state = ValueState.SET;                      
	            
	            // log debug message
	            if ( logger.isDebugEnabled() )
	            {
	        		Object[] args = { transport.value, attributeName };        	
	            	logger.debug( messageProvider.getMessage("xbmr.resolvevalue_set_succeed", args ) );        	            	
	            }
	            return transport;
	        }
	
	        // if default value if defined, get it and return it
	        if ( modelAccessor.isDefaultValueDefined( attributeType, xmlBean ) )
	        {
	            // get default value
	            transport.value = modelAccessor.getDefaultValue( attributeType, xmlBean );
	            transport.state = ValueState.DEFAULT;
	
	            // log debug message
	            if ( logger.isDebugEnabled() )
	            {
	        		Object[] args = { transport.value, attributeName };        	
	            	logger.debug( messageProvider.getMessage("xbmr.resolvevalue_default_succeed", args ) );        	            	
	            }	            
	            return transport;
	        }
	
	        // is values is nil, then return it
	        if ( modelAccessor.isNil( attributeType, xmlBean ) )
	        {
	            // set null value
	            transport.value = null;
	            transport.state = ValueState.NIL;
	
	            // log debug message
	            if ( logger.isDebugEnabled() )
	            {
	        		Object[] args = { transport.value, attributeName };        	
	            	logger.debug( messageProvider.getMessage("xbmr.resolvevalue_nil_succeed", args ) );        	            	
	            }
	            return transport;
	        }
	
	        // set failed resolution value and state
	        transport.value = null;
	        transport.state = ValueState.FAILED;
	
	        // log debug message
	        if ( logger.isDebugEnabled() )
	        {
	    		Object[] args = { transport.value, attributeName };        	
	        	logger.debug( messageProvider.getMessage("xbmr.resolvevalue_failed", args ) );        	            	
	        }
	        return transport;
        
        } catch (ModelResolutionFailedException e) {

	        // set null value and state
	        transport.value = null;
	        transport.state = ValueState.FAILED;
	        transport.exception = e;
	        
	        // log debug message
	        if ( logger.isDebugEnabled() )
	        {
	    		Object[] args = { attributeName, e };        	
	        	logger.debug( messageProvider.getMessage("xbmr.resolvevalue_error", args ) );        	            	
	        }
	        return transport;	        
        }        
    }


        
}
