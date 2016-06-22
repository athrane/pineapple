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


package com.alpha.pineapple.plugin.weblogic.jmx.command;

import javax.annotation.Resource;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.management.JMXManagmentException;
import com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.ResolvedModelJmxGetter;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolutionFailedException;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolver;
/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface which sets 
 * an attribute on a MBean.
 * </p>
 *
 * <p>
 * Precondition for execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>resolved-type</code> defines the resolved type to create to MBean from. The type is
 * <code>com.alpha.pineapple.resolvedmodel.ResolvedType</code>.</li> 
 *
 * <li><code>jmx-session</code> defines WebLogic JMX session to operate on. The type is
 * <code>com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession </code>.</li>
 *  
 * <li><code>execution-result</code> contains execution result object which collects
 * information about the execution of the test. The type is 
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>    
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the command succeeded. If the 
 * command failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the command fails due to an exception then the exception isn't caught, 
 * but passed on to the invoker whose responsibility it is to catch it and update 
 * the <code>ExecutionResult</code> with the state <code>ExecutionState.ERROR</code>.
 * </li>
 * </ul>
 * </p>
 */
public class SetMBeanAttributeCommand implements Command
{
	/**
	 * First array index.
	 */
    static final int FIRST_INDEX = 0;

	/**
     * Key used to identify property in context: Resolved type containing the values.
     */
    public static final String RESOLVED_TYPE = "resolved-type";

    /**
     * Key used to identify property in context: Contains execution result object,.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";
	
    /**
     * Key used to identify property in context: WebLogic JMX session to operate on.
     */
    public static final String JMXSESSION_KEY = "jmx-session";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;        
    
    /**
     * Resolved model JMX getter.
     */
    @Resource
    ResolvedModelJmxGetter modelJmxGetter;        

    /**
     * MBean model resolver.
     */
    @Resource
    ModelResolver mbeansObjectNameBasedModelResolver;        
        
    /**
     * JMX session.
     */
    @Initialize( JMXSESSION_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )        
    WeblogicJMXEditSession session;

    /**
     * Defines execution result object.
     */
    @Initialize( EXECUTIONRESULT_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )    
    ExecutionResult executionResult;

