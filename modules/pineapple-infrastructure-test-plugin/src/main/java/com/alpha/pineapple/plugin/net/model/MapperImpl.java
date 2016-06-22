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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.infrastructure.model.AccessUncPathTest;
import com.alpha.pineapple.plugin.infrastructure.model.AssertionValues;
import com.alpha.pineapple.plugin.infrastructure.model.DnsResolutionTest;
import com.alpha.pineapple.plugin.infrastructure.model.FtpServerActiveTest;
import com.alpha.pineapple.plugin.infrastructure.model.FtpServerContainsDirectoryTest;
import com.alpha.pineapple.plugin.infrastructure.model.FtpServerCreateDirectoryTest;
import com.alpha.pineapple.plugin.infrastructure.model.Header;
import com.alpha.pineapple.plugin.infrastructure.model.Headers;
import com.alpha.pineapple.plugin.infrastructure.model.HttpAssertion;
import com.alpha.pineapple.plugin.infrastructure.model.HttpAssertionValue;
import com.alpha.pineapple.plugin.infrastructure.model.HttpAssertions;
import com.alpha.pineapple.plugin.infrastructure.model.HttpConfiguration;
import com.alpha.pineapple.plugin.infrastructure.model.HttpFollowRedirect;
import com.alpha.pineapple.plugin.infrastructure.model.HttpHeaderAssertionValues;
import com.alpha.pineapple.plugin.infrastructure.model.HttpHeaderTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpRedirectAssertionValues;
import com.alpha.pineapple.plugin.infrastructure.model.HttpRedirectTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpStatusCodeTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpTest;
import com.alpha.pineapple.plugin.infrastructure.model.LoadBalancingTest;
import com.alpha.pineapple.plugin.infrastructure.model.NonExistingHeader;
import com.alpha.pineapple.plugin.infrastructure.model.NonExistingHeaders;
import com.alpha.pineapple.plugin.infrastructure.model.ProxyType;
import com.alpha.pineapple.plugin.infrastructure.model.Servers;
import com.alpha.pineapple.plugin.infrastructure.model.SessionStickynessTest;
import com.alpha.pineapple.plugin.infrastructure.model.TcpConfiguration;
import com.alpha.pineapple.plugin.infrastructure.model.TcpConnectionTest;
import com.alpha.pineapple.plugin.infrastructure.model.TcpPort;
import com.alpha.pineapple.plugin.infrastructure.model.Urls;
import com.alpha.pineapple.plugin.net.command.TestDnsForwardResolutionCommand;
import com.alpha.pineapple.plugin.net.command.TestDnsResolutionCommand;
import com.alpha.pineapple.plugin.net.command.TestDnsReverseResolutionCommand;
import com.alpha.pineapple.plugin.net.command.TestFtpServerActiveCommand;
import com.alpha.pineapple.plugin.net.command.TestFtpServerCanCreateDirectoryCommand;
import com.alpha.pineapple.plugin.net.command.TestFtpServerContainsDirectoryCommand;
import com.alpha.pineapple.plugin.net.command.TestHttpCommand;
import com.alpha.pineapple.plugin.net.command.TestTcpConnectionCommand;
import com.alpha.pineapple.plugin.net.command.TestUncPathCommand;
import com.alpha.pineapple.plugin.net.http.HttpConfigurationImpl;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSetImpl;

/**
 * Implementation of the <code>Mapper</code> interface.
 */
public class MapperImpl implements Mapper
{
	/**
	 * Single HTTP request only.
	 */
	static final int SINGLE_REQUEST = 1;	
	
	/**
	 * HTTP client state should be reset after each URL sequence.
	 */
	static boolean RESET_AFTER_EACH_SEQUENCE = true;

	/**
	 * HTTP client state should not be reset after each URL sequence.
	 */	
	static final boolean DONT_RESET_AFTER_EACH_SEQUENCE = false ;	
	
	/**
	 * Name of the Location HTTP header.
	 */
	static final String LOCATION_HEADER = "Location";

