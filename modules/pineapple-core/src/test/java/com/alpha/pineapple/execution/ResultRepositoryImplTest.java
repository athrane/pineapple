/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.execution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Unit test for the {@link ResultRepositoryImpl} class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(DirectoryTestExecutionListener.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class ResultRepositoryImplTest {

	/**
	 * Object under test.
	 */
	ResultRepositoryImpl repository;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * Result listener mock object.
	 */
	ResultListener listenerOne;

	/**
	 * Result listener mock object.
	 */
	ResultListener listenerTwo;

	/**
	 * Random description.
	 */
	String randomDescription;

	@Before
	public void setUp() throws Exception {
		randomDescription = RandomStringUtils.randomAlphabetic(10) + "-desc";

		// create repository
		repository = new ResultRepositoryImpl();

		// create mock listener
		listenerOne = EasyMock.createMock(ResultListener.class);

		// create mock listener
		listenerTwo = EasyMock.createMock(ResultListener.class);

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// inject message provider
		ReflectionTestUtils.setField(repository, "messageProvider", messageProvider, MessageProvider.class);

		// complete mock source initialization
		IAnswer<String> answer = new MessageProviderAnswerImpl();
		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class),
				(Object[]) EasyMock.isA(Object[].class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.replay(messageProvider);

	}

	@After
	public void tearDown() throws Exception {
		repository = null;
		listenerOne = null;
		listenerTwo = null;
		testDirectory = null;
		repository = null;
	}

	/**
	 * Test that the return listener array is initially defined.
	 */
	@Test
	public void testReturnedListenersArrayIsInitiallyDefined() {
		assertNotNull(this.repository.getListeners());
	}

	/**
	 * Test that the number of registered listeners is initially zero.
	 */
	@Test
	public void testNumberOfRegisterListenersIsInitialllyZero() {
		assertEquals(0, this.repository.getListeners().length);
	}

	/**
	 * Test that undefined execution result is rejected,
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedResultDuringNotification() throws Exception {

		// notify
		this.repository.notifyOfResultStateChange(null);
	}

	/**
	 * Test that no listeners is notified.
	 */
	@Test
	public void testCanNotifyZeroListeners() {

		// create mock execution result
		ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
		EasyMock.expect(result.getState()).andReturn(ExecutionState.EXECUTING).times(2);
		EasyMock.expect(result.getDescription()).andReturn(randomDescription);
		EasyMock.expect(result.getRootResult()).andReturn(result);
		EasyMock.replay(result);

		// notify
		this.repository.notifyOfResultStateChange(result);

		// verify mock objects
		EasyMock.verify(result);
	}

	/**
	 * Test that a single listener is notified.
	 */
	@Test
	public void testCanNotifyOneListener() {

		// register listener
		this.repository.addListener(listenerOne);

		// create mock execution result
		ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
		EasyMock.expect(result.getState()).andReturn(ExecutionState.EXECUTING).times(2);
		EasyMock.expect(result.getDescription()).andReturn(randomDescription);
		EasyMock.expect(result.getRootResult()).andReturn(result);
		EasyMock.replay(result);

		// setup mock listener
		listenerOne.notify(EasyMock.isA(ExecutionResultNotification.class));
		EasyMock.replay(listenerOne);

		// notify
		this.repository.notifyOfResultStateChange(result);

		// verify mock objects
		EasyMock.verify(listenerOne);
		EasyMock.verify(result);
	}

	/**
	 * Test that two listeners are notified.
	 */
	@Test
	public void testCanNotifyTwoListeners() {

		// register listener
		this.repository.addListener(listenerOne);
		this.repository.addListener(listenerTwo);

		// create mock execution result
		ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
		EasyMock.expect(result.getState()).andReturn(ExecutionState.EXECUTING).times(2);
		EasyMock.expect(result.getDescription()).andReturn(randomDescription);
		EasyMock.expect(result.getRootResult()).andReturn(result);
		EasyMock.replay(result);

		// setup mock listeners
		listenerOne.notify(EasyMock.isA(ExecutionResultNotification.class));
		EasyMock.replay(listenerOne);
		listenerTwo.notify(EasyMock.isA(ExecutionResultNotification.class));
		EasyMock.replay(listenerTwo);

		// notify
		this.repository.notifyOfResultStateChange(result);

		// verify mock objects
		EasyMock.verify(listenerOne);
		EasyMock.verify(listenerTwo);
		EasyMock.verify(result);
	}

	/**
	 * Test that a single listener can be registered.
	 */
	@Test
	public void testCanRegisterOneListener() {

		// register listener
		this.repository.addListener(listenerOne);

		// test
		assertEquals(1, this.repository.getListeners().length);
	}

	/**
	 * Test that two listeners can be registered.
	 */
	@Test
	public void testCanRegisterTwoListeners() {

		// register listeners
		this.repository.addListener(listenerOne);
		this.repository.addListener(listenerTwo);

		// test
		assertEquals(2, this.repository.getListeners().length);
	}

	/**
	 * Test that repository contains the registered listeners.
	 */
	@Test
	public void testRepositoryContainsRegisteredListeners() {

		// register listeners
		this.repository.addListener(listenerOne);
		this.repository.addListener(listenerTwo);

		// get listeners
		ResultListener[] listeners = this.repository.getListeners();

		// test
		assertEquals(listenerOne, listeners[0]);
		assertEquals(listenerTwo, listeners[1]);
	}

	/**
	 * Test that the same listener only can be registered once with the repository.
	 */
	@Test
	public void testListenerCanOnlyBeRegisteredOnce() {

		// register listeners
		this.repository.addListener(listenerOne);
		this.repository.addListener(listenerTwo);

		// get listeners
		this.repository.addListener(listenerOne);
		ResultListener[] listeners = this.repository.getListeners();

		// test
		assertEquals(2, this.repository.getListeners().length);
		assertEquals(listenerOne, listeners[0]);
		assertEquals(listenerTwo, listeners[1]);
	}

	/**
	 * Test that undefined listener is rejected during registration
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedListenerDuringRegistration() throws Exception {

		// register listener
		this.repository.addListener(null);
	}

	/**
	 * Test that listener can be unregistered.
	 */
	@Test
	public void testCanUnregisterListener() {

		// register listeners
		this.repository.addListener(listenerOne);
		this.repository.addListener(listenerTwo);

		// unregister listener
		this.repository.removeListener(listenerOne);

		// get listeners
		ResultListener[] listeners = this.repository.getListeners();

		// test
		assertEquals(1, this.repository.getListeners().length);
		assertEquals(listenerTwo, listeners[0]);
	}

	/**
	 * Test that listeners can be unregistered.
	 */
	@Test
	public void testCanUnregisterListeners() {

		// register listeners
		this.repository.addListener(listenerOne);
		this.repository.addListener(listenerTwo);

		// unregister listener
		this.repository.removeListener(listenerOne);
		this.repository.removeListener(listenerTwo);

		// test
		assertEquals(0, this.repository.getListeners().length);
	}

	/**
	 * Test that undefined listener is rejected during unregistration
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedListenerDuringUnregistration() throws Exception {

		// register listener
		this.repository.removeListener(null);
	}

	/**
	 * Test that undefined execution info is rejected.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedExecutionInfoForResultSequenceQuery() throws Exception {

		// query repository
		this.repository.getResultSequence(null, 1, 1);
	}

	/**
	 * Test that illegal index is rejected.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsIllegalIndexForResultSequenceQuery() throws Exception {

		ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);

		// query repository
		this.repository.getResultSequence(info, -1, 1);

		// test
		EasyMock.verify(info);
	}

	/**
	 * Test that illegal index is rejected.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsIllegalIndexForResultSequenceQuery2() throws Exception {

		ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);

		// query repository
		this.repository.getResultSequence(info, 1, -1);

		// test
		EasyMock.verify(info);
	}

	/**
	 * Test that illegal index is rejected, the query is rejected if the fist index
	 * is larger than the last index.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsIllegalIndexForResultSequenceQuery3() throws Exception {

		ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);

		// query repository
		this.repository.getResultSequence(info, 2, 1);

		// test
		EasyMock.verify(info);
	}

	/**
	 * Test that undefined execution info is rejected.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedExecutionInfoForResultSequenceIndexQuery() throws Exception {

		// query repository
		this.repository.getCurrentResultIndex(null);
	}

	/**
	 * Test that unknown execution info is rejected for result sequence query.
	 */
	@Test(expected = ExecutionInfoNotFoundException.class)
	public void testRejectsUnknownExecutionInfoForResultSequenceQuery() throws Exception {

		// create execution result mock
		ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
		EasyMock.replay(result);

		// create execution info mock
		ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);
		EasyMock.expect(info.getResult()).andReturn(result);
		EasyMock.replay(info);

		// query repository
		this.repository.getResultSequence(info, 0, 1);

		// test
		EasyMock.verify(info);
		EasyMock.verify(result);
	}

	/**
	 * Test that unknown execution info is rejected for result sequence index query.
	 */
	@Test(expected = ExecutionInfoNotFoundException.class)
	public void testRejectsUnknownExecutionInfoForResultSequenceIndexQuery() throws Exception {

		// create execution result mock
		ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
		EasyMock.replay(result);

		// create execution info mock
		ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);
		EasyMock.expect(info.getResult()).andReturn(result);
		EasyMock.replay(info);

		// query repository
		this.repository.getCurrentResultIndex(info);

		// test
		EasyMock.verify(info);
		EasyMock.verify(result);
	}

}
