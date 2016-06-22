/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen.
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

package com.alpha.testutils;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.model.module.Module;
import com.alpha.pineapple.model.module.Variable;
import com.alpha.pineapple.model.module.model.AggregatedModel;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.model.module.model.Trigger;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for
 * unit testing classes which uses a module.
 */
public class ObjectMotherModule {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Commons Configuration object mother.
     */
    ObjectMotherConfiguration configMother;

    /**
     * Location of the last created module.
     */
    File moduleDir;

    /**
     * Application directory in last created module.
     */
    File appDir;

    /**
     * Properties directory in last created module.
     */
    File modelsDir;

    /**
     * Plan directory in last created module.
     */
    File planDir;

    /**
     * ObjectMotherModule constructor.
     */
    public ObjectMotherModule() {
	super();

	// create object mother configuration
	configMother = new ObjectMotherConfiguration();
    }

    /**
     * Creates basic module with a module sub directory.
     * 
     * @param moduleName
     *            Name of the module.
     * @param rootDirectory
     *            The root directory where the module should be created.
     */
    public void createModuleDirectory(String moduleName, File rootDirectory) {
	// validate whether directory already exists
	if (!rootDirectory.exists()) {
	    // log debug message
	    StringBuilder message = new StringBuilder();
	    message.append("The root directory <");
	    message.append(rootDirectory);
	    message.append("> doesn't exists.");

	    // fail test
	    fail(message.toString());
	}

	// create module directory
	moduleDir = ObjectMotherIO.createSubDirectory(rootDirectory, moduleName);

	// log debug message
	StringBuilder message = new StringBuilder();
	message.append("Successfully created module ");
	message.append("in the directory <");
	message.append(moduleDir);
	message.append(">.");
	logger.debug(message.toString());
    }

    /**
     * Creates basic module, with a module directory and models sub directory.
     * 
     * @param moduleName
     *            Name of the module.
     * @param rootDirectory
     *            The root directory where the module should be created.
     */
    public void createModuleDirectoryWithModelsDirectory(String moduleName, File rootDirectory) {
	createModuleDirectory(moduleName, rootDirectory);

	// create models directory
	modelsDir = ObjectMotherIO.createSubDirectory(moduleDir, "models");

	// log debug message
	StringBuilder message = new StringBuilder();
	message.append("Successfully created module ");
	message.append("in the directory <");
	message.append(moduleDir);
	message.append(">.");
	logger.debug(message.toString());
    }

    /**
     * Marshall object graph to file using JAXB.
     * 
     * @param rootObject
     *            rootObject Root object of object graph which should be
     *            marshalled.
     * @param file
     *            File that the environment configuration should be saved to.
     */
    public void jaxbMarshall(Object rootObject, File file) {
	// define stream for exception handling
	OutputStream os = null;

	try {
	    // get package name
	    String packageName = rootObject.getClass().getPackage().getName();

	    // log debug message
	    StringBuilder message = new StringBuilder();
	    message.append("Will marshall objects <");
	    message.append(rootObject);
	    message.append("> to file <");
	    message.append(file.getAbsolutePath());
	    message.append("> using package <");
	    message.append(packageName);
	    message.append(">.");
	    logger.debug(message.toString());

	    JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
	    Marshaller marshaller = jaxbContext.createMarshaller();

	    // set pretty print
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

	    os = new FileOutputStream(file);
	    marshaller.marshal(rootObject, os);
	    os.close();
	} catch (Exception e) {
	    fail(StackTraceHelper.getStrackTrace(e));
	} finally {

	    // close OS
	    if (os != null) {
		try {
		    os.close();
		} catch (IOException e) {
		    fail(StackTraceHelper.getStrackTrace(e));
		}
	    }
	}

    }

    /**
     * Add model file to module.
     * 
     * @param environment
     *            The environment for which the model file should be added.
     * 
     * @param model
     *            The model object.
     */
    public void addModelFile(String environment, Models models) {
	try {
	    marshallModelFile(environment, models, modelsDir);
	} catch (Exception e) {
	    // fail test
	    fail(StackTraceHelper.getStrackTrace(e));
	}

    }

    /**
     * Create singular model file on disk.
     * 
     * @param environment
     *            The environment for which the model file should be added.
     * @param model
     *            The model object.
     * @param modelsDirectory
     *            Directory where the model is written.
     */
    public void marshallModelFile(String environment, Models model, File modelsDirectory) {
	try {
	    // set file name
	    StringBuilder fileName;
	    fileName = new StringBuilder();
	    fileName.append(environment);
	    fileName.append(".xml");

	    // create models file object
	    File modelFile = new File(modelsDirectory, fileName.toString());

	    // marshall model file
	    jaxbMarshall(model, modelFile);

	    // log debug message
	    StringBuilder message = new StringBuilder();
	    message.append("Successfully wrote model file <");
	    message.append(fileName.toString());
	    message.append("> to disk.");
	    logger.debug(message.toString());
	} catch (Exception e) {
	    // fail test
	    fail(StackTraceHelper.getStrackTrace(e));
	}

    }

