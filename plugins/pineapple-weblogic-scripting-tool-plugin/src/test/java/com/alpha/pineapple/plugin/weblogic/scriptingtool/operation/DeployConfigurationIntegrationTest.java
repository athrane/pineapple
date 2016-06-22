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


package com.alpha.pineapple.plugin.weblogic.scriptingtool.operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static com.alpha.pineapple.plugin.weblogic.scriptingtool.WeblogicScriptingToolConstants.*;

import java.io.File;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.command.ProcessExecutionCommand;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.model.Wlst;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.WSPIntTestConstants;

/**
 * Integration test for the <code>DeployConfiguration</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( {DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ContextConfiguration( locations = { "/com.alpha.pineapple.testutils-config.xml",
		"/com.alpha.pineapple.process.execution-config.xml",
		"/com.alpha.pineapple.plugin.weblogic.scriptingtool-config.xml" } )
public class DeployConfigurationIntegrationTest {

	/**
     * Current test directory.
     */
	File testDirectory;
		
    /**
     * Object under test.
     */
    @Resource
    DeployConfiguration deployConfiguration;

    /**
     * Injected mock provider from the test-utils project.
     * See http://pineapple.java.net/pineapple-test-projects/pineapple-test-utils/examples/writing-integration-test.html
     */
    @Resource
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;
    
    /**
     * Injected mock provider from the test-utils project.
     * See http://pineapple.java.net/pineapple-test-projects/pineapple-test-utils/examples/writing-integration-test.html
     */
    @Resource
    ExecutionInfoProvider coreExecutionInfoProvider;
    
    /**
     * Object mother for the WLST model.
     */
    ObjectMotherContent contentMother;
    
	/**
	 * Session.
	 */
    @Resource
    ProcessExecutionSession session;
			
	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Model resource.
	 */
	com.alpha.pineapple.model.configuration.Resource resource;

	/**
	 * Random module directory.
	 */
	File randomModuleDir;

	/**
	 * Random temp directory.
	 */	
	File randomTempDir;
	
    
	@Before
	public void setUp() throws Exception {
		
        // reset plugin provider
        EasyMock.reset(coreRuntimeDirectoryProvider);
        EasyMock.reset(coreExecutionInfoProvider);
        
        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		randomModuleDir = new File (testDirectory, RandomStringUtils.randomAlphabetic(8));
		randomTempDir = new File (testDirectory, RandomStringUtils.randomAlphabetic(8));
        
        // create mock resource
        resource = new com.alpha.pineapple.model.configuration.Resource();
       
        // create mock credential        
		Credential credential = EasyMock.createMock(Credential .class);
		
		// connect session
        session.connect(resource, credential);
		
        // create execution result
        executionResult = new ExecutionResultImpl( "Root result" );
        
        // create content mother
        contentMother = new ObjectMotherContent();		                        
	}

	@After
	public void tearDown() throws Exception {
        testDirectory = null;
		session = null;
		executionResult = null;
		contentMother = null;
	}

	/**
	 * Set home directory attribute in resource
	 */
	void configureHomeAttributeOnResource() {
		Property property = new Property();
		property.setKey(PRODUCT_HOME_RESOURCE_PROPERTY);
		property.setValue(WSPIntTestConstants.homeDirectoryWindows);
		resource.getProperty().add(property);
	}
	
    /**
     * Test that operation can be looked up from the context.
     */
    @Test
    public void testCanGetOperationFromContext()
    {
        assertNotNull( deployConfiguration );
    }
	
	/**
	 * Test that the operation can execute with a minimal model.
	 */
	@Test
	public void testCanExecuteWithMinimalModel() throws Exception {
				
        // create content
        Object content = contentMother.createEmptyWlst();

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );

        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 0 , executionResult.getChildren().length);
        
	}

	/**
	 * Test that the operation fails if home directory isn't
	 * defined in resource.
	 */
	@Test(expected=PluginExecutionFailedException.class)
	public void testFailsIfHomeDirIsntDefinedInResource() throws Exception {
				
        // create content
		File scriptFile = contentMother.writeModulePathSystemPropertyScript(testDirectory);				
        Wlst content = contentMother.createWlstDocumentWithNoProperties(scriptFile.getAbsolutePath());        

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );

        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 0 , executionResult.getChildren().length);        
	}
	
	/**
	 * Test that the operation fails if home directory defined in resource
	 * doesn't exist.
	 */
	public void testFailsIfHomeDirDoesntExist() throws Exception {

		File randomHomeDir = new File (testDirectory, RandomStringUtils.randomAlphabetic(8));

		// set home directory attribute in resource
		Property property = new Property();
		property.setKey(PRODUCT_HOME_RESOURCE_PROPERTY);
		property.setValue(randomHomeDir.getAbsolutePath());
		resource.getProperty().add(property);
		
        // create content
		File scriptFile = contentMother.writeModulePathSystemPropertyScript(testDirectory);				
        Wlst content = contentMother.createWlstDocumentWithNoProperties(scriptFile.getAbsolutePath());        

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );

        // test
        assertEquals( ExecutionState.FAILURE, executionResult.getState());
        assertEquals( 0 , executionResult.getChildren().length);        
	}
	
	
	/**
	 * Test that the operation can execute script which 
	 * reads the java system property 'pineapple.module.path'
	 * with a correct value. 
	 */
	@Test
	public void testCanGetModulePathFromWlstScript() throws Exception {
		
		configureHomeAttributeOnResource();
		
		// write simple script
		File scriptFile = contentMother.writeModulePathSystemPropertyScript(randomModuleDir);		

		// create module info		
		ModuleInfo moduleInfo = EasyMock.createMock(ModuleInfo.class);
		EasyMock.expect(moduleInfo.getDirectory()).andReturn(randomModuleDir);		
		EasyMock.replay( moduleInfo);
		
		// create execution info		
		ExecutionInfo executionInfo = EasyMock.createMock(ExecutionInfo.class);
		EasyMock.expect(executionInfo.getModuleInfo()).andReturn(moduleInfo);		
		EasyMock.replay( executionInfo );
		
		// complete provider setup
		EasyMock.expect( coreRuntimeDirectoryProvider.startsWithModulePathPrefix( (String) EasyMock.isA(String.class))).andReturn(false);		
		EasyMock.expect( coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(randomTempDir);
		EasyMock.replay( coreRuntimeDirectoryProvider );
		
		EasyMock.expect( coreExecutionInfoProvider.get( EasyMock.isA( ExecutionResult.class))).andReturn(executionInfo);
		EasyMock.replay( coreExecutionInfoProvider );		
		
        // create content
        Wlst content = contentMother.createWlstDocumentWithNoProperties(scriptFile.getAbsolutePath());        
        
        // invoke operation
        deployConfiguration.execute( content, session, executionResult );

        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 4 , executionResult.getChildren().length);

        // get wlst cmd result messages 
        ExecutionResult executeWlstScripCmdResult = executionResult.getChildren()[3];
        Map<String, String> messages = executeWlstScripCmdResult.getMessages();
        
        // test messages
        assertTrue( messages.get(ProcessExecutionCommand.MSG_STANDARD_OUT).contains(testDirectory.getAbsolutePath()));
        
        // verify
		EasyMock.verify( executionInfo );
		EasyMock.verify( moduleInfo );		
		EasyMock.verify( coreRuntimeDirectoryProvider );
		EasyMock.verify( coreExecutionInfoProvider );
	}

	/**
	 * Test that the operation can execute with script.
	 */
	@Test
	public void testCanExecuteHelloWorldScript() throws Exception {

		configureHomeAttributeOnResource();
		
		// write simple script
		File scriptFile = contentMother.writeHelloWorldScript(testDirectory);

		// create module info		
		ModuleInfo moduleInfo = EasyMock.createMock(ModuleInfo.class);
		EasyMock.expect(moduleInfo.getDirectory()).andReturn(randomModuleDir);		
		EasyMock.replay( moduleInfo);
		
		// create execution info		
		ExecutionInfo executionInfo = EasyMock.createMock(ExecutionInfo.class);
		EasyMock.expect(executionInfo.getModuleInfo()).andReturn(moduleInfo);		
		EasyMock.replay( executionInfo );
		
		// complete provider setup
		EasyMock.expect( coreRuntimeDirectoryProvider.startsWithModulePathPrefix( (String) EasyMock.isA(String.class))).andReturn(false);		
		EasyMock.expect( coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(randomTempDir);
		EasyMock.replay( coreRuntimeDirectoryProvider );
		
		EasyMock.expect( coreExecutionInfoProvider.get( EasyMock.isA( ExecutionResult.class))).andReturn(executionInfo);
		EasyMock.replay( coreExecutionInfoProvider );		
		
        // create content
        Wlst content = contentMother.createWlstDocumentWithNoProperties(scriptFile.getAbsolutePath());        
        
        // invoke operation
        deployConfiguration.execute( content, session, executionResult );

        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 4 , executionResult.getChildren().length);

        // get wlst cmd result messages 
        ExecutionResult executeWlstScripCmdResult = executionResult.getChildren()[3];
        Map<String, String> messages = executeWlstScripCmdResult.getMessages();
        
        // test messages
        assertTrue( messages.get(ProcessExecutionCommand.MSG_STANDARD_OUT).contains("Hello world!"));
        
        // verify
		EasyMock.verify( executionInfo );
		EasyMock.verify( moduleInfo );		
		EasyMock.verify( coreRuntimeDirectoryProvider );
		EasyMock.verify( coreExecutionInfoProvider );
	}
	
	
}
