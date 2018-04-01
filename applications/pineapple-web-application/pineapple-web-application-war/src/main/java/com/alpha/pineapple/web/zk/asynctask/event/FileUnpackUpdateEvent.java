package com.alpha.pineapple.web.zk.asynctask.event;

import org.zkoss.zk.ui.event.Event;

import com.alpha.pineapple.web.zk.asynctask.event.visitor.UnpackModuleTaskEventVisitor;

/**
 * Extends the {@linkplain Event} class and implements the {@linkplain Event}
 * interface which is used to communicate the progress of the unpack module
 * asynchronous task to a ZK composer.
 */
public class FileUnpackUpdateEvent extends Event implements UnpackModuleTaskEvent {

	private static final long serialVersionUID = 1016799256303068779L;

	public static final String NAME = "onFileUnpackUpdate";

	/**
	 * Unpack percentage.
	 */
	final int percentage;

	/**
	 * Current size of unpacked file.
	 */
	final long current;;

	/**
	 * Total size of file.
	 */
	final long total;

	public FileUnpackUpdateEvent(int percentage, long current, long total) {
		super(NAME, null, null);
		this.percentage = percentage;
		this.current = current;
		this.total = total;
	}

	/**
	 * Return percentage.
	 * 
	 * @return percentage.
	 */
	public int getPercentage() {
		return percentage;
	}

	/**
	 * Return total size.
	 * 
	 * @return total size.
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * Return size of current unpacked part.
	 * 
	 * @return size of current unpacked part.
	 */
	public long getCurrent() {
		return current;
	}

	@Override
	public void accept(UnpackModuleTaskEventVisitor visitor) {
		visitor.visit(this);
	}

}
