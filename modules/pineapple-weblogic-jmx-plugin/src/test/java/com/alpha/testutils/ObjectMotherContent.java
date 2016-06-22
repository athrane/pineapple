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


package com.alpha.testutils;

import static org.junit.Assert.fail;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;

import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.oracle.xmlns.weblogic.domain.DomainDocument;
import com.oracle.xmlns.weblogic.domain.DomainType;
import com.oracle.xmlns.weblogic.domain.DomainType.ConfigurationAuditType.Enum;
import com.oracle.xmlns.weblogic.domain.MachineType;
import com.oracle.xmlns.weblogic.domain.NodeManagerType;
import com.oracle.xmlns.weblogic.domain.SecurityConfigurationType;
import com.oracle.xmlns.weblogic.domain.SelfTuningType;
import com.oracle.xmlns.weblogic.domain.VirtualHostType;
import com.oracle.xmlns.weblogic.domain.WldfSystemResourceType;
import com.oracle.xmlns.weblogic.domain.WorkManagerShutdownTriggerType;
import com.oracle.xmlns.weblogic.domain.WorkManagerType;
import com.oracle.xmlns.weblogic.security.AuthenticationProviderType;
import com.oracle.xmlns.weblogic.security.RealmType;
import com.oracle.xmlns.weblogic.weblogicDiagnostics.WldfResourceDocument;

/**
 * Implementation of the ObjectMother pattern, 
 * provides helper functions for unit testing by creating content for operations.
 */
