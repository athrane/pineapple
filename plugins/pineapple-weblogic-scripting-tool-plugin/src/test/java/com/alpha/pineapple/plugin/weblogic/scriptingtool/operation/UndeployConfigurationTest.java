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

import static com.alpha.pineapple.plugin.weblogic.scriptingtool.WeblogicScriptingToolConstants.DEFAULT_PROCESS_TIMEOUT;
import static com.alpha.pineapple.plugin.weblogic.scriptingtool.WeblogicScriptingToolConstants.LEGAL_CONTENT_TYPES;
import static com.alpha.pineapple.plugin.weblogic.scriptingtool.WeblogicScriptingToolConstants.PRODUCT_HOME_RESOURCE_PROPERTY;

import java.io.File;

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
import com.alpha.javautils.OperationUtils;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.model.Wlst;
import com.alpha.pineapple.plugin.weblogic.scriptingtool.utils.ScriptHelper;
import com.alpha.pineapple.resource.ResourceException;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.pineapple.test.Asserter;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Unit test for the <code>UndeployConfiguration</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( {DirectoryTestExecutionListener.class})
public class UndeployConfigurationTest {

	/**
     * Current test directory.
     */
	File testDirectory;
		
    /**
     * Object under test.
     */
    UndeployConfiguration operation;
    
    /**
     * Object mother for the WLST model.
     */
    ObjectMotherContent contentMother;
    
	/**
	 * Mock Session.
	 */
    ProcessExecutionSession session;
			
	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;
	
    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;    
    
    /**
     * Mock operation utils.
     */
    OperationUtils operationUtils;      
    
    /**
     * Mock Hamcrest asserter.
     */
    Asserter asserter;
    
    /**
     * Mock Resource property getter.
     */
    ResourcePropertyGetter resourcePropertyGetter; 
    
    /**
     * Mock Script helper.
     */
    ScriptHelper scriptHelper;
    
    /**
     * Mock resource.
     */
    com.alpha.pineapple.model.configuration.Resource resource;
    
    /**
     * Random file name. 
     */
    String randomScriptName;

    /**
     * Random file name. 
     */
    String randomScriptName2;

    /**
     * Random home name. 
     */
    String randomHomeName;

    /**
     * Random home directory. 
     */    
    File randomHomeDir;    

    /**
     * Random WLST invoker. 
     */    
    File randomWlstInvoker;
    
    /**
     * Random file name. 
     */
    String randomPropsName;

    /**
     * Random file name. 
     */
    String randomPropsName2;
        
    /**
     * Random WLST script.
     */
	File randomWlst;
        
	@Before
	public void setUp() throws Exception {
	
        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();
		        
        // create random names
        randomScriptName = RandomStringUtils.randomAlphabetic(10);
        randomScriptName2 = RandomStringUtils.randomAlphabetic(10);
        randomHomeName = RandomStringUtils.randomAlphabetic(10);                
        randomHomeDir = new File(testDirectory, randomHomeName);
        randomPropsName = RandomStringUtils.randomAlphabetic(10);
        randomPropsName2 = RandomStringUtils.randomAlphabetic(10);                
		File commonDir = new File(randomHomeDir, "common");    	    	
		File binDir = new File(commonDir, "bin");    	    	    	    	
		randomWlstInvoker = new File(testDirectory, RandomStringUtils.randomAlphabetic(10));			
		randomWlst = new File(binDir, RandomStringUtils.randomAlphabetic(10));
		        
        // create mock resource
        resource = EasyMock.createMock(com.alpha.pineapple.model.configuration.Resource.class);
        
        // create mock credential        
		Credential credential = EasyMock.createMock(Credential.class);
		
        // create mock session
		session = EasyMock.createMock(ProcessExecutionSession.class);
				
        // create mocks
        messageProvider = EasyMock.createMock( MessageProvider.class );
        executionResult = EasyMock.createMock( ExecutionResult.class );
        operationUtils = EasyMock.createMock( OperationUtils.class );        
        asserter = EasyMock.createMock( Asserter.class );        
        resourcePropertyGetter = EasyMock.createMock( ResourcePropertyGetter.class );    
        scriptHelper = EasyMock.createMock( ScriptHelper.class );
        
        // create content mother
        contentMother = new ObjectMotherContent();		        
        
        // create operation
        operation = new UndeployConfiguration();

        // inject mocks operation
        ReflectionTestUtils.setField( operation, "operationUtils", operationUtils );
        ReflectionTestUtils.setField( operation, "asserter", asserter );
        ReflectionTestUtils.setField( operation, "resourcePropertyGetter", resourcePropertyGetter);
        ReflectionTestUtils.setField( operation, "scriptHelper", scriptHelper);
        ReflectionTestUtils.setField( operation, "messageProvider", messageProvider);
                
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
        testDirectory = null;
		session = null;
		executionResult = null;
		contentMother = null;
	}
		
