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

import java.net.InetAddress;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
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
 * which tests whether a FTP server is active and can be logged on to.</p>
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
 * The type is <code>java.lang.String</code>.
 * </li>
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
public class TestFtpServerActiveCommand implements TestCommand
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
     * Test result.
     */
    boolean testSucceded;
    
    public boolean execute( Context context ) throws Exception
    {

        // log debug message
        if(logger.isDebugEnabled()) 
            logger.debug( "Starting ftp-server-active test." );
        
        // initialize command
        CommandInitializer initializer =  new CommandInitializerImpl();
        initializer.initialize( context, this );

        // run test
        runTest();

        // save test result
        String testMessage = createTestMessage();        
        context.put( MESSAGE_KEY, testMessage );
        context.put( RESULT_KEY, new Boolean( testSucceded ) );
        
        // log debug message
        if(logger.isDebugEnabled()) {
            logger.debug( testMessage );                    
            logger.debug( "successfully completed ftp-server-active test." );                    
        }
        
        return Command.CONTINUE_PROCESSING;
    }


    /**
     * Run the test.
     */
    void runTest()
    {                         
        try
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
            if(logger.isDebugEnabled()) {
                StringBuilder message = new StringBuilder();
                message.append( "FTP-connect returned reply code <" );
                message.append( reply );
                message.append( ">." );
                logger.debug( message.toString() );
            }
            
            // test reply code
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                testSucceded = false;
                return;
            }

            // log on
            client.login( this.user, this.password );
            reply = client.getReplyString();            
            
            // log debug message            
            if(logger.isDebugEnabled()) {
                StringBuilder message = new StringBuilder();
                message.append( "FTP-logon returned reply code <" );
                message.append( reply );
                message.append( ">." );            
                logger.debug( message.toString() );                
            }

            // test reply code
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                testSucceded = false;
                return;
            }
            
            // list files
            FTPFile[] ftpFiles = client.listFiles();
            reply = client.getReplyString();            
            
            // log debug message            
            if(logger.isDebugEnabled()) {
                StringBuilder message = new StringBuilder();
                message.append( "FTP-list files returned reply code <" );
                message.append( reply );
                message.append( "> with <" );
                message.append( ftpFiles.length );
                message.append( "> file(s) found. ");
                logger.debug( message.toString() );                
            }            

            // test reply code
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                testSucceded = false;
                return;
            }
            
            testSucceded = true;            
        }
        catch ( Exception e )
        {
            testSucceded = false;
        }        
    }
    
    /**
     * Create test message describing the outcome of the test.
     * 
     * @return test message.
     */
    public String createTestMessage() {
        if (this.testSucceded) {
            
            // create info message
            StringBuilder message = new StringBuilder();
            message.append( "TEST SUCCEDED - ftp-server-active for <" );
            message.append( description );
            message.append( "> connected to <" );            
            message.append( hostname );
            message.append( ":" );
            message.append( this.port );            
            message.append( ">." );            
            
            return message.toString();
            
        } else {
            
            // create info message
            StringBuilder message = new StringBuilder();
            message.append( "TEST FAILED - ftp-server-active for <" );
            message.append( description );
            message.append( "> couldn't connect to <" );            
            message.append( hostname );
            message.append( ":" );
            message.append( this.port );            
            message.append( ">." );            

            return message.toString();            
        }
    }
        
}
