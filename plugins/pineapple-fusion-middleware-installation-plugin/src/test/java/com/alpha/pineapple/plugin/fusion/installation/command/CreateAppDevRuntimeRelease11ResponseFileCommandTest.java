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


package com.alpha.pineapple.plugin.fusion.installation.command;

import static com.alpha.pineapple.plugin.fusion.installation.FusionMiddlewareInstallationConstants.RESPONSE_INSTALL_FILE;
import static org.junit.Assert.*;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.test.Asserter;
import com.alpha.pineapple.test.matchers.PineappleMatchers;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Unit test for the {@linkplain CreateAdfRuntimeRelease10SilentXmlCommand}class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( {DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ContextConfiguration( locations = { "/com.alpha.pineapple.testutils-config.xml" } )
public class CreateAppDevRuntimeRelease11ResponseFileCommandTest {

	/**
     * Current test directory.
     */
	File testDirectory;
	
    /**
     * object under test
     */
    Command command;

	/**
	 * Context.
	 */
	Context context;

    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;    

    /**
     * Mock runtime directory provider.
     */
    @Resource
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;
    
    /**
     * Mock Hamcrest asserter.
     */
    Asserter asserter;

    /**
     * Mock Pineapple matcher factory.
     */
    PineappleMatchers pineappleMatchers;
    
	/**
	 * Mock execution result.
	 */	
	ExecutionResult executionResult;

	/**
	 * Random BEA home.
	 */
	String randomBeaHome;

	/**
	 * Random JVM home.
	 */
	String randomJvmHome;
	
	/**
	 * Random WebLogic home.
	 */
	String randomWlsHome;    
    
	@Before
	public void setUp() throws Exception {
		
		// generate names 
		randomBeaHome = RandomStringUtils.randomAlphabetic(10)+"-beahome";
		randomWlsHome = RandomStringUtils.randomAlphabetic(10)+"-wlshome";
		randomJvmHome = RandomStringUtils.randomAlphabetic(10)+"-jvmhome";				
		
        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();
		
    	// create command
		//command = new CreateAppDevRuntimeR11InstallResponseFileCommand();

		// create context
		context = new ContextBase();

        // create execution result
        executionResult = EasyMock.createMock( ExecutionResult.class );

        // create mock asserter
        asserter = EasyMock.createMock( Asserter.class );
        
        // inject asserter
        ReflectionTestUtils.setField( command, "asserter", asserter );

        // reset mock provider
        EasyMock.reset(coreRuntimeDirectoryProvider);
        
        // inject mock plugin provider
        ReflectionTestUtils.setField( command, "coreRuntimeDirectoryProvider", coreRuntimeDirectoryProvider );
                
        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
        
        // inject message source
        ReflectionTestUtils.setField( command, "messageProvider", messageProvider );
        
        // complete mock source initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl(); 

        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class )));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject()));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        EasyMock.replay(messageProvider);                                        
        
	}

	@After
	public void tearDown() throws Exception {
	}

	void completeAsserterSetup() {
		// complete mock asserter setup
	    asserter.setExecutionResult(executionResult);	    
		EasyMock.expect(asserter.assertObject((File) EasyMock.isA( File.class ), 
				(Matcher) EasyMock.isA( Matcher.class ), 
				(String) EasyMock.isA( String.class ))).andReturn(executionResult);		
		EasyMock.expect(asserter.lastAssertionSucceeded()).andReturn(true);
		EasyMock.replay(asserter);
	}

	void completeProviderSetup(File tempDirectory) {
		// complete mock provider setup
		EasyMock.expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(tempDirectory);
        EasyMock.replay(coreRuntimeDirectoryProvider);
	}

	void completeSuccessfulMockExecutionResultSetup() {
		// complete mock execution result setup
		executionResult.completeAsSuccessful((MessageProvider) EasyMock.isA( MessageProvider.class ),
				(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject());
		EasyMock.replay(executionResult);
	}
	
	/**
	 * Test that command can be execute successfully.
	 * 
	 * @throws Exception If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanExecuteCommandSuccessfully() throws Exception {
				
	    File beaHome = new File( testDirectory, randomBeaHome );
	    File wlsHome = new File( beaHome, randomWlsHome );
	    
	    // create temp directory
	    File tempDirectory = new File( testDirectory, "temp" );
	    assertTrue(tempDirectory.mkdirs());

		completeAsserterSetup();	           
		completeProviderSetup(tempDirectory);			
		completeSuccessfulMockExecutionResultSetup(); 
		
		// initialize context
		//context.put(CreateAppDevRuntimeR11InstallResponseFileCommand.EXECUTIONRESULT_KEY, executionResult);
		//context.put(CreateAppDevRuntimeR11InstallResponseFileCommand.LOCAL_JVM, randomJvmHome);
		//context.put(CreateAppDevRuntimeR11InstallResponseFileCommand.TARGET_DIRECTORY, wlsHome.getAbsolutePath());		
		
		// execute command
		command.execute(context);
		
		// Verify mocks		
		EasyMock.verify(executionResult);
		EasyMock.verify(asserter);
		EasyMock.verify(coreRuntimeDirectoryProvider);

		// create file object		
		File silentXMLFile = new File(tempDirectory, RESPONSE_INSTALL_FILE);
		
		// verify silent XML exists
		assertTrue(silentXMLFile.exists());		
	}

	/**
	 * Test that silent XML file is created successfully.
	 * 
	 * @throws Exception If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSilentXmlIsCreated() throws Exception {
				
	    File beaHome = new File( testDirectory, randomBeaHome );
	    File wlsHome = new File( beaHome, randomWlsHome );
	    
	    // create temp directory
	    File tempDirectory = new File( testDirectory, "temp" );
	    assertTrue(tempDirectory.mkdirs());

		completeAsserterSetup();	           
		completeProviderSetup(tempDirectory);			
		completeSuccessfulMockExecutionResultSetup(); 
		
		// initialize context
		//context.put(CreateAppDevRuntimeR11InstallResponseFileCommand.EXECUTIONRESULT_KEY, executionResult);
		//context.put(CreateAppDevRuntimeR11InstallResponseFileCommand.LOCAL_JVM, randomJvmHome);
		//context.put(CreateAppDevRuntimeR11InstallResponseFileCommand.TARGET_DIRECTORY, wlsHome.getAbsolutePath());		
		
		// execute command
		command.execute(context);
		
		// Verify mocks		
		EasyMock.verify(executionResult);
		EasyMock.verify(asserter);
		EasyMock.verify(coreRuntimeDirectoryProvider);

		// create file object		
		File silentXMLFile = new File(tempDirectory, RESPONSE_INSTALL_FILE);
		
		// verify silent XML exists
		assertTrue(silentXMLFile.exists());		
	}

	
	/**
	 * Test that silent XML file contains expected BEA HOME.
	 * 
	 * @throws Exception If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSilentXmlContainsCorrectBeaHome() throws Exception {
				
	    File beaHome = new File( testDirectory, randomBeaHome );
	    File wlsHome = new File( beaHome, randomWlsHome );
	    
	    // create temp directory
	    File tempDirectory = new File( testDirectory, "temp" );
	    assertTrue(tempDirectory.mkdirs());

		completeAsserterSetup();	           
		completeProviderSetup(tempDirectory);			
		completeSuccessfulMockExecutionResultSetup(); 
		
		// initialize context
		//context.put(CreateAppDevRuntimeR11InstallResponseFileCommand.EXECUTIONRESULT_KEY, executionResult);
		//context.put(CreateAppDevRuntimeR11InstallResponseFileCommand.LOCAL_JVM, randomJvmHome);
		//context.put(CreateAppDevRuntimeR11InstallResponseFileCommand.TARGET_DIRECTORY, wlsHome.getAbsolutePath());		
		
		// execute command
		command.execute(context);
		
		// Verify mocks		
		EasyMock.verify(executionResult);
		EasyMock.verify(asserter);
		EasyMock.verify(coreRuntimeDirectoryProvider);

		// create file object		
		File silentXmlFile = new File(tempDirectory, RESPONSE_INSTALL_FILE);
		
		// verify silent XML exists
		assertTrue(silentXmlFile.exists());
		
		// load file
		String silentXmlAsString = FileUtils.readFileToString(silentXmlFile);
		
		// get BEA home value
		String value = StringUtils.substringBetween(silentXmlAsString, "name=\"BEAHOME\" value=\"", "\" />");
		
		// test value
		assertEquals(beaHome.getAbsolutePath(), value);
		
	}

	/**
	 * Test that silent XML file contains expected WebLogic HOME.
	 * 
	 * @throws Exception If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSilentXmlContainsCorrectWeblogicHome() throws Exception {
				
	    File beaHome = new File( testDirectory, randomBeaHome );
	    File wlsHome = new File( beaHome, randomWlsHome );
	    
	    // create temp directory
	    File tempDirectory = new File( testDirectory, "temp" );
	    assertTrue(tempDirectory.mkdirs());

		completeAsserterSetup();	           
		completeProviderSetup(tempDirectory);			
		completeSuccessfulMockExecutionResultSetup(); 
		
		// initialize context
		//context.put(CreateAppDevRuntimeR11InstallResponseFileCommand.EXECUTIONRESULT_KEY, executionResult);
		//context.put(CreateAppDevRuntimeR11InstallResponseFileCommand.LOCAL_JVM, randomJvmHome);
		//context.put(CreateAppDevRuntimeR11InstallResponseFileCommand.TARGET_DIRECTORY, wlsHome.getAbsolutePath());		
		
		// execute command
		command.execute(context);
		
		// Verify mocks		
		EasyMock.verify(executionResult);
		EasyMock.verify(asserter);
		EasyMock.verify(coreRuntimeDirectoryProvider);

		// create file object		
		File silentXmlFile = new File(tempDirectory, RESPONSE_INSTALL_FILE);
		
		// verify silent XML exists
		assertTrue(silentXmlFile.exists());
		
		// load file
		String silentXmlAsString = FileUtils.readFileToString(silentXmlFile);
		
		// get BEA home value
		String value = StringUtils.substringBetween(silentXmlAsString, "name=\"WLS_INSTALL_DIR\" value=\"", "\" />");
		
		// test value
		assertEquals(wlsHome.getAbsolutePath(), value);
		
	}
	
}
