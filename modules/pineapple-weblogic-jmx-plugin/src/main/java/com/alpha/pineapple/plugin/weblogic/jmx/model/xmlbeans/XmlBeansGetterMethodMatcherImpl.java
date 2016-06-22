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


package com.alpha.pineapple.plugin.weblogic.jmx.model.xmlbeans;

import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicXmlBeanConstants;
import com.alpha.pineapple.plugin.weblogic.jmx.reflection.GetterMethodMatcher;
import com.alpha.pineapple.plugin.weblogic.jmx.reflection.MethodUtils;

/**
 * Implementation of the <code>GetterMethodMatcher</code> interface
 * which can match getter methods from XMLBeans. 
 * <p/>
 * A method is matched if it belongs to the package <code>com.bea.ns.weblogic</code>
 * (including sub packages) and starts with 'get'. 
 */
public class XmlBeansGetterMethodMatcherImpl implements GetterMethodMatcher
{    
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * Methods reflection utility object.
     */
    @Resource    
    MethodUtils methodUtils;
    
    
    public boolean isMatch( Method method )
    {
        // get declaring class
        Class<?> declaringClass = method.getDeclaringClass();
        
        // return unsuccessful result if method doesn't reside in model package 
        if (!methodUtils.classIsDeclaredInPackage( declaringClass, WebLogicXmlBeanConstants.MODEL_PACKAGE )) return false;

        // get method name
        String methodName = method.getName();            
        
     // return unsuccessful result if method take arguments
        if (! methodUtils.isMethodWithNoParameters( method )) return false;
        
        // return if method start with 'getXX'
        return methodName.startsWith( "get" );
    }

    public String resolveAttributeFromGetterMethod( Method method )
    {
        final int GET_INDEX = 3; // start index for get-XX substrings        
        final int ARRAY_LENGTH = 5; // start index for get-XX substrings        
        
        // get method name
        String methodName = method.getName();            

        // strip 'get' from method
        String strippedName = methodName.substring( GET_INDEX );
        
        // strip trailing 'Array'  
        if (methodName.endsWith( "Array" )) {
            return strippedName.substring( 0, strippedName.lastIndexOf( "Array" ) );
        }
        
        return strippedName;
    }

}
