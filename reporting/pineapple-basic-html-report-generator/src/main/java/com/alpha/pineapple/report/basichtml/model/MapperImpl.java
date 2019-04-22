/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2019 Allan Thrane Andersen..
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

package com.alpha.pineapple.report.basichtml.model;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.model.report.basichtml.Message;
import com.alpha.pineapple.model.report.basichtml.MessageValue;
import com.alpha.pineapple.model.report.basichtml.ObjectFactory;
import com.alpha.pineapple.model.report.basichtml.Report;
import com.alpha.pineapple.model.report.basichtml.Result;

/**
 * Implementation of the <code>Mapper</code> interface which maps execution
 * results into schema generated objects.
 */
public class MapperImpl implements Mapper {

	/**
	 * Null message value.
	 */
	static final String NULL_MESSAGE_VALUE = "n/a (value was null).";

	/**
	 * Message splitting chars.
	 */
	static final String MSG_SEPARATOR_CHARS = "\n";

	/**
	 * Date format pattern.
	 */
	final static String DATE_FORMAT_PATTERN = "HH:mm:ss";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * JAXB factory.
	 */
	@Resource
	ObjectFactory objectFactory;

	/**
	 * ReportId counter for results
	 */
	int IdGenerator = 0;

	public Report createReport() {

		// reset id generator
		IdGenerator = 0;

		// create report
		return objectFactory.createReport();
	}

	public Result mapOperationToReport(Report reportRoot, ExecutionResult result) {

		// format start time
		FastDateFormat fastDateFormat = FastDateFormat.getInstance(DATE_FORMAT_PATTERN);
		String formatedTime = fastDateFormat.format(result.getStartTime());

		// create report object for operation
		Result operationResult = objectFactory.createResult();
		operationResult.setDescription(removeIllegalXmlChars(result.getDescription()));		
		operationResult.setStartTime(formatedTime);
		operationResult.setTime((float) result.getTime());
		operationResult.setState(result.getState().toString());

		// add number children
		int children = result.getChildren().length;
		operationResult.setChildren(BigInteger.valueOf(children));

		// add number errors
		int errors = result.getChildrenWithState(ExecutionState.ERROR).length;
		operationResult.setErrors(BigInteger.valueOf(errors));

		// add number failures
		int failures = result.getChildrenWithState(ExecutionState.FAILURE).length;
		operationResult.setFailures(BigInteger.valueOf(failures));

		// number successful children
		int successful = result.getChildrenWithState(ExecutionState.SUCCESS).length;
		operationResult.setSuccessful(BigInteger.valueOf(successful));

		// map messages
		operationResult.setMessages(objectFactory.createMessages());
		Map<String, String> sourceMessages = result.getMessages();
		List<Message> destinationMessages = operationResult.getMessages().getMessage();
		mapMessages(sourceMessages, destinationMessages);

		// result type as operation
		operationResult.setType("operation");

		// add report id
		operationResult.setReportId(BigInteger.valueOf(getNextResultId()));

		// add result too root
		reportRoot.setResult(operationResult);

		return operationResult;
	}

	public Result mapModelToReport(Result operation, ExecutionResult result) {

		// format start time
		String pattern = "HH:mm:ss";
		FastDateFormat fastDateFormat = FastDateFormat.getInstance(pattern);
		String formatedTime = fastDateFormat.format(result.getStartTime());

		// create report object for operation
		Result reportResult = objectFactory.createResult();
		reportResult.setDescription(removeIllegalXmlChars(result.getDescription()));
		reportResult.setStartTime(formatedTime);
		reportResult.setTime((float) result.getTime());
		reportResult.setState(result.getState().toString());

		// add number children
		int children = result.getChildren().length;
		reportResult.setChildren(BigInteger.valueOf(children));

		// add number errors
		int errors = result.getChildrenWithState(ExecutionState.ERROR).length;
		reportResult.setErrors(BigInteger.valueOf(errors));

		// add number failures
		int failures = result.getChildrenWithState(ExecutionState.FAILURE).length;
		reportResult.setFailures(BigInteger.valueOf(failures));

		// number successful children
		int successful = result.getChildrenWithState(ExecutionState.SUCCESS).length;
		reportResult.setSuccessful(BigInteger.valueOf(successful));

		// map messages
		reportResult.setMessages(objectFactory.createMessages());
		Map<String, String> sourceMessages = result.getMessages();
		List<Message> destinationMessages = reportResult.getMessages().getMessage();
		mapMessages(sourceMessages, destinationMessages);

		// result type as operation
		reportResult.setType("model");

		// add report id
		reportResult.setReportId(BigInteger.valueOf(getNextResultId()));

		// add result to operation
		operation.getResult().add(reportResult);

		// get child results
		ExecutionResult[] childResults = result.getChildren();

		// iterate over the child results
		for (ExecutionResult childResult : childResults) {
			mapChildResultToReport(reportResult, childResult);
		}

		return reportResult;
	}

