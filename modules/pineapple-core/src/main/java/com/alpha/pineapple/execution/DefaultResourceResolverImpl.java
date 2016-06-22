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

import java.util.ArrayList;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.alpha.pineapple.resource.ResourceInfo;
import com.alpha.pineapple.resource.ResourceRepository;

/**
 * Default implementation of the {@linkplain ResourceResolver} interface.
 */
public class DefaultResourceResolverImpl implements ResourceResolver {

    /**
     * REgular expression prefix
     */
    private static final String REG_EX_ID = "regex:";

    /**
     * Empty result array.
     */
    final static String[] EMPTY_RESULT = new String[] {};

    /**
     * Resource repository.
     */
    @Resource
    ResourceRepository resourceRepository;

    @Override
    public String[] resolve(String targetResource, String environment) {
	if (targetResource == null)
	    return null;
	if (targetResource.isEmpty())
	    return EMPTY_RESULT;

	// trim
	targetResource = targetResource.trim();

	// resolve as list
	if (isList(targetResource)) {
	    return parseAsList(targetResource);
	}

	// resolve as regular expression
	if (isRegEx(targetResource)) {
	    return parseRegEx(targetResource, environment);
	}

	// return as simple string
	return new String[] { targetResource };
    }

    /**
     * Parse target resource as a regular expression.
     * 
     * @param targetResource
     *            target resource.
     * @param environment
     *            environment.
     * 
     * @return target resource parsed as a regular expression.
     */
    String[] parseRegEx(String targetResource, String environment) {

	// get regular expression and trim
	String regex = StringUtils.substringAfter(targetResource, REG_EX_ID);
	regex = regex.trim();

	// get all resources for current environment
	ResourceInfo[] resources = resourceRepository.getResources(environment);

	// match all resources in environment
	ArrayList<String> matches = new ArrayList<String>();
	for (ResourceInfo resourceInfo : resources) {
	    String resourceID = resourceInfo.getId();

	    // skip if empty
	    if (resourceID.isEmpty())
		continue;

	    // match
	    if (resourceID.matches(regex)) {
		matches.add(resourceID);
	    }
	}
	return (String[]) matches.toArray(new String[matches.size()]);
    }

    /**
     * Parse target resource as a list.
     * 
     * @param targetResource
     *            target resource.
     * 
     * @return target resource parsed as a list.
     */
    String[] parseAsList(String targetResource) {
	String nestedString = StringUtils.substringBetween(targetResource, "{", "}");

	// parse string
	String[] list = StringUtils.split(nestedString, ",");

	// trim
	int index = 0;
	for (String item : list) {
	    list[index] = item.trim();
	    index++;
	}
	return list;
    }

    /**
     * Return true if target resource defines a regular expression.
     * 
     * @param targetResource
     *            target resource to parse.
     * 
     * @return true if target resource defines a regular expression.
     */
    boolean isRegEx(String targetResource) {
	return (targetResource.startsWith(REG_EX_ID));
    }

    /**
     * Return true if target resource defines a list.
     * 
     * @param targetResource
     *            target resource to parse.
     * 
     * @return true if target resource defines a list.
     */
    boolean isList(String targetResource) {
	if (!targetResource.startsWith("{"))
	    return false;
	if (!targetResource.endsWith("}"))
	    return false;
	return true;
    }

}
