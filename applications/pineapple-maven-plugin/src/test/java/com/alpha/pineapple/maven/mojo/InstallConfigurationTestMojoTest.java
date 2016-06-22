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
import java.util.Properties;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProjectBuilder;
import org.junit.runner.RunWith;

/*
import com.alpha.testutils.NameAwareTestClassRunner;
import com.alpha.testutils.NamesFactory;
import com.alpha.testutils.ObjectMotherConfiguration;
import com.alpha.testutils.ObjectMotherIO;
*/
import com.alpha.testutils.ObjectMotherMojo;
/*
import com.alpha.testutils.ObjectMotherPineappleUnit;
import com.alpha.testutils.ObjectMotherPineappleUnitConfiguration;
import com.alpha.testutils.UnittestConstants;
import com.alpha.testutils.UnittestConstantsCore;
*/

/**
 * Unit test of class {@link DeployConfigurationMojoTest}.
 * 
 * Implementation note: The test is run with the test runner 
 * [{@link NameAwareTestClassRunner} which provides the name and
 * method name of the current test. This information is used
 * to create directories and files for testing purposes. 
 */
//@RunWith( NameAwareTestClassRunner.class )
public class InstallConfigurationTestMojoTest extends AbstractMojoTestCase
{
    /**
     * Mojo under test.
     */
    DeployConfigurationMojo mojo;
    
    /**
     * Object mother for mojos.
     */
    ObjectMotherMojo mojoMother;

    /**
     * Directory which will hold files for the test case.
     */
    //public File testDir;

    /**
     * Name of the test method.
     */
    //public String testMethodName;
    
    /**
     * Delete directories after test execution
     */
    //public boolean deleteDirectories =  false;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        /**
        // get name of current test method
        testMethodName = NameAwareTestClassRunner.getTestName();

        // create test dir name
        String dirName = NamesFactory.createFQTestName( this, testMethodName );

        // create directory for test
        testDir = ObjectMotherIO.createTestMethodDirectory( dirName );
        
         **/
                
        // create object mother for mojos.
        mojoMother =  new ObjectMotherMojo();
        
        // create mojo
        mojo = new DeployConfigurationMojo();
        
        // configure mojo logger
        mojo.setLog( mojoMother.createMojoLogger( container ));        
        
        // lookup artifact factory
        ArtifactFactory factory = (ArtifactFactory) lookup( ArtifactFactory.ROLE );
        setVariableValueToObject( mojo, "artifactFactory", factory  );

        // lookup project builder 
        MavenProjectBuilder projectBuilder;
        projectBuilder = (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE ) ;
        setVariableValueToObject( mojo, "projectBuilder", projectBuilder );
        
        // create local repository
        ArtifactRepository localRepository;
        localRepository = mojoMother.createArtifactRepository( container );
        
        // create maven execution properties
        Properties executionProperties;
        executionProperties = mojoMother.createExecutionProperties();        
        
        // create maven session
        MavenSession session = mojoMother.createSession( container, executionProperties );         
        setVariableValueToObject( mojo, "session", session );

        // set null settings
        setVariableValueToObject( mojo, "settings", null );        
        
        // lookup resolver
        ArtifactResolver artifactResolver = (ArtifactResolver) lookup( ArtifactResolver.ROLE );        
        setVariableValueToObject( mojo, "resolver", artifactResolver);
        
        // lookup metadata source
        ArtifactMetadataSource metadataSource = (ArtifactMetadataSource) lookup( ArtifactMetadataSource.ROLE );        
        setVariableValueToObject( mojo, "metadataSource", metadataSource);                
    }
        
    @Override
    protected void tearDown() throws Exception
    {        
        // delete objects
        mojo = null;        
        mojoMother = null;
     
        /**
        if (deleteDirectories) {
            // delete directories and files
            ObjectMotherIO.deleteDirectory( testDir );
        }
        **/
        
        super.tearDown();
    }

    /**
     * Test that mojo can be run.
     * 
     * @throws Exception
     */
    public void testCanRunMojo() throws Exception
    {
        /**
        // configure runtime root directory
        // i.e. <test-case-dir>\<app>-<version>-management.dir        
        StringBuilder rootDirName = new StringBuilder();
        rootDirName.append( this.testDir );
        rootDirName.append( File.separatorChar );        
        rootDirName.append( UnittestConstants.pineappleUnitName );        
        rootDirName.append( "-management.dir" );
        
        // create runtime root directory
        File rootDir = new File( rootDirName.toString() );        
        rootDir.mkdirs();
        
        // configuration object mother
        ObjectMotherConfiguration configMother = new ObjectMotherConfiguration();
        
        // create object mother       
        ObjectMotherPineappleUnitConfiguration pucMother;
        pucMother = new ObjectMotherPineappleUnitConfiguration();
        
        // create Pineapple-Unit-Configuration
        XMLConfiguration config;
        config = pucMother.createPineappleUnitConfigurationWithEnvironment();        
        
        // create Pineapple-Unit
        String moduleName;
        moduleName = UnittestConstantsCore.pineappleUnitName;
        ObjectMotherPineappleUnit mcMother;
        mcMother = new ObjectMotherPineappleUnit();
        mcMother.createNullPineappleUnit( moduleName, rootDir );        
        mcMother.addXMLFile( UnittestConstants.Environment.LOCAL, config );
        mcMother.addCommonXMLFile( configMother.createNullXMLConfiguration() );
                        
        // set group id
        setVariableValueToObject( mojo, "environment", UnittestConstants.platformEnvironment );
        
        // set project final name 
        setVariableValueToObject( mojo, "finalName", UnittestConstants.pineappleUnitName );        
        
        // set project build directory  
        setVariableValueToObject( mojo, "buildDirectory", this.testDir );
        */
                
        // execute mojo
        mojo.execute();
    }

}