public class ObjectMotherContent
{

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );    

    /**
     * Create domain document with no domain.
     * 
     * @return domain document with no domain.
     */
    public DomainDocument createEmptyDomain() {
        
        DomainDocument domainDoc = DomainDocument.Factory.newInstance();
        
        return domainDoc;        
    }

    /**
     * Create domain document with a single named domain.
     * 
     * @param name Name of the domain.
     * 
     * @return domain document with a single named domain.
     */
    public DomainDocument createMinimalDomain(String name ) {
        
        DomainDocument domainDoc = DomainDocument.Factory.newInstance();
        domainDoc.addNewDomain();
        domainDoc.getDomain().setName( name );        
        return domainDoc;        
    }

    /**
     * Create WLDF resource document;
     * 
     * @param name Name of the resource.
     * 
     * @return WLDF resource document with a single named WLDF resource..
     */
    public WldfResourceDocument createMinimalWldfResource(String name ) {
        
    	WldfResourceDocument wldfResourceDoc = WldfResourceDocument.Factory.newInstance();
    	wldfResourceDoc.addNewWldfResource();
    	wldfResourceDoc.getWldfResource().setName(name);        
        return wldfResourceDoc;        
    }
    
    /**
     * Create domain document 
     * with object: machine.
     * 
     * @param domainObjName Name of the domain.
     * @param domainObjName Name of the machine. 
     * 
     * @return domain document with a single named machine.
     */    
    public DomainDocument createDomainWithObjectMachine( String domain, String machine )
    {
        DomainDocument domainDoc = createMinimalDomain( domain );
        MachineType machineObject = domainDoc.getDomain().addNewMachine();
        machineObject.setName( machine );
                
        return domainDoc;
    }

    /**
     * Create domain document with object: virtual host.
     * 
     * @param domain Name of the domain.
     * @param vhName Name of the virtual host. 
     * 
     * @return domain document with a single named virtual host.
     */    
    public DomainDocument createDomainWithVirtualHost( String domain, String vhName )
    {
        DomainDocument domainDoc = createMinimalDomain( domain );
        VirtualHostType vhObject = domainDoc.getDomain().addNewVirtualHost();
        vhObject.setName( vhName);                
        return domainDoc;
    }    

    /**
     * Create domain document with WLDF system resource.
     * 
     * @param domain Name of the domain.
     * @param wldfResourceName Name of the WLDF system resource. 
     * 
     * @return domain document with a single named WLDF system resource.
     */        
	public Object createDomainWithWldfsystemResource(String domain ,String wldfResourceName) {
        DomainDocument domainDoc = createMinimalDomain( domain );
        WldfSystemResourceType wldfResource = domainDoc.getDomain().addNewWldfSystemResource();
        wldfResource.setName(wldfResourceName);
        return domainDoc;
	}    
    
    
    /**
     * Create domain document with object: virtual host
     * and setting the virtual host names attribute.
     * 
     * @param domainObjName Name of the domain.
     * @param vhName Name of the virtual host. 
     * 
     * @return domain document with a single named virtual host.
     */    
    public DomainDocument createDomainWithVirtualHostAndVHNames( String domain, String vhName, String[] names )
    {
        DomainDocument domainDoc = createMinimalDomain( domain );
        VirtualHostType vhObject = domainDoc.getDomain().addNewVirtualHost();
        vhObject.setName( vhName);                
        vhObject.setVirtualHostNameArray(names);
        return domainDoc;
    }
    
    
    
    /**
     * Create domain document 
     * with a primitive attribute: Notes.
     * 
     * @param domainObjName Name of the domain.
     * @param notes Content of the notes. 
     * 
     * @return domain document with content in the Notes attribute.
     */    
    public DomainDocument createDomainWithPrimitiveNotes( String domain, String notes )
    {
        DomainDocument domainDoc = createMinimalDomain( domain );
        DomainType domainObject = domainDoc.getDomain();
        domainObject.setNotes(notes);
        return domainDoc;
    }
    
    /**
     * Create domain document 
     * with a primitive attribute: ConfigurationAuditType.
     * 
     * @param domainObjName Name of the domain. 
     * @param configurationAuditType audit criteria.
     * 
     * @return domain document with content in the Notes attribute.
     */    
    public DomainDocument createDomainWithEnumConfigurationAuditType( String domain, Enum configurationAuditType )
    {
        DomainDocument domainDoc = createMinimalDomain( domain );
        DomainType domainObject = domainDoc.getDomain();
        domainObject.setConfigurationAuditType(configurationAuditType);
        return domainDoc;
    }

    /**
     * Create domain document with object: Machine.
     * 
     * @param domain Name of the domain.
     * @param machineName Name of the machine. 
     * 
     * @return domain document with a single named Machine.
     */    
    public DomainDocument createDomainWithMachine( String domain, String machineName )
    {
        DomainDocument domainDoc = createMinimalDomain( domain );
        MachineType machineObject = domainDoc.getDomain().addNewMachine();
        machineObject.setName( machineName );                
        return domainDoc;
    }    

    /**
     * Create domain document with objects: Machine and NodeManager
     * 
     * @param domain Name of the domain.
     * @param machineName Name of the machine. 
     * 
     * @return domain document with a single named Machine.
     */    
    public DomainDocument createDomainWithMachineAndNodeManager( String domain, String machineName )
    {    	
        DomainDocument domainDoc = createMinimalDomain( domain );
        MachineType machineObject = domainDoc.getDomain().addNewMachine();
        machineObject.setName( machineName );
        NodeManagerType nodeManagerObject = machineObject.addNewNodeManager();
        nodeManagerObject.setName(machineName);
        return domainDoc;
    }    

    /**
     * Create domain document with objects: Machine and NodeManager
     * 
     * The listenAddress attribute is set.
     * 
     * @param domain Name of the domain.
     * @param machineName Name of the machine.
     * @param listenAddress NodeManager Listen address. 
     * 
     * @return domain document with a single named Machine.
     */    
    public DomainDocument createDomainWithMachineAndNodeManagerWithAttribute( String domain, String machineName, String listenAddress )
    {    	
        DomainDocument domainDoc = createMinimalDomain( domain );
        MachineType machineObject = domainDoc.getDomain().addNewMachine();
        machineObject.setName( machineName );
        NodeManagerType nodeManagerObject = machineObject.addNewNodeManager();
        nodeManagerObject.setName(machineName);
        nodeManagerObject.setListenAddress(listenAddress);
        return domainDoc;
    }    

    /**
     * Create domain document with objects: WorkManager and ShutdownTrigger.
     * 
     * The trigger will have the same name as the work manager.
     * 
     * @param domain Name of the domain.
     * @param workManagerName Name of the WorkManager. 
     * 
     * @return domain document with a single named Machine.
     */    
    public DomainDocument createDomainWithWorkManagerAndShutdownTrigger( String domain, String workManagerName )
    {    	
        DomainDocument domainDoc = createMinimalDomain( domain );
        SelfTuningType selfTuningObject = domainDoc.getDomain().addNewSelfTuning();
        WorkManagerType workManagerObject = selfTuningObject.addNewWorkManager();
        workManagerObject.setName(workManagerName);
        WorkManagerShutdownTriggerType trigger = workManagerObject.addNewWorkManagerShutdownTrigger();
        trigger.setName(workManagerName);

        return domainDoc;
    }    

    /**
     * Create domain document with objects: WorkManager and ShutdownTrigger
     * 
     * @param domain Name of the domain.
     * @param workManagerName Name of the WorkManager.
     * @param triggerName Name of the Shutdown Trigger.  
     * 
     * @return domain document with a single named Machine.
     */    
    public DomainDocument createDomainWithWorkManagerAndShutdownTrigger( String domain, String workManagerName, String triggerName )
    {    	
        DomainDocument domainDoc = createMinimalDomain( domain );
        SelfTuningType selfTuningObject = domainDoc.getDomain().addNewSelfTuning();
        WorkManagerType workManagerObject = selfTuningObject.addNewWorkManager();
        workManagerObject.setName(workManagerName);
        WorkManagerShutdownTriggerType trigger = workManagerObject.addNewWorkManagerShutdownTrigger();
        trigger.setName(triggerName);

        return domainDoc;
    }    
    
    /**
     * Create domain document with objects: WorkManager. 
     * 
     * The listenAddress attribute is set.
     * 
     * @param domain Name of the domain.
     * @param workManagerName Name of the WorkManager. 
     * 
     * @return domain document with a single named Machine.
     */    
    public DomainDocument createDomainWithWorkManager( String domain, String workManagerName )
    {    	
        DomainDocument domainDoc = createMinimalDomain( domain );
        SelfTuningType selfTuningObject = domainDoc.getDomain().addNewSelfTuning();
        WorkManagerType workManagerObject = selfTuningObject.addNewWorkManager();
        workManagerObject.setName(workManagerName);
        return domainDoc;
    }    
    
	/**
	 * Load domain from file.
	 * 
	 * @param fileName File name to load from.
	 * @return loaded domain document.
	 * 
	 * @throws Exception If domain loading fails.
	 */
	public Object loadDomainfromFile( String fileName ) throws Exception
	
	{
		URL resource = this.getClass().getResource( fileName );		
		return DomainDocument.Factory.parse( resource );
	}

	/**
	 * Get schema property from for an attribute.
	 *    
	 * @param attributeName Name of attribute on XMLBean whose schema property should be retrieved. 
	 * @param schemaType Schema type for XMLBean.
	 * 
	 * @return schema property from for an attribute.
	 */
    public SchemaProperty getSchemaPropertyFromParentSchemaType( String attributeName, SchemaType schemaType )
    {
        // get schema properties (elements and attributes)
        SchemaProperty[] schemaProperties = schemaType.getProperties();

        // iterate to get schema property
        for ( SchemaProperty property : schemaProperties )
        {
            if ( property.getJavaPropertyName().equals( attributeName ) )
            {
                return property;
            }
        }

        // create error message and fail test
        StringBuilder message = new StringBuilder();
        message.append("failed to get schema propert for attribute <");
        message.append( attributeName );
        message.append("> from XMLBeans schema type <");
        message.append( schemaType );
        message.append(">.");        
        fail(message.toString());
        return null;
    }    

	/**
	 * Get schema property from for an attribute.
	 *    
	 * @param attributeName Name of attribute on XMLBean whose schema property should be retrieved. 
	 * @param schemaProperty Schema property for XMLBean.
	 * 
	 * @return schema property from for an attribute.
	 */
    
    public SchemaProperty getSchemaPropertyFromParentSchemaProperty( String attributeName, SchemaProperty schemaProperty )
    {
    	return getSchemaPropertyFromParentSchemaType(attributeName, schemaProperty.getType());
    }        
	
    /**
     * Create resolved participant which contains domain XMLBean.
     *  
     * @param domainDoc Domain document.
     * 
     * @return resolved participant which contains domain XMLBean.
     */
	public ResolvedParticipant createDomainResolvedParticipant(DomainDocument domainDoc ) {
		
        String domainName = domainDoc.getDomain().getName();
        
		// get root object in XML Beans model
		DomainType domainXmlBean = domainDoc.getDomain();
		SchemaProperty domainProperty = getSchemaPropertyFromParentSchemaType("Domain", domainDoc.schemaType() );                        

		// create participant
		return ResolvedParticipantImpl.createSuccessfulResult( domainName, domainProperty, domainXmlBean );
	}    

    /**
     * Create resolved participant which contains security realm.
     *  
     * @param realmName Realm name.
     * 
     * @return resolved participant which contains security realm XMLBean.
     */
	public ResolvedParticipant createRealmResolvedParticipant(String realmName ) {
		
        // build domain		
        DomainDocument domainDoc = createMinimalDomain( WJPIntTestConstants.targetEnvironment );
        
        // build realm
        SecurityConfigurationType securityConfiguration = domainDoc.getDomain().addNewSecurityConfiguration();
        RealmType realm = securityConfiguration.addNewRealm();
        realm.setName( realmName );    
        
		// get realm schema type
		SchemaProperty domainProperty = getSchemaPropertyFromParentSchemaType("Domain", domainDoc.schemaType() );
		SchemaProperty securityConfigProperty = getSchemaPropertyFromParentSchemaProperty("SecurityConfiguration", domainProperty );	
		SchemaProperty realmProperty = getSchemaPropertyFromParentSchemaProperty("Realm", securityConfigProperty );		

		// create participant
		return ResolvedParticipantImpl.createSuccessfulResult( realmName, realmProperty, realm );
	}    

    /**
     * Create resolved participant which contains authentication providers collection
     * with a single authentication provider.
     *  
     * @param providerNames Array of provider names. If empty null then no providers is added to collection
     * If array contains null then null name is added to provider.
     * 
     * @param realmName Realm name. 
     * 
     * @return resolved participant which contains authentication providers collection.
     */
	public ResolvedParticipant createAuthenticationProviderCollectionResolvedParticipant(String[] providerNames, String realmName ) {
		
		final String ATTRIBUTE_NAME = "AuthenticationProvider";
		
        // build domain		
        DomainDocument domainDoc = createMinimalDomain( WJPIntTestConstants.targetEnvironment );
        
        // build realm
        SecurityConfigurationType securityConfiguration = domainDoc.getDomain().addNewSecurityConfiguration();
        RealmType realm = securityConfiguration.addNewRealm();
        realm.setName( realmName );
        
        if (providerNames != null ) {
        	
        	for (String name : providerNames) {
        		
        		// create provider
        		AuthenticationProviderType provider = realm.addNewAuthenticationProvider();
                // set name
        		
                if( name != null ) {
                	provider.setName(name);
                }        		
        	}
        }
                
        // get providers
        AuthenticationProviderType[] providers = realm.getAuthenticationProviderArray();
        
		// get realm schema type
		SchemaProperty domainProperty = getSchemaPropertyFromParentSchemaType("Domain", domainDoc.schemaType() );
		SchemaProperty securityConfigProperty = getSchemaPropertyFromParentSchemaProperty("SecurityConfiguration", domainProperty );	
		SchemaProperty realmProperty = getSchemaPropertyFromParentSchemaProperty("Realm", securityConfigProperty );		
		SchemaProperty providerProperty = getSchemaPropertyFromParentSchemaProperty(ATTRIBUTE_NAME, realmProperty );

		// create participant
		return ResolvedParticipantImpl.createSuccessfulResult( ATTRIBUTE_NAME, providerProperty, providers );
	}

		
}
