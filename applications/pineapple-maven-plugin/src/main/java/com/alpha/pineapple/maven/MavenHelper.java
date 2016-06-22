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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.Validate;
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
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderConsoleLogger;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;

/**
 * Helper class which implements functionality to manipulate the 
 * Maven runtime system, create and resolve artifact and projects. 
 */
public class MavenHelper
{

    /**
     * Maven logger object.
     */
    Log logger;

    /**
     * MavenHelper constructor
     * 
     * @param logger
     *            Maven logger object.
     * 
     * @throws IllegalArgumentException
     *             If logger parameter is null.
     */
    public MavenHelper( Log logger )
    {

        // validate parameters
        Validate.notNull( logger, "logger is undefined." );

        this.logger = logger;
    }

    /**
     * Return Maven logger object.
     * 
     * @return Maven logger object
     */
    Log getLog()
    {
        return this.logger;
    }

    /**
     * Create Maven Embedder instance.
     * 
     * @throws MavenEmbedderException
     *             If Maven Embedder instantiation fails.
     */
    MavenEmbedder createMavenEmbedder() throws MavenEmbedderException
    {
        // define non interactive execution
        final boolean NON_INTERACTIVE = false;

        // log debug message
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Starting to create Maven Embedder." );
            getLog().debug( message.toString() );
        }

        // create maven embedder
        MavenEmbedder maven;
        maven = new MavenEmbedder();

        // set class loader
        ClassLoader classLoader;
        classLoader = Thread.currentThread().getContextClassLoader();
        maven.setClassLoader( classLoader );

        // disable interactive mode
        maven.setInteractiveMode( NON_INTERACTIVE );

