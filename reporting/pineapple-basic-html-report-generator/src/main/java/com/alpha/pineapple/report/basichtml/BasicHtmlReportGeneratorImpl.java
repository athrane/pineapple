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

package com.alpha.pineapple.report.basichtml;

import static com.alpha.javautils.ArgumentUtils.notNull;
import static com.alpha.javautils.StackTraceHelper.getStrackTrace;
import static com.alpha.pineapple.execution.ExecutionResult.MSG_ERROR_MESSAGE;
import static com.alpha.pineapple.execution.ExecutionResult.MSG_MESSAGE;
import static com.alpha.pineapple.execution.ExecutionResult.MSG_REPORT;
import static com.alpha.pineapple.execution.ExecutionResult.ExecutionState.EXECUTING;
import static javax.xml.transform.OutputKeys.ENCODING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ResultListener;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.report.basichtml.ObjectFactory;
import com.alpha.pineapple.model.report.basichtml.Report;
import com.alpha.pineapple.model.report.basichtml.Result;
import com.alpha.pineapple.report.basichtml.model.Mapper;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * Implementation of the <code>ResultListener</code> which generate an HTML
 * report.
 */
public class BasicHtmlReportGeneratorImpl implements ResultListener, ReportGeneratorInfo {

	/**
	 * Spring bean id for report generator.
	 */
	static final String GENERATOR_BEAN_ID = "reportGenerator";

	/**
	 * Spring configuration file for the report generator.
	 */
	static final String REPORT_CONFIG = "com.alpha.pineapple.report.basichtml-config.xml";

	/**
	 * Time stamp format.
	 */
	static final String TIMESTAMP_FORMAT = "yyyyMMdd-HHmmss";

	/**
	 * Default name for pineapple directory.
	 */
	public static final String PINEAPPLE_DIR = ".pineapple";

	/**
	 * Default name for report directory.
	 */
	public static final String REPORT_DIR = "reports";

	/**
	 * Pineapple home directory system property name.
	 */
	public static final String PINEAPPLE_HOME = "pineapple.home.dir";

	/**
	 * Default name for JUnit style report
	 */
	static final String REPORT_XML = "basic-report.xml";

	/**
	 * Default name for style sheet.
	 */
	static final String REPORT_XSL = "/com.alpha.pineapple.report.basichtml.xsl";

	/**
	 * Default name for HTML report.
	 */
	static final String REPORT_HTML = "basic-report.html";

	/**
	 * HTML output encoding.
	 */
	static final String UTF_8 = "UTF-8";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Report directory.
	 */
	File reportDirectory;

	/**
	 * Time stamp date format.
	 */
	DateFormat timestampFormat;

	/**
	 * JAXB factory.
	 */
	@Resource
	ObjectFactory objectFactory;

	/**
	 * JAXB RI namespace prefix mapper.
	 */
	@Resource
	NamespacePrefixMapper prefixMapper = new ReportNamespaceMapper();

