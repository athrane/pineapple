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

import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.*;

import javax.management.ObjectName;

/**
 * Implementation of {@link SpecialCasesAttributeMapper} interface.
 */
@Deprecated
public class DEPRECATEDSpecialCasesAttributeMapperImpl {

	public String mapResolution(String name) {
			
        // handle case for SNMPLogFilterMBean.EnabledServer 
		// which is represented by the MBean attribute SNMPLogFilterMBean.EnabledServers     	    	
    	if (name.equals( "EnabledServer") ) return "EnabledServers";

        // handle case for MBean attribute "WLDFHarvesterBean.HarvestedTypes"
    	// which fails to get resolved to "HarvestedType", but is instead to resolved to "WLDFHarvestedType".
    	// NB. This mapping rule due to the fact the MBean attribute name resolution is based on the return type of the getter's 
    	if (name.equals( "HarvestedType") ) return "WLDFHarvestedType";
    	
        // handle case for MBean attribute "WLDFWatchNotificationBean.Watches"
    	// which fails to get resolved to "Watch", but is instead to resolved to "WLDFWatch".      	
    	// NB. This mapping rule due to the fact the MBean attribute name resolution is based on the return type of the getter's    	
    	if (name.equals( "Watch") ) return "WLDFWatch";    	

        // handle case for MBean attribute "WLDFWatchNotificationBean.SnmpNotifications"
    	// which fails to get resolved to "SnmpNotification", but is instead to resolved to "WLDFSNMPNotification".      	
    	// NB. This mapping rule due to the fact the MBean attribute name resolution is based on the return type of the getter's    	
    	if (name.equals( "SnmpNotification") ) return "WLDFSNMPNotification";    	

        // handle case for HarvestedType.HarvestedAttribute 
		// which is represented by the MBean attribute WLDFHarvestedTypeBean.HarvestedAttributes
    	if (name.equals( "HarvestedAttribute") ) return "HarvestedAttributes";

        // handle case for Watch.Notification
		// which is represented by the MBean attribute WLDFWatchBean.Notificationa
    	if (name.equals( "Notification") ) return "Notifications";
    	    	
		// return original name if no match was found;
		return name;
	}

	public String mapCreateMBean(String name) {

        // handle case for WLDFHarvestedTypeBean  
		// which is must be created by the MBean method WLDFHarvesterBean.createHarvestedType()     	    	
		if (name.equals("WLDFHarvestedTypeBean")) return "HarvestedType";

        // handle case for WLDFWatchBeanBean  
		// which is must be created by the MBean method WLDFWatchNotificationBean.createWatch()     	    	
		if (name.equals("WLDFWatchBean")) return "Watch";

        // handle case for WLDFSNMPNotificationBean  
		// which is must be created by the MBean method WLDFWatchNotificationBean.createSNMPNotification()     	    			
		if (name.equals("WLDFSNMPNotificationBean")) return "SNMPNotification";		
		
		// return original name if no match was found;
		return name;
	}

	
	public String mapAttributeName(ObjectName objName, String name) {
	
        // handle case for Domain.Server 
		// which is represented by the MBean attribute Domain.Servers     	    	
    	if (name.equals("Server" )) {    		
    		if (isMBeanType(objName, DOMAIN_MBEAN_TYPE )) return "Servers";
    	}    	    	
				
		// handle case for SNMPxxxxMBean.EnabledServers		
    	if (name.equals("EnabledServer" )) {    		
    		if (isMBeanType(objName, "SNMPLogFilter")) return "EnabledServers";
    		if (isMBeanType(objName, "SNMPStringMonitor")) return "EnabledServers";      		
    		if (isMBeanType(objName, "SNMPGaugeMonitor")) return "EnabledServers";      		    			
    		if (isMBeanType(objName, "SNMPCounterMonitor")) return "EnabledServers";      		    			
    		if (isMBeanType(objName, "SNMPJMXMonitor")) return "EnabledServers";   		
    	}    	

		// handle case for WLDFSystemResource.Targets    	
    	if (name.equals("Target" )) {    		
    		if (isMBeanType(objName, WLDF_SYSTEMRESOURCE_MBEAN_TYPE )) return "Targets";
    	}    	

		// handle case for WLDFHarvestedTypeBean.HarvestedAttributes
    	if (name.equals("HarvestedAttribute" )) {    		
    		if (isMBeanType(objName, WLDF_HARVESTEDTYPE_MBEAN_TYPE )) return "HarvestedAttributes";
    	}    	    	

		// handle case for WLDFWatchBean.Notifications
    	if (name.equals("Notification" )) {    		
    		if (isMBeanType(objName, WLDF_WATCH_MBEAN_TYPE )) return "Notifications";
    	}    	    	
    	    	    
		// return original name if no match was found;
		return name;    	
	}
	
	
	public boolean isAttributeServerObjectName(ObjectName objName, String name) {
		
		// handle case for SNMPxxxxMBean.EnabledServers		
		if(name.equals("EnabledServers")) {						
    		if (isMBeanType(objName, "SNMPLogFilter")) return true;
    		if (isMBeanType(objName, "SNMPStringMonitor")) return true;      		
    		if (isMBeanType(objName, "SNMPGaugeMonitor")) return true;      		
    		if (isMBeanType(objName, "SNMPCounterMonitor")) return true;
    		if (isMBeanType(objName, "SNMPJMXMonitor")) return true;
		}

		// handle case for WLDFSystemResource.Targets    	
		if(name.equals("Targets")) {
			if (isMBeanType(objName, WLDF_SYSTEMRESOURCE_MBEAN_TYPE )) return true;
		}
	
		// return default answer
		return false;
	}
	
	
	public boolean isAttributeWldfNotificationObjectName(ObjectName objName, String name) {
		
		// handle case for Watch.Notifications    	
		if(name.equals("Notifications")) {
			if (isMBeanType(objName, WLDF_WATCH_MBEAN_TYPE )) return true;
		}
	
		// return default answer
		return false;
	}

	/**
	 * Return true if Object name contains key property name "type"
	 * which matches queried type name.
	 * 
	 * @param objName Object name to test.
	 * 
	 * @param type Type name to test.
	 * 
	 * @return true if Object name contains key property name "type"
	 * which matches queried type name.
	 */
	boolean isMBeanType(ObjectName objName, String type) {

		String typeProperty = objName.getKeyProperty("Type");
		
		if (typeProperty == null) return false;		
		return (typeProperty.equals(type));
	}
	
	
}
