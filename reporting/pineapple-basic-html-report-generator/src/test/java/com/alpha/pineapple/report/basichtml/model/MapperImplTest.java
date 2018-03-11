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


package com.alpha.pineapple.report.basichtml.model;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.model.report.basichtml.Result;
import com.alpha.pineapple.model.report.basichtml.Message;
import com.alpha.pineapple.model.report.basichtml.MessageValue;
import com.alpha.pineapple.model.report.basichtml.Messages;
import com.alpha.pineapple.model.report.basichtml.ObjectFactory;
import com.alpha.pineapple.model.report.basichtml.Report;

/**
 * Unit test of the class <code>MapperImpl</code>.
 */
public class MapperImplTest {

	/**
	 * Report result type for operation.
	 */
	static final String OPERATION_TYPE = "operation";
	
	/**
	 * Execution result description
	 */
	static final String DESC = "some-description";
	
	/**
	 * Execution time.
	 */
	static final Long EXECUTION_TIME = Long.valueOf(99);	
	
	/**
	 * Error state for report object.
	 */	
	static final String ERROR_STATE = "ERROR";
	
	/**
	 * Failed state for report object.
	 */	
	static final String FAILURE_STATE = "FAILURE";

	/**
	 * Successful state for report object.
	 */
	static final String SUCCESS_STATE = "SUCCESS";

	/**
	 * Executing state for report object.
	 */
	static final String EXECUTING_STATE = "EXECUTING";	
	
	/**
	 * Operation name.
	 */
	static final String OPERATION = "operation on module";
	
	/**
	 * First Report id.
	 */
	static final int FIRST_REPORT_ID = 1;
	
	/**
	 * Object under test.
	 */
	Mapper mapper;

	/**
	 * Mock execution result 
	 */
	ExecutionResult executionResult;
	
	/**
	 * Mock JAXB object factory 
	 */
	ObjectFactory objectFactory;	
	
	/**
	 * Mock report object
	 */
	Report report;
	
	/**
	 * Mock report result object.
	 */
	Result reportResult;
	
	/**
	 * Mock report result messages object. 
	 */
	Messages Messages;

	/**
	 * Random start time.
	 */
	Long randomStartTime;	

	/**
	 * Random String.
	 */
	String randomString;	

	/**
	 * Random Name.
	 */
	String randomName;	
	
	@Before
	public void setUp() throws Exception {		
		randomStartTime = RandomUtils.nextLong();
		randomString = RandomStringUtils.randomAlphabetic(10);
		randomName= RandomStringUtils.randomAlphabetic(10);
		
		mapper = new MapperImpl();
		
		// create mock execution result
        executionResult = EasyMock.createMock( ExecutionResult.class );
        
		// create mock object factory
        objectFactory = org.easymock.classextension.EasyMock.createMock( ObjectFactory.class );

		// create mock report result configuration
		reportResult = org.easymock.classextension.EasyMock.createMock( Result.class);

		// create mock report messages type
		Messages = org.easymock.classextension.EasyMock.createMock( Messages.class);

		// complete mock report configuration
		report = org.easymock.classextension.EasyMock.createMock( Report.class);
		report.setResult( reportResult);
		org.easymock.classextension.EasyMock.expectLastCall().anyTimes();
		org.easymock.classextension.EasyMock.expect( report.getResult()).andReturn( reportResult).anyTimes();
		org.easymock.classextension.EasyMock.replay( report );
		
        // inject object factory into mapper
        ReflectionTestUtils.setField( mapper, "objectFactory", objectFactory, ObjectFactory.class );        
	}

	@After
	public void tearDown() throws Exception {
		mapper = null;
		executionResult = null;
		reportResult = null;
		Messages = null;
		objectFactory = null;
		report = null;
	}

	/**
	 * Complete mock factory configuration.
	 */
	void completeMockObjectFactorySetup() {
		org.easymock.classextension.EasyMock.expect( objectFactory.createReport()).andReturn(report);
		org.easymock.classextension.EasyMock.expect( objectFactory.createResult()).andReturn(reportResult);
		org.easymock.classextension.EasyMock.expect( objectFactory.createMessages()).andReturn(Messages);		
		org.easymock.classextension.EasyMock.replay( objectFactory );
	}	

	/**
	 * Complete mock execution result configuration.
	 * 
	 * @param state Execution result state. 
	 */	
	void completeMockExecutionResultSetup(ExecutionState state) {
		HashMap<String, String> messages = new HashMap<String, String>();
		completeMockExecutionResultSetup(state, messages);
	}