	/**
	 * Model mapper object.
	 */
	@Resource
	Mapper mapper;

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "reportMessageProvider")
	MessageProvider messageProvider;

	/**
	 * BasicHtmlReportGenerator no-arg constructor.
	 */
	BasicHtmlReportGeneratorImpl() {

		// create date format
		timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
	}

	/**
	 * Set report root directory.
	 * 
	 * @param reportDir The report root directory.
	 */
	public void setReportDirectory(File reportDir) {
		this.reportDirectory = reportDir;

		// if report directory doesn't exist then create it
		if (!reportDir.exists()) {

			// log debug message
			if (logger.isDebugEnabled()) {
				String message = messageProvider.get("bhrg.create_root_directory", reportDir.getAbsolutePath());
				logger.debug(message);
			}

			reportDir.mkdirs();
		}

		// log debug message
		if (logger.isDebugEnabled()) {
			String message = messageProvider.get("bhrg.configured_root_directory", reportDir.getAbsolutePath());
			logger.debug(message);
		}
	}

	public void notify(ExecutionResultNotification notification) {

		// determine if execution is completed
		if (!isExecutionCompleted(notification))
			return;

		// create XML report
		File xmlReport = createXmlReport(notification.getResult());

		// create HTML report from XML file
		transformToHtml(xmlReport, notification.getResult());
	}

	/**
	 * Transform XML report to HTML report.
	 * 
	 * @param xmlReport       File name of the XML report.
	 * @param executionResult root execution result.
	 */
	void transformToHtml(File xmlReport, ExecutionResult executionResult) {

		// define report directory for this report
		File newReportDir = xmlReport.getParentFile();

		// create HTML file name.
		File htmlReport = new File(newReportDir, REPORT_HTML);

		// log debug message
		if (logger.isDebugEnabled()) {
			String message = messageProvider.get("bhrg.start_html_transform", xmlReport.getAbsolutePath(), REPORT_XSL,
					htmlReport);
			logger.debug(message);
		}

		try {

			// load XML report as DOM
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setNamespaceAware(true);
			DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
			Document xmlDoc = documentBuilder.parse(xmlReport);
			DOMSource dom = new DOMSource(xmlDoc);

			// create XSLT transformer factory
			TransformerFactory factory = TransformerFactory.newInstance();

			// get style sheet from class loader
			InputStream xslStream = this.getClass().getResourceAsStream(REPORT_XSL);

			// create streams
			StreamSource xsl = new StreamSource(xslStream);
			StreamResult html = new StreamResult(htmlReport);

			// create XSLT transformer
			Transformer transformer = factory.newTransformer(xsl);
			transformer.setOutputProperty(ENCODING, UTF_8);
			transformer.setOutputProperty(OutputKeys.METHOD, "html");

			// transform
			transformer.transform(dom, html);

			// log debug message
			String message = messageProvider.get("bhrg.completed_html_transform", htmlReport);
			if (logger.isDebugEnabled())
				logger.debug(message);

			// add report message to result
			executionResult.addMessage(MSG_REPORT, newReportDir.getName());
			executionResult.addMessage(MSG_MESSAGE, message);

		} catch (Exception e) {
			String message = messageProvider.get("bhrg.failed_html_transform", getStrackTrace(e));
			logger.error(message);

			// post error in execution result
			executionResult.addMessage(MSG_ERROR_MESSAGE, message);
		}
	}

	/**
	 * Create report in an XML document.
	 * 
	 * @param result Root execution result object.
	 * 
	 * @return File which contains the name of the XML document.
	 */
	File createXmlReport(ExecutionResult result) {

		// create directory for this report
		File newReportDir = createReportInstanceDirectory();

		// create report root object
		Report reportRoot = mapper.createReport();

		// add operation to report
		Result operationReportResult = mapper.mapOperationToReport(reportRoot, result);

		// add models to report
		ExecutionResult[] modelResults = result.getChildren();

		// iterate over the model results
		for (ExecutionResult childResult : modelResults) {
			mapper.mapModelToReport(operationReportResult, childResult);
		}

		// create report file name
		File reportFile = new File(newReportDir, REPORT_XML);

		// marshall to XML file
		jaxbMarshall(reportRoot, reportFile);

		return reportFile;
	}

	/**
	 * Create time stamped report directory for this report.
	 * 
	 * @return time stamped report directory for this report.
	 */
	File createReportInstanceDirectory() {

		// if report directory is null, then used the default directory
		if (reportDirectory == null) {
			setReportDirectory(getDefaultReportDirectory());
		}

		// create report directory name
		StringBuilder reportDirName = new StringBuilder();
		reportDirName.append("report-");
		reportDirName.append(timestampFormat.format(new Date()));

		// define report directory for this report
		File newReportDir = new File(reportDirectory, reportDirName.toString());

		// if directory doesn't exist then create it
		if (!newReportDir.exists()) {

			// log debug message
			if (logger.isDebugEnabled()) {
				String message = messageProvider.get("bhrg.create_instance_directory", newReportDir.getAbsolutePath());
				logger.debug(message);
			}

			newReportDir.mkdirs();
		}

		return newReportDir;
	}

	/**
	 * Return true if execution is completed, i.e. the root result signals that it
	 * isn't running anymore.
	 * 
	 * @param result The execution result notification to test.
	 * 
	 * @return true if execution is completed, i.e. the root result signals that it
	 *         isn't running anymore.
	 */
	boolean isExecutionCompleted(ExecutionResultNotification notification) {
		if (!notification.getResult().isRoot())
			return false;
		return (!notification.getState().equals(EXECUTING));
	}

	/**
	 * Marshall object graph to file using JAXB.
	 * 
	 * @param rootObject rootObject Root object of object graph which should be
	 *                   marshalled.
	 * @param file       File that the environment configuration should be saved to.
	 */
	void jaxbMarshall(Object rootObject, File file) {
		// define stream for exception handling
		OutputStream os = null;

		try {
			// get package name
			String packageName = rootObject.getClass().getPackage().getName();

			// log debug message
			if (logger.isDebugEnabled()) {
				Object[] args = { rootObject, file.getAbsolutePath(), packageName };
				String message = messageProvider.getMessage("bhrg.start_marshal", args);
				logger.debug(message);
			}

			JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
			Marshaller marshaller = jaxbContext.createMarshaller();

			// set prefix mapper if it is defined
			if (prefixMapper != null) {

				// log debug message
				if (logger.isDebugEnabled()) {
					String message = messageProvider.get("bhrg.set_prefixmapper", prefixMapper);
					logger.debug(message);
				}

				marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", prefixMapper);
			}

			// set pretty print and encoding
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, UTF_8);

			os = new FileOutputStream(file);
			marshaller.marshal(rootObject, os);
			os.close();
		} catch (Exception e) {
			String message = messageProvider.get("bhrg.failed_marshal", getStrackTrace(e));
			logger.error(message);

		} finally {

			// close OS
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {

					// log error message
					String message = messageProvider.get("bhrg.failed_close", getStrackTrace(e));
					logger.error(message);
				}
			}
		}
	}

	public File getCurrentReportDirectory() {
		return reportDirectory;
	}

	/**
	 * Returns the default report directory which is located at
	 * ${pineapple.home.dir}/reports/
	 * 
	 * @return The default report directory which is located at
	 *         ${pineapple.home.dir}/.pineapple/reports/
	 */
	static File getDefaultReportDirectory() {

		// get pineapple home
		String pineappleHome = System.getProperty(PINEAPPLE_HOME);

		// define default report directory
		return new File(pineappleHome, REPORT_DIR);
	}

	/**
	 * Creates report generator instance which is initialized from Spring context.
	 * 
	 * @return report generator initialized from Spring context.
	 */
	static BasicHtmlReportGeneratorImpl getInstanceFromSpring() {
		// load spring configuration file
		String[] appConfigFile = new String[] { REPORT_CONFIG };
		ApplicationContext appContext = new ClassPathXmlApplicationContext(appConfigFile);

		// get report generator
		BasicHtmlReportGeneratorImpl generator;
		generator = (BasicHtmlReportGeneratorImpl) appContext.getBean(GENERATOR_BEAN_ID);
		return generator;
	}

	/**
	 * Return instance of the basic HTML report generator. The generator will
	 * produce reports at the default location: ${user.home}/.pineapple/reports/
	 * 
	 * @return instance of the basic HTML report generator.
	 */
	public static ResultListener getInstance() {

		// create instance form spring context
		BasicHtmlReportGeneratorImpl generator = getInstanceFromSpring();

		// use default directory
		generator.setReportDirectory(getDefaultReportDirectory());

		return generator;
	}

	/**
	 * Return instance of the basic HTML report generator. The generator will
	 * produce reports at the root directory defined by the parameter
	 * <code>reportDirectory</code>.
	 * 
	 * @param reportDirectory Root directory where reports directories will be
	 *                        generated.
	 * 
	 * @return instance of the basic HTML report generator.
	 */
	public static ResultListener getInstance(File reportDirectory) {
		notNull(reportDirectory, "reportDirectory is undefined");

		// create instance form spring context
		BasicHtmlReportGeneratorImpl generator = getInstanceFromSpring();

		// use default directory
		generator.setReportDirectory(reportDirectory);

		return generator;
	}

}
