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


package com.alpha.pineapple.plugin.weblogic.deployment.argument;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.enterprise.deploy.spi.TargetModuleID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.jsr88.command.result.AvailableModulesResult;
import com.alpha.pineapple.resource.ResourceException;
import com.alpha.pineapple.session.SessionException;

/**
 * Helper class which build arguments list for weblogic.Deployer. 
 */
public class ArgumentBuilder
{
	/**
	 * Default deployment plan file name.  
	 */
    static final String PLAN_XML = "plan.xml";
    
    /**
     * Time stamp format.
     */
    static final String TIMESTAMP_FORMAT = "yyyyMMdd-HHmmss";

    /**
     * Management data application directory name. 
     */
    static final String APP_DIRECTORY_NAME = "app";

    /**
     * RAR suffix.
     */
    static final String RAR_SUFFIX = "rar";

    /**
     * JAR suffix.
     */    
    static final String JAR_SUFFIX = "jar";

    /**
     * WAR suffix.
     */    
    static final String WAR_SUFFIX = "war";

    /**
     * EAR suffix.
     */    
    static final String EAR_SUFFIX = "ear";

    /**
     * Plan identifier..
     */    
    static final String PLAN_IDENTIFIER = "plan";
    
    /**
     * Plan argument.
     */
    static final String PLAN_ARGUMENT = "-plan";

    /**
     * Name identifier.
     */
    static final String NAME_IDENTIFIER = "name";

    /**
     * DEployer argument
     */
    static final String DEPLOYER_ARGUMENT = "weblogic.Deployer";
    
    /**
     * Name argument
     */
    static final String NAME_ARGUMENT = "-name";

    /**
     * Source identifier.
     */
    static final String SOURCE_IDENTIFIER = "source";
    
    /**
     * Source argument.
     */
    static final String SOURCE_ARGUMENT = "-source";

    /**
     * Upload argument
     */
    static final String UPLOAD_ARGUMENT = "-upload";

    /**
     * Stage argument
     */
    static final String STAGE_ARGUMENT = "-stage";

    /**
     * Stage argument
     */
    static final String NOEXCLUSIVELOCK_ARGUMENT = "-usenonexclusivelock";
 
	/**
	 * Administration URL argument.
	 */
	static final String ADMINURL_ARGUMENT = "-adminurl";
    
    /**
     * First list index constant.
     */
    final static int FIRST_INDEX = 0;
    
    /**
     * Time stamp date format.
     */
    DateFormat timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
    
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * The builder result.
     */
    List<String> argumentList;

    /**
     * The management data parameters used as input for construction.
     */
    Map<String, String[]> parameters;

    /**
     * File locator object.
     */
    @Resource
    FileLocator fileLocator;    

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
    
    /**
     * Start building argument list.
     * 
     * @throws IllegalArgumentException If parameters is undefined.            
     */
    public void buildArgumentList() {

        // create the result
        argumentList = new ArrayList<String>(); 
    }
    
    /**
     * Return the result of the build process.
     * 
     * @return constructed argument list. 
     */
    public String[] getArgumentList() {
		String[] arguments = argumentList.toArray(new String[argumentList.size()] );
		return arguments;
    }
	                    
    /**
     * Map parameters in management data to argument list. 
     */
    public void mapArguments()
    {
        for ( String key : parameters.keySet() )
        {
            // create weblogic.Deployer argument
            StringBuilder argumentName = new StringBuilder();
            argumentName.append( "-" );
            argumentName.append( key );

            // add argument
            argumentList.add( argumentName.toString() );
                        
            // get values
            String[] values = parameters.get( key );            

            // create comma separated list
            String valuesAsSingleString;
            valuesAsSingleString = StringUtils.join(values, ",");
                        
            // add argument values
            argumentList.add( valuesAsSingleString );
            
            // log debug message
            if ( logger.isDebugEnabled() )
            {
            	Object[] args = { argumentName, ToStringBuilder.reflectionToString( values ) };    	        	
            	String message = messageProvider.getMessage("ab.map_argument_info", args );
            	logger.debug( message );            	
            }
        }
    }

    /**
     * Add a single argument to the product.
     * 
     * @param argument The argument to add.
     */
    public void addSingleArgument( String argument )
    {
        this.argumentList.add( argument );        
    }

    /**
     * Add deployer argument to weblogic.deployer argument list. 
     */
    public void addWebLogicDeployerArgument()
    {
        addSingleArgument( DEPLOYER_ARGUMENT );
    }

    /**
     * Add upload argument to weblogic.deployer argument list. 
     */
    public void addUploadArgument()
    {
        addSingleArgument( UPLOAD_ARGUMENT );
    }
    
    /**
     * Add stage argument to weblogic.deployer argument list. 
     */
    public void addStageArgument()
    {
        addSingleArgument( STAGE_ARGUMENT );
    }

