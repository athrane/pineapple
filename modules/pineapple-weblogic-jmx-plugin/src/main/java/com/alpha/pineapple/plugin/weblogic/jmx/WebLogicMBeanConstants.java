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


package com.alpha.pineapple.plugin.weblogic.jmx;

import com.oracle.xmlns.weblogic.domain.DomainDocument;
import com.oracle.xmlns.weblogic.weblogicDiagnostics.WldfResourceDocument;


/**
 * WebLogic JMX and MBean constants.
 */
public interface WebLogicMBeanConstants {
	
	/**
	 * Legal content types supported by plugin operations.
	 */
	public static final Class<?>[] LEGAL_CONTENT_TYPES = {DomainDocument.class, WldfResourceDocument.class };

	/**
	 * Object name of the WebLogic Edit Service MBean.
	 */
	public static final String EDIT_SERVICE_OBJECTNAME = "com.bea:Name=EditService,Type=weblogic.management.mbeanservers.edit.EditServiceMBean";

	/**
	 * Object name of the WebLogic Domain Runtime Service MBean.
	 */
	public static final String DOMAINRUNTIME_SERVICE_OBJECTNAME = "com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean";

	/**
	 * Object name of the WebLogic Type Service MBean.
	 */
	public static final String TYPE_SERVICE_OBJECTNAME = "com.bea:Name=MBeanTypeService,Type=weblogic.management.mbeanservers.MBeanTypeService";

	/**
	 * Name of the connection manager attribute on the EditServiceMBean.
	 */
    public static final String CONFIGURATION_MANAGER_ATTRIBUTE = "ConfigurationManager";	
	
	/**
	 * Deprecated realm MBean.
	 */
	public static final String DEPRECATED_REALM_MBEAN = "weblogic.management.configuration.RealmMBean";

	/**
	 * BEA JMX domain.
	 */
	public static final String BEA_JMX_DOMAIN= "com.bea:";

	/**
	 * BEA security JMX domain.
	 */
	public static final String SECURITY_JMX_DOMAIN = "Security:";

	/**
	 * MBean type used in object name for server objects.
	 */
	public static final String SERVER_MBEAN_TYPE = "Server";

	/**
	 * MBean type used in object name for DomainMBean objects.
	 */
	public static final String DOMAIN_MBEAN_TYPE = "Domain";
	
	/**
	 * MBean type used in object name for WLDFSystemResource objects.
	 */
	public static final String WLDF_SYSTEMRESOURCE_MBEAN_TYPE = "WLDFSystemResource";
	
	/**
	 * MBean type used in object name for WLDF Notification objects.
	 */
	public static final String WLDF_SNMPNOTIFICATION_MBEAN_TYPE = "weblogic.diagnostics.descriptor.WLDFSNMPNotificationBean";

	/**
	 * MBean type used in object name for WLDFWatch objects.
	 */	
	public static final String WLDF_WATCH_MBEAN_TYPE = "weblogic.diagnostics.descriptor.WLDFWatchBean";	

	/**
	 * MBean type used in object name for WLDFHarvestedtype objects.
	 */	
	public static final String WLDF_HARVESTEDTYPE_MBEAN_TYPE = "weblogic.diagnostics.descriptor.WLDFHarvestedTypeBean";	
	
	/**
	 * com.bea.creator descriptor field name.
	 */
    public static final String COM_BEA_CREATOR_DESCRIPTOR_FIELD = "com.bea.creator";

	/**
	 * com.bea.destroyer descriptor field name.
	 */    
    public static final String COM_BEA_DESTROYER_DESCRIPTOR_FIELD = "com.bea.destroyer";

	/**
	 * Interface class descriptor field name:
	 */
	public static final String INTERFACE_DESCRIPTOR_FIELD = "interfaceclassname";
        
	/**
	 * Method name for the startEdit() method.
	 */
    public static final String STARTEDIT_METHOD_NAME = "startEdit";

	/**
	 * Attribute name for WLDFSystemResource attribute on Domain MBean. 
	 */
    public static final String WLDF_SYSTEMRESOURCE_ATTRIBUTE = "WLDFSystemResource";

	/**
	 * Attribute name for WLDF Resource attribute on WLDFSystemResource MBean. 
	 */
    public static final String WLDF_RESOURCE_ATTRIBUTE = "WLDFResource";

	/**
	 * Name key property from object name. 
	 */
	public static final String OBJNAME_NAME_KEYPROPERTY = "Name";

	/*
	 * Type key property from object name.
	 */
	public static final String OBJNAME_TYPE_KEYPROPERTY = "Type";

	/**
	 * ObjectName array type as contained by MBean attributes in the {@linkplain Descriptor} type.
	 */
	public static final String OBJECTNAME_ARRAY_TYPE = "[Ljavax.management.ObjectName;";

	/**
	 * ObjectName type as contained by MBean attributes in the {@linkplain Descriptor} type.
	 */
	public static final String OBJECTNAME_TYPE = "javax.management.ObjectName";

	/**
	 * Provider packages that are consulted when looking for the handler for a protocol.
	 */
	public static final String WEBLOGIC_PROTOCOL_PROVIDER_PACKAGES = "weblogic.management.remote";
	
	/**
	 * Default JMX protocol used.
	 */
	public static final String DEFAULT_JMX_PROTOCOL = "jdk_iiop";	
	
	/**
	 * JNDI root.
	 */
	public static final String JNDI_ROOT = "/jndi/";

	/**
	 * Edit MBean server JNDI name.
	 */
	public static final String EDIT_SERVER_JNDI_NAME = "weblogic.management.mbeanservers.edit";

	/**
	 * Edit MBean server URL path.
	 */
	public static final String EDIT_SERVER_URLPATH = JNDI_ROOT + EDIT_SERVER_JNDI_NAME;
	
}