        // set logger
        MavenEmbedderConsoleLogger logger;
        logger = new MavenEmbedderConsoleLogger();
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
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Maven Embedder is configured with local repository <" );
            message.append( maven.getLocalRepository() );
            message.append( ">." );
            getLog().debug( message.toString() );
        }

        // log debug message
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Successfully created Maven embedder." );
            getLog().debug( message.toString() );
        }

        return maven;
    }

    /**
     * Create artifact.
     * 
     * @param artifactFactory
     *            Maven artifact factory.
     * 
     * @return created artifact.
     */
    public Artifact createArtifact( ArtifactFactory artifactFactory )
    {
        final String VERSION = "1.0-SNAPSHOT";
        final String PLUGIN_GRP_ID = "com.alpha.pineapple";
        final String PLUGIN_ARTIFACT_ID = "pineapple-maven-delegator-plugin";

        // validate parameters
        Validate.notNull( artifactFactory, "artifactFactory is undefined." );

        // initialize artifact version
        VersionRange versionRange;
        versionRange = VersionRange.createFromVersion( VERSION );

        // log debug message
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Starting to create artifact with groupId <" );
            message.append( PLUGIN_GRP_ID );
            message.append( "> artifactId <" );
            message.append( PLUGIN_ARTIFACT_ID );
            message.append( "> and version range <" );
            message.append( versionRange );
            message.append( ">. " );
            getLog().debug( message.toString() );
        }

        // create artifact
        Artifact artifact;
        artifact = artifactFactory.createPluginArtifact( PLUGIN_GRP_ID, PLUGIN_ARTIFACT_ID, versionRange );

        // log debug message
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Successfully created Maven artifact." );
            getLog().debug( message.toString() );
        }

        return artifact;
    }

    /**
     * Create Maven project in local repository.
     * 
     * @param Maven
     *            artifact to create Project from.
     * @param localRepository
     *            Local Maven repository.
     * @param mavenProjectBuilder
     *            Maven project builder.
     * 
     * @return Maven project created in local repository.
     * @throws ProjectBuildingException
     *             If project creation fails.
     */
    public MavenProject createProject( Artifact artifact, ArtifactRepository localRepository,
                                       MavenProjectBuilder mavenProjectBuilder ) throws ProjectBuildingException
    {

        // validate parameters
        Validate.notNull( artifact, "artifact is undefined." );
        Validate.notNull( localRepository, "localRepository is undefined." );
        Validate.notNull( mavenProjectBuilder, "mavenProjectBuilder is undefined." );

        // log debug message
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Starting to create project from artifact <" );
            message.append( artifact );
            message.append( "> in local repository <" );
            message.append( localRepository );
            message.append( ">. " );
            getLog().debug( message.toString() );
        }

        // create empty remote repositories list
        List<ArtifactRepository> remoteRepositories = new ArrayList<ArtifactRepository>();

        // create project
        MavenProject project;
        project = mavenProjectBuilder.buildFromRepository( artifact, remoteRepositories, localRepository );

        // log debug message
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Successfully created Maven project." );
            getLog().debug( message.toString() );
        }

        return project;
    }

    /**
     * Resolve Maven project.
     * 
     * @param project Maven project.
     * @param artifactFactory Maven artifact factory
     * @param resolver Maven artifact resolver.
     * @param localRepository Local repository.
     * @param metadataSource Maven artifact metadata source
     * @throws InvalidDependencyVersionException If project resolution fails.
     * @throws ArtifactResolutionException If project resolution fails.
     * @throws ArtifactNotFoundException If project resolution fails.
     */
    public void resolveProject( MavenProject project, ArtifactFactory artifactFactory, ArtifactResolver resolver,
                                ArtifactRepository localRepository, ArtifactMetadataSource metadataSource )
        throws InvalidDependencyVersionException, ArtifactResolutionException, ArtifactNotFoundException
    {
        // validate parameters
        Validate.notNull( project, "project is undefined." );
        Validate.notNull( artifactFactory, "artifactFactory is undefined." );
        Validate.notNull( resolver, "resolver is undefined." );
        Validate.notNull( localRepository, "localRepository is undefined." );        
        Validate.notNull( metadataSource, "metadataSource is undefined." );        
        
        // get artifact
        Artifact artifact;
        artifact = project.getArtifact();

        // get remote repositories
        List remoteRepositories = project.getRemoteArtifactRepositories();
        
        // log debug message
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Resolving artifact <" );
            message.append( artifact.getGroupId() );
            message.append( ":" );
            message.append( artifact.getArtifactId() );
            message.append( ":" );
            message.append( artifact.getType() );
            message.append( ":" );
            message.append( artifact.getBaseVersion() );
            message.append( "> with local repository <" );
            message.append( localRepository );
            message.append( "> and remote repositories <" );
            message.append( ReflectionToStringBuilder.toString( remoteRepositories ) );
            message.append( ">. " );
            getLog().debug( message.toString() );
        }

        // create sub artifacts for current project
        Set artifacts = project.createArtifacts( artifactFactory, null, null );

        // resolve dependencies transitively
        ArtifactResolutionResult result;
        result = resolver.resolveTransitively( artifacts, artifact, remoteRepositories, localRepository, metadataSource );

        // log debug message
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message = new StringBuilder();
            message.append( "Sucessfully resolved Maven project." );
            getLog().debug( message.toString() );
        }
    }

    /**
     * Get execution properties from Maven session.
     * 
     * @param session Maven session object.
     * 
     * @return Maven execution properties object.
     * 
     * @throws MojoExecutionException If execution properties is undefined in session. 
     *  
     */
    public Properties getExecutionProperties(MavenSession session) throws MojoExecutionException  {
        
        // validate parameters        
        Validate.notNull( session, "session is undefined." );
        
        // get properties
        Properties properties;
        properties = session.getExecutionProperties();
        
        // if properties object is defined, then return it
        if(properties != null) {
            return properties;            
        }
                
        // else throw an exception
        // - create error message
        StringBuilder message = new StringBuilder();
        message.append( "Execution properties is undefined in Maven session <" );
        message.append( ReflectionToStringBuilder.toString( session ) );
        message.append( ">." );
                
        // - throw exception
        throw new MojoExecutionException( message.toString());                                        
    }
    
    /**
     * Get user.home system property from Maven execution properties.
     * 
     * @param session Maven session object which contains execution properties. 
     * 
     * @return user.home system property.
     * 
     * @throws MojoExecutionException If property retrieval fails.
     */
    public String getUserHomeProperty(MavenSession session) throws MojoExecutionException
    {
        // validate parameters        
        Validate.notNull( session, "session is undefined." );
        
        // get execution properties
        Properties executionProperties;
        executionProperties = getExecutionProperties( session );
               
        // get user.home
        String property;
        property = executionProperties.getProperty( PluginConstants.USER_HOME );
                        
        // log debug message
        if ( getLog().isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Read user.home property from Maven execution properties <" );
            message.append( property );
            message.append( ">." );
            getLog().debug( message.toString() );
        }
                
        return property;        
    }

}
