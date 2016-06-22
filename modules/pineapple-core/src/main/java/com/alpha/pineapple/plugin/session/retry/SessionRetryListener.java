/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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
 *   
 *    DefaultRetryListener.java created by AllanThrane on 03/07/2014 
 *    File revision  $Revision: 0.0 $
 */

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

package com.alpha.pineapple.plugin.session.retry;

import org.springframework.retry.RetryListener;

import com.alpha.pineapple.execution.ExecutionResult;

/**
 * Extends the {@linkplain RetryListener} interface with support for setting a
 * {@linkplain ExecutionResult} which captures information about retries.
 * 
 * This interface is intended to be used to capturing retry information for
 * failed session connect and disconnect attempts.
 */
public interface SessionRetryListener extends RetryListener {

    /**
     * Set execution result which captures information about retries.
     * 
     * @param result
     *            execution result which captures information about retries.
     */
    void setResult(ExecutionResult result);
}
