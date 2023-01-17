package com.speno.xedm.gui.common.client.util;

public interface IFAdminMenuPriv {
	public void buildMenu(String finalCallbackId, long parentMenuId, boolean hasHistory);
	public void selectMenu(String menu, String subMenu, boolean bByHistory);
}
