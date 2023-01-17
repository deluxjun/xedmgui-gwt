package com.speno.xedm.gui.common.client;

import com.speno.xedm.core.service.serials.SUser;


/**
 * 
 * @author deluxjun
 *
 */
public interface SessionObserver {
	public void onUserLoggedIn(SUser user);
}
