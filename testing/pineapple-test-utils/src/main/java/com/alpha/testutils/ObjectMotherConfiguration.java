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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.ExpressionEngine;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for
 * unit testing classes which uses the Apache Commons Configuration API.
 */
public class ObjectMotherConfiguration {

    public static final String ELEMENT_LEVEL_1 = "level-1";

    /**
     * Name of default level-2 element
     */
    public static final String ELEMENT_LEVEL_2 = "level-2";

    /**
     * Defines the index in a list to access the first entry.
     */
    public static final int FIRST_LIST_ENTRY = 0;

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Save properties configuration.
     * 
     * @param sourceConfiguration
     *            The configuration which should be saved.
     */
    public void savePropertiesConfiguration(String fileName) {
	PropertiesConfiguration config;
	config = new PropertiesConfiguration();

	config.addProperty("property1", "value1");
	config.addProperty("property2", "value2");
	config.addProperty("property3", "value3");

	savePropertiesConfiguration(fileName, config);
    }

    /**
     * Save properties configuration.
     * 
     * @param fileName
     *            File name where the configuration should be saved to.
     * @param config
     *            The configuration which should be saved.
     */
    public void savePropertiesConfiguration(String fileName, Configuration config) {
	try {
	    PropertiesConfiguration propertiesConfig;
	    propertiesConfig = new PropertiesConfiguration();

	    // migrate configuration content to properties configuration
	    Iterator iterator = config.getKeys();
	    while (iterator.hasNext()) {
		String key = (String) iterator.next();
		propertiesConfig.setProperty(key, config.getProperty(key));
	    }

	    // save
	    propertiesConfig.save(fileName);

	    // log debug message
	    // log error message
	    StringBuilder message = new StringBuilder();
	    message.append("Saving properties configuration to <");
	    message.append(fileName);
	    message.append(">.");
	    logger.debug(message.toString());

	} catch (ConfigurationException e) {
	    // fail test
	    fail("Properties configuration save failed.");
	}
    }

    /**
     * Save XML configuration. The configuration contains three properties, i.e.
     * propertyN = valueN.
     * 
     * @param sourceConfiguration
     *            The configuration which should be saved.
     */
    public void saveXMLConfiguration(String fileName) {
	try {
	    // required whitespace before key
	    final String XPATH_WHITESPACE = " ";
	    XMLConfiguration xmlConfig;
	    xmlConfig = createNullXMLConfiguration();
	    xmlConfig.addProperty(XPATH_WHITESPACE + "property1", "value1");
	    xmlConfig.addProperty(XPATH_WHITESPACE + "property1", "value1");
	    xmlConfig.addProperty(XPATH_WHITESPACE + "property1", "value1");

	    // save
	    xmlConfig.save(fileName);

	    // log debug message
	    StringBuilder message = new StringBuilder();
	    message.append("Saving XML configuration to <");
	    message.append(fileName);
	    message.append(">.");
	    logger.debug(message.toString());
	} catch (ConfigurationException e) {
	    // fail test
	    fail("XMLConfiguration save failed.");
	}
    }

    /**
     * List content of configuration object to debug logger.
     * 
     * @param config
     *            configuration object.
     */
    public void printConfiguration(Configuration config) {

	// list properties
	logger.debug("Listing properties in configuration:");
	Iterator keysIterator = config.getKeys();
	while (keysIterator.hasNext()) {
	    String key = (String) keysIterator.next();
	    logger.debug(key + "=" + config.getProperty(key));
	}
    }

    /**
     * <p>
     * Create null XML Configuration, i.e. an empty configuration object.
     * </p>
     * 
     * <p>
     * the configuration is configured with XPathExpressionEngine and with
     * delimiter parsing disabled.
     * </p>
     * 
     * @return null XML Configuration
     */
    public XMLConfiguration createNullXMLConfiguration() {
	XMLConfiguration config;
	config = new XMLConfiguration();

	// disable parsing of properties into lists
	config.setDelimiterParsingDisabled(true);

	// set XPath expression engine
	config.setExpressionEngine(new XPathExpressionEngine());

	return config;
    }

    /**
     * <p>
     * Create null XML Configuration, i.e. an empty configuration object.
     * </p>
     * 
     * <p>
     * The configuration is configured with XPathExpressionEngine and with
     * delimiter parsing enabled.
     * </p>
     * 
     * @return null XML Configuration
     */
    public XMLConfiguration createNullXMLConfigurationWithDelimiterParsingEnabled() {
	XMLConfiguration config;
	config = new XMLConfiguration();

	// disable parsing of properties into lists
	config.setDelimiterParsingDisabled(false);

	// set XPath expression engine
	config.setExpressionEngine(new XPathExpressionEngine());

	return config;
    }

