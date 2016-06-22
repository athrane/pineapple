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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Methods reflection utility object. 
 */
public interface MethodUtils
{

    /**
     * Resolve getter methods from attribute name and method matcher object.
     *   
     * @param targetObject The object to get the method from.
     * @param attributeName The attribute name to resolve method from.
     * @param matcher Getter method matcher object which implements the matching criterias. 
     * 
     * @return getter methods resolved from from attribute name. 
     */
    public Method[] resolveGetterMethods( Object targetObject , String attributeName, GetterMethodMatcher matcher );

    /**
     * Resolve getter methods from method matcher object.
     *   
     * @param targetObject The object to get the method from.
     * @param matcher Getter method matcher object which implements the matching criterias. 
     * 
     * @return getter methods resolved from from attribute name. 
     */
    public Method[] resolveGetterMethods( Object targetObject , GetterMethodMatcher matcher );
    
    /**
     * Return true if class belong to the designated package and sub packages. 
     * <p/>
     * If the class is a dynamic proxy then it tested whether any of 
     * the proxy interfaces belongs to the designated package (and sub packages).
     *  
     * @param classObject The class to test.
     * @param classObject The designated package. 
     * 
     * @return true if class belong to the designated package.
     */
    boolean classIsDeclaredInPackage( Class<?> classObject, String packageName );    

    /**
     * Return true if class belong to the list of designated package and sub packages.
     * 
     * <p/>
     * If the class is a dynamic proxy then it tested whether any of 
     * the proxy interfaces belongs to the designated packages (and sub packages). 
     *  
     * @param classObject The class to test.
     * @param classObject List of designated packages. 
     * 
     * @return true if class belong to one of the designated packages.
     */    
    public boolean classIsDeclaredInPackages( Class<?> declaringClass, String[] candidatePackages );    
    
    /**
     * Return true if method starts with "get".
     * 
     * @param method the method to process.
     * 
     * @return true if method starts with "get".
     * 
     */
    public boolean methodNameStartsWithGet( Method method );

    /**
     * Return true if method starts with "is".
     * 
     * @param method the method to process.
     * 
     * @return true if method starts with "is".
     * 
     */
    public boolean methodNameStartsWithIs( Method method );
        
    /**
     * Return true if method has no parameters.
     * 
     * @param method The method to test against.
     * 
     * @return true if the method has no parameters.
     */
    
    public boolean isMethodWithNoParameters( Method method );
    
    /**
     * Invoke method with no arguments. 
     * 
     * @param targetObject the object to invoke the method on.
     * @param method the no-arg method to invoke.
     * 
     * @return The result returned by the invoked method.
     * 
     * @throws InvocationTargetException If method invocation fails.
     * @throws IllegalAccessException If method invocation fails.
     * @throws IllegalArgumentException If method invocation fails.
     */    
    public Object invokeMethodWithNoArgs(Object targetObject, Method method) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;
    
    /**
     * Get method object with no parameters using the simple method name.
     *  
     * @param targetObject The object to get the method from.
     * @param methodName Simple method name.
     * 
     * @return method object with no parameters.
     * 
     * @throws NoSuchMethodException If method retrieval fails.
     * @throws SecurityException If method retrieval fails.
     */    
    public Method getMethod( Object targetObject, String methodName ) throws SecurityException, NoSuchMethodException;

    /**
     * Return true if method returns a primitive boolean.
     * 
     * @param method The method to test against.
     * 
     * @return true if method returns a primitive boolean.
     */
    public boolean methodReturnsPrimitiveBoolean( Method method );

    /**
     * Return true if method returns array.
     * 
     * @param method The method to test against.
     * 
     * @return true if method returns array.
     */
    public boolean methodReturnsArray( Method method );    
}
