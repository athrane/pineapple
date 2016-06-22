/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2016 Allan Thrane Andersen..
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

package com.alpha.pineapple.web.report;

import static com.alpha.pineapple.web.WebApplicationConstants.MSG_ENVIRONMENT;
import static com.alpha.pineapple.web.WebApplicationConstants.MSG_MODULE;
import static com.alpha.pineapple.web.WebApplicationConstants.MSG_OPERATION;
import static com.alpha.pineapple.web.WebApplicationConstants.SIMPLE_DATE_FORMAT;

import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.WordUtils;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.report.ObjectFactory;
import com.alpha.pineapple.model.report.Report;
import com.alpha.pineapple.model.report.Reports;

/**
 * Implementation of the @link {@link ReportRepository} interface. The
 * repository contains information about reports stored on disk. The location of
 * the reports is resolved form the {@link RuntimeDirectoryProvider}.
 */
public class ReportRepositoryImpl implements ReportRepository {

    /**
     * Object factory.
     */
    @Resource
    ObjectFactory reportModelObjectFactory;

    /**
     * Execution result factory.
     */
    @Resource
    ExecutionResultFactory executionResultFactory;

    /**
     * Runtime directory resolver.
     */
    @Resource
    RuntimeDirectoryProvider runtimeDirectoryProvider;

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider webMessageProvider;

    /**
     * Report set marshaller.
     */
    @Resource
    ReportSetMarshaller reportSetMarshaller;

    /**
     * Collection of reports.
     */
    Map<String, Report> reports = new ConcurrentHashMap<String, Report>();

    /**
     * Comparator for returning sorted stream of {@linkplain Report}.
     */
    Comparator<Report> byName = (e1, e2) -> e1.getDirectory().compareTo(e2.getDirectory());

    /**
     * Comparator for returning sorted stream of {@linkplain Report} in reverse
     * order.
     */
    Comparator<Report> byNameReversed = byName.reversed();

    @Override
    public void initialize(ExecutionResult result) {

	// delete without saving
	reports.keySet().forEach(id -> reports.remove(id));

	// load
	String message = webMessageProvider.getMessage("rr.initialize_info");
	ExecutionResult childResult = result.addChild(message);
	load(childResult);

	Object[] args = { this.reports.size() };
	childResult.completeAsComputed(webMessageProvider, "rr.initialize_completed", args);
    }

    @Override
    public void refresh() {
	ExecutionResult result = executionResultFactory.startExecution("Refresh reports");
	initialize(result);
    }

    @Override
    public Report add(String id, ExecutionResult result) {
	Validate.notNull(id, "id is undefined.");
	Validate.notEmpty(id, "id is empty.");
	Validate.notNull(result, "result is undefined.");

	// return report if it exists
	if (reports.containsKey(id))
	    return reports.get(id);

	// create report directory
	File reportsDirectory = runtimeDirectoryProvider.getReportsDirectory();
	File reportDirectory = new File(reportsDirectory, id);

	// return null if report isn't valid.
	if (!isValidReportDir(reportDirectory))
	    return null;

	// get report information
	Map<String, String> messages = result.getMessages();
	String module = messages.get(MSG_MODULE);
	String model = messages.get(MSG_ENVIRONMENT);
	String operation = messages.get(MSG_OPERATION);
	String resultAsString = result.getState().name();
	long duration = result.getTime();
	long start = result.getStartTime();
	Date resultdate = new Date(start);

	// add report
	Report report = reportModelObjectFactory.createReport();
	report.setId(id);
	report.setModule(module);
	report.setModel(model);
	report.setOperation(operation);
	// report.setResult(WordUtils.capitalizeFully(resultAsString));
	report.setResult(resultAsString);
	report.setStart(SIMPLE_DATE_FORMAT.format(resultdate));
	report.setDuration(duration);
	report.setDirectory(reportDirectory.getAbsolutePath());
	reports.put(id, report);

	save();
	return report;
    }

    @Override
    public Stream<Report> getReports() {
	return reports.values().stream().sorted(byNameReversed);
    }

    @Override
    public void deleteAll() {
	reports.keySet().forEach(report -> delete(report));
    }

    @Override
    public void delete(String id) {
	Validate.notNull(id, "id is undefined");
	Validate.notEmpty(id, "id is empty");

	// exit if report doesn't exist
	if (!reports.containsKey(id))
	    return;
	reports.remove(id);

	save();
    }

    /**
     * Validate whether a file object represents a valid report directory.
     * 
     * @param reportDirectory
     *            file which should be validated.
     * 
     * @return true if the file represents a valid report directory.
     */
    boolean isValidReportDir(File reportDirectory) {
	return (reportDirectory.isDirectory());
    }

    /**
     * Load repository.
     * 
     * @param result
     *            execution result for capture the outcome of the operation.
     */
    void load(ExecutionResult result) {
	Reports modelReports = reportSetMarshaller.load(result);
	reports = modelReports.getReport().stream()
		.collect(Collectors.toConcurrentMap(Report::getId, Function.identity()));
    }

    /**
     * Save repository.
     */
    void save() {
	Reports modelReports = reportSetMarshaller.map(getReports());
	reportSetMarshaller.save(modelReports);
    }
}
