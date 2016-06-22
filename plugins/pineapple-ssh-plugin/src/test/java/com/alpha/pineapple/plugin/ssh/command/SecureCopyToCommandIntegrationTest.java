/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.plugin.ssh.command;

import static com.alpha.testutils.SshTestConstants.TESTSERVER_IP;
import static com.alpha.testutils.SshTestConstants.TESTSERVER_PORT;
import static com.alpha.testutils.SshTestConstants.TESTSERVER_USER;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.ssh.session.SshSession;
import com.alpha.pineapple.substitution.VariableSubstitutionProvider;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherSshSession;


/**
 * Integration test for the <code>DefaultOperation</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ActiveProfiles("integration-test")
@TestExecutionListeners( {DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.ssh-config.xml" } )
public class SecureCopyToCommandIntegrationTest {

	/**
	 * Current test directory.
	 */
	File testDirectory;

    /**
     * Object mother for the SSH model.
     */
    ObjectMotherContent contentMother;
	
	/**
	 * Object under test. 
	 */
	SshSession session;

	/**
	 * Execution result.
	 */
	ExecutionResult result;
	
	/**
	 * SshSession object mother.
	 */
	@Resource(name="objectMotherSshSession")
	ObjectMotherSshSession sessionMother;    
	
    /**
     * Object under test.
     */
    @Resource
    Command secureCopyCommand;

    /**
     * Mock Runtime directory provider.
     * From the "integration-test" profile.
     */
    @Resource
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;
    
    /**
     * Mock variable substitution provider. 
     * From the "integration-test" profile.
     */
    @Resource                
    VariableSubstitutionProvider coreVariableSubstitutionProvider;

    /**
     * Local directory (on host) used to stage file prior to copy.
     */
	File localStageDirectory;
    
    /**
     * Local directory (on host) shared between unit test and VM.
     */
	File localSharedTestDirectory;

    /**
     * remote directory (on guest VM) shared between unit test and VM.
     */	
	String remoteSharedTestDirectory;    

	/**
	 * Context.
	 */
	Context context;
	
    /**
     * Random file name.
     */
    String randomFileName;

    /**
     * Random file name.
     */
    String randomFileName2;
    
	@Before
	public void setUp() throws Exception {		
		randomFileName = RandomStringUtils.randomAlphabetic(10)+".txt";
		randomFileName2 = RandomStringUtils.randomAlphabetic(10)+".txt";		
		
		// create mother's
		contentMother =  new ObjectMotherContent();

		// create context
		context = new ContextBase();
		
        // create session      
        session = sessionMother.createConnectedSshSessionWithPasswordAuthentication(TESTSERVER_IP, TESTSERVER_PORT, TESTSERVER_USER, TESTSERVER_USER);
        
        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

        // define local shared vagrant directory
        localSharedTestDirectory = testDirectory;

        // define remote shared vagrant directory        
        remoteSharedTestDirectory = contentMother.resolveRemoteSharedDirectory_OnLinux(testDirectory);
        
        // define local stage directory
        localStageDirectory = new File(testDirectory, "local-stage");
        if(localStageDirectory.exists()) FileUtils.deleteDirectory(localStageDirectory);        
        localStageDirectory.mkdirs();
        
        // create execution result
        result = new ExecutionResultImpl( "Root result" );
                        
        // reset plugin provider
		EasyMock.reset(coreRuntimeDirectoryProvider);
		EasyMock.reset(coreVariableSubstitutionProvider);		
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Create remote file name for random file #1.
	 * 
	 * @return remote file name for random file #1.
	 */
	String createRemoteFileNameforRandomFileOne() {
		String remoteFileName = new StringBuilder()
			.append(remoteSharedTestDirectory)
			.append("/")
			.append(randomFileName)
			.toString();
		return remoteFileName;
	}
	
    /**
     * Test that command can be looked up from the context.
     */
    @Test
    public void testCanGetInstanceFromContext()
    {
        assertNotNull( secureCopyCommand );
    }
    
    /**
     * Test that command can copy a single file.
     * 
     * @throws Exception If test fails. 
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCanCopySingleFileWithVariableSubstitution() throws Exception
    {
		// create local test file 
		File localFile = new File(localStageDirectory, randomFileName);
		FileUtils.write(localFile, randomFileName);

        // create processed file         
		File processedFile = new File(localStageDirectory, randomFileName2);
		FileUtils.write(processedFile, randomFileName2);
		
        String remoteFileName = createRemoteFileNameforRandomFileOne();
				
        // create FTP'ed file name as it appears in the local shared VM directory
        File createdFile = new File(localSharedTestDirectory, randomFileName);
		
		// setup mocks
		expect(coreVariableSubstitutionProvider.createSubstitutedFile(localFile, session, result)).andReturn(processedFile);						
		replay(coreVariableSubstitutionProvider);
    	expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(isA(String.class)))
			.andReturn(false)
			.anyTimes();				
		replay(coreRuntimeDirectoryProvider);
		
        // invoke operation
        context.put(SecureCopyToCommand.EXECUTIONRESULT_KEY, result);
        context.put(SecureCopyToCommand.LOCAL_FILE_KEY, localFile.getAbsolutePath());      
        context.put(SecureCopyToCommand.REMOTE_FILE_KEY, remoteFileName);
        context.put(SecureCopyToCommand.SUBSTITUTE_VARIABLES_KEY, Boolean.TRUE);        
		context.put(SecureCopyToCommand.FILE_PERMISSIONS_KEY,-1); 
		context.put(SecureCopyToCommand.USER_OWNERSHIP_KEY,-1); 		
		context.put(SecureCopyToCommand.GROUP_OWNERSHIP_KEY,-1); 		
        context.put(SecureCopyToCommand.SESSION_KEY, session);        
        secureCopyCommand.execute(context);
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());
        assertTrue(createdFile.exists());
        assertTrue(createdFile.isFile());
		verify(coreVariableSubstitutionProvider);            
		verify(coreRuntimeDirectoryProvider);            		
    }


    /**
     * Test that command can copy a single file 
     * with variable substitution disabled.
     * 
     * @throws Exception If test fails. 
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCanCopySingleFileWithVariableSubstutionDisabled() throws Exception
    {
		// create local test file 
		File localFile = new File(localStageDirectory, randomFileName);
		FileUtils.write(localFile, randomFileName);

        // create processed file         
		File processedFile = new File(localStageDirectory, randomFileName2);
		FileUtils.write(processedFile, randomFileName2);
		
        String remoteFileName = createRemoteFileNameforRandomFileOne();
				
        // create FTP'ed file name as it appears in the local shared VM directory
        File createdFile = new File(localSharedTestDirectory, randomFileName);
		
		// setup mocks
		replay(coreVariableSubstitutionProvider);
    	expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(isA(String.class)))
			.andReturn(false)
			.anyTimes();				
		replay(coreRuntimeDirectoryProvider);
		
        // invoke operation
        context.put(SecureCopyToCommand.EXECUTIONRESULT_KEY, result);
        context.put(SecureCopyToCommand.LOCAL_FILE_KEY, localFile.getAbsolutePath());      
        context.put(SecureCopyToCommand.REMOTE_FILE_KEY, remoteFileName);
        context.put(SecureCopyToCommand.SUBSTITUTE_VARIABLES_KEY, Boolean.FALSE);        
		context.put(SecureCopyToCommand.FILE_PERMISSIONS_KEY,-1); 
		context.put(SecureCopyToCommand.USER_OWNERSHIP_KEY,-1); 		
		context.put(SecureCopyToCommand.GROUP_OWNERSHIP_KEY,-1); 		
        context.put(SecureCopyToCommand.SESSION_KEY, session);        
        secureCopyCommand.execute(context);
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());
        assertTrue(createdFile.exists());
        assertTrue(createdFile.isFile());
		verify(coreVariableSubstitutionProvider);            
		verify(coreRuntimeDirectoryProvider);            		
    }

    
    /**
     * Test that command fails if attempt to copy non existing single file.
     * 
     * @throws Exception If test fails. 
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCopyOfNonexistingFileFails() throws Exception
    {
		// create local test file 
		File localFile = new File(testDirectory, randomFileName);
		//FileUtils.write(localFile, randomFileName);
		
        String remoteFileName = createRemoteFileNameforRandomFileOne();

        // create FTP'ed file name as it appears in the local shared VM directory
        File createdFile = new File(localSharedTestDirectory, randomFileName);

		// setup mocks
		replay(coreVariableSubstitutionProvider);
    	expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(isA(String.class)))
    		.andReturn(false)
    		.anyTimes();				
		replay(coreRuntimeDirectoryProvider);
		
        // invoke operation
        context.put(SecureCopyToCommand.EXECUTIONRESULT_KEY, result);
        context.put(SecureCopyToCommand.LOCAL_FILE_KEY, localFile.getAbsolutePath());      
        context.put(SecureCopyToCommand.REMOTE_FILE_KEY, remoteFileName);
        context.put(SecureCopyToCommand.SUBSTITUTE_VARIABLES_KEY, Boolean.TRUE);               
		context.put(SecureCopyToCommand.FILE_PERMISSIONS_KEY,-1); 
		context.put(SecureCopyToCommand.USER_OWNERSHIP_KEY,-1); 		
		context.put(SecureCopyToCommand.GROUP_OWNERSHIP_KEY,-1); 		        
        context.put(SecureCopyToCommand.SESSION_KEY, session);        
        secureCopyCommand.execute(context);
        
        // test
        assertEquals(ExecutionState.FAILURE, result.getState());
        assertFalse(createdFile.exists());
		verify(coreVariableSubstitutionProvider);            
		verify(coreRuntimeDirectoryProvider);            		
    }
    
   /**
     * Test that command can copy a single file and set the file permissions.
     * 
     * @throws Exception If test fails. 
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetFilePermissionsToReadOnly() throws Exception
    {
		// create local test file 
		File localFile = new File(localStageDirectory, randomFileName);
		FileUtils.write(localFile, randomFileName);

        // create processed file         
		File processedFile = new File(localStageDirectory, randomFileName2);
		FileUtils.write(processedFile, randomFileName2);
		
        String remoteFileName = createRemoteFileNameforRandomFileOne();
				
        // create FTP'ed file name as it appears in the local shared VM directory
        File createdFile = new File(localSharedTestDirectory, randomFileName);
		
		// setup mocks
		replay(coreVariableSubstitutionProvider);
    	expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(isA(String.class)))
			.andReturn(false)
			.anyTimes();				
		replay(coreRuntimeDirectoryProvider);
		
        // invoke operation
        context.put(SecureCopyToCommand.EXECUTIONRESULT_KEY, result);
        context.put(SecureCopyToCommand.LOCAL_FILE_KEY, localFile.getAbsolutePath());      
        context.put(SecureCopyToCommand.REMOTE_FILE_KEY, remoteFileName);
        context.put(SecureCopyToCommand.SUBSTITUTE_VARIABLES_KEY, Boolean.FALSE);        
		context.put(SecureCopyToCommand.FILE_PERMISSIONS_KEY, 444); 
		context.put(SecureCopyToCommand.USER_OWNERSHIP_KEY,-1); 		
		context.put(SecureCopyToCommand.GROUP_OWNERSHIP_KEY,-1); 		
        context.put(SecureCopyToCommand.SESSION_KEY, session);        
        secureCopyCommand.execute(context);
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());
        assertTrue(createdFile.exists());
        assertTrue(createdFile.isFile());
		verify(coreVariableSubstitutionProvider);            
		verify(coreRuntimeDirectoryProvider);            		
    }    
    
    /**
     * Test that command can copy a single file and set the file permissions.
     * 
     * @throws Exception If test fails. 
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetFilePermissionsToExecuteOnly() throws Exception
    {
		// create local test file 
		File localFile = new File(localStageDirectory, randomFileName);
		FileUtils.write(localFile, randomFileName);

        // create processed file         
		File processedFile = new File(localStageDirectory, randomFileName2);
		FileUtils.write(processedFile, randomFileName2);
		
        String remoteFileName = createRemoteFileNameforRandomFileOne();
				
        // create FTP'ed file name as it appears in the local shared VM directory
        File createdFile = new File(localSharedTestDirectory, randomFileName);
		
		// setup mocks
		replay(coreVariableSubstitutionProvider);
    	expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(isA(String.class)))
			.andReturn(false)
			.anyTimes();				
		replay(coreRuntimeDirectoryProvider);
		
        // invoke operation
        context.put(SecureCopyToCommand.EXECUTIONRESULT_KEY, result);
        context.put(SecureCopyToCommand.LOCAL_FILE_KEY, localFile.getAbsolutePath());      
        context.put(SecureCopyToCommand.REMOTE_FILE_KEY, remoteFileName);
        context.put(SecureCopyToCommand.SUBSTITUTE_VARIABLES_KEY, Boolean.FALSE);        
		context.put(SecureCopyToCommand.FILE_PERMISSIONS_KEY, 111); 
		context.put(SecureCopyToCommand.USER_OWNERSHIP_KEY,-1); 		
		context.put(SecureCopyToCommand.GROUP_OWNERSHIP_KEY,-1); 		
        context.put(SecureCopyToCommand.SESSION_KEY, session);        
        secureCopyCommand.execute(context);
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());
        assertTrue(createdFile.exists());
        assertTrue(createdFile.isFile());
		verify(coreVariableSubstitutionProvider);            
		verify(coreRuntimeDirectoryProvider);            		
    } 
    
   /** 
    * Test that command can copy a single file and set the file permissions.
    * 
    * @throws Exception If test fails. 
    */
   @SuppressWarnings("unchecked")
	@Test
   public void testSetFilePermissionsToWriteOnly() throws Exception
   {
		// create local test file 
		File localFile = new File(localStageDirectory, randomFileName);
		FileUtils.write(localFile, randomFileName);

       // create processed file         
		File processedFile = new File(localStageDirectory, randomFileName2);
		FileUtils.write(processedFile, randomFileName2);
		
       String remoteFileName = createRemoteFileNameforRandomFileOne();
				
       // create FTP'ed file name as it appears in the local shared VM directory
       File createdFile = new File(localSharedTestDirectory, randomFileName);
		
		// setup mocks
		replay(coreVariableSubstitutionProvider);
   	expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(isA(String.class)))
			.andReturn(false)
			.anyTimes();				
		replay(coreRuntimeDirectoryProvider);
		
       // invoke operation
       context.put(SecureCopyToCommand.EXECUTIONRESULT_KEY, result);
       context.put(SecureCopyToCommand.LOCAL_FILE_KEY, localFile.getAbsolutePath());      
       context.put(SecureCopyToCommand.REMOTE_FILE_KEY, remoteFileName);
       context.put(SecureCopyToCommand.SUBSTITUTE_VARIABLES_KEY, Boolean.FALSE);        
		context.put(SecureCopyToCommand.FILE_PERMISSIONS_KEY, 222); 
		context.put(SecureCopyToCommand.USER_OWNERSHIP_KEY,-1); 		
		context.put(SecureCopyToCommand.GROUP_OWNERSHIP_KEY,-1); 		
       context.put(SecureCopyToCommand.SESSION_KEY, session);        
       secureCopyCommand.execute(context);
       
       // test
       assertEquals(ExecutionState.SUCCESS, result.getState());
       assertTrue(createdFile.exists());
       assertTrue(createdFile.isFile());
		verify(coreVariableSubstitutionProvider);            
		verify(coreRuntimeDirectoryProvider);            		
   }     
}
