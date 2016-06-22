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

import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProjectBuilder;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.testutils.ObjectMotherMojo;

public class MavenHelperTest extends AbstractMojoTestCase
{
    /**
     * Maven log object.
     */
    DefaultLog mojoLogger;

    /**
     * Object mother for mojos.
     */
    ObjectMotherMojo mojoMother;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        // create object mother for mojos.
        mojoMother = new ObjectMotherMojo();

        // create and configure mojo logger
        mojoLogger = mojoMother.createMojoLogger( container );
    }

    @Override
    protected void tearDown() throws Exception
    {
        mojoMother = null;

        super.tearDown();
    }

    /**
     * Test that helper can be created.
     * 
     * @throws Exception
     */
    public void testCanCreateInstance() throws Exception
    {
        // create object
        MavenHelper helper = new MavenHelper( mojoLogger );

        // test
        assertNotNull( helper );
    }

    /**
     * Constructor rejects undefined logger.
     */
    public void testConstructorRejectsUndefinedlogger()
    {
        try
        {
            // create object
            MavenHelper helper = new MavenHelper( null );

            fail( "Test should never reach here." );

        }
        catch ( IllegalArgumentException e )
        {
            assertTrue( true );
        }

    }

    /**
     * Can create Maven embedder.
     */
    public void testCanCreateEmbbedder() throws Exception
    {
        // create object
        MavenHelper helper = new MavenHelper( mojoLogger );

        // create embedder
        MavenEmbedder maven = helper.createMavenEmbedder();

        // test - artifact is defined;
        assertNotNull( maven );
    }

    /**
     * Can create artifact.
     */
    public void testCanCreateArtifact() throws Exception
    {
        // create object
        MavenHelper helper = new MavenHelper( mojoLogger );

        // lookup artifact factory
        ArtifactFactory factory = (ArtifactFactory) lookup( ArtifactFactory.ROLE );

        // create artifact
        Artifact artifact = helper.createArtifact( factory );

        // test - artifact is defined;
        assertNotNull( artifact );
    }

    /**
     * Create-project rejects undefined factory.
     */
    public void testCreateArtifactRejectsUndefinedFactory()
    {
        try
        {
            // create object
            MavenHelper helper = new MavenHelper( mojoLogger );

            // invoke to force exception
            Artifact artifact = helper.createArtifact( null );

            fail( "Test should never reach here." );

        }
        catch ( IllegalArgumentException e )
        {
            assertTrue( true );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }

    /**
     * Create-project rejects undefined repository.
     */
    public void testCreateProjectRejectsUndefinedRepository()
    {
        try
        {
            // create object
            MavenHelper helper = new MavenHelper( mojoLogger );

            // lookup artifact factory
            ArtifactFactory factory = (ArtifactFactory) lookup( ArtifactFactory.ROLE );

            // create artifact
            Artifact artifact = helper.createArtifact( factory );

            // lookup project builder
            MavenProjectBuilder projectBuilder;
            projectBuilder = (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE );

            helper.createProject( artifact, null, projectBuilder );

            fail( "Test should never reach here." );

        }
        catch ( IllegalArgumentException e )
        {
            assertTrue( true );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }

    }

    /**
     * Can get execution properties.
     */
    public void testCanGetExecutionProperties() throws Exception
    {
        // create object
        MavenHelper helper = new MavenHelper( mojoLogger );

        // create execution properties
        Properties executionProperties;
        executionProperties = new Properties();

        // create maven session
        MavenSession session = mojoMother.createSession( container, executionProperties );

        // get execution properties
        Properties properties = helper.getExecutionProperties( session );

        // test - properties is defined;
        assertEquals( executionProperties, properties );

    }

    /**
     * Helper rejects undefined session.
     */
    public void testGetExecutionPropertiesRejectsUndefinedSession()
    {
        try
        {
            // create object
            MavenHelper helper = new MavenHelper( mojoLogger );

            // get execution properties to force exception
            Properties properties = helper.getExecutionProperties( null );

            fail( "Test should never reach here." );

        }
        catch ( IllegalArgumentException e )
        {
            assertTrue( true );
        }
        catch ( MojoExecutionException e )
        {
            fail( "Test should never reach here." );
        }
    }

    /**
     * get-execution-properties fails if session doens't contain properties object.
     */
    public void testGetExecutionPropertiesFailsIfSessionDoesntContainProperties()
    {
        try
        {
            // create object
            MavenHelper helper = new MavenHelper( mojoLogger );

            // create maven session
            MavenSession session = mojoMother.createSessionWithUndefinedExecutionProps( container );

            // get execution properties to force exception
            Properties properties = helper.getExecutionProperties( session );

            fail( "Test should never reach here." );

        }
        catch ( MojoExecutionException e )
        {
            assertTrue( true );
        }
        catch ( Throwable e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test user.home property is defined.
     */
    public void testUserHomePropertyIsDefined() throws Exception
    {
        // create object
        MavenHelper helper = new MavenHelper( mojoLogger );

        // create execution properties
        Properties executionProperties;
        executionProperties = mojoMother.createExecutionProperties();

        // create maven session
        MavenSession session = mojoMother.createSession( container, executionProperties );

        // get user.home
        String userHome = helper.getUserHomeProperty( session );

        // test
        assertNotNull( userHome );
    }

    /**
     * Test user.home property is not empty string
     */
    public void testUserHomePropertyIsNotEmpty() throws Exception
    {
        // create object
        MavenHelper helper = new MavenHelper( mojoLogger );

        // create execution properties
        Properties executionProperties;
        executionProperties = mojoMother.createExecutionProperties();

        // create maven session
        MavenSession session = mojoMother.createSession( container, executionProperties );

        // get user.home
        String userHome = helper.getUserHomeProperty( session );

        // test
        assertTrue( userHome.length() != 0 );
    }

    /**
     * Test user.home property is equal to user.home system property.
     */
    public void testUserHomePropertyIsEqualToSystemProperty() throws Exception
    {
        // create object
        MavenHelper helper = new MavenHelper( mojoLogger );

        // create execution properties
        Properties executionProperties;
        executionProperties = mojoMother.createExecutionProperties();

        // create maven session
        MavenSession session = mojoMother.createSession( container, executionProperties );

        // get user.home
        String userHome = helper.getUserHomeProperty( session );

        // get user.home from system properties
        String expected = System.getProperty( PluginConstants.USER_HOME );

        // test
        assertEquals( expected, userHome );
    }

    /**
     * Test that get-user-home-property method rejects undefined session.
     */
    public void testGetUserHomePropertyRejectsUndefinedSession() throws Exception
    {
        try
        {

            // create object
            MavenHelper helper = new MavenHelper( mojoLogger );

            // invoke to force exception
            String userHome = helper.getUserHomeProperty( null );

            fail( "Test should never reach here." );
        }
        catch ( IllegalArgumentException  e )
        {
            assertTrue( true );
        }
        catch ( Throwable e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

}