    /**
     * Add module.xml file to module.
     * 
     * @param module
     *            The module object.
     */
    public void addModuleFile(Module module) {
	try {
	    // set file name
	    StringBuilder fileName;
	    fileName = new StringBuilder();
	    fileName.append(this.moduleDir.getAbsolutePath());
	    fileName.append(File.separatorChar);
	    fileName.append("module.xml");

	    // marshall module file
	    jaxbMarshall(module, new File(fileName.toString()));

	    // log debug message
	    StringBuilder message = new StringBuilder();
	    message.append("Successfully added module file <");
	    message.append(fileName.toString());
	    message.append("> to module.");
	    logger.debug(message.toString());
	} catch (Exception e) {
	    // fail test
	    fail(StackTraceHelper.getStrackTrace(e));
	}
    }

    /**
     * Create a module object.
     * 
     * @return module object.
     */
    public Module createModuleObject() {
	com.alpha.pineapple.model.module.ObjectFactory moduleFactory;
	moduleFactory = new com.alpha.pineapple.model.module.ObjectFactory();
	Module module = moduleFactory.createModule();
	return module;
    }

    /**
     * Create a module object with variables.
     * 
     * @return module object.
     */
    public Module createModuleObjectWithEmptyVariables() {
	com.alpha.pineapple.model.module.ObjectFactory moduleFactory;
	moduleFactory = new com.alpha.pineapple.model.module.ObjectFactory();
	Module module = moduleFactory.createModule();
	module.setVariables(moduleFactory.createVariables());
	return module;
    }

    /**
     * Add variable to descriptor.
     * 
     * @param descriptor
     *            descriptor where variable is added.
     */
    public void addVariable(Module descriptor, String key, String value) {
	com.alpha.pineapple.model.module.ObjectFactory modelFactory;
	modelFactory = new com.alpha.pineapple.model.module.ObjectFactory();
	List<Variable> modelVariablesList = descriptor.getVariables().getVariable();
	com.alpha.pineapple.model.module.Variable modelVariable = modelFactory.createVariable();
	modelVariable.setKey(key);
	modelVariable.setValue(value);
	modelVariablesList.add(modelVariable);
    }

    /**
     * Create a model object.
     * 
     * @return model object.
     */
    public Models createModelObject() {
	com.alpha.pineapple.model.module.model.ObjectFactory modelFactory;
	modelFactory = new com.alpha.pineapple.model.module.model.ObjectFactory();
	Models model = modelFactory.createModels();
	return model;
    }

    /**
     * Create a model object with defined variables section.
     * 
     * @return model object.
     */
    public Models createModelObjectWithEmptyVariables() {
	com.alpha.pineapple.model.module.model.ObjectFactory modelFactory;
	modelFactory = new com.alpha.pineapple.model.module.model.ObjectFactory();
	Models model = modelFactory.createModels();
	model.setVariables(modelFactory.createVariables());
	return model;
    }

    /**
     * Add variable to model.
     * 
     * @param model
     *            model where variable is added.
     */
    public void addVariable(Models model, String key, String value) {
	com.alpha.pineapple.model.module.model.ObjectFactory modelFactory;
	modelFactory = new com.alpha.pineapple.model.module.model.ObjectFactory();
	List<com.alpha.pineapple.model.module.model.Variable> modelVariablesList = model.getVariables().getVariable();
	com.alpha.pineapple.model.module.model.Variable modelVariable = modelFactory.createVariable();
	modelVariable.setKey(key);
	modelVariable.setValue(value);
	modelVariablesList.add(modelVariable);
    }

    /**
     * Create a module model object with a single aggregated model.
     * 
     * @return module model object with a single aggregated model.
     */
    public Models createModelObjectWithSingleModel() {
	// create models
	com.alpha.pineapple.model.module.model.ObjectFactory modelFactory;
	modelFactory = new com.alpha.pineapple.model.module.model.ObjectFactory();
	Models model = modelFactory.createModels();

	// add aggregated model
	AggregatedModel aggregatedModel = modelFactory.createAggregatedModel();
	model.getModel().add(aggregatedModel);

	return model;
    }

    /**
     * Create a module model object with a single aggregated model.
     * 
     * @return module model object with a single aggregated model.
     */
    public Models createModelObjectWithMultipleModels() {
	// create models
	com.alpha.pineapple.model.module.model.ObjectFactory modelFactory;
	modelFactory = new com.alpha.pineapple.model.module.model.ObjectFactory();
	Models model = modelFactory.createModels();

	// add aggregated model
	AggregatedModel aggregatedModel = modelFactory.createAggregatedModel();
	model.getModel().add(aggregatedModel);
	AggregatedModel aggregatedModel2 = modelFactory.createAggregatedModel();
	model.getModel().add(aggregatedModel2);

	return model;
    }

