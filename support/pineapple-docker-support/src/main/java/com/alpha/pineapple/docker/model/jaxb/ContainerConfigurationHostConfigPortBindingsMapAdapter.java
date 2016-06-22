/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.docker.model.jaxb;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.alpha.pineapple.docker.model.rest.ContainerConfigurationHostConfigPortBinding;
import com.alpha.pineapple.docker.model.rest.ContainerConfigurationHostConfigPortBindingValue;
import com.alpha.pineapple.docker.model.rest.ContainerConfigurationHostConfigPortBindings;
import com.alpha.pineapple.docker.model.rest.ObjectFactory;

/**
 * {@linkplain XmlAdapter} sub class to support customized binding to a map for
 * the {@linkplain ContainerConfigurationHostConfigPortBindings} JAXB generated
 * type.
 */
public class ContainerConfigurationHostConfigPortBindingsMapAdapter extends
	XmlAdapter<ContainerConfigurationHostConfigPortBindings, ContainerConfigurationHostConfigPortBindingsMap> {

    /**
     * Object factory.
     */
    ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public ContainerConfigurationHostConfigPortBindings marshal(ContainerConfigurationHostConfigPortBindingsMap map)
	    throws Exception {
	ContainerConfigurationHostConfigPortBindings bindings = objectFactory
		.createContainerConfigurationHostConfigPortBindings();
	Set<Entry<String, ContainerConfigurationHostConfigPortBindingValue[]>> entries = map.entrySet();
	for (Entry<String, ContainerConfigurationHostConfigPortBindingValue[]> entry : entries) {

	    ContainerConfigurationHostConfigPortBinding binding = objectFactory
		    .createContainerConfigurationHostConfigPortBinding();
	    binding.setContainerPort(entry.getKey());
	    List<ContainerConfigurationHostConfigPortBindingValue> bindingValueList = binding.getValue();

	    // add values in array
	    for (ContainerConfigurationHostConfigPortBindingValue bindingValue : entry.getValue()) {
		bindingValueList.add(bindingValue);
	    }
	}
	return bindings;
    }

    @Override
    public ContainerConfigurationHostConfigPortBindingsMap unmarshal(ContainerConfigurationHostConfigPortBindings value)
	    throws Exception {
	ContainerConfigurationHostConfigPortBindingsMap map = new ContainerConfigurationHostConfigPortBindingsMap();
	for (ContainerConfigurationHostConfigPortBinding binding : value.getBinding()) {

	    List<ContainerConfigurationHostConfigPortBindingValue> valueList = binding.getValue();
	    ContainerConfigurationHostConfigPortBindingValue[] valueArray = new ContainerConfigurationHostConfigPortBindingValue[valueList
		    .size()];
	    valueArray = valueList.toArray(valueArray);
	    map.put(binding.getContainerPort(), valueArray);
	}
	return map;
    }

}