    /**
     * <p>
     * Create null XML Configuration, i.e. an empty configuration object.
     * </p>
     * 
     * <p>
     * The configuration is configured with XPathExpressionEngine and with
     * delimiter parsing disabled.
     * </p>
     * 
     * @return null XML Configuration
     */
    public XMLConfiguration createNullXMLConfigurationWithDelimiterParsingDisabled() {
	XMLConfiguration config;
	config = new XMLConfiguration();

	// disable parsing of properties into lists
	config.setDelimiterParsingDisabled(true);

	// set XPath expression engine
	config.setExpressionEngine(new XPathExpressionEngine());

	return config;
    }

    /**
     * Create null combined Configuration, i.e. an empty configuration object.
     * Is configured with XPathExpressionEngine.
     * 
     * @return null XML Configuration
     */
    public CombinedConfiguration createNullCombinedConfiguration() {
	CombinedConfiguration config;
	config = new CombinedConfiguration();

	// disable parsing of properties into lists
	config.setDelimiterParsingDisabled(true);

	// set XPath expression engine
	config.setExpressionEngine(new XPathExpressionEngine());

	return config;
    }

    /**
     * Returns the first level-2 node from the first level-1 node.
     * 
     * @param config
     *            The configuration where the node should be returned from.
     * 
     * @return the first level-2 node from the first level-1 node.
     */
    public ConfigurationNode getFirstLevel2Element(XMLConfiguration config) {
	ConfigurationNode level1Node;
	level1Node = (ConfigurationNode) config.getRootNode().getChildren(ELEMENT_LEVEL_1).get(0);
	ConfigurationNode level2Node;
	level2Node = (ConfigurationNode) level1Node.getChildren(ELEMENT_LEVEL_2).get(0);
	return level2Node;
    }

    /**
     * Returns the value of a level-2 element.
     * 
     * @param propertyName
     *            Value of the Name element.
     * @param config
     *            the configuration where the value should be returned from.
     * 
     * @return the value of the level-2 element.
     */
    public String getLevel2ElementValueByName(String propertyName, HierarchicalConfiguration config) {
	// create XPath expression
	StringBuilder xpathExp = new StringBuilder();
	xpathExp.append("/level-1/level-2/");
	xpathExp.append(propertyName);

	// do query
	List<ConfigurationNode> actual = queryNodes(config, xpathExp.toString());

	// get first node
	return (String) actual.get(FIRST_LIST_ENTRY).getValue();
    }

    /**
     * Returns a list of configuration nodes based on an XPath query expression.
     * 
     * @param config
     *            The configuration where the node should be returned from.
     * @param XPAth
     *            expression used to find the nodes.
     * 
     * @return a list of configuration nodes.
     */
    public List<ConfigurationNode> queryNodes(HierarchicalConfiguration config, String xpathExp) {

	// log debug message
	StringBuilder message = new StringBuilder();
	message.append("Searching node with XPath expression <");
	message.append(xpathExp);
	message.append(">.");
	logger.debug(message.toString());

	// get expression engine
	ExpressionEngine engine = config.getExpressionEngine();

	// get query root node
	ConfigurationNode rootNode = config.getRootNode();

	// use XPath expression to identify target nodes to which the macro
	// should be applied
	List<ConfigurationNode> queryResult;
	queryResult = engine.query(rootNode, xpathExp);

	// log debug message
	message = new StringBuilder();
	message.append("found <");
	message.append(queryResult.size());
	message.append("> nodes.");
	logger.debug(message.toString());

	return queryResult;
    }

    /**
     * Add level-1 element named "level-1" to configuration with a null value
     * "".
     * 
     * @param config
     *            The configuration to which the element is added.
     */
    public void addLevelOneElement(XMLConfiguration config) {
	addLevelOneElement(config, ELEMENT_LEVEL_1, "");
    }

    /**
     * Add level-1 element to configuration. The element can have any name and
     * value.
     * 
     * @param config
     *            The configuration to which the element is added.
     * @param name
     *            Name of the element.
     * @param value
     *            the value of the element.
     */
    public void addLevelOneElement(XMLConfiguration config, String name, String value) {
	// create XPath expression
	StringBuilder xpathExp = new StringBuilder();
	xpathExp.append("/ " + name);

	// log debug message
	logger.debug("Adding element with XPath expression: " + xpathExp.toString());

	// add root element
	config.addProperty(xpathExp.toString(), value);
    }