	/**
	 * Complete mock execution result configuration.
	 * 
	 * @param state Execution result state.
	 * @param message Execution result messages. 
	 */	
	void completeMockExecutionResultSetup(ExecutionState state, HashMap<String, String> messages ) {
		ExecutionResult[] errors = new ExecutionResult[0];		
		ExecutionResult[] failures = new ExecutionResult[0];
		ExecutionResult[] success = new ExecutionResult[0];		
				
		// complete mock execution result configuration
		EasyMock.expect(executionResult.getDescription()).andReturn(DESC);
		EasyMock.expect(executionResult.getStartTime()).andReturn(randomStartTime);				
		EasyMock.expect(executionResult.getTime()).andReturn(EXECUTION_TIME);
		EasyMock.expect(executionResult.getState()).andReturn(state);
		EasyMock.expect(executionResult.getChildren()).andReturn(errors);		
		EasyMock.expect(executionResult.getChildrenWithState(ExecutionState.ERROR)).andReturn(errors);		
		EasyMock.expect(executionResult.getChildrenWithState(ExecutionState.FAILURE)).andReturn(failures);		
		EasyMock.expect(executionResult.getChildrenWithState(ExecutionState.SUCCESS)).andReturn(success);		
		EasyMock.expect(executionResult.getMessages()).andReturn(messages);		
		EasyMock.replay( executionResult );
	}
	
	
	/**
	 * Complete mock report messages type configuration.
	 */	
	void completeMockMessagesSetup() {
		org.easymock.classextension.EasyMock.expect( Messages.getMessage()).andReturn(new ArrayList<Message>());
		org.easymock.classextension.EasyMock.replay( Messages );
	}		
	
	/**
	 * Complete mock report result configuration.
	 * 
	 * @param state Execution result state. 
	 */	
	void completeMockReportResultSetup(String state, int messagesAccessed ) {
		
		// format start time
        FastDateFormat fastDateFormat = FastDateFormat.getInstance(MapperImpl.DATE_FORMAT_PATTERN);        
        String formatedTime = fastDateFormat.format(randomStartTime);
		
		org.easymock.classextension.EasyMock.expect( reportResult.getMessages()).andReturn( Messages ).times( messagesAccessed );
		reportResult.setDescription( DESC );
		reportResult.setStartTime(formatedTime);
		reportResult.setTime(EXECUTION_TIME.floatValue() );
		reportResult.setState(state);
		reportResult.setChildren(BigInteger.ZERO);	
		reportResult.setErrors(BigInteger.ZERO);
		reportResult.setFailures(BigInteger.ZERO);		
		reportResult.setSuccessful(BigInteger.ZERO);
		reportResult.setMessages(Messages);
		reportResult.setType(OPERATION_TYPE);
		reportResult.setReportId(BigInteger.ONE);
		org.easymock.classextension.EasyMock.replay( reportResult );
	}

	/**
	 * Complete mock report result configuration.
	 * 
	 * @param state Execution result state. 
	 */	
	void completeMockReportResultSetup(String state ) {
		completeMockReportResultSetup(state, 1); 
	}

	
	
	/**
	 * Test that report can be created.
	 */
	@Test
	public void testCreateReport() {
		
		// complete mock object factory configuration
		org.easymock.classextension.EasyMock.expect( objectFactory.createReport()).andReturn(report);
		org.easymock.classextension.EasyMock.replay( objectFactory );
		
		// create report
		Report report = mapper.createReport();
		
		// test
		assertEquals(report, report);
		
		// test
		org.easymock.classextension.EasyMock.verify(report);		
		org.easymock.classextension.EasyMock.verify( objectFactory );
	}

	
	/**
	 * Test that operation is mapped correct.  
	 */
	@Test
	public void testMapSuccessfulOperation() {

		// complete mock objects configuration
		completeMockObjectFactorySetup();			
		completeMockExecutionResultSetup(ExecutionState.SUCCESS);
		completeMockMessagesSetup();		
		completeMockReportResultSetup(SUCCESS_STATE);
		
		// create report
		Report report = mapper.createReport();
		
		// map 
		Result actualReportResult;
		actualReportResult = mapper.mapOperationToReport(report, executionResult);
		actualReportResult.hashCode();
				
		// test
		org.easymock.classextension.EasyMock.verify(report);		
		org.easymock.classextension.EasyMock.verify(objectFactory);
		EasyMock.verify(executionResult);
		org.easymock.classextension.EasyMock.verify(reportResult);	
		org.easymock.classextension.EasyMock.verify( Messages );
	}

	/**
	 * Test that operation is mapped correct.  
	 */
	@Test
	public void testMapFailedOperation() {

		// complete objects configuration
		completeMockObjectFactorySetup();			
		completeMockExecutionResultSetup(ExecutionState.FAILURE);
		completeMockMessagesSetup();		
		completeMockReportResultSetup(FAILURE_STATE);
		
		// create report
		Report report = mapper.createReport();
		
		// map 
		Result actualReportResult;
		actualReportResult = mapper.mapOperationToReport(report, executionResult);
		actualReportResult.hashCode();
				
		// test
		org.easymock.classextension.EasyMock.verify(objectFactory);
		EasyMock.verify(executionResult);
		org.easymock.classextension.EasyMock.verify(reportResult);	
		org.easymock.classextension.EasyMock.verify( Messages );
	}
	
