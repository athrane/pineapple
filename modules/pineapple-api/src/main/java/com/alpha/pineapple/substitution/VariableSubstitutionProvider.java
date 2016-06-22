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

package com.alpha.pineapple.substitution;

import java.io.File;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.session.Session;

/**
 * Variable substitution provider which can perform variable substitution on
 * artifacts used by a plugin.
 */
public interface VariableSubstitutionProvider {

    /**
     * Substitute variables in source string and return the processed string.
     * 
     * @param source
     *            source string whose content will be processed for variables.
     * @param result
     *            parent execution result for variable substitution process. The
     *            parent is not modified during the substitution process other
     *            than children are attached. The state and content of the child
     *            results are updated to reflect the outcome of the process.
     * 
     * @return string whose content have been processed for variables.
     */
    public String substitute(String source, ExecutionResult result) throws VariableSubstitutionException;

    /**
     * Substitute variables in source string and return the processed string.
     * 
     * @param source
     *            source string whose content will be processed for variables.
     * @param session
     *            plugin session.
     * @param result
     *            parent execution result for variable substitution process. The
     *            parent is not modified during the substitution process other
     *            than children are attached. The state and content of the child
     *            results are updated to reflect the outcome of the process.
     * 
     * @return string whose content have been processed for variables.
     */
    public String substitute(String source, Session session, ExecutionResult result)
	    throws VariableSubstitutionException;

    /**
     * Substitute variables in source file and store the result in a new
     * (temporary) file.
     * 
     * @param source
     *            source file whose content will be processed for variables.
     * @param result
     *            parent execution result for variable substitution process. The
     *            parent is not modified during the substitution process other
     *            than children are attached. The state and content of the child
     *            results are updated to reflect the outcome of the process.
     * 
     * @return new (temporary) file whose content have been processed for
     *         variables. The file is stored in the Pineapple temporary
     *         directory. The Pineapple temporary directory is resolved from the
     *         {@linkplain RuntimeDirectoryProvider}.
     */
    public File createSubstitutedFile(File source, ExecutionResult result) throws VariableSubstitutionException;

    /**
     * Substitute variables in source file and store the result in a new
     * (temporary) file. The properties of the resource accessed by the session
     * are used for resolution of variables.
     * 
     * @param source
     *            source file whose content will be processed for variables.
     * @param session
     *            plugin session.
     * @param result
     *            parent execution result for variable substitution process. The
     *            parent is not modified during the substitution process other
     *            than children are attached. The state and content of the child
     *            results are updated to reflect the outcome of the process.
     * 
     * @return new (temporary) file whose content have been processed for
     *         variables. The file is stored in the Pineapple temporary
     *         directory. The Pineapple temporary directory is resolved from the
     *         {@linkplain RuntimeDirectoryProvider}.
     */
    public File createSubstitutedFile(File source, Session session, ExecutionResult result)
	    throws VariableSubstitutionException;

}
