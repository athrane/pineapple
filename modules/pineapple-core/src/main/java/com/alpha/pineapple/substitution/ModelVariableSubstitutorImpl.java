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

package com.alpha.pineapple.substitution;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.model.module.Module;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.substitution.proxy.VariableSubstitutedProxyFactory;
import com.alpha.pineapple.substitution.variables.CompositeVariablesBuilder;
import com.alpha.pineapple.substitution.variables.ModelVariablesBuilder;
import com.alpha.pineapple.substitution.variables.ModuleDescriptorVariablesBuilder;
import com.alpha.pineapple.substitution.variables.ResourceVariablesBuilder;
import com.alpha.pineapple.substitution.variables.Variables;

/**
 * Implementation of the {@linkplain ModelVariableSubstitutor} interface.
 */
public class ModelVariableSubstitutorImpl implements ModelVariableSubstitutor {

    /**
     * Logger object
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Message provider for I18N support.
     */
    @javax.annotation.Resource
    MessageProvider messageProvider;

    /**
     * Resource variables builder Factory.
     */
    @javax.annotation.Resource
    ObjectFactory<ResourceVariablesBuilder> resourceVariablesBuilderFactory;

    /**
     * Model variables builder Factory.
     */
    @javax.annotation.Resource
    ObjectFactory<ModelVariablesBuilder> modelVariablesBuilderFactory;

    /**
     * Module descriptor variables builder Factory.
     */
    @javax.annotation.Resource
    ObjectFactory<ModuleDescriptorVariablesBuilder> moduleDescriptorVariablesBuilderFactory;

    /**
     * Composite variables builder Factory.
     */
    @javax.annotation.Resource
    ObjectFactory<CompositeVariablesBuilder> compositeVariablesBuilderFactory;

    /**
     * Variable substituted proxy factory Factory.
     */
    @javax.annotation.Resource
    ObjectFactory<VariableSubstitutedProxyFactory> variableSubstitutedProxyFactoryFactory;

    @Override
    public <T> T createObjectWithSubstitution(Module module, Models model, Resource resource, T targetObject)
	    throws VariableSubstitutionException {
	Validate.notNull(module, "module is undefined.");
	Validate.notNull(model, "model is undefined.");
	Validate.notNull(resource, "resource is undefined.");

	// create variables from module, model and resource
	ModuleDescriptorVariablesBuilder moduleBuilder = moduleDescriptorVariablesBuilderFactory.getObject();
	moduleBuilder.setModel(module);
	ModelVariablesBuilder modelBuilder = modelVariablesBuilderFactory.getObject();
	modelBuilder.setModel(model);
	ResourceVariablesBuilder resourceBuilder = resourceVariablesBuilderFactory.getObject();
	resourceBuilder.setResource(resource);

	// create composite variables
	CompositeVariablesBuilder compositeVariablesBuilder = compositeVariablesBuilderFactory.getObject();
	compositeVariablesBuilder.addBuilder("module", moduleBuilder);
	compositeVariablesBuilder.addBuilder("model", modelBuilder);
	compositeVariablesBuilder.addBuilder("resource", resourceBuilder);
	Variables compositeVariables = compositeVariablesBuilder.getVariables();

	VariableSubstitutedProxyFactory factory = variableSubstitutedProxyFactoryFactory.getObject();
	factory.initialize(compositeVariables);
	return factory.decorateWithProxy(targetObject);
    }

}
