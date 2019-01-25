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
import com.alpha.pineapple.docker.model.rest.StringMap;
import com.alpha.pineapple.docker.model.rest.StringMapElement;

/**
 * {@linkplain XmlAdapter} sub class to support customized binding to a map for
 * the {@linkplain StringMap} JAXB generated type.
 */
public class StringMapAdapter extends XmlAdapter<StringMap, StringMapMap> {

	/**
	 * Object factory.
	 */
	ObjectFactory objectFactory = new ObjectFactory();

	@Override
	public StringMap marshal(StringMapMap map) throws Exception {
		StringMap target = objectFactory.createStringMap();
		Set<Entry<String, String>> entries = map.entrySet();
		for (Entry<String, String> entry : entries) {
			StringMapElement element = objectFactory.createStringMapElement();
			element.setKey(entry.getKey());
			element.setValue(entry.getValue());
			target.getElement().add(element);
		}
		return target;
	}

	@Override
	public StringMapMap unmarshal(StringMap value) throws Exception {
		StringMapMap map = new StringMapMap();
		for (StringMapElement element : value.getElement()) {
			map.put(element.getKey(), element.getValue());
		}
		return map;
	}

}
