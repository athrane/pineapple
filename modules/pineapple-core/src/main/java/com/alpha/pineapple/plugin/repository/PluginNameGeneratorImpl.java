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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

/**
 * Plugin name generator
 */
public class PluginNameGeneratorImpl implements BeanNameGenerator {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {

	// get fully qualified class name
	String name = definition.getBeanClassName();

	// get package name
	String packageName = name.substring(0, name.lastIndexOf('.'));

	// create plugin name
	StringBuilder pluginName = new StringBuilder();
	pluginName.append("plugin:");
	pluginName.append(packageName);

	// log debug message
	if (logger.isDebugEnabled()) {
	    StringBuilder message = new StringBuilder();
	    message.append("Generated plugin name <");
	    message.append(pluginName.toString());
	    message.append(">.");
	    logger.debug(message.toString());
	}

	return pluginName.toString();
    }

}
