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
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.resolvedmodel.ResolvedCollection;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedType;


/**
 * Implementation of the {@link ResolvedModelJmxGetter} interface.
 */
public class ResolvedModelJmxGetterImpl implements ResolvedModelJmxGetter {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;        
	                
	public String getSecondaryAttributeID(ResolvedType resolvedType) {    	
    	ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();    	
    	return secondary.getName();
	}
        
    public Object getSecondaryAttributeType(ResolvedType resolvedType) {
    	ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();    	
    	return secondary.getType();
	}

	public Object getPrimaryAttributeValue(ResolvedType resolvedType) {    	
    	ResolvedParticipant primary = resolvedType.getPrimaryParticipant();    	
    	Object value = primary.getValue();
		return value;
	}
    
	public String getPrimaryAttributeTypeAsString(ResolvedType resolvedType) {
    	ResolvedParticipant primary = resolvedType.getPrimaryParticipant();    	
		
		// TODO Auto-generated method stub
		return null;
	}

	
	public ObjectName getParentObjectName(ResolvedType resolvedType) {
		
		// get parent
		ResolvedType parent = resolvedType.getParent();		
		
		// resolved parent object name
		return resolveObjectName(parent);
	}
    
    public ObjectName resolveObjectName(ResolvedType resolvedType) {
		
		if(logger.isDebugEnabled()) logger.debug("DEBUG resolvedType=" + resolvedType);    	
    	
		// get secondary participant
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();            
				
		// get secondary value 
		Object secondaryValue = secondary.getValue();		
		
		// define object name
		ObjectName objectName = null; 
		
		if(logger.isDebugEnabled()) logger.debug("DEBUG secondaryValue=" + secondaryValue );
		
		// if value is object name the get it
		if (secondaryValue instanceof ObjectName ) {
			
			if(logger.isDebugEnabled()) {
				Object[] args = { secondaryValue };
				logger.debug(messageProvider.getMessage( "rmjg.resolveobjectname_objname_info", args ));				
			}
			
			// store object name
			return (ObjectName) secondaryValue;
		}
		
		// log failed resolution
		if(logger.isDebugEnabled()) {
			Object[] args = { secondaryValue };
			logger.debug(messageProvider.getMessage( "rmjg.resolveobjectname_failed", args ));				
		}
		
		
		// return null
		return objectName;
	}
    		
	public ObjectName resolveParentObjectName(ResolvedType resolvedType) {

		if(logger.isDebugEnabled()) logger.debug("DEBUG resolved type=" + resolvedType);    	
		
		// get parent type 
		ResolvedType parent = resolvedType.getParent();

		if(logger.isDebugEnabled()) logger.debug("DEBUG parent resolved type=" + parent );    	
		
		// if parent is a collection then get parent of the collection
		if (parent instanceof ResolvedCollection ) {
			if(logger.isDebugEnabled()) logger.debug("DEBUG parent value is a collection - using parent's parent to resolved object name." );
			if(logger.isDebugEnabled()) logger.debug("DEBUG parent parent resolved type=" + parent.getParent() );    				
			return resolveObjectName(parent.getParent());
		}
				
		// define parent object name
		return resolveObjectName(parent);
	}
    
}
