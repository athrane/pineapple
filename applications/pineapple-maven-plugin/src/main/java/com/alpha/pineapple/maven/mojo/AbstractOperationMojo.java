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


package com.alpha.pineapple.maven.mojo;

import java.io.File;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import com.alpha.pineapple.CoreException;
import com.alpha.pineapple.CoreFactory;
import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.maven.MavenHelper;
import com.alpha.pineapple.maven.PluginConstants;

/**
 * Abstract base class for operation mojos.
 */
abstract class AbstractOperationMojo extends AbstractMojo implements Contextualizable
{    
    /**
     * Environment for target project.
     * 
     * @parameter expression = "${pineapple.environment}"
     * @required
     */
    String environment;

    /**
     * The Maven Project Object
     *
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    String finalName;

    /**
     * This is where build results go.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    File buildDirectory;
    
    /**
     * Maven settings object.
     * 
     * @parameter expression="${settings}"
     * @required
     */
    Settings settings;

    /**
     * Maven session object.
     * 
     * @parameter expression="${session}"
     * @required
     */
    MavenSession session;

    /**
     * Maven artifact factory.
     * 
     * @component
     */
    ArtifactFactory artifactFactory;

    /**
     * Maven artifact resolver.
     * 
     * @component
     */
    ArtifactResolver resolver;

    /**
     * Maven project builder.
     * 
     * @component
     */
    MavenProjectBuilder projectBuilder;

    /**
     * Maven artifact metadata source.
     * 
     * @component
     */
    ArtifactMetadataSource metadataSource;

    /**
     * Plexus container.
     */
    PlexusContainer container;

    /**
     * Maven embedder
     */
    MavenEmbedder maven;

    /**
     * Pineapple core component.
     */
    PineappleCore core;    

    /**
     * Maven helper object.
     */
    MavenHelper mavenHelper;     
    
    public final void execute() throws MojoExecutionException, MojoFailureException
    {
        // log debug message
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Starting pineapple-maven-plugin execution." );
            getLog().debug( message.toString() );

            // log parameters
            logPluginParameters();
        }
        
        // create helper
        mavenHelper = new MavenHelper(getLog()); 
        
        // initialize runtime directories
        initializeDirectories();

        // initialize pineapple
        initializePineapple();
        
        // configure module name
        String module = this.finalName;
        
        // configure runtime root directory
        // i.e. target\<app>-<version>-management.dir        
        StringBuilder rootDirectory = new StringBuilder();
        rootDirectory.append( this.buildDirectory.getAbsolutePath() );
        rootDirectory.append( File.separatorChar );        
        rootDirectory.append( module );        
        rootDirectory.append( "-management.dir" );        
                    
        // get root directory as file object
        File rootDirAsFile = new File( rootDirectory.toString());
        
        // get operation from sub class
        String operation = getOperation();
        
        // invoke operation
        ExecutionInfo info = core.executeOperation(operation, environment, module);            		
        
        // invoke to block execution
        ExecutionResult result = info.getResult();
        while (result.getState() == ExecutionState.EXECUTING) {            	
        }
                                