    /**
     * Add no exclusive lock argument to weblogic.deployer argument list. 
     */
    public void addNoExclusiveLockArgument()
    {
        addSingleArgument( NOEXCLUSIVELOCK_ARGUMENT );
    }
    
    
    /**
     * Add connection arguments to weblogic.deployer argument list. 
     * The connection arguments defines which server URL to access.
     * 
     * @param adminUrl Administration URL.
     */
    public void addAdministrationUrlArgument( String adminUrl ) 
    {
        // add administration URL
    	addSingleArgument( ADMINURL_ARGUMENT );
    	addSingleArgument( adminUrl );
    }
    
        
    /**
     * Add name argument to weblogic.deployer argument list. The added name 
     * argument is post fixed with a time stamp. 
     * 
     * The name argument defines the name under which the application is 
     * deployed in the application server.
     * 
     * If the name argument is defined manually in the management data 
     * parameters then it overrides generation of a name value from the 
     * management data.
     * 
     * The appended time stamp has the format YYYYMMDD_HHMMSS.
 
     * @param name Application name.
     */    
    public void addTimeStampedNameArgument( String name )
    {
        // create time stamp            
        String timestamp;
        timestamp = timestampFormat.format( new Date() );
        
        // create name with time stamp
        StringBuilder value = new StringBuilder();
        value.append( name );
        value.append( "_" );
        value.append( timestamp );
        
        // add argument
        addSingleArgument( NAME_ARGUMENT );        
        addSingleArgument( value.toString() );

        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { value.toString() };    	        	
        	String message = messageProvider.getMessage("ab.append_timestamp_info", args );
        	logger.debug( message );            	
        }                        
    }

    /**
     * Add name argument to weblogic.deployer argument list. The added name 
     * argument is post fixed with a time stamp. 
     * 
     * The name argument defines the name under which the application is 
     * deployed in the application server.
     *  
     * After the name have been identified then the set of modules is searched 
     * to find a module with the same name post fixed with a time stamp with 
     * the format YYYYMMDD_HHMMSS.
     * 
     * @param appName Application name.
     * @param modules J2EE modules.
     * 
     * @throws ArgumentBuilderException if location of module fails.
     */    
    public void addTimeStampedNameArgument( String appName, AvailableModulesResult modules ) throws ArgumentBuilderException
    {
        // define name
        String name = appName;
                                    
        // search for time stamped names in domain
        TargetModuleID[] timestampedNames;
        timestampedNames = modules.findModulesStartingWith( name );
        
        // if module name is found then add first found  
        if(timestampedNames.length > 0) {
            
            // get module id
            String moduleId = timestampedNames[ FIRST_INDEX ].getModuleID();
            
            // log debug message
            if ( logger.isDebugEnabled() )
            {
            	Object[] args = { appName };    	        	
            	String message = messageProvider.getMessage("ab.locate_module_info", args );
                logger.debug( message );
            }                        

            // add argument
            addSingleArgument( NAME_ARGUMENT );                        
            addSingleArgument( moduleId );
            return;
        } 
       
        // no time stamped name was found, fail               
        // throw exception        
    	Object[] args = { appName };    	        	
    	String message = messageProvider.getMessage("ab.locate_module_error", args );
        throw new ArgumentBuilderException( message );
        
    }

    /**
     * Add name argument to weblogic.deployer argument list. 
     *   
     * @param name Name argument.  
     */
    public void addNameArgument( String name)
    {
        addSingleArgument( NAME_ARGUMENT );
        addSingleArgument( name );        
    }
    
    
    /**
     * Add source argument to weblogic.deployer argument list. 
     *   
     * @param artifact Absolute path to deployment artifact.  
     */
    public void addSourceArgument( File artifact)
    {
        addSingleArgument( SOURCE_ARGUMENT );
        addSingleArgument( artifact.getAbsolutePath() );        
    }
    
    /**
     * Add source argument to weblogic.deployer argument list. 
     * 
     * The source argument defines archive file or exploded archive directory to deploy.
     * 
     * The deployment artifact is attempted to be located in the "/app"
     * directory of the module. Artifact types are searched in this order:
     * *.ear, *.war, *.jar, *.rar, exploded directory. 
     *    
     * @param moduleDirectory Module directory.
     *  
     * @throws ArgumentBuilderException If locating deployment artifact fails.
     */
    public void addGeneratedSourceArgument( File moduleDirectory ) throws ArgumentBuilderException
    {
        // create application directory name
        File appDirectory = getApplicationDirectory( moduleDirectory );

        // locate deployment artifact in application directory
        File artifact = locateDeploymentArtifact( appDirectory );

        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { artifact };    	        	
        	String message = messageProvider.getMessage("ab.located_deployment_artifact_info", args );
        	logger.debug( message );            	
        }

        // add source
        addSourceArgument( artifact );                
    }
    
    
    /**
     * Create directory name where applications are located.
     * 
     * @param moduleDirectory Module directory.
     * 
     * @return directory name where applications are located.
     */
    File getApplicationDirectory( File moduleDirectory )
    {
    	return new File(moduleDirectory, APP_DIRECTORY_NAME );
    }

    
    /**
     * Locate deployment artifact in application directory.
     * 
     * @param appDirectoryName
     *            Application directory which is searched for a deployable artifact.
     * 
     * @return deployment artifact.
     * 
     * @throws ArgumentBuilderException If locating deployment artifact failed. 
     */
    private File locateDeploymentArtifact( File appDirectory ) throws ArgumentBuilderException
    {
        // search for files with ear suffix
        String[] files = fileLocator.locateFileSet( EAR_SUFFIX, appDirectory );

        // select first ear file
        if ( files.length > 0 )
        {            
            return new File( appDirectory, files[0] );
        }

        // search for files with war suffix
        files = fileLocator.locateFileSet( WAR_SUFFIX, appDirectory );

        // select first war file
        if ( files.length > 0 )
        {
            return new File( appDirectory, files[0] );
        }

        // search for files with jar suffix
        files = fileLocator.locateFileSet( JAR_SUFFIX, appDirectory );

        // select first jar file
        if ( files.length > 0 )
        {
            return new File( appDirectory, files[0] );
        }

        // search for files with rar suffix
        files = fileLocator.locateFileSet( RAR_SUFFIX, appDirectory );

        // select first rar file
        if ( files.length > 0 )
        {
            return new File( appDirectory, files[0] );
        }
        
        // search for directories
        files = fileLocator.locateSubDirectories( appDirectory );

        // select first directory
        if ( files.length > 0 )
        {
            return new File( files[0] );
        }

        // throw exception        
    	Object[] args = { appDirectory };    	        	
    	String message = messageProvider.getMessage("ab.locate_deployment_artifact_error", args );
        throw new ArgumentBuilderException( message );
        
    }

    /**
     * Add plan argument to weblogic.deployer argument list. 
     *   
     * @param artifact Absolute path to deployment artifact.  
     */
    public void addPlanArgument( File plan)
    {
        addSingleArgument( PLAN_ARGUMENT );
        addSingleArgument( plan.getAbsolutePath() );        
    }
    
    
    /**
     * Add general plan argument to weblogic.deployer argument list. 
     * 
     * The plan argument defines the deployment plan to use for deployment.
     *
     * The deployment plan is attempted to be located in the "/plan"
     * directory of the module. Plans are searched in this order:
     * "&lt;environment&gt;.xml", plan.xml. where &lt;environment&gt;
     * is the name of the environment for which the module is executed.
     * 
     * @param moduleDirectory Module Directory. 
     * @param rootDirectory Deployment environment.      
     */
    public void addGeneratedPlanArgument( File moduleDirectory, String environment )
    {
        // get plan directory
        File planDirectory = getPlanDirectory( moduleDirectory );
        
        // exit if plan directory doesn't exists
        if(!planDirectory.exists()) {
            // log debug message
            if ( logger.isDebugEnabled() )
            {
            	Object[] args = { planDirectory };    	        	            	
            	String message = messageProvider.getMessage("ab.located_deployment_plan_failed", args );
            	logger.debug( message );            	
            }
            
            return;
        }

        // locate deployment plan in plan directory
        File planFile = locateDeploymentPlan( planDirectory, environment );

        // if no plan was located then exit
        if ( planFile == null )
        {
            // log debug message
            if ( logger.isDebugEnabled() )
            {
            	String message = messageProvider.getMessage("ab.located_deployment_plan_failed" );
            	logger.debug( message );            	
            }
            return;
        }

        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { planFile };    	        	
        	String message = messageProvider.getMessage("ab.located_deployment_plan_info", args );
        	logger.debug( message );            	
        }

        // add argument and value
        addPlanArgument( planFile );
    }

    /**
     * Create directory name where deployment plans are located.
     * 
     * @param moduleDirectory Module directory.
     * 
     * @return directory name where deployment plans are located.
     */
    private File getPlanDirectory ( File moduleDirectory)
    {
    	return new File( moduleDirectory, PLAN_IDENTIFIER );
    }

    /**
     * Locate deployment plan in plan directory. Searches for a file named
     * "plan.xml" and then a file named "&lt;environment&gt;.xml". 
     * Returns null if deployment plan couldn't be found.
     * 
     * @param planDirectoryName
     *            Directory which is searched for a deployment plan.
     * @param environment
     *            The environment.
     * 
     * @return File object containing location of deployment plan. If 
     * deployment plan couldn't be located then null is returned.
     */
    private File locateDeploymentPlan( File planDirectory, String environment )
    {
        // return file name if file exists
        if ( fileLocator.fileExists( planDirectory, PLAN_XML ) )
        {
            return new File ( planDirectory, PLAN_XML ) ;
        }

        // create plan name
        StringBuilder fileName = new StringBuilder();
        fileName.append( environment );
        fileName.append( ".xml" );

        // return file name if file exists
        if ( fileLocator.fileExists( planDirectory, fileName.toString() ) )
        {
            return new File ( planDirectory, fileName.toString() );
        }

        // no plan found, it is OK to return null        
        return null;
    }
       
}
