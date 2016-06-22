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


package com.alpha.pineapple.plugin.weblogic.deployment.operation;

import static com.alpha.pineapple.plugin.weblogic.deployment.WebLogicDeploymentConstants.*;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.ExecutableNameUtils;
import com.alpha.javautils.OperationUtils;
import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.weblogic.deployment.WebLogicDeploymentConstants;
import com.alpha.pineapple.plugin.weblogic.deployment.argument.ArgumentBuilder;
import com.alpha.pineapple.plugin.weblogic.deployment.model.Deployment;
import com.alpha.pineapple.plugin.weblogic.deployment.model.DeploymentArtifact;
import com.alpha.pineapple.plugin.weblogic.deployment.session.WeblogicDeploymentSession;
import com.alpha.pineapple.resource.ResourceException;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionException;

@PluginOperation(OperationNames.DEPLOY_APPLICATION)
public class DeployConfiguration implements Operation
{	 
    /**
     * The command to invoke the deployer with.
     */
    static final String DEPLOYER_COMMAND = "-deploy"; 
	
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
     * Operation utilities.
     */
    @Resource
    OperationUtils operationUtils;
        
    /**
     * Argument builder object. 
     */
    @Resource
    ArgumentBuilder argumentBuilder;
    
    /**
     * Execution info provider.
     */
    @Resource
    ExecutionInfoProvider coreExecutionInfoProvider;
    
    /**
     * Runtime directory provider.
     */
    @Resource
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;
    
    
    public void execute( Object content, Session session, ExecutionResult result ) throws PluginExecutionFailedException
    {
        // validate parameters
        Validate.notNull( content, "content is undefined." );
        Validate.notNull( session, "session is undefined." );
        Validate.notNull( result, "result is undefined." );        

        // log debug message
        if ( logger.isDebugEnabled() )
        {        	
        	Object[] args = { content.getClass().getName(), content };    	        	
        	String message = messageProvider.getMessage("dc.start", args );
        	logger.debug( message );
        }
        
        // validate parameters
        operationUtils.validateContentType(content, WebLogicDeploymentConstants.LEGAL_CONTENT_TYPES);
        operationUtils.validateSessionType(session, ProcessExecutionSession.class );

        try
        {
            // type cast to model object
            Deployment deploymentModel = (Deployment) content;

        	// type cast to process execution session 
            ProcessExecutionSession externalProcessSession = (ProcessExecutionSession) session; 
        	
    		// get execution info
    		ExecutionInfo info = coreExecutionInfoProvider.get(result);
            
            // do deployment
    		deploy(externalProcessSession, deploymentModel, info, result);        
        	        	
    		// compute result    		
    		result.completeAsComputed(messageProvider, "dc.completed", null, "dc.failed", null);        	            
        }
        catch ( Exception e )
        {
        	Object[] args = { StackTraceHelper.getStrackTrace( e ) };    	        	
        	String message = messageProvider.getMessage("dc.error", args );
            throw new PluginExecutionFailedException( message, e );        	
        }
    }

    /**
     * Do deployment.
     * 
     * @param session Plugin session.
     * @param model Plugin model.
     * @param info Execution info. 
     * @param result Execution result.
     * 
     * @throws Exception if deployment fails.
     */
	void deploy(ProcessExecutionSession session, Deployment model, ExecutionInfo info, ExecutionResult result) throws Exception {
		
		//exit if no model content is defined.
		if (!isModelContentDefined(model)) return;
		
		// get model content
		DeploymentArtifact artifact = model.getArtifact();		
			
		// get module info
		ModuleInfo moduleInfo = info.getModuleInfo();		
		
		// escape installer archive path if required
		File parent = null;		
		File deployer = ExecutableNameUtils.resolveJavaExecutable(parent);
		
		// start build process
		argumentBuilder.buildArgumentList();
		argumentBuilder.addWebLogicDeployerArgument();
		argumentBuilder.addAdministrationUrlArgument(createAdminURL(session));
				
		// set name
		String name = resolveName(artifact, moduleInfo);		
		if( isTimeStampEnabled(session)) {						
			argumentBuilder.addTimeStampedNameArgument( name );
		} else {
			argumentBuilder.addNameArgument(name);			
		}
				
		// set source argument
		if(isFileDefined(artifact)) {
			File resolvedPath = coreRuntimeDirectoryProvider.resolveModelPath(artifact.getFile(), moduleInfo);
			argumentBuilder.addSourceArgument( resolvedPath );
			
		} else {
			argumentBuilder.addGeneratedSourceArgument( moduleInfo.getDirectory());			
		}
		
		// set plan
		if(isPlanDefined(artifact)) {
			File resolvedPath = coreRuntimeDirectoryProvider.resolveModelPath(artifact.getPlan(), moduleInfo);
			argumentBuilder.addPlanArgument( resolvedPath );			
		} else {
			argumentBuilder.addGeneratedPlanArgument( moduleInfo.getDirectory(), info.getEnvironment());			
		}

		// map arguments
		argumentBuilder.mapArguments( );
		argumentBuilder.addUploadArgument();        
		argumentBuilder.addStageArgument();
		argumentBuilder.addNoExclusiveLockArgument();
		String[] arguments = argumentBuilder.getArgumentList(); 

		// get description    	        	
    	String description2 = messageProvider.getMessage("dc.execute" );
		
		// execute external process    	
		session.execute(deployer , arguments, DEFAULT_PROCESS_TIMEOUT, description2, result);		
	}

