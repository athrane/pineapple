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


package com.alpha.testutils;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.alpha.pineapple.plugin.infrastructure.model.AssertionValues;
import com.alpha.pineapple.plugin.infrastructure.model.Header;
import com.alpha.pineapple.plugin.infrastructure.model.Headers;
import com.alpha.pineapple.plugin.infrastructure.model.HttpConfiguration;
import com.alpha.pineapple.plugin.infrastructure.model.HttpHeaderTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpRedirectTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpStatusCodeTest;
import com.alpha.pineapple.plugin.infrastructure.model.Infrastructure;
import com.alpha.pineapple.plugin.infrastructure.model.LoadBalancingTest;
import com.alpha.pineapple.plugin.infrastructure.model.NonExistingHeader;
import com.alpha.pineapple.plugin.infrastructure.model.NonExistingHeaders;
import com.alpha.pineapple.plugin.infrastructure.model.ObjectFactory;
import com.alpha.pineapple.plugin.infrastructure.model.Property;
import com.alpha.pineapple.plugin.infrastructure.model.Servers;
import com.alpha.pineapple.plugin.infrastructure.model.Urls;

/**
 * Implementation of the ObjectMother pattern, 
 * provides helper functions for unit testing by creating content for operations.
 */
public class ObjectMotherContent
{

    /**
     * remote URL.
     */
    static final String HTTP_REMOTE_HOST = "http://127.0.0.1:3000/sniffer";    
	
	/**
	 * Key of default HTTP configuration.
	 */
    public static final String DEFAULT_CONFIG_KEY = "default-config";

	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * Infrastructure. object factory.
     */
	ObjectFactory infrastructureFactory;    
        
    /**
	 * ObjectMotherContent constructor.
	 */
	public ObjectMotherContent() {
		super();

        // create test case factory
        infrastructureFactory = new com.alpha.pineapple.plugin.infrastructure.model.ObjectFactory();		
	}

	/**
     * Create empty Infrastructure document.
     * 
     * @return empty Infrastructure document.
     */
    public Infrastructure createEmptyInfrastructure() {
        
    	Infrastructure infDoc = infrastructureFactory.createInfrastructure();          
        return infDoc;        
    }

	/**
     * Create map with single model HTTP Configuration with default configuration. The configuration
     * object has the key "default-config".
     * 
     * @return map with single model HTTP Configuration with default configuration
     */
    public HashMap<String, HttpConfiguration> createHttpConfigurationMapWithSingleDefaultHttpConfig() {
    	
		// create map
    	HashMap<String, HttpConfiguration> configurationMap;
    	configurationMap = new HashMap<String, HttpConfiguration>();
    	
		// create HTTP configuration
		HttpConfiguration httpConfig = infrastructureFactory.createHttpConfiguration();
		
		// add config
    	configurationMap.put(DEFAULT_CONFIG_KEY, httpConfig);
		
    	return configurationMap;
    }

    /**
     * Create model load balancing test case.
     * 
     * @return model load balancing test case.
     */
	public LoadBalancingTest createLoadBalancingTest() {
		
		// create test case
		LoadBalancingTest testcase = infrastructureFactory.createLoadBalancingTest();
		
		testcase.setDescription("decription");
		testcase.setHttpConfigurationRef(DEFAULT_CONFIG_KEY);
		testcase.setRequests(10);
				
		testcase.setAssert(infrastructureFactory.createAssertionValues());
		AssertionValues assertion = testcase.getAssert();
		assertion.setProperties(infrastructureFactory.createProperties());
		List<Property> properties = assertion.getProperties().getProperty();		
		Property property = infrastructureFactory.createProperty();
		property.setName("environment");
		property.setValue("null");
		properties.add(property);	
		property = infrastructureFactory.createProperty();
		property.setName("location");
		property.setValue("null");
		properties.add(property);	
		property = infrastructureFactory.createProperty();
		property.setName("host");
		property.setValue("null");
		properties.add(property);	
		
		Servers servers = infrastructureFactory.createServers();
		List<String> serversList = servers.getServer();
		serversList.add("null");
		testcase.getAssert().setServers(servers);
		
		Urls urls = infrastructureFactory.createUrls();
		List<String> urlsList = urls.getUrl();
		urlsList.add(HTTP_REMOTE_HOST);
		testcase.setUrls(urls);
		
		return testcase ;
	}

	/**
	 * Create model HTTP redirect test case.
	 * 
	 * @param url Target URL   
	 * @param redirectUrl Expected redirect URL.
	 * 
	 * @return model HTTP redirect test case.
	 */
	public HttpRedirectTest createHttpRedirectTest(String url, String redirectUrl) {
		
		// create test case
		HttpRedirectTest testcase = infrastructureFactory.createHttpRedirectTest();
		
		testcase.setDescription("decription");
		testcase.setHttpConfigurationRef(DEFAULT_CONFIG_KEY);		
        testcase.setUrl(url);
        testcase.setAssert(infrastructureFactory.createHttpRedirectAssertionValues());
        testcase.getAssert().setUrl(redirectUrl);        

        return testcase;
	}

	/**
	 * Create model HTTP status code test case.
	 * 
	 * @param url Target URL   
	 * @param statusCode Expected HTTP status code..
	 * 
	 * @return model HTTP redirect test case.
	 */
	public HttpStatusCodeTest createHttpStatusCodeTest(String url, int statusCode) {
		
		// create test case
		HttpStatusCodeTest testcase = infrastructureFactory.createHttpStatusCodeTest();
		
		testcase.setDescription("decription");
		testcase.setHttpConfigurationRef(DEFAULT_CONFIG_KEY);		
        testcase.setUrl(url);
        testcase.setExpected(statusCode);
        return testcase;
	}
	
	/**
	 * Create model HTTP header test case.
	 * 
	 * @param url Target URL 
	 * @param headers Map of HTTP headers.  
	 * @param nonPresentheaders Array of non existing HTTP headers.
	 * 
	 * @return model HTTP redirect test case.
	 */
	public HttpHeaderTest createHttpHeaderTest(String url, HashMap<String, String> headers, String[] nonPresentheaders ) {
		
		// create test case
		HttpHeaderTest testcase = infrastructureFactory.createHttpHeaderTest();
		
		testcase.setDescription("decription");
		testcase.setHttpConfigurationRef(DEFAULT_CONFIG_KEY);		
        testcase.setUrl(url);
        testcase.setAssert(infrastructureFactory.createHttpHeaderAssertionValues());
        
        Headers modelHeaders = infrastructureFactory.createHeaders();        
        for ( String name : headers.keySet()) {
        	Header modelHeader = infrastructureFactory.createHeader();
        	modelHeader.setName(name);
        	modelHeader.setValue(headers.get(name));
        	modelHeaders.getHeader().add( modelHeader);
        }
		testcase.getAssert().setHeaders(modelHeaders);
		
        NonExistingHeaders modelNonPresentheaders = infrastructureFactory.createNonExistingHeaders();
        for ( String name : nonPresentheaders ) {
        	NonExistingHeader modelHeader = infrastructureFactory.createNonExistingHeader();
        	modelHeader.setName(name);
        	modelNonPresentheaders.getHeader().add( modelHeader);
        }
		testcase.getAssert().setNonexistingHeaders(modelNonPresentheaders);

        return testcase;
	}
	
}