    /**
     * Create module directory with an "module.xml" which refers to a single
     * environment. An empty model file is added for the specified environment.
     * 
     * @param rootDir
     *            Root directory for modules.
     * @param moduleName
     *            application name.
     * @param environmentName
     *            Environment name.
     */
    public void createModuleWithSingleEmptyModel(File rootDir, String moduleName, String environmentName) {
	// create module object
	Module module = createModuleObject();

	// create model object
	Models model = createModelObject();

	// create module
	createModuleDirectoryWithModelsDirectory(moduleName, rootDir);
	addModuleFile(module);
	addModelFile(environmentName, model);
    }

    /**
     * Create module directory with an "module.xml" which refers to a single
     * environment. An model file is added for the specified environment.
     * 
     * @param rootDir
     *            Root directory for modules.
     * @param moduleName
     *            application name.
     * @param environmentName
     *            Environment name.
     * @param targetResource
     *            Model target resource.
     */
    public void createModuleWithSingleModel(File rootDir, String moduleName, String environmentName,
	    String targetResource) {
	// create module object
	Module module = createModuleObject();

	// create model object
	Models model = createModelObjectWithModelWithTargetResourceAttribute(targetResource);

	// create module
	createModuleDirectoryWithModelsDirectory(moduleName, rootDir);
	addModuleFile(module);
	addModelFile(environmentName, model);
    }

    /**
     * Create module directory with no "module.xml" and no models directory and
     * model files.
     * 
     * @param rootDir
     *            Root directory for modules.
     * @param moduleName
     *            application name.
     */
    public void createModuleWithWithNoModuleXmlAndNoModelsDirectory(File rootDir, String moduleName) {

	// create module
	createModuleDirectory(moduleName, rootDir);
    }

    /**
     * Create module directory with an "module.xml" and no model files.
     * 
     * @param rootDir
     *            Root directory for modules.
     * @param moduleName
     *            application name.
     */
    public void createModuleWithNoModels(File rootDir, String moduleName) {

	// create module object
	Module module = createModuleObject();

	// create module
	createModuleDirectory(moduleName, rootDir);
	addModuleFile(module);
    }

    /**
     * Create a module model object, with a single aggregated model.
     * 
     * The aggregated model is decorated with the target-resource attribute.
     * 
     * @param value
     *            Value of the target-resource attribute.
     * 
     * @return module model object with a single aggregated model.
     */
    public Models createModelObjectWithModelWithTargetResourceAttribute(String value) {
	// create models
	com.alpha.pineapple.model.module.model.ObjectFactory modelFactory;
	modelFactory = new com.alpha.pineapple.model.module.model.ObjectFactory();
	Models model = modelFactory.createModels();

	// add aggregated model
	AggregatedModel aggregatedModel = modelFactory.createAggregatedModel();
	model.getModel().add(aggregatedModel);

	// add target-resource
	aggregatedModel.setTargetResource(value);

	return model;
    }

    /**
     * Create a module model object, with a single aggregated model.
     * 
     * The aggregated model contain a trigger definition. The aggregated model
     * is decorated with the target-resource attribute.
     * 
     * @param targetResource
     *            Value of the target-resource attribute.
     * @param triggerOnResult
     *            trigger on-result attribute.
     * @param triggerOnOperation
     *            trigger on-result operation.
     * 
     * @return module model object with a single aggregated model.
     */
    public Models createModelObjectWithModelWithTrigger(String targetResource, String triggerOnResult,
	    String triggerOnOperation) {
	
	Models model = createModelObjectWithModelWithTargetResourceAttribute(targetResource);

	// create model factory
	com.alpha.pineapple.model.module.model.ObjectFactory modelFactory;
	modelFactory = new com.alpha.pineapple.model.module.model.ObjectFactory();	
	
	// get aggregated model		
	AggregatedModel aggregatedModel = model.getModel().iterator().next();

	addTrigger(triggerOnResult, triggerOnOperation, aggregatedModel);

	return model;
    }

    /**
     * Add trigger to aggregated model.
     * 
     * @param triggerOnResult trigger result directive.
     * @param triggerOnOperation trigger operation directive
     * public @param aggregatedModel aggregated to which the trigger is added.
     */
    public void addTrigger(String triggerOnResult, String triggerOnOperation, AggregatedModel aggregatedModel) {

	// create model factory
	com.alpha.pineapple.model.module.model.ObjectFactory modelFactory;
	modelFactory = new com.alpha.pineapple.model.module.model.ObjectFactory();	
	
	
	// add trigger
	Trigger trigger = modelFactory.createTrigger();
	trigger.setEnvironment(RandomStringUtils.randomAlphabetic(16));
	trigger.setModule(RandomStringUtils.randomAlphabetic(16));
	trigger.setName(RandomStringUtils.randomAlphabetic(16));
	trigger.setOperation(RandomStringUtils.randomAlphabetic(16));
	trigger.setOnTargetOperation(triggerOnOperation);
	trigger.setOnResult(triggerOnResult);
	aggregatedModel.getTrigger().add(trigger);
    }

}
