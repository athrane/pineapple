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

package com.alpha.pineapple.substitution.proxy;

import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import com.alpha.pineapple.substitution.VariableResolver;
import com.alpha.pineapple.substitution.variables.Variables;

/**
 * Implementation of the AOP {@linkplain MethodInterceptor} interface which
 * implements variable substitution for methods returning a string.
 */
public class VariableSubstitutionInterceptor implements MethodInterceptor {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Variable resolver.
     */
    @Resource
    VariableResolver resolver;

    /**
     * Mode proxy factory.
     */
    @Resource
    VariableSubstitutedProxyFactoryImpl factory;

    /**
     * Variables.
     */
    Variables variables;

    public void setVariables(Variables variables) {
	this.variables = variables;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

	// if return type is string intercept and process string
	if (isStringReturnType(invocation)) {
	    return processInterceptedString(invocation);
	}

	// process non string object by decorating it with a proxy
	Object returnValue = invocation.proceed();
	Object proxy = factory.decorateWithProxy(returnValue);

	return proxy;
    }

    /**
     * Process intercepted string.
     * 
     * @param invocation
     *            method invocation.
     * 
     * @return intercepted string which is processed for variable substitution.
     * 
     * @throws Throwable
     *             if processing fails.
     */
    String processInterceptedString(MethodInvocation invocation) throws Throwable {

	// invoke to get string
	Object returnValue = invocation.proceed();
	String strValue = returnValue.toString();

	// substitute variables
	String processedValue = resolver.resolve(variables, strValue);

	// log debug message
	if (logger.isDebugEnabled()) {
	    logger.debug("Processed string: " + strValue + " => " + processedValue);
	}

	return processedValue;
    }

    /**
     * Return true if method return type is string.
     * 
     * @param invocation
     *            method invocation.
     * 
     * @return true if method return type is string.
     */
    boolean isStringReturnType(MethodInvocation invocation) {

	// get method return type
	Method method = invocation.getMethod();
	Class<?> returnType = method.getReturnType();
	return String.class.isAssignableFrom(returnType);
    }

}
