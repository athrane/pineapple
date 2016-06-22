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

import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.OBJECTNAME_ARRAY_TYPE;
import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.OBJECTNAME_TYPE;
import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY;
import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY;

import javax.annotation.Resource;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.resolvedmodel.ResolvedCollection;
import com.alpha.pineapple.resolvedmodel.ResolvedEnum;
import com.alpha.pineapple.resolvedmodel.ResolvedObject;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedPrimitive;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.UnresolvedType;
import com.alpha.pineapple.resolvedmodel.traversal.ResolvedModelVisitor;
import com.alpha.pineapple.session.Session;

/**
 * Implementation of the <code>ResolvedModelVisitor </code>
 * which can produce a string based description of the visited node.  
 */
public class MBeansDescriptionGeneratorImpl implements ResolvedModelVisitor {

	/**
	 * Collection text signifier. 
	 */
	static final String COLLECTION_NOTATION = "[..]";

	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	        
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
    
	public void setSession(Session session) {
		// ignore session for now
	}
    
	public Object visit(ResolvedCollection resolvedCollection, ExecutionResult result) {
		return createCollectionDescription(resolvedCollection);		
	}

	public Object visit(ResolvedEnum resolvedEnum, ExecutionResult result) {
		return createSimpleDescription(resolvedEnum);		
	}

	public Object visit(ResolvedObject resolvedObject, ExecutionResult result) {
		return createDescription(resolvedObject);
	}

	public Object visit(ResolvedPrimitive resolvedPrimitive, ExecutionResult result ) {
		return createSimpleDescription(resolvedPrimitive);		
	}

	public Object visit(ResolvedType resolvedType, ExecutionResult result) {
		return createDescription(resolvedType);
	}

	public Object visit(UnresolvedType unresolved, ExecutionResult result) {
		return createDescription(unresolved);	
	}
	
    /**
     * Create description of the primary participant in the resolved type.
     *  
     * @param resolvedType Resolved type to describe.
     * 
     * @return description of the primary participant in the resolved type.
     */
	String createDescription(ResolvedType resolvedType) {
				
        // get secondary type
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();
        Object type = secondary.getType();
        
        // type is null
        if( type == null ) {
            return secondary.getValueAsSingleLineString();        	
        }
        
        // type is Object Name        
        // case: The Domain MBean whose type can't be represented as a attribute info.
        if (type.equals(ObjectName.class)) {
        	return createDescriptionForObjName(secondary);        	
        }
        
        // type is MBeanAttributeInfo
        if (type instanceof MBeanAttributeInfo) {
        	
        	// type cast
        	MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) type; 
        	
        	// handle object name type
        	// case: Attributes whose value an MBean.
        	// example: the security configuration located at Domain.SecurityConfiguration.          	
        	if(attributeInfo.getType().equals(OBJECTNAME_TYPE)) {
        		return createDescriptionForMBeanAttributeWithObjName(secondary);
        	}

        	// handle object name array type
        	// case: collection content which inherits the MBeanAttributeInfo from the collection
        	// example: an individual server in the Servers collection.         	
        	if(attributeInfo.getType().equals(OBJECTNAME_ARRAY_TYPE)) {        		
        		return createCollectionDescription(resolvedType);
        	}
        	
        }
        return createSimpleDescription(resolvedType);
	}


	/**
	 * Create simple description from name and value.
	 * 
	 * @param resolvedType resolved type.
	 * 
	 * @return simple description from name and value.
	 */
	String createSimpleDescription(ResolvedType resolvedType) {
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();
        StringBuilder description = new StringBuilder()
        	.append( secondary.getName())
        	.append( "=" )            
        	.append( secondary.getValue() );
		return description.toString();
	}

	/**
	 * Create description for a collection.
	 * 
	 * @param resolvedType 
	 * @return
	 */
	String createCollectionDescription(ResolvedType resolvedType) {
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();
        StringBuilder description = new StringBuilder()
			.append( secondary.getName() )
			.append( COLLECTION_NOTATION );		                
		return description.toString();
	}
	
	/**
	 * Create description for participant where the type is stored as MBeanAttributeInfo
	 * and the type in the info object is ObjectName.
	 * 
	 * This will be the case for attributes whose value an MBean. 
	 * Example: The security configuration located at Domain.SecurityConfiguration.  
	 * 
	 * @param participant Secondary participant.
	 */	
	String createDescriptionForMBeanAttributeWithObjName(ResolvedParticipant participant) {

		// get value as object name
		ObjectName objName = (ObjectName) participant.getValue();			

		// log error if type is undefined and fall back to value-as-string
		if( objName == null ) {
			
			// log error								
            Object[] args = { objName };    	        	
            String message = messageProvider.getMessage("mbdg.objname_failed", args );
            logger.error( message );            				

        	// type cast            
        	MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) participant.getType(); 
    		
    		// get name and type from MBean attribute info
    		String nameProperty = attributeInfo.getName();    	

    		StringBuilder description = new StringBuilder();
    		description.append( "null:");
    		description.append( nameProperty);		        		        		
    		return description.toString();
		}
		
		// get name and type from Object Name properties
		String nameProperty = objName.getKeyProperty(OBJNAME_NAME_KEYPROPERTY);
		String typeProperty = objName.getKeyProperty(OBJNAME_TYPE_KEYPROPERTY);

		if (nameProperty == null) {
            logger.error( "ERROR, FIX THIS: name property on object name is null:" + objName );            				
		}
		if (typeProperty == null) {
            logger.error( "ERROR, FIX THIS, type property on object name is null:" + objName );            				
		}
				
		StringBuilder description = new StringBuilder();
		description.append( nameProperty);		
		description.append( ":");
		description.append( typeProperty);        		        		
		return description.toString();
	}
			

	/**
	 * Create description for participant where the type is stored as ObjectName 
	 * which will be the case for the root, e.g. the Domain.
	 * 
	 * @param participant Secondary participant. 
	 */
	String createDescriptionForObjName(ResolvedParticipant participant) {
			
		// get value as object name
		ObjectName objName = (ObjectName) participant.getValue();			

		// log error if type is undefined and fall back to value-as-string
		if( objName == null ) {
			
			// log error								
            Object[] args = { objName };    	        	
            String message = messageProvider.getMessage("mbdg.objname_failed", args );
            logger.error( message );            				
			
			// fall back and return value as string
			return participant.getValueAsSingleLineString();
		}
				
		// get type as Object Name property
		String typeProperty = objName.getKeyProperty(OBJNAME_TYPE_KEYPROPERTY);
				
		// log error if type is undefined and fall back to value-as-string
		if( typeProperty == null ) {
			
			// log error								
            Object[] args = { objName };    	        	
            String message = messageProvider.getMessage("mbdg.objname_failed", args );
            logger.error( message );            				
			
			// fall back and return value as string
			return participant.getValueAsSingleLineString();
		}

		StringBuilder description = new StringBuilder();
		description.append( participant.getName());
		description.append( ":");
		description.append( typeProperty);        		        		
		return description.toString();
	}

	/**
	 * Create description from value.
	 * 
	 * @param resolvedAttribute Resolve attribute whose type is described.
	 * 
	 * @return Description of resolved attribute.  
	 */
	String describeTypeFromValue( ResolvedParticipant participant ) {
				
		// get value
		Object value = participant.getValue();
		
		if (value == null) {
			return "n/a";
		}

		return value.getClass().getSimpleName();		
	}	
	
}
