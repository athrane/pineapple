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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.infrastructure.model.AccessUncPathTest;
import com.alpha.pineapple.plugin.infrastructure.model.DnsResolutionTest;
import com.alpha.pineapple.plugin.infrastructure.model.Header;
import com.alpha.pineapple.plugin.infrastructure.model.HttpAssertion;
import com.alpha.pineapple.plugin.infrastructure.model.HttpAssertions;
import com.alpha.pineapple.plugin.infrastructure.model.HttpConfiguration;
import com.alpha.pineapple.plugin.infrastructure.model.HttpHeaderTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpRedirectTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpTest;
import com.alpha.pineapple.plugin.infrastructure.model.NonExistingHeader;
import com.alpha.pineapple.plugin.infrastructure.model.ObjectFactory;
import com.alpha.pineapple.plugin.infrastructure.model.ProxyType;
import com.alpha.pineapple.plugin.infrastructure.model.TcpConfiguration;
import com.alpha.pineapple.plugin.infrastructure.model.TcpConnectionTest;
import com.alpha.pineapple.plugin.infrastructure.model.TcpPort;
import com.alpha.pineapple.plugin.infrastructure.model.Urls;
import com.alpha.pineapple.plugin.net.command.TestDnsForwardResolutionCommand;
import com.alpha.pineapple.plugin.net.command.TestDnsResolutionCommand;
import com.alpha.pineapple.plugin.net.command.TestDnsReverseResolutionCommand;
import com.alpha.pineapple.plugin.net.command.TestHttpCommand;
import com.alpha.pineapple.plugin.net.command.TestTcpConnectionCommand;
import com.alpha.pineapple.plugin.net.command.TestUncPathCommand;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfo;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSetImpl;

/**
 * Unit test of the class {@link MapperImpl}.
 */
public class MapperTest
{
	
	/**
	 * Some context key
	 */
	static final String CONFIG_KEY = "some-key";
    
	/**
	 * First array index.
	 */
    static final int FIRST_INDEX = 0;

	/**
     * Object under test.
     */
    MapperImpl mapper;
    
    /**
     * Test case factory
     */
    ObjectFactory pluginFactory;

    /*
     * Command context. 
     */
    Context context;

    /**
     * HTTP configuration map
     */
    HashMap<String, HttpConfiguration> httpConfigMap;    
    
    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;
    
    /**
     * Assertion configurations for test cases.
     */   
    TestCaseAssertionConfigurations assertionConfigurations;
            
