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

import javax.annotation.Resource;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.metadata.MetadataRepository;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;
import com.alpha.pineapple.resolvedmodel.validation.ModelValidationFailedException;

/**
 * Helper class for accessing model objects represented as WebLogic MBeans.
 */
public class MBeansModelAccessorImpl implements MBeansModelAccessor
{		
    /**
     * First array index.
     */
    static final int FIRST_INDEX = 0;

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
            
    /**
     * MBean meta data repository.
     */
    @Resource
    MetadataRepository mbeanMetadataRepository;        
    
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;        
                        	
	public boolean isMBeanType(ObjectName objName, String type) {

		String typeProperty = objName.getKeyProperty("Type");
		
		if (typeProperty == null) return false;		
		return (typeProperty.equals(type));
	}

	
	public boolean isPrimitive(ResolvedParticipant secondary) {
		
		// TODO: implement logic to handle all of the bad cases.
		
		// get type
		Object type = secondary.getType();
		
		// handle null case
		if (type == null ) return false;
		
		// cast as class to process it
		if (type instanceof Class ) {
			
			// cast to class
			Class<?> typeClass = (Class<?>) type;
			
			if (typeClass.isPrimitive()) return true;
			
			if (typeClass.equals(String.class)) return true;
			
			return false;
		}
		
		return false;
	}

	
	public boolean isCollection(ResolvedParticipant secondary) {

		// TODO: implement logic to handle all of the bad cases.
		
		// get type
		Object type = secondary.getType();
		
		// handle null case
		if (type == null ) return false;
		
		// cast as class to process it
		if (type instanceof Class ) {
			
			// cast to class
			Class<?> typeClass = (Class<?>) type;
			
			return (typeClass.isArray());
		}
		
		return false;
	}

	
	public boolean isEnum(ResolvedParticipant secondary) {
		return false;
	}

	
	public boolean isObject(ResolvedParticipant secondary) {
		
		// TODO: implement logic to handle all of the bad cases.
		
		// get type
		Object type = secondary.getType();
		
		// handle null case
		if (type == null ) return false;
		
		// cast as class to process it
		if (type instanceof Class ) {
			
			// cast to class
			Class<?> typeClass = (Class<?>) type;
			
			// assumption is that MBean dynamic proxys implements interfaces
			Class[] interfaces = typeClass.getInterfaces();

			// fail if the object doesn't implement any interfaces
			return (interfaces.length > 0 );
		}
		
		return false;
	}	
    	        
    public void validateAttributeInfoIsDefined(MBeanAttributeInfo attributeInfo, String attributeName, ObjectName objName) throws ModelResolutionFailedException {
    	
    	// exit if attribute info is defined
    	if (attributeInfo != null) return;

		// signal failure to validate attribute info
        Object[] args = { attributeName, objName };    	        	
        String message = messageProvider.getMessage("mbma.attributeinfo_validation_failed", args );		            
        throw new ModelResolutionFailedException (message);		            
	}
    
    
	public void validateAttributeIsReadable(MBeanAttributeInfo attributeInfo, String attributeName, ObjectName objName) throws ModelResolutionFailedException {

		// exit if attribute is readable
        if ( attributeInfo.isReadable() ) return;

		// signal failure to validate attribute info
        Object[] args = { attributeName, objName };    	        	
        String message = messageProvider.getMessage("mbma.attribute_readable_validation_failed", args );		            
        throw new ModelResolutionFailedException (message);		            		
	}


	public void validateObjectIsObjectName(Object value) throws ModelValidationFailedException {

    	// exit if successful value can be type cast
    	if (ObjectName.class.isInstance(value)) return;
    	
		// signal failure to validate type cast
    	Object[] args = { value };    	        	    	
    	String message = messageProvider.getMessage("mbma.objectname_validation_failed", args );
        throw new ModelValidationFailedException( message );		    	    	
				
	}
	
}
