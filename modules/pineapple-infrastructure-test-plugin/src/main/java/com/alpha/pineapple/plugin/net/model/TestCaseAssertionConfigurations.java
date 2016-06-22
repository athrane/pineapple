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

import static com.alpha.pineapple.test.matchers.InfrastructureMatchers.cookieMapsContainsEqualValues;
import static com.alpha.pineapple.test.matchers.InfrastructureMatchers.cookieMapsContainsNonEqualValues;
import static com.alpha.pineapple.test.matchers.InfrastructureMatchers.isHeadersContaining;
import static com.alpha.pineapple.test.matchers.InfrastructureMatchers.isHeadersNotContaining;
import static com.alpha.pineapple.test.matchers.PineappleMatchers.distributedAcrossRange;
import static com.alpha.pineapple.test.matchers.PineappleMatchers.matchingSingleValueInRange;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.chain.Context;
import org.hamcrest.Matcher;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.net.command.TestHttpCommand;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSetImpl;


/**
 * Helper class which which contains predefined assertions for
 * HTTP test cases.
 */
public class TestCaseAssertionConfigurations {

	/**
	 * Name of the Location HTTP header.
	 */
	static final String LOCATION_HEADER = "Location";

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
	
	
   /**
     * Initialize response properties for the HTTP redirect test case and store 
     * them in the command context.
     * 
     * @param context Context where response properties are added to. 
     * @param redirecUrl Redirection target URL. 
     */
	@SuppressWarnings("unchecked")
	void initializeHttpRedirectTestProperties(Context context, String redirecUrl) {
		
		// create HTTP header map with location header
        org.apache.commons.httpclient.Header redirectHeader;
		redirectHeader = new org.apache.commons.httpclient.Header(LOCATION_HEADER, redirecUrl );
		org.apache.commons.httpclient.Header[] headers;
		headers = new org.apache.commons.httpclient.Header[] {redirectHeader}; 
	   	
	   	// create response property info set    	
	   	ResponsePropertyInfoSet properties;
	   	properties = new ResponsePropertyInfoSetImpl();
	   	    	
	   	// create response property info for HTTP status code
		List<Integer> range = Arrays.asList(301,302,303,304,305);
	   	String name = messageProvider.getMessage("tcac.redirect_prop_statuscode");    	
		String xpath = "method/statusCode";
		Matcher<?> intra = null;
		Matcher<?> inter = matchingSingleValueInRange(range);
	   	properties.addProperty(name, xpath, intra, inter);    	
	   		
	   	// create response property for headers 
	   	name = messageProvider.getMessage("tcac.redirect_prop_redirect_header" ); 
		xpath = "method/responseHeaders";		
		intra = null; 			
		inter = everyItem(isHeadersContaining(headers));		
	   	properties.addProperty(name, xpath, intra, inter);

	   	// store properties in context
        context.put( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, properties);
	}	
	
	/**
     * Initialize response properties for the HTTP header test case and store 
     * them in the command context.
     * 
     * @param context Context where response properties are added to. 
     * @param headers Expected HTTP header.
     * @param nonExistingHeaders Non expected HTTP header. 
     */
    @SuppressWarnings("unchecked")
	void initializeHttpHeaderTestProperties(Context context, HashMap<String, String> headers, String[] nonExistingHeaders ) {
    	
    	// create response property info set    	
    	ResponsePropertyInfoSet properties;
    	properties = new ResponsePropertyInfoSetImpl();
    	    	
    	// create response property info for HTTP status code
    	String name = messageProvider.getMessage("tcac.header_prop_statuscode" );
		String xpath = "method/statusCode";		
		Matcher<?> intra = everyItem(equalTo(200));
		Matcher<?> inter = everyItem(equalTo(200));
    	properties.addProperty(name, xpath, intra, inter);    	

    	// create response property for headers 
    	name = messageProvider.getMessage("tcac.header_prop_expected_headers" );    	
		xpath = "method/responseHeaders";		
		intra = everyItem(isHeadersContaining(headers)); 			
		inter = everyItem(isHeadersContaining(headers));		
    	properties.addProperty(name, xpath, intra, inter);

    	// create response property for non present headers
    	name = messageProvider.getMessage("tcac.header_prop_nonexpected_headers");    	    	
		xpath = "method/responseHeaders";		
		intra = everyItem(isHeadersNotContaining(nonExistingHeaders));
		inter = everyItem(isHeadersNotContaining(nonExistingHeaders));		
    	properties.addProperty(name, xpath, intra, inter);
    	
	   	// store properties in context
        context.put( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, properties);
	}

