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
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.execution.trigger;

import static com.alpha.pineapple.CoreConstants.TRIGGER_WILDCARD_OPERATION;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;

import com.alpha.pineapple.model.module.model.Trigger;

/**
 * Implementation of the {@linkplain OperationTriggerResolver} interface.
 */
public class DefaultOperationTriggerResolverImpl implements OperationTriggerResolver {

	/**
	 * List delimiter.
	 */
	static final String LIST_DELIMITER = ",";

	/**
	 * List start.
	 */
	static final String LIST_END = "}";

	/**
	 * List end.
	 */
	static final String LIST_START = "{";

	/**
	 * Returns true if trigger operation contains wild card value.
	 */
	Predicate<Trigger> isWildcardOperation = (Trigger t) -> t.getOnTargetOperation().trim()
			.equals(TRIGGER_WILDCARD_OPERATION);

	/**
	 * Returns true if trigger operation contains list notation.
	 */
	Predicate<Trigger> isOperationList = (Trigger t) -> t.getOnTargetOperation().trim().startsWith(LIST_START)
			&& t.getOnTargetOperation().trim().endsWith(LIST_END);

	/**
	 * Returns true if trigger operation is null or empty.
	 */
	Predicate<Trigger> isNullOrEmptyOperation = (Trigger t) -> (t.getOnTargetOperation() == null)
			|| (t.getOnTargetOperation().isEmpty());

	/**
	 * Returns true if trigger operation is identical to invoked operation.
	 */
	BiPredicate<Trigger, String> isExpectedOperation = (t1, o) -> t1.getOnTargetOperation().equalsIgnoreCase(o);

	/**
	 * Returns true if trigger operation list contain expected operation.
	 */
	BiPredicate<Trigger, String> isExecutedOperationInList = (t, o) -> {
		String nestedString = StringUtils.substringBetween(t.getOnTargetOperation(), LIST_START, LIST_END);
		String[] candidateOperation = StringUtils.split(nestedString, LIST_DELIMITER);
		return Stream.of(candidateOperation).anyMatch(s -> s.trim().equalsIgnoreCase(o));
	};

	@Override
	public Stream<Trigger> resolve(Stream<Trigger> triggers, String operation) {
		return triggers
				.filter(isNullOrEmptyOperation.or(isWildcardOperation.or(t -> isExpectedOperation.test(t, operation))
						.or(isOperationList.and(t -> isExecutedOperationInList.test(t, operation)))));
	}

}
