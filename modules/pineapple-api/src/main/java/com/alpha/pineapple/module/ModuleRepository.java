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

package com.alpha.pineapple.module;

/**
 * Module repository which contains all modules available to Pineapple.
 */
public interface ModuleRepository {

    /**
     * Initialize repository, which triggers repository to update its set of
     * modules.
     */
    void initialize();

    /**
     * Get array of module info objects for all modules in the repository.
     * 
     * @return collection of module info objects.
     */
    ModuleInfo[] getInfos();

    /**
     * Return true if module repository contains a module info with queried id.
     * 
     * @param id
     *            Id of the module to search for.
     * 
     * @return true if module repository contains a module with queried id.
     */
    boolean contains(String id);

    /**
     * Get module info with queried id. If the no module exists with the id then
     * a <code>ModuleNotFoundException</code> is thrown.
     * 
     * @param id
     *            ID of the module to search for.
     * 
     * @return module info with with queried id. If the no module exists with
     *         the id then a <code>ModuleNotFoundException</code> is thrown.
     * 
     * @throws ModuleNotFoundException
     *             if requested module info isn't defined in the repository.
     */
    ModuleInfo get(String id) throws ModuleNotFoundException;

    /**
     * Delete module. If the no module exists with the id then a
     * <code>ModuleNotFoundException</code> is thrown.
     * 
     * @param id
     *            ID of the module to delete.
     * 
     * @throws ModuleNotFoundException
     *             if module isn't defined in the repository.
     * @throws ModuleDeletionFailedException
     *             if module deletion fails.
     */
    void delete(String id) throws ModuleNotFoundException, ModuleDeletionFailedException;

    /**
     * Resolve module info with id and environment. If the no module exists with
     * the id and a module model for the requested environment then a
     * <code>ModuleNotFoundException</code> is thrown.
     * 
     * @param id
     *            Id of the module to search for.
     * @param environment
     *            Environment for which the module should contain a model.
     * 
     * @return module info. If the no module exists with the id and a module
     *         model for the requested environment then a
     *         <code>ModuleNotFoundException</code> is thrown.
     * 
     * @throws ModuleNotFoundException
     *             if requested module info isn't defined in the repository.
     * @throws ModelNotFoundException
     *             if requested module info doesn't contain requested
     *             environment.
     */
    ModuleInfo resolveModule(String id, String environment) throws ModuleNotFoundException;

    /**
     * Create new model. The created model is empty.
     * 
     * @param info
     *            Module info.
     * @param environment
     *            Environment for which the model is created.
     * 
     * @throws ModuleNotFoundException
     *             if requested module info isn't defined in the repository.
     * @throws ModelAlreadyException
     *             if module already contains model with in target environment.
     */
    void createModel(ModuleInfo info, String environment);

    /**
     * Save model. If model doesn't exist in module then it is added.
     * 
     * @param info
     *            Module info.
     * @param environment
     *            Environment for which the model is saved.
     * @param model
     *            The model content as string.
     */
    void saveModel(ModuleInfo info, String environment, String model);

    /**
     * Delete model. If model doesn't exist in module then it is added.
     * 
     * @param info
     *            Module info.
     * @param environment
     *            Environment for which the model is saved.
     * 
     * @throws ModuleNotFoundException
     *             if requested module info isn't defined in the repository.
     * @throws ModelNotFoundException
     *             if requested module info doesn't contain requested
     *             environment.
     * @throws IllegalArgumentException
     *             if module info is undefined.
     * @throws IllegalArgumentException
     *             if environment is undefined or empty.
     */
    void deleteModel(ModuleInfo info, String environment);

}
