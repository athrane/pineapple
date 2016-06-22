/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2016 Allan Thrane Andersen..
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
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 */

package com.alpha.pineapple.web.event.consumer;

import static com.alpha.pineapple.web.WebApplicationConstants.REACTOR_EVENT_CREATED_REPORT;
import static com.alpha.pineapple.web.WebApplicationConstants.REACTOR_TOPIC_SERVICE_CREATED_REPORT;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ResultListener;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.report.basichtml.ReportGeneratorInfo;
import com.alpha.pineapple.web.report.ReportRepository;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

/**
 * Implementation of the {@linkplain Consumer} interface which implement
 * creation of an HTML report using the report generator.
 * 
 * The class is invoked from the {@linkplain Reactor} where it is registered as
 * a consumer. The consumer listens to result notifications.
 */
public class CreateReportImpl implements Consumer<Event<ExecutionResultNotification>> {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Report repository.
     */
    @Resource
    ReportRepository reportRepository;

    /**
     * Runtime directory resolver.
     */
    @Resource
    RuntimeDirectoryProvider runtimeDirectoryProvider;

    /**
     * Report generator.
     */
    @Resource
    ResultListener reportGenerator;

    /**
     * Web application reactor.
     */
    @Resource
    Reactor webAppReactor;

    @Override
    public void accept(Event<ExecutionResultNotification> t) {
	
	try {

	    // get notification
	    ExecutionResultNotification notification = t.getData();

	    // exit if execution is completed
	    if (!isExecutionCompleted(notification))
		return;

	    // initialize report generator
	    ReportGeneratorInfo generatorInfo = (ReportGeneratorInfo) reportGenerator;
	    generatorInfo.setReportDirectory(runtimeDirectoryProvider.getReportsDirectory());

	    // generate report
	    reportGenerator.notify(notification);

	    // exit if no report was generated
	    Map<String, String> messages = notification.getResult().getMessages();
	    if (!messages.containsKey(ExecutionResult.MSG_REPORT))
		return;

	    // log exception if report generation failed.

	    // get generated report id
	    String reportId = messages.get(ExecutionResult.MSG_REPORT);

	    // update report repository with new execution
	    reportRepository.add(reportId, notification.getResult());

	    // post created report event
	    webAppReactor.notify(REACTOR_TOPIC_SERVICE_CREATED_REPORT, Event.wrap(REACTOR_EVENT_CREATED_REPORT));

	} catch (Exception e) {
	    logger.error(StackTraceHelper.getStrackTrace(e));
	}

    }

    /**
     * Return true if execution is completed, i.e. the root result signals that
     * it isn't running anymore.
     * 
     * @param result
     *            The execution result to test.
     * 
     * @return true if execution is completed, i.e. the root result signals that
     *         it isn't running anymore.
     */
    boolean isExecutionCompleted(ExecutionResultNotification ern) {
	if (!ern.getResult().isRoot())
	    return false;
	return (!isResultExecuting(ern));
    }

    /**
     * Return true if execution is running of a non-root result.
     * 
     * @param result
     *            The execution result to test.
     * 
     * @return true if execution is running of a non-root result.
     */
    boolean isResultExecuting(ExecutionResultNotification ern) {
	return (ern.getState().equals(ExecutionResult.ExecutionState.EXECUTING));
    }
}
