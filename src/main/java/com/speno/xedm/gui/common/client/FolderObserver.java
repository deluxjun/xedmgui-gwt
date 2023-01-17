package com.speno.xedm.gui.common.client;

import com.speno.xedm.core.service.serials.SFolder;


/**
 * 
 * @author deluxjun
 *
 */
public interface FolderObserver {

	public void onFolderSelected(SFolder folder);
	
	public void onFolderSaved(SFolder folder);
	
	public void onFolderReload();
}
