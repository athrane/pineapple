/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2016 Allan Thrane Andersen..
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

package com.alpha.pineapple.web.model;

import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.event.registry.Registration;
import reactor.event.registry.Registry;
import reactor.function.Consumer;

import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.execution.ResultListener;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;

public class DebugInfoModel {

	/**
	 * Runtime directory resolver.
	 */
	@Resource
	RuntimeDirectoryProvider runtimeDirectoryResolver;

	/**
	 * Pineapple core component.
	 */
	@Resource
	PineappleCore coreComponent;

	/**
	 * Web application reactor.
	 */
	@Resource
	Reactor webAppReactor;

	public Map<String, String> getDebugInfoModel() {

		// create map
		TreeMap<String, String> debugInfo = new TreeMap<String, String>();

		// add content
		Session zkSession = Sessions.getCurrent();
		debugInfo.put("zkSession.getLocalAddr()", zkSession.getLocalAddr());
		debugInfo.put("zkSession.getLocalName()", zkSession.getLocalName());
		debugInfo.put("zkSession.getRemoteAddr()", zkSession.getRemoteAddr());
		debugInfo.put("zkSession.getRemoteHost()", zkSession.getRemoteHost());
		debugInfo.put("zkSession.getServerName()", zkSession.getServerName());
		debugInfo.put("zkSession.getWebApp().getAppName()", zkSession.getWebApp().getAppName());

		HttpSession httpSession = (HttpSession) zkSession.getNativeSession();
		debugInfo.put("httpSession.getId()", httpSession.getId());
		debugInfo.put("httpSession.getCreationTime()", Long.toString(httpSession.getCreationTime()));
		debugInfo.put("httpSession.getLastAccessedTime()", new Date(httpSession.getLastAccessedTime()).toString());

		ServletContext servletContext = httpSession.getServletContext();
		debugInfo.put("servletContext.getServerInfo()", servletContext.getServerInfo());
		debugInfo.put("servletContext.getServletContextName()", servletContext.getServletContextName());

		Execution zkExecution = Executions.getCurrent();
		for (String name : zkExecution.getHeaderNames()) {
			String value = zkExecution.getHeader(name);
			debugInfo.put("zkExecution.headers." + name, value);
		}

		Properties systemPropeties = System.getProperties();
		for (Object key : systemPropeties.keySet()) {
			debugInfo.put("system.properties." + key, systemPropeties.getProperty(key.toString()));
		}

		debugInfo.put("pineapple.runtimeDirectoryResolver.getRuntimeDirectory()",
				runtimeDirectoryResolver.getHomeDirectory().toString());
		debugInfo.put("pineapple.runtimeDirectoryResolver.getConfigurationDirectory()",
				runtimeDirectoryResolver.getConfigurationDirectory().toString());
		debugInfo.put("pineapple.runtimeDirectoryResolver.getReportsDirectory()",
				runtimeDirectoryResolver.getReportsDirectory().toString());
		debugInfo.put("pineapple.runtimeDirectoryResolver.getModulesDirectory()",
				runtimeDirectoryResolver.getModulesDirectory().toString());
		debugInfo.put("pineapple.runtimeDirectoryResolver.getTempDirectory()()",
				runtimeDirectoryResolver.getTempDirectory().toString());

		// list consumers
		Registry<Consumer<? extends Event<?>>> registry = webAppReactor.getConsumerRegistry();
		int consumerIdNumber = 0;
		for (Registration<? extends Consumer<? extends Event<?>>> registration : registry) {
			StringBuilder key = new StringBuilder().append("Registered Reactor consumer #")
					.append(Integer.toString(consumerIdNumber));
			debugInfo.put(key.toString(), ReflectionToStringBuilder.toString(registration));
			consumerIdNumber++;
		}

		// list listeners
		ResultListener[] listeners = coreComponent.getListeners();
		int listenerIdNumber = 0;
		for (ResultListener listener : listeners) {
			StringBuilder key = new StringBuilder().append("Registered listener #")
					.append(Integer.toString(listenerIdNumber));
			debugInfo.put(key.toString(), ReflectionToStringBuilder.toString(listener));
			listenerIdNumber++;
		}

		return debugInfo;
	}

}
