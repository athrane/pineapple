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


package com.alpha.pineapple.plugin.net.model;

import java.util.HashMap;

import org.apache.commons.chain.Context;

import com.alpha.pineapple.plugin.infrastructure.model.AccessUncPathTest;
import com.alpha.pineapple.plugin.infrastructure.model.DnsResolutionTest;
import com.alpha.pineapple.plugin.infrastructure.model.FtpServerActiveTest;
import com.alpha.pineapple.plugin.infrastructure.model.FtpServerContainsDirectoryTest;
import com.alpha.pineapple.plugin.infrastructure.model.FtpServerCreateDirectoryTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpConfiguration;
import com.alpha.pineapple.plugin.infrastructure.model.HttpHeaderTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpRedirectTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpStatusCodeTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpTest;
import com.alpha.pineapple.plugin.infrastructure.model.LoadBalancingTest;
import com.alpha.pineapple.plugin.infrastructure.model.SessionStickynessTest;
import com.alpha.pineapple.plugin.infrastructure.model.TcpConnectionTest;


/**
 * Maps values from the schema generated objects into the command context.
 */
public interface Mapper
{

    /**
     * Maps the content of a HTTP-header test case into the command context.
     * 
     * @param test HTTP-header test case which is mapped.
     * @param context The context that  the test case content is mapped to. 
     * @param configurationMap Map containing defined HTTP configurations in the current module model.
     */
    public void mapHttpHeaderTest( HttpHeaderTest test, Context context, HashMap<String, HttpConfiguration> configurationMap );

    /**
     * Maps the content of a HTTP redirect test case into the command context.
     * 
     * @param test HTTP redirect test case which is mapped.
     * @param context The context that the test case content is mapped to.
     * @param configurationMap Map containing defined HTTP configurations in the current module model.  
     */    
	public void mapHttpRedirectTest(HttpRedirectTest test, Context context, HashMap<String, HttpConfiguration> configurationMap );
    
    /**
     * Maps the content of a HTTP status code test case into the command context.
     * 
     * @param test HTTP status code test case which is mapped.
     * @param context The context that the test case content is mapped to.
     * @param configurationMap Map containing defined HTTP configurations in the current module model.  
     */    
    public void mapHttpStatusCodeTest(HttpStatusCodeTest test, Context context, HashMap<String, HttpConfiguration> configurationMap );
		
    /**
     * Maps the content of a Access-UNC-path test case into the command context.
     * 
     * @param test Access-UNC-path test case which is mapped.
     * @param context The context that the test case content is mapped to. 
     */
    public void mapAccessUncPathTest( AccessUncPathTest test, Context context);
    
    /**
     * Maps the content of a FTP-server-active test case into the command context.
     * 
     * @param test FTP-server-active test case which is mapped.
     * @param context The context that the test case content is mapped to. 
     */    
    public void mapFtpServerActiveTest( FtpServerActiveTest test, Context context);

    /**
     * Maps the content of a FTP-server-contains-directory test case into the command context.
     * 
     * @param test FTP-server-contains-directory test case which is mapped.
     * @param context The context that the test case content is mapped to. 
     */        
    public void mapFtpServerContainsDirectoryTest( FtpServerContainsDirectoryTest test, Context context );

    /**
     * Maps the content of a FTP-server-create-directory test case into the command context.
     * 
     * @param test FTP-server-create-directory test case which is mapped.
     * @param context The context that the test case content is mapped to. 
     */            
    public void mapFtpServerCreateDirectoryTest( FtpServerCreateDirectoryTest test, Context context );    

    /**
     * Maps the content of a Stickyness test case into the command context.
     * 
     * @param test Stickyness test case which is mapped.
     * @param context The context that the test case content is mapped to.
     * @param configurationMap Map containing defined HTTP configurations in the current module model.
     */    
	public void mapStickynessTest(SessionStickynessTest test, Context context, HashMap<String, HttpConfiguration> configurationMap );

    /**
     * Maps the content of a Load-balancing test case into the command context.
     * 
     * @param test Load-balancing test case which is mapped.
     * @param context The context that the test case content is mapped to.
     * @param configurationMap Map containing defined HTTP configurations in the current module model.
     */    
	public void mapLoadBalancingTest(LoadBalancingTest test, Context context, HashMap<String, HttpConfiguration> configurationMap );

    /**
     * Maps the content of a TCP connection test case into the command context.
     * 
     * @param test TCP connection test case which is mapped.
     * @param context The context that  the test case content is mapped to. 
     */
    public void mapTcpConnectionTest( TcpConnectionTest test, Context context );

    /**
     * Maps the content of a DNS resolution test case into the command context.
     * 
     * @param test DNS resolution test case which is mapped.
     * @param context The context that  the test case content is mapped to. 
     */
    public void mapDnsResolutionTest( DnsResolutionTest test, Context context );

    /**
     * Maps the content of a DNS forward resolution test case into the command context.
     * 
     * @param test DNS resolution test case which is mapped.
     * @param context The context that  the test case content is mapped to. 
     */
    public void mapDnsForwardResolutionTest( DnsResolutionTest test, Context context );

    /**
     * Maps the content of a DNS reverse resolution test case into the command context.
     * 
     * @param test DNS resolution test case which is mapped.
     * @param context The context that  the test case content is mapped to. 
     */
    public void mapDnsReverseResolutionTest( DnsResolutionTest test, Context context );

    /**
     * Maps the content of a HTTP test case into the command context.
     * 
     * @param test HTTP test case which is mapped.
     * @param context The context that the test case content is mapped to.
     * @param configurationMap Map containing defined HTTP configurations in the current module model.
     */    
	public void mapHttpTest(HttpTest test, Context context, HashMap<String, HttpConfiguration> configurationMap );
    
}
