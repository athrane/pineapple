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
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Helper class for getting reflection info about Java methods and 
 * invoking methods using reflection. 
 */
public class MethodUtilsImpl implements MethodUtils 
{   
    /**
     * String string for retrieving getter method for any package. 
     */    
    static final String ANY_PACKAGE_NAME = "";
    
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );

            
    public Method[] resolveGetterMethods( Object targetObject, String attributeName, GetterMethodMatcher matcher )
    {
        // validate arguments
        Validate.notNull( targetObject, "targetObject is undefined.");
        Validate.notNull( attributeName, "attributeName is undefined.");        
        Validate.notNull( matcher, "matcher is undefined.");        

        // log debug message
        if (logger.isDebugEnabled()) {
            StringBuilder message = new StringBuilder();
            message.append( "Starting to resolve getter methods from <" );
            message.append( StringUtils.substringBefore( targetObject.toString(), "\n" ) );
            message.append( "> using attribute <" );
            message.append( attributeName );            
            message.append( "> and method matcher <" );            
            message.append( matcher );            
            message.append( ">." );            
            logger.debug( message.toString() );            
        }             
        
        // create result
        ArrayList<Method> result = new ArrayList<Method>();
        
        // exit if attribute is empty string
        if(attributeName.length() == 0) return new Method[0];
        
        // get all methods
        Method[] methods = targetObject.getClass().getMethods();                     
                       
        // iterate over methods                
        for (Method method : methods ) {
                        	        	
            // validate method is positive match 
            if( matcher.isMatch( method )) {                
                            	
                // resolve attribute name from matcher
                String candidateName = matcher.resolveAttributeFromGetterMethod( method );

                // if attribute name is a match add the method to result set
                if (attributeName.equalsIgnoreCase( candidateName )) {
                
                    // add method to result set.
                    result.add( method );                    
                }
            }
        }

        // convert to array
        Method[] resultArray = result.toArray( new Method[result.size()] );         
        
        // log debug message
        if (logger.isDebugEnabled()) {
            StringBuilder message = new StringBuilder();
            message.append( "Successfully resolved getters methods on object <" );
            message.append( StringUtils.substringBefore( targetObject.toString(), "\n" ) );
            message.append( "> with result <" );
            message.append( ReflectionToStringBuilder.toString( resultArray) );            
            message.append( ">." );            
            logger.debug( message.toString() );            
        }             
            
        return resultArray;                
    }
        
    public Method[] resolveGetterMethods( Object targetObject, GetterMethodMatcher matcher )
    {
        // validate arguments
        Validate.notNull( targetObject, "targetObject is undefined.");        
        Validate.notNull( matcher, "matcher is undefined.");
        
        // log debug message
        if (logger.isDebugEnabled()) {
            StringBuilder message = new StringBuilder();
            message.append( "Starting to resolve getter methods from <" );
            message.append( StringUtils.substringBefore( targetObject.toString(), "\n" ) );
            message.append( "> using method matcher <" );            
            message.append( matcher );            
            message.append( ">." );            
            logger.debug( message.toString() );            
        }             

        // create result
        ArrayList<Method> result = new ArrayList<Method>();
        
        // get all methods
        Method[] methods = targetObject.getClass().getMethods();                     
        
        // iterate over methods                
        for (Method method : methods ) {
                                    
            // validate method is positive match 
            if( matcher.isMatch( method )) {                
                                               
                    // add method to result set.
                    result.add( method );                    
                }
            }

        // convert to array
        Method[] resultArray = result.toArray( new Method[result.size()] );         
        
        // log debug message
        if (logger.isDebugEnabled()) {
            StringBuilder message = new StringBuilder();
            message.append( "Successfully resolved getters methods on object <" );
            message.append( StringUtils.substringBefore( targetObject.toString(), "\n" ) );
            message.append( "> with result <" );
            message.append( ReflectionToStringBuilder.toString( resultArray) );            
            message.append( ">." );            
            logger.debug( message.toString() );            
        }             
            
        return resultArray;             
    }

    public boolean classIsDeclaredInPackage( Class<?> classObject, String packageName )    
    {   
        // get class package
        Package classPackage = classObject.getPackage();
        
        // if package is null then we have a proxy'ed class
        if (classPackage == null ) {
        	
            // handle dynamic proxy case
            
            // get proxy interfaces
            Class<?>[] interfaces = classObject.getInterfaces();
            
            // iterate over the interfaces
            for (Class<?> proxyInterface : interfaces ) {
                
                // return positive match is interface package match
                if (proxyInterface.getName().startsWith( packageName )) return true;
            }
 
            // return negative match 
            return false;
        }
               
        // handle non-proxy case
        return  (classPackage.getName().startsWith( packageName )); 
    }
        
    public boolean classIsDeclaredInPackages( Class<?> declaringClass, String[] candidatePackages )
    {
        for (String candidatePackage : candidatePackages) {
            if (classIsDeclaredInPackage( declaringClass, candidatePackage )) {
                return true;
            }
        }
        
        return false;
    }

    public boolean methodNameStartsWithGet( Method method )
    {
        return method.getName().startsWith("get");
    }
            
    public boolean methodNameStartsWithIs( Method method )
    {
        return method.getName().startsWith("is");    }

    public boolean isMethodWithNoParameters( Method method ) 
    {        
        return (method.getParameterTypes().length == 0);        
    }
            
    public boolean methodReturnsPrimitiveBoolean( Method method )
    {
        // get return type
        Class<?> returnType = method.getReturnType();

        // do the test
        return ( returnType == java.lang.Boolean.TYPE );        
    }
        
    public boolean methodReturnsArray( Method method )
    {
        // get return type
        Class<?> returnType = method.getReturnType();

        return returnType.isArray();        
    }

    public Object invokeMethodWithNoArgs(Object targetObject, Method method) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        
        // null argument array
        final Object[] NO_ARGS = new  Object[0]; 
        
        // invoke method
        return method.invoke( targetObject, NO_ARGS );
    }    

    public Method getMethod( Object targetObject, String methodName ) throws SecurityException, NoSuchMethodException    
    {
        // null parameters array
        final Class<?>[] NO_PARAMS = new  Class[0]; 
        
        return targetObject.getClass().getMethod( methodName, NO_PARAMS );        
    }
    
    
    /**
     * Returns all getter methods from target Object.
     * 
     * @param targetObject The target object to process.
     * @param packageName Methods are only included from classes
     * which are declared in the package or any sub packages. To return
     * getters declared in any package supply the empty string as argument.
     * 
     * @return Array of <code>Method</code> objects which are getter methods. 
     */
    public Method[] getGetterMethods( Object targetObject, String packageName ) {
                       
        // log debug message
        if (logger.isDebugEnabled()) {
            StringBuilder message = new StringBuilder();
            message.append( "Starting to process object <" );
            message.append( targetObject );
            message.append( "> for getter methods which are declared by classes in package <" );
            message.append( packageName );
            message.append( ">." );            
            logger.debug( message.toString() );
        }             
        
        // create result
        ArrayList<Method> result = new ArrayList<Method>();
        
        // get all methods
        Method[] methods = targetObject.getClass().getMethods();                     
            
        // iterate over methods                
        for (Method method : methods ) {
            
            // get declaring class
            Class<?> declaringClass = method.getDeclaringClass();

            // skip method if it doesn't reside in the specified package 
            if (!classIsDeclaredInPackage( declaringClass, packageName )) continue;
            
            // skip method if the name isn't a getter
            if (!methodNameStartsWithGet( method )) continue;            

            // add class to result set.
            result.add( method );
        }

        // convert to array
        Method[] resultArray = result.toArray( new Method[result.size()] );         
        
        // log debug message
        if (logger.isDebugEnabled()) {
            StringBuilder message = new StringBuilder();
            message.append( "Successfully processed object <" );
            message.append( targetObject );
            message.append( "> for getter methods <" );
            message.append( ReflectionToStringBuilder.toString( resultArray) );            
            message.append( ">." );            
            logger.debug( message.toString() );            
        }             
            
        return resultArray;                
    }           
   
 
    
       
}
