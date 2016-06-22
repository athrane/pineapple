/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2016 Allan Thrane Andersen.
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

package com.alpha.pineapple.execution.trigger;

import static com.alpha.pineapple.execution.ExecutionResult.ExecutionState.ERROR;
import static com.alpha.pineapple.execution.ExecutionResult.ExecutionState.FAILURE;
import static com.alpha.pineapple.execution.ExecutionResult.ExecutionState.INTERRUPTED;
import static com.alpha.pineapple.execution.ExecutionResult.ExecutionState.SUCCESS;
import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.model.module.model.AggregatedModel;
import com.alpha.pineapple.model.module.model.ObjectFactory;
import com.alpha.pineapple.model.module.model.Trigger;

/**
 * Unit test of the class {@linkplain DefaultResultTriggerResolverImpl}.
 */
public class DefaultResultTriggerResolverImplTest {

    /**
     * SUT.
     */
    ResultTriggerResolver resolver;

    /**
     * Model factory.
     */
    ObjectFactory objectFactory = new ObjectFactory();

    /**
     * Random module.
     */
    String randomModule;

    /**
     * Random name.
     */
    String randomName;

    /**
     * Random operation.
     */
    String randomOperation;

    /**
     * Random value.
     */
    String randomValue;

    /**
     * Random environment.
     */
    String randomEnvironment;

    @Before
    public void setUp() throws Exception {
	randomModule = RandomStringUtils.randomAlphabetic(16);
	randomName = RandomStringUtils.randomAlphabetic(16);
	randomOperation = RandomStringUtils.randomAlphabetic(16);
	randomValue = RandomStringUtils.randomAlphabetic(16);
	randomEnvironment = RandomStringUtils.randomAlphabetic(16);

	// create resolver
	resolver = new DefaultResultTriggerResolverImpl();
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Can resolve empty trigger list.
     */
    @Test
    public void testResolveEmptyTriggerList() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, SUCCESS);

	// test
	assertEquals(0, result.count());
    }

    /**
     * Trigger with result wild card is resolved for execution.
     */
    @Test
    public void testTriggerWithResultWildCardWillExecute() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult(CoreConstants.TRIGGER_WILDCARD_RESULT);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, SUCCESS);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with empty result is resolved for execution.
     */
    @Test
    public void testTriggerWithEmptyResultIsResolved() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult("");
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, SUCCESS);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with empty result is resolved for execution.
     */
    @Test
    public void testTriggerWithEmptyResultIsResolved2() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult("");
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, ERROR);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with null result is resolved for execution.
     */
    @Test
    public void testTriggerWithNullResultIsResolved() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult(null);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, SUCCESS);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with result wild card with leading space is resolved for
     * execution.
     */
    @Test
    public void testTriggerWithResultWildCardAndLeadingSpaceIsResolved() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult(" " + CoreConstants.TRIGGER_WILDCARD_RESULT);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, SUCCESS);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with result wild card with trailing space is resolved for
     * execution.
     */
    @Test
    public void testTriggerWithResultWildCardAndTrailingSpaceIsResolved() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult(CoreConstants.TRIGGER_WILDCARD_RESULT + " ");
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, SUCCESS);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with result with identical state is resolved for execution.
     */
    @Test
    public void testTriggerWithResultIdenticalStateWillExecute() {
	ExecutionState state = SUCCESS;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult(state.toString());
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with result with identical state is resolved for execution.
     */
    @Test
    public void testTriggerWithResultIdenticalStateWillExecute2() {
	ExecutionState state = ERROR;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult(state.toString());
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with result with identical state is resolved for execution.
     */
    @Test
    public void testTriggerWithResultIdenticalStateWillExecute3() {
	ExecutionState state = INTERRUPTED;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult(state.toString());
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with result with different state isn't resolved for execution.
     */
    @Test
    public void testTriggerWithResultDifferentStateIsntResolved() {
	ExecutionState state = SUCCESS;
	ExecutionState differentState = INTERRUPTED;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult(differentState.toString());
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(0, result.count());
    }

    /**
     * Trigger with result with different state isn't resolved for execution.
     */
    @Test
    public void testTriggerWithResultDifferentStateIsntResolved2() {
	ExecutionState state = INTERRUPTED;
	ExecutionState differentState = SUCCESS;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult(differentState.toString());
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(0, result.count());
    }

    /**
     * Trigger with result list is resolved for execution.
     */
    @Test
    public void testTriggerWithResultListWillExecute() {
	ExecutionState state = SUCCESS;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult("{" + state.toString() + "}");
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with result with list with two results is resolved for execution.
     * One result is matched.
     */
    @Test
    public void testTriggerWithResultListWithTwoResultsWillExecute() {
	ExecutionState state = SUCCESS;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult("{" + state.toString() + "," + ERROR.toString() + "}");
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with result list with two results is resolved for execution. The
     * list contains two identical results. One result is matched.
     */
    @Test
    public void testTriggerWithResultListWithTwoResultsWillExecute2() {
	ExecutionState state = SUCCESS;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult("{" + state.toString() + "," + state.toString() + "}");
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with result list with two results is resolved for execution. The
     * list contains two results which differ from the expected state. No
     * results are matched.
     */
    @Test
    public void testTriggerWithResultListWithTwoResultsInstResolved() {
	ExecutionState state = SUCCESS;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult("{" + ERROR.toString() + "," + FAILURE.toString() + "}");
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(0, result.count());
    }

    /**
     * Trigger with result list with leading space is resolved for execution.
     */
    @Test
    public void testTriggerWithResultListWithLeadingSpaceWillExecute() {
	ExecutionState state = SUCCESS;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult(" {" + state.toString() + "}");
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with result list with leading space is resolved for execution.
     */
    @Test
    public void testTriggerWithResultListWithLeadingSpaceWillExecute2() {
	ExecutionState state = SUCCESS;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult("{ " + state.toString() + "}");
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with result list with trailing space is resolved for execution.
     */
    @Test
    public void testTriggerWithResultListWithTrailingSpaceWillExecute() {
	ExecutionState state = SUCCESS;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult("{" + state.toString() + "} ");
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with result with empty list resolves nothing for execution.
     */
    @Test
    public void testTriggerWithEmptyResultList() {
	ExecutionState state = SUCCESS;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult("{}");
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(0, result.count());
    }

    /**
     * Trigger with result with empty list resolves nothing for execution.
     */
    @Test
    public void testTriggerWithEmptyResultList2() {
	ExecutionState state = SUCCESS;

	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult(" { } ");
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, state);

	// test
	assertEquals(0, result.count());
    }
}
