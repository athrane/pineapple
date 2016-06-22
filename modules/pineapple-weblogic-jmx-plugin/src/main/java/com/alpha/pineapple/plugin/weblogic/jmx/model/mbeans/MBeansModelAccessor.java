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

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;

/**
 * Interface for accessing MBean model objects. 
 */
public interface MBeansModelAccessor
{

    /**
     * Returns true if object name corresponds to MBean type. If the
     * object name doesn't contain a Type property then false is 
     * returned. The comparison is case sensitive.
     * 
     * @param objName Object name whose type is validated.  
     * @param type The type is is validated. 
     * 
     * @return true if object name corresponds to MBean type. If the
     * object name doesn't contain a Type property then false is 
     * returned.
     */
	public boolean isMBeanType(ObjectName objName, String type);
	
        
    /**
     * Returns true if participant represent a primitive attribute on an MBean.

     * @param participant which is tested.
     * 
     * @return true if participant represent a primitive attribute on an MBean.
     */
	boolean isPrimitive(ResolvedParticipant participant );

    /**
     * Returns true if participant represent a collection attribute on an MBean.

     * @param participant which is tested.
     * 
     * @return true if participant represent a collection attribute on an MBean.
     */	
	boolean isCollection(ResolvedParticipant secondary);

    /**
     * Returns true if participant represent an enum attribute on an MBean.

     * @param participant which is tested.
     * 
     * @return true if participant represent a enum attribute on an MBean.
     */		
	boolean isEnum(ResolvedParticipant secondary);

    /**
     * Returns true if participant represent a object attribute on an MBean.

     * @param participant which is tested.
     * 
     * @return true if participant represent a object attribute on an MBean.
     */		
	boolean isObject(ResolvedParticipant secondary);
			
}
