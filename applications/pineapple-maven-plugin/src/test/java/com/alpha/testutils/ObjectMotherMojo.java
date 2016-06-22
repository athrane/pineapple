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

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Properties;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.logging.Logger;

/**
 * Implementation of the ObjectMother pattern. The class provides helper functions for unit testing Maven mojos.
 */
public class ObjectMotherMojo
{
    
    /**
     * ObjectMotherMojo constructor.
     */
    public ObjectMotherMojo() {        
    }
    
    /**
     * Create mojo logger with logging threshold set to debugging.
     * 
     * @param container Plexus container.
     * 
     * @return mojo logger with debug logging threshold.
     */
    public DefaultLog createMojoLogger(PlexusContainer container) {
        
        // set logging threshold in plexus to debug
        container.getLoggerManager().setThreshold( Logger.LEVEL_DEBUG );
        
        // create and configure mojo logger
        DefaultLog mojoLogger;
        mojoLogger = new DefaultLog( container.getLoggerManager().getLoggerForComponent( Mojo.ROLE ) );                                 
    
        return mojoLogger;
    }
    
    /**
     * Creates an instance of {@link ArtifactRepository}.
     * 
     * @param container Plexus container.
 
     * @return A configured instance of {@link DefaultArtifactRepository}.
     * 
     * @throws Exception Creating the object failed.
     */
    public ArtifactRepository createArtifactRepository(PlexusContainer container)
            throws Exception
    {
        // attempt to load settings file from settings file in user.home
        File m2Dir = new File( System.getProperty( "user.home" ), ".m2" );
        File settingsFile = new File( m2Dir, "settings.xml" );
        String localRepo = null;
        if ( settingsFile.exists() )
        {
            Settings settings = new SettingsXpp3Reader().read( new FileReader( settingsFile ) );
            localRepo = settings.getLocalRepository();
        }
        if ( localRepo == null )
        {
            localRepo = System.getProperty( "user.home" ) + "/.m2/repository";
        }
        
        // get layout
        ArtifactRepositoryLayout repositoryLayout =
            (ArtifactRepositoryLayout) container.lookup(ArtifactRepositoryLayout.ROLE, "default" );
        
        // create repository
        return new DefaultArtifactRepository( "local", "file://" + localRepo, repositoryLayout );
    }
    
    /**
     * Create Maven session object.
     * 
     * @param container Plexus container.
     * 
     * @return Maven session object.
     * 
     * @throws Exception if session object initialization fails.
     */
    public MavenSession createSession(PlexusContainer container, Properties executionProperties ) throws Exception {
        
        // create local repository
        ArtifactRepository localRepository;
        localRepository = createArtifactRepository( container );

        // create maven session
        MavenSession session = new MavenSession( container, 
                                                 null, // Settings settings,
                                                 localRepository, 
                                                 null, // EventDispatcher eventDispatcher,
                                                 null, // ReactorManager reactorManager,
                                                 null, // List goals,
                                                 null, // String executionRootDir
                                                 executionProperties, 
                                                 Calendar.getInstance().getTime() );
        
        return session;
    }
    
    /**
     * Create Maven session object with undefined execution properties.
     * 
     * @param container Plexus container.
     * 
     * @return Maven session object.
     * 
     * @throws Exception if session object initialization fails.
     */
    public MavenSession createSessionWithUndefinedExecutionProps(PlexusContainer container) throws Exception {
        return createSession( container, null );
    }
    
    /**
     * Create Maven execution properties initialized with the JVM system properties.
     * 
     * @return Maven execution properties initialized with the JVM system properties.
     */
    public Properties createExecutionProperties() {
       
        // create properties
        Properties executionProperties = new Properties();
        
        // add system properties
        executionProperties.putAll( System.getProperties() );

        return executionProperties;               
    }
    
}
