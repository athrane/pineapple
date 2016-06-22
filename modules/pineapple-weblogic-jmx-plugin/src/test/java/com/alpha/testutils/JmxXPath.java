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


package com.alpha.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Utility class for accessing MBeans using XPath expressions.
 */
public class JmxXPath {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	
	/**
	 * MBean server connection
	 */
	MBeanServerConnection connection;
	
	/**
	 * JmxXPath constructor.
	 * 
	 * @param connection MBean server connection.
	 * 
	 * @throws IllegalArgumentException if connection is null.
	 */
	JmxXPath(MBeanServerConnection connection) {
		Validate.notNull(connection, "Connection is undefined.");
		this.connection = connection;
	}	
	
    /**
     * get attribute value on MBean using a (very limited) XPath query.
     * 
     * Queries must start with leading "/", e.g. "/SecurityConfiguration".
     * 
     * Queries can have arbitrary depth, e.g. "/SecurityConfiguration" or "/SecurityConfiguration/Realms".
     * 
     * The [n] predicate can be used select between multiple results, e.g. 
     * "/SecurityConfiguration" or "/SecurityConfiguration/Realms[0]".       
     *
     * The [@attr='value'] predicate can be used select between multiple results, e.g. 
     * "/SecurityConfiguration" or "/SecurityConfiguration/Realms[@Name='myrealm']". If
     * multiple beans match the query then the first is returned. 
     * 
     * @param objName MBean object name which is searched for attributes.
     * @param query Query expression, e.g. "/SecurityConfiguration" or "/SecurityConfiguration/Realms". 
 
     * @throws Exception If getting attribute value from MBean fails.
     */
    public Object evaluate( ObjectName objName, String query ) throws Exception	
    {
    	Validate.notNull(objName, "ObjName is undefined.");
    	Validate.notNull(query, "query is undefined.");
    	Validate.notEmpty(query, "query is empty.");
    	
    	// log debug message
    	if (logger.isDebugEnabled()) {
    		logger.debug("Will search for attribute with XPath expression[" + query + "] on MBean [" + objName + "]");
    	}
    	
    	// split query expression
    	String[] expressions = org.apache.commons.lang.StringUtils.split(query, "/");
    	
    	// declare query object name set
    	Set<ObjectName> queryObjNameSet = new HashSet<ObjectName>();
    	
    	// declare result object name set
    	Set<ObjectName> resultObjNameSet = new HashSet<ObjectName>();

    	// declare simple result set
    	Set<Object> resultSimpleSet = new HashSet<Object>();
    	
    	// add query object name to result set
    	resultObjNameSet.add(objName);
    	
    	// declare predicate
    	String predicate = null; 
    	
    	// iterate over the expressions
    	for (String expression : expressions) {
    		    		
    		// log debug message
    		if (logger.isDebugEnabled()) logger.debug("Processing expression: " + expression);
    			
			// clear query object name set
			queryObjNameSet.clear();
			
			// add content of result to query set
			queryObjNameSet.addAll(resultObjNameSet);
			
			// clear result set
			resultObjNameSet.clear();
			resultSimpleSet.clear();			
			
    		// log debug message
    		if (logger.isDebugEnabled()) logger.debug("Query set: " + ReflectionToStringBuilder.toString(queryObjNameSet.toArray()));    		    		        		    			
    		
    		// determine if expression contains a predicate
    		boolean exprContainsPredicate = containsPredicate(expression);
    		
    		// parse predicate
    		if (exprContainsPredicate) {
    			predicate = getPredicate(expression);
    			
        		// strip predicate from expression
        		expression = stripPredicate(expression);    			
    		}
    		    		
        	// iterate over the object name in the query set
        	for (ObjectName queryObjName : queryObjNameSet) {

        		// get attribute
        		Object queryResult = getAttributeValue(queryObjName, expression);

        		// evaluate expression
        		if (exprContainsPredicate) {
        			    			
        			// evaluate predicate
        			queryResult = evaluatePredicate(queryResult , predicate);
        		}
        		
        		// test
        		assertNotNull( "Query result is null.", queryResult);    		
        		
        		
        		if (queryResult instanceof ObjectName) {
        			
            		// handle object name result
        			resultObjNameSet.add((ObjectName) queryResult);
        		} else if (queryResult instanceof ObjectName[]) {
        			
            		// handle object name array result        		
        			// type cast 
        			ObjectName[] queryResultArray = (ObjectName[]) queryResult;
        			for (ObjectName someObjName : queryResultArray ) {
        				resultObjNameSet.add(someObjName);
        			}
        		} else {
            		// handle simple result
            		resultSimpleSet.add(queryResult);        			
        		}
        		        		
        		// log debug message
        		if (logger.isDebugEnabled()) {
        			logger.debug("ObjectName result set: " + ReflectionToStringBuilder.toString(resultObjNameSet.toArray()));
        			logger.debug("Simple result set: " + ReflectionToStringBuilder.toString(resultSimpleSet.toArray()));
        		}
        			    		    		        		
        	}
        	
        	// break if simple result, since we can query on simple results 
        	if (!resultSimpleSet.isEmpty()) {
        		break;
        	}
    		    		
    	}

    	// handle simple result
    	if (!resultSimpleSet.isEmpty()) {

        	// create result array 
        	Object[] resultArray = new Object[resultSimpleSet.size()];
        	resultArray = resultSimpleSet.toArray(resultArray);

        	// if result array contains a single entry then return the object name
        	if (resultArray.length == 1) return resultArray[0];
        	
        	// return as object name array
        	return resultArray;    		
    	}
    	
    	// create result array 
    	ObjectName[] resultArray = new ObjectName[resultObjNameSet.size()];
    	resultArray = resultObjNameSet.toArray(resultArray);

    	// if result array contains a single entry then return the object name
    	if (resultArray.length == 1) return resultArray[0];
    	
    	// return as object name array
    	return resultArray;
    }

    
    
