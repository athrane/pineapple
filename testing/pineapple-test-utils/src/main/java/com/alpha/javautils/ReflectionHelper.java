/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen.
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

package com.alpha.javautils;

import java.lang.reflect.Constructor;

/**
 * Helper class which provides helper functions for exception handling.
 */
public class ReflectionHelper {

    /**
     * Create object instance for class which takes single String as constructor
     * argument.
     * 
     * @param classs
     *            Class to instantiate object from.
     * @param argument
     *            String argument which should be used as parameter when
     *            invoking the constructor.
     * 
     * @return object instance of supplied class.
     */
    public Object createObject(Class classs, String argument) {
	try {
	    Class[] classArgs = { String.class };
	    Constructor constructor;
	    constructor = classs.getConstructor(classArgs);
	    Object[] constructorArgs = new Object[] { argument };
	    return constructor.newInstance(constructorArgs);
	} catch (Exception e) {
	    throw new InstantiationError("Object instantiation failed.");
	}
    }

    /**
     * Create object instance with no-arg constructor.
     * 
     * @param className
     *            Class name to create instance of.
     * 
     * @return object instance of requested class.
     */
    public Object createObject(String className) {
	try {
	    Class classs = Class.forName(className);
	    return classs.newInstance();
	} catch (Exception e) {
	    throw new InstantiationError("Object instantiation failed.");
	}
    }

}
