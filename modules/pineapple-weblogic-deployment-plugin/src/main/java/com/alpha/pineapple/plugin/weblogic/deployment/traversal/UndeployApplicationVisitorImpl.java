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

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import weblogic.Deployer;

import com.alpha.pineapple.configuration.ConfigurationFactory;
import com.alpha.pineapple.configuration.access.ConfigurationAccessor;
import com.alpha.pineapple.context.ManagerContext;
import com.alpha.pineapple.jsr88.command.result.AvailableModulesResult;
import com.alpha.pineapple.plugin.weblogic.deployment.argument.ArgumentBuilder;
import com.alpha.pineapple.plugin.weblogic.deployment.argument.FileLocator;
import com.alpha.pineapple.plugin.weblogic.deployment.argument.FileLocatorImpl;
import com.alpha.pineapple.plugin.weblogic.deployment.session.WeblogicDeploymentSession;
import com.alpha.pineapple.session.Session;

/**
 * Visitor which implements an undeploy-application management operation.
 */
public class UndeployApplicationVisitorImpl
{

    /**
     * The command to invoke the deployer with.
     */
    static final String DEPLOYER_COMMAND = "-undeploy";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * Configuration accessor object.
     */
    ConfigurationAccessor configAccessor;

    /**
     * WebLogic Deployment session object.
     */
    WeblogicDeploymentSession session;

    /**
     * WebLogic.deployer object.
     */
    Deployer deployer;

    /**
     * Pineapple context object.
     */
    Context context;

    /**
     * File locator object.
     */
    FileLocator fileLocator;

    /**
     * Argument builder object.
     */
    ArgumentBuilder builder;

    /**
     * UninstallAppVisitorImpl constructor.
     * 
     * @param context
     *            Context Context object.
     * @param session
     *            Session which the visitor should access.
     */
    public UndeployApplicationVisitorImpl( Context context, Session session )
    {
        this( new FileLocatorImpl(), context, session );
    }

    /**
     * UninstallAppVisitorImpl constructor.
     * 
     * @param fileLocator
     *            File locator object.
     * @param context
     *            Context Context object.
     * @param session
     *            Session which the visitor should access.
     */
    public UndeployApplicationVisitorImpl( FileLocator fileLocator, Context context, Session session )
    {
        super();

        // validate parameters
        Validate.notNull( session, "session is undefined." );
        Validate.notNull( context, "context is undefined." );
        Validate.notNull( context, "fileLocator is undefined." );

        // type cast session
        this.session = WeblogicDeploymentSession.class.cast( session );

        // set context
        this.context = context;

        // create file locator
        this.fileLocator = fileLocator;

        // create configuration accessor
        configAccessor = ConfigurationFactory.createDefaultAccessor();
        
        // create argument builder
        builder = new ArgumentBuilder( fileLocator );        
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
        // get management data parameters
        Map<String, String[]> parameters;
        parameters = configAccessor.getChildrenAsMap( node );

        // read management data identifier from context
        String identifier;
        identifier = (String) context.get( ManagerContext.MODULE_ID_KEY );

        // start build process
        builder.buildArgumentList( parameters );

        // add deploy command
        builder.addSingleArgument( DEPLOYER_COMMAND );

        // set name argument
        if( session.isTimeStampEnabled()) {
            
            // get available modules
            AvailableModulesResult modules = session.getAvailableModules();
            
            // add time stamped name
            builder.addTimeStampedNameArgument( identifier,  modules );
            
        } else {
            builder.addNameArgument( identifier );
        }      
        
        // map arguments
        builder.mapArguments();

        // get product
        List<String> argumentList = builder.getArgumentList();

        // invoke deployer
        session.invokeDeployer( argumentList );
    }

    public void visitResourceBeforeChildren( ConfigurationNode node ) throws Exception
    {
        // no functionality here.
    }

}
