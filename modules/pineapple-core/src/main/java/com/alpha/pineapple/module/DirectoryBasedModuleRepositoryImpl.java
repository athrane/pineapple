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

package com.alpha.pineapple.module;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;

/**
 * Implementation of the <code>DirectoryBasedModuleRepository</code> for file
 * based representation for storage of modules.
 */
public class DirectoryBasedModuleRepositoryImpl implements DirectoryBasedModuleRepository {

    /**
     * XML model suffix.
     */
    static final String MODEL_SUFFIX = ".xml";

    /**
     * Empty model.
     */
    static final String EMPTY_MODEL = "";

    /**
     * File encoding used to save the file.
     */
    static final String FILE_ENCODING = "UTF-8";

    /**
     * String array separator.
     */
    static final String CHAR_SEPARATOR = ",";

    /**
     * First array index.
     */
    static final int FIRST_INDEX = 0;

    /**
     * Defines file overwrite policy on save.
     */
    static final boolean APPEND_TO_FILE = false;

    /**
     * IO filter for locating directories.
     */
    static final IOFileFilter DIRECTORY_FILTER = FileFilterUtils.directoryFileFilter();

    /**
     * IO filter for locating models file objects.
     */
    static final IOFileFilter MODELS_FILTER = FileFilterUtils.nameFileFilter("models");

    /**
     * Combined IO filter for locating the "models" directory.
     */
    static final IOFileFilter MODELS_DIR_FILTER = FileFilterUtils.andFileFilter(DIRECTORY_FILTER, MODELS_FILTER);

    /**
     * IO filters for locating model files.
     */
    static final IOFileFilter MODEL_FILE_FILTER = FileFilterUtils.suffixFileFilter(MODEL_SUFFIX);

    /**
     * Length of ".xml" suffix.
     */
    static final int SUFFIX_LENGTH = 4;

    /**
     * First char sequence index.
     */
    static final int FIRST_CHAR_INDEX = 0;

    /**
     * Logger object
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Runtime directory provider.
     */
    @Resource
    RuntimeDirectoryProvider runtimeDirectoryProvider;

    /**
     * Modules container
     */
    SortedMap<String, ModuleInfo> modules;

    /**
     * Modules repository directory.
     */
    File repositoryDirectory;

    /**
     * DirectoryBasedModuleRepositoryImpl no-arg constructor which will create
     * uninitialized repository.
     */
    public DirectoryBasedModuleRepositoryImpl() {
	super();

	// create container
	modules = new TreeMap<String, ModuleInfo>();
    }

    public void initialize() {

	// resolve modules directory
	repositoryDirectory = runtimeDirectoryProvider.getModulesDirectory();

	// clear container
	modules.clear();

	// initialize repository
	initializeRepository();
    }

    public boolean contains(String id) {
	Validate.notNull(id, "id is undefined.");
	Validate.notEmpty(id, "id is empty.");
	return modules.containsKey(id);
    }

    public ModuleInfo get(String id) {
	if (!contains(id)) {

	    // create message and throw exception
	    Object[] args = { id };
	    String message = messageProvider.getMessage("dbmr.lookup_failed", args);
	    throw new ModuleNotFoundException(message);
	}

	return modules.get(id);
    }

    @Override
    public void delete(String id) throws ModuleNotFoundException {
	ModuleInfo info = get(id);

	// get module directory
	File directory = info.getDirectory();

	try {
	    // delete
	    FileUtils.deleteDirectory(directory);

	    // remove module
	    modules.remove(info.getId());

	} catch (IOException e) {
	    Object[] args = { id, e.getMessage() };
	    String message = messageProvider.getMessage("dbmr.module_deletion_failed", args);
	    throw new ModuleDeletionFailedException(message, e);
	}
    }

    public ModuleInfo[] getInfos() {
	Collection<ModuleInfo> values = modules.values();
	return values.toArray(new ModuleInfo[values.size()]);
    }

    public File getModuleRepositoryDirectory() {
	return this.repositoryDirectory;
    }

