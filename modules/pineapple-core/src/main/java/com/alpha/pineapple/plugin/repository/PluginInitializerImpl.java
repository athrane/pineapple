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

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.alpha.pineapple.admin.AdministrationProvider;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.Plugin;
import com.alpha.pineapple.plugin.PluginInitializationFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.PluginSession;
import com.alpha.pineapple.substitution.VariableSubstitutionProvider;

public class PluginInitializerImpl implements PluginInitializer {

	/**
	 * Execution info provider bean id.
	 */
	static final String EXECUTION_INFO_PROVIDER_BEANID = "coreExecutionInfoProvider";

	/**
	 * Runtime directory provider bean id.
	 */
	static final String RUNTIME_DIRECTORY_PROVIDER_BEANID = "coreRuntimeDirectoryProvider";

	/**
	 * Administration provider bean id.
	 */
	static final String ADMINISTRATION_PROVIDER_BEANID = "coreAdministrationProvider";

	/**
	 * Variable Substitution provider bean id.
	 */
	static final String VARIABLE_SUBSTITUTION_PROVIDER_BEANID = "coreVariableSubstitutionProvider";

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
	 * Operation name generator.
	 */
	@Resource
	BeanNameGenerator operationNameGenerator;

	/**
	 * Session name generator.
	 */
	@Resource
	BeanNameGenerator sessionNameGenerator;

	/**
	 * Execution info provider.
	 */
	@Resource
	ExecutionInfoProvider executionInfoProvider;

	/**
	 * Runtime directory provider.
	 */
	@Resource
	RuntimeDirectoryProvider runtimeDirectoryProvider;

	/**
	 * Administration API provider.
	 */
	@Resource
	AdministrationProvider administrationProvider;

	/**
	 * Variable substitution provider.
	 */
	@Resource
	VariableSubstitutionProvider variableSubstitutionProvider;

	public PluginInfo initializePlugin(Object pluginClass) throws PluginInitializationFailedException {

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { pluginClass, pluginClass.getClass().getAnnotation(Plugin.class) };
			String message = messageProvider.getMessage("pi.initialize_start", args);
			logger.debug(message);
		}

		// validate plugin annotation
		validateAnnotationIsDefined(pluginClass);

		// create plugin info object
		PluginInfo pluginInfo = new PluginInfoImpl();

		// set plugin id
		pluginInfo.setPluginId(pluginClass.getClass().getPackage().getName());

		// create plugin application context
		GenericApplicationContext context = new GenericApplicationContext();

		// add providers to plugin context
		addProvidersToContext(context);

		// setup input marshalling
		initializeInputMarshalling(context, pluginInfo, pluginClass);

		// scan for components
		addOperationsToContext(pluginInfo.getPluginId(), context);
		addSessionToContext(pluginInfo.getPluginId(), context);

		// setup session handling
		intializeSessionHandling(pluginInfo, context);

		// refresh to support usage in Spring 4.x (see PINEAPPLE-727+773 for
		// more info)
		context.refresh();

		// log debug message
		if (logger.isDebugEnabled()) {
			for (String beanDefName : context.getBeanDefinitionNames()) {
				Object[] args = { beanDefName };
				String message = messageProvider.getMessage("pi.inputmarshalling_info", args);
				logger.debug(message);
			}
		}

