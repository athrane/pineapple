package com.alpha.pineapple.web.zk.asynctask.event;

import org.zkoss.zk.ui.event.Event;

import com.alpha.pineapple.web.event.EventDispatcherImpl;

/**
 * Implementation of the {@linkplain Event} interface.
 * 
 * This event is used to dispatch ZK events using the
 * {@linkplain EventDispatcherImpl} in the situation where the dispatched event
 * doesn't carry any arguments.
 */
public class GenericEvent extends Event {

    private static final long serialVersionUID = 1016799256303068779L;

    public static final String NAME = "onGenericEvent";

    public GenericEvent() {
	super(NAME, null, null);
    }

}
