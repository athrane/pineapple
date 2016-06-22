/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.plugin.repository;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.Plugin;
import com.alpha.pineapple.plugin.PluginInitializationFailedException;

/**
 * Implementation of the {@link PluginCandidateScanner} interface.
 */
public class PluginCandidateScannerImpl implements PluginCandidateScanner {

    /**
     * Disable default filters during Spring component scan.
     */
    static final boolean DISABLE_DEFAULT_FILTERS = false;

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Plugin name generator
     */
    @Resource
    BeanNameGenerator pluginNameGenerator;

    public ApplicationContext scanForPlugins(String[] pluginIds) throws PluginInitializationFailedException {

	// validate parameters
	Validate.notNull(pluginIds, "pluginIds is undefined.");

	try {
	    // get class loader
	    ClassLoader classLoader = this.getClass().getClassLoader();

	    // iterate over the plugin id's
	    for (String pluginId : pluginIds) {
		// log debug message
		if (logger.isDebugEnabled()) {
		    Object[] args = { pluginId };
		    String message = messageProvider.getMessage("pcs.plugin_scan_start", args);
		    logger.debug(message);
		}

		// replace dots .. with forward slashes
		String preparedPluginId = StringUtils.replaceChars(pluginId, '.', '/');

		// resolve plugin id with class loader
		Enumeration<URL> classLoaderResult = classLoader.getResources(preparedPluginId);

		// log debug message
		if (logger.isDebugEnabled()) {
		    Object[] args = { ReflectionToStringBuilder.toString(classLoaderResult) };
		    String message = messageProvider.getMessage("pcs.plugin_scan_classloader", args);
		    logger.debug(message);
		}
	    }

	    // create context to store scan result in
	    GenericApplicationContext context = new GenericApplicationContext();
	    context.setClassLoader(classLoader);

	    // skip scanning if no plugins are defined
	    if (pluginIds.length == 0)
		return context;

	    // create component scanner
	    ClassPathBeanDefinitionScanner scanner;
	    scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) context, DISABLE_DEFAULT_FILTERS);

	    // setup plugin class scanning
	    scanner.addIncludeFilter(new AnnotationTypeFilter(Plugin.class));
	    scanner.setBeanNameGenerator(pluginNameGenerator);

	    // scan for plugins
	    scanner.scan(pluginIds);

	    // log debug message
	    if (logger.isDebugEnabled()) {
		String message = messageProvider.getMessage("pcs.plugin_scan_completed");
		logger.debug(message);
	    }

	    // refresh to support usage in Spring 4.x (see PINEAPPLE-727 for
	    // more info)
	    context.refresh();

	    return context;

	} catch (IOException e) {
	    // throw exception if interfaces couldn't be resolved
	    Object[] args = { e.toString() };
	    String message = messageProvider.getMessage("pcs.plugin_scan_error", args);
	    throw new PluginInitializationFailedException(message, e);
	}
    }

}
