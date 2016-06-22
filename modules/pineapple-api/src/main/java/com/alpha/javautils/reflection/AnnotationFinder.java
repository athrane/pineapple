/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
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

package com.alpha.javautils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Helper class which can search objects for annotations.
 */
public class AnnotationFinder {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Search object for fields which are annotated by a specific annotation.
     * 
     * @param annotatedObject
     *            The object whose fields are searched for annotation.
     * @param annotationClass
     *            The annotation to search for.
     * 
     * @return Array of {@link Field} which are the fields on the searched
     *         object which are annotated which specific annotation.
     * 
     * @IllegalArgumentException If the parameter annotatedObject is undefined.
     * @IllegalArgumentException If the parameter annotation is undefined.
     */
    public Field[] findAnnotatedFields(Object annotatedObject, Class<? extends Annotation> annotationClass) {

	// validate parameters
	Validate.notNull(annotatedObject, "annotatedObject is undefined");
	Validate.notNull(annotationClass, "annotation is undefined");

	// get fields
	Field[] fields = annotatedObject.getClass().getDeclaredFields();

	// create result set
	ArrayList<Field> result = new ArrayList<Field>();

	// iterate over the fields
	for (Field field : fields) {

	    // if annotation is defined all it to result.
	    if (field.isAnnotationPresent(annotationClass)) {

		// add result
		result.add(field);
	    }
	}

	return result.toArray(new Field[result.size()]);

    }
}