    /**
     * Null URLs array
     */
    static final String[] NULL_ULRS_ARRAY = {};

    /**
     * Null server array
     */
    static final String[] NULL_SERVER_ARRAY = {"null" };

    /**
     * Null ports array
     */
    static final int[] NULL_PORTS_ARRAY = {};

    /**
     * Disable HTTP follow redirects. 
     */
	static final boolean NO_REDIRECT = false;
    
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Assertion configurations for test cases.
     */
    @Resource
    TestCaseAssertionConfigurations assertionConfigurations;
    
    
    @SuppressWarnings("unchecked")
    public void mapHttpHeaderTest( HttpHeaderTest test, Context context, HashMap<String, HttpConfiguration> configurationMap )
    {
        // map requests 
        context.put( TestHttpCommand.REQUESTS_KEY, SINGLE_REQUEST );
    	
        // map URL
    	mapUrl(test.getUrl(), context, TestHttpCommand.URLS_KEY );

        // map reset after each sequence
        context.put( TestHttpCommand.RESET_KEY, RESET_AFTER_EACH_SEQUENCE);
        
        // map HTTP configuration
        mapHttpConfiguration(test.getHttpConfigurationRef(), 
        		context, 
        		configurationMap, 
        		TestHttpCommand.HTTPCONFIGURATION_KEY);

        // create header map
        HashMap<String, String> headersMap = new HashMap<String, String>();
        
        // get assertion values
        HttpHeaderAssertionValues assertValues = test.getAssert();
        
        // process headers if assert block is defined 
        if( assertValues != null ) {
        
            // get headers container
            Headers headers = assertValues.getHeaders();

            // process headers if they are defined
            if (headers != null ) {

                // put header into map
                List<Header> assertHeaders = headers.getHeader();            
                for ( Header header : assertHeaders )
                {
                    headersMap.put( header.getName(), header.getValue() );
                }                            
            }            
        }
                 
        // declare expected non existing headers variable
        String[] nonExistingheaders = null;
        
        // process headers if assert block is defined
        if( assertValues != null ) {
            
            // get header container
            NonExistingHeaders assertNonExistingHeaders = assertValues.getNonexistingHeaders();
            
            // process headers if they are defined            
            if (assertNonExistingHeaders != null ) {
                
                // get non existing headers                
                List<NonExistingHeader> assertHeaders = assertNonExistingHeaders.getHeader();

                // create header array
                nonExistingheaders = new String[assertHeaders.size()]; 
                                
                // put header into array
                int i = 0;
                for ( NonExistingHeader header : assertHeaders ) {
                    nonExistingheaders[i] = header.getName();
                    i++;
                }            
                
            } else {

                // create header array
                nonExistingheaders = new String[0];                             
            }
                        
        } else {

            // create header array
            nonExistingheaders = new String[0];             
        }
        
       // initialize and map assertions into the context
       assertionConfigurations.initializeHttpHeaderTestProperties(context, headersMap, nonExistingheaders );                       
    }

          
    @SuppressWarnings("unchecked")
	public void mapHttpRedirectTest(HttpRedirectTest test, Context context, HashMap<String, HttpConfiguration> configurationMap) {
    	    	    	
        // map requests 
        context.put( TestHttpCommand.REQUESTS_KEY, SINGLE_REQUEST );
    	
        // map URL
    	mapUrl(test.getUrl(), context, TestHttpCommand.URLS_KEY );

        // map reset after each sequence
        context.put( TestHttpCommand.RESET_KEY, RESET_AFTER_EACH_SEQUENCE);
        
        // map HTTP configuration
        mapHttpConfiguration(test.getHttpConfigurationRef(), 
        		context, 
        		configurationMap, 
        		TestHttpCommand.HTTPCONFIGURATION_KEY);
        
        // disable follow direct flag
		// get HTTP configuration
        com.alpha.pineapple.plugin.net.http.HttpConfiguration httpConfig;
        httpConfig = (com.alpha.pineapple.plugin.net.http.HttpConfiguration) context.get(TestHttpCommand.HTTPCONFIGURATION_KEY);
        httpConfig.setHttpFollowRedirects(NO_REDIRECT);
            	
    	// get target URL
        String redirecUrl = null;
        HttpRedirectAssertionValues assertValues = test.getAssert();
       
       // process headers if assert block is defined        
       if( assertValues != null ) {

           // set values to be asserted                                                
    	   redirecUrl = assertValues.getUrl();    			        	
       }

       // initialize and map assertions into the context
       assertionConfigurations.initializeHttpRedirectTestProperties(context, redirecUrl);               
	}

    
    @SuppressWarnings("unchecked")	
	public void mapHttpStatusCodeTest(HttpStatusCodeTest test, Context context, HashMap<String, HttpConfiguration> configurationMap) {
		
        // map requests 
        context.put( TestHttpCommand.REQUESTS_KEY, SINGLE_REQUEST );
    	
        // map URL
    	mapUrl(test.getUrl(), context, TestHttpCommand.URLS_KEY );

        // map reset after each sequence
        context.put( TestHttpCommand.RESET_KEY, RESET_AFTER_EACH_SEQUENCE);
        
        // map HTTP configuration
        mapHttpConfiguration(test.getHttpConfigurationRef(), 
        		context, 
        		configurationMap, 
        		TestHttpCommand.HTTPCONFIGURATION_KEY);
                    	
    	// get expected status code
        int statusCode = test.getExpected();

       // initialize and map assertions into the context
       assertionConfigurations.initializeHttpStatusCodeTestProperties(context, statusCode);
	}

