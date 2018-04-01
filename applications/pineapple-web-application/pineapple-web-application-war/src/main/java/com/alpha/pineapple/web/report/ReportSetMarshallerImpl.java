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

import static com.alpha.pineapple.web.WebApplicationConstants.REPORTS_FILE;
import static com.alpha.pineapple.execution.ExecutionResult.MSG_MESSAGE;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.alpha.pineapple.exception.SaveConfigurationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.report.ObjectFactory;
import com.alpha.pineapple.model.report.Report;
import com.alpha.pineapple.model.report.Reports;

/**
 * Implementation of the {@linkplain ReportSetMarshaller}.
 */
public class ReportSetMarshallerImpl implements ReportSetMarshaller {

	/**
	 * Reports package name.
	 */
	static final String PACKAGE_NAME = Reports.class.getPackage().getName();

	/**
	 * Object factory.
	 */
	@Resource
	ObjectFactory reportModelObjectFactory;

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

	@Override
	public Reports load(ExecutionResult result) {

		// resolve file
		File reportsDirectory = runtimeDirectoryProvider.getReportsDirectory();
		File reportsFile = new File(reportsDirectory, REPORTS_FILE);

		// create execution result
		Object[] args = { reportsFile };
		String message = webMessageProvider.getMessage("rsm.load_reports_info", args);
		ExecutionResult loadResult = result.addChild(message);

		// return empty configuration if file doesn't exist
		if (!reportsFile.exists()) {

			// complete as success
			Object[] args3 = { reportsFile };
			loadResult.completeAsSuccessful(webMessageProvider, "rsm.load_reports_nofile_info", args3);

			// return empty configuration
			return reportModelObjectFactory.createReports();
		}

		try {

			// create unmarshaller
			JAXBContext jaxbContext = JAXBContext.newInstance(PACKAGE_NAME);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			// load
			Object results = unmarshaller.unmarshal(reportsFile);

			// complete as success
			Object[] args3 = { reportsFile };
			loadResult.completeAsSuccessful(webMessageProvider, "rsm.load_reports_success", args3);

			return Reports.class.cast(results);

		} catch (Exception e) {

			Object[] args3 = { e.getMessage() };
			loadResult.completeAsError(webMessageProvider, "rsm.load_reports_error", args3, e);

			// create and throw exception
			String errorMessage = webMessageProvider.getMessage("rsm.load_reports_error", args3);
			throw new SaveConfigurationFailedException(errorMessage, e);
		}
	}

	@Override
	public void save(Reports reports) {

		// resolve file
		File reportsDirectory = runtimeDirectoryProvider.getReportsDirectory();
		File reportsFile = new File(reportsDirectory, REPORTS_FILE);

		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(PACKAGE_NAME);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

			// save
			marshaller.marshal(reports, reportsFile);

		} catch (Exception e) {
			// create and throw exception
			Object[] args = { e };
			String errorMessage = webMessageProvider.getMessage("rsm.save_reports_error", args);
			throw new SaveConfigurationFailedException(errorMessage, e);
		}
	}

	@Override
	public Reports map(Stream<Report> reports) {
		Reports modelReports = reportModelObjectFactory.createReports();
		List<Report> reportList = modelReports.getReport();
		reports.forEach(reportList::add);
		return modelReports;
	}

}
