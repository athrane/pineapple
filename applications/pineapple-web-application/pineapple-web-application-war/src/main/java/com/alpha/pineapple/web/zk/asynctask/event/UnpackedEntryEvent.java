package com.alpha.pineapple.web.zk.asynctask.event;

import java.util.zip.ZipEntry;

import org.zkoss.zk.ui.event.Event;

import com.alpha.pineapple.web.zk.asynctask.event.visitor.UnpackModuleTaskEventVisitor;

/**
 * Extends the {@linkplain Event} class and implements the {@linkplain Event}
 * interface which is used to communicate the progress of the unpack module
 * asynchronous task to a ZK composer.
 */
public class UnpackedEntryEvent extends Event implements UnpackModuleTaskEvent {

    private static final long serialVersionUID = 1016799256303068779L;

    /**
     * Copy buffer size.
     */
    final int ONE_KB_BUFFER = 1024;

    /**
     * Event ID.
     */
    public static final String NAME = "onUnpackedEntry";

    /**
     * Percentage.
     */
    final int percentage;

    /**
     * Unpacked ZIP entry.
     */
    final ZipEntry entry;

    /**
     * Entry number.
     */
    final int current;

    /**
     * Total number of entries..
     */
    final int total;

    public UnpackedEntryEvent(int percentage, ZipEntry entry, int current, int total) {
	super(NAME, null, null);
	this.percentage = percentage;
	this.entry = entry;
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
     * Return unpacked entry name.
     * 
     * @return unpacked entry name.
     */
    public String getEntryName() {
	return entry.getName();
    }

    /**
     * Return current entry number.
     * 
     * @return current entry number.
     */
    public int getCurrent() {
	return current;
    }

    /**
     * Return total number of entries.
     * 
     * @return total number of entries.
     */
    public int getTotal() {
	return total;
    }

    /**
     * Return unpacked entry type.
     * 
     * @return unpacked entry type, which will be either a file or directory.
     */
    public String getEntryType() {
	if (entry.isDirectory())
	    return "Directory";
	return "File";
    }

    /**
     * Return unpacked entry size in KB.
     * 
     * @return unpacked entry size in KB.
     */
    public long getEntrySize() {
	if (entry.getSize() < ONE_KB_BUFFER)
	    return 1;
	double sizeKb = (double) entry.getSize() / (double) ONE_KB_BUFFER;
	sizeKb = Math.ceil(sizeKb);
	return (long) sizeKb;
    }

    @Override
    public void accept(UnpackModuleTaskEventVisitor visitor) {
	visitor.visit(this);
    }

}
