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


package com.alpha.pineapple.plugin.ssh.session;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.plugin.PluginSession;
import com.alpha.pineapple.plugin.ssh.SshConstants;
import com.alpha.pineapple.plugin.ssh.utils.JSchLog4JLogger;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.session.SessionDisconnectException;
import com.alpha.pineapple.session.SessionException;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Implementation of the <code>SshSession</code> interface. 
 */
@PluginSession
public class SshSessionImpl implements SshSession
{   
	/**
	 * SFTP Channel ID.
	 */
	static final String SFTP_CHANNEL_ID = "sftp";

	/**
	 * EXEC Channel ID.
	 */
	static final String EXEC_CHANNEL_ID = "exec";
	
	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
 
    /**
     * SSH resource.
     */
    com.alpha.pineapple.model.configuration.Resource resource;

    /**
     * Resource credential.
     */
    Credential credential;

    /**
     * Command runner
     */
    @Resource
    CommandRunner commandRunner;   
    
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
    
    /**
     * Resource property getter.
     */
    @Resource    
    ResourcePropertyGetter propertyGetter;

    /**
	 * JSCH log4j logger.
     */
    @Resource
    JSchLog4JLogger jschLogger;
    
    /**
     * SSH library.
     */
    @Resource    
	JSch jsch;
		
    /**
     * SSH Session.
     */	
	Session sshSession;

	/**
	 * SFTP channel.
	 */
	ChannelSftp sftpChannel;

	/**
	 * EXEC channel.
	 */
	ChannelExec execChannel;
	
    /**
     * SshSessionImpl no-arg constructor.
     * 
     * @throws Exception If Session creation fails.
     */
    public SshSessionImpl() throws Exception {
        super();                        
        setLogger();
    }
    
    /**
     * Set logger o the JSCH API.
     */
    public void setLogger() {
        com.jcraft.jsch.JSch.setLogger(jschLogger);    	
    }

    public void connect( com.alpha.pineapple.model.configuration.Resource resource, Credential credential ) throws SessionConnectException {
        // validate parameters
        Validate.notNull( resource, "resource is undefined." );
        Validate.notNull( credential, "credential is undefined." );
        
        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { resource, credential.getId() };    	        	
            String message = messageProvider.getMessage("ss.connect_start", args );
            logger.debug( message );            
    	}
        
	    // store in fields
	    this.credential = credential;
	    this.resource = resource;

