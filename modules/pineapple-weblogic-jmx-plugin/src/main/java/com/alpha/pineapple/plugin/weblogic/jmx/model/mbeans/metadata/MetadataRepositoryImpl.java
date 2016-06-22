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

import java.util.TreeMap;

import javax.annotation.Resource;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;

import org.apache.log4j.Logger;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;

/**
 * Implementation of the {@linkplain MetadataRepository} interface. 
 */
public class MetadataRepositoryImpl implements MetadataRepository {

	/**
	 * Value on MBeanInfo which contains MBean name for Virtual Host.
	 */	
	static final String MBEAN_NAME_VIRTUAL_HOST = "VirtualHostMBeanImpl";

	/**
	 * Field on MBeanInfo which contains MBean name.
	 */
    static final String MBEANINFO_NAME_FIELD = "Name";

	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    // attribute meta data container
    TreeMap<String, MBeanMetadata> mbeans =  new TreeMap<String, MBeanMetadata>(); 
    
	public String getIdFromMBeanInfo(MBeanInfo mbeanInfo) {
		return mbeanInfo.getDescriptor().getFieldValue(MBEANINFO_NAME_FIELD).toString();		
	}

	public void addMBean(MBeanInfo mbeanInfo) {
		
		// exit if MBean is already registered 
		if (containsMBean(mbeanInfo)) return;

		// create MBeanMetadata
		MBeanMetadata mbeanMetadata = new MBeanMetadataImpl( mbeanInfo );
		
		// store meta data
		String key = getIdFromMBeanInfo(mbeanInfo);
		mbeans.put(key, mbeanMetadata);		
	}

	
	public boolean containsMBean(MBeanInfo mbeanInfo) {
		String key = getIdFromMBeanInfo(mbeanInfo);
		return mbeans.containsKey(key);
	}

	
	public MBeanAttributeInfo getAttributeInfo(MBeanInfo mbeanInfo, String attributeName) {
		
		// add MBean if it doesn't already exists
		addMBean(mbeanInfo);
				
		// get MBean meta data
		MBeanMetadata mbeanMetadata = getMBeanMetadata(mbeanInfo);
		 		
		// map VirtualHost.VirtualHostName to VirtualHost.VirtualHostNames
		String id = getIdFromMBeanInfo(mbeanInfo);
		if (id.equalsIgnoreCase(MBEAN_NAME_VIRTUAL_HOST)) {			
			if( attributeName == "VirtualHostName") {
				attributeName = "VirtualHostNames";
			}
		}
				
		// get attribute meta data
		AttributeMetadata attributeMetadata = mbeanMetadata.getAttribute(attributeName);
		
		// handle null case
		if (attributeMetadata == null) return null;
				
		return attributeMetadata.getInfo();
	}

	
	public MBeanMetadata getMBeanMetadata(MBeanInfo mbeanInfo) {
		String key = getIdFromMBeanInfo(mbeanInfo);
		return mbeans.get(key);
	}
	
	
	public MBeanOperationInfo getCreateMethod(MBeanInfo mbeanInfo, String attributeName) {
		
		// add MBean if it doesn't already exists
		addMBean(mbeanInfo);
				
		// get MBean meta data
		MBeanMetadata mbeanMetadata = getMBeanMetadata(mbeanInfo);
		
		// get attribute meta data
		AttributeMetadata attributeMetadata = mbeanMetadata.getAttribute(attributeName);
		
		// handle null case
		if (attributeMetadata == null) return null;
		
		// get method name
		String methodName = attributeMetadata.getCreateMethod();
		
		// handle null case
		if (methodName == null) return null;
		
		// get operation info
		MBeanOperationInfo info = mbeanMetadata.getOperationInfo(methodName);
		
		return info;
	}

	
	public MBeanOperationInfo getDestroyMethod(MBeanInfo mbeanInfo, String attributeName) {
		// add MBean if it doesn't already exists
		addMBean(mbeanInfo);
				
		// get MBean meta data
		MBeanMetadata mbeanMetadata = getMBeanMetadata(mbeanInfo);
		
		// get attribute meta data
		AttributeMetadata attributeMetadata = mbeanMetadata.getAttribute(attributeName);
		
		// handle null case
		if (attributeMetadata == null) return null;
		
		// get method name
		String methodName = attributeMetadata.getDestroyMethod();
		
		// handle null case
		if (methodName == null) return null;
		
		// get operation info
		MBeanOperationInfo info = mbeanMetadata.getOperationInfo(methodName);
		
		return info;
	}

	public String[] resolveCandidateReferenceTypes(MBeanAttributeInfo info) {
		
		// validate if current attribute defines targeting attribute
		if(( info.getName().equals("Targets")) && (info.getType().equals(WebLogicMBeanConstants.OBJECTNAME_ARRAY_TYPE))) {
			
			// return target type: cluster and server 
			return new String[] { "Cluster" , "Server" };
		}

		// validate if current attribute defines targeting attribute
		if(( info.getName().equals("Machine")) && (info.getType().equals(WebLogicMBeanConstants.OBJECTNAME_TYPE))) {
			
			// return target type: cluster and server 
			return new String[] { "Machine" };
		}

		// validate if current attribute defines targeting attribute
		if(( info.getName().equals("Cluster")) && (info.getType().equals(WebLogicMBeanConstants.OBJECTNAME_TYPE))) {
			
			// return target type: cluster and server 
			return new String[] { "Cluster" };
		}

		// validate if current attribute defines targeting attribute
		if(( info.getName().equals("Capacity")) && (info.getType().equals(WebLogicMBeanConstants.OBJECTNAME_TYPE))) {
			
			// return target type: cluster and server 
			return new String[] { "Capacity" };
		}

		// validate if current attribute defines targeting attribute
		if(( info.getName().equals("MaxThreadsConstraint")) && (info.getType().equals(WebLogicMBeanConstants.OBJECTNAME_TYPE))) {
			
			// return target type: cluster and server 
			return new String[] { "MaxThreadsConstraint" };
		}

		// validate if current attribute defines targeting attribute
		if(( info.getName().equals("MinThreadsConstraint")) && (info.getType().equals(WebLogicMBeanConstants.OBJECTNAME_TYPE))) {
			
			// return target type: cluster and server 
			return new String[] { "MinThreadsConstraint" };
		}
		
		// create error message
    	Object[] args = { info };
    	String message = messageProvider.getMessage("mdr.resolvecandidatetype_failed", args);        	
		
		// throw exception
		throw new UnsupportedOperationException(message);
	}
	

	
}