	/**
     * Evaluate predicate.
     * 
     * @param result Query result.
	 * @param result2 
     * 
     * @param predicate Predicate to evaluate.
     * 
     * @return result.
     */    
    Object evaluatePredicate(Object result, String predicate) {
    	
    	if (isIntegerPredicate(predicate)) {
    		return evaluateIntegerPredicate(result, predicate);
    	}

    	if (isAttributeValuePredicate(predicate)) {
    		return evaluateAttributeValuePredicate(result, predicate);
    	}
    	
    	throw new UnsupportedOperationException("Unsupported predicate: " + predicate);
	}

    /**
     * Returns true if predicate is an attribute with a value predicate [attr='value'].
     * 
     * @param predicate Predicate to inspect.
     * 
     * @return true if predicate is an integer attribute with a value predicate .
     */
	boolean isAttributeValuePredicate(String predicate) {
		
		if (!predicate.startsWith("@")) return false;
		if (!predicate.contains("='")) return false;
		if (!predicate.endsWith("'")) return false;
		
		// get attribute
		String attribute = StringUtils.substringBetween( predicate, "@", "='");
		if (StringUtils.isEmpty(attribute)) return false;
		if (!StringUtils.isAlphanumeric(attribute)) return false;		
		return true;
	}
    
	/**
     * Evaluate attribute value predicate.
     * 
     * @param result Query result.
     * 
     * @param predicate attribute value predicate
     * 
     * @return result.
     */
	Object evaluateAttributeValuePredicate(Object result, String predicate) {
		
		// test
    	assertNotNull("Result is null.", result);
    	
		// get attribute
		String attribute = StringUtils.substringBetween(predicate, "@", "='");
		
		// get value 
		String attributeValue = StringUtils.substringBetween(predicate, "='", "'");
		
		// create result container
		ArrayList<ObjectName> resultContainer =  new ArrayList<ObjectName>();

		// handle object name case
		if(result instanceof ObjectName) {			
			ObjectName[] resultObjNameArray = { (ObjectName) result }; 
			result = resultObjNameArray;						
		}
		
		// handle object name array case
		if(result instanceof ObjectName[]) {

	    	// type cast 
	    	ObjectName[] objNameArray = (ObjectName[]) result;
	    			
			// iterate over the results
			for (ObjectName objName : objNameArray) {
				
	    		// declare result
	    		Object queryResult;
	    		
				try {
		    		// get attribute value				
					queryResult = getAttributeValue(objName, attribute);				
				} catch (Exception e) {				
		    		// query failed 
					queryResult = null;
				}

	    		// add object name to results if the values match
	    		if (queryResult != null && attributeValue.equals(queryResult)) {
	    			resultContainer.add(objName);
	    		}
			}
			
			ObjectName[] resultObjNameArray = new ObjectName[resultContainer.size()]; 
			return resultContainer.toArray(resultObjNameArray);			
		}
				
		// handle default case
    	assertTrue("Failed to handle evaluate predicate on unsupported type:" + result, true);
    	return null;
	}
	
    /**
     * Returns true if predicate is an integer predicate [n].
     * 
     * @param predicate Predicate to inspect.
     * 
     * @return true if predicate is an integer predicate.
     */
	boolean isIntegerPredicate(String predicate) {
		try {
			// parse to provoke exception if predicate isn't a number
			Integer.parseInt(predicate);
			return true;			
		} catch (Exception e) {
			return false;
		}		
	}

	/**
     * Evaluate integer predicate.
     * 
     * @param result Query result.
     * 
     * @param predicate Integer predicate
     * 
     * @return result.
     */
	Object evaluateIntegerPredicate(Object result, String predicate) {
		
		// test
    	assertEquals(ObjectName[].class, result.getClass());
		
    	// type cast 
    	Object[] array = (Object[]) result;

    	// parse integer
    	int index = Integer.parseInt(predicate);
    
    	// test 
    	assertTrue( "Index predicate [" + index + "] is larger than array size [" + array.length + "]",
    			index < array.length );
    	
    	return array[index];
	}

	/**
     * Strip predicate from expression
     * 
     * @param expression Expression to strip predicate from.
     * 
     * @return Expression without predicate..
     */    
    String stripPredicate(String expression) {
    	return StringUtils.substringBefore(expression, "[");
	}

	/**
     * Get predicate from expression. The returned predicate is 
     * stripped of the enclosing "[...]". 
     * 
     * @param expression Expression to extract predicate from.
     * 
     * @return Extracted predicate.
     */
    String getPredicate(String expression) {
    	return StringUtils.substringBetween(expression, "[", "]");
	}

	/**
     * Return true if expression contains predicate.
     *  
     * @param expression Expression to evaluate.
     * 
     * @return true if expression contains predicate.
     * 
     */
    boolean containsPredicate(String expression) {    	
    	return StringUtils.contains(expression, "[");
	}
	
    /**
     * get attribute value on MBean.
     * 
     * @param objName MBean object name which is searched for attributes.
     * @param attributeID Name of the MBean attribute.  
 
     * @throws Exception If getting attribute value from MBean fails.
     */
    public Object getAttributeValue( ObjectName objName, String attributeID ) throws Exception	
    {
		// log debug message
		if (logger.isDebugEnabled()) logger.debug("Will get attribute [" + attributeID + "] on MBean [" + objName + "]" );
    	
        return connection.getAttribute( objName, attributeID );
    }
    
}