	@SuppressWarnings("unchecked")
    public void mapAccessUncPathTest( AccessUncPathTest test, Context context )
    {
        // set arguments
        context.put( TestUncPathCommand.HOSTNAME_KEY, test.getHost());
        context.put( TestUncPathCommand.SHARE_KEY, test.getPath());                
    }
    

    @SuppressWarnings("unchecked")
	public void mapFtpServerActiveTest( FtpServerActiveTest test, Context context )
    {
        // set arguments
        context.put( TestFtpServerActiveCommand.HOSTNAME_KEY, test.getHost());
        context.put( TestFtpServerActiveCommand.PORT_KEY , test.getPort());                        
        context.put( TestFtpServerActiveCommand.USER_KEY, test.getUser());        
        context.put( TestFtpServerActiveCommand.PASSWORD_KEY, test.getPassword());        
    }


    @SuppressWarnings("unchecked")
	public void mapFtpServerContainsDirectoryTest( FtpServerContainsDirectoryTest test, Context context )
    {
        // set arguments
        context.put( TestFtpServerContainsDirectoryCommand.HOSTNAME_KEY, test.getHost());
        context.put( TestFtpServerContainsDirectoryCommand.PORT_KEY , test.getPort());                        
        context.put( TestFtpServerContainsDirectoryCommand.DIRECTORY_KEY, test.getDirectory());        
        context.put( TestFtpServerContainsDirectoryCommand.USER_KEY, test.getUser());        
        context.put( TestFtpServerContainsDirectoryCommand.PASSWORD_KEY, test.getPassword());
    }

    
    @SuppressWarnings("unchecked")
	public void mapFtpServerCreateDirectoryTest( FtpServerCreateDirectoryTest test, Context context )
    {
        // set arguments
        context.put( TestFtpServerCanCreateDirectoryCommand.HOSTNAME_KEY, test.getHost());
        context.put( TestFtpServerCanCreateDirectoryCommand.PORT_KEY , test.getPort());                                
        context.put( TestFtpServerCanCreateDirectoryCommand.USER_KEY, test.getUser());        
        context.put( TestFtpServerCanCreateDirectoryCommand.PASSWORD_KEY, test.getPassword());        
    }
   
