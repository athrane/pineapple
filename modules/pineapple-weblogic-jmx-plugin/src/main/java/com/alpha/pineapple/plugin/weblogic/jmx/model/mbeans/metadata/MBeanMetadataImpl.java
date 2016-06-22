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

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Implementation of the {@linkplain MBeanMetadata} interface. 
 */
public class MBeanMetadataImpl implements MBeanMetadata {
	
	/**
	 * Empty string.
	 */	
	static final String EMPTY_STRING = "";
	
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	
	/**
	 * Attribute meta data container
	 */
    TreeMap<String, AttributeMetadata> attributes =  new TreeMap<String, AttributeMetadata>();

	/**
	 * Operation meta data container
	 */
    TreeMap<String, MBeanOperationInfo> operations = new TreeMap<String, MBeanOperationInfo>();
    
	/**
	 * MBean info.
	 */
    MBeanInfo mbeanInfo;
    
	/**
	 * MBeanMetadataImpl constructor.
	 * 
	 * @param mbeanInfo MBean info object.
	 */
    public MBeanMetadataImpl(MBeanInfo mbeanInfo) {
    	
    	// store MBean Info
    	this.mbeanInfo = mbeanInfo;
    	
    	// iterate over the attributes to create meta data for each attribute   	
    	for (MBeanAttributeInfo attributeInfo : mbeanInfo.getAttributes()) {    		
    		AttributeMetadata attributeMetadata =  new AttributeMetadataImpl( attributeInfo );    		
    		storeMetadata(attributeMetadata);    		
    	}    	        	
    	
        // get operation info's
        MBeanOperationInfo[] operationInfos = mbeanInfo.getOperations();
        
    	// iterate over the operations to store meta data for each attribute 
        for ( MBeanOperationInfo operationInfo : operationInfos ) {
        	storeOperationInfo(operationInfo);        	        	        	
        }    	
	}

    /**
     * Store operation info. An operation info is only stored if it has 
     * a single argument signature.  
     * 
     * @param operationInfo Operation info to store.
     */
	void storeOperationInfo(MBeanOperationInfo operationInfo) {		
		MBeanParameterInfo[] signature = operationInfo.getSignature();
		String key = operationInfo.getName();

		// exit if signature has more than one argument
		if (signature.length > 1) return;		

		// if no operation is defined with name the store it
		if(!operations.containsKey(key)) {
			operations.put(key, operationInfo);            	
			return;
		}
		
		// if new operation is single arg then overwrite zero-arg
		if (signature.length == 1) {
			operations.put(key, operationInfo);            	            	        					
		}
			
		return;
	}

    /**
     * Store meta data in map in name and alternate name.
     * 
     * @param attributeMetadata Meta data to be stored.
     */
	void storeMetadata(AttributeMetadata attributeMetadata) {
								
		// store attribute meta data by name 
		String name = attributeMetadata.getName().toLowerCase();		
		attributes.put(name, attributeMetadata);

		// get alternate name
		String alternateName = attributeMetadata.getAlternateName().toLowerCase();    	
		
		// don't store alternate name if it is null 
		if (alternateName == null) return;

		// don't store alternate name if it is null 
		if (alternateName.equals(EMPTY_STRING)) return;

		// don't store alternate name if it is equal to the name 
		if (alternateName.equalsIgnoreCase(name)) return;
		
		// store alternate name if it doesn't exist
		if (!attributes.containsKey(alternateName)) {
			attributes.put(alternateName, attributeMetadata);			
			return;
		}
		 
		// only overwrite if the name and the alternate name begins with the same sequence
		if (!overwriteAlternateName(name, alternateName)) return;
				
		// overwrite
		attributes.put(alternateName, attributeMetadata);
	}
    
	public AttributeMetadata getAttribute(String name) {
		return attributes.get(name.toLowerCase());
	}

	public MBeanOperationInfo getOperationInfo(String name) {
		return operations.get(name);
	}
	
	public MBeanInfo getInfo() {
		return mbeanInfo;
	}

	/**
	 * Return true if alternate name should be overwritten, i.e. 
	 * the name of the name and alternate have a common prefix.
	 *  
	 * @param name Attribute name.
	 * @param alternateName Alternate attribute name.
	 * 
	 * @return true if alternate name should be overwritten, i.e. 
	 * the name of the name and alternate have a common prefix.
	 */
	boolean overwriteAlternateName(String name, String alternateName) {
		String [] names = { name, alternateName };
		String prefix = StringUtils.getCommonPrefix(names);
		return ( prefix.length() != 0);
	}
	
}
