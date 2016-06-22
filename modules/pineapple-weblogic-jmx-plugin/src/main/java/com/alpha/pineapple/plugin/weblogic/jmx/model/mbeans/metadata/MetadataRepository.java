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
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;

/**
 * Repository for providing information about WebLogic MBeans.
 */
public interface MetadataRepository {

	/**
	 * Return MBean ID  from MBeanInfo object.
	 * 
	 * @param mbeanInfo BeanInfo object.
	 * 
	 * @return Id from MBeanInfo object.
	 */
	String getIdFromMBeanInfo(MBeanInfo mbeanInfo);	
		
	/**
	 * Add meta data about the supplied MBean to repository. 
	 * 
	 * @param mbeanInfo MBean info. 
	 */
	void addMBean(MBeanInfo mbeanInfo);

	/**
	 * Returns true if the repository already contains 
	 * meta data about the MBean. 
	 * 
	 * @param mbeanInfo MBean info. 
	 */
	boolean containsMBean(MBeanInfo mbeanInfo);
	
	/**
	 * Get MBean meta data for MBean.
	 *  
	 * @param mbeanInfo MBean info. 
	 * 
	 * @return MBean meta data for MBean.
	 */
	MBeanMetadata getMBeanMetadata(MBeanInfo mbeanInfo);
	
	/**
	 * Resolve MBean attribute info from object name and attribute name.
	 * <p>
	 * If the meta data about the MBean isn't registered in the repository
	 * then is is added. 
	 * </p>
	 * <p>
	 * The returned attribute info object may have a different name than the 
	 * supplied attribute name due to a mapping from the source attribute name
	 * to the attribute in the MBean model.       
	 * </p>
	 * If the MBean doesn't contain an attribute with the queried name then 
	 * null is returned.
	 *  
	 * @param mbeanInfo MBean info for the MBean where the attribute should be resolved on.
	 * @param attributeName Name of the attribute which should be resolved.
	 *  
	 * @return MBean attribute info 
	 */
	MBeanAttributeInfo getAttributeInfo(MBeanInfo mbeanInfo, String attributeName );
	
	/**
	 * Resolve MBean factory method for attribute.
	 * <p>
	 * If the meta data about the MBean isn't registered in the repository
	 * then is is added. 
	 * </p>
	 * <p>
	 * The returned operation info object may have a different name than the 
	 * supplied attribute name due to a mapping from the source attribute name
	 * to the attribute in the MBean model.       
	 * </p>
	 * If the MBean doesn't contain an attribute with the queried name then 
	 * null is returned.
	 * If the MBean contains an attribute with the queried name but the attribute
	 * doesn't have a factory method then null is returned.
	 *  
	 * @param mbeanInfo MBean info for the MBean where the method should be resolved on.
	 * @param attributeName Name of the attribute which should be resolved.
	 *  
	 * @return MBean operation info object for the factory method. 
	 */	
	MBeanOperationInfo getCreateMethod(MBeanInfo mbeanInfo, String attributeName);

	/**
	 * Resolve MBean destroy method for attribute.
	 * <p>
	 * If the meta data about the MBean isn't registered in the repository
	 * then is is added. 
	 * </p>
	 * <p>
	 * The returned operation info object may have a different name than the 
	 * supplied attribute name due to a mapping from the source attribute name
	 * to the attribute in the MBean model.       
	 * </p>
	 * If the MBean doesn't contain an attribute with the queried name then 
	 * null is returned.
	 * If the MBean contains an attribute with the queried name but the attribute
	 * doesn't have a factory method then null is returned.
	 *  
	 * @param mbeanInfo MBean info for the MBean where the method should be resolved on.
	 * @param attributeName Name of the attribute which should be resolved.
	 *  
	 * @return MBean operation info object for the destroy method. 
	 */	
	MBeanOperationInfo getDestroyMethod(MBeanInfo mbeanInfo, String attributeName);

	/**
	 * Resolves a list of candidate MBean types used to convert MBean names to object names.
	 * The resolution is based on meta data about the attribute.
	 * 
	 * @param info MBean attribute info.
	 * 
	 * @return list of candidate MBean types. 
	 */
	String[] resolveCandidateReferenceTypes(MBeanAttributeInfo info);
		
}
