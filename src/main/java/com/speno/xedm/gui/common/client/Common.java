package com.speno.xedm.gui.common.client;

import com.google.gwt.core.client.EntryPoint;

/**
 * 
 * @author deluxjun
 *
 */
public class Common implements EntryPoint {

	private static Common instance;

	/**
	 * @return singleton Main instance
	 */
	public static Common get() {
		return instance; 
	}

	@Override
	public void onModuleLoad() {
		instance = this;
	}
}