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

import static com.alpha.pineapple.CoreConstants.TRIGGER_WILDCARD_RESULT;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.model.module.model.Trigger;

/**
 * Implementation of the {@linkplain ResultTriggerResolver} interface.
 */
public class DefaultResultTriggerResolverImpl implements ResultTriggerResolver {

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
	 * Returns true if trigger result contains wild card value.
	 */
	Predicate<Trigger> isWildcardResult = (Trigger t) -> t.getOnResult().trim().equals(TRIGGER_WILDCARD_RESULT);

	/**
	 * Returns true if trigger result is null or empty.
	 */
	Predicate<Trigger> isNullOrEmptyResult = (Trigger t) -> (t.getOnResult() == null) || (t.getOnResult().isEmpty());

	/**
	 * Returns true if trigger result contains list notation.
	 */
	Predicate<Trigger> isResultList = (Trigger t) -> t.getOnResult().trim().startsWith(LIST_START)
			&& t.getOnResult().trim().endsWith(LIST_END);

	/**
	 * Returns true if trigger result is identical to expected execution state.
	 */
	BiPredicate<Trigger, ExecutionState> isExpectedState = (t1, e) -> t1.getOnResult().equalsIgnoreCase(e.toString());

	/**
	 * Returns true if trigger result list contain expected execution state.
	 */
	BiPredicate<Trigger, ExecutionState> isExecutedStateInResultList = (t, e) -> {
		String nestedString = StringUtils.substringBetween(t.getOnResult(), LIST_START, LIST_END);
		String[] candidateState = StringUtils.split(nestedString, LIST_DELIMITER);
		return Stream.of(candidateState).anyMatch(s -> s.trim().equalsIgnoreCase(e.toString()));
	};

	/**
	 * Returns true if trigger operation is null or empty.
	 */
	Predicate<Trigger> isNullOrEmptyOperation = (Trigger t) -> (t.getOnResult() == null) || (t.getOnResult().isEmpty());

	@Override
	public Stream<Trigger> resolve(Stream<Trigger> triggers, ExecutionState state) {

		return triggers.filter(isNullOrEmptyResult.or(isWildcardResult.or(t -> isExpectedState.test(t, state))
				.or(isResultList.and(t -> isExecutedStateInResultList.test(t, state)))));
	}

}