        // log debug message
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Pineapple execution result <" );
            message.append( result.getState() );                
            message.append( ">" );                
            getLog().debug( message.toString() );
        }
        
        // throw exception if operation failed
        if( result.getState() != ExecutionState.SUCCESS ) {

            // create error message
            StringBuilder message = new StringBuilder();
            message.append( "Pineapple execution failed. ");
            message.append( "Please consult the log files for more information." );
            
            // throw exception
            throw new MojoExecutionException( message.toString() );                
        }                                

        // log debug message
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Successfully completed pineapple-maven-plugin execution." );
            getLog().debug( message.toString() );
        }
    }

    /**
     * Initialize runtime directories
     * 
     * @throws MojoExecutionException If initialization fails.
     */
    void initializeDirectories() throws MojoExecutionException
    {
        // get user.home
        String userHomeDirectory = mavenHelper.getUserHomeProperty( session );

        // create pineapple runtime directory from user.home directory
        File runtimeDirectory = new File( userHomeDirectory, PluginConstants.PINEAPPLE_DIR ); 
        createRuntimeDirectory( runtimeDirectory );

        // create logging directory 
        File loggingDirectory = new File( runtimeDirectory, PluginConstants.LOGGING_DIR ); 
        createRuntimeDirectory( loggingDirectory );

        // create conf directory 
        File confDirectory = new File( runtimeDirectory, PluginConstants.CONF_DIR); 
        createRuntimeDirectory( confDirectory );        
    }

    /**
     * Create runtime directory
     * 
     * @param runtimeDirectory Runtime directory which should be created.
     *  
     * @throws MojoExecutionException If directory creation fails.
     */
    void createRuntimeDirectory( File runtimeDirectory ) throws MojoExecutionException
    {
        // validate parameters
        Validate.notNull( runtimeDirectory, "runtimeDirectory is undefined." );
        
        // create the directory if it doesn't exists
        if ( !runtimeDirectory.exists() )
        {

            boolean result = runtimeDirectory.mkdirs();

            if ( result )
            {
                // creation succeeded                
                // log debug message
                if ( getLog().isDebugEnabled() )
                {
                    StringBuilder message = new StringBuilder();
                    message.append( "Successfully created Pineapple runtime directory <" );
                    message.append( runtimeDirectory );
                    message.append( ">." );
                    getLog().debug( message.toString() );
                }
            }
            else
            {
                // creation failed
                
                // create error message
                StringBuilder message = new StringBuilder();
                message.append( "Failed to create Pineapple runtime directory <" );
                message.append( runtimeDirectory );
                message.append( ">." );
                
                // log debug message
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( message.toString() );
                }
                
                // throw exception
                throw new MojoExecutionException( message.toString());                                

            }
        }
    }

    /**
     * Initialize Pineapple manager.
     * 
     * @throws MojoExecutionException
     *             If initialization fails.
     */
    void initializePineapple() throws MojoExecutionException
    {
        try
        {
            // get user.home            
            String userHomeDirectory = mavenHelper.getUserHomeProperty( session ); 

            // initialize pineapple runtime directory from user.home directory
            File runtimeDirectory = new File( userHomeDirectory, PluginConstants.PINEAPPLE_DIR ); 

            // initialize conf directory 
            File confDirectory = new File( runtimeDirectory, PluginConstants.CONF_DIR);            
            
            // initialize credential file in user.home/.pineapple/conf directory
            File credentialsFile = new File( confDirectory, PluginConstants.CREDENTIALS_FILE );

            // create core factory
            CoreFactory coreFactory = new CoreFactory();
            
            // create credential provider
            CredentialProvider provider = coreFactory.createCredentialProvider( credentialsFile );

            // initialize resources file in user.home/.pineapple/conf directory
            File resourcesfile;
            resourcesfile = new File( confDirectory, PluginConstants.RESOURCES_FILE );            
            
            // create pineapple core component             
            core = coreFactory.createCore( provider, resourcesfile );
        }
        catch ( CoreException e )
        {
            // create error message
            StringBuilder message = new StringBuilder();
            message.append( "Pinapple core initialization failed with exception: " );
            message.append( e.toString() );

            // throw exception
            throw new MojoExecutionException( message.toString(), e );            
        }

    }

    /**
     * Log plugin parameters.
     * 
     * @throws MojoFailureException
     *             if plugin parameter validation fails.
     */
    void logPluginParameters() throws MojoFailureException
    {
        // log debug message
        StringBuilder message = new StringBuilder();
        message.append( "Invoked with parameter Environment <" );
        message.append( this.environment );
        message.append( ">." );
        getLog().debug( message.toString() );

        // log debug message
        message = new StringBuilder();
        message.append( "Invoked with parameter project final name <" );
        message.append( this.finalName );
        message.append( ">." );
        getLog().debug( message.toString() );

        // log debug message
        message = new StringBuilder();
        message.append( "Invoked with parameter Maven build directory <" );
        message.append( ReflectionToStringBuilder.toString( this.buildDirectory) );
        message.append( ">." );
        getLog().debug( message.toString() );
        
        // log debug message
        message = new StringBuilder();
        message.append( "Invoked with parameter Maven session <" );
        message.append( ReflectionToStringBuilder.toString( this.session ) );
        message.append( ">." );
        getLog().debug( message.toString() );

        // log debug message
        message = new StringBuilder();
        message.append( "Invoked with parameter Maven settings <" );
        message.append( ReflectionToStringBuilder.toString( this.settings ) );
        message.append( ">." );
        getLog().debug( message.toString() );

        // log debug message
        message = new StringBuilder();
        message.append( "Invoked with parameter Maven artifact factory <" );
        message.append( ReflectionToStringBuilder.toString( this.artifactFactory ) );
        message.append( ">." );
        getLog().debug( message.toString() );

        // log debug message
        message = new StringBuilder();
        message.append( "Invoked with parameter Maven artifact resolver <" );
        message.append( ReflectionToStringBuilder.toString( this.resolver ) );
        message.append( ">." );
        getLog().debug( message.toString() );
    }

    public void contextualize( Context context ) throws ContextException
    {

        // log debug message
        StringBuilder message = new StringBuilder();
        message.append( this.getClass().getName() + "::contextualize invoked." );
        getLog().info( message.toString() );

        // get container
        this.container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    /**
     * Query sub class for operation which should be executed.
     * 
     * @return operation which should be executed.  
     */
    public abstract String getOperation();
}