    public ModuleInfo resolveModule(String id, String environment)
	    throws ModuleNotFoundException, ModelNotFoundException {

	// fail resolution if module isn't defined in the repository
	if (!contains(id)) {

	    // create error message and throw exception
	    Object[] args = { id, runtimeDirectoryProvider.getModulesDirectory() };
	    String message = messageProvider.getMessage("dbmr.moduleresolution_module_failed", args);
	    throw new ModuleNotFoundException(message);
	}

	// get module info
	ModuleInfo moduleInfo = get(id);

	// fail resolution if module model isn't defined for environment
	if (!moduleInfo.containsModel(environment)) {

	    // create error message and throw exception
	    Object[] args = { environment, id };
	    String message = messageProvider.getMessage("dbmr.moduleresolution_envs_failed", args);
	    throw new ModelNotFoundException(message);
	}

	return moduleInfo;
    }

    @Override
    public void createModel(ModuleInfo info, String environment) {
	Validate.notNull(info, "info is undefined.");
	Validate.notNull(environment, "environment is undefined.");
	Validate.notEmpty(environment, "environment is empty.");

	// validate module - trigger expection if module doesn't exist
	ModuleInfo validatedInfo = get(info.getId());

	// validate model doesn't exist
	if (validatedInfo.containsModel(environment)) {
	    Object[] args = { environment, validatedInfo.getId() };
	    String message = messageProvider.getMessage("dbmr.model_creation_failed", args);
	    throw new ModelAlreadyExistsException(message);
	}

	// create model content
	String modelContent = new StringBuilder()
		.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
		.append("<mmd:models xmlns:mmd=\"http://pineapple.dev.java.net/ns/module_model_1_0\" >\n")
		.append("</mmd:models>").toString();

	// create model
	saveModel(validatedInfo, environment, modelContent);
    }

    @Override
    public void deleteModel(ModuleInfo info, String environment) {
	Validate.notNull(info, "info is undefined.");
	Validate.notNull(environment, "environment is undefined.");
	Validate.notEmpty(environment, "environment is empty.");

	// validate module and model - trigger expection if module and model
	// doesn't exist
	ModuleInfo validatedInfo = resolveModule(info.getId(), environment);

	// create model file
	File modelFile = createModelFile(validatedInfo, environment);

	try {
	    // delete
	    modelFile.delete();

	} catch (Exception e) {
	    Object[] args = { environment, validatedInfo.getId(), e.getMessage() };
	    String message = messageProvider.getMessage("dbmr.model_deletion_failed", args);
	    throw new ModuleDeletionFailedException(message, e);
	}

	// remove module
	validatedInfo.deleteEnvironment(environment);
    }

    @Override
    public void saveModel(ModuleInfo info, String environment, String model) throws ModuleSaveFailedException {

	// if mode is null then save it as empty file
	if (model == null)
	    model = EMPTY_MODEL;

	try {
	    // create model file
	    File modelFile = createModelFile(info, environment);

	    // save the model
	    FileUtils.writeStringToFile(modelFile, model, FILE_ENCODING, APPEND_TO_FILE);

	    // add model to info
	    info.addEnvironment(environment);

	    // log debug message
	    if (logger.isDebugEnabled()) {
		Object[] args = { environment, info.getId() };
		logger.debug(messageProvider.getMessage("dbmr.save_model_info", args));
		return;
	    }

	} catch (IOException e) {
	    Object[] args = { environment, info.getId() };
	    String message = messageProvider.getMessage("dbmr.model_save_failed", args);
	    throw new ModuleSaveFailedException(message);
	}
    }

    /**
     * Initialize repository with directory.
     */
    void initializeRepository() {

	// create directory if it doesn't exist
	if (!this.repositoryDirectory.exists()) {

	    if (logger.isDebugEnabled()) {
		Object[] args = { this.repositoryDirectory };
		String message = messageProvider.getMessage("dbmr.initialize_dirdoesntexist_info", args);
		logger.debug(message);
	    }

	    // exit since the directory doesn't exist
	    return;
	}

	// initialize modules in modules directory
	initializeModules(repositoryDirectory);

	// log debug message
	if (logger.isDebugEnabled()) {
	    Object[] args = { repositoryDirectory, modules.size() };
	    String message = messageProvider.getMessage("dbmr.initialize_success", args);
	    logger.debug(message);
	}

    }

    /**
     * Initialize modules in modules directory.
     * 
     * @param repositoryDirectory
     *            modules directory.
     */
    void initializeModules(File repositoryDirectory) {

	// get sub directories in directory
	File[] directories = repositoryDirectory.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);

