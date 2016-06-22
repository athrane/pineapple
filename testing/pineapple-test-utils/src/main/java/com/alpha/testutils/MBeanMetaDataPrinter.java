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

package com.alpha.testutils;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;

/**
 * Helper class which prints MBean meta data to log files.
 */
public class MBeanMetaDataPrinter {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * JMX Server connection.
     */
    MBeanServerConnection connection;

    /**
     * MBeanMetaDataPrinter constructor.
     * 
     * @param connection
     *            JMX Server connection.
     */
    public MBeanMetaDataPrinter(MBeanServerConnection connection) {
	super();
	this.connection = connection;
    }

    /**
     * Print MBean attribute metatdata for selected attribute.
     * 
     * @param objectName
     *            MBean which have the attribute defined.
     * @param attrName
     *            Name of the attribute.
     */
    public void printMBeanAttribute(ObjectName objectName, String attrName) {

	try {
	    // get mbean info
	    MBeanInfo info;
	    info = connection.getMBeanInfo(objectName);

	    MBeanAttributeInfo[] attributeInfos = info.getAttributes();
	    for (MBeanAttributeInfo attributeInfo : attributeInfos) {

		// get name
		String name = attributeInfo.getName();

		// list attributes metatdata if name matches
		if (name.equals(attrName)) {
		    logger.debug("ModelAttribute name=" + attributeInfo.getName());
		    logger.debug("ModelAttribute type=" + attributeInfo.getType());
		    logger.debug("ModelAttribute description=" + attributeInfo.getDescription());
		}
	    }

	} catch (Exception e) {
	    fail(StackTraceHelper.getStrackTrace(e));
	}

    }

    /**
     * Print meta data for MBean attributes.
     * 
     * @param objectName
     *            The MBean whose attribute meta data should be printed.
     */
    public void printMBeanAttributes(ObjectName objectName) {
	try {
	    // get mbean info
	    MBeanInfo info;
	    info = connection.getMBeanInfo(objectName);

	    MBeanAttributeInfo[] attributeInfos = info.getAttributes();
	    for (MBeanAttributeInfo attributeInfo : attributeInfos) {

		// list attributes metatdata
		logger.debug("ModelAttribute name=" + attributeInfo.getName());
		logger.debug("ModelAttribute type=" + attributeInfo.getType());
		logger.debug("ModelAttribute description=" + attributeInfo.getDescription());
	    }
	} catch (Exception e) {
	    fail(StackTraceHelper.getStrackTrace(e));
	}
    }

    /**
     * Print meta data for MBean operations.
     * 
     * @param objectName
     *            The MBean whose operation meta data should be printed.
     */
    public void printMBeanOperations(ObjectName objectName) {
	try {
	    // mbean info
	    MBeanInfo info = connection.getMBeanInfo(objectName);

	    // list operations
	    MBeanOperationInfo[] opInfos = info.getOperations();
	    for (MBeanOperationInfo opInfo : opInfos) {
		logger.debug("Operation name=" + opInfo.getName());
		logger.debug("Operation returnType=" + opInfo.getReturnType());
		logger.debug("Operation  description=" + opInfo.getDescription());
	    }
	} catch (Exception e) {
	    fail(StackTraceHelper.getStrackTrace(e));
	}
    }

    /**
     * Print meta data for selected MBean operations which match selected return
     * type.
     * 
     * @param objectName
     *            The MBean whose operation meta data should be printed.
     * @param returnType
     *            Name of the return type.
     */
    public void printMBeanOperationsWithMatchingReturnType(ObjectName objectName, String returnType) {

	try {
	    // mbean info
	    MBeanInfo info = connection.getMBeanInfo(objectName);

	    // list operations
	    MBeanOperationInfo[] opInfos = info.getOperations();
	    for (MBeanOperationInfo opInfo : opInfos) {

		// get return type
		String foundReturnType = opInfo.getReturnType();

		// list operation which match return type
		if (foundReturnType.equals(returnType)) {
		    logger.debug("Operation name=" + opInfo.getName());
		    logger.debug("Operation returnType=" + opInfo.getReturnType());
		    logger.debug("Operation  description=" + opInfo.getDescription());
		}
	    }
	} catch (Exception e) {
	    fail(StackTraceHelper.getStrackTrace(e));
	}
    }

