/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2013 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package com.alpha.pineapple.plugin.weblogic.scriptingtool.utils;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.easymock.IAnswer;
import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.WeblogicScriptingToolConstants;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.argument.ArgumentBuilder;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.argument.SystemPropertiesArgumentBuilder;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.model.Wlst;
import com.alpha.pineapple.test.matchers.PineappleMatchers;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Unit test of the {@linkplain ScriptHelperImpl} class.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( DirectoryTestExecutionListener.class )
public class ScriptHelperTest {

	/**
	 * No demo trust constant.
	 */
	final static boolean NO_DEMO_TRUST = false;

	/**
	 * Enable demo trust constant.
	 */
	final static boolean ENABLE_DEMO_TRUST = true;
	
	/**
	 * No fast random generator constant.
	 */
	final static boolean NO_FAST_RANDOM_GEN = false;

	/**
	 * Enable fast random generator constant.
	 */
	final static boolean ENABLE_FAST_RANDOM_GEN = true;
	
    /**
     * Current test directory.
     */
    File testDirectory;
	
	/**
	 * Subject under test.
	 */
    ScriptHelperImpl scriptHelper;

    /**
     * Object mother for the WLST model.
     */
    ObjectMotherContent contentMother;
    
    /**
     * Message provider for I18N support.
     */
    MessageProvider messageProvider;
	            
    /**
     * Runtime directory provider.
     */
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;
        
    /**
     * Execution info provider.
     */
    ExecutionInfoProvider coreExecutionInfoProvider;
    	
    /**
     * Pineapple matcher factory.
     */
    PineappleMatchers pineappleMatchers;
    
    /**
     * Java System properties.
     */
    Properties systemProperties;

    /**
     * Java system utilities.
     */
    SystemUtils systemUtils;
    
    /**
     * Argument builder.
     */
    ArgumentBuilder argumentBuilder;           

    /**
     * Argument builder.
     */
    SystemPropertiesArgumentBuilder systemPropertiesArgumentBuilder;           

    /**
     * ExecutionResult.
     */
    ExecutionResult executionResult;           
    
    /**
     * Random dir.
     */
    File randomDir;

    /**
     * Random WLST script.
     */
	File randomWlst;

    /**
     * Random string.
     */
    String randomName;
	
    /**
     * Random string.
     */
    String randomName2;
	
