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
import javax.management.MBeanOperationInfo;
import javax.management.ObjectName;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.ResolvedModelJmxGetter;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedCollection;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedType;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface 
 * which creates a new MBean instance in an WebLogic Edit MBean Server. 
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>resolved-type</code> defines the resolved type to create to MBean from. The type is
 * <code>com.alpha.pineapple.resolvedmodel.ResolvedType</code>.</li> 
 *
 * <li><code>jmx-session</code> defines WebLogic JMX Edit session to operate on. The type is
 * <code>com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession </code>.</li>
 *  
 * <li><code>execution-result</code> contains execution result object which collects
 * information about the execution of the command. The type is 
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>    
 * </ul>
 * </p>
 * 
 * <p>Postcondition after execution of the command is: 
 * <ul> 
 * <li>If the creation of the MBean was successful then secondary participant is replaced
 * with a new one whose resolution state is successful and contains the object name of the
 * created MBean as value.</li>
 * 
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the command succeeded. If the 
 * command failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the command fails due to an exception then the exception isn't caught, 
 * but passed on the the invoker whose responsibility it is to catch it and update 
 * the <code>ExecutionResult</code> with the state <code>ExecutionState.ERROR</code>.
 * </li>
 * </ul>  
 * </p>         
 */
public class CreateMBeanCommand implements Command
{
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
     * Resolved model JMX getter..
     */
    @Resource
    ResolvedModelJmxGetter modelJmxGetter;        
        
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

    /** 
     * Object name for create MBean.
     */
    ObjectName objName;
    
	public boolean execute( Context context ) throws Exception
    {
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("cmbc.start") );        	
        }
    	
        // initialize command
        CommandInitializer initializer =  new CommandInitializerImpl();
        initializer.initialize( context, this );
            	
        // create MBean 
        createMBean();
        
	    // log debug message
	    if ( logger.isDebugEnabled() ) 
	    {
	    	logger.debug( messageProvider.getMessage("cmbc.completed") );        	
	    }
	                    
		return Command.CONTINUE_PROCESSING;
    }

    /**
     * Create MBean.
     * 
     * @throws Exception If MBean creation fails.
     */
	void createMBean() throws Exception {
		
		// clean object name
		this.objName = null;
		
		// get secondary participant
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();            
		
		// validate whether MBean already exists
		if ( secondary.getValueState() == ResolvedParticipant.ValueState.SET) {

			// get secondary value 
			Object secondaryValue = secondary.getValue();		

			// validate value is of type object name
			session.validateIsObjectName(secondaryValue);
															
			// set result and exit
			Object[] args = { secondary.getValueAsSingleLineString() };
			executionResult.completeAsSuccessful(messageProvider, "cmbc.bean_already_exists", args );
			return;
		}	
				
		// validate
		if(!isSecondaryNameIsDefined()) return;		
		if(!isSecondaryTypeIsDefined()) return;           		
		if(!isParentIsDefined()) return;			
		if(!isGrandParentIsDefinedForCollection()) return;				
		if(!isParentSecondaryIsSet()) return;						
		
		// get parent object name
		// if parent is collection then it resolved grand parent object name
		ObjectName parentObjectName = modelJmxGetter.resolveParentObjectName( resolvedType );
								
		// fail if parent object name is undefined
		if ( parentObjectName == null ) {
			Object[] args = { secondary.getName() };
			executionResult.completeAsFailure(messageProvider, "cmbc.parent_objectname_null_failed", args );
			return;
		}		
		
		// validate parent type is object name 
		session.validateIsObjectName(parentObjectName);
		
		// get type info
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) secondary.getType();
				
		// get factory method for attribute
		MBeanOperationInfo operationInfo = session.getCreateMethod(parentObjectName, attributeInfo.getName());
				
		// validate parent parent MBean contain factory method
		if ( operationInfo == null ) {
			
			// set result
			Object[] args = { secondary.getName(), parentObjectName, attributeInfo.getName() };
			executionResult.completeAsFailure(messageProvider, "cmbc.factorymethod_resolution_failed", args );
			
			// exit
			return;
		}						

		// declare param
		Object[] params = null; 
		
		// setup arguments 
		switch(operationInfo.getSignature().length) {
			
			case 0:
				params = new Object[] { };
				break;
			case 1:
		        params = new Object[] { secondary.getName() };
				break;		        
			default:
				
				// fail command since it doesn't support creator methods with 1+ arguments.
				Object[] args = { secondary.getName() }; 
				executionResult.completeAsFailure(messageProvider, "cmbc.factorymethod_unsupported_failed", args );
				
				// exit
				return;				
		}
		
		// invoke creator method
		objName = (ObjectName) session.invokeMethod(parentObjectName, operationInfo, params );
                
    	// create new secondary participant with MBean
    	ResolvedParticipant newSecondary = ResolvedParticipantImpl.createSuccessfulResult(
    			resolvedType.getSecondaryParticiant().getName(),
    			resolvedType.getSecondaryParticiant().getType(),
    			objName);

    	// update resolved object
    	resolvedType.updateSecondaryParticipant(newSecondary);
		
		// set result
		Object[] args = { objName };
		executionResult.completeAsSuccessful(messageProvider, "cmbc.succeed", args );				
	}
	
	
	/**
	 * Validate secondary name is defined.
	 */
	boolean isSecondaryNameIsDefined() {
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();            
		if ( secondary.getName() != null ) return true;
		
		Object[] args = { secondary.getValueAsSingleLineString() };
		executionResult.completeAsFailure(messageProvider, "cmbc.mbean_name_null_failed", args );
		return false;
	}

	/**
	 * Validate secondary type is defined.
	 */	
	boolean isSecondaryTypeIsDefined() {
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();		
		if ( secondary.getType() != null ) return true;
		
		Object[] args = { secondary.getName() };
		executionResult.completeAsFailure(messageProvider, "cmbc.mbean_type_null_failed", args );
		return false;		
	}

	/**
	 * Validate parent is defined.
	 */		
	boolean isParentIsDefined() {
		ResolvedType parent = resolvedType.getParent();		
		if ( parent != null ) return true;

		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();				
		Object[] args = { secondary.getValueAsSingleLineString() };
		executionResult.completeAsFailure(messageProvider, "cmbc.parent_null_failed", args );
		return false;
	}


	/**
	 * If parent is a resolved collection then validate grand parent is defined.
	 */
	boolean isGrandParentIsDefinedForCollection() {				
		ResolvedType parent = resolvedType.getParent();		
		if (!( parent instanceof ResolvedCollection )) return true;
		
		// get grand parent 
		ResolvedType grandParent = parent.getParent();
			
		// fail if grand parent is undefined
		if ( grandParent != null ) return true;
		
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();						
		Object[] args = { secondary.getValueAsSingleLineString() };
		executionResult.completeAsFailure(messageProvider, "cmbc.grandparent_null_failed", args );
		return false;
	}

	
	/**
	 * Validate parent secondary is set. 
	 */
	boolean isParentSecondaryIsSet() {
		ResolvedParticipant secondaryParent = resolvedType.getParent().getSecondaryParticiant();				
		if ( secondaryParent.getValueState() == ResolvedParticipant.ValueState.SET) return true;

		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();								
		Object[] args = { secondary.getValueAsSingleLineString() };
		executionResult.completeAsFailure(messageProvider, "cmbc.parent_secondary_notset_failed", args );
		return false;
	}
	
}
