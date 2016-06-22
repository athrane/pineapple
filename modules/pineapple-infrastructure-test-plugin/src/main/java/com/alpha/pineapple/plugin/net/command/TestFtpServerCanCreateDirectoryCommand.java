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


package com.alpha.pineapple.plugin.net.command;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.command.test.TestCommand;

/**
 * <p>Implementation of the 
 * <code>com.alpha.pineapple.command.test.TestCommand</code> interface 
 * tests whether a FTP server can create and delete a requested directory.</p>
 * 
 * <p>Precondition for execution of the command is definition of these keys in 
 * the context:
 * 
 * <ul>
 * <li><code>description</code> defines a human readable description of the 
 * test. The type is <code>java.lang.String</code>.</li> 
 * 
 * <li><code>hostname</code> defines host name of the FTP server. The type 
 * is <code>java.lang.String</code>.</li>
 * 
 * <li><code>port</code> defines the port number of the FTP server. The type 
 * is <code>int</code>.</li>
 * 
 * <li><code>user</code> defines the user to access the FTP server. The type is 
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>password</code> defines the password to access the FTP server. 
 * The type is <code>java.lang.String</code>.</li>
  * </ul>
 * </p>      
 *  
 * <p>Postcondition after execution of the command is definition of these keys 
 * in the context:
 * 
 * <ul>
 * <li><code>message</code> which contains a human readable 
 * description of how the result of the test. The type is 
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>result</code> which contains the result of the 
 * test as a boolean value. The type is <code>java.lang.Boolean</code>.</li>             
 * </ul>
 * </p>           
 */
public class TestFtpServerCanCreateDirectoryCommand implements TestCommand
{

    /**
     * Key used to identify property in context: Name of the host.
     */
    public static final String HOSTNAME_KEY = "hostname";

    /**
     * Key used to identify property in context: Defines the port number.
     */
    public static final String PORT_KEY = "port";

    /**
     * Key used to identify property in context: Defines the user.
     */
    public static final String USER_KEY = "user";

    /**
     * Key used to identify property in context: Defines the password.
     */
    public static final String PASSWORD_KEY = "password";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Test description.
     */
    @Initialize( DESCRIPTION_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )
    String description;
    
    /**
     * Port number.
     */
    @Initialize( PORT_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )    
    int port;

    /**
     * Host name.
     */
    @Initialize( HOSTNAME_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )        
    String hostname;

    /**
     * User.
     */
    @Initialize( USER_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )            
    String user;

    /**
     * Password.
     */
    @Initialize( PASSWORD_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )            
    String password;

    /**
     * Directory to create and delete.
     */
    String directory;

    /**
     * Test result.
     */
    boolean testSucceded;

    public boolean execute( Context context ) throws Exception
    {

        // log debug message
        if ( logger.isDebugEnabled() )
            logger.debug( "Starting ftp-server-can-create-directory test." );

        // initialize command
        CommandInitializer initializer =  new CommandInitializerImpl();
        initializer.initialize( context, this );

        // create directory name
        StringBuilder dirName = new StringBuilder();
        dirName.append( this.getClass().getName() );
        this.directory = dirName.toString();
        
        // run test
        runTest();

        // save test result
        String testMessage = createTestMessage();
        context.put( MESSAGE_KEY, testMessage );
        context.put( RESULT_KEY, new Boolean( testSucceded ) );

        // log debug message
        if ( logger.isDebugEnabled() )
        {
            logger.debug( testMessage );
            logger.debug( "successfully completed ftp-server-can-create-directory test." );
        }

        return Command.CONTINUE_PROCESSING;
    }

    /**
     * Run the test.
     */
    void runTest() throws Exception
    {
        // create client
        FTPClient client;
        client = new FTPClient();

        // create host address
        InetAddress inetAddress = InetAddress.getByName( this.hostname );

        // connect
        client.connect( inetAddress, this.port );
        String reply = client.getReplyString();

        // log debug message
        if ( logger.isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "FTP-connect returned reply code <" );
            message.append( reply );
            message.append( ">." );
            logger.debug( message.toString() );
        }

        // test reply code
        if ( !FTPReply.isPositiveCompletion( client.getReplyCode() ) )
        {
            testSucceded = false;
            return;
        }

        // log on
        client.login( this.user, this.password );
        reply = client.getReplyString();

