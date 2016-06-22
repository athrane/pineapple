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


package com.alpha.pineapple.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.cli.ConsoleDownloadMonitor;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderConsoleLogger;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.embedder.PlexusLoggerAdapter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.monitor.event.DefaultEventMonitor;
import org.apache.maven.monitor.event.EventMonitor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.events.TransferListener;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * Executes pineapple test operation.
 * 
 * @goal test1
 * 
 */
public class TestOperationMojoV1 extends AbstractMojo implements Contextualizable
{
    /**
     * GroupId for target project.
     * 
     * @parameter expression = "${pineapple.groupId}"
     * @required
     */
    String groupId;

    /**
     * ArtifactId for target project.
     * 
     * @parameter expression = "${pineapple.artifactId}"
     * @required
     */
    String artifactId;

    /**
     * Environment for target project.
     * 
     * @parameter expression = "${pineapple.environment}"
     * @required
     */
    String environment;

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
     * Maven artifact resolver
     * 
     * @component
     */
    ArtifactResolver resolver;

    /**
     * Maven project builder.
     * 
     * @component
     */
    MavenProjectBuilder mavenProjectBuilder;

    /**
     * Maven artifact metadata source.
     * 
     * @component
     */
    protected ArtifactMetadataSource metadataSource;

    /**
     * Plexus container.
     */
    PlexusContainer container;

    /**
     * Maven embedder
     */
    MavenEmbedder maven;

    public void execute() throws MojoExecutionException, MojoFailureException
    {               
        // define test goal
        final String TEST_GOAL = "ptest";
        final String VERSION = "1.0-SNAPSHOT";        

        // log info message
        StringBuilder message = new StringBuilder();
        message.append( "Starting pineapple-maven-plugin:test" );
        getLog().info( message.toString() );

        // log parameters
        logPluginParameters();
        
        try
        {
            // create maven
            createMavenEmbedder();

            // create composite goal
            StringBuilder compositeGoal = new StringBuilder();
            compositeGoal.append( this.groupId );
            compositeGoal.append( ":" );
            compositeGoal.append( this.artifactId );
            compositeGoal.append( ":" );        
            compositeGoal.append( VERSION );
            compositeGoal.append( ":" );
            compositeGoal.append( TEST_GOAL );
            
            // define goal
            List<String> goals = Collections.singletonList( compositeGoal.toString() );

            // resolve plugin project 
            MavenProject pluginProject;
            pluginProject = resolvePluginProject();

            // setup event monitor
            EventMonitor eventMonitor =
                new DefaultEventMonitor( new PlexusLoggerAdapter( new MavenEmbedderConsoleLogger() ) );

            // create properties
            Properties properties = new Properties();
            properties.setProperty( "key", "hello wolrd!" );

            // setup ...
            TransferListener transferListener;
            transferListener = new ConsoleDownloadMonitor();

            // setup execution directory
            File executionRootDirectory = new File( "c:\temp" );

            // execute request
            maven.execute( pluginProject, goals, eventMonitor, transferListener, properties, executionRootDirectory );

            // stop embedder
            maven.stop();
        }
        catch ( Exception e )
        {
            // create error message
            message = new StringBuilder();
            message.append( "Pineapple plugin execution failed with exception: " );
            message.append( e.getMessage() );

            // throw exception
            throw new MojoExecutionException( message.toString() );
        }

        // log info message
        message = new StringBuilder();
        message.append( "Successfully completed pineapple-maven-plugin:test" );
        getLog().info( message.toString() );
    }

