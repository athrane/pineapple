package com.alpha.pineapple.web.zk.asynctask.event;

import org.zkoss.zk.ui.event.Event;

import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.web.event.EventDispatcherImpl;

/**
 * Implementation of the {@linkplain Event} interface.
 * 
 * This event is used to dispatch ZK events using the
 * {@linkplain EventDispatcherImpl} to signal that a
 * {@linkplain ExecutionResultNotification} the dispatched event was received
 * from Reactor.
 */
public class ResultNotificationEvent extends Event {

    private static final long serialVersionUID = 1016799256303068779L;

    public static final String NAME = "onExecutionResultNotificationEvent";

    public ResultNotificationEvent(ExecutionResultNotification notification) {
	super(NAME, null, notification);
    }

}