	/**
	 * Test that operation is mapped correct.  
	 */
	@Test
	public void testMapOperationWithError() {

		// complete mock objects configuration
		completeMockObjectFactorySetup();			
		completeMockExecutionResultSetup(ExecutionState.ERROR);
		completeMockMessagesSetup();		
		completeMockReportResultSetup(ERROR_STATE);
		
		// create report
		Report report = mapper.createReport();
		
		// map 
		Result actualReportResult;
		actualReportResult = mapper.mapOperationToReport(report, executionResult);
		actualReportResult.hashCode();
				
		// test
		org.easymock.classextension.EasyMock.verify(report);		
		org.easymock.classextension.EasyMock.verify(objectFactory);
		EasyMock.verify(executionResult);
		org.easymock.classextension.EasyMock.verify(reportResult);	
		org.easymock.classextension.EasyMock.verify( Messages );
	}
	
	/**
	 * Test that operation is mapped correct.  
	 */
	@Test
	public void testMapOperationWithExecutingState() {

		// complete objects configuration
		completeMockObjectFactorySetup();			
		completeMockExecutionResultSetup(ExecutionState.EXECUTING);
		completeMockMessagesSetup();		
		completeMockReportResultSetup(EXECUTING_STATE);
		
		// create report
		Report report = mapper.createReport();
		
		// map 
		Result actualReportResult;
		actualReportResult = mapper.mapOperationToReport(report, executionResult);
		actualReportResult.hashCode();
				
		// test
		org.easymock.classextension.EasyMock.verify(report);		
		org.easymock.classextension.EasyMock.verify(objectFactory);
		EasyMock.verify(executionResult);
		org.easymock.classextension.EasyMock.verify(reportResult);	
		org.easymock.classextension.EasyMock.verify( Messages );
	}
	
	
	/**
	 * Test that operation with zero message is mapped correct.  
	 */
	@Test
	public void testMapOperationWithZeroMessages() {

		// Complete mock report messages type configuration.
		ArrayList<Message> messageList = new ArrayList<Message>();			
		org.easymock.classextension.EasyMock.expect( Messages.getMessage()).andReturn(messageList).times(2);		
		org.easymock.classextension.EasyMock.replay( Messages );
		
		// complete mock objects configuration
		completeMockObjectFactorySetup();			
		completeMockExecutionResultSetup(ExecutionState.SUCCESS);
		completeMockReportResultSetup(SUCCESS_STATE, 2);
		
		// create report
		Report report = mapper.createReport();
		
		// map 
		Result actualReportResult;
		actualReportResult = mapper.mapOperationToReport(report, executionResult);
		actualReportResult.hashCode();
		
		// test		
		assertEquals(0, report.getResult().getMessages().getMessage().size());
				
		// test
		org.easymock.classextension.EasyMock.verify(report);		
		org.easymock.classextension.EasyMock.verify(objectFactory);
		EasyMock.verify(executionResult);
		org.easymock.classextension.EasyMock.verify(reportResult);	
		org.easymock.classextension.EasyMock.verify( Messages );		
	}
	
	/**
	 * Test that operation with one message is mapped correct.  
	 */
	@Test
	public void testMapOperationWithOneMessage() {

		// Complete mock report message type configuration.		
		Message message = org.easymock.classextension.EasyMock.createMock( Message.class );
		org.easymock.classextension.EasyMock.replay( message );
		
		// Complete mock report messages type configuration.
		ArrayList<Message> messageList = new ArrayList<Message>();	
		messageList.add(message);		
		org.easymock.classextension.EasyMock.expect( Messages.getMessage()).andReturn(messageList).times(2);		
		org.easymock.classextension.EasyMock.replay( Messages );
		
		// complete mock objects configuration
		completeMockObjectFactorySetup();			
		completeMockExecutionResultSetup(ExecutionState.SUCCESS);
		completeMockReportResultSetup(SUCCESS_STATE, 2);
		
		// create report
		Report report = mapper.createReport();
		
		// map 
		Result actualReportResult;
		actualReportResult = mapper.mapOperationToReport(report, executionResult);
		actualReportResult.hashCode();
		
		// test		
		assertEquals(1, report.getResult().getMessages().getMessage().size());
				
		// test
		org.easymock.classextension.EasyMock.verify(report);		
		org.easymock.classextension.EasyMock.verify(objectFactory);
		EasyMock.verify(executionResult);
		org.easymock.classextension.EasyMock.verify(reportResult);	
		org.easymock.classextension.EasyMock.verify( Messages );
		org.easymock.classextension.EasyMock.verify( message );		
	}