    /**
     * Add level-2 element named "level-2" to configuration with a null value,
     * i.e. "". The property is added at the XPath location: /level-1/level-2.
     * 
     * @param config
     *            The configuration to which the element is added.
     */
    public void addLevelTwoElement(XMLConfiguration config) {
	addLevelTwoElement(config, ELEMENT_LEVEL_1, ELEMENT_LEVEL_2);
    }

    /**
     * Add level-2 element to configuration with a null value, i.e. "". The
     * property is added at the XPath location:
     * /&lt;level1ElementName&gt;/&lt;level2ElementName&gt;.
     * 
     * @param config
     *            The configuration to which the element is added.
     *
     * @param level1ElementName
     *            Name of level-1 element.
     * @param level2ElementName
     *            Name of level-2 element.
     * 
     */
    public void addLevelTwoElement(XMLConfiguration config, String level1ElementName, String level2ElementName) {
	// create XPath expression
	StringBuilder xpathExp = new StringBuilder();
	xpathExp.append("/");
	xpathExp.append(level1ElementName);
	xpathExp.append(" ");
	xpathExp.append(level2ElementName);

	// log debug message
	logger.debug("Adding element with XPath expression: " + xpathExp.toString());

	// add element with a empty value
	config.addProperty(xpathExp.toString(), "");
    }

    /**
     * Add level-2 element named "level-2" and level-3 element to configuration.
     * The level-3 element can have variable name and value. The level3 element
     * is added at the XPath location: /level1/level2/&lt;level3ElementName&gt;
     * 
     * @param config
     *            The configuration to which the element is added.
     * @param property
     *            The name of the level-3 property.
     * @param value
     *            The value of the level-3 property.
     */
    public void addLevelTwoElementWithLevelthreeElement(XMLConfiguration config, String property, String value) {
	// create XPath expression
	StringBuilder xpathExp = new StringBuilder();
	xpathExp.append("/level-1 level-2/");
	xpathExp.append(property);

	// log debug message
	logger.debug("Adding element with XPath expression: " + xpathExp.toString());

	// add element with a wild card
	config.addProperty(xpathExp.toString(), value);
    }

    /**
     * Add level-2 element named "level-2" and level-3 element named "Name" to
     * configuration. The level-3 element can have variable value. The level3
     * element is added at the XPath location: /level1/level2/Name
     * 
     * @param config
     *            The configuration to which the element is added.
     */
    public void addLevelTwoElementWithLevelThreeNameElement(XMLConfiguration config, String nameValue) {
	addLevelTwoElementWithLevelthreeElement(config, "Name", nameValue);
    }

    /**
     * Add level-N element to configuration with a null value, i.e. "". The
     * property is added at the XPath location:
     * /&lt;nodeXPath&gt;/&lt;elementName&gt;.
     * 
     * @param config
     *            The configuration to which the element is added.
     *
     * @param level1ElementName
     *            Name of level-1 element.
     * @param level2ElementName
     *            Name of level-2 element.
     * 
     */
    public void addElement(XMLConfiguration config, String nodeXPath, String elementName) {
	// create XPath expression
	StringBuilder xpathExp = new StringBuilder();
	xpathExp.append(nodeXPath);
	xpathExp.append(" ");
	xpathExp.append(elementName);

	// log debug message
	logger.debug("Adding element with XPath expression: " + xpathExp.toString());

	// add element with a empty value
	config.addProperty(xpathExp.toString(), "");
    }

    /**
     * Add attribute to element. The attribute is added at the XPath location:
     * /&lt;nodeXPath&gt;[&lt;attrName&gt;]
     * 
     * @param config
     *            The configuration to which the element is added.
     * @param nodeXPath
     *            XPath to the element to which the attribute is added.
     * @param attrName
     *            The name of the attribute
     * @param attrValue
     *            The value of the attribute.
     * 
     */
    public void addAttributeToElement(XMLConfiguration config, String nodeXPath, String attrName, String attrValue) {
	// create XPath expression
	StringBuilder xpathExp = new StringBuilder();
	xpathExp.append(nodeXPath);
	xpathExp.append(" @");
	xpathExp.append(attrName);

	logger.debug("Adding attribute with XPath expression: " + xpathExp.toString());

	// add element with a wild card
	config.addProperty(xpathExp.toString(), attrValue);
    }

