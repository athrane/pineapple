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

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.model.report.basichtml.Report;
import com.alpha.pineapple.model.report.basichtml.Result;

/**
 * Integration test of the class <code>MapperImpl</code>.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class})
@ContextConfiguration(locations = { "/com.alpha.pineapple.report.basichtml-config.xml" })
public class MapperImplIntegrationTest {

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
	 * Operation name.
	 */
	static final String OPERATION = "operation on module";

	/**
	 * First Report id.
	 */
	static final int FIRST_REPORT_ID = 1;

	/**
	 * First list index.
	 */
	static final int FIRST_LIST_INDEX = 0;

	/**
	 * Object under test.
	 */
	@Resource
	Mapper mapper;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		mapper = null;
	}

	/**
	 * Test that operation is mapped correct.
	 */
	@Test
	public void testMapSuccessfulOperation() {

		// create report
		Report report = mapper.createReport();

		// create execution results
		ExecutionResult result = new ExecutionResultImpl(OPERATION);
		result.setState(ExecutionState.SUCCESS);

		// map
		Result reportResult;
		reportResult = mapper.mapOperationToReport(report, result);

		// test
		assertEquals(result.getDescription(), reportResult.getDescription());
		assertEquals(SUCCESS_STATE, reportResult.getState());
		assertEquals("operation", reportResult.getType());
		assertEquals(0, reportResult.getChildren().intValue());
		assertEquals(0, reportResult.getErrors().intValue());
		assertEquals(0, reportResult.getFailures().intValue());
		assertEquals(true, reportResult.getMessages().getMessage().isEmpty());
		assertEquals(FIRST_REPORT_ID, reportResult.getReportId().intValue());
		assertEquals(true, reportResult.getResult().isEmpty());
		assertEquals(0, reportResult.getSuccessful().intValue());
	}

	/**
	 * Test that operation is mapped correct.
	 */
	@Test
	public void testMapFailedOperation() {

		// create report
		Report report = mapper.createReport();

		// create execution results
		ExecutionResult result = new ExecutionResultImpl(OPERATION);
		result.setState(ExecutionState.FAILURE);

		// map
		Result reportResult;
		reportResult = mapper.mapOperationToReport(report, result);

		// test
		assertEquals(result.getDescription(), reportResult.getDescription());
		assertEquals(FAILURE_STATE, reportResult.getState());
		assertEquals("operation", reportResult.getType());
		assertEquals(0, reportResult.getChildren().intValue());
		assertEquals(0, reportResult.getErrors().intValue());
		assertEquals(0, reportResult.getFailures().intValue());
		assertEquals(true, reportResult.getMessages().getMessage().isEmpty());
		assertEquals(FIRST_REPORT_ID, reportResult.getReportId().intValue());
		assertEquals(true, reportResult.getResult().isEmpty());
		assertEquals(0, reportResult.getSuccessful().intValue());
	}

	/**
	 * Test that operation is mapped correct.
	 */
	@Test
	public void testMapOperationWithError() {

		// create report
		Report report = mapper.createReport();

		// create execution results
		ExecutionResult result = new ExecutionResultImpl(OPERATION);
		result.setState(ExecutionState.ERROR);

		// map
		Result reportResult;
		reportResult = mapper.mapOperationToReport(report, result);

		// test
		assertEquals(result.getDescription(), reportResult.getDescription());
		assertEquals(ERROR_STATE, reportResult.getState());
		assertEquals("operation", reportResult.getType());
		assertEquals(0, reportResult.getChildren().intValue());
		assertEquals(0, reportResult.getErrors().intValue());
		assertEquals(0, reportResult.getFailures().intValue());
		assertEquals(true, reportResult.getMessages().getMessage().isEmpty());
		assertEquals(FIRST_REPORT_ID, reportResult.getReportId().intValue());
		assertEquals(true, reportResult.getResult().isEmpty());
		assertEquals(0, reportResult.getSuccessful().intValue());
	}

	/**
	 * Test that operation is mapped correct.
	 */
	@Test
	public void testMapOperationWithComputedState() {

		// create report
		Report report = mapper.createReport();

		// create execution results
		ExecutionResult result = new ExecutionResultImpl(OPERATION);
		result.setState(ExecutionState.COMPUTED);

		// map
		Result reportResult;
		reportResult = mapper.mapOperationToReport(report, result);

		// test
		assertEquals(result.getDescription(), reportResult.getDescription());
		assertEquals(SUCCESS_STATE, reportResult.getState());
		assertEquals("operation", reportResult.getType());
		assertEquals(0, reportResult.getChildren().intValue());
		assertEquals(0, reportResult.getErrors().intValue());
		assertEquals(0, reportResult.getFailures().intValue());
		assertEquals(1, reportResult.getMessages().getMessage().size());
		assertEquals(FIRST_REPORT_ID, reportResult.getReportId().intValue());
		assertEquals(true, reportResult.getResult().isEmpty());
		assertEquals(0, reportResult.getSuccessful().intValue());
	}

	/**
	 * Test that operation with a single successful model is mapped correct.
	 */
	@Test
	public void testMapOperationWithSingleSuccessfulModel() {

		// create report
		Report report = mapper.createReport();

		// create execution results
		ExecutionResult result = new ExecutionResultImpl(OPERATION);
		result.setState(ExecutionState.SUCCESS);

		ExecutionResult child = result.addChild("model#1");
		child.setState(ExecutionState.SUCCESS);

		// map
		Result reportResult;
		reportResult = mapper.mapOperationToReport(report, result);

		// test
		assertEquals(result.getDescription(), reportResult.getDescription());
		assertEquals(SUCCESS_STATE, reportResult.getState());
		assertEquals("operation", reportResult.getType());
		assertEquals(1, reportResult.getChildren().intValue());
		assertEquals(0, reportResult.getErrors().intValue());
		assertEquals(0, reportResult.getFailures().intValue());
		assertEquals(1, reportResult.getMessages().getMessage().size());
		assertEquals(FIRST_REPORT_ID, reportResult.getReportId().intValue());
		assertEquals(true, reportResult.getResult().isEmpty());
		assertEquals(1, reportResult.getSuccessful().intValue());
	}

	/**
	 * Test that operation with a single failed model is mapped correct.
	 */
	@Test
	public void testMapOperationWithSingleFailedModel() {

		// create report
		Report report = mapper.createReport();

		// create execution results
		ExecutionResult result = new ExecutionResultImpl(OPERATION);
		result.setState(ExecutionState.SUCCESS);

		ExecutionResult child = result.addChild("model#1");
		child.setState(ExecutionState.FAILURE);

		// map
		Result reportResult;
		reportResult = mapper.mapOperationToReport(report, result);

		// test
		assertEquals(result.getDescription(), reportResult.getDescription());
		assertEquals(FAILURE_STATE, reportResult.getState());
		assertEquals("operation", reportResult.getType());
		assertEquals(1, reportResult.getChildren().intValue());
		assertEquals(0, reportResult.getErrors().intValue());
		assertEquals(1, reportResult.getFailures().intValue());
		assertEquals(1, reportResult.getMessages().getMessage().size());
		assertEquals(FIRST_REPORT_ID, reportResult.getReportId().intValue());
		assertEquals(true, reportResult.getResult().isEmpty());
		assertEquals(0, reportResult.getSuccessful().intValue());
	}

	/**
	 * Test that operation with a single model with error is mapped correct.
	 */
	@Test
	public void testMapOperationWithSingleModelWithError() {

		// create report
		Report report = mapper.createReport();

		// create execution results
		ExecutionResult result = new ExecutionResultImpl(OPERATION);
		result.setState(ExecutionState.SUCCESS);

		ExecutionResult child = result.addChild("model#1");
		child.setState(ExecutionState.ERROR);

		// map
		Result reportResult;
		reportResult = mapper.mapOperationToReport(report, result);

		// test
		assertEquals(result.getDescription(), reportResult.getDescription());
		assertEquals(ERROR_STATE, reportResult.getState());
		assertEquals("operation", reportResult.getType());
		assertEquals(1, reportResult.getChildren().intValue());
		assertEquals(1, reportResult.getErrors().intValue());
		assertEquals(0, reportResult.getFailures().intValue());
		assertEquals(1, reportResult.getMessages().getMessage().size());
		assertEquals(FIRST_REPORT_ID, reportResult.getReportId().intValue());
		assertEquals(true, reportResult.getResult().isEmpty());
		assertEquals(0, reportResult.getSuccessful().intValue());
	}

	/**
	 * Test that operation with a two successful models are mapped correct.
	 */
	@Test
	public void testMapOperationWithMultipleSuccessfulModels() {

		// create report
		Report report = mapper.createReport();

		// create execution results
		ExecutionResult result = new ExecutionResultImpl(OPERATION);
		result.setState(ExecutionState.SUCCESS);

		ExecutionResult child = result.addChild("model#1");
		child.setState(ExecutionState.SUCCESS);

		ExecutionResult child2 = result.addChild("model#2");
		child2.setState(ExecutionState.SUCCESS);

		// map
		Result reportResult;
		reportResult = mapper.mapOperationToReport(report, result);

		// test
		assertEquals(result.getDescription(), reportResult.getDescription());
		assertEquals(SUCCESS_STATE, reportResult.getState());
		assertEquals("operation", reportResult.getType());
		assertEquals(2, reportResult.getChildren().intValue());
		assertEquals(0, reportResult.getErrors().intValue());
		assertEquals(0, reportResult.getFailures().intValue());
		assertEquals(1, reportResult.getMessages().getMessage().size());
		assertEquals(FIRST_REPORT_ID, reportResult.getReportId().intValue());
		assertEquals(true, reportResult.getResult().isEmpty());
		assertEquals(2, reportResult.getSuccessful().intValue());
	}

	/**
	 * Test that operation with a two failed models are mapped correct.
	 */
	@Test
	public void testMapOperationWithMultipleFailedModels() {

		// create report
		Report report = mapper.createReport();

		// create execution results
		ExecutionResult result = new ExecutionResultImpl(OPERATION);
		ExecutionResult child = result.addChild("model#1");
		child.setState(ExecutionState.FAILURE);
		ExecutionResult child2 = result.addChild("model#2");
		child2.setState(ExecutionState.FAILURE);
		result.setState(ExecutionState.COMPUTED);

		// map
		Result reportResult;
		reportResult = mapper.mapOperationToReport(report, result);

		// test
		assertEquals(result.getDescription(), reportResult.getDescription());
		assertEquals(FAILURE_STATE, reportResult.getState());
		assertEquals("operation", reportResult.getType());
		assertEquals(2, reportResult.getChildren().intValue());
		assertEquals(0, reportResult.getErrors().intValue());
		assertEquals(2, reportResult.getFailures().intValue());
		assertEquals(false, reportResult.getMessages().getMessage().isEmpty());
		assertEquals(FIRST_REPORT_ID, reportResult.getReportId().intValue());
		assertEquals(true, reportResult.getResult().isEmpty());
		assertEquals(0, reportResult.getSuccessful().intValue());
	}

	/**
	 * Test that operation with a two models with errors are mapped correct.
	 */
	@Test
	public void testMapOperationWithMultipleModelsWithErrors() {

		// create report
		Report report = mapper.createReport();

		// create execution results
		ExecutionResult result = new ExecutionResultImpl(OPERATION);
		ExecutionResult child = result.addChild("model#1");
		child.setState(ExecutionState.ERROR);
		ExecutionResult child2 = result.addChild("model#2");
		child2.setState(ExecutionState.ERROR);
		result.setState(ExecutionState.COMPUTED);

		// map
		Result reportResult;
		reportResult = mapper.mapOperationToReport(report, result);

		// test
		assertEquals(result.getDescription(), reportResult.getDescription());
		assertEquals(ERROR_STATE, reportResult.getState());
		assertEquals("operation", reportResult.getType());
		assertEquals(2, reportResult.getChildren().intValue());
		assertEquals(2, reportResult.getErrors().intValue());
		assertEquals(0, reportResult.getFailures().intValue());
		assertEquals(false, reportResult.getMessages().getMessage().isEmpty());
		assertEquals(FIRST_REPORT_ID, reportResult.getReportId().intValue());
		assertEquals(true, reportResult.getResult().isEmpty());
		assertEquals(0, reportResult.getSuccessful().intValue());
	}

}
