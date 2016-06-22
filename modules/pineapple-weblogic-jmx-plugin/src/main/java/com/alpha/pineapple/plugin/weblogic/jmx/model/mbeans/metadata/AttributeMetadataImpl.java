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

import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;

/**
 * Implementation of the {@linkplain AttributeMetadata} interface. 
 */
public class AttributeMetadataImpl implements AttributeMetadata {

	/**
	 * Empty string.
	 */	
	static final String EMPTY_STRING = "";

	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Create method name.
     */
	String createMethod;

    /**
     * Destroy method name.
     */	
	String destroyMethod;

    /**
     * Alternate attribute name.
     */		
	String alternateName;

    /**
     * Attribute name.
     */			
	String attributeName;

    /**
     * Attribute type.
     */			
	String attributeType;

	/**
	 * MBean attribute info.
	 */
	MBeanAttributeInfo attributeInfo;
	
    public AttributeMetadataImpl(MBeanAttributeInfo attributeInfo) {
    	
    	// get descriptor
    	Descriptor descriptor = attributeInfo.getDescriptor();
    	    	
    	// resolve attributes
		attributeName = attributeInfo.getName();
		attributeType = attributeInfo.getType();		
		alternateName = parseAlternateName(descriptor);
		createMethod = parseCreateMethod(descriptor);     	
		destroyMethod = parseDestroyerMethod(descriptor);
		
		this.attributeInfo = attributeInfo;		
	}
    
    public String getName() {		
		return attributeName;
	}


	public String getAlternateName() {
		return alternateName;
	}
	
	public String getType() {
		return attributeType;
	}


	public String getCreateMethod() {
		return createMethod;
	}


	public String getDestroyMethod() {
		return destroyMethod;
	}


	public MBeanAttributeInfo getInfo() {
		return attributeInfo;
	}

	
	/**
     * Parse the alternate name which can be used to map to this attribute from 
     * the source attribute name.
     * 
     * The algorithm is to use the singular of the attribute which
     * is used in the create and destroy operations, e.g. resolve the alternate 
     * name from 'createXX' where the alternate name is 'XX'.   
     * 
     * If the MBean doesn't have any create method defined then an alternate
     * name is computed from the interface name if one exists. 
     * 
     * @param descriptor Attribute meta data. 
     * 
     * @return Name of the create method name.
     */
	String parseAlternateName(Descriptor descriptor) {						
		String createMethod = parseCreateMethod(descriptor);
						
		// handle case with defined create method
		if (!createMethod.equals(EMPTY_STRING)) {
			return StringUtils.substringAfter(createMethod, "create");			
		}
				
		// get interface field
		Object interfaceName = descriptor.getFieldValue(WebLogicMBeanConstants.INTERFACE_DESCRIPTOR_FIELD);

		// exit if no interface is defined
		if (interfaceName == null ) return EMPTY_STRING;		
		
		// parse name from interface: [Lweblogic.management.configuration.AppDeploymentMBean;
		String name = StringUtils.substringAfterLast(interfaceName.toString(), ".");
		
		// exit if substring is empty 
		if (name.equals(EMPTY_STRING))return EMPTY_STRING; 
		
		name = StringUtils.substringBefore(name, "MBean");
		return name;
	}
    
	
    /**
     * Parse the create method name from the meta data.
     * 
     * @param descriptor Attribute meta data.
     * 
     * @return Name of the create method name.
     */
	String parseCreateMethod(Descriptor descriptor) {					
		Object value = descriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_CREATOR_DESCRIPTOR_FIELD);
		
		// return value
		if (value != null) return value.toString();
		
		// handle null case
		return EMPTY_STRING;
	}

	
    /**
     * Parse the destroy method name from the embedded meta data.
     * 
     * @param descriptor Attribute meta data.
     * 
     * @return Name of the destroy method name.
     */
	String parseDestroyerMethod(Descriptor descriptor) {		
		Object value = descriptor.getFieldValue(WebLogicMBeanConstants.COM_BEA_DESTROYER_DESCRIPTOR_FIELD);

		// return value
		if (value != null) return value.toString();
		
		// handle null case
		return EMPTY_STRING;		
	}

		
}