    /**
     * Add attribute to element. The attribute is added at the XPath location:
     * /&lt;nodeXPath&gt;/&lt;attrXpathPrefix&gt;[&lt;attrName&gt;]
     * 
     * @param config
     *            The configuration to which the element is added.
     * @param nodeXPath
     *            XPath to the element to which the attribute is added.
     * @param attrName
     *            The name of the attribute
     * @param attrValue
     *            The value of the attribute.
     * 
     */
    public void addAttributeToElement(XMLConfiguration config, String nodeXPath, String attrXpathPrefix,
	    String attrName, String attrValue) {
	// create XPath expression
	StringBuilder xpathExp = new StringBuilder();
	xpathExp.append(nodeXPath);
	xpathExp.append(" ");
	xpathExp.append(attrXpathPrefix);
	xpathExp.append("@");
	xpathExp.append(attrName);

	logger.debug("Adding attribute with XPath expression: " + xpathExp.toString());

	// add element with a wild card
	config.addProperty(xpathExp.toString(), attrValue);
    }

    /**
     * Add attribute to level-2 element named "level-2" with a level-3 element
     * named "Name" depending on value of the Name element. The attribute can
     * have variable name and value. The attribute is added at the XPath
     * location: /level1/level2/[Name=??]
     * 
     * 
     * @param config
     *            The configuration to which the element is added.
     * @param level2Name
     *            The name of the level2 element to which the attribute is
     *            added.
     * @param attrName
     *            The name of the attribute
     * @param attrValue
     *            The value of the attribute.
     * 
     */
    public void addAttributeToLevel2Element(XMLConfiguration config, String level2Name, String attrName,
	    String attrValue) {
	// create XPath expression
	StringBuilder xpathExp = new StringBuilder();
	xpathExp.append("/level-1/level-2[Name='");
	xpathExp.append(level2Name);
	xpathExp.append("'] @");
	xpathExp.append(attrName);

	logger.debug("Adding attribute with XPath expression: " + xpathExp.toString());

	// add element with a wild card
	config.addProperty(xpathExp.toString(), attrValue);
    }

    /**
     * Create combined configuration object, with defined root element.
     * 
     * @return combined configuration object with defined root element.
     */
    public CombinedConfiguration createCombinedConfigurationWithRootElement() {
	CombinedConfiguration config = createNullCombinedConfiguration();

	// add root node
	ConfigurationNode rootNode = new HierarchicalConfiguration.Node("root-element");
	config.setRootNode(rootNode);

	return config;
    }

    /**
     * Create and save XMLConfiguration with a single level-2 property.
     * 
     * @param property
     *            Name of the level-2 element.
     * @param propertyValue
     *            Value of the level-2 element.
     * @param fqFileName
     *            Fully qualified path to where the configuration should be
     *            saved.
     */
    public void createAndSaveXMLConfigWithSingleLevel2Element(String property, String propertyValue,
	    String fqFileName) {
	try {
	    // create configuration
	    XMLConfiguration config = createNullXMLConfiguration();
	    addLevelOneElement(config);
	    addLevelTwoElementWithLevelthreeElement(config, property, propertyValue);
	    config.save(fqFileName);
	} catch (ConfigurationException e) {
	    fail(StackTraceHelper.getStrackTrace(e));
	}
    }

    /**
     * Create and save XMLCofiguration with a two level-2 properties with a
     * child name element.
     * 
     * @param property
     *            Value of the child elements. The properties are post fixed
     *            with the integers 1 and 2.
     * @param propertyValue
     *            Value of the child elements. The value are post fixed with the
     *            integers 1 and 2.
     * @param fqFileName
     *            Fully qualified path to where the configuration should be
     *            saved.
     */
    public void createAndSaveXMLConfigWithTwoLevel2Element(String property, String propertyValue, String fqFileName) {
	try {
	    // create configuration
	    XMLConfiguration config = createNullXMLConfiguration();
	    addLevelOneElement(config);
	    addLevelTwoElementWithLevelthreeElement(config, property + "1", propertyValue + "1");
	    addLevelTwoElementWithLevelthreeElement(config, property + "2", propertyValue + "2");
	    config.save(fqFileName);
	} catch (ConfigurationException e) {
	    fail(StackTraceHelper.getStrackTrace(e));
	}
    }

}
