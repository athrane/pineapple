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

import javax.annotation.Resource;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.objenesis.ObjenesisHelper;
import org.springframework.aop.framework.ProxyFactory;

import com.alpha.javautils.ReflectionHelper;
import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.substitution.VariableResolver;
import com.alpha.pineapple.substitution.variables.Variables;

/**
 * Implementation of the {@linkplain VariableSubstitutedProxyFactory} interface.
 */
public class VariableSubstitutedProxyFactoryImpl implements VariableSubstitutedProxyFactory {

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
	 * Reflection helper.
	 */
	@Resource
	ReflectionHelper reflectionHelper;

	/**
	 * Variable resolver.
	 */
	@Resource
	VariableResolver resolver;

	/**
	 * Substitution AOP advice.
	 */
	@Resource
	VariableSubstitutionInterceptor variableSubstitutionInterceptor;

	/**
	 * CGLIB call back filter types.
	 */
	final Class<?>[] CALLBACK_TYPES = new Class<?>[] { CglibVariableSubstitutionInterceptorImpl.class };

	/**
	 * Variables.
	 */
	Variables variables;

	@Override
	public void initialize(Variables variables) {
		Validate.notNull(variables, "variables is undefined.");

		this.variables = variables;
		variableSubstitutionInterceptor.setVariables(variables);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T decorateWithProxy(T targetObject) {
		validateInitialization();

		// if target object is null, immutable or primitive then skip proxying
		// it
		if (targetObject == null)
			return null;

		// process string for substitution
		if (reflectionHelper.isStringType(targetObject)) {
			String processedValue = resolver.resolve(variables, (String) targetObject);

			// log debug message
			if (logger.isDebugEnabled()) {
				logger.debug("Processed string: " + targetObject + " => " + processedValue);
			}

			return (T) processedValue;
		}

		if (reflectionHelper.isImmutable(targetObject))
			return targetObject;
		if (reflectionHelper.isPrimitiveReturnType(targetObject))
			return targetObject;

		try {

			if (reflectionHelper.isPrivateClass(targetObject)) {
				return createJdkProxy(targetObject);
			}

			return createCglibProxy(targetObject);

		} catch (Exception e) {

			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("vspf.decorate_error", args);
			logger.error(message);
			logger.error(StackTraceHelper.getStrackTrace(e));

			// abort decoration and return original object
			return (T) targetObject;
		}
	}

	/**
	 * Create proxy for class.
	 * 
	 * Can handle classes with no no-arg constructor constructor (e.g. only
	 * constructor which requires arguments) since class creation is bypassing
	 * constructor through the usage of Objenesis.
	 * 
	 * Example of a class with no no-arg constructor is JaxbElement, which have
	 * multi-arg constructors.
	 * 
	 * @param targetObject
	 *            target object to proxy.
	 * 
	 * @return CGLIB proxy.
	 * 
	 */
	@SuppressWarnings("unchecked")
	<T> T createCglibProxy(T targetObject) {
		if (logger.isDebugEnabled()) {
			Object[] args = { targetObject };
			String message = messageProvider.getMessage("vspf.create_cglib_proxy_info", args);
			logger.debug(message);
		}

		final Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(targetObject.getClass());
		enhancer.setCallbackTypes(CALLBACK_TYPES);
		final Class<?> proxyClass = enhancer.createClass();
		final Callback[] callBacks = new Callback[] {
				new CglibVariableSubstitutionInterceptorImpl(this, variables, resolver, targetObject) };
		Enhancer.registerCallbacks(proxyClass, callBacks);
		return (T) ObjenesisHelper.newInstance(proxyClass);
	}

	/**
	 * Create JDK proxy.
	 * 
	 * @param targetObject
	 *            target object to proxy.
	 * 
	 * @return JDK proxy.
	 */
	@SuppressWarnings("unchecked")
	<T> T createJdkProxy(T targetObject) {
		if (logger.isDebugEnabled()) {
			Object[] args = { targetObject };
			String message = messageProvider.getMessage("vspf.create_jdk_proxy_info", args);
			logger.debug(message);
		}

		// create factory for creation of JDK proxies (e.g. targetClass = false)
		ProxyFactory factory = new ProxyFactory(targetObject);
		factory.addAdvice(variableSubstitutionInterceptor);
		factory.setProxyTargetClass(false);

		// advice will not be change after proxy creation - freeze it to let
		// Spring optimize calls
		factory.setFrozen(true);

		// add interfaces
		Class<?> targetObjectType = targetObject.getClass();
		Class<?>[] interfaces = targetObjectType.getInterfaces();
		factory.setInterfaces(interfaces);

		// create proxy
		return (T) factory.getProxy();
	}

	/**
	 * Validate factory is initialized.
	 */
	void validateInitialization() {
		if (variables != null)
			return;

		// handle uninitialized case
		String message = messageProvider.getMessage("vspf.not_initialized_error");
		throw new IllegalStateException(message);
	}

}