	/**
	 * Test that operation with multiple messages is mapped correct.  
	 */
	@Test
	public void testMapOperationWithMultipleMessages() {

		// Complete mock report message type configuration.		
		Message message = org.easymock.classextension.EasyMock.createMock( Message.class );
		org.easymock.classextension.EasyMock.replay( message );

		Message message2 = org.easymock.classextension.EasyMock.createMock( Message.class );
		org.easymock.classextension.EasyMock.replay( message2 );
				
		// Complete mock report messages type configuration.
		ArrayList<Message> messageList = new ArrayList<Message>();	
		messageList.add(message);		
		messageList.add(message2);		
		org.easymock.classextension.EasyMock.expect( Messages.getMessage()).andReturn(messageList).times(2);		
		org.easymock.classextension.EasyMock.replay( Messages );

		// complete mock objects configuration
		completeMockObjectFactorySetup();
		completeMockExecutionResultSetup(ExecutionState.SUCCESS);		
		completeMockReportResultSetup(SUCCESS_STATE, 2);

		
		// create report
		Report report = mapper.createReport();
		
		// map 
		Result actualReportResult;
		actualReportResult = mapper.mapOperationToReport(report, executionResult);
		actualReportResult.hashCode();
		
		// test		
		assertEquals(2, report.getResult().getMessages().getMessage().size());
				
		// test
		org.easymock.classextension.EasyMock.verify(report);		
		org.easymock.classextension.EasyMock.verify(objectFactory);
		EasyMock.verify(executionResult);
		org.easymock.classextension.EasyMock.verify(reportResult);	
		org.easymock.classextension.EasyMock.verify( Messages );
		org.easymock.classextension.EasyMock.verify( message );		
	}
	
	/**
	 * Test that operation with one message whose content is null is mapped correct.  
	 */
	@Test
	public void testMapOperationWithNullMessage() {

		// Complete mock report message value configuration.		
		MessageValue MessageValue = org.easymock.classextension.EasyMock.createMock( MessageValue.class );
		MessageValue.setValue(MapperImpl.NULL_MESSAGE_VALUE);
		org.easymock.classextension.EasyMock.replay( MessageValue );
		
		// Complete mock report message type configuration.
		ArrayList<MessageValue> messageValueList = new ArrayList<MessageValue>();	
		Message message = org.easymock.classextension.EasyMock.createMock( Message.class );
		message.setName(randomString);
		org.easymock.classextension.EasyMock.expect(message.getValue()).andReturn(messageValueList);
		org.easymock.classextension.EasyMock.replay( message );
		
		// Complete mock report messages type configuration.
		ArrayList<Message> messageList = new ArrayList<Message>();			
		org.easymock.classextension.EasyMock.expect( Messages.getMessage()).andReturn(messageList).times(2);		
		org.easymock.classextension.EasyMock.replay( Messages );
		
		// complete execution result mock configuration
		HashMap<String, String> messages = new HashMap<String, String>();
		messages.put(randomString, null);
				
		// complete mock execution result configuration
		completeMockExecutionResultSetup(ExecutionState.SUCCESS, messages);

		// complete mock configuration
		org.easymock.classextension.EasyMock.expect( objectFactory.createReport()).andReturn(report);
		org.easymock.classextension.EasyMock.expect( objectFactory.createResult()).andReturn(reportResult);
		org.easymock.classextension.EasyMock.expect( objectFactory.createMessages()).andReturn(Messages);				
		org.easymock.classextension.EasyMock.expect( objectFactory.createMessage()).andReturn(message);
		org.easymock.classextension.EasyMock.expect( objectFactory.createMessageValue()).andReturn(MessageValue);
		org.easymock.classextension.EasyMock.replay( objectFactory );
		
		// complete mock configuration			
		completeMockReportResultSetup(SUCCESS_STATE, 2);
		
		// create report
		Report report = mapper.createReport();
		
		// map 
		Result actualReportResult;
		actualReportResult = mapper.mapOperationToReport(report, executionResult);
		actualReportResult.hashCode();
		
		// test		
		assertEquals(1, report.getResult().getMessages().getMessage().size());
				
		// test
		org.easymock.classextension.EasyMock.verify(report);		
		org.easymock.classextension.EasyMock.verify(objectFactory);
		EasyMock.verify(executionResult);
		org.easymock.classextension.EasyMock.verify(reportResult);	
		org.easymock.classextension.EasyMock.verify( messages);
		org.easymock.classextension.EasyMock.verify( message);	
		org.easymock.classextension.EasyMock.verify( MessageValue );
	}
	
}