	@Before
	public void setUp() throws Exception {

        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		randomDir = new File(testDirectory, RandomStringUtils.randomAlphabetic(10));		
		randomWlst = new File(randomDir, RandomStringUtils.randomAlphabetic(10));
		randomName = RandomStringUtils.randomAlphabetic(10);
		randomName2 = RandomStringUtils.randomAlphabetic(10);

		// create directory
		FileUtils.forceMkdir(randomDir);
        		
        // create content mother
        contentMother = new ObjectMotherContent();		        
		
		// create helper
		scriptHelper = new ScriptHelperImpl();
		
        // create mocks
        messageProvider = EasyMock.createMock( MessageProvider.class );
        coreRuntimeDirectoryProvider = EasyMock.createMock(RuntimeDirectoryProvider.class);
        coreExecutionInfoProvider = EasyMock.createMock(ExecutionInfoProvider.class);
        systemProperties = createMock( Properties.class );
        systemUtils = createMock( SystemUtils.class );
        systemPropertiesArgumentBuilder = EasyMock.createMock(SystemPropertiesArgumentBuilder.class);
        argumentBuilder = EasyMock.createMock(ArgumentBuilder.class);
        executionResult = EasyMock.createMock(ExecutionResult.class);
                       
        // inject mocks operation
        ReflectionTestUtils.setField( scriptHelper, "messageProvider", messageProvider);
        ReflectionTestUtils.setField( scriptHelper, "coreRuntimeDirectoryProvider", coreRuntimeDirectoryProvider);
        ReflectionTestUtils.setField( scriptHelper, "coreExecutionInfoProvider", coreExecutionInfoProvider);
        ReflectionTestUtils.setField( scriptHelper, "systemProperties", systemProperties );
        ReflectionTestUtils.setField( scriptHelper, "systemUtils", systemUtils);        
        ReflectionTestUtils.setField( scriptHelper, "systemPropertiesArgumentBuilder", systemPropertiesArgumentBuilder);
        ReflectionTestUtils.setField( scriptHelper, "argumentBuilder", argumentBuilder);        

        
        // complete mock source initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl(); 
        
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject()));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class )));        
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        EasyMock.replay(messageProvider);                                
		
	}

	@After
	public void tearDown() throws Exception {
	}

	
	/**
	 * Test correct response for model with no content.
	 */
	@Test
	public void testIsContentDefinedForModelWithNoContent() {
		
        // create content
        Wlst model = contentMother.createEmptyWlst();
		
        // test
        assertFalse(scriptHelper.isModelContentDefined(model));
	}

	
	/**
	 * Test correct response for model with content.
	 */
	@Test
	public void testIsContentDefinedForModelWithContent() {
		
        // create content
        Wlst model = contentMother.createWlstDocument(
        		RandomStringUtils.randomAlphabetic(10), 
        		RandomStringUtils.randomAlphabetic(10));
		
        // test
        assertTrue(scriptHelper.isModelContentDefined(model));
	}

	
	/**
	 * Test module path resolution.
	 */
	@Test
	public void testGetModulePath() {

        // complete mock setup				
		ModuleInfo moduleInfo = EasyMock.createMock( ModuleInfo.class );
		EasyMock.expect(moduleInfo.getDirectory()).andReturn(randomDir);		
		EasyMock.replay(moduleInfo);		

        // complete mock setup				
		ExecutionInfo executionInfo = EasyMock.createMock( ExecutionInfo.class );
		EasyMock.expect(executionInfo.getModuleInfo()).andReturn(moduleInfo);				
		EasyMock.replay(executionInfo);		
				
        // complete mock setup		
		expect(coreExecutionInfoProvider.get(executionResult)).andReturn(executionInfo);		
		EasyMock.replay(coreExecutionInfoProvider);
		
		// complete mock setup
		replay(executionResult);
						
		// get module path
		String actual = scriptHelper.getModulePath(executionResult);		
		
		String expected = randomDir.toString();
		
		// test
		assertEquals(expected, actual);
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(executionInfo);
		EasyMock.verify(moduleInfo);		
		EasyMock.verify(executionResult);
	}
	
	
	/**
	 * Test WLST invoker script name can be created for Windows.
	 */
	@Test
	public void testWlstInvokerFileNameForWindows() {

        // complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		
		// complete mock setup
		EasyMock.expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(testDirectory);		
		EasyMock.replay(coreRuntimeDirectoryProvider);
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);
		replay(systemUtils);
				
		// create script name
		File actual = scriptHelper.createWlstInvokerFileName();
		
		String expected = WeblogicScriptingToolConstants.class.getPackage().getName();;
		expected += ".cmd";				
		File expectedFile = new File( testDirectory, expected);
		
		// test
		assertEquals(expectedFile, actual);
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}
	
	/**
	 * Test WLST invoker script name can be created for Linux.
	 */
	@Test
	public void testWlstInvokerFileNameForLinux() {

        // complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		
		// complete mock setup
		EasyMock.expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(testDirectory);		
		EasyMock.replay(coreRuntimeDirectoryProvider);
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(false);
		replay(systemUtils);
				
		// create script name
		File actual = scriptHelper.createWlstInvokerFileName();
		
		String expected = WeblogicScriptingToolConstants.class.getPackage().getName();;
		expected += ".sh";				
		File expectedFile = new File( testDirectory, expected);
		
		// test
		assertEquals(expectedFile, actual);
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test WLST invoker script content for Windows
	 * contains expected number of lines. 
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testWlstInvokerContentForWindowsContainsExpectedLines() throws Exception {

		String[] systemPropsArguments = {};		

		// complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);		
		replay(systemUtils);
				  
		// create script name
		Collection<String> actual = scriptHelper.createInvokerFileContent(randomWlst, systemPropsArguments);
				
		// test
		assertEquals(9, actual.size());
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test WLST invoker script content for Linux
	 * contains expected number of lines. 
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testWlstInvokerContentForLinuxContainsExpectedLines() throws Exception {

		String[] systemPropsArguments = {};		

		// complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(false);		
		replay(systemUtils);
				
		// create script name
		Collection<String> actual = scriptHelper.createInvokerFileContent(randomWlst, systemPropsArguments);
				
		// test
		assertEquals(9, actual.size());
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test WLST invoker script content for Windows
	 * contains expected WLST file. 
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testWlstInvokerContentForWindowsContainsExpectedWlst() throws Exception {

		String[] systemPropsArguments = {};		

		// complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);		
		replay(systemUtils);
				  
		// create script name
		Collection<String> actual = scriptHelper.createInvokerFileContent(randomWlst, systemPropsArguments);
				
		// convert to array		
		String[] actualAsArray= actual.toArray(new String[9]);
		
		// test
		assertTrue(actualAsArray[7].contains(randomWlst.toString()));		
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test WLST invoker script content for Linux
	 * contains expected WLST file. 
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testWlstInvokerContentForLinuxContainsExpectedWlst() throws Exception {

		String[] systemPropsArguments = {};		

		// complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(false);		
		replay(systemUtils);
				  
		// create script name
		Collection<String> actual = scriptHelper.createInvokerFileContent(randomWlst, systemPropsArguments);
				
		// convert to array		
		String[] actualAsArray= actual.toArray(new String[9]);
		
		// test
		assertTrue(actualAsArray[7].contains(randomWlst.toString()));		
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test WLST invoker script content for Windows
	 * contains no system properties. 
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testWlstInvokerContentForWindowsContainsNoSystemProps() throws Exception {

		String[] systemPropsArguments = {};		

		// complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);		
		replay(systemUtils);
				  
		// create script name
		Collection<String> actual = scriptHelper.createInvokerFileContent(randomWlst, systemPropsArguments);
				
		// convert to array		
		String[] actualAsArray= actual.toArray(new String[9]);
		
		// test
		assertEquals("SET WLST_PROPERTIES=%WLST_PROPERTIES% ", actualAsArray[4]);		
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test WLST invoker script content for Linux
	 * contains no system properties. 
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testWlstInvokerContentForLinuxContainsNoSystemProps() throws Exception {

		String[] systemPropsArguments = {};		

		// complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(false);		
		replay(systemUtils);
				  
		// create script name
		Collection<String> actual = scriptHelper.createInvokerFileContent(randomWlst, systemPropsArguments);
				
		// convert to array		
		String[] actualAsArray= actual.toArray(new String[9]);
		
		// test
		assertEquals("WLST_PROPERTIES=\"${WLST_PROPERTIES} "+ "\"", actualAsArray[3]);		
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test WLST invoker script content for Windows
	 * contains expected system properties. 
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testWlstInvokerContentForWindowsContainsExpectedSystemProps() throws Exception {

		String arg1 = randomName+"="+randomName;
		String arg2 = randomName+"="+randomName;		
		String[] systemPropsArguments = { arg1, arg2};
		
        // complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true);		
		replay(systemUtils);
				  
		// create script name
		Collection<String> actual = scriptHelper.createInvokerFileContent(randomWlst, systemPropsArguments);
				
		// convert to array		
		String[] actualAsArray= actual.toArray(new String[9]);
		
		// expected
		StringBuilder expected = new StringBuilder()
			.append("SET WLST_PROPERTIES=%WLST_PROPERTIES%")
			.append(" ").append(arg1)		
			.append(" ").append(arg2);		
		
		// test		 
		assertEquals(expected.toString(), actualAsArray[4]);		
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test WLST invoker script content for Windows
	 * contains expected system properties. 
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testWlstInvokerContentForLinuxContainsExpectedSystemProps() throws Exception {

		String arg1 = randomName+"="+randomName;
		String arg2 = randomName+"="+randomName;		
		String[] systemPropsArguments = { arg1, arg2};
		
        // complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(false);		
		replay(systemUtils);
				  
		// create script name
		Collection<String> actual = scriptHelper.createInvokerFileContent(randomWlst, systemPropsArguments);
				
		// convert to array		
		String[] actualAsArray= actual.toArray(new String[9]);
		
		// expected
		StringBuilder expected = new StringBuilder()
			.append("WLST_PROPERTIES=\"${WLST_PROPERTIES}")
			.append(" ").append(arg1)		
			.append(" ").append(arg2)
			.append("\"");
		
		// test		 
		assertEquals(expected.toString(), actualAsArray[3]);		
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test created WLST invoker script is a file. 
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testCreateWlstInvokerScriptCreatesFile() throws Exception {

		String[] systemPropsArguments = {};		

		// complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(testDirectory);		
		EasyMock.replay(coreRuntimeDirectoryProvider);		
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true).times(2);		
		replay(systemUtils);
				  
		// create script name
		File actual = scriptHelper.createWlstInvokerScript(randomWlst, systemPropsArguments);
				
		// test
		assertTrue(actual.exists());
		assertTrue(actual.isFile());		
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test created WLST invoker script is executable.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testCreateWlstInvokerScriptCreatedFileIsExecutable() throws Exception {

		String[] systemPropsArguments = {};		

		// complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(testDirectory);		
		EasyMock.replay(coreRuntimeDirectoryProvider);		
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true).times(2);		
		replay(systemUtils);
				  
		// create script name
		File actual = scriptHelper.createWlstInvokerScript(randomWlst, systemPropsArguments);
				
		// test
		assertTrue(actual.exists());
		assertTrue(actual.canExecute());		
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test created WLST invoker script is readable and writable.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testCreateWlstInvokerScriptCreatedFileIsReadableAndWritable() throws Exception {

		String[] systemPropsArguments = {};		

		// complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(testDirectory);		
		EasyMock.replay(coreRuntimeDirectoryProvider);		
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true).times(2);		
		replay(systemUtils);
				  
		// create script name
		File actual = scriptHelper.createWlstInvokerScript(randomWlst, systemPropsArguments);
				
		// test
		assertTrue(actual.exists());
		assertTrue(actual.canExecute());		
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test existing WLST invoker script is deleted
	 * prior to creation of WLST invoker script. 
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testExistingWlstInvokerScriptIsDeletedPriorToDeletion() throws Exception {

		String[] systemPropsArguments = {};	
		
		Collection<String> lines = new ArrayList<String>();
		lines.add(randomName);
		lines.add(randomName2);		
		FileUtils.writeLines(randomWlst, lines);		
		
        // complete mock setup		
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(testDirectory);		
		EasyMock.replay(coreRuntimeDirectoryProvider);		
		replay(systemProperties);
		
		// complete mock setup
		expect(systemUtils.isWindowsOperatingSystem(systemProperties)).andReturn(true).times(2);		
		replay(systemUtils);
				  
		// create script name
		File actual = scriptHelper.createWlstInvokerScript(randomWlst, systemPropsArguments);
				
		// test
		assertTrue(actual.exists());
		assertTrue(actual.isFile());		
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		verify(systemProperties);
		verify(systemUtils);
	}

	/**
	 * Test system properties arguments can be created
	 * with demo trust disabled. 
	 */
	@Test
	public void testCreateSystemPropertiesArgumentsWithNoDemoTrust() {
		
		String[] arguments = { randomName, randomName2 };
		
        // complete mock setup				
		ModuleInfo moduleInfo = EasyMock.createMock( ModuleInfo.class );
		EasyMock.expect(moduleInfo.getDirectory()).andReturn(randomDir);		
		EasyMock.replay(moduleInfo);		

        // complete mock setup				
		ExecutionInfo executionInfo = EasyMock.createMock( ExecutionInfo.class );
		EasyMock.expect(executionInfo.getModuleInfo()).andReturn(moduleInfo);				
		EasyMock.replay(executionInfo);		
				
        // complete mock setup		
		expect(coreExecutionInfoProvider.get(executionResult)).andReturn(executionInfo);		
		EasyMock.replay(coreExecutionInfoProvider);
		
		// complete mock setup
		replay(executionResult);
		
		// complete mock setup		
		systemPropertiesArgumentBuilder.buildArgumentList();
		systemPropertiesArgumentBuilder.addPineappleArguments(randomDir);
		EasyMock.expect(systemPropertiesArgumentBuilder.getArgumentList()).andReturn(arguments);				

		EasyMock.replay(systemPropertiesArgumentBuilder);		
						
		// get module path
		String actual[]= scriptHelper.createSystemPropertiesArguments(NO_DEMO_TRUST, NO_FAST_RANDOM_GEN, executionResult);		
		
		// test
		assertEquals(2, actual.length);
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(executionInfo);
		EasyMock.verify(moduleInfo);		
		EasyMock.verify(executionResult);
		EasyMock.verify(systemPropertiesArgumentBuilder);
	}

	/**
	 * Test system properties arguments can be created
	 * with demo trust enabled. 
	 */
	@Test
	public void testCreateSystemPropertiesArgumentsWithDemoTrust() {

		String[] arguments = { randomName, randomName2 };
		
        // complete mock setup				
		ModuleInfo moduleInfo = EasyMock.createMock( ModuleInfo.class );
		EasyMock.expect(moduleInfo.getDirectory()).andReturn(randomDir);		
		EasyMock.replay(moduleInfo);		

        // complete mock setup				
		ExecutionInfo executionInfo = EasyMock.createMock( ExecutionInfo.class );
		EasyMock.expect(executionInfo.getModuleInfo()).andReturn(moduleInfo);				
		EasyMock.replay(executionInfo);		
				
        // complete mock setup		
		expect(coreExecutionInfoProvider.get(executionResult)).andReturn(executionInfo);		
		EasyMock.replay(coreExecutionInfoProvider);
		
		// complete mock setup
		replay(executionResult);
		
		// complete mock setup		
		systemPropertiesArgumentBuilder.buildArgumentList();
		systemPropertiesArgumentBuilder.addDemoTrustArguments();
		systemPropertiesArgumentBuilder.addPineappleArguments(randomDir);
		EasyMock.expect(systemPropertiesArgumentBuilder.getArgumentList()).andReturn(arguments);				

		EasyMock.replay(systemPropertiesArgumentBuilder);		
						
		// get module path
		String actual[]= scriptHelper.createSystemPropertiesArguments(ENABLE_DEMO_TRUST, NO_FAST_RANDOM_GEN, executionResult);		
		
		// test
		assertEquals(2, actual.length);
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(executionInfo);
		EasyMock.verify(moduleInfo);		
		EasyMock.verify(executionResult);
		EasyMock.verify(systemPropertiesArgumentBuilder);
	}

	/**
	 * Test arguments can be created with no properties. 
	 */
	@Test
	public void testCreateArgumentsWithNullProperties() {

		String properties = null;
		String[] arguments = { randomName, randomName2 };
				
		// complete mock setup		
		argumentBuilder.buildArgumentList();
		argumentBuilder.addSkipScanningArgument();
		argumentBuilder.addSingleArgument(randomName);		
		EasyMock.expect(argumentBuilder.getArgumentList()).andReturn(arguments);				
		EasyMock.replay(argumentBuilder);		
								
		// get module path
		String actual[]= scriptHelper.createArguments(randomName, properties);		
		
		// test
		assertEquals(2, actual.length);
				
		EasyMock.verify(argumentBuilder);
	}

	/**
	 * Test arguments can be created with empty properties. 
	 */
	@Test
	public void testCreateArgumentsWithEmptyProperties() {

		String emptyProperties = "";
		String[] arguments = { randomName, randomName2 };
				
		// complete mock setup		
		argumentBuilder.buildArgumentList();
		argumentBuilder.addSkipScanningArgument();
		argumentBuilder.addLoadPropertiesArgument();
		argumentBuilder.addSingleArgument(emptyProperties);
		argumentBuilder.addSingleArgument(randomName);		
		EasyMock.expect(argumentBuilder.getArgumentList()).andReturn(arguments);				
		EasyMock.replay(argumentBuilder);		
								
		// get module path
		String actual[]= scriptHelper.createArguments(randomName, emptyProperties);		
		
		// test
		assertEquals(2, actual.length);
				
		EasyMock.verify(argumentBuilder);
	}

	/**
	 * Test arguments can be created with properties. 
	 */
	@Test
	public void testCreateArgumentsWithProperties() {

		String properties = RandomStringUtils.randomAlphabetic(10);
		String[] arguments = { randomName, randomName2 };
				
		// complete mock setup		
		argumentBuilder.buildArgumentList();
		argumentBuilder.addSkipScanningArgument();
		argumentBuilder.addLoadPropertiesArgument();
		argumentBuilder.addSingleArgument(properties);
		argumentBuilder.addSingleArgument(randomName);		
		EasyMock.expect(argumentBuilder.getArgumentList()).andReturn(arguments);				
		EasyMock.replay(argumentBuilder);		
								
		// get module path
		String actual[]= scriptHelper.createArguments(randomName, properties);		
		
		// test
		assertEquals(2, actual.length);
				
		EasyMock.verify(argumentBuilder);
	}

	/**
	 * Test system properties arguments can be created
	 * with fast random generator enabled. 
	 */
	@Test
	public void testCreateSystemPropertiesArgumentsWithLinuxFastSecureRandom() {

		String[] arguments = { randomName, randomName2 };
		
        // complete mock setup				
		ModuleInfo moduleInfo = EasyMock.createMock( ModuleInfo.class );
		EasyMock.expect(moduleInfo.getDirectory()).andReturn(randomDir);		
		EasyMock.replay(moduleInfo);		

        // complete mock setup				
		ExecutionInfo executionInfo = EasyMock.createMock( ExecutionInfo.class );
		EasyMock.expect(executionInfo.getModuleInfo()).andReturn(moduleInfo);				
		EasyMock.replay(executionInfo);		
				
        // complete mock setup		
		expect(coreExecutionInfoProvider.get(executionResult)).andReturn(executionInfo);		
		EasyMock.replay(coreExecutionInfoProvider);
		
		// complete mock setup
		replay(executionResult);
		
		// complete mock setup		
		systemPropertiesArgumentBuilder.buildArgumentList();
		systemPropertiesArgumentBuilder.addLinuxFastSecureRandomArguments();
		systemPropertiesArgumentBuilder.addPineappleArguments(randomDir);
		EasyMock.expect(systemPropertiesArgumentBuilder.getArgumentList()).andReturn(arguments);				

		EasyMock.replay(systemPropertiesArgumentBuilder);		
						
		// get module path
		String actual[]= scriptHelper.createSystemPropertiesArguments(NO_DEMO_TRUST, ENABLE_FAST_RANDOM_GEN, executionResult);		
		
		// test
		assertEquals(2, actual.length);
				
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(executionInfo);
		EasyMock.verify(moduleInfo);		
		EasyMock.verify(executionResult);
		EasyMock.verify(systemPropertiesArgumentBuilder);
	}
	
	
}
