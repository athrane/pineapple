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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.model.report.basichtml.Message;
import com.alpha.pineapple.model.report.basichtml.MessageValue;
import com.alpha.pineapple.model.report.basichtml.Messages;
import com.alpha.pineapple.model.report.basichtml.ObjectFactory;
import com.alpha.pineapple.model.report.basichtml.Report;
import com.alpha.pineapple.model.report.basichtml.Result;

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
        executionResult = createMock( ExecutionResult.class );
        
		// create mock object factory
        objectFactory = createMock( ObjectFactory.class );

		// create mock report result configuration
		reportResult = createMock( Result.class);

		// create mock report messages type
		Messages = createMock( Messages.class);

		// complete mock report configuration
		report = createMock( Report.class);
		report.setResult( reportResult);
		expectLastCall().anyTimes();
		expect( report.getResult()).andReturn( reportResult).anyTimes();
		replay( report );
		
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
		expect( objectFactory.createReport()).andReturn(report);
		expect( objectFactory.createResult()).andReturn(reportResult);
		expect( objectFactory.createMessages()).andReturn(Messages);		
		replay( objectFactory );
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
		expect(executionResult.getDescription()).andReturn(DESC);
		expect(executionResult.getStartTime()).andReturn(randomStartTime);				
		expect(executionResult.getTime()).andReturn(EXECUTION_TIME);
		expect(executionResult.getState()).andReturn(state);
		expect(executionResult.getChildren()).andReturn(errors);		
		expect(executionResult.getChildrenWithState(ExecutionState.ERROR)).andReturn(errors);		
		expect(executionResult.getChildrenWithState(ExecutionState.FAILURE)).andReturn(failures);		
		expect(executionResult.getChildrenWithState(ExecutionState.SUCCESS)).andReturn(success);		
		expect(executionResult.getMessages()).andReturn(messages);		
		replay( executionResult );
	}
	
	
	/**
	 * Complete mock report messages type configuration.
	 */	
	void completeMockMessagesSetup() {
		expect( Messages.getMessage()).andReturn(new ArrayList<Message>());
		replay( Messages );
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
		
		expect( reportResult.getMessages()).andReturn( Messages ).times( messagesAccessed );
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
		replay( reportResult );
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
		expect( objectFactory.createReport()).andReturn(report);
		replay( objectFactory );
		
		// create report
		Report report = mapper.createReport();
		
		// test
		assertEquals(report, report);
		
		// test
		verify(report);		
		verify( objectFactory );
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
		verify(report);		
		verify(objectFactory);
		verify(executionResult);
		verify(reportResult);	
		verify( Messages );
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
		verify(objectFactory);
		verify(executionResult);
		verify(reportResult);	
		verify( Messages );
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
		verify(report);		
		verify(objectFactory);
		verify(executionResult);
		verify(reportResult);	
		verify( Messages );
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
		verify(report);		
		verify(objectFactory);
		verify(executionResult);
		verify(reportResult);	
		verify( Messages );
	}
	
	
	/**
	 * Test that operation with zero message is mapped correct.  
	 */
	@Test
	public void testMapOperationWithZeroMessages() {

		// Complete mock report messages type configuration.
		ArrayList<Message> messageList = new ArrayList<Message>();			
		expect( Messages.getMessage()).andReturn(messageList).times(2);		
		replay( Messages );
		
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
		verify(report);		
		verify(objectFactory);
		verify(executionResult);
		verify(reportResult);	
		verify( Messages );		
	}
	
	/**
	 * Test that operation with one message is mapped correct.  
	 */
	@Test
	public void testMapOperationWithOneMessage() {

		// Complete mock report message type configuration.		
		Message message = createMock( Message.class );
		replay( message );
		
		// Complete mock report messages type configuration.
		ArrayList<Message> messageList = new ArrayList<Message>();	
		messageList.add(message);		
		expect( Messages.getMessage()).andReturn(messageList).times(2);		
		replay( Messages );
		
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
		verify(report);		
		verify(objectFactory);
		verify(executionResult);
		verify(reportResult);	
		verify( Messages );
		verify( message );		
	}

	/**
	 * Test that operation with multiple messages is mapped correct.  
	 */
	@Test
	public void testMapOperationWithMultipleMessages() {

		// Complete mock report message type configuration.		
		Message message = createMock( Message.class );
		replay( message );

		Message message2 = createMock( Message.class );
		replay( message2 );
				
		// Complete mock report messages type configuration.
		ArrayList<Message> messageList = new ArrayList<Message>();	
		messageList.add(message);		
		messageList.add(message2);		
		expect( Messages.getMessage()).andReturn(messageList).times(2);		
		replay( Messages );

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
		verify(report);		
		verify(objectFactory);
		verify(executionResult);
		verify(reportResult);	
		verify( Messages );
		verify( message );		
	}
	
	/**
	 * Test that operation with one message whose content is null is mapped correct.  
	 */
	@Test
	public void testMapOperationWithNullMessage() {

		// Complete mock report message value configuration.		
		MessageValue MessageValue = createMock( MessageValue.class );
		MessageValue.setValue(MapperImpl.NULL_MESSAGE_VALUE);
		replay( MessageValue );
		
		// Complete mock report message type configuration.
		ArrayList<MessageValue> messageValueList = new ArrayList<MessageValue>();	
		Message message = createMock( Message.class );
		message.setName(randomString);
		expect(message.getValue()).andReturn(messageValueList);
		replay( message );
		
		// Complete mock report messages type configuration.
		ArrayList<Message> messageList = new ArrayList<Message>();			
		expect( Messages.getMessage()).andReturn(messageList).times(2);		
		replay( Messages );
		
		// complete execution result mock configuration
		HashMap<String, String> messages = new HashMap<String, String>();
		messages.put(randomString, null);
				
		// complete mock execution result configuration
		completeMockExecutionResultSetup(ExecutionState.SUCCESS, messages);

		// complete mock configuration
		expect( objectFactory.createReport()).andReturn(report);
		expect( objectFactory.createResult()).andReturn(reportResult);
		expect( objectFactory.createMessages()).andReturn(Messages);				
		expect( objectFactory.createMessage()).andReturn(message);
		expect( objectFactory.createMessageValue()).andReturn(MessageValue);
		replay( objectFactory );
		
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
		verify(report);		
		verify(objectFactory);
		verify(executionResult);
		verify(reportResult);	
		verify( messages);
		verify( message);	
		verify( MessageValue );
	}
	
}