    @Before
    public void setUp() throws Exception
    {
        // create mapper
        mapper = new MapperImpl();
        
        // create test case factory
        pluginFactory = new com.alpha.pineapple.plugin.infrastructure.model.ObjectFactory();
        
        // create context
        context = new ContextBase();
        
        // create HTTP configuration map
        httpConfigMap = new HashMap<String, HttpConfiguration>();
        
        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
                
        // inject message provider
        ReflectionTestUtils.setField( mapper, "messageProvider", messageProvider, MessageProvider.class );
        
        // complete mock source initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl();         
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class )));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class ),
        		(Object[]) EasyMock.isA( Object[].class)));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        EasyMock.replay(messageProvider);                                
        
        // create assertion configuration helper
        assertionConfigurations = new TestCaseAssertionConfigurations();
                
        // inject assertion configuration helper
        ReflectionTestUtils.setField( mapper, "assertionConfigurations", assertionConfigurations , TestCaseAssertionConfigurations.class );

        // inject message provider
        ReflectionTestUtils.setField( assertionConfigurations, "messageProvider", messageProvider, MessageProvider.class );
        
    }

    @After
    public void tearDown() throws Exception
    {
        mapper = null;
        pluginFactory = null;
        context = null;
        httpConfigMap = null;
        messageProvider = null;
    }

    /**
     * Assert default HTTP configuration 
     * 
     * @param context Context where the configuration is stored.
     * @param key Context key under which the configuration is stored.
     */
	void assertDefaultHttpConfiguration(Context context, String key) {

		// get HTTP configuration
        com.alpha.pineapple.plugin.net.http.HttpConfiguration httpConfig;
        httpConfig = (com.alpha.pineapple.plugin.net.http.HttpConfiguration) context.get(key);
		
        // test
		assertNotNull(httpConfig);        
        assertEquals(true, httpConfig.getHttpFollowRedirects());
        assertEquals("", httpConfig.getProxyHost());
        assertEquals(0, httpConfig.getProxyPort());
        assertEquals(3000, httpConfig.getTcpSocketTimeout());
	}

    /**
     * Assert default HTTP configuration for redirect tests. 
     * 
     * @param context Context where the configuration is stored.
     * @param key Context key under which the configuration is stored.
     */
	void assertDefaultRedirectHttpConfiguration(Context context, String key) {

		// get HTTP configuration
        com.alpha.pineapple.plugin.net.http.HttpConfiguration httpConfig;
        httpConfig = (com.alpha.pineapple.plugin.net.http.HttpConfiguration) context.get(key);
		
        // test
		assertNotNull(httpConfig);        
        assertEquals(false, httpConfig.getHttpFollowRedirects());
        assertEquals("", httpConfig.getProxyHost());
        assertEquals(0, httpConfig.getProxyPort());
        assertEquals(3000, httpConfig.getTcpSocketTimeout());
	}

	
    /**
     * Test that HTTP configuration default values can be mapped.
     */
    @Test
    public void testCanMapHttpConfigurationDefaultConfiguration()
    {
        // define values
        String httpConfigId = "my-config";

        // create HTTP configuration 
        HttpConfiguration config = pluginFactory.createHttpConfiguration();
        
		// store HTTP configuration
        httpConfigMap.put(httpConfigId, config);
                        		
		// map values
        mapper.mapHttpConfiguration(httpConfigId, context, httpConfigMap, CONFIG_KEY);        
        
        // get mapped config
        com.alpha.pineapple.plugin.net.http.HttpConfiguration actualConfig = 
        	(com.alpha.pineapple.plugin.net.http.HttpConfiguration) context.get( CONFIG_KEY );

        // test
        assertEquals("", actualConfig.getProxyHost() );
        assertEquals(0, actualConfig.getProxyPort() );               
        assertEquals(true, actualConfig.getHttpFollowRedirects() );
        assertEquals(3000, actualConfig.getTcpSocketTimeout() );
    }
	
	
    /**
     * Test that HTTP configuration proxy can be mapped.
     */
    @Test
    public void testCanMapHttpConfigurationWithProxy()
    {
        // define values
        String httpConfigId = "my-config";
        String proxyHost = "littelbigproxy";
        int proxyPort = 8080;

        // create HTTP configuration 
        HttpConfiguration config = pluginFactory.createHttpConfiguration();
        config.setProxy(pluginFactory.createProxyType());
        ProxyType proxy = config.getProxy();
        proxy.setHost(proxyHost);
        proxy.setPort(proxyPort);
        
		// store HTTP configuration
        httpConfigMap.put(httpConfigId, config);
                        
        // map values
        mapper.mapHttpConfiguration(httpConfigId, context, httpConfigMap, CONFIG_KEY );        
                
        // get mapped config
        com.alpha.pineapple.plugin.net.http.HttpConfiguration actualConfig = 
        	(com.alpha.pineapple.plugin.net.http.HttpConfiguration) context.get( CONFIG_KEY );

        // test
        assertEquals(proxyHost, actualConfig.getProxyHost() );
        assertEquals(proxyPort, actualConfig.getProxyPort() );               
    }
    
    /**
     * Test that HTTP configuration time out can be mapped.
     */
    @Test
    public void testCanMapHttpConfigurationWithSocketTimeout()
    {
        // define values
        String httpConfigId = "my-config";    	
        int timeout = 3598;

        // create test case
        HttpHeaderTest testcase = pluginFactory.createHttpHeaderTest();   
        testcase.setHttpConfigurationRef(httpConfigId);

        // create HTTP configuration 
        HttpConfiguration config = pluginFactory.createHttpConfiguration();
        config.setTcp(pluginFactory.createTcpConfiguration());
        TcpConfiguration tcp = config.getTcp();
        tcp.setSocketTimeout(timeout);
        
		// store HTTP configuration
        httpConfigMap.put(httpConfigId, config);
                        
        // map values
        mapper.mapHttpConfiguration(httpConfigId, context, httpConfigMap, CONFIG_KEY );        
                
        // get mapped config
        com.alpha.pineapple.plugin.net.http.HttpConfiguration actualConfig = 
        	(com.alpha.pineapple.plugin.net.http.HttpConfiguration) context.get( CONFIG_KEY );

        // test
        assertEquals(timeout, actualConfig.getTcpSocketTimeout() );
               
    }

    
    /**
     * Test that HTTP configuration follow redirecst out can be mapped.
     */
    @Test
    public void testCanMapHttpConfigurationWithFollowRedirects()
    {
        // define values
        String httpConfigId = "my-config";    	
        boolean dontFollow = false;

        // create test case
        HttpHeaderTest testcase = pluginFactory.createHttpHeaderTest();   
        testcase.setHttpConfigurationRef(httpConfigId);

        // create HTTP configuration 
        HttpConfiguration config = pluginFactory.createHttpConfiguration();
        config.setFollowRedirects(pluginFactory.createHttpFollowRedirect());
        config.getFollowRedirects().setValue(dontFollow);
        
		// store HTTP configuration
        httpConfigMap.put(httpConfigId, config);
                        
        // map values
        mapper.mapHttpConfiguration(httpConfigId, context, httpConfigMap, CONFIG_KEY );        
                
        // get mapped config
        com.alpha.pineapple.plugin.net.http.HttpConfiguration actualConfig = 
        	(com.alpha.pineapple.plugin.net.http.HttpConfiguration) context.get( CONFIG_KEY );

        // test
        assertEquals(dontFollow, actualConfig.getHttpFollowRedirects() );
               
    }

    /**
     * Test that empty URLs can be mapped.
     */
    @Test
    public void testCanMapEmptyUrls()
    {
    	// initialize parameters
    	Urls urlsContainer = pluginFactory.createUrls();;
		String configKey = "my-key";
    	
        // map values
		mapper.mapUrls(urlsContainer, context, configKey);        
        
        // test
        assertNotNull( context.get( configKey ) );        
        Object urlsArray = context.get( configKey );                
        assertTrue( urlsArray instanceof String[] ); 
        assertEquals( 0, ((String[]) urlsArray).length );                       
        
    }
    
    /**
     * Test that single URL can be mapped.
     */
    @Test
    public void testCanMapUrlsWithSingleUrl()
    {
    	String url1 = "http://www.hosta.com";
    	
    	// initialize parameters
        Urls urlsContainer= pluginFactory.createUrls();
        List<String> urlsList = urlsContainer.getUrl();        
		urlsList.add(url1);
		String configKey = "my-key";
    	
        // map values
		mapper.mapUrls(urlsContainer, context, configKey);        
        
        // test array
        assertNotNull( context.get( configKey ) );        
        Object urlsArray = context.get( configKey );        
        assertTrue( urlsArray instanceof String[] );
        String[] urlsArray2 = (String[]) urlsArray; 
        assertEquals( 1, urlsArray2 .length );
        
        // test URL
        assertEquals( url1, urlsArray2[FIRST_INDEX]);
    }

    /**
     * Test that multiple URLs can be mapped.
     */
    @Test
    public void testCanMapUrlsWithMultipleUrls()
    {
    	String url1 = "http://www.hosta.com";
    	String url2 = "http://www.hostb.com";    	
    	
    	// initialize parameters
        Urls urlsContainer= pluginFactory.createUrls();
        List<String> urlsList = urlsContainer.getUrl();        
		urlsList.add(url1);
		urlsList.add(url2);		
		String configKey = "my-key";
    	
        // map values
		mapper.mapUrls(urlsContainer, context, configKey);        
        
        // test array
        assertNotNull( context.get( configKey ) );        
        Object urlsArray = context.get( configKey );        
        assertTrue( urlsArray instanceof String[] );
        String[] urlsArray2 = (String[]) urlsArray; 
        assertEquals( 2, urlsArray2 .length );
        
        // test URLs
        assertEquals( url1, urlsArray2[FIRST_INDEX]);
        assertEquals( url2, urlsArray2[FIRST_INDEX+1]);
        
    }
    
    
    /**
     * Test that empty test case can be mapped.
     */
    @Test
    public void testCanMapEmptyHttpTest()
    {        
        // create test case
        HttpTest testcase = pluginFactory.createHttpTest();                
        
        // map values
        mapper.mapHttpTest( testcase, context, httpConfigMap );        
        
        // test
        assertEquals( 0, context.get( TestHttpCommand.REQUESTS_KEY) );
        assertEquals( false, context.get( TestHttpCommand.RESET_KEY ) );
        
        Object urlsArray = context.get( TestHttpCommand.URLS_KEY );                
        assertTrue( urlsArray instanceof String[] ); 
        assertEquals( 0, ((String[]) urlsArray).length );                       
        assertDefaultHttpConfiguration(context, TestHttpCommand.HTTPCONFIGURATION_KEY);
        
        Object properties = context.get( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY);
        assertTrue( properties instanceof ResponsePropertyInfoSet );
        assertNotNull( ((ResponsePropertyInfoSet ) properties).getProperties());
        assertEquals(0, ((ResponsePropertyInfoSet ) properties).getProperties().length);
    }

    /**
     * Test that single request can be mapped.
     */
    @Test
    public void testCanMapHttpTestWithSingleRequest()
    {        
        // create test case
        HttpTest testcase = pluginFactory.createHttpTest();
        
		// set request
        int requests = 1;
        testcase.setRequests(requests);
        
        // map values
        mapper.mapHttpTest( testcase, context, httpConfigMap );        
        
        // test
        assertEquals( requests, context.get( TestHttpCommand.REQUESTS_KEY) );
    }    

    /**
     * Test that multiple requests can be mapped.
     */
    @Test
    public void testCanMapHttpTestWithMultipleRequests()
    {        
        // create test case
        HttpTest testcase = pluginFactory.createHttpTest();
        
		// set request
        int requests = 999;
        testcase.setRequests(requests);
        
        // map values
        mapper.mapHttpTest( testcase, context, httpConfigMap );        
        
        // test
        assertEquals( requests, context.get( TestHttpCommand.REQUESTS_KEY) );
    }    
    
    /**
     * Test that no reset can be mapped.
     */
    @Test
    public void testCanMapHttpTestwithNoReset()
    {        
        // create test case
        HttpTest testcase = pluginFactory.createHttpTest();
                
		// set reset
        boolean reset = false;
        testcase.setReset(reset);
        
        // map values
        mapper.mapHttpTest( testcase, context, httpConfigMap );        
        
        // test
        assertEquals( reset, context.get( TestHttpCommand.RESET_KEY ) );        
    }

    /**
     * Test that reset can be mapped.
     */
    @Test
    public void testCanMapHttpTestwithReset()
    {        
        // create test case
        HttpTest testcase = pluginFactory.createHttpTest();
                
		// set reset
        boolean reset = true;
        testcase.setReset(reset);
        
        // map values
        mapper.mapHttpTest( testcase, context, httpConfigMap );        
        
        // test
        assertEquals( reset, context.get( TestHttpCommand.RESET_KEY ) );        
    }
    
    /**
     * Test that test case with single URL can be mapped.
     */
    @Test
    public void testCanMapHttpTestWithSingleUrl()
    {        
        // create test case
        HttpTest testcase = pluginFactory.createHttpTest();                
        
		// set URLs
        Urls urls = pluginFactory.createUrls();
        List<String> urlsList = urls.getUrl();
        urlsList.add("http://www.hosta.com");
        testcase.setUrls(urls);
        
        // map values
        mapper.mapHttpTest( testcase, context, httpConfigMap );        
        
        // test        
        Object urlsArray = context.get( TestHttpCommand.URLS_KEY );                
        assertTrue( urlsArray instanceof String[] ); 
        assertEquals( 1, ((String[]) urlsArray).length );                       
        assertDefaultHttpConfiguration(context, TestHttpCommand.HTTPCONFIGURATION_KEY);
    }
    
    /**
     * Test that test case with two URLs can be mapped.
     */
    @Test
    public void testCanMapHttpTestWithMultipleUrls()
    {        
        // create test case
        HttpTest testcase = pluginFactory.createHttpTest();                
        
		// set URLs
        Urls urls = pluginFactory.createUrls();
        List<String> urlsList = urls.getUrl();
        urlsList.add("http://www.hosta.com");
        urlsList.add("http://www.hostb.com");        
        testcase.setUrls(urls);
        
        // map values
        mapper.mapHttpTest( testcase, context, httpConfigMap );        
        
        // test        
        Object urlsArray = context.get( TestHttpCommand.URLS_KEY );                
        assertTrue( urlsArray instanceof String[] ); 
        assertEquals( 2, ((String[]) urlsArray).length );                       
        assertDefaultHttpConfiguration(context, TestHttpCommand.HTTPCONFIGURATION_KEY);
    }

    /**
     * Test that test case with two identical URLs can be mapped.
     */
    @Test
    public void testCanMapHttpTestWithMultipleIdenticalUrls()
    {        
        // create test case
        HttpTest testcase = pluginFactory.createHttpTest();                
        
		// set URLs
        Urls urls = pluginFactory.createUrls();
        List<String> urlsList = urls.getUrl();
        urlsList.add("http://www.hosta.com");
        urlsList.add("http://www.hosta.com");        
        urlsList.add("http://www.hosta.com");        
        testcase.setUrls(urls);
        
        // map values
        mapper.mapHttpTest( testcase, context, httpConfigMap );        
        
        // test        
        Object urlsArray = context.get( TestHttpCommand.URLS_KEY );                
        assertTrue( urlsArray instanceof String[] ); 
        assertEquals( 3, ((String[]) urlsArray).length );                       
        assertDefaultHttpConfiguration(context, TestHttpCommand.HTTPCONFIGURATION_KEY);
    }

    /**
     * Test that single assertion can be mapped.
     */
    @Test
    public void testCanMapHttpTestWithSingleAssertion()
    {   
        String name = "name";
        String xpath = "xpath";
        String interStrategy = "nop";
        String intraStrategy = "nop";        
    	    	
        // create test case
        HttpTest testcase = pluginFactory.createHttpTest();                
        
        // add assertion                 
        HttpAssertions assertionsContrainer = pluginFactory.createHttpAssertions();
        testcase.setAssertions(assertionsContrainer);        
        List<HttpAssertion> assertionList = assertionsContrainer.getAssertion();        
        HttpAssertion assertion = pluginFactory.createHttpAssertion();        
		assertion.setName(name);
		assertion.setXpath(xpath);
		assertion.setInterStrategy(interStrategy);
		assertion.setIntraStrategy(intraStrategy);
		assertionList.add(assertion);
        
        // map values
        mapper.mapHttpTest( testcase, context, httpConfigMap );        
        
        // test        
        Object properties = context.get( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY);
        assertTrue( properties instanceof ResponsePropertyInfoSet );
        ResponsePropertyInfo[] responseProperties = ((ResponsePropertyInfoSet ) properties).getProperties(); 
        assertNotNull( responseProperties );
        assertEquals(1, responseProperties.length);
        ResponsePropertyInfo property = responseProperties[FIRST_INDEX];
        assertEquals(name, property.getName());
        assertEquals(xpath, property.getXPath());                        
    }

    /**
     * Test that single assertion can be mapped.
     */
    @Test
    public void testCanMapSingleAssertion()
    {   
        String name = "name";
        String xpath = "xpath";
        String interStrategy = "nop";
        String intraStrategy = "nop";        
    	    	       
        // create properties
    	ResponsePropertyInfoSet properties;
    	properties = new ResponsePropertyInfoSetImpl();
        
        // create assertion                 
        HttpAssertion assertion = pluginFactory.createHttpAssertion();        
		assertion.setName(name);
		assertion.setXpath(xpath);
		assertion.setInterStrategy(interStrategy);
		assertion.setIntraStrategy(intraStrategy);
        
        // map values
        mapper.mapAssertion(assertion, properties);        
        
        // test 
        assertEquals(1, properties.getProperties().length);       
        
        // get mapped property
        ResponsePropertyInfo property = properties.getProperties()[FIRST_INDEX];

        // test
        assertEquals(name, property.getName());
        assertEquals(xpath, property.getXPath());        
    }
    
    /**
     * Test that equal-to matcher can be mapped.
     */
    @Test
    public void testCanMapMatcherEqualTo()
    {   
        
        String[] valuesAsArray = new String[] {"200" };
		String MatcherName = "equalTo";
		
		// map values
        Matcher<?> matcher = mapper.mapMatcher(MatcherName, valuesAsArray);        
        
        // test 
        assertNotNull(matcher);               
    }
    
    /**
     * Test that empty test case can be mapped.
     */
    @Test
    public void testCanMapEmptyHttpHeaderTest()
    {        
        // create test case
        HttpHeaderTest testcase = pluginFactory.createHttpHeaderTest();                
        
        // map values
        mapper.mapHttpHeaderTest( testcase, context, httpConfigMap );        
        
        // test        
        assertNotNull( context.get( TestHttpCommand.URLS_KEY ) );
        assertTrue( context.get( TestHttpCommand.URLS_KEY ) instanceof String[]);
        String[] urls = (String[]) context.get( TestHttpCommand.URLS_KEY );
        assertEquals(1, urls.length );
        assertDefaultHttpConfiguration(context, TestHttpCommand.HTTPCONFIGURATION_KEY);
        ResponsePropertyInfoSet propertiesSet = (ResponsePropertyInfoSet) context.get( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY);
        assertNotNull(propertiesSet);
        ResponsePropertyInfo[] props = propertiesSet.getProperties();
        assertEquals(3,props.length);
    }

    /**
     * Test that test case can be mapped.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCanMapHttpHeaderTestWithSingleHeader()
    {
        // define values
        String description = "description";
        String url = "http://nicehost";
        String header1 = "header-1";
        String headerValue1 = "header-value-1";        
                
        // create test case
        HttpHeaderTest testcase = pluginFactory.createHttpHeaderTest();
        testcase.setDescription( description );
        testcase.setUrl( url );                
        testcase.setAssert( pluginFactory.createHttpHeaderAssertionValues() );
        testcase.getAssert().setHeaders( pluginFactory.createHeaders() );
        List<Header> headers = testcase.getAssert().getHeaders().getHeader();
        Header header = pluginFactory.createHeader();
        header.setName( header1 );
        header.setValue( headerValue1 );
        headers.add( header );
        
        // map values
        mapper.mapHttpHeaderTest( testcase, context, httpConfigMap );        
                
        // test        
        assertNotNull( context.get( TestHttpCommand.URLS_KEY ) );
        assertTrue( context.get( TestHttpCommand.URLS_KEY ) instanceof String[]);
        String[] urls = (String[]) context.get( TestHttpCommand.URLS_KEY );
        assertEquals(1, urls.length );
        assertDefaultHttpConfiguration(context, TestHttpCommand.HTTPCONFIGURATION_KEY);
        ResponsePropertyInfoSet propertiesSet = (ResponsePropertyInfoSet) context.get( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY);
        assertNotNull(propertiesSet);
        ResponsePropertyInfo[] props = propertiesSet.getProperties();
        assertEquals(3,props.length);        
    }
    
    

    /**
     * Test that test case can be mapped.
     */
    @Test
    public void testCanMapHttpHeaderTestWithSingleNonExistingHeader()
    {
        // define values
        String description = "description";
        String url = "http://nicehost";
        String header1 = "header-1";
        String headerValue1 = "header-value-1";                
        
        // create test case
        HttpHeaderTest testcase = pluginFactory.createHttpHeaderTest();
        testcase.setDescription( description );
        testcase.setUrl( url );               
        testcase.setAssert( pluginFactory.createHttpHeaderAssertionValues() );
        testcase.getAssert().setNonexistingHeaders( pluginFactory.createNonExistingHeaders() );
        List<NonExistingHeader> headers = testcase.getAssert().getNonexistingHeaders().getHeader();
        NonExistingHeader header = pluginFactory.createNonExistingHeader();
        header.setName( header1 );
        headers.add( header );
        
        // map values
        mapper.mapHttpHeaderTest( testcase, context, httpConfigMap );        
                
        // test        
        assertNotNull( context.get( TestHttpCommand.URLS_KEY ) );
        assertTrue( context.get( TestHttpCommand.URLS_KEY ) instanceof String[]);
        String[] urls = (String[]) context.get( TestHttpCommand.URLS_KEY );
        assertEquals(1, urls.length );
        assertDefaultHttpConfiguration(context, TestHttpCommand.HTTPCONFIGURATION_KEY);
        ResponsePropertyInfoSet propertiesSet = (ResponsePropertyInfoSet) context.get( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY);
        assertNotNull(propertiesSet);
        ResponsePropertyInfo[] props = propertiesSet.getProperties();
        assertEquals(3,props.length);        
    }
    
    
    
    /**
     * Test that test case with a assert block but
     * with no headers be mapped.
     */
    @Test
    public void testCanMapHttpHeaderTestWithNoHeaders()
    {
        // define values
        String description = "description";
        String url = "http://nicehost";
                
        // create test case
        HttpHeaderTest testcase = pluginFactory.createHttpHeaderTest();
        testcase.setDescription( description );
        testcase.setUrl( url );               
        testcase.setAssert( pluginFactory.createHttpHeaderAssertionValues() );
        
        // map values
        mapper.mapHttpHeaderTest( testcase, context, httpConfigMap );        
                
        // test        
        assertNotNull( context.get( TestHttpCommand.URLS_KEY ) );
        assertTrue( context.get( TestHttpCommand.URLS_KEY ) instanceof String[]);
        String[] urls = (String[]) context.get( TestHttpCommand.URLS_KEY );
        assertEquals(1, urls.length );
        assertDefaultHttpConfiguration(context, TestHttpCommand.HTTPCONFIGURATION_KEY);
        ResponsePropertyInfoSet propertiesSet = (ResponsePropertyInfoSet) context.get( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY);
        assertNotNull(propertiesSet);
        ResponsePropertyInfo[] props = propertiesSet.getProperties();
        assertEquals(3,props.length);        
    }    

    /**
     * Test that empty test case can be mapped.
     */
    @Test
    public void testCanMapEmptyAccessUncPathTest()
    {
        // create empty map
        HashMap<String, String> headersMap = new HashMap<String, String>();
        
        // create test case
        AccessUncPathTest testcase = pluginFactory.createAccessUncPathTest();                
        
        // map values
        mapper.mapAccessUncPathTest( testcase, context );        
        
        // test        
        assertEquals( null, context.get( TestUncPathCommand.HOSTNAME_KEY ) );
        assertEquals( null, context.get( TestUncPathCommand.SHARE_KEY ) );                
    }    

    /**
     * Test that test case can be mapped.
     */
    @Test
    public void testCanMapAccessUncPathTest()
    {
        String description = "description";
        String host = "host";        
        String path = "/path";        
        
        // create empty map
        HashMap<String, String> headersMap = new HashMap<String, String>();
        
        // create test case
        AccessUncPathTest testcase = pluginFactory.createAccessUncPathTest();
        testcase.setDescription( description );
        testcase.setHost( host );        
        testcase.setPath( path );
        
        // map values
        mapper.mapAccessUncPathTest( testcase, context );        
        
        // test        
        assertEquals( host, context.get( TestUncPathCommand.HOSTNAME_KEY ) );
        assertEquals( path, context.get( TestUncPathCommand.SHARE_KEY ) );                
    }    
    
    /**
     * Test that empty test case can be mapped.
     */
    @Test
    public void testCanMapEmptyDnsResolutionTest()
    {        
        // create test case
        DnsResolutionTest testcase = pluginFactory.createDnsResolutionTest();                
        
        // map values
        mapper.mapDnsResolutionTest( testcase, context );        
        
        // test        
        assertEquals( null, context.get( TestDnsResolutionCommand.HOSTNAME_KEY ) );
        assertEquals( null, context.get( TestDnsResolutionCommand.IP_KEY) );                
    }    

    
    /**
     * Test that test case can be mapped.
     */
    @Test
    public void testCanMapDnsResolutionTest()
    {
        String description = "description";
        String host = "host";        
        String ip = "127.0.0.1";        
    	
        // create test case
        DnsResolutionTest testcase = pluginFactory.createDnsResolutionTest();
        testcase.setDescription(description);
        testcase.setHost(host);
        testcase.setIp(ip);
        
        // map values
        mapper.mapDnsResolutionTest( testcase, context );        
        
        // test        
        assertEquals( host, context.get( TestDnsResolutionCommand.HOSTNAME_KEY ) );
        assertEquals( ip, context.get( TestDnsResolutionCommand.IP_KEY) );                
    }    

    /**
     * Test that empty test case can be mapped.
     */
    @Test
    public void testCanMapEmptyDnsForwardResolutionTest()
    {        
        // create test case
        DnsResolutionTest testcase = pluginFactory.createDnsResolutionTest();                
        
        // map values
        mapper.mapDnsForwardResolutionTest( testcase, context );        
        
        // test        
        assertEquals( null, context.get( TestDnsForwardResolutionCommand.HOSTNAME_KEY ) );
        assertEquals( null, context.get( TestDnsForwardResolutionCommand.IP_KEY) );                
    }    

    
    /**
     * Test that test case can be mapped.
     */
    @Test
    public void testCanMapDnsForwardResolutionTest()
    {
        String description = "description";
        String host = "host";        
        String ip = "127.0.0.1";        
    	
        // create test case
        DnsResolutionTest testcase = pluginFactory.createDnsResolutionTest();
        testcase.setDescription(description);
        testcase.setHost(host);
        testcase.setIp(ip);
        
        // map values
        mapper.mapDnsForwardResolutionTest( testcase, context );        
        
        // test        
        assertEquals( host, context.get( TestDnsForwardResolutionCommand.HOSTNAME_KEY ) );
        assertEquals( ip, context.get( TestDnsForwardResolutionCommand.IP_KEY) );                
    }    

    /**
     * Test that empty test case can be mapped.
     */
    @Test
    public void testCanMapEmptyDnsReverseResolutionTest()
    {        
        // create test case
        DnsResolutionTest testcase = pluginFactory.createDnsResolutionTest();                
        
        // map values
        mapper.mapDnsReverseResolutionTest( testcase, context );        
        
        // test        
        assertEquals( null, context.get( TestDnsReverseResolutionCommand.HOSTNAME_KEY ) );
        assertEquals( null, context.get( TestDnsReverseResolutionCommand.IP_KEY) );                
    }    

    
    /**
     * Test that test case can be mapped.
     */
    @Test
    public void testCanMapDnsReverseResolutionTest()
    {
        String description = "description";
        String host = "host";        
        String ip = "127.0.0.1";        
    	
        // create test case
        DnsResolutionTest testcase = pluginFactory.createDnsResolutionTest();
        testcase.setDescription(description);
        testcase.setHost(host);
        testcase.setIp(ip);
        
        // map values
        mapper.mapDnsReverseResolutionTest( testcase, context );        
        
        // test        
        assertEquals( host, context.get( TestDnsReverseResolutionCommand.HOSTNAME_KEY ) );
        assertEquals( ip, context.get( TestDnsReverseResolutionCommand.IP_KEY) );                
    }    
    
    
    
    /**
     * Test that empty test case can be mapped.
     */
    @Test
    public void testCanMapEmptyTcpConnectionTest()
    {        
        // create test case
        TcpConnectionTest testcase = pluginFactory.createTcpConnectionTest();                
        
        // map values
        mapper.mapTcpConnectionTest( testcase, context );        
        
        // test        
        assertNull( context.get( TestTcpConnectionCommand.HOST_KEY ) );
        Object actualPorts = context.get( TestTcpConnectionCommand.PORTS_KEY );
        assertNotNull(actualPorts);
        assertTrue( actualPorts instanceof int[] );        
        assertEquals( 0, ((int[]) actualPorts).length );                
    }    
    

    /**
     * Test that test case can be mapped.
     */
    @Test
    public void testCanMapTcpConnectionTestWithSinglePort()
    {
        String description = "description";
        String host = "host";        
        int port = 8081;        
    	
        // create test case
        TcpPort tcpPort = pluginFactory.createTcpPort();
        tcpPort.setValue(port);
        TcpConnectionTest testcase = pluginFactory.createTcpConnectionTest();                
        testcase.setDescription(description);
        testcase.setHost(host);
        testcase.getPort().add(tcpPort);
        
        // map values
        mapper.mapTcpConnectionTest(testcase, context );        
        
        // test        
        assertEquals( host, context.get( TestTcpConnectionCommand.HOST_KEY ) );
        int[] actualPorts = (int[]) context.get( TestTcpConnectionCommand.PORTS_KEY );        
        assertEquals( 1, actualPorts.length );                
        assertEquals( port , actualPorts[FIRST_INDEX]);        
    }    

    /**
     * Test that test case can be mapped.
     */
    @Test
    public void testCanMapTcpConnectionTestWithMultiplePorts()
    {
        String description = "description";
        String host = "host";        
        int port = 8081;
        int port2 = 8082;        
        
    	
        // create test case
        TcpPort tcpPort = pluginFactory.createTcpPort();
        tcpPort.setValue(port);
        TcpPort tcpPort2 = pluginFactory.createTcpPort();
        tcpPort2.setValue(port2);        
        TcpConnectionTest testcase = pluginFactory.createTcpConnectionTest();                
        testcase.setDescription(description);
        testcase.setHost(host);
        testcase.getPort().add(tcpPort);
        testcase.getPort().add(tcpPort2);
        
        // map values
        mapper.mapTcpConnectionTest(testcase, context );        
        
        // test        
        assertEquals( host, context.get( TestTcpConnectionCommand.HOST_KEY ) );
        int[] actualPorts = (int[]) context.get( TestTcpConnectionCommand.PORTS_KEY );        
        assertEquals( 2, actualPorts.length );                
        assertEquals( port , actualPorts[FIRST_INDEX]);        
        assertEquals( port2 , actualPorts[FIRST_INDEX+1]);        
    }    
    
    /**
     * Test that empty test case can be mapped.
     */
    @Test
    public void testCanMapEmptyHttpRedirectTest()
    {        
        // create test case
        HttpRedirectTest testcase = pluginFactory.createHttpRedirectTest();                
        
        // map values
        mapper.mapHttpRedirectTest( testcase, context, httpConfigMap );        
        
        // test        
        assertNotNull( context.get( TestHttpCommand.URLS_KEY ) );
        assertTrue( context.get( TestHttpCommand.URLS_KEY ) instanceof String[]);
        String[] urls = (String[]) context.get( TestHttpCommand.URLS_KEY );
        assertEquals(1, urls.length );
        assertDefaultRedirectHttpConfiguration(context, TestHttpCommand.HTTPCONFIGURATION_KEY);
        ResponsePropertyInfoSet propertiesSet = (ResponsePropertyInfoSet) context.get( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY);
        assertNotNull(propertiesSet);
        ResponsePropertyInfo[] props = propertiesSet.getProperties();
        assertEquals(2,props.length);    	
    }

    /**
     * Test that test case can be mapped.
     */
    @Test
    public void testCanMapHttpRedirectTest()
    {
        // define values
        String description = "description";
        String url = "http://nicehost";
        String redirectUrl = "http://anotherhost";        

        // create test case
        HttpRedirectTest testcase = pluginFactory.createHttpRedirectTest();
        testcase.setDescription(description);
        testcase.setUrl(url);
        testcase.setAssert(pluginFactory.createHttpRedirectAssertionValues());
        testcase.getAssert().setUrl(redirectUrl);        
        
        // map values
        mapper.mapHttpRedirectTest( testcase, context, httpConfigMap );        
        
        // test        
        assertNotNull( context.get( TestHttpCommand.URLS_KEY ) );
        assertTrue( context.get( TestHttpCommand.URLS_KEY ) instanceof String[]);
        String[] urls = (String[]) context.get( TestHttpCommand.URLS_KEY );
        assertEquals(1, urls.length );
        assertDefaultRedirectHttpConfiguration(context, TestHttpCommand.HTTPCONFIGURATION_KEY);
        ResponsePropertyInfoSet propertiesSet = (ResponsePropertyInfoSet) context.get( TestHttpCommand.RESPONSEPROPERTIES_SET_KEY);
        assertNotNull(propertiesSet);
        ResponsePropertyInfo[] props = propertiesSet.getProperties();
        assertEquals(2,props.length);    	
    }

    
}
