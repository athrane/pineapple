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

import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.alpha.pineapple.docker.model.rest.ObjectFactory;
import com.alpha.pineapple.docker.model.rest.PortSet;
import com.alpha.pineapple.docker.model.rest.PortSetElement;
import com.alpha.pineapple.docker.model.rest.PortSetElementNullValue;

/**
 * {@linkplain XmlAdapter} sub class to support customised binding to a map for
 * the {@linkplain PortSet} JAXB generated type.
 */
public class PortSetMapAdapter extends XmlAdapter<PortSet, PortSetMap> {

	/**
	 * Object factory.
	 */
	ObjectFactory objectFactory = new ObjectFactory();

	@Override
	public PortSet marshal(PortSetMap map) throws Exception {
		PortSet ports = objectFactory.createPortSet();
		Set<Entry<String, PortSetElementNullValue>> entries = map.entrySet();
		for (Entry<String, PortSetElementNullValue> entry : entries) {
			PortSetElement element = objectFactory.createPortSetElement();
			element.setPort(entry.getKey());
			element.setNullValue(objectFactory.createPortSetElementNullValue());
			ports.getElements().add(element);
		}
		return ports;
	}

	@Override
	public PortSetMap unmarshal(PortSet value) throws Exception {
		PortSetMap map = new PortSetMap();
		for (PortSetElement element : value.getElements()) {
			map.put(element.getPort(), element.getNullValue());
		}
		return map;
	}

}