	for (File moduleDirectory : directories) {

	    // if directory is a valid module directory, then add it to the list
	    if (isValidModuleDir(moduleDirectory)) {

		// get directory name
		String name = moduleDirectory.getName();

		// is module descriptor defined
		boolean isDescriptorDefined = isModuleDescriptorDefined(moduleDirectory);

		// get environments
		String[] environments = getEvironments(moduleDirectory);

		// create new module
		ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(name, environments, isDescriptorDefined,
			moduleDirectory);

		// store module
		modules.put(name, moduleInfo);

		// log debug message
		if (logger.isDebugEnabled()) {
		    Object[] args = { moduleInfo.getId(), moduleDirectory,
			    StringUtils.join(environments, CHAR_SEPARATOR) };
		    String message = messageProvider.getMessage("dbmr.initialize_module_success", args);
		    logger.debug(message);
		}

	    }
	}
    }

    /**
     * Return list of environments for which valid module models exist for the
     * module.
     * 
     * @param moduleDirectory
     *            Directory which contains the module.
     */
    String[] getEvironments(File moduleDirectory) {

	// create environments list
	ArrayList<String> environments = new ArrayList<String>();

	// get models directory
	File modelsDirectory = getModelsDirectory(moduleDirectory);

	// exit if models directory isn't defined
	if (modelsDirectory == null)
	    return convertEnvironmentListToArray(environments);
	;

	// get model files from models directory
	File[] modelFiles = modelsDirectory.listFiles((FileFilter) MODEL_FILE_FILTER);

	// exit if no model files is defined
	if (modelFiles.length == 0)
	    convertEnvironmentListToArray(environments);
	;

	// iterate over the model files
	for (File fileObject : modelFiles) {

	    // store module model name
	    String name = createModelName(fileObject);
	    environments.add(name);
	}

	// convert to array
	return convertEnvironmentListToArray(environments);
    }

    /**
     * Get models directory from a module.
     * 
     * @param moduleDirectory
     *            module directory.
     * 
     * @return models directory from a module. Return null if directory doesn't
     *         exists.
     */
    File getModelsDirectory(File moduleDirectory) {

	// get models sub directory
	File[] directories = moduleDirectory.listFiles((FileFilter) MODELS_DIR_FILTER);

	// exit if models directory isn't defined
	if (directories.length == 0)
	    return null;
	if (directories[FIRST_INDEX] == null)
	    return null;

	// get models directory
	File modelsDirectory = directories[FIRST_INDEX];
	return modelsDirectory;
    }

    /**
     * Create model name which is the file name without the ".xml" suffix.
     * 
     * @param fileObject
     *            File object.
     * 
     * @return model name which is the file name without the ".xml" suffix.
     */
    String createModelName(File fileObject) {
	int endIndex = fileObject.getName().length() - SUFFIX_LENGTH;
	return fileObject.getName().substring(FIRST_CHAR_INDEX, endIndex);
    }

    /**
     * Create model file name for saving a model.
     * 
     * @param info
     *            module info.
     * @param environment
     *            Model name.
     * 
     * @return File object defining the absolute path to the model file.
     * 
     */
    File createModelFile(ModuleInfo info, String environment) {
	StringBuilder fileName = new StringBuilder();
	fileName.append(environment);
	fileName.append(MODEL_SUFFIX);

	File modelsDirectory = getModelsDirectory(info.getDirectory());

	// create file
	return new File(modelsDirectory, fileName.toString());
    }

    /**
     * Converts environment list to array
     * 
     * @param list
     *            List of environments.
     * 
     * @return array which contain environments.
     */
    String[] convertEnvironmentListToArray(ArrayList<String> list) {
	return list.toArray(new String[list.size()]);
    }

    /**
     * Validate whether a file object represents a valid module directory.
     * 
     * @param fileObject
     *            the file object which should be validated.
     * 
     * @return true if the fileObject represents a valid module directory.
     */
    boolean isValidModuleDir(File fileObject) {

	// validate FO is a directory
	if (!fileObject.isDirectory())
	    return false;

	// get files + sub directories in directory
	// File[] fileObjectsInDir = fileObject.listFiles();

	// validate modules directory contains "module.xml" file

	// validate modules directory contains "models" directory

	return true;
    }

    /**
     * Returns true if module descriptor "module.xml" is defined.
     * 
     * @param moduleDirectory
     *            Module directory.
     * 
     * @return true if module descriptor "module.xml" is defined.
     */
    boolean isModuleDescriptorDefined(File moduleDirectory) {
	File moduleDescriptor = new File(moduleDirectory, "module.xml");
	return (moduleDescriptor.exists());
    }

}
