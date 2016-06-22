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


package com.alpha.pineapple.plugin.weblogic.jmx.operation;

import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.WLDF_RESOURCE_ATTRIBUTE;
import static com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants.WLDF_SYSTEMRESOURCE_ATTRIBUTE;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Resource;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ObjectName;

import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.SchemaProperty;

import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;
import com.alpha.pineapple.plugin.weblogic.jmx.model.xmlbeans.XmlBeansModelAccessor;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;
import com.oracle.xmlns.weblogic.domain.DomainDocument;
import com.oracle.xmlns.weblogic.domain.DomainType;
import com.oracle.xmlns.weblogic.weblogicDiagnostics.WldfResourceDocument;
import com.oracle.xmlns.weblogic.weblogicDiagnostics.WldfResourceType;

public class ResolvedModelIntializerImpl implements ResolvedModelInitializer {

	/**
	 * XMLBeans schema property name for the WLDF resource type.
	 */	
	static final String WLDFRESOURCE_SCHEMA_PROPERTY_NAME = "WldfResource";

	/**
	 * XMLBeans schema property name for the domain type.
	 */
	static final String DOMAIN_SCHEMA_PROPERTY_NAME = "Domain";

	/**
	 * WebLogic Edit session type.
	 */
    static final Class<WeblogicJMXEditSession> WEBLOGIC_EDIT_SESSION_CLASS = WeblogicJMXEditSession.class;

    /**
     * First list index.
     */
	static final int FIRST_INDEX = 0;
	
    /**
     * Null parent, used in initial resolved object.
     */
    static final ResolvedType NULL_PARENT = null;
	
	/**
	 * Local visitor "accept" method names.
	 */
	static final String INITIALIZE_METHOD = "initialize";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	
    /**
     * XMLBean model access object.
     */
    @Resource( name = "xmlBeansModelAccessor" )
    XmlBeansModelAccessor modelAccessor;
        		
	public ResolvedType initialize(DomainDocument domainDoc, WeblogicJMXEditSession session ) throws Exception {

        // create primary participant        	        
        ResolvedParticipant primary = createDomainXmlBeanParticipant(domainDoc);
        
        // create secondary participant        
        ResolvedParticipant secondary = createDomainMBeanParticipant( session );
		
        // create resolved type at the root of the models
        return ResolvedTypeImpl.createResolvedObject( NULL_PARENT, primary, secondary );                              	        	
	}

	public ResolvedType initialize(WldfResourceDocument wldfResourceDoc, WeblogicJMXEditSession session ) throws Exception {

        // create primary participant        	        
        ResolvedParticipant primary = createWldfResourceXmlBeanParticipant(wldfResourceDoc);
        
        // get resource name from doc
        WldfResourceType wldfResource = wldfResourceDoc.getWldfResource();                
        String name = wldfResource.getName();        

        // create secondary participant        
        ResolvedParticipant secondary = createWldfResourceMBeanParticipant(session, name );

        // create resolved type at the root of the models
        return ResolvedTypeImpl.createResolvedObject( NULL_PARENT, primary, secondary );                              	        			
	}

	public ResolvedType initialize(Object content, WeblogicJMXEditSession session ) throws Exception {
				
		// get interfaces 
		List<Class<?>> interfaces = ClassUtils.getAllInterfaces(content.getClass());
		
		// use first interface
		Class<?> documentInterface = interfaces.get(FIRST_INDEX); 
		
		// define parameters
		Class<?>[] parameterTypes = new  Class[]{ documentInterface, WEBLOGIC_EDIT_SESSION_CLASS }; 

		// locate visitor "accept" method with correct type on this class  
		Method initializerMethod = getClass().getMethod(INITIALIZE_METHOD, parameterTypes);
				
		// define method arguments
		final Object[] args = new Object[]{content, session};
				
		// invoke local "accept" method		
		return (ResolvedType) initializerMethod.invoke( this, args );
	}

    /**
     * Create resolved participant with Domain XMLBean as object.
     *  
     * @param content
     * @return
     */
	ResolvedParticipant createDomainXmlBeanParticipant(Object content) {
		
		// get root object in XML Beans model
		DomainDocument domainDoc = (DomainDocument) content;
		
		// get domain bean
		DomainType domainXmlBean = domainDoc.getDomain();                        
	
		// get domain name
		String domainName = getDomainName( domainXmlBean );
		
		// get schema property for XMLBean
		SchemaProperty schemaProperty = modelAccessor.getSchemaPropertyByName(DOMAIN_SCHEMA_PROPERTY_NAME, domainDoc);        
		
		// create primary participant
		ResolvedParticipant primary = ResolvedParticipantImpl.createSuccessfulResult( domainName, schemaProperty, domainXmlBean );
		
		return primary;
	}

