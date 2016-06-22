/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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

package com.alpha.pineapple.docker.model.jaxb.reflection;

import static com.alpha.javautils.reflection.MethodUtils.classIsDeclaredInPackage;
import static com.alpha.javautils.reflection.MethodUtils.isMethodWithNoParameters;
import static com.alpha.pineapple.docker.DockerConstants.MODEL_PACKAGE;

import java.lang.reflect.Method;

import com.alpha.javautils.reflection.GetterMethodMatcher;

/**
 * Implementation of the {@linkplain GetterMethodMatcher} interface which can
 * match getter methods from JAXB.
 * 
 * A method is matched if it belongs to the package
 * <code>com.alpha.pineapple.docker.model.rest</code> (including sub packages)
 * and starts with 'get'.
 */
public class DockerJaxbGetterMethodMatcherImpl implements GetterMethodMatcher {

    /**
     * Start index for get-XX substrings.
     */
    static final int GET_INDEX = 3;

    /**
     * Start index for is-XX substrings.
     */
    static final int IS_INDEX = 2;

    public boolean isMatch(Method method) {
	// get declaring class
	Class<?> declaringClass = method.getDeclaringClass();

	// return unsuccessful result if method doesn't reside in model package
	if (!classIsDeclaredInPackage(declaringClass, MODEL_PACKAGE))
	    return false;

	// get method name
	String methodName = method.getName();

	// return unsuccessful result if method take arguments
	if (!isMethodWithNoParameters(method))
	    return false;

	// return if method start with 'getXX'
	if (methodName.startsWith("get"))
	    return true;

	// return if method start with 'isXX'
	return (methodName.startsWith("is"));
    }

    public String resolveAttributeFromGetterMethod(Method method) {

	// get method name
	String methodName = method.getName();

	// strip 'get' from method
	if (methodName.startsWith("get")) {
	    String strippedName = methodName.substring(GET_INDEX);
	    return strippedName;
	}

	// strip 'is' from method
	String strippedName = methodName.substring(IS_INDEX);
	return strippedName;
    }

}