    /**
     * Complete operation utils setup
     * 
     * @param content Content document.
     * 
     * @throws Exception If test fails.
     */
	void completeMockOperationUtilsSetup(Object content) throws Exception 
	{
		operationUtils.validateContentType(content, LEGAL_CONTENT_TYPES);
		operationUtils.validateSessionType(session, ProcessExecutionSession.class );		
		EasyMock.replay( operationUtils );
	}

	/**
	 * Complete asserter setup.
	 */
	void completeMockAsserterSetup() {
		asserter.setExecutionResult(executionResult);
		EasyMock.replay( asserter);
	}

	/**
	 * Complete session setup.
	 */
	void completeMockSessionSetup() {
		EasyMock.expect(session.getResource()).andReturn(resource);
		EasyMock.replay( session );
	}

	/**
	 * Complete session setup,
	 * where session is conducting an execution.
	 */	
	void completeMockSessionSetup2(String[] arguments) {
		EasyMock.expect(session.getResource()).andReturn(resource);
		session.execute(randomWlstInvoker.getAbsolutePath(), arguments, DEFAULT_PROCESS_TIMEOUT, "uc.execute", executionResult);				
		EasyMock.replay( session );
	}		
	
	/**
	 * Complete ResourcePropertyGetter setup.
	 */
	void completeMockResourcePropertyGetterSetup() {
		resourcePropertyGetter.setResource(resource);
		EasyMock.replay( resourcePropertyGetter);
	}
	
	/**
	 * Complete ResourcePropertyGetter setup.
	 */	
	void completeMockResourcePropertyGetterSetup2() throws ResourceException {
		// complete mock getter setup
		resourcePropertyGetter.setResource(resource);
		EasyMock.expect(resourcePropertyGetter.getProperty(PRODUCT_HOME_RESOURCE_PROPERTY)).andReturn(randomHomeName);
		EasyMock.replay( resourcePropertyGetter);
	}
	
	
	/**
	 * Test that the operation can execute with a minimal model.
	 */
	@Test
	public void testCanExecuteWithMinimalModel() throws Exception {

        // create content
        Object content = contentMother.createEmptyWlst();

        // complete mock setups
		completeMockOperationUtilsSetup( content );
		completeMockAsserterSetup();
		completeMockResourcePropertyGetterSetup();		
		completeMockSessionSetup();
		
        // complete mock setups
		EasyMock.expect(scriptHelper.isModelContentDefined( (Wlst) content)).andReturn(false);		
		EasyMock.replay( scriptHelper);
                
		// complete execution result initialization		
        executionResult.completeAsComputed(messageProvider, "uc.completed", null, "uc.failed", null);
		EasyMock.replay( executionResult );
		
        // invoke operation
        operation.execute( content, session, executionResult );
        
        // verify mock objects
		EasyMock.verify( operationUtils);
		EasyMock.verify( asserter);        
		EasyMock.verify( resourcePropertyGetter);        
		EasyMock.verify( scriptHelper);        
		EasyMock.verify( executionResult);
		EasyMock.verify( session);
	}

	/**
	 * Test that the operation fails if home directory isn't
	 * defined in resource.
	 */
	@Test(expected=PluginExecutionFailedException.class)
	public void testFailsIfHomeDirIsntDefinedInResource() throws Exception {

        // create content
        Object content = contentMother.createWlstDocumentWithNoProperties(randomScriptName);

        // complete mock setups
		completeMockOperationUtilsSetup( content );
		completeMockAsserterSetup();
		completeMockSessionSetup();

		// complete mock getter setup
		resourcePropertyGetter.setResource(resource);
		Throwable throwable = new ResourceException("resource not found");
		EasyMock.expect(resourcePropertyGetter.getProperty(PRODUCT_HOME_RESOURCE_PROPERTY)).andThrow(throwable);
		EasyMock.replay( resourcePropertyGetter);
				
        // complete mock script helper setup
		EasyMock.expect(scriptHelper.isModelContentDefined( (Wlst) content)).andReturn(true);
		EasyMock.expect(scriptHelper.resolveModulePath(executionResult, randomScriptName)).andReturn(randomScriptName2);
		EasyMock.expect(scriptHelper.isPropertiesFileDefined( (Wlst) content)).andReturn(false);		
		EasyMock.replay( scriptHelper);
                
		// complete execution result initialization		
		EasyMock.replay( executionResult );
		
        // invoke operation
        operation.execute( content, session, executionResult );
	}

