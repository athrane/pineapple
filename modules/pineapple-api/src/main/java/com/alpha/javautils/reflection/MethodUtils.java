/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

/**
 * Helper class for getting reflection info about Java methods and invoking
 * methods using reflection.
 */
public class MethodUtils {
	/**
	 * String string for retrieving getter method for any package.
	 */
	static final String ANY_PACKAGE_NAME = "";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Resolve getter methods from attribute name and method matcher object.
	 * 
	 * @param targetObject
	 *            The object to get the method from.
	 * @param attributeName
	 *            The attribute name to resolve method from.
	 * @param matcher
	 *            Getter method matcher object which implements the matching
	 *            criterias.
	 * 
	 * @return getter methods resolved from from attribute name.
	 */
	public static Method[] resolveGetterMethods(Object targetObject, String attributeName,
			GetterMethodMatcher matcher) {
		Validate.notNull(targetObject, "targetObject is undefined.");
		Validate.notNull(attributeName, "attributeName is undefined.");
		Validate.notNull(matcher, "matcher is undefined.");

		/**
		 * // log debug message if (logger.isDebugEnabled()) { StringBuilder message =
		 * new StringBuilder(); message.append( "Starting to resolve getter methods from
		 * <" ); message.append( StringUtils.substringBefore( targetObject.toString(),
		 * "\n" ) ); message.append( "> using attribute <" ); message.append(
		 * attributeName ); message.append( "> and method matcher <" ); message.append(
		 * matcher ); message.append( ">." ); logger.debug( message.toString() ); }
		 **/

		// create result
		ArrayList<Method> result = new ArrayList<Method>();

		// exit if attribute is empty string
		if (attributeName.length() == 0)
			return new Method[0];

		// get all methods
		Method[] methods = targetObject.getClass().getMethods();

		// iterate over methods
		for (Method method : methods) {

			// validate method is positive match
			if (matcher.isMatch(method)) {

				// resolve attribute name from matcher
				String candidateName = matcher.resolveAttributeFromGetterMethod(method);

				// if attribute name is a match add the method to result set
				if (attributeName.equalsIgnoreCase(candidateName)) {

					// add method to result set.
					result.add(method);
				}
			}
		}

		// convert to array
		Method[] resultArray = result.toArray(new Method[result.size()]);

		/**
		 * // log debug message if (logger.isDebugEnabled()) { StringBuilder message =
		 * new StringBuilder(); message.append( "Successfully resolved getters methods
		 * on object <" ); message.append( StringUtils.substringBefore(
		 * targetObject.toString(), "\n" ) ); message.append( "> with result <" );
		 * message.append( ReflectionToStringBuilder.toString( resultArray) );
		 * message.append( ">." ); logger.debug( message.toString() ); }
		 **/

		return resultArray;
	}

	/**
	 * Resolve getter methods from method matcher object.
	 * 
	 * @param targetObject
	 *            The object to get the method from.
	 * @param matcher
	 *            Getter method matcher object which implements the matching
	 *            criterias.
	 * 
	 * @return getter methods resolved from from attribute name.
	 */
	public static Method[] resolveGetterMethods(Object targetObject, GetterMethodMatcher matcher) {
		Validate.notNull(targetObject, "targetObject is undefined.");
		Validate.notNull(matcher, "matcher is undefined.");

		/**
		 * // log debug message if (logger.isDebugEnabled()) { StringBuilder message =
		 * new StringBuilder(); message.append( "Starting to resolve getter methods from
		 * <" ); message.append( StringUtils.substringBefore( targetObject.toString(),
		 * "\n" ) ); message.append( "> using method matcher <" ); message.append(
		 * matcher ); message.append( ">." ); logger.debug( message.toString() ); }
		 **/

		// create result
		ArrayList<Method> result = new ArrayList<Method>();

		// get all methods
		Method[] methods = targetObject.getClass().getMethods();

		// iterate over methods
		for (Method method : methods) {

			// validate method is positive match
			if (matcher.isMatch(method)) {

				// add method to result set.
				result.add(method);
			}
		}

		// convert to array
		Method[] resultArray = result.toArray(new Method[result.size()]);

		/**
		 * // log debug message if (logger.isDebugEnabled()) { StringBuilder message =
		 * new StringBuilder(); message.append( "Successfully resolved getters methods
		 * on object <" ); message.append( StringUtils.substringBefore(
		 * targetObject.toString(), "\n" ) ); message.append( "> with result <" );
		 * message.append( ReflectionToStringBuilder.toString( resultArray) );
		 * message.append( ">." ); logger.debug( message.toString() ); }
		 **/

		return resultArray;
	}