		// store context
		pluginInfo.setContext(context);

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { pluginClass };
			String message = messageProvider.getMessage("pi.initialize_success", args);
			logger.debug(message);
		}

		return pluginInfo;
	}

	/**
	 * Validate Plugin annotation is defined on the plugin class. Otherwise an
	 * exception is thrown.
	 * 
	 * @param pluginClass
	 *            Plugin to validate
	 * 
	 * @throws PluginInitializationFailedException
	 *             If annotation isn't defined.
	 */
	void validateAnnotationIsDefined(Object pluginClass) throws PluginInitializationFailedException {

		// get plugin annotation info
		Plugin annotation = pluginClass.getClass().getAnnotation(Plugin.class);

		// throw exception if annotation isn't defined
		if (annotation == null) {
			Object[] args = { pluginClass };
			String message = messageProvider.getMessage("pi.validateannotation_failed", args);
			throw new PluginInitializationFailedException(message);
		}
	}

	/**
	 * initialize session handling.
	 *
	 * @param pluginInfo
	 *            Plugin meta data.
	 * @param context
	 *            Plugin application context.
	 */
	void intializeSessionHandling(PluginInfo pluginInfo, ApplicationContext context) {
		// get session id
		String sessionId = PluginSession.class.getName();

		// store session id
		pluginInfo.setSessionId(sessionId);

		// session is defined in context then activate session handling
		boolean isSessionDefined = context.containsBean(sessionId);

		// validate that session implements the Session interface
		// TODO: implement

		// set state
		pluginInfo.setSessionHandlingEnabled(isSessionDefined);
	}

	/**
	 * Initialize input marshalling from plugin class. The method updates the plugin
	 * info object with info whether marshalling is enabled or not.
	 * 
	 * Also the plugin application context file is loaded if it exists. If it
	 * doesn't exist then an in memory version is created.
	 * 
	 * @param pluginInfo
	 *            Plugin meta data.
	 * @param pluginClass
	 *            Plugin class.
	 * 
	 * @return plugin application context.
	 */
	void initializeInputMarshalling(GenericApplicationContext context, PluginInfo pluginInfo, Object pluginClass) {
		// get plugin annotation info
		Plugin annotation = pluginClass.getClass().getAnnotation(Plugin.class);

		pluginInfo.setUnmarshallerId(annotation.unmarshaller());
		pluginInfo.setConfigFileName(annotation.configFile());

		// create class path resource
		ClassPathResource classPathResource = new ClassPathResource(pluginInfo.getConfigFileName());

		// if plugin configuration file exits then load it
		if (classPathResource.exists()) {

			// log debug message
			if (logger.isDebugEnabled()) {
				Object[] args = { pluginInfo.getConfigFileName() };
				String message = messageProvider.getMessage("pi.inputmarshalling_start", args);
				logger.debug(message);
			}

			// load configuration file
			XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context);
			xmlReader.loadBeanDefinitions(classPathResource);

			// log debug message
			if (logger.isDebugEnabled()) {
				Object[] args = { pluginInfo.getConfigFileName() };
				String message = messageProvider.getMessage("pi.inputmarshalling_completed", args);
				logger.debug(message);
			}

			// validate that context contains unmarshaller id
			// TODO: throw exception....

			// enable input marshalling
			pluginInfo.setInputMarshallingEnabled(true);
			return;
		} else {
			// disable input marshalling
			pluginInfo.setInputMarshallingEnabled(false);
		}

	}

	/**
	 * TODO: document
	 * 
	 * @param pluginId
	 * @param context
	 */
	void addSessionToContext(String pluginId, ApplicationContext context) {
		// create component scanner
		ClassPathBeanDefinitionScanner scanner;
		scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) context, DISABLE_DEFAULT_FILTERS);

		// limit the scan to the plugin package
		String basePackage = pluginId;

		// setup session scanning
		scanner.addIncludeFilter(new AnnotationTypeFilter(PluginSession.class));
		scanner.setBeanNameGenerator(sessionNameGenerator);

		// scan for session
		scanner.scan(basePackage);
	}

	/**
	 * TODO: document
	 * 
	 * @param pluginId
	 * @param context
	 */
	void addOperationsToContext(String pluginId, ApplicationContext context) {
		// create component scanner
		ClassPathBeanDefinitionScanner scanner;
		scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) context, DISABLE_DEFAULT_FILTERS);

		// limit the scan to the plugin package
		String basePackage = pluginId;

		// setup operation scanning
		scanner.addIncludeFilter(new AnnotationTypeFilter(PluginOperation.class));
		scanner.setBeanNameGenerator(operationNameGenerator);

		// scan for operations
		scanner.scan(basePackage);
	}

	/**
	 * Add providers to plugin context
	 * 
	 * @param context
	 *            Plugin application context where the providers is added to.
	 */
	void addProvidersToContext(GenericApplicationContext context) {
		// cast context
		ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) context;

		// get factory
		ConfigurableListableBeanFactory factory = configurableContext.getBeanFactory();

		// register bean definition
		RootBeanDefinition beanDefinition = new RootBeanDefinition(executionInfoProvider.getClass());
		context.registerBeanDefinition(EXECUTION_INFO_PROVIDER_BEANID, beanDefinition);

		// register execution info provider as singleton
		factory.registerSingleton(EXECUTION_INFO_PROVIDER_BEANID, executionInfoProvider);

		// register bean definition
		beanDefinition = new RootBeanDefinition(runtimeDirectoryProvider.getClass());
		context.registerBeanDefinition(RUNTIME_DIRECTORY_PROVIDER_BEANID, beanDefinition);

		// register execution info provider
		factory.registerSingleton(RUNTIME_DIRECTORY_PROVIDER_BEANID, runtimeDirectoryProvider);

		// register bean definition
		beanDefinition = new RootBeanDefinition(administrationProvider.getClass());
		context.registerBeanDefinition(ADMINISTRATION_PROVIDER_BEANID, beanDefinition);

		// register administration provider
		factory.registerSingleton(ADMINISTRATION_PROVIDER_BEANID, administrationProvider);

		// register bean definition
		beanDefinition = new RootBeanDefinition(variableSubstitutionProvider.getClass());
		context.registerBeanDefinition(VARIABLE_SUBSTITUTION_PROVIDER_BEANID, beanDefinition);

		// register variable substitution provider
		factory.registerSingleton(VARIABLE_SUBSTITUTION_PROVIDER_BEANID, variableSubstitutionProvider);
	}

}
