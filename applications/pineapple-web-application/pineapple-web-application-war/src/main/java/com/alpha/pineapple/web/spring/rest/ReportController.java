/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.web.spring.rest;

import static com.alpha.pineapple.web.WebApplicationConstants.REST_REPORT_DELETE_REPORTS_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_REPORT_GET_REPORTS_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_REPORT_URI;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.report.Reports;
import com.alpha.pineapple.web.model.RestResultMapper;
import com.alpha.pineapple.web.report.ReportRepository;

/**
 * Report REST web service controller.
 *
 */
@Controller
@RequestMapping(REST_REPORT_URI)
public class ReportController {

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider webMessageProvider;

	/**
	 * Report repository.
	 */
	@Resource
	ReportRepository reportRepository;

	/**
	 * REST result mapper.
	 */
	@Resource
	RestResultMapper restResultMapper;

	/**
	 * Get reports.
	 * 
	 * @return collection of reports and HTTP 200 (OK).
	 */
	@RequestMapping(value = REST_REPORT_GET_REPORTS_PATH, method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Reports getReports() {
		return this.restResultMapper.mapReports(reportRepository.getReports());
	}

	/**
	 * Delete all scheduled operations.
	 */
	@RequestMapping(value = REST_REPORT_DELETE_REPORTS_PATH, method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public void deleteAll() {
		reportRepository.deleteAll();
	}

}
