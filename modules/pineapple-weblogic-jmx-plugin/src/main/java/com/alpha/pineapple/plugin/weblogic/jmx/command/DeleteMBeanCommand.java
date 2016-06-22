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
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
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
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedType;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface 
 * which deletes a MBean instance in an WebLogic Edit MBean Server. 
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>resolved-type</code> defines the resolved type to delete MBean from. The type is
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
public class DeleteMBeanCommand implements Command
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
     * JMX session.
     */
    @Initialize( JMXSESSION_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )        
    WeblogicJMXEditSession jmxSession;

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
        	logger.debug( messageProvider.getMessage("dmbc.start") );        	
        }
    	
        // initialize command
        CommandInitializer initializer =  new CommandInitializerImpl();
        initializer.initialize( context, this );
            	
        try
        {
            // delete MBean 
            deleteMBean();
            
		    // log debug message
		    if ( logger.isDebugEnabled() ) 
		    {
		    	logger.debug( messageProvider.getMessage("dmbc.completed") );        	
		    }
		                    
			return Command.CONTINUE_PROCESSING;
        }
        catch ( Exception e )
        {
            // complete with error
			executionResult.completeAsError(messageProvider, "dmbc.error", e);
			
			return Command.CONTINUE_PROCESSING;
        }

    }

    /**
     * Delete MBean.
     * 
     * @throws Exception If MBean creation fails.
     */
	void deleteMBean() throws Exception {
		
		// get secondary participant
		ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();            
				
		// validate whether MBean already exists
		if ( secondary.getValueState() == ResolvedParticipant.ValueState.FAILED) {
			            	            
			// set result
			Object[] args = { secondary.getValueAsSingleLineString() };
			executionResult.completeAsSuccessful(messageProvider, "dmbc.bean_doesnt_exists", args );
			
			// exit
			return;
		}
		
		// validate MBean name is defined
		if ( secondary.getName() == null ) {
			            	            
			// set result
			Object[] args = { secondary.getValueAsSingleLineString() };
			executionResult.completeAsFailure(messageProvider, "dmbc.mbean_name_null_failed", args );
			
			// exit
			return;			
		}
		
		// validate whether MBean type is defined
		if ( secondary.getType() == null ) {
			            	            
			// set result
			Object[] args = { secondary.getValueAsSingleLineString() };
			executionResult.completeAsFailure(messageProvider, "dmbc.mbean_type_null_failed", args );
			
			// exit
			return;
		}            		
		
		// get JMX connection
		MBeanServerConnection connection = jmxSession.getMBeanServerConnection();

		// get domain object name
		ObjectName domainObjectName = jmxSession.getDomainMBeanObjectName();
		logger.debug("DEBUG domainObjectName:" + domainObjectName );
				
		// Validate domain object name is defined 
		if ( domainObjectName == null ) {
			            	            
			// set result
			Object[] args = { secondary.getValueAsSingleLineString() };
			executionResult.completeAsFailure(messageProvider, "dmbc.domainobject_null_failed", args );
			
			// exit
			return;			
		}                                    

        // get object name
		ObjectName objName = (ObjectName) secondary.getValue(); 
		logger.debug("DEBUG objName:" + objName );

		// validate secondary value is object name
		
		// get operation id 		
		String mbeanType = objName.getKeyProperty("Type");
        String operationId = "destroy"+mbeanType;
		logger.debug("DEBUG operationId:" + operationId );        		                
		
		// find meta data for operation
		MBeanOperationInfo operationInfo = findOperationInfo( connection, objName, operationId );				
		String operationName = operationInfo.getName();
		logger.debug("DEBUG operationName:" + operationName  );
		MBeanParameterInfo[] parameterInfo = operationInfo.getSignature();
		String parameterType = parameterInfo[0].getType();
		logger.debug("DEBUG parameterType:" + parameterType );
		
		// MBean name
		String mbeanName = secondary.getName();
		logger.debug("DEBUG mbeanName:" + mbeanName );		
		
		Object[] params = {  };
		String[] signature = { parameterType };
		connection.invoke(domainObjectName,  operationName, params, signature);						
	}

    /**
     * Find operation info for MBean operation. If the operation is defined 
     * then an {@link MBeanOperationInfo} object is returned. Otherwise the 
     * method returns null.
     * 
     * @param connection MBean server connection object.  
     * @param objName MBean object name which is searched for operations.
     * @param operationId Name of the MBean operation.  
     * 
     * @return Return an {@link MBeanOperationInfo} object if the operation is found. 
     * Otherwise the method returns null.
     * 
     * @throws Exception If operation retrieval fails.
     */
    MBeanOperationInfo findOperationInfo( MBeanServerConnection connection, ObjectName objName, String operationId ) throws Exception
    {    	        
		// get MBean meta data
        MBeanInfo mbeanInfo = connection.getMBeanInfo( objName );

        // get operation info's
        MBeanOperationInfo[] operationInfos = mbeanInfo.getOperations();

        // define result holder
        MBeanOperationInfo foundOperationInfo = null;

        // locate operation 
        for ( MBeanOperationInfo operationInfo : operationInfos )
        {		
			// compare operation names
            if ( operationInfo.getName().equals( operationId ) )
            {
                // store found attribute
                foundOperationInfo = operationInfo;

                // exit loop
                break;
            }
        }

        return foundOperationInfo;
    }
	
	
}