	/**
	 * Return true if class belong to the designated package and sub packages.
	 * 
	 * If the class is a dynamic proxy then it tested whether any of the proxy
	 * interfaces belongs to the designated package (and sub packages).
	 * 
	 * @param classObject
	 *            The class to test.
	 * @param classObject
	 *            The designated package.
	 * 
	 * @return true if class belong to the designated package.
	 */
	public static boolean classIsDeclaredInPackage(Class<?> classObject, String packageName) {
		// get class package
		Package classPackage = classObject.getPackage();

		// if package is null then we have a proxy'ed class
		if (classPackage == null) {

			// handle dynamic proxy case

			// get proxy interfaces
			Class<?>[] interfaces = classObject.getInterfaces();

			// iterate over the interfaces
			for (Class<?> proxyInterface : interfaces) {

				// return positive match is interface package match
				if (proxyInterface.getName().startsWith(packageName))
					return true;
			}

			// return negative match
			return false;
		}

		// handle non-proxy case
		return (classPackage.getName().startsWith(packageName));
	}

	/**
	 * Return true if class belong to the list of designated package and sub
	 * packages.
	 * 
	 * If the class is a dynamic proxy then it tested whether any of the proxy
	 * interfaces belongs to the designated packages (and sub packages).
	 * 
	 * @param classObject
	 *            The class to test.
	 * @param classObject
	 *            List of designated packages.
	 * 
	 * @return true if class belong to one of the designated packages.
	 */
	public static boolean classIsDeclaredInPackages(Class<?> declaringClass, String[] candidatePackages) {
		for (String candidatePackage : candidatePackages) {
			if (classIsDeclaredInPackage(declaringClass, candidatePackage)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return true if method starts with "get".
	 * 
	 * @param method
	 *            the method to process.
	 * 
	 * @return true if method starts with "get".
	 */
	public static boolean methodNameStartsWithGet(Method method) {
		return method.getName().startsWith("get");
	}

	/**
	 * Return true if method starts with "is".
	 * 
	 * @param method
	 *            the method to process.
	 * 
	 * @return true if method starts with "is".
	 */
	public static boolean methodNameStartsWithIs(Method method) {
		return method.getName().startsWith("is");
	}

	/**
	 * Return true if method has no parameters.
	 * 
	 * @param method
	 *            method to test against.
	 * 
	 * @return true if the method has no parameters.
	 */
	public static boolean isMethodWithNoParameters(Method method) {
		return (method.getParameterTypes().length == 0);
	}

	/**
	 * Return true if method has no parameters.
	 * 
	 * @param method
	 *            method to test against.
	 * 
	 * @return true if the method has no parameters.
	 */
	public static boolean methodReturnsPrimitiveBoolean(Method method) {
		// get return type
		Class<?> returnType = method.getReturnType();
		return (returnType == java.lang.Boolean.TYPE);
	}

	/**
	 * Return true if method returns a string.
	 * 
	 * @param method
	 *            method to test against.
	 * 
	 * @return true if method returns a string.
	 */
	public static boolean methodReturnsString(Method method) {
		return methodReturnsObject(method, String.class);
	}

	/**
	 * Return true if method returns a primitive, i.e. on of the eight standard Java
	 * primitives: int, long, double, bool, byte short, float or char.
	 * 
	 * @param method
	 *            method to test against.
	 * 
	 * @return true if method returns a standard Java primitive.
	 */
	public static boolean methodReturnsPrimitive(Method method) {
		Validate.notNull(method, "method is undefined.");

		Class<?> returnType = method.getReturnType();

		// handle null case
		if (returnType == null)
			return false;

		if (returnType instanceof Class) {
			// cast as class to process it
			Class<?> typeClass = (Class<?>) returnType;
			if (typeClass.isPrimitive())
				return true;
			return false;
		}
		return false;
	}

	/**
	 * Return true if method returns a primitive as object, i.e. one of the eight
	 * standard Java primitives (Char excluded) as an object representation:
	 * Integer, Long, Double, Boolean, Byte, Short or Float. Char doesn't have an
	 * object representation.
	 * 
	 * @param method
	 *            method to test against.
	 * 
	 * @return true if method returns a standard Java primitive.
	 */
	public static boolean methodReturnsPrimitiveAsObject(Method method) {
		Validate.notNull(method, "method is undefined.");

		Class<?> returnType = method.getReturnType();

		// handle null case
		if (returnType == null)
			return false;

		// cast as class to process it
		if (returnType instanceof Class) {
			Class<?> typeClass = (Class<?>) returnType;
			if (typeClass.equals(Integer.class))
				return true;
			if (typeClass.equals(Long.class))
				return true;
			if (typeClass.equals(Double.class))
				return true;
			if (typeClass.equals(Boolean.class))
				return true;
			if (typeClass.equals(Byte.class))
				return true;
			if (typeClass.equals(Short.class))
				return true;
			if (typeClass.equals(Float.class))
				return true;
			return false;
		}

		return false;
	}

	/**
	 * Return true if method returns designated class.
	 * 
	 * @param method
	 *            method to test against.
	 * @param classObject
	 *            designated class that method should return.
	 * 
	 * @return true if method returns designated class.
	 */
	public static boolean methodReturnsObject(Method method, Class<?> classObject) {
		Validate.notNull(method, "method is undefined.");
		Validate.notNull(classObject, "classObject is undefined.");

		Class<?> returnType = method.getReturnType();
		if (returnType instanceof Class) {
			// cast as class to process it
			Class<?> typeClass = (Class<?>) returnType;
			return (typeClass.equals(classObject));
		}
		return false;
	}

	/**
	 * Return true if method returns class in designated Java package or sub
	 * packages.
	 * 
	 * @param method
	 *            method to test against.
	 * @param packageName
	 *            Java package name that return type should belong to.
	 * 
	 * @return true if method returns class in designated Java package or sub
	 *         packages.
	 */
	public static boolean methodReturnsObjectInPackage(Method method, String packageName) {
		Validate.notNull(method, "method is undefined.");
		Validate.notNull(packageName, "packageName is undefined.");

		Class<?> returnType = method.getReturnType();
		if (returnType instanceof Class) {
			// cast as class to process it
			Class<?> typeClass = (Class<?>) returnType;
			return classIsDeclaredInPackage(typeClass, packageName);
		}
		return false;
	}

	/**
	 * Return true if method returns array.
	 * 
	 * @param method
	 *            method to test against.
	 * 
	 * @return true if method returns array.
	 */
	public static boolean methodReturnsArray(Method method) {
		Class<?> returnType = method.getReturnType();
		return returnType.isArray();
	}

	/**
	 * Invoke method with no arguments.
	 * 
	 * @param targetObject
	 *            the object to invoke the method on.
	 * @param method
	 *            the no-arg method to invoke.
	 * 
	 * @return The result returned by the invoked method.
	 * 
	 * @throws InvocationTargetException
	 *             If method invocation fails.
	 * @throws IllegalAccessException
	 *             If method invocation fails.
	 * @throws IllegalArgumentException
	 *             If method invocation fails.
	 */
	public static Object invokeMethodWithNoArgs(Object targetObject, Method method)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		// null argument array
		final Object[] NO_ARGS = new Object[0];

		// invoke method
		return method.invoke(targetObject, NO_ARGS);
	}

	/**
	 * Get method object with no parameters using the simple method name.
	 * 
	 * @param targetObject
	 *            The object to get the method from.
	 * @param methodName
	 *            Simple method name.
	 * 
	 * @return method object with no parameters.
	 * 
	 * @throws NoSuchMethodException
	 *             If method retrieval fails.
	 * @throws SecurityException
	 *             If method retrieval fails.
	 */
	public static Method getMethod(Object targetObject, String methodName)
			throws SecurityException, NoSuchMethodException {
		// null parameters array
		final Class<?>[] NO_PARAMS = new Class[0];
		return targetObject.getClass().getMethod(methodName, NO_PARAMS);
	}

	/**
	 * Returns all getter methods from target Object.
	 * 
	 * @param targetObject
	 *            The target object to process.
	 * @param packageName
	 *            Methods are only included from classes which are declared in the
	 *            package or any sub packages. To return getters declared in any
	 *            package supply the empty string as argument.
	 * 
	 * @return Array of <code>Method</code> objects which are getter methods.
	 */
	public static Method[] getGetterMethods(Object targetObject, String packageName) {

		/**
		 * // log debug message if (logger.isDebugEnabled()) { StringBuilder message =
		 * new StringBuilder(); message.append( "Starting to process object <" );
		 * message.append( targetObject ); message.append( "> for getter methods which
		 * are declared by classes in package <" ); message.append( packageName );
		 * message.append( ">." ); logger.debug( message.toString() ); }
		 **/

		// create result
		ArrayList<Method> result = new ArrayList<Method>();

		// get all methods
		Method[] methods = targetObject.getClass().getMethods();

		// iterate over methods
		for (Method method : methods) {

			// get declaring class
			Class<?> declaringClass = method.getDeclaringClass();

			// skip method if it doesn't reside in the specified package
			if (!classIsDeclaredInPackage(declaringClass, packageName))
				continue;

			// skip method if the name isn't a getter
			if (!methodNameStartsWithGet(method))
				continue;

			// add class to result set.
			result.add(method);
		}

		// convert to array
		Method[] resultArray = result.toArray(new Method[result.size()]);

		/**
		 * // log debug message if (logger.isDebugEnabled()) { StringBuilder message =
		 * new StringBuilder(); message.append( "Successfully processed object <" );
		 * message.append( targetObject ); message.append( "> for getter methods <" );
		 * message.append( ReflectionToStringBuilder.toString( resultArray) );
		 * message.append( ">." ); logger.debug( message.toString() ); }
		 **/

		return resultArray;
	}

}
