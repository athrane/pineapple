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


package com.alpha.pineapple.report.basichtml;

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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ResultListener;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.report.basichtml.ExecutionResultType;
import com.alpha.pineapple.model.report.basichtml.ObjectFactory;
import com.alpha.pineapple.model.report.basichtml.Report;
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
    @Resource(name="reportMessageProvider")
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
	 * @param reportDirectory The report root directory.
	 */
	public void setReportDirectory(File reportDirectory) {
		this.reportDirectory = reportDirectory;
		
		// if report directory doesn't exist then create it				
		if (!reportDirectory.exists()) {

	        // log debug message
	        if ( logger.isDebugEnabled() )
	        {
	        	Object[] args = { reportDirectory.getAbsolutePath() };    	        	
	        	String message = messageProvider.getMessage("bhrg.create_root_directory", args );
	        	logger.debug( message );
	        }

	        reportDirectory.mkdirs();
		}
		
        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { reportDirectory.getAbsolutePath() };    	        	
        	String message = messageProvider.getMessage("bhrg.configured_root_directory", args );
        	logger.debug( message );
        }		
	}
	
	public void notify(ExecutionResultNotification notification) {

		// determine if execution is completed
		if (!isExecutionCompleted(notification))
			return;

		// create XML report
		File xmlReport = createXmlReport(notification.getResult());

		// create HTML report from XML file
		transformToHtml(xmlReport);
		
		// add report message to result
		File newReportDirectory = xmlReport.getParentFile();
		notification.getResult().addMessage(ExecutionResult.MSG_REPORT, newReportDirectory.getName());
	}

	/**
	 * Transform XML report to HTML report.
	 * 
	 * @param xmlReport File name of the XML report. 
	 */
	void transformToHtml(File xmlReport) {

		// define report directory for this report
		File newReportDirectory = xmlReport.getParentFile();

		// create HTML file name.
		File htmlReport = new File(newReportDirectory, REPORT_HTML);

        // log debug message
        if ( logger.isDebugEnabled() )
        {
        	Object[] args = { xmlReport.getAbsolutePath(), REPORT_XSL, htmlReport };    	        	
        	String message = messageProvider.getMessage("bhrg.start_html_transform", args );
        	logger.debug( message );
        }
		
        // define stream 
        FileOutputStream fileOutputStream = null;
        
		try {
			// create XSLT transformer factory
			TransformerFactory factory = TransformerFactory.newInstance();			

			// get style sheet for class loader
			InputStream xslStream;
			xslStream = this.getClass().getResourceAsStream(REPORT_XSL);

			// create streams
			StreamSource xsl = new StreamSource(xslStream);
			StreamSource xml = new StreamSource(xmlReport);
			fileOutputStream = new FileOutputStream(htmlReport);
			StreamResult html = new StreamResult(fileOutputStream);			

			// create XSLT transformer
			Transformer transformer = factory.newTransformer(xsl);
			transformer.setParameter("output.dir", newReportDirectory.getAbsolutePath());

			try {
				// transform
				transformer.transform(xml, html);				
			} finally {				
				// if not null then close 
				if (fileOutputStream != null) {
					fileOutputStream.close();				
				}
			}			
			
	        // log debug message
	        if ( logger.isDebugEnabled() )
	        {
	        	Object[] args = { htmlReport };    	        	
	        	String message = messageProvider.getMessage("bhrg.completed_html_transform", args );
	        	logger.debug( message );
	        }
			
		} catch (Exception e) {			
        	Object[] args = { StackTraceHelper.getStrackTrace(e) };    	        	
        	String message = messageProvider.getMessage("bhrg.failed_html_transform", args );
			logger.error(message);
		} 
	}

	/**
	 * Create report in an XML document.
	 * 
	 * @param result
	 *            Root execution result object.
	 * 
	 * @return File which contains the name of the XML document.
	 */
	File createXmlReport(ExecutionResult result) {

		// create directory for this report
		File newReportDirectory = createReportInstanceDirectory();

		// create report root object
		Report reportRoot = mapper.createReport();

		// add operation to report
		ExecutionResultType operationReportResult;
		operationReportResult = mapper.mapOperationToReport(reportRoot, result);

		// add models to report
		ExecutionResult[] modelResults = result.getChildren();

		// iterate over the model results
		for (ExecutionResult childResult : modelResults) {
			mapper.mapModelToReport(operationReportResult, childResult);
		}

		// create report file name
		File reportFile = new File(newReportDirectory, REPORT_XML);

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
		StringBuilder reportDirectoryName = new StringBuilder();
		reportDirectoryName.append("report-");
		reportDirectoryName.append(timestampFormat.format(new Date()));

		// define report directory for this report
		File newReportDirectory = new File(reportDirectory, reportDirectoryName.toString());

		// if directory doesn't exist then create it
		if (!newReportDirectory.exists()) {

	        // log debug message
	        if ( logger.isDebugEnabled() )
	        {
	        	Object[] args = { newReportDirectory.getAbsolutePath() };    	        	
	        	String message = messageProvider.getMessage("bhrg.create_instance_directory", args );
	        	logger.debug( message );
	        }
			
			newReportDirectory.mkdirs();
		}

		return newReportDirectory;
	}

	/**
	 * Return true if execution is completed, i.e. the root result signals that
	 * it isn't running anymore.
	 * 
	 * @param result The execution result notification to test.
	 * 
	 * @return true if execution is completed, i.e. the root result signals that
	 *         it isn't running anymore.
	 */
	boolean isExecutionCompleted(ExecutionResultNotification notification) {
		if (!notification.getResult().isRoot()) return false;
		return (!notification.getState().equals(ExecutionResult.ExecutionState.EXECUTING));
	}

	/**
	 * Marshall object graph to file using JAXB.
	 * 
	 * @param rootObject
	 *            rootObject Root object of object graph which should be
	 *            marshalled.
	 * @param file
	 *            File that the environment configuration should be saved to.
	 */
	void jaxbMarshall(Object rootObject, File file) {
		// define stream for exception handling
		OutputStream os = null;

		try {
			// get package name
			String packageName = rootObject.getClass().getPackage().getName();

	        // log debug message
	        if ( logger.isDebugEnabled() )
	        {
	        	Object[] args = { rootObject, file.getAbsolutePath(), packageName };    	        	
	        	String message = messageProvider.getMessage("bhrg.start_marshal", args );
	        	logger.debug( message );
	        }		

			JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
			Marshaller marshaller = jaxbContext.createMarshaller();

			// set prefix mapper if it is defined
			if (prefixMapper != null) {

		        // log debug message
		        if ( logger.isDebugEnabled() )
		        {
		        	Object[] args = { prefixMapper };    	        	
		        	String message = messageProvider.getMessage("bhrg.set_prefixmapper", args );
		        	logger.debug( message );
		        }		
				
				marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", prefixMapper);
			}

			// set pretty print
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			os = new FileOutputStream(file);
			marshaller.marshal(rootObject, os);
			os.close();
		} catch (Exception e) {

        	Object[] args = { StackTraceHelper.getStrackTrace(e) };    	        	
        	String message = messageProvider.getMessage("bhrg.failed_marshal", args );
        	logger.error( message );
						
		} finally {

			// close OS
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					
					// log error message
		        	Object[] args = { StackTraceHelper.getStrackTrace(e) };    	        	
		        	String message = messageProvider.getMessage("bhrg.failed_close", args );
		        	logger.error( message );
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
        String[] appConfigFile = new String[] {REPORT_CONFIG };
        ApplicationContext appContext = new ClassPathXmlApplicationContext(appConfigFile);
                           
        // get report generator
        BasicHtmlReportGeneratorImpl generator;
		generator = (BasicHtmlReportGeneratorImpl) appContext.getBean(GENERATOR_BEAN_ID );
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
	 * @param reportDirectory
	 *            Root directory where reports directories will be generated.
	 * 
	 * @return instance of the basic HTML report generator.
	 */
	public static ResultListener getInstance(File reportDirectory) {

		// validate argument 
		Validate.notNull(reportDirectory, "reportDirectory is undefined");
		
		// create instance form spring context
        BasicHtmlReportGeneratorImpl generator = getInstanceFromSpring();

		// use default directory
		generator.setReportDirectory(reportDirectory);
		
		return generator;
	}

}