	public Result mapChildResultToReport(Result parent, ExecutionResult result) {

		// format start time
		String pattern = "HH:mm:ss";
		FastDateFormat fastDateFormat = FastDateFormat.getInstance(pattern);
		String formatedTime = fastDateFormat.format(result.getStartTime());

		// create report object for operation
		Result reportResult = objectFactory.createResult();
		reportResult.setDescription(removeIllegalXmlChars(result.getDescription()));
		reportResult.setTime((float) result.getTime());
		reportResult.setStartTime(formatedTime);
		reportResult.setState(result.getState().toString());

		// add number children
		int children = result.getChildren().length;
		reportResult.setChildren(BigInteger.valueOf(children));

		// add number errors
		int errors = result.getChildrenWithState(ExecutionState.ERROR).length;
		reportResult.setErrors(BigInteger.valueOf(errors));

		// add number failures
		int failures = result.getChildrenWithState(ExecutionState.FAILURE).length;
		reportResult.setFailures(BigInteger.valueOf(failures));

		// number successful children
		int successful = result.getChildrenWithState(ExecutionState.SUCCESS).length;
		reportResult.setSuccessful(BigInteger.valueOf(successful));

		// map messages
		reportResult.setMessages(objectFactory.createMessages());
		Map<String, String> sourceMessages = result.getMessages();
		List<Message> destinationMessages = reportResult.getMessages().getMessage();
		mapMessages(sourceMessages, destinationMessages);

		// result type as operation
		reportResult.setType("default");

		// add report id
		reportResult.setReportId(BigInteger.valueOf(getNextResultId()));

		// add result to parent
		parent.getResult().add(reportResult);

		// get child results
		ExecutionResult[] childResults = result.getChildren();

		// iterate over the child results
		for (ExecutionResult childResult : childResults) {
			mapChildResultToReport(reportResult, childResult);
		}

		return reportResult;
	}

	/**
	 * Map messages.
	 * 
	 * @param sourceMessages
	 *            Map of source messages.
	 * @param destinationMessages
	 *            List of mapped messages.
	 */
	void mapMessages(Map<String, String> sourceMessages, List<Message> destinationMessages) {
		for (String key : sourceMessages.keySet()) {			
			Message message = objectFactory.createMessage();						
			message.setName(removeIllegalXmlChars(key));
			destinationMessages.add(message);

			// split message into multiple lines
			String[] lines = StringUtils.split(sourceMessages.get(key), MSG_SEPARATOR_CHARS);

			// add message lines
			if (lines != null) {
				for (String line : lines) {					
					
					// create value object
					MessageValue messageValue = objectFactory.createMessageValue();
					messageValue.setValue(removeIllegalXmlChars(line));
					message.getValue().add(messageValue);
				}

				// skip to next iteration
				continue;
			}

			// handle null case, add null message
			MessageValue messageValue = objectFactory.createMessageValue();
			messageValue.setValue(NULL_MESSAGE_VALUE);
			message.getValue().add(messageValue);
		}
	}

	/**
	 * Get next ID for a result.
	 * 
	 * @return next ID for a result.
	 */
	int getNextResultId() {
		this.IdGenerator++;
		return this.IdGenerator;
	}

	/**
	 * Resets the report ID generator
	 */
	void resetResultId() {
		this.IdGenerator = 0;
	}

	/**
	 * Remove illegal XML chars.
	 * 
	 * To removed all invalid XML chars then then all text is escaped to XML 1.0 
	 * and then unescaped again mediately. The purpose of the unescaping is to 
	 * let JAXB and the XSLT transformer handling the escaping of XML.
	 * 
	 * @param string to remove illegal XML chars from.
	 * 
	 * @return string without illegal XML chars.
	 */
	String removeIllegalXmlChars(String string) {
		var escaped = StringEscapeUtils.escapeXml10(string);
		return StringEscapeUtils.unescapeXml(escaped);		
	}
	
	
	
}
