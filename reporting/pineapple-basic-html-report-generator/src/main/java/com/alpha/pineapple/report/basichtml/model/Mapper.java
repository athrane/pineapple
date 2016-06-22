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

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.model.report.basichtml.ExecutionResultType;
import com.alpha.pineapple.model.report.basichtml.Report;

/**
 * Maps execution results into schema generated objects.
 */
public interface Mapper {
	
	/**
	 * Create report root object.
	 * @return report root object.
	 */
	public Report createReport(); 
	
	/**
	 * Map operation result object into a report result object and
	 * add it to the report.
	 * 
	 * @param reportRoot Report root object. 
	 * @param result Execution result containing execution result for an operation.
	 * 
	 * @return report result object for operation. 
	 */
	public ExecutionResultType mapOperationToReport(Report reportRoot, ExecutionResult result);	

	/**
	 * Map model result object into a report result object and add it
	 * to the supplied parent report result object. 
	 * 
	 * @param operation Parent report result object which should represent a operation.
	 * @param result Execution result containing execution result for a model.
	 *  
	 * @return report result object for model.
	 */
	public ExecutionResultType mapModelToReport(ExecutionResultType operation, ExecutionResult result);
	
	/**
	 * Map model result object into a report result object and add it
	 * to the supplied parent report result object. 
	 * 
	 * @param parent Parent report result object. 
	 * @param result Execution result containing result for child.
	 */
	public ExecutionResultType mapChildResultToReport(ExecutionResultType parent, ExecutionResult result);
}
