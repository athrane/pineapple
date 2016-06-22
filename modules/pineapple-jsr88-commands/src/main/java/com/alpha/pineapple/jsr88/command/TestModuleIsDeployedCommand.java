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

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.command.test.TestCommand;
import com.alpha.pineapple.jsr88.command.result.AvailableModulesResult;

/**
 * <p>
 * Implementation of the <code>com.alpha.pineapple.command.test.TestCommand</code> interface which tests whether
 * a module is deployed to a set of targets in a domain.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the context:
 * 
 * <ul>
 * <li><code>description</code> defines a human readable description of the 
 * test. The type is <code>java.lang.String</code>.</li>
 *  
 * <li><code>deployment-manager</code> contains the deployment manager instance. The type is
 * <code>javax.enterprise.deploy.spi.DeploymentManager</code>.</li>
 * 
 * <li><code>module</code> defines the module whose deployment status is tested. The type is<code>java.lang.String</code>.</li>
 * 
 * <li><code>targets</code> defines the targets that the module should be deployed to in the domain. The type is<code>java.lang.String[]</code>.</li>
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
public class TestModuleIsDeployedCommand implements TestCommand
{

    /**
     * Key used to identify property in context: Contains the deployment manager which should be used to access modules.
     */
    public static final String MANAGER_KEY = GetDeploymentManagerCommand.MANAGER_KEY;

    /**
     * Key used to identify property in context: Defines the module that should be deployed to in the domain.
     */
    public static final String MODULE_KEY = "module";

    /**
     * Key used to identify property in context: Defines the targets that the module should be deployed to in the
     * domain.
     */
    public static final String TARGETS_KEY = "targets";

    /**
     * Key used to identify property in context: Describes the test..
     */
    public static final String DESCRIPTION_KEY = "description";

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
     * Expected targets that module should be deployed to.
     */
    @Initialize( TARGETS_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )
    String[] targets;

    /**
     * Module under test.
     */
    @Initialize( MODULE_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )
    String module;

    /**
     * Test result.
     */
    boolean testSucceded;

    /**
     * Actual target list in current domain.
     */
    Set<String> actualTargets;

    public boolean execute( Context context ) throws Exception
    {

        // log debug message
        if ( logger.isDebugEnabled() )
            logger.debug( "Starting module-is-deployed test." );

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
            logger.debug( "Successfully completed module-is-deployed test." );
        }

        return Command.CONTINUE_PROCESSING;
    }

    /**
     * Run the test.
     * 
     * @throws Exception
     *             If test fails due to runtime exception.
     */
    void runTest() throws Exception
    {
        // create context
        Context context;
        context = new ContextBase();

        // setup context for command
        context.put( GetAvailableModulesCommand.MANAGER_KEY, manager );

        // create get deployment manager command
        Command command = new GetAvailableModulesCommand();

        // execute command
        command.execute( context );

        // get result
        AvailableModulesResult result;
        result = (AvailableModulesResult) context.get( GetAvailableModulesCommand.RESULT_KEY );

        // search for module in domain
        TargetModuleID[] foundModules;
        foundModules = result.findModules( module );

        // create set actual targets
        actualTargets = createActualTargetsSet( foundModules );

        // log debug message
        if ( logger.isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Actual targets <" );
            message.append( actualTargets );
            message.append( ">." );

            logger.debug( message.toString() );
        }

        // create set for expected targets
        Set<String> expectedTargets;
        expectedTargets = createExpectedTargetsSet( targets );

        // log debug message
        if ( logger.isDebugEnabled() )
        {
            StringBuilder message = new StringBuilder();
            message.append( "Expected targets <" );
            message.append( expectedTargets );
            message.append( ">." );

            logger.debug( message.toString() );
        }

        // compare target sets
        testSucceded = actualTargets.equals( expectedTargets );
    }

    /**
     * Create set with expected targets
     * 
     * @param modules
     *            Array of expected targets.
     * 
     * @return set with expected targets.
     */
    Set<String> createExpectedTargetsSet( String[] targets )
    {
        // create set
        HashSet<String> expectedTargets;
        expectedTargets = new HashSet<String>();

        // populate set
        for ( String target : targets )
        {
            expectedTargets.add( target );

        }

        return expectedTargets;
    }

    /**
     * Create set with actual targets
     * 
     * @param modules
     *            Array of targeted modules.
     * 
     * @return set with actual targets.
     */
    Set<String> createActualTargetsSet( TargetModuleID[] modules )
    {
        // create set
        HashSet<String> actualTargets;
        actualTargets = new HashSet<String>();

        // populate set
        for ( TargetModuleID targetedModule : modules )
        {

            // get target
            Target target = targetedModule.getTarget();

            // get target name
            String name = target.getName();

            // add target module
            actualTargets.add( name );
        }

        return actualTargets;
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
            message.append( "TEST SUCCEDED - module-is-deployed for <" );
            message.append( description );
            message.append( "> found module <" );
            message.append( module );
            message.append( "> deployed to targets <" );
            message.append( ReflectionToStringBuilder.toString( targets ) );
            message.append( ">." );
            return message.toString();

        }
        else
        {

            // create info message
            StringBuilder message = new StringBuilder();
            message.append( "TEST FAILED - module-is-deployed for <" );
            message.append( description );
            message.append( "> failed to find module <" );
            message.append( module );
            message.append( "> deployed to expected targets <" );
            message.append( ReflectionToStringBuilder.toString( targets ) );
            message.append( ">. Actual targets are <" );
            message.append( actualTargets );
            message.append( ">." );

            return message.toString();
        }
    }

}
