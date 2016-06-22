/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.command;

import java.io.File;

import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.module.model.AggregatedModel;
import com.alpha.pineapple.plugin.activation.PluginActivator;

/**
 * Command facade for core commands.
 */
public interface CommandFacade {

    /**
     * Unmarshall JAXB objects from file.
     * 
     * @param file
     *            file to load.
     * @param targetClass
     *            root target JAXB generated class that XML file is marshalled
     *            into.
     * @param result
     *            root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            loading the file.
     * 
     * @return root object of the unmarshalled object.
     * 
     * @throws CommandFacadeException
     *             if load fails.
     */
    <T> T unmarshallJaxbObjects(File file, Class<T> targetClass, ExecutionResult result);

    /**
     * Load JAXB objects from XML file.
     * 
     * @param file
     *            file to load.
     * @param targetClass
     *            root target JAXB generated class that XML file is marshalled
     *            into.
     * @param result
     *            root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            loading the file.
     * 
     * @return root object of the unmarshalled object.
     * 
     * @throws CommandFacadeException
     *             if load fails.
     */
    <T> T loadJaxbObjects(File file, Class<T> targetClass, ExecutionResult result);

    /**
     * Marshall JAXB objects to file.
     * 
     * @param file
     *            file to marshall objects to. If the file isn't specified as a
     *            absolute path the the operation terminates with a failure. If
     *            any directories in the path doesn't exist then they will be
     *            created.
     * @param root
     *            Defines the root object in the object graph which should be
     *            marshalled.
     * @param result
     *            root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            saving the file.
     * 
     * @throws CommandFacadeException
     *             if save fails.
     */
    void marshallJaxbObjects(File file, Object root, ExecutionResult result);

    /**
     * Save JAXB objects to file.
     * 
     * @param file
     *            file to marshall objects to. If the file isn't specified as a
     *            absolute path the the operation terminates with a failure. If
     *            any directories in the path doesn't exist then they will be
     *            created.
     * @param root
     *            Defines the root object in the object graph which should be
     *            marshalled.
     * @param result
     *            root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            saving the file.
     * 
     * @throws CommandFacadeException
     *             if save fails.
     */
    void saveJaxbObjects(File file, Object root, ExecutionResult result);

    /**
     * Copy example modules to target directory.
     * 
     * @param modulesDirectory
     *            target directory for examples modules.
     * @param result
     *            root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            saving the file.
     * 
     * @throws CommandFacadeException
     *             if copy fails.
     */
    void copyExampleModules(File modulesDirectory, ExecutionResult result);

    /**
     * Initialize plugin activator.
     * 
     * @param provider
     *            credential provider.
     * @param configuration
     *            resource configuration.
     * @param result
     *            root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            saving the file.
     * 
     * @return initialized plugin activator
     * 
     * @throws CommandFacadeException
     *             if initialization fails.
     */
    PluginActivator initializePluginActivator(CredentialProvider provider, Configuration configuration,
	    ExecutionResult result);

    /**
     * Execute triggers in aggregate module model.
     * 
     * @param model
     *            aggregated model containing the triggers.
     * @param executionInfo
     *            execution info.
     * @param modelResult
     *            model result. Used for reading the execution result to match
     *            trigger resolution directive to determine whether a trigger
     *            should be executed.
     * @param result
     *            model result. A child execution result is added to the model
     *            result. The child result describes the outcome of executing
     *            any triggers.
     */
    void executeTriggers(AggregatedModel model, ExecutionInfo executionInfo, ExecutionResult modelResult,
	    ExecutionResult result);

}