	@SuppressWarnings("unchecked")
	public void mapStickynessTest(SessionStickynessTest test, Context context, HashMap<String, HttpConfiguration> configurationMap) {

        // map requests 
        context.put( TestHttpCommand.REQUESTS_KEY, test.getRequests() );

        // map reset after each sequence
        context.put( TestHttpCommand.RESET_KEY, DONT_RESET_AFTER_EACH_SEQUENCE );

        // map URLs
        mapUrls(test.getUrls(), context, TestHttpCommand.URLS_KEY);
        
        // map HTTP configuration
        mapHttpConfiguration(test.getHttpConfigurationRef(), 
        		context, 
        		configurationMap, 
        		TestHttpCommand.HTTPCONFIGURATION_KEY);

        // set values to be asserted
        AssertionValues assertValues = test.getAssert();
        AssertionValuesPropertyGetter propertyGetter = new AssertionValuesPropertyGetter(assertValues);
        String environment = propertyGetter.getProperty( "environment" );
        String host = propertyGetter.getProperty( "host" );
        String location = propertyGetter.getProperty( "location" );
        
        // map servers
        String[] servers = mapServers(assertValues.getServers());

        // initialize and map assertions into the context
        assertionConfigurations.initializeHttpStickynessTestProperties(context, environment, host, location, servers);                               
	}
	
	@SuppressWarnings("unchecked")
	public void mapLoadBalancingTest(LoadBalancingTest test, Context context, HashMap<String, HttpConfiguration> configurationMap ) {

        // map requests 
        context.put( TestHttpCommand.REQUESTS_KEY, test.getRequests() );

        // map reset after each sequence
        context.put( TestHttpCommand.RESET_KEY, RESET_AFTER_EACH_SEQUENCE );

        // map URLs
        mapUrls(test.getUrls(), context, TestHttpCommand.URLS_KEY);
        
        // map HTTP configuration
        mapHttpConfiguration(test.getHttpConfigurationRef(), 
        		context, 
        		configurationMap, 
        		TestHttpCommand.HTTPCONFIGURATION_KEY);

        // set values to be asserted
        AssertionValues assertValues = test.getAssert();
        AssertionValuesPropertyGetter propertyGetter = new AssertionValuesPropertyGetter(assertValues);
        String environment = propertyGetter.getProperty( "environment" );
        String host = propertyGetter.getProperty( "host" );
        String location = propertyGetter.getProperty( "location" );
        
        // map servers
        String[] servers = mapServers(assertValues.getServers());

        // initialize and map assertions into the context
        assertionConfigurations.initializeHttpLoadBalancingTestProperties(context, environment, host, location, servers);                               
	}

	@SuppressWarnings("unchecked")
	public void mapHttpTest(HttpTest test, Context context, HashMap<String, HttpConfiguration> configurationMap) {
		
        // map requests 
        context.put( TestHttpCommand.REQUESTS_KEY, test.getRequests() );

        // map reset after each sequence
        context.put( TestHttpCommand.RESET_KEY, test.isReset());
                
        // map URLs
        mapUrls(test.getUrls(), context, TestHttpCommand.URLS_KEY);
        
        // map HTTP configuration
        mapHttpConfiguration(test.getHttpConfigurationRef(), 
        		context, 
        		configurationMap, 
        		TestHttpCommand.HTTPCONFIGURATION_KEY);

    	// create response property info set    	
    	ResponsePropertyInfoSet properties;
    	properties = new ResponsePropertyInfoSetImpl();
        
        // get container
        HttpAssertions assertionContainer = test.getAssertions();
        
        // handle null case
        if (assertionContainer == null) {
        	context.put( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, properties );
        	return;
        }
        
        // get assertion list
        List<HttpAssertion> assertionList = assertionContainer.getAssertion();
        
        // handle empty assertion list case
        if( assertionList == null || assertionList.size() == 0) {
        	context.put( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, properties );
        	return;        	
        }

        // iterate over the assertions
        for (HttpAssertion assertion : assertionList) {        	
        	mapAssertion( assertion, properties );        	
        }
        
        // add properties to context
    	context.put( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY, properties );
	}

