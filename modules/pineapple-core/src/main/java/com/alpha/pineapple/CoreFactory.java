/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple;

import java.io.File;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.credential.FileBasedCredentialProviderImpl;
import com.alpha.pineapple.execution.ResultListener;
import com.alpha.pineapple.model.configuration.Configuration;

/**
 * Factory class for Pineapple core component.
 */
public class CoreFactory {
    /**
     * Spring bean id for core component.
     */
    static final String CORE_BEAN_ID = "uninitializedPineappleCore";

    /**
     * Spring bean id for core component.
     */
    static final String PROVIDER_BEAN_ID = "uninitializedFileBasedCredentialProvider";

    /**
     * Spring configuration file for the core component.
     */
    static final String CORE_CONFIG = "com.alpha.pineapple.core-config.xml";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Pineapple core component
     */
    @Resource
    CoreImpl uninitializedPineappleCore;

    /**
     * File based credential provider implementation.
     */
    @Resource
    FileBasedCredentialProviderImpl uninitializedFileBasedCredentialProvider;

    /**
     * Spring application context, used to lookup the core component
     */
    ApplicationContext context;

    /**
     * Create Pineapple core instance with default configuration.
     *
     * @return new Pineapple core instance with default configuration.
     * 
     * @throws CoreException
     *             If core instance creation fails.
     */
    public PineappleCore createCore() throws CoreException {
	// create core
	CoreImpl core = getCoreInstanceFromSpring();

	// initialize core
	core.initialize();

	return core;
    }

    /**
     * Create Pineapple core instance with default configuration.
     *
     * @param listeners
     *            Array of Result listeners.
     * 
     * @return new Pineapple core instance with default configuration.
     * 
     * @throws CoreException
     *             If core instance creation fails.
     */
    public PineappleCore createCore(ResultListener[] listeners) throws CoreException {
	// create core
	CoreImpl core = getCoreInstanceFromSpring();

	// register listeners prior to initialization
	core.addListeners(listeners);

	// initialize core
	core.initialize();

	return core;
    }

    /**
     * Create Pineapple core instance.
     *
     * @param provider
     *            Credential provider, which provides security credentials for
     *            resources.
     * 
     * @return new Pineapple core instance.
     * 
     * @throws CoreException
     *             If core instance creation fails.
     */
    public PineappleCore createCore(CredentialProvider provider) throws CoreException {
	// create core
	CoreImpl core = getCoreInstanceFromSpring();

	// initialize core
	core.initialize(provider);

	return core;
    }

    /**
     * Create Pineapple core instance.
     *
     * @param provider
     *            Credential provider, which provides security credentials for
     *            resources.
     * @param listeners
     *            Array of Result listeners.
     * 
     * @return new Pineapple core instance.
     * 
     * @throws CoreException
     *             If core instance creation fails.
     */
    public PineappleCore createCore(CredentialProvider provider, ResultListener[] listeners) throws CoreException {
	// create core
	CoreImpl core = getCoreInstanceFromSpring();

	// register listeners prior to initialization
	core.addListeners(listeners);

	// initialize core
	core.initialize(provider);

	return core;
    }

    /**
     * Create Pineapple core instance.
     *
     * @param provider
     *            Credential provider, which provides security credentials for
     *            resources.
     * @param resources
     *            Resources file object. Defines location where the resources
     *            file should be loaded from.
     * 
     * @return new Pineapple core instance.
     * 
     * @throws CoreException
     *             If core instance creation fails.
     */
    public PineappleCore createCore(CredentialProvider provider, File resources) throws CoreException {
	// create core
	CoreImpl core = getCoreInstanceFromSpring();

	// initialize core
	core.initialize(provider, resources);

	return core;
    }

    /**
     * Create Pineapple core instance.
     *
     * @param provider
     *            Credential provider, which provides security credentials for
     *            resources.
     * @param resources
     *            Resources file object. Defines location where the resources
     *            file should be loaded from.
     * @param listeners
     *            Array of Result listeners.
     * 
     * @return new Pineapple core instance.
     * 
     * @throws CoreException
     *             If core instance creation fails.
     */
    public PineappleCore createCore(CredentialProvider provider, File resources, ResultListener[] listeners)
	    throws CoreException {
	// create core
	CoreImpl core = getCoreInstanceFromSpring();

	// register listeners prior to initialization
	core.addListeners(listeners);

	// initialize core
	core.initialize(provider, resources);

	return core;
    }

    /**
     * Create credential provider which loads credentials from XML file.
     *
     * @param file
     *            File object which defines location of XML credentials file.
     * 
     * @return new credential provider instance.
     * 
     * @throws CoreException
     *             If provider instance creation fails.
     */
    public CredentialProvider createCredentialProvider(File file) throws CoreException {
	// create provider
	FileBasedCredentialProviderImpl provider = getFileBasedCredentialProviderInstanceFromSpring();

	// initialize provider
	provider.initialize(file);

	return provider;
    }

    /**
     * Create credential provider which loads credentials from environment
     * configuration object.
     *
     * @param configuration
     *            Environment configuration.
     * 
     * @return new credential provider instance.
     * 
     * @throws CoreException
     *             If provider instance creation fails.
     */
    public CredentialProvider createCredentialProvider(Configuration configuration) throws CoreException {
	// create provider
	FileBasedCredentialProviderImpl provider = getFileBasedCredentialProviderInstanceFromSpring();

	// initialize provider
	provider.initialize(configuration);

	return provider;
    }

    /**
     * Creates core instance which is initialized from Spring context.
     * 
     * @return core instance initialized from Spring context.
     */
    CoreImpl getCoreInstanceFromSpring() {

	// exit if factory already contains instance
	if (uninitializedPineappleCore != null)
	    return uninitializedPineappleCore;

	// initialize Spring context if needed
	if (context == null) {
	    String[] appConfigFile = new String[] { CORE_CONFIG };
	    context = new ClassPathXmlApplicationContext(appConfigFile);
	}

	// get core component
	return (CoreImpl) context.getBean(CORE_BEAN_ID);
    }

    /**
     * Create file based credential provider instance which is initialized from
     * Spring context.
     * 
     * @return file based credential provider initialized from Spring context.
     */
    FileBasedCredentialProviderImpl getFileBasedCredentialProviderInstanceFromSpring() {

	// exit if factory already contains instance
	if (uninitializedFileBasedCredentialProvider != null)
	    return uninitializedFileBasedCredentialProvider;

	// initialize Spring context if needed
	if (context == null) {
	    String[] appConfigFile = new String[] { CORE_CONFIG };
	    context = new ClassPathXmlApplicationContext(appConfigFile);
	}

	// get provider
	FileBasedCredentialProviderImpl provider = (FileBasedCredentialProviderImpl) context.getBean(PROVIDER_BEAN_ID);
	return provider;
    }

}