    /**
     * Initialize response properties for the HTTP stickyness test case and store 
     * them in the command context.
 
     * @param context Context where response properties are added to.
     * @param environment Environment property.
     * @param host Host property.
     * @param location Location property.
     * @param servers Servers property.
     */
	@SuppressWarnings("unchecked")
	void initializeHttpStickynessTestProperties(Context context, String environment, String host, String location, String[] servers) {

		// create response property info set    	
    	ResponsePropertyInfoSet properties;
    	properties = new ResponsePropertyInfoSetImpl();    	
    	
    	// create response property info for servers
    	String name = messageProvider.getMessage("tcac.stickyness_prop_servers");
		String xpath = "body/html/body/j2ee-sniffer/system-properties/pineapple.server";		
		List<String> range = Arrays.asList(servers);
		Matcher<?> intra = matchingSingleValueInRange(range);
		Matcher<?> inter = matchingSingleValueInRange(range);		
    	properties.addProperty(name, xpath, intra, inter);    	

    	// create response property info for environment
    	name = messageProvider.getMessage("tcac.stickyness_prop_environment");
		xpath = "body/html/body/j2ee-sniffer/system-properties/pineapple.environment";				
		intra = everyItem(equalTo(environment));
		inter= everyItem(equalTo(environment));
    	properties.addProperty(name, xpath, intra, inter);    	
    	
    	// create response property info for location
    	name = messageProvider.getMessage("tcac.stickyness_prop_location");
		xpath = "body/html/body/j2ee-sniffer/system-properties/pineapple.location";		
		intra = everyItem(equalTo(location));
		inter= everyItem(equalTo(location));
    	properties.addProperty(name, xpath, intra, inter);    	

    	// create response property info for host
    	name = messageProvider.getMessage("tcac.stickyness_prop_host");
		xpath = "body/html/body/j2ee-sniffer/hostname";		
		intra = everyItem(equalTo(host));
		inter= everyItem(equalTo(host));
    	properties.addProperty(name, xpath, intra, inter);    	

    	// create response property info for HTTP status code
    	name = messageProvider.getMessage("tcac.stickyness_prop_statuscode");
		xpath = "method/statusCode";		
		intra = everyItem(equalTo(200));
		inter= everyItem(equalTo(200));
    	properties.addProperty(name, xpath, intra, inter);    	

    	// create response property info for cookies
    	name = messageProvider.getMessage("tcac.stickyness_prop_cookies");
		xpath = "state/cookies";		
		intra = cookieMapsContainsEqualValues();
		inter= cookieMapsContainsEqualValues();
    	properties.addProperty(name, xpath, intra, inter);
    			
	   	// store properties in context
        context.put( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, properties);		
	}    	
	
    /**
     * Initialize response properties for the HTTP load balancing test case and store 
     * them in the command context.
 
     * @param context Context where response properties are added to.
     * @param environment Environment property.
     * @param host Host property.
     * @param location Location property.
     * @param servers Servers property.
     */
	@SuppressWarnings("unchecked")
	void initializeHttpLoadBalancingTestProperties(Context context, String environment, String host, String location, String[] servers) {
		
    	// create response property info set    	
    	ResponsePropertyInfoSet properties;
    	properties = new ResponsePropertyInfoSetImpl();
    	    	
    	// create response property info for servers
    	String name = messageProvider.getMessage("tcac.loadbalancing_prop_servers");
		String xpath = "body/html/body/j2ee-sniffer/system-properties/pineapple.server";		
		List<String> range = Arrays.asList(servers);
		Matcher<?> intra = matchingSingleValueInRange(range);
		Matcher<?> inter = distributedAcrossRange(range);		
    	properties.addProperty(name, xpath, intra, inter);    	

    	// create response property info for environment
    	name = messageProvider.getMessage("tcac.loadbalancing_prop_environment");    	
		xpath = "body/html/body/j2ee-sniffer/system-properties/pineapple.environment";				
		intra = everyItem(equalTo(environment));
		inter= everyItem(equalTo(environment));
    	properties.addProperty(name, xpath, intra, inter);    	
    	
    	// create response property info for location
    	name = messageProvider.getMessage("tcac.loadbalancing_prop_location");    	
		xpath = "body/html/body/j2ee-sniffer/system-properties/pineapple.location";		
		intra = everyItem(equalTo(location));
		inter= everyItem(equalTo(location));
    	properties.addProperty(name, xpath, intra, inter);    	

    	// create response property info for host
    	name = messageProvider.getMessage("tcac.loadbalancing_prop_host");
		xpath = "body/html/body/j2ee-sniffer/hostname";		
		intra = everyItem(equalTo(host));
		inter= everyItem(equalTo(host));
    	properties.addProperty(name, xpath, intra, inter);    	

    	// create response property info for HTTP status code
    	name = messageProvider.getMessage("tcac.loadbalancing_prop_statuscode");    	    	
		xpath = "method/statusCode";		
		intra = everyItem(equalTo(200));
		inter= everyItem(equalTo(200));
    	properties.addProperty(name, xpath, intra, inter);    	

    	// create response property info for cookies
    	name = messageProvider.getMessage("tcac.loadbalancing_prop_cookies");    	    	
		xpath = "state/cookies";		
		intra = cookieMapsContainsEqualValues();
		inter= cookieMapsContainsNonEqualValues();
    	properties.addProperty(name, xpath, intra, inter);

	   	// store properties in context
        context.put( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, properties);		    	
	}	

	   /**
     * Initialize response properties for the HTTP status code test case and store 
     * them in the command context.
     * 
     * @param context Context where response properties are added to. 
     * @param statusCode Expected HTTP status code. 
     */
	@SuppressWarnings("unchecked")
	void initializeHttpStatusCodeTestProperties(Context context, int statusCode) {
			   	
	   	// create response property info set    	
	   	ResponsePropertyInfoSet properties;
	   	properties = new ResponsePropertyInfoSetImpl();

	   	// create response property info for HTTP status code
	   	String name = messageProvider.getMessage("tcac.statuscode_prop_statuscode");    	
		String xpath = "method/statusCode";
		Matcher<?> intra = everyItem(equalTo(statusCode));
		Matcher<?> inter = everyItem(equalTo(statusCode));
	   	properties.addProperty(name, xpath, intra, inter);    	
	   		
	   	// store properties in context
        context.put( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, properties);
	}	
	
}
