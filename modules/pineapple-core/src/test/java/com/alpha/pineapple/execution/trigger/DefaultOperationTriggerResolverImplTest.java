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
 * Unit test of the class {@linkplain DefaultOperationTriggerResolverImpl}.
 */
public class DefaultOperationTriggerResolverImplTest {

    /**
     * SUT.
     */
    OperationTriggerResolver resolver;

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
     * Random operation.
     */
    String randomInvokedOperation;

    /**
     * Random value.
     */
    String randomValue;

    /**
     * Random environment.
     */
    String randomEnvironment;

    /**
     * Random result.
     */
    String randomResult;

    @Before
    public void setUp() throws Exception {
	randomModule = RandomStringUtils.randomAlphabetic(16);
	randomName = RandomStringUtils.randomAlphabetic(16);
	randomOperation = RandomStringUtils.randomAlphabetic(16);
	randomValue = RandomStringUtils.randomAlphabetic(16);
	randomEnvironment = RandomStringUtils.randomAlphabetic(16);
	randomInvokedOperation = RandomStringUtils.randomAlphabetic(16);
	randomResult = RandomStringUtils.randomAlphabetic(16);

	// create resolver
	resolver = new DefaultOperationTriggerResolverImpl();
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
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(0, result.count());
    }

    /**
     * Trigger with operation wild card is resolved for execution.
     */
    @Test
    public void testTriggerWithOperationWildCardWillExecute() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(CoreConstants.TRIGGER_WILDCARD_OPERATION);
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with empty result is resolved for execution.
     */
    @Test
    public void testTriggerWithEmptyOperationIsResolved() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation("");
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with null operation is resolved for execution.
     */
    @Test
    public void testTriggerWithNullOperationIsResolved() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(null);
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with operation wild card with leading space is resolved for
     * execution.
     */
    @Test
    public void testTriggerWithOperationWildCardAndLeadingSpaceIsResolved() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(" " + CoreConstants.TRIGGER_WILDCARD_OPERATION);
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with operation wild card with trailing space is resolved for
     * execution.
     */
    @Test
    public void testTriggerWithOperationWildCardAndTrailingSpaceIsResolved() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(CoreConstants.TRIGGER_WILDCARD_OPERATION + " ");
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with operation which is identical to invoked model operation is
     * resolved for execution.
     */
    @Test
    public void testTriggerWithOperationIdenticalWithInvokedModelOperationWillExecute() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomInvokedOperation);
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with operation with different state isn't resolved for execution.
     */
    @Test
    public void testTriggerWithOperationDifferentToInvokedModelOperationIsntResolved() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(randomValue);
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(0, result.count());
    }

    /**
     * Trigger with operation list is resolved for execution.
     */
    @Test
    public void testTriggerWithOperationListWillExecute() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation("{" + randomValue + "}");
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomValue);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with operation list with two operations is resolved for
     * execution. operation is matched.
     */
    @Test
    public void testTriggerWithOperationListWithTwoOperationsWillExecute() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation("{" + randomValue + "," + randomInvokedOperation + "}");
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with operation list with two operations is resolved for
     * execution. The list contains two identical operations. One result is
     * matched.
     */
    @Test
    public void testTriggerWithOperationListWithTwoOperationsWillExecute2() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation("{" + randomInvokedOperation + "," + randomInvokedOperation + "}");
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with operation list with two operations is resolved for
     * execution. The list contains two operations which differ from the invoked
     * operation. No results are matched.
     */
    @Test
    public void testTriggerWithOperationListWithTwoOperationsInstResolved() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation("{" + randomValue + "," + randomValue + "2" + "}");
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(0, result.count());
    }

    /**
     * Trigger with operation list with leading space is resolved for execution.
     */
    @Test
    public void testTriggerWithOperationListWithLeadingSpaceWillExecute() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation(" {" + randomInvokedOperation + "}");
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with operation list with leading space is resolved for execution.
     */
    @Test
    public void testTriggerWithResultListWithLeadingSpaceWillExecute2() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation("{ " + randomInvokedOperation + "}");
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with operation with list with trailing space is resolved for
     * execution.
     */
    @Test
    public void testTriggerWithOperationListWithTrailingSpaceWillExecute() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation("{" + randomInvokedOperation + " }");
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(1, result.count());
    }

    /**
     * Trigger with operation with empty list resolves nothing for execution.
     */
    @Test
    public void testTriggerWithEmptyResultList() {
	AggregatedModel aggregatedModel = objectFactory.createAggregatedModel();
	Trigger trigger = objectFactory.createTrigger();
	trigger.setEnvironment(randomEnvironment);
	trigger.setModule(randomModule);
	trigger.setName(randomName);
	trigger.setOperation(randomOperation);
	trigger.setOnTargetOperation("{}");
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(0, result.count());
    }

    /**
     * Trigger with operation with empty list resolves nothing for execution.
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
	trigger.setOnTargetOperation("{ }");
	trigger.setOnResult(randomResult);
	aggregatedModel.getTrigger().add(trigger);

	Stream<Trigger> triggers = aggregatedModel.getTrigger().stream();
	Stream<Trigger> result = resolver.resolve(triggers, randomInvokedOperation);

	// test
	assertEquals(0, result.count());
    }
}
