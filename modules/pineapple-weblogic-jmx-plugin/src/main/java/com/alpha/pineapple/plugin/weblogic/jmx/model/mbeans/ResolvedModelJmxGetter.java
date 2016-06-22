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

import javax.management.ObjectName;

import com.alpha.pineapple.resolvedmodel.ResolvedType;

/**
 * Helper class which retrieves data from a resolved model and interprets it as JMX data. 
 */
public interface ResolvedModelJmxGetter {
		
	/**
	 * Get attribute id from resolved type. The id is resolved 
	 * from the name of the secondary participant.
	 * 
	 * @param resolvedType Resolved where the id is resolved from.
	 * 
	 * @return attribute id resolved from the name of the secondary participant.
	 */
    public String getSecondaryAttributeID(ResolvedType resolvedType);    	

    /**
     * Get attribute value from from resolved type. The value is resolved 
     * from the value of the primary participant which is returned as an Object.
     * 
	 * @param resolvedType Resolved where the value is resolved from.
	 * 
	 * @return attribute value resolved from the value of the primary participant.
     */
    public Object getPrimaryAttributeValue(ResolvedType resolvedType);

	/**
	 * Get attribute type from resolved type. The type is resolved from the type 
	 * of the secondary participant.
	 * 
	 * @param resolvedType Resolved where the type is resolved from.
	 * 
	 * @return attribute type resolved from the type of the secondary participant.
	 */
    public Object getSecondaryAttributeType(ResolvedType resolvedType);    	

	/**
	 * Get attribute type from resolved type. The type is resolved from the type 
	 * of the primary participant.
	 * 
	 * @param resolvedType Resolved where the type is resolved from.
	 * 
	 * @return attribute type resolved from the type of the primary participant.
	 */
    public String getPrimaryAttributeTypeAsString(ResolvedType resolvedType);    	
    
    /**
     * Get MBean object name. The object name is resolved from object name of the dynamic 
     * MBean proxy which is contained in the secondary participant in the parent resolved 
     * type. If the object name resolution failed then null is returned.    
     * 
	 * @param resolvedType Resolved where the value is resolved from.
	 *  
     * @return MBean object name resolved from the dynamic MBean proxy. 
     */
    public ObjectName getParentObjectName(ResolvedType resolvedType);
    
    /**
     * Resolve MBean object name. The object name is resolved from the value contained in 
     * the secondary participant in the parent resolved type.
     * 
     * If the value contained in the secondary participant is a object name 
     * then it is returned. If the value is a dynamic MBean Proxy then the object
     * name of the proxy is resolved. If the object name resolution failed then null is
     * returned.  
     *   
     * 
	 * @param resolvedType Resolved where the value is resolved from.
	 *  
     * @return MBean object name resolved from the dynamic MBean proxy. 
     */
    public ObjectName resolveObjectName(ResolvedType resolvedType);
    
	/**
	 * Resolve parent object name. The method support resolution of the parent object name
	 * if the parent secondary contains a value of the type ObjectName or a value which 
	 * is a dynamic MBean Proxy.
	 * 
	 * If the parent is a resolved collection then parent of the collection is used
	 * because no object names exist for a resolved collection.   
	 * 
	 * @return parent object name
	 */
	public ObjectName resolveParentObjectName(ResolvedType resolvedType);
   	
}
