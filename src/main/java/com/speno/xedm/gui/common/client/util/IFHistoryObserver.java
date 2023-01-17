package com.speno.xedm.gui.common.client.util;

public interface IFHistoryObserver {
//	public String getParentMenuRef();
	public void selectByHistory(String refid);
	public void onHistoryAdded(String refid);
}
