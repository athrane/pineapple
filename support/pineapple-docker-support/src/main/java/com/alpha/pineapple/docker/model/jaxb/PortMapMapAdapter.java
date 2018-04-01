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

import com.alpha.pineapple.docker.model.rest.ObjectFactory;
import com.alpha.pineapple.docker.model.rest.PortBinding;
import com.alpha.pineapple.docker.model.rest.PortMap;
import com.alpha.pineapple.docker.model.rest.PortMapElement;

/**
 * {@linkplain XmlAdapter} sub class to support customised binding to a map for
 * the {@linkplain PortMap} JAXB generated type.
 */
public class PortMapMapAdapter extends XmlAdapter<PortMap, PortMapMap> {

	/**
	 * Object factory.
	 */
	ObjectFactory objectFactory = new ObjectFactory();

	@Override
	public PortMap marshal(PortMapMap map) throws Exception {
		PortMap ports = objectFactory.createPortMap();
		Set<Entry<String, PortBinding[]>> entries = map.entrySet();
		for (Entry<String, PortBinding[]> entry : entries) {
			PortMapElement element = objectFactory.createPortMapElement();
			element.setPort(entry.getKey());
			List<PortBinding> bindingList = element.getBinding();

			// add values in array
			for (PortBinding binding : entry.getValue()) {
				bindingList.add(binding);
			}

		}

		return ports;
	}

	@Override
	public PortMapMap unmarshal(PortMap value) throws Exception {
		PortMapMap map = new PortMapMap();
		for (PortMapElement element : value.getElements()) {
			List<PortBinding> valueList = element.getBinding();
			PortBinding[] valueArray = new PortBinding[valueList.size()];
			valueArray = valueList.toArray(valueArray);
			map.put(element.getPort(), valueArray);
		}
		return map;
	}

}