    /**
     * Print MBean instance for Object Name.
     * 
     * @param objectName
     *            The MBean.
     */
    public void printMBeanInstance(ObjectName objectName) {
	try {
	    // get mbean instance
	    ObjectInstance instance = connection.getObjectInstance(objectName);

	    // create message
	    StringBuilder message = new StringBuilder();
	    message.append("MBean instance class name=");
	    message.append(instance.getClassName());

	    // log message
	    logger.debug(message.toString());

	} catch (Exception e) {
	    fail(StackTraceHelper.getStrackTrace(e));
	}
    }

    /**
     * Print MBean interfaces implemented by the MBean. An interface is
     * identified as an MBean interface is has the name *MBean.
     * 
     * @param objectName
     *            The MBeanfor print MBean interfaces for.
     */
    public void printMBeanInterfacesImplementedByMBean(ObjectName objectName) {

	// get MBean interfaces implemented by MBean.
	ArrayList<Class> mbeanInterfaces = getMBeanInterfaces(objectName);

	for (Class mbeanInterface : mbeanInterfaces) {
	    String interfaceName = mbeanInterface.getCanonicalName();

	    // create message
	    StringBuilder message = new StringBuilder();
	    message.append("MBean interface name=");
	    message.append(interfaceName);

	    // log message
	    logger.debug(message.toString());

	}
    }

    /**
     * print Mbean operations
     * 
     * @param objectName
     * @param mbeanType
     */
    public void printMBeanOperationsOnImpl(ObjectName objectName, String fqMBeanClassName) {

	// get MBean interfaces implemented by MBean.
	ArrayList<Class> mbeanInterfaces = getMBeanInterfaces(objectName);

	// get mbean methods
	for (Class mbeanInterface : mbeanInterfaces) {
	    String interfaceName = mbeanInterface.getCanonicalName();
	    Method[] declaredMethods = mbeanInterface.getDeclaredMethods();
	    for (Method method : declaredMethods) {

		Class<?> returnType = method.getReturnType();
		String returnTypeName = returnType.getCanonicalName();

		if (returnTypeName.contains(fqMBeanClassName)) {
		    StringBuilder message = new StringBuilder();
		    message.append("M:");
		    message.append(interfaceName);
		    message.append(".");
		    message.append(method.getName());
		    message.append("::");
		    message.append(method.getReturnType().getCanonicalName());
		    logger.debug(message.toString());
		}
	    }
	}
    }

    /**
     * Print MBean object name .
     * 
     * @param objectName
     *            MBean objectName.
     */
    public void printObjectName(ObjectName objectName) {

	StringBuilder message = new StringBuilder();
	message.append("ObjectName=");
	message.append(objectName);
	logger.debug(message.toString());
    }

    /**
     * Return a list of MBean interfaces implemented by an MBean. The selection
     * criteria which decides if an interface is an MBean interface is whether
     * the interface contains the String MBean in the interface name.
     * 
     * @param objectName
     *            the MBean for which the interfaces are returned.
     * 
     * @return Return a list of MBean interfaces implemented by an MBean.
     * 
     */
    ArrayList<Class> getMBeanInterfaces(ObjectName objectName) {
	try {

	    // get mbean instance
	    ObjectInstance instance;
	    instance = connection.getObjectInstance(objectName);

	    // instance class name
	    String instanceClassName = instance.getClassName();

	    logger.debug("Object instance className=" + instance.getClassName());

	    // get implementation class
	    Class<?> instanceClass = Class.forName(instanceClassName);

	    logger.debug("instanceClass=" + instanceClass);

	    // list interfaces
	    Class[] interfaces = instanceClass.getInterfaces();
	    for (Class aInterface : interfaces) {
		logger.debug("Interface=" + aInterface.getCanonicalName());
	    }

	    // get *MBean interfaces
	    ArrayList<Class> mbeanInterfaces = new ArrayList<Class>();
	    for (Class aInterface : interfaces) {
		String interfaceName = aInterface.getCanonicalName();

		if (interfaceName.contains("MBean")) {
		    mbeanInterfaces.add(aInterface);
		    logger.debug("MBean interface=" + aInterface.getName());
		}
	    }
	    return mbeanInterfaces;

	} catch (InstanceNotFoundException e) {
	    fail(StackTraceHelper.getStrackTrace(e));
	} catch (IOException e) {
	    fail(StackTraceHelper.getStrackTrace(e));
	} catch (ClassNotFoundException e) {
	    fail(StackTraceHelper.getStrackTrace(e));
	}

	return null;
    }

}
