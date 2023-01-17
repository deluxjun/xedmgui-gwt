package com.speno.xedm.gui.common.client;

import com.speno.xedm.core.service.serials.SUser;

/**
 * User Observer
 * 
 * @author ¹Ú»ó±â
 * @since 1.0
 */
public interface UserObserver {
	/**
	 * Invoked when some changes on the user happens
	 */
	public void onUserChanged(SUser user, String attribute);
	
	public void onUserAdded(SUser user);
	
	public void onUserRemoved();
}