	/**
	 * Returns true is time stamp is enabled.
	 * 
	 * @param session Plugin session.
	 * 
	 * @return Returns true is time stamp is enabled.
	 * 
	 * @throws ResourceException If getting property from resource fails.
	 */
	boolean isTimeStampEnabled(ProcessExecutionSession session) throws ResourceException {
		
		// exit if not defined
		if (!session.isResourcePropertyDefined(TIMESTAMP_PROPERTY )) return false;
		
		// get resource property
		String value = session.getResourceProperty(TIMESTAMP_PROPERTY);
    
		// parse to boolean 
		return Boolean.parseBoolean( value );		
	}

    /**
     * Create URL to WebLogic administration server.
     * 
     * @param session Plugin session.
     * 
     * @return URL to WebLogic administration server.
     *  
     * @throws ResourceException If getting resources properties fails.
     */
    String createAdminURL(ProcessExecutionSession session) throws ResourceException {
        StringBuilder url = new StringBuilder()
        	.append( session.getResourceProperty(TIMESTAMP_PROPERTY))
        	.append( "://" )
        	.append( session.getResourceProperty(TIMESTAMP_PROPERTY))
        	.append( ":" )
        	.append( session.getResourceProperty(TIMESTAMP_PROPERTY));
        return url.toString();
    }
	
	
	
	/**
	 * Resolve deployment artifact name. 
	 * 
	 * If the deployment artifact name is defined in the model then it is used.
	 * Otherwise the module name is used. 
	 * 
	 * @param artifact Deployment artifact.
	 *  
	 * @param moduleInfo module info.
	 * @return deployment artifact name.
	 */
	String resolveName(DeploymentArtifact artifact, ModuleInfo moduleInfo) {
		if(isNameDefined(artifact)) return artifact.getName();
		return moduleInfo.getId();			
	}

	/**
	 * Returns true if plan is defined.
	 * 
	 * @param deploymentArtifact Deployment artifact.
	 *  
	 * @return true if plan is defined.
	 */	
	boolean isPlanDefined(DeploymentArtifact artifact) {
		if (artifact.getPlan() == null) return false;
		return (!artifact.getPlan().equalsIgnoreCase(""));
	}

	/**
	 * Returns true if application name is defined.
	 * 
	 * @param artifact Deployment artifact.
	 *  
	 * @return true if application name is defined.
	 */
	boolean isNameDefined(DeploymentArtifact artifact) {
		if (artifact.getName() == null) return false;
		return (!artifact.getName().equalsIgnoreCase(""));
	}

	/**
	 * Returns true if file is defined.
	 * 
	 * @param artifact Deployment artifact.
	 *  
	 * @return true if file is defined.
	 */
	boolean isFileDefined(DeploymentArtifact artifact) {
		if (artifact.getFile() == null) return false;
		return (!artifact.getFile().equalsIgnoreCase(""));
	}
	
	/**
	 * Returns true if deployment artifact is defined.
	 * 
	 * @param model Deployment model.
	 * 
	 * @return if deployment artifact is defined.
	 */
	boolean isModelContentDefined(Deployment model) {
		return ((model.getArtifact() != null ));
	}
		
	
}
