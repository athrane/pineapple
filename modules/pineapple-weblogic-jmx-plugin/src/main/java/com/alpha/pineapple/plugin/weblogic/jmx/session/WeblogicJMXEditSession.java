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


package com.alpha.pineapple.plugin.weblogic.jmx.session;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ObjectName;

import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;
import com.alpha.pineapple.resolvedmodel.validation.ModelValidationFailedException;

/**
 * A WebLogic JMX edit session represents a JMX connection to a WebLogic Edit MBean server which is used to manage the
 * configuration of a WebLogic domain.
 */
public interface WeblogicJMXEditSession extends JMXSession
{

    /**
     * Initialize edit session for connected JMX session.
     * 
     * @throws Exception If...
     */
    public void startEdit() throws Exception;

    
    /**
     * Save and activate changes.
     * 
     * @throws Exception If....
     */
    public void saveAndActivate() throws Exception;

    
    /**
     * Returns true if the JMX session has an active edit session with the WebLogic server.
     * 
     * @return True if the JMX session has an active edit session with the WebLogic server.
     */
    public boolean isEditSessionActive();
    
    
    /**
     * Returns object name for domain MBean.
     * 
     * @return Returns object name for domain MBean.
     */
    public ObjectName getDomainMBeanObjectName();

	/**
	 * Get MBean info for MBean.
	 * 
	 * @param objName MBean object name.
	 * 
	 * @return MBean info for MBean.
	 * 
	 * @throws Exception If MBean info retrieval fails. 
	 */
	public MBeanInfo getMBeanInfo(ObjectName objName) throws Exception;
        
    /**
     * Find attribute info for MBean attribute. If the attribute is defined 
     * then an {@link MBeanAttributeInfo} object is returned. Otherwise the 
     * method returns null.
     *   
     * @param objName MBean object name which is searched for attributes.
     * @param attributeName Name of the MBean attribute.  
     * 
     * @return Return an {@link MBeanAttributeInfo} object if the attribute is found. 
     * Otherwise the method returns null.
     * 
     * @throws Exception If attribute retrieval fails.
     */    
    public MBeanAttributeInfo getAttributeInfo(ObjectName objName, String attributeName ) throws Exception;
    
    
    /**
     * Find MBean in current MBEean server.
     * 
     * @param type The MBean type.
     * @param name The name of MBean.
     * 
     * @return a Object name for the found MBean.
     * 
     * @throws Exception if query fails.
     */	
    public ObjectName findMBean(String type, String name ) throws Exception;

    
    /**
     * Get attribute value on MBean.
     * 
     * @param objName MBean object name which is searched for attributes.
     * @param attributeID Name of the MBean attribute.  
 
     * @throws Exception If getting attribute value from MBean fails.
     */
    public Object getAttribute( ObjectName objName, String attributeID ) throws Exception;
    
    /**
     * Set attribute value on MBean.
     * 
     * @param objName MBean object name which is contains the attribute.
     * @param info MBean attribute info.
     * @param value Attribute value.  
 
     * @throws Exception If setting attribute on MBean fails.
     */
	public void setAttribute(ObjectName objName, MBeanAttributeInfo attributeInfo, Object value) throws Exception;
    
    /**
     * Return the value of the attribute named "Name" from a MBean.
     * 
     * @param objName The MBean object to query.
     * 
     * @return Value of the attribute named "Name" from the MBean.
     * 
     * @throws Exception If attribute value retrieval fails.
     */    
    public String getNameAttributeFromObjName( ObjectName objName ) throws Exception;    
    
    
    
	/**
	 * Convert attribute value(s) which is a list to referenced object names of specified type.
	 * 
	 * @param info MBean attributeInfo info. 
	 * @param value Values which represents a list of server names.
	 * 
	 * @return array of referenced object names.
	 * 
	 * @throws Exception If conversion fails.
	 */
	public ObjectName[] convertReferenceToObjectNames(MBeanAttributeInfo info, Object value) throws Exception; 
    
	
    /**
     * Invoke MBean method.
     * 
     * @param objName Object name for MBean on which the method is invoked.
     * @param operationInfo MBean operation info for method to be invoked.
     * @param params Method parameters.  
     *  
     * @return result of method invocation.
     * 
     * @throws Exception If invocation fails.
     */
    public Object invokeMethod(ObjectName objName, MBeanOperationInfo operationInfo, Object[] params ) throws Exception;
    
    
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
	 *        
	 * @param objName Object name for MBean Bean where the method should be resolved on.
	 * @param attributeName Name of the attribute which should be resolved.
	 *  
	 * @return MBean operation info.
	 * 
     * @throws Exception if resolution fails.  
	 */	
	public MBeanOperationInfo getCreateMethod(ObjectName objName, String attributeName) throws Exception;
    

	/**
	 * Resolve MBean destructor method for attribute.
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
	 *        
	 * @param objName Object name for MBean Bean where the method should be resolved on.
	 * @param attributeName Name of the attribute which should be resolved.
	 *  
	 * @return MBean operation info.
	 * 
     * @throws Exception if resolution fails.  
	 */	
	public MBeanOperationInfo getDeleteMethod(ObjectName objName, String attributeName) throws Exception;
	
	
	/**
	 * Validate that attribute info is defined. 
	 * 
	 * @param attributeInfo MBean attribute info which is validated.
	 * @param attributeName Attribute name used for exception.
	 * @param objName Object name used for exception.
	 * 
	 * @throws ModelResolutionFailedException if validation fails.
	 */
    public void validateAttributeInfoIsDefined(MBeanAttributeInfo attributeInfo, String attributeName, ObjectName objName) throws ModelResolutionFailedException;
	
    
	/**
	 * Validate that attribute is readable. 
	 * 
	 * @param attributeInfo MBean attribute info which is validated.
	 * @param objName Object name used for exception.
	 * 
	 * @throws ModelResolutionFailedException if validation fails.
	 */
    public void validateAttributeIsReadable(MBeanAttributeInfo attributeInfo, ObjectName objName) throws ModelResolutionFailedException;
       
	
	/**
	 * Validate that object is of type object name. 
	 * 
	 * @param value Object whose type is validated.
	 * 
	 * @throws ModelResolutionFailedException if validation fails.
	 */    
    public void validateIsObjectName(Object value) throws ModelValidationFailedException;

    
	/**
	 * Validate that object is of type MBeanAttributeInfo 
	 * 
	 * @param value Object whose type is validated.
	 * 
	 * @throws ModelResolutionFailedException if validation fails.
	 */    
    public void validateIsMBeanAttributeInfo(Object value) throws ModelValidationFailedException;

    /**
     * Returns true if attribute type is object name.  
     * 
     * @param info MBean attribute info.
     * 
     * @return true if attribute type is object name, e.g. "javax.management.ObjectName".
     */
	public boolean isAttributeObjectNameReference(MBeanAttributeInfo info);

    /**
     * Returns true if attribute type is object name array.  
     * 
     * @param info MBean attribute info.
     * 
     * @return true if attribute type is object name array, e.g. "[Ljavax.management.ObjectName;".
     */
	public boolean isAttributeObjectNameArrayReference(MBeanAttributeInfo info);
	
}