        // log debug message
        if ( logger.isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "FTP-logon returned reply code <" );
            message.append( reply );
            message.append( ">." );
            logger.debug( message.toString() );
        }

        // test reply code
        if ( !FTPReply.isPositiveCompletion( client.getReplyCode() ) )
        {
            testSucceded = false;
            return;
        }

        // if directory exists delete it first
        if ( isDirectoryDefined( client ) ) {                       
            deleteDirectory( client );
            
            // test reply code
            if ( !FTPReply.isPositiveCompletion( client.getReplyCode() ) )
            {
                // fail text if existing directory couldn't be deleted.
                testSucceded = false;
                return;
            }            
        }
            
        // try to create the directory
        createDirectory( client );
        
        // if directory exists the mark test as succeeded.
        if ( isDirectoryDefined( client ) ) {
            testSucceded = true;
            
            // clean up, delete the directory
            deleteDirectory( client );            
            return;
            
        } else {
            testSucceded = false;            
        }                
    }

    /**
     * Create test directory.
     * 
     * @param client The FTP client
     * 
     * @throws Exception if Directory creation fails.
     * 
     */
    void createDirectory( FTPClient client ) throws Exception
    {
        // create the directory
        client.makeDirectory( this.directory );

        // get reply
        String reply = client.getReplyString();        
        
        // log debug message
        if ( logger.isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Directory creation returned reply code <" );
            message.append( reply );
            message.append( ">." );
            logger.debug( message.toString() );
        }        
    }

    /**
     * Delete test directory.
     * 
     * @param client The FTP client
     * @throws Exception if Directory deletion fails.
     */
    void deleteDirectory( FTPClient client ) throws Exception
    {
        // log debug message
        if ( logger.isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Starting deletion of directory <" );
            message.append( this.directory );
            message.append( ">." );
            logger.debug( message.toString() );
        }        
        
        // get the files
        FTPFile[] ftpFiles = getFileList( client );

        // exit if get list failed.                
        if( ftpFiles == null) {
            // log debug message
            if ( logger.isDebugEnabled() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "Aborted deletion of directory <<" );
                message.append( this.directory );
                message.append( "> because file list retrieved failed." );
                logger.debug( message.toString() );
            }
            
            // exit 
            return;
        }
        
        // get the directory
        FTPFile ftpDir = getDirectory( ftpFiles );
        
        // delete the directory
        client.removeDirectory( ftpDir.getName() );
        
        // get reply
        String reply = client.getReplyString();        
        
        // log debug message
        if ( logger.isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Directory deletion  returned reply code <" );
            message.append( reply );
            message.append( ">." );
            logger.debug( message.toString() );
        }        
    }

    /**
     * Get file list 
     * 
     * @param The FTP client. 
     * 
     * @throws Exception If getting file list fails.
     */
    FTPFile[] getFileList( FTPClient client ) throws Exception
    {
        // list files
        FTPFile[] ftpFiles = client.listFiles();

        // get reply
        String reply = client.getReplyString();        
        
        // log debug message
        if ( logger.isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "List files returned reply code <" );
            message.append( reply );
            message.append( ">." );
            logger.debug( message.toString() );
        }        

        // test reply code
        if ( !FTPReply.isPositiveCompletion( client.getReplyCode() ) )
        {
            testSucceded = false;
            return null;
        }
        
        // log debug message
        if ( logger.isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "List files returned file list <" );
            message.append( ReflectionToStringBuilder.toString( ftpFiles ) );
            message.append( ">." );
            logger.debug( message.toString() );
        }
        
        return ftpFiles;
    }

    /**
     * Look for directory in located file set.
     * 
     * @param ftpFiles
     *            file set from FTP server.
     *            
     * @return true if directory is found.
     * 
     * @throws IOException If operation fails.
     */
    boolean isDirectoryDefined( FTPClient client ) throws IOException        
    {
        // list files
        FTPFile[] ftpFiles = client.listFiles();

        // exit if get list failed.        
        if( ftpFiles == null) {
            // log debug message
            if ( logger.isDebugEnabled() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "Aborted directory existence test of <" );
                message.append( this.directory );
                message.append( "> because file list retrieved failed." );
                logger.debug( message.toString() );
            }
            
            // exit 
            return false;
        }
        
        for ( int i = 0; i < ftpFiles.length; i++ )
        {
            FTPFile ftpFile = ftpFiles[i];
            if ( ftpFile.isDirectory() )
            {
                // log debug message
                if ( logger.isDebugEnabled() )
                {
                    StringBuilder message = new StringBuilder();
                    message.append( "Accessing FTP file <" );
                    message.append( ReflectionToStringBuilder.toString( ftpFile ) );
                    message.append( ">." );
                    logger.debug( message.toString() );
                }

                // get directory name
                String name = ftpFile.getName();

                // test
                if ( this.directory.equals( name ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get directory in located file set.
     * 
     * @param ftpFiles
     *            file set from FTP server.
     * @return directory. 
     */
    FTPFile getDirectory( FTPFile[] ftpFiles )
    {
        for ( int i = 0; i < ftpFiles.length; i++ )
        {
            FTPFile ftpFile = ftpFiles[i];
            if ( ftpFile.isDirectory() )
            {
                // log debug message
                if ( logger.isDebugEnabled() )
                {
                    StringBuilder message = new StringBuilder();
                    message.append( "Accessing FTP file <" );
                    message.append( ReflectionToStringBuilder.toString( ftpFile ) );
                    message.append( ">." );
                    logger.debug( message.toString() );
                }

                // get directory name
                String name = ftpFile.getName();

                // test
                if ( this.directory.equals( name ) )
                {
                    return ftpFile;
                }
            }
        }

        return null;
    }
    
    
    /**
     * Create test message describing the outcome of the test.
     * 
     * @return test message.
     */
    public String createTestMessage()
    {
        if ( this.testSucceded )
        {

            // create info message
            StringBuilder message = new StringBuilder();
            message.append( "TEST SUCCEDED - ftp-server-can-create-directory <" );
            message.append( description );
            message.append( "> connected to <" );
            message.append( hostname );
            message.append( ":" );
            message.append( this.port );
            message.append( "> and create directory <" );
            message.append( this.directory );
            message.append( ">." );

            return message.toString();

        }
        else
        {

            // create info message
            StringBuilder message = new StringBuilder();
            message.append( "TEST FAILED - ftp-server-can-create-directory <" );
            message.append( description );
            message.append( "> couldn't connect to <" );
            message.append( hostname );
            message.append( ":" );
            message.append( this.port );
            message.append( "> and create directory <" );
            message.append( this.directory );
            message.append( ">." );

            return message.toString();
        }
    }

}
