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

import java.util.stream.Stream;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.model.report.Report;

/**
 * Interface for a report repository.
 */
public interface ReportRepository {

    /**
     * Initialize repository.
     * 
     * @param result
     *            A child execution result will be added to this result. The
     *            child result will reflects the out come of the initialization.
     *            If the initialization fails the state of the result object
     *            will be failure or error and will contain a stack trace
     *            message.
     */
    void initialize(ExecutionResult result);

    /**
     * Forced refresh of the repository.
     */
    public void refresh();

    /**
     * Get all reports.
     * 
     * @return stream of reports.
     */
    Stream<Report> getReports();

    /**
     * Add new report from reports repository directory.
     * 
     * If report already exists then existing report is returned.
     * 
     * If report directory isn't valid then null is returned and no report is
     * added to the repository.
     * 
     * @param id
     *            report ID.
     * @param result
     *            execution result for reported execution.
     * 
     * @return created report. If report already exists then existing report is
     *         returned. If report directory isn't valid then null is returned
     *         and no report is added to the repository.
     */
    public Report add(String id, ExecutionResult result);

    /**
     * Delete report
     * 
     * @param id
     *            report id.
     */
    void delete(String id);

    /**
     * Delete all reports.
     */
    void deleteAll();
}
