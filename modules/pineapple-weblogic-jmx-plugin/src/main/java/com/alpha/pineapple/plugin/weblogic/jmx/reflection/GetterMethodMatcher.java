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


package com.alpha.pineapple.plugin.weblogic.jmx.reflection;

import java.lang.reflect.Method;

/**
 * Interface for specification of getter method matching criteria.
 */
public interface GetterMethodMatcher
{
    /**
     * Returns true is method is a match.
     *  
     * @param method the method to evaluate.
     * 
     * @return true is method is a match.
     */
    boolean isMatch(Method method); 
    
    /**
     * Resolve attribute name from a getter method.
     *  
     * @param method The getter method to resolve the attribute name from.
     * 
     * @return Resolved attribute name from a getter method.
     */
    String resolveAttributeFromGetterMethod(Method method);
}