        try {  
		    // create resource property getter
		    ResourcePropertyGetter getter = new ResourcePropertyGetter( resource );		    
		    
		    // get resource attributes            
		    String host = getter.getProperty( "host" );
		    int port = Integer.parseInt( getter.getProperty( "port", SshConstants.DEFAULT_PORT ) );
		    int connectTimeOut = Integer.parseInt( getter.getProperty( "timeout", SshConstants.DEFAULT_TIMEOUT ) );
		    String strictHostKeyChecking = getter.getProperty( "strict-host-key-checking", SshConstants.DEFAULT_STRICT_HOSTKEY_CHECKING );
		    
		    // get credential attributes
		    String user = credential.getUser();
            String password = credential.getPassword();

            connect(host, port, user, password, connectTimeOut);
            
        } catch (SessionConnectException e) {
        	
        	// rethrow session exception from connect
        	throw e;
        	
        } catch ( Exception e ) {
        	
        	// clear session
        	sshSession = null;
        	
            Object[] args = { resource.getId(), e };    	        	
            String message = messageProvider.getMessage("ss.connect_failure", args );
            throw new SessionConnectException( message, e );            
        }        
        
        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { resource.getId() };    	        	
            String message = messageProvider.getMessage("ss.connect_completed", args );
            logger.debug( message );            
    	}        
        
    }
        
    @Override
	public void connect(String host, int port, String user, String password, int timeOut) throws SessionConnectException {
        // validate parameters
        Validate.notNull( host, "host is undefined." );
        Validate.notNull( port, "port is undefined." );
        Validate.notNull( user, "user is undefined." );
        Validate.notNull( password, "password is undefined." );
        Validate.notNull( timeOut, "timeOut is undefined." );

        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { user, host, port };    	        	    	        	
            String message = messageProvider.getMessage("ss.connect_start2", args );
            logger.debug( message );            
    	}
    	
	    try {
		    // create SSH session	    	
			sshSession = jsch.getSession(user, host, port);
			sshSession.setPassword(password);
		    
		    // add properties
		    java.util.Properties config = new java.util.Properties();
		    config.put("StrictHostKeyChecking", "no");
		    config.put("PreferredAuthentications", "password");		    
		    sshSession.setConfig(config);		    
		    
		    // connect
		    sshSession.connect(timeOut);
		    
	    } catch (Exception e) {
	    	
        	// clear session
        	sshSession = null;
        	sftpChannel = null;
        	
            Object[] args = { host, port, user, e };    	        	
            String message = messageProvider.getMessage("ss.connect_failure2", args );
            throw new SessionConnectException( message, e );            
		}

        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { host, port, user };    	        	
            String message = messageProvider.getMessage("ss.connect_completed2", args );
            logger.debug( message );            
    	}        	    
	}

    @Override
	public void connect(String host, int port, String user, String passPhrase, String privateKeyFile, int timeOut) throws SessionConnectException {
        // validate parameters
        Validate.notNull( host, "host is undefined." );
        Validate.notEmpty(user, "host is empty." );        
        Validate.notNull( port, "port is undefined." );
        Validate.notNull( user, "user is undefined." );
        Validate.notNull( user, "privateKeyFile is undefined." );        
        Validate.notEmpty(user, "privateKeyFile is empty." );        
        Validate.notNull( timeOut, "timeOut is undefined." );

        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { user, host, port };    	        	    	        	
            String message = messageProvider.getMessage("ss.connect_start2", args );
            logger.debug( message );            
    	}
    	
	    try {

	    	if(isNotEmpty(passPhrase)) {
		    	// set identity with private key and pass phrase
		    	jsch.addIdentity(privateKeyFile, passPhrase);	    		
	    	} else {
		    	// set identity with private key and no pass phrase
		    	jsch.addIdentity(privateKeyFile);	    			    		
	    	}
	    		    	
		    // create SSH session	    	
			sshSession = jsch.getSession(user, host, port);
		    
		    // add properties
		    java.util.Properties config = new java.util.Properties();
		    config.put("StrictHostKeyChecking", "no");
		    sshSession.setConfig(config);		    
		    
		    // connect
		    sshSession.connect(timeOut);
		    
	    } catch (Exception e) {
	    	
        	// clear session
        	sshSession = null;
        	sftpChannel = null;
        	
            Object[] args = { host, port, user, e };    	        	
            String message = messageProvider.getMessage("ss.connect_failure2", args );
            throw new SessionConnectException( message, e );            
		}

        // log debug message
    	if(logger.isDebugEnabled()) {
            Object[] args = { host, port, user };    	        	
            String message = messageProvider.getMessage("ss.connect_completed2", args );
            logger.debug( message );            
    	}        	    
	}
        
	public void disconnect() throws SessionDisconnectException {
    	
        // exit if not connected.
        if ( !isConnected() ) {
            logger.error( messageProvider.getMessage("ss.disconnect_notconnected"));
            return;
        }
        
        // disconnect and clear SFTP channel
        if (sftpChannel != null) {
        	if(sftpChannel.isConnected()) sftpChannel.disconnect();
        	sftpChannel = null;
        }

        // disconnect and clear EXEC channel
        if (execChannel != null) {
        	if(execChannel.isConnected()) execChannel.disconnect();
        	execChannel = null;
        }
        
        // disconnect and clear session
        sshSession.disconnect();
        sshSession = null;
    }
      
	public com.alpha.pineapple.model.configuration.Resource getResource() {
		return this.resource;
	}

	public Credential getCredential() {
		return this.credential;
	}
    	
    public boolean isConnected() {
        return ( sshSession != null );
    }

	@Override
	public ChannelSftp getSftpChannel() throws SessionException {

        // exit if not connected.
        if ( !isConnected() ) {
        	String message = messageProvider.getMessage("ss.sftp_notconnected_failure");
            throw new SessionException( message );
        }		
		
		try {
			sftpChannel = (ChannelSftp)sshSession.openChannel(SFTP_CHANNEL_ID);
	        sftpChannel.connect();
	        
	        // log debug message
	    	if(logger.isDebugEnabled()) {
	            Object[] args = { sftpChannel.getId(), sftpChannel.getHome() };    	        	
	            String message = messageProvider.getMessage("ss.sftp_connect_completed", args );
	            logger.debug( message );            
	    	}        	    
	        	        
	        return sftpChannel;
						
		} catch (Exception e) {
            Object[] args = { SFTP_CHANNEL_ID, e };    	        	
            String message = messageProvider.getMessage("ss.sftp_connect_failure", args );
            throw new SessionException( message, e );            
		}
		
	}	
    
		
	@Override
	public ChannelExec getExecuteChannel() throws SessionException {

		// exit if not connected.
        if ( !isConnected() ) {
        	String message = messageProvider.getMessage("ss.exec_notconnected_failure");
            throw new SessionException( message );
        }		
		
		try {
			execChannel = (ChannelExec) sshSession.openChannel(EXEC_CHANNEL_ID);
			
			// versus the sftp channel then exec channel the connect isn't
			// invoked on a create channel. The command needs to be set first
			// before connect is invoked - is done in the operation.
			//execChannel.connect();
	        
	        // log debug message
	    	if(logger.isDebugEnabled()) {
	            Object[] args = { execChannel.getId() };    	        	
	            String message = messageProvider.getMessage("ss.exec_connect_completed", args );
	            logger.debug( message );            
	    	}        	    
	        	        
	        return execChannel;
						
		} catch (Exception e) {
            Object[] args = { EXEC_CHANNEL_ID, e };    	        	
            String message = messageProvider.getMessage("ss.exec_connect_failure", args );
            throw new SessionException( message, e );            
		}		
	}
	
	/**
	 * Return true if string ins't empty.
	 * 
	 * @param string string to test.
	 * 
	 * @return true if string ins't empty.
	 */
	boolean isNotEmpty(String string) {
		if (string == null) return false;
		if (string.isEmpty()) return false;		
		return true;
	}
	    
}