    /**
     * Resolved type.
     */
    @Initialize( RESOLVED_TYPE )
    @ValidateValue( ValidationPolicy.NOT_NULL )        
    ResolvedType resolvedType ;
    
    
    public boolean execute( Context context ) throws Exception
    {        
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("smbac.start") );        	
        }
        
        // initialize command
        CommandInitializer initializer = new CommandInitializerImpl();
        initializer.initialize( context, this );
        
        // set MBean attribute
        setMBeanAttribute();
        
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("smbac.completed") );        	
        }
        
        // return successful result
        return Command.CONTINUE_PROCESSING;
    }
        
    /**
     * Set MBean attribute.
     * 
     * @throws Exception If attribute retrieval fails.
     */
	void setMBeanAttribute() throws Exception {
				
		// get secondary participant
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();  

		// if secondary wasn't successfully resolved then try to resolve it		
		if (!secondary.isResolutionSuccesful()) {			
			redoAttributeResolution();
			
			// get updated secondary participant
			secondary = resolvedType.getSecondaryParticiant();
		}
				
		// validate
		if(!isSecondaryAttributeNameDefined()) return;
		if(!isParentDefined()) return;				
		if(!isParentSecondarySet()) return;								
		
		// get secondary parent value   
		Object parentSecondaryValue = resolvedType.getParent().getSecondaryParticiant().getValue();						
    	if(!isSecondaryParentValueDefined()) return;						
     	
		// validate value is object name
        session.validateIsObjectName(parentSecondaryValue);

		// type cast object name
		ObjectName objName = (ObjectName) parentSecondaryValue;
		
		// validate and get type 
		if(!isSecondaryTypeDefined(objName)) return;
        session.validateIsMBeanAttributeInfo(secondary.getType());
        MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) secondary.getType();
		        
		// get value from primary participant    	
    	Object attributeValue = resolvedType.getPrimaryParticipant().getValue();
               		        		                    							
		// convert value to type exposed by MBean attribute
		Object value = convertAttributeValue( attributeInfo, attributeValue);

		// validate whether value is ready set with the same value, then skip 
		if (isValueIsAlreadySet(value)) return;
		
		// validate MBean attribute is writable, otherwise skip setting the attribute
        if(!validateAttributeIsWritable(attributeInfo, objName)) return;
		
		// set attribute value
		setAttributeValue( objName, attributeInfo, value );
		
    	// create new secondary participant with MBean
    	ResolvedParticipant newSecondary = ResolvedParticipantImpl.createSuccessfulResult(
    			resolvedType.getSecondaryParticiant().getName(),
    			resolvedType.getSecondaryParticiant().getType(),
    			value);

    	// update resolved object
    	resolvedType.updateSecondaryParticipant(newSecondary);
				
		// set result
		Object[] args = { attributeValue, attributeInfo.getName() };
		executionResult.completeAsSuccessful(messageProvider, "smbac.succeed", args );		
	}

	
	/**
	 * Resolve secondary participant, i.e the attribute once more to obtain 
	 * meta data about the attribute, e.g. name and MBean attribute info. 
	 */
	void redoAttributeResolution() {
		
		// get secondary parent
		ResolvedType parent = resolvedType.getParent();
		ResolvedParticipant secondaryParent = parent.getSecondaryParticiant();
		
		// get attribute name
		ResolvedParticipant primary = resolvedType.getPrimaryParticipant();			
		
		// get new secondary
		ResolvedParticipant newSecondary = mbeansObjectNameBasedModelResolver.resolveAttribute(primary.getName(), secondaryParent );
		
		// update secondary
		resolvedType.updateSecondaryParticipant(newSecondary);
	}


    /**
     * Set attribute value on MBean.
     * 
     * @param objName MBean object name where the attribute is set.
     * @param info MBean attribute info. 
     * @param value The value which should be set on the MBean attribute.
     * 
     * @throws JMXManagmentException If setting attribute value on MBean fails.
     */
    void setAttributeValue( ObjectName objName, MBeanAttributeInfo info, Object value ) throws JMXManagmentException
    {
    	try {
    		session.setAttribute(objName, info, value);
        }
        catch ( Exception e )
        {
            // create error message
        	Object[] args = { value, e.getMessage() }; 
        	String message = messageProvider.getMessage("smbac.setvalue_failed", args);
            throw new JMXManagmentException( message, e );        	
        }
    }

    
	/**
	 * Convert value to type exposed by MBean attribute.
	 * 
	 * @param info Attribute info.
	 * @param value Attribute value.
	 * 
	 * @return value converted to type exposed by MBean attribute.
	 * 
	 * @throws Exception If conversion fails.
	 */
	Object convertAttributeValue(MBeanAttributeInfo info, Object value) throws Exception {
		
		// if type is object name then convert reference to object name
		if(session.isAttributeObjectNameReference(info)) {
			
			// convert to reference
			ObjectName[] objNames = session.convertReferenceToObjectNames(info, value);
			
			// get first value
			return objNames[FIRST_INDEX]; 
		}

		// if type is object name array then convert reference to object names
		if(session.isAttributeObjectNameArrayReference(info)) {
			return session.convertReferenceToObjectNames(info, value);
		}
					
		// convert simple value to type exposed by MBean attribute
		return convertSimpleAttributeValue( value, info );
	}

    /**
     * Convert attribute value to type defined by MBean attribute.
     *
     * @param value Attribute value.
     * @param attributeInfo MBean attribute meta data.
     * 
     * @return Value converted from string to the type defined by MBean attribute meta data.
     * 
     * @throws JMXManagmentException If conversion fails.
     */
    Object convertSimpleAttributeValue( Object value, MBeanAttributeInfo attributeInfo ) throws JMXManagmentException
    {    	
        // define type variable
        String mbeanTypeAsString = null;

        // exit if value is null
        if (value == null) {
        	
            // log debug message
            if ( logger.isDebugEnabled() ) {            	
            	Object[] args = { ReflectionToStringBuilder.toString(value) };
            	logger.debug( messageProvider.getMessage("smbac.convert_null_skipped", args) );        	
            }
        	        	        	
        	return value;
        }
                
        // get the type of the actual value
        Class<? extends Object> attributeType = value.getClass();
        
        try
        {        	
            // get MBean attribute type
            mbeanTypeAsString = attributeInfo.getType();

            // get MBean attribute class
            Class<? extends Object> mbeanClass = Class.forName( mbeanTypeAsString );
                     
            // get MBean type canonical name
            String mbeanTypeCanonicalName = mbeanClass.getCanonicalName();

            // handle null case
            if (attributeType != null) {
            	
                // get attribute type canonical name
                String attributeCanonicalName = attributeType.getCanonicalName();

                // skip conversion if type are equal
                if(mbeanTypeCanonicalName.equals(attributeCanonicalName) ) {            	
                	
                    // log debug message
                    if ( logger.isDebugEnabled() ) 
                    {            	
                    	Object[] args = { ReflectionToStringBuilder.toString(value) };
                    	logger.debug( messageProvider.getMessage("smbac.convert_value_skipped", args) );        	
                    }
                	
                	return value;
                }                
            }
                                                
            // convert value to string
            String valueAsStr = ConvertUtils.convert(value);
            
            // convert string based value to MBean type  
            Object valueAsObj = ConvertUtils.convert(valueAsStr, mbeanClass);
                        
            // log debug message
            if ( logger.isDebugEnabled() ) 
            {            	
            	Object[] args = { ReflectionToStringBuilder.toString(value), ReflectionToStringBuilder.toString(valueAsObj)};
            	logger.debug( messageProvider.getMessage("smbac.convert_value_info", args) );        	
            }
            
            return valueAsObj;            
        }
        catch ( ClassNotFoundException e )
        {
            // create error message
        	Object[] args = { value, mbeanTypeAsString }; 
        	String message = messageProvider.getMessage("smbac.convertvalue_failed", args);
            throw new JMXManagmentException( message );
        }
    }

	/**
	 * Validate attribute name is defined in the secondary participant name.
	 */
	boolean isSecondaryAttributeNameDefined() {
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();		
		if ( secondary.getName() != null ) return true;
			
		Object[] args = { secondary.getValueAsSingleLineString() };
		executionResult.completeAsFailure(messageProvider, "smbac.attribute_name_null_failed", args );
		return false;
	}
	
	/**
	 * Validate parent is defined.
	 */	
	boolean isParentDefined() {
		ResolvedType parent = resolvedType.getParent();		
		if ( parent != null ) return true;

		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();				
		Object[] args = { secondary.getName() };
		executionResult.completeAsFailure(messageProvider, "smbac.parent_null_failed", args );
		return false;
	}

	/**
	 * Validate parent secondary value is set.   
	 */		
	boolean isParentSecondarySet() {
		ResolvedType parent = resolvedType.getParent();
		ResolvedParticipant parentSecondary = parent.getSecondaryParticiant();		
		if ( parentSecondary.getValueState() == ResolvedParticipant.ValueState.SET) return true;
		
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();		
		Object[] args = { secondary.getValueAsSingleLineString() };
		executionResult.completeAsFailure(messageProvider, "smbac.parent_secondary_notset_failed", args );
		return false;
	}

	/**
	 * Validate parent secondary value is defined.  
	 */			
	boolean isSecondaryParentValueDefined() {
		ResolvedType parent = resolvedType.getParent();
		ResolvedParticipant parentSecondary = parent.getSecondaryParticiant();				
		Object parentSecondaryValue = parentSecondary.getValue();								
		if ( parentSecondaryValue != null ) return true;

		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();				
		Object[] args = { secondary.getName() };
		executionResult.completeAsFailure(messageProvider, "smbac.objectname_null_failed", args );
		return false;
	}
	
	/**
	 * Validate secondary type is defined.
	 */
	boolean isSecondaryTypeDefined(ObjectName objName) {
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();
		if ( secondary.getType() != null ) return true;
		
		Object[] args = { secondary.getName(), objName };
		executionResult.completeAsFailure(messageProvider, "smbac.mbean_attribute_resolution_failed", args );
		return false;
	}

	/**
	 * Validate MBean attribute is writable.
	 */	
	boolean validateAttributeIsWritable(MBeanAttributeInfo attributeInfo, ObjectName objName) throws ModelResolutionFailedException {

		// exit if attribute is writable
        if ( attributeInfo.isWritable() ) return true;

        Object[] args = { attributeInfo.getName(), objName };
		executionResult.completeAsFailure(messageProvider, "smbac.mbean_attribute_writable_validation_failed", args );
		return false;
	}

	/**
	 * Validate whether value is already set, comparing the two values. 
	 * 
	 * If value is set then the method updates the execution result with a
	 * successful execution      
	 */
	boolean isValueIsAlreadySet(Object value) {
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();

		// exit if secondary isn't set
		if(!secondary.getValueState().equals(ResolvedParticipant.ValueState.SET)) return false;
		
		// handle null case
		if(secondary.getValue() == null) {
			
			// exit if attribute isn't set		
			if(value == null ) return true;
			
			// exit if both are null
			return false;
		}
						
		// exit if attribute isn't set		
		if(!secondary.getValue().equals(value)) return false;

		// get value from primary participant    	
    	Object attributeValue = resolvedType.getPrimaryParticipant().getValue();
		
		// set result and exit
		Object[] args = { attributeValue, secondary.getName()};
		executionResult.completeAsSuccessful(messageProvider, "smbac.attribute_already_set", args );
		return true;
				
	}
	
}
