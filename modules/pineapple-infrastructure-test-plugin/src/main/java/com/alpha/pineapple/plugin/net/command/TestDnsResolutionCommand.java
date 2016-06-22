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

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.test.Asserter;
import com.alpha.pineapple.test.matchers.IsHostResolvableToIpAddress;
import com.alpha.pineapple.test.matchers.InfrastructureMatchers;

/**
 * <p>Implementation of the <code>org.apache.commons.chain.Command</code> interface which 
 * asserts that a forward DNS lookup can resolve a host name to an IP address and that
 * a reverse DNS lookup can resolve an IP address to a host name.</p>
 * 
 * <p>If the the IP address is undefined then a forward DNS lookup is attempted and the 
 * resolved IP address is used to do a reverse DNS lookup to verify that the IP address 
 * can be resolved back to the host name.
 * </p>
 * 
 * <p>Precondition for execution of the command is definition of these keys in 
 * the context:
 * 
 * <ul> 
 * <li><code>hostname</code> defines name of the host. The type is 
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>ip</code> defines the expected IP address to resolve the name to. 
 * The type is <code>java.lang.String</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which collects
 * information about the execution of the test. The type is 
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>  
 * </ul>
 * </p>      
 * 
 * <p>Postcondition after execution of the command is: 
 * <ul> 
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the test succeeded. If the 
 * test failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the test fails due to an exception then the exception isn't caught, 
 * but passed on the the invoker whose responsibility it is to catch it and update 
 * the <code>ExecutionResult</code> with the state <code>ExecutionState.ERROR</code>.
 * </li>
 * </ul>  
 * </p>        
 */
public class TestDnsResolutionCommand implements Command
{
    /**
     * Key used to identify property in context: Defines the host name to resolve.
     */
    public static final String HOSTNAME_KEY = "hostname";

    /**
     * Key used to identify property in context: Defines the expected IP address to resolve name to.
     */
    public static final String IP_KEY = "ip";  
    
    /**
     * Key used to identify property in context: Contains execution result object,.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";
      
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * Host name.
     */
    @Initialize( HOSTNAME_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )    
    String hostname;

    /**
     * Expected IP address.
     */
    @Initialize( IP_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )    
    String ip;
 
    /**
     * Defines execution result object.
     */
    @Initialize( EXECUTIONRESULT_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )    
    ExecutionResult executionResult;
    
    /**
     * Hamcrest Matcher asserter.
     */
    @Resource    
	Asserter asserter;    
    
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
    
    /**
     * Actual IP address.
     */
    String actualIP;
    
    public boolean execute( Context context ) throws Exception
    {
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("tdrc.start") );        	
        }
        
        // initialize command
        CommandInitializer initializer = new CommandInitializerImpl();
        initializer.initialize( context, this );
        
        // configure asserter with execution result		
		asserter.setExecutionResult(executionResult);					        
        
        // run test
        doTest( context );

        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("tdrc.completed") );        	
        }
        
        return Command.CONTINUE_PROCESSING;
    }
      
    /**
     * Do test.
     * 
     * @param context
     *            Command context.
     * 
     * @throws Exception
     *             If test execution fails.
     */
    void doTest( Context context ) throws Exception
    {
    	if (isIpAddressDefined()) {
    		testWithDefinedIpAddress();
    	} else {
    		testWithUndefinedIpAddress();    		
    	}
    	
        // compute result
    	executionResult.completeAsComputed(messageProvider, "tdrc.succeed", null, "tdrc.failed", null );    	    	
    }

    /**
     * Test forward and reserve DNS lookup with undefined IP address.
     */    
    void testWithUndefinedIpAddress() {
	
		// create matcher
    	Matcher hostIsResolvableMatcher = InfrastructureMatchers.isHostResolvableToIpAddress();		
    	        	
    	// create assertion description
    	Object[] args = {this.hostname };        	
    	String message = messageProvider.getMessage("tdrc.assert_forward_dns_unknown_ip_info", args );
    	
		// assert object        	
    	asserter.assertObject(this.hostname, hostIsResolvableMatcher, message);
    	
    	// declare IP address
    	String ipAddress = null;
    	
    	// get IP address
    	if (asserter.lastAssertionSucceeded()) {
    		
    		// type cast to very specific type
    		IsHostResolvableToIpAddress matcher;
    		matcher = (IsHostResolvableToIpAddress) hostIsResolvableMatcher;
    		
    		// get IP address
    		ipAddress = matcher.getLastIpAddress();
    	} else {
    		ipAddress = "null";
    	}    	
    	
		// create matcher
    	Matcher ipAddressIsResolvableMatcher = InfrastructureMatchers.isIpAddressResolvableToHost(ipAddress);		
    	        	
    	// create assertion description
    	Object[] args2 = { ipAddress, this.hostname };        	
    	message = messageProvider.getMessage("tdrc.assert_reverse_dns_info", args2 );
    	
		// assert object        	
    	asserter.assertObject(this.hostname, ipAddressIsResolvableMatcher, message);		    	
	}

    /**
     * Test forward and reserve DNS lookup with defined IP address.
     */
	void testWithDefinedIpAddress() {
		
		// create matcher
    	Matcher hostIsResolvableMatcher = InfrastructureMatchers.isHostResolvableToSpecificIpAddress(this.ip);		
    	        	
    	// create assertion description
    	Object[] args = {this.hostname, this.ip };        	
    	String message = messageProvider.getMessage("tdrc.assert_forward_dns_info", args );
    	
		// assert object        	
    	asserter.assertObject(this.hostname, hostIsResolvableMatcher, message);

		// create matcher
    	Matcher ipAddressIsResolvableMatcher = InfrastructureMatchers.isIpAddressResolvableToHost(ip);		
    	        	
    	// create assertion description
    	Object[] args2 = { this.ip, this.hostname };        	
    	message = messageProvider.getMessage("tdrc.assert_reverse_dns_info", args2 );
    	
		// assert object        	
    	asserter.assertObject(this.hostname, ipAddressIsResolvableMatcher, message);		
	}

	/**
     * Return true if IP address is defined.
     * 
     * @return true if IP address is defined.
     */
	boolean isIpAddressDefined() {
		return (this.ip.length() != 0);
	}
         
}