    /**
     * Create resolved participant with WLDFResource XMLBean as object.
     *  
     * @param content Plugin model.
     *  
     * @return resolved participant with WLDFResource XMLBean as object.
     */
	ResolvedParticipant createWldfResourceXmlBeanParticipant(Object content) {
		
		// get root object in XMLBeans model
		WldfResourceDocument wldfResourceDoc = (WldfResourceDocument) content;
		
		// get resource bean
		WldfResourceType wldfResourceXmlBean = wldfResourceDoc.getWldfResource();
		                        
		// get resource name
		String resourceName = getWldfResourceName(wldfResourceXmlBean);

		// get schema property for XMLBean
		SchemaProperty schemaProperty = modelAccessor.getSchemaPropertyByName(WLDFRESOURCE_SCHEMA_PROPERTY_NAME, wldfResourceDoc );        
		
		// create primary participant
		ResolvedParticipant primary = ResolvedParticipantImpl.createSuccessfulResult( resourceName, schemaProperty, wldfResourceXmlBean );
		
		return primary;
	}
	
		
    /**
     * Create resolved participant with Domain MBean object name as object.
     * 
     * @param editSession WebLogic JMX edit session.
     * 
     * @return resolved participant with Domain MBean object name as object.
     * 
     * @throws Exception if creation fails
     */
	ResolvedParticipant createDomainMBeanParticipant( WeblogicJMXEditSession editSession ) throws Exception {
		
		// get root object in MBeans model
        ObjectName objName = editSession.getDomainMBeanObjectName();        
        
        // get name
        String domainName = objName.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY);
        
        // create secondary participant
        ResolvedParticipant secondary;
        secondary = ResolvedParticipantImpl.createSuccessfulResult( domainName, objName.getClass(), objName );
		return secondary;
	}
	
			
    /**
     * Create resolved participant with WLDFResource MBean as object.
     * 
     * @param session WebLogic JMX edit session.
     * 
     * @param domainObjName
     * 
     * @return resolved participant with Domain MBean object name as object.
     * 
     * @throws Exception if creation fails
     */
	ResolvedParticipant createWldfResourceMBeanParticipant( WeblogicJMXEditSession session, String resourceName ) throws Exception {
		
		// get root object in MBeans model
        ObjectName objName = session.getDomainMBeanObjectName();
        
        // find WLDF system resource object name
        ObjectName wldfSystemResourceObjName = session.findMBean(WLDF_SYSTEMRESOURCE_ATTRIBUTE, resourceName);
        
        // create WLDF system resource
        if (wldfSystemResourceObjName == null ) {
        	    				
    		// get factory method for attribute
    		MBeanOperationInfo operationInfo = session.getCreateMethod(objName, WLDF_SYSTEMRESOURCE_ATTRIBUTE );
        	
    		// package parameters
            Object[] params = new Object[] { resourceName };
    		
    		// invoke method
    		objName = (ObjectName) session.invokeMethod(objName, operationInfo, params );    		
        }

        // get attribute info
        MBeanAttributeInfo info = session.getAttributeInfo(wldfSystemResourceObjName, WLDF_RESOURCE_ATTRIBUTE);
        
		// get WLDF resource
        ObjectName wldfResourceObjName = (ObjectName) session.getAttribute(wldfSystemResourceObjName, WLDF_RESOURCE_ATTRIBUTE);
        
        // create secondary participant
        ResolvedParticipant secondary;
        secondary = ResolvedParticipantImpl.createSuccessfulResult( resourceName, info, wldfResourceObjName );
		return secondary;        
	}
	
    /**
     * Get the domain name from the domain XMLBean. 
     * If the XMLBean is undefined then return a null string.
     * 
     * @param domainXmlBean the domain XMLBean.
     *  
     * @return The domain name from the domain XMLBean. If the 
     * XMLBean is undefined then return a null string.
     * 
     */
    String getDomainName( DomainType domainXmlBean )
    {
        if (domainXmlBean != null ) return domainXmlBean.getName();
        return UNDEFINED_DOMAIN_NAME;
    }

    /**
     * Get the resource name from the WLDFResource XMLBean. 
     * If the XMLBean is undefined then return a null string.
     * 
     * @param WldfResourceType the WLDFResource XMLBean.
     *  
     * @return The name from the WLDFResource XMLBean. If the 
     * XMLBean is undefined then return a null string.
     * 
     */
    String getWldfResourceName( WldfResourceType wldfResource )
    {
        if (wldfResource != null ) return wldfResource.getName();
        return UNDEFINED_DOMAIN_NAME;
    }
            
}
