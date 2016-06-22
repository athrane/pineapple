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


package com.alpha.pineapple.plugin.weblogic.installation.command;

import static com.alpha.pineapple.plugin.weblogic.installation.WeblogicInstallationConstants.SILENT_INSTALL_XML;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

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
import com.alpha.pineapple.plugin.weblogic.installation.response.ResponseFileBuilder;
import com.alpha.pineapple.test.Asserter;
import com.alpha.pineapple.test.matchers.PineappleMatchers;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Unit test for the {@linkplain CreateSilentXmlCommand}class.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( {DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ContextConfiguration( locations = { "/com.alpha.pineapple.testutils-config.xml" } )
public class CreateSilentXmlCommandTest {

	/**
	 * End search marker.
	 */
	static final String SEARCH_MARKER_END = ")";

	/**
	 * Start search marker.
	 */
	static final String SEARCH_MARKER_START= "(";

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
	 * Mock response file builder for product.
	 */
	ResponseFileBuilder responseFileBuilder;
	
	/**
	 * Random Middleware home.
	 */
	File randomMwHome;

	/**
	 * Random JVM home.
	 */
	File randomJvmHome;
	
	/**
	 * Random WebLogic home.
	 */
	File randomWlsHome;

	/**
	 * Random temp. directory.
	 */	
	File randomTempDirectory;    
    
	@Before
	public void setUp() throws Exception {
				
        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// generate names 
		randomMwHome = new File( testDirectory, RandomStringUtils.randomAlphabetic(10)+"-mwhome" ); 
		randomWlsHome = new File( randomMwHome, RandomStringUtils.randomAlphabetic(10)+"-wlshome" ); 
		randomJvmHome = new File(RandomStringUtils.randomAlphabetic(10)+"-jvmhome");         
	    
		// create temp. directory
		randomTempDirectory = new File( testDirectory, RandomStringUtils.randomAlphabetic(10) );
	    assertTrue(randomTempDirectory.mkdirs());
				
    	// create command
		command = new CreateSilentXmlCommand();

		// create context
		context = new ContextBase();

        // create mocks
        executionResult = EasyMock.createMock( ExecutionResult.class );
        asserter = EasyMock.createMock( Asserter.class );
        messageProvider = EasyMock.createMock( MessageProvider.class );
        responseFileBuilder = EasyMock.createMock( ResponseFileBuilder.class );

        // reset mock provider
        EasyMock.reset(coreRuntimeDirectoryProvider);
        
        // inject mocks
        ReflectionTestUtils.setField( command, "asserter", asserter );
        ReflectionTestUtils.setField( command, "coreRuntimeDirectoryProvider", coreRuntimeDirectoryProvider );                
        ReflectionTestUtils.setField( command, "responseFileBuilder", responseFileBuilder );
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

	/**
	 * Complete mock asserter setup. 
	 */
	void completeAsserterSetup() {
		// complete mock asserter setup
	    asserter.setExecutionResult(executionResult);	    
		EasyMock.expect(asserter.assertObject((File) EasyMock.isA( File.class ), 
				(Matcher) EasyMock.isA( Matcher.class ), 
				(String) EasyMock.isA( String.class ))).andReturn(executionResult);		
		EasyMock.expect(asserter.lastAssertionSucceeded()).andReturn(true);
		EasyMock.replay(asserter);
	}

	/**
	 * Complete mock provider setup.
	 * 
	 * @param tempDirectory Pineapple temporary directory.
	 */
	void completeProviderSetup(File tempDirectory) {
		EasyMock.expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(tempDirectory);
        EasyMock.replay(coreRuntimeDirectoryProvider);
	}

	/**
	 * Complete mock execution result setup.
	 */	
	void completeSuccessfulMockExecutionResultSetup() {
		executionResult.completeAsSuccessful((MessageProvider) EasyMock.isA( MessageProvider.class ),
				(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject());
		EasyMock.replay(executionResult);
	}

	/**
	 * Complete mock response file builder setup.
	 */		
	void completeMockResponseFileBuilderSetup()	{
		// BEA_HOME is interpreted as the target directory minus the last directory		
		Collection<String> lines = new ArrayList<String>();		
		lines.add(SEARCH_MARKER_END+"BEAHOME="+randomMwHome.getAbsolutePath()+SEARCH_MARKER_END);
		lines.add(SEARCH_MARKER_END+"WLS_INSTALL_DIR="+randomWlsHome.getAbsolutePath()+SEARCH_MARKER_END);
		lines.add(SEARCH_MARKER_END+"LOCAL_JVMS="+randomJvmHome.getAbsolutePath()+SEARCH_MARKER_END);
		
		EasyMock.expect(responseFileBuilder.getResponseForInstallation(randomWlsHome, randomJvmHome)).andReturn(lines);
        EasyMock.replay(responseFileBuilder);		
	}
		
	
	/**
	 * Test that silent XML is defined in context after execution.
	 * 
	 * @throws Exception If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSilentXmlIsDefinedInContentAfterExecution() throws Exception {
					    
		completeAsserterSetup();	           
		completeProviderSetup(randomTempDirectory);			
		completeSuccessfulMockExecutionResultSetup();
		completeMockResponseFileBuilderSetup();
		
		// initialize context
		context.put(CreateSilentXmlCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateSilentXmlCommand.LOCAL_JVM, randomJvmHome);
		context.put(CreateSilentXmlCommand.TARGET_DIRECTORY, randomWlsHome);		
		
		// execute command
		command.execute(context);
		
		// Verify mocks		
		EasyMock.verify(executionResult);
		EasyMock.verify(asserter);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		EasyMock.verify(responseFileBuilder);

		// get file object		
		File silentXMLFile = (File) context.get(CreateSilentXmlCommand.SILENTXML_FILE); 
		
		// test 
		assertNotNull(silentXMLFile);		
	}

	/**
	 * Test that silent XML file is created successfully.
	 * 
	 * @throws Exception If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSilentXmlIsCreated() throws Exception {
	    
		completeAsserterSetup();	           
		completeProviderSetup(randomTempDirectory);
		completeSuccessfulMockExecutionResultSetup();
		completeMockResponseFileBuilderSetup();
		
		// initialize context
		context.put(CreateSilentXmlCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateSilentXmlCommand.LOCAL_JVM, randomJvmHome);
		context.put(CreateSilentXmlCommand.TARGET_DIRECTORY, randomWlsHome);		
		
		// execute command
		command.execute(context);
		
		// Verify mocks		
		EasyMock.verify(executionResult);
		EasyMock.verify(asserter);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		EasyMock.verify(responseFileBuilder);

		// create file object		
		File silentXMLFile = new File(randomTempDirectory, SILENT_INSTALL_XML);
		
		// verify silent XML exists
		assertTrue(silentXMLFile.exists());		
	}

	
	/**
	 * Test that silent XML file contains expected BEA Home.
	 * 
	 * @throws Exception If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSilentXmlContainsCorrectBeaHome() throws Exception {
	    
		completeAsserterSetup();	           
		completeProviderSetup(randomTempDirectory);
		completeSuccessfulMockExecutionResultSetup();
		completeMockResponseFileBuilderSetup();
		
		// initialize context
		context.put(CreateSilentXmlCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateSilentXmlCommand.LOCAL_JVM, randomJvmHome);
		context.put(CreateSilentXmlCommand.TARGET_DIRECTORY, randomWlsHome);		
		
		// execute command
		command.execute(context);
		
		// Verify mocks		
		EasyMock.verify(executionResult);
		EasyMock.verify(asserter);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		EasyMock.verify(responseFileBuilder);

		// create file object		
		File silentXmlFile = new File(randomTempDirectory, SILENT_INSTALL_XML);
		
		// verify silent XML exists
		assertTrue(silentXmlFile.exists());
		
		// load file
		String silentXmlAsString = FileUtils.readFileToString(silentXmlFile);
		
		// get BEA home value
		//String value = StringUtils.substringBetween(silentXmlAsString, "name=\"BEAHOME\" value=\"", "\" />");
		String value = StringUtils.substringBetween(silentXmlAsString, SEARCH_MARKER_END+"BEAHOME=",SEARCH_MARKER_END );		
		
		// test value
		assertEquals(randomMwHome.getAbsolutePath(), value);
		
	}

	/**
	 * Test that silent XML file contains expected WebLogic Home.
	 * 
	 * @throws Exception If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSilentXmlContainsCorrectWeblogicHome() throws Exception {
	    
	    // create temp directory
	    File tempDirectory = new File( testDirectory, "temp" );
	    assertTrue(tempDirectory.mkdirs());

		completeAsserterSetup();	           
		completeProviderSetup(tempDirectory);			
		completeSuccessfulMockExecutionResultSetup();
		completeMockResponseFileBuilderSetup();
		
		// initialize context
		context.put(CreateSilentXmlCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateSilentXmlCommand.LOCAL_JVM, randomJvmHome);
		context.put(CreateSilentXmlCommand.TARGET_DIRECTORY, randomWlsHome);		
		
		// execute command
		command.execute(context);
		
		// Verify mocks		
		EasyMock.verify(executionResult);
		EasyMock.verify(asserter);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		EasyMock.verify(responseFileBuilder);

		// create file object		
		File silentXmlFile = new File(tempDirectory, SILENT_INSTALL_XML);
		
		// verify silent XML exists
		assertTrue(silentXmlFile.exists());
		
		// load file
		String silentXmlAsString = FileUtils.readFileToString(silentXmlFile);
		
		// get Weblogic home value		
		//String value = StringUtils.substringBetween(silentXmlAsString, "name=\"WLS_INSTALL_DIR\" value=\"", "\" />");
		String value = StringUtils.substringBetween(silentXmlAsString, SEARCH_MARKER_END+"WLS_INSTALL_DIR=",SEARCH_MARKER_END );		

		
		// test value
		assertEquals(randomWlsHome.getAbsolutePath(), value);		
	}

	/**
	 * Test that silent XML file contains expected JVM Home.
	 * 
	 * @throws Exception If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSilentXmlContainsCorrectJvmHome() throws Exception {
	    
	    // create temp directory
	    File tempDirectory = new File( testDirectory, "temp" );
	    assertTrue(tempDirectory.mkdirs());

		completeAsserterSetup();	           
		completeProviderSetup(tempDirectory);			
		completeSuccessfulMockExecutionResultSetup();
		completeMockResponseFileBuilderSetup();
		
		// initialize context
		context.put(CreateSilentXmlCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateSilentXmlCommand.LOCAL_JVM, randomJvmHome);
		context.put(CreateSilentXmlCommand.TARGET_DIRECTORY, randomWlsHome);		
		
		// execute command
		command.execute(context);
		
		// Verify mocks		
		EasyMock.verify(executionResult);
		EasyMock.verify(asserter);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		EasyMock.verify(responseFileBuilder);

		// create file object		
		File silentXmlFile = new File(tempDirectory, SILENT_INSTALL_XML);
		
		// verify silent XML exists
		assertTrue(silentXmlFile.exists());
		
		// load file
		String silentXmlAsString = FileUtils.readFileToString(silentXmlFile);
		
		// get Weblogic home value		
		//String value = StringUtils.substringBetween(silentXmlAsString, "name=\"WLS_INSTALL_DIR\" value=\"", "\" />");
		String value = StringUtils.substringBetween(silentXmlAsString, SEARCH_MARKER_END+"LOCAL_JVMS=",SEARCH_MARKER_END );		

		
		// test value
		assertEquals(randomJvmHome.getAbsolutePath(), value);
		
	}
	
}
