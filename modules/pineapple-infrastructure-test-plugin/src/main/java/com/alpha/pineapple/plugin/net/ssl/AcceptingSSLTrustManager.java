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


package com.alpha.pineapple.plugin.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

/**
 *  
 * <p>
 * AcceptingSSLTrustManager which unlike default {@link X509TrustManager} accepts
 * any certificate. The Implementation is based on the example in the Java almananc.
 * For info please consult: <A HREF="http://www.exampledepot.com/egs/javax.net.ssl/TrustAll.html?l=rel"> 
 * e502. Disabling Certificate Validation in an HTTPS Connection</A>. 
 * </p>
 * 
 * <p>
 * This trust manager SHOULD NOT be used for productive systems due to security
 * reasons, unless it is a concious decision and you are perfectly aware of
 * security implications of accepting self-signed certificates
 * </p>
 *  
 */
public class AcceptingSSLTrustManager implements X509TrustManager
{

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
    public void checkClientTrusted( X509Certificate[] chain, String authType ) throws CertificateException
    {
        // log debug message
        if(logger.isDebugEnabled()) {
            StringBuilder message = new StringBuilder();
            message.append( "ACCEPTING client with certificate chain <" );
            message.append( ReflectionToStringBuilder.toString ( chain ));
            message.append( "> with authentication type <" );
            message.append( authType );
            message.append( ">." );
            logger.debug( message.toString() );            
        }
        
    }

    public void checkServerTrusted( X509Certificate[] chain, String authType ) throws CertificateException
    {
        // log debug message
        if(logger.isDebugEnabled()) {
            StringBuilder message = new StringBuilder();
            message.append( "ACCEPTING server with certificate chain <" );
            message.append( ReflectionToStringBuilder.toString ( chain ));
            message.append( "> with authentication type <" );
            message.append( authType );
            message.append( ">." );
            logger.debug( message.toString() );      
        }
    }

    public X509Certificate[] getAcceptedIssuers()
    {
        /**
    }
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

        KeyStore keystore = null;
        factory.init(keystore);
        
        TrustManager[] trustmanagers = factory.getTrustManagers();
        
        if (trustmanagers.length == 0) {
            throw new NoSuchAlgorithmException("no trust manager found");
        }
        
        X509TrustManager standardTrustManager;
        standardTrustManager = (X509TrustManager)trustmanagers[0];

        X509Certificate[] issuers = standardTrustManager.getAcceptedIssuers();
        
        // log debug message
        if(logger.isDebugEnabled()) {
            StringBuilder message = new StringBuilder();
            message.append( "ACCEPTED issuers <" );
            message.append( ReflectionToStringBuilder.toString ( issuers ));
            message.append( ">." );
            logger.debug( message.toString() );    
        }
        
        */

        // log debug message
        if(logger.isDebugEnabled()) {
            StringBuilder message = new StringBuilder();
            message.append( "Returning null ACCEPTED issuers." );
            logger.debug( message.toString() );    
        }
        
        return new X509Certificate[0]; 
    }

    
}
