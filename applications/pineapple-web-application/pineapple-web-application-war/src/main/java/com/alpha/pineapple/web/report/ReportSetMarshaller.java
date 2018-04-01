/*

 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.web.report;

import java.util.stream.Stream;

import com.alpha.pineapple.exception.SaveConfigurationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.scheduled.ScheduledOperationInfo;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperations;
import com.alpha.pineapple.model.report.Report;
import com.alpha.pineapple.model.report.Reports;

/**
 * Interface for loading and saving the set of reports (as XML) from/to disk to
 * objects.
 */
public interface ReportSetMarshaller {

	/**
	 * Save configuration which contains the set of registered reports.
	 * 
	 * The report directory is resolved from the runtime directory provider. The
	 * configuration file is saved in the default reports file reports.xml.
	 * 
	 * @param reports
	 *            reports model which is saved.
	 * 
	 * @throws SaveConfigurationFailedException
	 *             if save fails.
	 */

	void save(Reports reports);

	/**
	 * Load configuration file with the set of registered reports. If no file exist
	 * then an empty file is returned.
	 * 
	 * @param executionResult
	 *            A child execution result will be added to this result. The child
	 *            result will reflects the out come of the save operation. If the
	 *            save operation fails the state of the result object will be
	 *            failure or error and will contain a stack trace message.
	 * @return loaded configuration file with the set of registered reports.
	 * 
	 * @throws LoadConfigrationFailedException
	 *             if save fails.
	 */
	Reports load(ExecutionResult executionResult);

	/**
	 * Map reports for saving.
	 * 
	 * @param reports
	 *            stream of {@linkplain Reports} which are mapped.
	 * 
	 * @return reports configuration which can be marshalled.
	 */
	Reports map(Stream<Report> reports);

}
