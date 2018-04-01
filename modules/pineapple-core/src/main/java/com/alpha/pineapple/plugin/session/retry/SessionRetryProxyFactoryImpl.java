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

package com.alpha.pineapple.plugin.session.retry;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.retry.RetryListener;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;

import com.alpha.javautils.ReflectionHelper;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.session.retry.SessionRetryListener;
import com.alpha.pineapple.session.Session;

/**
 * Implementation of the {@linkplain SessionRetryProxyFactory} interface.
 */
public class SessionRetryProxyFactoryImpl implements SessionRetryProxyFactory {

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
	 * Factory which create retry template initialized with Spring dependencies.
	 */
	@Resource
	ObjectFactory<RetryTemplate> springRetryTemplateFactory;

	/**
	 * Factory which create retry listener initialized with Spring dependencies.
	 */
	@Resource
	ObjectFactory<SessionRetryListener> sessionRetryListenerFactory;

	/**
	 * Factory which create retry advice initialized with Spring dependencies.
	 */
	@Resource
	ObjectFactory<RetryOperationsInterceptor> springRetryOperationsInterceptorFactory;

	/**
	 * Create JDK proxy.
	 * 
	 * @param targetObject
	 *            target object to proxy.
	 * 
	 * @return JDK proxy.
	 */
	public Session decorateWithProxy(Session session, ExecutionResult result) {
		Validate.notNull(session, "session is undefined");
		Validate.notNull(result, "result is undefined");

		if (logger.isDebugEnabled()) {
			Object[] args = { session };
			String message = messageProvider.getMessage("srpf.create_proxy_info", args);
			logger.debug(message);
		}

		// create retry listener, template and advice
		SessionRetryListener retryListener = sessionRetryListenerFactory.getObject();
		retryListener.setResult(result);
		RetryListener[] listeners = { retryListener };
		RetryTemplate retryTemplate = springRetryTemplateFactory.getObject();
		retryTemplate.setListeners(listeners);
		RetryOperationsInterceptor retryInterceptor = springRetryOperationsInterceptorFactory.getObject();
		retryInterceptor.setRetryOperations(retryTemplate);

		// create factory for creation of JDK proxies (e.g. targetClass = false)
		ProxyFactory factory = new ProxyFactory(session);
		factory.addAdvice(retryInterceptor);
		factory.setProxyTargetClass(false);

		// advice will not be change after proxy creation - freeze it to let
		// Spring optimize calls
		factory.setFrozen(true);

		// add interfaces
		Class<?> targetObjectType = session.getClass();
		Class<?>[] interfaces = targetObjectType.getInterfaces();
		factory.setInterfaces(interfaces);

		// create proxy
		return (Session) factory.getProxy();
	}

}
