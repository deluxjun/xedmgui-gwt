package com.speno.xedm.gui.common.client;

/**
 * 
 * @author deluxjun
 *
 */
public class Feature {

	public static String CHECKOUT = "gui.feature.checkout";
	public static String APPROVAL = "gui.feature.approval";
	public static String DELEGATION = "gui.feature.delegation";
	public static String SHARING = "gui.feature.sharing";
	public static String THUMBNAIL = "gui.feature.thumbnail";

	public static boolean isEnbaled(String feature){
		return Session.get().getInfo().isEnabled(feature);
	}
}
