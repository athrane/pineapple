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

import com.alpha.pineapple.plugin.PluginOperation;

/**
 * Operation name generator.
 */
public class OperationNameGeneratorImpl implements BeanNameGenerator {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		try {
			// get fully qualified class name
			String name = definition.getBeanClassName();

			// get operation class
			Class<?> operationClass = Class.forName(name);

			// get operation annotation
			PluginOperation annotation = operationClass.getAnnotation(PluginOperation.class);

			// get operation id
			String operationId = annotation.value();

			// create operation name
			StringBuilder operationName = new StringBuilder();
			operationName.append(PluginOperation.class.getName());
			operationName.append(":");
			operationName.append(operationId);

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Generated operation name <");
				message.append(operationName.toString());
				message.append("> for bean <");
				message.append(definition);
				message.append(">.");
				logger.debug(message.toString());
			}

			return operationName.toString();

		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
