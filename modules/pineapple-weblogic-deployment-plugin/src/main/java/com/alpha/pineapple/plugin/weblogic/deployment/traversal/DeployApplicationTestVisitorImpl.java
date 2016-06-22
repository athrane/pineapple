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


package com.alpha.pineapple.plugin.weblogic.deployment.traversal;

import java.util.Map;

import javax.enterprise.deploy.spi.DeploymentManager;

import org.apache.commons.chain.Context;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.ChainFactory;
import com.alpha.pineapple.command.DefaultChainFactoryImpl;
import com.alpha.pineapple.command.test.TestRunnerV1;
import com.alpha.pineapple.configuration.ConfigurationFactory;
import com.alpha.pineapple.configuration.access.ConfigurationAccessor;
import com.alpha.pineapple.context.ManagerContext;
import com.alpha.pineapple.jsr88.command.CommandNames;
import com.alpha.pineapple.jsr88.command.TestModuleIsDeployedCommand;
import com.alpha.pineapple.jsr88.command.TestTargetExistsInDomainCommand;
import com.alpha.pineapple.plugin.weblogic.deployment.argument.FileLocator;
import com.alpha.pineapple.plugin.weblogic.deployment.argument.FileLocatorImpl;
import com.alpha.pineapple.plugin.weblogic.deployment.session.WeblogicDeploymentSession;
import com.alpha.pineapple.session.Session;

/**
 * Visitor which implements an deploy-application-test management operation.
 */
public class DeployApplicationTestVisitorImpl
{
    /**
     * Module "targets" parameter.
     */
    static final String MODULE_TARGETS_PARAMETER = "targets";

    /**
     * First list index constant.
     */
    final static int FIRST_INDEX = 0;

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Chain factory.
     */
    ChainFactory factory;

    /**
     * Configuration accessor object.
     */
    ConfigurationAccessor configAccessor;

    /**
     * WebLogic Deployment session object.
     */
    WeblogicDeploymentSession session;

    /*
     * Test runner
     */
    TestRunnerV1 runner;

    /**
     * Command context.
     */
    Context context;

    /**
     * File locator object.
     */
    FileLocator fileLocator;

    /**
     * InstallApplicationTestVisitorImpl constructor.
     * 
     * @param context
     *            Context Context object.
     * @param session
     *            Session which the visitor should access.
     * @throws Exception
     *             If visitor creation fails.
     */
    public DeployApplicationTestVisitorImpl( Context context, Session session ) throws Exception
    {
        this( new FileLocatorImpl(), context, session );
    }

    /**
     * InstallApplicationTestVisitorImpl constructor.
     * 
     * @param fileLocator
     *            File locator object.
     * @param context
     *            Context Context object.
     * @param session
     *            Session which the visitor should access.
     * @throws Exception
     *             If visitor creation fails.
     */
    public DeployApplicationTestVisitorImpl( FileLocator fileLocator, Context context, Session session )
        throws Exception
    {
        super();

        // validate parameters
        Validate.notNull( session, "session is undefined." );
        Validate.notNull( context, "context is undefined." );
        Validate.notNull( context, "fileLocator is undefined." );

        // type cast session
        this.session = WeblogicDeploymentSession.class.cast( session );

        // create factory
        factory = DefaultChainFactoryImpl.getInstance();

        // load catalog
        factory.loadCatalog( CommandNames.CATALOG );

        // set context
        this.context = context;

        // create file locator
        this.fileLocator = fileLocator;

        // create configuration accessor
        configAccessor = ConfigurationFactory.createDefaultAccessor();

        // create test runner
        runner = factory.getTestRunner();
    }

    public void visitElementAfterChildren( ConfigurationNode node ) throws Exception
    {
        // no functionality here.
    }

    public void visitElementBeforeChildren( ConfigurationNode node ) throws Exception
    {
        // no functionality here.
    }

    public void visitResourceAfterChildren( ConfigurationNode node ) throws Exception
    {
        // get module parameters
        Map<String, String[]> parameters;
        parameters = configAccessor.getChildrenAsMap( node );

        // get targets
        String[] targets;
        targets = parameters.get( MODULE_TARGETS_PARAMETER );

        // read module identifier from context
        String identifier;
        identifier = (String) context.get( ManagerContext.MODULE_ID_KEY );

        // get deployment manager
        DeploymentManager manager;
        manager = session.getDeploymentManager();

        // create context for test commands
        Context testContext = factory.createContext();

        // initialize result collection
        runner.resetResults();

        // test that targets exists in domain
        for ( String target : targets )
        {

            // setup context
            testContext.put( TestTargetExistsInDomainCommand.DESCRIPTION_KEY, identifier );
            testContext.put( TestTargetExistsInDomainCommand.MANAGER_KEY, manager );
            testContext.put( TestTargetExistsInDomainCommand.TARGET_KEY, target );

            // execute test
            runner.run( CommandNames.TARGET_EXISTS, testContext );
        }

        // setup context
        testContext.put( TestModuleIsDeployedCommand.DESCRIPTION_KEY, identifier );
        testContext.put( TestModuleIsDeployedCommand.MANAGER_KEY, manager );
        testContext.put( TestModuleIsDeployedCommand.MODULE_KEY, identifier );
        testContext.put( TestModuleIsDeployedCommand.TARGETS_KEY, targets );

        // execute test
        runner.run( CommandNames.MODULE_IS_DEPLOYED, testContext );

        // post test result
        runner.postResults();
    }

    public void visitResourceBeforeChildren( ConfigurationNode node ) throws Exception
    {
        // no functionality here.
    }

}