	/**
	 * Map an assertion to an response property. 
	 *   
	 * @param assertion Model assertion which is mapped  into an response property. 
	 * @param properties Response properties container to which the property is added.
	 */
	void mapAssertion(HttpAssertion assertion, ResponsePropertyInfoSet properties) {

			// get expected values
			List<HttpAssertionValue> assertionValueContainer = assertion.getValue();
			
			// map values into string array
			String[] valuesAsArray = new String[assertionValueContainer.size()];
			int i = 0;
			for( HttpAssertionValue assertionValue : assertionValueContainer) {
				valuesAsArray[i] = assertionValue.getV();
				i++;
			}
									
			// map matchers 						
			Matcher<?> intra = mapMatcher(assertion.getIntraStrategy(), valuesAsArray );
			Matcher<?> inter = mapMatcher(assertion.getInterStrategy(), valuesAsArray );			
			
			// add property
			properties.addProperty(assertion.getName(), assertion.getXpath(), intra, inter);		
	}

	/**
	 * Map matcher name to initialized matcher object.
	 *  
	 * @param matcherName Name of the matcher.
	 * 
	 * @param values Array of constructor values.
	 * 
	 * @return initialized matcher object with the constructor initialized with 
	 * the array of constructor values.
	 */
	Matcher<?> mapMatcher(String matcherName, String[] values) {
				
		if (matcherName.equalsIgnoreCase("equalto")) {
			
			// handle case with single value 
			if (isSingleValueArray(values)) {

				// get value as string
				String valueAsString = values[0];
				
				// convert to Integer if value is a number
				if (StringUtils.isNumeric(valueAsString)) {

					// get value as numeric					
					Object numbericValue = ConvertUtils.convert(valueAsString, Integer.class);
					
					// create matcher
					Matcher<?> equalToMatcher = everyItem(equalTo(numbericValue));
					return equalToMatcher;															
				} 
								
				// create matcher with string value
				Matcher<?> equalToMatcher = everyItem(equalTo(valueAsString));
				
				return equalToMatcher;
			}
			
		}		
		
		// wrap matcher with everyItem		
		// except matchingSingleValueInRange + distributedAcrossRange
		
		return null;
	}


	private boolean isSingleValueArray(String[] values) {		
		return (values.length == 1); 
	}


	@SuppressWarnings("unchecked")
	public void mapTcpConnectionTest(TcpConnectionTest test, Context context) {
				
        // set host
        context.put( TestTcpConnectionCommand.HOST_KEY, test.getHost() );
        
        // set ports
        if ( test.getPort() == null )
        {
            // set null array
            context.put( TestTcpConnectionCommand.PORTS_KEY, NULL_PORTS_ARRAY );
        }
        else
        {
            // set populated array
        	
            // get ports
        	List<TcpPort> ports = test.getPort();

            // create port array
            int[] portsArray = new int[ports.size()]; 
                            
            // put ports into array
            int i = 0;
            for ( TcpPort port : ports ) {
                portsArray[i] = port.getValue();
                i++;
            }            
        	        	            
            context.put( TestTcpConnectionCommand.PORTS_KEY, portsArray );
        }
		
	}
   
    @SuppressWarnings("unchecked")
	public void mapDnsResolutionTest(DnsResolutionTest test, Context context) {
        context.put( TestDnsResolutionCommand.HOSTNAME_KEY, test.getHost() );
        context.put( TestDnsResolutionCommand.IP_KEY, test.getIp() );
	}

    @SuppressWarnings("unchecked")    
	public void mapDnsForwardResolutionTest(DnsResolutionTest test, Context context) {
        context.put( TestDnsForwardResolutionCommand.HOSTNAME_KEY, test.getHost() );
        context.put( TestDnsForwardResolutionCommand.IP_KEY, test.getIp() );
		
	}

    @SuppressWarnings("unchecked")
	public void mapDnsReverseResolutionTest(DnsResolutionTest test, Context context) {
        context.put( TestDnsReverseResolutionCommand.HOSTNAME_KEY, test.getHost() );
        context.put( TestDnsReverseResolutionCommand.IP_KEY, test.getIp() );
	}
	
