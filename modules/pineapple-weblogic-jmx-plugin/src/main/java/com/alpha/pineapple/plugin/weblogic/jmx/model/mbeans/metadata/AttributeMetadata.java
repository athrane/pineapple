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


package com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.metadata;

import javax.management.MBeanAttributeInfo;

/**
 * Meta data for MBean attribute.
 */
public interface AttributeMetadata {

	/**
	 * Return the name of the attribute. 
	 * 
	 * The returned value is the actual MBean attribute name.
	 * 
	 * @return the name of the attribute.
	 */
	String getName();

	/**
	 * Return the type of the attribute. 
	 * 
	 * @return the type of the attribute.
	 */
	String getType();
	
	/**
	 * Return the alternate name of the attribute. 
	 * 
	 * The returned value is the alternate attribute name.
	 * 
	 * @return the name of the attribute.
	 */	
	String getAlternateName();
	
	/**
	 * Return method on MBean which is used to create an instance of the attribute.
	 *  
	 * The create method is only supported for attributes which reference MBeans
	 * either singletons or collections. 
	 * 
	 * If the attribute is a primitive type then null is returned.
	 *    
	 * @return method on MBean which is used to create and instance of the attribute. 
	 */
	String getCreateMethod();
	
	/**
	 * Return method on MBean which is used to destroy an instance of the attribute.
	 *  
	 * The destroy method is only supported for attributes which reference MBeans
	 * either singletons or collections. 
	 * 
	 * If the attribute is a primitive type then null is returned.
	 *    
	 * @return method on MBean which is used to destroy and instance of the attribute. 
	 */	
	String getDestroyMethod();

	/**
	 * Get MBeanAttributeInfo object from meta data.
	 * 
	 * @return MBeanAttributeInfo object 
	 */
	MBeanAttributeInfo getInfo();
}
