package com.speno.xedm.gui.frontend.client.clipboard;

import java.util.HashSet;
import java.util.Set;

import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.speno.xedm.core.service.serials.SDocument;

public class Clipboard extends HashSet<SDocument> {

	private static final long serialVersionUID = 1L;

	public static final String COPY = "copy";

	public static final String CUT = "cut";

	private static Clipboard instance = new Clipboard();

	private Set<ClipboardObserver> observers = new HashSet<ClipboardObserver>();

	private String lastAction = COPY;

	public String getLastAction() {
		return lastAction;
	}

	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}

	private Clipboard() {
		super();
	}

	public static Clipboard getInstance() {
		return instance;
	}

	public void addObserver(ClipboardObserver observer) {
		observers.add(observer);
	}

	@Override
	public boolean add(SDocument e) {
		if (super.add(e)) {
			for (ClipboardObserver observer : observers) {
				observer.onAdd(e);
			}
			return true;
		}
		return false;
	}

	@Override
	public void clear() {
		super.clear();
		for (ClipboardObserver observer : observers) {
			observer.onRemove(null);
		}
	}

	@Override
	public boolean remove(Object o) {
		if (super.remove(o)) {
			for (ClipboardObserver observer : observers) {
				observer.onRemove((SDocument) o);
			}
			return true;
		}
		return false;
	}

	 public ListGridRecord[] getRecords() {
		ListGridRecord[] array = new ListGridRecord[size()];
		int i = 0;
		for (SDocument document : this) {
			array[i] = new ListGridRecord();
			array[i].setAttribute("id", Long.toString(document.getId()));
			array[i].setAttribute("title", document.getTitle());
			i++;
		}
		return array;
	}
}