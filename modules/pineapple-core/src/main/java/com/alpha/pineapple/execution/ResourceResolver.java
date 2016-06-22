/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.execution;

/**
 * Interface for resolution of target resource(s) for execution of an model.
 */
public interface ResourceResolver {

    /**
     * Return list of resolved target resources. Resolved targets are not
     * guaranteed to be valid.
     * 
     * Supports the syntax for lists: {cn1, cn2}
     * 
     * Supports the syntax for regular expressions: regex:cn*
     * 
     * @param targetResource
     *            target resource from model.
     * @param environment
     *            environment where resources are matched if target resource
     *            defines a regular expression
     * 
     * @return list of resolved target resources.
     */
    String[] resolve(String targetResource, String environment);

}
