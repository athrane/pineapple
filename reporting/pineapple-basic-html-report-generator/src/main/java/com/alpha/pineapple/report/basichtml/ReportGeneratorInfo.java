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

import java.io.File;

/**
 * Provides information about the report generation process.
 */
public interface ReportGeneratorInfo {

	/**
	 * Returns the directory where the last report was created in.
	 * 
	 * @return the directory where the last report was created in. The method can
	 *         return if the report generation failed.
	 */
	public File getCurrentReportDirectory();

	/**
	 * Set report root directory.
	 * 
	 * @param reportDirectory
	 *            The report root directory.
	 */
	public void setReportDirectory(File reportDirectory);

}
