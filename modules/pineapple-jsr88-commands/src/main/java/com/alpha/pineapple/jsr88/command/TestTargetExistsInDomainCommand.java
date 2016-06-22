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


package com.alpha.pineapple.jsr88.command;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.command.test.TestCommand;

/**
 * <p>
 * Implementation of the <code>com.alpha.pineapple.command.test.TestCommand</code> interface 
 * which tesst whether a target is exists in a domain.  
 * </p>
 * 
 * <p>The command uses the deployment manager contained in the key <code>deployment-manager</code> 
 * to get a set of all available targets in the domain that the deployment manager is connected to.
 * The set is matched against the value in the <code>target</code> key. If a target name is 
 * found which matches the content of the <code>target</code> key then it is considered a 
 * match and the test succeeds. 
 * </p> 
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>description</code> defines a human readable description of the 
 * test. The type is <code>java.lang.String</code>.</li>
 *  
 * <li><code>deployment-manager</code> Contains the created deployment manager instance. The type is
 * <code>javax.enterprise.deploy.spi.DeploymentManager</code>.</li>
 * 
 * <li><code>target</code> Defines the target that whose existence is tested, i.e the expected values in the test.
 * The type is<code>java.lang.String</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>message</code> which contains a human readable description of how the result of the test. The type is
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>result</code> which contains the result of the test as a boolean value. The type is
 * <code>java.lang.Boolean</code>.</li>
 * </ul>
 * </p>
 */
public class TestTargetExistsInDomainCommand implements TestCommand
{

    /**
     * Key used to identify property in context: Contains the deployment manager which should be used to access modules.
     */
    public static final String MANAGER_KEY = GetDeploymentManagerCommand.MANAGER_KEY;

    /**
     * Key used to identify property in context: Defines the target to look for in domain.
     */
    public static final String TARGET_KEY = "target";

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
     * deployment manager.
     */
    @Initialize( MANAGER_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )    
    DeploymentManager manager;

    /**
     * Target to test for.
     */
    @Initialize( TARGET_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )        
    String target;

    /**
     * Test result.
     */
    boolean testSucceded;

    /**
     * Target found during test
     */
    Target actualTarget;

    /**
     * Target list in current domain.
     */
    Target[] actualTargets;

    public boolean execute( Context context ) throws Exception
    {

        // log debug message
        if ( logger.isDebugEnabled() ) {
            logger.debug( "Starting target-exists test." );            
        }

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
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "Successfully completed target-exists test." );
        }

        return Command.CONTINUE_PROCESSING;
    }

    /**
     * Run the test.
     */
    void runTest()
    {
        // get targets
        actualTargets = manager.getTargets();

        // log debug message
        if ( logger.isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Got actual targets <" );
            message.append( ToStringBuilder.reflectionToString( actualTargets ) );
            message.append( ">." );

            logger.debug( message.toString() );
        }

        // iterate over targets
        for ( Target currentTarget : actualTargets )
        {
            // get name
            String name = currentTarget.getName();

            // compare
            if ( name.equals( target ) )
            {

                // found match
                testSucceded = true;

                // store target
                actualTarget = currentTarget;

                return;
            }
        }

        // flag test as failed
        testSucceded = false;
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
            message.append( "TEST SUCCEDED - target-exists for <" );
            message.append( description );
            message.append( "> found expected target <" );
            message.append( target );
            message.append( "> with actual values <" );
            message.append( ReflectionToStringBuilder.toString( actualTarget ) );
            message.append( ">." );

            return message.toString();

        }
        else
        {

            // create info message
            StringBuilder message = new StringBuilder();
            message.append( "TEST FAILED - target-exists for <" );
            message.append( description );
            message.append( "> couldn't find expected target <" );
            message.append( target );
            message.append( ">. Available target list was <" );
            message.append( ReflectionToStringBuilder.toString( actualTargets ) );
            message.append( ">." );

            return message.toString();
        }
    }

}