    /**
     * Map source HTTP configuration form model to target HTTP Configuration for command objects
     * and store the target HTTP configuration object in the command context. 
     *  
     * @param configId Id of Model configuration object.
     * @param context Context where HTTP configuration is added to. 
     * @param configurationMap Map of source HTTP configuration objects.
     * @param configKey Key in the context where the target HTTP configuration object is stored under. 
     */
	@SuppressWarnings("unchecked")
	void mapHttpConfiguration( String configId, Context context, HashMap<String,HttpConfiguration> configurationMap, String configKey ) {

    	// create HTTP configuration for commands
    	HttpConfigurationImpl httpConfigForCmd = new HttpConfigurationImpl();

		// if config is undefined then store configuration with default values 
		if (configId == null ) {
			context.put( configKey, httpConfigForCmd );
			return;
		}
		        	        	
    	// get configuration
    	HttpConfiguration config = configurationMap.get(configId);
    	
    	// map proxy values
    	if ( config.getProxy() != null ) {

        	// get proxy
        	ProxyType proxy = config.getProxy();
    		
        	httpConfigForCmd.setProxyHost(proxy.getHost());
        	httpConfigForCmd.setProxyPort(proxy.getPort());
    	}    	
    	    	
    	// map TCP values
    	if( config.getTcp() != null ) {
    		
    		// get TCP configuration
    		TcpConfiguration tcpConfig = config.getTcp();
    		
    		// map value 
    		httpConfigForCmd.setTcpSocketTimeout( tcpConfig.getSocketTimeout());
    	}
    	    	    	
    	// map HTTP follow-redirects
    	if( config.getFollowRedirects() != null ) {
    	
    		// get follow redirects
    		HttpFollowRedirect followConfig = config.getFollowRedirects();
    		
    		// map values     		
    		httpConfigForCmd.setHttpFollowRedirects(followConfig.isValue() );
    	}
    	
    	// add config to context
		context.put( configKey, httpConfigForCmd );    	
	}

	/**
	 * Map URLs string array and store it in the context. If no URLs are defined 
	 * in the model then an empty array is stored.
	 * 
	 * @param urlsContainer Model URLs container object where the URL's are read from.
     * @param context Context where the string array is added to  
     * @param configKey Key in the context where the URLS are stored.  
	 */
	@SuppressWarnings("unchecked")
	void mapUrls( Urls urlsContainer, Context context, String configKey ) {

        // handle null case
        if (urlsContainer == null || urlsContainer.getUrl() == null ) {
        	context.put( configKey, NULL_ULRS_ARRAY );
        	return;
        }
        	
    	// get URL's
    	List<String> urls = urlsContainer.getUrl();
    	
    	// map
        String[] urlsArray = urls.toArray( new String[urls.size()] );
        context.put( configKey, urlsArray );        	 
	}

	/**
	 * Map single URL to string array and store it in the context.
	 * 
	 * @param url The URL which is mapped into the string array.
	 * @param context The context where the string array is stored. 
     * @param configKey Key in the context where the URLS are stored. 
	 */
	@SuppressWarnings("unchecked")
	void mapUrl(String url, Context context, String configKey ) {
		// map URLs
    	String[] urls = new String[] { url };
    	context.put( configKey, urls);
	}

	/** 
	 * Map servers to a string array. If no servers are defined 
	 * in the model then an empty array is returned.
	 * 
	 * @param servers Source model servers element.
	 *  
	 * @return string array array containing the mapped servers. 
	 */
	String[] mapServers(Servers servers) {
		
        // set server range
        if ( servers == null || servers.getServer() == null )
        {
        	return NULL_SERVER_ARRAY;
        }
        else
        {
            // populate array
            List<String> serverList = servers.getServer();
            String[] serversArray = serverList.toArray( new String[serverList.size()] );
            return serversArray;
        }		
	}
	
	
}
