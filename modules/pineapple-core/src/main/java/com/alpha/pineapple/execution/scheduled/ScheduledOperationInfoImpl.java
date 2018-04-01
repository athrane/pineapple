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
package com.alpha.pineapple.execution.scheduled;

import java.util.concurrent.ScheduledFuture;

import com.alpha.pineapple.model.execution.scheduled.ScheduledOperation;

/**
 * Implementation of the interface {@linkplain ScheduledOperationInfo}.
 */
public class ScheduledOperationInfoImpl implements ScheduledOperationInfo {

	/**
	 * Scheduled operation.
	 */
	ScheduledOperation operation;

	/**
	 * Scheduled future.
	 */
	ScheduledFuture<?> future;

	/**
	 * ScheduledOperationInfoImpl constructor.
	 * 
	 * @param operation
	 *            scheduled operation.
	 * @param future
	 *            scheduled future.
	 */
	public ScheduledOperationInfoImpl(ScheduledOperation operation, ScheduledFuture<?> future) {
		this.operation = operation;
		this.future = future;
	}

	@Override
	public ScheduledOperation getOperation() {
		return operation;
	}

	@Override
	public ScheduledFuture<?> getFuture() {
		return future;
	}

	@Override
	public boolean isScheduled() {
		return (future != null);
	}

	@Override
	public void cancel() {
		if (future == null)
			return;
		future.cancel(true);
	}

}
