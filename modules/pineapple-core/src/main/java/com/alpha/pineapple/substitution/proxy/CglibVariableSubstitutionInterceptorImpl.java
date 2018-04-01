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

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.log4j.Logger;

import com.alpha.pineapple.substitution.VariableResolver;
import com.alpha.pineapple.substitution.variables.Variables;

/**
 * Implementation of the CGLIB {@linkplain MethodInterceptor} interface which
 * implements variable substitution for methods returning a string.
 */
public class CglibVariableSubstitutionInterceptorImpl implements MethodInterceptor {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Proxied object.
	 */
	Object proxiedObject;

	/**
	 * Factory.
	 */
	VariableSubstitutedProxyFactory factory;

	/**
	 * Variable resolver.
	 */
	VariableResolver resolver;

	/**
	 * Variables.
	 */
	Variables variables;

	/**
	 * ProxyInterceptorImpl constructor.
	 * 
	 * @param proxiedObject
	 *            object which proxied.
	 */
	CglibVariableSubstitutionInterceptorImpl(VariableSubstitutedProxyFactory factory, Variables variables,
			VariableResolver resolver, Object proxiedObject) {
		this.factory = factory;
		this.proxiedObject = proxiedObject;
		this.variables = variables;
		this.resolver = resolver;
	}

	@Override
	public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

		// invoke method on proxied object
		Object returnValue = method.invoke(proxiedObject, args);

		// exit if value is null
		if (returnValue == null)
			return null;

		// process string for substitution
		if (isStringType(returnValue)) {
			String strValue = returnValue.toString();

			// substitute variables
			String processedValue = resolver.resolve(variables, strValue);

			// log debug message
			if (logger.isDebugEnabled()) {
				logger.debug("Processed string: " + strValue + " => " + processedValue);
			}

			return processedValue;
		}

		// process non string object by decorating it with a proxy
		return factory.decorateWithProxy(returnValue);
	}

	/**
	 * Return true if target object type is string.
	 * 
	 * @param targetObject
	 *            target object.
	 * 
	 * @return true if target object type is string.
	 */
	public boolean isStringType(Object targetObject) {
		Class<?> returnType = targetObject.getClass();
		return String.class.isAssignableFrom(returnType);
	}

}