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

import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;

/**
 * Meta data object for WebLogic MBean.
 */
public interface MBeanMetadata {
		
	/**
	 * Get attribute by name. Returns null if no attribute was found. 
	 * 
	 * @param name Attribute to search for.
	 * 
	 * @return attribute meta data.
	 */
	AttributeMetadata getAttribute(String name);

	/**
	 * Get operation info by name. Returns null if no attribute was found.
	 * 
	 * @param name Operation to search for.
	 * 
	 * @return operation info. 
	 */	
	MBeanOperationInfo getOperationInfo(String name);

	/**
	 * Get MBeanInfo object from meta data.
	 * 
	 * @return MBeanInfo object 
	 */
	MBeanInfo getInfo();

}