    /**
     * Resolve plugin artifact.
     * 
     * @return plugin artifact.
     * 
     * @throws ProjectBuildingException
     *             If artifact resolution fails.
     * @throws ArtifactResolutionException
     *             If artifact resolution fails.
     * @throws ArtifactNotFoundException
     *             If artifact resolution fails.
     * @throws InvalidDependencyVersionException
     *             If artifact resolution fails.
     */
    @SuppressWarnings( "unchecked" )
    MavenProject resolvePluginProject()
        throws ProjectBuildingException, ArtifactResolutionException, ArtifactNotFoundException,
        InvalidDependencyVersionException
    {
        final String VERSION = "1.0-SNAPSHOT";

        // log debug message
        StringBuilder message = new StringBuilder();
        message.append( "Starting to resolve Maven plugin project." );
        getLog().debug( message.toString() );

        // create empty remote repositories list
        List<ArtifactRepository> remoteRepositories = new ArrayList<ArtifactRepository>();

        // get local repository
        ArtifactRepository localRepository;
        localRepository = this.session.getLocalRepository();

        // initialize artifact version
        VersionRange versionRange;
        versionRange = VersionRange.createFromVersion( VERSION );

        // log debug message
        message = new StringBuilder();
        message.append( "Creating artifact with groupId <" );
        message.append( this.groupId );
        message.append( "> artifactId <" );
        message.append( this.artifactId );
        message.append( "> and version range <" );
        message.append( versionRange );
        message.append( ">. " );
        getLog().debug( message.toString() );

        // create plugin artifact
        Artifact pomArtifact = artifactFactory.createPluginArtifact( groupId, artifactId, versionRange );
       
        // log debug message
        message = new StringBuilder();
        message.append( "Creating project with artifact <" );
        message.append( pomArtifact );
        message.append( "> with local repository <" );
        message.append( localRepository );
        message.append( "> and remote repositories <" );
        message.append( ReflectionToStringBuilder.toString( remoteRepositories ) );
        message.append( ">. " );
        getLog().debug( message.toString() );

        // create plugin project
        MavenProject pomProject;
        pomProject = mavenProjectBuilder.buildFromRepository( pomArtifact, remoteRepositories, localRepository );
                
        // log debug message
        message = new StringBuilder();
        message.append( "Resolving artifact <" );
        message.append( pomArtifact.getGroupId() );
        message.append( ":" );
        message.append( pomArtifact.getArtifactId() );
        message.append( ":" );
        message.append( pomArtifact.getType() );
        message.append( ":" );
        message.append( pomArtifact.getBaseVersion() );
        message.append( "> with local repository <" );
        message.append( localRepository );
        message.append( "> and remote repositories <" );
        message.append( ReflectionToStringBuilder.toString( remoteRepositories ) );
        message.append( ">. " );
        getLog().debug( message.toString() );

        // create sub artifacts for current artifact
        Set artifacts = pomProject.createArtifacts( this.artifactFactory, null, null );

        // resolve dependencies transitively
        ArtifactResolutionResult result;
        result = resolver.resolveTransitively( artifacts, pomArtifact, remoteRepositories, localRepository, metadataSource );                

        // log debug message
        message = new StringBuilder();
        message.append( "Sucessfully resolved Maven plugin project." );
        getLog().debug( message.toString() );

        return pomProject;
    }

    /**
     * Create Maven Embedder instance.
     * 
     * @throws MavenEmbedderException
     *             If Maven Embedder instantiation fails.
     */
    void createMavenEmbedder() throws MavenEmbedderException
    {
        // log debug message
        StringBuilder message = new StringBuilder();
        message.append( "Starting to create Maven Embedder." );
        getLog().debug( message.toString() );

        // define non interactive execution
        final boolean NON_INTERACTIVE = false;

        // create maven embedder
        maven = new MavenEmbedder();

        // set class loader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        maven.setClassLoader( classLoader );

        // disable interactive mode
        maven.setInteractiveMode( NON_INTERACTIVE );

        // set logger
        MavenEmbedderConsoleLogger logger = new MavenEmbedderConsoleLogger();
        if ( getLog().isDebugEnabled() )
        {
            logger.setThreshold( MavenEmbedderLogger.LEVEL_DEBUG );
        }
        else
        {
            logger.setThreshold( MavenEmbedderLogger.LEVEL_INFO );
        }
        maven.setLogger( logger );

        // setup user installation alignment
        maven.setAlignWithUserInstallation( true );

        // start embedder
        maven.start();

        // log debug message
        message = new StringBuilder();
        message.append( "Configured with local repository <" );
        message.append( maven.getLocalRepository() );
        message.append( ">." );
        getLog().debug( message.toString() );

        // log debug message
        message = new StringBuilder();
        message.append( "Successfully created Maven embedder." );
        getLog().debug( message.toString() );
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
        message.append( "Invoked with parameter GroupId <" );
        message.append( this.groupId );
        message.append( ">." );
        getLog().debug( message.toString() );

        // log debug message
        message = new StringBuilder();
        message.append( "Invoked with parameter ArtifactId <" );
        message.append( this.artifactId );
        message.append( ">." );
        getLog().debug( message.toString() );

        // log debug message
        message = new StringBuilder();
        message.append( "Invoked with parameter Environment <" );
        message.append( this.environment );
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

}
