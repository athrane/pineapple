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

package com.alpha.pineapple.plugin.ssh.session;

import static com.alpha.testutils.SshTestConstants.*;
import static com.alpha.testutils.SshTestConstants.TESTSERVER_PORT;
import static com.alpha.testutils.SshTestConstants.TESTSERVER_PWD;
import static com.alpha.testutils.SshTestConstants.TESTSERVER_USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.javautils.ConcurrencyUtils;
import com.alpha.pineapple.session.SessionException;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherSshSession;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;

/**
 * Integration test for the class {@linkplain SshSessionImpl} session.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ActiveProfiles("integration-test")
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.ssh-config.xml" } )
@TestExecutionListeners( {DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class} )
public class SshSessionImplIntegrationTest {
	
    /**
	 * Command output buffer size.
	 */
	static final int BUFFER_SIZE = 1024;
	
	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	
    /**
     * Current test directory.
     */
    File testDirectory;
    
	/**
	 * Object under test. 
	 */
    SshSession session;

	/**
	 * SshSession object mother.
	 */
	@Resource(name="objectMotherSshSession")
	ObjectMotherSshSession sessionMother;    

    /**
     * Object mother for the SSH model.
     */
    ObjectMotherContent contentMother;
    
    /**
     * Random file name.
     */
    String randomFileName;

    /**
     * Local directory (on host) shared between unit test and VM.
     */
	File localSharedTestDirectory;

    /**
     * remote directory (on guest VM) shared between unit test and VM.
     */	
	String remoteSharedTestDirectory;    
	
	@Before
	public void setUp() throws Exception {
		
		randomFileName = RandomStringUtils.randomAlphabetic(10)+".txt";
		
        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();
        
		// create mother's
		contentMother =  new ObjectMotherContent();
        
        // define and local shared vagrant directory
        localSharedTestDirectory = testDirectory;

        // define remote shared vagrant directory        
        remoteSharedTestDirectory = contentMother.resolveRemoteSharedDirectory_OnLinux(testDirectory);
	}

	@After
	public void tearDown() throws Exception {
    	if(session == null) return;
		if(!session.isConnected()) return;
		session.disconnect();    	
	}
	
    /**
     * Test session can connect to SSH test server using password authentication.  
     */
    @Test
    public void testConnectWithPassword() {
        session = sessionMother.createConnectedSshSessionWithPasswordAuthentication(TESTSERVER_IP, TESTSERVER_PORT, TESTSERVER_USER, TESTSERVER_USER);
    	
        // test
        assertNotNull(session);
    }

    /**
     * Test session can connect to SSH test server using public key authentication.  
     */
    @Test
    public void testConnectWithPublicKey() {
        session = sessionMother.createConnectedSshSessionWithPasswordAuthentication(TESTSERVER_IP, TESTSERVER_PORT, TESTSERVER_USER, TESTSERVER_USER);
    	
        // test
        assertNotNull(session);
    }
    
    
	/**
	 * Test session returns true when connected.
	 */
	@Test
	public void testIsConnectedReturnTrueAfterConnect() {
        session = sessionMother.createConnectedSshSessionWithPasswordAuthentication(TESTSERVER_IP, TESTSERVER_PORT, TESTSERVER_USER, TESTSERVER_PWD);
    	
        // test
        assertNotNull(session);
        assertTrue(session.isConnected());                
	}

	/**
	 * Test session returns false prior to connecting.
	 */
	@Test
	public void testIsConnectedReturnFalsePriorToConnect() {
        session = sessionMother.createUnconnectedSshSession();
    	
        // test
        assertNotNull(session);
        assertFalse(session.isConnected());                
	}

	/**
	 * Test session returns false after disconnecting.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testIsConnectedReturnFalseAfterDisconnecting() throws Exception {
        // create session      
        session = sessionMother.createConnectedSshSessionWithPasswordAuthentication(TESTSERVER_IP, TESTSERVER_PORT, TESTSERVER_USER, TESTSERVER_PWD);
    	
        // test
        assertNotNull(session);
        assertTrue(session.isConnected());
        
        // disconnect
        session.disconnect();
        
        // test
        assertFalse(session.isConnected());                
        
	}

	/**
	 * Test that attempt to get SFTP channel fails if session isn't connected.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test(expected = SessionException.class )
	public void testGetSftpChannelFailsIfSessionIsntConnected() throws Exception {
        session = sessionMother.createUnconnectedSshSession();
    	
        // test
        assertNotNull(session);
        assertFalse(session.isConnected());

        // get channel to trigger exception 
        session.getSftpChannel();                
	}
	
	/**
	 * Test that attempt to get SFTP channel succeeds.
	 * 
	 * @throws SessionException If test fails.
	 */
	@Test
	public void testGetSftpChannelSucceedsIfSessionIsConnected() throws SessionException {
        session = sessionMother.createConnectedSshSessionWithPasswordAuthentication(TESTSERVER_IP, TESTSERVER_PORT, TESTSERVER_USER, TESTSERVER_PWD);
    	
        // test
        assertNotNull(session);
        assertTrue(session.isConnected());     
        
        // get channel 
        ChannelSftp sftpChannel = session.getSftpChannel();
        
        // test
        assertNotNull(sftpChannel);        
	}

	/**
	 * Test that attempt to get SFTP channel fails after session is disconnected.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test(expected = SessionException.class )
	public void testGetSftpChannelFailsAfterSessionIsDisconnected() throws Exception {
        session = sessionMother.createConnectedSshSessionWithPasswordAuthentication(TESTSERVER_IP, TESTSERVER_PORT, TESTSERVER_USER, TESTSERVER_PWD);
    	
        // test
        assertNotNull(session);
        assertTrue(session.isConnected());     

        // disconnect
        session.disconnect();
        
        // get channel 
        ChannelSftp sftpChannel = session.getSftpChannel();
        
        // test
        assertNotNull(sftpChannel);        
	}
	
	/**
	 * Test file can be copied using SFTP.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testCanCopyFileToRemoteHostUsingSftp() throws Exception {
		
		// create local test file 
		File localFile = new File(testDirectory, randomFileName);
		FileUtils.write(localFile, randomFileName);
		
        // create remote file name
		String remoteFileName = new StringBuilder()
			.append(remoteSharedTestDirectory)
			.append("/")
			.append(randomFileName)
			.toString();

        // create FTP'ed file name as it appears in the local shared VM directory
        File createdFile = new File(localSharedTestDirectory, randomFileName);
		
		// create session
        session = sessionMother.createConnectedSshSessionWithPasswordAuthentication(TESTSERVER_IP, TESTSERVER_PORT, TESTSERVER_USER, TESTSERVER_PWD);
    	
        // test
        assertNotNull(session);
        assertTrue(session.isConnected());     
        
        // get channel 
        ChannelSftp sftpChannel = session.getSftpChannel();
                
        // test
        assertNotNull(sftpChannel);
        		
		// copy (and overwrite)
        sftpChannel.put(localFile.getAbsolutePath(), remoteFileName, ChannelSftp.OVERWRITE);
        
        // test 
        assertTrue(createdFile.exists());
        assertTrue(createdFile.isFile());
        
	}

	/**
	 * Test that attempt to get EXEC channel fails if session isn't connected.
	 * 
	 * @throws SessionException If test fails.
	 */
	@Test(expected = SessionException.class )
	public void testGetexecChannelFailsIfSessionIsntConnected() throws SessionException {
        session = sessionMother.createUnconnectedSshSession();
    	
        // test
        assertNotNull(session);
        assertFalse(session.isConnected());

        // get channel to trigger exception 
        session.getExecuteChannel();                
	}
	
	/**
	 * Test that attempt to get EXEC channel succeeds.
	 * 
	 * @throws SessionException If test fails.
	 */
	@Test
	public void testGetexecChannelSucceedsIfSessionIsConnected() throws SessionException {
        session = sessionMother.createConnectedSshSessionWithPasswordAuthentication(TESTSERVER_IP, TESTSERVER_PORT, TESTSERVER_USER, TESTSERVER_PWD);
    	
        // test
        assertNotNull(session);
        assertTrue(session.isConnected());     
        
        // get channel 
        ChannelExec execChannel = session.getExecuteChannel();
        
        // test
        assertNotNull(execChannel);        
	}

	/**
	 * Test that attempt to get EXEC channel fails after session is disconnected.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test(expected = SessionException.class )
	public void testGetExecChannelFailsAfterSessionIsDisconnected() throws Exception {
        session = sessionMother.createConnectedSshSessionWithPasswordAuthentication(TESTSERVER_IP, TESTSERVER_PORT, TESTSERVER_USER, TESTSERVER_PWD);
    	
        // test
        assertNotNull(session);
        assertTrue(session.isConnected());     

        // disconnect
        session.disconnect();
        
        // get channel 
        ChannelExec execChannel = session.getExecuteChannel();
        
        // test
        assertNotNull(execChannel);        
	}

	/**
	 * Test remote command can be executed on remote host.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testCanExecuteRemoteCommand() throws Exception {
		
		// declare
		int commandStatus = 0;	
		StringBuilder cmdOutput = new StringBuilder();
		
		// create session
        session = sessionMother.createConnectedSshSessionWithPasswordAuthentication(TESTSERVER_IP, TESTSERVER_PORT, TESTSERVER_USER, TESTSERVER_PWD);
    	
        // test
        assertNotNull(session);
        assertTrue(session.isConnected());     
        
        // get channel 
        ChannelExec execChannel = session.getExecuteChannel();
                
        // test
        assertNotNull(execChannel);
        		
		// set command
		execChannel.setCommand("pwd");
	    
		// set streams
		execChannel.setInputStream(null);
		execChannel.setOutputStream(System.out);
		execChannel.setErrStream(System.out);
	 
		// get input stream
		InputStream is = execChannel.getInputStream();
	 
		// connect to execute
		execChannel.connect();
	 
		// capture output
		byte[] tmpBuffer = new byte[BUFFER_SIZE];
		while(true){
			while(is.available()>0){
	          int i=is.read(tmpBuffer, 0, BUFFER_SIZE);
	          if(i<0)break;
	          
	          // get buffer content
	          String bufferContent = new String(tmpBuffer, 0, i);
	          if(bufferContent != null) cmdOutput.append(bufferContent);	          
	          logger.debug("command output: " + bufferContent);
	        }
			
			// handle closure
	        if(execChannel.isClosed()){

	        	// command exit status
	        	commandStatus = execChannel.getExitStatus();
	        	break;
	        }
	        
	        ConcurrencyUtils.waitOneSec();		        
	      }
		
		// disconnect
		execChannel.disconnect();
        
        // test 
        assertEquals(0,commandStatus);
        assertTrue(cmdOutput.length() != 0);        
	}

	
	
}