	/**
	 * Test that the operation can execute with a model
	 * with properties file.
	 */
	@Test
	public void testCanExecuteWithModel() throws Exception {

		String[] arguments = { };
		String[] systemPropsArguments = { };
		
        // create content
        Object content = contentMother.createWlstDocument(randomScriptName, randomPropsName);

        // complete mock setups
		completeMockOperationUtilsSetup( content );
		completeMockAsserterSetup();
		completeMockSessionSetup2(arguments);
		completeMockResourcePropertyGetterSetup2();		
				
        // complete mock script helper setup
		EasyMock.expect(scriptHelper.isModelContentDefined( (Wlst) content)).andReturn(true);
		EasyMock.expect(scriptHelper.isProductHomeValid(EasyMock.isA(File.class), org.easymock.EasyMock.eq(asserter))).andReturn(true);
		EasyMock.expect(scriptHelper.resolveModulePath(executionResult, randomScriptName)).andReturn(randomScriptName2);
		EasyMock.expect(scriptHelper.isScriptValid( randomScriptName2, asserter)).andReturn(true);
		EasyMock.expect(scriptHelper.isPropertiesFileDefined( (Wlst) content)).andReturn(true);
		EasyMock.expect(scriptHelper.resolveModulePath(executionResult, randomPropsName)).andReturn(randomPropsName2);		
		EasyMock.expect(scriptHelper.isPropertiesFileValid(randomPropsName2, asserter)).andReturn(true);
		EasyMock.expect(scriptHelper.getWlstInvoker(EasyMock.isA(File.class))).andReturn(randomWlst);					
		EasyMock.expect(scriptHelper.createWlstInvokerScript(randomWlst, systemPropsArguments)).andReturn(randomWlstInvoker);			
		EasyMock.expect(scriptHelper.isWlstInvokerValid(randomWlst, asserter)).andReturn(true);
		EasyMock.expect(scriptHelper.createArguments(randomScriptName2, randomPropsName2)).andReturn(arguments);		
		EasyMock.expect(scriptHelper.createSystemPropertiesArguments(false, false, executionResult)).andReturn(systemPropsArguments);		
		EasyMock.expect(scriptHelper.getFastRandomGeneratorForLinuxProperty(session)).andReturn(false);				
		EasyMock.replay( scriptHelper);
                
		// complete execution result initialization		
        executionResult.completeAsComputed(messageProvider, "uc.completed", null, "uc.failed", null);
		EasyMock.replay( executionResult );
		
        // invoke operation
        operation.execute( content, session, executionResult );
        
        // verify mock objects
		EasyMock.verify( operationUtils);
		EasyMock.verify( asserter);        
		EasyMock.verify( resourcePropertyGetter);        
		EasyMock.verify( scriptHelper);        
		EasyMock.verify( executionResult);
		EasyMock.verify( session);
	}
	
	
	/**
	 * Test that the operation can execute with a model
	 * with no properties file.
	 */
	@Test
	public void testCanExecuteWithModelWithNoPropertiesFiles() throws Exception {

		String[] arguments = { };
		String[] systemPropsArguments = { };		
		
        // create content
        Object content = contentMother.createWlstDocumentWithNoProperties(randomScriptName);

        // complete mock setups
		completeMockOperationUtilsSetup( content );
		completeMockAsserterSetup();
		completeMockSessionSetup2(arguments);
		completeMockResourcePropertyGetterSetup2();		
				
        // complete mock script helper setup
		EasyMock.expect(scriptHelper.isModelContentDefined( (Wlst) content)).andReturn(true);
		EasyMock.expect(scriptHelper.isProductHomeValid(EasyMock.isA(File.class), org.easymock.EasyMock.eq(asserter))).andReturn(true);
		EasyMock.expect(scriptHelper.resolveModulePath(executionResult, randomScriptName)).andReturn(randomScriptName2);
		EasyMock.expect(scriptHelper.isPropertiesFileDefined( (Wlst) content)).andReturn(false);
		EasyMock.expect(scriptHelper.isScriptValid( randomScriptName2, asserter)).andReturn(true);
		EasyMock.expect(scriptHelper.getWlstInvoker(EasyMock.isA(File.class))).andReturn(randomWlst);					
		EasyMock.expect(scriptHelper.createWlstInvokerScript(randomWlst, systemPropsArguments)).andReturn(randomWlstInvoker);			
		EasyMock.expect(scriptHelper.isWlstInvokerValid(randomWlst, asserter)).andReturn(true);
		EasyMock.expect(scriptHelper.createArguments(randomScriptName2, null)).andReturn(arguments);		
		EasyMock.expect(scriptHelper.createSystemPropertiesArguments(false, false, executionResult)).andReturn(systemPropsArguments);
		EasyMock.expect(scriptHelper.getFastRandomGeneratorForLinuxProperty(session)).andReturn(false);						
		EasyMock.replay( scriptHelper);
                
		// complete execution result initialization		
        executionResult.completeAsComputed(messageProvider, "uc.completed", null, "uc.failed", null);
		EasyMock.replay( executionResult );
		
        // invoke operation
        operation.execute( content, session, executionResult );
        
        // verify mock objects
		EasyMock.verify( operationUtils);
		EasyMock.verify( asserter);        
		EasyMock.verify( resourcePropertyGetter);        
		EasyMock.verify( scriptHelper);        
		EasyMock.verify( executionResult);
		EasyMock.verify( session);
	}
	
}
